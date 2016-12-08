import java.util.*;
import java.io.Serializable;

public class Network implements Serializable
{

	LayerBridge[] bridges;
	Layer initLayer;
	Neuron output;
	double learningRate;

	public Network(int hiddenLayers,int[] neuronsForLayer,double[] biasForLayer, double learningRate)
	{
		this.learningRate = learningRate;
		//se crea la primera capa(32 neuronas para piezas + 14 para datos de interes) y la ultima (1 neurona de salida)
		this.output = new Neuron();
		Neuron[] initLayerNeurons = new Neuron[46];
		for (int i=0;i < 46 ; i++) 
		{
			initLayerNeurons[i] = new Neuron();
		}
		this.initLayer = new Layer(initLayerNeurons,0);
		//se crea la ultima capa
		Neuron[] lastNeuron = new Neuron[1];
		lastNeuron[0] = output;
		Layer lastLayer = new Layer(lastNeuron,0);
		/*se crean las demas capas y son pareadas en puentes (clase que administra las conexiones entre dos capas
		y los metodos de propagacion entre estas)
		*/
		this.bridges = new LayerBridge[hiddenLayers+1];
		Layer auxLayer = this.initLayer;
 		for (int i=0; i < hiddenLayers ; i++)
		{
			Neuron[] neurons = new Neuron[neuronsForLayer[i]];
			for (int j=0;j < neuronsForLayer[i] ; j++) 
			{
				neurons[j] = new Neuron();
			}
			Layer layer = new Layer(neurons,biasForLayer[i]);
			this.bridges[i] = new LayerBridge(auxLayer, layer);
			auxLayer = layer;
		}

		//se conecta la ultima hidden layer con la capa output (output)
		this.bridges[hiddenLayers] = new LayerBridge(auxLayer, lastLayer);
	}

	public double evaluate(Board board)
	{
		int[] numPieces = {8,2,2,2,1,1};
		int[] numWhite = new int[6];
		int[] numBlack = new int[6];
		//se setean las neuronas de la primera capa segun el tablero
		for (int i=0;i < Board.BOARDSIZE; i++ ) 
		{
			for (int j=0;j < Board.BOARDSIZE; j++ ) 
			{
				int piece = board.getPiece(new Coord(i,j));
				if(piece>0)
				{
					numWhite[piece-1]++;
				}
				if(piece<0)
				{
					piece*=-1;
					numBlack[piece-1]++;
				}
			}			
		}

		int layerPointer = 0;

		//white pieces
		for (int p=0; p < 6 ; p++) 
		{
			int i = 0;
			for (; i < numWhite[p] ; i++) 
			{
				initLayer.neurons[layerPointer++].setOutputValue(1);
			}
			for (; i < numPieces[p] ; i++) 
			{
				initLayer.neurons[layerPointer++].setOutputValue(0);
			}
		}
		//black pieces
		for (int p=0; p < 6 ; p++) 
		{
			int i = 0;
			for (; i < numBlack[p] ; i++) 
			{
				initLayer.neurons[layerPointer++].setOutputValue(1);
			}
			for (; i < numPieces[p] ; i++) 
			{
				initLayer.neurons[layerPointer++].setOutputValue(0);
			}
		}

		board.turn = -1;
		initLayer.neurons[layerPointer++].setOutputValue(board.getValidMoves().length);
		board.turn = 1;
		initLayer.neurons[layerPointer++].setOutputValue(board.getValidMoves().length);

		//looking for both kings
		Coord posKingWhite = null;
		Coord posKingBlack = null;
		for (int i=0; i < 8 ; i++)
		{
			for (int j=0;j < 8 ;j++) 
			{
				Coord coord = new Coord(i,j);
				//piece is king
				if(board.getPiece(coord) == 6)
				{
					posKingWhite = coord;
				}
				if(board.getPiece(coord) == -6)
				{
					posKingBlack = coord;
				}
			}
			if(posKingWhite!=null && posKingBlack!=null)break;
		}

		initLayer.neurons[layerPointer++].setOutputValue(board.kingMoves(posKingWhite,1).length);
		initLayer.neurons[layerPointer++].setOutputValue(board.kingMoves(posKingBlack,-1).length);

		initLayer.neurons[layerPointer++].setOutputValue(Util.closerPromotion(board,1));
		initLayer.neurons[layerPointer++].setOutputValue(Util.closerPromotion(board,-1));

		initLayer.neurons[layerPointer++].setOutputValue(Util.fatherDevelopment(board,1));
		initLayer.neurons[layerPointer++].setOutputValue(Util.fatherDevelopment(board,-1));

		initLayer.neurons[layerPointer++].setOutputValue(Util.potentialThread(board,1));
		initLayer.neurons[layerPointer++].setOutputValue(Util.potentialThread(board,-1));

		initLayer.neurons[layerPointer++].setOutputValue(board.getShortCastle(Board.TURNBLACK)?1:0);
		initLayer.neurons[layerPointer++].setOutputValue(board.getLongCastle(Board.TURNBLACK)?1:0);
		initLayer.neurons[layerPointer++].setOutputValue(board.getShortCastle(Board.TURNWHITE)?1:0);
		initLayer.neurons[layerPointer++].setOutputValue(board.getLongCastle(Board.TURNWHITE)?1:0);
		//se propagan los valores
		for (LayerBridge bridge : bridges)
		{
			bridge.propagateForward();
		}

		//se retorna el output de la red
		return output.getOutputValue();
	}

	public void train(Board board, double targetValue)
	{
		double outputVal = evaluate(board);

		//calcular error
		double totalError = 0.5*(targetValue - outputVal)*(targetValue - outputVal);

		//usar propagateBackward (backpropagation) para propagarlo

		this.output.setBpval(-(targetValue - outputVal));

		for (int i = bridges.length-1; i >= 0 ; i--)
		{
			bridges[i].propagateBackward(this.learningRate);	
		}

	}

	//testing
	public static void main(String[] args) 
	{
		Network net = new Network(2,new int[]{256,256},new double[]{0,0},0.03);	

		int[][] board1={{-4,-2,-3,-5,-6,-3,-2,-4},
				 {-1,-1,-1,-1,-1,-1,-1,-1},
				 {0,0,0,0,0,0,0,0},
				 {0,0,0,0,0,0,0,0},
 				 {0,0,0,0,0,0,0,0},
				 {0,0,0,0,0,0,0,0},
	  			 {1,1,1,1,1,1,1,1},
				 {4,2,3,5,6,3,2,4}};

		 int[][] board2={{-3,0,0,0,0,0,0,0},
				{0,5,0,-4,0,-1,0,0},
				{0,0,-2,0,3,0,0,0},
				{0,0,0,-5,1,0,0,0},
				{0,0,0,-1,-1,0,2,0},
				{0,0,0,0,0,-6,1,-1},
				{0,0,2,4,-1,1,-1,6},
				{0,0,0,0,3,0,-2,0}};

				
		Board b1 = new Board(); b1.fromArray(board1);
		Board b2 = new Board(); b1.fromArray(board2);

		double target1 = Double.parseDouble(args[0]); //entre 0 y 1
		target1 = (target1 + 1) / 2;

		double target2 = Double.parseDouble(args[1]); //entre 0 y 1
		target2 = (target2 + 1) / 2;

		double eval1 = 0;
		double eval2 = 0;
		long counter = 0;
		do
		{
			eval1 = net.evaluate(b1);
			eval2 = net.evaluate(b2);
			if(counter%10000 == 0)System.out.println((eval1*2-1)+" "+(eval2*2-1));
			net.train(b1,target1);
			net.train(b2,target2);
			counter++;
		}while(Math.abs(target1-eval1) > 0.0005 || Math.abs(target2-eval2) > 0.0005);
		System.out.println((eval1*2-1)+" "+(eval2*2-1)+" after "+counter+" iterations");
	}
}
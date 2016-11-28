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
		//se crea la primera capa(64 neuronas para representar tablero) y la ultima (1 neurona)
		this.output = new Neuron();
		Neuron[] initLayerNeurons = new Neuron[68];
		for (int i=0;i < 68 ; i++) 
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
		//se setean las neuronas de la primera capa segun el tablero
		for (int i=0;i < Board.BOARDSIZE; i++ ) 
		{
			for (int j=0;j < Board.BOARDSIZE; j++ ) 
			{
				initLayer.neurons[i*Board.BOARDSIZE+j].setInputValue(board.getPiece(new Coord(i,j)));
			}			
		}

		initLayer.neurons[64].setInputValue(board.getShortCastle(Board.TURNBLACK)?1:0);
		initLayer.neurons[65].setInputValue(board.getLongCastle(Board.TURNBLACK)?1:0);
		initLayer.neurons[66].setInputValue(board.getShortCastle(Board.TURNWHITE)?1:0);
		initLayer.neurons[67].setInputValue(board.getLongCastle(Board.TURNWHITE)?1:0);
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
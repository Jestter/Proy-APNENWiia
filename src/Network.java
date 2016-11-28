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
		Neuron[] initLayerNeurons = new Neuron[64];
		for (int i=0;i < 64 ; i++) 
		{
			initLayerNeurons[i] = new Neuron();
		}
		this.initLayer = new Layer(initLayerNeurons,1);
		//se crea la ultima capa
		lastNeuron = new Neuron[1];
		lastNeuron[0] = output;
		Layer lastLayer = new Layer(lastNeuron,1);
		/*se crean las demas capas y son pareadas en puentes (clase que administra las conexiones entre dos capas)
		y los metodos de propagacion entre estas
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
				initLayer.neurons[i*Board.BOARDSIZE+j].setValue(board[i][j]);
			}			
		}

		//se propagan los valores
		for (LayerBridge bridge : bridges)
		{
			bridge.propagateForward();
		}

		//se retorna el output de la red
		return output.getValue();
	}

	public void train(Board board, double targetValue)
	{
		double outputVal = evaluate(board);

		//calcular error
		double totalError = 0.5*(targetValue - outputVal)*(targetValue - outputVal);

		//usar propagateBackward (backpropagation) para propagarlo

		this.output.setBpval(-(totalError));

		for (int i = bridges.length-1; i >= 0 ; i--)
		{
			bridges[i].propagateBackward(this.learningRate);	
		}

	}


}
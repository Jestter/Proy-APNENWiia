import java.util.*;

public class Network
{

	LayerBridge[] bridges;
	Neuron[] initLayer;
	Neuron output;

	public Network(int hiddenLayers,int[] neuronsInLayer)
	{
		//se crea la primera capa(64 neuronas para representar tablero) y la ultima (1 neurona)
		this.output = new Neuron();
		this.initLayer = new Neuron[64];
		for (int i=0;i < 64 ; i++) 
		{
			initLayer[i] = new Neuron();
		}
		//se crea la ultima capa
		lastLayer = new Neuron[1];
		lastLayer[0] = output;
		/*se crean las demas capas y son pareadas en puentes (clase que administra las conexiones entre dos capas)
		y los metodos de propagacion entre estas
		*/
		this.bridges = new LayerBridge[hiddenLayers+1];
		Neuron[] auxLayer = this.initLayer;
 		for (int i=0; i < hiddenLayers ; i++)
		{
			Neuron[] layer = new Neuron[neuronsInLayer[i]];
			for (int j=0;j < neuronsInLayer[i] ; j++) 
			{
				layer[j] = new Neuron();
			}
			this.bridges[i] = new LayerBridge(auxLayer, layer);
			auxLayer = layer;
		}

		//se conecta la ultima hidden layer con la ultima capa (output)
		this.bridges[hiddenLayers] = new LayerBridge(auxLayer, lastLayer);
	}

	public double evaluate(Board board)
	{
		
	}

	public double train(Board board, double value)
	{
		
	}


}
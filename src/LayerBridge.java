

public class LayerBridge
{

	double[][] weights;
	Neuron[] layer1;
	Neuron[] layer2;

	public LayerBridge(Neuron[] layer1,Neuron[] layer2)
	{
		this.layer1 = layer1;
		this.layer2 = layer2;
		this.weights = new double[layer1.length][layer2.length];
		for (int i=0;  i < layer1.length ; i++)
		{
			for (int j=0;  j < layer2.length ; j++)
			{
				weights[i][j] = Math.random()*2-1;
			}	
		}
	}

	/*
		Para actualizar los valores de la siguiente capa al evaluar
	*/
	public void propagateForward()
	{

	}

	/*
		Para ajustar los pesos con backprop
	*/
	public void propagatebackward()
	{
			
	}
}
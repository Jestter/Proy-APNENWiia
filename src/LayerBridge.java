import java.lang.Math;

public class LayerBridge
{

	double[][] weights;
	Layer layer1;
	Layer layer2;


	public LayerBridge(Layer layer1,Layer layer2)
	{
		this.layer1 = layer1;
		this.layer2 = layer2;
		this.weights = new double[layer1.neurons.length][layer2.neurons.length];
		for (int i=0;  i < layer1.neurons.length ; i++)
		{
			for (int j=0;  j < layer2.neurons.length ; j++)
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
		for (int i=0; i < this.layer2.neurons.length ; i++)
		{
			double sum = 0;
			for (int j=0;j < this.layer1.neurons.length; j++)
			{
				sum += weights[j][i] * this.layer1.neurons[j].getOutputValue();
			}
			this.layer2.neurons[i].setInputValue(sum+layer2.bias);
		}
	}

	/*
		Para ajustar los pesos con backprop
	*/
	public void propagateBackward(double lrate)
	{
		for (int i=0;i < this.layer1.neurons.length ; i++)
		{
			for (int j=0; j < this.layer2.neurons.length ; j++)
			{
				double delta = this.layer2.neurons[j].getBpval() * this.layer2.neurons[i].getPartialDerivate();
				this.layer1.neurons[i].setBpvalue(delta);
				this.weights[i][j] -= lrate * delta * this.layer1.neurons[i].getOutputValue();		
			}	
		}	
	}
}
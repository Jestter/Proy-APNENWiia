import java.lang.Math;
import java.io.Serializable;

public class LayerBridge implements Serializable
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
				weights[i][j] = (2*Math.random()-1)/2;
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
			this.layer1.neurons[i].setBpval(0);
			for (int j=0; j < this.layer2.neurons.length ; j++)
			{
				//calculate delta which its used to modified actual weight and added to layer 1's neuron bpval
				double delta = this.layer2.neurons[j].getBpval() * this.layer2.neurons[j].getPartialDerivate();
				//update neuron bpval
				this.layer1.neurons[i].setBpval(this.layer1.neurons[i].getBpval() + delta*this.weights[i][j]);
				//update the current weight
				this.weights[i][j] -= lrate * delta * this.layer1.neurons[i].getOutputValue();		
			}	
		}	
	}
}
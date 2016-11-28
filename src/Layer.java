import java.io.Serializable;

public class Layer implements Serializable
{
	public Neuron[] neurons;
	public double bias;

	public Layer(Neuron[] neurons,double bias)
	{
		this.neurons = neurons;
		this.bias = bias;
	}
}
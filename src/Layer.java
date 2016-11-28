
public class Layer
{
	public Neuron[] neurons;
	public double bias;

	public Layer(Neuron[] neurons,double bias)
	{
		this.neurons = neurons;
		this.bias = bias;
	}
}
import java.io.Serializable;

public class Neuron implements Serializable
{

	private double inputValue;
	private boolean changed;
	private double outputValue;
	private double bpval; //valor asociado a los cambios de los pesos del output de esta neurona

	public Neuron()
	{
		this.inputValue = 0;
		this.outputValue = 0;
		this.changed = true;
	}

	public double getInputValue()
	{
		return inputValue;
	}

	public double getOutputValue()
	{
		if(changed)
		{
			outputValue = Util.sigmoidFunction(inputValue);
			changed = false;
		}
		return outputValue;
	}

	public void setInputValue(double value)
	{
		this.inputValue = value;
		changed = true;
	}

	public void setBpval(double val)
	{
		this.bpval = val;
	}

	public void getBpval()
	{
		return this.bpval;
	}

	public double getPartialDerivate()
	{
		return (this.getOutputValue() * (1-this.getOutputValue()));
	}
}
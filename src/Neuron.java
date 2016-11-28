

public class Neuron
{

	double inputValue;
	private boolean changed;
	private double outputValue;

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
}


public class Neuron
{

	double value;

	public Neuron()
	{
		this.value = 0;
	}

	public double getValue()
	{
		if(this.isActivated())
		{
			return value;
		}
		return 0;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public boolean isActivated()
	{
		/* codigo para determinar si esta activada aca*/

		return false;
	}
}
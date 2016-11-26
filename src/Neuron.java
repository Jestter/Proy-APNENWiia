

public class Neuron
{

	double value;

	public Neuron()
	{
		this.value = 0;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public boolean isActivated()
	{
		/* codigo para determinar si esta activada aca*/

		//pasa siempre por ahora
		return true;
	}
}
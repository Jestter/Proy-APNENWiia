import java.lang.Math;

public class Util
{
	public static double sigmoidFunction(double val)
	{
		return (1.0/(1.0+Math.pow(Math.E,-val));
	}
}
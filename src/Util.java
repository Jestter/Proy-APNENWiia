import java.lang.Math;

public class Util
{
	public static double sigmoidFunction(double val)
	{
		return (1/(1+Math.pow(Math.E,-val));
	}
}
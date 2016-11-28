import java.lang.Math;

public class Util
{
	public static double sigmoidFunction(double val)
	{
		return (1.0/(1.0+Math.pow(Math.E,-val));
	}

	public static boolean saveNetwork(Network net,string filename)
	{
		try(FileOutputStream fileOut = new FileOutputStream(filename);
		    ObjectOutputStream out = new ObjectOutputStream(fileOut))
		{
		    out.writeObject(net);
      	}
      	catch(Exception i) 
      	{
        	return false
      	}
      	return true;
	}

	public static Network loadNetwork(string filename)
	{
		Network net = null;
		try(FileInputStream fileIn = new FileInputStream(filename);
		    ObjectInputStream in = new ObjectInputStream(fileIn))
		{
		    net = (Network)in.readObject();
      	}
      	catch(Exception i) 
      	{
        	return null
      	}
      	return net;
	}
}
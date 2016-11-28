import java.lang.Math;
import java.io.*;
import java.util.*;

public class Util
{

	public static double sigmoidFunction(double val)
	{
		return (1.0/(1.0+Math.pow(Math.E,-val)));
	}

	public static boolean saveObject(Object obj,String filename)
	{
		try(FileOutputStream fileOut = new FileOutputStream(filename);
		    ObjectOutputStream out = new ObjectOutputStream(fileOut))
		{
		    out.writeObject(obj);
      	}
      	catch(Exception i) 
      	{
        	return false;
      	}
      	return true;
	}

	public static Object loadObject(String filename)
	{
		Object obj = null;
		try(FileInputStream fileIn = new FileInputStream(filename);
		    ObjectInputStream in = new ObjectInputStream(fileIn))
		{
		    obj = in.readObject();
      	}
      	catch(Exception i) 
      	{
        	return null;
      	}
      	return obj;
	}
}
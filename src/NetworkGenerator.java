import java.io.*;

public class NetworkGenerator
{
	public NetworkGenerator(String filename, int[] hln,double[] hlb,double lr)
	{
		Network net = new Network(hln.length,hln,hlb,lr);
		Util.saveObject(net,filename);
	}

	public static void main(String[] args) 
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Hey! Welcome to NetworkGenerator,\nplease fill the following required data.");
			System.out.println("Network filename:");
			String filename = br.readLine();
			System.out.println("Number of neurons of each Hidden Layer (space-separated):");
			String[] hlns = br.readLine().split(" ");
			int[] hln = new int[hlns.length];
			for(int i=0;i<hlns.length;i++) hln[i] = Integer.parseInt(hlns[i]);
			System.out.println("Bias for each Hidden Layer (space-separated):");
			String[] hlbs = br.readLine().split(" ");
			double[] hlb = new double[hlbs.length];
			for(int i=0;i<hlbs.length;i++) hlb[i] = Double.parseDouble(hlbs[i]);
			System.out.println("Learning Rate:");
			double lr = Double.parseDouble(br.readLine());

			new NetworkGenerator(filename, hln, hlb, lr);
		}catch(Exception ex){System.out.println("Input Error: please retry running the program.");}
	}
}
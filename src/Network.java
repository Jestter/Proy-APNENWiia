import java.util.*;

public class Network
{

	Arraylist<ArrayList<Connection> > connections;

	public Network(int hiddenLayers,int[] neuronsInLayer)
	{
		int initCapacity = 65;
		for (int i=0; i < hiddenLayers; i++) initCapacity += neuronsInLayer[i];

		connections = new Arraylist<Arraylist<Connection>>(initCapacity);

		for (int i=0; i < 64 ; i++)
		{
			for (int j=64; j< 64+neuronsInLayer[0];j++ )
			{
				double ranWeight = Math.random()*2-1;
				connections.get(i).add(new Connection(j,ranWeight));
			}
		}

		int neurons = 64;
		for (int l=0; l < hiddenLayers-1 ;l++ ) 
		{
			int ini = neurons + neuronsInLayer[l];
			for (; neurons < neuronsInLayer[l] ; neurons++)
			{
				for (int j= ini ; j < ini+neuronsInLayer[l+1];j++)
				{
					double ranWeight = Math.random()*2-1;
					connections.get(neurons).add(new Connection(j,ranWeight));
				}
			}			
		}

		int output = neurons + neuronsInLayer[hiddenLayers-1];
		for (; neurons < neuronsInLayer[hiddenLayers-1] ; neurons++)
		{
			double ranWeight = Math.random()*2-1;
			connections.get(neurons).add(new Connection(output,ranWeight));
		}
	}

	public double evaluate(Board board)
	{

	}

	public double train(Board board, double value)
	{

	}


}
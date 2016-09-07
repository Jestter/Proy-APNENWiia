
public class Agent
{

	String filePath;
	int waitTime;

	Board iniBoard;

	public static void main(String[] args)
	{
		new Agent(args[0],Integer.parseInt(args[1]));
	}

	public Agent(String fPath, int time)
	{
		this.filePath = fPath;
		this.waitTime = time;

		//leer archivo y obtener datos
	}
}
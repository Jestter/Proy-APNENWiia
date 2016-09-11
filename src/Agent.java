
public class Agent
{

	static final int BOARDSIZE=8;	/// The size of the board

	private String filePath;
	private int waitTime;

	private Board iniBoard;

	public static void main(String[] args)
	{
		new Agent(args[0],Integer.parseInt(args[1]));
	}

	public Agent(String fPath, int time)
	{
		this.filePath = fPath;
		this.waitTime = time;
		this.iniBoard = new Board();


		int[][] board = new int[BOARDSIZE][BOARDSIZE];
		BufferedReader bf = new BufferedReader(new FileReader(fPath));
		for(int i = 0;((String line = bf.readLine())!=null);i++)
		{
			
		}

		iniBoard.fromArray(board);

	}

	public void DFS(Board board)
	{
		Move[] validMoves = board.getValidMoves();
		for(Move m : validMoves)
		{
			Board aux = board.clone();
			aux.makeMove(m);
			DFS(aux);
		}

	}
}
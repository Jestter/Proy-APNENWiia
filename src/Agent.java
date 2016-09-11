import java.io.*;

public class Agent
{

	private String filePath;
	private int waitTime;

	private Board iniBoard;
	private int count;

	public static void main(String[] args) throws IOException
	{
		new Agent(args[0],Integer.parseInt(args[1]));
	}

	public Agent(String fPath, int time) throws IOException
	{
		this.filePath = fPath;
		this.waitTime = time;
		this.iniBoard = new Board();
		this.count = 0;

		int[][] board = new int[Board.BOARDSIZE][Board.BOARDSIZE];
		BufferedReader bf = new BufferedReader(new FileReader(fPath));
		int l = 0;
		String line;
		while((line = bf.readLine())!=null)
		{
			if(l < 8)
			{
				String splited[] = line.split(" ");
				for (int j=0; j < 8; j++)
				{
					board[l][j] = Integer.parseInt(splited[j]);
					System.out.print(board[l][j]+" ");
				}
				System.out.println();
			}
			if(l == 8)
			{
				if(line.equals("B"))
				{
					iniBoard.setTurn(Board.TURNWHITE);
				}
				else
				{
					iniBoard.setTurn(Board.TURNBLACK);
				}
			}
			else
			{
				if(line.startsWith("MovsHastaEmpate"))
				{
					iniBoard.setMovesToDraw(Integer.parseInt(line.split(" ")[1]));
				}

				else if(line.startsWith("EnroqueL_B"))
				{
					iniBoard.setLongCastle(iniBoard.turn , true);
				}

				else if(line.startsWith("EnroqueL_N"))
				{
					iniBoard.setLongCastle(iniBoard.turn , true);
				}

				else if(line.startsWith("EnroqueC_B"))
				{
					iniBoard.setShortCastle(iniBoard.turn , true);
				}

				else if(line.startsWith("EnroqueC_N"))
				{
					iniBoard.setShortCastle(iniBoard.turn , true);
				}

				else if(line.startsWith("AlPaso"))
				{
					iniBoard.setEnPassent(new Coord(line.split(" ")[1]));
				}
			}

			l++;
		}

		iniBoard.fromArray(board);
		System.out.println("min prof : "+DFS(iniBoard,0));
	}

	public long DFS(Board board, long depth)
	{
		if(depth > 5)return 10000;
		Move[] validMoves = board.getValidMoves();
		long minDepth = 10000;
		for(Move m : validMoves)
		{
			Board aux = board.clone();
			aux.makeMove(m);
			if(!board.isCheckMate())
			{
				count++;
				//System.out.println(depth);
				minDepth = Math.min(minDepth , DFS(aux, depth+1));
			}
			else
			{
				//System.out.println("Agg");
				return depth;
			}
		}
		return minDepth;
	}
}
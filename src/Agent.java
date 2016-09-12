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
		int l = 0; //contador de lineas
		String line;
		while((line = bf.readLine())!=null)
		{
			if(l < 8)
			{
				String splited[] = line.split(" ");
				for (int j=0; j < 8; j++)
				{
					board[l][j] = Integer.parseInt(splited[j]);
				}
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
		System.out.println("next move : "+ nextMove().toString());
	}

	public Move nextMove()
	{
		Move bestMove = null;
		long bestScoreSoFar = Long.MIN_VALUE;
		Move[] validMoves = iniBoard.getValidMoves();
		for(Move m : validMoves)
		{
			Board aux = iniBoard.clone();
			aux.makeMove(m);
			count++;
			//long score = DFS(aux);
			long score = 0;
			long depth = 0;
			while((score = IDS(aux,depth)) == 0) depth++;
			if(score > bestScoreSoFar) 
			{
				bestMove = m;
				bestScoreSoFar = score;
			}
		}
		return bestMove;
	}

	public long DFS(Board board)
	{
		if(board.isStalemate()) return 0;
		if(board.isCheckMate()) return (board.turn == iniBoard.turn? 0 : 1);

		Move[] validMoves = board.getValidMoves();
		long sum = 0;
		for(Move m : validMoves)
		{
			Board aux = board.clone();
			aux.makeMove(m);
			count++;
			long score = DFS(aux);
			sum += score;
		}

		return sum;
	}

	public long IDS(Board board, long depth)
	{
		if(board.isCheckMate()) return (board.turn == iniBoard.turn? 0 : 1);
		if(board.isStalemate() || depth == 0) return 0;

		Move[] validMoves = board.getValidMoves();
		long sum = 0;
		for(Move m : validMoves)
		{
			Board aux = board.clone();
			aux.makeMove(m);
			count++;
			long score = IDS(aux, depth-1);
			sum += score;
		}

		return sum;
	}

}
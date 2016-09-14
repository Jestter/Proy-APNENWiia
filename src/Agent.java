import java.io.*;

public class Agent
{

	private String filePath;
	private int waitTime;

	private Board iniBoard;
	private int count;

	public static void main(String[] args) throws IOException
	{
		try
		{
			new Agent(args[0],Integer.parseInt(args[1]));
		}
		catch(IOException exc)
		{
			System.out.println("Error al leer archivo: "+args[0]);
		}
	}

	public Agent(String fPath, int time) throws IOException
	{
		this.filePath = fPath;
		this.waitTime = time;
		this.iniBoard = new Board();
		this.count = 0;

		loadTBL();
		
		long tini = System.nanoTime();

		System.out.println("next move : "+ nextMoveDFS().toString()
			+"\nElapsed time: " + ((System.nanoTime()-tini)/1e9) + " s");


		tini = System.nanoTime();

		System.out.println("next move : "+ nextMoveIDS().toString()
			+"\nElapsed time: " + ((System.nanoTime()-tini)/1e9) + " s");
	}

	/** Carga el archivo .tbl especificado en this.filePath
	**
	**/

	public void loadTBL() throws IOException
	{
		int[][] board = new int[Board.BOARDSIZE][Board.BOARDSIZE];
		BufferedReader bf = new BufferedReader(new FileReader(this.filePath));
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
	}

	/**	Realiza una busqueda del siguiente movimiento con DFS 
	** @return el mejor movimiento encontrado (si es que lo encuentra)
	**/

	public Move nextMoveDFS()
	{
		Move bestMove = null;
		int bestScoreSoFar = -2;
		Move[] validMoves = iniBoard.getValidMoves();
	
		for(Move m : validMoves)
		{
			Board aux = iniBoard.clone();
			aux.makeMove(m);
			count++;
			int score = DFS(aux);
			if(score > bestScoreSoFar) 
			{
				bestMove = m;
				bestScoreSoFar = score;
				if(bestScoreSoFar == 1)break;
			}
		}
	
		return bestMove;
	}

	/** Realiza una busqueda del siguiente movimiento con IDS 
	** @return el mejor movimiento encontrado
	**/

	public Move nextMoveIDS()
	{
		Move bestMove = null;
		int bestScoreSoFar = -2;
		Move[] validMoves = iniBoard.getValidMoves();
		long depth = 0;
		while(bestScoreSoFar != 1)
		{
			//System.out.println("depth: "+depth);
			for(Move m : validMoves)
			{
				Board aux = iniBoard.clone();
				aux.makeMove(m);
				count++;
				//long score = DFS(aux);
				int score = IDS(aux,depth);
				if(score > bestScoreSoFar) 
				{
					bestMove = m;
					//System.out.println("MoveChanged to " + m.toString());
					bestScoreSoFar = score;
					if(bestScoreSoFar == 1)break;
				}
			}
			//System.out.println("mejor encontrado: "+bestScoreSoFar);
			depth++;
		}
		return bestMove;
	}

	/** Funcion Minimax implementada con busqueda con DFS
	** @return el resultado de minimax
	**/

	private int DFS(Board board)
	{
		if(board.isCheckMate()) return (board.turn == iniBoard.turn? -1 : 1);
		if(board.isStalemate()) return 0;

		Move[] validMoves = board.getValidMoves();
		int bestVal = (board.turn == iniBoard.turn? -1 : 1);

		for(Move m : validMoves)
		{
			Board aux = board.clone();
			aux.makeMove(m);
			count++;
			int score = DFS(aux);
			if(board.turn == iniBoard.turn)
			{
				bestVal = Math.max(bestVal, score);
				if(bestVal == 1) return 1;
			}
			else
			{
				bestVal = Math.min(bestVal, score);
				if(bestVal == -1) return -1;
			}
		}

		return bestVal;
	}

	/** Funcion Minimax implementada con busqueda IDS
	** @return el valor encontrado del minimax
	**/

	private int IDS(Board board, long depth)
	{
		if(board.isCheckMate()) return (board.turn == iniBoard.turn? -1 : 1);
		if(board.isStalemate() || depth == 0) return 0;

		Move[] validMoves = board.getValidMoves();
		int bestVal = (board.turn == iniBoard.turn? -1 : 1);

		for(Move m : validMoves)
		{
			Board aux = board.clone();
			aux.makeMove(m);
			count++;
			int score = IDS(aux, depth-1);
			if(board.turn == iniBoard.turn)
			{
				bestVal = Math.max(bestVal, score);
				if(bestVal == 1) return 1;
			}
			else
			{
				bestVal = Math.min(bestVal, score);
				if(bestVal == -1) return -1;
			}
		}

		return bestVal;
	}

}
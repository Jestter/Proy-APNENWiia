import java.io.*;

public class Agent
{
	/**
   * Dirección del archivo .tbl a usar en la ejecución
   */
	String filePath;

	/**
   * Tiempo máximo de espera para la búsqueda
   */
	int waitTime;

	/**
   * Tablero inicial para la búsqueda
   */
	Board iniBoard;
	
	private int expanded;

	public static void main(String[] args)
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

	/** Constructor para el Agente.
	**	Imprime por stdout el tiempo requerido y nodos expandidos para cada búsqueda.
	** 
	** @param fPath dirección del archivo .tbl a utilizar
	** @param time tiempo máximo para la búsqueda
	** @throws IOException
	**/
	public Agent(String fPath, int time) throws IOException
	{
		this.filePath = fPath;
		this.waitTime = time;
		this.iniBoard = new Board();
		this.expanded = 0;

		loadTBL();
		
		long tini = System.nanoTime();

		System.out.println("Searching next move using IDS...");
		System.out.println("next move IDS: "+ nextMoveIDS().toString()
			+"\nElapsed time: " + ((System.nanoTime()-tini)/1e9) + " s"
			+"\nNodes expanded: " + this.expanded+ "\n");

		tini = System.nanoTime();
		System.out.println("Searching next move using DFS...");
		System.out.println("next move DFS: "+ nextMoveDFS().toString()
			+"\nElapsed time: " + ((System.nanoTime()-tini)/1e9) + " s"
			+"\nNodes expanded: " + this.expanded);
	}

	/** Carga el archivo .tbl especificado en filePath
	**	@throws IOException
	**/
	public void loadTBL() throws IOException
	{
		int[][] board = new int[Board.BOARDSIZE][Board.BOARDSIZE];
		BufferedReader bf = new BufferedReader(new FileReader(this.filePath));
		int l = 0; //contador de lineas
		String line;
		while((line = bf.readLine())!=null)
		{
			//verifica si aun se lee la posicion de las piezas
			if(l < 8)
			{
				String splited[] = line.split(" ");
				for (int j=0; j < 8; j++)
				{
					board[l][j] = Integer.parseInt(splited[j]);
				}
			}
			//verifica si está en la linea que especifica el jugador actual
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
			//lee información adicionl del archivo
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

	/**	Realiza una búsqueda del siguiente movimiento con DFS y a lo más 4 niveles de profundidad
	** @return el mejor movimiento encontrado (si es que lo encuentra)
	**/
	public Move nextMoveDFS()
	{
		Move bestMove = null;
		this.expanded = 0;
		int bestScoreSoFar = -2;
		Move[] validMoves = iniBoard.getValidMoves();
	
		//se expande a los siguientes movimientos
		for(Move m : validMoves)
		{
			this.expanded++;
			Board aux = iniBoard.clone();
			aux.makeMove(m);
			int score = DFS(aux, 4);
			//se actualiza la mejor opcion
			if(score > bestScoreSoFar) 
			{
				bestMove = m;
				bestScoreSoFar = score;
				if(bestScoreSoFar == 1)break;
			}
		}
	
		return bestMove;
	}

	/** Realiza una búsqueda del siguiente movimiento con IDS (aumentando la profundidad de DFS de forma iterativa) 
	** @return el mejor movimiento encontrado
	**/
	public Move nextMoveIDS()
	{
		Move bestMove = null;
		int bestScoreSoFar = -2;
		Move[] validMoves = iniBoard.getValidMoves();
		long depth = 0;

		//se aumenta de forma iterativa la profundidad
		while(bestScoreSoFar != 1)
		{
			this.expanded = 0;

			//se expande a los siguientes movimientos
			for(Move m : validMoves)
			{
				this.expanded++;
				Board aux = iniBoard.clone();
				aux.makeMove(m);
				int score = DFS(aux,depth);
				//se actualiza la mejor opcion
				if(score > bestScoreSoFar) 
				{
					bestMove = m;
					bestScoreSoFar = score;
					if(bestScoreSoFar == 1)break;
				}
			}
			depth++;
		}
		return bestMove;
	}

	/** Funcion Minimax implementada con búsqueda DFS con límite de profundidad
	** @param board tablero a revisar y expandir
	** @param depth profundidad restante para cortar la búsqueda
	** @return el valor encontrado del minimax
	**/
	private int DFS(Board board, long depth)
	{
		//se revisa si es nodo terminal o pasó el limite de profundidad
		if(board.isCheckMate()) return (board.turn == iniBoard.turn? -1 : 1);
		if(board.isStalemate() || depth == 0) return 0;

		Move[] validMoves = board.getValidMoves();
		int bestVal = (board.turn == iniBoard.turn? -1 : 1);

		//se expande a los siguintes movimientos
		for(Move m : validMoves)
		{
			this.expanded++;
			Board aux = board.clone();
			aux.makeMove(m);
			int score = DFS(aux, depth-1);
			//se propaga el mejor valor para el jugador actual
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
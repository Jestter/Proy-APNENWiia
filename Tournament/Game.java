import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class Game {

	/********* CHANGE THIS TO THE COMMAND TO RUN YOUR AGENT ********/
	public static String AGENT1COMMAND="java IDSAgent 2";



	public static String RANDOMAGENTCOMMAND="java RandomAgent";
	int timelimit=2; // TIME LIMIT IN MINUTES




	public static int NOBODY=-1;
	public static int AGENT0=0;	
	public static int AGENT1=1;	

	
	public Agent[] agent;


	Process currentProcess;

	int currentagent;
	public int winner;
	public int loser;
	boolean gameover;
	public boolean draw;

	Game() {
		winner=NOBODY;
		loser=NOBODY;
		currentagent=NOBODY;
		draw=false;
		agent=new Agent[10];
	}

	public int nextAgent() {
		if (currentagent==AGENT0) return AGENT1;
		else return AGENT0;
	}	

	
	/** Run a game between two agents.
	**/
	public int runGame(IDSAgent agent1, IDSAgent agent2) {
		//choose randomly which agent goes first

		//create a new board
		Board b=new Board();
		IDSAgent[] agents = new IDSAgent[2];
		agents[0]=agent1;
		agents[1]=agent2;
		
		gameover=false;
		currentagent=AGENT0;
		Move move;
		int currentmove=0;
		while (!gameover) {
			//System.out.println(b);
			if (!b.isStalemate() && !b.isCheckMate()) {
				move=agents[currentagent].getBestMove(b, 3);
				//System.out.println(move);
				if (!b.validMove(move)) {	
					return nextAgent();
				}
				else b.makeMove(move);
			//	System.out.println(b);
			} else if (b.isStalemate()) {
				return NOBODY;// nobody wins
			} else if (b.isCheckMate()) {
				return nextAgent();
				//agents[currentagent].addLoss();
			}
			currentagent=nextAgent();
			currentmove++;
		};
		return -1;
	}





	public static void main(String[] args){
		IDSAgent a1=new IDSAgent(IDSAgent.MINIMAX);
		a1.utility=new MaterialValue();
		IDSAgent a2=new IDSAgent(IDSAgent.ALPHABETA);
		a2.utility=new MaterialValue();
		Game g=new Game();
		System.out.println(g.runGame(a1,a2));

	}
}
import java.io.*;
import java.util.*;

public class Trainer
{
	public static String TRAININGANGENT ="java HotSingleAgent 2";
	public static String ENEMY ="java RandomAgent";

	public static final double LAMBDA = 0.5;

	//This is the trainer class, it trains the NN with TD method. Please especify the enemy agent above :p
	public Trainer(int iter)
	{
		for (int i=0; i < iter ; i++)
		{
			System.out.println("Iteration: "+ (i+1));
			new TrainingInstance();	
		}
	}

	public static void main(String[] args) {
		if(args.length < 1 || args.length > 1){ System.out.println("argumentos invalidos. ingrese cantidad de iteraciones."); return;}
		new Trainer(Integer.parseInt(args[0]));
	}

	public class TrainingInstance
	{

		public TrainingInstance()
		{
			Server s = new Server();
			Agent A1 =new Agent("Training Agent", TRAININGANGENT);
			Agent A2 =new Agent("Oponent", ENEMY);
			
			System.out.println("init GAME!");
			s.runGame(A1, A2);
			System.out.println("init Training");
			double learnValue = 0;
			if(!s.draw)
			{
				if(s.winner == Server.AGENT0) learnValue = 1;
				else learnValue = -1;
			}

			System.out.println("learnValue = "+learnValue);

			ArrayList<String> bseq = (ArrayList<String>)Util.loadObject("boardSequence.train");
			if(bseq!=null)for (int l = bseq.size()-1; l >= 0 ; l--) 
			{
				int[][] board = new int[8][8];
				boolean readOK = true;
				Board b=new Board();
				try 
				{
					BufferedReader input =   new BufferedReader(new FileReader(bseq.get(l)));
					for (int i=0; i<8; i++) {
						String line=input.readLine();
						String[] pieces=line.split("\\s");
						for (int j=0; j<8; j++) {
							board[i][j]=Integer.parseInt(pieces[j]);
						}
					}
					String turn=input.readLine();
					b.fromArray(board);
					if (turn.equals("N")) b.setTurn(b.TURNBLACK);
					else b.setTurn(b.TURNWHITE);
					b.setShortCastle(b.TURNWHITE,false);
					b.setLongCastle(b.TURNWHITE,false);
					b.setShortCastle(b.TURNBLACK,false);
					b.setLongCastle(b.TURNBLACK,false);
				
					String st=input.readLine();
					while (st!=null) {
						if (st.equals("EnroqueC_B")) b.setShortCastle(b.TURNWHITE,true);
						if (st.equals("EnroqueL_B")) b.setLongCastle(b.TURNWHITE,true);
						if (st.equals("EnroqueC_N")) b.setShortCastle(b.TURNBLACK,true);
						if (st.equals("EnroqueL_N")) b.setLongCastle(b.TURNBLACK,true);
						st=input.readLine();
				}
				} catch (Exception e) {readOK = false;}

				if(readOK)
				{
					Network net = (Network)Util.loadObject("TDNetwork.nn");
					if(net == null)
					{
						net = new Network(2,new int[]{256,256},new double[]{0,0},0.03);
					}
					net.train(b, ((learnValue+1)/2));
					//TD lambda training :D
					learnValue = LAMBDA * learnValue + (1 - LAMBDA) * (net.evaluate(b)*2-1);
					Util.saveObject(net,"TDNetwork.nn");
				}
			}
			System.out.println("Removing boardSequence.");
			File f = new File("boardSequence.train");
			if(f.delete()){System.out.println("Removed.");}
			System.out.println("Training Done!");
		}

	}
}
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Trainer
{
	public static String TRAININGANGENT ="java HotSingleAgent 2";
	public static String ENEMY ="java IDSAgent 2";

	public static final double LAMBDA = 0.5;

	public int won;
	public int lost;
	public int draws;
	public Semaphore currThreads;
	public Semaphore networkFileLock;
	public Semaphore copySequence;
	//This is the trainer class, it trains the NN with TD method. Please especify the enemy agent above :p
	public Trainer(int iter, int maxThreads)
	{
		File dir = new File("wins");
		if(!dir.exists())dir.mkdir();
		dir = new File("draws");
		if(!dir.exists())dir.mkdir();
		dir = new File("lost");
		if(!dir.exists())dir.mkdir();

		won = 0;
		lost = 0;
		draws = 0;
		currThreads = new Semaphore(maxThreads);
		networkFileLock = new Semaphore(1);
		copySequence = new Semaphore(1);
		for (int i=0; i < iter ; i++)
		{
			while(currThreads.getQueueLength() >= 4);
			new Thread(new TrainingInstance(i+1)).start();
		}
	}

	//Constructor for batch training using one of the folders: wins,lost,draws.
	public Trainer(int iter,String dir)
	{
		double constLearnValue = dir.equals("wins")?1:(dir.equals("lost")?-1:0);
		System.out.println("learning: "+constLearnValue);
		File dirf = new File(dir);
		int totalfiles = dirf.listFiles().length;
		int counter = 1;
		for (File f: dirf.listFiles())
		{
			//System.out.print("*");
			Network net = (Network)Util.loadObject("TDNetwork.nn");
			if(net == null)
			{
				net = new Network(2,new int[]{256,256},new double[]{0,0},0.03);
			}
			ArrayList<String> bseq = (ArrayList<String>)Util.loadObject(f.getAbsolutePath());
			if(bseq!=null)
			{
				for (int ii=0; ii < iter ; ii++) 
				{
					double learnValue = constLearnValue;
					for (int l = bseq.size()-1; l >= 0 ; l--) 
					{
						//System.out.println("training board: "+bseq.get(l));
						int[][] board = new int[8][8];
						boolean readOK = true;
						Board b=new Board();
						try 
						{
							BufferedReader input =   new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bseq.get(l).getBytes())));
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
							net.train(b, (learnValue+1)/2);
							//TD lambda training :D
							learnValue = LAMBDA * learnValue + (1 - LAMBDA) * (net.evaluate(b)*2-1);
						}
					}	
				}
			}
			Util.saveObject(net,"TDNetwork.nn");
			System.out.println(counter+"/"+totalfiles+" files done");
			counter++;
		}
	}

	public static void main(String[] args) {
		if(args.length < 3 || args.length > 3)
		{
			System.out.println("argumentos invalidos. ingrese:");
			System.out.println("\tjava Trainer standard <iteraciones> <num de threads>");
			System.out.println("\tjava Trainer batch <iteraciones> <carpeta>");
			return;
		}
		if(args[0].equals("standard"))
		{
			new Trainer(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		}
		else if(args[0].equals("batch"))
		{
			System.out.println("Starting Batch Training using folder "+args[2]);
			new Trainer(Integer.parseInt(args[1]), args[2]);
		}
	}

	public class TrainingInstance implements Runnable
	{
		int trainNumber;

		public TrainingInstance(int trainNumber)
		{
			this.trainNumber = trainNumber;
		}

		public void run()
		{
			try
			{
				currThreads.acquire();	
				System.out.println("Iteration: "+ trainNumber);
				Server s = new Server();
				s.trainNumber = this.trainNumber;
				Agent A1 =new Agent("Training Agent", TRAININGANGENT);
				Agent A2 =new Agent("Oponent", ENEMY);

				System.out.println("Training "+trainNumber+": "+"init GAME!");
				int totalMoves = s.runGame(A1, A2);
				System.out.println("Training "+trainNumber+": "+"init Training");
				double learnValue = 0;
				String folder = "draws";
				if(!s.draw)
				{
					if(s.winner == Server.AGENT0) {learnValue = 1;won++;folder = "wins";}
					else {learnValue = -1;lost++;folder = "lost";}
				}
				else draws++;

				try
				{
					copySequence.acquire();
					Path path1 = new File("boardSequence"+trainNumber+".train").toPath();
					Path path2 = new File(searchNewFileName(folder+"/"+totalMoves+"boardSequence","train")).toPath();
					Files.copy(path1, path2, StandardCopyOption.REPLACE_EXISTING);
					copySequence.release();
				}catch(Exception ex){ex.printStackTrace();}

				System.out.println("Training "+trainNumber+": "+"learnValue = "+learnValue);

				networkFileLock.acquire();
				Network net = (Network)Util.loadObject("TDNetwork.nn");
				if(net == null)
				{
					net = new Network(2,new int[]{256,256},new double[]{0,0},0.03);
				}
				ArrayList<String> bseq = (ArrayList<String>)Util.loadObject("boardSequence"+trainNumber+".train");
				if(bseq!=null)
				{
					for (int l = bseq.size()-1; l >= 0 ; l--) 
					{
						//System.out.println("training board: "+bseq.get(l));
						int[][] board = new int[8][8];
						boolean readOK = true;
						Board b=new Board();
						try 
						{
							BufferedReader input =   new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bseq.get(l).getBytes())));
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
							net.train(b, (learnValue+1)/2);
							//TD lambda training :D
							learnValue = LAMBDA * learnValue + (1 - LAMBDA) * (net.evaluate(b)*2-1);
						}
					}
				}
				Util.saveObject(net,"TDNetwork.nn");

				networkFileLock.release();

				System.out.println("Training "+trainNumber+": "+"Removing boardSequence.");
				File f = new File("boardSequence"+trainNumber+".train");
				if(f.delete()){System.out.println("Training "+trainNumber+": "+"Removed.");}
				System.out.println("Training "+trainNumber+": "+"Training Done! won:"+won+" draws:"+draws+" lost:"+lost);
				currThreads.release();
			}catch(Exception ex){System.out.println("Training "+trainNumber+": "+"Interrupted Exception Thrown.");}
		}

		private String searchNewFileName(String path,String ext)
		{
			int i=1;
			while(new File(path+i+"."+ext).exists())i++;

			return (path+i+"."+ext);
		}
	}
}
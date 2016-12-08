import java.lang.Math;
import java.io.*;
import java.util.*;

public class Util
{

	public static double sigmoidFunction(double val)
	{
		return (1.0/(1.0+Math.pow(Math.E,-val)));
	}

	public static boolean saveObject(Object obj,String filename)
	{
		try(FileOutputStream fileOut = new FileOutputStream(filename);
		    ObjectOutputStream out = new ObjectOutputStream(fileOut))
		{
		    out.writeObject(obj);
      	}
      	catch(Exception i) 
      	{
        	return false;
      	}
      	return true;
	}

	public static Object loadObject(String filename)
	{
		Object obj = null;
		try(FileInputStream fileIn = new FileInputStream(filename);
		    ObjectInputStream in = new ObjectInputStream(fileIn))
		{
		    obj = in.readObject();
      	}
      	catch(Exception i) 
      	{
        	return null;
      	}
      	return obj;
	}

	/*
		return a value representing how close is the player to a pawn promotion
	*/
	public static int closerPromotion(Board board, int turn)
	{
		int whoMoves = turn;
		int pawn = whoMoves*1;
		int start = whoMoves==1?7:0;
		int fartherPawn = start;
		for (int i=0; i < 8 ; i++)
		{
			for (int j=0;j < 8 ;j++) 
			{
				//piece is a pawn
				if(board.getPiece(new Coord(i,j)) == pawn)
				{
					fartherPawn = (int)Math.abs(Math.max(fartherPawn*whoMoves,j*whoMoves));
				}
			}	
		}
		return ((int)Math.abs(start-fartherPawn));
	}

	/*
		A value representing the how many pieces are at the oposite side of the board
	*/
	public static int fatherDevelopment(Board board,int turn)
	{
		int count = 0;
		for (int i=0; i < 8 ; i++)
		{
			for (int j=0;j < 8 ;j++) 
			{
				Coord coord = new Coord(i,j);
				if(board.getPiece(coord)*turn > 0 && i*turn > 3*turn)
				{
					count++;
				}
			}
		}
		return count;
	}

	/*
		A value representing the number threathened positions near player's king
	*/
	public static int potentialThread(Board board, int turn)
	{
		int whoMoves = turn;
		int king = whoMoves*6; //king

		//looking for king
		Coord posKing = null;
		for (int i=0; i < 8 ; i++)
		{
			for (int j=0;j < 8 ;j++) 
			{
				Coord coord = new Coord(i,j);
				//piece is king
				if(board.getPiece(coord) == king)
				{
					posKing = coord;
					break;
				}
			}
			if(posKing!=null)break;
		}

		Coord[] km=new Coord[8];
		km[0]=new Coord(posKing.x+1, posKing.y+1);
		km[1]=new Coord(posKing.x+1, posKing.y-1);
		km[2]=new Coord(posKing.x, posKing.y+1);
		km[3]=new Coord(posKing.x, posKing.y-1);
		km[4]=new Coord(posKing.x-1, posKing.y+1);
		km[5]=new Coord(posKing.x-1, posKing.y-1);
		km[6]=new Coord(posKing.x+1, posKing.y);
		km[7]=new Coord(posKing.x-1, posKing.y);
		
		int count = 0;
		for (Coord coord : km) 
		{
			if(board.isThreatened(coord, whoMoves))
			{
				count++;
			}
		}

		return count;
	}
}
/***************************************************************************
 *   Copyright (C) 2009 by Matthew Bardeen   *
 *   mbardeen@utalca.cl   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
import java.util.ArrayList;
import java.io.File;

public class NeuralNetwork extends Heuristic
{
	private static final String networkPath = "TDNetwork.nn";
	private Network network;

	public NeuralNetwork()
	{
		File f = new File(networkPath);
		if(!f.exists())
		{ 
		 	network = new Network(2,new int[]{256,256},new double[]{0,0},0.03);
		}
		else
		{
			network = (Network)Util.loadObject(networkPath);
		}

		if(network == null)
		{
			network = new Network(2,new int[]{256,256},new double[]{0,0},0.03);
		}
	}
	
	/**
	    Takes a board and returns the heuristic value of the board
	**/
	public int evaluate(Board inb) 
	{
	   return (int)(100*(this.network.evaluate(inb)*2-1));
	}

}
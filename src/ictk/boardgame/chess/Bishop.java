/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Bishop.java,v 1.1.1.1 2003/03/24 22:38:07 jvarsoke Exp $
 *
 *  This file is part of ICTK.
 *
 *  ICTK is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  ICTK is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ICTK; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ictk.boardgame.chess;

import java.util.Arrays;
import java.util.List;

/* Bishop *******************************************************************/
/** the Bishop piece for the game of chess
 */
public class Bishop extends ChessPiece {
     /** used to indicate what type of piece this is w/o instanceof */
   public final static byte    INDEX  = 3;

   protected static final int MAX_LEGAL_DESTS = 13,
                          MAX_GUARDS      = 13;

   public Bishop () {
      super(INDEX, true, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Bishop (boolean blackness) {
      super(INDEX, blackness, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Bishop (boolean blackness, Square o, ChessBoard _board) {
      super(INDEX, blackness, o, _board, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   /* getName ***************************************************************/
   /** returns the English name for the Piece.  Used in debugging info.
    */
   protected String getName () { return "Bishop"; }

   //LegalDests////////////////////////////////////////////////////////////
 
   /* genLegalDests *********************************************************/
   protected int genLegalDests () {
      super.genLegalDests();
      Square dest;
      boolean done;

	 if (captured) return 0;

 //NEED: change over to board.getLineOfSight()
         //northeast
         done = false;
 
         for (int f=orig.file+1, r=orig.rank+1;
              f <= board.MAX_FILE && r <= board.MAX_RANK && !done;
              f++, r++) {
 
             dest = board.getSquare(f, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }
 
         //southeast
         done = false;
 
         for (int f=orig.file+1, r=orig.rank-1;
              f <= board.MAX_FILE && r > 0 && !done;
              f++, r--) {
 
             dest = board.getSquare(f, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }
 
         //southwest
         done = false;
 
         for (int f=orig.file -1, r=orig.rank -1;
              f > 0 && r > 0 && !done;
              f--, r--) {
 
             dest = board.getSquare(f, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }
 
         //northwest
         done = false;
 
         for (int f=orig.file-1, r=orig.rank+1;
              f > 0 && r <= board.MAX_RANK && !done;
              f--, r++) {
 
             dest = board.getSquare(f, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }
 
         return legalDests.size();
   }
   
   /* adjustPinsLegalDests **********************************************/
   public void adjustPinsLegalDests (ChessPiece king, List enemyTeam) {
      Square[] line = getLineOfSight(king, false);
      ChessPiece pin = null, tmp;
      boolean done = false;

      if (captured) return;

      if (line != null) {
         if (board.staleLegalDests)
	    board.genLegalDests();

	 //remember to ignore [0] since it is this piece's square
         for (int i=1; i < line.length && !done; i++) {

            tmp = line[i].getOccupant();

	    if (tmp != null) {

	       //two pieces blocking the attack is not a pin
	       if (pin != null)  {
	          pin = null;
		  done = true;
	       } 

	       //friend in the way
	       else if (tmp.isBlack == this.isBlack())
		  done = true;

	       //found a possible pin
	       else
	          pin = tmp;
	    }
	 }

	 if (pin != null) {
	    //need to AND moves with line
	    List maintainPins = Arrays.asList(line); //includes this square
	    pin.setPinned(this, maintainPins);
	 }
      }
   }

   /* getLineOfSight ****************************************************/
   public Square[] getLineOfSight (ChessPiece target, boolean inclusive) {
      return getLineOfSight(target.getSquare().file, 
                            target.getSquare().rank, 
			    inclusive);
   }
   public Square[] getLineOfSight (Square target, boolean inclusive) {
      return getLineOfSight(target.file, target.rank, inclusive);
   }

   public Square[] getLineOfSight (int t_file, int t_rank, boolean inclusive) {
      Square[] return_set = null;
      Square[] return_tmp = new Square[7];
      int o_file = getSquare().file,
          o_rank = getSquare().rank;
      int f = 0, r = 0, 
	  i = 0;  //return square counter

	  //forward slash diag  ex: 0,0 - 7,7 or 1,4 - 2,5
	  if ((o_file - t_file) == (o_rank - t_rank)) {
	     //target is northeast
	     if (o_rank < t_rank) {
	        for (f = o_file+1, r = o_rank+1; r <= t_rank; f++, r++)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	     //target is southwest
	     else {
	        for (f = o_file-1, r = o_rank-1; r >= t_rank; f--, r--)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	  }
	  //backslash diag ex: 0,7 - 7,0
	  else if ((o_file - t_file) == ((o_rank - t_rank) * -1)) {
	     //target is northwest
	     if ((o_rank - t_rank) < 0) {
	        for (f = o_file-1, r = o_rank+1; r <= t_rank; f--, r++)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	     //target is southeast
	     else {
	        for (f = o_file+1, r = o_rank-1; r >= t_rank; f++, r--)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	  }

          //origin and target were not in-line if i==0

	  if (i != 0) { 
	     return_set = new Square[i+1];
	     return_set[0] = this.getSquare();  //put self in 0th spot
	     System.arraycopy(return_tmp, 0, return_set, 1, i); 
	  }

	  return return_set;
   }

   public boolean isBishop () { return true; }
}

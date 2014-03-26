/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ictk.boardgame.chess;

import java.util.Arrays;
import java.util.List;

public class Rook extends ChessPiece {
   public final static byte    INDEX           = 2;
   protected static final int  MAX_LEGAL_DESTS = 14,
                               MAX_GUARDS      = 14;

   public Rook () {
      super(INDEX, true, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Rook (boolean blackness) {
      super(INDEX, blackness, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Rook (boolean blackness, Square o, ChessBoard _board) {
      super(INDEX, blackness, o, _board, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   protected String getName () { return "Rook"; }

   //LegalDests////////////////////////////////////////////////////////////

   /* genLegalDests ******************************************************/
   protected int genLegalDests () {
      super.genLegalDests();
      Square dest;
      boolean done;

	 if (captured) return 0;

         //north
         done = false;

//NEED: use getLineOfSight()
         //north
         for (int r=orig.rank+1;r <= ChessBoard.MAX_RANK && !done; r++) {
             dest = board.getSquare(orig.file, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }

         //east
         done = false;
 
         for (int f=orig.file+1;f <= ChessBoard.MAX_FILE && !done; f++) {
             dest = board.getSquare(f, orig.rank);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }

         //south
         done = false;
 
         for (int r=orig.rank-1;r >= 1 && !done; r--) {
             dest = board.getSquare(orig.file, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }
 
         //west
         done = false;
 
         for (int f=orig.file-1;f >= 1 && !done; f--) {
             dest = board.getSquare(f, orig.rank);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }

         return legalDests.size();
   }

   /* adjustPinsLegalDests **********************************************/
   public void adjustPinsLegalDests (ChessPiece king, List<ChessPiece> enemyTeam) {
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

         //okay we have a pin
	 //legal moves for the pinned piece are those which maintian the pin
	 if (pin != null) {
	    //need to AND moves with line
	    List<Square> maintainPins = Arrays.asList(line); //includes this square
	    pin.setPinned(this, maintainPins);
	    //pin.legalDests.retainAll(maintainPins);
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

          //same file
	  if (o_file == t_file) {
             //target is north
	     if (o_rank < t_rank) {
	        for (r = o_rank+1; r <= t_rank; r++)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(o_file, r);
	     }
	     //target is south
	     else {
	        for (r = o_rank-1; r >= t_rank; r--)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(o_file, r);
	     }
	  }

	  //same rank
	  else if (o_rank == t_rank) {
	     //target is east
	     if (o_file < t_file) {
	        for (f = o_file+1; f <= t_file; f++)
	           if (f != t_file || inclusive) 
		      return_tmp[i++] = board.getSquare(f, o_rank);
	     }
	     //target is west
	     else {
	        for (f = o_file-1; f >= t_file; f--)
	           if (f != t_file || inclusive) 
		      return_tmp[i++] = board.getSquare(f, o_rank);
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

   public boolean isRook () { return true; }
}

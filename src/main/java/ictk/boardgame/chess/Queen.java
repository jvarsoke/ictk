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

public class Queen extends ChessPiece {
   public final static byte    INDEX           = 1;
   protected static final int  MAX_LEGAL_DESTS = 27,
                               MAX_GUARDS      = 27;

   public Queen () {
      super(INDEX, true, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Queen (boolean blackness) {
      super(INDEX, blackness, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Queen (boolean blackness, Square o, ChessBoard _board) {
      super(INDEX, blackness, o, _board, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   protected String getName () { return "Queen"; }

   //LegalDests////////////////////////////////////////////////////////////

   /* genLegalDests ******************************************************/
   protected int genLegalDests () {
      super.genLegalDests();
      Square dest;
      boolean done = false;

	 if (captured) return 0;
 
 //FIXME: change over to board.getLineOfSight()
         //north
         for (int r=orig.rank+1;r <= ChessBoard.MAX_RANK && !done; r++) {
             dest = board.getSquare(orig.file, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }

         //northeast
         done = false;
 
         for (int f=orig.file+1, r=orig.rank+1;
              f <= ChessBoard.MAX_FILE && r <= ChessBoard.MAX_RANK && !done;
              f++, r++) {
 
             dest = board.getSquare(f, r);
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

         //southeast
         done = false;
 
         for (int f=orig.file+1, r=orig.rank-1;
              f <= ChessBoard.MAX_FILE && r > 0 && !done;
              f++, r--) {
 
             dest = board.getSquare(f, r);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }

         //south
         done = false;
 
         for (int r=orig.rank-1;r > 0 && !done; r--) {
             dest = board.getSquare(orig.file, r);
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
 
         //west
         done = false;
 
         for (int f=orig.file-1;f > 0 && !done; f--) {
             dest = board.getSquare(f, orig.rank);
             done = !addLegalDest(dest);
             //done = (done || dest.isOccupied());
             done = (done || (dest.isOccupied() && 
	            !(board.isBlackMove != isBlack 
		       && dest.piece.isKing())));
         }

         
         //northwest
         done = false;
 
         for (int f=orig.file-1, r=orig.rank+1;
              f > 0 && r <= ChessBoard.MAX_RANK && !done;
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

	 if (pin != null) {
	    //need to AND moves with line
	    List<Square> maintainPins = Arrays.asList(line); //includes this square
	    pin.setPinned(this, maintainPins);
	 }
      }
   }

   public boolean isQueen () { return true; }
}

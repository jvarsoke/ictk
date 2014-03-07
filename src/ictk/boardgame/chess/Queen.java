/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Queen.java,v 1.1.1.1 2003/03/24 22:38:07 jvarsoke Exp $
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
         for (int r=orig.rank+1;r <= board.MAX_RANK && !done; r++) {
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
              f <= board.MAX_FILE && r <= board.MAX_RANK && !done;
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
 
         for (int f=orig.file+1;f <= board.MAX_FILE && !done; f++) {
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
              f <= board.MAX_FILE && r > 0 && !done;
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

   public boolean isQueen () { return true; }
}

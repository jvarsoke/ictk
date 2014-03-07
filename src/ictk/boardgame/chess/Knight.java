/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Knight.java,v 1.1.1.1 2003/03/24 22:38:07 jvarsoke Exp $
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

/* Knight *****************************************************************/
public class Knight extends ChessPiece {
   public final static byte    INDEX           = 4;
   protected static final int  MAX_LEGAL_DESTS = 8,
                               MAX_GUARDS      = 8;

   public Knight () {
      super(INDEX, true, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Knight (boolean blackness) {
      super(INDEX, blackness, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Knight (boolean blackness, Square o, ChessBoard _board) {
      super(INDEX, blackness, o, _board, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   protected String getName () { return "Knight"; }

   //LegalDests////////////////////////////////////////////////////////////
 
   /* genLegalDests *******************************************************/
   protected int genLegalDests () {
      super.genLegalDests();
      Square dest;
      byte f,r;

	 if (captured) return 0;

         //yes, I know there is a more effecient way to do this
         //but I like the way it looks

         //one o'clock
         f = (byte) (orig.file + 1);
         r = (byte) (orig.rank + 2);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //two o'clock
         f = (byte) (orig.file + 2);
         r = (byte) (orig.rank + 1);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //four o'clock
         f = (byte) (orig.file + 2);
         r = (byte) (orig.rank - 1);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //five o'clock
         f = (byte) (orig.file + 1);
         r = (byte) (orig.rank - 2);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //seven o'clock
         f = (byte) (orig.file - 1);
         r = (byte) (orig.rank - 2);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //eight o'clock
         f = (byte) (orig.file - 2);
         r = (byte) (orig.rank - 1);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //ten o'clock
         f = (byte) (orig.file - 2);
         r = (byte) (orig.rank + 1);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         //eleven o'clock
         f = (byte) (orig.file - 1);
         r = (byte) (orig.rank + 2);
         if (board.isFileValid(f) && board.isRankValid(r)) 
            addLegalDest(board.getSquare(f,r));

         return legalDests.size();
    }

    /* isBlockable **********************************************************/
    public boolean isBlockable (Square target) {
       return false;
    }


   /* isBlockable ********************************************************/
   public boolean isBlockable (Square blocker, ChessPiece target) {
     return false;
   }

    public boolean isKnight() { return true; }
}

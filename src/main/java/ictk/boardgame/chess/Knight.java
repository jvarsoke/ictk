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

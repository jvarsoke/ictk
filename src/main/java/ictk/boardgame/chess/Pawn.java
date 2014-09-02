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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Pawn extends ChessPiece {
   public final static byte    INDEX           = 5;
   protected static final int  MAX_LEGAL_DESTS = 4,
                               MAX_GUARDS      = 2;


   public Pawn () {
      super(INDEX, true, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Pawn (boolean blackness) {
      super(INDEX, blackness, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public Pawn (boolean blackness, Square o, ChessBoard _board) {
      super(INDEX, blackness, o,  _board, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   protected String getName () {
      return "Pawn";
   }

   //Defaults//////////////////////////////////////////////////////////////
   /*
   static public boolean isDefaultSquare (Square sq) {
      return (sq.rank == 1 || sq.rank == 6); //2nd & 7th Rank
   }
   */

   //LegalDests////////////////////////////////////////////////////////////

   /* genLegalDests() *****************************************************/
   protected int genLegalDests () {
      super.genLegalDests();
      Square dest = null;
      boolean done;
      byte dir,                //direction of travel
          file,
          rank;
     
	 if (captured) return 0;

         dir = (isBlack) ? (byte)-1 : (byte) 1;

         //one step
         if (board.isRankValid((byte) (orig.rank + (1 * dir)))) {
            dest = board.getSquare(orig.file, 
                                   (byte) (orig.rank + (1 * dir)));
	    if (!dest.isOccupied()) 
               addLegalDest(dest);
         }

         //the kill 
         if (board.isRankValid(rank = (byte) (orig.rank + (1 * dir)))) {
            file = orig.file; 

            //if valid file
	    //and the square is oocupied (by the other team)
	    //    or  it's not my move (so I'm threatening the square)
            if (board.isFileValid((byte) (file + 1))
                && ((board.getSquare((byte) (file + 1), rank).isOccupied()
		    && isBlack != 
		         board.getSquare((byte) (file +1), rank)
			      .getOccupant().isBlack())
	           || board.isBlackMove != isBlack
		   )
	       )
               addLegalDest(board.getSquare((byte) (file + 1),
                                            rank));
            if (board.isFileValid((byte) (file - 1))
                && ((board.getSquare((byte) (file - 1), rank).isOccupied()
		    && isBlack != 
		         board.getSquare((byte) (file -1), rank)
			      .getOccupant().isBlack())
	           || board.isBlackMove != isBlack
		   )
	       )
               addLegalDest(board.getSquare((byte) (file - 1),
                                            rank));
         }
               
         //two step
         if (!hasMoved()
             && board.isRankValid((byte) (orig.rank + (2 * dir)))) {
            dest = board.getSquare(orig.file, 
                                   (byte) (orig.rank + (2 * dir)));
            if (!dest.isOccupied() &&
	        !board.getSquare(orig.file,
                                (byte) (orig.rank + (1 * dir))).isOccupied())
               addLegalDest(dest);
         }

         //enPassant
         if (isBlack == board.isBlackMove
	     && onEnPassantRank()) {
            file = orig.file; 

	    if (board.isFileValid((byte) (file +1)) 
	        && board.isEnPassantFile((byte) (file+1)))
               addLegalDest(board.getSquare((byte)(file + 1), rank));

	    if (board.isFileValid((byte) (file -1))
	         && board.isEnPassantFile((byte) (file-1)))
               addLegalDest(board.getSquare((byte) (file - 1), rank));
	 }

         return legalDests.size();
   }

   /* genLegalDestsSaveKing **********************************************/
   /**the king is under attack.  This goes through the legalmoves array
    * and keeps only those that save the king's life.
    */
   protected void genLegalDestsSaveKing (ChessPiece king, ChessPiece threat) {
      //check if is the square of the threat, or blocks threat from
      //king square
      Iterator<Square> oldLegals = legalDests.iterator();
      Square sq = null;

         if (captured) return;

         legalDests = new ArrayList<>(2);

         while (oldLegals.hasNext()) {
            sq = oldLegals.next();

            //does the destination (sq) block the threat?
            if (threat.isBlockable(sq, king))
               legalDests.add(sq);

            //does it capture the threat?
            else if (sq.equals(threat.getSquare()))
               legalDests.add(sq);

            //if the threat is a pawn
	    //and that pawn has performed a double-move on the last turn
	    //and this pawn can take it enPassant
	    else if (threat.isPawn()
	             && threat.getSquare().getFile() 
		        == board.getEnPassantFile()
		     && sq.getFile()
		        == board.getEnPassantFile()
		    )
               legalDests.add(sq);
         }
   }

   /* onEnPassantRank ***************************************************/
   /** is this pawn on the a possible enpassant rank (not the file).
    */
   public boolean onEnPassantRank () {
      return ((!isBlack && orig.rank == 5)   //5th Rank
            || (isBlack && orig.rank == 4)); //4th Rank
   }

   /* isBlockable *******************************************************/
   public boolean isBlockable (Square target) {

      return false;
   }


   /* isBlockable ********************************************************/
   public boolean isBlockable (Square blocker, ChessPiece target) {
     return false;
   }


   /* isLegalAttack ****************************************************/
   public boolean isLegalAttack (Square target) {
      if (board.staleLegalDests)
         board.genLegalDests();

      if (target.file == orig.file) {
         return false;
      }
      else {
         return isLegalDest(target); 
      }
   }

   /* hasMoved() *******************************************************/
   public boolean hasMoved () {
      if ((isBlack && orig.rank == 7) //7th Rank
          || (!isBlack && orig.rank == 2)) //2st Rank
         return false;
      else
         return true;
   }

   //Utilities//////////////////////////////////////////////////////////
   /* isPromotionSquare *************************************************/
   /** if the pawn reaches this square will it promote.
    *  not sure how useful this function is.
    */
   static public boolean isPromotionSquare (Square sq, boolean isBlack) {
      if (isBlack && sq.rank == 1) return true;
      else if (!isBlack && sq.rank == 8) return true;
      else return false;
   }

   public boolean isPawn () { return true;}
          
}

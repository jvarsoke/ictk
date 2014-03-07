/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: King.java,v 1.1.1.1 2003/03/24 22:38:07 jvarsoke Exp $
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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/* King *******************************************************************/
public class King extends ChessPiece {
   public final static byte    INDEX           = 0;
   protected static final int  MAX_LEGAL_DESTS = 8,
                               MAX_GUARDS      = 8;

   public King () {
      super(INDEX, true, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public King (boolean blackness) {
      super(INDEX, blackness, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   public King (boolean blackness, Square o, ChessBoard _board) {
      super(INDEX, blackness, o, _board, MAX_LEGAL_DESTS, MAX_GUARDS);
   }

   protected String getName () { return "King"; }

   //LegalDests////////////////////////////////////////////////////////////
 
   /* genLegalDests ******************************************************/
   protected int genLegalDests () {
      super.genLegalDests();
      Square dest;
      int dir;

         //we generate all the possible moves for a king
         //then later we'll trim out all the which are illegal
         //because they put the king in check

         //one square moves
         for (byte f = (byte)(-1 + orig.file); f <= 1 + orig.file; f++) 
            for (byte r = (byte) (-1 + orig.rank);
                 r <= 1 + orig.rank;
                 r++) {
                if (board.isFileValid(f) && board.isRankValid(r))
                   addLegalDest(board.getSquare(f, r));
            }

         //need to do castling later to make sure no one can
         //attack the castle

         return legalDests.size();
   }
   
   /* genLegalDestsFinal **************************************************/
   /** this is used to restrict the moves of the King depending on 
    *  the attack lines of opposing piece on the board.
    */
   protected int genLegalDestsFinal () {
      Square      dest;
      int         dir;
      Iterator    perlimMoves;
      List        tmpLegalDests;
      ChessPiece  rook;
      boolean     blocked = false;
 
         tmpLegalDests = legalDests;
         perlimMoves = tmpLegalDests.iterator();

         legalDests = new ArrayList (8);

         //make sure the King doesn't move into a kill
         while (perlimMoves.hasNext()) {
            dest = (Square) perlimMoves.next();

            if (!board.isThreatened(dest, !isBlack)
                && !board.isGuarded(dest, !isBlack))
               addLegalDest(dest);
         }

         //castling//////////
         //make sure they haven't moved yet
         //needs to check if anyone is blocking the castle
         //by checking if squares are occupied between the two pieces
         //make sure no one is threatening the king or a sq he walks through

         if (moveCount == 0) {

            //Castle Far (Queenside)
            rook = board.getSquare(1, orig.rank).piece;
            if (rook != null
                && rook.moveCount == 0) {
                blocked = false;

                for (byte f=(byte) (rook.orig.file +1); 
		     f <= orig.file && !blocked; f++) {
		  
		   //is someone in the way
                   if (f < orig.file) 
                      blocked = board.getSquare(f, orig.rank)
                                   .isOccupied(); 

                   //is king walking in an assassin's sights
                   if (!blocked && f >= orig.file -2) 
                      blocked = board.isThreatened(
                                   board.getSquare(f, orig.rank)
                                   , !isBlack);
                }

                if (!blocked)
                   addLegalDest(getQueensideCastleSquare());
            }

            //Castle Near (Kingside)
            rook = board.getSquare(board.getMaxFile(), orig.rank).piece;
            if (rook != null
                && rook.moveCount == 0) {
                blocked = false;

		   //is someone in the way
                for (byte f=(byte)(rook.orig.file-1); 
		     f >= orig.file && !blocked; f--) {
                   if (f > orig.file) 
                      blocked = board.getSquare(f, orig.rank)
                                   .isOccupied(); 
                   //is king walking in an assassin's sights
                   if (!blocked) 
                      blocked = board.isThreatened(
                                   board.getSquare(f, orig.rank)
                                   , !isBlack);
                }

                if (!blocked)
                   addLegalDest(getKingsideCastleSquare());
            }
         }

         return legalDests.size();
   }

   /** these functions are used so variants can override them */
   public Square getQueensideCastleSquare () {
      return board.getSquare('c', ((isBlack) ? '8' : '1'));
   }

   /** these functions are used so variants can override them */
   public Square getKingsideCastleSquare () {
      return board.getSquare('g', ((isBlack) ? '8' : '1'));
   }


   /* genLegalDestsSaveKing ***************************************************/
   /** does nothing for the King.
    */
   protected void genLegalDestsSaveKing (ChessPiece king, ChessPiece threat) {
   }

   /* isInCheck ***************************************************************/
   /** is this King currently threatened.  This will also return true
    *  if the King is also in checkmate.
    */
   public boolean isInCheck () {
      return board.isThreatened(this);
   }

   /* isCastleableQueenside ***************************************************/
   /**can a future move include a Queen-side castle?
    */
   public boolean isCastleableQueenside () {
      ChessPiece rook = findMyRook(true);
      return (moveCount == 0 && rook != null && rook.moveCount == 0);
   }

   /* isCastleableKingside ****************************************************/
   /**Checks to see if a future move can include a King-side castle.
    * King side is always to the right of the white player, 
    * and left of the black player.
    */
   public boolean isCastleableKingside () {
      ChessPiece rook = findMyRook(false);
      return (moveCount == 0 && rook != null && rook.moveCount == 0);
   }

   /* setCastlableQueenside ***************************************************/
   /** sets the castleable bit for the Queenside.
    *
    *  @throws IllegalStateException of there's no rook on that side to castle
    *          with.
    */
   public void setCastleableQueenside (boolean t) {
      ChessPiece rook = findMyRook(true);
      if (rook == null && t == true) 
         throw new IllegalStateException(
	 "can't set castleable when there's no rook on that side of the board.");
      if (t) { 
         moveCount = 0;
	 rook.moveCount = 0;
      }
      else if (rook != null && rook.moveCount == 0) 
         rook.moveCount = 1;
   }

   /* setCastlableKingside ****************************************************/
   /** sets the castleable bit for the Kingside.
    *
    *  @throws IllegalStateException of there's no rook on that side to castle
    *          with.
    */
   public void setCastleableKingside (boolean t) {
      ChessPiece rook = findMyRook(false);
      if (rook == null && t == true) 
         throw new IllegalStateException(
	 "can't set castleable when there's no rook on that side of the board.");
      if (t) { 
         moveCount = 0;
	 rook.moveCount = 0;
      }
      else if (rook != null && rook.moveCount == 0) 
         rook.moveCount = 1;
   }

   /* findMyRook *************************************************************/
   /** this function finds the rook on a particular side
    *  to establish castling rights.  It's done this way 
    *  to allow for Fischer Random.  The rook is searched for
    *  going from the outside toward the king.  If for some reason
    *  in the FR setup the castleable rook is nearer the King 
    *  and on the same side as a moved rook, then the moved rook
    *  will be grabbed instead of the correct unmoved rook.  However,
    *  this is probably a very very rare case.  Note: FEN would also have
    *  this design flaw.
    */
   protected Rook findMyRook (boolean qside) {
      byte file = 1;
      ChessPiece p = null;
      if (qside) {
         for (file = 1; p == null && file< orig.file; file++) {
	    p = board.getSquare(file, orig.rank).piece;
	    if (p == null || !p.isRook() || p.isBlack() != isBlack()) 
	       p = null;
	 }
      }
      //kside
      else {
         for (file = board.MAX_FILE; p == null && file > orig.file; file--) {
	    p = board.getSquare(file, orig.rank).piece;
	    if (p == null || !p.isRook() || p.isBlack() != isBlack())
	       p = null;
	 }
      }
      return (Rook) p;
   }

   /* isBlockable*************************************************************/
   public boolean isBlockable (Square target) {

      return false;
   }

   /* isBlockable ********************************************************/
   public boolean isBlockable (Square blocker, ChessPiece target) {
      return false;
   }

    /* isLegalAttack *********************************************************
    /** same as isLegalDest(Square) but does not count castling as
     *  an attack square
     */
    public boolean isLegalAttack (Square target) {
       if ( Math.abs(target.file - orig.file) == 2) {
          return false;
       }
       else {
          return isLegalDest(target);
       }
            
    }

    public boolean isKing () { return true; }
}

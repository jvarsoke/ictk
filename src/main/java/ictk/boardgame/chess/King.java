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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
      Square dest;
      Iterator<Square> perlimMoves;
      List<Square> tmpLegalDests;

      tmpLegalDests = legalDests;
      perlimMoves = tmpLegalDests.iterator();

      legalDests = new ArrayList<>(8);

      // make sure the King doesn't move into a kill
      while (perlimMoves.hasNext()) {
         dest = perlimMoves.next();

         if (!board.isThreatened(dest, !isBlack) && !board.isGuarded(dest, !isBlack))
            addLegalDest(dest);
      }

      // castling//////////
      // TODO It would be good with an enum for king-side and queen-side castling.
      if (isCastlingAllowed(true)) {
         addLegalDestNoCheckOfDest(getQueensideCastleSquare());
      }
      if (isCastlingAllowed(false)) {
         addLegalDestNoCheckOfDest(getKingsideCastleSquare());
      }

      return legalDests.size();
   }

   /**
    * Return true if castling is allowed, false otherwise.
    * Make sure they haven't moved yet.
    * Needs to check if anyone is blocking the castle
    * by checking if squares are occupied between the two pieces.
    * Make sure no one is threatening the king or a sq he walks through.
    * @param castleQueenside true if this is queen-side castling, false otherwise.
    */
   private boolean isCastlingAllowed(boolean castleQueenside) {
      if (moveCount != 0) {
         return false;
      }
      
      
      Rook rook = findMyRook(castleQueenside);
      if (rook == null || rook.moveCount > 0) {
         return false;
      }
         
      boolean castlingNotAllowed = false;
      Square rookDest = findRookCastlingDestination(castleQueenside);
      Square rookOrig = rook.getSquare();
      Square kingDest = findCastlingDestination(castleQueenside);
      Square kingOrig = orig;

      // The direction variables contain the int that should be added to each
      // piece's file to move it to its destination.
      int kingDirection = Integer.signum(kingDest.file - kingOrig.file);
      int rookDirection = Integer.signum(rookDest.file - rookOrig.file);

      if (!rookDest.equals(rookOrig)) {
         // Verify that the rook has a free path to its destination.
         // Need to allow rook to walk past where the king stands.
         for (byte f = (byte) (rookOrig.file + rookDirection); f != rookDest.file && !castlingNotAllowed; f += rookDirection) {
            castlingNotAllowed = occupiedByOtherThan(board.getSquare(f, orig.rank), this);
         }
         // Verify that the rook destination is also empty.
         if (!castlingNotAllowed) {
            castlingNotAllowed = occupiedByOtherThan(board.getSquare(rookDest.file, orig.rank), this);
         }
      }

      if (!kingDest.equals(kingOrig)) {
         // Verify that the king has a free path to its destination.
         // Need to allow king to walk past where the rook stands.
         for (byte f = (byte) (kingOrig.file + kingDirection); f != kingDest.file && !castlingNotAllowed; f += kingDirection) {
            castlingNotAllowed = occupiedByOtherThan(board.getSquare(f, orig.rank), rook);

            // is king walking in an assassin's sights
            if (!castlingNotAllowed)
               castlingNotAllowed = board.isThreatened(board.getSquare(f, orig.rank), !isBlack);
         }
         // Verify that the king destination is also empty and non-threatened.
         if (!castlingNotAllowed) {
            castlingNotAllowed = occupiedByOtherThan(board.getSquare(kingDest.file, orig.rank), rook);

            // is king walking in an assassin's sights
            if (!castlingNotAllowed)
               castlingNotAllowed = board.isThreatened(board.getSquare(kingDest.file, orig.rank), !isBlack);
         }
      }
      return !castlingNotAllowed;
   }

   /**
    * Returns true if square is occupied by a piece other than 'piece'.
    */
   private boolean occupiedByOtherThan(Square square, ChessPiece piece) {
      return square.isOccupied() && !square.getPiece().equals(piece);
   }

   Square findRookCastlingDestination(boolean castleQueenside) {
      int offset = castleQueenside ? 1 : -1;
      
      Square kingDest = findCastlingDestination(castleQueenside);
      return board.getSquare(kingDest.getX() + offset, kingDest.getY());
   }

   Square findCastlingDestination(boolean castleQueenside) {
      if (castleQueenside) {
         return getQueensideCastleSquare();
      } else {
         return getKingsideCastleSquare();
      }
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
         for (file = ChessBoard.MAX_FILE; p == null && file > orig.file; file--) {
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

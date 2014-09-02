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

/* ChessPiece.java - generic ChessPiece class
 *
 * by jvarsoke (jvarsoke@bigfoot.com) 
 */
package ictk.boardgame.chess;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import ictk.boardgame.Board;
import ictk.boardgame.Piece;

public abstract class ChessPiece extends Piece {
     /** the null piece INDEX */
   public static byte  NULL_PIECE = -1;
     /** an offset for black pieces of the same INDEX value*/
   public static byte BLACK_OFFSET = 70;  

     /** used for quick id of what kind of piece it is */
   protected byte      index; 
     /** color or team of the piece */
   protected boolean   isBlack; 
     /** is this still an active piece on the board, or has it been captured */
   protected boolean   captured;
     /** how many times it moved this game */
   protected short     moveCount;
     /** legal destinations for next move */
   protected List<Square> legalDests,
     /** squares with pieces this piece guards */
                       guardSquares;
     /** origin square */
   protected Square    orig;
     /** the board this piece belongs to */
   protected ChessBoard board;
      /** a piece that is pinning this piece */
   protected ChessPiece     pinnedBy;      


   //Contructors/////////////////////////////////////////////////////////
   /** 
    * @param ind is the piece index
    * @param maxlegaldests max number of legal destinations for this piece
    * @param maxguards max number of guard squares for this piece
    */
   public ChessPiece (byte ind, 
		 int maxlegaldests, int maxguards) {
      this (ind, false, maxlegaldests, maxguards);
   }

   /** 
    * @param ind is the piece index
    * @param _isBlack is the piece black or white
    * @param maxlegaldests max number of legal destinations for this piece
    * @param maxguards max number of guard squares for this piece
    */
   public ChessPiece (byte ind, boolean _isBlack,
		 int maxlegaldests, int maxguards) {
      this (ind, _isBlack, null, null, maxlegaldests, maxguards);
   }

   /** 
    * @param ind is the piece index
    * @param _isBlack is the piece black or white
    * @param _orig which square should it be placed on
    * @param _board which board should it be on
    * @param maxlegaldests max number of legal destinations for this piece
    * @param maxguards max number of guard squares for this piece
    */
   public ChessPiece (byte ind, boolean _isBlack, 
                 Square _orig, ChessBoard _board, 
		 int maxlegaldests, int maxguards) {
      index          = ind;
      isBlack        = _isBlack;
      moveCount      = 0;
      orig           = _orig;
      board          = _board;
      legalDests     = new ArrayList<>(maxlegaldests);  
      guardSquares   = new ArrayList<>(maxguards);  
   }

   //Status//////////////////////////////////////////////////////////////

   /* isCapture ********************************************************/
   /** has this piece been captured.
    */
   public boolean isCaptured () {
      return captured;
   }

   /* setCaptured ******************************************************/
   /** sets the piece captured or not.
    */
   public void setCaptured (boolean t) {
      legalDests.clear();
      guardSquares.clear();
      captured = t;
   }

   public ChessPiece getPinnedBy () { return pinnedBy; }

   //Defaults/////////////////////////////////////////////////////////////

   //Team//////////////////////////////////////////////////////////////////

   /* isBlack ************************************************************/
   /** which side is this piece on?
    */
   public boolean isBlack () {
      return (isBlack);
   }

   /* setBoard **********************************************************/
   /** sets the chess board that this piece is on.
    */
   public void setBoard (ChessBoard b) {
      board = b;
   }

   //Legal Moves///////////////////////////////////////////////////////////

   /* genLegalDests ******************************************************/
   /** generate the legal destinations for this piece on the current board.
    * @return int - the number of legal destination for this piece on the 
    *               current board.
    */
   protected int genLegalDests () {
         pinnedBy = null;
         legalDests.clear();
         guardSquares.clear();
      return 0;
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
      
         //FIXME: do we really want to create a new array here?
         legalDests = new ArrayList<>(3);
	  
	 while (oldLegals.hasNext()) {
	    sq = oldLegals.next();

	    //does the destination (sq) block the threat?
            if (threat.isBlockable(sq, king)) 
	       legalDests.add(sq);

	    //does it capture the threat?
	    else if (sq.equals(threat.getSquare())) 
	       legalDests.add(sq);
	 }
   }
   

   /* addLegalDests ******************************************************/
   /**adds the destination to the legal moves list if the
    * destination is not occupied by a teammate
    *
    * @param dest  - square being assessed
    *
    * @return true  - if the destination is legal
    * @return false - if the destination is illegal
    */
   protected boolean addLegalDest (Square dest) {
       boolean valid = true;         //did we add something?
         if (dest.isOccupied()
             && dest.piece.isBlack() == isBlack()) {
            guardSquares.add(dest);
            valid = false;
         }
         else
            legalDests.add(dest);
 
         return valid;
   }

   /**
    * Same as {@link #addLegalDest(Square)}, but doesn't validate that dest is free from like-colored pieces.
    * In chess960 it can be legal to take the place of a like-colored piece when castling.
    */
   protected void addLegalDestNoCheckOfDest (Square dest) {
      // For chess 960 the king could have the possibility to move to a square
      // both by moving a single step (without castling) and by castling. Hence
      // we need to check that we're not adding a duplicate square.
      if (!legalDests.contains(dest))
        legalDests.add(dest);
   }

   /* setPinned **********************************************************/
   /**sets the pinnedBy piece and modifies the legal moves for this piece
    * to the Union of lineOfSight and current Legal Moves
    */
   protected void setPinned (ChessPiece pinner, List<Square> lineOfSight) {
      pinnedBy = pinner;
      assert !pinner.isCaptured() : "Captured Pinner: " + pinner.dump();
      legalDests.retainAll(lineOfSight);
   }
 
   /* isLegalDests *******************************************************/
   /**checks the legalDests list to see if the destination is
    * in that list.  If yes then it is a legal move.
    * the piece must also be alive.
    *
    * @param  dest - the target of the move, which needs to be considered
    * @return  true - if alive and legalDests contains this destination
    */
   public boolean isLegalDest (Square dest) {
      if (!captured) {
         if (board.staleLegalDests)
	    board.genLegalDests();
	 return legalDests.contains(dest);
      }
      else
         return false;
   }

   /* isLegalAttack *****************************************************/
   /** same as isLegalDests except for special moves like castle and
    *  pawn moves.
    */
   public boolean isLegalAttack (Square dest) {
      return isLegalDest(dest);
   }

   /* getLegalDests *****************************************************/
   /** returns the Squares that are legal destinations on this piece
    */
   public List<Square> getLegalDests () {
      if (board.staleLegalDests)
          board.genLegalDests();
      return legalDests;
   }

   /* removeLegalDests **************************************************/
   /** clears all legal destinations for this piece.  This could be used 
    *  to eliminate a certian piece from being considered to move in on 
    *  the board.
    */
   public void removeLegalDests () {
      legalDests.clear();
      guardSquares.clear();
   }

   /* getGuardSquares **************************************************/
   /** returns the Squares that this piece guards (when it is the other 
    *  sides turn to move.
    */
   public List<Square> getGuardSquares () {
      if (board.staleLegalDests)
          board.genLegalDests();
      return guardSquares;
   }

   /* isGuarding ******************************************************/
   /** is this piece guarding the destination Square?
    */
   public boolean isGuarding (Square dest) {
      if (!captured) {
         if (board.staleLegalDests)
	    board.genLegalDests();
	 return guardSquares.contains(dest);
      }
      else
         return false;
   }

   /* isGuarding ******************************************************/
   /** is this piece guarding the parameter piece.
    */
   public boolean isGuarding (ChessPiece piece) {
      return guardSquares.contains(piece.orig);
   }

   //board geometry //////////////////////////////////////////////////////
   /* getLineOfSight ****************************************************/
   /** get all the squares between two squares along a line
    *  (diag or straight).  Useful for Queens, Rooks and Bishops in
    *  determining legal moves and attacks on the King.
    *
    *  @return squares in line starting with this peice to target
    *          if the squares are not in line then null is returned
    *  @param inclusive include the target square in the return set
    */
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
      byte o_file = getSquare().file,
          o_rank = getSquare().rank;
      byte f = 0, r = 0, 
	  i = 0;  //return square counter

          //same file
	  if (o_file == t_file) {
             //target is north
	     if (o_rank < t_rank) {
	        for (r = (byte) (o_rank+1); r <= t_rank; r++)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(o_file, r);
	     }
	     //target is south
	     else {
	        for (r = (byte) (o_rank-1); r >= t_rank; r--)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(o_file, r);
	     }
	  }

	  //same rank
	  else if (o_rank == t_rank) {
	     //target is east
	     if (o_file < t_file) {
	        for (f = (byte) (o_file+1); f <= t_file; f++)
	           if (f != t_file || inclusive) 
		      return_tmp[i++] = board.getSquare(f, o_rank);
	     }
	     //target is west
	     else {
	        for (f = (byte) (o_file-1); f >= t_file; f--)
	           if (f != t_file || inclusive) 
		      return_tmp[i++] = board.getSquare(f, o_rank);
	     }
	  }

	  //forward slash diag  ex: 0,0 - 7,7 or 1,4 - 2,5
	  else if ((o_file - t_file) == (o_rank - t_rank)) {
	     //target is northeast
	     if (o_rank < t_rank) {
	        for (f = (byte) (o_file+1), r = (byte)(o_rank+1); 
		     r <= t_rank; f++, r++)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	     //target is southwest
	     else {
	        for (f = (byte)(o_file-1), r = (byte)(o_rank-1); 
		     r >= t_rank; f--, r--)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	  }
	  //backslash diag ex: 0,7 - 7,0
	  else if ((o_file - t_file) == ((o_rank - t_rank) * -1)) {
	     //target is northwest
	     if ((o_rank - t_rank) < 0) {
	        for (f = (byte)(o_file-1), r = (byte)(o_rank+1); 
		     r <= t_rank; f--, r++)
	           if (r != t_rank || inclusive) 
		      return_tmp[i++] = board.getSquare(f, r);
	     }
	     //target is southeast
	     else {
	        for (f = (byte)(o_file+1), r = (byte)(o_rank-1); 
		     r >= t_rank; f++, r--)
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

   /* isBlockable ********************************************************/
   /**checks to see if another piece may be able to intercede between the
    * piece's origin and destination square
    *
    * @param blocker - the proposed square that can block the attack
    * @param target  - the piece under attack
    *
    * @return true  - the blocker intercedes the attack
    * @return false - the blocker doesn't intercede
    */
   public boolean isBlockable (Square blocker, ChessPiece target) {
       boolean blockable = false;
       Square  dest = target.getSquare();
       Square[] lineOfSight = null;

       if (board.staleLegalDests)
          board.genLegalDests();

       //is the target even legal?
       if (!isLegalDest(dest))
          throw new IllegalArgumentException(this 
	     + "cannot be blocked for illegal destination square (" 
	     + dest + ")");

       lineOfSight = getLineOfSight(target, false);
       int i = 0;
       while (!blockable && lineOfSight != null && i < lineOfSight.length)
          blockable = (blocker.equals(lineOfSight[i++]));

     return blockable;
   }

   /* adjustPinsLegalDests ***********************************************/
   //FIXME: not sure what this function was for
   protected void adjustPinsLegalDests (ChessPiece king, List<ChessPiece> enemyTeam) {
   }


   //Utils////////////////////////////////////////////////////////////////

   /* getIndex **********************************************************/
   /** gets the Index for the type of piece in question.  This is often
    *  used to evade the cost of instanceof and use switch() statements.
    */
   public byte getIndex () {
      return (byte) (index + ((isBlack) ? BLACK_OFFSET : 0));
   }

   /* isBlackIndex ******************************************************/
   /** is this index of a black piece (using the BlACK_OFFSET
    */
   static boolean isBlackIndex (byte i) {
         return (i > BLACK_OFFSET);
   }
  
   //Misc Object//////////////////////////////////////////////////////////

   /* getSquare *********************************************************/
   /** gets the square this piece is currently occupying.
    */
   public Square getSquare () {
      return orig;
   }

   /* getBoard **********************************************************/
   public Board getBoard () {
      return board;
   }

   public String toString () {
      return getName();
   }

   /* getName ***********************************************************/
   /** gets the English name for this type of piece.  This is primary
    *  used for diagnostic messages and not presentation, which is why
    *  it's "protected" and not "public".
    */
   protected abstract String getName ();

   public boolean isKing () { return false; }
   public boolean isQueen () { return false; }
   public boolean isRook () { return false; }
   public boolean isBishop () { return false; }
   public boolean isKnight () { return false; }
   public boolean isPawn () { return false; }

   /** toChessPiece ******************************************************/
   /** this factory method allows for easy conversion of indices to Piece 
    *  objects of different types.
    */
   static public ChessPiece toChessPiece (int i) {
      ChessPiece p = null;
         switch (i % BLACK_OFFSET) {
	    case King.INDEX:   p = new King(); break;
	    case Queen.INDEX:  p = new Queen(); break;
	    case Rook.INDEX:   p = new Rook(); break;
	    case Bishop.INDEX: p = new Bishop(); break;
	    case Knight.INDEX: p = new Knight(); break;
	    case Pawn.INDEX:   p = new Pawn(); break;
	    default:
	       throw new IllegalArgumentException(
	          "Illegal ChessPiece INDEX(" + i + ")");
	 }
      return p;
   }

   public String dump () {
      StringBuffer sb = new StringBuffer();

         sb.append(getName())
	   .append(" captured: ").append(captured)
	   .append(" square: ").append(orig)
	   .append(" legalDests: ").append(legalDests)
	   .append(" guardSquares: ").append(guardSquares)
	   .append(" pinnedBy: ").append(pinnedBy);
      return sb.toString();
   }
}

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

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.chess.io.SAN;
import ictk.boardgame.chess.io.FEN;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

/* ChessBoard **************************************************************/
/** This is the playing board.  It is a matrix of Squares with Pieces
 *  on those Squares.  The Pieces themselves determine their legal moves.
 *  ChessBoard facilitates this but does not assess move legality by itself.
 *  Whenever specifying coordinates on a ChessBoard it should be done
 *  by using the range 1-MAX_FILE, or 1-8 for normal chess.
 */
public class ChessBoard implements Board {
      /** used as a mask for Log.debug() */
   public final static long DEBUG = Log.Board;
      /** the file that indicates no file is specified */
   public static final byte NULL_FILE = 0,
      /** the file that indicates no rank is specified */
                            NULL_RANK = 0,
      /** the file that indicates no enpassant file was specified */
                            NO_ENPASSANT = NULL_FILE;

      /** the default setup of pieces in a traditional chess board */
   public final static char[][] 
      DEFAULT_POSITION={{'R','P',' ',' ',' ',' ','p','r'},
			{'N','P',' ',' ',' ',' ','p','n'},
			{'B','P',' ',' ',' ',' ','p','b'},
			{'Q','P',' ',' ',' ',' ','p','q'},
			{'K','P',' ',' ',' ',' ','p','k'},
			{'B','P',' ',' ',' ',' ','p','b'},
			{'N','P',' ',' ',' ',' ','p','n'},
			{'R','P',' ',' ',' ',' ','p','r'}};
      /** the max number of files on a traditional chess board*/
   public static final byte MAX_FILE = 8;
      /** the max number of ranks on a traditional chess board*/
   public static final byte MAX_RANK = 8;
      /** only really used to help display debugging output */
   protected static final SAN san = new SAN();
      /** only used for hashCode */
   protected static final FEN fen = new FEN();

      /** objects listening for updates to the board */
   protected BoardListener[] listeners;

      /** technically this is the actual board */
   protected Square    squares[][]; //file,rank
      /** the team of white Pieces */
   protected List<ChessPiece> whiteTeam, 
      /** the team of black Pieces */
                       blackTeam;
      /** the King for the white side */
   protected King      whiteKing, 
      /** the King for the black side */
                       blackKing;

      /** whose move is it? */
   protected boolean   isBlackMove = false;
      /** this is the last move that was executed on the board */
   protected ChessMove lastMove = null;
      /** if the last move was a double pawn move this flag is set */
   protected byte enpassantFile = NO_ENPASSANT;

      /** is the initial position of this board the default chess 
       *  position?
       */
   protected boolean isInitialPositionDefault = true;

      /** used to calculate 50 move rule*/
   protected int    plyCount50 = 0;

      /** moves in game*/
   protected int    moveNumber = 0;

      /**used to do smart generating of legaldests
         should be set stale everytime an verified move is done*/
   protected boolean staleLegalDests = true;

   /** calls ChessBoard(true)
    */
   public ChessBoard () {
      this(true);
   }

   /** 
    * @param defaultBoard if true the default chess position will be used.
    *                     if false the board will be cleared and w/o pieces.
    */
   public ChessBoard (boolean defaultBoard) {
      squares = new Square[MAX_FILE][MAX_RANK];

         whiteTeam = new ArrayList<>(16);
         blackTeam = new ArrayList<>(16);
 
         for (byte f=0, r=0; f < MAX_FILE; f++) 
            for (r=0;r < MAX_RANK; r++) 
               squares[f][r] = new Square((byte)(f+1),(byte)(r+1)); 
         if (defaultBoard)
	    setPositionDefault();
	 else
	    setPositionClear();
   }

   /** 
    * @param matrix the position using English SAN notation
    *               for the pieces, (PNBRQK) where uppercase is
    *               used for White pieces and lowercase for Black.
    */
   public ChessBoard (char[][] matrix) {
      this();
      setPosition(matrix);
   }

   /** 
    * @param matrix the position using English SAN notation
    *               for the pieces, (PNBRQK) where uppercase is
    *               used for White pieces and lowercase for Black.
    * @param isBlackMove who's move is it?
    * @param castleWK can white castle Kingside?
    * @param castleWQ can white castle Queenside?
    * @param castleBK can white castle Kingside?
    * @param castleBQ can white castle Queenside?
    * @param enpassantFile the file (A-H) that is vulnerable to enpassant
    * @param plyCount the ply count for the 50 move rule.
    * @param moveNum which move number are we on?
    */
   public ChessBoard (char[][] matrix, 
   		 boolean isBlackMove,
                 boolean castleWK,
                 boolean castleWQ,
                 boolean castleBK,
                 boolean castleBQ,
		 char enpassantFile,
		 int plyCount,
		 int moveNum
		 ) {
      this();
      setPosition(matrix);
      this.isBlackMove = isBlackMove;
      setWhiteCastleableKingside(castleWK);
      setWhiteCastleableQueenside(castleWQ);
      setBlackCastleableKingside(castleBK);
      setBlackCastleableQueenside(castleBQ);
      setEnPassantFile(enpassantFile);
      plyCount50 = plyCount;
      moveNumber = moveNum;
   }

   //board dimensions/////////////////////////////////////////////////////

   /* isRankValid *******************************************************/
   /** is this a valid rank for the board (in the proper range)
    */
   public boolean isRankValid (int r) { return (r >= 1 && r <= MAX_RANK); }

   /* isFileValid *******************************************************/
   /** is this a valid file for the board (in the proper range)
    */
   public boolean isFileValid (int f) { return (f >= 1 && f <= MAX_FILE); }

   /* getMaxRank ********************************************************/
   /** what is the maxium rank allowed.
    */
   public int getMaxRank () { return MAX_RANK; }

   /* getMaxFile ********************************************************/
   /** what is the maxium file allowed.
    */
   public int getMaxFile () { return MAX_FILE; }

   //moves/////////////////////////////////////////////////////////////////

   /** playMove() ********************************************************/
   /** plays the move on the board.  Note: if a move is played on the
    *  board this way it is NOT added to the History.  This could
    *  seriously FUBAR the state of the board if you use the two 
    *  methods on the same board.
    */
   public void playMove (Move move) 
          throws IllegalMoveException, OutOfTurnException {
      ChessMove m = (ChessMove) move;
         m.execute();
   }

   /* getPlayerToMove ***************************************************/
   /** @return 0 for White and 1 for Black
    */
   public int getPlayerToMove () {
      return (isBlackMove) ? 1 : 0;
   }

   /* genLegalDests *****************************************************/
   /** generates all legal moves for the side that needs to move.
    *  Steps:<br>
    *     all possible moves are generated.<br>
    *     king moves are limited so king doesn't move into check<br>
    *     all pins are assessed and pinned pieces moves are restricted<br>
    *     check is determine on moving king<br>
    *        if one check then find a king move, block or counter-attack<br>
    *        if two checks then only a king move will do<br>
    *     check if there are any moves<br>
    *        if no moves and check then Checkmate<br>
    *        if no moves and no check then Stalemate<br>
    */
   protected void genLegalDests () {
      ChessPiece[] threats = null;
      King movingKing = null,
           otherKing  = null;
      List<ChessPiece> movingTeam = null,
           otherTeam = null;

         if (Log.debug)
	    Log.debug(DEBUG, "generating legal moves");

         staleLegalDests = false;
//FIXME: maybe should be array not List

         for (byte f=0, r=0; f < MAX_FILE; f++) {
            for (r=0;r < MAX_RANK; r++) {
                if (squares[f][r].piece != null)
                   squares[f][r].piece.genLegalDests();
            }
         }

	 movingKing = (isBlackMove) ? blackKing : whiteKing; 
	 movingTeam = (isBlackMove) ? blackTeam : whiteTeam;
	 otherKing  = (isBlackMove) ? whiteKing : blackKing;
	 otherTeam  = (isBlackMove) ? whiteTeam : blackTeam;
	 
         movingKing.genLegalDestsFinal();
         otherKing.genLegalDestsFinal();

	 //now have to check to see if any of the present moves are
	 //illegal because the move puts the king in check (a pin)
	 //check queens, bishops and rooks
	 for (byte i=0; i < otherTeam.size(); i++) 
	     otherTeam.get(i).adjustPinsLegalDests(movingKing, movingTeam);
	 

         //if King is in check then need to modify
	 //legalMove lists to deal with the check
	 if (movingKing.isInCheck()) {
	    if (lastMove != null)
	       lastMove.setCheck(true);
	    threats = getThreats(movingKing);

	    if (Log.debug) {
	       Log.debug(DEBUG, "THREATS TO MOVING KING! (" 
	          + threats.length + ")");
	       Log.debug2(DEBUG, "Threat: " + threats[0]);
	    }

	    switch (threats.length) {
	       case 1:
	          //only valid moves are king moves, block and counter-attack
		  for (byte i=0; i < movingTeam.size(); i++) {
		      movingTeam.get(i)
			      .genLegalDestsSaveKing(movingKing, threats[0]);
		  }
		  break;

	       case 2:
	          //only valid moves are king moves
		  for (byte i=0; i < movingTeam.size(); i++) {
		      ChessPiece p = movingTeam.get(i);
		      if (p != movingKing)
		         p.removeLegalDests();
		  }
		  if (lastMove != null)
		     lastMove.setDoubleCheck(true);
		  break;

	       default:
	          //can't have more that 2 checks on a king
	          assert false 
		      : "King reports in check with " + threats.length 
		        + " threats.";
	    }
	 }

	 //if king has no moves and is being threatened
	 //then should throw Checkmate exception.
	 //if no moves and no threats then stalemate.
	 if (getLegalMoveCount() == 0) 
	    if (movingKing.isInCheck()) {
	       if (lastMove != null)
		  lastMove.setCheckmate(true);
	    }
	    else {
	       if (lastMove != null)
		  lastMove.setStalemate(true);
	    }

//FIXME: not removing moves to keep threats available
	 //can now remove non-to-move teams' legalDests
	 //for (byte i=0; i < otherTeam.size(); i++) 
	 //   ((ChessPiece) otherTeam.get(i))
	 //      .removeLegalDests();

   }


   /* isMoveLegal(Move m) ****************************************************/
   /** Checks to see if the is a legal move
    *
    * @deprecated use {@link #verifyIsLegalMove(Move m)} instead.  
    */
    @Deprecated
    public boolean isLegalMove(Move m) {
        try {
            verifyIsLegalMove(m);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


   /* verifyIsLegalMove ******************************************************/
   /** Checks to see if the move is legal on the current board.  This will not
    *  affect the History of this game in any way.
    */
   @Override
   public void verifyIsLegalMove(Move m) throws OutOfTurnException, IllegalMoveException {
      if (m == null)
         throw new IllegalArgumentException("Passing null move.");
      String before = m.getBoard().toString();
      ((ChessMove) m).execute();
      String afterExecute = m.getBoard().toString();
      ((ChessMove) m).unexecute();
      String afterUnexecute = m.getBoard().toString();
      if (!before.equals(afterUnexecute)) {
         throw new RuntimeException("Unexecute didn't work as expected.\nMove: " + m + "\nBefore:\n" + before
               + "\nAfter execute:\n" + afterExecute + "\nAfter un-execute:\n" + afterUnexecute);
      }
   }

   //Assessment/////////////////////////////////////////////////////////////

   /* getThreats **********************************************************/
   /** returns an array of the pieces of the team specified who can
    * attack the square specified.
    *
    * @param sq       square to do threat analysis for
    * @param isBlack  attackers must be of this team
    *
    * @return ChessPiece[] - set of all pieces which have this square in their
    *              legalDests variable
    * @return null         - if no pieces threaten the square
    */
   public ChessPiece[] getThreats (Square sq, boolean isBlack) {
      Iterator<ChessPiece> team = null;
      List<ChessPiece> attackers = null;
      ChessPiece       piece = null;
      ChessPiece[]     threats = null;

         if (sq == null)
            throw new NullPointerException(
	       "cannot assess threats to null square");

	 if (staleLegalDests)
	    genLegalDests();

         attackers = new LinkedList<>();
         team = (isBlack) ? blackTeam.iterator() : whiteTeam.iterator();
	 
	 if (Log.debug) 
	    Log.debug(DEBUG, "Finding "
	       + ((isBlack) ? "Black" : "White")
	       + " attackers on " + sq);

         while (team.hasNext()) {
            piece = team.next();
            if (piece.isLegalAttack(sq)) {
               attackers.add(piece);
	       if (Log.debug)
	          Log.debug2(DEBUG, 
		     "attacker: " + piece + "(" + piece.getSquare() + ")");
	    }
         }
         
         if (attackers.size() > 0) {
	    threats = new ChessPiece[attackers.size()];
	    threats = attackers.toArray(threats);
            return threats;
         }
	 else {
	    if (Log.debug)
	       Log.debug(DEBUG, "no attackers found.");
	 }

	 return threats;
   }

   //getThreats//////////////////////////////////////////////////////////////

   /* getThreats ***********************************************************/
   /** return the ChessPieces that threaten to take this piece.
    */
   public ChessPiece[] getThreats (ChessPiece piece) {
        if (piece == null)
           throw new NullPointerException(
	      "cannot assess threats to null piece");

        return getThreats(piece.orig, !piece.isBlack);
   }

   /* isThreatened *********************************************************/
   /** is a ChessPiece attacking this square?
    *
    * @param sq      the square in question.
    * @param isBlack which side do you suppose attacks this square?
    */
   public boolean isThreatened (Square sq, boolean isBlack) {
        return (getThreats(sq, isBlack) != null);
   }

   /* isThreatened *********************************************************/
   /** is a ChessPiece attacking this piece.
    *
    * @param piece the piece under possible attack
    */
   public boolean isThreatened (ChessPiece piece) {
        if (piece == null)
           throw new NullPointerException(
	      "cannot assess threats to null piece");

        return isThreatened(piece.orig, !piece.isBlack);
   }

   //Guarding///////////////////////////////////////////////////////////////

   /* getGuards **********************************************************/
   /**returns an array of the pieces of the team specified who can
    * guard the square (a friendly piece) specified.
    * this is used to determine which pieces the King could take
    * this method is extremely similar to getThreats
    *
    * @param sq       square to do threat analysis for
    * @param isBlack  attackers must be of this team
    *
    * @return ChessPiece[]  - set of all pieces which have this square in their
    *              guardSquares variable
    * @return null     - if no pieces threaten the square
    */
   public ChessPiece[] getGuards (Square sq, boolean isBlack) {
      Iterator<ChessPiece> team = null;
      List<ChessPiece>     attackers = null;
      ChessPiece           piece = null;
      ChessPiece[]         guards = null;
 
         if (sq == null)
            throw new NullPointerException(
	       "cannot assess guards of null square");

	 if (staleLegalDests)
	    genLegalDests();

         attackers = new LinkedList<>();
         team = (isBlack) ? blackTeam.iterator() : whiteTeam.iterator();
 
         while (team.hasNext()) {
            piece = team.next();
            if (piece.isGuarding(sq))
               attackers.add(piece);
         }
 
         if (attackers.size() > 0) {
	    guards = new ChessPiece[attackers.size()];
            guards = attackers.toArray(guards);
	 }
	 return guards;
   }
 
   /* getGuards **********************************************************/
   /**returns an array of the pieces of the team specified who can
    * guard the square (a friendly piece) specified.
    * this is used to determine which pieces the King could take
    * this method is extremely similar to getThreats
    *
    * @param piece the piece to be guarded
    *
    * @return ChessPiece[] set of all pieces which have this square in their
    *                      guardSquares variable
    * @return null         if no pieces threaten the square
    */
   public ChessPiece[] getGuards (ChessPiece piece) {
        if (piece == null)
           throw new NullPointerException(
	      "cannot assess threats to null piece");
 
        return getThreats(piece.orig, !piece.isBlack);
   }
 
   /* isGuarded ************************************************************/
   /** does a fellow team member recapture this square if the piece is taken
    */
   public boolean isGuarded (Square sq, boolean isBlack) {
        return (getGuards(sq, isBlack) != null);
   }
 
   /* isGuarded ************************************************************/
   /** does a fellow team member recapture this square if the piece is taken
    */
   public boolean isGuarded (ChessPiece piece) {
        if (piece == null)
           throw new NullPointerException(
	      "cannot assess threats to null piece");
 
        return isGuarded(piece.orig, !piece.isBlack);
   }


   //Legal Move Utilities///////////////////////////////////////////////////

   /* getLegalMoveCount *****************************************************/
   /** how many legal moves on the current board.
    */
   public int getLegalMoveCount () {
      int count = 0;
      List<ChessPiece> movingTeam = (isBlackMove) ? blackTeam : whiteTeam;

	 if (staleLegalDests)
	    genLegalDests();

	 for (int i=0; i < movingTeam.size(); i++) 
	     count += movingTeam.get(i).getLegalDests().size();
      
      return count;
   }

   /* getLegalMoves *********************************************************/
   /** returns a list of moves that are legal on the current board.
    */
   public List<Move> getLegalMoves () {
      List<Move> list = new LinkedList<>();
      List<ChessPiece> movingTeam = (isBlackMove) ? blackTeam : whiteTeam;
      List<Square> dests = null;
      ChessPiece piece = null;
      Square orig = null;
      Square dest = null;

	 if (staleLegalDests)
	    genLegalDests();

         for (int i=0; i < movingTeam.size(); i++) {
	    piece = movingTeam.get(i);
	    dests = piece.getLegalDests();
	    orig = piece.orig;
	    for (int j=0; j < dests.size(); j++) {
	       dest = dests.get(j);
	       //dest.piece set for promotion when it is really
	       //ane expected casualty.  but this allows for the
	       //communication of a possible caputure. (a hack)
	       //FIXME: check to see if the above really works as intended
	       list.add(new ChessMove(orig, dest, dest.piece, this));  
	    }
	 }
      
      return list;
      
   }

   /* isDestUniqueForClass ************************************************/
   /** determines how unique this move is for the Type of piece it is.
    *  this is used for Algebraic notation short-form.  If there is only
    *  one Knight that can move to f3, then the move notation needs to be
    *  Nf3.  But if two Knights can move there then Ndf3 etc.
    *  @return boolean[] - [0] rank is unique, [1] file is unique
    */
   protected boolean[] isDestUniqueForClass (Square dest, ChessPiece p) {
      boolean[] unique = {true, true}; //file, rank
      List<ChessPiece> movingTeam = (isBlackMove) ? blackTeam : whiteTeam;
      List<Square> dests = null;
      ChessPiece   piece = null;

      if (p.isKing()) return unique;

      for (int i=0; i < movingTeam.size(); i++) {
         piece = movingTeam.get(i);

	 if (piece != p 
	     && !piece.isCaptured() 
	     && piece.getIndex() == p.getIndex()) {
	    dests = piece.getLegalDests();

	    if (dests.contains(dest)) {
	       if (p.orig.file == piece.orig.file)
	          unique[0] = false;  //file is not unique
	       if (p.orig.rank == piece.orig.rank) 
	          unique[1] = false; //rank is not unique

	       //special case for knights on e5 and f6 with dest d7
	       //or rooks on h1 & d2 moving to d1 etc.
	       if (unique[0] == true && unique[1] == true) 
		  unique[1] = false;
	    }
	 }
      }

      return unique;
   }

   /* getOrigin **********************************************************/
   /** determines where the origin of the move that goes to this destination
    *  is.  This is the recipercal method for what  isDestUniqueForClass allows
    */
   public Square getOrigin (byte piece_index, Square dest)
          throws AmbiguousChessMoveException,
	         IllegalMoveException {
      return getOrigin (piece_index, -1, -1, dest);
   }

   /* getOrigin **********************************************************/
   /** determines where the origin of the move that goes to this destination
    *  is.  This is the recipercal method for what  isDestUniqueForClass allows
    */
   public Square getOrigin (byte piece_index, int file, int rank, 
                            Square dest) 
          throws AmbiguousChessMoveException,
	         IllegalMoveException {
      if (file > MAX_FILE || rank > MAX_RANK)
         throw new IllegalArgumentException("origin or rank too big");

      byte orig_f = (byte) file,
           orig_r = (byte) rank;

      List<ChessPiece> movingTeam = (isBlackMove) ? blackTeam : whiteTeam;
      List<ChessPiece> dupes = new ArrayList<>(1);
      ChessPiece   piece = null;
      ChessPiece   mover = null;  //piece to move
      boolean found = false;
      int     count = 0;   //how many pieces with this dest?

      for (int i=0; i < movingTeam.size(); i++) {
         piece = movingTeam.get(i);

	 if (((piece.getIndex() % ChessPiece.BLACK_OFFSET) == piece_index) 
	     && piece.isLegalDest(dest))

	    if ((orig_f < 1 && orig_r < 1)
	        || (orig_r < 1 && piece.orig.file == orig_f)
	        || (orig_f < 1 && piece.orig.rank == orig_r)) {
	       found = true;
	       if (++count > 1) {
	          if (dupes == null) {
		     dupes = new ArrayList<>(2);
		     dupes.add(mover);
		  }
	          dupes.add(piece);
	       }
	       mover = piece;
	    }
      }

      if (!found) {
         if (Log.debug) {
	    Log.debug(DEBUG, 
	       "Illegal Move " 
               + "piece: " + piece_index + " file: " + orig_f 
               + " rank: " + orig_r + " dest: " + dest);
	    Log.debug2(DEBUG, this);
	    Log.debug2(DEBUG, dumpLegalMoves());
	    Log.debug2(DEBUG, dumpLegalMoves(!isBlackMove));
	 }
	 throw new IllegalMoveException("Illegal Move");
      }

      if (found && count > 1) {
         if (Log.debug) {
	    Log.debug(DEBUG,
                      "AMBIGUOUSMOVE!!!! to " + dest);
	    Log.debug2(DEBUG, this);
	    Log.debug2(DEBUG, dumpLegalMoves());
	    Log.debug2(DEBUG, dumpLegalMoves(!isBlackMove));
	 }
         throw new AmbiguousChessMoveException("Ambiguous Move", 
	    piece_index, 
	    orig_f,
	    orig_r,
	    dest.file,
	    dest.rank,
	    dupes);
      }

      return mover.orig;
   }

   //promotion//////////////////////////////////////////////////////////////

   /* promote() ***********************************************************/
   /** used for pawn promotion.
    *  replaces the pawn on the board and in the team set.
    */
   protected void promote (ChessPiece pawn, ChessPiece promo) {
      promo.orig = pawn.orig;
      promo.orig.piece = promo;
      promo.board = this;
      promo.isBlack = pawn.isBlack;

      if (pawn.isBlack) {
	 blackTeam.set(blackTeam.indexOf(pawn), promo);
      }
      else {
	 whiteTeam.set(whiteTeam.indexOf(pawn), promo);
      }
   }

   //Checking for Checkmate/////////////////////////////////////////////////

   /* isCheckmate () ******************************************************/
   /** returns true if the current board is in checkmate
    */
   public boolean isCheckmate () {
      if (lastMove != null)
         return lastMove.isCheckmate();

      if (staleLegalDests)
	 genLegalDests();

      if (getLegalMoveCount() == 0 && isCheck())
	 return true;
      else 
	 return false;
   }

   /* isCheck ************************************************************/
   /** returns true if the King in the side to move is in check
    *  or double-check.
    */
   public boolean isCheck () {
      boolean check = false;

      if (lastMove != null)
         return lastMove.isCheck();

      if (staleLegalDests)
         genLegalDests();


      check = (isBlackMove) ? blackKing.isInCheck() : whiteKing.isInCheck();

      if (Log.debug)
         Log.debug(ChessBoard.DEBUG, "the King in check: " + check);

      return check;
   }

   /* isDoubleCheck *****************************************************/
   /** returns true if the King on the side to move is threatened by 
    *  two pieces.
    */
   public boolean isDoubleCheck () {
      boolean dcheck = false;

      if (lastMove != null)
         return lastMove.isDoubleCheck();

      if (staleLegalDests)
         genLegalDests();

      dcheck = getThreats( ((isBlackMove) ? blackKing : whiteKing)).length == 2;

      return dcheck;
   }

   /* isStalemate ***********************************************************/
   /** returns true if there are no legal moves and the King is not in check 
    */
   public boolean isStalemate () {
      if (lastMove != null)
         return lastMove.isStalemate();

      if (staleLegalDests)
	 genLegalDests();

      if (getLegalMoveCount() == 0 && !isCheck())
	 return true;
      else 
	 return false;
   }

   //Info///////////////////////////////////////////////////////////////////
   /** returns the character matrix array of the current board.  This will
    *  contain characters to represent the pieces.  The characters are of
    *  the English SAN set PNBRQK (for white) and pnbrqk (for black)
    *
    * @return char[file][rank]
    */
   public char[][] toCharArray () {
      char[][] board = new char[MAX_RANK+1][MAX_FILE+1];
      for (byte r=0; r < MAX_RANK; r++) 
         for (byte f=0; f < MAX_FILE; f++) 
            if (squares[f][r].isOccupied()) {
	       switch (squares[f][r].piece.getIndex() 
	               % ChessPiece.BLACK_OFFSET) {
	          case Pawn.INDEX:   board[f][r] = 'P'; break;
	          case Knight.INDEX: board[f][r] = 'N'; break;
	          case Bishop.INDEX: board[f][r] = 'B'; break;
	          case Rook.INDEX:   board[f][r] = 'R'; break;
	          case Queen.INDEX:  board[f][r] = 'Q'; break;
	          case King.INDEX:   board[f][r] = 'K'; break;
		  default:
	       }
	       if (squares[f][r].piece.getIndex() >= ChessPiece.BLACK_OFFSET) 
	          board[f][r] = Character.toLowerCase(board[f][r]);
	    }
      return board;
   }

   /* getCapturedPieces ******************************************************/
   /** get the pieces that have been captured.
    *  @param isBlack if true returns Black pieces that have been captured.
    */
   public ChessPiece[] getCapturedPieces (boolean isBlack) {
      return getCaptures(isBlack, true);
   }

   /* getUnCapturedPieces ***************************************************/
   /** get the pieces that haven't been captured.
    *  @param isBlack if true returns Black pieces that haven't been captured.
    */
   public ChessPiece[] getUnCapturedPieces (boolean isBlack) {
      return getCaptures (isBlack, false);
   }

   /* getCaptures ************************************************************/
   /** the generic version of get(Un)CapturedPieces
    *  @param isCaptured decides if you want the Captured pieces (true)
    *         or the un-captured pieces (false)
    */
   protected ChessPiece[] getCaptures (boolean isBlack, boolean isCaptured) {
      ChessPiece[] pows = null;
      List<ChessPiece> team = (isBlack) ? blackTeam : whiteTeam;
      int count = 0;
      ChessPiece piece = null;

         for (int i=0; i < team.size(); i++) {
	    if (team.get(i).isCaptured() == isCaptured)
	       count++;
	 }

	 if (count > 0) {
	    pows = new ChessPiece[count];
	    count = 0;
	    //put them into the new array
	    for (int i=0; i < team.size(); i++) {
	       piece = team.get(i);
	       if (piece.isCaptured() == isCaptured)
	          pows[count++] = piece;
	          
	    }
	 }
      return pows;
   }

   /* getMaterialCount *******************************************************/
   /** gets the material count evaluation for the side specified.
    *  The evaluation is based the following values.
    *  Pawn   = 1, 
    *  Knight = 3,
    *  Bishop = 3,
    *  Rook   = 5,
    *  Queen  = 9.
    *  The King is not taken into account as it has an infinite value.
    *
    *  @param isBlack the color of the side you wish to evaluate.
    */
   public int getMaterialCount (boolean isBlack) {
      int material = 0; 
      List<ChessPiece> team = (isBlack) ? blackTeam : whiteTeam;
      ChessPiece piece = null;

         for (int i=0; i < team.size(); i++) {
	    piece = team.get(i);
	    if (!piece.isCaptured()) {
	       switch (piece.getIndex() % ChessPiece.BLACK_OFFSET) {
		  case Pawn.INDEX: material   += 1; break;
		  case Knight.INDEX:
		  case Bishop.INDEX: material += 3; break;
		  case Rook.INDEX:   material += 5; break;
		  case Queen.INDEX:  material += 9; break;
	       }
	    }
	 }

      return material;
   }

   /* setBlackMove() *********************************************************/
   /** sets who's turn it is to move.  Setting this midway through
    *  throw a game will throw an exception.
    *
    * @throws IllegalStateException if a move has already been executed.
    */
   public void setBlackMove (boolean t) {
      if (lastMove != null)
         throw new IllegalStateException(
	    "can't set the move color for a game in progress.");
      isBlackMove = t;
   }

   /* isBlackMove ***********************************************************/
   /** who's move is it?
    */
   public boolean isBlackMove () {
      return isBlackMove;
   }

/*
   public Square getSquare (char[] coords) {
      return getSquare(coords[0], coords[1]);
   }
*/

   /* getSquare *************************************************************/
   /** cx and cy should subscribe to the default locale of 
    *  boardgame.chess.io.SAN.  That is, [a-h] & [1-8]
    */
   public Square getSquare (char file, char rank) {
      Square sq;
         sq = squares[san.fileToNum(file)-1][san.rankToNum(rank)-1]; 
      return sq; 
   }

   /* getSquare *************************************************************/
   /** x and y should be in the range 1-8
    */
   public Square getSquare (int x, int y) {
      return squares[x-1][y-1];
   }

   //Display/////////////////////////////////////////////////////////////////

   /* toString *************************************************************/
   /** a diagnostic tool which shows the board state.
    */
   public String toString () {
      SAN san = new SAN();
      StringBuffer s_buffer = new StringBuffer();
      StringBuffer last_line = new StringBuffer();
      char c = ' ';

         last_line.append("\n    ");
         for (byte r=MAX_RANK-1, f=0; r >= 0; r--,f=0) {
            s_buffer.append(san.rankToChar(squares[f][r].rank) + "   ");


            for (f=0; f < MAX_FILE; f++) {
               if (squares[f][r].isOccupied()) { 
	           c = san.pieceToChar(squares[f][r].piece);
		   if (squares[f][r].piece.isBlack())
		      c = Character.toLowerCase(c);
                   s_buffer.append(c + " ");
	       }
               else 
		  if (squares[f][r].isBlack())
		     s_buffer.append("  ");
		  else
		     s_buffer.append("# ");
               if (r==MAX_RANK-1) 
                  last_line.append(Character.toUpperCase(
		     san.fileToChar(squares[f][r].file)) + " ");
            }
            s_buffer.append('\n'); 

         }
         s_buffer.append(last_line);
         return s_buffer.toString();
   }

   //Boardable Interface///////////////////////////////////////////////////

   /* setPositionDefault ****************************************************/
   /** sets the board position to the default chess board setup.
    *
    * @throws IllegalStateException if this move is used on a board that
    *                               already has moves played on it.
    */
   public void setPositionDefault () {
      if (lastMove != null)
         throw new IllegalStateException(
	    "can't set the board position for a game in progress.");
      setPositionClear();
      setPosition(DEFAULT_POSITION);
      isInitialPositionDefault = true;
   }

   /* setPositionClear ******************************************************/
   /** removes all pieces from the board
    *
    * @throws IllegalStateException if this move is used on a board that
    *                               already has moves played on it.
    */
   public void setPositionClear () {
      if (lastMove != null)
         throw new IllegalStateException(
	    "can't set the board position for a game in progress.");

      for (byte r=0; r < MAX_RANK; r++)
         for (byte f=0; f < MAX_FILE; f++)
	    squares[f][r].piece = null;
      blackTeam.clear();
      whiteTeam.clear();
      blackKing = null;
      whiteKing = null;
      isInitialPositionDefault = false;
      plyCount50 = 0;
      enpassantFile = NO_ENPASSANT;
      moveNumber = 0;
   }

   /* setPosition () *********************************************************/
   /**set the position from an 8x8 matrix using the FEN characters to
    * represent piece positions.  Lowercase is black.  P=Pawn, N=Knight,
    * B=Bishop, R= Rook, Q=Queen, K=King.  Better is to 
    * use setPostion(byte[][]).<br>
    *
    * Note: if rooks and kings are placed on traditional chess default
    *       squares the appropriate castleable bit will be set true.
    *       If not, then it will eb set to false.
    *
    * @throws IllegalStateException if this move is used on a board that
    *                               already has moves played on it.
    */
   public void setPosition (char[][] matrix) {
      if (lastMove != null)
         throw new IllegalStateException(
	    "can't set the board position for a game in progress.");

      boolean wking = false, bking = false;

      if (matrix.length != MAX_RANK || matrix[0].length != MAX_FILE)
         throw new IllegalArgumentException (
	    "setPosition() takes a matrix the same dimensions as the board."
	    );

      isInitialPositionDefault = false;


      setPositionClear();
      for (byte file=0; file < matrix.length; file++) {
         for (byte rank=0; rank < matrix[file].length; rank++) {

	    switch (matrix[file][rank]) {

	       case 'p': addPawn(file+1, rank+1, true); break;
	       case 'P': addPawn(file+1, rank+1, false); break;
               case 'n': addKnight(file+1, rank+1, true); break;
	       case 'N': addKnight(file+1, rank+1, false); break;
	       case 'b': addBishop(file+1, rank+1, true); break;
	       case 'B': addBishop(file+1, rank+1, false); break;
	       case 'r': addRook(file+1, rank+1, true); break;
	       case 'R': addRook(file+1, rank+1, false); break;
	       case 'q': addQueen(file+1, rank+1, true); break;
	       case 'Q': addQueen(file+1, rank+1, false); break;
	       case 'k': addKing(file+1, rank+1, true); 
	          bking = true;
	          break;
	       case 'K': addKing(file+1, rank+1, false); 
	          wking = true;
	          break;
	       default:
	    }
	 }
      }

      //try to figure out castling rules for default positions
      /*
      if (Log.debug(DEBUG)) {
         Log.debug(DEBUG, "setting this position");
	 for (byte file=0; file < matrix.length; file++) 
	    for (byte rank=0; rank < matrix[file].length; rank++) 
	       Log.debug2(DEBUG, "[" + file + "][" + rank + "] = '"
	         + matrix[file][rank] + "'");
      }
      */
      if (wking)
	 if (matrix[4][0] == 'K') {
	    if (matrix[0][0] != 'R') {
	       if (Log.debug)
	          Log.debug(DEBUG, "setting white q-side castle: false");
	       setWhiteCastleableQueenside(false);
	    }
	    if (matrix[7][0] != 'R') {
	       if (Log.debug)
	          Log.debug(DEBUG, "setting white k-side castle: false");
	       setWhiteCastleableKingside(false);
	    }
	 }
	 else {
	    if (Log.debug)
	       Log.debug(DEBUG, "setting white castleable: false");
	    whiteKing.moveCount = 1;
	 }

      if (bking)
	 if (matrix[4][7] == 'k') {
	    if (matrix[0][7] != 'r') {
	       if (Log.debug)
	          Log.debug(DEBUG, "setting black q-side castle: false");
	       setBlackCastleableQueenside(false);
	    }
	    if (matrix[7][7] != 'r') {
	       if (Log.debug)
	          Log.debug(DEBUG, "setting black k-side castle: false");
	       setBlackCastleableKingside(false);
	    }
	 }
	 else {
	    if (Log.debug)
	       Log.debug(DEBUG, "setting black castleable: false");
	    blackKing.moveCount = 1;
	 }
   }

   /* addPawn **************************************************************/
   /** adds a Pawn to a Square on the board and to the team of pieces
    *  of the correct color.
    *  <br>
    *  NOTE: if the game is already is progress it might not be wise
    *  to add a piece.  the results are undefined.
    *
    * @param file the X coordinate (1-8)
    * @param rank the Y coordinate (1-8)
    */
   public void addPawn (int file, int rank, boolean isBlack) {
      ChessPiece p;
      Square orig = getSquare(file, rank);
         orig.setOccupant(p = new Pawn (isBlack, orig, this)); 
         if (isBlack) blackTeam.add(p);
         else         whiteTeam.add(p);
   }

   /* addKight *************************************************************/
   /** adds a Knight to a Square on the board and to the team of pieces
    *  of the correct color.
    *  <br>
    *  NOTE: if the game is already is progress it might not be wise
    *  to add a piece.  the results are undefined.
    *
    * @param file the X coordinate (1-8)
    * @param rank the Y coordinate (1-8)
    */
   public void addKnight (int file, int rank, boolean isBlack) {
      ChessPiece p;
      Square orig = getSquare(file, rank);
         orig.setOccupant(p = new Knight (isBlack, orig, this)); 
         if (isBlack) blackTeam.add(p);
         else         whiteTeam.add(p);
   }

   /* addBishop ************************************************************/
   /** adds a Bishop to a Square on the board and to the team of pieces
    *  of the correct color.
    *  <br>
    *  NOTE: if the game is already is progress it might not be wise
    *  to add a piece.  the results are undefined.
    *
    * @param file the X coordinate (1-8)
    * @param rank the Y coordinate (1-8)
    */
   public void addBishop (int file, int rank, boolean isBlack) {
      ChessPiece p;
      Square orig = getSquare(file, rank);
         orig.setOccupant(p = new Bishop (isBlack, orig, this)); 
         if (isBlack) blackTeam.add(p);
         else         whiteTeam.add(p);
   }

   /* addRook **************************************************************/
   /** adds a Rook to a Square on the board and to the team of pieces
    *  of the correct color.
    *  <br>
    *  NOTE: if the game is already is progress it might not be wise
    *  to add a piece.  the results are undefined.
    *
    * @param file the X coordinate (1-8)
    * @param rank the Y coordinate (1-8)
    */
   public void addRook (int file, int rank, boolean isBlack) {
      ChessPiece p;
      Square orig = getSquare(file, rank);
         orig.setOccupant(p = new Rook (isBlack, orig, this)); 
         if (isBlack) blackTeam.add(p);
         else         whiteTeam.add(p);
   }

   /* addQueen *************************************************************/
   /** adds a Queen to a Square on the board and to the team of pieces
    *  of the correct color.
    *  <br>
    *  NOTE: if the game is already is progress it might not be wise
    *  to add a piece.  the results are undefined.
    *
    * @param file the X coordinate (1-8)
    * @param rank the Y coordinate (1-8)
    */
   public void addQueen (int file, int rank, boolean isBlack) {
      ChessPiece p;
      Square orig = getSquare(file, rank);
         orig.setOccupant(p = new Queen (isBlack, orig, this)); 
         if (isBlack) blackTeam.add(p);
         else         whiteTeam.add(p);
   }

   /* addKing **************************************************************/
   /** adds a King to a Square on the board and to the team of pieces
    *  of the correct color.  You cannot add more than one King to the
    *  board.  The old king will be over written.
    *  <br>
    *  NOTE: if the game is already is progress it might not be wise
    *  to add a piece.  the results are undefined.
    *
    * @param file the X coordinate (1-8)
    * @param rank the Y coordinate (1-8)
    */
   public void addKing (int file, int rank, boolean isBlack) {
      ChessPiece p;
      Square orig = getSquare(file, rank);
         orig.setOccupant(p = new King (isBlack, orig, this)); 
         if (isBlack) {
	    if (blackKing != null)
	       blackTeam.remove(blackKing);
            blackTeam.add(p);
            blackKing = (King) p;
         }
         else {
	    if (whiteKing != null)
	       whiteTeam.remove(whiteKing);
            whiteTeam.add(p);
            whiteKing = (King) p;
         }
   }

   public boolean isWhiteCastleableKingside () {
      return whiteKing.isCastleableKingside();
   }

   public boolean isWhiteCastleableQueenside () {
      return whiteKing.isCastleableQueenside();
   }

   public boolean isBlackCastleableKingside () {
      return blackKing.isCastleableKingside();
   }

   public boolean isBlackCastleableQueenside () {
      return blackKing.isCastleableQueenside();
   }

   public void setWhiteCastleableKingside (boolean t) {
      whiteKing.setCastleableKingside(t);
   }

   public void setWhiteCastleableQueenside (boolean t) {
      whiteKing.setCastleableQueenside(t);
   }

   public void setBlackCastleableKingside (boolean t) {
      blackKing.setCastleableKingside(t);
   }

   public void setBlackCastleableQueenside (boolean t) {
      blackKing.setCastleableQueenside(t);
   }

   /* getEnPassantFile *****************************************************/
   /** returns the file index (1-8) of the file where enpassant is legal
    * @return NO_ENPASSANT if the file is not set
    */
   public byte getEnPassantFile () {
      return enpassantFile;
   }

   /* setEnPassantFile *****************************************************/
   /** sets the file index (1-8) of the file where enpassant is legal
    * @param f NO_ENPASSANT if the file is not set
    */
   public void setEnPassantFile (int f) {
      if (f > MAX_FILE) 
         throw new IllegalArgumentException(
	    "EnPassant file cannot be larget than MAX_FILE");
      enpassantFile = (byte) f;
   }

   /* setEnPassantFile *****************************************************/
   /** sets the file (a-h) of the file where enpassant is legal
    * @param f '-' if the file is not set
    */
   public void setEnPassantFile (char f) {
      enpassantFile = san.fileToNum(f);
   }

   /* isEnPassantFile ******************************************************/
   /** tests to see if the file (a-h) is the enpassant file.
    */
   public boolean isEnPassantFile (char f) {
      if (enpassantFile == NO_ENPASSANT) return false;
      return enpassantFile == san.fileToNum(f);
   }

   /* isEnPassantFile ******************************************************/
   /** tests to see if the file (1-8) is the enpassant file.
    */
   public boolean isEnPassantFile (int f) {
      if (enpassantFile == NO_ENPASSANT) return false;
      return enpassantFile == f;
   }

   /* get50MoveRulePlyCount ************************************************/
   /** returns how many ply (half-moves) the board is into the 50 move rule.
    */
   public int get50MoveRulePlyCount () {
      return plyCount50;
   }

   /* get50MoveRulePlyCount ************************************************/
   /** sets many ply (half-moves) the board is into the 50 move rule.
    */
   public void set50MoveRulePlyCount (int i) {
      plyCount50 = i;
   }

   /* is50MoveRuleApplicible **********************************************/
   /** tests to see if the 50 move rule can be applied to this game, thus
    *  indicating a legal Draw may be called.
    */
   public boolean is50MoveRuleApplicible () {
      return (plyCount50 > 99);
   }

   /* getCurrentMoveNumber ************************************************/
   /** get move number for the last move executed
    */
   public int getCurrentMoveNumber () {
      return moveNumber;
   }

   /* isInitialPositionDefault *********************************************/
   /** was the initial position of the board (before any move) the default
    *  position of traditional chess?
    */
   public boolean isInitialPositionDefault () {
      return isInitialPositionDefault;
   }

   //Events///////////////////////////////////////////////////////////////////

   /* addBoardListerner *****************************************************/
   public void addBoardListener (BoardListener bl) {
      int size = 0;
      boolean found = false;
      BoardListener[] bls = null;

      if (listeners != null) {
         size = listeners.length;

	 for (int i = 0; !found && i < size; i++) {
	    found = listeners[i] == bl;
	 }
	 if (found) return;
      }

      bls = new BoardListener[size+1];
      if (listeners != null)
         System.arraycopy(listeners, 0, bls, 0, size);
      bls[size] = bl;

      listeners = bls;
   }

   /* getBoardListerners ****************************************************/
   public BoardListener[] getBoardListeners () {
      return listeners;
   }

   /* removeBoardListener ***************************************************/
   public void removeBoardListener (BoardListener bl) {
      int size = 0,
          idx  = 0;
      boolean found = false;
      BoardListener[] bls = null;

      if (listeners == null)
         return;

      size = listeners.length;

      for (int i = 0; !found && i < size; i++) {
	 found = listeners[i] == bl;
	 if (found) idx = i;
      }

      if (!found) return;

      listeners[idx] = null;

      bls = new BoardListener[size-1];

      if (idx != 0)
	 System.arraycopy(listeners, 0, bls, 0, idx);

      if (idx != size-1)
	 System.arraycopy(listeners, idx+1, bls, idx, size-1);

      listeners = bls;
   }

   /* fireBoardEvent ********************************************************/
   public void fireBoardEvent (int event) {
      if (listeners != null)
         for (int i=0; i < listeners.length; i++)
	    listeners[i].boardUpdate(this, event);
   }

   ///////////////////////////////////////////////////////////////////////////
   /* equals() **************************************************/
   /** standard override of the equals(Object) function.
    *  This tests if the positions of the two board are exactly 
    *  equal.  
    *  <p>
    *  Some things checked are:<br>
    *  1) who's move <br>
    *  2) pieces on the same squares <br>
    *  3) castling rights <br>
    *  4) enpassant file  <br>
    *  <p>
    *  Things not checked, but you might think they were:<br>
    *  1) last move played <br>
    *  2) plyCount (for 50 move rule determination <br>
    *  3) the move number <br>
    *  4) is the initial position the default for both boards
    */
   public boolean equals (Object o) {
      if (o == this) return true;
      if ((o == null) || (o.getClass() != this.getClass()))
         return false;
      //FIXME: this could use FEN to do the comparison but wouldn't get the Log
      
      boolean equal = true;
      ChessBoard b = (ChessBoard) o;

         if (Log.debug) {
	    Log.debug(DEBUG, "comparing boards");
	 }

         equal = this.isBlackMove == b.isBlackMove;
	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "move parity failed");
	    
	 if (equal) 
	    equal = this.squares.length == b.squares.length;

	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "board dimension(f) failed");
	    
	 if (equal) 
	    equal = this.squares[0].length == b.squares[0].length;

	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "board dimension(r) failed");
	    
	 if (equal)
	    equal = this.isWhiteCastleableQueenside()
	            == b.isWhiteCastleableQueenside();

	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "castling QW failed");
	    
	 if (equal)
	    equal = this.isBlackCastleableQueenside()
	            == b.isBlackCastleableQueenside();

	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "castling QB failed");
	    
	 if (equal)
	    equal = this.isWhiteCastleableKingside()
	            == b.isWhiteCastleableKingside();

	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "castling KW failed");
	    
	 if (equal)
	    equal = this.isBlackCastleableKingside()
	            == b.isBlackCastleableKingside();

	 if (Log.debug && !equal)
	    Log.debug2(DEBUG, "castling KB failed");
	    
	 if (equal)
	    equal = this.enpassantFile == b.enpassantFile;

	 if (Log.debug && !equal) 
	    Log.debug2(DEBUG, "enpassant failed");
	    
	 if (equal) {
	    for (byte i=0; i< this.squares.length && equal; i++) {
	       for (byte j=0; j < this.squares[i].length && equal; j++) {
	          if (this.squares[i][j].getOccupant() == null)
		     equal = b.squares[i][j].getOccupant() == null;
		  else {
		     equal = b.squares[i][j].getOccupant() != null;

		     if (Log.debug && !equal)
			Log.debug2(DEBUG, "squares[" + i + "]["
					 + j + "] nulls failed");

                     if (equal) {
			equal = this.squares[i][j].getOccupant().getIndex()
				== b.squares[i][j].getOccupant().getIndex();

			if (Log.debug && !equal)
			   Log.debug2(DEBUG, "squares[" + i + "]["
					    + j + "].Occupant failed");
		     }

		  }
	       }
	    }
	 }

	 if (Log.debug && equal)
	    Log.debug2(DEBUG, "boards are the same");

	 return equal;
   }

   /* hashCode ***********************************************************/
   public int hashCode () {

      int hash = 7;
      hash = 31 * hash + fen.boardToString(this).hashCode();

      return hash;
   }

   //Diagnostics//////////////////////////////////////////////////////////
   public String dumpLegalMoves () {
      return dumpLegalMoves(isBlackMove);
   }

   public String dumpOpposingMoves () {
      return dumpLegalMoves(!isBlackMove);
   }

   public String dumpLegalMoves (boolean blacksMoves) {
      StringBuffer sb = new StringBuffer();
      ChessPiece p;
      List<ChessPiece> team = (blacksMoves) ? blackTeam : whiteTeam;

      if (blacksMoves) 
         sb.append("Black's team moves-----------------------\n");
      else
         sb.append("White's team moves-----------------------\n");

      for (int i=0; i < team.size(); i++) {
         p = team.get(i);
	 sb.append( (!p.captured) ? " " : "x");
         sb.append(p) 
	   .append("(")
	   .append(p.orig)
	   .append(") ")
	   .append(p.getLegalDests())
	   .append("\n");
      }
      return sb.toString();
   }

   /** full dump of everything
    */
   public String dump () {
      StringBuffer sb = new StringBuffer();

      sb.append(toString())
	.append("\n")
        .append("isInitialPositionDefault: ")
	.append(isInitialPositionDefault)
	.append("\n")
        .append("isBlackMove: ")
	.append(isBlackMove)
	.append("\n")
        .append("enpassant file: ");
      if (enpassantFile > 0)
        sb.append(san.fileToChar(enpassantFile));
      else
        sb.append("-");
      sb.append("\n")
	.append("lastMove: ")
	.append(lastMove)
	.append("\n")
	.append("moveNumber: ")
	.append(moveNumber)
	.append("\n")
	.append("plyCount50: ")
	.append(plyCount50)
	.append("\n")
	.append("staleLegalDests: ")
	.append(staleLegalDests)
	.append("\n");
      sb.append(dumpLegalMoves())
        .append(dumpOpposingMoves());

      return sb.toString();
   }
   
   public Square findKingSquare(boolean black) {
      for (int y = 1; y <= 8; y++) {
         for (int x = 1; x <= 8; x++) {
            Square s = getSquare(x, y);
            if ((s.piece instanceof King) && (s.piece.isBlack() == black)) {
               return s;
            }
         }
      }
      throw new RuntimeException("King not found.");
   }
}

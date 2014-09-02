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


/* ChessMove ****************************************************************/
/** ChessMove is an implementation of the Command Pattern of OOD.  It 
 *  contains mostly references to other objects (Squares, Pieces etc).
 *<p>
 *  There are two two phases to the Move object
 *<p> 1) unverified (rare state)<br>
 *      The move is but coordinates, not associated with a board or
 *      history list.  There is no guarentee that the move is valid.
 *      Executing a method that relies on valid information will result
 *      in an UnexecutedMoveException.  Such functions are isCheckmate(),
 *      isStalemate(), getChessPiece() etc).
 *<p> 2) verified (common state)<br>
 *      The move has been exceuted at least once on a board and verified
 *      to be a legal move.  By default most of the constructors available
 *      check move legality when the move is created.  However, if the move
 *      gets played (via ChessBoard, or History) on the board after the
 *      ChessBoard position has changed the move might throw an 
 *      IllegalMoveException.  Once the move is in the verified state
 *      all functions are available including isStalemate() etc.
 *<p>
 *  Typical usage goes like this:<br>
 *<pre>
 *  ChessGame game = new ChessGame();
 *  History history = game.getHistory();
 *  ChessMove move = new ChessMove (game.getBoard(), 5, 2, 5, 4); //1.e4
 *  history.add(move);
 *</pre>
 *<p>
 *  An alternative way to do the same thing by using the 
 *  {@link ictk.boardgame.chess.io.SAN} object:
 *<pre>
 *   SAN san = new SAN();
 *   ChessMove move = san.stringToMove(board, "e4");
 *</pre>
 *<p>
 *  Castling can be done via the following:
 *<pre>
 *   ChessMove move = ChessMove(board, ChessMove.CASTLE_QUEENSIDE);
 *</pre>
 *<p>Or
 *<pre>
 *   ChessMove move = san.stringToMove(board, "O-O-O");
 *</pre>
 *<p>
 *  A Promotion move might look like this:
 *<pre>
 *   ChessMove move = ChessMove(board, 5, 7, 5, 8, Queen.INDEX);
 *</pre>
 */
public class ChessMove extends Move {
      /** a mask for Log.debug() */
   public static long DEBUG = Log.Move;

      /** Queenside always means to white's left or black's right
       ** that is, toward the a-file.  This is true no matter what
       ** the setup of the board (as in Fischer Random) */
   public static final int CASTLE_QUEENSIDE  = -1;
      /** Kingside always means to white's right or black's left
       ** that is, toward the h-file.  This is true no matter what
       ** the setup of the board (as in Fischer Random) */
   public static final int CASTLE_KINGSIDE =  1;

   //Instance//////////////////////////////////////////////////////////////////
      /** the board this move is indented to be played on */
   protected ChessBoard board;
      /** the origin square */
   protected Square orig,
      /** the destination square */
          dest;         
   /**
    * The original square of the rook if a castling was performed.
    * This is needed to support Chess960 when reverting a move, since the rook starting
    * position is not always the same.
    */
   private Square rookCastleOrig;

      /** the piece making the move */
   protected ChessPiece piece,         
      /** the piece captured in the move */
         casualty,
      /** pawn's promotion piece */
         promotion;        

      /** the board's previous enpassant file before this move was executed*/
   protected int prevEnPassantFile;

      /** for 50 move rule */
   protected int prevPlyCount50;

      /** does the move place the king in check */
   protected boolean check, 
      /** does the move place the king in double check */
	   doublecheck,
      /** does the move result in checkmate */
           checkmate,
      /** does the move result in stalemate */
	   stalemate,
      /** is the move a castle on the queen's side */
	   castleQueenside,
      /** is the move a castle on the king's side */
	   castleKingside;

      /** how unique the move is {file, rank} */
   protected boolean[] unique = {false, false};

   //Constructors/////////////////////////////////////////////////////////////
   /** only used for castle moves.  
    *  Legal paramters are CASTLE_QUEENSIDE and CASTLE_KINGSIDE.
    */
   public ChessMove (ChessBoard b, int i) 
          throws IllegalMoveException {
      if (b == null) 
         throw new IllegalArgumentException (
	    "A ChessMove cannot be associated with a null ChessBoard");
      board = b;
      switch (i) {
         case CASTLE_QUEENSIDE: castleQueenside = true; break;
	 case CASTLE_KINGSIDE: castleKingside = true; break;
	 default:
	    throw new IllegalArgumentException(
	       "illegal parameter sent to ChessMove Castle Constructor;"
	       + " check docs");
      }
      board.verifyIsLegalMove(this);
   }

   /** international coordinate based constructor
    *  all numbers should be in the range 1-8 
    *
    * @param of origin file
    * @param or origin rank
    * @param df destination file
    * @param dr destination rank
    */
   public ChessMove (ChessBoard b, int of, int or, int df, int dr) 
          throws IllegalMoveException {
      this(b, of,or,df,dr, ChessPiece.NULL_PIECE);
   }

   /** main int constructor 
    * All numbers should be in the range 1-8.
    * @param of origin file
    * @param or origin rank
    * @param df destination file
    * @param dr destination rank
    * @param promo must be <ChessPieceClass>.INDEX
    */
   public ChessMove (ChessBoard b, int of, int or, 
                                      int df, int dr, int promo) 
          throws IllegalMoveException {
      board = b;
      orig = board.squares[of-1][or-1];
      dest = board.squares[df-1][dr-1];
      if (promo > 0) promotion = ChessPiece.toChessPiece(promo);

      if (promotion != null 
             && (promotion.isKing() || promotion.isPawn()))
         throw new IllegalMoveException("Can't promote a pawn to King or Pawn");

      board.verifyIsLegalMove(this);
   }

   /** This constructor take Square objects.  The Square objects
    *  don't have to be from the board that the move will be 
    *  executed on.  These moves are only used to get the 
    *  coordinates of the move.
    *
    * @param o origin square
    * @param d destination square
    */
   public ChessMove (ChessBoard b, Square o, Square d)
          throws IllegalMoveException {
      this(b, o, d, null);
   }

   /** This constructor take Square objects.  The Square objects
    *  don't have to be from the board that the move will be 
    *  executed on.  These moves are only used to get the 
    *  coordinates of the move.  The promotion piece, however, will.
    *
    * @param o origin square
    * @param d destination square
    */
   public ChessMove (ChessBoard b, Square o, Square d, ChessPiece promo)
          throws IllegalMoveException {
      if (b == null) 
         throw new IllegalArgumentException (
	    "A ChessMove cannot be associated with a null ChessBoard");

      board = b;
      orig = o;
      dest = d;
      promotion = promo;
      if (promotion != null 
             && (promotion.isKing() || promotion.isPawn()))
         throw new IllegalMoveException("Can't promote a pawn to King or Pawn");

      board.verifyIsLegalMove(this);
   }


   /** This constructor takes a ChessPiece on the board to move. And a 
    *  Desitnation where the piece is to move to.
    */
   public ChessMove (ChessPiece piece, Square destination) 
          throws IllegalMoveException {
      this (piece, destination, null);
   }

   /** This constructor takes a ChessPiece on the board to move, A 
    *  Desitnation where the piece is to move to, and the ChessPiece
    *  it is to promote to when it gets there. (assuming it's a pawn
    *  that's moving)
    */
   public ChessMove (ChessPiece piece, Square destination, ChessPiece promotion)
          throws IllegalMoveException {
      this((ChessBoard) piece.getBoard(), 
           piece.getSquare(),
	   destination,
	   promotion);
   }

   /** This is a 'private' constructor.  It does not check for the
    *  legality of the move on the board but is otherwise the same.
    *  This is _only_ used when the legality check might cause an
    *  infinite loop, or when, for some reason, the CPU needs saving
    */
   ChessMove (Square o, Square d, ChessPiece promo, ChessBoard b) {
      board = b;
      orig = o;
      dest = d;
      promotion = promo;
   }


   //Accessors///////////////////////////////////////////////////////////

   //Mutators////////////////////////////////////////////////////////////

   /* dispose () *********************************************************/
   /** reclaims all resources and recursively deletes all branch moves.
    */
   public void dispose () {
      super.dispose();
         piece = null;
         casualty = null;
         promotion = null;
	 orig = null;
	 dest = null;
	 //coord = null;
   }

   /* execute **********************************************************/
   /** affects the change on the board.
    *  special handling of enpassant and castling.
    *  This function also asks the associated board for fire a
    *  boardUpdate() to all listeners.
    */
   protected void execute ()  
          throws IllegalMoveException, 
	         OutOfTurnException {

      // if the move isn't verified then we need to locate
      // the origin and destination on the board
      // locateSquares();

      prevEnPassantFile = board.enpassantFile;

      boolean castling = castleQueenside || castleKingside;
      ChessPiece rookForCastling = null;
      King kingForCastling = null;
      if (castling) {
         orig = board.findKingSquare(board.isBlackMove);
         kingForCastling = (King) orig.piece;
         dest = kingForCastling.findCastlingDestination(castleQueenside);

         // Find the rook before we move the King (possibly on top of the rook).
         rookForCastling = kingForCastling.findMyRook(castleQueenside);
         rookCastleOrig = rookForCastling.getSquare();

         // Validate that castling is a legal move.
         if (castleKingside) {
            if (!kingForCastling.isCastleableKingside()) {
               throw new IllegalMoveException(buildExecuteCastleErrorString(kingForCastling), this);
            }
         } else if (!kingForCastling.isCastleableQueenside()) {
            throw new IllegalMoveException(buildExecuteCastleErrorString(kingForCastling), this);
         }
      }

      piece = orig.piece;

      if (piece == null)
         throw new IllegalMoveException("No piece to move.", this);

      if (Log.debug) {
         Log.debug(DEBUG, "executing move: " + this);
         Log.debug2(DEBUG, piece.dump());
         Log.debug2(DEBUG, board);
      }

      if (piece.isBlack() != board.isBlackMove)
         throw new OutOfTurnException("It is " + ((board.isBlackMove) ? "Black" : "White") + "'s move");

      if (!verified && !piece.isLegalDest(dest)) {
         if (Log.debug) {
            Log.debug(DEBUG, "tried to execute move with illegal destination");
            Log.debug2(DEBUG, "piece is: " + piece.dump());
            Log.debug2(DEBUG, "dest is: " + dest);
         }
         throw new IllegalMoveException("Illegal move " + this, this);
      }

      if (!castling) {
         // In Chess960, the king or rook can take the other piece's place,
         // hence the need to ensure this wasn't a castle.
         casualty = dest.piece;
      }

	 //special enpassant rules
	 if (piece.isPawn()
	     && casualty == null
	     && orig.file != dest.file) {
	    casualty = board.getSquare(dest.file, orig.rank).piece;
	    if (Log.debug) {
	       Log.debug(DEBUG, "enpassant encounterd: " 
	           + this + "orig: " + orig 
	           + " dest:" + dest + " casualty: " + casualty);
	    }
	 }
	 else {
	    board.enpassantFile = ChessBoard.NO_ENPASSANT;
	 }

         //50MoveRule
	 if (piece.isPawn()
	    || casualty != null) {
	    prevPlyCount50 = board.plyCount50;
	    board.plyCount50 = 0;
	 }
	 else 
	    board.plyCount50++;



         //take casualty off the board
         if (casualty != null) { 
	    casualty.setCaptured(true); 
	    casualty.orig.piece = null; //take piece off board
	    //casualty.orig = null;  //don't do or hard to undo enpassant
	 }

      // mark enpassant on board
      if (piece.isPawn() && (orig.rank - dest.rank == 2 || orig.rank - dest.rank == -2))
         board.setEnPassantFile(orig.file);

      // check how unique this move is (for short form Algebraic)
      unique = board.isDestUniqueForClass(dest, piece);

         //actually move piece
      dest.piece = piece;
      piece.orig = dest;
      if (!orig.equals(dest)) // Can happen in chess960 castling.
         orig.piece = null;
      piece.moveCount++;

      // Move the rook if we're castling.
      if (castling) {
         Square rookDest = kingForCastling.findRookCastlingDestination(castleQueenside);
         rookDest.piece = rookForCastling;
         boolean rookMoved = !rookDest.equals(rookCastleOrig);
         boolean kingTookRooksPlace = dest.equals(rookCastleOrig);
         if (rookMoved && !kingTookRooksPlace) {
            // Make sure we don't overwrite the king in case of Chess960 where
            // the king ends up on the rook's square.
            rookCastleOrig.piece = null;
         }
         rookForCastling.orig = rookDest;
         rookForCastling.moveCount++;
      }

	 //pawn promotion
	 if (piece.isPawn() && Pawn.isPromotionSquare(dest, piece.isBlack)) {
	    if (promotion == null)  //auto promo
	       promotion  = new Queen();
	    board.promote(piece, promotion);
	 }
	 else
	    promotion = null;  //just in case

         //set board to alternate who moves flag
	 board.isBlackMove =  !piece.isBlack;
	 prev = board.lastMove;
	 board.lastMove = this;
         //incr move counter
	 if (!piece.isBlack)
	    board.moveNumber++;


         executed = true;


	 board.staleLegalDests = true;

         //gen legal moves only if we don't know the next move
         if (!verified || continuation.isTerminal() && !isEndOfGame())
	    board.genLegalDests();

	 verified = true;

         //broadcast changes in the model
         board.fireBoardEvent(BoardEvent.MOVE);

	 if (Log.debug) {
	    Log.debug(DEBUG, "execute successful");
	    Log.debug2(DEBUG, board);
	 }
   }

   private String buildExecuteCastleErrorString(King king) {
      String pieceThatMovedStr = king.moveCount == 0 ? "rook has moved." : "king has moved.";
      return "Trying to castle king side when " + pieceThatMovedStr + ". Rook square: " + rookCastleOrig + ".";
   }

   /* unexecute() *****************************************************/
   /** undo the this move
    */
   protected void unexecute () {
         if (Log.debug) {
	    Log.debug(DEBUG, "unexecuting move: " + this);
	    Log.debug2(DEBUG, board);
	 }

         board.setEnPassantFile(prevEnPassantFile);

	 //pawn promotion (use same function to reverse the promotion)
	 if (piece.isPawn() && Pawn.isPromotionSquare(dest, piece.isBlack)) {
	    board.promote(promotion, piece);
	 }

      dest.piece.moveCount--;
      boolean castling = castleQueenside || castleKingside;
      if (!castling) {
         orig.piece = dest.piece;
         piece.orig = orig;
         dest.piece = null;
      } else 
      {
         King kingPiece = (King) dest.piece;
         Square rook_dest = kingPiece.findRookCastlingDestination(castleQueenside);
         ChessPiece rookPiece = rook_dest.piece;
         orig.piece = kingPiece;
         kingPiece.orig = orig;
         rookCastleOrig.piece = rookPiece;
         rookPiece.orig = rookCastleOrig;

         boolean kingMoved = !dest.equals(orig);
         if (kingMoved && !dest.equals(rookCastleOrig)) {
            // Make the square empty again.
            dest.piece = null;
         }

         rookPiece.moveCount--;

         boolean rookMoved = !rookCastleOrig.equals(rook_dest);
         if (rookMoved && !rook_dest.equals(orig)) {
            rook_dest.piece = null;
         }
      }

         if (casualty != null) {
            casualty.setCaptured(false);
	    casualty.orig.piece = casualty;  //put piece on board
         }

         //50MoveRule
	 if (piece.isPawn()
	    || casualty != null)
	    board.plyCount50 = prevPlyCount50;
	 else
	    board.plyCount50--;

         //set board to alternate who moves flag
	 board.isBlackMove =  piece.isBlack;
	 board.lastMove = (ChessMove) prev; 
         //move number
	 if (!piece.isBlack)
	    board.moveNumber--;

         executed = false;

         board.staleLegalDests = true;

         //broadcast changes in the model
         board.fireBoardEvent(BoardEvent.UNMOVE);

	 if (Log.debug) {
	    Log.debug(DEBUG, "unexecute successful");
	    Log.debug2(DEBUG, board);
	 }
   }

   //Info About Move///////////////////////////////////////////////////////

   //Checking the Move///////////////////////////////////////////////////

   /* isLegal ************************************************************/
   /** this is kinda a strange function since ChessMoves are checked for
    *  legality when they are created.  But if moves are played on the board
    *  this move can still be checked for legality and see if it's still valid.
    */
   public boolean isLegal () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Check.");
      return piece.isLegalDest(dest);
   }

   /* getUniqueness ******************************************************/
   /** is this move's file or rank unique, so SAN short-hand can be
    *  used to represent it?
    *  @return [true][false] if the file is unique
    *  @return [false][true] if the rank is unique
    *  @return [true][true] if both file and rank are unique
    */
   public boolean[] getUniqueness () {
      return unique;
   }

   /* isFileUnique ******************************************************/
   /** is the file unique?
    */
   public boolean isFileUnique () {
      return unique[0];
   }

   /* isRankUnique ******************************************************/
   /** is the rank unique?
    */
   public boolean isRankUnique () {
      return unique[1];
   }

   /* isCheck ************************************************************/
   /** is this move a check (a piece attacking the king) */
   public boolean isCheck () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Check.");
      return check;
   }

   /* isDoubleCheck ******************************************************/
   /** is this move a double check (two pieces attacking the king) */
   public boolean isDoubleCheck () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Double Check.");
      return doublecheck;
   }

   /* isCheckmate *******************************************************/
   /** does this move result in checkmate?
    */
   public boolean isCheckmate () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Checkmate.");
      return checkmate;
   }

   /* isStalemate *******************************************************/
   /** has stalemate resulted in one of the detected ways
    *  (currently only no-legal move stalemate)
    */
   public boolean isStalemate () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Stalemate.");
      return stalemate;
   }

   /* isEndOfGame ******************************************************/
   /** does this move terminate the game, either by checkmate, 
    *  stalemate, or an ChessResult has been set for the move.  
    *  This does <b>not</b> indicate that their is no current move that 
    *  follows this move.
    */
   public boolean isEndOfGame () {
      return (checkmate || stalemate || 
              (result != null && !result.isUndecided()));
   }

   /* getResult ***********************************************************/
   /** returns the result of the game.
    */
   public Result getResult () {
      return result;
   }

   //move attribute sets////////////////////////////////////////////////////
   //to be set from the Board's genLegalMoves function
   protected void setCheck (boolean t) {
      check = t;
   }

   /** sets double check to the specificed boolean value.  if the value is
    *  True, then isCheck() will also be true.
    */
   protected void setDoubleCheck (boolean t) {
      doublecheck = t;
      if (t)
         check = t;
   }

   /* setStalemate ********************************************************/
   /** sets teh checkmate flag and also sets the result field
    *  to a new ChessResult <b>if and only if</b> result was
    *  null.  This is to allow irregularities such as when
    *  the players don't know it's mate and one resigns.
    */
   protected void setCheckmate (boolean t) {
      checkmate = t;
      if (result == null)
         result = new ChessResult((board.isBlackMove()) 
	             ? ChessResult.BLACK_WIN
	             : ChessResult.WHITE_WIN);
   }

   /* setStalemate ********************************************************/
   /** sets teh stalemate flag and also sets the result field
    *  to a new ChessResult <b>if and only if</b> result was
    *  null.  This is to allow irregularities such as when
    *  the players don't know it's stalemate and one resigns.
    */
   protected void setStalemate (boolean t) {
      stalemate = t;
      if (result == null)
         result = new ChessResult(ChessResult.DRAW);
   }

   /* setResult **********************************************************/
   public void setResult (Result res) {
      result = res;

      //need to make sure there are no moves after this one on this line.
      if (res != null && !res.isUndecided())
         continuation.setMainLineTerminal();

      //FIXME: should we walk back up the history and erase Results?
      //FIXME: or erase result when we do an add(Move) to the main line?
   }

   //Execution/////////////////////////////////////////////////////////////
   /* isExecuted *********************************************************/
   /** has this move been executed on the board.
    */
   public boolean isExecuted () {
      return executed;
   }

   /* isVerified *********************************************************/
   /** has this move been verified on the board at some point?
    */
   public boolean isVerified () {
      return verified;
   }

   /* getChessPiece ******************************************************/
   /** returns the piece that is to move 
    */
   public ChessPiece getChessPiece () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine which piece an unverified move will affect.");
      return piece;
   }

   /* getCasualty *******************************************************/
   /** if this move is a capture then the piece captured will be returned.
    */
   public ChessPiece getCasualty () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine which piece an unverified move will affect.");
      return casualty;
   }

   /* getPromotion ******************************************************/
   /** if this move leads to the promotion of a pawn it returns the
    *  promotion piece.
    */
   public ChessPiece getPromotion () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine which piece an unverified move will affect.");
      return promotion;
   }

   /* isBlackMove *******************************************************/
   /** is this a black move?
    */
   public boolean isBlackMove () {
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move will affect black or white.");

      return piece.isBlack();
   }

   /* isCastleQueenside *************************************************/
   /** is this move a queenside castle.
    */
   public boolean isCastleQueenside () { 
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Castle Queenside.");
      return castleQueenside;  
   }

   /* isCastleKingside **************************************************/
   /** is this a move a kingside castle
    */
   public boolean isCastleKingside() { 
      if (!verified) throw new UnverifiedMoveException(
         "Cannot determine if an unverified move is Castle Kingside.");
      return castleKingside; 
   }


   /* getCoordinatesNumeric ********************************************/
   /** returns an array of ints that are the numeric coordinates of
    *  the move.  Coordinates start at 1 and end at 8
    */
   public int[] getCoordinates () {
      int[] coords = null;

      if (castleQueenside || castleKingside) {
         coords = new int[1];
	 coords[0] = (castleQueenside) ? CASTLE_QUEENSIDE : CASTLE_KINGSIDE;
      }
      else {
         if (promotion != null) {
	    coords = new int[5];
	    coords[4] = promotion.getIndex();
	 }
	 else
	    coords = new int[4];
	    
	 coords[0] = orig.file;
	 coords[1] = orig.rank;
	 coords[2] = dest.file;
	 coords[3] = dest.rank;
      }

      return coords;
   }

   /* getDestinationCoordinates ***************************************/
   /** returns an array of the ints that are the numeric coordinates of
    *  the move.  Corrdinates start at 1 and end at 8
    */
   public int[] getDestinationCoordinates () {
      int[] coords = new int[2];
      int[] full   = getCoordinates();

         coords[0] = full[2];
	 coords[1] = full[3];

      return coords;
   }

   /* getDestintion ***************************************************/
   /** returns the Square this the piece intends to move
    */
   public Square getDestination() {
      return dest;
   }

   /* getOrigin *******************************************************/
   /** returns the Square the piece to move originates on.
    */
   public Square getOrigin() {
      return orig;
   }

   /* getOriginCoordinates ********************************************/
   /* returns the integer coordinates of this move in array form.
    */
   public int[] getOriginCoordinates () {
      int[] coords = new int[2];
      int[] full   = getCoordinates();

         coords[0] = full[0];
	 coords[1] = full[1];

      return coords;
   }
   //Misc Obj Stuff//////////////////////////////////////////////////////

   /* toString *********************************************************/
   /*  will either return the unverified coordinates or a formated string
    *  based on the verified move.  This should only be used for debugging.
    *  Output is bias to English SAN notation and is not really compliant 
    *  to any standard..
    *  Instead of this function one of the I/O Classes should be used for
    *  non-debugging output.
    */
   public String toString () {
      String move_seperator   = "-";
      String take_seperator   = "x";
      String check_name       = "+";
      String doublecheck_name = "+";
      String checkmate_name   = "#";

      String s;

      if (castleQueenside) 
	 s = "O-O-O";

      else if (castleKingside)
	 s = "O-O";
	 
      else if (!verified) {
         s = orig.toString() + "-" + dest.toString();
	 if (promotion != null)
	    s += "=" + Character.toUpperCase(
	               ChessBoard.san.pieceToChar(promotion.getIndex()));
      }

      else {
	 s =  ((piece != null && !(piece instanceof Pawn)) 
		   ? (Character.toUpperCase(
	               ChessBoard.san.pieceToChar(piece.getIndex())))
		   + "" 
		   : " ") 
		+ orig.toString()
		+ ((casualty != null) ? take_seperator : move_seperator)
		+ dest.toString();

	 if (promotion != null) {
	    s += "=" + Character.toUpperCase(
	               ChessBoard.san.pieceToChar(promotion.getIndex()));
	 }

	 if (checkmate) s += checkmate_name;
	 else {
	    if (check) s += check_name;
	    if (doublecheck) s += doublecheck_name;
	 }
      }

      String colorToMove = "";
      if (piece != null) {
          if (piece.isBlack) {
        	  colorToMove = "black";
          }
          else {
        	  colorToMove = "white";
          }
          if (!verified) {
        	  colorToMove += " (unverified)";
          }
          colorToMove = " (" + colorToMove + ")";
      }
      return s + colorToMove;
   }

   /* equals ************************************************************/
   /** compares the coordinate data, which is still valid.
    */
   public boolean equals (Object o) {
      if (o == this) return true;
      if ((o == null) || (o.getClass() != this.getClass()))
         return false;

      ChessMove m = (ChessMove) o;

      return (orig.equals(m.orig) && dest.equals(m.dest));
   }

   /* hashCode **********************************************************/
   public int hashCode () {
      int hash = 7;

      hash = 31 * hash + ((orig == null) ? 0 : orig.hashCode());
      hash = 31 * hash + ((dest == null) ? 0 : dest.hashCode());

      return hash;
   }

   public String dump () {
      StringBuffer sb = new StringBuffer();
      int[] coord = getCoordinates();

         sb.append("Move: \n")
	   .append("   coordinates: ");

	 if (coord.length == 1)
	   sb.append(((coord[0] == CASTLE_QUEENSIDE) ? "O-O-O" : "O-O"));
	 else
	   sb.append("" + coord[0] + coord[1] 
	             + coord[2] + coord[3]);

	 sb.append("\n")
	   .append("   verified: " + verified + "\n")
	   .append("   origin: " + orig + "\n")
	   .append("   destination: " + dest + "\n")

	   .append("   isFileUnique: " + unique[0] + "\n")
	   .append("   isRankUnique: " + unique[1] + "\n")

	   .append("   piece: " + piece + "\n");
	 if (piece != null)
	   sb.append("      " + piece.dump());
	 
	   sb.append("   casualty: " + casualty + "\n");

	 if (casualty != null)
	   sb.append(casualty.dump());

	 sb.append("   promotion: " + promotion + "\n")
	   .append("   isCheck: " + check + "\n")
	   .append("   isDoubleCheck: " + doublecheck + "\n")
	   .append("   isCheckmate: " + checkmate + "\n")
	   .append("   isStalemate: " + stalemate + "\n")
	   .append("   result: " + result + "\n")
	   .append("   isEndOfGame: " + isEndOfGame() + "\n")
	   .append("   prenotation: ").append(getPrenotation())
	   .append("\n")
	   .append("   annotation: ").append(getAnnotation())
	   .append("\n")
	   .append("   previous: ").append(getPrev())
	   .append("\n");

	 if (continuation == null || continuation.isTerminal())
	    sb.append("   #continuations: terminal");
	 else
	    sb.append("   ").append(continuation.dump());

      return sb.toString();
   }

   //Move Abstract Methods///////////////////////////////////////////////
   public void setBoard (Board b) {
      board = (ChessBoard) b;
   }

   public Board getBoard () { return board; }
}

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

package ictk.boardgame.chess.io;


import java.util.Locale;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;

import ictk.util.Log;
import ictk.boardgame.Board;
import ictk.boardgame.chess.*;

/* FEN *********************************************************************/
/** FEN is an abstract class that parses strings to
 *  produce Board objects.
 */
public class FEN implements ChessBoardNotation {
     /* for debugging */
   public static long DEBUG = Log.BoardNotation;

     /** for translation from PNBRQK to Pieces */
   protected static SAN san = new SAN();

   Locale locale;

   public FEN () {
   }

   /* setLocale ************************************************************/
   /** NOT IMPLEMENTED YET
    */
   public void setLocale (Locale loc) {
   }

   /* getLocale ************************************************************/
   /** NOT IMPLEMENTED YET
    */
   public Locale getLocale () {
      return locale;
   }

   /* stringToBoard ********************************************************/
   /** converts strings into ChessBoard objects.
    *
    * @throws IOException for the normal reasons
    */
   public Board stringToBoard (String str) 
          throws IOException {
      ChessBoard board = null;
      char[][] matrix = new char[ChessBoard.MAX_FILE][ChessBoard.MAX_RANK];
      char[]   strArray = null;
      int rank = ChessBoard.MAX_RANK-1;
      int file = 0;
      boolean isBlackMove = false;
      boolean canWhiteCastleKingside = false,
              canWhiteCastleQueenside = false,
              canBlackCastleKingside = false,
              canBlackCastleQueenside = false;
      char    enpassantFile = '-';
      int plyCount = 0, moveNumber = 1;
      
      str.trim();
      strArray = str.toCharArray();

      //read board
      int i;
      for (i=0; i < strArray.length; i++) {
         //if rank terminator
	 if (strArray[i] == '/') {
	    rank--;
	    file = 0;
	 }
         //if space indicator
         else if (Character.isDigit(strArray[i])) {
	    file += Character.digit(strArray[i], 10);
	 }
	 else if (isPieceChar(strArray[i])) {
	    matrix[file++][rank] = strArray[i];
	 }
	 else if (strArray[i] == ' ') {
	    break;
         }
	 else {
	    throw new IOException("Unsupported character found in FEN at:"
	       + i);
	 }
      }

      i++; //space

      //who's move it is
      if (strArray[i] == 'w')
         isBlackMove = false;
      else if (strArray[i] == 'b')
         isBlackMove = true;
      else
	 throw new IOException("Unsupported character found in FEN at:"
	    + i + "(" + strArray[i] + ") expecting who to move");
      i++; //pass who's move

      i++; //space

      //castling block KQkq or -
      for (;i<strArray.length && strArray[i] != ' ';i++) {
         switch (strArray[i]) {
	    case 'K': canWhiteCastleKingside = true; break;
	    case 'Q': canWhiteCastleQueenside = true; break;
	    case 'k': canBlackCastleKingside = true; break;
	    case 'q': canBlackCastleQueenside = true; break;
	 }
      }

      i++; //space

      //enpassant square
      if (strArray[i] != '-') {
         enpassantFile = strArray[i];
	 i++; //this is the rank, which isn't necessary
      }
      i++; //pass enpassant block

      i++; //space

      //ply count
      int j;
      String strPly = str.substring(i, j = str.indexOf(" ", i));
      try {
         plyCount = Integer.parseInt(strPly);
      }
      catch (NumberFormatException e) {
	 throw new IOException("Unsupported character found in FEN at:"
	    + i + " (" + strPly + ") expecting ply count");
      }
      i = j;


      i++; //space

      //full move number
      String strMoves = str.substring(i, str.length());
      try {
         moveNumber = Integer.parseInt(strMoves);
      }
      catch (NumberFormatException e) {
	 throw new IOException("Unsupported character found in FEN at:"
	    + i + " (" + strMoves + ") expecting move number");
      }

      board = new ChessBoard(matrix, 
                        isBlackMove,
                        canWhiteCastleKingside, 
                        canWhiteCastleQueenside,
			canBlackCastleKingside,
			canBlackCastleQueenside,
			enpassantFile,
			plyCount,
			moveNumber);

      return board; 
   }


   /* boardToString() *****************************************************/
   /** converts board objects into string format
    */
   public String boardToString (Board b) {
      ChessBoard board = (ChessBoard) b;
      char[][] ray = board.toCharArray();
      StringBuffer buff = new StringBuffer();
      

      //board
      int count = 0;
      for (int r=ChessBoard.MAX_RANK-1; r >= 0; r--) {
         if (r != ChessBoard.MAX_RANK-1) buff.append("/");
	 
	 count = 0;
         for (int f=0; f < ChessBoard.MAX_FILE; f++) {
	    if (Character.isLetter(ray[f][r])) {
	       if (count > 0) {
	          buff.append(count);
		  count = 0;
	       }
	       buff.append(ray[f][r]);
	    }
	    else count++;
	 }
	 if (count > 0)
	    buff.append(count);
      }

      buff.append(" "); //space

      //who's move
      buff.append( ((board.isBlackMove()) ? 'b' : 'w'));

      buff.append(" "); //space

      //castling block
      boolean castle = false;
      if (board.isWhiteCastleableKingside()) {
         castle = true;
	 buff.append("K");
      }
      if (board.isWhiteCastleableQueenside()) {
         castle = true;
	 buff.append("Q");
      }
      if (board.isBlackCastleableKingside()) {
         castle = true;
	 buff.append("k");
      }
      if (board.isBlackCastleableQueenside()) {
         castle = true;
	 buff.append("q");
      }
      if (!castle)
         buff.append("-");

      buff.append(" "); //space

      //enpassant file
      if (board.getEnPassantFile() != ChessBoard.NO_ENPASSANT) {
         buff.append(san.fileToChar(board.getEnPassantFile()));
	 if (board.isBlackMove())
	    buff.append("3"); //the rank (silly I know -- but standard)
	 else
	    buff.append("6");
      }
      else
         buff.append("-");
      
      buff.append(" "); //space

      //ply clock
      buff.append(board.get50MoveRulePlyCount()); 

      buff.append(" "); //space

      //move number
      buff.append(board.getCurrentMoveNumber());

      return buff.toString();
   }

   //Utilities///////////////////////////////////////////////////////////

   /* isPieceChar *******************************************************/
   /** FEN is not Locale specifc yet.
     * @return true if the character entered is a legal piece in FEN
    */
   protected boolean isPieceChar (char c) {
      switch (c) {
         //black
         case 'r':
	 case 'n':
	 case 'b':
	 case 'q':
	 case 'k':
	 case 'p':
	 //white
         case 'R':
	 case 'N':
	 case 'B':
	 case 'Q':
	 case 'K':
	 case 'P':
	    return true;

	 default:
	    return false;
      }
   }
}

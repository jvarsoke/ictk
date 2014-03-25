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

import ictk.util.Log;
import ictk.boardgame.Move;
import ictk.boardgame.Board;
import ictk.boardgame.io.MoveNotation;
import ictk.boardgame.chess.*;
import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.Result;

/* ChessMoveNotation *********************************************************/
/** the parent class of all ChessNotations.  It contains many methods
 *  for piece and coordinate (file/rank) notations in many language
 *  to be translated into numberic notation (1-8).  
 *  There are a few pre-set Locales.
 */
public abstract class ChessMoveNotation implements MoveNotation {
      /** Log.debug() mask */
   public static final long DEBUG = Log.MoveNotation;

      /** contains piece sets in differnt languages PNBRQK */
   public static final char[][] PIECE_SETS  = {
      {'P','N','B','R','Q','K'},  //0  English
      {'P','J','S','V','D','K'},  //1  Czech
      {'B','S','L','T','D','K'},  //2  Danish
      {'P','R','O','V','L','K'},  //3  Estonian
      {'P','R','L','T','D','K'},  //4  Finnish
      {'P','C','F','T','D','R'},  //5  French
      {'B','S','L','T','D','K'},  //6  German
      {'G','H','F','B','V','K'},  //7  Hungarian
      {'P','R','B','H','D','K'},  //8  Icelandic
      {'P','C','A','T','D','R'},  //9  Italian
      {'B','S','L','T','D','K'},  //10 Norwegian
      {'P','S','G','W','H','K'},  //11 Polish
      {'P','C','B','T','D','R'},  //12 Portuguese
      {'P','C','N','T','D','R'},  //13 Romanian
      {'P','C','A','T','D','R'},  //14 Spanish
      {'B','S','L','T','D','K'},  //15 Swedish
      {'P','S','L','T','D','K'},  //16 Croatian
      };

      /** contains file sets for different languages a-h */
   public static final char[][] FILE_SETS = {
      {'a','b','c','d','e','f','g','h'},  //roman
      };

      /** for the unlikely case your using non arabic numbers */
   public static final char[][] RANK_SETS = {
      {'1','2','3','4','5','6','7','8'},  //arabic
      };

   //instance///////////////////////////////////////////////////////////////
      /** defaults to English */
   protected char[] pieceSet = PIECE_SETS[0],
      /** defaults to Roman */
                    fileSet  = FILE_SETS[0],
      /** defaults to Arabic */
		    rankSet  = RANK_SETS[0];

      /** default Locale assumes English Language characters */
   protected Locale locale;

   public ChessMoveNotation () {
      this(null);
   }

   /** note: different Locales other than the default (English) should
    *  only be used for local presentation.  The archive standard for SAN
    *  and PGN is English.
    */
   public ChessMoveNotation (Locale loc) {
      locale = loc;
      setLocale(locale);
   }

   /* setLocale () *********************************************************/
   /** sets the Locale for the ChessMoveNotation to use localised piece 
    *  representations.
    *  Note: this should be used for local presentation only.  The archive
    *  standard for SAN and PGN is English (the default).
    *  This function uses the  ISO 639-2/T language code.
    *  (ftp://dkuug.dk/i18n/iso-639-2.txt)
    *
    *  @return false if no specific pieceset is registered for that locale
    */
   public boolean setLocale (Locale loc) {
      String lang = null;
      if (loc == null 
          || "eng".equals(lang = loc.getISO3Language()))      //english
         pieceSet = PIECE_SETS[0];
      else if ("gem".equals(lang)) //german
         pieceSet = PIECE_SETS[6];
      else if ("fra".equals(lang)) //french
         pieceSet = PIECE_SETS[5];
      else if ("ces".equals(lang))
         pieceSet = PIECE_SETS[1]; //czech
      else if ("dan".equals(lang))
         pieceSet = PIECE_SETS[2]; //danish
      else if ("est".equals(lang))
         pieceSet = PIECE_SETS[3]; //danish
      else if ("fin".equals(lang))
         pieceSet = PIECE_SETS[4]; //finnish
      else if ("hun".equals(lang))
         pieceSet = PIECE_SETS[7]; //hungarian
      else if ("isl".equals(lang))
         pieceSet = PIECE_SETS[8]; //icelandic
      else if ("ita".equals(lang))
         pieceSet = PIECE_SETS[9]; //italian
      else if ("nor".equals(lang))
         pieceSet = PIECE_SETS[10]; //norwegian
      else if ("nor".equals(lang))
         pieceSet = PIECE_SETS[10]; //norwegian
      else if ("pol".equals(lang))
         pieceSet = PIECE_SETS[11]; //polish
      else if ("por".equals(lang))
         pieceSet = PIECE_SETS[12]; //portuguese
      else if ("ron".equals(lang))
         pieceSet = PIECE_SETS[13]; //romanian
      else if ("spa".equals(lang))
         pieceSet = PIECE_SETS[14]; //spanish
      else if ("swe".equals(lang))
         pieceSet = PIECE_SETS[15]; //swedish
      else if ("swe".equals(lang))
         pieceSet = PIECE_SETS[15]; //swedish
      else if ("hrv".equals(lang))
         pieceSet = PIECE_SETS[16]; //croatian
      else
         return false;

      return true;
   }

   /* getLocale ************************************************************/
   /** returns the Locale being used with this SAN object. 
    *
    *  @return null if the default is being used
    */
   public Locale getLocale () { return locale; }

   /* getPieceSet **********************************************************/
   /** gets the piece set used in the current Locale setting
    */
   public char[] getPieceSet () { return pieceSet; }

   /* setPieceSet **********************************************************/
   /** sets a custom piece set.  This will hopefully be useful for 
    *  using fonts that do not directly corrilate to one of the 
    *  standard piece sets.  Or if your Locale is not yet supported.
    *  This only takes 6 pieces in the order of P,N,B,R,Q,K.
    */
   public void setPieceSet (char[] set) { 
      if (set.length != 6)
         throw new IllegalArgumentException(
	    "The set must contain 6 characters in the order of [PNBRQK].");
      pieceSet = set; 
   }

   //piece translations//////////////////////////////////////////////////////

   /* pieceToNum ***********************************************************/
   /** returns the number Piece.getIndex() from the character
    *  representation of the piece.
    */
   public byte pieceToNum (String s) {
      return pieceToNum(s.charAt(0));
   }

   /* pieceToNum ***********************************************************/
   /** returns the number Piece.getIndex() from the character
    *  representation of the piece.
    */
   public byte pieceToNum (char c) {
      byte p = ChessPiece.NULL_PIECE;
      
      c = Character.toUpperCase(c);

      //can't use map here because don't know if ChessPiece.INDEX is in order
      if (c == pieceSet[0] || c == ' ')
         p = Pawn.INDEX;
      else if (c == pieceSet[1])
         p = Knight.INDEX;
      else if (c == pieceSet[2])
         p = Bishop.INDEX;
      else if (c == pieceSet[3])
         p = Rook.INDEX;
      else if (c == pieceSet[4])
         p = Queen.INDEX;
      else if (c == pieceSet[5])
         p = King.INDEX;

      if (p == ChessPiece.NULL_PIECE) {
	    if (Log.debug)
	       Log.debug(DEBUG, "unknown piece: <" + c + ">");
	    throw new ArrayIndexOutOfBoundsException("Unknown ChessPiece");
      }

      return p;
   }


   /* pieceToChar **********************************************************/
   /** returns the character representation of the piece
    *  in the correct Locale.
    */
   public char pieceToChar (int p) {
      char c = ' ';
      switch (p % ChessPiece.BLACK_OFFSET) {
         case Pawn.INDEX:   c = pieceSet[0]; break;
         case Knight.INDEX: c = pieceSet[1]; break;
         case Bishop.INDEX: c = pieceSet[2]; break;
         case Rook.INDEX:   c = pieceSet[3]; break;
         case Queen.INDEX:  c = pieceSet[4]; break;
         case King.INDEX:   c = pieceSet[5]; break;
	 default: 
	    if (Log.debug)
	       Log.debug(DEBUG, "unknown piece index: <" + p + ">");
	    throw new ArrayIndexOutOfBoundsException("Unknown ChessPiece");
      }

      return c;
   }

   /* pieceToChar **********************************************************/
   /** returns the character representation of the piece
    *  in the correct Locale.
    */
   public char pieceToChar (ChessPiece p) {
      return pieceToChar(p.getIndex());
   }

   /* fileToNum ************************************************************/
   /** returns the byte representation of the file. In English/Roman
    *  letters this would be a=1, b=2 etc
    */
   public byte fileToNum (String s) {
      return fileToNum(s.charAt(0));
   }

   /* fileToNum ************************************************************/
   /** returns the byte representation of the file. In English/Roman
    *  letters this would be a=1, b=2 etc.  If '-' or ' ' is sent 
    *  ChessBoard.NULL_FILE is returned.
    */
   public byte fileToNum (char c) {
      byte p = ChessBoard.NULL_FILE;

      p = mapSetToNum(Character.toLowerCase(c), fileSet);

      if (p == ChessBoard.NULL_FILE) {
	 if (c == '-' || c == ' ') return p;

	    if (Log.debug)
	       Log.debug(DEBUG, "unknown file: <" + c + ">");
	    throw new ArrayIndexOutOfBoundsException(
	        "file out of range: " + c);
      }
      return p;
   }

   /* rankToNum ************************************************************/
   /** returns the byte representation of the rank. In English/Arabic
    *  letters this would be '1'=1, '2'=2 etc.  if '-' or ' '  is sent
    *  ChessBoard.NULL_FILE is returned.
    */
   public byte rankToNum (char c) {
      byte p = ChessBoard.NULL_RANK;

      p = mapSetToNum(Character.toLowerCase(c), rankSet);

      if (p == ChessBoard.NULL_RANK) {
	 if (c == '-' || c == ' ') return p;

	    if (Log.debug)
	       Log.debug(DEBUG, "unknown file: <" + c + ">");
	    throw new ArrayIndexOutOfBoundsException(
	       "rank out of range: " + c);
      }

      return p;
   }

   /* fileToChar ***********************************************************/
   /** returns the character representation of the file. In English/Roman
    *  letters this would be 1='a', 2='b' etc.
    */
   public char fileToChar (int i) {
      char c = ' ';

      if (i > 0 && i <= fileSet.length)
         c = fileSet[i-1];
      else {
	    if (Log.debug)
	       Log.debug(DEBUG, "file out of range: <" + i + ">");
	    throw new ArrayIndexOutOfBoundsException ("file out of range (" 
	       + i + ")");
      }
      return c;
   }

   /* rankToChar ***********************************************************/
   /** returns the character representation of the rank. In English/Arabic
    *  letters this would be 1='1', 2='2' etc. 
    */
   public char rankToChar (int i) {
      char c = ' ';

      if (i > 0 && i <= rankSet.length)
         c = rankSet[i-1];
      else {
	    if (Log.debug)
	       Log.debug(DEBUG, "rank out of range: <" + i + ">");
	    throw new ArrayIndexOutOfBoundsException ("rank out of range");
      }
      return c;
   }

   /* mapSetToNum **********************************************************/
   /** maps a character to a index for internationalization use
    */
   protected byte mapSetToNum (char c, char[] set) {
      byte f = ChessBoard.NULL_FILE;
      for (byte i=0; f == ChessBoard.NULL_FILE && i < set.length; i++)
         if (c == set[i])
	    f = (byte) (i+1);
      return f;
   }
}

/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
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

package ictk.boardgame.chess.io;


import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.*;
import java.util.Stack;

import ictk.util.Log;
import ictk.boardgame.Game;
import ictk.boardgame.History;
import ictk.boardgame.Board;
import ictk.boardgame.GameInfo;
import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.OutOfTurnException;
import ictk.boardgame.io.InvalidGameFormatException;

import ictk.boardgame.chess.*;

/* PGNReader *****************************************************************/
/** PGNReader reads PGN formated files
 */
public class PGNReader extends ChessReader {
      /** mask for Log.debug() */
   public static final long DEBUG = Log.GameReader;

      /** for reading boards in the FEN tag */
   protected static FEN fen = new FEN();

     /**game information pattern*/
   protected static final Pattern giPattern;

     /**for reading SAN. Might one day be Localized */
   protected ChessMoveNotation notation = new SAN();

     /** used for error recovery */
   protected ChessGame game;
     /** used for error recovery */
   protected ChessGameInfo gameInfo;
   protected ChessBoard board;


   //static constructor////////////////////////////////////////////////////////
   static {
      giPattern = Pattern.compile("\\[\\s*(\\w+)\\s+\"(.*)\"\\s*\\]");
   }


   //constructors//////////////////////////////////////////////////////////////
   public PGNReader (Reader _ir) {
      super(_ir);
   }

   /* readGame ***************************************************************/
   public Game readGame () 
          throws InvalidGameFormatException,
	         IllegalMoveException,
		 AmbiguousMoveException,
                 IOException {
      History    history  = null;

         gameInfo = (ChessGameInfo) readGameInfo();
	 board    = (ChessBoard) readBoard();
	 if (board == null) 
	    board = new ChessBoard();
         game     = new ChessGame(gameInfo, board); 

	 history = readHistory();

	 if (gameInfo == null && history == null) return null;

	 return game;
   }

   /* getGame ***************************************************************/
   /** gets the last game read.  This can be used if an exception was
    *  thrown during the reading, and you still want the game.
    */
   public Game getGame () {
      return game;
   }

   /* readGameInfo () *******************************************************/
   /** reads the game info header portion of the PGN file
    */ 
   public GameInfo readGameInfo () 
          throws IOException {

      String        line    = null;
      Matcher       result  = null;
      ChessGameInfo gameInfo = new ChessGameInfo();
      boolean       headerFound = false,
                    headerDone = false;
      String        key = null, value = null;

         while (!headerDone && (line = readLine()) != null) {
	    if (line.startsWith("%")) continue;  //PGN line comment
	    if (headerFound && line.equals("")) {
	       headerDone = true;
	    }
	    else {
	       result = giPattern.matcher(line);
	       if (result.find()) {
	          headerFound = true;

		  if (Log.debug) 
		     Log.debug(DEBUG, "GameInfo header", result);

		  key = result.group(1);
		  value = result.group(2);
		  _setGameInfo(gameInfo, key, value);
	       }
	    }
	 }

       if (!headerFound) return null;
       else              return gameInfo;
   }

   /* readHistory ***********************************************************/
   /** reads the history list which must be in SAN format.
    */
   public History readHistory () 
           throws InvalidGameFormatException,
                  IllegalMoveException,
		  AmbiguousMoveException,
	          IOException {
      History     history = game.getHistory();
      boolean     done    = false;
      String      line    = null;
      StringTokenizer st  = null;
      String      tok     = null;
      ChessMove   move    = null;
      ChessMove   lastMove = null;
      int         count   = 0;  //move count
      int i = 0;  //used for temp
      ChessResult res     = null;
      Stack       forks   = new Stack(); //fork for variations to return to
      ChessAnnotation anno = null;
      

//NEED: should read all move data into one string (eliminate \n) until \n\n
//NEED: ^ problem with ; comment
         //should detect '\n' and stop there. so can throw on bad move format

      StringBuffer sb = new StringBuffer();

      while (!done && (line = readLine()) != null) {
         if (Log.debug) 
	    Log.debug(DEBUG, "line in: " + line);

         if (line.startsWith("%")) continue;  //pgn ESC comment line
	 if (line.equals("")) done = true;
	 sb.append(line).append("\n");  //reproducing the original line
      }

      //now we have whole move list in the sb
      st = new StringTokenizer(sb.toString(), " .\n", true);

      String tok2 = null;
      while (st != null && st.hasMoreTokens()) {
         if (Log.debug)
	    Log.debug(DEBUG, "token: " + tok);

         tok = st.nextToken();

         if (tok.charAt(0) == ' '
	     || tok.charAt(0) == '.' 
	     || tok.startsWith("\n")) continue;  //token delim

	 //eol annotaiton
	 else if (tok.startsWith(";")) {   //comment until eol
	    sb = new StringBuffer();
	    done = false;
	    while (!done && st.hasMoreTokens()) {
	       tok2 = st.nextToken();
	       if (tok2.startsWith("\n")) done = true;
	       sb.append(tok2);
	    }
	    if (lastMove != null)
	       anno = (ChessAnnotation) lastMove.getAnnotation();
	       if (anno == null)
	          anno = new ChessAnnotation();
	       anno.appendComment(sb.toString());
	       lastMove.setAnnotation(anno);
	 }

	 //internal {anno} Annotation
	 else if (tok.startsWith("{")) {
	    sb = new StringBuffer();
	    done = false;
	    if (tok.length() > 1)
	       sb.append(tok.substring(1, tok.length()));

	    while (!done && st.hasMoreTokens()) {
	       tok2 = st.nextToken();
	       if (tok2.endsWith("}")) {
	          if (tok2.length() > 1) 
		     sb.append(tok2.substring(0, tok2.length()-1));
	          done = true;
	       }
	       else
	          sb.append(tok2);
	    }
	    if (lastMove != null)
	       anno = (ChessAnnotation) lastMove.getAnnotation();
	       if (anno == null)
	          anno = new ChessAnnotation();
	       anno.appendComment(sb.toString());
	       lastMove.setAnnotation(anno);
	 }

         //move number or Result
	 else if (Character.isDigit(tok.charAt(0))) {
	    if ((res = (ChessResult) notation.stringToResult(tok)) != null) {
	       done = true;
	       if (lastMove != null)
		  lastMove.setResult(res);
	    }
	 }

	 //actual move
	 else if (Character.isLetter(tok.charAt(0))) {
	    try {
	       move = (ChessMove) notation.stringToMove(board, tok);
	       if (move != null) {
		  history.add(move);
		  lastMove = move;
		  count++; //FIXME: not sure what this is for
	       }
	       else {
	          if (Log.debug)
		     Log.debug(DEBUG, "Thought this was a move: " + tok);
		  throw new IOException("Thought this was a move: " + tok);
	       }
	    }
	    catch (OutOfTurnException e) {
	       if (Log.debug) {
	          Log.debug(DEBUG, e);
		  Log.debug2(DEBUG,"From Token: " + tok);
		  Log.debug2(DEBUG,"Board: \n" + board);
	       }
	       done = true;
	       throw e;
	    }
	    catch (AmbiguousMoveException e) {
	       if (Log.debug) {
	          Log.debug(DEBUG, e);
		  Log.debug2(DEBUG,"From Token: " + tok);
		  Log.debug2(DEBUG,"Board: \n" + board);
	       }
	       done = true;
	       throw e;
	    }
	    catch (IllegalMoveException e) {
	       if (Log.debug) {
	          Log.debug(DEBUG, e);
		  Log.debug2(DEBUG,"From Token: " + tok);
		  Log.debug2(DEBUG,"Board: \n" + board);
	       }
	       done = true;
	       //history.rewind();
		  throw e;
	    } 
	 }

         //NAG
	 else if (tok.charAt(0) == '$') {
	    try {
	       if (lastMove != null)
		  anno = (ChessAnnotation) lastMove.getAnnotation();
		  if (anno == null)
		     anno = new ChessAnnotation();
		  anno.addNAG(Short.parseShort(tok.substring(1, tok.length())));
		  lastMove.setAnnotation(anno);
	    }
	    catch (NumberFormatException e) {
	       //just move along
	    }
	 }

         //start of a variation
	 else if (tok.charAt(0) == '(') {
	    //go back one move so the next history.add() will add
	    //a variation
	    history.prev();  
	    forks.push(history.getCurrentMove());
	 }

         //end of a variation
	 else if (tok.charAt(0) == ')') {
	    ChessMove fork = (ChessMove) forks.pop();
	    history.goTo(fork);
	    history.next();
	 }

         //undecided result of game
	 else if (tok.charAt(0) == '*') {
	    if (lastMove != null)
	       lastMove.setResult(new ChessResult(ChessResult.UNDECIDED));
	 }

	 //unknown
	 else {
	    //no idea what this is
	    if (Log.debug) 
	       Log.debug(DEBUG, "No idea what this is: <" + tok + ">");
	 }
      }
      history.rewind();
      if (count == 0) return null;
      else return history;
   }

   /* readBoard *********************************************************/
   /** looks for a readable board notation (FEN) in the GameInfo header
    *  @return null if there is no particular position associated with
    *                this board.
    */
   public Board readBoard () 
           throws IOException {
      Board board = null;
      String fenStr = null;

      if (gameInfo == null) return null;
      fenStr = gameInfo.get("FEN");

      if (fenStr == null) return null;
      else return fen.stringToBoard(fenStr);
   }

   /* _setGameInfo *******************************************************/
   protected void _setGameInfo (ChessGameInfo gi, String key, String value) {
      StringTokenizer st = null;
      String tok = null;
      ChessPlayer p = null;

      //event
      if ("Event".equalsIgnoreCase(key)) {
         if (!value.equals("?"))
            gi.setEvent(value);
      }

      //site
      else if ("Site".equalsIgnoreCase(key)) {
         if (!value.equals("?"))
	    gi.setSite(value);
      }

      //round
      else if ("Round".equalsIgnoreCase(key)) {
         if (!value.equals("-") && !value.equals("?"))
	    gi.setRound(value);
      }

      //subround
      else if ("SubRound".equalsIgnoreCase(key)) {
         if (!value.equals("-") && !value.equals("?"))
	    gi.setSubRound(value);
      }

      //white player
      else if ("White".equalsIgnoreCase(key)) {
         if (!value.equals("")) {
	    p = new ChessPlayer(value);
	    gi.setWhite(p);
	 }
      }

      //black player
      else if ("Black".equalsIgnoreCase(key)) {
         if (!value.equals("")) {
	    p = new ChessPlayer(value);
	    gi.setBlack(p);
	 }
      }

      //result
      else if ("Result".equalsIgnoreCase(key)) {
         gi.setResult((ChessResult) notation.stringToResult(value));
      }

      //whiteRating
      else if ("WhiteElo".equalsIgnoreCase(key)) {
         try {
	    gi.setWhiteRating(Integer.parseInt(value));
	 }
	 catch (NumberFormatException e) {
	    //move along
	 }
      }

      //blackElo
      else if ("BlackElo".equalsIgnoreCase(key)) {
         try {
	    gi.setBlackRating(Integer.parseInt(value));
	 }
	 catch (NumberFormatException e) {
	    //move along
	 }
      }

      //ECO
      else if ("ECO".equalsIgnoreCase(key)) {
	 gi.setECO(value);
      }

      else if ("TimeControl".equalsIgnoreCase(key)) {
         st = new StringTokenizer(value, "+", false);
	 try {
	    if (st.hasMoreTokens()) {
	       gi.setTimeControlInitial(Integer.parseInt(st.nextToken()));

	       if (st.hasMoreTokens())
		  gi.setTimeControlIncrement(Integer.parseInt(st.nextToken()));
	    }
	 }
	 catch (NumberFormatException e) {
	    //move along
	 }
      }

      //date
      else if ("Date".equalsIgnoreCase(key)) {
         if (!value.equals("????.??.??")) {
	    Calendar date = new GregorianCalendar();
	    st = new StringTokenizer(value,"./", false);
	    tok = null;
	    
	    try {
	       if (st.hasMoreTokens()) {
	          tok = st.nextToken();
		  if (!tok.startsWith("?")) {
		     date.set(Calendar.YEAR, Integer.parseInt(tok));
		     gi.setYear(Integer.parseInt(tok));
		  }

		  if (st.hasMoreTokens()) {
		     tok = st.nextToken();
		     if (!tok.startsWith("?")) {
			date.set(Calendar.MONTH, Integer.parseInt(tok) -1);
			gi.setMonth(Integer.parseInt(tok));
		     }

		     if (st.hasMoreTokens()) {
			tok = st.nextToken();
			if (!tok.startsWith("?")) {
			   date.set(Calendar.DAY_OF_MONTH, 
			      Integer.parseInt(tok));
			   gi.setDay(Integer.parseInt(tok));
			}
		     }
		  }
	       }
	    }
	    catch (NumberFormatException e) {
	       //nothing to do, just move along
	    }
	    gi.setDate(date);                                                        
	 }
      }
      //don't recognize this data, but want to keep it
      else {
         gi.add(key, value);
      }
   }
}


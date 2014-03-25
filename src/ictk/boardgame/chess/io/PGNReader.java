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


import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.*;
import java.io.StreamTokenizer;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.GregorianCalendar;
import java.util.regex.*;
import java.util.Stack;

import ictk.util.Log;
import ictk.boardgame.Game;
import ictk.boardgame.History;
import ictk.boardgame.Board;
import ictk.boardgame.GameInfo;
import ictk.boardgame.Move;
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
      History     history  = game.getHistory();
      boolean     finished = false,
                  done     = false;
      String      line     = null;
      String      tok      = null,
                  tok2     = null;
      ChessMove   move     = null;
      ChessMove   lastMove = null;
      int         count    = 0;  //move count
      int i = 0;  //used for temp
      ChessResult res      = null;
      Stack<Move> forks    = new Stack<>(); //fork for variations to return to
      ChessAnnotation anno = null;
                  /* this is for when we have two or more annotations
	           * in a row.  We either need to apply it to the 
		   * lastMove (if there are >2 in all) or 
		   * the next move (if there are exactly 2).
		   * This is only true for { } annotations. */
      String       savedComment = null;
      short        nag     = 0;
      StreamTokenizer st   = new StreamTokenizer(this);
      StringBuffer sb      = new StringBuffer();  //for storing comments

      if (Log.debug)
         Log.debug(DEBUG, "reading History");

      //number parsing is on by default.  turn it off
      st.ordinaryChars(33, 255);
      st.wordChars(33,255);

      //delimeters
      st.ordinaryChar('.');  
      st.ordinaryChar('(');  //RAV begin
      st.ordinaryChar(')');  //RAV end
      st.ordinaryChar('{');  //comment
      st.ordinaryChar('}');  //comment end
      st.ordinaryChar(';');  //comment to end of line
      st.ordinaryChar('*');  //undefined result

      st.ordinaryChar('\t'); //eat these
      st.eolIsSignificant(true); //just to eat them


      //loop until we hit a result or see the EOF
      while (!finished && st.nextToken() != StreamTokenizer.TT_EOF) {
         tok = st.sval;

         if (Log.debug)
	    Log.debug(DEBUG, "token: " + tok);

         //delimeter token
	 if (tok == null) {
	    switch (st.ttype) {
	       case StreamTokenizer.TT_EOL:
	       case '.':
	          continue;
		  
	       //comment until eol
	       case ';':
		  sb = new StringBuffer();

                  st.ordinaryChar(' ');
		  while (st.nextToken() != StreamTokenizer.TT_EOL) {
		     tok2 = st.sval;
		     if (st.ttype != '\t')
		        if (tok2 != null)
			   sb.append(tok2);
			else
			   sb.append((char) st.ttype);
		  }
		  st.whitespaceChars(' ', ' ');

		  if (Log.debug)
		     Log.debug(DEBUG, "eol comment: {" + sb.toString() + "}");

		  //set as annotation of last move
		  //if lastMove != history.getCurrentMove() then we just started
		  //a variation, and the comment needs to be a prenotation of
		  //the next move we run across
		  if (lastMove != null 
		     && lastMove == history.getCurrentMove()) {
		     anno = (ChessAnnotation) lastMove.getAnnotation();

		     if (anno == null || anno.getComment() == null) {
			if (anno == null)
			   anno = new ChessAnnotation();
			anno.setComment(sb.toString());
			lastMove.setAnnotation(anno);
			if (Log.debug)
			   Log.debug(DEBUG, 
			      "eol comment for (" +  lastMove + "): "
			      + lastMove.getAnnotation().getComment());
		     }
		     else
			anno.appendComment(" " + sb.toString());

		     anno = null;
		  }

		  //keep for prenotation of next move
		  else {
		     savedComment = sb.toString();
		  }
		  break;

               //internal {anno} Annotation
	       case '{':
		  sb = new StringBuffer();
		  done = false;

                  st.ordinaryChar(' ');
		  while (!done && st.nextToken() != StreamTokenizer.TT_EOF) {
		     tok2 = st.sval;
		     switch (st.ttype) {
			case '}': 
			   done = true; 
			   break;
			case StreamTokenizer.TT_EOL:
			case '\t':
			   break;
			default:
			   if (tok2 != null)
			      sb.append(tok2);
			   else
			      sb.append((char) st.ttype);
		     }
		  }
		  st.whitespaceChars(' ', ' ');

		  if (Log.debug)
		     Log.debug(DEBUG, "comment: {" + sb.toString() + "}");

		  //if lastMove != history.getCurrentMove() then we just started
		  //a variation, and the comment needs to be a prenotation of
		  //the next move we run across
		  if (lastMove != null 
		     && lastMove == history.getCurrentMove()) {
		     anno = (ChessAnnotation) lastMove.getAnnotation();

		     //if this is the first annotation we've seen after the move
		     //then it certainly belongs to the lastMove.
		     if (anno == null || anno.getComment() == null) {
			if (anno == null) 
			   anno = new ChessAnnotation();
			anno.setComment(sb.toString());
			lastMove.setAnnotation(anno);
			anno = null;
		     }

		     //If the lastMove already has an annotation then
		     //this comment might belong to the next move.
		     //If there is more than one comment between
		     //moves. N-1 are appended to the lastMove.
		     else {
			if (savedComment != null)
			   lastMove.getAnnotation().appendComment(" "
			      + savedComment);
			savedComment = sb.toString();
		     }
		  }
		  //if there's no lastMove this must be the game comment.
		  //It should be attached as a preNotation to the first move.
		  else {
		     savedComment = sb.toString();
		  }
		  break;

	       //RAV: start of a variation
	       case '(':
		  //go back one move so the next history.add() will add
		  //a variation
		  history.prev();  
		  forks.push(history.getCurrentMove());

		  if (Log.debug)
		     Log.debug(DEBUG, "starting variation from " 
			+ history.getCurrentMove());

		  //if we still have a comment that has no home it must go
		  //with the last move outside the variation, not the 
		  //prenotation of the first move in the variation.
		  if (savedComment != null) {
		     anno = (ChessAnnotation) lastMove.getAnnotation();
		     if (anno == null || anno.getComment() == null) {
			if (anno == null) 
			   anno = new ChessAnnotation();
			anno.setComment(savedComment);
		     }
		     else
			anno.appendComment(" " + savedComment);
		     savedComment = null;
		  }
		  break;

	       //RAV: end of a variation
	       case ')':
		  ChessMove fork = (ChessMove) forks.pop();
		  history.goTo(fork);

		  if (Log.debug)
		     Log.debug(DEBUG, "ending variation from " 
			+ fork);

		  history.next();

		  //if we still have a comment that has no home it must go
		  //with the last move in the variation and not the
		  //prenotation of the first move outside the variation
		  if (savedComment != null) {
		     anno = (ChessAnnotation) lastMove.getAnnotation();
		     if (anno == null || anno.getComment() == null) {
			if (anno == null) 
			   anno = new ChessAnnotation();
			anno.setComment(savedComment);
		     }
		     else
			anno.appendComment(" " + savedComment);
		     savedComment = null;
		  }

		  //make sure we set lastMove to the mainline again
		  lastMove = (ChessMove) history.getCurrentMove();
		  break;

	       //undecided result of game
	       case '*':
		  if (Log.debug)
		     Log.debug(DEBUG, "Result token: " + tok);
		  if (lastMove != null)
		     lastMove.setResult(new ChessResult(ChessResult.UNDECIDED));
		  finished = true;
		  break;

	    } //end switch

	 } //end null tok

         //NAG - numeric of symbol
	 else if ((nag = NAG.stringToNumber(tok)) != 0) {
	    if (Log.debug)
	       Log.debug(DEBUG, "NAG symbol(nag): " + tok);

	    if (lastMove != null) {
	       anno = (ChessAnnotation) lastMove.getAnnotation();
	       if (anno == null)
		  anno = new ChessAnnotation();
	       anno.addNAG(nag);
	       lastMove.setAnnotation(anno);
	    }
	    //else skip this since it's not really legal.
	 }

         //move number or Result
	 else if (Character.isDigit(tok.charAt(0))) {
	    if ((res = (ChessResult) notation.stringToResult(tok)) != null) {

	       finished = true;

	       if (Log.debug)
	          Log.debug(DEBUG, "Result token: " + tok);
	       if (lastMove != null) {
		  lastMove.setResult(res);
		  if (Log.debug) {
		     Log.debug(DEBUG, "Result set(" + lastMove + "): " + res);
		     ChessMove prevTmp = (ChessMove) lastMove.getPrev();
		     if (prevTmp != null)
		     Log.debug(DEBUG, "Result set(" + lastMove + "): " 
		        + res + " prev move: " + lastMove.getPrev().dump());

		  }
	       }
	       else
	          if (Log.debug)
		     Log.debug(DEBUG, "Result not set; no last move");
	    }
	    //if it's not a result we don't do anything with the move number
	 }

	 //actual move
	 else if (Character.isLetter(tok.charAt(0))) {
	    try {
	       move = (ChessMove) notation.stringToMove(board, tok);
	       if (move != null) {
		  history.add(move);

		  //if there is a comment in the hopper
		  //we need to apply it to this move's
		  //pre-notation member.
		  if (savedComment != null) {
		     anno = new ChessAnnotation();
		     anno.setComment(savedComment);
		     move.setPrenotation(anno);
		     Log.debug(DEBUG, "prenotation set: " 
		        + move.getPrenotation().getComment());
		     savedComment = null;
		  }

		  lastMove = move;

		  count++; //just to see if we found any moves in the history
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
	       throw e;
	    }
	    catch (AmbiguousMoveException e) {
	       if (Log.debug) {
	          Log.debug(DEBUG, e);
		  Log.debug2(DEBUG,"From Token: " + tok);
		  Log.debug2(DEBUG,"Board: \n" + board);
	       }
	       throw e;
	    }
	    catch (IllegalMoveException e) {
	       if (Log.debug) {
	          Log.debug(DEBUG, e);
		  Log.debug2(DEBUG,"From Token: " + tok);
		  Log.debug2(DEBUG,"Board: \n" + board);
	       }
	       //history.rewind();
		  throw e;
	    } 
	 }

	 //unknown
	 else {
	    //no idea what this is
	    if (Log.debug) 
	       Log.debug(DEBUG, "No idea what this is: <" + tok + ">");
	 }

      }

      if (Log.debug) {
         history.goToEnd();
	 if (history.getCurrentMove() != null) {
            Log.debug(DEBUG, "final result is: " 
	       + history.getCurrentMove().getResult());
	 }
      }

      history.rewind();

      if (count == 0) {
         if (Log.debug)
	    Log.debug(DEBUG, "finished reading History: empty");
         return null;
      }
      else {
         if (Log.debug)
	    Log.debug(DEBUG, "finished reading History");
         return history;
      }
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


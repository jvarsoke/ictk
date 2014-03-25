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

import java.io.FileReader;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

import ictk.util.Log;

import ictk.boardgame.Game;
import ictk.boardgame.GameInfo;
import ictk.boardgame.History;
import ictk.boardgame.Board;
import ictk.boardgame.Move;
import ictk.boardgame.ContinuationList;
import ictk.boardgame.chess.*;

import ictk.boardgame.io.GameWriter;
import ictk.boardgame.io.MoveNotation;

/* PGNWriter *****************************************************************/
/** PGNWriter writes Portable Game Notation chess files
 */
public class PGNWriter extends ChessWriter {

   public static final long DEBUG = Log.GameWriter;

      /** do not output symbolic nor numeric NAGs */
   public static final int NO_GLYPH                   = 0,
      /** output symbolic NAGs (!, ?, += etc) when possible.
       ** Otherwise use numeric ($123) */
                           SYMBOLIC_AND_NUMERIC_GLYPH = 1,
      /** only output numeric NAGs ($1, $123 etc) */
                           NUMERIC_GLYPH              = 2,
      /** only output symbolic NAGs.  If there is not symbolic
       ** notation for the NAG, then ommit it. */
                           SYMBOLIC_ONLY_GLYPH        = 3,
      /** only output suffix NAGs -- those that come immediately
       ** after a move (!, ?, !?, ?!. !!, ?? only). */
                           SUFFIX_ONLY_GLYPH          = 4;

      /** for indicating what type of output is sent to the formatter */
   protected static final int _NOTHING          = 0,
                              _MOVE_W           = 1,
                              _MOVE_B           = 2,
                              _VARIATION_BEGIN  = 3,
			      _VARIATION_END    = 4,
			      _COMMENT          = 5,
			      _GLYPH            = 6,
			      _RESULT           = 7,
                              _MISC             = 8;

      /** output held for formatting */
   protected StringBuffer buffer = null;
     
      /** maximum column for output */
   protected int     colWidth         = 80,
      /** what type of symbol style should be outputted. */
		     glyphStyle       = SYMBOLIC_AND_NUMERIC_GLYPH;
      /** export comments { }?  This is independent of NAGs */
   protected boolean exportComments   = true,
      /** export variations? */
                     exportVariations = true,
      /** indent comments */
		     indentComments   = false,
      /** indent variations */
		     indentVariations = false,
      /** only print one move (2 ply) per line */
		     oneMovePerLine   = false;

      /** used for PGN output to indicate the next move needs a number infront*/
   private boolean needNumber     = false; 
      /** used for the formatter to keep track of how deep we are for indent*/
   private int     variationsDeep = 0;
   private String  indentStr      = "    ";

      /** the type of notation to use for output */
   protected ChessMoveNotation notation = new SAN();

   //constructor///////////////////////////////////////////////////////////////
   public PGNWriter (OutputStream _out) {
      super(_out);
   }

   public PGNWriter (Writer _out) {
      super(_out);
   }

   //options//////////////////////////////////////////////////////////////////

   public void setMoveNotation (MoveNotation notation) { 
      this.notation = (ChessMoveNotation) notation; 
   }

   public MoveNotation getMoveNotation () { return notation; }

   /* setColumnWidth ********************************************************/
   /** sets the width of the output for the move list.  Setting this 
    *  to a rediculously small value is not advised.<br>
    *  DEFAULT: 80
    */
   public void setColumnWidth (int col) { colWidth = col; }

   /* getColumnWidth ********************************************************/
   /** returns the current column width.
    */
   public int getColumnWidth () { return colWidth; }

   /* setExportComments *****************************************************/
   /** turns on or off whether prenotation and annotation comments are 
    *  output.  This does not affect whether NAGs are output.<br>
    *  DEFAULT: true
    */ 
   public void setExportComments (boolean t) { exportComments = t; }


   /* isExportComments ******************************************************/
   /** returns true if prenotationa and annotations are allowed in the
    *  output stream.
    */
   public boolean isExportComments () { return exportComments; }

   /* setExportVariations ***************************************************/
   /** turns on or off whether variations are in the output stream.
    *  <br>
    *  DEFAULT: true
    */
   public void setExportVariations (boolean t) { exportVariations = t; }

   /* isExportVariations ***************************************************/
   /** returns true if variations are allowed in the output stream.
    */
   public boolean isExportVariations () { return exportVariations; }

   /* setIndentComments *****************************************************/
   /** indent the main-line comments.  This only affects comments on 
    *  the main-line.  Variation comments are not affected.
    *  <br>
    *  DEFAULT: false
    */
   public void setIndentComments (boolean t) { indentComments = t; }
   public boolean isIndentComments () { return indentComments; }

   /* setIndentVariations ***************************************************/
   /** indent the main-line variatins.  This only affects variations off of 
    *  the main-line.  Variation comments are not affected.
    *  <br>
    *  DEFAULT: false
    */
   public void setIndentVariations (boolean t) { indentVariations = t; }
   public boolean isIndentVariations () { return indentVariations; }

   /* setOneMovePerLine *****************************************************/
   /* sets the output to emulate column notation style often found in 
    *  books and newspaper articles.
    *  <br>
    *  DEFAULT: false
    */
    /*
   public void setOneMovePerLine (boolean t) { oneMovePerLine = t; }
   public boolean isOneMovePerLine () { return oneMovePerLine; }
   */

   /* setAnnotationGlyphStyle ***********************************************/
   /** sets the annotation glyph style. This determins how NAGs and other
    *  symbolic annotations such as !, ?, !?, +=, -/+ etc wll be 
    *  presented.
    *  <br>
    *  DEFAULT: SYMBOLIC_AND_NUMERIC_GLYPH
    */
   public void setAnnotationGlyphStyle (int style) { glyphStyle = style; }

   /* getAnnotationGlyphStyle ***********************************************/
   /** returns the currect Glyph Style setting.
    */
   public int getAnnotationGlyphStyle () { return glyphStyle; }

   //writing//////////////////////////////////////////////////////////////////
   public void writeGame (Game game) 
          throws IOException {
      ChessGame g = (ChessGame) game;

      if (g == null) {
         if (Log.debug)
	    Log.debug(DEBUG, "can't write a null game");
         throw new NullPointerException ("can't write null game");
      }

      if (Log.debug)
         Log.debug(DEBUG, "Writing game");

      g.getHistory().rewind();
      writeGameInfo(g.getGameInfo());
      if (!g.getBoard().isInitialPositionDefault())
         writeBoard(g.getBoard());
      println();
      writeHistory(g.getHistory());
   }


   /* writeGameInfo () ***************************************************/
   public void writeGameInfo (GameInfo gameinfo) 
          throws IOException {
      ChessGameInfo gi = (ChessGameInfo) gameinfo;
      StringBuffer sb = new StringBuffer();
      String event, site, date, round, white, black, result;

      event = site = date = round = white = black = result = null;

      if (Log.debug)
         if (gi == null)
	    Log.debug(DEBUG, "gameInfo is null, so writing default header");

      if (gi != null) {
         event = gi.getEvent();
	 site = gi.getSite();
	 date = gi.getDateString();
	 round = gi.getRound();
	 if (gi.getWhite() != null)
	    white = gi.getWhite().getName();
	 if (gi.getBlack() != null)
	    black = gi.getBlack().getName();

	 result = notation.resultToString(gi.getResult());
      }

      if (event == null)
         event = "?";

      if (site == null)
         site = "?";

      if (date == null)
         date = "????.??.??";

      if (white == null)
         white = "?";

      if (black == null)
         black = "?";

      if (round == null)
         round = "-";  //round not appropriate

      if (result == null)
         result = "*"; //continuing

      //required
      sb.append("[Event \"").append(event).append("\"]\n");
      sb.append("[Site \"").append(site).append("\"]\n");
      sb.append("[Date \"").append(date).append("\"]\n");
      sb.append("[Round \"").append(round).append("\"]\n");
      if (gi != null
         && gi.getSubRound() != null 
	 && !gi.getSubRound().equals(""))
         sb.append("[SubRound \"").append(gi.getSubRound()).append("\"]\n");
      sb.append("[White \"").append(white).append("\"]\n");
      sb.append("[Black \"").append(black).append("\"]\n");
      sb.append("[Result \"").append(result).append("\"]\n");

      //optional 
      if (gi != null) {
         if (gi.getWhiteRating() > 0)
	    sb.append("[WhiteElo \"")
	      .append(gi.getWhiteRating()).append("\"]\n");

         if (gi.getBlackRating() > 0)
	    sb.append("[BlackElo \"")
	      .append(gi.getBlackRating()).append("\"]\n");

	 if (gi.getECO() != null)
	    sb.append("[ECO \"").append(gi.getECO()).append("\"]\n");

	 if (gi.getTimeControlInitial() > 0)
	    sb.append("[TimeControl \"").append(gi.getTimeControlInitial())
	    .append("+").append(gi.getTimeControlIncrement())
	    .append("\"]\n");

         Enumeration<?> keys = gi.props.propertyNames();
	 String key = null, value = null;
	 while (keys.hasMoreElements()) {
	    key = (String) keys.nextElement();
	    value = gi.props.getProperty(key);
	    sb.append("[").append(key).append(" \"")
	      .append(value).append("\"]\n");
	 }
      }

      if (Log.debug)
         Log.debug(DEBUG, "writing gameInfo block to stream");
      print(sb); //no extra \n (might add FEN)
   }

   /* writeHistory ********************************************************/
   /** write the history list. 
    *  <br>
    *  <p>Prenotations: Might not be recognized by all readers.<br>
    *  If there are Prenotation on moves that is not the head of a variation, 
    *  or the game, or are not following a move with an Annotation, the 
    *  Prenotation will probably be interpreted by the next reader as an 
    *  Annotation of the previous move. <i>(there is a way around this
    *  by making empty annotations, but it's unclear how much
    *  adding Prenotations to PGN will make the output incompadible with
    *  other readers)</i>.
    *  <br>
    */
   /* it's synchronized because buffer and variationsDeep are by instance */
   public synchronized void writeHistory (History history) 
          throws IOException {
      Move currMove = null,
           lastMove = null,
	   walker = null;
      ChessResult result = null;
      int  num  = history.getInitialMoveNumber(); 

         if (history == null) {
	     if (Log.debug)
	        Log.debug(DEBUG, "can't write a null history");
             throw new NullPointerException ("can't write null history");
	 }

         buffer = new StringBuffer(colWidth);

         if (Log.debug)
	    Log.debug(DEBUG, "walking the History move tree");

	 walkMoveTreeBreadthFirst(history.getFirstAll(), num);

         walker = history.getFinalMove(true);

	 if (walker != null)
	    result = (ChessResult) walker.getResult();

	 if (result == null || result.isUndecided())
	    formatOutput("*", _RESULT);
	 else
	    formatOutput(notation.resultToString(result), _RESULT);

         //empty buffer
         if (buffer.length() != 0)
            print(buffer.toString());

      println();  // \n pgn formatting
   }

   /* walkMoveTreeBeadthFirst ********************************************/
   /** walks down the move branches in a breadth first manner decending
    *  the last node first.  The String representations of the moves will
    *  be added to the StirngBuffer object.
    *
    *  @param cont all moves at this branch level
    *  @param num  current move number count
    */
   protected void walkMoveTreeBreadthFirst (ContinuationList cont, 
                                            int num) {
      ChessMove m = null;
      boolean isBlackMove = true;
      short[] nags = null;

      //11...cxd4=Q+!! (longest)
      StringBuffer sbtmp = new StringBuffer(12);

      ChessAnnotation anno = null;

         if (Log.debug)
	    Log.debug(DEBUG, "continuations(" + cont.size() + ")"
	       + ((cont.isTerminal()) ? " is " : " is not ") 
	       + "terminal");

	 if (cont != null && !cont.isTerminal()) {

            //figure out if we're on a black move or white
	    if (cont.get(0) != null)
	       isBlackMove = ((ChessMove)cont.get(0)).isBlackMove();
	    else if (cont.get(1) != null)
	       isBlackMove = ((ChessMove)cont.get(1)).isBlackMove();
	    else
	       assert false
		  : "ContinuationList contained null mainline, no variations"
		  + " and was not isTerminal().";

            //loop through all variations on the next move
	    for (int i=0; (i==0 || exportVariations) && i<cont.size(); i++) {
	       m = (ChessMove) cont.get(i);

	       if (i > 0) {
		  variationsDeep++;
	          formatOutput("(", _VARIATION_BEGIN);
	       }

	       //add prenotation in "{anno}"
	       if (exportComments
	          && m.getPrenotation() != null
	          && (m.getPrenotation().getComment() 
		       != null)) {
		  formatOutput(" {" 
			       + m.getPrenotation().getComment()
		               + "} ",
			       _COMMENT);
	       }

               //add numbers
	       if (!isBlackMove)
		  sbtmp.append(num).append(".");

	       if ((i > 0 || needNumber) && isBlackMove)
	          sbtmp.append(num).append("...");

	       needNumber = false;

	       //add move
	       sbtmp.append(notation.moveToString(m));

               anno = (ChessAnnotation) m.getAnnotation();

	       //add suffix
	       if (anno != null 
		   && glyphStyle != NO_GLYPH 
		   && glyphStyle != NUMERIC_GLYPH
		   && anno.getSuffix() != 0) {
		  sbtmp.append(NAG.numberToString(anno.getSuffix()));
	       }

	       //send move chunk to output
	       formatOutput(sbtmp.toString(), 
	                    ((isBlackMove) ? _MOVE_B : _MOVE_W));

               //empty move buffer
	       sbtmp.delete(0, sbtmp.length());

	       //add NAG
	       if (anno != null
	           && glyphStyle != NO_GLYPH
		   && glyphStyle != SUFFIX_ONLY_GLYPH
	           && ((nags = anno.getNAGs()) != null)) {

                  //loop through all nags and send to output
		  for (int j=((NAG.isSuffix(nags[0]) 
		              && glyphStyle != NUMERIC_GLYPH) ? 1 : 0);
		       j < nags.length; j++) {

		     needNumber = true;

		     switch (glyphStyle) {

		        case NUMERIC_GLYPH:
			   formatOutput(NAG.numberToString(nags[j], true),
					_GLYPH);
			   break;

			case SYMBOLIC_ONLY_GLYPH:
			   if (NAG.isSymbol(nags[j]))
			      formatOutput(NAG.numberToString(nags[j]),
					   _GLYPH);
			   break;

			case SYMBOLIC_AND_NUMERIC_GLYPH:
			   formatOutput(NAG.numberToString(nags[j]),
					_GLYPH);
			   break;
		     }
		  }
	       }

	       //comment
	       if (exportComments
	           && anno != null
	           && anno.getComment() != null) {
		   formatOutput("{"
				+ anno.getComment()
				+ "}",
				_COMMENT);
	           needNumber = true;
	       }

               //decend for all variations (non-mainline)
	       if (m != null && exportVariations && i > 0) {
	          if (Log.debug)
		     Log.debug(DEBUG, m + " descending variation");
	          walkMoveTreeBreadthFirst(m.getContinuationList(), 
		     num + ((isBlackMove) ? 1 : 0));

                  formatOutput(")", _VARIATION_END);
		  variationsDeep--;
		  needNumber = true;
	       }
	    }

            //now do main-line
	    m = (ChessMove) cont.get(0);
	    if (m != null) {
	       if (Log.debug)
		  Log.debug(DEBUG, m + " descending mainline");
	       walkMoveTreeBreadthFirst(m.getContinuationList(), 
		  num + ((isBlackMove) ? 1 : 0));
	    }
	 }

	 //if this is a terminal node and the line ends with
	 //a result we don't do anything with the result
	 //as the PGN standard specifies only one result for 
	 //the entire move body.
	 //TODO: might want to put it in a comment it it's not
	 //      the mainline.
	 else {
	 }
   }

   /* formatOutput ********************************************************/
   /** formats output to colWidth length before putting it to the output
    *  stream.  Data is held in the StringBuffer until colWidth is reached
    *  in which case the data is put on the output stream.  If String
    *  puts the StringBuffer over colWidth then the data in the String
    *  will be tolkenized and split into pieces.  If the StringBuffer
    *  does not reach colWidth then data is added to the buffer for
    *  later.
    *
    *  @param str data that has yet to be put on the buffer
    *  @param type the type of token
    */
   protected void formatOutput (String str, int type) {
      boolean spacer = buffer.length() != 0;
      int length = buffer.length() 
                   + str.length() 
                   + ((spacer) ? 1 : 0);

      if (Log.debug)
         Log.debug(DEBUG, 
	    "[" + length + "/" + colWidth + "] "
	    + "buffer(" + buffer.length() 
	    + ") + \"" + str + "\"(" + str.length() + ")"
	    );

      //if starting a variation with indent
      if (indentVariations 
          && type == _VARIATION_BEGIN 
	  && variationsDeep > 0) {
         println(buffer.toString());
	 buffer.delete(0, buffer.length());

	 if (variationsDeep == 1)
	    buffer.append(this.indentStr);
	 else
	    buffer.append(indentStr).append(indentStr);

	 buffer.append(str);
      }

      //ending a variation with indents
      else if (indentVariations 
               && type == _VARIATION_END
	       && variationsDeep > 0) {

         if (buffer.length() == 0) {
	    if (variationsDeep == 1) {
	       print(indentStr);
	       println(str);
	    }
	    else {
	       print(indentStr);
	       print(indentStr);
	       println(str);
	       if (variationsDeep == 2)
	          buffer.append(indentStr);
	       else
	          buffer.append(indentStr).append(indentStr);
	    }
	 }
	 else {
	    print(buffer.toString());
	    print(" ");
	    println(str);
	    buffer.delete(0, buffer.length());
	 }
      }

      else if (indentComments && type == _COMMENT && variationsDeep == 0) {
         println(buffer.toString());
	 buffer.delete(0, buffer.length());

	 if (indentStr.length() + str.length() > colWidth)
	    formatLongComment(str);
	 else
	    println(indentStr + str);
      }

      //buffer not yet full
      else if (length <= colWidth) {
         if (spacer)
            buffer.append(" ");
         buffer.append(str);
	 if (Log.debug)
	    Log.debug(DEBUG, "appending: " + str);
      }

      //buffer doth spillith over
      else {
         if (type != _COMMENT) {
	    println(buffer.toString());
	    if (Log.debug)
	       Log.debug(DEBUG, "writing: " + buffer.toString());
	    buffer.delete(0, buffer.length());

	    if (indentVariations) {
	       if (variationsDeep > 0)
	          buffer.append(this.indentStr);
	       if (variationsDeep > 1)
	          buffer.append(this.indentStr);
	    }

	    buffer.append(str);
         }
	 //if it's a comment
	 else 
	    formatLongComment(str);
      }
   }

   /* formatLongComment ******************************************************/
   protected void formatLongComment (String str) {
      int len = buffer.length();
      String tok = null;
      StringTokenizer st = new StringTokenizer (str, " ", false);

      while (st.hasMoreTokens()) {
	 tok = st.nextToken();

	 //less, so add
	 if (len + 1 + tok.length() <= colWidth) {

	    if (buffer.length() == 0) {
	       if (indentComments && variationsDeep == 0)
	          buffer.append(indentStr);
	    } 
	    else
	       buffer.append(" ");

	    buffer.append(tok);
	    len = buffer.length();
	 }

	 //more so write and append
	 else {
	    if (Log.debug)
	       Log.debug(DEBUG, "writing: " + buffer.toString());

	    println(buffer.toString());

	    buffer.delete(0, buffer.length());

	    if (indentComments) {
	       if (variationsDeep == 0)
	          buffer.append(indentStr);
	       else if (indentVariations && variationsDeep > 0) {
	          if (variationsDeep == 1)
		     buffer.append(indentStr);
		  else
		     buffer.append(indentStr).append(indentStr);
	       }
	    }

	    buffer.append(tok);
	    len = buffer.length();
	 }
      }

      if (indentComments && variationsDeep == 0 && buffer.length() > 0) {
         println(buffer.toString());
	 buffer.delete(0, buffer.length());
      }

   }

   /* writeBoard *************************************************************/
   /** writeBoard(Board) writes the current board
    */
   public void writeBoard (Board board)
          throws IOException {
      StringBuffer buff = new StringBuffer();
      
      buff.append("[FEN \"");
      buff.append(new FEN().boardToString(board));
      buff.append("\"]");

      println(buff.toString());
   }
}

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

import ictk.boardgame.Game;
import ictk.boardgame.GameInfo;
import ictk.boardgame.History;
import ictk.boardgame.Board;
import ictk.boardgame.Move;
import ictk.boardgame.ContinuationList;
import ictk.boardgame.chess.*;

import ictk.boardgame.io.GameReader;
import ictk.boardgame.io.GameWriter;

/* PGNWriter *****************************************************************/
/** PGNWriter writes Portable Game Notation chess files
 */
public class PGNWriter extends ChessWriter {
   protected ChessMoveNotation notation = new SAN();
     
     /** used for PGN output to indicate the next move needs a number infront*/
   private boolean needNumber = false; 

   public PGNWriter (OutputStream _out) {
      super(_out);
   }

   public PGNWriter (Writer _out) {
      super(_out);
   }

   public void writeGame (Game game) 
          throws IOException {
      ChessGame g = (ChessGame) game;
      if (g == null) throw new NullPointerException ("can't write null game");
      g.getHistory().rewind();
      writeGameInfo(g.getGameInfo());
      if (!g.getBoard().isInitialPositionDefault())
         writeBoard(g.getBoard());
      println();
      writeHistory(g.getHistory());
      //pgn has no board
   }


   /* writeGameInfo () ***************************************************/
   public void writeGameInfo (GameInfo gameinfo) 
          throws IOException {
      ChessGameInfo gi = (ChessGameInfo) gameinfo;
      StringBuffer sb = new StringBuffer();
      String event, site, date, round, white, black, result;

      event = site = date = round = white = black = result = null;

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

      if (white == null)
         white = "";

      if (black == null)
         black = "";

      if (round == null)
         round = "-";  //round not appropriate

      if (result == null)
         result = "*"; //continuing

      //required
      sb.append("[Event \"").append(event).append("\"]\n");
      sb.append("[Site \"").append(site).append("\"]\n");
      sb.append("[Date \"").append(date).append("\"]\n");
      sb.append("[Round \"").append(round).append("\"]\n");
      if (gi.getSubRound() != null && !gi.getSubRound().equals(""))
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

         Enumeration keys = gi.props.propertyNames();
	 String key = null, value = null;
	 while (keys.hasMoreElements()) {
	    key = (String) keys.nextElement();
	    value = gi.props.getProperty(key);
	    sb.append("[").append(key).append(" \"")
	      .append(value).append("\"]\n");
	 }
      }

      print(sb); //no extra \n (might add FEN)
   }


   /* writeHistory () ****************************************************/
   public void writeHistory (History history) 
          throws IOException {
      writeHistory(history, 80);
   }

   /* writeHistory ********************************************************/
   /** write the history list.
    *  @param cols the column limition (80 is default)
    */
   public void writeHistory (History history, int cols) 
          throws IOException {
      StringBuffer sb = null;
      Move currMove = null,
           lastMove = null,
	   walker = null;
      int  num  = history.getInitialMoveNumber(); 

         if (history == null)
             throw new NullPointerException ("can't write null history");

         sb = new StringBuffer();

	 //walkMoveTree((ChessMove) history.getFirst(), num, sb);

	 walkMoveTreeBreadthFirst(history.getFirstAll(), num, sb);
	 ChessResult result = (ChessResult) history.getFinalMove(true).getResult();
	    if (result == null || result.isUndecided())
	       sb.append(" *");
	    //else  nothing to do, already printed
	       //sb.append(" ").append(notation.resultToString(result));

	 formattedOutput(sb.toString(), cols);

      println();  // \n pgn formatting
   }

   /* walkMoveTreeBeadthFirst ********************************************/
   /** walks down the move branches in a breadth first manner decending
    *  the last node first.  The String representations of the moves will
    *  be added to the StirngBuffer object.
    *
    *  @param cont - all move at this branch level
    *  @param num  - current move number count
    *  @param sb   - the string to add all the moves to
    */
   protected void walkMoveTreeBreadthFirst (ContinuationList cont, 
                                            int num,
					    StringBuffer sb) {
      ChessMove m = null;
      boolean isBlackMove = true;

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
	    for (int i=0; i<cont.size(); i++) {
	       m = (ChessMove) cont.get(i);
	       sb.append(" ");

	       if (i > 0) 
	          sb.append("( ");

	       if (!isBlackMove)
		  sb.append(num).append(".");

	       if ((i > 0 || needNumber) && isBlackMove)
		  sb.append(num).append("...");

	       needNumber = false;

	       //add move
	       sb.append(notation.moveToString(m));

	       //add annotation in "{anno}"
	       if (m.getAnnotation() != null
	          && ((ChessAnnotation)m.getAnnotation()).getComment() 
		       != null) {
		  sb.append(" {")
		    .append(((ChessAnnotation)m.getAnnotation()).getComment())
		    .append("}");
	          needNumber = true;
	       }
	    }

            //decend each variation starting with the last
	    for (int j=cont.size()-1; j >= 0; j--) {
	       m = (ChessMove) cont.get(j);

	       if (m != null) {
	          walkMoveTreeBreadthFirst(m.getContinuationList(), 
		     num + ((isBlackMove) ? 1 : 0), sb);

		  if (j > 0) {
		     sb.append(" ) ");
		     needNumber = true;
		  }
	       }
	    }

	 }
	 else {
	    ChessResult result = (ChessResult) cont.getDepartureMove().getResult();
	    if (result != null && !result.isUndecided())
	       sb.append(" ").append(notation.resultToString(result));
	 }
   }

   /* formmattedOutput **************************************************/
   /** formats the move list into maxlen col lines
    */
   protected void formattedOutput (String moves, int maxlen) {
      StringTokenizer st = new StringTokenizer(moves, " ", false);

      int col = 0;
      String tok = null;
      int count = 0;
      while (st.hasMoreTokens()) {
         tok = st.nextToken();
	 //rest won't fit on line (-1 for space char)
	 if (maxlen > 0 && col + tok.length() >= maxlen-1) {
	    println();
	    col = 0;
	 }

	 //space between tokens
         //if (col != 0 && count++ != 0) 
         if (col != 0) {
	    print(" ");
	    col++;
	 }
	 print(tok);
	 col += tok.length();
      }

      println();  // end of move list
   }

//FIXME: shouldn't write current board, but original board
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

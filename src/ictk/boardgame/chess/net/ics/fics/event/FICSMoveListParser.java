/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: FICSMoveListParser.java,v 1.3 2003/08/26 20:43:09 jvarsoke Exp $
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
package ictk.boardgame.chess.net.ics.fics.event;

import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.*;
import java.util.regex.*;
import java.io.IOException;

/**

 */
public class FICSMoveListParser extends ICSEventParser {

   //static/////////////////////////////////////////////////////////////////
   public static FICSMoveListParser singleton;
   public static final Pattern masterPattern, moveLinePattern;

/*
Movelist for game 34:

homerg (1626) vs. drakorg (1678) --- Mon Nov  4, 08:38 CET 2002
Rated standard match, initial time: 10 minutes, increment: 12 seconds.

Move  homerg                  drakorg
----  ---------------------   ---------------------
  1.  e4      (0:00.000)      e6      (0:00.000)
  2.  Nf3     (0:01.699)      d5      (0:11.371)
  3.  e5      (0:02.251)      c5      (0:01.980)
  4.  c3      (0:01.491)      Nc6     (0:03.301)
  5.  d4      (0:01.260)      Qb6     (0:07.200)
  6.  Bd3     (0:02.090)      cxd4    (0:06.380)
  7.  Ng5     (0:15.370)      Bc5     (0:30.369)
  8.  Bxh7    (0:15.330)      dxc3    (0:16.700)
  9.  Qe2     (1:20.360)      Nd4     (0:25.269)
      {Still in progress} *
*/
   static {

      masterPattern  = Pattern.compile("^:?"
	 + "Movelist for game (\\d+):\\n:?\\n" //board number
	 + "(\\S+)"                            //white name
	 + "\\s"
	 //FIXME: rating could be "UNR"
         + "(?:\\(\\s*([0-9+-]+[EP]?)\\))"     //white's rating
	 + "\\svs\\.\\s"
	 + "(\\S+)"                            //black name
	 + "\\s"
         + "(?:\\(\\s*([0-9+-]+[EP]?)\\))"     //black's rating
	 + "\\s---\\s"
	 + ICSDate.REGEX
	 + "\\n"
	 + "^:?(Rated|Unrated)"                //rated/unrated
	 + "\\s"
	 + "(\\w+)"                            //variant
	 + "\\smatch, initial time:\\s"
	 + "(\\d+)"                            //inital time
	 + "\\sminutes, increment:\\s"
	 + "(\\d+)"                            //incr
	 + "\\sseconds\\.\\n\\n"
 	 + "Move\\s+"
	 + "(\\S+)"                            //white's name
	 + "\\s+"
	 + "(\\S+)"                            //black's name
	 + "\\s*\\n"
	 + "^----\\s+---------------------\\s+---------------------\\n"
	 + "(.*)"                            //moves
	 + "^\\s{6}\\{([^}]+)\\}\\s(\\S)"    //result line
       , Pattern.MULTILINE | Pattern.DOTALL);

      //individual moves
      moveLinePattern = Pattern.compile("^:?"
	 + "\\s*(\\d+)\\.\\s+"               //move number
	 + "(\\S+)\\s+"                      //white move
	 + "(\\((\\d+):(\\d\\d)\\.?(\\d{3})?\\))?"  //time
	 + "\\s*"
	 + "((\\S+)?\\s*"                    //black move
	 + "(\\((\\d+):(\\d\\d)\\.(\\d{3})?\\))?)?\\s*$"//time
         , Pattern.MULTILINE);

      singleton = new FICSMoveListParser();
   }

   //instance///////////////////////////////////////////////////////////////
   protected FICSMoveListParser () {
      super(masterPattern);
   }

   /* getInstance ***********************************************************/
   public static ICSEventParser getInstance () {
       return singleton;
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSMoveListEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSMoveListEvent evt = (ICSMoveListEvent) event;

      if (Log.debug && debug)
         Log.debug(DEBUG, "assigning matches", m);

/*
1: 95
2: theep
3: 1656
4: Quandary
5: 1682
6: Tue Nov  5, 03:50 CET 2002
7: Tue
8: Nov
9: 5
10: 03
11: 50
12: CET
13: 2002
14: Rated
15: standard
16: 30
17: 5
18: theep
19: Quandary
20:   1.  ...                     c5      (0:00.000)
21: Still in progress
22: *
*/
      //setMessage(m.group(0));
      //ICSDebug.dumpRegex(m);

      evt.setWhitePlayer(m.group(2));
      evt.setBlackPlayer(m.group(4));
      evt.setWhiteRating(new ICSRating(m.group(3)));
      evt.setBlackRating(new ICSRating(m.group(5)));
      evt.setVariant(new ICSVariant(m.group(15)));
      evt.setStatus(m.group(21));
      evt.setResult(new ICSResult(m.group(22)));

      evt.setRated("Rated".equals(m.group(14)));

      int i = 0;
      ICSDate date = new ICSDate(m.group(6));
      evt.setDate(date);
      try {
         i = 1;
         evt.setBoardNumber(Integer.parseInt(m.group(i)));
	 i = 16;
	 evt.setInitialTime(Integer.parseInt(m.group(i)));
	 i = 17;
	 evt.setIncrement(Integer.parseInt(m.group(i)));

      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
	    "Couldn't parse a number in: " + m.group(i));
      }

/*
0:   7.  Bg5     (1:06.240)      O-O     (0:38.265)
1: 7
2: Bg5
3: (1:06.240)
4: 1
5: 06
6: 240
7: O-O     (0:38.265)
8: O-O
9: (0:38.265)
10: 0
11: 38
12: 265

and for one move lines

0:   8.  Qd2     (0:28.890)
1: 8
2: Qd2
3: (0:28.890)
4: 0
5: 28
6: 890
7:
8: null
9: null
10: null
11: null
12: null

black to move first (rare case)

0:   1.  ...     (0:00.000)      e5      (0:00.000)
1: 1
2: ...
3: (0:00.000)
4: 0
5: 00
6: 000
7: e5      (0:00.000)
8: e5
9: (0:00.000)
10: 0
11: 00
12: 000
*/
      Matcher mvMatch = moveLinePattern.matcher(m.group(20));
      List tmplist = new LinkedList();
      ICSMove tmp = null;
      int  min = 0, s = 0, ms = 0;

      while (mvMatch.find()) {

         if (Log.debug && debug)
	    Log.debug(ICSEventParser.DEBUG, "move", mvMatch);

	 if (mvMatch.group(2) != null &&
	     !mvMatch.group(2).equals("...")) {
	    tmp = new ICSMove ();
	    
	    try {
	       tmp.setSAN(mvMatch.group(i = 2));
	       tmp.setBlack(false);
	       tmp.setMoveNumber(Integer.parseInt(mvMatch.group(i = 1)));
	       min = Integer.parseInt(mvMatch.group(i = 4));
	       s = Integer.parseInt(mvMatch.group(i = 5));
	       ms = Integer.parseInt(mvMatch.group(i = 6));
	       tmp.setMoveTime(min * 60000 + s * 1000 + ms);
	    }
	    catch (NumberFormatException e) {
	       Log.error(Log.PROG_WARNING,
		 "threw NumberFormatException"
		 + "for(" + i + "): " + mvMatch.group(i)
		 + " of " + mvMatch.group(0));
	       evt.setEventType(ICSEvent.UNKNOWN_EVENT);
	       evt.setMessage(m.group(0));
	       if (Log.debug)
		  Log.debug(ICSEventParser.DEBUG, "regex", mvMatch);
	       return;
	    }
	    tmplist.add(tmp);
	 }

	 if (mvMatch.group(8) != null) {
            tmp = new ICSMove ();

            try {
               tmp.setSAN(mvMatch.group(i = 8));
               tmp.setBlack(true);
               tmp.setMoveNumber(Integer.parseInt(mvMatch.group(i = 1)));
               min = Integer.parseInt(mvMatch.group(i = 10));
               s = Integer.parseInt(mvMatch.group(i = 11));
               ms = Integer.parseInt(mvMatch.group(i = 12));
               tmp.setMoveTime(min * 60000 + s * 1000 + ms);
            }
	    catch (NumberFormatException e) {
	       Log.error(Log.PROG_WARNING,
		 "threw NumberFormatException"
		 + "for(" + i + "): " + mvMatch.group(i)
		 + " of " + mvMatch.group(0));
	       evt.setEventType(ICSEvent.UNKNOWN_EVENT);
	       evt.setMessage(m.group(0));
	       if (Log.debug)
		  Log.debug(ICSEventParser.DEBUG, "regex", mvMatch);
	       return;
	    }
            tmplist.add(tmp);
	 }
      }

      ICSMove[] moves = null;
      if (tmplist.size() > 0) {
         moves = new ICSMove[tmplist.size()];
	 for (i = 0; i < tmplist.size(); i++) {
	    moves[i] = (ICSMove) tmplist.get(i);
	 }
      }
      evt.setMoves(moves);
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSMoveListEvent evt = (ICSMoveListEvent) event;
      StringBuffer sb = new StringBuffer(20);
      
      if (evt.isFake()) sb.append(":");
/*
Movelist for game 34:

homerg (1626) vs. drakorg (1678) --- Mon Nov  4, 08:38 CET 2002
Rated standard match, initial time: 10 minutes, increment: 12 seconds.

Move  homerg                  drakorg
----  ---------------------   ---------------------
  1.  e4      (0:00.000)      e6      (0:00.000)
  2.  Nf3     (0:01.699)      d5      (0:11.371)
  3.  e5      (0:02.251)      c5      (0:01.980)
  4.  c3      (0:01.491)      Nc6     (0:03.301)
  5.  d4      (0:01.260)      Qb6     (0:07.200)
  6.  Bd3     (0:02.090)      cxd4    (0:06.380)
  7.  Ng5     (0:15.370)      Bc5     (0:30.369)
  8.  Bxh7    (0:15.330)      dxc3    (0:16.700)
  9.  Qe2     (1:20.360)      Nd4     (0:25.269)
      {Still in progress} *
*/
      sb.append("Movelist for game ")
        .append(evt.getBoardNumber())
	.append(":\n\n")
	.append(evt.getWhitePlayer())
	.append(" (")
	//FIXME: need UNR exception
	.append(evt.getWhiteRating())
	.append(") vs. ")
	.append(evt.getBlackPlayer())
	.append(" (")
	.append(evt.getBlackRating())
	.append(") --- ")
	.append(evt.getDate())
	.append("\n");

      if (evt.isRated())
         sb.append("Rated ");
      else
         sb.append("Unrated ");

      sb.append(evt.getVariant())
        .append(" match, initial time:");

      int time = evt.getInitialTime();
      if (time < 100)
         sb.append(" ");
      if (time < 10)
         sb.append(" ");
      sb.append(time)
        .append(" minutes, increment:");

      int incr = evt.getIncrement();
      if (incr < 100)
         sb.append(" ");
      if (incr < 10)
         sb.append(" ");
      sb.append(incr)
        .append(" seconds.\n\n");

      //move list header-------
      sb.append("Move  ");
      pad(sb, evt.getWhitePlayer(), 21, false);
      sb.append("   ")
        .append(evt.getBlackPlayer())
        .append("\n----  ---------------------   ---------------------\n");

      //move list -------------
      ICSMove[] moves = evt.getMoves();
      if (moves != null) {
         //1.  ...     (0:00.000)      e5      (0:00.000)
         if (moves[0].isBlack())
	    sb.append("  1.  ...     (0:00.000)      ");

	 for (int i=0; i < moves.length; i++) {
	    if (!moves[i].isBlack()) {
	       pad(sb, "" + moves[i].getMoveNumber(), 3, true);
	       sb.append(".  ");
	    }
	    else {
	       sb.append("   ");
	    }
	    pad(sb, moves[i].getSAN(), 8, false);

	    if (!moves[i].isBlack() && i < (moves.length-1))
	       pad(sb, "(" + moves[i].getMoveTimeAsString() + ")", 13, false);
	    else
	       sb.append("(")
	         .append(moves[i].getMoveTimeAsString())
	         .append(")\n");
	 }
      }

      //current result
      sb.append("      {")
        .append(evt.getStatus())
	.append("} ")
	.append(evt.getResult())
	.append("\n");

      return sb.toString();
   }

   protected void pad (StringBuffer sb, String str, int max, boolean rj) {
      int len = str.length();

      if (!rj) sb.append(str);

      for (int i=len; i < max; i++)
         sb.append(" ");

      if (rj) sb.append(str);
   }
}

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

package ictk.boardgame.chess.net.ics.fics.event;

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.event.*;


import java.util.List;
import java.util.LinkedList;
import java.util.regex.*;
import java.io.IOException;

public class FICSMoveListEvent extends ICSMoveListEvent {
   //static///////////////////////////////////////////////////////////////
   public static final int MOVE_LIST_EVENT = ICSEvent.MOVE_LIST_EVENT;
   public static final Pattern pattern, moveLinePattern;

   static {
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
      pattern  = Pattern.compile("^:?"
               + "Movelist for game (\\d+):\\n:?\\n" //board number
	       + "(\\S+)"                          //white name
	       + "\\s"
	       //FIXME: rating could be "UNR"
	       + ICSEvent.REGEX_rating		   //white's rating
	       + "\\svs\\.\\s"
	       + "(\\S+)"                          //black name
	       + "\\s"
	       + ICSEvent.REGEX_rating		   //black's rating
	       + "\\s---\\s"
	       + ICSEvent.REGEX_date
	       + "\\n"
	       + "^:?(Rated|Unrated)"              //rated/unrated
	       + "\\s"
	       + "(\\w+)"                          //variant
	       + "\\smatch, initial time:\\s"
	       + "(\\d+)"                          //inital time
	       + "\\sminutes, increment:\\s"
	       + "(\\d+)"			   //incr
	       + "\\sseconds\\.\\n\\n"
	       + "Move\\s+"
	       + ICSEvent.REGEX_handle
	       + "\\s+"
	       + ICSEvent.REGEX_handle
	       + "\\s*\\n"
	       + "^----\\s+---------------------\\s+---------------------\\n"
	       + "(.*)"                            //moves
	       + "^\\s{6}\\{([^}]+)\\}\\s(\\S)"    //result line
          , Pattern.MULTILINE | Pattern.DOTALL);

      moveLinePattern = Pattern.compile("^:?"
               + "\\s*(\\d+)\\.\\s+"               //move number
	       + "(\\S+)\\s+"                      //white move
	       + "(\\((\\d+):(\\d\\d)\\.?(\\d{3})?\\))?"  //time
	       + "\\s*"
	       + "((\\S+)?\\s*"                      //black move
	       + "(\\((\\d+):(\\d\\d)\\.(\\d{3})?\\))?)?\\s*$"//time
          , Pattern.MULTILINE);
   } 

   //instance/////////////////////////////////////////////////////////////
   
   //constructors/////////////////////////////////////////////////////////
   public FICSMoveListEvent (ICSProtocolHandler server) {
      super(server);
   }


   protected FICSMoveListEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSMoveListEvent(server, m);
      else 
         return null;
   }

   //ICSMessageEvent//////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   //ICSChannelEvent/////////////////////////////////////////////////////
   protected void assignMatches (Matcher m) {
      detectFake(m.group(0));

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

      setWhitePlayer(m.group(2));
      setBlackPlayer(m.group(4));
      setWhiteRating(new ICSRating(m.group(3)));
      setBlackRating(new ICSRating(m.group(5)));
      setVariant(new ICSVariant(m.group(15)));
      //setStatus(m.group(21));
      setResult(new ICSResult(m.group(22)));

      int i = 0;
      try {
         i = 1;
         setBoardNumber(Integer.parseInt(m.group(i)));
	 i = 16;
	 setInitialTime(Integer.parseInt(m.group(i)));
	 i = 17;
	 setIncrementTime(Integer.parseInt(m.group(i)));
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("FICSMoveListEvent:"
	    + " couldn't parse a number in: " + m.group(i));
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
         ICSDebug.dumpRegex(mvMatch);
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
	       ICSErrorLog.report("FICSMoveListEvent: "
	          + " error parsing number field(" + i + "): "
		  + mvMatch.group(i));
	    }
	    tmplist.add(tmp);
	 }

	 if (mvMatch.group(7) != null) {
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
               ICSErrorLog.report("FICSMoveListEvent: "
                  + " error parsing number field(" + i + "): "
                  + mvMatch.group(i));
            }
            tmplist.add(tmp);
	 }
      }

      if (tmplist.size() > 0) {
         moves = new ICSMove[tmplist.size()];
	 for (i = 0; i < tmplist.size(); i++) {
	    moves[i] = (ICSMove) tmplist.get(i);
	 }
      }
   }

   public String toString () {
      StringBuffer sb = new StringBuffer();
         sb.append("Move List[").append(getBoardNumber()).append("]: ")
	   .append(getWhitePlayer()).append("(")
	   .append(getWhiteRating()).append(") v. ")
	   .append(getBlackPlayer()).append("(")
	   .append(getBlackRating()).append(")\n");
	 sb.append(getVariant()).append(" ")
	   .append(getInitialTime()).append(" ")
	   .append(getIncrementTime()).append(" ");
	 if (!isRated())
	    sb.append("un");
	 sb.append("rated\n\n");

	 if (moves != null)
	    for (int i=0; i < moves.length; i++) {
	       sb.append(moves[i]).append("\n");
	    }
	 sb.append(getResult()).append("\n");

      return sb.toString();
   }
   public String getNative () { return null; }
}

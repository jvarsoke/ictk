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
import ictk.boardgame.chess.net.ics.event.*;

import ictk.boardgame.chess.net.ics.*;

import java.util.regex.*;
import java.io.IOException;

public class FICSHistoryEvent extends ICSHistoryEvent {
   public static int FICS_HISTORY_MAX = 10;
/*
History for TibetianTick:
                  Opponent      Type         ECO End Date
42: + 1604 W 1612 Edwinn        [ sr 30  20] C00 Res Fri Oct 11, 14:04 EST 2002
43: - 1596 W 1783 Xman          [ sr 20   0] C53 Mat Tue Oct 15, 13:46 EST 2002
44: - 1587 B 1748 Chessmark     [ sr 20   0] B07 Res Tue Oct 15, 14:07 EST 2002
45: + 1596 W 1458 jfa           [ sr 20   0] C57 Mat Tue Oct 15, 14:24 EST 2002
46: = 1598 W 1659 mrbishop      [ sr 60  15] B22 Agr Wed Oct 16, 22:48 EST 2002
47: + 1604 W 1317 davidrpotesta [ sr 60  15] C50 Mat Fri Oct 18, 21:14 EST 2002
48: = 1606 B 1664 mrbishop      [ sr 60  15] B07 Agr Wed Oct 23, 23:09 EST 2002
49: + 1078 W 1128 RCarson       [ br  5  12] C54 Fla Fri Oct 25, 12:43 EST 2002
50: - 1600 B 1881 Ronanm        [ sr 60  15] A48 Res Sun Oct 27, 19:34 EST 2002
51: + 1616 W 1655 mrbishop      [ sr 60  15] B22 Res Wed Oct 30, 23:36 EST 2002
 4: + 1500 W    0 HiddenTick    [ su999 999] *** Dis Mon Oct 14, 06:42 CET 2002
*/

   //static initializer/////////////////////////////////////////////////////
   public static final int HISTORY_EVENT =  ICSEvent.HISTORY_EVENT;
   public static final Pattern pattern, historyItemPattern;
   public static final String  REGEX_history_item;

   static {
      REGEX_history_item = 
         "(^\\s?(\\d+)"           //history number
	 + ":\\s"
	 + "([\\+\\-=])"          //win loss draw
	 + "\\s+"
	 + "(\\d+)"               //user's rating
	 + "\\s"
	 + "([BW])"                 //was user black of white?
	 + "\\s+"
	 + "(\\d+)"               //opponent's rating
	 + "\\s"
	 + ICSEvent.REGEX_handle //opponent
	 + "\\s+"
	 + "\\["
	 + "(p)?"                 //private?
	 + "\\s?"
	 + "([ubBsSwlL])"          //variant type
	 + "([ur])"               //rated / unrated
	 + "\\s*"
	 + "(\\d+)"               //initial time
	 + "\\s+"
	 + "(\\d+)"               //increment
	 + "\\]\\s"
	 + "(\\S+)"               //ECO
	 + "\\s"
	 + "(\\S+)"               //verbose results
	 + "\\s"
	 + "(\\S+)"               //DoW
	 + "\\s"
	 + "(\\S+)"               //Month
	 + "\\s+"
	 + "(\\d+)"               //DoM
	 + ",\\s"
	 + "(\\d+)"               //hour
	 + ":"
	 + "(\\d+)"               //minute
	 + "\\s"
	 + "(\\S+)"               //TimeZone
	 + "\\s"
	 + "(\\d{4})"             //year
	 + "$)";

      historyItemPattern = Pattern.compile(
                  REGEX_history_item
          , Pattern.MULTILINE);

      pattern  = Pattern.compile( "^" 
                  + "History for\\s"
		  + ICSEvent.REGEX_handle
		  + ":\\s*\\n"
		  + "\\s+Opponent\\s+Type\\s+ECO\\sEnd\\sDate\\n"
		  + "(.*" + REGEX_history_item + "\\n?)"
          , Pattern.MULTILINE | Pattern.DOTALL);
   }

   //instance vars//////////////////////////////////////////////////////////
   ICSHistory[] list;
   String player;

   //constructors///////////////////////////////////////////////////////////
   public FICSHistoryEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSHistoryEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   //ICSEvent/////////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;
      if ((m = matches(s)) != null) 
         return new FICSHistoryEvent(server, m);
      else
         return null;
   }

   //ICSMessage///////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   //ICSKibitz////////////////////////////////////////////////////////////////
   protected void assignMatches (Matcher m) {
      setPlayer(m.group(1));
      list = new ICSHistory[FICS_HISTORY_MAX];

      int i = 0;
      Matcher itemMatcher = historyItemPattern.matcher(m.group(2));
      while (itemMatcher.find()) {
         System.out.println("---begin---");
         ICSDebug.dumpRegex(itemMatcher);
         System.out.println("----end----");
	 if (i > list.length) {
	    ICSHistory[] tmp = new ICSHistory[list.length *2];
	    System.arraycopy(list, 0, tmp, 0, list.length);
	    list = tmp;
	 }
         list[i] = new ICSHistory();
	 list[i].setPlayer(getPlayer());
	 list[i].setPlayerRating(new ICSRating(itemMatcher.group(4)));
	 list[i].setBlack(itemMatcher.group(5).charAt(0) == 'B');
	 list[i].setOpponentRating(new ICSRating(itemMatcher.group(6)));
	 list[i].setOpponent(itemMatcher.group(7));
	 list[i].setVariant(new ICSVariant(itemMatcher.group(9).charAt(0)));
	 list[i].setResult(new ICSResult());

	 switch (itemMatcher.group(3).charAt(0)) {
	    case '=':
	       list[i].getResult().setResultCode(ICSResult.DRAW);
	       break;
	    case '+':
	       if (list[i].isBlack())
	          list[i].getResult().setResultCode(ICSResult.BLACK_WIN);
	       else
	          list[i].getResult().setResultCode(ICSResult.WHITE_WIN);
	       break;
	    case '-':
	       if (list[i].isBlack())
	          list[i].getResult().setResultCode(ICSResult.BLACK_WIN);
	       else
	          list[i].getResult().setResultCode(ICSResult.WHITE_WIN);
	       break;
	    default:
	       ICSErrorLog.report("FICSHistoryEvent: " 
		  + "unexpected result '" + itemMatcher.group(3).charAt(0) 
		  + "' in " + m.group(0));
	       list[i].getResult().setResultCode(ICSResult.UNDECIDED);
	 }

	 list[i].setRated(itemMatcher.group(10).charAt(0) == 'r');
	 list[i].setEco(new ICSEco(itemMatcher.group(13)));

	 try {
	    list[i].setIndex(Integer.parseInt(itemMatcher.group(2)));
	    list[i].setInitialTime(Integer.parseInt(itemMatcher.group(11)));
	    list[i].setIncrementTime(Integer.parseInt(itemMatcher.group(12)));
	 }
	 catch (NumberFormatException e) {
	    ICSErrorLog.report("FICSHistoryEvent: "
	       + "numberic format exception in: " +  itemMatcher.group(0));
	 }
	 i++;
      }
      setMessage(m.group(0));
   }

   public String toString () {
      StringBuffer sb = new StringBuffer(100);
      sb.append("<HISTORY>for player: ")
        .append(getPlayer())
	.append("\n");
      for (int i=0; i<list.length; i++) {
         if (list[i] != null) {
	    sb.append(list[i]).append("\n");
	 }
      }
      sb.append(getMessage());
      sb.append("</HISTORY>");
      return sb.toString();
   }
   public String getNative () { return null; }
}

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

public class FICSGameResultEvent extends ICSGameResultEvent {
   public static final int GAME_RESULT_EVENT 
                = ICSEvent.GAME_RESULT_EVENT;
   public static final Pattern pattern;

   static {
/*
{Game 81 (jeremya vs. SuperSkeelos) jeremya checkmated} 0-1
{Game 79 (PVFLU vs. cowchess) PVFLU's partner won} 1-0
{Game 98 (ruffe vs. Goober) Game drawn by mutual agreement} 1/2-1/2
{Game 71 (Kevorkian vs. Zryvik) Zryvik resigns} 1-0
{Game 5 (LukasG vs. Kuvasz) LukasG resigns} 0-1
{Game 96 (badcoffee vs. Ferox) Ferox forfeits on time} 1-0
{Game 70 (EdwardBloom vs. danguy) EdwardBloom lost connection; game adjourned} *{Game 23 (Sordid vs. tone) tone wins by losing all material} 0-1
{Game 44 (franky vs. FoxyFiddler) Game aborted on move 1} *
{Game 94 (Broussy vs. Chagoyo) Chagoyo lost connection; game adjourned} *
{Game 16 (OJSimpson vs. DynamoK) DynamoK ran out of time and OJSimpson has no material to mate} 1/2-1/2
{Game 32 (Axxe vs. Gianmarco) Game courtesyadjourned by Gianmarco} *
{Game 49 (Snaps vs. Mickster) Mickster wins by losing all material} 0-1
{Game 113 (reuss vs. CoolLogic) reuss lost connection and too few moves; game aborted} *
{Game 42 (Svag vs. SapceBoy) Game drawn by repetition} 1/2-1/2
{Game 70 (superwillempje vs. gsi) Game drawn because both players ran out of time} 1/2-1/2
{Game 81 (dezi vs. DafLeSauvageon) Neither player has mating material} 1/2-1/2
{Game 57 (ChessEcstacy vs. Pulcinella) ChessEcstacy wins by having less material (stalemate)} 1-0
{Game 74 (MGotel vs. MOUKARI) Game aborted on move 1} *
TRCSPOT ran out of time and leonz has no material to mate} 1/2-1/2
*/
      pattern = Pattern.compile("^:?("               //beginning
                                    + "\\{Game\\s"
                                    + "(\\d+)"               //game number
                                    + "\\s\\("
                                    + REGEX_handle           //white
                                    + "\\svs\\.\\s"
                                    + REGEX_handle           //black
                                    + "\\)\\s"
                                    + "([^}]+)"              //description
                                    + "\\}\\s"
                                    + "(\\S+)"               //results
                                    + ")"                    //end match
          , Pattern.MULTILINE);
   }

   //instance/////////////////////////////////////////////////////////////
   public FICSGameResultEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSGameResultEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      //ICSEvent
      detectFake(m.group(0));

      white = m.group(3);
      black = m.group(4);

      setMessage(m.group(5));
      result = new ICSResult(m.group(6));

      //numbers
      try {
	 boardNumber = Integer.parseInt(m.group(2));
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("ICSGameResultEvent"
	   + " threw NumberFormatException"
	   + "for: " + m.group(0));
	 setEventType(ICSEvent.UNKNOWN_EVENT);
	 setMessage(m.group(0));
      }
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSGameResultEvent(server, m);
      else 
         return null;
   }

   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
      StringBuffer sb = new StringBuffer(80);
      sb.append("Game Result(" + getBoardNumber() + "): ")
        .append(getWhitePlayer())
	.append(" vs. ")
	.append(getBlackPlayer())
	.append(" " + getResult());
      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
   public String getNative () { return null; }
}

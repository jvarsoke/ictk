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

package ictk.boardgame.chess.net.ics.event;
import ictk.boardgame.chess.net.ics.*;

import java.util.regex.*;
import java.io.IOException;

public class ICSBoardUpdateStyle12Event extends ICSBoardUpdateEvent {
   public static final Pattern pattern;

   static {
/**Style 12: <12> rnbqkb-r pppppppp -----n-- -------- ----P--- -------- PPPPKPPP RNBQ-BNR B -1 0 0 1 1 0 7 Newton Einstein 1 2 12 39 39 119 122 2 K/e1-e2 (0:06) Ke2 0 1 123

//with iset ms 1  (time in milliseconds)
<12> r-bq-rk- pp-nppbp -----np- --pp---- ---P---- --PBPN-- PP-N-PPP R-BQR-K- B -1 0 0 0 0 2 2 bbbb Simpan -2 3 0 39 39 177810 171259 8 R/f1-e1 (0:00.000) Re1 0 0 0
 **/
      pattern = Pattern.compile("^:?(<12>\\s"          //beginning
                                    + "([rnbqkpRNBQKP-]{8})" //8th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //7th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //6th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //5th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //4th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //3th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //2th rank
                                    + "\\s"
                                    + "([rnbqkpRNBQKP-]{8})" //1th rank
                                    + "\\s"
                                    + "([BW])"               //who's move
                                    + "\\s"
                                    + "(-?[0-7])"             //enpassant file
                                    + "\\s"
                                    + "([01])"               //white O-O?
                                    + "\\s"
                                    + "([01])"               //white O-O-O?
                                    + "\\s"
                                    + "([01])"               //black O-O?
                                    + "\\s"
                                    + "([01])"               //black O-O-O?
                                    + "\\s"
                                    + "(\\d+)"                //moves since irreversable
                                    + "\\s"
                                    + "(\\d+)"                //game number
                                    + "\\s"
                                    + ICSEvent.REGEX_handle   //white player
                                    + "\\s"
                                    + ICSEvent.REGEX_handle   //black player
                                    + "\\s"
                                    + "([-]?[0-3])"           //relation to game
                                    + "\\s"
                                    + "(\\d+)"                //initial time
                                    + "\\s"
                                    + "(\\d+)"                //incr
                                    + "\\s"
                                    + "(\\d+)"                //white's material
                                    + "\\s"
                                    + "(\\d+)"                //black's material
                                    + "\\s"
                                    + "(\\d+)"                //white's time
                                    + "\\s"
                                    + "(\\d+)"                //black's time
                                    + "\\s"
                                    + "(\\d+)"                //move number
                                    + "\\s"
                                    + "(\\S+)"                //last move
                                    + "\\s"
                                    + "\\((\\d+)"      //last move minutes
				    + ":(\\d+)"        //          seconds
				    + "\\.?(\\d+)?\\)" //    milli-seconds
                                    + "\\s"
                                    + "(\\S+)"                //SAN last move
                                    + "\\s"
                                    + "([01])"                //flip board?
                                    + "\\s"
                                    + "([01])"                //clock moving
                                    + "\\s"
                                    + "(\\d+)"                //timeseal delta
                                    + ")"                     //end match
          , Pattern.MULTILINE);
        //  , Pattern.MULTILINE | Pattern.DOTALL);
   }

   //instance/////////////////////////////////////////////////////////////
   public ICSBoardUpdateStyle12Event (ICSProtocolHandler server) {
      super(server, BOARD_UPDATE_EVENT);
   }

   public ICSBoardUpdateStyle12Event (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      //ICSEvent
      detectFake(m.group(0));

      int r = 7;
      int gr = 2;
      while (r < 8 && r >= 0) {
         parseRank(r--, m.group(gr++));
      }

      if (m.group(10).charAt(0) == 'W')
         isBlackMove = true;

      white = m.group(18);
      black = m.group(19);

      canWhiteCastleOO = (m.group(12).charAt(0) == '1');
      canWhiteCastleOOO = (m.group(13).charAt(0) == '1');
      canBlackCastleOO = (m.group(14).charAt(0) == '1');
      canBlackCastleOOO = (m.group(15).charAt(0) == '1');
      verboseMove = m.group(28);
      sanMove = m.group(32);
      flipBoard = (m.group(33).charAt(0) == '1');
      clockMoving = (m.group(34).charAt(0) == '1');

      //numbers
      int i = 0;
      try {
         enpassantFile = Integer.parseInt(m.group(i = 11)); //-1 no enpassant
	 irreversable = Integer.parseInt(m.group(i = 16)); //100-ply rule
	 boardNumber = Integer.parseInt(m.group(i = 17));
	 relation = Integer.parseInt(m.group(i=20));
	 itime = Integer.parseInt(m.group(i=21));
	 incr = Integer.parseInt(m.group(i=22));
	 whiteMaterial = Integer.parseInt(m.group(i=23));
	 blackMaterial = Integer.parseInt(m.group(i=24));
	 whiteClock = Integer.parseInt(m.group(i=25));
	 blackClock = Integer.parseInt(m.group(i=26));
	 moveNumber = Integer.parseInt(m.group(i=27));

	 moveTime = Integer.parseInt(m.group(i=29)) * 60
	          + Integer.parseInt(m.group(i=30));
	 timesealDelta = Integer.parseInt(m.group(i=35));
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("ICSBoardUpdateStyle12Event"
	   + " threw NumberFormatException"
	   + "for(" + i + "): " + m.group(i));
	 setEventType(ICSEvent.UNKNOWN_EVENT);
	 setMessage(m.group(0));
      }
   }

   /**parse a rank line from Syle12 */
   protected void parseRank (int rank, String s) {
      for (int file=0; file < 8; file++) {
         if (s.charAt(file) != '-')
	    board[file][rank] = s.charAt(file);
	 else
	    board[file][rank] = ' ';
      }
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new ICSBoardUpdateStyle12Event(server, m);
      else 
         return null;
   }

//FIXME: obviously this needs to be implemented
   public String getNative () { return null; }

}

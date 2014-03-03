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

package ictk.boardgame.chess;

import java.util.Calendar;
import java.util.Properties;
import ictk.boardgame.GameInfo;
import ictk.boardgame.Result;
import ictk.boardgame.Player;
import ictk.util.Log;
import java.util.Enumeration;

/* ChessGameInfo ************************************************************/
/** This is a data class that contains player's, setting, date etc
 */
public class ChessGameInfo extends GameInfo {
     /** used as a mask for Log.debug() */
   public static final long DEBUG = Log.GameInfo;

   protected String eco;

     /** rating at time of the game */
   protected int    whiteRating,
     /** rating at time of the game */
		    blackRating,
     /** initial time control in seconds */
		    timeControlInitial,
     /** increment for the time control in seconds */
		    timeControlIncrement;

   public ChessGameInfo () {
      players = new Player[2];
      result = new ChessResult(ChessResult.UNDECIDED);
   }

   public ChessGameInfo (ChessPlayer _white, ChessPlayer _black) {
      players = new Player[2];
      players[0] = _white;
      players[1] = _black;
   }

   //Accessors/////////////////////////////////////////////////////////////
   public ChessPlayer getWhite ()    { return (ChessPlayer) players[0]; }
   public ChessPlayer getBlack ()    { return (ChessPlayer) players[1]; }
   public int    getTimeControlInitial () { return timeControlInitial; }
   public int    getTimeControlIncrement () { return timeControlIncrement; }
   public int    getWhiteRating () { return whiteRating; }
   public int    getBlackRating () { return blackRating; }
   public String getECO ()      { return eco; }

   public Result getResult ()   { return result; }

   //Mutators/////////////////////////////////////////////////////////////
   public void setWhite (ChessPlayer w)        { players[0] = w; }
   public void setBlack (ChessPlayer b)        { players[1] = b; }
   public void setTimeControlInitial (int i) { timeControlInitial = i; }
   public void setTimeControlIncrement (int i) { timeControlIncrement = i; }
   public void setWhiteRating (int rating) { whiteRating = rating; }
   public void setBlackRating (int rating) { blackRating = rating; }
   public void setECO (String eco)        { this.eco = eco; }

   /* toString ***********************************************************/
   /** only used for debugging.
    */
   public String toString () {
      String tmp = null;

         tmp = "[White: " + players[0] + "]\n"
	     + "[Black: " + players[1] + "]\n"
	     + "[Event: " + event + "]\n"
	     + "[Site:  " + site  + "]\n"
	     + "[Date:  " + getDateString() + "]\n"
	     + "[Round: " + round + "]\n"
	     + "[SubRound: " + subround + "]\n"
	     + "[TimeControlInitial: " + timeControlInitial + "]\n"
	     + "[TimeControlIncrement: " + timeControlIncrement + "]\n"
	     + "[Result: " + result + "]\n"
	     + "[WhiteElo: " + whiteRating + "]\n"
	     + "[BlackElo: " + blackRating + "]\n"
	     + "[ECO: " + eco + "]";
	 return tmp;
   }

   /* equals ***************************************************************/
   /** the test for equality against all data in the object
    */
   public boolean equals (Object obj) {
      if (this == obj) return true;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;

      ChessGameInfo gi = (ChessGameInfo) obj;
      boolean t = true;
         if (Log.debug) Log.debug(DEBUG, "checking for equality");
	 t = t && super.equals(obj);

	 if (t) {
	    t = t && timeControlInitial == gi.timeControlInitial;
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "tcInit: " + timeControlInitial
		  + " / " + gi.timeControlInitial);
	 }

	 if (t) {
	    t = t && timeControlIncrement == gi.timeControlIncrement;
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "tcIncr: " + timeControlIncrement
		  + " / " + gi.timeControlIncrement);
	 }

	 if (t) {
	    t = t 
	        && ((result == null && gi.result == null)
		   || (result != null && result.equals(gi.result)));
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "result: " + result
		  + " / " + gi.result);
	 }

	 if (t) {
	    t = t && whiteRating == gi.whiteRating;
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "whiteRating: " + whiteRating
		  + " / " + gi.whiteRating);
	 }

	 if (t) {
	    t = t && blackRating == gi.blackRating;
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "blackRating: " + blackRating
		  + " / " + gi.blackRating);
	 }

	 if (t) {
	    t = t 
	        && ((eco == null && gi.eco == null)
		   || (eco != null && eco.equals(gi.eco)));
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "eco: " + eco
		  + " / " + gi.eco);
	 }

	 if (t) {
	    t = t 
	        && ((props == null && gi.props == null)
		   || (props != null && props.equals(gi.props)));
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "aux: " + props
		  + " / " + gi.props);
	 }

	 if (t && Log.debug)
	    Log.debug2(DEBUG, "equal");
      return t;
   }

   /* dump *****************************************************************/
   /** diagonostic tool
    */
   public void dump () {
      StringBuffer sb = new StringBuffer();
         sb.append("##GameInfo Dump")
           .append(toString())
           .append("Aux Data:");
      if (props == null)
         sb.append("None");
      else {
         Enumeration enum = props.propertyNames();
	 String key = null;
	 while (enum.hasMoreElements()) {
	    key = (String) enum.nextElement();
	    sb.append(key).append(" = ")
	      .append(props.getProperty(key, null));
	 }
      }
   }
}

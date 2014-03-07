/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSGameInfo.java,v 1.2 2003/08/20 15:42:50 jvarsoke Exp $
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

package ictk.boardgame.chess.net.ics;

import java.util.Calendar;

/** This contians info on games such as initial time, increment and player
 *  names.
 */
public class ICSGameInfo {

   protected boolean isBlack,
   		     isRated;
   protected int index,
   		 initTime,
		 incrTime;
   protected String player,  
                    opponent,
		    endBy;
   protected ICSRating playerRating,
   		       oppRating;
   protected ICSVariant variant;
   protected ICSEco eco;
   protected ICSResult result;
   protected Calendar  date;

   public ICSGameInfo () {
   }

   public int getIndex () { return index; }
   public void setIndex (int idx) { index = idx; } 

   public String getPlayer () { return player; }
   public void setPlayer (String player) { this.player = player; }

   public String getOpponent () { return opponent; }
   public void setOpponent (String name) { opponent = name; }

   public ICSVariant getVariant () { return variant; }
   public void setVariant (ICSVariant gameType) { variant = gameType; }

   public ICSRating getPlayerRating () { return playerRating; }
   public void setPlayerRating (ICSRating rating) { playerRating = rating; }
   
   public ICSRating getOpponentRating () { return oppRating; }
   public void setOpponentRating (ICSRating rating) { oppRating = rating; }

   public ICSEco getEco () { return eco; }
   public void setEco (ICSEco eco) { this.eco = eco; }

   public Calendar getDate () { return date; }
   public void setDate (Calendar date) { this.date = date; }

   public ICSResult getResult () { return result; }
   public void setResult (ICSResult res) { result = res; }

   /** was getPlayer() black?
    */
   public boolean isBlack () { return isBlack; }
   public void setBlack (boolean t) { isBlack = t; }

   public int getInitialTime () { return initTime; }
   public void setInitialTime (int minutes) { initTime = minutes; }

   public int getIncrementTime () { return incrTime; }
   public void setIncrementTime (int seconds) { incrTime = seconds; }

   public boolean isRated () { return isRated; }
   public void setRated (boolean rated) { isRated = rated; }

   public String toString () {
      return "HISTORY: " + getPlayer() + "[" + getPlayerRating()
             + "] v " + getOpponent() + "[" + getOpponentRating() + "]";
   }


}

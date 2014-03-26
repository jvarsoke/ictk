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

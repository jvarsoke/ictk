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

public class ICSMove {

   /** is this move black's move */
   protected boolean isBlack;
   /** the Standard Algebraic Notation version of the move**/
   protected String  san;
   /** time it took for the last move in milliseconds*/
   protected int moveTime,
                 moveNumber;

   public ICSMove () {
   }

   public String getSAN () { return san; }
   public void setSAN (String move) { san = move; }

   public int getMoveTime () { return moveTime; }
   public void setMoveTime (int milliseconds) { moveTime = milliseconds; }

   public boolean isBlack () { return isBlack; }
   public void setBlack (boolean t) { isBlack = t; }

   public int getMoveNumber () { return moveNumber; }
   public void setMoveNumber (int number) { moveNumber = number; } 

   /** The format will be HH:MM:SS.mmm or MM:SS.mmm or M:SS.mmm depending
    *  on if H > 0 and if M > 9.
    */
   public String getMoveTimeAsString () {
      StringBuffer sb = new StringBuffer(7);
      int h, m, s, ms;

      h = moveTime / 3600000;
      m = (moveTime % 3600000) / 60000;
      s = (moveTime % 60000) / 1000;
      ms = moveTime % 1000;

      if (h > 1) {
         sb.append(h).append(":");
	 if (m < 10)
	    sb.append(0);
      }

      sb.append(m).append(":");
      if (s < 10)
         sb.append(0);

      sb.append(s).append(".");
      if (ms < 100)
         sb.append(0);
      if (ms < 10)
         sb.append(0);
      sb.append(ms);
      return sb.toString();
   }

   public String toString () {
      return moveNumber + ((isBlack) ? "b" : "w") + ". " + san 
         + "(" + getMoveTimeAsString() + ")";
   }
}

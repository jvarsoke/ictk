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

import ictk.util.Log;

public class ICSResult {
   public static final int UNDECIDED = 0,
   			   WHITE_WIN = 1,
			   DRAW      = 2,
			   BLACK_WIN = 3;

   protected int result;
   protected String desc;

   public ICSResult () {
      result = UNDECIDED;
   }

   public ICSResult (String s) {
      this();
      setResultCode(s);
   }

   public int getResultCode () { return result; }
   public void setResultCode (int res) { result = res; }

   public void setResultCode (String s) {
      if ("*".equals(s))
         result = UNDECIDED;
      else if ("1-0".equals(s))
         result = WHITE_WIN;
      else if ("1/2-1/2".equals(s))
         result = DRAW;
      else if ("0-1".equals(s))
         result = BLACK_WIN;
      else {
         Log.error(Log.PROG_WARNING, 
	    "ICSResult received '" + s + "' as a result.");
	 result = UNDECIDED;
      }
   }

   public String getReadableResult () {
      String s = null;

      switch (result) {
         case UNDECIDED: s = "*"; break;
	 case WHITE_WIN: s = "1-0"; break;
	 case DRAW:      s = "1/2-1/2"; break;
	 case BLACK_WIN: s = "0-1"; break;
	 default:
      }
      return s;
   }

   public String getDescription () { return desc; }
   public void setDescription (String s) { desc = s; }

   public String toString () {
      return getReadableResult();
   }


}

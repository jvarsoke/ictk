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

package ictk.boardgame.chess;

import junit.framework.*;

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;

public class ChessGameInfoTest extends TestCase {
   ChessGameInfo gi;

   public ChessGameInfoTest (String name) {
      super(name);
   }

   public void setUp () {
      gi = new ChessGameInfo();
   }

   public void tearDown () {
      gi = null;
      Log.removeMask(ChessGameInfo.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testEquality () {
      gi.add("foo", "bar");
      ChessGameInfo gi2 = new ChessGameInfo();
      gi2.add("foo", "bar");

      assertTrue(gi.equals(gi2));
      assertTrue(gi2.equals(gi));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualityNot () {
      gi.add("foo", "bar");
      ChessGameInfo gi2 = new ChessGameInfo();
      gi2.add("foo", "baz");

      assertFalse(gi.equals(gi2));
      assertFalse(gi2.equals(gi));
   }
}

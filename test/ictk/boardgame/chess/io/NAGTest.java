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

package ictk.boardgame.chess.io;

import junit.framework.*;

public class NAGTest extends TestCase {
   NAG nag;
   short[] nags;

   public NAGTest (String name) {
      super(name);
   }

   public void setUp () {
      nag = new NAG();
   }

   public void tearDown () {
      nag = null;
      nags = null;
   }

   //////////////////////////////////////////////////////////////////////
   public void testVerboseString () {
      assertTrue(nag.numberToDescription((short) 1).equals("good move"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testNumberToString () {
      assertTrue(nag.numberToString(1).equals("!"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testIsSuffixNumber () {
      assertTrue(nag.isSuffix(1)); // !
   }

   //////////////////////////////////////////////////////////////////////
   public void testIsSuffixNumberNot () {
      assertFalse(nag.isSuffix(7));
   }

   //////////////////////////////////////////////////////////////////////
   public void testIsSuffixString () {
      assertTrue(nag.isSuffix("!?"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testIsSuffixStringNot () {
      assertFalse(nag.isSuffix("N"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testIsSymbol () {
      assertTrue(nag.isSymbol(145));
   }

   //////////////////////////////////////////////////////////////////////
   public void testString () {
      assertTrue(nag.numberToString(14).equals("+="));
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringNumeric () {
      assertTrue(nag.numberToString(14, true).equals("$14"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringReverse () {
      nags = nag.stringToNumbers("$9");
      assertTrue(nags.length == 1);
      assertTrue(nags[0] == 9);
   }

   //////////////////////////////////////////////////////////////////////
   public void testSymbolToNumber () {
      assertTrue(nag.symbolToNumber("N") == 146);
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToNumbers () {
      nags = nag.stringToNumbers("! +=");
      assertTrue(nags.length == 2);
      assertTrue(nags[0] == 1);
      assertTrue(nags[1] == 14);
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToNumbersOne () {
      nags = nag.stringToNumbers("$5");
      assertTrue(nags.length == 1);
      assertTrue(nags[0] == 5);
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToNumbersEmpty () {
      nags = nag.stringToNumbers("");
      assertTrue(nags == null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToNumbersJunk () {
      nags = nag.stringToNumbers("fjdkslfj32n23jdnj 3jerk32 jrker");
      assertTrue(nags == null);
   }
}

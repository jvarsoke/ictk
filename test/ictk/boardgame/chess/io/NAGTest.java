/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: NAGTest.java,v 1.3 2003/07/28 16:17:10 jvarsoke Exp $
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

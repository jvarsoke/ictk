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
      assertTrue(nag.shortToDescription((short) 1).equals("good move"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testSuffixString () {
      assertTrue(nag.shortToSuffix((short) 1).equals("!"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testString () {
      assertTrue(nag.shortToString((short) 177).equals("$177"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testSuffixToShort () {
      assertTrue(nag.suffixToShort("N") == 146);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShortArray () {
      nags = nag.stringToShortArray("! +=");
      assertTrue(nags.length == 2);
      assertTrue(nags[0] == 1);
      assertTrue(nags[1] == 14);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShortArray2 () {
      nags = nag.stringToShortArray("$5");
      assertTrue(nags.length == 1);
      assertTrue(nags[0] == 5);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShortArray3 () {
      nags = nag.stringToShortArray("");
      assertTrue(nags == null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShortArray4 () {
      nags = nag.stringToShortArray("fjdkslfj32n23jdnj 3jerk32 jrker");
      assertTrue(nags == null);
   }
}

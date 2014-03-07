/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessGameInfoTest.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

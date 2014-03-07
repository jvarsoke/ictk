/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: AllTests.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

public class AllTests {

   public static void main(String[] args) {
      junit.textui.TestRunner.run(suite());
   }
	
   public static Test suite() {
      TestSuite suite= new TestSuite("Chess Test");
      suite.addTest(new TestSuite(ChessGameTest.class));
      suite.addTest(new TestSuite(ChessGameInfoTest.class));
      suite.addTest(new TestSuite(ChessBoardTest.class));
      suite.addTest(new TestSuite(ChessMoveTest.class));
      suite.addTest(new TestSuite(PawnTest.class));
      suite.addTest(new TestSuite(KnightTest.class));
      suite.addTest(new TestSuite(BishopTest.class));
      suite.addTest(new TestSuite(RookTest.class));
      suite.addTest(new TestSuite(QueenTest.class));
      suite.addTest(new TestSuite(KingTest.class));
      return suite;
   }
}

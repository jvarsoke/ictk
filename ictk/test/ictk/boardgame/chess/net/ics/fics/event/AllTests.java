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

package ictk.boardgame.chess.net.ics.fics.event;

import junit.framework.*;

public class AllTests {

   public static void main(String[] args) {
      junit.textui.TestRunner.run(suite());
   }

   public static Test suite() {
      TestSuite suite= new TestSuite("FICS event parsers");

      suite.addTest(new TestSuite(FICSKibitzParserTest.class));
      suite.addTest(new TestSuite(FICSChannelParserTest.class));
      suite.addTest(new TestSuite(FICSGameCreatedParserTest.class));
      suite.addTest(new TestSuite(FICSGameNotificationParserTest.class));
      suite.addTest(new TestSuite(FICSGameResultParserTest.class));
      suite.addTest(new TestSuite(FICSPlayerConnectionParserTest.class));
      suite.addTest(new TestSuite(FICSPlayerNotificationParserTest.class));
      suite.addTest(new TestSuite(FICSSeekAdParserTest.class));
      suite.addTest(new TestSuite(FICSSeekAdReadableParserTest.class));
      suite.addTest(new TestSuite(FICSSeekClearParserTest.class));
      suite.addTest(new TestSuite(FICSSeekRemoveParserTest.class));
      suite.addTest(new TestSuite(FICSShoutParserTest.class));
      suite.addTest(new TestSuite(FICSTellParserTest.class));
      suite.addTest(new TestSuite(FICSMoveListParserTest.class));
      suite.addTest(new TestSuite(FICSBoardUpdateStyle12ParserTest.class));

      return suite;
   }
}

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
import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;

import junit.framework.*;

public class FICSChannelEventParserTest extends ParserTest {
   ICSChannelEvent evt;

   public FICSChannelEventParserTest () throws IOException {
      super("ictk.boardgame.chess.net.ics.fics.event");
   }

   public void setUp () {
      parser = FICSChannelEventParser.getInstance();
      //debug = true;
   }

   public void tearDown () {
      evt = null;
      parser = null;
   }

   //////////////////////////////////////////////////////////////////////
   public void testMessage0 () {
      //debug = true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {

         evt = (ICSChannelEvent) parser.createICSEvent(mesg[0]);
	 assertTrue(evt != null);

         //begin test
	 assertTrue("Gorgonian".equals(evt.getPlayer()));
	 assertTrue(evt.getChannel() == 50);
	 assertTrue("da".equals(evt.getMessage()));
	 //end test
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testMessage1 () {
      //debug = true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {

         evt = (ICSChannelEvent) parser.createICSEvent(mesg[1]);
	 assertTrue(evt != null);

         //begin test
	 assertTrue(evt.getAccountType().is(ICSAccountType.CHESS_ADVISOR));
         //end test

      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testMessage3 () {
      //debug = true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {

         evt = (ICSChannelEvent) parser.createICSEvent(mesg[3]);
	 assertTrue(evt != null);

         //begin test
	 assertTrue(evt.getAccountType().is(ICSAccountType.ADMIN));
	 assertTrue(evt.getAccountType().is(ICSAccountType.SERVICE_REP));
	 assertTrue(evt.getAccountType().is(ICSAccountType.CHESS_ADVISOR));
	 assertTrue(evt.getAccountType().is(ICSAccountType.TOURNAMENT_MANAGER));
	 assertTrue(evt.getChannel() == 49);
         //end test
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testMessage4Tournament () {
      //debug = true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {

         evt = (ICSChannelEvent) parser.createICSEvent(mesg[4]);
	 assertTrue(evt != null);

         //begin test
	 assertTrue(evt.getEventType() == ICSEvent.TOURNAMENT_CHANNEL_EVENT);
	 assertTrue(evt.getChannel() == 50);
         //end test
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }

   //inherited///////////////////////////////////////////////////////////
   public void testParseAll () {
      //debug=true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {
         super.testParseAll();
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testNative () {
      //debug=true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {
         super.testNative();
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }
}

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

package ictk.boardgame.chess.net.ics.event;

import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;

import junit.framework.*;

public class ICSEventParserTest extends TestCase {
   Pattern 
      phandle,
      pacct,
      prating,
      pmesg,
      pdate;
   Matcher m;
   ICSAccountType acct;
   ICSRating rating;
   String mesg;
   ICSEventParser eventParser;

   public ICSEventParserTest () throws IOException {
   }

   public void setUp () {
      phandle = Pattern.compile(ICSEventParser.REGEX_handle);
      pacct = Pattern.compile(ICSEventParser.REGEX_acct_type);
      prating = Pattern.compile(ICSEventParser.REGEX_rating);
      pmesg = Pattern.compile(ICSEventParser.REGEX_mesg);
      pdate = Pattern.compile(ICSEventParser.REGEX_date);

      eventParser = new ICSEventParser(null) {
         public void assignMatches (Matcher m, ICSEvent evt) {}
	 public ICSEvent createICSEvent (Matcher match) { return null; }
         public String toNative (ICSEvent evt) { return null; }
      };
   }

   public void tearDown () {
      phandle = pacct = prating = pmesg = pdate = null;
      eventParser = null;
      m = null;
      acct = null;
      rating = null;
      mesg = null;
   }

   //tests///////////////////////////////////////////////////////////////
   public void testAcctType0 () {
      m = pacct.matcher("(SR)"); 
      m.find();
      //Log.debug(0L, "testAcctType0", m);
      assertTrue(m.group(1).equals("(SR)"));
      acct = eventParser.parseICSAccountType(m, 1);
      assertTrue(acct.is(ICSAccountType.SERVICE_REP));
   }

   //////////////////////////////////////////////////////////////////////
   public void testAcctType1 () {
      m = pacct.matcher("(*)"); 
      m.find();
      //Log.debug(0L, "testAcctType1", m);
      assertTrue(m.group(1).equals("(*)"));
      acct = eventParser.parseICSAccountType(m, 1);
      assertTrue(acct.is(ICSAccountType.ADMIN));
   }

   //////////////////////////////////////////////////////////////////////
   public void testAcctType2 () {
      m = pacct.matcher("(*)(SR)"); 
      m.find();
      //Log.debug(0L, "testAcctType2", m);
      assertTrue(m.group(1).equals("(*)(SR)"));
      acct = eventParser.parseICSAccountType(m, 1);
      assertTrue(acct.is(ICSAccountType.ADMIN));
      assertTrue(acct.is(ICSAccountType.SERVICE_REP));
   }

   //////////////////////////////////////////////////////////////////////
   public void testAcctType3 () {
      m = pacct.matcher("(*)(SR)(CA)(TM)"); 
      m.find();
      //Log.debug(0L, "testAcctType3", m);
      acct = eventParser.parseICSAccountType(m, 1);
      assertTrue(acct.is(ICSAccountType.ADMIN));
      assertTrue(acct.is(ICSAccountType.SERVICE_REP));
      assertTrue(acct.is(ICSAccountType.CHESS_ADVISOR));
      assertTrue(acct.is(ICSAccountType.TOURNAMENT_MANAGER));
   }
}

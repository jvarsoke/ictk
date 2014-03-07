/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ParserTest.java,v 1.5 2003/08/24 05:44:49 jvarsoke Exp $
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

import java.util.*;
import java.util.regex.*;
import java.io.*;

import junit.framework.*;

public class ParserTest extends TestCase {
      /** for seeing the native i/o */
   public boolean debug;
   public String[] mesg;
   String filename;
   public ICSEventParser parser;
   String dataDir = "./data";

   public ParserTest (String packageName) throws IOException {

      String sysprop = packageName + ".dataDir";
      filename = this.getClass().getName();

      if (System.getProperty(sysprop) != null)
         dataDir = System.getProperty(sysprop);


      filename = filename.substring(filename.lastIndexOf('.') +1, 
                                    filename.length()) + ".data";
      filename = dataDir + "/" + filename;
      mesg = processFile(new File(filename));
   }

   public void setUp () {
   }

   public void tearDown () {
      parser = null;
   }


   public static String[] processFile (File file) throws IOException {
      String[] mesg;
      List list = new LinkedList();

      StringBuffer sb = new StringBuffer(80);
      String line = null;
      BufferedReader in = new BufferedReader(new FileReader(file));

      int lines = 0;
      while ((line = in.readLine()) != null) {
         if (line.startsWith("#")) {
	    if (lines != 0) {
	       list.add(sb.toString());
	       sb = new StringBuffer(80);
	       lines = 0;
	    }
	    //otherwise just skip it
	 }
	 else {
	    sb.append(line).append("\n");
	    lines++;
	 }
      }
      in.close();
      //get last bit of data
      if (lines != 0) {
	 list.add(sb.toString());
      }

      mesg = new String[list.size()];
      for (int i=0; i < list.size(); i++) {
         mesg[i] = (String) list.get(i);
      }
      return mesg;
   }

   //tests/////////////////////////////////////////////////////////////////
   

   //////////////////////////////////////////////////////////////////////////
   /** make sure we can parse all the data without errors
    */
   public void testParseAll () {
      if (debug) {
         //Log.addMask(ICSEventParser.DEBUG);
         //parser.setDebug(true);
      }

      ICSEvent evt = null;
      try {
	 for (int i=0; i < mesg.length; i++) {
	     evt = parser.createICSEvent(mesg[i]); 
	     if (debug && evt == null) {
	        System.err.println("Couldn't match: " + mesg[i]);
	     }
	     assertTrue(evt != null);
	 }
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
         parser.setDebug(false);
      }
   }

   //////////////////////////////////////////////////////////////////////////
   /** make sure that the toNative() function works and gives us exactly
    *  what we started with.
    */
   public void testNative () {
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }

      try { 
	 ICSEvent evt = null;
	 String nativeStr = null;
	 for (int i=0; i < mesg.length; i++) {
	     evt = parser.createICSEvent(mesg[i]); 
	     assertTrue(evt != null);
	     nativeStr = parser.toNative(evt) + "\n";

             if (debug && !nativeStr.equals(mesg[i])) {
		System.out.println("origin[" +i + "]: <<|" + mesg[i] + "|>>");
		System.out.println("native[" +i + "]: <<|" + nativeStr + "|>>");
	     }
	     assertTrue(nativeStr.equals(mesg[i]));
	 }
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
         parser.setDebug(false);
      }
   }

   //tests-end/////////////////////////////////////////////////////////////

}

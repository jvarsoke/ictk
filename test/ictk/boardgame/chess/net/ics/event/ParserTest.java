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

package ictk.boardgame.chess.net.ics.event;

import ictk.util.Log;

import java.util.*;
import java.io.*;

import junit.framework.*;

public abstract class ParserTest extends TestCase {
      /** for seeing the native i/o */
   public boolean debug;
   public String[] mesg;
   String filename;
   public ICSEventParser parser;
   String dataDir = "./data";

   public ParserTest (String packageName) throws IOException {
      mesg = processResource();
   }

   protected BufferedReader getReaderFromResource(String file) {
      BufferedReader reader;
      file = "data/" + file;
      try {
         System.out.println("class: " + this.getClass());
         reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file)));
      }
      catch (RuntimeException e) {
         System.out.println("Failed to load + '" + file + "'.");
         throw new RuntimeException("Failed to load + '" + file + "'.", e);
      }
      return reader;
   }
   
   public void setUp () {
   }

   public void tearDown () {
      parser = null;
   }


   private String[] processResource () throws IOException {
      String[] mesg;
      List<String> list = new LinkedList<>();

      StringBuffer sb = new StringBuffer(80);
      String line = null;
      
      filename = this.getClass().getName();
      filename = filename.substring(filename.lastIndexOf('.') +1, 
                                    filename.length()) + ".data";

      BufferedReader in = getReaderFromResource(filename);

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
	     assertEquals(mesg[i], nativeStr);
	 }
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
         parser.setDebug(false);
      }
   }

   //tests-end/////////////////////////////////////////////////////////////

}

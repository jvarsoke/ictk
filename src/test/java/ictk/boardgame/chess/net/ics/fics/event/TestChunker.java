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

package ictk.boardgame.chess.net.ics.fics.event;
import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.regex.*;
import java.util.*;
import java.io.*;

import junit.framework.*;

/** this class reads one of the data test files and fills an array with each
 *  of the messages.  This is to simulate the input from the server, which
 *  is chunked by prompt.
 */
public class TestChunker {

   public static String[] processFile (File file) throws IOException {
      String[] mesg;
      List<String> list = new LinkedList<>();

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
	    sb.append(line);
	    lines++;
	 }
      }
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
}

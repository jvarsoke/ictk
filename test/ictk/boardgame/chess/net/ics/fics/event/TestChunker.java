/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: TestChunker.java,v 1.1 2003/05/13 15:38:58 jvarsoke Exp $
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

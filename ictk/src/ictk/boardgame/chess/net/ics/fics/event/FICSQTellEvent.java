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

import java.util.regex.*;
import java.io.IOException;


public class FICSQTellEvent extends ICSQTellEvent {

   //static initializer/////////////////////////////////////////////////////
   public static final int QTELL_EVENT =  ICSEvent.QTELL_EVENT;

   public static final Pattern pattern;

   static {
      pattern  = Pattern.compile("^(:"                  //begin full match
                                  + "((.|\\s+:)*)"      //message
				  + ")"                 //end
          , Pattern.MULTILINE);
   }

   //instance vars//////////////////////////////////////////////////////////

   //constructors///////////////////////////////////////////////////////////
   public FICSQTellEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSQTellEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   //ICSEvent/////////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;
      if ((m = matches(s)) != null) 
         return new FICSQTellEvent(server, m);
      else
         return null;
   }

   //ICSMessage///////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   //ICSKibitz////////////////////////////////////////////////////////////////
   protected void assignMatches (Matcher m) {
      setMessage(m.group(1));
   }

   public String toString () {
      return getMessage();
   }
   public String getNative () { return null; }
}



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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ICSEvent {
   public static final String REGEX_handle    = "([\\w]+)",
   			      REGEX_acct_type = "(\\(\\S*\\))?",
			      REGEX_rating    = "\\(\\s*([0-9+-]+[EP]?)\\)",
			      REGEX_mesg      = "((.|\\s+\\\\|\\s+:)*)",
			      /*Sun Nov  3, 21:11 CET 2002*/
			      REGEX_date      = "((\\w{3})\\s(\\w{3})\\s+"
			                      + "(\\d+),\\s(\\d+):(\\d{2})\\s"
					      + "(\\w+)\\s(\\d{4}))";

   public static final int    
                              UNKNOWN_EVENT                =  0,
			      BOARD_UPDATE_EVENT           =  1,
			      CHANNEL_EVENT                =  2,
			      SHOUT_EVENT                  =  3,
   			      SEEK_AD_EVENT                =  4,
			      SEEK_REMOVE_EVENT            =  5,
			      TELL_EVENT                   =  6,
			      SAY_EVENT                    =  7,
			      KIBITZ_EVENT                 =  8,
			      WHISPER_EVENT                =  9,
			      SHOUT_EMOTE_EVENT            = 10,
			      CSHOUT_EVENT                 = 11,
			      SSHOUT_EVENT                 = 12,
			      TSHOUT_EVENT                 = 13,
			      QTELL_EVENT                  = 14,
			      AUTO_SALUTE_EVENT            = 15,
			      MOVE_LIST_EVENT              = 16,
			      GAME_RESULT_EVENT            = 17,
			      MATCH_REQUEST_EVENT          = 18,
			      TAKEBACK_REQUEST_EVENT       = 19,
			      PLAYER_NOTIFICATION_EVENT    = 20,
			      GAME_NOTIFICATION_EVENT      = 21,
			      AVAIL_INFO_EVENT             = 22,
			      USER_DEFINED_EVENT           = 23,
   			      SEEK_AD_READABLE_EVENT       = 24,
			      SEEK_REMOVE_READABLE_EVENT   = 25,
			      BOARD_SAY_EVENT              = 26, 
			      SEEK_CLEAR_EVENT             = 27,
			      PLAYER_CONNECTION_EVENT      = 28,
			      GAME_CREATED_EVENT           = 29,
			      HISTORY_EVENT                = 30;

   protected int eventType = UNKNOWN_EVENT;
   protected String message;
   protected ICSProtocolHandler server;
   protected boolean isFake;

   public ICSEvent (ICSProtocolHandler server, int eventType) {
      this.server = server;
      this.eventType = eventType;
   }

   /** returns the server that originated this message */
   public ICSProtocolHandler getServer () {
      return server;
   }

   public void setServer (ICSProtocolHandler server) {
      this.server = server;
   }

   /** returns the type of the object (for easy casting)*/
   public int getEventType () {
      return eventType;
   }

   public void setEventType (int type) {
      eventType = type;
   }

   public void setMessage (String mesg) {
      message = mesg;
   }

   public String getMessage () {
      return message;
   }

   abstract public Pattern getPattern ();

   public Matcher matches (CharSequence s) {
      Matcher m = null;
      
      m = getPattern().matcher(s);
      if (m.find())
         return m;
      else
         return null;
   }

   /**is this a QTell in disquise?
    */
   public void setFake (boolean t) {  isFake = t; }
   public boolean isFake () { return isFake; }

   protected void detectFake (String s) {
      if (s.charAt(0) == ':')
         isFake = true;
   }
   abstract protected void assignMatches (Matcher m);

   /** Parses the CharSequence and returns the correct ICSEvent
    *  that it corrisponds to.  Returns Null if the CharSequence
    *  doesn't match this event.
    */
   abstract public ICSEvent newICSEventInstance (CharSequence s);

   /** Returns a string of the native output from the server for this
    *  event.
    */
   abstract public String getNative ();

   public String toString () {
      return message;
   }

}

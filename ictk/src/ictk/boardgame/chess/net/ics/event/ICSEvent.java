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

import ictk.util.Log;
import ictk.boardgame.chess.net.ics.*;

import java.util.Date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ICSEvent {
   public static final int    
                              UNKNOWN_EVENT                =  0,
			      BOARD_UPDATE_EVENT           =  1,
			      CHANNEL_EVENT                =  2,

   			      SEEK_AD_EVENT                =  4,
			      SEEK_REMOVE_EVENT            =  5,
			      TELL_EVENT                   =  6,
			      SAY_EVENT                    =  7,
			      KIBITZ_EVENT                 =  8,
			      WHISPER_EVENT                =  9,

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
			      HISTORY_EVENT                = 30,
			      NUM_EVENT			   = 31;

      /** each event has a type for easy casting */
   protected int eventType = UNKNOWN_EVENT;

      /** This is the parser for particular ICS messages.  This way
       ** FICS parsers and ICC parsers can be used by the same ICSEvent
       ** objects. */
   protected ICSEventParser eventParser;

      /** this is the server the ICSEvent originally came from */
   protected ICSProtocolHandler server;

      /** some servers have a way of sending events from non-server
       ** origins, such as QTells, where the event might look like
       ** a tell, but is proceeded by a ":" because it's actually
       ** generated from a bot. */
   protected boolean isFake;

      /** this is the timestamp of when the message was received by
       ** the ICTK program.  This date might slightly differ from when
       ** the server thought it sent the event, as transmission time
       ** is involved. */
   protected Date timestamp;


      /** this is used by many of the events for a human readable 
       ** message of some kind.  It also doubles as a repository for
       ** the parsing errors that produce UNKNONW_EVENTs. */
   protected String message;

      /** this is the original data that was parsed to create this 
       ** message.  This is only valid if Log.debug() is true. */
   protected String original;

   public ICSEvent (ICSProtocolHandler server, int eventType) {
      this.server = server;
      this.eventType = eventType;
      this.timestamp = new Date();
   }

   public ICSEvent (int eventType) {
      this.eventType = eventType;
      this.timestamp = new Date();
   }

   /* getServer *************************************************************/
   /** returns the server that originated this message 
    */
   public ICSProtocolHandler getServer () {
      return server;
   }

   /* setServer *************************************************************/
   /** sets the server this event originally came from
    */
   public void setServer (ICSProtocolHandler server) {
      this.server = server;
   }

   /* getEventType **********************************************************/
   /** returns the type of the object (for easy casting)
    */
   public int getEventType () {
      return eventType;
   }

   /* setEventType **********************************************************/
   /** sets the type of event this is.
    */
   public void setEventType (int type) {
      eventType = type;
   }

   /* getTimestamp **********************************************************/
   /** this is the moment the event was received from the server.
    */
   public Date getTimestamp () {
      return timestamp;
   }

   /* setTimestamp **********************************************************/
   /** sets the timestamp to the value specified.
    */
   public void setTimestamp (Date timestamp) {
      this.timestamp = timestamp;
   }

   /* getMessage ************************************************************/
   /** returns a non-parseable string associated with this message, or if
    *  an error in the parsing as occured setting the message type to
    *  UNKNOWN_EVENT then this will contain the entire original event string.
    */
   public String getMessage () {
      return message;
   }

   /* setMessage ************************************************************/
   /** sets a non-parseable string associated with this message, or if
    *  an error in the parsing as occured setting the message type to
    *  UNKNOWN_EVENT then this will contain the entire original event string.
    */
   public void setMessage (String mesg) {
      message = mesg;
   }

   public ICSEventParser getEventParser () {
      return eventParser;
   }

   public void setEventParser (ICSEventParser parser) {
      this.eventParser = parser;
   }

   /* setFake ***************************************************************/
   /**is this a QTell in disquise?
    */
   public void setFake (boolean t) {  isFake = t; }

   /* isFake ****************************************************************/
   /**is this a QTell in disquise?
    */
   public boolean isFake () { return isFake; }

   /* getReadable ***********************************************************/
   /** returns a readable form of this event.  Typically this is similar
    *  to the original text sent by the server, but it does not have
    *  to be.
    */
   abstract public String getReadable ();

   /** this method stores the original message that was parsed to 
    *  create this event.  This only happens if debug mode is on
    *  (Log.debug).  If not, then no value is stored.
    */
   public void setOriginal (String s) {
      if (Log.debug)
         original = s;
   }

   /* DEBUG_getOriginal *****************************************************/
   /** this method returns the original text that was parsed to get the 
    *  the event.  This method is not valid if debug mode is not active.
    *  If this method is called and Log.debug is not true, then an
    *  IllegalStateException will be thrown.
    */
   public String DEBUG_getOriginal () {
      if (Log.debug)
         return original;
      else {
         throw new IllegalStateException (
	    "Can't ask for original if not in debug mode.");
      }
   }

   public String toString () {
      return getReadable();
   }

}

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

package ictk.boardgame.chess.net.ics.fics; 

import free.freechess.timeseal.TimesealingSocket;

import ictk.util.Log;
import ictk.boardgame.chess.net.ics.fics.event.*;
import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.ui.cli.*;
import ictk.boardgame.chess.net.*;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.regex.*;
import java.util.Date;


/* FICSProtocolHandler ********************************************************/
/** Handles logging into the FICS server and splitting the server messages
 *  into chunks.  These chunks are then examined to see if they corrispond to
 *  any of the known FICS events.  The message is then sent to the 
 *  ICSEventRouter connected to this object so it can be dispatched to
 *  an object that wishes to be notified of that event.
 */
public class FICSProtocolHandler extends ICSProtocolHandler {
     /** the socket time-out is used to see if our connection is dropped
      ** and we're not told about it. The number is in milliseconds*/
   public final static int SOCKET_TIMEOUT = 30 * 100 * 60;
   /** this is the delay between tests to see if there's any info on the 
    ** socket in nanoseconds. Setting this to 0 will peg your CPU needlessly **/
   protected int SOCKET_DELAY = 1000;

   protected String LOGIN_PROMPT   = "login:",
                    PASSWD_PROMPT  = "password:",
                    CMD_PROMPT     = "\nfics% ",
                    GUEST_PROMPT   = "Press return to enter the server as \"",
                    START_SESSION  = "**** Starting FICS session as ",
                    INVALID_PASSWD = "**** Invalid password! ****",
                    INTERFACE_NAME = 
		       "-=[ ictk ]=- v0.2 http://ictk.sourceforge.net";

   //common regex phrases
   protected final static String REGEX_handle    = "([\\w]+)",
                                 REGEX_acct_type = "(\\(\\S*\\))?",  
                                 REGEX_rating    = "\\(\\s*([0-9+-]+[EP]?)\\)";

   //NEED: RATING - UNR  is a possiblity

   //colors
   public final static char ESC = '\u001B';
   public final static String BLACK   = "[0;30",
                              RED     = "[0;31m",
			      GREEN   = "[0;32m",
			      YELLOW  = "[0;33m",
                              BLUE    = "[0;34m",
                              MAGENTA = "[0;35m",
                              CYAN    = "[0;36m",
                              WHITE   = "[0;37m",
                              BOLD_BLACK   = "[1;30m",
                              BOLD_RED     = "[1;31m",
			      BOLD_GREEN   = "[1;32m",
			      BOLD_YELLOW  = "[1;33m",
                              BOLD_BLUE    = "[1;34m",
                              BOLD_MAGENTA = "[1;35m",
                              BOLD_CYAN    = "[1;36m",
                              BOLD_WHITE   = "[1;37m",
			      PLAIN = "[0;m";

   static final protected Pattern 
                                  seekRemovedPattern,
				  autosalutePattern,
				  matchPattern,
				  takebackPattern,
				  availInfoPattern;

   final protected ICSEventParser[] eventFactories;

      /** Telnet connection to server */
//   boolean loggedIn = false;

      /** is block_mode turned on for the server protocol. */
   boolean isBlockMode = false;

      /** the max amount the server input buffer can hold before throwing 
       ** an error.  The value of this should be plenty as it really only
       ** needs to be as large as the longest message chunk.  */
   int BUFFER_SIZE = 128 * 1048;

      /** collected input yet to be processed */
   CharBuffer buffer = CharBuffer.allocate(BUFFER_SIZE);

   //constructor//////////////////////////////////////////////////////////
   static {
      //patterns
      seekRemovedPattern = Pattern.compile(
                                    "^(Ads removed:\\s"
                                  + "([\\d ]+)"          //ad numbers
				  + ")"
          , Pattern.MULTILINE);

      autosalutePattern  = Pattern.compile("^("            //begin full match
                                  + "-->\\s"
				  + REGEX_handle
				  + REGEX_acct_type
				  + ">\\s"
				  + "\\(ics-auto-salutes '"
				  + "(\\w+)\\)"            //message
				  + "((.|\\s+\\\\)*)"     //message
				  + ")"                    //end
          , Pattern.MULTILINE);

      matchPattern = Pattern.compile("^("                   //beginning
                                    + "Challenge:\\s"
				    + REGEX_handle          //challenger
				    + "\\s"
				    + REGEX_rating          //rating
				    + "\\s"
				    + "(\\[\\w*\\])?" //colors
				    + "\\s?"
				    + REGEX_handle          //challenged
				    + "\\s"
				    + REGEX_rating          //rating
				    + "\\s"
				    + "(\\w+)"              //rated/unrated
				    + "\\s"
				    + "(\\S+)"              //variant wild/8
				    + "\\s"
				    + "(\\d+)"              //init time
				    + "\\s"
				    + "(\\d+)"              //incr time
				    + "\\."
				    + ")"                   //end match
          , Pattern.MULTILINE | Pattern.DOTALL);

      takebackPattern = Pattern.compile("^("                //beginning
                                    + REGEX_handle          //player
				    + "\\swould like to take back\\s"
				    + "(\\d+)"            //number of half moves
				    + "\\shalf move\\(s\\)\\."
				    + ")"                  //end match
          , Pattern.MULTILINE | Pattern.DOTALL);

      availInfoPattern = Pattern.compile("^("		    //beginning
                                    + REGEX_handle          //player
				    + REGEX_acct_type
				    + "\\s"
				    + "(Blitz\\s?"
				    + REGEX_rating
				    + ", Std\\s?"
				    + REGEX_rating
				    + ", Wild\\s?"
				    + REGEX_rating
				    + ", Light\\s?"
				    + REGEX_rating
				    + ", Bug\\s?"
				    + REGEX_rating
				    + ")?"
				    + ".*is"
				    + "(.+)"             //"now" or "no longer"
				    + "\\savailable for matches\\."
				    + ")"
          , Pattern.MULTILINE | Pattern.DOTALL);
   }


   //constructors/////////////////////////////////////////////////////////////
   public FICSProtocolHandler () {
      host   = "64.71.131.140"; //defaults
      port   = 5000;

      int i = 0;
      //eventFactories = new ICSEventParser[16];
      eventFactories = new ICSEventParser[8];
      eventFactories[i++] = FICSTellEventParser.getInstance();
      eventFactories[i++] = FICSKibitzEventParser.getInstance();
      eventFactories[i++] = FICSChannelEventParser.getInstance();
      eventFactories[i++] = FICSShoutEventParser.getInstance();
      eventFactories[i++] = FICSGameResultEventParser.getInstance();
      eventFactories[i++] = FICSGameCreatedEventParser.getInstance();
      eventFactories[i++] = FICSPlayerConnectionEventParser.getInstance();
      eventFactories[i++] = FICSPlayerNotificationEventParser.getInstance();
      /*
      eventFactories[i++] = new ICSBoardUpdateStyle12Event(this);
      eventFactories[i++] = new FICSPlayerConnectionEventParser();
      eventFactories[i++] = new FICSGameNotificationEventParser();
      eventFactories[i++] = new FICSSeekRemoveEventParser();
      eventFactories[i++] = new FICSGameResultEventParser();
      eventFactories[i++] = new FICSGameCreatedEventParser();
      eventFactories[i++] = new FICSChannelEventParser();
      eventFactories[i++] = new FICSShoutEventParser();
      eventFactories[i++] = new FICSSeekAdReadableEventParser();
      eventFactories[i++] = new FICSKibitzEventParser();
      eventFactories[i++] = new FICSTellEventParser();
      eventFactories[i++] = new FICSSeekClearEventParser();
      eventFactories[i++] = new FICSPlayerNotificationEventParser();
      */
      /*
      eventFactories[i++] = new FICSQTellEvent(this);
      */
      router = new ICSEventRouter();
   }

   public FICSProtocolHandler (String host, int port) {
      this();
      this.host = host;
      this.port = port;
   }

   public ICSEventRouter getRouter() {
      return router;
   }

   //methods/////////////////////////////////////////////////////////////////
   public void connect () 
      throws UnknownHostException, IOException {

      if (handle == null || passwd == null)
         throw new IllegalStateException(
	    "Both handle and password must be set before login");

      if (isLagCompensated)
         socket = new TimesealingSocket(host, port);
      else
         socket = new Socket(host, port);

      //socket.setSoTimeout(SOCKET_TIMEOUT);
      try {
         socket.setKeepAlive(true);
      }
      catch (SocketException e) {
         Log.error(Log.USER_WARNING, e.getMessage());
      }

//FIXME: Should BufferedInputStream be used here?
      in = new InputStreamReader(socket.getInputStream());
      out = new PrintWriter(socket.getOutputStream());
      thread.start();
   }

   /* run *********************************************************************/
   public void run () {
       try {
	  isLoggedIn = doLogin();
	  if (isLoggedIn) {
             setLoginVars();
	     processServerOutput(); 
	  }
	  else {
	  //close socket etc
	  }
	  //end thread
	  if (socket != null && !socket.isClosed()) {}
	     //socket.close();
       }
       catch (IOException e) {
          e.printStackTrace();
       }
       System.err.println("FICSProtocolHandler: run end.");
       buffer.rewind();
       System.out.print(buffer.toString());
       System.out.flush();
       System.err.println(new Date());
   }

   /* doLogin ****************************************************************/
   protected boolean doLogin () throws IOException {
      boolean seenLogin = false,
              seenPasswd = false;
      String tmp = null;
      int b = 0;
      char c = ' ';
      int mark = 0;
      
      Matcher match = null;
	 //Successful login start
      Pattern REGEX_sessionStart = Pattern.compile(
		     "^\\*\\*\\*\\* Starting FICS session as "
		     + REGEX_handle
		     + REGEX_acct_type
		     + " \\*\\*\\*\\*"
		      , Pattern.MULTILINE);

         while ((b = in.read()) != -1) {

	    //out of range invisible characters
	    //10 is \n
	    //13 is \r ?
	    if (b!= 10 && b!=13 && (b < 32 || b > 126)) {
	    //Diagnostics
	    /*
	       String foo = "[" + b + "]";
	       for (int z=0;z<foo.length();z++)
		  buffer.put(foo.charAt(z));
	    */
	    }

	    //normal character
	    else {
	       c = (char) b;

	       if (c == '\r') {} //get rid of these
	       else 
		  buffer.put(c);

               //this was a line of text that wasn't a prompt
	       if (c == '\n' && !seenPasswd) { 
	          buffer.limit(buffer.position());
		  buffer.rewind();
		  System.out.print(buffer.toString());
		  System.out.flush();
		  buffer.clear();
	       }

               //we've hit a prompt (probably)
	       else if (c == ':') {
		  mark = buffer.position();
	          buffer.limit(mark);
		  buffer.rewind();
	          tmp = buffer.toString();

                  //login prompt
		  if (!seenLogin
		      && tmp.lastIndexOf(LOGIN_PROMPT) > -1) {
		        System.out.print(tmp);
		        System.out.print(" ");
			System.out.flush();
		        buffer.rewind();
		        buffer.clear();

		        sendCommand(handle);
			seenLogin = true;
		  }

                  //password prompt
		  else if (seenLogin && !seenPasswd 
		           && tmp.lastIndexOf(PASSWD_PROMPT) > -1) {
		        System.out.print(tmp);
		        System.out.print(" ");
			System.out.flush();
		        buffer.rewind();
		        buffer.clear();

		        sendCommand(passwd, false);
			seenPasswd = true;
                        System.out.println();
		  }

		  //guest login prompt (instead of password)
		  else if (seenLogin && !seenPasswd 
		          && tmp.lastIndexOf(GUEST_PROMPT) > -1) {
		        System.out.print(tmp);
			System.out.flush();
		        buffer.rewind();
		        buffer.clear();

		        sendCommand("");
			seenPasswd = true;
		  }
		  else {
		     buffer.limit(buffer.capacity());
		     buffer.position(mark);
		  }
	       }

               //looking for a response from the password
	       else if (c == '\n' && seenPasswd) {
		  mark = buffer.position();
	          buffer.limit(mark);
		  buffer.rewind();
	          tmp = buffer.toString();

                  //Invalid password
		  if (tmp.lastIndexOf(INVALID_PASSWD) > -1) {
		     System.out.print(tmp);
		     System.out.flush();
		     buffer.rewind();
		     buffer.clear();

		     return false;
		  }

                  //Successful Login
		  else if (tmp.lastIndexOf(START_SESSION) > -1) {
		     match = REGEX_sessionStart.matcher(tmp);
		     if (match.find()) {
		        handle = match.group(1);
			try {
			   if (match.group(2) == null)
			      acctType = new ICSAccountType();
			   else 
			      acctType = new ICSAccountType (match.group(2));
			}
			catch (IOException e) {
			   Log.error(Log.PROG_ERROR, 
			      "On Login: " + e.getMessage());
			}
		     }
		     else {
		        Log.error(Log.PROG_ERROR,
			   "On Login: never matched session start: "
			   + tmp);
		     }
		     System.out.print(tmp);
		     System.out.flush();
		     buffer.rewind();
		     buffer.clear();

		     return true;
		  }

		  else {
		     buffer.limit(buffer.capacity());
		     buffer.position(mark);
		  }
	       }
	    }
	 }
      return true;
   }

   /* setLoginVars **********************************************************/
   /** sets variables this connection will use.  Some are necessary
    *  for the correct parsing of server messages.
    */
   protected void setLoginVars () { 
      //sendCommand("iset block 1");
      //isBlockMode = true;
      sendCommand("set prompt", false);
      sendCommand("set interface " + INTERFACE_NAME, false);
      sendCommand("iset ms 1", false);
      sendCommand("set style 12", false);
      sendCommand("set bell 0", false);
   }


   /* processServerOutput **************************************************/
   /** processes output once the user is logged in
    */
   protected void processServerOutput () {
      if (isBlockMode)
         chunkByBlockMode();
      else
         chunkByPrompt();
   }

   /* chunkByPrompt ********************************************************/
   /** if we're not using BlockMode we have to guess where the message
    *  chunk begins and ends.  This method defines a server message chunk
    *  as that output between two prompts.  This mode is not as efficient
    *  as BlockMode because a regex is used to figure out which message
    *  type we're looking at, but it does work with Timeseal.
    */
   protected void chunkByPrompt () {
      int b    = -1;  //integer form of the character
      char c   = ' ';
      byte ptr = 0;   //pointer to a position in the prompt
      char[] prompt = CMD_PROMPT.toCharArray();

      try {

         while ((b = in.read()) != -1) {

            //FIXME: diagnostics
	    //out of range invisible characters
	    //10 is \n
	    //13 is \r ?
	    if (b!= 10 && b!=13 && (b < 32 || b > 126)) {
	       String foo = "[" + b + "]";
	       for (int z=0;z<foo.length();z++)
		  buffer.put(foo.charAt(z));
	    }

	    //normal character
	    else {
	       c = (char) b;

	       if (c != '\r') { //get rid of these
		  buffer.put(c);
		  if (c == prompt[ptr]) {  //look for the prompt
		     ptr++;
		     if (prompt.length == ptr) { //found prompt
			buffer.limit(buffer.position() - prompt.length);
			buffer.rewind();
			parse(buffer);
			buffer.clear();
			ptr = 0;
		     }
		  } 
		  else {  //not prompt
		     ptr = 0;
		  }
	       }
	    }
	 }
	 //purge remaining buffer
	 if (buffer.position() > 0) {
	    buffer.limit(buffer.position());
	    buffer.rewind();
	    parse(buffer);
	 }
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      System.out.println("FICSProtocolHandler: socket connection closed");
   }

   /* chunkByBlockMode *****************************************************/
   /** uses FICS's block mode to process the server output.  This cannot
    *  be used with Timeseal because Timeseal chops off the high-order bits
    *  and DAV made Block_mode use the high-order bits.  Duh.
    *
    *  <bold>This is currently disabled and has at least one known killer
    *  bug.  But is kept in the code for future reference.</bold>
    */
   protected void chunkByBlockMode () { 
      short block_state = 0;
      int id = 0,  //block-id
          cmd = 0; //block-cmd number
      char c = ' ';
      int b = ' '; //integer form of the character
      short ptr = 0;
      CharBuffer idBuff = CharBuffer.allocate(6),
		 cmdBuff = CharBuffer.allocate(3);
      char[] prompt = CMD_PROMPT.toCharArray();

      try {
	    while ((b = in.read()) != -1) { //in.ready()) {
	       
	       //Dealing with Block Mode
	       if (b == FICSBlockMode.BLOCK_START
	           || b == FICSBlockMode.BLOCK_SEPARATOR)
		  block_state++;

	       else if (b == FICSBlockMode.BLOCK_END) {
	          idBuff.limit(idBuff.position());
	          idBuff.rewind();
		  cmdBuff.limit(cmdBuff.position());
		  cmdBuff.rewind();
		  buffer.limit(buffer.position());
		  buffer.rewind();
	          try {
		     id = Integer.parseInt(idBuff.toString());
		     cmd = Integer.parseInt(cmdBuff.toString());
		  }
		  catch (NumberFormatException e) {
		     idBuff.rewind();
		     cmdBuff.rewind();
		     ICSErrorLog.report("Couldn't parse an int for the block"
		        + "{" + block_state + "}"
		        + " id(" + idBuff.toString() 
			+ ") or cmd(" + cmdBuff.toString() + "):"
			+ buffer.toString());
		  }
		  finally {
		     parseResponse(id, cmd, buffer);
		     idBuff.clear();
		     cmdBuff.clear();
		     buffer.clear();
	             block_state = 0;
		     ptr = 0;
		  }
	       }

               //out of range invisible characters
	       else if (b!= 10 && b!=13 && (b < 32 || b > 126)) {
	          String foo = "[" + b + "]";
		  for (int z=0;z<foo.length();z++)
		     buffer.put(foo.charAt(z));
	       }

	       //normal character
	       else {
	          c = (char) b;

		  switch (block_state) {
		     case 1:  //
		        idBuff.put(c);
			break;
		     case 2:
		        cmdBuff.put(c);
			break;
		     case 3:
		        if (c != '\r')
			   buffer.put(c);
			break;
		     case 0:
			if (c != '\r') { //get rid of these
			   buffer.put(c);
			   if (c == prompt[ptr]) {  //look for the prompt
			      ptr++;
			      if (prompt.length == ptr) { //found prompt
				 buffer.limit(buffer.position()- prompt.length);
				 buffer.rewind();
				 parse(buffer);
				 buffer.clear();
				 ptr = 0;
			      }
			   } else {  //not prompt
			      ptr = 0;
			   }
			}
			break;
		     default:
		        ICSErrorLog.report("FICSChunker has a block_state of "
			  + block_state);
			block_state = 0;
			assert false : "FICSChunker error in block_state";
		  }
	       }
	    }
	 if (buffer.position() > 0) {
	    buffer.rewind();
	    parse(buffer);
	 }
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      System.out.println("FICSProtocolHandler: socket connection closed");
   }

   /* disconnect *************************************************************/
   public void disconnect () {
      sendCommand("exit");
   }

   /* sendCommand () *********************************************************/
   /** send a command to the server.
    */
   public void sendCommand (String cmd, boolean echo) {
       if (echo) 
          System.out.println(cmd);

       if (isBlockMode)
          out.println(1 + " " + cmd);
       else
          out.println(cmd);
       out.flush();
   }

   public void sendCommand (String cmd) {
      sendCommand(cmd, true);
   }

   /* parse () **************************************************************/
   /** The 'datagram' or message chunk has already been establish, now we
    *  just gotta figure out what the message is and send it to the right
    *  listeners.
    */
   protected void parse (CharSequence str) {
      ICSEvent icsEvent = null;
      Matcher matcher = null;
      boolean found = false;

      for (int i=0; i < eventFactories.length && !found; i++) {
         if ((icsEvent = eventFactories[i].createICSEvent(str)) != null) {
	    icsEvent.setServer(this);
	    found = true;
	    router.dispatch(icsEvent);
	 }
      }

      if (found) { /*no-op*/ }
      else if ((matcher = match(seekRemovedPattern, str)) != null)
         sendSeekRemovedEvent(matcher);

      else if ((matcher = match(matchPattern, str)) != null)
         sendMatchEvent(matcher);

      else if ((matcher = match(takebackPattern, str)) != null)
         sendTakeBackEvent(matcher);
      else if ((matcher = match(availInfoPattern, str)) != null)
         sendAvailInfoEvent(matcher);

      else 
         System.out.println(str);

      //what's after the match?
      if (matcher != null) {
        int end = matcher.end();
        if (end < str.length() && str.charAt(end) == '\n') 
	   end++;
        if (end < str.length())
           System.out.println(str.subSequence(end, str.length()));
      }
   }

   protected Matcher match (Pattern p, CharSequence str) {
      Matcher m = null;
      m = p.matcher(str);

      if (m != null && m.find())
         return m;
      else 
         return null;
   }

   //parseResponse/////////////////////////////////////////////////////////////
   protected void parseResponse (int id, int cmd, CharSequence str) {
   /*
      ICSEvent icsEvent = null;
      ICSEvent history = new FICSHistoryEvent(this);
      ICSEvent movelist = new FICSMoveListEvent(this);

      switch (cmd) {

         case FICSBlockMode.BLK_HISTORY:
	    System.out.println(">>Got a history!");
	    icsEvent = history.newICSEventInstance(str);
	    if (icsEvent == null)
	    System.out.println("But it didn't match");
	    break;

	 case FICSBlockMode.BLK_MOVES:
	    System.out.println(">>Got moves list!");
	    icsEvent = movelist.newICSEventInstance(str);
	    if (icsEvent == null)
	    System.out.println("But it didn't match");

	 default:

      }
      if (icsEvent != null)
         router.dispatch(icsEvent);
      else
         System.out.print("BLOCK(" + id + "/" + cmd + "): " 
	    + str.toString() + "</BLOCK>");
     */
   }


   //actions//////////////////////////////////////////////////////////////////
   protected void sendConnectionEvent (Matcher m) {
      System.out.println(ESC + BOLD_BLACK + m.group() + ESC + PLAIN);
   }

   protected void sendBoardEvent (Matcher m) {
      System.out.println(ESC + YELLOW + m.group() + ESC + PLAIN);
   }

   protected void sendMoveListEvent (Matcher m) {
      System.out.println(ESC + YELLOW + m.group() + ESC + PLAIN);
      debugGroupDump(m);
   }

   protected void sendMatchEvent (Matcher m) {
      System.out.println(ESC + BOLD_RED + m.group() + ESC + PLAIN);
   }

   protected void sendSeekRemovedEvent (Matcher m) {
      System.out.println(ESC + BLUE + m.group() + ESC + PLAIN);
   }


   protected void sendTakeBackEvent (Matcher m) {
      System.out.println(ESC + RED + m.group() + ESC + PLAIN);
   }

   protected void sendAutoSaluteEvent (Matcher m) {
      System.out.println(ESC + BOLD_BLACK + m.group(1) + ESC + PLAIN);
      //1 - message (without trailing prompt letter)
      //2 - handle
      //3 - account type
      //4 - who is being saluted
   }

   protected void sendAvailInfoEvent (Matcher m) {
      System.out.println(ESC + BOLD_BLACK + m.group() + ESC + PLAIN);
   }

   //debug functions////////////////////////////////////////////////////////
   private void debugGroupDump (Matcher m) {
      for(int i=0; i<=m.groupCount(); i++) {
         System.out.println(i + ": " + m.group(i));
      }
   }
}

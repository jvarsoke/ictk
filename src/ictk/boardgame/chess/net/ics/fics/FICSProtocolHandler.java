/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: FICSProtocolHandler.java,v 1.24 2004/04/07 19:56:24 jvarsoke Exp $
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

   //FIXME: this stuff is WAY too server specific
   protected String LOGIN_PROMPT   = "login:",
                    PASSWD_PROMPT  = "password:",
                    CMD_PROMPT     = "\nfics% ",
                    GUEST_PROMPT   = "Press return to enter the server as \"",
                    START_SESSION  = "**** Starting FICS session as ",
                    INVALID_PASSWD = "**** Invalid password! ****",
		    ALREADY_LOGGED_IN = "is already logged in ***",
                    INTERFACE_NAME = 
		       "-=[ ictk ]=- v0.2 http://ictk.sourceforge.net";

   //common regex phrases
   protected final static String REGEX_handle    = "([\\w]+)",
                                 REGEX_acct_type = "(\\(\\S*\\))?";

   protected PrintStream stdout = System.out;

   //FIXME: RATING - UNR  is a possiblity

      /** parsers for various events from the server.
       ** The order of the parsers should be optimized by frequency
       ** of events. */
   final protected ICSEventParser[] eventFactories;

      /** is block_mode turned on for the server protocol. */
   boolean isBlockMode = false;

      /** the max amount the server input buffer can hold before throwing 
       ** an error.  The value of this should be plenty as it really only
       ** needs to be as large as the longest message chunk.  */
   int BUFFER_SIZE = 128 * 1048;

      /** collected input yet to be processed */
   CharBuffer buffer = CharBuffer.allocate(BUFFER_SIZE);

   //constructors/////////////////////////////////////////////////////////////
   public FICSProtocolHandler () {
      host   = "64.71.131.140"; //defaults
      port   = 5000;

      int i = 0;
      eventFactories = new ICSEventParser[24];
      eventFactories[i++] = FICSBoardUpdateStyle12Parser.getInstance();
      eventFactories[i++] = FICSMoveListParser.getInstance();
      eventFactories[i++] = FICSTellParser.getInstance();
      eventFactories[i++] = FICSKibitzParser.getInstance();
      eventFactories[i++] = FICSChannelParser.getInstance();
      eventFactories[i++] = FICSShoutParser.getInstance();
      eventFactories[i++] = FICSGameResultParser.getInstance();
      eventFactories[i++] = FICSGameCreatedParser.getInstance();
      eventFactories[i++] = FICSPlayerConnectionParser.getInstance();
      eventFactories[i++] = FICSPlayerNotificationParser.getInstance();
      eventFactories[i++] = FICSGameNotificationParser.getInstance();
      eventFactories[i++] = FICSSeekClearParser.getInstance();
      eventFactories[i++] = FICSSeekAdParser.getInstance();
      eventFactories[i++] = FICSSeekRemoveParser.getInstance();
      eventFactories[i++] = FICSSeekAdReadableParser.getInstance();
      eventFactories[i++] = FICSChallengeParser.getInstance();

      eventFactories[i++] = FICSExamineNavigationParser.getInstance();
      eventFactories[i++] = FICSExaminerOtherParser.getInstance();
      eventFactories[i++] = FICSExaminerSelfParser.getInstance();
      eventFactories[i++] = FICSObserverSelfParser.getInstance();
      eventFactories[i++] = FICSExamineCommitParser.getInstance();
      eventFactories[i++] = FICSExamineNavigationBeginParser.getInstance();
      eventFactories[i++] = FICSExamineNavigationEndParser.getInstance();
      eventFactories[i++] = FICSExamineNavigationEndVariationParser.getInstance();
 

      router = new ICSEventRouter();
   }

   public FICSProtocolHandler (String host, int port) {
      this();
      this.host = host;
      this.port = port;
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
             dispatchConnectionEvent(new ICSConnectionEvent(this));
	     processServerOutput(); 
	  }
	  else {
	     Log.error(Log.USER_ERROR, "unsuccessful login");
	  }
	  //end thread
	  if (socket != null && !socket.isClosed()) {}
	     socket.close();
       }
       catch (IOException e) {
          e.printStackTrace();
       }
       //FIXME: might want to differentiate between bad end and normal end
       dispatchConnectionEvent(new ICSConnectionEvent(this));
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
		  stdout.print(buffer.toString());
		  stdout.flush();
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
		        stdout.print(tmp);
		        stdout.print(" ");
			stdout.flush();
		        buffer.rewind();
		        buffer.clear();

		        sendCommand(handle);
			seenLogin = true;
		  }

                  //password prompt
		  else if (seenLogin && !seenPasswd 
		           && tmp.lastIndexOf(PASSWD_PROMPT) > -1) {
		        stdout.print(tmp);
		        stdout.print(" ");
			stdout.flush();
		        buffer.rewind();
		        buffer.clear();

		        sendCommand(passwd, false);
			seenPasswd = true;
                        stdout.println();
		  }

                  //guest already logged in
		  else if (seenLogin && !seenPasswd 
		           && tmp.lastIndexOf(ALREADY_LOGGED_IN) > -1) {
		        stdout.print(tmp);
		        stdout.print(" ");
			stdout.flush();
		        buffer.rewind();
		        buffer.clear();

                        stdout.println();
			return false;
		  }

		  //guest login prompt (instead of password)
		  else if (seenLogin && !seenPasswd 
		          && tmp.lastIndexOf(GUEST_PROMPT) > -1) {
		        stdout.print(tmp);
			stdout.flush();
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
		     stdout.print(tmp);
		     stdout.flush();
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
		     stdout.print(tmp);
		     stdout.flush();
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
      sendCommand("set style 12", false);
      sendCommand("iset ms 1", false);
      sendCommand("set interface " + INTERFACE_NAME, false);
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

            //diagnostics
	    //out of range invisible characters
	    //10 is \n
	    //13 is \r ?
	    if (b!= 10 && b!=13 && (b < 32 || b > 126)) {
	       /*
	       String foo = "[" + b + "]";
	       for (int z=0;z<foo.length();z++)
		  buffer.put(foo.charAt(z));
	       */
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

                        //send to parser for processing
			parse(buffer);

			buffer.clear();
			ptr = 0;
		     }
		  } 
		  else if (ptr == 1 && c == prompt[0]) {
		     //got \n before prompt -- do nothing
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
		     Log.error(Log.PROG_ERROR,
		        "Couldn't parse an int for the block"
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
		        Log.error(Log.PROG_ERROR, 
			"FICSChunker has a block_state of "
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
   }

   /* disconnect *************************************************************/
   public void disconnect () {
      sendCommand("exit");
   }

   /* sendCommand ************************************************************/
   /** send a command to the server.
    */
   public void sendCommand (String cmd, boolean echo) {
       //FIXME: this should go to a setable stream, not stdout
       if (echo)
          stdout.println(cmd);

       if (isBlockMode)
          out.println(1 + " " + cmd);
       else
          out.println(cmd);
       out.flush();
   }

   public void sendCommand (String cmd) {
      sendCommand(cmd, true);
   }

   /* parse *****************************************************************/
   /** The 'datagram' or message chunk has already been establish, now we
    *  just gotta figure out what the message is and send it to the right
    *  listeners.
    *  <br>
    *  It is possible that there are multiple messages in one chunk.  To
    *  deal with this the string before and after the matched data must 
    *  also be examined for messages.  These message need to be queued up
    *  and then sent to the router in order.
    */
   protected void parse (CharSequence str) {
      ICSEvent icsEvent = null;
      Matcher matcher = null;
      boolean found = false;

      if (debugParser) 
         stdout.println("<PARSING>" + str + "</PARSING>");

      for (int i=0; i < eventFactories.length && !found; i++) {

         if ((matcher = eventFactories[i].match(str)) != null) {
	    icsEvent = eventFactories[i].createICSEvent(matcher); 
	    assert icsEvent != null : "parser matched, but event null?";

	    if (matcher.start() > 3)
	       parse(str.subSequence(0, matcher.start()));

	    icsEvent.setServer(this);
	    router.dispatch(icsEvent);

	    if (str.length() - matcher.end() > 3)
	       parse(str.subSequence(matcher.end(), str.length()));

	    found = true;
	 }
      }

      if (!found)
         stdout.println(str);
   }

/*
   protected Matcher match (Pattern p, CharSequence str) {
      Matcher m = null;
      m = p.matcher(str);

      if (m != null && m.find())
         return m;
      else 
         return null;
   }
*/

   //parseResponse/////////////////////////////////////////////////////////////
   /** <b>used for blockmode, currently disabled</b>
    */
   private void parseResponse (int id, int cmd, CharSequence str) {
   /*
      ICSEvent icsEvent = null;
      ICSEvent history = new FICSHistoryEvent(this);
      ICSEvent movelist = new FICSMoveListEvent(this);

      switch (cmd) {

         case FICSBlockMode.BLK_HISTORY:
	    stdout.println(">>Got a history!");
	    icsEvent = history.newICSEventInstance(str);
	    if (icsEvent == null)
	    stdout.println("But it didn't match");
	    break;

	 case FICSBlockMode.BLK_MOVES:
	    stdout.println(">>Got moves list!");
	    icsEvent = movelist.newICSEventInstance(str);
	    if (icsEvent == null)
	    stdout.println("But it didn't match");

	 default:

      }
      if (icsEvent != null)
         router.dispatch(icsEvent);
      else
         stdout.print("BLOCK(" + id + "/" + cmd + "): " 
	    + str.toString() + "</BLOCK>");
     */
   }
}

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

import ictk.boardgame.chess.net.ics.fics.event.*;
import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.*;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.regex.*;
//import com.jvarsoke.boardgame.chess.*;

public class FICSProtocolHandler extends ICSProtocolHandler {
   public final static int SOCKET_TIMEOUT = 10000;
   /** this is the delay between tests to see if there's any info on the 
    ** socket in nanoseconds. Setting this to 0 will peg your CPU needlessly **/
   protected int SOCKET_DELAY = 1000;
   protected PrintWriter out;
   protected InputStreamReader in;

   protected String LOGIN_PROMPT  = "login:";
   protected String PASSWD_PROMPT = "password:";
   protected String INTERFACE_NAME = "-=[ ictk ]=- v0.2 http://ictk.sourceforge.net";
   protected String CMD_PROMPT    = "\nfics% ";
   protected String GUEST_PROMPT  = "Press return to enter the server as \"";
   protected String START_SESSION = "**** Starting FICS session as ";
   protected char[] prompt = CMD_PROMPT.toCharArray();

   //common regex phrases
   protected final static String REGEX_handle    = "([\\w]+)";      
   protected final static String REGEX_acct_type = "(\\(\\S*\\))?";  
   protected final static String REGEX_rating    = "\\(\\s*([0-9+-]+[EP]?)\\)";

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

   final protected ICSEvent[] eventFactories;

      /** moniker the chess player is using */
   String handle = null;
   String passwd = null;
   String host   = "64.71.131.140";
   int    port   = 5000;

      /** Telnet connection to server */
   boolean loggedIn = false;
   boolean seenLogin = false;
   boolean seenPasswd = false;
   boolean isBlockMode = false;

      /** collected input yet to be processed */
   StringBuffer buffer;
   int BUFFER_SIZE = 128 * 1048;

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
      int i = 0;
      eventFactories = new ICSEvent[16];
      eventFactories[i++] = new FICSSeekAdEvent(this);
      eventFactories[i++] = new FICSSeekRemoveEvent(this);
      eventFactories[i++] = new ICSBoardUpdateStyle12Event(this);
      eventFactories[i++] = new FICSGameResultEvent(this);
      eventFactories[i++] = new FICSGameCreatedEvent(this);
      eventFactories[i++] = new FICSChannelEvent(this);
      eventFactories[i++] = new FICSShoutEvent(this);
      eventFactories[i++] = new FICSSeekAdReadableEvent(this);
      eventFactories[i++] = new FICSKibitzEvent(this);
      eventFactories[i++] = new FICSTellEvent(this);
      eventFactories[i++] = new FICSBoardSayEvent(this);
      eventFactories[i++] = new FICSSeekClearEvent(this);
      eventFactories[i++] = new FICSPlayerConnectionEvent(this);
      eventFactories[i++] = new FICSPlayerNotificationEvent(this);
      eventFactories[i++] = new FICSGameNotificationEvent(this);
      eventFactories[i++] = new FICSQTellEvent(this);
      router = new ICSEventRouter();
      router.setDefaultRoute(new ANSIConsole());
   }

   public FICSProtocolHandler (String host, int port) {
      this();
      this.host = host;
      this.port = port;
   }

   //methods/////////////////////////////////////////////////////////////////
   public void setHandle (String handle) {
      this.handle = handle;
   }

   public String getHandle () { 
      return handle; 
   }

   public void setPassword (String password) {
      this.passwd = password;
   }

   public void setHost (String host) {
      this.host = host;
   }

   public void setPort (int port) {
      this.port = port;
   }


   public boolean isConnected () {
      if (socket == null)
         return false;
      return socket.isConnected();
   }

   public void connect () 
      throws UnknownHostException, IOException {

      if (handle == null || passwd == null)
         throw new IllegalStateException(
	    "Both handle and password must be set before login");

      socket = new Socket(host, port);
      //socket.setSoTimeout(SOCKET_TIMEOUT);
      in = new InputStreamReader(socket.getInputStream());
      out = new PrintWriter(socket.getOutputStream());
      thread.start();
   }

   public void run () {
   //prompt = char[];
       try {
	  loggedIn = doLogin();
	  if (loggedIn) {
	     processServerOutput(); 
	  }
	  else {
	  //close socket etc
	  }
	  //end thread
	  if (socket != null)
	     socket.close();
       }
       catch (IOException e) {
          e.printStackTrace();
       }
       System.err.println("FICSProtocolHandler: run end.");
   }

   /* doLogin() **********************************************************/
   /** Do the login sequence.  This is non-chunked data
    */
    /*this is a mess -- but it only runs once*/
   protected boolean doLogin () throws IOException {
      int loginState = 0;  //0=haven't tried, -1=fail, 1=pass
      BufferedReader b_in = new BufferedReader(in);
      char[] cbuff = new char[BUFFER_SIZE];
      boolean _loggedIn = false;
      buffer = new StringBuffer(BUFFER_SIZE);
      String str = null;
      int cnt = 0;

      while (socket != null && socket.isConnected()
             && loginState == 0) {
	 cnt = b_in.read(cbuff, 0, BUFFER_SIZE);

	 if (cnt > 0) {
	    str = new String(cbuff, 0, cnt);
      
	    System.out.print(str);
	    System.out.flush();
	    //figure out if we've hit the login prompt
	    String tmp = null;
	    buffer.append(str); 

	    //the easy case where we got the whole string
	    if (!seenLogin) {
	       if (str.length() > LOGIN_PROMPT.length())
		  tmp = str;
	       else
		  tmp = buffer.toString();
		  

	       if (tmp.lastIndexOf(LOGIN_PROMPT) > 0) {
		  //we have the prompt
		  sendCommand(handle);
		  seenLogin = true;
		  buffer = new StringBuffer(BUFFER_SIZE);
	       }
	    }
	    //looking for password prompt
	    else if (!seenPasswd) {
	       if (str.length() > PASSWD_PROMPT.length())
		  tmp = str;
	       else
		  tmp = buffer.toString();

	       if (tmp.lastIndexOf(PASSWD_PROMPT) > 0) {
		  sendCommand(passwd);
		  seenPasswd = true;
	       }
	       else if (tmp.lastIndexOf(GUEST_PROMPT) > 0) {
		  sendCommand("");
		  seenPasswd = true;
	       }
	       buffer = new StringBuffer(BUFFER_SIZE);
	    }

	    //looking for start of session
	    //and grabbing handle
	    else {
	       tmp = buffer.toString();
	       StringBuffer login = new StringBuffer(20);
	       int begin = tmp.indexOf(START_SESSION);
	       boolean found = false;
	       if (begin > -1) {
		  begin += START_SESSION.length();
		  for (int i=begin; i < begin+20 && !found; i++) {
		     found =  (tmp.charAt(i) == ' ' || tmp.charAt(i) == '(');
		     if (!found) login.append(tmp.charAt(i));
		  }
		  handle = login.toString();
		  _loggedIn=true;
		  loginState = 1; //FIXME: need setting for bad state
	       }
	    }
	 }
      }
      setLoginVars();
      return _loggedIn;
   }

   protected void setLoginVars () { 
      sendCommand("iset block 1");
      isBlockMode = true;
      sendCommand("iset ms 1");
      sendCommand("set interface " + INTERFACE_NAME);
   }


   /* processServerOutput **************************************************/
   /** processes output once the user is logged in
    */
   protected void processServerOutput () { 
      short block_state = 0;
      int id = 0,  //block-id
          cmd = 0; //block-cmd number
      char c = ' ';
      int b = ' '; //integer form of the character
      short ptr = 0;
      CharBuffer buff = CharBuffer.allocate(BUFFER_SIZE),
                 idBuff = CharBuffer.allocate(6),
		 cmdBuff = CharBuffer.allocate(3);

      try {
/*
         while (socket != null && socket.isConnected()) {
try {
Thread.sleep(0L, SOCKET_DELAY);	    
}
catch (Exception e) {
  System.err.println("exception in thread");
}
*/
/*
//FIXME: this from comp.lang.java.programmer
Just perfom the read()-operation without checking if there's something
to read. Java supports select(), but you'll need to switch to java.nio,
which supports blocking and non-blocking io operations on channels.
*/
	    while ((b = in.read()) != -1) { //in.ready()) {
	       
	       //b = in.read();

	       //Dealing with Block Mode
	       if (b == FICSBlockMode.BLOCK_START
	           || b == FICSBlockMode.BLOCK_SEPARATOR)
		  block_state++;

	       else if (b == FICSBlockMode.BLOCK_END) {
	          idBuff.limit(idBuff.position());
	          idBuff.rewind();
		  cmdBuff.limit(cmdBuff.position());
		  cmdBuff.rewind();
		  buff.limit(buff.position());
		  buff.rewind();
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
			+ buff.toString());
		  }
		  finally {
		     parseResponse(id, cmd, buff);
		     idBuff.clear();
		     cmdBuff.clear();
		     buff.clear();
	             block_state = 0;
		     ptr = 0;
		  }
	       }

               //out of range invisible characters
	       else if (b!= 10 && b!=13 && (b < 32 || b > 126)) {
	          String foo = "[" + b + "]";
		  for (int z=0;z<foo.length();z++)
		     buff.put(foo.charAt(z));
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
			   buff.put(c);
			break;
		     case 0:
			if (c != '\r') { //get rid of these
			   buff.put(c);
			   if (c == prompt[ptr]) {  //look for the prompt
			      ptr++;
			      if (prompt.length == ptr) { //found prompt
				 buff.limit(buff.position() - prompt.length);
				 buff.rewind();
				 parse(buff);
				 buff.clear();
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
/*
	 }
*/
	 //FIXME: doens't work
	 if (buff.position() > 0) {
	    buff.rewind();
	    parse(buff);
	 }
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      System.out.println("FICSProtocolHandler: socket connection closed");
   }

   public void disconnect () {
      sendCommand("exit");
   }

   /* sendCommand () *********************************************************/
   /** send a command to the server.
    */
   public void sendCommand (String cmd) {
       System.out.println(cmd);
       if (isBlockMode)
          out.println(1 + " " + cmd);
       else
          out.println(cmd);
       out.flush();
   }




   /* parse () **************************************************************/
   /** The 'datagram' or message chunk has already been establish, not we
    *  just gotta figure out what the message is and send it to the right
    *  listeners.
    */
    /* What I wouldn't give for function pointers.  Yes, I know I can use
     * reflection, but don't want to burn the CPU on it.
     */
   protected void parse (CharSequence str) {
      ICSEvent icsEvent = null;
      Matcher matcher = null;
      boolean found = false;

      for (int i=0; i < eventFactories.length && !found; i++) {
         if ((icsEvent = eventFactories[i].newICSEventInstance(str)) != null) {
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


   //main -- test/////////////////////////////////////////////////////////

   public static void main (String[] args) {
   }
}

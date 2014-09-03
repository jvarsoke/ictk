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

/* SimpleICSClient ***********************************************************/
/**
 * This is a simple ICS Chess Server client.  It logs in and color codes
 * the text.  There is no Board associated with it yet, so playing a 
 * game is not practical.  But it should give the simplist idea of how
 * to write an interface with the ICTK library.
 *
 * @author  jvarsoke
 */

import java.util.Date;

import ictk.boardgame.chess.net.ics.ui.cli.ANSIConsole;
import ictk.boardgame.chess.net.ics.ICSProtocolHandler;
import ictk.boardgame.chess.net.ics.ICSEventRouter;
import ictk.boardgame.chess.net.ics.fics.FICSProtocolHandler;
import ictk.boardgame.chess.net.ics.event.ICSEvent;
import ictk.boardgame.chess.net.ics.event.ICSConnectionListener;
import ictk.boardgame.chess.net.ics.event.ICSConnectionEvent;

public class SimpleICSClient implements ICSConnectionListener {
   String handle;
   String passwd;

   ICSProtocolHandler ics;
   ANSIConsole ansiConsole;
   CommandInput commandLine;
   
   /** creates a new SimpleICSClient registereing the ICS protocol handler
    *  and the default listener for all events.
    */
   public SimpleICSClient(String handle, String passwd) {
      this.handle = handle;
      this.passwd = passwd;

      ics = new FICSProtocolHandler();
      ics.addConnectionListener(this);

      ansiConsole = new ANSIConsole();

      ICSEventRouter router = ics.getEventRouter();
      router.setDefaultListener(ansiConsole);

      //channel 1 (help) goes to a different listener
      router.addChannelListener(ICSEvent.CHANNEL_EVENT, 1,
				new ChannelListenerExample());

      //need all other channel events to still go to ansiConsole
      ics.getEventRouter().addEventListener(ICSEvent.CHANNEL_EVENT, ansiConsole);

      //so the main ChannelEvent listener doesn't also get the event
      router.setChannelExclusive(ICSEvent.CHANNEL_EVENT, 1, true);
      //so the defaultListener doesn't get the event
      router.setExclusive(ICSEvent.CHANNEL_EVENT, true);

      commandLine = new CommandInput("Simple ICS Client", ics);
   }

    /** if not already connected this will establish a connection to the
     *  server.
     */
    public void connect() {
       // Add your handling code here:
       if (!ics.isConnected()) {
          try {
	     System.out.println("[Client] attempting to connect");
        ics.setHost("69.36.243.188");
	     ics.setHandle(handle);
	     ics.setPassword(passwd);
	     ics.setLagCompensation(true);
             ics.connect();
	  }
	  catch (java.net.UnknownHostException e) {
	     e.printStackTrace();
	  }
	  catch (java.io.IOException e) {
	     e.printStackTrace();
	  }
       }
    }

    public void setVisible (boolean t) {
       commandLine.setVisible(t);
    }

    public void connectionStatusChanged (ICSConnectionEvent evt) {
       ICSProtocolHandler conn = evt.getConnection();

       if (!conn.isConnected()) {
          System.err.println("Connection Terminated: " + new Date());
          System.exit(0);
       }
       else {
          System.err.println("Connection Live but received event: " + evt);
       }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
       SimpleICSClient client = null;
       if (args.length < 2) {
          System.err.println("You must supply username and password");
	  System.exit(1);
       }
       client = new SimpleICSClient(args[0], args[1]);
       client.setVisible(true);
       client.connect();
    }
}

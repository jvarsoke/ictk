/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: SimpleICSClient.java,v 1.5 2003/09/11 02:46:43 jvarsoke Exp $
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

/* SimpleICSClient ***********************************************************/
/**
 * This is a simple ICS Chess Server client.  It logs in and color codes
 * the text.  There is no Board associated with it yet, so playing a 
 * game is not practical.  But it should give the simpist idea of how
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

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

/* SimpleICSClient ***********************************************************/
/**
 * This is a simple ICS Chess Server client.  It logs in and color codes
 * the text.  There is no Board associated with it yet, so playing a 
 * game is not practical.  But it should give the simpist idea of how
 * to write an interface with the ICTK library.
 *
 * @author  jvarsoke
 */

import ictk.boardgame.chess.net.ics.ui.cli.ANSIConsole;
import ictk.boardgame.chess.net.ics.fics.FICSProtocolHandler;

public class SimpleICSClient {
   String handle;
   String passwd;

   FICSProtocolHandler fics;
   ANSIConsole ansiConsole;
   CommandInput commandLine;
   
   /** Creates new form SimpleICSClient */
    public SimpleICSClient(String handle, String passwd) {
        this.handle = handle;
	this.passwd = passwd;

	fics = new FICSProtocolHandler();
	ansiConsole = new ANSIConsole();
	fics.getEventRouter().setDefaultListener(ansiConsole);

        commandLine = new CommandInput("Simple ICS Client", fics);
    }

    public void connect() {
       // Add your handling code here:
       if (!fics.isConnected()) {
          try {
	     System.out.println("[Client] attempting to connect");
	     fics.setHandle(handle);
	     fics.setPassword(passwd);
	     fics.setLagCompensation(true);
             fics.connect();
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

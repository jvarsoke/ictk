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

package ictk.boardgame.chess.net.ics;

import java.net.*;
import java.io.*;
import ictk.boardgame.chess.net.ics.event.ICSConnectionListener;
import ictk.boardgame.chess.net.ics.event.ICSConnectionEvent;

/** The generic connection object.  This handles logins, disconnects
 *  and chunking the server messages.
 */
public abstract class ICSProtocolHandler implements Runnable {
   protected Thread thread;

      /** this connection to the server */
   protected Socket socket;
      /** connected to the socket */
   protected PrintWriter out;
      /** connected to the socket */
   protected InputStreamReader in;

      /** the object that is routing events from this server */
   protected ICSEventRouter router;

      /** the user's login handle for the server */
   protected String handle,
      /** the user's password */
                    passwd;
      /** the account type for this user when they log in.
       ** This helps determine if you're a guest, computer or team.*/
   protected ICSAccountType acctType;

      /** the server you want to connect to */
   protected String host;
      /** the port on the server you want to connect to */
   protected int    port;

      /** is lag compensation turned on (timeseal, timestamp, accuclock etc)
       ** for this connection.*/
   protected boolean isLagCompensated;

      /** is the user logged in */
   protected boolean isLoggedIn;

      /** connection listeners interested in the status of the socket */
   protected ICSConnectionListener[] conSubscribers;

   //constructors//////////////////////////////////////////////////////////////
   public ICSProtocolHandler () {
      thread = new Thread(this);
   }

   //assessors-mutators////////////////////////////////////////////////////////
   /* setHandle **************************************************************/
   /** sets the user login handle.  This must be done before you try to
    *  connect to the server.
    */
   public void setHandle (String handle) {
      this.handle = handle;
   }

   /* getHandle **************************************************************/
   /** returns the handle that the user is logged in as.  Note: this is the
    *  true handle as obtained from the server and may differ from the one 
    *  set before login.  Usually only the case is different.
    */
   public String getHandle () {
      return handle;
   }

   /* setPassword ************************************************************/
   /** sets the password this user will use to connect.  This must be set
    *  before you attempt to log into the server.
    */
   public void setPassword (String password) {
      passwd = password;
   }

   /* getPassword ************************************************************/
   /** returns the password used to login.
    */
   public String getPassword () {
      return passwd;
   }

   /* setHost ****************************************************************/
   /** set the host (server) you want to connect to.  Setting a bad host will
    *  not throw an error until you try to connect.
    */
   public void setHost (String host) {
      this.host = host;
   }

   /* getHost ****************************************************************/
   /** gets the name of the host that you want to / or have connected to.
    */
   public String getHost () {
      return host;
   }

   /* setPort ****************************************************************/
   /** sets the port number you want to connect to.
    */
   public void setPort (int port) {
      this.port = port;
   }

   /* getPort ****************************************************************/
   /** gets the port number you want to / or have connected to.
    */
   public int getPort () {
      return port;
   }

   /* setLagCompensation *****************************************************/
   /** sets the connection for lag compensation if t is true.  This can only
    *  be done before the connection is established.  Any attempt to set this
    *  value if there is already a connection to the host will result in 
    *  an exception being thrown.
    *
    *  @throws IllegalStateException if there is an active connection.
    */
   public void setLagCompensation (boolean t) {
      if (isConnected()) 
         throw new IllegalStateException(
	    "Cannot set lag compensation after connection already established."
	    );
      else
         isLagCompensated = t;
   }

   /* isLagCompensated *******************************************************/
   /** this is true if the connection has, or is currently set to have, lag
    *  compensation turned on for the connection.  This usually means timeseal,
    *  timestamp, accuclock, or some other lag compensation program is being
    *  used for the connection.
    */
   public boolean isLagCompensated () {
      return isLagCompensated;
   }

   /* isConnected ************************************************************/
   /** is the program currently connected to the host.  Note: this does
    *  not indicate if the program is logged into the server.  You cannot
    *  start sending commands yet.  Instead you need to use isLoggedIn();
    */
   public boolean isConnected () {
      if (socket == null)
         return false;
      return socket.isClosed();
   }

   /* isLoggedIn *************************************************************/
   /** is the user currently logged into the server.  This implies that 
    *  there is currently a connection to the server.
    */
   public boolean isLoggedIn () {
      return isLoggedIn;
   }

   /* setEventRouter *********************************************************/
   /** sets the object to do the event routing for this connection.  By default
    *  a router is setup.  But you might want to change it for some reason.
    */
   public void setEventRouter (ICSEventRouter router) {
      this.router = router;
   }

   /* getEventRouter *********************************************************/
   /** returns the event router being used for this connetion.
    */
   public ICSEventRouter getEventRouter () {
      return router;
   }


   //connection////////////////////////////////////////////////
   abstract public void connect ()
      throws UnknownHostException, IOException;

   abstract public void disconnect ();

   abstract public void sendCommand (String cmd);
   abstract public void sendCommand (String cmd, boolean echo);

   /* addConnectionListener *************************************************/
   public void addConnectionListener (ICSConnectionListener listener) {
      ICSConnectionListener[] tmp = null;

      if (conSubscribers == null) {
         tmp = new ICSConnectionListener[1];
         tmp[0] = listener;
      }
      else {
         tmp = new ICSConnectionListener[conSubscribers.length+1];
         System.arraycopy(conSubscribers, 0, tmp, 0, conSubscribers.length);
         tmp[conSubscribers.length] = listener;
      }
      conSubscribers = tmp;
   }

   /* removeConnectionListener **********************************************/
   public void removeConnectionListener (ICSConnectionListener listener) {
      ICSConnectionListener[] tmp = null;

      if (conSubscribers != null && conSubscribers.length > 1) {
         tmp = new ICSConnectionListener[conSubscribers.length - 1];
         int count = 0;
         for (int i=0; i < conSubscribers.length; i++)
            if (conSubscribers[i] != listener)
               tmp[count++] = conSubscribers[i];
      }
      conSubscribers = tmp;
   }

   /* dispatchConnectionEvent ***********************************************/
   public void dispatchConnectionEvent (ICSConnectionEvent evt) {
      if (conSubscribers != null)
         for (int i=0; i<conSubscribers.length; i++)
	    conSubscribers[i].connectionStatusChanged(evt);
   }
}

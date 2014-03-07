/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSResponse.java,v 1.2 2003/08/20 15:42:50 jvarsoke Exp $
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

/**This interface is for those Events that are triggered as a response
 *  to the client's command.  The ResponseKey is an arbitrary identifier
 *  used to link back to the caller of the command.
 */
 /* this is primarily for FICS BLOCK_MODE */
public interface ICSResponse {

   /**Determines if this Event was generated in response to a client's command*/
   public boolean isResponse ();

   /**FICS uses an integer response key*/
   public void setResponseKey (int key);
   public int getResponseKey ();

   /**ICC uses an arbrtrary string key*/
   public void setResponseKey (String key);
   public String getResponseKeyString ();

}

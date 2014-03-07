/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessTeam.java,v 1.1.1.1 2003/03/24 22:38:08 jvarsoke Exp $
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

package ictk.boardgame.chess;

import java.util.List;
import java.util.ArrayList;

import ictk.boardgame.Player;
import ictk.boardgame.Team;

/* ChessTeam ****************************************************************/
/** A ChessPlayer that involves several members, possibly playing as a 
 *  team in one game. 
 */
public class ChessTeam extends ChessPlayer implements Team {
   List players;

   public ChessTeam () {
      players = new ArrayList(2);
   }

   public ChessTeam (String n) {
      super(n);
      players = new ArrayList(2);
   }

   public String getTeamName () {
      return lastname;
   }

   public void setTeamName (String name) {
      lastname = name;
   }

   /* getPlayers ************************************************************/
   /** returns the List of players that are on this team.  This will
    *  never be null.
    *  NOTE: if you add anything other than ChessPlayers it will
    *        break certain methods.
    */
   public List getPlayers () {
      return players;
   }

   /* getRating *************************************************************/
   /** returns an average rating for the team if and only if all players
    *  have ratings or the rating has explicitly been set to non-zero.  
    */
   public int getRating () {
      if (rating > 0) return rating;
      else {
         int rating = 0;
	 for (int i=0; i < players.size(); i++)
	    rating += ((ChessPlayer) players.get(i)).getRating();
	 if (players.size() > 0)
	    rating = rating / players.size();
	 return rating;
      }
   }

   /* toString **************************************************************/
   /** diagonostic function.
    */
   public String toString () {
      String str = getName();
      return str;
   }
}

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
   List<Player> players;

   public ChessTeam () {
      players = new ArrayList<>(2);
   }

   public ChessTeam (String n) {
      super(n);
      players = new ArrayList<>(2);
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
    */
   public List<Player> getPlayers () {
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

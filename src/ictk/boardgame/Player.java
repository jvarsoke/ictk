/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Player.java,v 1.1.1.1 2003/03/24 22:38:06 jvarsoke Exp $
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

package ictk.boardgame;

/* Player *******************************************************************/
/** a Player represents a person, team or computer that is playing the game.
 *  All the methods of Player might not make sense for all subclasses (eg: 
 *  any Team implementation does not need a firstname / lastname) but 
 *  over interfacing this class doesn't make engineering sense.
 */
public class Player {
   protected String firstname,
                    lastname;

   public Player () {
   }

   /** 
    * @param n - "lastname, firstname" combo.  If a string "name" is 
    *            it will be set to the lastname.
    */
   public Player (String n) {
      int i = -1;
      if ((i = n.indexOf(',')) != -1) {
         lastname = n.substring(0, i);
	 if (i+2 < n.length())
	    firstname = n.substring(i+2, n.length()); //should be the space
      }
      else
         lastname = n;
   }

   public String getLastName ()  { return lastname; }
   public String getFirstName () { return firstname; }

   public void setLastName (String l) { lastname = l; }
   public void setFirstName (String f) { firstname = f; }

   /* getName ************************************************************/
   /** used to get name of player in form "Lastname, Firstname"
    *  or just "Lastname" if no firstname is set.
    */
   public String getName() { 
      String name = null;

      if (lastname != null) {
         name = lastname;
	 if (firstname != null)
	    name += ", " + firstname;
      }
      else {
         if (firstname != null) 
	    name = firstname;
      }
         
      return name; 
   }
   
   /* toString *********************************************************/
   /** a diagnostic tool only, currently returns getName();
    */
   public String toString () {
      String str = getName();
      return str;
   }

   /* equals ***********************************************************/
   /** compares the lastname and firstname of the players
    */
   public boolean equals (Object o) {
      if (o == this) return true;
      if ((o == null) || (o.getClass() != this.getClass()))
         return false;

      Player p = (Player) o;
      boolean t = true;

      t = (lastname == p.lastname) 
             || (lastname != null && lastname.equals(p.lastname));
      t = t && ((firstname == p.firstname)
             || (firstname != null && firstname.equals(p.firstname)));

      return t;
   }

   /* hashCode *********************************************************/
   /** uses lastname and firstname to compute the hash
    */
   public int hashCode () {
      int hash = 7;

      hash = 31 * hash + ((lastname == null) ? 0 : lastname.hashCode());
      hash = 31 * hash + ((firstname == null) ? 0 : firstname.hashCode());

      return hash;
   }
}

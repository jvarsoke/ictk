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

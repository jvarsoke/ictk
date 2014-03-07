/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessPlayer.java,v 1.1.1.1 2003/03/24 22:38:08 jvarsoke Exp $
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

import ictk.boardgame.Player;

/* ChessPlayer **************************************************************/
/** A ChessPlayer is usually a person, but could be a computer or a Team
 *  if it implements the appropriate interface.
 */
public class ChessPlayer extends Player {
   public static final byte NO_TITLE = 0,
      /** Grand Master */
                            GM       = 1,
      /** Women's Grand Master */
                            WGM      = 2,
      /** Internation Master */
			    IM       = 3,
      /** Women's International Master */
			    WIM      = 4,
      /** FIDE Master */
			    FM       = 5,
      /** Women's FIDE Master */
			    WFM      = 6,
      /** National Master */
                            NM       = 7,
      /** Women's National Master */
			    WNM      = 8,
      /** Some other title that isn't standard */
			    OTHER_TITLE = 9;

    /** titles such as GM, WGM, FM, etc **/
   protected byte title;
   protected int    rating;

   public ChessPlayer () {
   }

   public ChessPlayer (String n) {
      super(n);
   }

   public ChessPlayer (String n, int _rating) {
      super (n);
      rating = _rating;
   }

   /* getTitle **************************************************************/
   /** returns a title equal to on of the preset static values on ChessPlayer.
    */
   public byte   getTitle ()     { return title; }

   /* getRating *************************************************************/
   /** typically this is the Elo rating set by FIDE, but could also
    *  be FICS, USCF or other numerical rating.
    */
   public int    getRating()        { return rating; }

   /* setTitle **************************************************************/
   /** returns a title equal to on of the preset static values on ChessPlayer.
    */
   public void setTitle (int t)  { 
      if (t > 128 || t < -128)
         throw new IllegalArgumentException ("Title needs to be of byte size");
      title = (byte) t;
   }

   /* setRating *************************************************************/
   /** typically this is the Elo rating set by FIDE, but could also
    *  be FICS, USCF or other numerical rating.
    */
   public void setRating (int rating)        { this.rating = rating; }

   /* toString **************************************************************/
   /** diagonostic function.
    */
   public String toString () {
      String str = getName();
         if (rating > 0)
	    str += "(" + rating + ")";
      return str;
   }
}

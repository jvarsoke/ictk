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

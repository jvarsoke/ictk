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

package ictk.boardgame.chess.net.ics;

import java.util.regex.Matcher;

/** Ratings are playing strength estimates on the server.
 */
public class ICSRating {

   int rating;
   boolean isProvisional,
           isEstimated,
	   isNotApplicable,
	   isNotSet;

   public ICSRating () {
   }

   public ICSRating (String s) throws NumberFormatException {
      set(s);
   }

   public void set (int rate) {
      rating = rate;
   }

   /** gets the numeric rating.  If no number is applicable such as the 
    *  case for unset ratings then a 0 is returned.
    */
   public int get () { return rating; }

   public boolean isNotSet () { return  isNotSet; }
   public boolean isNotApplicable () { return  isNotApplicable; }
   public boolean isProvisional () { return  isProvisional; }
   public boolean isEstimated () { return  isEstimated; }

   public void setNotApplicable (boolean t) {
      isNotApplicable = t;
   }

   public void setProvisional (boolean t) {
      isProvisional = t;
   }

   public void setEstimated (boolean t) {
      isEstimated = t;
   }

   public void set (String s) throws NumberFormatException {
         //the rating
	 if (s.equals("UNR")) 
	    isNotSet = true;
         else if (s.charAt(0) == '-')
	    isNotSet = true;
	 else if (s.charAt(0) == '+') 
	    isNotApplicable = true;
	 else {
	    if (!Character.isDigit(s.charAt(s.length() -1))) {
	       rating = Integer.parseInt(s.substring(0,s.length()-1));

	       //suffixes
	       if (s.charAt(s.length() -1) == 'P')
		  isProvisional = true;
	       else if (s.charAt(s.length() -1) == 'E')
		  isEstimated = true;
	    }
	    else
               rating = Integer.parseInt(s);

	 }
   }

   public String toString () {
      String s = null;

      if (isNotSet)
         s = "----";
      else if (isNotApplicable)
         s = "++++";
      else if (isProvisional)
         s = rating + "P";
      else if (isEstimated)
         s = rating + "E";
      else 
         s = "" + rating;

      return s;
   }
}

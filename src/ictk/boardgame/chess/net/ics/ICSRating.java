/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSRating.java,v 1.4 2003/08/24 09:53:00 jvarsoke Exp $
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

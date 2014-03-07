/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSDate.java,v 1.2 2003/08/24 05:44:49 jvarsoke Exp $
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

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.*;

public class ICSDate {
      /** format: Sun Nov  3, 21:11 CET 2002*/
   public static final String REGEX 
       = "("          //whole thing
       + "(\\w{3})"   //DoW
       + "\\s" 
       + "(\\w{3})"   //Month
       + "\\s+"
       + "(\\d+)"     //DoM
       + ",\\s"
       + "(\\d+)"     //hour
       + ":"
       + "(\\d{2})"   //minute
       + "\\s"
       + "(\\w+)"     //TimeZone
       + "\\s"
       + "(\\d{4})"   //Year
       + ")";

   public static final Pattern pattern;

   static {
      pattern = Pattern.compile(REGEX);
   }

     /** the time zone the date was offered in */
   protected Calendar calendar;
   protected String original,
                    tzoneID,
		    dayOfWeek,
		    month;
   protected int    year, day, hour, minute;

   public ICSDate () {
   }

   public ICSDate (String str) {
      set(str);
   }

   public void set (String str) throws IllegalArgumentException {
      Matcher m = pattern.matcher(str);

      if (!m.find())
         throw new IllegalArgumentException("Can't understand " + str);

      tzoneID = m.group(7);
      if (tzoneID.equals("???")) tzoneID = null;

      dayOfWeek = m.group(2);
      month = m.group(3);

      int i = 0;
      try {
	 day = Integer.parseInt(m.group(i=4));
	 hour = Integer.parseInt(m.group(i=5));
	 minute = Integer.parseInt(m.group(i=6));
	 year = Integer.parseInt(m.group(i=8));
      }
      catch (NumberFormatException e) {
         throw new IllegalArgumentException("Can't understand " + str);
      }
   }

   public String getMonthID () { return month; }
   public int getYear ()  { return year; } 
   public int getDay () { return day; }
   public int getHour () { return hour; }
   public int getMinute () { return minute; }
   public String getTimeZoneID () { return tzoneID; }
   public String getDayOfWeekID ()  { return dayOfWeek; }

   public void setMonth (String m) { month = m; }
   public void setYear (int y) { year = y; }
   public void setDay (int dayOfMonth) { day = dayOfMonth; }
   public void setHour (int twentyfour) { hour = twentyfour; }
   public void setMinute (int min) { minute = min; }
   public void setTimeZone (String id) { tzoneID = id; }

   /** Returns a String in the common ICS date format.
    *  Format: <pre>Sun Nov  3, 21:11 CET 2002</pre>
    */
   public String toString () {
      //Sun Nov  3, 21:11 CET 2002
      StringBuffer sb = new StringBuffer(27);
         sb.append(dayOfWeek)
	   .append(" ")
	   .append(month)
	   .append(" ");

         if (day < 10)
	    sb.append(" ");

         sb.append(day)
	   .append(", ");

         if (hour < 10)
	    sb.append("0");

         sb.append(hour)
	   .append(":");

	 if (minute < 10)
	    sb.append("0");

	 sb.append(minute)
	   .append(" ");

	 if (tzoneID == null)
	    sb.append("???");
	 else
	   sb.append(tzoneID);

	 sb.append(" ")
	   .append(year);

      return sb.toString();
   }
}

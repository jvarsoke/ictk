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

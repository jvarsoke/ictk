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
import java.util.Calendar;
import java.util.Properties;
import ictk.util.Log;
import java.util.Enumeration;

/* GameInfo ******************************************************************/
/** GameInfo contains all the information about the players of the game,
 *  where the game was played, what where the specific game/rule conditions
 *  of the game including time controls.
 */
public abstract class GameInfo {
   public static final long DEBUG = Log.GameInfo;
   public Properties props = new Properties();  //for extra data we don't use

   protected Player[] players;
   protected Calendar date;
   protected String event,
                    site,
		    round,
		    subround;
   protected Result result;
   protected int    year, month, day;

   public GameInfo () {
   }

   //Accessors/////////////////////////////////////////////////////////////
   /* getPlayers *************************************************************/
   /** gets the entire players array.  There is no way to set players given
    *  in this abstract class since this should be game specific.
    */
   public Player[] getPlayers () { return players; }
   public String getEvent ()    { return event; }
   public String getSite ()     { return site; }
   public Calendar getDate ()   { return date; }
   public String getRound ()    { return round; }
   public String getSubRound () { return subround; }
   public int    getYear ()     { return year; }
   public int    getMonth ()    { return month; }
   public int    getDay ()      { return day; }

   public Result getResult ()   { return result; }

   /* getAuxilleryProperties *************************************************/
   /** returns the Porperties object containing all the auxillery data
    *  that you want to save about the game but is not standard for this
    *  type of game.
    */
   public Properties getAuxilleryProperties () { return props; };

/* for when I figure out how the Calendar object really works
   public String getDateString () {
      if (date == null)
         return "????.??.??";
      StringBuffer sb = new StringBuffer(14);
      int i = -1;

         if (date.isSet(Calendar.YEAR))
            sb.append(date.get(Calendar.YEAR));
	 else
	    sb.append("????");

	 sb.append(".");

         if (date.isSet(Calendar.MONTH)) {
	    i = date.get(Calendar.MONTH) +1;
	    if (i < 10)
	       sb.append("0");
	    sb.append(i);
	 }
	 else
	    sb.append("??");
	 
	 sb.append(".");

         if (date.isSet(Calendar.DAY_OF_MONTH)) {
	    i = date.get(Calendar.DAY_OF_MONTH);
	    if (i < 10)
	       sb.append("0");
	    sb.append(i);
	 }
	 else
	    sb.append("??");

      return sb.toString();
   }
*/

   /* getDateString **********************************************************/
   /** returns the date string in typical YYYY.MM.DD format.  If one of
    *  the values is not set it will be replaced by ????.??.?? accordingly.
    */
   public String getDateString () {
      StringBuffer sb = new StringBuffer(10);
      if (year > 0) {
         if (year < 10)
	    sb.append(" ");
	 if (year < 100) 
	    sb.append(" ");
	 if (year < 1000)
	    sb.append(" ");
	 sb.append(year);
      }
      else
         sb.append("????");

      sb.append(".");

      if (month > 0) {
         if (month < 10)
	    sb.append("0");
	 sb.append(month);
      }
      else
         sb.append("??");

      sb.append(".");

      if (day > 0) {
         if (day < 10)
	    sb.append("0");
	 sb.append(day);
      }
      else
         sb.append("??");

      return sb.toString();
   }


   //Mutators/////////////////////////////////////////////////////////////
   public void setEvent (String event)    { this.event = event; }
   public void setSite (String site)      { this.site = site; }
   public void setDate (Calendar date)    { this.date = date; }
   public void setRound (String round)    { this.round = round; }
   public void setSubRound (String round) { this.subround = round; }
   public void setYear (int i)            { year = i; }
   public void setMonth (int i)           { month = i; }
   public void setDay (int i)             { day = i; }
   public void setResult (Result res)     { result = res; }

   /* setAuxilleryProperties **********************************************/
   /** this sets the entire properiest list to whatever you're sending it.
    */
   public void setAuxilleryProperties (Properties p) { props = p; };

   /*add******************************************************************/
   /** extra data that we don't use might be in an imported file, and we'd 
    *like to keep it.  This is put in Auxillery Properties
    */
   public void add (String key, String value) {
      props.setProperty(key, value);
   }

   /*get******************************************************************/
   /** retrieve the Aux data.
    */
   public String get (String key) {
      return props.getProperty(key);
   }

   /* toString ***********************************************************/
   /** only used for debugging.
    */
   public String toString () {
      String tmp = null;

         tmp = "[Event: " + event + "]\n"
	     + "[Site:  " + site  + "]\n"
	     + "[Date:  " + getDateString() + "]\n"
	     + "[Round: " + round + "]\n"
	     + "[SubRound: " + subround + "]\n"
	     + "[Result: " + result + "]\n";
	 return tmp;
   }

   /* equals ***************************************************************/
   /** the test for equality against all data in the object
    */
   public boolean equals (Object o) {
      if (o == this) return true;
      if ((o == null) || (o.getClass() != this.getClass()))
         return false;

      GameInfo gi = (GameInfo) o;
      boolean t = true;

         if (Log.debug) Log.debug(DEBUG, "checking for equality");

         if (t && players == gi.players)
	    t = true;
	 else if (t
	     && players != null && gi.players != null 
	     && players.length == gi.players.length) {

	    for (int i=0; t && i <  players.length; i++) {
	       t = t && isSame(players[i], gi.players[i]);

	       if (Log.debug && !t) 
		  Log.debug2(DEBUG, 
		     "players[" + i + "]: " + players[i]
		     + " / " + gi.players[i]);
	    }
	 }

	 if (t) {
	    t = t && isSame(event, gi.event);
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "event: " + event 
		  + " / " + gi.event);
	 }

	 if (t) {
	    t = t && equalDates(gi.date);
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "date: " + date 
		  + " / " + gi.date);
	 }

	 if (t) {
	    t = t && isSame(round, gi.round);
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "round: " + round 
		  + " / " + gi.round);
	 }

	 if (t) {
	    t = t && isSame(subround, gi.subround);
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "subround: " + subround 
		  + " / " + gi.subround);
	 }

	 if (t) {
	    t = t && isSame(result, gi.result);
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "result: " + result
		  + " / " + gi.result);
	 }

	 if (t) {
	    t = t && isSame(props, gi.props);
	    if (Log.debug && !t) 
	       Log.debug2(DEBUG, 
	          "aux: " + props
		  + " / " + gi.props);
	 }

	 if (t && Log.debug)
	    Log.debug2(DEBUG, "equal");
      return t;
   }

   /* isSame ***********************************************************/
   /** makes sure both are either null or equals()
    */
   protected boolean isSame (Object o, Object p) {

      return (o == p) 
             || (o != null && o.equals(p));
   }

  
   /* equalDates ******************************************************/
   /** compares the YEAR MONTH and DAY.  What is set in one has
    *  to be set in the other.  This is because Calendar.equals()
    *  is rather daft in comparing things that haven't been set.
    */
   protected boolean equalDates (Calendar cal) {
      boolean t = true;

         if (date == null && date == cal)
	    return true;

	 t = t && cal != null;
	 t = t && date.isSet(Calendar.YEAR) == cal.isSet(Calendar.YEAR);
	 t = t && date.isSet(Calendar.YEAR) 
	       && date.get(Calendar.YEAR) == cal.get(Calendar.YEAR);
	 t = t && date.isSet(Calendar.MONTH) 
	       && date.get(Calendar.MONTH) == cal.get(Calendar.MONTH);
	 t = t && date.isSet(Calendar.DAY_OF_MONTH) 
	       && date.get(Calendar.DAY_OF_MONTH) 
	          == cal.get(Calendar.DAY_OF_MONTH);
      return t;
   }

   /* hashCode *************************************************************/
   public int hashCode () {
      int hash = 5;

      if (players != null)
         for (int i=0; i < players.length; i++)
	    hash = 31 * hash + ((players[i] == null) ? 0 : players[i].hashCode());
      hash = 31 * hash + getDateString().hashCode();
      hash = 31 * hash + ((event == null) ? 0 : event.hashCode());
      hash = 31 * hash + ((site == null) ? 0 : site.hashCode());
      hash = 31 * hash + ((round == null) ? 0 : round.hashCode());
      hash = 31 * hash + ((subround == null) ? 0 : subround.hashCode());

      return hash;
   }

   /* dump *****************************************************************/
   /** diagonostic tool
    */
   public void dump () {
      StringBuffer sb = new StringBuffer();
         sb.append("##GameInfo Dump")
           .append(toString())
           .append("Aux Data:");
      if (props == null)
         sb.append("None");
      else {
         Enumeration<?> enu = props.propertyNames();
	 String key = null;
	 while (enu.hasMoreElements()) {
	    key = (String) enu.nextElement();
	    sb.append(key).append(" = ")
	      .append(props.getProperty(key, null));
	 }
      }
   }
}

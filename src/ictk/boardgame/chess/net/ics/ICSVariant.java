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

/** Types of games available on ICS servers.
 */
public class ICSVariant {
   public static final int UNKNOWN        =  0,
		           UNTIMED        =  1,
		           STANDARD       =  2,
                           BLITZ          =  3,
		           BULLET         =  4,
		           LIGHTNING      =  5,
		           MINUTE_5       =  6,
		           MINUTE_1       =  7,
		           FISCHER_RANDOM =  8,
		           BUGHOUSE       =  9,
		           CRAZYHOUSE     = 10,
		           SUICIDE        = 11,
		           LOSERS         = 12,
		           ATOMIC         = 13,
		           KRIEGSPIEL     = 14,
		           WILD           = 15;

   protected int variant;
   protected String name;

   public ICSVariant () {
   }

   public ICSVariant (String s) {
      setType(s);
   }

   public ICSVariant (char c) {
      setType(c);
   }

   public void setType (char c) {
      switch (c) {
         case 'u': variant = UNTIMED; break;
         case 's': variant = STANDARD; break;
         case 'S': variant = SUICIDE; break;
         case 'b': variant = BLITZ; break;
         case 'B': variant = BUGHOUSE; break;
         case 'z': variant = CRAZYHOUSE; break;
         case 'l': variant = LIGHTNING; break;
         case 'L': variant = LOSERS; break;
      }
   }

   public void setType (int type) { variant = type; }
   public int getType () { return variant; }

   public void setType (String type) {
      String vtype = type.toLowerCase();
      
      if ("blitz".equals(vtype))
         variant = BLITZ;
      else if ("lightning".equals(vtype))
         variant = LIGHTNING;
      else if ("standard".equals(vtype)) 
         variant = STANDARD;
      else if ("bughouse".equals(vtype))
         variant = BUGHOUSE;
      else if ("crazyhouse".equals(vtype))
         variant = CRAZYHOUSE;
      else if ("suicide".equals(vtype))
         variant = SUICIDE;
      else if ("losers".equals(vtype))
         variant = LOSERS;

     //FIXME: lots missing
      else {
         variant = UNKNOWN;
	 name = type;
      }
   }

   public String getName () { 
      String rvalue = null;

      //a known variant: names are not stored to reduce memory cost
         switch (variant) {
	    case STANDARD:   rvalue = "standard"; break;
	    case BLITZ:      rvalue = "blitz"; break;
	    case LIGHTNING:  rvalue = "lightning"; break;
	    case BUGHOUSE:   rvalue = "bughouse"; break;
	    case CRAZYHOUSE: rvalue = "crazyhouse"; break;
	    case SUICIDE:    rvalue = "suicide"; break;
	    case LOSERS:     rvalue = "losers"; break;
	    default:
	       rvalue = name;
         }

      return rvalue;
   }

   public boolean isChess () {
      switch (variant) {
         case STANDARD:
	 case BLITZ:
	 case BULLET:
	 case LIGHTNING:
	 case MINUTE_5:
	 case MINUTE_1:
	    return true;
	 default:
	    return false;
      }
   }

   public String toString () {
      return getName();
   }


}

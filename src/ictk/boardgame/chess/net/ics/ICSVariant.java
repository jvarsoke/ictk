/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSVariant.java,v 1.2 2003/08/20 15:42:50 jvarsoke Exp $
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

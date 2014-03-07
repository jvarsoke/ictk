/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: AmbiguousChessMoveException.java,v 1.1.1.1 2003/03/24 22:38:09 jvarsoke Exp $
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

import java.util.List;
import ictk.boardgame.AmbiguousMoveException;

/* AmbiguousMoveException ***************************************************/
/** some notations use shorthand for moves that relies on a particular
 *  position on the board.  If the position is incorrect the move is
 *  often not intelligible since Ne5 might refer to a Knight on f3 or
 *  a Knight on d3.  This exception is thrown when the move does not
 *  specify a unique piece to move.  This exception tries to give as 
 *  much information as possible to help determine what went wrong.
 */
public class AmbiguousChessMoveException extends AmbiguousMoveException {
     /** an array of pieces that legally move to this desination */
   protected ChessPiece[] pieces;

     /** index of the piece type supposed to move.*/
   protected int p, 
     /** file of origin, if set. */
                 of, 
     /** rank of origin, if set. */
		 or, 
     /** file of destination, if set. */
		 df, 
     /** rank of destination, if set. */
		 dr;

   public AmbiguousChessMoveException () { super(); }
   public AmbiguousChessMoveException (String mesg) { super(mesg); }

   /** 
    * @param mesg a message about the ambiguity
    * @param p index of the piece type supposed to move
    * @param of origin file index
    * @param or origin rank index
    * @param df destination file index
    * @param dr destination rank index
    */
   public AmbiguousChessMoveException (String mesg,
                                   int p,
				   int of, int or, int df, int dr) {
      this(mesg, p,of, or, df, dr, (List) null);
   }

   /** 
    * @param mesg a message about the ambiguity
    * @param p index of the piece type supposed to move
    * @param of origin file index
    * @param or origin rank index
    * @param df destination file index
    * @param dr destination rank index
    * @param dupes list of pieces on the board which this move might refer to
    */
   public AmbiguousChessMoveException (String mesg,
                                   int p,
				   int of,
				   int or,
				   int df,
				   int dr,
				   List dupes) {
      super(mesg);
      this.p = p;
      this.of = of;
      this.or = or;
      this.df = df;
      this.dr = dr;

      if (dupes != null) {
	 Object[] objs = dupes.toArray();
	 pieces = new ChessPiece[objs.length];
	 for (int i=0; i<objs.length; i++)
	    pieces[i] = (ChessPiece) objs[i];
      }
   }

   /** 
    * @param mesg a message about the ambiguity
    * @param p index of the piece type supposed to move
    * @param of origin file index
    * @param or origin rank index
    * @param df destination file index
    * @param dr destination rank index
    * @param dupes list of pieces on the board which this move might refer to
    */
   public AmbiguousChessMoveException (String mesg,
                                   int p,
				   int of,
				   int or,
				   int df,
				   int dr,
				   ChessPiece[] dupes) {
      super(mesg);
      this.p = p;
      this.of = of;
      this.or = or;
      this.df = df;
      this.dr = dr;
      pieces = dupes;
   }

   /* getPieces **************************************************************/
   /** returns an array of pieces that are candidates which could possibly
    *  move to this destination.
    */
   public ChessPiece[] getPieces() {
      return pieces;
   }

   public String toString () {
      StringBuffer sb = new StringBuffer();

      sb.append(getMessage()).append(" ");
      if (p != '\0' && p != ' ')
         sb.append(p);
      if (of != '\0' && of != ' ')
         sb.append(of);
      if (or != '\0' && of != ' ')
         sb.append(or);
      if (df != '\0' && of != ' ')
         sb.append(df);
      if (dr != '\0' && of != ' ')
         sb.append(dr);

      return sb.toString();
   }
}

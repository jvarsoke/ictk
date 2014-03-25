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

import java.util.List;
import ictk.boardgame.Move;
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
   public static final long serialVersionUID = 1L;

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
      this(mesg, p,of, or, df, dr, (List<ChessPiece>) null);
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
				   List<ChessPiece> dupes) {
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

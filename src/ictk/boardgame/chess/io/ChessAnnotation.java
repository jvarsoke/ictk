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

package ictk.boardgame.chess.io;

import ictk.boardgame.io.Annotation;

/* ChessAnnotation ***********************************************************/
/** ChessAnnotation - typicall annotations and comments on a move.
 *  Primarily these are Numeric Annotation Glyphs and String comments.
 */
public class ChessAnnotation implements Annotation {
      /**traditional verbose annotation */
   protected String comment;
      /** NumericAnnotationGlyphs*/
   protected short[] nags;

   public ChessAnnotation () {
   }

   public ChessAnnotation (String comment) {
      this.comment = comment;
   }

   //Suffix///////////////////////////////////////////////////////////////////
   /** this returns the first nag in the nags list only if it is one of the
    *  nags that can be used as a suffix to a move. Such as (?,!!,??,!?,?!)
    *  If not, then 0 is returned, even if nags[0] has a value.
    */
   public short getSuffix () { 
      if (nags != null 
          && nags.length > 0 && nags[0] > 0 && NAG.isSuffix(nags[0]))
         return nags[0];
      else
         return 0;
   }

   /* addNAG *****************************************************************/
   /** add a new NumericAnnotationGlyph (NAG) to the annotation.  There is no 
    * theoretical limit on the max number of NAGs for a particular annotation.
    */
   public void addNAG (int nag) { 
      int size = 0;
      short[] newNag = null;

         if (nags != null)
	    size = nags.length;

	 newNag = new short[size+1];

	 if (nags != null)
	    System.arraycopy(nags, 0, newNag, 0, nags.length);

	 newNag[newNag.length-1] = (short) nag;
	 nags = newNag;
   }

   /* setNAG ****************************************************************/
   /** sets a nag to a specific value.
    *  @throws ArrayIndexOutOfBoundsException
    */
   public void setNAG (int i, int nag) {
      if (nags == null || nags.length >= i)
         throw new ArrayIndexOutOfBoundsException();
      else {
         nags[i] = (short) nag;
      }
   }

   /* getNAG ****************************************************************/
   /* gets a NAG from the internal datastructure
    * @throws ArrayIndexOutOfBoundsException
    */
   public short getNAG (int i) {
      if (nags == null)
         throw new ArrayIndexOutOfBoundsException();
      return nags[i];
   }

   /* getNAGs ***************************************************************/
   /** returns a short array of NAGs for this annotation.
    *
    *  NOTE: this is a reference to the original array, not a copy.
    *        However this might change at any time, so don't rely on this.
    */
   public short[] getNAGs () { 
      /*
      //with compression
      short[] rnags = null;
      int count = 0;

         if (nags == null) 
	    return null;

         for (int i=0; i < nags.length; i++)
	    if (nags[i] != 0)
	       count++;
	 if (count < nags.length) {
	    rnags = new short[count];
	    count = 0;
	    for (int i=0; i < nags.length; i++)
	       if (nags[i] != 0)
	          rnags[count++] = nags;
	    //compress the nags array
	    nags = rnags;
	 }
      */
	    
      return nags;
   }

   /* getNAGString **********************************************************/
   /** returns the array of NAGs as a string delimited by <space>.  This will
    *  include suffixes and numeric NAGs.  Those NAGs that have a non-numeric
    *  notation will be represented as such.
    *  
    *  @param allNumeric represents all NAGs numerically.
    *
    *  @return null if there are no NAGs associated with this annotation
    */
   public String getNAGString (boolean allNumeric) {
      if (nags == null) return null;

      StringBuffer sb = new StringBuffer();
      String suff = null;
      int count = 0;

         for (int i=0; i<nags.length; i++) {
	    suff = NAG.numberToString(nags[i], allNumeric);

	    if (suff != null) {
	       if (count++ > 0)
	          sb.append(" ");
	       sb.append(suff);
	    }
	 }
      return sb.toString();
   }


   /* getNAGString ***********************************************************/
   /** calls getNAGString(false)
    */
   public String getNAGString () {
      return getNAGString(false);
   }

   /* removeNAG *************************************************************/
   /** removes the NAG from the NAG list.
    */
   public void removeNAG (int i) {

      short[] newNag = null;

      if (nags == null || i >= nags.length)
         throw new ArrayIndexOutOfBoundsException();

      if (nags.length == 1) {
         nags = null;
	 return;
      }

      nags[i] = 0;
      newNag = new short[nags.length-1];
      if (i == 0)
         System.arraycopy(nags, 1, newNag, 0, newNag.length);
      else {
	 //i=1 nags.length=3, new=2 
	 //0-0 are copied
         System.arraycopy(nags, 0, newNag, 0, i);
	 //2, 1, 2- (1-1) 2 is copied to new[1]
	 System.arraycopy(nags, i+1, newNag, i, newNag.length - i);
      }
      nags = newNag;
   }

   //Comment///////////////////////////////////////////////////////////////////
   public void setComment (String com) { comment = com; }
   public void appendComment (String com) { 
      if (comment != null) comment = comment + com; 
      else comment = com;
   }
   public String getComment () { return comment; }

   /* equals *****************************************************************/
   /** this is an exact, one for one, comparison
    */
   public boolean equals (Object obj) {
      if (this == obj) return true;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;

      ChessAnnotation anno = (ChessAnnotation) obj;
      boolean t = true;

      //check out the NAGs
      if (nags == null)
         if (nags == anno.nags)
            t = true;
	 else
	    t = false;
      else if (anno.nags != null) {
         t = t && nags.length == anno.nags.length;
	 int i = 0;
	 while (i < nags.length && t) {
	    t = nags[i] == anno.nags[i];
	    i++;
	 }
	 if (i != nags.length)
	    t = false;
      }

      //comment
      if (t && comment == null) {
         if (comment != anno.comment)
	    t = false;
      }
      else if (t && anno.comment != null) {
         t = comment.equals(anno.comment);
      }
         
      return t;
   }

   /* hashCode ***************************************************************/
   /** hashes on the comment and the nags
    */
   public int hashCode () {
      int hash = 7;

      hash = 31 * hash + ((comment == null) ? 0 : comment.hashCode());
      if (nags != null)
         for (int i=0; i < nags.length; i++)
	    hash = 31 * hash + nags[i];

      return hash;
   }

   /* toString ***************************************************************/
   /** only useful for diagnostics.
    */
   public String toString () {
      return dump();
   }

   /* dump *****************************************************************/
   /** used for diagnostics only
    */
   public String dump () {
      StringBuffer sb = new StringBuffer();

      sb.append("comment: ")
        .append(comment)
	.append(" nags: ");
      if (nags != null)
         for (int i=0; i < nags.length; i++) {
	    sb.append(nags[i]).append(" ");
	 }
      return sb.toString();
   }

}

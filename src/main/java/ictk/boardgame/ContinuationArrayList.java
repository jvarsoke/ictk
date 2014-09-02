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


/* ContinuationArrayList *****************************************************/
/**
 * ContinuationArrayList uses arrays internally to represent the 
 * banching structure.  Arrays are compacted when null items are
 * created.
 */
public class ContinuationArrayList implements ContinuationList { 
      /**the move that precedes all the variations*/
   protected Move    departureMove;
      /**array of next moves, allowing for treeing from main line*/
   protected Move[]  branches;  //next moves (tree)

   //Constructors/////////////////////////////////////////////////////////////
   public ContinuationArrayList (Move m) {
      branches = new Move[1];
      setDepartureMove(m);
   }

   public ContinuationArrayList () {
      this(null);
   }

   /* setDepartureMove() ************************************************/
   protected void setDepartureMove (Move m) { departureMove = m; }

   /* getMove() *********************************************************/
   public Move getDepartureMove () { return departureMove; }

   /* isTerminal() ******************************************************/
   public boolean isTerminal () { 
      return getMainLine() == null && sizeOfVariations() == 0; 
   }

   /* setMainLineTerminal() *********************************************/
   public boolean setMainLineTerminal () {
      if (branches == null || branches[0] == null) 
         return false;  //nothing to do
      else {
         add(null, true);  //add null as main line and bump everyone
	 return true;
      }
   }

   //Datastructure Operations////////////////////////////////////////////
   /* exists ************************************************************/
   public boolean exists (int variation) {
      try {
	 if (branches != null && branches[variation] != null)
	    return true;
	 else
	    return false;
      }
      //user asked for a branch that clearly isn't there
      catch (ArrayIndexOutOfBoundsException e) {
         return false;
      }
   }

   /* exists ************************************************************/
   public boolean exists (Move move) {
      return getIndex(move) != -1;
   }

   /* hasMainLine() *****************************************************/
   public boolean hasMainLine () { return exists(0); }


   /* getMainLine () ****************************************************/
   public Move getMainLine () { 
      if (branches == null) return null; 
      else return branches[0]; 
   }

   /* hasVariations() ***************************************************/
   public boolean hasVariations () { return sizeOfVariations() > 0; }

   /* get() *************************************************************/
   public Move get (int i) { 
      if (i == 0 && branches == null) return null; 
      else return branches[i]; 
   }

   /* set() *************************************************************/
   /*
   //FIXME: not implemented
   public Move set (int i, Move m) {
      Move old = null;
      
      if (i == 0 && branches == null) {
         branches = new Move[1];
	 branches[0] = m;
      }
      else if (i < size()) {
         old = branches[i];
         branches[i] = m;
	 compressVariations();
      }
      else if (m != null)
         add(m, false);

      return old;
   }
   */

   /* size() **************************************************************/
   public int size () {
      int len = 0;

      if (branches == null) return 0;
      else {
         len = branches.length;
	 if (len == 1 && branches[0] == null)
	    len = 0;
      }

      return len;
   }

   /* sizeOfVariations() ***************************************************/
   public int sizeOfVariations () { 
      if (branches == null) 
         return 0;
      else 
         return branches.length - 1;
   }

   /* add() *****************************************************************/
   public void add (Move m, boolean isMain) {
      Move[] tmp = null;
      
         assert (departureMove == null 
	       || departureMove.getHistory().getCurrentMove() == departureMove)
	    : "You cannot add a continuation to moves other than the "
	    + "last move played on the board (History.getCurrentMove())";

         //never had a next move
         if (branches == null) {

            //add main line
	    if (isMain) {
	       branches = new Move[1];
	       branches[0] = m;
	    }
	    //add variation w/o main line
	    else {
	       branches = new Move[2];
	       branches[1] = m;
	    }
         }
	 else if (isMain && branches[0] == null) 
	       branches[0] = m;
         //has a main line and possibily variations
	 else {
	    //increase the array by one to fit the new move
	    tmp = new Move[branches.length+1];

	    //if this is main put the move in the 0th element
	    if (isMain) {
	       tmp[0] = m;
	       System.arraycopy(branches, 0, tmp, 1, branches.length);
	    }
	    //this move is a variation and should go at the end of the array
	    else {
	       System.arraycopy(branches, 0, tmp, 0, branches.length);
	       tmp[tmp.length-1] = m;
	    }
	    branches = tmp;
	 }

	 //who's your daddy?
	 if (m != null)
            m.prev = departureMove;
   }

   /* add() **************************************************************/
   public void add (Move m) {
      Move[] tmp = null;
      assert (departureMove.getHistory().getCurrentMove() == departureMove)
	 : "You cannot add a continuation to moves other than the "
	 + "last move played on the board (History.getCurrentMove())";
      if (branches == null) {
         branches = new Move[1];
	 branches[0] = m;
      } 
      else if (branches.length == 1 && branches[0] == null)
         branches[0] = m;
      else {
         tmp = new Move[branches.length+1];
	 System.arraycopy(branches, 0, tmp, 0, branches.length);
	 tmp[tmp.length-1] = m;
      }

      //who's your daddy?
      m.prev = departureMove;
   }

  /* getIndex () ********************************************************/
   public int getIndex (Move m) {
      int i = 0;

         if (branches == null)
	    return -1;

         for (i=0; i < branches.length ; i++) { 
             if (branches[i] == m)
                break;
         }
         if (i < branches.length)
            return i;
         else
            return -1;
   }

   /* find ***************************************************************/
   public Move[] find (Move m) {
      Move[] moves = null;
      int[] idx = findIndex(m);

	 if (idx != null) {
	    moves = new Move[idx.length];
	    for (int i = 0; i < idx.length; i++)
	       moves[i] = branches[idx[i]];
	 }
	 return moves;
   }

   /* findIndex **********************************************************/
   public int[] findIndex (Move m) {
      int i        = 0;
      int count    = 0; //found
      int[] rvalue = null,
            tmp    = null;

	 if (branches == null) 
	    return null;
	 else {
	    tmp = new int[branches.length];

	    for (i=0; i < branches.length ; i++) { 
	       if (branches[i] != null && branches[i].equals(m))
		  tmp[count++] = i;
	    }

	    if (count == 0)
	       return null;
	    else if (count == branches.length)
	       return tmp;
	    else {
	       rvalue = new int[count];
	       System.arraycopy(tmp, 0, rvalue, 0, rvalue.length);
	       return rvalue;
	    }
         }
   }

   /* protected boolean compressVariations () ***************************/
   /** compresses the branches array so there are no nulls
    *  in the list except for possibily the main line (0).  
    *  If the list is already
    *  full the list is not affected.  If the list is empty
    *  then no action occurs.
    *  
    *  @return true if compression occured
    *  @return false compression was not needed
    */
   protected boolean compressVariations () {
      boolean compressed = false;
      Move[] compBranches = null;
      int  count = 0;

         for (int i=1; i < branches.length; i++)
	    if (branches[i] != null) count++;
	    else
	       compressed = true;

         if (compressed && count > 0) {
	    compBranches = new Move[count+1]; //+1 for main line

	    count = 0;
	    for (int i=0; i < branches.length; i++) 
	       if (i < 1 || branches[i] != null)
		  compBranches[count++] = branches[i];

	    branches = compBranches;
	 }

      return compressed && count > 0;
   }

   /* remove() ***********************************************************/
   public void remove (int i) {
      if (branches == null)
         throw new ArrayIndexOutOfBoundsException("no moves to remove");

      if (i == -1)
         throw new ArrayIndexOutOfBoundsException(
	    "move does not exist in continuation list");

      if (branches[i] != null) {
         //must rewind first to not but board in bad state
         if (branches[i].isExecuted())
	    departureMove.getHistory().goTo(departureMove);

         branches[i].dispose();
	 branches[i] = null;

	 //compression needed even for removal of main line
	 compressVariations();
      }
   }

   public void remove (Move m) {
      remove(getIndex(m));
   }

   /* removeAll() ********************************************************/
   public void removeAll () {
      if (branches != null)

      for (int i = 0; i < branches.length; i++) {

         //must rewind first to not but board in bad state
         if (branches[i].isExecuted())
	    departureMove.getHistory().goTo(departureMove);

         branches[i].dispose();
	 branches[i] = null;
      }
      branches = null;
   }

   /* removeAllVariations () *********************************************/
   public void removeAllVariations () {

         if (branches != null && sizeOfVariations() > 0) {
	    for (int i = 1; i < branches.length; i++) {
	       if (branches[i] != null) {
		  //must rewind first to not put board in bad state
		  if (branches[i].isExecuted())
		     departureMove.getHistory().goTo(departureMove);

		  branches[i].dispose();
		  branches[i] = null;
	       }
	    }
	    compressVariations();
	 }
   }

   /* dispose () *********************************************************/
   /** reclaims all resources and recursively deletes all branch moves.
    */
   public void dispose () {
         departureMove = null;
	 removeAll();
	 branches = null;
   }

   //promote, demote variations///////////////////////////////////////////

   /* promote() **********************************************************/
   public int promote (Move move, int num) {
      if (num < 0) 
         throw new IllegalArgumentException(
            "cannot perform negative promotion");

      Move tmp = null;
      int newIndex = 0;
      int oldIndex = getIndex(move);

      if (oldIndex == -1) 
         throw new NullPointerException ("Move is not a current variation");
      else
         move = branches[oldIndex]; //make sure to get the real move

      if (num != 0) 
         newIndex = oldIndex - num;

      if (newIndex < 0)
         throw new ArrayIndexOutOfBoundsException
	    ("Move cannot be promoted beyond the main line");

      //destination is main and main is null
      if (newIndex == 0 && branches[0] == null) {
         //note: order of next 2 lines important
	 branches[oldIndex] = null;
         branches[0] = move;
	 compressVariations();
      }
      //destination is not main or main is not null
      else {
         branches[oldIndex] = null;
	 for (int i = newIndex; i <= oldIndex; i++) {
	    tmp = branches[i];
	    branches[i] = move;
	    move = tmp;
	 }
	 compressVariations();
      }

      return newIndex;
   }
   
   /* demote() ***********************************************************/
   public int demote (Move move, int num) {
      if (num < 0) 
         throw new IllegalArgumentException(
            "cannot perform negative demotion");

      Move tmp = null;
      int newIndex = 0;
      int oldIndex = getIndex(move);

      if (oldIndex == -1) 
         throw new NullPointerException ("Move is not a current variation");
      else
         move = branches[oldIndex];  //make sure to get the real move

      if (num != 0) 
         newIndex = oldIndex + num;
      else
         newIndex = branches.length -1;

      if (newIndex >= branches.length)
         throw new ArrayIndexOutOfBoundsException
	    ("Move cannot be demoted beyond the end of the list");

      for (int i = oldIndex; i < newIndex; i++) {
	 branches[i] = branches[i+1];
      }
      branches[newIndex] = move;

      return newIndex;
   }

   /* dump ************************************************************/
   /** for debugging
    */
   public String dump () {
      StringBuffer sb = new StringBuffer();

         sb.append("branches(")
	   .append(branches.length).append("): \n");
	 for (int i=0; i<branches.length; i++) {
	    sb.append("   branches[").append(i).append("]:");
	    if (branches[i] == null)
	       sb.append("null");
	    else
	      sb.append(branches[i].toString());

	    sb.append("\n");
	 }
      return sb.toString();
   }
}

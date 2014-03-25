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

/* ContinuationList ******************************************************/
/** This class facilitiates the branching aspect of the History tree.
 *  There are three main features the "departure move", which is the 
 *  the move from which all the continuations originate.  There is the
 *  "main-line", which is the primary continuation from the "move".  And
 *  then there are "variations".  These are auxilery or alternate
 *  continuations.
 *  <p>
 *  This class allows the variations to be promoted in their order and
 *  all the way up to the main-line, which, though not a "variation" in
 *  the sense used for this class, is the top most variation in regards
 *  to the ordered variations.  In other words: if you promote a variation
 *  high enough it will become the main-line.
 *  <p>
 *  Note: the main-line is the only continuation that is allowed to be null.
 *        this is often used when the game ends with the departure move
 *        and the variations show possible continuations.
 */
public interface ContinuationList { 
   //Datastructure Operations////////////////////////////////////////////
   /* getDepartureMove () ************************************************/
   /** gets the departure move, that is the move that comes before all of
    *  these variations.
    */
   public Move getDepartureMove ();

   /* setDepartureMove() *************************************************/
   /** sets the departure move.
    *  Note: you can really screw things up by changing the departure
    *        move to something other than what was originally in the
    *        history list.
    */
   //protected void setDepartureMove (Move m);

   /* isTerminal() *******************************************************/
   /** no main line and no variations exist.  
    *  The move that owns this ContinuationList a terminal node.  In this 
    *  case sizeOfVariation() == 0 and !hasMainLine().  
    *  This is also the same as isEmpty();
    */
   public boolean isTerminal ();

   /* setMainLineTerminal() **********************************************/
   /** set the mainline to null.  If there existed a main line then it is
    *  bumped down to a variation.  All other variations are similarly 
    *  bumped.
    *
    *  @return true if there previously existed a mainline
    *  @return false if no mainline existed, or the mainline was set to null
    */
   public boolean setMainLineTerminal();

   /* exists *************************************************************/
   /** this does not throw ArrayOutOfBoundsException if your ask
    *  an index out of range, you just get false returned.
    * 
    *  @param  variation which branch to look at (0) is the main line
    *  @return false     there is no variation on that line
    *  @return true      there is a move after this one
    */
   public boolean exists (int variation);

   /* exists *************************************************************/
   /** determines if the move is in the current list of continuations
    *
    *  @param move is this move in the list of continuation
    */
   public boolean exists (Move move);

   /* hasMainLine *******************************************************/
   /** calls exists(0).  This queries the move to see if there is
    *  a main line that follows.
    */
   public boolean hasMainLine ();

   /* getMainLine () ****************************************************/
   /** get the next move in the game history.  If the move branches
    *  (if there are several variations of the game after this move)
    *  then this function returns the main line of descent for this
    *  game.
    *
    *  @return null if isTerminal() or the mainline has been set to null
    */
   public Move getMainLine ();

   /* hasVariations() ***************************************************/
   /* continuation moves exist other than the mainline.
    */
   public boolean hasVariations ();

   /* get() *************************************************************/
   /*  gets the next move in the game history where "i" is the 
    *  continuation you want.  0 is always main-line whether null or not
    *  no other index may return null. 
    *
    *  @param i the index of the continuation (0 is main-line)
    *  @throws This could throw ArrayIndexOutOfBoundsException
    */
   public Move get (int i);

   /* set() ***************************************************************/
   /** sets continuation node i to Move m.  The reference to the old move
    *  is returned if there was one.  
    *
    *  Note: setting continuations besides the mainline to null will
    *        remove the variation list and compress the list, which
    *        will shift the index of all variations greater than i by -1
    *
    *  @param if i is greater than the last index currently in the list
    *         then add(m, false) is called and the move is appended to the
    *         variation list.
    *
    *  NOTE: this is not implemented due to logistic problems concerning
    *        setting on an index that contains an executed move.
    */
    /*
   public Move set (int i, Move m);
   */

   /* size() **************************************************************/
   /** gets the number of continuation (variations + the main-line) that
    *  exist after this move.  This isn't as useful as it may seem.
    *  
    *  @return 0 if no variations and main-line is null
    *  @return 1 if no variations but main-line is !null
    *  @return >1 if 1 variation regardless of the value of main-line
    */
   public int size ();

   /* sizeOfVariations() *************************************************/
   /** much more useful than size(), this function returns the number of
    *  variations (continuations minus the main-line).
    */
   public int sizeOfVariations ();

   /* add() **************************************************************/
   /** adds a move to the continuation list.  This does not check to see if
    *  the move is already one of the variations.  This function should
    *  be only accessed through the History class.  If you are to access
    *  it outside of History the depatureMove and History.getCurrentMove()
    *  must be the same.  That is, you can only add a move to the
    *  continuation list of the last move played.
    *
    *  @param  isMain If true then the move added will be the first 
    *          continuation.  All other moves will be bumped down (if the 
    *          mainline wasn't null to begin with). LIFO
    *          if isMake is not true then the move will be the last branch,
    *          in otherwords, appended to the list. FIFO
    */
   public void add (Move m, boolean isMain);

   /* add() ***************************************************************/
   /** This adds the move to the continuation list.  If no moves exist
    *  (no main-line and no variations) the move added will become the
    *  main-line.  If the main-line has been set to null the move added
    *  will <b>still</b> become the main line (if this is not what you
    *  want you must use add(Move,false)).  If the main-line exists
    *  the move is appended to the end of the variation list. FIFO
    *  <br>
    *  This function should be only accessed through the History class.  
    *  If you are to access it outside of History the depatureMove 
    *  and History.getCurrentMove()
    */
   public void add (Move m);

   /* getIndex() *********************************************************/
   /** gets the index of this Move object in the variation branches.
    *  To search for equavilant moves in the variation list you should
    *  use find(Move) or findIndex(Move) since there could be more than
    *  one possible match.
    *
    *  @return n if move is found
    *  @return -1 if not found
    */
   public int getIndex (Move m);

   /* find ***************************************************************/
   /** searches the continuation list for moves that are equal to the
    *  move submitted.  It is enough for their coordinates to be equal.
    *  Each move that matches will be returned in the Move array.
    *
    *  @return null if no moves match the inputed move.
    */
   public Move[] find (Move m);

   /* findIndex **********************************************************/
   /** searches the continuation list for moves that are equal to the
    *  move submitted.  It is enough for their coordinates to be equal.
    *  Each move that matches will have its index returned in the int 
    *  array.
    *
    *  @return null if no moves match the inputed move.
    */
   public int[] findIndex (Move m);

   /* remove *************************************************************/
   /** removes a variation from the continuation list.
    *  All data from this variation will be destroyed as Move.dispose() is
    *  called recursively down the line.  This list will then be compressed
    *  <b>even</b> if it was the main-line that was removed.  To make the
    *  main-line null you need to call set(0, null);
    *  <br>
    *  If the move to be removed is currently executed the history list
    *  will be rewound to this move's departureMove.
    *
    *  @throws may throw ArrayIndexOutOfBoundException
    */
   public void remove (int i);

   /* remove() ***********************************************************/
   /** removes the move from the continuation list.  All data for this 
    *  continuation will be destroyed as Move.dispose() is called recursively
    *  down the line.  The list will then be compressed <b>even</b> if it
    *  was the main-line that was removed.
    *  <br>
    *  If the move to be removed is currently executed the history list
    *  will be rewound to this move's departureMove.
    *
    *  @throws may throw ArrayIndexOutOfBoundException
    */
   public void remove (Move m);

   /* removeAll () *******************************************************/
   /** makes this move a terminal node.  All variations are destroyed
    *  as Move.dispose() is called recusively down the line for each
    *  variation.
    *  <br>
    *  If the move to be removed is currently executed the history list
    *  will be rewound to this move's departureMove.
    */
   public void removeAll ();

   /* removeAllVariations () *********************************************/
   /** All variations are destroyed as Move.dispose() is called recusively 
    *  down the line for each variation.  The main-line is not affected.
    *  <br>
    *  If the move to be removed is currently executed the history list
    *  will be rewound to this move's departureMove.
    */
   public void removeAllVariations ();

   /* dispose () *********************************************************/
   /** reclaims all resources and recursively deletes all branch moves.
    *  isTerminal() will return true as a result.  This will also unlink
    *  all references to the departure move.
    */
   public void dispose ();

   //promote, demote variations///////////////////////////////////////////

   /* promote () *********************************************************/
   /** promotes the move up the list of continuations.  
    * Note: if a variation is promoted to the main-line and the main-line
    *       is null the "null" will not be bumped down the list.
    *
    * @param num how many places to displace the variations.
    *            If 0 the variation will be promoted to the main line.
    * @return the index of this variation after the promotion
    * @throws IndexArrayOutOfBoundException if you promote past the main line
    * @throws NullPointerException if the move is not in the variation list
    */
   public int promote (Move move, int num);
   
   /* demote () *********************************************************/
   /** demotes the variation moving it down the variation list.  Note:
    *  if the mainline is the only variation and is demoted then the 
    *  resulting mainline will be null and the old mainline will be the
    *  first variation.
    *
    * @param num how many places to displace the variations.
    *            If 0 the variation will be demoted to last variation
    * @return the index of this variation after the promotion
    * @throws IndexArrayOutOfBoundException if demoted past the last variation
    * @throws NullPointerException if the move is not in the variation list
    */
   public int demote (Move move, int num);

   /* dump ************************************************************/
   /** for debugging
    */
   public String dump ();

}

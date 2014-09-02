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

import ictk.boardgame.io.MoveNotation;
import ictk.util.Log;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


/* History ******************************************************************/
/** the History class is a data structure resembling a tree.  As moves are
 *  added to the History they are executed.  The next move added is appended
 *  to the previous move and so on.  By added all the moves for a particular
 *  game you end up with a linked list refered to as the Main-Line.  This is
 *  the true history of how the game was played.  You are able to go
 *  forward and backward through this list.  Going forward with next() 
 *  executes the next move that was on the main-line.  prev() unexecutes
 *  the move on the board.  At this point the board status is exactly
 *  the way it was before the move was played.<br> 
 *  <p>
 *  History also has the ability to hold variations.  This is, of course,
 *  where the Tree idea comes in.  At any position there might be another
 *  another move that could have been played (which isn't the main-line move)
 *  that holds some interest.  You can add this move to the History by
 *  backing up to the previous move and using add() again.  This will add
 *  a variation and create a branch at the previous move where you could
 *  then choose to proceed in either of 2 variations with next(n).<br>
 *  <p>
 *  For example:
 *  <pre>
 *  History history = game.getHistory();
 *  history.add(moveA);
 *  history.add(moveB);
 *  history.prev();  //returning the when moveA was just played
 *  history.add(moveC);
 *  history.add(moveC2);
 *  </pre><br>
 *  in this case the the moves look like this:
 *  <pre>
 *     A-->B
 *      \->C->C2
 *  </pre>
 *  After added the rest of Continuation C you could return to A by 
 *  using the move rewindToLastFork();<br>
 *  <p>
 *  Any number of variations is allowable.  You can even add variations
 *  to the very first move.
 *  <pre>
 *  history.rewind();  //goes back to the beginning
 *  history.add(MoveA);
 *  history.rewind();
 *  history.add(MoveB);
 *  </pre>
 *  In this game both MoveA and MoveB are ways to start the game.<br>
 *  <p>
 *  You can play moves directly on the Board objects but they will not
 *  be added to the history list and <b>will</b> FUBAR your board's 
 *  internal state if you start using History after that.
 *  <p>
 *  Moves are played and verified when they are added to the History.
 */
public class History {
   public static final long DEBUG = Log.History;

      /** the Game this History is associated with */
   protected Game game;
      /**the array of first moves entire list.  This move is only used because
         of its access to the variation operations*/
   protected ContinuationList head;

      /**the internal placeholder of what move was last executed 
         (needs to be gaurnteed) */
   protected Move currMove;

      /**what's the starting move number (default 1)*/
   protected int initialMoveNumber = 1,
      /**the current move number as known by History */
                 currMoveNumber    = 0;


   //Constructors//////////////////////////////////////////////////////////
   public History (Game game) {
      this(game, 1);
   }

   /** @param initMoveNum the number of the first move. (default: 1)
    */
   public History (Game game, int initMoveNum) {
      this.game = game;
      head = new ContinuationArrayList(null);
      currMove = null;
      currMoveNumber = initialMoveNumber = initMoveNum;
   }

   //Accessors/////////////////////////////////////////////////////////////
   public int  getInitialMoveNumber () { return initialMoveNumber; }

   //Mutators//////////////////////////////////////////////////////////////
   public void setInitialMoveNumber (int i) { 
      currMoveNumber += i - initialMoveNumber;
      initialMoveNumber = i; 
   }

   /* getCurrentMoveNumber **************************************************/
   /** gets the move number of the last move executed on the board.
    *  @return 0 if no moves executed on the board.
    */
   public int getCurrentMoveNumber () {
      return currMoveNumber;
   }

   //Add/Del///////////////////////////////////////////////////////////////
   /* add()******************************************************************/
   /** adds a move to the game history, adding a continuation onto wherever
    *  the current move pointer is.  If the parameter asMainLine is true
    *  then the move added becomes the main line; all other moves are shifted
    *  down one in the variation list.  That is, if there existed a mainLine it
    *  is now the first variation.  The previous first variation is now the 
    *  second  and so on.
    *  <p>
    *  If asMainLine is false then the move is appended to the variation list.
    *  If there are no moves that follow the current move (no main line, no 
    *  variations) the mainLine will be null and the move added will be the
    *  first variation.
    *  <p>
    *  When the move is added History.next() is called on the move and the 
    *  move is played.
    */
   public void add (Move _move, boolean asMainLine) 
          throws IllegalMoveException,
                 IndexOutOfBoundsException,
                 OutOfTurnException,
		 AmbiguousMoveException {
      Move next = null;
      boolean found = false;
      ContinuationList cont = null;

      _move.setHistory(this);

      if (currMove != null) 
         cont = currMove.getContinuationList();
      else
         cont = head;
        
      cont.add(_move, asMainLine);

      try {
         _next(_move);  //executes next move (we just added)
      }
      catch (OutOfTurnException e) {
	 _delBadAdd(_move);
	 throw e;
      }
      catch (IllegalMoveException e) {
         //need to remove the move since the execute failed
	 _delBadAdd(_move);
	 throw e;
      }
      catch (AmbiguousMoveException e) {
         _delBadAdd(_move);
	 throw e;
      }
   }

   /* add (Move) ***********************************************************/
   /** adds a move to the continuation list.  If there are no continuations
    *  then this move becomes the mainline.  If there are continuations
    *  then this move becomes a variation and is added to the end of the list.
    *  If the mainline is null then this will be added as a variation.
    */
   public void add (Move m) 
          throws IllegalMoveException,
                 IndexOutOfBoundsException,
                 OutOfTurnException,
		 AmbiguousMoveException {

      ContinuationList cont = head;
         if (currMove != null)
	    cont = currMove.getContinuationList();

         if (cont.hasMainLine())
	    add(m, false);
	 else
	    add(m, true);
   }


   /** addVariation() *******************************************************/
   /** This is the same as add(Move, false)
    */
   public void addVariation (Move m) 
          throws IllegalMoveException,
                 IndexOutOfBoundsException,
                 OutOfTurnException,
		 AmbiguousMoveException {
      add(m, false);
   }


   /* getFirst() ************************************************************/
   /** gets the first move on the main line of the history list
    *
    *  @return null if there is no first move
    */
   public Move getFirst () {
      return head.getMainLine();
   }

   /* getFirstAll ***********************************************************/
   /** this returns the ContinuationList of all the moves that could begin 
    *  the game. All variations are returned as well as the main line.  The 
    *  main line will always be at the Zero index.
    */
   public ContinuationList getFirstAll () {
      return head;
   }

   /* next() ****************************************************************/
   /** executes the next move after currMove, or the first move of the list.
    *
    *  @return the move just executed.
    */
   public Move next () {
      try {
         return next(0); //main line
      }
      catch (IndexOutOfBoundsException e) {
         throw new IndexOutOfBoundsException(
	     "No main line for move:" + currMove);
      }
   }

   /* next (i) **************************************************************/
   /** executes the next variation
    */
   public Move next (int i) {
      try {
         return _next(i);
      }
      catch (MoveException e ) {
         assert false 
	    : "Already verified move threw: " + e.getMessage() 
	    + " for move: " + e.getMove().dump();
	 return null;
      }
   }

   /* next (move) **********************************************************/
   /** executes m if and only if the move is <b>already</b> in the 
    *  ContinuationList that follows the current move.  If it is not
    *  the move is no executed, and not added to the History, but an
    *  exception is thrown:
    *
    *  @throws IndexOutOfBoundsException is thrown if the move is not in
    *          the ContinuationList.
    */
   public Move next (Move m) {
      ContinuationList cont = head;

      if (currMove != null)
         cont = currMove.getContinuationList();

      if (!cont.exists(m))
         throw new IndexOutOfBoundsException(
	    "this move is not found in the current list of continuations."
	    );
      else
         try {
            return _next(m);
	 }
	 catch (MoveException e) {
	    assert false
	       : "Already verified move threw: " + e.getMessage() 
	       + " for move: " + e.getMove().dump();
	    return null;
	 }
   }

   /* _next (move) *********************************************************/
   /** this is an internal function used to get all the next() type calls
    *  in one place.
    */
   protected Move _next (Move m) 
          throws IllegalMoveException,
                 OutOfTurnException,
		 AmbiguousMoveException {

      m.execute();
      currMoveNumber++;
      currMove = m;
      return currMove;
   }
      

   /* _next (int) **********************************************************/ 
   /** internal function that throws exceptions different from normal
    *  next(i).  This is so add(Move) can use the same function
    */
   protected Move _next (int i) 
          throws IllegalMoveException,
                 OutOfTurnException,
		 AmbiguousMoveException {

      Move m = null;

      if (currMove != null)
         m = currMove.getContinuationList().get(i);
      else
         m = head.get(i);

	 if (m == null) 
	    throw new IndexOutOfBoundsException (
	       "variation [" + i + "] does not exist for " + currMove);
	 
	 m.execute();
	 currMove = m;

      return currMove;
   }

   /* getNext() *************************************************************/
   /** returns the next move on the currMove's main line.  It returns the
    *  move that would be executed by next(0);
    */
   public Move getNext () {
      return getNext(0);
   }

   /* getNext(i) ***********************************************************/
   /** returns the next move (variation 'i').  This is just a convientient
    *  way of calling history.getCurrentMove().getContinuationList().get(i);
    *
    * @param i is the variation number '0' is the main line
    *
    * @return null if there are no more moves, or if the list is empty
    * @return !null this is the next move to be executed
    *
    * @throws IndexArrayOutOfBounds if variation asked for doesn't exist
    */
   public Move getNext (int i) {
      if (currMove == null)
         return head.get(i);
      else
         return currMove.getContinuationList().get(i);
   }

   /* getContinuationList ***************************************************/
   /** returns the continuationList for the currMove.  Or if there is no
    *  current move then the ContinuationList of the start of the game.
    */
   public ContinuationList getContinuationList () {
      if (currMove == null) return head;
      return currMove.getContinuationList();
   }

   /* prev () **************************************************************/
   /** unexecutes the last move and backs the currMove up one
    * @return null if already at the beginning of the history
    * @return !null the move just unexecuted (previous currMove)
    */
   public Move prev () {
      Move prev = null;
      if (currMove == null)
         prev = null;
      else {
         currMove.unexecute();
	 prev = currMove;
	 currMove = currMove.prev;
	 currMoveNumber--;
      }
      return prev;
   }

   /* goTo (move) *********************************************************/
   /**goes to this move in the history list unexecuting or executing moves
    * as is necessary to reach a game state where m was the last move 
    * executed on the board.
    *
    * @param m if null a rewind() will be executed
    */
   public Move goTo (Move m) {
   /* tracks backward through the history list to the root node.
    * Then it rewinds the history and moves back down to this move
    * using the tracked moves.
    */
      if (m == null) {
         rewind();
	 return null;
      }

      if (m.getHistory() != this) 
         throw new IllegalArgumentException (
	     "Can't goTo() a move that doesn't belong to this history list.");

      LinkedList<Move> tracks = new LinkedList<>();
      ListIterator<Move> li;
      Move walker = m;

      do {
         tracks.addFirst(walker);
      } while ((walker = walker.prev) != null);

      notifyBoardsOfTraversal(true);

      _rewind();  //go back to the beginning

      li = tracks.listIterator(0);
      while (li.hasNext()) {
	 walker = li.next();
	 try {
	    walker.execute();
	    currMoveNumber++;
	 }
	 catch (OutOfTurnException e) {
	    assert false
	       : "Previously verified move threw " + e 
	       + " History is out of sequence for some reason.";
	 }
	 catch (IllegalMoveException e) {
	    assert false
	       : "Previously verified move threw " + e;
	 }

	 currMove = walker;
      }
      notifyBoardsOfTraversal(false);

      assert getCurrentMove() == m : "History couldn't walk back to " + m 
          + " last move is " + getCurrentMove();

      return m;
   }

   /* _rewind *************************************************************/
   /** the protected version of rewind() that does not notify the board
    *  of the traversal.
    */
   protected void _rewind () {
      while (currMove != null) {
         currMove.unexecute();
	 currMove = currMove.prev;
      }
      currMoveNumber = initialMoveNumber;
   }

   /* rewind () ***********************************************************/
   /**  undoes any moves already done until we are at the begining
    */
   public void rewind () {
      notifyBoardsOfTraversal(true);

      _rewind();

      notifyBoardsOfTraversal(false);
   }

   /* fastforward *********************************************************/
   /** moves forward on the mainline of the current variation N moves.
    *  If there are less than N moves on the line it will stop at the 
    *  last move.  
    *
    *  @param  n the number of moves to move through.
    *  @return the number of moves actually moved through
    *          (in the case of early termination of the line).
    */
   public int fastforward (int n) {
      notifyBoardsOfTraversal(true);

      int count = 0;
      while (hasNext() && count < n) {
         next();
	 count++;
      }

      notifyBoardsOfTraversal(false);

      return count;
   }

   /* goToEnd () **********************************************************/
   /** opposite of rewind() this goes to the end of the current branch
    *  performing all moves on the branch's main line as it goes.
    */
   public void goToEnd () {
      notifyBoardsOfTraversal(true);

      while (hasNext()) {
         next();
      }

      notifyBoardsOfTraversal(false);
   }

   /* rewindToLastFork() **************************************************/
   /** rewinds the history until the current move is a move with
    *  variations.
    *
    * @return Move - the current move (last move played)
    */
   public Move rewindToLastFork () {
      if (currMove == null) return currMove;

      notifyBoardsOfTraversal(true);

      do {
         currMove.unexecute();
	 currMoveNumber--;
	 currMove = currMove.prev;
      } while (currMove != null 
               && !currMove.getContinuationList().hasVariations());

      notifyBoardsOfTraversal(false);

      return currMove;
   }

   /* hasNext () **********************************************************/
   /** this applies to the main line.
    * @return false no move follows the last move executed
    * @return true  there are more moves to be executed
    */
   public boolean hasNext () {
      boolean hasNext = true;
      if (currMove == null)
         hasNext = head.hasMainLine();
      else 
         hasNext = currMove.getContinuationList().hasMainLine();

      return hasNext;
   }

   /* isEmpty () **********************************************************/
   /** is the history list completely empty?
    */
   public boolean isEmpty () {
      return head.size() == 0;
   }

   /* getCurrentMove () ***************************************************/
   /** returns the last executed move.
    * @return null at the beginning of the list
    * @return !null last move executed
    */
   public Move getCurrentMove () {
      return currMove; 
   }

   /* getFinalMove ********************************************************/
   /** returns the final move played on the main line or the current line.
    *  Note: this does not effect the move pointer (getCurrentMove()).
    *
    *  @param trueMainLine do you want the last move on the main line from
    *         game (instead of the main line from the current variation).
    *  @return last move from the main line.  if trueMainLine is true then
    *         this will be the last move from the how the game was played.
    *         if trueMainLine is false then the final move from the current
    *         variation is returned.  If the current variation is the main
    *         line then the results are the same no matter the value of
    *         trueMainLine.
    *  @return null if there are no moves to this game.
    */
   public Move getFinalMove (boolean trueMainLine) {
      Move walker = null;
      if (trueMainLine)
         walker = head.get(0);
      else
         walker = currMove;

      while (walker != null && !walker.getContinuationList().isTerminal())
         walker = walker.getContinuationList().getMainLine();

      return walker;
   }

   /* removeLastMove ******************************************************/
   /** unexecutes the last move executed, disposes of all its data
    *  then returns the move, which should only contain the cooridinates 
    *  of the move.
    *  The move tree is truncated at this point.
    *
    *  @return what's left of the deleted move (dispose was called)
    */
   public Move removeLastMove () {
      Move gonner = null;
         //unexecute
         gonner = prev();

         if (currMove != null)
	    currMove.getContinuationList().remove(gonner);
	 else
	    head.remove(gonner);
         
      return gonner;
   }

   /* truncate ************************************************************/
   /** deletes all branches from currMove (making currMove a terminal node)
    *  for the "i" variation.
    *
    *  @param  i - the branch we want to truncate.  (-1 to remove all)
    *  @return Move - the current move (same as getLastMove)
    */
   public Move truncate (int i) {

      if (i == -1)
         if (currMove != null)
	    currMove.getContinuationList().removeAll();
	 else
	    head.removeAll();
      else
         if (currMove != null)
	    currMove.getContinuationList().remove(i);
	 else
	    head.remove(i);

      return currMove;
   }

   /** calls truncate(-1) */
   public Move truncate () {
      return truncate(-1);
   }

   /* _delBadAdd **********************************************************/
   /** deletes a IllegalMove that was added to the history list in an
    *  add() attempt.
    */
   private void _delBadAdd (Move gonner) {
      // Handle exceptions so that the "original" exception is not blown away.
      try {
         if (currMove != null)  //tried to add a Nth move (where not 1)
            currMove.getContinuationList().remove(gonner);
         else 
            head.remove(gonner);
      }
      catch (RuntimeException e) {
         // Probably no need to even log this. We're already throwing an exception that something went wrong.
      }
   }

   /* size ***************************************************************/
   /** returns the number of moves in the main line
    */
   public int size () {
      Move walker = null;
      int i = 0;
      
         walker = head.getMainLine();

	 while (walker != null) {
	    i++;
	    walker = walker.getContinuationList().getMainLine();
	 }

      return i;
   }

   //Object Overrides///////////////////////////////////////////////////////
   public String toString () {
      Move walker  = null;
      String str   =  new String("");
      String tmp   = null;
         //only doing main line walk
         walker = head.getMainLine();;

         int i = 1;
	 while (walker != null) {
	    if (i % 2 == 1) {
	       tmp = walker.toString();
	       for (int j=tmp.length(); j < 9; j++)
	          tmp += " ";
	       str += ((i/2) +1) + ((i < 19) ? ".  " : ". ");
	       str += tmp;
	    }
	    else
	       str += " " + walker + "\n";
	    i++;
	    if (walker.getContinuationList().hasMainLine())
	       walker = walker.getContinuationList().getMainLine();
            else
	       walker = null;
	 }

      return str;
   }

   /* equals **************************************************************/
   /** this compares the main-line only.  Annotations and variants will
    *  not be compared.
    */
   public boolean equals (History hist) {
      Move walker = head.getMainLine(),
           walker2 = hist.head.getMainLine();

      while (walker != null && walker2 != null && walker.equals(walker2)) {
         walker = walker.getContinuationList().getMainLine();
         walker2 = walker2.getContinuationList().getMainLine();
      }
      if (walker == walker2 && walker == null)
         return true;
      else 
         return false;
   }

   /* deepEquals ***********************************************************/
   /** this compares all moves on the main-line, and all variations.
    *  The order of the variations will not matter (not even the 
    *  main line) only that they are indeed a continuation. If results
    *  are being checked then a null result is considered the same
    *  as isUndecided();
    *
    * @param checkAnno if true will compare the annotations, prenotations, and 
    *                   variation results as well.
    *                  If false the results will also not be checked.
    */
   public boolean deepEquals (History hist, boolean checkAnno) {
      boolean t = false;

      if (Log.debug)
         Log.debug(DEBUG, "beginning probe:" 
	    + ((checkAnno) ? "" : " not") + " checking Annotations");

      t = probeDeepEquals(head, hist.head, checkAnno);

      if (Log.debug)
         Log.debug(DEBUG, "histories: " 
	    + ((t) ? "same" : "different"));

      return t;
   }

   /* probeDeepEquals ******************************************************/
   /** the recursive utility of deepEquals()
    */
   protected boolean probeDeepEquals (ContinuationList cont, 
                                      ContinuationList cont2,
                                      boolean checkAnno) {
      Move    move1   = null,   //game 1
              move2   = null;   //game 2
      Move[]  possibleMatches = null;
      boolean t       = true,   //equals
              found   = false,  //possibleMatche found
	      isMatch = true;   //possibleMatch truth
      int     j       = 0;      //possibleMatch counter

         //are they both terminal?
         if (cont.isTerminal() && cont2.isTerminal()) {
	    if (Log.debug)
	       Log.debug(DEBUG, "+ both histories terminate");
	    return t;
         }
	 else
	    //check all moves in the game1 continuation list
	    for (int i=0; t && i< cont.size(); i++) {
	       move1 = cont.get(i);

	       //are both mainlines null?
	       if (i == 0 && move1 == null && cont2.get(0) == null) {
	          Log.debug(DEBUG, "+ both mainlines are null");
	          t = true;
	       }

	       //stop if move from game1 isn't found in the continuation list
	       else if ((possibleMatches = cont2.find(move1)) == null) {
	          if (Log.debug) 
		     Log.debug(DEBUG, "- move (" + move1 
		        + ") not found in continuation list of game 2");
	          t = false;
	       }

	       //check all possible continuation lines that match
	       //the first move.
	       else {
	          found = false;

		  if (Log.debug)
		     Log.debug(DEBUG,
		        "+ (" + possibleMatches.length 
			+ ") possible matches of (" + move1 
			+ ")");

	          for (j=0; j < possibleMatches.length && !found; j++) {
		     move2 = possibleMatches[j];

		     isMatch = move2 != null;

		     if (Log.debug && !isMatch)
		        Log.error(Log.PROG_ERROR, 
			   "- hmm, possible match was null?");

                     //check annotations
                     if (isMatch && checkAnno) {
			isMatch = (move1.getAnnotation()
			           == move2.getAnnotation())
			          || (move1.getAnnotation() != null 
				     && move2.getAnnotation() != null
				     && move1.getAnnotation()
				             .equals(move2.getAnnotation())
			             );

			if (Log.debug)
			   Log.debug(DEBUG, 
			      ((isMatch) ? "+" : "-")
			      + " [" + (j+1) + "/" + possibleMatches.length 
			      + "] (" + move1 + ") annotation: "
			      + ((isMatch) ? "same" : "different")
			      );
		     }

                     //check prenotations
		     if (isMatch && checkAnno) {
			isMatch = (move1.getPrenotation()
			           == move2.getPrenotation())
			          || (move1.getPrenotation() != null 
				     && move2.getPrenotation() != null
				     && move1.getPrenotation()
				             .equals(move2.getPrenotation())
			 	     );

			if (Log.debug)
			   Log.debug(DEBUG, 
			      ((isMatch) ? "+" : "-")
			      + " [" + (j+1) + "/" + possibleMatches.length 
			      + "] (" + move1 + ") prenotation: "
			      + ((isMatch) ? "same" : "different")
			      );
		     }

                     //check results only if checking annotations
		     //since a result in a variation line is basically 
		     //an annotation.
		     if (isMatch && checkAnno) {
		        isMatch = ((move1.getResult() == null 
			              || move1.getResult().isUndecided())
				   && move2.getResult() == null
				      || move2.getResult().isUndecided())
				  || (move1.getResult() != null
				        && move2.getResult() != null
					&& move1.getResult()
					        .equals(move2.getResult())
				     );

			if (Log.debug)
			   if (move1.getResult() != null 
			       || move2.getResult() != null)
			      Log.debug(DEBUG,
				 ((isMatch) ? "+" : "-")
				 + " [" + (j+1) + "/" + possibleMatches.length 
				 + "] (" + move1 + ") result: "
				 + ((isMatch) ? "same" : "different")
				 + ((move1.getResult() == null 
				     || move2.getResult() == null) 
				        ? " {" + ((move1.getResult() == null) 
					          ? "null/undecided"
						  : "undecided/null"
					         )
					  + "}"
					: "")
				 );
		     }

	             isMatch = isMatch 
		               && probeDeepEquals(move1.getContinuationList(),
		                  move2.getContinuationList(),
		                  checkAnno);
		     found = isMatch;
	       }

	       if (Log.debug)
	          Log.debug(DEBUG, "("
		     + move1 + ") continuation: "
		     + ((found) ? "[" + j 
		                  + "/" + possibleMatches.length 
		                  + "] matched"
		                : "no match found"));
	       t = found;
	    }
         }
      return t;
   }

   /* hashCode ***************************************************************/
   /** this returns a static number.  And thus isn't terribly useful.  But
    *  since the data in History changes so often it might be the right thing
    *  to do until someone can convince me otherwise.
    */
   public int hashCode () {
      return 0;
   }

   //utility functions////////////////////////////////////////////////////////
   /** sends  BoardEvent.TRAVERSAL_(BEGIN/END) to all boards
    *  associate with this game.<br>
    *  Note: this function does assume that this History, and all Boards are
    *  unified under one Game.  If this is not true, and this history is
    *  for severall Game objects that use different boards, some boards will
    *  not get notified.
    *
    *  @param begin if TRUE then TRAVERSAL_BEGIN is sent.
    *  @param begin if FALSE then TRAVERSAL_END is sent.
    */
   protected void notifyBoardsOfTraversal (boolean begin) {
      Board[] boards = null;
      int event = ((begin) ? BoardEvent.TRAVERSAL_BEGIN 
                           : BoardEvent.TRAVERSAL_END);

         boards = game.getBoards();

	 if (boards != null) {
	    for (int i=0; i < boards.length; i++) {
	       boards[i].fireBoardEvent(event);
	    }
	 }
   }
}

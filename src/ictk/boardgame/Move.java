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

import ictk.boardgame.io.Annotation;

/* Move **********************************************************************/
/** A move contains all the necessary components to make a move on the board.  
 * Whether it be simply placing a piece at a location, moving pieces around 
 * or taking a piece off the board, a Move is the embodyment of a player 
 * action for one turn.
 * <p>
 * The Move is an implementation of a Command Pattern.  It is sent to its
 * Board object via the Board.play(Move) or more commonly History.add(Move);
 * (the latter being prefered -- see those methods for a more detailed
 * explaination of why.  When execute() is called on the Move it performs
 * all the operations necessary on the Board's state.  unexecute() rolls
 * back any changes made by execute().
 * <p>
 * Move contains mostly references to other objects.  From this move
 * there branches a tree of other moves called "continuations".  Continuations
 * include the main-line (which was how the official game was played) and 
 * a list of variations (where are possible continuations of the game). These
 * can all be accessed through the getContinuationList() method.
 * <p>
 * There are two two phases to the Move object
 * <p> 1) unverified <br>
 *     The move is but coordinates, not associated with a board or
 *     history list.  There is no guarentee that the move is valid.
 *     Executing a method that relies on valid information will result
 *     in an UnexecutedMoveException.  This is very rare, however, since
 *     most Move objects are verififed and validated immediately upon
 *     creation.
 *<p> 2) verified <br>
 *     The move has been executed at least once on the corrisponding  board 
 *     and verified to be a legal move.  At this point all information 
 *     about the move can be assertained including such things as 
 *     isCheckmate() (for ChessMoves).
 */
public abstract class Move {
   protected boolean
      /** is the move executed on the current board */
           executed = false,
      /** has the move been executed before.  This is used to determine
          if there is a piece on the origin square, if the move is unique.
          and if there is a casualty.  Also the legality of the move is
          checked against the legal moves for this piece.*/
           verified = false;

   //list operations//////////////////////////////////////////////////////////
   protected History history; 
      /**array of next moves, allowing for treeing from main line.*/
   protected ContinuationList continuation;
      /**the next move by default. Is set each descent down the list.*/
   protected Move    prev;      //the previous move in the history of the game

     /** typically this is only used for the end of a game or variation line
      ** and indicates that there is some finality to the game at this point.
      **/
   protected Result     result;

     /** comment before the move. */
   protected Annotation prenotation,
     /** comment after the move */
                        annotation;

   //Constructors/////////////////////////////////////////////////////////////
   public Move () {
      continuation = new ContinuationArrayList(this);
   }

   //Accessors///////////////////////////////////////////////////////////
   public History getHistory () { return history; }
   abstract public Board getBoard ();

   //Mutators////////////////////////////////////////////////////////////
   protected void setHistory (History h) { history = h; }
   abstract protected void setBoard   (Board b);

   //Datastructure Operations////////////////////////////////////////////
   /* public void setPrev (Move m) **************************************/
   /** sets which move precedes this move in the history list 
    */
   protected void setPrev (Move m) { prev = m; }

   /* getContinuationList ***************************************************/
   /** returns the ContinuationList of moves that follow this move.  This
    *  can be use for accessing the variations other than the main-line,
    *  and ordering the variations.
    */
   public ContinuationList getContinuationList () {
      return continuation;
   }

   /* public Move getPrev () ********************************************/
   /** gets the move previous to this move.
    */
   public Move getPrev () { return prev; }

   /* hasNext ***********************************************************/
   /** checks to see if there is a main-line continuation from this move.
    *  ie, a move that follows this move in the actual game.
    */
   public boolean hasNext () { 
      return !continuation.hasMainLine(); 
   }

   /* public Move getNext () ********************************************/
   /** get the next move in the game history.  If the move branches
    *  (if there are several variations of the game after this move)
    *  then this function returns the main line of descent for this
    *  game.
    *
    */
   public Move getNext () { 
      return continuation.getMainLine();
   }

   /* addNext() **********************************************************/
   /** Adds a move onto the list of continuations that follow this move.
    *  This follows the same rules as <i>ContinuationList.add(Move)</i>
   */
   protected void addNext (Move move) {
      continuation.add(move);
   }

   /* dispose () *********************************************************/
   /** reclaims all resources and recursively deletes all branch moves.
    *
    *  @throws IllegalStateException if the move is executed
    */
   public void dispose () {
         if (isExecuted())
	    throw new IllegalStateException(
	       "Cannot dispose of an executed move.  Rewind history first.");
         history = null;
	 prev = null;
	 continuation.dispose();
	 continuation = null;
	 history = null;
   }

   //Info About Move///////////////////////////////////////////////////////

   //Checking the Move///////////////////////////////////////////////////
   public abstract boolean isLegal ();

   //Execution///////////////////////////////////////////////////////////

   /* execute **********************************************************/
   /** affects the change on the board. It should also call
    *  fireBoardEvent(BoardEvent.MOVE) for the board to 
    *  propagate the changes to all listeners.
    */
   protected abstract void execute ()  
          throws IllegalMoveException, 
	         OutOfTurnException;

   /* unexecute *******************************************************/
   /** undo the move onb the board.  This does not imply a takeback, but
    *  only that the move should be unplayed.  It should also call
    *  fireBoardEvent(BoardEvent.UNMOVE) for the board to 
    *  propagate the changes to all listeners.
    */
   protected abstract void unexecute();

   /* isExecuted ******************************************************/
   /** has this move currently been executed on its board
    */
   public boolean isExecuted () {
      return executed;
   }

   /* isVerified ******************************************************/
   /** has the move been verified to be legal (this usually follows a
    *  successful execute(), though the move might not be isExecuted
    *  currently.
    */
   public boolean isVerified () {
      return verified;
   }

   /* getPrenotation ***************************************************/
   /** returns an object full of comments that are supposed to be read
    *  before the move these comments are attached to.
    *
    *  @return the Annotation objects is an abstract base class so
    *          casting is necessary.
    */
   public Annotation getPrenotation () {
      return prenotation;
   }

   /* setPrenotation ****************************************************/
   /** by default the prenotation object is null. 
    */
   public void setPrenotation (Annotation anno) { prenotation = anno; }

   /* getAnnotation ****************************************************/
   /** returns an object full of comments on the move or the board
    *  position.  
    * 
    *  @return the Annotation objects is an abstract base class so
    *          casting is necessary.
    */
   public Annotation getAnnotation () {
      return annotation;
   }

   /* setAnnotation ****************************************************/
   /** by default the annotation object is null. 
    */
   public void setAnnotation (Annotation anno) { annotation = anno; }


   /* getResult ********************************************************/
   /** returns the current game result on this move.
    *
    *  @return null if no result has been assigned to this move.
    */
   public Result getResult () {
      return result;
   }

   /* setResult ********************************************************/
   /** sets the result. Note: a move does not have to be terminal to 
    *  have a result.
    */
   public void setResult (Result res) {
      result = res;
   }

   // IO ////////////////////////////////////////////////////////////////
   public abstract String toString ();

   /* dump ************************************************************/
   /** for debugging
    */
   public String dump () {
      StringBuffer sb = new StringBuffer();

         sb.append("prev: ").append(prev.toString())
	   .append("\n")
	   .append("continuation#:").append(continuation.dump())
	   .append("\n")
	   .append("prenotation: ").append(prenotation)
	   .append("\n")
	   .append("annotation: ").append(annotation)
	   .append("\n")
	   .append("result: ").append(result);
	   ;
      return sb.toString();
   }
}

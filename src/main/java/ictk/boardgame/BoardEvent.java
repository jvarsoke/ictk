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

public interface BoardEvent {
      /** a move has been executed on the board */
   public int MOVE            = 1,
      /** a move has been unexecuted on the board. This could be 
       ** because of a Takeback, or a call to prev() in the History */
              UNMOVE          = 2,
      /** the position has changed in some way and the whole board should
          be updated */
              POSITION        = 2,
      /** a series of moves is about to be executed and the Display should
       ** choose whether it wants to display each move, or whether it only
       ** should display the end position. This must always be followed by
       ** a TRAVERSAL_END_CODE. */
	      TRAVERSAL_BEGIN = 3,
      /** the traversal has ended and the Display should assume the next
       ** move event is not part of the traversal. */
	      TRAVERSAL_END   = 4;
}

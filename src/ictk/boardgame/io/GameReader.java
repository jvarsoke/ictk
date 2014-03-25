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

package ictk.boardgame.io;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.Game;
import ictk.boardgame.GameInfo;
import ictk.boardgame.History;
import ictk.boardgame.Board;

/* GameReader ***************************************************************/
/** reads games in various notations.
 */
public abstract class GameReader extends BufferedReader {

   public GameReader (Reader _ir) {
      super(_ir);
   }

   /** read the Game (History, Board, and GameInfo) from the input Buffer
    */
   public abstract Game readGame ()
          throws InvalidGameFormatException,
                 IllegalMoveException,
                 AmbiguousMoveException,
                 IOException;

   /** read the GameInfo from the input buffer
    */
   public abstract GameInfo readGameInfo ()
          throws IOException;

   /** read the game History from the input buffer
    */
   public abstract History readHistory ()
          throws InvalidGameFormatException,
                 IllegalMoveException,
                 AmbiguousMoveException,
                 IOException;

   /** read the game Board from the input buffer 
    */
   public abstract Board readBoard ()
          throws IOException;

}

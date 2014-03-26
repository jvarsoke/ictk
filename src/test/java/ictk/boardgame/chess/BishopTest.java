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

import junit.framework.*;
import java.util.List;

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;

public class BishopTest extends TestCase {
   boolean DEBUG = false;
   ChessBoard board;
   ChessMove   move;
   List list;
   Bishop bishop;

   public BishopTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      move = null;
      bishop = null;
      DEBUG = false;
   }

   //////////////////////////////////////////////////////////////////////
   public void testFullMoveScope () {
      board.setPositionClear();
      board.addBishop(4, 4, false); //d4
      board.addKing(4, 1, false);
      board.addKing(4, 1, true);
      bishop = (Bishop) board.getSquare(4,4).getOccupant();

      list = bishop.getLegalDests();
      assertTrue(list.size() == Bishop.MAX_LEGAL_DESTS);

      list.remove(board.getSquare('a','1'));
      list.remove(board.getSquare('b','2'));
      list.remove(board.getSquare('c','3'));
      list.remove(board.getSquare('e','5'));
      list.remove(board.getSquare('f','6'));
      list.remove(board.getSquare('g','7'));
      list.remove(board.getSquare('h','8'));

      list.remove(board.getSquare('a','7'));
      list.remove(board.getSquare('b','6'));
      list.remove(board.getSquare('c','5'));
      list.remove(board.getSquare('e','3'));
      list.remove(board.getSquare('f','2'));
      list.remove(board.getSquare('g','1'));

      assertTrue(list.size() == 0);
   }
}

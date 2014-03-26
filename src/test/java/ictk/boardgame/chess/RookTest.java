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

public class RookTest extends TestCase {
   boolean DEBUG = false;
   ChessBoard board;
   ChessMove   move;
   List list;
   Rook rook;

   public RookTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      move = null;
      rook = null;
      DEBUG = false;
   }

   //////////////////////////////////////////////////////////////////////
   public void testFullMoveScope () {
      board.setPositionClear();
      board.addRook(4, 4, false); //d4
      board.addKing(1, 1, false);
      board.addKing(8, 1, true);
      rook = (Rook) board.getSquare(4,4).getOccupant();

      list = rook.getLegalDests();
      assertTrue(list.size() == 14);

      list.remove(board.getSquare('a','4'));
      list.remove(board.getSquare('b','4'));
      list.remove(board.getSquare('c','4'));
      list.remove(board.getSquare('e','4'));
      list.remove(board.getSquare('f','4'));
      list.remove(board.getSquare('g','4'));
      list.remove(board.getSquare('h','4'));

      list.remove(board.getSquare('d','1'));
      list.remove(board.getSquare('d','2'));
      list.remove(board.getSquare('d','3'));
      list.remove(board.getSquare('d','5'));
      list.remove(board.getSquare('d','6'));
      list.remove(board.getSquare('d','7'));
      list.remove(board.getSquare('d','8'));

      assertTrue(list.size() == 0);
   }
}

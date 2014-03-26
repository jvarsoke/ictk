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

public class PawnTest extends TestCase {
   boolean DEBUG = false;
   ChessBoard board;
   ChessMove   move;
   Pawn pawn;
   List list;

   public PawnTest (String name) {
      super(name);
      //Log.addMask(Pawn.DEBUG);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      move = null;
      pawn = null;
      DEBUG = false;
   }

   //////////////////////////////////////////////////////////////////////
   public void testDefaultAFile () {
      pawn = (Pawn) board.getSquare('a','2').getOccupant();
      assertTrue(!pawn.isBlack());
      list = pawn.getLegalDests();
      assertTrue(list.size() == 2);
      list.remove(board.getSquare('a','3'));
      list.remove(board.getSquare('a','4'));
      assertTrue(list.size() == 0);
      assertTrue(!pawn.hasMoved());
   }

   //////////////////////////////////////////////////////////////////////
   public void testDefaultBFileOpposition () {
      board.setBlackMove(true);
      pawn = (Pawn) board.getSquare('b','2').getOccupant();
      assertTrue(!pawn.isBlack());
      assertTrue(pawn.isLegalAttack(board.getSquare('a','3')));
      assertTrue(!pawn.isLegalAttack(board.getSquare('b','3')));
      assertTrue(!pawn.isLegalAttack(board.getSquare('b','4')));
      assertTrue(!pawn.isLegalAttack(board.getSquare('b','5')));
      assertTrue(pawn.isLegalAttack(board.getSquare('c','3')));
      list = pawn.getLegalDests();
      assertTrue(list.size() == 4);
      list.remove(board.getSquare('a','3'));
      list.remove(board.getSquare('b','3'));
      list.remove(board.getSquare('b','4'));
      list.remove(board.getSquare('c','3'));
      assertTrue(list.size() == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testEnPassant () throws IllegalMoveException {
      DEBUG = false;
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ','p',' ',' ',' ','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      board.playMove(new ChessMove(board, 2, 2, 2, 4, 0));
      assertTrue(board.isEnPassantFile('b'));

      pawn = (Pawn) board.getSquare('c','4').getOccupant();

      if (DEBUG) {
         System.err.println(board);
         System.err.println(pawn.dump());
      }

      assertTrue(pawn.isBlack());
      assertTrue(pawn.isLegalAttack(board.getSquare('b','3')));
      assertTrue(!pawn.isLegalAttack(board.getSquare('d','3')));
      list = pawn.getLegalDests();
      assertTrue(list.size() == 2);
      list.remove(board.getSquare('b','3'));
      list.remove(board.getSquare('c','3'));
      assertTrue(list.size() == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testCapture () throws IllegalMoveException {
      DEBUG = false;
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N',' ','P',' ',' ',' ','p','n'},
                        {'B','P',' ','p',' ',' ',' ','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);

      pawn = (Pawn) board.getSquare('b','3').getOccupant();

      if (DEBUG) {
         System.err.println(board);
         System.err.println(pawn.dump());
      }

      assertTrue(!pawn.isBlack());
      assertTrue(pawn.isLegalAttack(board.getSquare('c','4')));
      assertTrue(!pawn.isLegalAttack(board.getSquare('b','4')));
      list = pawn.getLegalDests();
      assertTrue(list.size() == 2);
      list.remove(board.getSquare('c','4'));
      list.remove(board.getSquare('b','4'));
      assertTrue(list.size() == 0);
   }

}

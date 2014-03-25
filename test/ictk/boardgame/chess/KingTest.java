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

public class KingTest extends TestCase {
   boolean DEBUG = false;
   ChessBoard board;
   ChessMove   move;
   List list;
   King king;

   public KingTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      move = null;
      king = null;
      DEBUG = false;
   }

   //////////////////////////////////////////////////////////////////////
   public void testFullMoveScope () {
      board.setPositionClear();
      board.addKing(4, 4, false);
      board.addKing(8, 2, true);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 8);

      list.remove(board.getSquare('c','3'));
      list.remove(board.getSquare('c','4'));
      list.remove(board.getSquare('c','5'));
      list.remove(board.getSquare('d','3'));
      list.remove(board.getSquare('d','5'));
      list.remove(board.getSquare('e','3'));
      list.remove(board.getSquare('e','4'));
      list.remove(board.getSquare('e','5'));

      assertTrue(list.size() == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testNotIntoCheck1 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ','K',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 5);

      list.remove(board.getSquare('d','3'));
      list.remove(board.getSquare('d','5'));
      list.remove(board.getSquare('e','3'));
      list.remove(board.getSquare('e','4'));
      list.remove(board.getSquare('e','5'));

      assertTrue(list.size() == 0);
   }
   
   //////////////////////////////////////////////////////////////////////
   public void testNotIntoCheck2 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ','K',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 2);

      list.remove(board.getSquare('d','3'));
      list.remove(board.getSquare('d','5'));

      assertTrue(list.size() == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testNotIntoCheck3 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ','K',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ','r',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 1);

      list.remove(board.getSquare('d','3'));

      assertTrue(list.size() == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testStalemate () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ','K',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ','r',' ','r',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertTrue(board.isStalemate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmate () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ','K',' ',' ',' ','r'},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertTrue(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmate2 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ','n',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ','K',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','r'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ','r',' ','r',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(4,4).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertTrue(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmateSmother () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ','n',' ',' ',' ',' ',' ',' '},
                         {'R','P',' ',' ',' ',' ',' ',' '},
                         {'K','P',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(8,1).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertTrue(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmateNotSmother () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'B',' ',' ',' ',' ',' ',' ',' '},
                         {' ','n',' ',' ',' ',' ',' ',' '},
                         {'R','P',' ',' ',' ',' ',' ',' '},
                         {'K','P',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(8,1).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertFalse(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmateSmother2 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'r',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ','n',' ',' ',' ',' ',' ',' '},
                         {'B','P',' ',' ',' ',' ',' ',' '},
                         {'K','P',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(8,1).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertTrue(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmateSmother3 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'r',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ','n',' ',' ',' ',' ',' ',' '},
                         {'B','p',' ',' ',' ',' ',' ',' '},
                         {'K','p',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(8,1).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 2);
      assertFalse(board.isStalemate());
      assertFalse(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmateSave1 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ','r',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ','R',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'r',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertFalse(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testCheckmateSave2Non () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ','r',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ','R',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'r',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();

      list = king.getLegalDests();
      assertTrue(list.size() == 0);
      assertFalse(board.isStalemate());
      assertTrue(board.isCheckmate());
   }

   //////////////////////////////////////////////////////////////////////
   public void testFindRook1 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'B',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();
      Rook rook = (Rook) board.getSquare(1,1).getOccupant();

      assertTrue(rook == king.findMyRook(true));
   }

   //////////////////////////////////////////////////////////////////////
   public void testFindRook2 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'R',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();
      Rook rook = (Rook) board.getSquare(8,1).getOccupant();

      assertTrue(rook == king.findMyRook(false));
   }

   //////////////////////////////////////////////////////////////////////
   public void testFindRook3 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R',' ',' ',' ',' ',' ',' ',' '},
                         {'N',' ',' ',' ',' ',' ',' ',' '},
                         {'B',' ',' ',' ',' ',' ',' ',' '},
                         {'Q',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {'B',' ',' ',' ',' ',' ',' ',' '},
                         {'N',' ',' ',' ',' ',' ',' ',' '},
                         {'R',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();
      Rook rook = (Rook) board.getSquare(1,1).getOccupant();

      assertTrue(rook == king.findMyRook(true));
   }

   //////////////////////////////////////////////////////////////////////
   public void testFindRook4 () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'B',' ',' ',' ',' ',' ',' ',' '},
                         {'N',' ',' ',' ',' ',' ',' ',' '},
                         {'R',' ',' ',' ',' ',' ',' ',' '},
                         {'Q',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {'B',' ',' ',' ',' ',' ',' ',' '},
                         {'N',' ',' ',' ',' ',' ',' ',' '},
                         {'R',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();
      Rook rook = (Rook) board.getSquare(3,1).getOccupant();

      assertTrue(rook == king.findMyRook(true));
   }

   //////////////////////////////////////////////////////////////////////
   /**This is an odd Fischer Random case where the rook on a1 is acutally
    * that has moved from the king-side.  The rook on b1 is the original
    * Q-side rook, which has not moved.  Thus castling should be fine here
    * but if setCastleableQueenside(true) is called it will set the wrong
    * rook.
    * FIXME: 2003.03.05 - known bug
    */
   public void testFindRook5Problem () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R',' ',' ',' ',' ',' ',' ',' '},
                         {'R',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','k'}};

      board.setPosition(position);
      king = (King) board.getSquare(5,1).getOccupant();
      Rook krook = (Rook) board.getSquare(1,1).getOccupant();
      krook.moveCount = 2;
      Rook qrook = (Rook) board.getSquare(2,1).getOccupant();

      //testing positive for the bug
      assertFalse(qrook == king.findMyRook(true));
      assertTrue(krook == king.findMyRook(true));
   }

   //////////////////////////////////////////////////////////////////////
   /** Checks to see if EnPassant is a valid way to escape check.
    */
   public void testEnPassantEscapesCheck () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{' ',' ',' ','P',' ',' ',' ',' '},
                         {' ',' ',' ','P','p',' ',' ',' '},
                         {' ',' ',' ','K','P','k',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '}};

      board.setPosition(position);
      board.setEnPassantFile('b');
      Pawn pawn = (Pawn) board.getSquare(3,5).getOccupant();
      assertTrue(pawn.isLegalDest(board.getSquare(2,6)));
   }
}

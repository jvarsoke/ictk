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

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;

public class ChessMoveTest extends TestCase {
   ChessBoard board, board2;
   ChessResult res;
   ChessMove   move;
   char[][] default_position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};


   public ChessMoveTest (String name) {
      super(name);
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      board2 = null;
      move = null;
      Log.removeMask(ChessMove.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testIllegalCastle () {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      try {
        move = new ChessMove(board, ChessMove.CASTLE_QUEENSIDE);
        fail("shouldn't be able to castle queenside on the initial board");
      }
      catch (IllegalMoveException e) { }

      try {
        move = new ChessMove(board, ChessMove.CASTLE_KINGSIDE);
        fail("shouldn't be able to castle kingside on the initial board");
      }
      catch (IllegalMoveException e) { }

   }

   public void testCastleQsideWhite () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {' ','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      //white
      move = new ChessMove(board, ChessMove.CASTLE_QUEENSIDE);
   }

   //////////////////////////////////////////////////////////////////////
   public void testCastleKsideWhite () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {' ','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      //white
      move = new ChessMove(board, ChessMove.CASTLE_KINGSIDE);
   }

   //////////////////////////////////////////////////////////////////////
   public void testCastleQsideBlack () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p',' '},
                        {'B','P',' ',' ',' ',' ','p',' '},
                        {'Q','P',' ',' ',' ',' ','p',' '},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      board.setBlackMove(true);
      move = new ChessMove(board, ChessMove.CASTLE_QUEENSIDE);
   }

   //////////////////////////////////////////////////////////////////////
   public void testCastleKsideBlack () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p',' '},
                        {'N','P',' ',' ',' ',' ','p',' '},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      board.setBlackMove(true);
      move = new ChessMove(board, ChessMove.CASTLE_KINGSIDE);
   }

   //////////////////////////////////////////////////////////////////////
   public void testA2A3 () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R',' ','P',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};


      move = new ChessMove(board, 1, 2, 1, 3); //a2-a3
      board.playMove(move);
      board2 = new ChessBoard(position);
      board2.setBlackMove(true);
      //System.err.println("b1: " + board.getEnPassantFile()
      //   + " b2: " + board2.getEnPassantFile());
      assertTrue(board.equals(board2));
      assertTrue(board.plyCount50 == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testA2A4 () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R',' ',' ','P',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};


      move = new ChessMove(board, 1, 2, 1, 4); //a2-a4
      board.playMove(move);
      board2 = new ChessBoard(position);
      board2.setBlackMove(true);
      board2.setEnPassantFile('a');
      assertTrue(board.equals(board2));
      assertTrue(board.plyCount50 == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testNf3 () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P','N',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};


      assertTrue (board != null);
      move = new ChessMove(board, 7, 1, 6, 3); //Nf3
      board.playMove(move);
      board2 = new ChessBoard(position);
      board2.setBlackMove(true);
      assertTrue(board.equals(board2));
      assertTrue(board.plyCount50 == 1);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPawnPromotion () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      Piece piece = null;
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','k',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {'K',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','P',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '}};

      char[][] position2={{' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','k',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {'K',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ','N'},
                        {' ',' ',' ',' ',' ',' ',' ',' '}};


      board.setPosition(position);

      move = new ChessMove(board, 7, 7, 7, 8, Knight.INDEX); //g8=Q
      board.playMove(move);
      board2 = new ChessBoard(position2);
      board2.setBlackMove(true);
      assertTrue(board.equals(board2));
      assertTrue(board.plyCount50 == 0);
      piece = board.getSquare('g','8').getOccupant();
      assertTrue(piece instanceof Knight);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPawnPromotionAutoQueen () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      Piece piece = null;
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','k',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {'K',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','P',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '}};

      char[][] position2={{' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','k',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {'K',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ','Q'},
                        {' ',' ',' ',' ',' ',' ',' ',' '}};


      board.setPosition(position);

      move = new ChessMove(board, 7, 7, 7, 8); //g8=?
      board.playMove(move);
      board2 = new ChessBoard(position2);
      board2.setBlackMove(true);
      assertTrue(board.equals(board2));
      assertTrue(board.plyCount50 == 0);
      piece = board.getSquare('g','8').getOccupant();
      assertTrue(piece instanceof Queen);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPawnPromotionBad () throws IllegalMoveException {
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      Piece piece = null;
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','k',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {'K',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '},
                        {' ',' ',' ',' ',' ',' ','P',' '},
                        {' ',' ',' ',' ',' ',' ',' ',' '}};

      board.setPosition(position);

      try {
         //can't be a King either
         move = new ChessMove(board, 7, 7, 7, 8, Pawn.INDEX); //g8=p
	 fail("Can't promote to King in standard chess rules");
      }
      catch (IllegalMoveException e) {}
   }

   //Odd Errors Found////////////////////////////////////////////////////
   /** David Spencer found a bug in this position.
    *  The bug was in the */
   public void testPromotionCaptureWithDoubleCheck () throws IllegalMoveException {
      
      //Log.addMask(ChessMove.DEBUG);
      //Log.addMask(ChessBoard.DEBUG);
      Piece piece = null;
      char[][] position={
                         {' ','P',' ',' ','p',' ',' ',' '},
                         {' ','P','K',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','n'},
                         {' ',' ','R',' ',' ',' ','P','k'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ','r',' ',' ',' ','p',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '}};

      char[][] position2={
                         {' ','P',' ',' ','p',' ',' ',' '},
                         {' ','P','K',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','Q'},
                         {' ',' ','R',' ',' ',' ',' ','k'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ','r',' ',' ',' ','p',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '}};


      board.setPosition(position);

      move = new ChessMove(board, 4, 7, 3, 8, Queen.INDEX); //dxc8=Q++
      board.playMove(move);
      board2 = new ChessBoard(position2);
      board2.setBlackMove(true);
      assertTrue(board.equals(board2));
      assertTrue(board.plyCount50 == 0);
      piece = board.getSquare('c','8').getOccupant();
      assertTrue(piece instanceof Queen);
      assertTrue(board.getLegalMoveCount() == 2); //take Q and e7
      assertTrue(!board.isCheckmate());
      assertTrue(board.isCheck());
      assertTrue(board.isDoubleCheck());
   }
}

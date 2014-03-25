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

package ictk.boardgame.chess.io;

import junit.framework.*;
import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;
import ictk.boardgame.chess.*;

public class SANTest extends TestCase {
   SAN san;
   ChessBoard board;
   ChessResult res;
   ChessMove   move;

   public SANTest (String name) {
      super(name);
   }

   public void setUp () {
      san = new SAN();
   }

   public void tearDown () {
      san = null;
      board = null;
      res = null;
      move = null;
      Log.removeMask(san.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToResult () {
      res = (ChessResult) san.stringToResult("1-0");
      assertTrue(res.equals(new ChessResult(ChessResult.WHITE_WIN)));
      res = (ChessResult) san.stringToResult("0-1");
      assertTrue(res.equals(new ChessResult(ChessResult.BLACK_WIN)));
      res = (ChessResult) san.stringToResult("1/2-1/2");
      assertTrue(res.equals(new ChessResult(ChessResult.DRAW)));
      res = (ChessResult) san.stringToResult("*");
      assertTrue(res.equals(new ChessResult(ChessResult.UNDECIDED)));
      res = (ChessResult) san.stringToResult("");
      assertTrue(res == null);
      res = (ChessResult) san.stringToResult("fjdkslfjs");
      assertTrue(res == null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testResultToString () {
      res = new ChessResult(ChessResult.WHITE_WIN);
      assertTrue("1-0".equals(san.resultToString(res)));
      res = new ChessResult(ChessResult.BLACK_WIN);
      assertTrue("0-1".equals(san.resultToString(res)));
      res = new ChessResult(ChessResult.DRAW);
      assertTrue("1/2-1/2".equals(san.resultToString(res)));
      res = new ChessResult(ChessResult.UNDECIDED);
      assertTrue("*".equals(san.resultToString(res)));
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToMove_1 () 
          throws IllegalMoveException, 
	         AmbiguousMoveException {
      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4");
      assertTrue (move != null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMoveToString_1 () 
          throws IllegalMoveException, 
	         AmbiguousMoveException {
      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4");
      assertTrue(san.moveToString(move).equals("e4"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testStringToMove_2 () {
      board = new ChessBoard();
      try {
         assertTrue (null ==  (ChessMove) san.stringToMove(board, "24"));
         assertTrue (null ==  (ChessMove) san.stringToMove(board, "fdfd"));
         assertTrue (null ==  (ChessMove) san.stringToMove(board, ""));
         assertTrue (null ==  (ChessMove) san.stringToMove(board, "	f5#@"));
	 try {
            move = (ChessMove) san.stringToMove(board, "e5");
	 }
	 catch (IllegalMoveException e) {}
      }
      catch (Exception e) {
         fail("san parsed garbage and threw and exception: " + e);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testPromotion () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ','P',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ','k',' '}};

      board = new ChessBoard(position);
      move = (ChessMove) san.stringToMove(board, "e8=Q");
      assertTrue (move != null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPromotionWithCheck () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ','P',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ','k'}};

      board = new ChessBoard(position);
      move = (ChessMove) san.stringToMove(board, "e8=Q+");
      assertTrue (move != null);
   }


   //////////////////////////////////////////////////////////////////////
   public void testPromotionWithCapture () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','n'},
                         {' ',' ',' ',' ',' ',' ','P',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ','k',' '}};

      board = new ChessBoard(position);
      move = (ChessMove) san.stringToMove(board, "exd8=Q");
      assertTrue (move != null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPromotionWithCaptureAndCheck () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','n'},
                         {' ',' ',' ',' ',' ',' ','P',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ','k'}};

      board = new ChessBoard(position);
      move = (ChessMove) san.stringToMove(board, "exd8=Q+");
      assertTrue (move != null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPromotionWithCaptureAndDoubleCheck () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ','n'},
                         {'R',' ',' ',' ',' ',' ','P','k'},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ',' '}};

      board = new ChessBoard(position);
      move = (ChessMove) san.stringToMove(board, "exd8=Q++");
      assertTrue (move != null);
   }

   //////////////////////////////////////////////////////////////////////
   /** testing bug: 775816 - full dis-ambiguation
    */
   public void testFullDisAmbiguation () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {'q',' ',' ',' ',' ',' ',' ',' '},
                         {'q',' ',' ','p','n',' ',' ',' '},
                         {' ',' ',' ','P','p',' ',' ','k'},
                         {' ',' ',' ',' ','P','p','q','b'},
                         {'N',' ',' ',' ',' ','Q',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ','Q',' ','Q'}};

      board = new ChessBoard(position);
      move = (ChessMove) san.stringToMove(board, "Qh6f8");
      assertTrue (move != null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testNAG () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      ChessAnnotation anno = null;

      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4!");
      assertTrue (move != null);
      anno = (ChessAnnotation) move.getAnnotation();
      assertTrue (anno != null);
      assertTrue (anno.getNAGString().equals("!"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testNAG1 () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      ChessAnnotation anno = null;

      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4! +=");
      assertTrue (move != null);
      anno = (ChessAnnotation) move.getAnnotation();
      assertTrue (anno != null);
      assertTrue (anno.getNAGString().equals("! +="));
   }

   //////////////////////////////////////////////////////////////////////
   public void testNAGNumeric () 
          throws IllegalMoveException,
                 AmbiguousChessMoveException {
      //Log.addMask(SAN.DEBUG);
      ChessAnnotation anno = null;

      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4 $9");
      assertTrue (move != null);
      anno = (ChessAnnotation) move.getAnnotation();
      assertTrue (anno != null);
      assertTrue (anno.getNAGString().equals("$9"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testFileToChar ()  {
      assertTrue (san.fileToChar(1) == 'a');
      assertTrue (san.fileToChar(8) == 'h');
      try {
        san.fileToChar(0);
	fail("FileToChar bounds not correct 0");
      }
      catch (ArrayIndexOutOfBoundsException e) {
      }
      try {
        san.fileToChar(9);
	fail("FileToChar bounds not correct 9");
      }
      catch (ArrayIndexOutOfBoundsException e) {
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testRankToChar ()  {
      assertTrue (san.rankToChar(1) == '1');
      assertTrue (san.rankToChar(8) == '8');
      try {
        san.rankToChar(0);
	fail("RankToChar bounds not correct 0");
      }
      catch (ArrayIndexOutOfBoundsException e) {
      }
      try {
        san.rankToChar(9);
	fail("RankToChar bounds not correct 9");
      }
      catch (ArrayIndexOutOfBoundsException e) {
      }
   }
}

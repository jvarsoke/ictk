/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: SANTest.java,v 1.4 2003/07/26 07:13:08 jvarsoke Exp $
 *
 *  This file is part of ICTK.
 *
 *  ICTK is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  ICTK is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ICTK; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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

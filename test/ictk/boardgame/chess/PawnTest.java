/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: PawnTest.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

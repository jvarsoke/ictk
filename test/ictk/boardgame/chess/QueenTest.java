/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: QueenTest.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

public class QueenTest extends TestCase {
   boolean DEBUG = false;
   ChessBoard board;
   ChessMove   move;
   List list;
   Queen queen;

   public QueenTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      move = null;
      queen = null;
      DEBUG = false;
   }

   //////////////////////////////////////////////////////////////////////
   public void testFullMoveScope () {
      board.setPositionClear();
      board.addQueen(4, 4, false); //d4
      board.addKing(1, 2, false);
      board.addKing(8, 2, true);
      queen = (Queen) board.getSquare(4,4).getOccupant();

      list = queen.getLegalDests();
      assertTrue(list.size() == 27);
      //Bishop moves
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

      //Rook moves
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

/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: BishopTest.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

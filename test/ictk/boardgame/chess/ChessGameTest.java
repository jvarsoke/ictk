/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessGameTest.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;

public class ChessGameTest extends TestCase {
   ChessGame game;
   History history;
   ChessBoard board;
   GameInfo gi;

   public ChessGameTest (String name) {
      super(name);
   }

   public void setUp () {
   }

   public void tearDown () {
      game = null;
      history = null;
      board = null;
      gi = null;
   }

   //////////////////////////////////////////////////////////////////////
   public void testConstructor () {
      game = new ChessGame();

      assertTrue(game.getBoard() != null);
      assertTrue(game.getHistory() != null);
   }

   //////////////////////////////////////////////////////////////////////
   public void testPlayerToMove () {
      game = new ChessGame();

      assertTrue(game.getPlayerToMove() == 0); //white
   }

   //////////////////////////////////////////////////////////////////////
   public void testResutl () {
      game = new ChessGame();

      assertTrue(game.getResult().isUndecided()); //white
   }
}

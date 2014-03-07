/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ContinuationListTest.java,v 1.1.1.1 2003/03/24 22:38:13 jvarsoke Exp $
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

package ictk.boardgame;

import junit.framework.*;

import ictk.util.Log;
import ictk.boardgame.chess.*;
import ictk.boardgame.chess.io.*;

public class ContinuationListTest extends TestCase {
   ChessGame game;
   SAN san;
   History history;
   ChessBoard board, board2;
   Move   move;
   ContinuationList cont;

/*
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P','N',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
*/


   public ContinuationListTest (String name) {
      super(name);
   }

   public void setUp () {
      san = new SAN();
   }

   public void tearDown () {
      game = null;
      history = null;
      board = board2 = null;
      san = null;
      cont = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testHeadList () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();
      board = (ChessBoard) game.getBoard();
      assertTrue(board != null);

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      history.prev();
      history.add(san.stringToMove(game.getBoard(), "g3"));
      history.prev();

      cont = history.getFirstAll();  //head continuation list

      assertTrue(cont.size() == 4);
      assertTrue(cont.exists(3)); 
      assertFalse(cont.exists(4));
      assertTrue(cont.hasMainLine());
      assertFalse(cont.isTerminal());
      assertTrue(cont.hasVariations());
      assertTrue(cont.getMainLine() == move);
      assertTrue(cont.get(0) == move);
      assertTrue(cont.sizeOfVariations() == 3);
      assertTrue(cont.getIndex(move) == 0);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShufflePromote1 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      Move move2, move3, move4 = null;
      game = new ChessGame();
      history = game.getHistory();

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(move2 = san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.add(move3 = san.stringToMove(game.getBoard(), "Nf3"));
      history.prev();
      history.add(move4 = san.stringToMove(game.getBoard(), "g3"));
      history.prev();

      cont = history.getFirstAll();  //head continuation list

      assertTrue(cont.size() == 4);
      assertTrue(cont.getMainLine() == move);
      cont.promote(move2, 1);

      assertTrue(cont.size() == 4);
      assertTrue(cont.getMainLine() == move2);
      //System.err.println(cont.dump());
      assertTrue(cont.get(1) == move);

      assertTrue(cont.get(3) == move4);
      cont.promote(move4, 2);
      assertTrue(cont.get(1) == move4);
      assertTrue(cont.get(3) == move3);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMainLineTerminal () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      Move move2, move3, move4 = null;
      game = new ChessGame();
      history = game.getHistory();

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(move2 = san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.add(move3 = san.stringToMove(game.getBoard(), "Nf3"));
      history.prev();
      history.add(move4 = san.stringToMove(game.getBoard(), "g3"));
      history.prev();

      cont = history.getFirstAll();  //head continuation list

      assertTrue(cont.size() == 4);
      assertTrue(cont.getMainLine() == move);
      assertTrue(cont.setMainLineTerminal()); //sets mainline to null
      assertTrue(cont.get(0) == null);
      assertTrue(cont.get(1) == move);

      assertTrue(cont.size() == 5);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShufflePromote2 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      Move move2, move3, move4 = null;
      game = new ChessGame();
      history = game.getHistory();

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(move2 = san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.add(move3 = san.stringToMove(game.getBoard(), "Nf3"));
      history.prev();
      history.add(move4 = san.stringToMove(game.getBoard(), "g3"));
      history.prev();

      cont = history.getFirstAll();  //head continuation list

      assertTrue(cont.size() == 4);
      assertTrue(cont.getMainLine() == move);
      assertTrue(cont.setMainLineTerminal()); //sets mainline to null
      assertTrue(cont.get(0) == null);
      assertTrue(cont.get(1) == move);

      assertTrue(cont.size() == 5);

      cont.promote(move3, 0); //to main line
      assertTrue(cont.size() == 4);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShuffleDemote () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      Move move2, move3, move4 = null;
      game = new ChessGame();
      history = game.getHistory();

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(move2 = san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.add(move3 = san.stringToMove(game.getBoard(), "Nf3"));
      history.prev();
      history.add(move4 = san.stringToMove(game.getBoard(), "g3"));
      history.prev();

      cont = history.getFirstAll();  //head continuation list

      assertTrue(cont.size() == 4);
      cont.demote(move, 1);
      assertTrue(cont.size() == 4);
      assertTrue(cont.getMainLine() == move2);
      assertTrue(cont.get(1) == move);
      assertTrue(cont.get(2) == move3);
   }

   //////////////////////////////////////////////////////////////////////
   public void testShuffleDemote2 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      Move move2, move3, move4 = null;
      game = new ChessGame();
      history = game.getHistory();

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(move2 = san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.add(move3 = san.stringToMove(game.getBoard(), "Nf3"));
      history.prev();
      history.add(move4 = san.stringToMove(game.getBoard(), "g3"));
      history.prev();

      cont = history.getFirstAll();  //head continuation list

      assertTrue(cont.size() == 4);
      cont.demote(move, 0); //to last variation
      assertTrue(cont.size() == 4);
      assertTrue(cont.getMainLine() == move2);
      assertTrue(cont.get(1) == move3);
      assertTrue(cont.get(2) == move4);
      assertTrue(cont.get(3) == move);
   }
}

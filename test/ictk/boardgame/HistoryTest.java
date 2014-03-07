/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: HistoryTest.java,v 1.2 2003/07/30 07:18:50 jvarsoke Exp $
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

public class HistoryTest extends TestCase {
   ChessGame game;
   SAN san;
   History history;
   ChessBoard board, board2;
   Move   move;
   Move[] moves;

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


   public HistoryTest (String name) {
      super(name);
   }

   public void setUp () {
      san = new SAN();
   }

   public void tearDown () {
      game = null;
      history = null;
      moves = null;
      board = board2 = null;
      san = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testAdd () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCR () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      board = (ChessBoard) game.getBoard();
      history.prev();
      history.next();
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCR2 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      board = (ChessBoard) game.getBoard();
      history.prev();
      history.prev();
      history.prev();
      history.next();
      history.next();
      history.next();
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCR3 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      board = (ChessBoard) game.getBoard();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.prev();
      history.next();
      history.next();
      history.next();
      history.next();
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCRRewind () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      board = (ChessBoard) game.getBoard();
      history.rewind();
      history.next();
      history.next();
      history.next();
      history.next();
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCRFastForward () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(move = san.stringToMove(game.getBoard(), "Nf3"));
      board = (ChessBoard) game.getBoard();
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      history.add(san.stringToMove(game.getBoard(), "Bc4"));

      history.rewind();
      history.fastforward(3);
      assertTrue(move == history.getCurrentMove());
      assertTrue(board.equals(game.getBoard()));
   }


   //////////////////////////////////////////////////////////////////////
   public void testVCRRewindFF () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(move = san.stringToMove(game.getBoard(), "Nf3"));
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      history.add(san.stringToMove(game.getBoard(), "Bc4"));
      board = (ChessBoard) game.getBoard();

      history.rewind();
      history.goToEnd();
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCRRewindGoto () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(move = san.stringToMove(game.getBoard(), "Nf3"));
      board = (ChessBoard) game.getBoard();
      history.add(san.stringToMove(game.getBoard(), "Nc6"));

      history.rewind();
      history.goTo(move);
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVCRFFGoto () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(move = san.stringToMove(game.getBoard(), "Nf3"));
      board = (ChessBoard) game.getBoard();
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      history.add(san.stringToMove(game.getBoard(), "Bc4"));

      history.rewind();
      history.goToEnd();
      history.goTo(move);
      assertTrue(board.equals(game.getBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.prev();
      history.next(); //next should play 1.e4 

        //should be added to the main line
      history.add(san.stringToMove(game.getBoard(), "e5"));
         //next is an impossible move if the variation did not work out
      history.add(san.stringToMove(game.getBoard(), "d4"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations2 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(san.stringToMove(game.getBoard(), "d5"));
      history.prev();
      history.prev();
      history.next(); //next should play 1.e4 

        //should be added to the main line
      history.add(san.stringToMove(game.getBoard(), "e5"));
         //next is an impossible move if the variation did not work out
      history.add(san.stringToMove(game.getBoard(), "d4"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations3 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(san.stringToMove(game.getBoard(), "d5"));
      history.rewind();
      history.next(); //next should play 1.e4 

        //should be added to the main line
      history.add(san.stringToMove(game.getBoard(), "e5"));
         //next is an impossible move if the variation did not work out
      history.add(san.stringToMove(game.getBoard(), "d4"));
   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations4Goto () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(move = san.stringToMove(game.getBoard(), "d5"));
      board = (ChessBoard) game.getBoard();
      history.rewind();
      history.next(); //next should play 1.e4 

        //should be added to the main line
      history.add(san.stringToMove(game.getBoard(), "e5"));
         //next is an impossible move if the variation did not work out
      history.add(san.stringToMove(game.getBoard(), "d4"));

      history.goTo(move);

      assertTrue(board.equals(game.getBoard()));

   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations4aGotoBad () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      ChessGame game2 = new ChessGame();
      game2.getHistory().add(move = san.stringToMove(game2.getBoard(), "c4"));

      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "d4"));

      //going to move from another board -- illegal
      try {
         history.goTo(move);
	 fail("shouldn't be able to goto a move outside"
	      + " the history list.");
      }
      catch (IllegalArgumentException e) {
         //can't goto a move not his this history list
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations5Next () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(move = san.stringToMove(game.getBoard(), "d5"));
      board = (ChessBoard) game.getBoard();
      history.rewind();
      history.next(); //next should play 1.e4 

        //should be added to the main line
      history.add(san.stringToMove(game.getBoard(), "e5"));
         //next is an impossible move if the variation did not work out
      history.add(san.stringToMove(game.getBoard(), "d4"));

      history.rewind();
      history.next(1);  //1.d4 variation
      history.goToEnd();

      assertTrue(board.equals(game.getBoard()));

   }

   //////////////////////////////////////////////////////////////////////
   public void testVariations6NextBad () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
         //add as a variation
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(move = san.stringToMove(game.getBoard(), "d5"));
      board = (ChessBoard) game.getBoard();
      history.rewind();
      history.next(); //next should play 1.e4 

        //should be added to the main line
      history.add(san.stringToMove(game.getBoard(), "e5"));
         //next is an impossible move if the variation did not work out
      history.add(san.stringToMove(game.getBoard(), "d4"));

      history.rewind();

      try {
         history.next(2);  //illegal variation
	 fail("Next index should have been out of range");
      }
      catch (ArrayIndexOutOfBoundsException e) {
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testRewindToFork () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(move = san.stringToMove(game.getBoard(), "e4"));
      history.prev();
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(san.stringToMove(game.getBoard(), "d5"));

      history.rewindToLastFork();
      assertTrue(history.getCurrentMove() == null);
      
      history.next();
      assertTrue(history.getCurrentMove() == move);

      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));

   }

   //////////////////////////////////////////////////////////////////////
   public void testRewindToFork2 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
      history.add(move = san.stringToMove(game.getBoard(), "d4"));
      history.add(san.stringToMove(game.getBoard(), "d5"));
      history.prev();
      history.add(san.stringToMove(game.getBoard(), "Nf6"));

      history.rewindToLastFork();
      assertTrue(history.getCurrentMove() == move);
      
   }

   //////////////////////////////////////////////////////////////////////
   public void testEquality () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      ChessGame game2 = new ChessGame();
      History history2 = game2.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));

      history2.add(san.stringToMove(game2.getBoard(), "e4"));
      history2.add(san.stringToMove(game2.getBoard(), "e5"));
      history2.add(san.stringToMove(game2.getBoard(), "Nf3"));

      assertTrue(history.equals(history2));
      assertTrue(history2.equals(history));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualityBad () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      ChessGame game2 = new ChessGame();
      History history2 = game2.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));

      history2.add(san.stringToMove(game2.getBoard(), "e4"));
      history2.add(san.stringToMove(game2.getBoard(), "e5"));
      history2.add(san.stringToMove(game2.getBoard(), "Bc4"));

      assertFalse(history.equals(history2));
      assertFalse(history2.equals(history));
   }

   //////////////////////////////////////////////////////////////////////
   public void testDeepEquality () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      ChessGame game2 = new ChessGame();
      History history2 = game2.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
      history.add(san.stringToMove(game.getBoard(), "d4"));
      history.add(san.stringToMove(game.getBoard(), "d5"));

      history.rewindToLastFork();
      history.next(); //e4

      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));


      history2.add(san.stringToMove(game2.getBoard(), "e4"));
      history2.prev();
      history2.add(san.stringToMove(game2.getBoard(), "d4"));
      history2.add(san.stringToMove(game2.getBoard(), "d5"));

      history2.rewindToLastFork();
      history2.next(); //e4

      history2.add(san.stringToMove(game2.getBoard(), "e5"));
      history2.add(san.stringToMove(game2.getBoard(), "Nf3"));

      assertTrue(history.equals(history2));
      assertTrue(history2.equals(history));

      assertTrue(history.deepEquals(history2, false));
      assertTrue(history2.deepEquals(history, false));
   }

   //////////////////////////////////////////////////////////////////////
   public void testDeepEquality2 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      ChessGame game2 = new ChessGame();
      History history2 = game2.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
      history.add(move = san.stringToMove(game.getBoard(), "d4"));
      ChessAnnotation anno = new ChessAnnotation();
      anno.addNAG((short)1);
      move.setAnnotation(anno);
      history.add(san.stringToMove(game.getBoard(), "d5"));

      history.rewindToLastFork();
      history.next(); //e4

      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));


      history2.add(san.stringToMove(game2.getBoard(), "e4"));
      history2.prev();
      history2.add(move = san.stringToMove(game2.getBoard(), "d4"));
      anno = new ChessAnnotation();
      anno.addNAG((short) 2);
      move.setAnnotation(anno);
      history2.add(san.stringToMove(game2.getBoard(), "d5"));

      history2.rewindToLastFork();
      history2.next(); //e4

      history2.add(san.stringToMove(game2.getBoard(), "e5"));
      history2.add(san.stringToMove(game2.getBoard(), "Nf3"));

      assertTrue(history.equals(history2));
      assertTrue(history2.equals(history));

      assertTrue(history.deepEquals(history2, false));
      assertTrue(history2.deepEquals(history, false));
   }

   //////////////////////////////////////////////////////////////////////
   public void testDeepEquality3 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      ChessGame game2 = new ChessGame();
      History history2 = game2.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
      history.add(move = san.stringToMove(game.getBoard(), "d4"));
      ChessAnnotation anno = new ChessAnnotation();
      anno.addNAG((short)1);
      move.setAnnotation(anno);
      history.add(san.stringToMove(game.getBoard(), "d5"));

      history.rewindToLastFork();
      history.next(); //e4

      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));


      history2.add(san.stringToMove(game2.getBoard(), "e4"));
      history2.prev();
      history2.add(move = san.stringToMove(game2.getBoard(), "d4"));
      anno = new ChessAnnotation();
      anno.addNAG((short) 1);
      move.setAnnotation(anno);
      history2.add(san.stringToMove(game2.getBoard(), "d5"));

      history2.rewindToLastFork();
      history2.next(); //e4

      history2.add(san.stringToMove(game2.getBoard(), "e5"));
      history2.add(san.stringToMove(game2.getBoard(), "Nf3"));

      assertTrue(history.equals(history2));
      assertTrue(history2.equals(history));

      assertTrue(history.deepEquals(history2, true));
      assertTrue(history2.deepEquals(history, true));
   }

   //////////////////////////////////////////////////////////////////////
   public void testDeepEquality4 () throws IllegalMoveException,
                              OutOfTurnException,
                              AmbiguousMoveException {
      game = new ChessGame();
      history = game.getHistory();

      ChessGame game2 = new ChessGame();
      History history2 = game2.getHistory();

      history.add(san.stringToMove(game.getBoard(), "e4"));
      history.prev();
      history.add(move = san.stringToMove(game.getBoard(), "d4"));
      ChessAnnotation anno = new ChessAnnotation();
      anno.addNAG((short)1);
      move.setAnnotation(anno);
      history.add(san.stringToMove(game.getBoard(), "d5"));

      history.rewindToLastFork();
      history.next(); //e4

      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));


      history2.add(san.stringToMove(game2.getBoard(), "e4"));
      history2.prev();
      history2.add(move = san.stringToMove(game2.getBoard(), "d4"));
      anno = new ChessAnnotation();
      anno.addNAG((short) 3);
      move.setAnnotation(anno);
      history2.add(san.stringToMove(game2.getBoard(), "d5"));

      history2.rewindToLastFork();
      history2.next(); //e4

      history2.add(san.stringToMove(game2.getBoard(), "e5"));
      history2.add(san.stringToMove(game2.getBoard(), "Nf3"));

      assertTrue(history.equals(history2));
      assertTrue(history2.equals(history));

      assertFalse(history.deepEquals(history2, true));
      assertFalse(history2.deepEquals(history, true));

      assertTrue(history.deepEquals(history2, false));
      assertTrue(history2.deepEquals(history, false));
   }

   public void testSize () throws IllegalMoveException,
                      OutOfTurnException,
		      AmbiguousMoveException {

      game = new ChessGame();
      history = game.getHistory();
      history.add(san.stringToMove(game.getBoard(), "e4"));
      assert(history.size() == 1);
      history.add(san.stringToMove(game.getBoard(), "e5"));
      history.add(san.stringToMove(game.getBoard(), "Nf3"));
      assert(history.size() == 3);
      history.prev();
      history.add(san.stringToMove(game.getBoard(), "Nc3"));
      assert(history.size() == 3);
      history.add(san.stringToMove(game.getBoard(), "Nf6"));
      history.rewindToLastFork();
      history.next();
      history.add(san.stringToMove(game.getBoard(), "Nc6"));
      assert(history.size() == 4);
   }
}

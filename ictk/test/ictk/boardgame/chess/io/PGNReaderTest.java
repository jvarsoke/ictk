/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
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

import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class PGNReaderTest extends TestCase {
   public static String dataDir = "./";
   String pgn_nonvariation = "test_nonvariation.pgn",
          pgn_variation    = "test_variation.pgn",
          pgn_annotation   = "test_annotation.pgn",
	  pgn_bad          = "test_bad.pgn",
	  pgn_debug        = "test_debug.pgn";
   SAN         san;
   ChessBoard  board;
   ChessResult res;
   ChessMove   move;
   ChessReader in;
   Game        game;
   List        games;

   public PGNReaderTest (String name) {
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
      game = null;
      in = null;
      games = null;
      Log.removeMask(san.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkNonVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_nonvariation, false);
      assertTrue(games.size() > 0);
      
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_variation, false);
      assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkAnnotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false);
      assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   /** this is used for testing new PGNs that have revealed bugs.
    *  After the bug is squashed the PGN should be moved to another
    *  file to become a permenent member of the regression testing
    *  suite.
    */
   public void testDebug () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      loadGames(dataDir + pgn_debug, false);
   }


   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationCommentAfterMove () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      games = loadGames(dataDir + pgn_annotation, false);

	 game = (Game) games.get(0);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 ChessAnnotation anno 
	    = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getComment().equals("Best by test"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationExclaimation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false);

	 game = (Game) games.get(0);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();
	 ChessAnnotation anno 
	    = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getSuffix() == (short) 1);
   }

   ///////////////////////////////////////////////////////////////////////////
   /*
   public void testAnnotationCommentBeforeGame () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, true);

	 game = (Game) games.get(1);
	 History history = game.getHistory();

	 history.rewind();
	 ChessAnnotation anno 
	    = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getComment().equals("Comment Before Game"));
   }
   */

   ///////////////////////////////////////////////////////////////////////////
   public void testBadPGNs () 
          throws FileNotFoundException,
	         InvalidGameFormatException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + pgn_bad)));

         try {
	    game = in.readGame();
	    fail("read in bad game but no error?");
	 }
	 catch (IOException e) {
	    game = in.getGame(); //from failed read
	    assertTrue(28 == game.getHistory().size());
	 }
	 catch (IllegalMoveException e) {
	    fail("wrong error for game 1: " + e);
	 }
	 catch (AmbiguousMoveException e) {
	    fail("wrong error for game 1: " + e);
	 }
   }

   //Helper///////////////////////////////////////////////////////////////////
   /** loads the games into a list so aspects of the games can be tested */
   protected List loadGames (String file, boolean debug)
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      List list = new LinkedList();

      if (debug) {
        Log.addMask(SAN.DEBUG);
        Log.addMask(PGNReader.DEBUG);
      }
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(file)));

	 while ((game = in.readGame()) != null) {
	    game.getHistory().goToEnd();
	    list.add(game);
	    count++;
	    game = null;
	 }

      if (debug) {
         Log.removeMask(SAN.DEBUG);
	 Log.removeMask(PGNReader.DEBUG);
      }
      return list;
   }
}

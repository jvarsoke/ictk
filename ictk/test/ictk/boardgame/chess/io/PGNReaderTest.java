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

public class PGNReaderTest extends TestCase {
   public static String dataDir = "./";
   String bulk_nonvariation = "test_nonvariation.pgn",
          bulk_variation = "test_variation.pgn",
          bulk_annotation = "test_annotation.pgn",
	  bulk_bad = "test_bad.pgn";
   SAN san;
   ChessBoard board;
   ChessResult res;
   ChessMove   move;
   ChessReader in;
   Game game;

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
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_nonvariation)));

	 while ((game = in.readGame()) != null) {
	    game.getHistory().goToEnd();
	 }
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_variation)));

	 while ((game = in.readGame()) != null) {
	    game.getHistory().goToEnd();
	 }
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkAnnotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_annotation)));

	 while ((game = in.readGame()) != null) {
	    game.getHistory().goToEnd();
	 }

   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_annotation)));

	 game = in.readGame();
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 ChessAnnotation anno 
	    = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getComment().equals("Best by test"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotation2 () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_annotation)));

	 game = in.readGame();
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();
	 ChessAnnotation anno 
	    = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getSuffix() == (short) 1);
   }

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
		    new File(dataDir + bulk_bad)));

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
}

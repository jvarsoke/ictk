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

public class PGNWriterTest extends TestCase {
   public static String dataDir = "./";
   String bulk_nonvariation = "test_nonvariation.pgn",
          bulk_variation = "test_variation.pgn",
          bulk_annotation = "test_annotation.pgn";
   PGNWriter writer;
   SAN san;
   ChessBoard board;
   ChessResult res;
   ChessMove   move;
   ChessReader in;
   Game game, game2;
   StringWriter sw;
   StringReader sr;
   PGNReader spgnin;

   public PGNWriterTest (String name) {
      super(name);
   }

   public void setUp () {
      san = new SAN();
   }

   public void tearDown () {
      spgnin = null;
      sw = null;
      sr = null;
      writer = null;
      san = null;
      board = null;
      res = null;
      move = null;
      game = null;
      game2 = null;
      in = null;
      Log.removeMask(san.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
      Log.removeMask(ChessGameInfo.DEBUG);
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
      //Log.addMask(ChessGameInfo.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_nonvariation)));

	 while ((game = in.readGame()) != null) {
	    writer = new PGNWriter(sw = new StringWriter()); 
	    writer.writeGame(game);
	    spgnin = new PGNReader(new StringReader(sw.toString()));
	    game2 = spgnin.readGame();
	    assertTrue(game.getHistory().equals(game2.getHistory()));
	    assertTrue(((ChessGameInfo)game.getGameInfo())
	      .equals(game2.getGameInfo()));
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
      //Log.addMask(ChessGameInfo.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + bulk_variation)));

	 while ((game = in.readGame()) != null) {
	    writer = new PGNWriter(sw = new StringWriter()); 
	    writer.writeGame(game);
	    spgnin = new PGNReader(new StringReader(sw.toString()));
	    game2 = spgnin.readGame();

	    assertTrue(((ChessGameInfo)game.getGameInfo())
	      .equals(game2.getGameInfo()));

	    assertTrue(game.getHistory().equals(game2.getHistory()));
	    assertTrue(game.getHistory().deepEquals(game2.getHistory(),false));
	    assertTrue(game.getHistory().deepEquals(game2.getHistory(),true));
	 }
   }
}

/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: PGNWriterTest.java,v 1.8 2003/08/12 18:56:13 jvarsoke Exp $
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

public class PGNWriterTest extends TestCase {
   public String dataDir = "./";
   String pgn_nonvariation = "test_nonvariation.pgn",
          pgn_variation    = "test_variation.pgn",
          pgn_annotation   = "test_annotation.pgn",
	  pgn_debug	   = "test_debug.pgn";

   PGNWriter writer;
   SAN san;
   ChessBoard board;
   ChessResult res;
   ChessMove   move, move2;
   ChessReader in;
   Game game, game2;
   StringWriter sw;
   StringReader sr;
   PGNReader spgnin;
   ChessAnnotation anno;
   List list;

   public PGNWriterTest (String name) {
      super(name);

      if (System.getProperty("ictk.boardgame.chess.io.dataDir") != null)
         dataDir = System.getProperty("ictk.boardgame.chess.io.dataDir");
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
      anno = null;
      Log.removeMask(san.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
      Log.removeMask(ChessGameInfo.DEBUG);
      Log.removeMask(PGNWriter.DEBUG);
   }

   ///////////////////////////////////////////////////////////////////////////
   /** bug: 784950 - wrong disambiguation on pawn capture.
    *  This is a tricky bug, and specific to the PGN used for testing it.
    *  It turned out that ChessBoard thought the move was ambigious because
    *  it was looking at captured pieces as well as uncaptured pieces to 
    *  determine if the move was ambigious.
    */ 
   public void testWrongDisambiguationBug () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      //Log.addMask(ChessGameInfo.DEBUG);
      try {
         list = PGNReaderTest.loadGames(dataDir + pgn_variation, false, 9);
	 game = (ChessGame) list.get(9);

	 writer = new PGNWriter(sw = new StringWriter());
	 writer.writeGame(game);

	 String pgn = sw.toString();
	 assertTrue(pgn.indexOf("5xd4") < 0);
      }
      finally {
          Log.removeMask(SAN.DEBUG);
          Log.removeMask(PGNReader.DEBUG);
          Log.removeMask(ChessGameInfo.DEBUG);
          Log.removeMask(PGNWriter.DEBUG);
      }
   }


   ///////////////////////////////////////////////////////////////////////////
   /* this tests to make sure variations are not nested unnecessiarily.
    * The move string should look like this: 1.e4 e5 ( 1...c5 ) (1...e6 ) *
    * Not like this:                         1.e4 e5 ( 1...c5 ( 1...e6 ) ) *
    * bug: 778408
    */
   public void testVariationNesting () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      //Log.addMask(ChessGameInfo.DEBUG);
      try { 
            game = new ChessGame();
            board = (ChessBoard) ((ChessGame) game).getBoard();

            move = (ChessMove) san.stringToMove(board, "e4");
            game.getHistory().add(move);

            //main line
	    move = (ChessMove) san.stringToMove(board, "e5");
            game.getHistory().add(move);

	    game.getHistory().prev();

            //variation 1
	    move = (ChessMove) san.stringToMove(board, "c5");
            game.getHistory().add(move);

	    game.getHistory().prev();

            //variation 2
	    move = (ChessMove) san.stringToMove(board, "e6");
            game.getHistory().add(move);

            writer = new PGNWriter(sw = new StringWriter());
	    writer.writeGame(game);

	    BufferedReader in = new BufferedReader(
	                           new StringReader(sw.toString()));
	    boolean found = false;
	    String line = null;

	    while (!found && (line = in.readLine()) != null) {
	       found = line.startsWith("1.e4");
	    }

	    assertTrue(found);
            int openVarIndex = line.indexOf('(');
	    assertTrue(openVarIndex != -1);

	    int closeVarIndex = line.indexOf(')');
	    assertTrue(closeVarIndex != -1);

	    int nextOpenVarIndex = line.indexOf('(', openVarIndex+1);
	    assertTrue(nextOpenVarIndex != -1);

            //make sure we closed the first variation before opening the 2nd.
	    assertTrue(closeVarIndex < nextOpenVarIndex);

      }
      finally {
          Log.removeMask(SAN.DEBUG);
          Log.removeMask(PGNReader.DEBUG);
          Log.removeMask(ChessGameInfo.DEBUG);
          Log.removeMask(PGNWriter.DEBUG);
      }
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
      try { 
	 int count = 0;

	    in = new PGNReader(
		    new FileReader(
		       new File(dataDir + pgn_nonvariation)));

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
      finally {
          Log.removeMask(SAN.DEBUG);
          Log.removeMask(PGNReader.DEBUG);
          Log.removeMask(ChessGameInfo.DEBUG);
          Log.removeMask(PGNWriter.DEBUG);
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
      //Log.addMask(PGNWriter.DEBUG);
      //Log.addMask(History.DEBUG);

      int count = 0;
      try {

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + pgn_variation)));

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
	    count++;
	 }
      }
      catch (Exception e) {
         if (Log.isDebug(PGNWriter.DEBUG)) {
            System.out.println("Game: " + count);
            new PGNWriter(System.err).writeGame(game); 
         }
	 
         throw e;
      }
      finally {
          Log.removeMask(SAN.DEBUG);
          Log.removeMask(PGNReader.DEBUG);
          Log.removeMask(ChessGameInfo.DEBUG);
          Log.removeMask(PGNWriter.DEBUG);
          Log.removeMask(History.DEBUG);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testZeroGameInfoZeroHistory () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      //Log.addMask(PGNWriter.DEBUG);

      game = new ChessGame();
      board = (ChessBoard) ((ChessGame) game).getBoard();

      writer = new PGNWriter(sw = new StringWriter()); 

      writer.writeGame(game);

      Log.removeMask(PGNWriter.DEBUG);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testPrenotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      //Log.addMask(PGNWriter.DEBUG);
      //Log.addMask(History.DEBUG);

      try {
	 game = new ChessGame();
	 board = (ChessBoard) ((ChessGame) game).getBoard();

	 move = (ChessMove) san.stringToMove(board, "e4");

	 game.getHistory().add(move);
	 move.setPrenotation(new ChessAnnotation("before1"));

	 writer = new PGNWriter(sw = new StringWriter()); 

	 writer.writeGame(game);

	 spgnin = new PGNReader(new StringReader(sw.toString()));
	 game2 = spgnin.readGame();

	 assertTrue(game.getHistory().equals(game2.getHistory()));
	 assertTrue(game.getHistory().deepEquals(game2.getHistory(),false));
	 assertTrue(game.getHistory().deepEquals(game2.getHistory(),true));

	 game.getHistory().next();
	 game2.getHistory().next();

	 assertTrue(game.getHistory().getCurrentMove().equals(
	    game2.getHistory().getCurrentMove()));

	 //the prenotation test
	 move = (ChessMove) game.getHistory().getCurrentMove();
	 move2 = (ChessMove) game2.getHistory().getCurrentMove();
	 assertTrue(move.getPrenotation().equals(move2.getPrenotation()));
      }
      catch (Exception e) {
         throw e;
      }
      finally {
         Log.removeMask(PGNWriter.DEBUG);
	 Log.removeMask(History.DEBUG);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationThenPrenotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      //Log.addMask(PGNWriter.DEBUG);

      game = new ChessGame();
      board = (ChessBoard) ((ChessGame) game).getBoard();

      move = (ChessMove) san.stringToMove(board, "e4");

      game.getHistory().add(move);
      move.setAnnotation(new ChessAnnotation("after1"));

      move = (ChessMove) san.stringToMove(board, "e5");
      game.getHistory().add(move);
      move.setPrenotation(new ChessAnnotation("before2"));

      writer = new PGNWriter(sw = new StringWriter()); 

      writer.writeGame(game);

      spgnin = new PGNReader(new StringReader(sw.toString()));
      game2 = spgnin.readGame();

      assertTrue(game.getHistory().equals(game2.getHistory()));
      assertTrue(game.getHistory().deepEquals(game2.getHistory(),false));
      assertTrue(game.getHistory().deepEquals(game2.getHistory(),true));

      game.getHistory().next();
      game2.getHistory().next();
      assertTrue(game.getHistory().getCurrentMove().equals(
         game2.getHistory().getCurrentMove()));

      //the prenotation test
      move = (ChessMove) game.getHistory().getCurrentMove();
      move2 = (ChessMove) game2.getHistory().getCurrentMove();
      assertTrue(move.getAnnotation().equals(move2.getAnnotation()));

      game.getHistory().next();
      game2.getHistory().next();
      move = (ChessMove) game.getHistory().getCurrentMove();
      move2 = (ChessMove) game2.getHistory().getCurrentMove();
      assertTrue(move.getPrenotation().equals(move2.getPrenotation()));

      Log.removeMask(PGNWriter.DEBUG);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testPrenotationForVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      //Log.addMask(PGNWriter.DEBUG);

      game = new ChessGame();
      board = (ChessBoard) ((ChessGame) game).getBoard();

      move = (ChessMove) san.stringToMove(board, "e4");
      game.getHistory().add(move);

      move = (ChessMove) san.stringToMove(board, "e5");
      game.getHistory().add(move);

      //add the variation
      game.getHistory().prev();
      move = (ChessMove) san.stringToMove(board, "c5");
      game.getHistory().add(move);
      move.setPrenotation(new ChessAnnotation("Sicilian"));

      writer = new PGNWriter(sw = new StringWriter()); 

      writer.writeGame(game);

      spgnin = new PGNReader(new StringReader(sw.toString()));
      game2 = spgnin.readGame();

      assertTrue(game.getHistory().equals(game2.getHistory()));
      assertTrue(game.getHistory().deepEquals(game2.getHistory(),false));
      assertTrue(game.getHistory().deepEquals(game2.getHistory(),true));

      game.getHistory().next();
      game2.getHistory().next();

      //go down variation line
      game.getHistory().next(1);
      game2.getHistory().next(1);

      move = (ChessMove) game.getHistory().getCurrentMove();
      move2 = (ChessMove) game2.getHistory().getCurrentMove();
      assertTrue(move.getPrenotation().equals(move2.getPrenotation()));

      Log.removeMask(PGNWriter.DEBUG);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGSymetry () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      //Log.addMask(PGNWriter.DEBUG);

      game = new ChessGame();
      board = (ChessBoard) ((ChessGame) game).getBoard();

      move = (ChessMove) san.stringToMove(board, "e4");
      anno = new ChessAnnotation();
      anno.addNAG(1);   // !
      anno.addNAG(123); // $123
      anno.addNAG(145); // RR
      move.setAnnotation(anno);
      game.getHistory().add(move);

      move = (ChessMove) san.stringToMove(board, "e5");
      game.getHistory().add(move);

      writer = new PGNWriter(sw = new StringWriter()); 

      writer.writeGame(game);

      if (Log.isDebug(PGNWriter.DEBUG)) {
         writer = new PGNWriter(System.out);
	 writer.writeGame(game);
      }

      spgnin = new PGNReader(new StringReader(sw.toString()));
      game2 = spgnin.readGame();

      assertTrue(game.getHistory().equals(game2.getHistory()));
      assertTrue(game.getHistory().deepEquals(game2.getHistory(),false));
      assertTrue(game.getHistory().deepEquals(game2.getHistory(),true));

      Log.removeMask(PGNWriter.DEBUG);
   }
}

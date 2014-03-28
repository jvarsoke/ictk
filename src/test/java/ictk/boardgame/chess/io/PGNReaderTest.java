/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ictk.boardgame.chess.io;

import junit.framework.*;
import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;
import ictk.boardgame.chess.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.LinkedList;
import java.net.URL;

public class PGNReaderTest extends TestCase {
   public String dataDir = "./";
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
   ChessAnnotation anno;

   //constructor
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
      anno = null;
      Log.removeMask(san.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
   }

   //UTILITY//////////////////////////////////////////////////////////////////
   /** load the local test resource file by leveraging the classpath.
    *  This should grab the file from where the tests are being run.
    */
   public File getTestFile (String filename) throws URISyntaxException {
      File file = null;

      if (file == null || !file.exists()) {
	 URL r = this.getClass().getResource(filename);
         assertNotNull("Couldn't find '" + filename + "' on classpath", r);
	 file = new File(r.toURI());
      }

      assertNotNull("Null resource file: '" + filename + "'", file);
      assertTrue("Couldn't read resource: '" + filename + "'", file.exists());

      return file;
   }

   //DEBUG SECTION////////////////////////////////////////////////////////////

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

      games = loadGames(getTestFile(pgn_debug), false, -1);
      //assertTrue(games.size() > 0);
   }

//STOP//
//public void testStop () { System.exit(0); }

   //NON-VARIATION SECTION////////////////////////////////////////////////////

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkNonVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_nonvariation), false, -1);
      assertTrue(games.size() > 0);
      
   }

   //VARIATION SECTION////////////////////////////////////////////////////////

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_variation), false, -1);
      assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   /** History.add() used to disallow duplicate continuations.
    *  That isn't the case anymore.
    *  bug: 779762 - not allowing duplicate continuations.
    */
   public void testDuplicateContinuation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_variation), false, 7);
      assertTrue(games.size() > 0);
      game = (ChessGame) games.get(7);
      game.getHistory().fastforward(50);
      ContinuationList cont = game.getHistory()
                                  .getCurrentMove()
				  .getContinuationList();
      assertTrue(cont.size() == 2);
   }

   ///////////////////////////////////////////////////////////////////////////
   /** bug: 780102 - result being assigned to variation instead of main line.
    */
   public void testMainLineResultOnVariationBugTest () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_variation), false, 8);
      assertTrue(games.size() > 0);
      game = (ChessGame) games.get(8);

      ChessResult res1 = (ChessResult) game.getResult();
      ChessResult res2 = (ChessResult) game.getHistory()
                                           .getFinalMove(true).getResult();

      assertTrue(res1 != null);
      assertTrue(res2 != null);
      assertTrue(res1.equals(res2));
      assertTrue(res1.equals(new ChessResult(ChessResult.DRAW)));
   }

   //ANNOTAITON SECTION///////////////////////////////////////////////////////

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkAnnotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, -1);
      assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationCommentAfterMove () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      games = loadGames(getTestFile(pgn_annotation), false, 0);

	 game = (Game) games.get(0);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getComment().equals("Best by test"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNonVariationResultSwitchBug () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      games = loadGames(getTestFile(pgn_nonvariation), false, 4);
      game = (ChessGame) games.get(4);

      game.getHistory().rewind();
      res = (ChessResult) game.getResult();
      game.getHistory().goToEnd();
      Result res2 = game.getHistory().getCurrentMove().getResult();
      assertTrue(res.equals(res2));
      assertTrue(res.isWhiteWin());
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationExclaimation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 1);

	 game = (Game) games.get(1);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getSuffix() == (short) 1);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationCommentBeforeGame () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 2);

	 game = (Game) games.get(2);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Comment Before Game"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationComment2ndMoveWithCommentAfter1st () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 3);

	 game = (Game) games.get(3);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

         //check post-notation of first move
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after1"));

         //check pre-notation of second move
	 history.next();
	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("before2"));

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after2"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotation2CommentsAfter1stOneBefore2nd () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 4);

	 game = (Game) games.get(4);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

         //check post-notation of first move
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after1 after1a"));

         //check pre-notation of second move
	 history.next();
	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("before2"));

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after2"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationEndLineComment () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 6);

	 game = (Game) games.get(6);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Best by test"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotation2EndLineCommentsInARow () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 7);

	 game = (Game) games.get(7);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Best by test so says Fischer"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationHeadingVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 8);

	 game = (Game) games.get(8);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next(1);

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Sicilian"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationHeadingVariationEOL () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 9);

	 game = (Game) games.get(9);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next(1);

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Sicilian"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationBeforeVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 10);

	 game = (Game) games.get(10);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment() != null);
	 assertTrue(anno.getComment().equals("post"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationAfterNAGBeforeVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 11);

	 game = (Game) games.get(11);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment() != null);
	 assertTrue(anno.getComment().equals("post"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testDoubleAnnotationBeforeVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 12);

	 game = (Game) games.get(12);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment() != null);
	 assertTrue(anno.getComment().equals("post repost"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testDoubleAnnotationBeforeVariationEnd () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 13);

	 game = (Game) games.get(13);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next(1);

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment() != null);
	 assertTrue(anno.getComment().equals("post repost"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGOver8 () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 14);

	 game = (Game) games.get(14);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getNAG(0) == 9);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGSymbol () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(getTestFile(pgn_annotation), false, 15);

	 game = (Game) games.get(15);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getNAG(0) == 145 );
   }

   //BAD PGNs SECTION/////////////////////////////////////////////////////////

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
		    getTestFile(pgn_bad)));

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
	 finally {
	    Log.removeMask(SAN.DEBUG);
	    Log.removeMask(PGNReader.DEBUG);
	 }
   }

   //Helper///////////////////////////////////////////////////////////////////

   /** loads the games into a list so aspects of the games can be tested */
   protected static List loadGames (File file, boolean debug, int gameToDebug)
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      Game game = null;
      PGNReader in = null;
      List list = new LinkedList();

      if (debug && gameToDebug < 0) {
        Log.addMask(SAN.DEBUG);
        Log.addMask(PGNReader.DEBUG);
      }

      try {
	 int count = 0;

	    Log.debug(PGNReader.DEBUG, "Reading file: " + file.getName());
	    in = new PGNReader(
	   	    new FileReader(file));

	    //turn on single game debugging for next read
	    if (debug && gameToDebug == count) {
	       System.out.println("turing logs on");
	       Log.addMask(SAN.DEBUG);
	       Log.addMask(PGNReader.DEBUG);
	    }

	    while ((game = in.readGame()) != null) {
	       list.add(game);

               //turn off single game debugging
	       if (debug && gameToDebug == count) {
		  Log.removeMask(SAN.DEBUG);
		  Log.removeMask(PGNReader.DEBUG);
	       }

	       count++;

               //turn on single game debugging for next read
	       if (debug && gameToDebug == count) {
	          System.out.println("turing logs on");
		  Log.addMask(SAN.DEBUG);
		  Log.addMask(PGNReader.DEBUG);
	       }
	       game = null;
	    }
      }
      catch (Exception e) {
         throw e;
      }
      finally {
	 if (debug) {
	    Log.removeMask(SAN.DEBUG);
	    Log.removeMask(PGNReader.DEBUG);
	 }
      }
      return list;
   }
}

/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: SimplePGNDemo.java,v 1.3 2003/08/19 22:34:42 jvarsoke Exp $
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

import java.io.*;
import ictk.boardgame.*;
import ictk.boardgame.chess.*;
import ictk.boardgame.chess.io.*;

/** this program reads in a PGN from the command line, writes the PGN
 *  out to STDOUT, writes the final board's FEN, and calculates some
 *  statistics about the mainline of the game.
 */ 
public class SimplePGNDemo {

   public static void main (String[] args) {
      PGNReader reader = null;
      PGNWriter writer = null;
      ChessGame game = null;

      //check the command line args
      if (args.length != 1) {
         System.err.println("usage: java SimplePGNReader <pgn file>");
	 System.exit(1);
      }

      try {

         //establish the reader object
         reader = new PGNReader(new FileReader(new File(args[0])));

	 //read in the first game in the file
	 game = (ChessGame) reader.readGame();

	 displayPGN(game);
         displayLastPosition(game);
         displayStats(game);

      }
      catch (Exception e) {
         System.err.println(e);
      }
   }

   /** gather and display some use(less) statistics about the game */
   public static void displayStats (ChessGame game) {
      ChessMoveNotation san = new SAN();
      ChessAnnotation anno  = null;
      ChessMove move        = null;
      ContinuationList cont = null;
      History history       = game.getHistory();
      int count       = 0,
          comments    = 0, 
          precomments = 0, 
	  variations  = 0,
          goodmoves   = 0,
	  greatmoves  = 0, 
	  badmoves    = 0, 
	  blunders    = 0,
	  captures    = 0,
	  promotions  = 0,
	  checks      = 0,
	  pmoves_w    = 0,
	  pmoves_b    = 0,
	  nmoves_w    = 0,
	  nmoves_b    = 0,
	  bmoves_w    = 0,
	  bmoves_b    = 0,
	  rmoves_w    = 0,
	  rmoves_b    = 0,
	  qmoves_w    = 0,
	  qmoves_b    = 0,
	  kmoves_w    = 0,
	  kmoves_b    = 0;

      history.rewind();
      while (history.hasNext()) {
         move = (ChessMove) history.next();
	 count++;

	 if (move.isCheck()) checks++;
	 if (move.getCasualty() != null) captures++;

         switch (move.getChessPiece().getIndex() % ChessPiece.BLACK_OFFSET) {
	    case Pawn.INDEX:
	       if (move.isBlackMove()) pmoves_b++;
	       else pmoves_w++;
	       if (move.getPromotion() != null)
	          promotions++;
	       break;
	    case Knight.INDEX:
	       if (move.isBlackMove()) nmoves_b++;
	       else nmoves_w++;
	       break;
	    case Bishop.INDEX:
	       if (move.isBlackMove()) bmoves_b++;
	       else bmoves_w++;
	       break;
	    case Rook.INDEX:
	       if (move.isBlackMove()) rmoves_b++;
	       else rmoves_w++;
	       break;
	    case Queen.INDEX:
	       if (move.isBlackMove()) qmoves_b++;
	       else qmoves_w++;
	       break;
	    case King.INDEX:
	       if (move.isBlackMove()) kmoves_b++;
	       else kmoves_w++;
	       break;
	 }

	 anno = (ChessAnnotation) move.getPrenotation();
	 if (anno != null) {
	    if (anno.getComment() != null) precomments++;
	 }

	 anno = (ChessAnnotation) move.getAnnotation();
	 if (anno != null) {
	    if (anno.getComment() != null) comments++;
	    switch (anno.getSuffix()) {
	       case 1: goodmoves++; break;
	       case 2: badmoves++; break;
	       case 3: greatmoves++; break;
	       case 4: blunders++; break;
	    }
	 }

	 cont = move.getContinuationList();
	 if (cont.sizeOfVariations() > 0)
	    variations += cont.sizeOfVariations();
      }

      println("Game Statistics------------");
      println("for the main-line of the game");
      println("          plies: " + count);
      println("           pawn: w" + pmoves_w + "\tb" + pmoves_b);
      println("         knight: w" + nmoves_w + "\tb" + nmoves_b);
      println("         bishop: w" + bmoves_w + "\tb" + bmoves_b);
      println("           rook: w" + rmoves_w + "\tb" + rmoves_b);
      println("          queen: w" + qmoves_w + "\tb" + qmoves_b);
      println("           king: w" + kmoves_w + "\tb" + kmoves_b);
      println("         checks: " + checks);
      println("       captures: " + captures);
      println("     promotions: " + promotions);
      println("  good moves(!): " + goodmoves);
      println("great moves(!!): " + greatmoves);
      println("   bad moves(?): " + badmoves);
      println("   blunders(??): " + blunders);
      println("   pre-comments: " + precomments);
      println("       comments: " + comments);
      println("     variations: " + variations); //off mainline

      if (game.getResult() == null)
         println("         result: undecided");
      else
         println("         result: " + san.resultToString(game.getResult()));
      System.out.println();
   }

   /** utility function */
   public static void println (String str) {
      System.out.println(str);
   }

   /** displays the last board position in FEN notation
    */
   public static void displayLastPosition (ChessGame game) {
      ChessBoardNotation fen = new FEN();

         game.getHistory().goToEnd();
	 println("Last board position--------");
	 println("FEN: " + fen.boardToString(game.getBoard()));
	 System.out.println();
   }

   /** writes the game to STDOUT.  To modify the game presentation
    *  you can change the settings of the writer.
    */
   public static void displayPGN (ChessGame game) {
      PGNWriter writer = null;
      try {
         writer = new PGNWriter(System.out);

         //writer.setColumnWidth(60);
	 writer.setIndentVariations(true);
	 writer.setIndentComments(true);
	 //writer.setAnnotationGlyphStyle(PGNWriter.NUMERIC_GLYPH);
	 //writer.exportVariations(false);
	 //writer.exportComments(false);

	 println("PGN------------------------");
	 writer.writeGame(game);
	 System.out.println();
      }
      catch (IOException e) {
         System.err.println(e);
      }
   }
}

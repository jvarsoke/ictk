/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: FENTest.java,v 1.1.1.1 2003/03/24 22:38:14 jvarsoke Exp $
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

import java.io.IOException;

import junit.framework.*;
import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;
import ictk.boardgame.chess.*;

public class FENTest extends TestCase {
   FEN fen = null;
   SAN san = null;
   ChessMove move = null;
   ChessBoard board, board2;

   public FENTest (String name) {
      super(name);
   }

   public void setUp () {
      fen = new FEN();
      san = new SAN();
   }

   public void tearDown () {
      fen = null;
      san = null;
      board = null;
      board2 = null;
      move = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testReadRandom1 () 
          throws IOException {
      Log.addMask(FEN.DEBUG);
      char[][] position={{' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ','P',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {' ',' ',' ',' ',' ',' ',' ',' '},
                         {'K',' ',' ',' ',' ',' ',' ','k'}};

      board = new ChessBoard(position);
      board2 = (ChessBoard) fen.stringToBoard(
         "7k/4P3/8/8/8/8/8/7K w - - 0 1");

      assertTrue (board.isWhiteCastleableKingside()
                 == board2.isWhiteCastleableKingside());

      assertTrue (board.isBlackCastleableKingside()
                 == board2.isBlackCastleableKingside());

      assertTrue (board.isWhiteCastleableQueenside()
                 == board2.isWhiteCastleableQueenside());

      assertTrue (board.isBlackCastleableQueenside()
                 == board2.isBlackCastleableQueenside());

      assertTrue (board.getEnPassantFile()
                 == board2.getEnPassantFile());

      assertTrue (board.get50MoveRulePlyCount() 
                 == board2.get50MoveRulePlyCount());

      if (!board.equals(board2)) {
         Log.debug(FEN.DEBUG, "Boards not equal");
         Log.debug2(FEN.DEBUG, board.dump());
         Log.debug2(FEN.DEBUG, board2.dump());
      }

      assertTrue (board.equals(board2));
      assertTrue (board.toString().equals(board2.toString()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testReadDefault () 
          throws IOException {
      //Log.addMask(ChessBoard.DEBUG);

      board = new ChessBoard();
      board2 = (ChessBoard) fen.stringToBoard(
         "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
	 );
      assertTrue (board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testReadDefaultMove1e4 () 
          throws IOException,
	         IllegalMoveException,
		 AmbiguousMoveException,
		 OutOfTurnException {
      //Log.addMask(ChessBoard.DEBUG);

      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4");
      board.playMove(move);
      board2 = (ChessBoard) fen.stringToBoard(
         "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
	 );

      assertTrue (board.isWhiteCastleableKingside()
                 == board2.isWhiteCastleableKingside());

      assertTrue (board.isBlackCastleableKingside()
                 == board2.isBlackCastleableKingside());

      assertTrue (board.isWhiteCastleableQueenside()
                 == board2.isWhiteCastleableQueenside());

      assertTrue (board.isBlackCastleableQueenside()
                 == board2.isBlackCastleableQueenside());

      assertTrue (board.getEnPassantFile()
                 == board2.getEnPassantFile());

      assertTrue (board.get50MoveRulePlyCount() 
                 == board2.get50MoveRulePlyCount());

      if (!board.equals(board2)) {
         Log.debug(FEN.DEBUG, "Boards not equal");
         Log.debug2(FEN.DEBUG, board.dump());
         Log.debug2(FEN.DEBUG, board2.dump());
      }

      assertTrue (board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testReadDefaultMove1e4c5 () 
          throws IOException,
	         IllegalMoveException,
		 AmbiguousMoveException,
		 OutOfTurnException {
      //Log.addMask(ChessBoard.DEBUG);

      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4");
      board.playMove(move);
      move = (ChessMove) san.stringToMove(board, "c5");
      board.playMove(move);
      board2 = (ChessBoard) fen.stringToBoard(
	 "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2"
	 );

      assertTrue (board.isWhiteCastleableKingside()
                 == board2.isWhiteCastleableKingside());

      assertTrue (board.isBlackCastleableKingside()
                 == board2.isBlackCastleableKingside());

      assertTrue (board.isWhiteCastleableQueenside()
                 == board2.isWhiteCastleableQueenside());

      assertTrue (board.isBlackCastleableQueenside()
                 == board2.isBlackCastleableQueenside());

      assertTrue (board.getEnPassantFile()
                 == board2.getEnPassantFile());

      assertTrue (board.get50MoveRulePlyCount() 
                 == board2.get50MoveRulePlyCount());

      if (!board.equals(board2)) {
         Log.debug(FEN.DEBUG, "Boards not equal");
         Log.debug2(FEN.DEBUG, board.dump());
         Log.debug2(FEN.DEBUG, board2.dump());
      }

      assertTrue (board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testReadDefaultMove1e4c5Nf3 () 
          throws IOException,
	         IllegalMoveException,
		 AmbiguousMoveException,
		 OutOfTurnException {
      //Log.addMask(ChessBoard.DEBUG);

      board = new ChessBoard();
      move = (ChessMove) san.stringToMove(board, "e4");
      board.playMove(move);
      move = (ChessMove) san.stringToMove(board, "c5");
      board.playMove(move);
      move = (ChessMove) san.stringToMove(board, "Nf3");
      board.playMove(move);
      board2 = (ChessBoard) fen.stringToBoard(
         "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"
	 );

      assertTrue (board.isWhiteCastleableKingside()
                 == board2.isWhiteCastleableKingside());

      assertTrue (board.isBlackCastleableKingside()
                 == board2.isBlackCastleableKingside());

      assertTrue (board.isWhiteCastleableQueenside()
                 == board2.isWhiteCastleableQueenside());

      assertTrue (board.isBlackCastleableQueenside()
                 == board2.isBlackCastleableQueenside());

      assertTrue (board.getEnPassantFile()
                 == board2.getEnPassantFile());

      assertTrue (board.get50MoveRulePlyCount() 
                 == board2.get50MoveRulePlyCount());

      if (!board.equals(board2)) {
         Log.debug(FEN.DEBUG, "Boards not equal");
         Log.debug2(FEN.DEBUG, board.dump());
         Log.debug2(FEN.DEBUG, board2.dump());
      }

      assertTrue (board.equals(board2));
   }
}

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
import java.io.*;
import ictk.boardgame.*;
import ictk.boardgame.io.*;
import ictk.boardgame.chess.*;
import ictk.boardgame.chess.io.*;

/** Demonstrates how to enter the moves of a game with the ICTK API"
 */
public class SimpleChessGameExample {

   public static void main (String[] args) {
      ChessGame     game    = null;
      Board         board   = null;
      History       history = null;
      Move          move    = null,
                    e4      = null;
      MoveNotation  san     = new SAN();
      PGNWriter     writer  = null;
      ChessGameInfo gi      = null;
      ChessPlayer   player  = null;

      try {
         System.out.println("SimpleChessGameExample: "
	    + "demonstrates how to enter the moves of a game with the API");
	 System.out.println();

	 game = new ChessGame();

	 gi = new ChessGameInfo();
	 //white player
	 player = new ChessPlayer();
	 player.setFirstName("Bobby");
	 player.setLastName("Fischer");
	 gi.setWhite(player);

         //black player
	 player = new ChessPlayer();
	 player.setFirstName("Boris");
	 player.setLastName("Spasky");
	 gi.setBlack(player);

	 game.setGameInfo(gi);


         //setup the moves
         history = game.getHistory();
	 board = game.getBoard();

	 //1st way of adding a move to the move list
	 move = new ChessMove((ChessBoard) board, 5, 2, 5, 4); //1.e4
	 history.add(move);

	 //2nd way
	 move = (ChessMove) san.stringToMove(board, "e5");
	 history.add(move);

	 //3rd way
	 history.add(san.stringToMove(board, "Nc3"));

	 //to add a variation of Black's first move (e5 above)
	    //goto the move before "e5"
	 history.prev();
	 history.prev(); 
	 e4 = history.getCurrentMove();
	    //this time history.add() will add a variation
	 move = san.stringToMove(board, "c5");
	 move.setAnnotation(new ChessAnnotation("The Sicilian"));
	 history.add(move);

	 //the next add will continue down the variation
	 history.add(san.stringToMove(board, "Nc3"));

	 //to return to the mainline
	 history.rewindToLastFork();
	 if (history.getCurrentMove() != e4)
	    System.err.println("Ooops, did something wrong.");

	 //set the game's result
	 game.setResult(new ChessResult(ChessResult.DRAW));

	 writer = new PGNWriter(System.out);
	 writer.writeGame(game);

      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}

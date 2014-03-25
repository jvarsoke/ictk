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

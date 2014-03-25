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
import java.util.*;
import ictk.boardgame.*;
import ictk.boardgame.chess.*;
import ictk.boardgame.chess.io.*;

/** this program reads in a PGN from the command line and determines
 *  if a 3 fold repetition occured in the game.  If no 3x occured then
 *  there is no output.
 */ 
public class RepCheck {
   public static void main (String[] args) {
      ChessGame game = null;
      ChessBoard board = null;
      History history = null;
      Move[] tmp = null;
      PGNReader reader = null;
      SAN san = null;
      FEN fen =  null;
      String fenStr = null;
      Map<String,Move[]> hash = null;
      boolean found = false;

      //check the command line args
      if (args.length != 1) {
         System.err.println("usage: java RepCheck <pgn file>");
	 System.exit(1);
      }

      try {
         hash = new HashMap<>();
	 fen = new FEN();
         //establish the reader object
         reader = new PGNReader(new FileReader(new File(args[0])));

	 //read in the first game in the file
	 game = (ChessGame) reader.readGame();

	 history = game.getHistory();
	 board = (ChessBoard) game.getBoard();

         while (!found && history.hasNext()) {
	    history.next();
	    fenStr = fen.boardToString(board);
	    fenStr = fenStr.substring(0, fenStr.indexOf(' '));
	    if (!hash.containsKey(fenStr)) {
	       tmp = new Move[3];
	       tmp[0] = history.getCurrentMove();
	       hash.put(fenStr, tmp);
	    }
	    else {
	       tmp = hash.get(fenStr);
	       if (tmp[1] == null) {
	          tmp[1] = history.getCurrentMove();
	       }
	       else {
	          tmp[2] = history.getCurrentMove();
		  found = true;
	       }
	    }
	 }

	 if (found) {
	    san = new SAN();
	    System.out.print("3xRepeat: ");
	    for (int i=0; i< tmp.length; i++) {
	       history.goTo(tmp[i]);
	       System.out.print(board.getCurrentMoveNumber() + ".");
	       if (((ChessMove) tmp[i]).isBlackMove())
	          System.out.print("..");
	       System.out.print(san.moveToString(tmp[i]) + " ");
	    }
	    System.out.println(fenStr);
	 }
      }
      catch (Exception e) {
         System.err.println(e);
      }
   }
}

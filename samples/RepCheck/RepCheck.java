/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id:$
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

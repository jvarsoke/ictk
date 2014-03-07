/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: CLIPGNViewer.java,v 1.1 2003/08/18 03:38:42 jvarsoke Exp $
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
import ictk.boardgame.chess.ui.cli.*;

/** this program reads in a PGN from the command line. Then prompts
 *  you to walk through the PGN while CommandLineInteface (CLI) boards
 *  are displayed.
 */ 
public class CLIPGNViewer {
   public static ChessGame game;
   public static ChessMoveNotation san = new SAN();

   public static void main (String[] args) {
      PGNReader reader = null;
      CLIChessBoardDisplay display = null;

      //check the command line args
      if (args.length != 1) {
         System.err.println("usage: java CLIPGNViewer <pgn file>");
	 System.exit(1);
      }

      try {

         //establish the reader object
         reader = new PGNReader(new FileReader(new File(args[0])));

	 //read in the first game in the file
	 game = (ChessGame) reader.readGame();

         //setup the BoardDisplay
         display = new TxChessBoardDisplay((ChessBoard) game.getBoard());
	 //this tells the board to send update events to this display
	 game.getBoard().addBoardListener(display);

	 help();

         //this is the last time in the code you'll see mention of display
	 //it print itself out everytime the Board model changes.
	 //NOTE: this kind of event modeling is typical for GUI but 
	 //      not often seen in CLI aps.  It's really only to give you
	 //      a feel for how to do this.
         display.print();

         //the UI loop
	 do {
	    menu();
	    processChoice();
	 } while (true);

      }
      catch (Exception e) {
         System.err.println(e);
      }
   }

   /* help ******************************************************************/
   public static void help () {
         print("CLI PGN Viewer---------------");
	 print("   After each board is displayed you will be presented");
	 print("   with moves that continue from the position. Usually");
	 print("   you will only see a main-line.  But sometimes the");
	 print("   game branches in variation lines.");
	 print("");
	 print("   >  follows the main line.");
	 print("   <  to go back one move");
	 print("   #  the number indicates which line to follow.");
	 print("   f  returns to the move before the current variation.");
	 print("   << rewind to the beginning of the game.");
	 print("   >> go to the end of the current line.");
	 print("   q  to quit");
	 print("   ?  to see this help file again.");
	 print("");
   }

   /* menu ******************************************************************/
   /** shows the variations you can chose from, or the result.
    */
   public static void menu () {
      ContinuationList lines = null;
      Move move = null;

         move = game.getHistory().getCurrentMove();
	 if (move == null)
	    lines = game.getHistory().getFirstAll();
	 else
	    lines = move.getContinuationList();

         if (lines.isTerminal()) {
	    System.out.println();
	    System.out.print("You've reached the end of this line: ");
	    if (move != null) {
	       ChessResult result = (ChessResult) move.getResult();
	       if (result == null)
	          print("undecided");
	       else
	          print(san.resultToString(result));
	    }
	    else {
	       print("undecided");
	    }
	    System.out.println();
	 }
	 else {
	    System.out.println();
            print("Lines:");
	    for (int i=0; i < lines.size(); i++) {
	       System.out.print("   [");
	       if (i==0)
	          System.out.print(">");
	       else
	          System.out.print(i);
	       System.out.print("] ");

	       print(san.moveToString(lines.get(i)));
	    }
	    System.out.println();
	 }
   }

   /* processChoice *********************************************************/
   /** processes user input.  Minimal error checking is done -- hey, it's only
    *  a sample app.
    */
   public static void processChoice () {
      boolean goodChoice = true;
      String choice = "";
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

         do {
	    System.out.print("Choose (>,<,#,f,<<,>>,?,q): ");
	    goodChoice = true;

	    try {
	       choice = in.readLine().trim();
	       System.out.println();
	    }
	    catch (IOException e) {
	       System.err.println(e);
	    }

	    if (">".equals(choice))
	       game.getHistory().next();

	    else if ("<".equals(choice))
	       game.getHistory().prev();

	    else if ("f".equals(choice))
	       game.getHistory().rewindToLastFork();

	    else if ("<<".equals(choice))
	       game.getHistory().rewind();

	    else if (">>".equals(choice))
	       game.getHistory().goToEnd();

	    else if ("?".equals(choice))
	       help();

	    else if ("q".equals(choice))
	       System.exit(1);

	    else if (Character.isDigit(choice.charAt(0))) 
	       try {
	          game.getHistory().next(Integer.parseInt(choice));
	       }
	       catch (Exception e) {
	          System.out.println("Error: don't understand that number");
	          goodChoice = false;
	       }

	    else {
	       System.out.println("Error: don't understand that option.");
	       goodChoice = false;
	    }

	 } while (!goodChoice);
   }

   /* print *****************************************************************/
   public static void print (String s) {
      System.out.println(s);
   }
}

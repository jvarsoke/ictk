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

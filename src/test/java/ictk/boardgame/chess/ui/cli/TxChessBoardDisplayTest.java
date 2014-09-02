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

package ictk.boardgame.chess.ui.cli;

import junit.framework.*;

import java.io.*;
import java.util.*;

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;
import ictk.boardgame.chess.*;

public class TxChessBoardDisplayTest extends TestCase {
   TxChessBoardDisplay display;
   ChessBoard          board;
   ChessMove           move;
   StringWriter        swriter;
   String              actual,
                       expected;

   public TxChessBoardDisplayTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
      display = new TxChessBoardDisplay(board, swriter = new StringWriter());
   }

   public void tearDown () {
      board = null;
      move = null;
      swriter = null;
      actual = null;
      expected = null;
      display = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testDefaultInitialPosition () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "8   r n b q k b n r\n"
	      + "7   p p p p p p p p\n"
	      + "6   #   #   #   #  \n"
	      + "5     #   #   #   #\n"
	      + "4   #   #   #   #  \n"
	      + "3     #   #   #   #\n"
	      + "2   P P P P P P P P\n"
	      + "1   R N B Q K B N R\n"
	      + "\n"
	      + "    A B C D E F G H\n"
	      ;


	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testDefaultInitialPositionFlipped () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "1   R N B K Q B N R\n"
	      + "2   P P P P P P P P\n"
	      + "3   #   #   #   #  \n"
	      + "4     #   #   #   #\n"
	      + "5   #   #   #   #  \n"
	      + "6     #   #   #   #\n"
	      + "7   p p p p p p p p\n"
	      + "8   r n b k q b n r\n"
	      + "\n"
	      + "    H G F E D C B A\n"
	      ;


         display.setWhiteOnBottom(false);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testNoCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "r n b q k b n r\n"
	      + "p p p p p p p p\n"
	      + "#   #   #   #  \n"
	      + "  #   #   #   #\n"
	      + "#   #   #   #  \n"
	      + "  #   #   #   #\n"
	      + "P P P P P P P P\n"
	      + "R N B Q K B N R\n"
	      ;

         display.setVisibleCoordinates(display.NO_COORDINATES);
	 display.print();
	 actual = swriter.toString();
	 assertTrue (actual.equals(expected));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testTopCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "A B C D E F G H\n"
	      + "\n"
	      + "r n b q k b n r\n"
	      + "p p p p p p p p\n"
	      + "#   #   #   #  \n"
	      + "  #   #   #   #\n"
	      + "#   #   #   #  \n"
	      + "  #   #   #   #\n"
	      + "P P P P P P P P\n"
	      + "R N B Q K B N R\n"
	      ;

         display.setVisibleCoordinates(display.TOP_COORDINATES);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testRightCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "r n b q k b n r  8\n"
	      + "p p p p p p p p  7\n"
	      + "#   #   #   #    6\n"
	      + "  #   #   #   #  5\n"
	      + "#   #   #   #    4\n"
	      + "  #   #   #   #  3\n"
	      + "P P P P P P P P  2\n"
	      + "R N B Q K B N R  1\n"
	      ;

         display.setVisibleCoordinates(display.RIGHT_COORDINATES);
	 display.print();
	 actual = swriter.toString();
	 assertTrue (actual.equals(expected));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testBottomCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "r n b q k b n r\n"
	      + "p p p p p p p p\n"
	      + "#   #   #   #  \n"
	      + "  #   #   #   #\n"
	      + "#   #   #   #  \n"
	      + "  #   #   #   #\n"
	      + "P P P P P P P P\n"
	      + "R N B Q K B N R\n"
	      + "\n"
	      + "A B C D E F G H\n"
	      ;

         display.setVisibleCoordinates(display.BOTTOM_COORDINATES);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testLeftCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "8  r n b q k b n r\n"
	      + "7  p p p p p p p p\n"
	      + "6  #   #   #   #  \n"
	      + "5    #   #   #   #\n"
	      + "4  #   #   #   #  \n"
	      + "3    #   #   #   #\n"
	      + "2  P P P P P P P P\n"
	      + "1  R N B Q K B N R\n"
	      ;

         display.setVisibleCoordinates(display.LEFT_COORDINATES);
	 display.print();
	 actual = swriter.toString();
	 assertTrue (actual.equals(expected));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testAllCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected =
	        "    A B C D E F G H\n"
	      + "\n"
	      + "8   r n b q k b n r   8\n"
	      + "7   p p p p p p p p   7\n"
	      + "6   #   #   #   #     6\n"
	      + "5     #   #   #   #   5\n"
	      + "4   #   #   #   #     4\n"
	      + "3     #   #   #   #   3\n"
	      + "2   P P P P P P P P   2\n"
	      + "1   R N B Q K B N R   1\n"
	      + "\n"
	      + "    A B C D E F G H\n"
	      ;


         display.setVisibleCoordinates(display.TOP_COORDINATES 
	                               | display.RIGHT_COORDINATES
				       | display.BOTTOM_COORDINATES
				       | display.LEFT_COORDINATES);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testCompact () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "8 rnbqkbnr\n"
	      + "7 pppppppp\n"
	      + "6 # # # # \n"
	      + "5  # # # #\n"
	      + "4 # # # # \n"
	      + "3  # # # #\n"
	      + "2 PPPPPPPP\n"
	      + "1 RNBQKBNR\n"
	      + "\n"
	      + "  ABCDEFGH\n"
	      ;

         display.setCompact(true);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }
   //////////////////////////////////////////////////////////////////////
   public void testCompactAllCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected =
	        "  ABCDEFGH\n"
	      + "\n"
	      + "8 rnbqkbnr 8\n"
	      + "7 pppppppp 7\n"
	      + "6 # # # #  6\n"
	      + "5  # # # # 5\n"
	      + "4 # # # #  4\n"
	      + "3  # # # # 3\n"
	      + "2 PPPPPPPP 2\n"
	      + "1 RNBQKBNR 1\n"
	      + "\n"
	      + "  ABCDEFGH\n"
	      ;

         display.setVisibleCoordinates(display.TOP_COORDINATES 
	                               | display.RIGHT_COORDINATES
				       | display.BOTTOM_COORDINATES
				       | display.LEFT_COORDINATES);
         display.setCompact(true);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testLowerCaseCoords () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "8   r n b q k b n r\n"
	      + "7   p p p p p p p p\n"
	      + "6   #   #   #   #  \n"
	      + "5     #   #   #   #\n"
	      + "4   #   #   #   #  \n"
	      + "3     #   #   #   #\n"
	      + "2   P P P P P P P P\n"
	      + "1   R N B Q K B N R\n"
	      + "\n"
	      + "    a b c d e f g h\n"
	      ;


         display.setLowerCaseCoordinates(true);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testInverse () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 expected = "8   r n b q k b n r\n"
	      + "7   p p p p p p p p\n"
	      + "6     #   #   #   #\n"
	      + "5   #   #   #   #  \n"
	      + "4     #   #   #   #\n"
	      + "3   #   #   #   #  \n"
	      + "2   P P P P P P P P\n"
	      + "1   R N B Q K B N R\n"
	      + "\n"
	      + "    A B C D E F G H\n"
	      ;


         display.setInverse(true);
	 display.print();
	 actual = swriter.toString();
	 assertEquals(expected, actual);
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }
}

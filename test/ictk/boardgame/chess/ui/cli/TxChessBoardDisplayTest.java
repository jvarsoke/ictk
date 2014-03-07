/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: TxChessBoardDisplayTest.java,v 1.2 2003/08/14 01:17:03 jvarsoke Exp $
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
   String              str,
                       str2;

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
      str = null;
      str2 = null;
      display = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testDefaultInitialPosition () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "8   r n b q k b n r\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testDefaultInitialPositionFlipped () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "1   R N B K Q B N R\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testNoCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "r n b q k b n r\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testTopCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "A B C D E F G H\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testRightCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "r n b q k b n r  8\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testBottomCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "r n b q k b n r\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testLeftCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "8  r n b q k b n r\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testAllCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 =
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testCompact () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "8 rnbqkbnr\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }
   //////////////////////////////////////////////////////////////////////
   public void testCompactAllCoordinates () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 =
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testLowerCaseCoords () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "8   r n b q k b n r\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testInverse () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "8   r n b q k b n r\n"
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
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }
}

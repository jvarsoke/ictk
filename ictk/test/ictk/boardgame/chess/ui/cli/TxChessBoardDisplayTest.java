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
   ChessBoard          board, 
                       board2;
   ChessMove           move;
   //PrintWriter         out;
   StringWriter        swriter;
   String              str,
                       str2;

   char[][] default_position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

/*
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P','N',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
*/


   public TxChessBoardDisplayTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
      display = new TxChessBoardDisplay(board, swriter = new StringWriter());
   }

   public void tearDown () {
      board = null;
      board2 = null;
      move = null;
      swriter = null;
      str = null;
      str2 = null;
      display = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testSetPositionDataReset () {
      //Log.addMask(ChessBoard.DEBUG);
      try {
	 str2 = "8   r n b q k b n r \n"
	      + "7   p p p p p p p p \n"
	      + "6   #   #   #   #   \n"
	      + "5     #   #   #   # \n"
	      + "4   #   #   #   #   \n"
	      + "3     #   #   #   # \n"
	      + "2   P P P P P P P P \n"
	      + "1   R N B Q K B N R \n"
	      + "\n"
	      + "    A B C D E F G H \n"
	      ;


	 display.print();
	 str = swriter.toString();
	 assertTrue (str.equals(str2));
      }
      finally {
         Log.removeMask(ChessBoard.DEBUG);
      }
   }
}

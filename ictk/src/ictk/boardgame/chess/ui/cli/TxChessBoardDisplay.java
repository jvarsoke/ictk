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

import ictk.boardgame.Board;
import ictk.boardgame.BoardListener;
import ictk.boardgame.chess.ChessBoard;
import ictk.boardgame.chess.ChessPiece;
import ictk.boardgame.chess.Square;
import ictk.boardgame.chess.ui.ChessBoardDisplay;
import ictk.boardgame.chess.io.ChessMoveNotation;
import ictk.boardgame.chess.io.SAN;

import java.io.*;


/* TxChessBoardDisplay ******************************************************/
/** This is a TxChess style command-line visual representation of a board.
 *  TxChess is an old C program used for email chess.
 */
public class TxChessBoardDisplay implements CLIChessBoardDisplay,
                                            BoardListener {
   protected PrintWriter out = new PrintWriter(System.out, true);
   protected ChessBoard board;

   protected ChessMoveNotation notation = new SAN();

   public TxChessBoardDisplay (ChessBoard board, OutputStream out) {
      this(board, new PrintWriter(out));
   }

   public TxChessBoardDisplay (ChessBoard board, Writer out) {
      this(board, new PrintWriter(out, true));
   }

   public TxChessBoardDisplay (ChessBoard board, PrintWriter out) {
      this.board = board;
      this.out = out;
   }

   public TxChessBoardDisplay (ChessBoard board) {
      this.board = board;
   }

   //BoardListenerd Interface/////////////////////////////////////////////////
   public void boardUpdate (Board b, int code) {
      update();
   }

   //BoardDisplay Interface///////////////////////////////////////////////////
   public void setBoard (Board board) {
      this.board = (ChessBoard) board;
   }

   public Board getBoard () { return board; }

   public void update () {
      print();
   }

   //CLIBoardDisplay Interface////////////////////////////////////////////////
   public void setWriter (PrintWriter out) {
      this.out = out;
   }

   public Writer getWriter () {
      return out;
   }

   public void print () {
      print(board);
   }

   public void print (Board board) {
      ChessBoard b = (ChessBoard) board;
      Square sq = null;
      StringBuffer last_line = new StringBuffer(20);
      char c = ' ';
      int r, f;

         last_line.append("\n    ");
         for (r=b.MAX_RANK, f=1; r >= 1; r--,f=1) {
	    sq = b.getSquare(f, r);
            out.print(notation.rankToChar(sq.getRank()));
	    out.print("   ");


            for (f=1; f <= b.MAX_FILE; f++) {
	       sq = b.getSquare(f, r);
               if (sq.isOccupied()) {
                   c = notation.pieceToChar((ChessPiece) sq.getPiece());
                   if (((ChessPiece)sq.getPiece()).isBlack())
                      c = Character.toLowerCase(c);
                   out.print(c);
		   out.print(" ");
               }
               else
                  if (sq.isBlack())
                     out.print("  ");
                  else
                     out.print("# ");
               if (r==b.MAX_RANK)
                  last_line.append(Character.toUpperCase(
                     notation.fileToChar(sq.getFile()))) 
		     .append(" ");
            }
            out.println();

         }
         out.println(last_line.toString());
   }
}

/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Square.java,v 1.1.1.1 2003/03/24 22:38:07 jvarsoke Exp $
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

package ictk.boardgame.chess;

import ictk.boardgame.Location;
import ictk.boardgame.Piece;

/* Square *******************************************************************/
/** this is a typical location on a chess board.  It contains an occupant,
 *  file, and a rank.
 */
public class Square implements Location, Cloneable {
     /** the piece standing on the square */
   public  ChessPiece piece;
     /** the horizontal component of the chess board (a-h) legal values: 1-8 */
   byte file;
     /** the vertical component of the chess board (1-8) legal values: 1-8 */
   byte rank;
 
   public Square () {}

   public Square (byte f, byte r)  {
      file = f;
      rank = r;
   }

   public Square (char f, char r) { 
      file = ChessBoard.san.fileToNum(f);
      rank = ChessBoard.san.rankToNum(r);
   }

   //Location interface/////////////////////////////////////////////////
   public int getX () { return file; }
   public int getY () { return rank; }

   //FIXME: possibly should be some bounds checking here
   public void setX (int x) { file = (byte) x; }
   public void setY (int y) { rank = (byte) y; }

   /* getPiece **************************************************************/
   /** gets the current occupant of the square.
    *  @return null if there is no occupant
    */
   public Piece getPiece () { return piece; }


   /* setPiece **************************************************************/
   /** sets the occupant of the square
    * @return the piece that occupied this square before the method call
    */
   public Piece setPiece (Piece p) {
      ChessPiece old_p = piece;
      piece = (ChessPiece) p;

      return old_p;
   }

   /* isBlack ***************************************************************/
   /** what color is this square on a traditional chess board?
    */
   public boolean isBlack () {
      return (file % 2 == rank % 2); 
   }

   /* getFile ***************************************************************/
   /**@return 1-8 */
   public byte getFile () { return file;}

   /* getRank ***************************************************************/
   /**@return 1-8 */
   public byte getRank () { return rank;}

   /* getFileAsChar *********************************************************/
   /** translates the file into its traditional english letter value
    *  ('a'-'h')
    */
   public char getFileAsChar () {  return ChessBoard.san.fileToChar(file); }

   /* getRankAsChar *********************************************************/
   /** translates the rank into its traditional Arabic number value
    *  ('1'-'8')
    */
   public char getRankAsChar () {  return ChessBoard.san.rankToChar(rank); }

   //Occupation/////////////////////////////////////////////////////////
   /* isOccupied ************************************************************/
   /** is there a piece standing on this square.
    */
   public boolean isOccupied () {
      return (piece != null);
   }

   /* setOccupant ***********************************************************/
   /** this is the same as setPiece, but specific for the Chess package.
    */
   public boolean setOccupant (ChessPiece p) {
      boolean wasOccupied = isOccupied();

      piece = p;
      piece.orig = this;
	 return wasOccupied;
   }

   /* getOccupant ***********************************************************/
   /** same as getPiece() but returns the piece in the ChessPiece class
    */
   public ChessPiece getOccupant() {
      return piece;
   }

   public void set (byte f, byte r) {
      file = f;
      rank = r;
   }

   public void set (char f, char r) {
      file = ChessBoard.san.fileToNum(f);
      rank = ChessBoard.san.rankToNum(r);
   }

   /* getCoordinatesNumeric ************************************************/
   /** this returns an int array of the numberic representation of the file
    *  and the rank.
    */
   public int[] getCoordinatesNumeric () {
      int[] coords = new int[2];

	 coords[0] = file;
	 coords[1] = rank;

      return coords;
   }
   
   /* equals ***************************************************************/
   /** checks to see of the file and rank are the same.  The occupant doesn't
    *  matter.
    */
   public boolean equals (Object obj) {
      if (this == obj) return true;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;

      Square s = (Square) obj;
      return (file == s.file && rank == s.rank);
   }

   /* hashCode *************************************************************/
   public int hashCode () {
      int hash = 5;

      hash = 31 * hash + file;
      hash = 31 * hash + 10 * rank;

      return hash;
   }

   public String toString () {
      return "" + getFileAsChar() + getRankAsChar();
   }

   public String dump () {
      StringBuffer sb = new StringBuffer();

         sb.append("file=").append(file)
	   .append(" rank=").append(rank)
	   .append(" cfile=").append(getFileAsChar())
	   .append(" crank=").append(getRankAsChar())
	   .append(" piece=").append(piece);
      return sb.toString();
   }
}

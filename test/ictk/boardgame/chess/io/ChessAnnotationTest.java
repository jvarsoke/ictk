/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessAnnotationTest.java,v 1.2 2003/07/26 07:13:08 jvarsoke Exp $
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

package ictk.boardgame.chess.io;

import junit.framework.*;
import ictk.util.Log;

import java.io.*;

public class ChessAnnotationTest extends TestCase {
   ChessAnnotation anno;

   public ChessAnnotationTest (String name) {
      super(name);
   }

   public void setUp () {
      anno = new ChessAnnotation();
   }

   public void tearDown () {
      anno = null;
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGAdd () {
      anno.addNAG((short) 1);
      assertTrue(anno.getSuffix() == 1);
      assertTrue(anno.getNAG(0) == 1);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGAdd2 () {
      anno.addNAG((short) 123);
      assertTrue(anno.getSuffix() == 0);
      assertTrue(anno.getNAG(0) == 123);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGAdd3 () {
      anno.addNAG((short) 123);
      anno.addNAG((short) 2);
      assertTrue(anno.getNAGs().length == 2);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGRemove () {
      anno.addNAG((short) 123);
      anno.addNAG((short) 2);
      anno.addNAG((short) 8);
      assertTrue(anno.getNAGs().length == 3);
      anno.removeNAG(1);
      assertTrue(anno.getNAGs().length == 2);
      assertTrue(anno.getNAGs()[0] == 123);
      assertTrue(anno.getNAGs()[1] == 8);
      anno.removeNAG(1);
      assertTrue(anno.getNAGs().length == 1);
      assertTrue(anno.getNAGs()[0] == 123);
      anno.removeNAG(0);
      assertTrue(anno.getNAGs() == null);

   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGRemove2 () {
      anno.addNAG((short) 123);
      anno.addNAG((short) 2);
      anno.addNAG((short) 8);
      anno.addNAG((short) 55);
      assertTrue(anno.getNAGs().length == 4);
      anno.removeNAG(0);
      assertTrue(anno.getNAGs().length == 3);
      assertTrue(anno.getNAGs()[0] == 2);
      assertTrue(anno.getNAGs()[1] == 8);
      assertTrue(anno.getNAGs()[2] == 55);
      anno.removeNAG(2);
      assertTrue(anno.getNAGs().length == 2);
      assertTrue(anno.getNAGs()[0] == 2);
      assertTrue(anno.getNAGs()[1] == 8);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testEquality () {
      ChessAnnotation anno2 = new ChessAnnotation();

      anno.addNAG((short) 123);
      anno.addNAG((short) 2);
      anno2.addNAG((short) 123);
      anno2.addNAG((short) 2);
      anno.setComment("best by test");
      anno2.setComment("best by test");

      assertTrue(anno.equals(anno2));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNotEquality () {
      ChessAnnotation anno2 = new ChessAnnotation();

      anno.addNAG((short) 123);
      anno.addNAG((short) 2);
      anno2.addNAG((short) 123);
      anno2.addNAG((short) 3);
      anno.setComment("best by test");
      anno2.setComment("best by test");

      assertFalse(anno.equals(anno2));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testNAGStoString () {
      anno.addNAG((short) 123);
      anno.addNAG((short) 2);
      assertTrue(anno.getNAGs().length == 2);

      assertTrue(anno.getNAGString().equals("$123 ?"));
   }

}

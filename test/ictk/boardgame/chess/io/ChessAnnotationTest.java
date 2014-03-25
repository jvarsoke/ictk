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

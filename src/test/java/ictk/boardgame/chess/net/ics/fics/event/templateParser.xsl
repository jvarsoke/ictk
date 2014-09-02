<?xml version="1.0"?>
<!--
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
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:redirect="http://xml.apache.org/xalan/redirect"
                xmlns:java="java"
                extension-element-prefixes="redirect"
                >

<xsl:output method="text"
            omit-xml-declaration="yes"/>

<xsl:template match="unitTest">
   <xsl:apply-templates select="unit"/>
</xsl:template>

<!-- necessary because XSLTC redirect:write does not respect Ant:destdir -->
<xsl:param name="destpath"/>

<!-- main event -->
<xsl:template match="unit">
   <xsl:variable name="parserclassname" select="concat('FICS',
                                          @class,
                                          'Parser')"
                                          />
   <xsl:variable name="classname"  select="concat($parserclassname,
                                          'Test')"
                                          />
   <xsl:variable name="rel-filename" select="concat($classname,
                                                '.java')"
                                                 />
   <xsl:variable name="filename" select="concat($destpath,
						'/',
						$rel-filename)"
/>


   <!-- show the filename so we can capture in a log and delete later -->
   <xsl:value-of select="$rel-filename"/><xsl:text>
</xsl:text>

   <redirect:write file="{$filename}">/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke &lt;ictk.jvarsoke [at] neverbox.com&gt;
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

package ictk.boardgame.chess.net.ics.fics.event;

/*--------------------------------------------------------------------------*
 * This file was auto-generated
 * by $Id$
 * on <xsl:value-of select="java:util.Date.new()"/>
 *--------------------------------------------------------------------------*/

import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;

import junit.framework.*;

public class <xsl:value-of select="$classname"/>  extends ParserTest {
   ICS<xsl:value-of select="@event"/>Event evt;

   public <xsl:value-of select="$classname"/> () throws IOException {
      super("ictk.boardgame.chess.net.ics.fics.event");
   }

   public void setUp () {
      parser = <xsl:value-of select="$parserclassname"/>.getInstance();
      //debug = true;
   }

   public void tearDown () {
      evt = null;
      parser = null;
   }
<xsl:apply-templates select="test"/>
   //inherited///////////////////////////////////////////////////////////
   public void testParseAll () {
      //debug=true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {
         super.testParseAll();
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }

   //////////////////////////////////////////////////////////////////////
   public void testNative () {
      //debug=true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {
         super.testNative();
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }
}
</redirect:write>
</xsl:template>

<xsl:template match="test">
   //////////////////////////////////////////////////////////////////////
   public void test<xsl:value-of select="@name"/> () {
      //debug = true;
      if (debug) {
         Log.addMask(ICSEventParser.DEBUG);
         parser.setDebug(true);
      }
      try {

         evt = (ICS<xsl:value-of select="../@event"/>Event) <!--
	 -->parser.createICSEvent(mesg[<xsl:value-of select="@iochunk"/>]);
	 assertTrue(evt != null);

	 //begin test 
<xsl:apply-templates select="code" mode="test"/>
	 //end test
      }
      finally {
         Log.removeMask(ICSEventParser.DEBUG);
	 debug = false;
      }
   }
</xsl:template>

<xsl:template match="code" mode="test">
   <xsl:if test="@format='java'">
      <xsl:value-of select="."/>
   </xsl:if>
</xsl:template>

</xsl:stylesheet>


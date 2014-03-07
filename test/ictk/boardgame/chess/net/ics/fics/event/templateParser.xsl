<?xml version="1.0"?>
<!--
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: templateParser.xsl,v 1.2 2004/01/30 08:50:38 jvarsoke Exp $
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

<!-- main event -->
<xsl:template match="unit">
   <xsl:variable name="parserclassname" select="concat('FICS',
                                          @class,
                                          'Parser')"
                                          />
   <xsl:variable name="classname"  select="concat($parserclassname,
                                          'Test')"
                                          />
   <xsl:variable name="filename" select="concat($classname,
                                                '.java')"
                                                 />

   <!-- show the filename so we can capture in a log and delete later -->
   <xsl:value-of select="$filename"/><xsl:text>
</xsl:text>

   <redirect:write select="$filename">/*
/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2003 J. Varsoke &lt;jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
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

package ictk.boardgame.chess.net.ics.fics.event;

/*--------------------------------------------------------------------------*
 * This file was auto-generated
 * by $Id: templateParser.xsl,v 1.2 2004/01/30 08:50:38 jvarsoke Exp $
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


<?xml version="1.0"?>
<!--
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
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:redirect="http://xml.apache.org/xalan/redirect"
                xmlns:str="http://ictk.sourceforge.net/namespace/string"
                xmlns:text="http://ictk.sourceforge.net/namespace/text"
		xmlns:java="java"
		extension-element-prefixes="redirect"
		>

<xsl:import href="string.xsl"/>
<xsl:import href="text.xsl"/>
<xsl:import href="parser.xsl"/>

<xsl:output method="text" 
            omit-xml-declaration="yes"/>

<xsl:template match="icsevtml">
   <xsl:apply-templates select="event"/>
</xsl:template>

<!-- main event -->
<xsl:template match="event">
   <xsl:variable name="classname" select="concat('ICS',
                                                 @class,
						 'Event')"
						 />
   <xsl:variable name="filename" select="concat($classname,
                                                '.java')"
						 />
						  
   <!-- show the filename so we can capture in a log and delete later -->
   <xsl:value-of select="$filename"/><xsl:text>
</xsl:text>

   <redirect:write select="$filename">/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2003 J. Varsoke &lt;jvarsoke@ghostmanonfirst.com&gt;
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

package ictk.boardgame.chess.net.ics.event;

/*--------------------------------------------------------------------------*
 * This file was auto-generated 
 * by $Id$
 * on <xsl:value-of select="java:util.Date.new()"/>
 *--------------------------------------------------------------------------*/

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.fics.event.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;

/**
<xsl:apply-templates select="description" mode="text:comment-wrap">
   <xsl:with-param name="width" select="70"/>
   <xsl:with-param name="style" select="'C'"/>
   <xsl:with-param name="indent" select="1"/>
</xsl:apply-templates>
 */
public class ICS<xsl:apply-templates select="@class"/>Event extends <xsl:apply-templates select="@extends"/>
   <xsl:call-template name="implements-declaration"/> {


   //static initializer/////////////////////////////////////////////////////
   protected static final int <xsl:value-of select="@enum"/>_EVENT =  ICSEvent.<xsl:value-of select="@enum"/>_EVENT;

   <xsl:apply-templates select="statics"/>

   //instance vars//////////////////////////////////////////////////////////
<xsl:apply-templates select="member" mode="variable-declaration"/>

   //constructors///////////////////////////////////////////////////////////
   public ICS<xsl:value-of select="@class"/>Event () {
      super(<xsl:value-of select="@enum"/>_EVENT);
   }

   //assessors/////////////////////////////////////////////////////////////
<xsl:apply-templates select="member" mode="getter"/>
<xsl:text>   //mutators//////////////////////////////////////////////////////////////
</xsl:text>
<xsl:apply-templates select="member" mode="setter"/>
<xsl:apply-templates select="implements" mode="method"/>
   //readable//////////////////////////////////////////////////////////////
   public String getReadable () {
      String str = null;
         switch (getEventType()) {
	    <xsl:apply-templates select="parser" mode="readableSwitch"/>
	 }
      return str;
   }
}
</redirect:write>
   <xsl:apply-templates select="parser"/>
</xsl:template>

<!-- implements declaration -->
<xsl:template name="implements-declaration">
    <xsl:if test="child::implements">
                            implements <xsl:for-each select="implements">
			    <xsl:value-of select="id(@idref)/@type"/>
			    <xsl:if test="following-sibling::implements">,
			               </xsl:if>
    </xsl:for-each>
    </xsl:if>
</xsl:template>

<!-- implements methods -->
<xsl:template match="implements" mode="method">

   <xsl:text>   //</xsl:text>
   <xsl:value-of select="id(@idref)/@type"/>
   <xsl:text>////////////////////////////////////////////////////</xsl:text>

   <xsl:for-each select="id(@idref)/method">
      <xsl:apply-templates select="code"/>
   </xsl:for-each>

</xsl:template>

<xsl:template match="statics">
   <xsl:if test="@format='java'">
      <xsl:value-of select="."/>
   </xsl:if>
</xsl:template>

<xsl:template match="code">
   <xsl:if test="@format='java'">
      <xsl:value-of select="."/>
   </xsl:if>
</xsl:template>

<!-- member variable-declaration -->
<xsl:template match="member" mode="variable-declaration">
   <xsl:variable name="varname">
      <xsl:choose>
	 <xsl:when test="@varname">
	    <xsl:value-of select="@varname"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="id(@typeref)/@varname"/>
	 </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:if test="not(@inherit='member') and not(@inherit='both')">
      <xsl:text>   protected </xsl:text>
      <xsl:value-of select="id(@typeref)/@type"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="$varname"/>;
</xsl:if>

</xsl:template>


<!-- getter functions -->
<xsl:template match="member" mode="getter">
   <xsl:variable name="varname">
      <xsl:choose>
	 <xsl:when test="@varname">
	    <xsl:value-of select="@varname"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="id(@typeref)/@varname"/>
	 </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:variable name="functname">
      <xsl:choose>
	 <xsl:when test="@functname">
	    <xsl:value-of select="@functname"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="id(@typeref)/@functname"/>
	 </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:variable name="type" select="id(@typeref)/@type"/>

   <xsl:if test="not(@inherit='method') and not(@inherit='both')">
   <xsl:text>   public </xsl:text>
   <xsl:value-of select="$type"/><xsl:text> </xsl:text>
   <xsl:choose>
      <xsl:when test="@exactfunctname='yes'"></xsl:when>
      <xsl:when test="$type='boolean'">is</xsl:when>
      <xsl:otherwise>get</xsl:otherwise>
   </xsl:choose>
   <xsl:value-of select="$functname"/>
   <xsl:text> () {
      return </xsl:text><xsl:value-of select="$varname"/>;
   }

</xsl:if>
</xsl:template>

<!-- setter functions -->
<xsl:template match="member" mode="setter">
   <xsl:variable name="varname">
      <xsl:choose>
	 <xsl:when test="@varname">
	    <xsl:value-of select="@varname"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="id(@typeref)/@varname"/>
	 </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:variable name="functname">
      <xsl:choose>
	 <xsl:when test="@functname">
	    <xsl:value-of select="@functname"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="id(@typeref)/@functname"/>
	 </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:variable name="type" select="id(@typeref)/@type"/>

   <xsl:if test="not(@inherit='method') and not(@inherit='both')">
   <xsl:text>   public void </xsl:text>
   <xsl:choose>
      <xsl:when test="@exactfunctname='yes'"></xsl:when>
      <xsl:otherwise>set</xsl:otherwise>
   </xsl:choose>
   <xsl:value-of select="$functname"/>
   <xsl:text> (</xsl:text>
   <xsl:value-of select="$type"/>
   <xsl:text> </xsl:text>
   <xsl:value-of select="$varname"/>) {
      this.<xsl:value-of select="$varname"/> = <xsl:value-of select="$varname"/>;
   }

</xsl:if>
</xsl:template>

<!-- readableSwitch -->
<xsl:template match="parser" mode="readableSwitch">
   <xsl:variable name="enum">
      <xsl:choose>
	 <xsl:when test="@enum">
	    <xsl:value-of select="@enum"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="../@enum"/>
	 </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
         case ICSEvent.<xsl:value-of select="$enum"/>_EVENT:
            str = <xsl:value-of select="concat(@protocol,@name,'Parser')"
	       />.getInstance().toNative(this);
	    break;
</xsl:template>
</xsl:stylesheet>

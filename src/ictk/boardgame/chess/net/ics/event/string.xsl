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
		xmlns:str="http://ictk.sourceforge.net/namespace/string"
		extension-element-prefixes="str">

<!-- standard library functions -->

<!-- toUpper ......................................................-->
<xsl:template name="str:toUpper">
   <xsl:param name="input"/>
   <xsl:value-of select="translate($input, 
                          'abcdefghijklmnopqrstuvwxyz',
                          'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
</xsl:template>

<!-- toLower ......................................................-->
<xsl:template name="str:toLower">
   <xsl:param name="input"/>
   <xsl:value-of select="translate($input, 
                          'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
                          'abcdefghijklmnopqrstuvwxyz')"/>
</xsl:template>


<!-- capitalize ...................................................-->
<!-- capitalizes the first character in the ing.
-->
<xsl:template name="str:capitalize">
   <xsl:param name="input"/>
   <xsl:call-template name="str:toUpper">
      <xsl:with-param name="input" select="substring($input, 1, 1)"/>
   </xsl:call-template>
   <xsl:value-of select="substring($input, 2, string-length($input))"/>
</xsl:template>

<!-- dup -->
<xsl:template name="str:dup">
   <xsl:param name="input"/>
   <xsl:param name="count" select="1"/>
   <xsl:choose>
      <xsl:when test="not($count) or not($input)"/>
      <xsl:when test="$count = 1">
         <xsl:value-of select="$input"/>
      </xsl:when>
      <xsl:otherwise>
         <!-- If $count is odd append an extra copy of input -->
	 <xsl:if test="$count mod 2">
	    <xsl:value-of select="$input"/>
	 </xsl:if>
	 <!-- recurively apply template after doubling input 
	      and halving output count.
	 -->
	 <xsl:call-template name="str:dup">
	    <xsl:with-param name="input"
	                    select="concat($input, $input)"/>
	    <xsl:with-param name="count"
	                    select="floor($count div 2)"/>
	 </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<!-- substring-before-last ........................................-->
<!-- reverse search starting at the end of the string.
     Code from "XSLT Cookbook" by Sal Mangano
-->
<xsl:template name="str:substring-before-last">
   <xsl:param name="input"/>
   <xsl:param name="substr"/>
   <xsl:if test="$substr and contains($input, $substr)">
      <xsl:variable name="tmp" select="substring-after($input, $substr)"/>
      <xsl:value-of select="substring-before($input, $substr)"/>
      <xsl:if test="contains($tmp, $substr)">
         <xsl:value-of select="$substr"/>
	 <xsl:call-template name="str:substring-before-last">
	    <xsl:with-param name="input" select="$tmp"/>
	    <xsl:with-param name="substr" select="$substr"/>
	 </xsl:call-template>
      </xsl:if>
   </xsl:if>
</xsl:template>

<!-- search-and-replace ...........................................-->
<!-- searches for a ing and replaces all occurances with replace
-->
<xsl:template name="str:search-and-replace">
   <xsl:param name="input"/>
   <xsl:param name="search"/>
   <xsl:param name="replace"/>

   <xsl:choose>
      <!-- replace and then recursively call this function on the rest
           of the input.
      -->
      <xsl:when test="$search and contains($input, $search)">
         <xsl:value-of select="substring-before($input, $search)"/>
	 <xsl:value-of select="$replace"/>
	 <xsl:call-template name="str:search-and-replace">
	    <xsl:with-param name="input" 
	                    select="substring-after($input, $search)"/>
	    <xsl:with-param name="search"
	                    select="$search"/>
	    <xsl:with-param name="replace"
	                    select="$replace"/>
	 </xsl:call-template>
      </xsl:when>

      <!-- didn't find anymore instances so return the ing as is -->
      <xsl:otherwise>
         <xsl:value-of select="$input"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>

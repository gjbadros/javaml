<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:cvs="http://www.cvshome.org"
                saxon:trace="no"
                version="1.0"
                extension-element-prefixes="saxon xt"
		cvs:version="$Id$">
<!-- 
 Copyright (C) 2000, Infospace.com
 All Rights Reserved
 xml-flatten.xslt
 Greg J. Badros <gregb@go2net.com>

 This stylesheet outputs a tab-separated two-column text file
 that contains all of the text nodes in the second column with
 the list of enclosing ancestors separated by "/" in the first
 column.  E.g., for

<?xml version="1.0"?>
<!DOCTYPE email SYSTEM "email.dtd">
<email>
  <head>
    <to>Mom</to>
    <from>Greg</from>
    <subject>My trip</subject>
  </head>
  <body encoding="ascii">Weather is terrific</body>
</email>

 this stylesheet will output (with a single tab between columns):

email/head/to   	Mom
email/head/from 	Greg
email/head/subject      My trip
email/body      	Weather is terrific

 -->
 <xsl:strip-space elements="*"/>
 <xsl:output method="text"/>

 <xsl:template match="text()">
   <xsl:for-each select="ancestor::*">
     <xsl:value-of select="name()"/>
     <xsl:if test="not(position()=last())">
       <xsl:text>/</xsl:text>
     </xsl:if>
   </xsl:for-each>
   <xsl:text>&#x9;</xsl:text>
   <xsl:value-of select="."/><xsl:text>&#xA;</xsl:text>
 </xsl:template>

</xsl:stylesheet>
<!--
  Local Variables:
  tab-width: 8
  End:
  vim:ts=8:sw=2:sta 
 -->

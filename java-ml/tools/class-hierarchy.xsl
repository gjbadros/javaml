<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml call-graph.xsl -->

<xsl:output method="text"/>

<xsl:template match="text()"/>

<xsl:template match="class">
  <xsl:value-of select="@visibility"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:text>, parent is </xsl:text>
  <xsl:value-of select="./superclass/@name"/>
  <xsl:call-template name="newline"/>  
</xsl:template>

<xsl:template match="interface">
  <xsl:text>interface </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:if test="./extend">
    <xsl:text> extends </xsl:text>
    <xsl:value-of select="./extend/@interface"/>
  </xsl:if>
  <xsl:call-template name="newline"/>  
</xsl:template>

<xsl:template name="newline">
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

</xsl:stylesheet>

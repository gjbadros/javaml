<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: 
     saxon foo.java.xml module-dependencies.xsl
  -->

<xsl:variable name="arrow" select="' -> '"/>
<xsl:output method="text"/>

<xsl:template match="/">
  <xsl:text>digraph G {</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="java-class-file">
  <xsl:apply-templates select="import">
    <xsl:with-param name="source" select="@name"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="import">
  <xsl:param name="source"/>
  <xsl:choose>
    <xsl:when test="@module='parser.*'">
      <xsl:text>"</xsl:text>
      <xsl:value-of select="$source"/>
      <xsl:text>" [color = blue];</xsl:text>
    </xsl:when>
  </xsl:choose>
  <xsl:text>"</xsl:text>
  <xsl:value-of select="$source"/>
  <xsl:text>"</xsl:text>
  <xsl:value-of select="$arrow"/>
  <xsl:text>"</xsl:text>
  <xsl:value-of select="@module"/>
  <xsl:text>"</xsl:text>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

</xsl:stylesheet>

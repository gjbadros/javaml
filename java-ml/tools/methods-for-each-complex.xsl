<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml methods.xsl -->

<xsl:variable name="numMethods" select="count(//method)"/>

<xsl:template match="/">
  <xsl:for-each select="//method[@visibility = 'public']">
    <xsl:sort order="ascending" select="@name"/>
      <xsl:number/>
      <xsl:text>/</xsl:text>
      <xsl:value-of select="$numMethods"/>
      <xsl:text> </xsl:text>
    <xsl:value-of select="@visibility"/>
    <xsl:if test="@visibility">
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:value-of select="@name"/>
    <xsl:text>&#xa;</xsl:text>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>

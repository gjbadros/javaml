<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml methods.xsl -->

<xsl:template match="/">
  <xsl:apply-templates match="method"/>
</xsl:template>

<xsl:template match="method">
  <xsl:value-of select="@visibility"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
</xsl:template>

</xsl:stylesheet>

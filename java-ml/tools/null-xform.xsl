<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml methods.xsl -->

<xsl:template match="/">
 <xsl:apply-templates/>
 <xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="*">
  <xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>

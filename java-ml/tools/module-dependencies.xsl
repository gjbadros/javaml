<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: 
     saxon foo.java.xml module-dependencies.xsl
  -->

<xsl:template match="java-class-file">
  <xsl:value-of select="@name"/>
  <xsl:text> imports:&#xA;</xsl:text>
  <xsl:apply-templates select="import"/>
</xsl:template>

<xsl:template match="import">
  <xsl:value-of select="@module"/>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

</xsl:stylesheet>

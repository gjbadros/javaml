<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml method-rename.xsl -->

<xsl:output mode="xml"/>
<xsl:preserve-space elements="java-source-program"/>

<xsl:template match="*|@*">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|text()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                xmlns:saxon="http://icl.com/saxon"
                saxon:trace="no"
                version="1.0"
                extension-element-prefixes="saxon xt">
<!-- Run this with: xt foo.java.xml javaml-to-plain-source.xsl -->
<!-- or: xt foo.java.xml javaml-to-plain-source.xsl annotate-var-refs-with-type=true -->
<!-- or use saxon: saxon foo.java.xml javaml-to-plain-source.xsl annotate-var-refs-with-type=true -->

<xsl:output method="text"/>

<xsl:template match="*">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*[@comment]">
  <xsl:value-of select="@comment"/>
  <xsl:text>&#xA;</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: 
     xt foo.java.xml method-rename.xsl oldname=paint newname=Paint 
  -->

<xsl:param name="oldname"/>
<xsl:param name="newname"/>

<!-- mostly do an identity transform -->
<xsl:template match="*|@*|text()">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|text()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="method[@name=$oldname]">
  <method name="{$newname}">
    <xsl:apply-templates/>
  </method>
</xsl:template>

<xsl:template match="send[@message=$oldname]">
  <send message="{$newname}">
    <xsl:apply-templates/>
  </send>
</xsl:template>

<!-- Add newline to end of output -->
<xsl:template match="/">
 <xsl:apply-templates/>
 <xsl:text>&#xa;</xsl:text>
</xsl:template>

</xsl:stylesheet>

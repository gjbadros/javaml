<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:date="http://www.jclark.com/xt/java/java.util.Date">

<xsl:template match="/">
  <html>
    <xsl:if test="function-available('date:to-string') and function-available('date:new')">
      <p><xsl:value-of select="date:to-string(date:new())"/></p>
    </xsl:if>
  </html>
</xsl:template>

</xsl:stylesheet>

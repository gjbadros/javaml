<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml pretty-printer.xsl -->

<xsl:output method="text"/>

<xsl:template match="*"/>

<xsl:template match="java-source-program">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="import">
  <xsl:text>import </xsl:text>
  <xsl:value-of select="@module"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="package-decl">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="class">
  <xsl:value-of select="@visibility"/>
  <xsl:text> class </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:apply-templates select="superclass"/>
  <xsl:if test="count(//implement)">
    <xsl:text> implements </xsl:text>
    <xsl:for-each select="//implement">
      <xsl:value-of select="@interface"/>
    </xsl:for-each>
  </xsl:if>
  <xsl:text> {&#xa;</xsl:text>
  <xsl:apply-templates select="//interface"/>
  <xsl:apply-templates select="//class"/>
  <xsl:apply-templates select="//constructor"/>
  <xsl:apply-templates select="//method"/>
  <xsl:text>} /* class */&#xa;</xsl:text>
</xsl:template>

<xsl:template match="superclass">
  <xsl:text> extends </xsl:text>
  <xsl:value-of select="@class"/>
</xsl:template>

<xsl:template match="method">
  <xsl:apply-templates select="type"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@visibility"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:apply-templates select="formal-arguments"/>
  <xsl:apply-templates select="statements"/>
  <xsl:text> /* method */&#xa;</xsl:text>
</xsl:template>

<xsl:template match="constructor">
  <xsl:value-of select="@name"/>
  <xsl:apply-templates select="formal-arguments"/>
  <xsl:apply-templates select="statements"/>
  <xsl:text> /* method */&#xa;</xsl:text>
</xsl:template>

<xsl:template match="formal-arguments">
  <xsl:text>(</xsl:text>
  <xsl:for-each select="formal-argument">
    <xsl:apply-templates select="."/>
    <xsl:if test="not(position()=last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="formal-argument">
  <xsl:apply-templates select="type"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="type">
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="statements">
  <xsl:text> {</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="send">
  <xsl:apply-templates select="target"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="@message"/>
  <xsl:apply-templates select="arguments"/>
</xsl:template>

<xsl:template match="target">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="arguments">
  <xsl:text>(</xsl:text>
  <xsl:for-each select="*">
    <xsl:apply-templates select="."/>
    <xsl:if test="not(position()=last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="var-ref">
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="var-set">
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="field-access">
  <xsl:apply-templates/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="@field"/>
</xsl:template>

<xsl:template match="local-variable">
  <xsl:apply-templates select="type"/>
  <xsl:text> = </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="literal-string">
  <xsl:text>"</xsl:text>
  <xsl:value-of select="."/>
  <xsl:text>"</xsl:text>
</xsl:template>

<xsl:template match="literal-number">
  <xsl:value-of select="@value"/>
</xsl:template>

</xsl:stylesheet>

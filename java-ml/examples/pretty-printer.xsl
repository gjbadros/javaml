<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                version="1.0"
                extension-element-prefixes="xt">
<!-- Run this with: xt foo.java.xml pretty-printer.xsl -->

<xsl:output method="text"/>

<xsl:template match="*|@*|text()"/>

<xsl:template match="java-source-program">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="import">
  <xsl:text>import </xsl:text>
  <xsl:value-of select="@module"/>
  <xsl:text>;&#xA;</xsl:text>
</xsl:template>

<xsl:template match="package-decl">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:text>;&#xA;</xsl:text>
</xsl:template>

<xsl:template match="class">
  <xsl:if test="@visibility">
    <xsl:value-of select="@visibility"/>
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:if test="@abstract">
    <xsl:text>abstract </xsl:text>
  </xsl:if>
  <xsl:if test="@static">
    <xsl:text>static </xsl:text>
  </xsl:if>
  <xsl:if test="@synchronized">
    <xsl:text>synchronized </xsl:text>
  </xsl:if>
  <xsl:text>class </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:apply-templates select="superclass"/>
  <xsl:if test="count(implement)">
    <xsl:text> implements </xsl:text>
    <xsl:for-each select="implement">
      <xsl:value-of select="@interface"/>
    </xsl:for-each>
  </xsl:if>
  <xsl:text> {&#xa;</xsl:text>
  <xsl:apply-templates select="interface"/>
  <xsl:apply-templates select="class"/>
  <xsl:apply-templates select="constructor"/>
  <xsl:apply-templates select="method"/>
  <xsl:apply-templates select="field"/>
  <xsl:text>}&#xa;</xsl:text>
</xsl:template>

<xsl:template match="superclass">
  <xsl:text> extends </xsl:text>
  <xsl:value-of select="@class"/>
</xsl:template>

<xsl:template match="field">
  <xsl:if test="@visibility">
    <xsl:value-of select="@visibility"/>
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:if test="@abstract">
    <xsl:text>abstract </xsl:text>
  </xsl:if>
  <xsl:if test="@static">
    <xsl:text>static </xsl:text>
  </xsl:if>
  <xsl:if test="@synchronized">
    <xsl:text>synchronized </xsl:text>
  </xsl:if>
  <xsl:apply-templates select="type"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:text>;&#xA;</xsl:text>
</xsl:template>

<xsl:template match="method">
  <xsl:value-of select="@visibility"/>
  <xsl:text> </xsl:text>
  <xsl:if test="@abstract">
    <xsl:text>abstract </xsl:text>
  </xsl:if>
  <xsl:if test="@static">
    <xsl:text>static </xsl:text>
  </xsl:if>
  <xsl:if test="@final">
    <xsl:text>final </xsl:text>
  </xsl:if>
  <xsl:if test="@synchronized">
    <xsl:text>synchronized </xsl:text>
  </xsl:if>
  <xsl:if test="@native">
    <xsl:text>native </xsl:text>
  </xsl:if>
  <xsl:apply-templates select="*[1]"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:apply-templates select="*[position() > 1]"/>
  <xsl:choose>
    <xsl:when test="statements"/>
    <xsl:otherwise><xsl:text>;&#xA;</xsl:text></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="constructor">
  <xsl:value-of select="@name"/>
  <xsl:apply-templates/>
  <xsl:text>&#xa;</xsl:text>
</xsl:template>

<xsl:template match="formal-arguments">
  <xsl:text>(</xsl:text>
  <xsl:for-each select="formal-argument">
    <xsl:apply-templates select="."/>
    <xsl:if test="not(position()=last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="formal-argument">
  <xsl:if test="@final">
    <xsl:text>final </xsl:text>
  </xsl:if>
  <xsl:apply-templates select="type"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="throws">
  <xsl:text>throws </xsl:text>
  <xsl:value-of select="@exception"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="type">
  <xsl:value-of select="@name"/>
<!--GJB:FIXME:: this is ugly -->
  <xsl:choose>
    <xsl:when test="not(@dimensions)"/>
    <xsl:when test="@dimensions='1'">
      <xsl:text>[]</xsl:text></xsl:when>
    <xsl:when test="@dimensions='2'">
      <xsl:text>[][]</xsl:text></xsl:when>
    <xsl:when test="@dimensions='3'">
      <xsl:text>[][][]</xsl:text></xsl:when>
    <xsl:when test="@dimensions='4'">
      <xsl:text>[][][][]</xsl:text></xsl:when>
   </xsl:choose>
</xsl:template>

<xsl:template match="statements">
  <xsl:text>{&#xA;</xsl:text>
  <xsl:for-each select="*">
    <xsl:apply-templates select="."/>
    <xsl:choose>
      <xsl:when test="if|true-case|false-case|loop|statements|local-variable"/>
      <xsl:otherwise>
        <xsl:text>;&#xA;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>}&#xA;</xsl:text>
</xsl:template>

<xsl:template match="send">
  <xsl:if test="target">
    <xsl:apply-templates select="target"/>
    <xsl:text>.</xsl:text>
  </xsl:if>
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

<xsl:template match="array-ref">
  <xsl:apply-templates select="base"/>
  <xsl:text>[</xsl:text>
  <xsl:apply-templates select="offset"/>
  <xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="base|offset">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="assignment-expr">
  <xsl:apply-templates select="*[1]"/>
  <xsl:text> = </xsl:text>
  <xsl:apply-templates select="*[position() > 1]"/>
</xsl:template>

<xsl:template match="lvalue">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="local-variable">
  <xsl:choose>
    <xsl:when test="not(@FIXMEGJB-continued)">
    <xsl:if test="@visibility">
    <xsl:value-of select="@visibility"/>
    <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:if test="@abstract">
    <xsl:text>abstract </xsl:text>
    </xsl:if>
    <xsl:if test="@static">
    <xsl:text>static </xsl:text>
    </xsl:if>
    <xsl:if test="@synchronized">
    <xsl:text>synchronized </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="*[position() = 1]"/>
    <xsl:text> </xsl:text>
    </xsl:when>
  <xsl:otherwise><xsl:text>, </xsl:text></xsl:otherwise>
  </xsl:choose>
  <xsl:value-of select="@name"/>
  <xsl:if test="*[position() > 1]">
    <xsl:text> = </xsl:text>
    <xsl:apply-templates select="*[position() > 1]"/>
  </xsl:if>
</xsl:template>

<xsl:template match="literal-string">
  <xsl:text>"</xsl:text>
  <xsl:value-of select="."/>
  <xsl:text>"</xsl:text>
</xsl:template>

<xsl:template match="literal-number">
  <xsl:value-of select="@value"/>
</xsl:template>

<xsl:template match="literal-true">
  <xsl:text>true</xsl:text>
</xsl:template>

<xsl:template match="literal-false">
  <xsl:text>false</xsl:text>
</xsl:template>

<xsl:template match="literal-null">
  <xsl:text>null</xsl:text>
</xsl:template>

<xsl:template match="new">
  <xsl:text>new </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="if">
  <xsl:text>if</xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="test">
  <xsl:text> (</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="test" mode="no-parens">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="true-case">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="false-case">
  <xsl:text> else </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="binary-expr">
  <xsl:apply-templates select="*[1]"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="@op"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select="*[2]"/>
</xsl:template>

<xsl:template match="unary-expr">
  <xsl:if test="not(@post)">
    <xsl:value-of select="@op"/>
  </xsl:if>
  <xsl:apply-templates/>
  <xsl:if test="@post">
    <xsl:value-of select="@op"/>
  </xsl:if>
</xsl:template>

<xsl:template match="return">
  <xsl:text>return </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="throw">
  <xsl:text>throw </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="loop[@kind='for']">
  <xsl:text>for (</xsl:text>
  <xsl:apply-templates select="init"/>
  <xsl:text>; </xsl:text>
  <xsl:apply-templates select="test" mode="no-parens"/>
  <xsl:text>; </xsl:text>
  <xsl:apply-templates select="update"/>
  <xsl:text>) </xsl:text>
  <xsl:apply-templates select="statements"/>
</xsl:template>

<xsl:template match="init|update">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

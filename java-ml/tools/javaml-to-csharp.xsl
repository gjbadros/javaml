<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:cvs="http://www.cs.washington.edu/cvs"
                saxon:trace="no"
                version="1.0"
                extension-element-prefixes="saxon xt"
		cvs:version="$Id$">
<!-- javaml-to-csharp.xsl
     Copyright (C) 2001 by Greg J. Badros <badros@cs.washington.edu> 

  This does not (yet?) convert library calls to a CLR equivalent,
  and does not really try to get the semantics exactly right, but just
  does a fairly superfical re-write into Java keywords, etc.
-->

<!-- Run this with: xt foo.java.xml javaml-to-plain-source.xsl -->
<!-- or: xt foo.java.xml javaml-to-plain-source.xsl annotate-var-refs-with-type=true -->
<!-- or use saxon: saxon foo.java.xml javaml-to-plain-source.xsl annotate-var-refs-with-type=true -->

<xsl:param name="annotate-var-refs-with-type"/>

<xsl:variable name="cchIndent" select="0" saxon:assignable="yes"/>

<xsl:output method="text"/>

<xsl:template match="*|@*|text()"/>

<xsl:template match="java-source-program">
  <xsl:for-each select="*">
    <xsl:text>// FILE: </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>&#xA;</xsl:text>
    <xsl:if test="package-decl">
       <xsl:apply-templates select="package-decl" mode="do-namespace"/>
    </xsl:if>
    <xsl:apply-templates select="."/>
    <xsl:if test="package-decl">
       <xsl:text>}</xsl:text>
    </xsl:if>
    <xsl:if test="not(position()=last())">
      <xsl:text>//----------------&#xA;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template match="java-class-file">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="code-fragment|result">
  <xsl:for-each select="*">
    <xsl:apply-templates select="."/>
    <xsl:call-template name="newline"/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="import">
  <xsl:if test="@module != ''">
    <xsl:text>using </xsl:text>
    <xsl:value-of select="@module"/>
    <xsl:call-template name="semicolon-newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="package-decl" mode="do-namespace">
  <xsl:text>namespace </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:text> {</xsl:text>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="@visibility[.='package']">
  <xsl:text>internal</xsl:text>
</xsl:template>

<xsl:template match="@visibility">
  <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="class">
  <xsl:if test="@visibility">
    <xsl:apply-templates select="@visibility"/>
    <xsl:text> </xsl:text>
  </xsl:if>
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
  <xsl:text>class </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:apply-templates select="superclass"/>
  <xsl:if test="count(implement)">
    <xsl:call-template name="newline"/>
    <xsl:text>: </xsl:text>
    <xsl:for-each select="implement">
      <xsl:value-of select="@interface"/>
      <xsl:if test="not(position()=last())">
        <xsl:text>, </xsl:text>
      </xsl:if>
    </xsl:for-each>
  </xsl:if>
  <xsl:text> </xsl:text>
  <xsl:call-template name="open-brace-newline"/>
  <xsl:apply-templates select="interface|class|constructor|method|field"/>
  <xsl:call-template name="close-brace-newline"/>
</xsl:template>

<xsl:template match="superclass">
  <xsl:text> : </xsl:text>
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="field">
  <xsl:call-template name="local-variable-or-field"/>
  <xsl:call-template name="semicolon-newline"/>
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
    <xsl:when test="block"/>
    <xsl:otherwise>
      <xsl:text>;</xsl:text>
      <xsl:call-template name="newline"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="constructor">
  <xsl:value-of select="@name"/>
  <xsl:apply-templates/>
  <xsl:call-template name="newline"/>
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
  <xsl:text>// throws </xsl:text>
  <xsl:value-of select="@exception"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="type">
  <xsl:value-of select="@name"/>
  <xsl:value-of select="substring('[][][][][][][][][][][][][][][][][]',1,(@dimensions*2))"/>
</xsl:template>

<xsl:template match="type[@name='boolean']">
  <xsl:text>bool</xsl:text>
  <xsl:value-of select="substring('[][][][][][][][][][][][][][][][][]',1,(@dimensions*2))"/>
</xsl:template>

<xsl:template match="block">
  <xsl:call-template name="open-brace-newline"/>
  <xsl:choose>
    <xsl:when test="preceding-sibling::super-call">
      <xsl:text>super</xsl:text>
      <xsl:apply-templates select="preceding-sibling::super-call/arguments"/>
      <xsl:call-template name="semicolon-newline"/>
    </xsl:when>
    <xsl:when test="preceding-sibling::this-call">
      <xsl:text>this</xsl:text>
      <xsl:apply-templates select="preceding-sibling::this-call/arguments"/>
    </xsl:when>
  </xsl:choose>
  <xsl:for-each select="*">
    <xsl:apply-templates select="."/>
    <xsl:choose>
      <xsl:when test="if|true-case|false-case|loop|block|catch"/>
      <xsl:when test="following-sibling::local-variable[@continued and position()=1]">
        <xsl:value-of select="following-sibling"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="semicolon-newline"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:call-template name="close-brace-newline"/>
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
  <xsl:if test="$annotate-var-refs-with-type">
    <xsl:text>@[</xsl:text>
    <xsl:value-of select="id(@idref)/type/@name"/>
    <xsl:text>@]</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="var-set">
  <xsl:value-of select="@name"/>
</xsl:template>

<xsl:template match="field-access">
  <xsl:apply-templates/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="@field"/>
</xsl:template>

<xsl:template match="field-set">
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
  <xsl:text> </xsl:text>
  <xsl:value-of select="@op"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select="*[position() > 1]"/>
</xsl:template>

<xsl:template match="lvalue">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="local-variable" name="local-variable-or-field">
  <xsl:choose>
    <xsl:when test="not(@continued)">
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
     <xsl:if test="@final">
      <xsl:text>final </xsl:text>
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
    <xsl:choose>
      <xsl:when test="*[last() - position() > 2]">
	<xsl:call-template name="open-brace"/>
  	<xsl:for-each select="*[position() > 1]">
  	  <xsl:apply-templates select="."/>
  	  <xsl:if test="not(position()=last())">
  	    <xsl:text>, </xsl:text>
  	  </xsl:if>
  	</xsl:for-each>
	<xsl:call-template name="close-brace"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="*[position() > 1]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template match="literal-string">
  <xsl:text>"</xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>"</xsl:text>
</xsl:template>

<xsl:template match="literal-char">
  <xsl:text>'</xsl:text>
  <xsl:value-of select="@value"/>
  <xsl:text>'</xsl:text>
</xsl:template>

<xsl:template match="literal-number">
  <xsl:value-of select="@value"/>
</xsl:template>

<xsl:template match="literal-boolean[@value='true']">
  <xsl:text>true</xsl:text>
</xsl:template>

<xsl:template match="literal-boolean[@value='false']">
  <xsl:text>false</xsl:text>
</xsl:template>

<xsl:template match="literal-null">
  <xsl:text>null</xsl:text>
</xsl:template>

<xsl:template match="this">
  <xsl:text>this</xsl:text>
</xsl:template>

<xsl:template match="super">
  <xsl:text>super</xsl:text>
</xsl:template>

<xsl:template match="new">
  <xsl:text>new </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="new-array">
  <xsl:text>new </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="dim-expr">
  <xsl:text>[</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]</xsl:text>
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
  <xsl:if test="not(.//block)">
    <xsl:call-template name="semicolon-newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="false-case">
  <xsl:text> else </xsl:text>
  <xsl:apply-templates/>
  <xsl:if test="not(.//block)">
    <xsl:call-template name="semicolon-newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="paren">
  <xsl:text>(</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>)</xsl:text>
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

<xsl:template match="conditional-expr">
  <xsl:apply-templates select="*[1]"/>
  <xsl:text>? </xsl:text>
  <xsl:apply-templates select="*[2]"/>
  <xsl:text> : </xsl:text>
  <xsl:apply-templates select="*[3]"/>
</xsl:template>

<xsl:template match="cast-expr">
  <xsl:text>(</xsl:text>
  <xsl:apply-templates select="type"/>
  <xsl:text>) </xsl:text>
  <xsl:apply-templates select="*[position() > 1]"/>
</xsl:template>

<xsl:template match="instanceof-test">
  <xsl:apply-templates select="*[1]"/>
  <xsl:text> instanceof </xsl:text>
  <xsl:apply-templates select="*[2]"/>
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
  <xsl:text>)</xsl:text>
  <xsl:call-template name="newline"/>
  <xsl:variable name="numtoskip" select="count(init|test|update)"/>
  <xsl:apply-templates select="*[position() > $numtoskip]"/>
  <xsl:if test="not(.//block)">
    <xsl:call-template name="semicolon-newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="loop[@kind='while']">
  <xsl:text>while </xsl:text>
  <xsl:apply-templates/>
  <xsl:if test="not(.//block)">
    <xsl:call-template name="semicolon-newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="do-loop">
  <xsl:text>do </xsl:text>
  <xsl:apply-templates select="*[1]"/>
  <xsl:if test="not(block)">
    <xsl:text>{} </xsl:text>
  </xsl:if>
  <xsl:text>while </xsl:text>
  <xsl:apply-templates select="test"/>
  <xsl:call-template name="semicolon-newline"/>
</xsl:template>

<xsl:template match="init|update">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="try">
  <xsl:text>try </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="catch">
  <xsl:text>catch (</xsl:text>
  <xsl:apply-templates select="formal-argument"/>
  <xsl:text>)</xsl:text>
  <xsl:apply-templates select="*[position() > 1]"/>
  <xsl:if test="not(block)">
    <xsl:text>{} </xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="finally">
  <xsl:text>finally </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="synchronized">
  <xsl:text>synchronized </xsl:text>
  <xsl:apply-templates select="expr"/>
  <xsl:apply-templates select="block"/>
</xsl:template>

<xsl:template match="expr">
  <xsl:text>(</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="break">
  <xsl:text>break</xsl:text>
</xsl:template>

<xsl:template match="switch">
  <xsl:text>switch (</xsl:text>
  <xsl:apply-templates select="*[1]"/>
  <xsl:text>) </xsl:text>
  <xsl:call-template name="open-brace-newline"/>
  <xsl:apply-templates select="*[position() > 1]"/>
  <xsl:call-template name="close-brace-newline"/>
</xsl:template>

<xsl:template match="case">
  <xsl:text>case </xsl:text>
  <xsl:apply-templates/>
  <xsl:text>:</xsl:text>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="default-case">
  <xsl:text>default:</xsl:text>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="array-initializer">
  <xsl:call-template name="open-brace"/>
  <xsl:for-each select="*">
    <xsl:apply-templates select="."/>
       <xsl:if test="not(position()=last())">
  	 <xsl:text>, </xsl:text>
       </xsl:if>
  </xsl:for-each>
  <xsl:call-template name="close-brace"/>
</xsl:template>

<xsl:template name="semicolon-newline">
  <xsl:text>;</xsl:text>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template name="newline">
  <xsl:text>&#xA;</xsl:text>
  <xsl:value-of select="substring('                                                          ',1,$cchIndent)"/>
</xsl:template>

<xsl:template name="open-brace">
  <xsl:text>{</xsl:text>
</xsl:template>

<xsl:template name="close-brace">
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template name="open-brace-newline">
  <saxon:assign name="cchIndent" select="$cchIndent + 2"/>
  <xsl:call-template name="newline"/>
  <xsl:call-template name="open-brace"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template name="close-brace-newline">
  <saxon:assign name="cchIndent" select="$cchIndent - 2"/>
  <xsl:call-template name="close-brace"/>
  <xsl:call-template name="newline"/>
</xsl:template>

</xsl:stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xt="http://www.jclark.com/xt"
                xmlns:saxon="http://icl.com/saxon"
                saxon:trace="no"
                version="1.0"
                extension-element-prefixes="saxon xt">
<!-- javaml-to-html.xsl
     Copyright (C) 2000 by Greg J. Badros <gjb@cs.washington.edu> -->

<!-- Run this with: xt foo.java.xml javaml-to-html.xsl -->
<!-- or: xt foo.java.xml javaml-to-html.xsl -->
<!-- or use saxon: saxon foo.java.xml javaml-to-html.xsl -->

<xsl:param name="annotate-var-refs-with-type"/>
<xsl:param name="want-method-index">true</xsl:param>

<xsl:param name="clr-import">blue</xsl:param>
<xsl:param name="clr-package">blue</xsl:param>
<xsl:param name="clr-visibility">purple</xsl:param>
<xsl:param name="clr-abstract">purple</xsl:param>
<xsl:param name="clr-type">olive</xsl:param>
<xsl:param name="clr-static">purple</xsl:param>
<xsl:param name="clr-synchronized">purple</xsl:param>
<xsl:param name="clr-final">red</xsl:param>
<xsl:param name="clr-class">blue</xsl:param>
<xsl:param name="clr-implements">blue</xsl:param>
<xsl:param name="clr-extends">green</xsl:param>
<xsl:param name="clr-native">purple</xsl:param>
<xsl:param name="clr-true">green</xsl:param>
<xsl:param name="clr-false">red</xsl:param>
<xsl:param name="clr-null">red</xsl:param>
<xsl:param name="clr-this">blue</xsl:param>
<xsl:param name="clr-super">blue</xsl:param>
<xsl:param name="clr-new">blue</xsl:param>
<xsl:param name="clr-var-ref">green</xsl:param>
<xsl:param name="clr-var-set">fuchsia</xsl:param>
<xsl:param name="clr-switch">blue</xsl:param>
<xsl:param name="clr-case">blue</xsl:param>
<xsl:param name="clr-default-case">blue</xsl:param>
<xsl:param name="clr-break">blue</xsl:param>
<xsl:param name="clr-try">blue</xsl:param>
<xsl:param name="clr-catch">blue</xsl:param>
<xsl:param name="clr-if">blue</xsl:param>
<xsl:param name="clr-while">blue</xsl:param>
<xsl:param name="clr-do">blue</xsl:param>
<xsl:param name="clr-for">blue</xsl:param>
<xsl:param name="clr-else">blue</xsl:param>
<xsl:param name="clr-binary-operator">blue</xsl:param>
<xsl:param name="clr-synchronized-stmt">blue</xsl:param>
<xsl:param name="clr-super-call">blue</xsl:param>
<xsl:param name="clr-this-call">blue</xsl:param>
<xsl:param name="clr-field-set-field">blue</xsl:param>
<xsl:param name="clr-finally">blue</xsl:param>
<xsl:param name="clr-instanceof">blue</xsl:param>

<xsl:variable name="cchIndent" select="0"/>

<xsl:output method="html"/>


<xsl:template match="*|@*|text()"/>

<xsl:template match="java-source-program">
  <html>
    <head>
      <title><xsl:value-of select="@name"/></title>
    </head>
    <body>
  <xsl:if test="'true' = $want-method-index">
   <h1><xsl:text>Method index:</xsl:text></h1>
   <xsl:apply-templates select="//method" mode="index"/>
   <h1><xsl:text>Source code:</xsl:text></h1>
  </xsl:if>
  <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="import">
  <em><font color="{$clr-import}"><xsl:text>import </xsl:text></font></em>
  <strong><xsl:value-of select="@module"/></strong>
  <xsl:call-template name="semicolon-newline"/>
</xsl:template>

<xsl:template match="package-decl">
  <em><font color="{$clr-package}"><xsl:text>package </xsl:text></font></em>
  <strong><xsl:value-of select="@name"/></strong>
  <xsl:call-template name="semicolon-newline"/>
</xsl:template>

<xsl:template match="class">
  <xsl:if test="@visibility">
    <em><font color="{$clr-visibility}"><xsl:value-of select="@visibility"/></font></em>
    <xsl:text> </xsl:text>
  </xsl:if>
  <xsl:if test="@abstract">
    <em><font color="{$clr-abstract}"><xsl:text>abstract </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@static">
    <em><font color="{$clr-static}"><xsl:text>static </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@final">
    <em><font color="{$clr-final}"><xsl:text>final </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@synchronized">
    <em><font color="{$clr-synchronized}"><xsl:text>synchronized </xsl:text></font></em>
  </xsl:if>
  <em><font color="{$clr-class}"><xsl:text>class </xsl:text></font></em>
  <strong><xsl:value-of select="@name"/></strong>
  <xsl:apply-templates select="superclass"/>
  <xsl:if test="count(implement)">
    <xsl:call-template name="newline"/>
    <em><font color="{$clr-implements}"><xsl:text>implements </xsl:text></font></em>
    <xsl:for-each select="implement">
      <strong><xsl:value-of select="@interface"/></strong>
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
  <em><font color="{$clr-extends}"><xsl:text> extends </xsl:text></font></em>
  <strong><xsl:value-of select="@name"/></strong>
</xsl:template>

<xsl:template match="field">
  <xsl:call-template name="local-variable-or-field"/>
  <xsl:call-template name="semicolon-newline"/>
</xsl:template>

<xsl:template match="method" mode="index">
  <xsl:element name="A">
    <xsl:attribute name="HREF">
      <xsl:text>#</xsl:text><xsl:value-of select="@id"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/><br/>
  </xsl:element>
</xsl:template>

<xsl:template match="method">
  <em><font color="{$clr-visibility}"><xsl:value-of select="@visibility"/></font></em>
  <xsl:text> </xsl:text>
  <xsl:if test="@abstract">
      <em><font color="{$clr-abstract}"><xsl:text>abstract </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@static">
    <em><font color="{$clr-static}"><xsl:text>static </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@final">
    <em><font color="{$clr-final}"><xsl:text>final </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@synchronized">
    <em><font color="{$clr-synchronized}"><xsl:text>synchronized </xsl:text></font></em>
  </xsl:if>
  <xsl:if test="@native">
    <em><font color="{$clr-native}"><xsl:text>native </xsl:text></font></em>
  </xsl:if>
  <xsl:apply-templates select="*[1]"/>
  <xsl:text> </xsl:text>
  <xsl:element name="A">
    <xsl:attribute name="NAME">
      <xsl:value-of select="@id"/>
    </xsl:attribute>
    <strong><xsl:value-of select="@name"/></strong>
  </xsl:element>
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
  <strong><xsl:value-of select="@name"/></strong>
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
    <em><font color="{$clr-final}"><xsl:text>final </xsl:text></font></em>
  </xsl:if>
  <xsl:apply-templates select="type"/>
  <xsl:text> </xsl:text>
  <strong><xsl:value-of select="@name"/></strong>
</xsl:template>

<xsl:template match="throws">
  <xsl:text>throws </xsl:text>
  <xsl:value-of select="@exception"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="type">
  <strong><font color="{$clr-type}"><xsl:value-of select="@name"/>
  <xsl:value-of select="substring('[][][][][][][][][][][][][][][][][]',1,(@dimensions*2))"/></font></strong>
</xsl:template>

<xsl:template match="block">
  <xsl:call-template name="open-brace-newline"/>
  <xsl:choose>
    <xsl:when test="preceding-sibling::super-call">
      <em><font color="{$clr-super-call}"><xsl:text>super-call</xsl:text></font></em>
      <xsl:apply-templates select="preceding-sibling::super-call/arguments"/>
      <xsl:call-template name="semicolon-newline"/>
    </xsl:when>
    <xsl:when test="preceding-sibling::this-call">
      <em><font color="{$clr-this-call}"><xsl:text>this</xsl:text></font></em>
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
    <strong><xsl:apply-templates select="target"/></strong>
    <xsl:text>.</xsl:text>
  </xsl:if>
  <strong><xsl:value-of select="@message"/></strong>
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
  <strong><font color="{$clr-var-ref}"><xsl:value-of select="@name"/></font></strong>
  <xsl:if test="$annotate-var-refs-with-type">
    <xsl:text>@[</xsl:text>
    <xsl:value-of select="id(@idref)/type/@name"/>
    <xsl:text>@]</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="var-set">
  <strong><font color="{$clr-var-set}"><xsl:value-of select="@name"/></font></strong>
</xsl:template>

<xsl:template match="field-access">
  <xsl:apply-templates/>
  <xsl:text>.</xsl:text>
  <strong><xsl:value-of select="@field"/></strong>
</xsl:template>

<xsl:template match="field-set">
  <xsl:apply-templates/>
  <xsl:text>.</xsl:text>
  <em><font color="{$clr-field-set-field}"><xsl:value-of select="@field"/></font></em>
</xsl:template>

<xsl:template match="array-ref">
  <strong><xsl:apply-templates select="base"/></strong>
  <xsl:text>[</xsl:text>
  <strong><xsl:apply-templates select="offset"/></strong>
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
      <em><font color="{$clr-visibility}"><xsl:value-of select="@visibility"/></font></em>
      <xsl:text> </xsl:text>
     </xsl:if>
     <xsl:if test="@abstract">
      <em><font color="{$clr-abstract}"><xsl:text>abstract </xsl:text></font></em>
     </xsl:if>
     <xsl:if test="@static">
      <em><font color="{$clr-static}"><xsl:text>static </xsl:text></font></em>
     </xsl:if>
     <xsl:if test="@final">
      <em><font color="{$clr-final}"><xsl:text>final </xsl:text></font></em>
     </xsl:if>
     <xsl:if test="@synchronized">
      <em><font color="{$clr-synchronized}"><xsl:text>synchronized </xsl:text></font></em>
     </xsl:if>
     <xsl:apply-templates select="*[position() = 1]"/>
     <xsl:text> </xsl:text>
    </xsl:when>
  <xsl:otherwise><xsl:text>, </xsl:text></xsl:otherwise>
  </xsl:choose>
  <strong><xsl:value-of select="@name"/></strong>
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
  <tt><xsl:value-of select="@value"/></tt>
  <xsl:text>"</xsl:text>
</xsl:template>

<xsl:template match="literal-char">
  <xsl:text>'</xsl:text>
  <strong><tt><xsl:value-of select="@value"/></tt></strong>
  <xsl:text>'</xsl:text>
</xsl:template>

<xsl:template match="literal-number">
  <xsl:value-of select="@value"/>
</xsl:template>

<xsl:template match="literal-boolean[@value='true']">
  <em><font color="{$clr-true}"><xsl:text>true</xsl:text></font></em>
</xsl:template>

<xsl:template match="literal-boolean[@value='false']">
  <em><font color="{$clr-false}"><xsl:text>false</xsl:text></font></em>
</xsl:template>

<xsl:template match="literal-null">
  <em><font color="{$clr-null}"><xsl:text>null</xsl:text></font></em>
</xsl:template>

<xsl:template match="this">
  <em><font color="{$clr-this}"><xsl:text>this</xsl:text></font></em>
</xsl:template>

<xsl:template match="super">
  <em><font color="{$clr-super}"><xsl:text>super</xsl:text></font></em>
</xsl:template>

<xsl:template match="new">
  <em><font color="{$clr-new}"><xsl:text>new </xsl:text></font></em>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="new-array">
  <em><font color="{$clr-new}"><xsl:text>new </xsl:text></font></em>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="dim-expr">
  <xsl:text>[</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="if">
  <em><font color="{$clr-if}"><xsl:text>if</xsl:text></font></em>
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
  <em><font color="{$clr-else}"><xsl:text> else </xsl:text></font></em>
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
  <font color="{$clr-binary-operator}"><xsl:value-of select="@op"/></font>
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
  <em><font color="{$clr-instanceof}"><xsl:text> instanceof </xsl:text></font></em>
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
  <em><font color="{$clr-for}"><xsl:text>for (</xsl:text></font></em>
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
  <em><font color="{$clr-while}"><xsl:text>while </xsl:text></font></em>
  <xsl:apply-templates/>
  <xsl:if test="not(.//block)">
    <xsl:call-template name="semicolon-newline"/>
  </xsl:if>
</xsl:template>

<xsl:template match="do-loop">
  <em><font color="{$clr-do}"><xsl:text>do </xsl:text></font></em>
  <xsl:apply-templates select="*[1]"/>
  <xsl:if test="not(block)">
    <xsl:text>{} </xsl:text>
  </xsl:if>
  <em><font color="{$clr-while}"><xsl:text>while </xsl:text></font></em>
  <xsl:apply-templates select="test"/>
  <xsl:call-template name="semicolon-newline"/>
</xsl:template>

<xsl:template match="init|update">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="try">
  <em><font color="{$clr-try}"><xsl:text>try </xsl:text></font></em>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="catch">
  <em><font color="{$clr-catch}"><xsl:text>catch (</xsl:text></font></em>
  <xsl:apply-templates select="formal-argument"/>
  <xsl:text>)</xsl:text>
  <xsl:apply-templates select="*[position() > 1]"/>
  <xsl:if test="not(block)">
    <xsl:text>{} </xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="finally">
  <em><font color="{$clr-finally}"><xsl:text>finally </xsl:text></font></em>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="synchronized">
  <em><font color="{$clr-synchronized-stmt}"><xsl:text>synchronized </xsl:text></font></em>
  <xsl:apply-templates select="expr"/>
  <xsl:apply-templates select="block"/>
</xsl:template>

<xsl:template match="expr">
  <xsl:text>(</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="break">
  <em><font color="{$clr-break}"><xsl:text>break</xsl:text></font></em>
</xsl:template>

<xsl:template match="switch">
  <em><font color="{$clr-switch}"><xsl:text>switch (</xsl:text></font></em>
  <xsl:apply-templates select="*[1]"/>
  <xsl:text>) </xsl:text>
  <xsl:call-template name="open-brace-newline"/>
  <xsl:apply-templates select="*[position() > 1]"/>
  <xsl:call-template name="close-brace-newline"/>
</xsl:template>

<xsl:template match="case">
  <em><font color="{$clr-case}"><xsl:text>case </xsl:text></font></em>
  <xsl:apply-templates/>
  <xsl:text>:</xsl:text>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template match="default-case">
  <em><font color="{$clr-default-case}"><xsl:text>default:</xsl:text></font></em>
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
  <br/>
  <xsl:call-template name="indent"/>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

<xsl:template name="open-brace">
  <xsl:text>{</xsl:text>
</xsl:template>

<xsl:template name="close-brace">
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template name="open-brace-newline">
  <saxon:assign name="cchIndent" select="$cchIndent + 1"/>
  <xsl:call-template name="newline"/>
  <xsl:call-template name="open-brace"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template name="close-brace-newline">
  <saxon:assign name="cchIndent" select="$cchIndent - 1"/>
  <xsl:call-template name="close-brace"/>
  <xsl:call-template name="newline"/>
</xsl:template>

<xsl:template name="indent">
  <xsl:variable name="i" select="0"/>
  <saxon:while test="$i &lt; $cchIndent">
     <saxon:entity-ref name="nbsp"/>
     <saxon:entity-ref name="nbsp"/>
     <saxon:entity-ref name="nbsp"/>
     <saxon:entity-ref name="nbsp"/>
     <saxon:assign name="i" select="$i + 1"/>
  </saxon:while>
</xsl:template>

</xsl:stylesheet>

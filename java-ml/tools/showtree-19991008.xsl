<?xml version="1.0"?><!--showtree.xsl-->
<!--XSLT PR - http://www.CraneSoftwrights.com/training -->
<!DOCTYPE xsl:stylesheet [
<!ENTITY nl "&#xa;"><!--new line sequence-->
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
<xsl:output method="text"/>
<!--
  PUBLIC
    "+//ISBN 1-894049::CSL::XSL::Debug//DOCUMENT Show All Nodes Stylesheet//EN"
  Copyright (C) Crane Softwrights Ltd. - http://www.CraneSoftwrights.com

  1999-10-23 07:00

  This stylesheet will report the node structure and content of an input
  document, noting the ordinal positions in the hierarchy of each component
  of the ancestry.

  This stylesheet exposes root, element, attribute, text, comment,
  and pi nodes.  Note that XSL does not consider attributes to be "children"
  or "descendants", but this program enumerates them as the engine orders
  the union of attributes with children.

  This stylesheet does not expose the namespace axis due to limitations
  acknowledged in the current implementation of XT.
  
  By default, element nodes are described only by their breakdown of
  constituent nodes.  To request that the element's value be shown in addition
  to the element's descendants, set the top-level parameter 
  $show-element-values to any non-empty string.

CAVEAT - The XSL syntax used in this file follows only the Proposed
         Recommenation of the XSLT Specification and is not necessarily
         representative of any finalized or authorized specification.
         The following has been tested only on XT-19991008.
-->

<xsl:param name="show-element-values"/>

<!--========================================================================-->
<!--nodes with children-->

<xsl:template match="/">                            <!--root node-->
  <xsl:text>SHOWTREE Stylesheet - </xsl:text>
  <xsl:text>http://www.CraneSoftwrights.com/resources/</xsl:text>
  <xsl:text>&nl;Root: </xsl:text>
  <xsl:apply-templates select="node()"/>
</xsl:template>

<xsl:template match="*">                            <!--an element-->
  <xsl:param name="ancestry"/>
  <xsl:variable name="this-ancestry">
    <xsl:if test="$ancestry!=''">
      <xsl:value-of select="$ancestry"/><xsl:text>,</xsl:text>
    </xsl:if>
    <xsl:value-of select="name(.)"/>
  </xsl:variable>
  <xsl:param name="node-pos"/>
  <xsl:variable name="this-node-pos">
    <xsl:if test="$node-pos!=''">
      <xsl:value-of select="$node-pos"/><xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:call-template name="node-count"/>
  </xsl:variable>
  <xsl:call-template name="node">
    <xsl:with-param name="ancestry" select="$ancestry"/>
    <xsl:with-param name="node-pos" select="$this-node-pos"/>
    <xsl:with-param name="type">Element</xsl:with-param>
    <xsl:with-param name="contents">                <!--node value-->
      <xsl:if test="$show-element-values">
        <xsl:text>{</xsl:text><xsl:value-of select="."/><xsl:text>}</xsl:text>
      </xsl:if>
      <xsl:choose>              <!--node children and attributes-->
        <xsl:when test="node()|@*"> 
          <xsl:apply-templates select="node()|@*">
            <xsl:with-param name="ancestry" select="$this-ancestry"/>
            <xsl:with-param name="node-pos" select="$this-node-pos"/>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:when test="$show-element-values"/>     <!--already shown-->
        <xsl:otherwise>(no child nodes or attributes)</xsl:otherwise>
      </xsl:choose>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!--========================================================================-->
<!--for every leaf node, show the value in report-->

<!--leaf nodes that can't be children of the root-->

<xsl:template match="comment()">                    <!--a comment-->
  <xsl:param name="ancestry"/>
  <xsl:param name="node-pos"/>
  <xsl:variable name="this-node-pos">
    <xsl:if test="$node-pos!=''">
      <xsl:value-of select="$node-pos"/><xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:call-template name="node-count"/>
  </xsl:variable>
  <xsl:call-template name="node">
    <xsl:with-param name="ancestry" select="$ancestry"/>
    <xsl:with-param name="node-pos" select="$this-node-pos"/>
    <xsl:with-param name="type">Comment</xsl:with-param>
    <xsl:with-param name="contents">
      <xsl:text>{</xsl:text><xsl:value-of select="."/><xsl:text>}</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template match="processing-instruction()">     <!--a processing inst.-->
  <xsl:param name="ancestry"/>
  <xsl:param name="node-pos"/>
  <xsl:variable name="this-node-pos">
    <xsl:if test="$node-pos!=''">
      <xsl:value-of select="$node-pos"/><xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:call-template name="node-count"/>
  </xsl:variable>
  <xsl:call-template name="node">
    <xsl:with-param name="ancestry" select="$ancestry"/>
    <xsl:with-param name="node-pos" select="$this-node-pos"/>
    <xsl:with-param name="type">Proc. Inst.</xsl:with-param>
    <xsl:with-param name="contents">
      <xsl:text>{</xsl:text><xsl:value-of select="."/><xsl:text>}</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!--leaf nodes that can't be children of the root-->

<xsl:template match="@*">                           <!--an attribute-->
  <xsl:param name="ancestry"/>
  <xsl:param name="node-pos"/>
  <xsl:variable name="this-node-pos">
    <xsl:value-of select="$node-pos"/><xsl:text>.</xsl:text>
    <xsl:call-template name="node-count"/>
  </xsl:variable>
  <xsl:call-template name="node">
    <xsl:with-param name="ancestry" select="$ancestry"/>
    <xsl:with-param name="node-pos" select="$this-node-pos"/>
    <xsl:with-param name="type">Attribute</xsl:with-param>
    <xsl:with-param name="contents">
      <xsl:text>{</xsl:text><xsl:value-of select="."/><xsl:text>}</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template match="text()">                       <!--a text node-->
  <xsl:param name="ancestry"/>
  <xsl:param name="node-pos"/>
  <xsl:variable name="this-node-pos">
    <xsl:value-of select="$node-pos"/><xsl:text>.</xsl:text>
    <xsl:call-template name="node-count"/>
  </xsl:variable>
  <xsl:call-template name="node">
    <xsl:with-param name="ancestry" select="$ancestry"/>
    <xsl:with-param name="node-pos" select="$this-node-pos"/>
    <xsl:with-param name="type">Text</xsl:with-param>
    <xsl:with-param name="contents">
      <xsl:text>{</xsl:text><xsl:value-of select="."/><xsl:text>}</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!--========================================================================-->
<!--display the content of a node-->

<xsl:template name="node">
  <xsl:param name="ancestry"/>
  <xsl:param name="node-pos"/>
  <xsl:param name="type"/>
  <xsl:param name="contents"/>
  <xsl:text>&nl;</xsl:text>
  <xsl:value-of select="$node-pos"/>               <!--report location-->
  <xsl:text>  </xsl:text>
  <xsl:value-of select="$type"/>                   <!--node type-->
  <xsl:if test="not(name(.)='')">                  <!--node name (if appl.)-->
    <xsl:text> '</xsl:text>
    <xsl:if test="namespace-uri(.)!=''">           <!--with namespace-->
      <xsl:text>{</xsl:text>
      <xsl:value-of select="namespace-uri(.)"/>
      <xsl:text>}</xsl:text>
    </xsl:if>
    <xsl:value-of select="name(.)"/>
    <xsl:text>'</xsl:text>
  </xsl:if>
  <xsl:text> (</xsl:text>
  <xsl:value-of select="$ancestry"/>               <!--node context-->
  <xsl:text>)</xsl:text>
  <xsl:text>: </xsl:text>
  <xsl:value-of select="$contents"/>
</xsl:template>

<!--========================================================================-->
<!--show the position context of the node within children of parent-->

<xsl:template name="node-count">
  <xsl:variable name="this-id" select="generate-id(.)"/>
  <xsl:for-each select="../node()|../@*"> <!--count child nodes & attributes-->
           <!--according to the order of the engine (attributes are not ordered
           by the recommendation, yet this report reveals the engine's order-->
    <xsl:if test="generate-id(.)=$this-id">
      <xsl:value-of select="position()"/></xsl:if>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" indent="yes"/>

	<!-- Fancy XML Tree Viewer                                            -->
	<!-- Version: 3.5 (07 Dec 1999)                                       -->
	<!-- Author: mbrown@webb.net (Mike J Brown)                           -->
	<!-- XSLT Version: 1.0 (16 Nov 1999 W3C Recommendation)               -->
	<!-- License: GNU General Public License, v2 or later                 -->
	<!--   see http://www.gnu.org/copyleft/gpl.html                       -->
	<!--                                                                  -->
	<!-- Overview:                                                        -->
	<!-- -->
	<!-- The source tree, as defined by the input XML document, consists  -->
	<!-- of a hierarchy of nodes of different types.                      -->
	<!-- -->
	<!-- An XSLT processor starts at the root node of the source tree,    --> 
	<!-- applying a template, if there is one, that best "matches" that   -->
	<!-- node. The built-in templates for various types of nodes cause    -->
	<!-- the default action of the processor to be a recursive descent    -->
	<!-- of the source tree.                                              -->
	<!-- -->
	<!-- Each template drives the creation of a result tree fragment with -->
	<!-- the same kind of hierarchical node structure. Some kind of       -->
	<!-- output may be derived from the completed result tree (typically  -->
	<!-- XML or HTML, but it can be anything, or nothing).                -->
	<!-- -->
	<!-- The templates in this stylesheet will match every kind of node,  -->
	<!-- and will produce a result tree that contains information about   -->
	<!-- each node in the form of nested HTML tables.                     -->
	<!-- -->
	<!-- Requirements:                                                    -->
	<!-- James Clark's XT 19991105 (or comparable XSLT processor) and a   -->
	<!-- web browser capable of handling nested tables. Netscape          -->
	<!-- Navigator is *not* such a browser. Microsoft Internet Explorer   -->
	<!-- 4.0 or newer, and Opera will both work.                          -->
	<!-- -->
	<!-- Change history:                                                  -->
	<!--  3.5: added version attributes to xsl:stylesheet and xsl:output  -->
	<!--  3.4: syntax updated to conform to proposed recommendation;      -->
	<!--       handling of empty elements improved                        -->
	<!--  3.3: syntax updated to conform to current XSLT working draft;   -->
	<!--       comments reformatted to fit in 78 columns                  -->
	<!--  3.2: fixed top-level select to catch comment and p.i. nodes     -->
	<!--  3.1: syntax updated to conform to current XSLT working draft    -->
	<!--  3.0: first public version                                       -->

	<!-- template that matches the root node -->
	<xsl:template match="/">
		<html>
			<head>
				<title>Fancy XML Tree Viewer</title>
				<style type="text/css">
					<xsl:text>&#xA;</xsl:text>
					<xsl:text>body { color: #CCCCCC; background-color: #333355 }&#xA;</xsl:text>
					<xsl:text>table { font-family: monospace; font-size: 97%; margin-bottom: 0.5em }&#xA;</xsl:text>
					<xsl:text>td { color: #CCCCCC; background-color: #445566 }&#xA;</xsl:text>
					<xsl:text>td td { color: #CCCCCC; background-color: #556677 }&#xA;</xsl:text>
					<xsl:text>td td td { color: #CCCCCC; background-color: #667788 }&#xA;</xsl:text>
					<xsl:text>td td td td { color: #CCCCCC; background-color: #778877 }&#xA;</xsl:text>
					<xsl:text>td td td td td { color: #CCCCCC; background-color: #887766 }&#xA;</xsl:text>
					<xsl:text>td td td td td td { color: #CCCCCC; background-color: #776655 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td { color: #CCCCCC; background-color: #665544 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td td { color: #CCCCCC; background-color: #554455 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td td td { color: #CCCCCC; background-color: #664488 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td td td td { color: #CCCCCC; background-color: #553377 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td td td td td { color: #CCCCCC; background-color: #444466 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td td td td td td { color: #CCCCCC; background-color: #335555 }&#xA;</xsl:text>
					<xsl:text>td td td td td td td td td td td td td { color: #CCCCCC; background-color: #446666 }&#xA;</xsl:text>
					<xsl:text>pre { margin: 0 0 0 0 }&#xA;</xsl:text>
					<xsl:text>ul { margin: 0 2em 0 2em }&#xA;</xsl:text>
					<xsl:text>.nodeblock { color: black; background-color: #333355 }&#xA;</xsl:text>
					<xsl:text>.nodetype { color: #000033; background-color: #8888AA }&#xA;</xsl:text>
					<xsl:text>.namespace { color: #222277 }&#xA;</xsl:text>
					<xsl:text>.nodename { color: #55AA55; background-color: #444455; font-weight: bold; font-family: sans-serif }&#xA;</xsl:text>
					<xsl:text>.data { color: #AACC88; background-color: #444422 }&#xA;</xsl:text>
				</style>
			</head>
			<body>
				<h2>Fancy XML Tree Viewer</h2>
				<p>The root node contains the following nodes:</p>
				<!-- apply the templates that match each top-level        -->
				<!-- element, comment, or processing instruction node.    -->
				<!-- XPath data model rules state that there are no text  -->
				<!-- nodes at the top level (1 level under the root)      -->
				<xsl:apply-templates select="*|comment()|processing-instruction()"/>
			</body>
		</html>
	</xsl:template>

	<!-- template that matches any text, comment, or processing           -->
	<!-- instruction node.                                                -->
	<xsl:template match="text()|comment()|processing-instruction()">
		<!-- call the template to show the contents of this node -->
		<xsl:call-template name="show_node">
			<xsl:with-param name="type">
				<xsl:choose>
					<!-- pass as parameters a description of the node     -->
					<!-- type along with the node's contents. the type    -->
					<!-- of the current node can be easily determined if  -->
					<!-- it is one of these 3                             -->
					<xsl:when test="self::text()">Text</xsl:when>
					<xsl:when test="self::comment()">Comment</xsl:when>
					<xsl:when test="self::processing-instruction()">Processing Instruction</xsl:when>
					<xsl:otherwise>Unknown</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="contents" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- a node is either an element or attribute if it has a non-empty   -->
	<!-- node name, but there is no way to determine which type it is.    -->
	<!-- therefore, we must match elements separately from attributes.    -->

	<!-- template that matches any element node -->
	<xsl:template match="*">
		<!-- call the template to show the contents of this node -->
		<xsl:call-template name="show_node">
			<!-- pass as parameters a description of the node type along  -->
			<!-- with the node's contents -->
			<xsl:with-param name="type" select="'Element'"/>
			<xsl:with-param name="contents" select="."/>
		</xsl:call-template>
	</xsl:template>

	<!-- template that matches any attribute node -->
	<xsl:template match="@*">
		<!-- call the template to show the contents of this node -->
		<xsl:call-template name="show_node">
			<!-- pass as parameters a description of the node type along  -->
			<!-- with the node's contents -->
			<xsl:with-param name="type" select="'Attribute'"/>
			<xsl:with-param name="contents" select="."/>
		</xsl:call-template>
	</xsl:template>
		
	<!-- named template that descriptively shows in an HTML table:        -->
	<!--   type of node,                                                  -->
	<!--   name of node (if any),                                         -->
	<!--   associated namespace URI (if any),                             -->
	<!--   contents of node                                               -->
	<xsl:template name="show_node">
		<!-- import the type and contents parameters -->
		<xsl:param name="type"/>
		<xsl:param name="contents"/>
		<!-- begin an enclosing table for this node -->
		<table class="nodeblock" border="1">
			<tr>
				<td>
					<!-- display the description of the node type -->
					<xsl:text>Node Type: </xsl:text>
					<span class="nodetype">
						<xsl:value-of select="$type"/>
					</span>
					<!-- start a bulleted list of this node's properties  -->
					<ul>
						<!-- if the name of the current node isn't empty  -->
						<!-- then do the following. this should only be   -->
						<!-- true for elements and attributes, since      -->
						<!-- they are the only types of nodes that have   -->
						<!-- names, and those names cannot be empty.      -->
						<xsl:if test="not(name(.)='')">
							<!-- show the node's name as a list item      -->
							<li>
								<xsl:text>Name: {</xsl:text>
								<span class="nodename">
									<xsl:value-of select="name(.)"/>
								</span>
					    		<xsl:text>}</xsl:text>
							</li>
							<!-- show the node's associated namespace URI -->
							<!-- as a list item                           -->
							<xsl:if test="not(namespace-uri(.))=''">
								<li>
									<xsl:text>Namespace: {</xsl:text>
									<span class="namespace">
										<xsl:value-of select="namespace-uri(.)"/>
									</span>
						    		<xsl:text>}</xsl:text>
									<br/>
								</li>
							</xsl:if>
						</xsl:if>
						<!-- do the following for all nodes -->
						<!-- show the node's contents as a list item -->
						<li>
							<xsl:text>Contents: </xsl:text>
							<!-- there's a difference between contents    -->
							<!-- and the string value of a node. using    -->
							<!-- <xsl:value-of> is not the way to see the -->
							<!-- contents of all types of nodes. it's     -->
							<!-- fine for text, comment, attribute, and   -->
							<!-- processing-instruction nodes, because    -->
							<!-- those are leaf nodes and the string      -->
							<!-- value is what we can call the contents.  -->
							<!-- but for element nodes, the contents are  -->
							<!-- more nodes, not a string of text. the    -->
							<!-- string value of an element node is the   -->
							<!-- rather useless concatenation of all      -->
							<!-- PCDATA in the element's descendant text  -->
							<!-- nodes, including those of sub-elements.  -->
							<!--                                          -->
							<!-- so, the rules are as follows:            -->
							<!-- if this node has no text node children,  -->
							<!-- do the following                         -->
							<xsl:if test="not(text())">
								<xsl:choose>
									<!-- if this node is not an element   -->
									<!-- has an empty string value, say   -->
									<!-- that it's empty                  -->
									<xsl:when test="not(self::*) and .=''">
										<i>
											<xsl:text>(empty)</xsl:text>
										</i>
									</xsl:when>
									<!-- if this node is not an element   -->
									<!-- and has a string value, show the -->
									<!-- string value                     -->
									<xsl:when test="not(self::*)">
										<xsl:text> {</xsl:text>
										<br/>
										<pre>
											<span class="data">
												<xsl:value-of select="."/>
											</span>
										</pre>
										<xsl:text>}</xsl:text>
									</xsl:when>
									<!-- otherwise, it must be an element -->
									<!-- so don't say anything about its  -->
									<!-- string value -->
								</xsl:choose>
							</xsl:if>
							<!-- apply the templates that match each of   -->
							<!-- the current node's child attribute       -->
							<!-- nodes, text nodes, comment nodes, and    -->
							<!-- p.i. nodes. only element nodes will have -->
							<!-- these types of nodes as children, so     -->
							<!-- we're only doing this for elements.      -->
							<xsl:apply-templates select="@*|node()"/>
						<!-- close off the list item for the current      -->
						<!-- node's contents                              -->
						</li>
					<!-- close off the bulleted list and enclosing table  -->
					<!-- for the current node's properties                -->
					</ul>
				</td>
			</tr>
		</table>
	<!-- done with current node-->
	</xsl:template>

</xsl:stylesheet>
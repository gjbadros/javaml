<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               version="1.0">
  <xsl:output method="html" indent="yes"
              doctype-system="svg-19991203.dtd"
              version="1.0"
              encoding="ISO-8859-1" />
  <xsl:strip-space elements="*"/>
  <!--================================================================-->
  <!-- Root Element Template                                          -->
  <!-- This template specifies what creates the root element of the   -->
  <!-- result tree.  In this case, it tells the XSL processor to      -->
  <!-- start with the <sales> element.                                -->
  <!--================================================================-->

  <xsl:template match="/">
    <xsl:apply-templates select="sales"/>
  </xsl:template>


  <!--================================================================-->
  <!-- <sales> template                                               -->
  <!--                                                                -->
  <!-- This template creates the entire SVG bar chart.  The title of  -->
  <!-- the chart comes from the <heading> element, and the size and   -->
  <!-- position of the bars in the graph are calculated by adding the -->
  <!-- <product> elements inside each <region> element.  In addition, -->
  <!-- the labels in the graph legend are generated from the text of  -->
  <!-- the <name> element inside each <region> element.               -->
  <!--                                                                -->
  <!-- To simplify the stylesheet, we hardcoded the scale, the dashed -->
  <!-- lines behind the graph, and other assumptions about how much   -->
  <!-- space we'll need, etc.  You could certainly add more variables -->
  <!-- and parameters to the templates to make the stylesheet more    -->
  <!-- robust....                                                     -->
  <!--================================================================-->

  <xsl:template match="sales">
    <svg width="250" height="300">

  <!--================================================================-->
  <!-- Create the title for the bar chart.  We'll use the text of the -->
  <!-- <heading> element for this.  Before we output the text, we'll  -->
  <!-- set up the SVG <text> element with the appropriate style, x,   -->
  <!-- and y attributes.  We hardcoded the x and y values to simplify -->
  <!-- things; it would complicate the stylesheet significantly to    -->
  <!-- figure out how wide the entire chart needed to be, then anchor -->
  <!-- the title in the middle.                                       -->
  <!--================================================================-->

      <text style="font-size:18; text-anchor:middle" x="120" y="20">
        <xsl:value-of select="caption/heading"/>
      </text>
  
  <!--================================================================-->
  <!-- Now we print the subtitle of the bar chart.  This text doesn't -->
  <!-- change, so we just hardcode the SVG element here.              -->
  <!--================================================================-->

      <text style="font-size:12; text-anchor:middle" 
            y="33" x="120">(in millions of dollars)</text>

  <!--================================================================-->
  <!-- This <g> (group) element sets properties for all elements that -->
  <!-- are output inside it.                                          -->
  <!--================================================================-->


      <g style="stroke:black; stroke-width:2; fillrule:evenodd; 
                font-size:9; text-anchor:middle">

  <!--================================================================-->
  <!-- We hardcode the X and Y axis and the grid lines.  Note that we -->
  <!-- use a gray color and the stroke-dasharray properties to make   -->
  <!-- the lines lighter.  Also, because all SVG drawing operations   -->
  <!-- are opaque by default, we draw the grid lines first.  That     -->
  <!-- means everything we draw later will appear on top of the grid  -->
  <!-- lines.                                                         -->
  <!--                                                                -->
  <!-- A note about the d attribute of the <path> elements:           -->
  <!-- The values here contain move (M) commands, lineto (L) commands,-->
  <!-- and closepath (Z) commands.  The first <path> element here     -->
  <!-- tells the SVG rendering engine to move to x,y coordinate       -->
  <!-- (40, 200), draw a line to (40, 30), then close the path. We'll -->
  <!-- use this syntax throughout the stylesheet.  See the SVG spec   -->
  <!-- for more information about paths and other drawing commands.   -->
  <!--================================================================-->

        <g style="stroke-width:2; fill:black">
          <path d="M 40 220 L  40  30 Z"/>
          <path d="M 40 220 L 200 220 Z"/>
        </g>
        <g style="fill:none; stroke:#B0B0B0; stroke-width:1; 
                  stroke-dasharray:2 4">
          <path d="M 42 200 L 198 200 Z"/>
          <path d="M 42 180 L 198 180 Z"/>
          <path d="M 42 160 L 198 160 Z"/>
          <path d="M 42 140 L 198 140 Z"/>
          <path d="M 42 120 L 198 120 Z"/>
          <path d="M 42 100 L 198 100 Z"/>
          <path d="M 42  80 L 198  80 Z"/>
          <path d="M 42  60 L 198  60 Z"/>
          <path d="M 42  40 L 198  40 Z"/>
        </g>

  <!--================================================================-->
  <!-- We hardcoded the labels here as well.  Each one is written to  -->
  <!-- the left of the grid lines, and is right-justified (that's     -->
  <!-- what text-anchor:end does).                                    -->
  <!--================================================================-->
        <g style="text-anchor:end; font-size:9">
          <text x="36" y="203">20</text>
          <text x="36" y="183">40</text>
          <text x="36" y="163">60</text>
          <text x="36" y="143">80</text>
          <text x="36" y="123">100</text>
          <text x="36" y="103">120</text>
          <text x="36" y="83">140</text>
          <text x="36" y="63">160</text>
          <text x="36" y="43">$180</text>
        </g>

  <!--================================================================-->
  <!-- Now it's time to get to work.  For each region we're going to  -->
  <!-- represent in the bar chart, we'll need several things:         -->
  <!--                                                                -->
  <!--   1) The x-coordinate where the bar should start               -->
  <!--   2) The y-coordinate where the bar should start               -->
  <!--   3) The color we should use to fill in the bar                -->
  <!--   4) The y-coordinate where the legend entry should start.     -->
  <!--                                                                -->
  <!-- We'll use the variables $x-offset, $y-offset, $color, and      -->
  <!-- $y-legend-offset to represent these things.  We'll update      -->
  <!-- these variables during each iteration of the <xsl:for-each>    -->
  <!-- element.                                                       -->
  <!--================================================================-->
  
  <!--================================================================-->
  <!-- $x-offset is initialized to 60, and $y-legend-offset is set to -->
  <!-- 90.  These variables are updated after each <region> element.  -->
  <!--================================================================-->
        <xsl:variable name="x-offset" select="60"/>
        <xsl:variable name="y-legend-offset" select="90"/>

  <!--================================================================-->
  <!-- We use the <for-each> element to loop through all of the       -->
  <!-- instances of the <region> tag.                                 -->
  <!--================================================================-->
        <xsl:for-each select="region">

  <!--================================================================-->
  <!-- We use the position of this region element to determine the    -->
  <!-- color.  The combination of the position() function from XPath  -->
  <!-- and the mod operator lets us cycle through five different      -->
  <!-- colors.  Notice that we had to put the color names in single   -->
  <!-- quotes; otherwise, the XSLT processor looks for <red> elements -->
  <!-- or <yellow> elements, etc.                                     -->
  <!--================================================================-->
          <xsl:choose>
            <xsl:when test="(position() mod 5) = 1">
              <xsl:variable name="color" select="'red'"/>
            </xsl:when>
            <xsl:when test="(position() mod 5) = 2">
              <xsl:variable name="color" select="'yellow'"/>
            </xsl:when>
            <xsl:when test="(position() mod 5) = 3">
              <xsl:variable name="color" select="'purple'"/>
            </xsl:when>
            <xsl:when test="(position() mod 5) = 4">
              <xsl:variable name="color" select="'blue'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="color" select="'green'"/>
            </xsl:otherwise>
          </xsl:choose>

  <!--================================================================-->
  <!-- Now that we've set up all the variables we'll need, we'll      -->
  <!-- invoke the template for the <region> element.                  -->
  <!--================================================================-->
          <xsl:apply-templates select=".">
            <xsl:with-param name="x-offset" select="$x-offset"/>
            <xsl:with-param name="color" select="$color"/>
            <xsl:with-param name="y-legend-offset" 
                            select="$y-legend-offset"/>

  <!--================================================================-->
  <!-- Notice that we just hardcoded $y-offset....                    -->
  <!--================================================================-->
            <xsl:with-param name="y-offset" select="220"/>
          </xsl:apply-templates>

  <!--================================================================-->
  <!-- Now we update the variables before we move forward.  $x-offset -->
  <!-- is incremented by 30 and $y-legend-offset is incremented by 20.-->
  <!--================================================================-->
          <xsl:variable name="x-offset">
            <xsl:value-of select="$x-offset + 30"/>
          </xsl:variable>
          <xsl:variable name="y-legend-offset">
            <xsl:value-of select="$y-legend-offset + 20"/>
          </xsl:variable>
        </xsl:for-each>
      </g>
    </svg>
  </xsl:template>

  <!--================================================================-->
  <!-- <region> template                                              -->
  <!--                                                                -->
  <!-- This template generates the bar chart data for each region.    -->
  <!-- For each region, we have to output four things:                -->
  <!--                                                                -->
  <!--   1) The bar itself.  The bar's height is determined by the    -->
  <!--      sales data from the XML document, and it's fill color is  -->
  <!--      passed in on the $color parameter.                        -->
  <!--   2) The sales total.  This is drawn as text, centered just    -->
  <!--      above the bar.  The value is calculated from the XML.     -->
  <!--   3) The text of the legend.  This is the <name> element.  It  -->
  <!--      is drawn based on the $y-legend-offset.                   -->
  <!--   4) The color bar for the legend.  This is a block filled in  -->
  <!--      based on the $color parameter.  Its position is based on  -->
  <!--      the $y-legend-offset.                                     -->
  <!--================================================================-->
  <xsl:template match="region">

  <!--================================================================-->
  <!-- Our four parameters are the x- and y-offsets where the bar     -->
  <!-- should start, the color used to fill in the bar, and the       -->
  <!-- y-offset where the legend entry for this <region> should start.-->
  <!--================================================================-->
    <xsl:param name="x-offset" select="'60'"/>
    <xsl:param name="y-offset" select="220"/>
    <xsl:param name="color" select="'red'"/>
    <xsl:param name="y-legend-offset" select="90"/>

  <!--================================================================-->
  <!-- We'll calculate the y value for the top of the bar based on    -->
  <!-- the total sales for this <region>.  This value is subtracted   -->
  <!-- from the y-offset from which we started.                       -->
  <!--================================================================-->
    <xsl:variable name="y">
      <xsl:value-of select="$y-offset - sum(product)"/>
    </xsl:variable>

  <!--================================================================-->
  <!-- Task 1:  Draw the bar                                          -->
  <!--                                                                -->
  <!-- For this task, we'll create an SVG <path> element.  We'll set  -->
  <!-- the fill property so the bar will be filled with the correct   -->
  <!-- color.  Notice that fill is a property set with the XML style  -->
  <!-- attribute; SVG does this to be consistent with CSS, even       -->
  <!-- though it creates some confusion as to which things are        -->
  <!-- attributes and which things are properties set in the text of  -->
  <!-- attributes.                                                    -->
  <!--                                                                -->
  <!-- Once we've created the style attribute and set the fill        -->
  <!-- property correctly, we'll create the d attribute.  The d       -->
  <!-- attribute contains five commands:                              -->
  <!--                                                                -->
  <!--   1) Move to the origin of the bar.                            -->
  <!--   2) Draw a line to the upper left corner of the bar.          -->
  <!--   3) Draw a line to the upper right corner of the bar.         -->
  <!--   4) Draw a line to the lower right corner of the bar.         -->
  <!--   5) Close the path.  This draws a line from wherever we are   -->
  <!--      to the start of the path.                                 -->
  <!--                                                                -->
  <!-- The move command is M, the lineto command is L, and the        -->
  <!-- closepath command is Z.                                        -->
  <!--================================================================-->
    <xsl:element name="path">
      <xsl:attribute name="style">
        <xsl:text>fill:</xsl:text>
        <xsl:value-of select="$color"/>
      </xsl:attribute>
      <xsl:attribute name="d">

  <!--================================================================-->
  <!-- Move to the origin of the bar.  The $x-offset parameter is the -->
  <!-- center of the bar, so it begins 10 units to the left of        -->
  <!-- $x-offset ($x-offset - 10), and ends 10 units to the right     -->
  <!-- ($x-offset + 10).  The $y-offset is used as is.                -->
  <!--================================================================-->
        <xsl:text>M </xsl:text>
        <xsl:value-of select="$x-offset - 10"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="$y-offset"/>

  <!--================================================================-->
  <!-- Move to the upper left corner of the bar.  The x dimension is  -->
  <!-- ($x-offset - 10), and the y dimension is the $y variable we    -->
  <!-- calculated earlier.                                            -->
  <!--================================================================-->
        <xsl:text> L </xsl:text>
        <xsl:value-of select="$x-offset - 10"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="$y"/>

  <!--================================================================-->
  <!-- Move to the upper right corner of the bar.  The x dimension is -->
  <!-- ($x-offset + 10), and the y dimension is the $y variable we    -->
  <!-- calculated earlier.                                            -->
  <!--================================================================-->
        <xsl:text> L </xsl:text>
        <xsl:value-of select="$x-offset + 10"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="$y"/>

  <!--================================================================-->
  <!-- Move to the lower right corner of the bar.  The x dimension is -->
  <!-- ($x-offset + 10), and the y dimension is the $y-offset value   -->
  <!-- that was passed in.                                            -->
  <!--================================================================-->
        <xsl:text> L </xsl:text>
        <xsl:value-of select="$x-offset + 10"/>
        <xsl:text> </xsl:text> 
        <xsl:value-of select="$y-offset"/>

  <!--================================================================-->
  <!-- Close the path.  All we need is a Z command.                   -->
  <!--================================================================-->
        <xsl:text> Z</xsl:text>
      </xsl:attribute>
    </xsl:element> 

  <!--================================================================-->
  <!-- Task 2:  Draw the sales total.                                 -->
  <!--                                                                -->
  <!-- For this task, we'll create an SVG <text> element.  It will be -->
  <!-- centered (text-anchor:middle, a property we inherit from an    -->
  <!-- earlier <g> element) and drawn just above the top of the bar.  -->
  <!-- The center of the bar is $x-offset, which we received as a     -->
  <!-- parameter, and the top of the bar is $y.  We'll subtract 5     -->
  <!-- units from $y to calculate its starting point.  The value we   -->
  <!-- print is based on the sum of all <product> elements contained  -->
  <!-- inside this <region>.                                          -->
  <!--================================================================-->
    <xsl:element name="text">
      <xsl:attribute name="x">
        <xsl:value-of select="$x-offset"/>
      </xsl:attribute>
      <xsl:attribute name="y">
        <xsl:value-of select="$y - 5"/>
      </xsl:attribute>
      <xsl:value-of select="sum(product)"/>
    </xsl:element>

  <!--================================================================-->
  <!-- Task 3:  Draw the text of the legend.                          -->
  <!--                                                                -->
  <!-- For this task, we'll use the <name> element contained inside   -->
  <!-- this <region>.  We create an SVG <text> element to do this.    -->
  <!-- The x coordinate of the text is 240, while the y coordinate is -->
  <!-- based on the $y-legend-offset.  We simply fill in the style,   -->
  <!-- x, and y attributes of the <text> tag, then use the value of   -->
  <!-- the <name> element as the text of the new tag.                 -->
  <!--================================================================-->
    <xsl:element name="text">
      <xsl:attribute name="style">
        <xsl:text>font-size:12; text-anchor:start</xsl:text> 
      </xsl:attribute> 
      <xsl:attribute name="x">
        <xsl:text>240</xsl:text> 
      </xsl:attribute>
      <xsl:attribute name="y">
        <xsl:value-of select="$y-legend-offset"/>
      </xsl:attribute>
      <xsl:value-of select="name"/>
    </xsl:element>

  <!--================================================================-->
  <!-- Task 4. Draw the color bar for the legend.                     -->
  <!--                                                                -->
  <!-- For this task, we'll draw a small box that is filled in with   -->
  <!-- the correct color.  The position of the box is based on the    -->
  <!-- hardcoded x coordinate of 220, and the $y-legend-offset we got -->
  <!-- as a parameter.  The box is 10 x 10 units.                     -->
  <!--================================================================-->
    <xsl:element name="path">
      <xsl:attribute name="style">
        <xsl:text>stroke:black; stroke-width:2; fill:</xsl:text> 
        <xsl:value-of select="$color"/>
      </xsl:attribute> 
      <xsl:attribute name="d">
        <xsl:text>M 220 </xsl:text> 
        <xsl:value-of select="$y-legend-offset - 10"/>
        <xsl:text> L 220 </xsl:text> 
        <xsl:value-of select="$y-legend-offset"/>
        <xsl:text> L 230 </xsl:text> 
        <xsl:value-of select="$y-legend-offset"/>
        <xsl:text> L 230 </xsl:text> 
        <xsl:value-of select="$y-legend-offset - 10"/>
        <xsl:text> Z</xsl:text> 
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

</xsl:transform>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
	xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
	xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
	xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
	extension-element-prefixes	= "gui"
	exclude-result-prefixes		= "#all"
	expand-text					= "yes">
	
	
	<xsl:variable name="OPTION_FIRST_CHILD"		as="xs:string" select="'First Child'"/>
	<xsl:variable name="OPTION_NEXT_SIBLING"	as="xs:string" select="'Next Sibling'"/>
	<xsl:variable name="OPTION_ABORT"			as="xs:string" select="'Abort'"/>
	
	<xsl:template match="/">
		
		<xsl:call-template name="Traverse">
			<xsl:with-param name="Element" select="*"/>
		</xsl:call-template>
		
		<xsl:message>Done!</xsl:message>
		
	</xsl:template>
	
	<xsl:template name="Traverse">
		<xsl:param name="Element" as="element()"/>
		
		<xsl:variable name="OptionList" as="xs:string+">
			<xsl:if test="exists($Element/child::*)">{$OPTION_FIRST_CHILD}</xsl:if>
			<xsl:if test="exists($Element/following-sibling::*)">{$OPTION_NEXT_SIBLING}</xsl:if>
			{$OPTION_ABORT}
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="count($OptionList) le 1">
				
				<gui:message-dialog title="Option-Dialog Test" icon="warning">
					<xsl:text>The name of the current element is '{name($Element)}'.&#x0A;</xsl:text>
					<xsl:text>The end of this traversal is reached.</xsl:text> 
				</gui:message-dialog>
				
			</xsl:when>
			<xsl:otherwise>
				
				<xsl:variable name="Selection" as="xs:integer">
					<gui:option-dialog title="Option-Dialog Test" icon="question" options="$OptionList">
						<xsl:text>The name of the current element is '{name($Element)}'.&#x0A;</xsl:text>
						<xsl:text>Where to go next?</xsl:text> 
					</gui:option-dialog>
				</xsl:variable>
				
				<xsl:choose>
					<xsl:when test="$OptionList[$Selection] = $OPTION_FIRST_CHILD">
						<xsl:call-template name="Traverse">
							<xsl:with-param name="Element" select="$Element/child::*[1]"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$OptionList[$Selection] = $OPTION_NEXT_SIBLING">
						<xsl:call-template name="Traverse">
							<xsl:with-param name="Element" select="$Element/following-sibling::*[1]"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
				
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
	xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
	xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
	xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
	extension-element-prefixes	= "gui"
	exclude-result-prefixes		= "#all"
	expand-text					= "yes">
	
	
	
	<xsl:template match="/">
		<xsl:variable name="var0" select="10"/>
		<xsl:variable name="x">
			<x>
				<xsl:variable name="var1" select="1"/>
				<xsl:variable name="var2" select="2"/>
				<y>
					<xsl:text>result: {$var0 + $var1 + $var2}</xsl:text>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="$var0"/>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="$var1"/>
				</y>
			</x>
		</xsl:variable>
		<gui:message-dialog title="Test" icon="warning" text="{$x}"/>
		
		<xsl:message>Done!</xsl:message>
		
	</xsl:template>
	
</xsl:stylesheet>
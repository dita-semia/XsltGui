<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
	xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
	xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
	xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
	extension-element-prefixes	= "gui"
	exclude-result-prefixes		= "#all"
	expand-text					= "yes">
	
	<xsl:template match="/">
		
		<xsl:variable name="var1" as="element()">
			<var1>Text</var1>
		</xsl:variable>
		<xsl:variable name="var2" as="element()">
			<var2>Text</var2>
		</xsl:variable>
		<xsl:variable name="root" as="element()">
			<root>
				<xsl:sequence select="$var1"/>
				<xsl:sequence select="$var2"/>
			</root>
		</xsl:variable>
		
		<xsl:message>
			<gui:test select="$root"/>	
		</xsl:message>
		
		<xsl:message>
			<gui:test>
				<root>
					<xsl:sequence select="$var1"/>
					<xsl:sequence select="$var2"/>
				</root>
			</gui:test>
		</xsl:message>
		
		<xsl:message>Done!</xsl:message>
		
	</xsl:template>

</xsl:stylesheet>
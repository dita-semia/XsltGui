<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
	xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
	xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
	xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
	extension-element-prefixes	= "gui"
	exclude-result-prefixes		= "#all"
	expand-text					= "yes">
	
	<xsl:template match="/">
		
		<gui:message-dialog title="Message-Dialog Test" icon="info">
			<xsl:text>The name of the root element is '{name(*)}'.</xsl:text>
		</gui:message-dialog>
		
		<xsl:message>Done!</xsl:message>
		
	</xsl:template>

</xsl:stylesheet>
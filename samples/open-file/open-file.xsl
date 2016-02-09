<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
    xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
    xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
    xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
    extension-element-prefixes	= "gui"
    
    expand-text					= "yes">
    
    <xsl:template match="/">
    	
    	<gui:open-file url="{resolve-uri('test.xml')}" view="text"/>
    	
    	<gui:open-file url="{resolve-uri('test.xhtml')}" view="author"/>
        
        <xsl:message>Done!</xsl:message>
        
    </xsl:template>
    
</xsl:stylesheet>
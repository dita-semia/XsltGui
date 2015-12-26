<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
    xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
    xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
    xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
    extension-element-prefixes	= "gui"
    
    expand-text					= "yes">
    
    <xsl:template match="/">
        
        <xsl:variable name="html1" as="element()" select="doc(resolve-uri('TFS-Report.xhtml'))/*"/>
        
        <xsl:variable name="html2" as="element()" select="doc(resolve-uri('html-dialog.xhtml'))/*"/>
        
        <xsl:message>
            <gui:html-dialog html="$html2" title="Titel ..." size="(600, 400)"/>
            <!--<gui:html-dialog html="$html2" title="Titel ..." size="(300, 200)"/>-->
        </xsl:message>
        
        <!--<xsl:message>
            <gui:html-dialog>
                <root>
                    <xsl:sequence select="$var1"/>
                    <xsl:sequence select="$var2"/>
                </root>
            </gui:html-dialog>
        </xsl:message>-->
        
        <xsl:message>Done!</xsl:message>
        
    </xsl:template>
    
</xsl:stylesheet>
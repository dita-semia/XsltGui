<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl                   = "http://www.w3.org/1999/XSL/Transform"
    xmlns:xs                    = "http://www.w3.org/2001/XMLSchema"
    xmlns:gui                   = "http://www.dita-semia.org/xslt-gui"
    extension-element-prefixes  = "gui"
    exclude-result-prefixes     = "xs"
    version                     = "2.0">
	
	<xsl:variable name="FILENAME_TEMP" 		as="xs:string" select="'temp.xhtml'"/>
	<xsl:variable name="FILENAME_RESULT" 	as="xs:string" select="'result.xhtml'"/>
	
	<xsl:variable name="BUTTON_OK" 			as="xs:string" select="'OK'"/>
	<xsl:variable name="BUTTON_ABORT" 		as="xs:string" select="'Abort'"/>
    
    <xsl:template match="/">
    	
    	<xsl:variable name="content" as="element()*">
    		<p>Timestamp: &lt;<xsl:value-of select="current-dateTime()"/>&gt;</p>
    		<ul>
	    		<xsl:for-each select="1 to 100">
	    			<li>Data #<xsl:value-of select="."/></li>
	    		</xsl:for-each>
    		</ul>
    	</xsl:variable>
    	
    	<xsl:variable name="tempPath" select="$FILENAME_TEMP"/>
    	<xsl:result-document href="{$tempPath}" method="xhtml">
    		<html>
    			<head>
    				<title>Result ***TEMP***</title>
    			</head>
    			<body>
    				<xsl:copy-of select="$content"/>
    			</body>
    		</html>
        </xsl:result-document>
    	<gui:open-file url="{$tempPath}" view="author"/>
    	
    	<xsl:variable name="htmlResult" as="element()*">
    		<xsl:variable name="tempPath" select="$FILENAME_TEMP"/>
    		<gui:html-dialog 
    				title			= "Check Result" 
    				buttons			= "($BUTTON_OK, $BUTTON_ABORT)" 
    				properties-key	= "open-generated-file" 
    				block-parent	= "no">
    			<center>Is the result OK?</center>
    		</gui:html-dialog>
    	</xsl:variable>


    	<xsl:choose>
    		<xsl:when test="$htmlResult/self::button = $BUTTON_OK">
    			
    			<xsl:variable name="resultPath" select="$FILENAME_RESULT"/>
    			<xsl:result-document href="{$resultPath}" method="xhtml">
    				<html>
    					<head>
    						<title>Result</title>
    					</head>
    					<body>
    						<xsl:copy-of select="$content"/>
    					</body>
    				</html>
    			</xsl:result-document>
    			<gui:open-file url="{$resultPath}" view="author"/>
				<xsl:message>Done!</xsl:message>
    			
    		</xsl:when>
    		<xsl:otherwise>
    			
    			<xsl:message>Abort by user!</xsl:message>
    			
    		</xsl:otherwise>
    	</xsl:choose>

    </xsl:template>
    
</xsl:stylesheet>
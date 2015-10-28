<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
	xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
	xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
	xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
	xmlns:ds	= "http://www.dita-semia.org"
	extension-element-prefixes	= "gui"
	exclude-result-prefixes		= "#all"
	expand-text					= "yes">
	
	
	<xsl:template match="/">
		<xsl:variable name="resultUrl" as="xs:anyURI" select="resolve-uri('Result.xml')"/>
		
		<xsl:if test="ds:fileExists($resultUrl)">
			
			<xsl:variable name="decision" as="xs:integer">
				<gui:option-dialog 
					title	= "Overwrite File?"
					text	= "The file '{$resultUrl}' already exists."
					icon	= "warning" 
					options	= "('Overwrite', 'Abort')"
					default = "1"/>
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="$decision = 2">
					<xsl:message terminate="yes">Abort by user.</xsl:message>
				</xsl:when>
				<xsl:when test="not(ds:canOverwrite($resultUrl))">
					<xsl:message terminate="yes">Can't overwrite file '{$resultUrl}'.</xsl:message>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		
		<xsl:result-document href="{$resultUrl}" method="xml">
			<xsl:sequence select="."/>
		</xsl:result-document>
		
		<xsl:message>Content written to '{$resultUrl}'.</xsl:message>
		
	</xsl:template>
	
	
	<xsl:function name="ds:canOverwrite" as="xs:boolean">
		<xsl:param name="url" as="xs:anyURI"/>
		
		<xsl:choose>
			<xsl:when test="not(ds:fileCanWrite($url))">
				
				<xsl:variable name="decision" as="xs:integer">
					<!-- 
						WARNING: 
						When the instruction is used without any variables the result is cached by saxon.
						Thus, the dialog won't be opened again resulting in an infinite recursion.
					-->
					<gui:option-dialog 
						title	= "Overwrite File?"
						text	= "The file '{$url}' is read-only."
						icon	= "warning" 
						options	= "('Retry', 'Abort')"
						default = "2"/>
				</xsl:variable>
				
				<xsl:choose>
					<xsl:when test="$decision = 2">
						<!-- Abort! -->
						<xsl:sequence select="false()"/>		
					</xsl:when>
					<xsl:otherwise>
						<!-- Check again -->
						<xsl:sequence select="ds:canOverwrite($url)"/>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:when>
			<xsl:otherwise>
				<!-- File is writable - Proceed! -->
				<xsl:sequence select="true()"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>


	<xsl:function name="ds:fileExists" as="xs:boolean">
		<xsl:param name="url" as="xs:anyURI"/>
		<xsl:sequence select="file:exists(file:new($url))" xmlns:file="java.io.File"/>
	</xsl:function>
	
	
	<xsl:function name="ds:fileCanWrite" as="xs:boolean">
		<xsl:param name="url" as="xs:anyURI"/>
		<xsl:sequence select="file:canWrite(file:new($url))" xmlns:file="java.io.File"/>
	</xsl:function>
	

</xsl:stylesheet>
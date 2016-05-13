<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
	xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
	xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
	exclude-result-prefixes="#all"
	version="2.0">
	
	<xsl:variable name="BUTTON_OK" 		as="xs:string" select="'OK'"/>
	<xsl:variable name="BUTTON_CANCEL" 	as="xs:string" select="'Cancel'"/>
	
	<xsl:template match="/">
		
		<xsl:copy>	<!-- keep the document-node -->
			<xsl:for-each select="*">
				<xsl:copy>	<!-- keep the root element -->
					
					<xsl:sequence select="attribute()"/>	<!-- always keep the attributes -->
					
					<xsl:variable name="htmlResult">
						<gui:html-dialog title="HTML Dialog">
							<table>
								<tr>
									<td>Edit field 1</td>
									<td>
										<input type="text" name="input1" size="20" value="Text"/>
									</td>
								</tr>
								<tr>
									<td>Edit field 2</td>
									<td>
										<input type="text" name="input2" size="20" value="More Text"/>
									</td>
								</tr>
								<tr>
									<td>Checkbox 1</td>
									<td>
										<input type="checkbox" name="checkbox1" checked="1"/>
									</td>
								</tr>
								<tr>
									<td>Checkbox 2</td>
									<td>
										<input type="checkbox" name="checkbox2"/>
									</td>
								</tr>
								<tr>
									<td>Radiobutton 1</td>
									<td>
										<input type="radio" name="radio" value="radio1" checked="1"/>
									</td>
								</tr>
								<tr>
									<td>Radiobutton 2</td>
									<td>
										<input type="radio" name="radio" value="radio2"/>
									</td>
								</tr>
								<tr>
									<td>Combobox</td>
									<td>
										<select name="combobox"> 
											<option>Option 1</option>
											<option selected="1">Option 2</option>
											<option>Option 3</option>
										</select>
									</td>
								</tr>
							</table><!--
							<p>Edit the text:</p>
							<input type="text" name="text" size="50" value="{.}"/>-->
						</gui:html-dialog>
					</xsl:variable>
					
					<xsl:choose>
						<xsl:when test="$htmlResult/button = $BUTTON_OK">
							<xsl:value-of select="$htmlResult/text"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:sequence select="node()"/> <!-- Keep the current content. -->
						</xsl:otherwise>
					</xsl:choose>
					
				</xsl:copy>
			</xsl:for-each>
		</xsl:copy>
		
	</xsl:template>
	
	<!-- 
		the following code is not working, but why???
		behavior: the first xsl:when within xsl:choose won't be used when pressing OK!?
	-->
	<xsl:template name="editTextContent2">
		<xsl:variable name="htmlResult" as="element()*">
			<gui:html-dialog title="HTML Dialog" size="(200, 130)">
				<p>Edit the text:</p>
				<input type="text" name="text" size="200" value="{.}"/>
			</gui:html-dialog>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$htmlResult/self::button = $BUTTON_OK">
				<xsl:value-of select="$htmlResult/self::text"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="node()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
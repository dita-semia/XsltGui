<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl	= "http://www.w3.org/1999/XSL/Transform"
    xmlns:xs	= "http://www.w3.org/2001/XMLSchema"
    xmlns:gui	= "http://www.dita-semia.org/xslt-gui"
    extension-element-prefixes	= "gui">
    
	<xsl:template match="/">
        
        <xsl:variable name="htmlResult" as="document-node()">
        	<gui:html-dialog title="HTML Dialog" block-parent="yes" resizable="true">
        		<form action="#">
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
        			</table>
        		</form>
        	</gui:html-dialog>
        </xsl:variable>
    	
    	<xsl:for-each select="$htmlResult/*">
    		<xsl:message select="."/>	
    	</xsl:for-each>
        
        
        <xsl:message>Done!</xsl:message>
        
    </xsl:template>
    
</xsl:stylesheet>
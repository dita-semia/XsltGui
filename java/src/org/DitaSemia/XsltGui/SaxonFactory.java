/*
 * This file is part of the DITA-SEMIA project (www.dita-semia.org).
 * See the accompanying LICENSE file for applicable licenses.
 */

package org.DitaSemia.XsltGui;

import net.sf.saxon.style.StyleElement;

import com.saxonica.xsltextn.ExtensionElementFactory;

public class SaxonFactory implements ExtensionElementFactory {
	
	@Override
	public Class<? extends StyleElement> getExtensionClass(String localname) {
		if 		(localname.equals("message-dialog")) 		return GuiMessageDialog.class;
		else if (localname.equals("option-dialog")) 		return GuiOptionDialog.class;
		else return null;
	}

}

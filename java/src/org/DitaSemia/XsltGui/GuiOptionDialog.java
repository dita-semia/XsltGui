/*
 * This file is part of the DITA-SEMIA project (www.dita-semia.org).
 * See the accompanying LICENSE file for applicable licenses.
 */

package org.DitaSemia.XsltGui;

import java.awt.Frame;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import net.sf.saxon.expr.*;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.style.Compilation;
import net.sf.saxon.style.ComponentDeclaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BigIntegerValue;

public class GuiOptionDialog extends ExtensionInstruction {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiOptionDialog.class.getName());
	
	Expression title;
	Expression text;
	Expression options;
	Expression defaultOption;
	Expression icon;

	@Override
    public void prepareAttributes() throws XPathException {
    	
    	// notwendiges title-Attribut
    	final String titleAtt = getAttributeValue("", "title");
        if (titleAtt == null) {
            reportAbsence("title");
        } else {
        	title = makeAttributeValueTemplate(titleAtt);
        }
        
    	// optionales text-Attribut
    	String textAtt = getAttributeValue("", "text");
        if (textAtt!= null) {
        	text = makeAttributeValueTemplate(textAtt);
        } else {
        	text = null;
        }

    	// optionales options-Attribut
    	String optionsAtt = getAttributeValue("", "options");
        if (optionsAtt == null) {
        	optionsAtt = "";
        }
        options = makeExpression(optionsAtt);

    	// optionales defaultButton-Attribut
    	String defaultAtt = getAttributeValue("", "default");
        if (defaultAtt == null) {
        	defaultAtt = "0";
        }
        defaultOption = makeAttributeValueTemplate(defaultAtt);

        // optionales icon-Attribut
    	String iconAtt = getAttributeValue("", "icon");
        if (iconAtt == null) {
        	iconAtt = GuiMessageDialog.ICON_PLAIN;
        }
        icon = makeAttributeValueTemplate(iconAtt);
    }

	@Override
    public void validate(ComponentDeclaration decl) throws XPathException {
        super.validate(decl);
        title 			= typeCheck("title", 	title);
        options 		= typeCheck("buttons", 	options);
        defaultOption 	= typeCheck("default",	defaultOption);

        if (text != null) {
        	text 	= typeCheck("text",		text);
        }

		if ((text != null) && (hasChildNodes())) {
			compileError("When the text attribute is present no child nodes are allowed.");
        } else if ((text == null) && (!hasChildNodes())) {
        	compileError("When there are no child nodes the text attribute needs to be present.");
        }
    }

	@Override
    public Expression compile(Compilation exec, ComponentDeclaration decl) throws XPathException {
		if (text == null) {
			text = compileSequenceConstructor(exec, decl, iterateAxis(AxisInfo.CHILD), false);
		}
		
        return new OptionDialogInstruction(title, text, options, defaultOption, icon);
    }

	private static class OptionDialogInstruction extends SimpleExpression {

        public static final int TITLE	= 0;
        public static final int TEXT	= 1;
        public static final int OPTIONS	= 2;
        public static final int DEFAULT	= 3;
        public static final int ICON	= 4;

        public OptionDialogInstruction(Expression title, Expression text, Expression options, Expression defaultOption, Expression icon) {
			Expression[] subs = {title, text, options, defaultOption, icon};
        	setArguments(subs);
        }

        public int computeCardinality() {
            return StaticProperty.EXACTLY_ONE;
        }

        public String getExpressionType() {
            return "gui:option-dialog";
        }

        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
            final String titleString 	= arguments[TITLE	].head().getStringValue();
            final String defaultString 	= arguments[DEFAULT	].head().getStringValue();
            final String iconString 	= arguments[ICON].head().getStringValue();

            String textString = "";
            SequenceIterator iterator = arguments[TEXT].iterate();
			Item item = iterator.next();
			while (item != null) {
				textString += item.getStringValue();
				item = iterator.next();
			}
            
            SequenceIterator optionsIterator	= arguments[OPTIONS].iterate();
            List<String> optionList = new LinkedList<String>();
            Item optionItem = optionsIterator.next();
            while (optionItem != null) {
            	optionList.add(optionItem.getStringValue());
            	optionItem = optionsIterator.next();
            }
            
            if ((optionList.size() < 2) || (optionList.size() > 3)) {
            	throw new XPathException("The options attribute needs to contain a sequence of 2 or 3 items.");
            }
            String defaultOption = null;
            if (!defaultString.isEmpty())
            {
	            try
	            {
	            	final int defaultIndex = Integer.parseInt(defaultString);
	            	defaultOption = optionList.get(defaultIndex + 1);
	            }
	            catch (Exception e)
	            {
	            	throw new XPathException("The default attribute needs to be empty or contain an integer value between one and the number of options. (" + optionList.size() + ")");	
	            }
            }
            
            final int result = JOptionPane.showOptionDialog(
            		(Frame)PluginWorkspaceProvider.getPluginWorkspace().getParentFrame(),
            		textString,
            	    titleString,
            	    (optionList.size() == 2) ?  JOptionPane.YES_NO_OPTION : JOptionPane.YES_NO_CANCEL_OPTION,
            	    GuiMessageDialog.getMessageType(iconString),
            	    null,
            	    optionList.toArray(new String[optionList.size()]),
            	    defaultOption);

            return new BigIntegerValue(result + 1);
        }
    }
}

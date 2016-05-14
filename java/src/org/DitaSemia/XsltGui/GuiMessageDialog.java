/*
 * This file is part of the DITA-SEMIA project (www.dita-semia.org).
 * See the accompanying LICENSE file for applicable licenses.
 */

package org.DitaSemia.XsltGui;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import net.sf.saxon.expr.*;
import net.sf.saxon.expr.instruct.Executable;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.style.Declaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;

@SuppressWarnings({ "serial", "unchecked" })
public class GuiMessageDialog extends ExtensionInstruction {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiMessageDialog.class.getName());
	
	public final static String ICON_PLAIN		= "plain";
	public final static String ICON_ERROR		= "error";
	public final static String ICON_INFO		= "info";
	public final static String ICON_WARNING		= "warning";
	public final static String ICON_QUESTION	= "question";

	Expression title;
	Expression text;
	Expression icon;

	@Override
    public void prepareAttributes() throws XPathException {
    	
    	// mandatory title attribute
    	final String titleAtt = getAttributeValue("", "title");
        if (titleAtt == null) {
            reportAbsence("title");
        } else {
        	title = makeAttributeValueTemplate(titleAtt);
        }
        
    	// optional text attribute
    	final String textAtt = getAttributeValue("", "text");
        if (textAtt != null) {
        	text = makeAttributeValueTemplate(textAtt);
        } else {
        	text = null;
        }

        // optional icon attribute
    	String iconAtt = getAttributeValue("", "icon");
        if (iconAtt == null) {
        	iconAtt = ICON_PLAIN;
        }
        icon = makeAttributeValueTemplate(iconAtt);
    }

	@Override
    public void validate(Declaration decl) throws XPathException {
        super.validate(decl);
        title	= typeCheck("title", 	title);
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
    public Expression compile(Executable exec, Declaration decl) throws XPathException {
		if (text == null) {
			text = compileSequenceConstructor(exec, decl, iterateAxis(AxisInfo.CHILD), false);
		}
		
        return new MessageDialogInstruction(title, text, icon);
    }

	private static class MessageDialogInstruction extends SimpleExpression {

        public static final int TITLE	= 0;
        public static final int TEXT	= 1;
        public static final int ICON	= 2;
        
		public MessageDialogInstruction(Expression title, Expression text, Expression icon) {
			Expression[] subs = {title, text, icon};
        	setArguments(subs);
        }

    	@Override
        public int computeCardinality() {
            return StaticProperty.EXACTLY_ONE;
        }

    	@Override
        public String getExpressionType() {
            return "gui:message-dialog";
        }

    	@Override
        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
            final String titleString 	= arguments[TITLE].head().getStringValue();
            final String iconString 	= arguments[ICON].head().getStringValue();
            
            String textString = "";
            SequenceIterator<? extends Item> iterator = arguments[TEXT].iterate();
			Item item = iterator.next();
			while (item != null) {
				textString += item.getStringValue();
				item = iterator.next();
			}

            JOptionPane.showMessageDialog(
            		(Frame)PluginWorkspaceProvider.getPluginWorkspace().getParentFrame(),
            		textString,
            	    titleString,
            	    getMessageType(iconString));
            
    		return EmptySequence.getInstance();
        }
    }

	public static int getMessageType(String iconString) throws XPathException {
		if (iconString.equals(ICON_PLAIN)) 			return JOptionPane.PLAIN_MESSAGE;
		else if (iconString.equals(ICON_ERROR)) 	return JOptionPane.ERROR_MESSAGE;
		else if (iconString.equals(ICON_INFO)) 		return JOptionPane.INFORMATION_MESSAGE;
		else if (iconString.equals(ICON_WARNING)) 	return JOptionPane.WARNING_MESSAGE;
		else if (iconString.equals(ICON_QUESTION)) 	return JOptionPane.QUESTION_MESSAGE;
		else {
			throw new XPathException("Invalid value for icon attribute: '" + iconString + "'. Valid values are: " + ICON_PLAIN + ", " + ICON_ERROR + ", " + ICON_INFO + ", " + ICON_WARNING + " and " + ICON_QUESTION + ".");
		}
	}
}

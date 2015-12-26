/*
 * This file is part of the DITA-SEMIA project (www.dita-semia.org).
 * See the accompanying LICENSE file for applicable licenses.
 */

package org.DitaSemia.XsltGui;

import org.apache.log4j.Logger;

import net.sf.saxon.event.SequenceOutputter;
import net.sf.saxon.expr.*;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NoNamespaceName;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.style.Compilation;
import net.sf.saxon.style.ComponentDeclaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Untyped;
import net.sf.saxon.value.StringValue;

public class GuiTest extends ExtensionInstruction {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiTest.class.getName());

	Expression select;

	@Override
	protected void prepareAttributes() throws XPathException {

    	// optional select attribute
    	String selectAtt = getAttributeValue("", "select");
        if (selectAtt!= null) {
        	select = makeExpression(selectAtt);
        } else {
        	select = null;
        }
	}


	@Override
    public void validate(ComponentDeclaration decl) throws XPathException {
        super.validate(decl);

        if (select != null) {
        	select 	= typeCheck("select",	select);
        }

		if ((select != null) && (hasChildNodes())) {
			compileError("When the select attribute is present no child nodes are allowed.");
        } else if ((select == null) && (!hasChildNodes())) {
        	compileError("When there are no child nodes the select attribute needs to be present.");
        }
    }
	
	
	@Override
    public Expression compile(Compilation exec, ComponentDeclaration decl) throws XPathException {

		if (select == null) {
			select = compileSequenceConstructor(exec, decl, iterateAxis(AxisInfo.CHILD), false);
		}
		
        return new TestInstruction(select);
    }

	
	private static class TestInstruction extends SimpleExpression {

        public static final int HTML	= 0;
        
		public TestInstruction(Expression select) {
			Expression[] subs = {select};
        	setArguments(subs);
		}
		
        public int computeCardinality() {
            return StaticProperty.EXACTLY_ONE;
        }

        public String getExpressionType() {
            return "gui:test";
        }

        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
        	
        	String inputString = "";		
        	SequenceIterator selectIterator	= arguments[HTML].iterate();
            Item item = selectIterator.next();
            while (item != null) {
            	if (item instanceof StringValue) {		
            		inputString += item.getStringValue();
				} else if (item instanceof NodeInfo) {
					inputString += QueryResult.serialize((NodeInfo)item);
				} else {
					throw new XPathException("Can't serialize item: " + item.getClass()); 
				}
            	item = selectIterator.next();
            }
        	
        	final SequenceOutputter out = context.getController().allocateSequenceOutputter(50);
        	
        	out.startElement(new NoNamespaceName("Input"), Untyped.getInstance(), locationId, 0);
        	out.characters(inputString, locationId, 0);
        	out.endElement();
        	
        	out.startElement(new NoNamespaceName("Result"), Untyped.getInstance(), locationId, 0);
        	out.characters("Hello, world!", locationId, 0);
        	out.endElement();
        	
        	return SequenceTool.toLazySequence(out.iterate());
        }
    }
}

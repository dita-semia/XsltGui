/*
 * This file is part of the DITA-SEMIA project (www.dita-semia.org).
 * See the accompanying LICENSE file for applicable licenses.
 */

package org.DitaSemia.XsltGui;

import java.awt.Frame;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JToggleButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML.Attribute;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sf.saxon.event.SequenceOutputter;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.Executable;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.NoNamespaceName;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.s9api.BuildingStreamWriterImpl;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.style.Declaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Untyped;
import net.sf.saxon.value.EmptySequence;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

import com.sun.javafx.application.PlatformImpl;

@SuppressWarnings({ "serial", "unchecked" })
public class GuiTest extends ExtensionInstruction {

//	@SuppressWarnings("unused")
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
    public void validate(Declaration decl) throws XPathException {
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
    public Expression compile(Executable exec, Declaration decl) throws XPathException {

		if (select == null) {
			select = compileSequenceConstructor(exec, decl, iterateAxis(AxisInfo.CHILD), false);
		}
		
        return new TestInstruction(select);
    }

	
	private static class TestInstruction extends SimpleExpression {

//        public static final int HTML	= 0;
        
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

        	final Processor 		processor 	= new Processor(context.getConfiguration());
			final DocumentBuilder 	builder 	= processor.newDocumentBuilder();
			
			try {
				BuildingStreamWriterImpl writer = builder.newBuildingStreamWriter();

				writer.writeStartElement("test1");
				writer.writeCharacters("text1");
				writer.writeEndElement();
				
				writer.writeStartElement("test2");
				writer.writeCharacters("text2");
				writer.writeEndElement();
				
				return writer.getDocumentNode().getUnderlyingValue();
				
			} catch (SaxonApiException | XMLStreamException e) {
				logger.error(e, e);
				throw new XPathException("Failed to create return value: " + e.getMessage());
			}
        }
			
		public Sequence call2(XPathContext context, Sequence[] arguments) throws XPathException {
        	
        	final SequenceOutputter out = context.getController().allocateSequenceOutputter(50);
        	
        	out.startElement(new NoNamespaceName("test1"), Untyped.getInstance(), locationId, 0);
        	out.startContent();
			out.characters("text1", locationId, 0);
			out.endElement();
			
			out.startElement(new NoNamespaceName("test2"), Untyped.getInstance(), locationId, 0);
			out.startContent();
			out.characters("text2", locationId, 0);
			out.endElement();
			
			return out.getSequence();
		}
		
		public Sequence call3(XPathContext context, Sequence[] arguments) throws XPathException {
			
        	logger.info("call");
        	try {
            FXThread fxThread = new FXThread();
            Platform.setImplicitExit(false);
            PlatformImpl.startup(fxThread);
            fxThread.join();
//            Platform.runLater(fxThread);
        	} catch (Exception e) {
        		logger.error(e, e);
        	}
	        return EmptySequence.getInstance();
        	
//        	String inputString = "";		
//        	SequenceIterator selectIterator	= arguments[HTML].iterate();
//            Item item = selectIterator.next();
//            while (item != null) {
//            	if (item instanceof StringValue) {		
//            		inputString += item.getStringValue();
//				} else if (item instanceof NodeInfo) {
//					inputString += QueryResult.serialize((NodeInfo)item);
//				} else {
//					throw new XPathException("Can't serialize item: " + item.getClass()); 
//				}
//            	item = selectIterator.next();
//            }
//        	
//        	final SequenceOutputter out = context.getController().allocateSequenceOutputter(50);
//        	
//        	out.startElement(new NoNamespaceName("Input"), Untyped.getInstance(), locationId, 0);
//        	out.characters(inputString, locationId, 0);
//        	out.endElement();
//        	
//        	out.startElement(new NoNamespaceName("Result"), Untyped.getInstance(), locationId, 0);
//        	out.characters("Hello, world!", locationId, 0);
//        	out.endElement();
//        	
//        	return SequenceTool.toLazySequence(out.iterate());
        }
    }
	
	private static class FXThread extends Thread {
//		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(FXThread.class.getName());
		
		public FXThread() {
			
		}

		@Override
		public void run() {
			try{ 
			final Stage primaryStage 		= new Stage();
			primaryStage.getIcons().add(new Image("file:C:/Program Files/Oxygen XML Editor 17.0/Oxygen128.png"));
    		HBox 		hbox		= new HBox(10);
			hbox.setPadding(new Insets(10, 10, 10, 10));
			VBox 		vbox 		= new VBox(10);
			WebView 	webView 	= new WebView();
			ScrollPane 	scrollPane	= new ScrollPane();
            WebEngine 	webEngine 	= webView.getEngine();
            scrollPane.setContent(webView);
            webEngine.setJavaScriptEnabled(true);
            webEngine.loadContent(	"<form action=\"#\">" + 
            							"Input 1: <input name=\"input1\" type=\"text\"/><br/>" +
										"Input 2: <input name=\"input2\" type=\"text\"/><br/>" +
										"<input type=\"radio\" name=\"radio1\">Radio 1</input>" +
										"<input type=\"radio\" name=\"radio2\">Radio 2</input><br/>" +
										"<input type=\"checkbox\" name=\"checkbox1\" value=\"checkbox1\">Checkbox 1</input>" +
										"<input type=\"checkbox\" name=\"checkbox2\" value=\"checkbox2\">Checkbox 2</input><br/>" +
										"<select name=\"combobox\"> " +
											"<option>Option 1</option>" +
											"<option>Option 2</option>" +
										"</select><br/>" +
										"<input type=\"submit\" value=\"Confirm\"/>" +
										"<input type=\"reset\" value=\"Reset\"/>" +
									"<form>"); 
			final Button button1 = new Button("Button 1");
			final Button button2 = new Button("Button 2");
			hbox.getChildren().add(button1);
			hbox.getChildren().add(button2);
			button1.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
					logger.info("button.getText(): " + button1.getText());
					primaryStage.close();
					
			    }
			});
			button2.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
					logger.info("button.getText(): " + button2.getText());
					primaryStage.close();
			    }
			});
            vbox.getChildren().addAll(webView, hbox);
            Scene 		scene 		= new Scene(vbox);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
            primaryStage.setAlwaysOnTop(true);
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.show();
			} catch (Exception e) {
				logger.error(e, e);
			}
		}
	}
}

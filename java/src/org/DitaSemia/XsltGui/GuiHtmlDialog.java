package org.DitaSemia.XsltGui;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import net.sf.saxon.event.SequenceOutputter;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
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

public class GuiHtmlDialog extends ExtensionInstruction {

//	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiHtmlDialog.class.getName());
	
	Expression html;
	Expression size;
	Expression title;

	@Override
    public void prepareAttributes() throws XPathException {
//        logger.info("prepareAttributes");
        //required title attribute
        String titleAtt = getAttributeValue("", "title");
        if (titleAtt != null) {
        	title = makeAttributeValueTemplate(titleAtt);
        } else {
        	reportAbsence(titleAtt);
        }
//        logger.info("title: " + title);
        
        //optional size attribute
        String sizeAtt = getAttributeValue("", "size");
        if (sizeAtt != null) {
        	size = makeAttributeValueTemplate(sizeAtt);
        } else {
        	size = null;
        }
//        logger.info("size: " + size);
		
		//optional html attribute
		String htmlAtt = getAttributeValue("", "html");
        if (htmlAtt!= null) {
        	html = makeExpression(htmlAtt);
        } else {
        	html = null;
        }
//        logger.info("html: " + html);
        
        //properties-key -> letzte Groesse in den Oxygen-Properties speichern; Beim oeffnen alte groesse uebernehmen
        
        //buttons -> Beliebig lange Liste mit Texten, die automatisch am Ende als buttons im Dialog eingefuegt werden. In der CML-Rueckgabe wird der Text des
        //ausgewaehlten Buttons im Tag <Button> angegeben.
    }

	@Override
    public void validate(ComponentDeclaration decl) throws XPathException {
//		logger.info("validate");
        super.validate(decl);
        title 	= typeCheck("title", 	title);
        //TODO ??
        size 	= typeCheck("size", 	size);
        
        if (html != null) {
        	html  = typeCheck("text", html);
        }

		if ((html != null) && (hasChildNodes())) {
			compileError("When the text attribute is present no child nodes are allowed.");
        } else if ((html == null) && (!hasChildNodes())) {
        	compileError("When there are no child nodes the text attribute needs to be present.");
        }
    }

	@Override
    public Expression compile(Compilation exec, ComponentDeclaration decl) throws XPathException {
		if (html == null) {
			html = compileSequenceConstructor(exec, decl, iterateAxis(AxisInfo.CHILD), false);
		}
		
        return new TestInstruction(title, size, html);
    }

	private static class TestInstruction extends SimpleExpression {
		
        public static final int TITLE	= 0;
        public static final int SIZE	= 1;
        public static final int HTML	= 2;
        
    	private String[] changes;

        public TestInstruction(Expression title, Expression size, Expression html) {
//        	logger.info("new TestInstruction: " + html + ", " + size + ", " + title);
			Expression[] subs = {title, size, html};
        	setArguments(subs);
        }

        public int computeCardinality() {
            return StaticProperty.EXACTLY_ONE;
        }

        public String getExpressionType() {
            return "gui:html-dialog";
        }

        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
        	final String titleString = arguments[TITLE].head().getStringValue();
        	final String sizeString = arguments[SIZE].head().getStringValue();
        	final String htmlString = generateHtml(arguments);
	        showDialog(titleString, sizeString, htmlString);
        	return outputToXml(context);
        }

		private String generateHtml(Sequence[] arguments) throws XPathException {
//			logger.info("generateHTML");
			String htmlString = "";
			try {
				SequenceIterator iterator = arguments[HTML].iterate();
			
			Item item = iterator.next();
			while (item != null) {
				if (item instanceof StringValue) {		
					htmlString += item.getStringValue();
				} else if (item instanceof NodeInfo) {
					htmlString += QueryResult.serialize((NodeInfo)item);
				} else {
					throw new XPathException("Can't serialize item: " + item.getClass()); 
				}
            	item = iterator.next();
			}
			} catch (Exception e) {
				logger.error(e);
			}
//			logger.info("generated HTML: " + htmlString);
			return htmlString;
		}
    

		private void showDialog(String title, String size, String htmlString) {
//			logger.info("showDialog");
			int x, y;
			size = size.replace('(', ' ');
			size = size.replace(')', ' ');
			size = size.trim();
			String[] coord = size.split(", ");			
//			logger.info(Arrays.toString(coord));
			
			x = Integer.parseInt((coord[0].replace('(', ' ')));
			y = Integer.parseInt((coord[1].replace(')', ' ')));
			
	        final JDialog frame = new JDialog((Frame)PluginWorkspaceProvider.getPluginWorkspace().getParentFrame(), title, true);
			JTextPane textPane = new JTextPane();
	        textPane.setContentType("text/html");
	        textPane.setEditable(false);

	        textPane.setText(htmlString);
	        
	        HTMLEditorKit kit = (HTMLEditorKit)textPane.getEditorKit();
            kit.setAutoFormSubmission(false);
            textPane.setOpaque(false);
	        
            textPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					logger.info("hyperlinkUpdate");
					if (e instanceof FormSubmitEvent) {
						logger.info("FormSubmitEvent");
						changes = getChanges(((FormSubmitEvent)e).getData());
//                        logger.info("changes: " + Arrays.toString(changes));
                        frame.dispose();
                    } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
	                	if(Desktop.isDesktopSupported()) {
	                    	URL url = e.getURL();
	                    	String protocol = url.getProtocol();
	                        try {
	                        	if (protocol.equals("http")) {
	                        		Desktop.getDesktop().browse(url.toURI());
	                        	} else if (protocol.equals("file")){
	                        		Desktop.getDesktop().open(new File(url.toURI()));                  		
	                        	}
	                        }
	                        catch (Exception e1) {
	                            logger.error(e1, e1);
	                        }
	                	}
                    }
				}
            }); 
            
            JScrollPane scrollPane 	= new JScrollPane(textPane);
//	        JButton 	confirm 	= new JButton("Confirm");
//	        confirm.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// TODO Auto-generated method stub
//					HyperlinkEvent submit = new HyperlinkEvent(null, HyperlinkEvent.EventType.ACTIVATED, null);
//					textPane.fireHyperlinkUpdate(submit);
//					logger.info("button pressed.");
//				}
//	        });
//	        JButton 	cancel 		= new JButton("Cancel");
//	        
//	        scrollPane.add(confirm);
	        frame.getContentPane().add(scrollPane);
	        frame.setSize(x, y);
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
		}

		private Sequence outputToXml(XPathContext context) throws XPathException {
//			logger.info("OutputToXml");
			final SequenceOutputter out = context.getController().allocateSequenceOutputter(50);
			String nextToken = null;
			if (changes != null) {
				for (String s : changes) {
					StringTokenizer stok = new StringTokenizer(s, "=");
					try {
						nextToken = URLDecoder.decode(stok.nextToken(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error(e, e);
					}
					out.startElement(new NoNamespaceName(nextToken), Untyped.getInstance(), locationId, 0);
					if (stok.hasMoreTokens()) {	
						try {
							nextToken = URLDecoder.decode(stok.nextToken(), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							logger.error(e, e);
						}
						out.characters(nextToken, locationId, 0);
					} else {
						out.characters("", locationId, 0);
					}
					out.endElement();
				}
			}
    		Sequence output = SequenceTool.toLazySequence(out.iterate());
			return output;
		}

		public String[] getChanges(String input) {
			return input.split("&");
		}
	}
}

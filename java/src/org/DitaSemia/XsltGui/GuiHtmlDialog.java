package org.DitaSemia.XsltGui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.xml.stream.XMLStreamException;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.s9api.BuildingStreamWriterImpl;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.style.Compilation;
import net.sf.saxon.style.ComponentDeclaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.StringValue;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

public class GuiHtmlDialog extends ExtensionInstruction {

	//@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiHtmlDialog.class.getName());
	
	Expression html;
	Expression size;
	Expression title;
	Expression propertiesKey;
	Expression buttons;
	Expression blockParent;
	Expression cssRules;
	Expression cssUri;
	Expression resizable;
	
	public static final String DEFAULT_BUTTONS = "('OK', 'Cancel')";

	@Override
    public void prepareAttributes() throws XPathException {
		//logger.info("prepareAttributes");
		
        //required title attribute
        String titleAtt = getAttributeValue("", "title");
        if (titleAtt != null) {
        	title = makeAttributeValueTemplate(titleAtt);
        } else {
        	reportAbsence("title");
        }
        //logger.info("title: " + title);
        
        //optional size attribute
        String sizeAtt = getAttributeValue("", "size");
        if (sizeAtt != null) {
        	size = makeExpression(sizeAtt);
        } else {
        	size = makeAttributeValueTemplate("");
        }
        //logger.info("size: " + size);
		
		//optional html attribute
		String htmlAtt = getAttributeValue("", "html");
        if (htmlAtt!= null) {
        	html = makeExpression(htmlAtt);
        } else {
        	html = null;
        }
        //logger.info("html: " + html);
        
        // optional properties-key
        String propertiesAtt = getAttributeValue("", "properties-key");
        if (propertiesAtt != null) {
        	propertiesKey = makeAttributeValueTemplate(propertiesAtt);
        } else {
        	propertiesKey = makeAttributeValueTemplate("");
        }
        //logger.info("properties: " + propertiesKey);
        
        
        //optional button(s) attribute
        String buttonsAtt = getAttributeValue("", "buttons");
        if (buttonsAtt != null) {
        	buttons = makeExpression(buttonsAtt);
        } else {
        	buttons = makeExpression(DEFAULT_BUTTONS);
        }
        //logger.info("buttons: " + buttons);
        
        //optional modality attribute
        String blockParentAtt = getAttributeValue("", "block-parent");
        if (blockParentAtt != null) {
        	blockParent = makeAttributeValueTemplate(blockParentAtt);
        } else {
        	blockParent = makeAttributeValueTemplate("yes");
        }
        //logger.info("blockParent: " + blockParent);
        
        //optional css-rules attribute
        String cssRulesAtt = getAttributeValue("", "css-rules");
        if (cssRulesAtt != null) {
        	cssRules = makeAttributeValueTemplate(cssRulesAtt);
        } else {
        	cssRules = makeAttributeValueTemplate("");
        }
        //logger.info("cssRules: " + cssRules);
        
        //optional css-uri attribute
        String cssUriAtt = getAttributeValue("", "css-uri");
        if (cssUriAtt != null) {
        	cssUri = makeAttributeValueTemplate(cssUriAtt);
        } else {
        	cssUri = makeAttributeValueTemplate("");
        }
        //logger.info("cssUri: " + cssUri);
        
        //optional resizable attribute
        String resizableAtt = getAttributeValue("", "resizable");
        if (resizableAtt != null) {
        	resizable = makeAttributeValueTemplate(resizableAtt);
        } else {
        	resizable = makeAttributeValueTemplate("no");
        }
        //logger.info("resizable: " + resizable);
    }

	@Override
    public void validate(ComponentDeclaration decl) throws XPathException {
		//logger.info("validate");
        super.validate(decl);
        title 			= typeCheck("title", 			title);
        propertiesKey 	= typeCheck("properties-key", 	propertiesKey);
        buttons  		= typeCheck("buttons", 			buttons);
        size  	 		= typeCheck("size", 			size);
        blockParent		= typeCheck("block-parent", 	blockParent);	
        cssRules		= typeCheck("css-rules", 		cssRules);
        cssUri			= typeCheck("css-uri", 			cssUri);
        resizable 		= typeCheck("resizable",		resizable);	
       
        if (html 	!= null) {
        	html  	 = typeCheck("html", 	html);
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
	
        return new HtmlDialogInstruction(title, size, html, propertiesKey, buttons, blockParent, cssRules, cssUri, resizable);
    }

	private static class HtmlDialogInstruction extends SimpleExpression {
		
        public static final int TITLE			= 0;
        public static final int SIZE			= 1;
        public static final int HTML			= 2;
        public static final int PROPERTIESKEY 	= 3;
        public static final int BUTTONS 		= 4;
        public static final int BLOCK_PARENT	= 5;
        public static final int CSS_RULES		= 6;
        public static final int CSS_URI			= 7;
        public static final int RESIZABLE		= 8;

    	private Document document;
    	
    	private String pressedButton = "";

        public HtmlDialogInstruction(	Expression title, 
        								Expression size, 
        								Expression html, 
        								Expression properties, 
        								Expression buttons, 
        								Expression blockParent,
        								Expression cssRules,
        								Expression cssUri,
        								Expression resizable) {
        	//logger.info("HtmlDialogInstruction: " + title + ", " + size + ", " + html + ", " + properties + ", " + buttons);
			Expression[] subs = {title, size, html, properties, buttons, blockParent, cssRules, cssUri, resizable};
        	setArguments(subs);
        }

        public int computeCardinality() {
            return StaticProperty.EXACTLY_ONE;
        }

        public String getExpressionType() {
            return "gui:html-dialog";
        }

        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
        	//logger.info("call"); 
        	try {
	        	final String 	titleString 		= arguments[TITLE].head().getStringValue();
	        	final String 	htmlString 			= generateHtml(arguments);
	        	final String 	propertiesString 	= arguments[PROPERTIESKEY].head().getStringValue();
	        	final Dimension	size	 			= getSize(arguments);
	        	final String[] 	buttonsString 		= getButtons(arguments);
	            final boolean 	blockParent			= checkBoolean("block-parent", 	arguments[BLOCK_PARENT].head().getStringValue());
	            final String 	cssRules			= arguments[CSS_RULES].head().getStringValue();
	            final boolean 	resizable			= checkBoolean("resizable", 	arguments[RESIZABLE].head().getStringValue());
	            final URL 		cssUrl;
	            try {
	            	cssUrl	= getCssUrl(context, arguments);
	            } catch (Exception e) {
	            	throw new XPathException("Invalid URI ('" + arguments[CSS_URI].head().getStringValue() + "')");
	            }
	            
	            Runnable runnable = new Runnable() {
	                @Override
	                public void run() {
	                	showDialog(titleString, size, htmlString, propertiesString, buttonsString, blockParent, cssRules, cssUrl, resizable);
	                }
	            };
	            if (SwingUtilities.isEventDispatchThread()) {
	              runnable.run();
	            } else {
	              SwingUtilities.invokeAndWait(runnable);
	            }
		        
	        	return dataToXml(context);
        	} catch (Exception e) {
        		logger.error(e, e);
        		return EmptySequence.getInstance();
        	}
        }
        
        private boolean checkBoolean(String attribute, String value) throws XPathException {
        	//logger.info("checkBoolean: " + attribute + ", " + value);
        	if (value.equals("no") || value.equals("false") || value.equals("0")) {
        		return false;
        	} else if (value.equals("yes") || value.equals("true") || value.equals("1")) {
        		return true;
        	} else {
        		throw new XPathException("Invalid value for " + attribute + " attribute ('" + value + "'). "
        				+ "The " + attribute + " attribute must contain one of the following values: 'yes', 'true', '1', 'no', 'false', '0'");
        	}
        }
        
        private URL getCssUrl(XPathContext context, Sequence[] arguments) throws MalformedURLException, XPathException {
        	//logger.info("getCssUrl()");
        	String cssUri = arguments[CSS_URI].head().getStringValue();
        	if (cssUri.isEmpty()) {
        		return null;
        	} else {
	        	URL base = null;
				final Item item = context.getContextItem();
				if (item instanceof NodeInfo) {
					base = new URL(((NodeInfo) item).getBaseURI());
				}
				URL url = new URL(base, cssUri);
				return url;
        	}
        }
        
        private Dimension getSize(Sequence[] arguments) throws XPathException {
        	//logger.info("getSize: " + arguments[SIZE].toString());
        	if (arguments[SIZE].head().getStringValue().isEmpty()) {
        		return null;
        	}
        	SequenceIterator 	iterator = arguments[SIZE].iterate();
        	ArrayList<Integer> 	sizeList = new ArrayList<Integer>();
            Item item = iterator.next();
            while (item != null) {
            	if (!(item instanceof IntegerValue)) {
            		throw new XPathException("The size attribute must contain only integer values ('" + item.getStringValue() + "').");	
            	}
            	sizeList.add((int)(((IntegerValue)item).longValue()));
            	item = iterator.next();
            }
            if (sizeList.size() != 2) {
            	throw new XPathException("The size attribute must be empty or contain a sequence of 2 items.");
            }

           	return new Dimension(sizeList.get(0), sizeList.get(1));
        }
        
        private String[] getButtons(Sequence[] arguments) throws XPathException {
        	//logger.info("getButtons: " + arguments[BUTTONS].toString());
        	SequenceIterator iterator = arguments[BUTTONS].iterate();
        	List<String> buttonsList = new LinkedList<>();
        	Item item = iterator.next();
        	while (item != null) {
        		buttonsList.add(item.getStringValue());
        		item = iterator.next();
        	}
        	String[] buttonsString = buttonsList.toArray(new String[buttonsList.size()]);
        	
        	return buttonsString;
        }

		private String generateHtml(Sequence[] arguments) throws XPathException {
			//logger.info("generateHTML");
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
			//logger.info("generated HTML: " + htmlString);
			return htmlString;
		}
    
		private void showDialog(String 		title, 
								Dimension 	size, 
								String 		htmlString, 
								String 		properties, 
								String[] 	buttons, 
								boolean 	blockParent,
								String 		cssRules,
								URL 		cssUrl,
								boolean 	resizable) 	{
			//logger.info("showDialog(" + 
			//	title + ", " + size + ", " + htmlString + ", " + properties + 
			//	")");
			final Frame		frame		= (Frame)PluginWorkspaceProvider.getPluginWorkspace().getParentFrame();
			
			JDialog 		dialog 		= new JDialog(frame, title, true);
			JTextPane 		textPane 	= new JTextPane();
			JPanel		 	buttonPane 	= new JPanel();
			JScrollPane 	scrollPane 	= new JScrollPane(textPane);
			
			
	        //create buttons
	        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
	        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            addButtons(buttonPane, buttons, textPane, dialog, properties);
            
			//set text and stylesheet configurations in textPane
	        textPane.setEditable(false);
	        HTMLEditorKit kit = new HTMLEditorKit();
	        kit.setAutoFormSubmission(false);
	        textPane.setEditorKit(kit);
	        StyleSheet stylesheet = new StyleSheet();
	        addCssRules(stylesheet, cssUrl, cssRules);
	        kit.setStyleSheet(stylesheet);
	        Document doc = kit.createDefaultDocument();
	        textPane.setOpaque(false);
	        scrollPane.setBorder(BorderFactory.createEmptyBorder());
	        textPane.setDocument(doc);
		    textPane.setText(htmlString);
		    
			//check properties for predefined size
			if (!properties.isEmpty()) {
				size = loadState(properties, size);
			}
			
			//configure settings of dialog
	        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        dialog.setResizable(resizable);
	        
	        dialog.addWindowListener(new WindowAdapter() {
	        	@Override
	        	public void windowClosing(WindowEvent e) {
	        		document = textPane.getDocument();
	        	}
	        });
	        
	        if (!blockParent) {
				frame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
				dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			} else {
				frame.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
			}
	        
	        dialog.getContentPane().setLayout(new BorderLayout());
	        dialog.getContentPane().add(scrollPane, BorderLayout.NORTH);
            dialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
	        dialog.addNotify();
	        if (properties.isEmpty() && size == null) {
	        	dialog.pack();
	        } else {
	        	dialog.setSize(size);
	        }
	        dialog.setLocationRelativeTo(null);
	        dialog.setVisible(true);
		}

		private void addCssRules(StyleSheet stylesheet, URL cssUrl, String cssRules) {
			//logger.info("addCssRules: " + cssRules);
			if (cssUrl != null) {
				stylesheet.importStyleSheet(cssUrl);
			}
			if (!cssRules.isEmpty()){
				stylesheet.addRule(cssRules);
			}
		}
		
		private void addButtons(JPanel panel, String[] buttons, JTextPane textPane, JDialog dialog, String properties) {
			//logger.info("addButtons: " + Arrays.toString(buttons));
			panel.add(Box.createGlue());
			for (String s : buttons) {
				JButton button = new JButton(s);
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						pressedButton 	= button.getText();
						document 		= textPane.getDocument();
						saveState(properties, dialog.getSize());
						dialog.dispose();
					}
					
				});
				panel.add(button);
				panel.add(Box.createRigidArea(new Dimension(5, 0)));
				panel.add(Box.createGlue());
			} 
		}
		
		@SuppressWarnings("rawtypes")
		private Sequence dataToXml(XPathContext context) throws XPathException {
			//logger.info("dataToXml");
			final Processor 				processor 	= new Processor(context.getConfiguration());
			final DocumentBuilder 			builder 	= processor.newDocumentBuilder();
			
			try {
				BuildingStreamWriterImpl 	writer 		= builder.newBuildingStreamWriter();
				final ElementIterator 		iterator 	= new ElementIterator(document);
				Element 					element;
				
				while ((element = iterator.next()) != null) {
					final AttributeSet 	atts 	= element.getAttributes();
					final String        name 	= (String)atts.getAttribute(Attribute.NAME);
					final String 		value 	= (String)atts.getAttribute(Attribute.VALUE);
					final String 		type 	= (String)atts.getAttribute(Attribute.TYPE);
					if (name != null) {
						final Object model = atts.getAttribute(StyleConstants.ModelAttribute);
						//logger.info("name: '" + name + "', model: " + model);
						if (model instanceof PlainDocument) {
							final PlainDocument content = (PlainDocument)model; 
								//logger.info(name + ": '" + content.getText(0, content.getLength()) + "'");
							try {
								createElement(writer, name, content.getText(0, content.getLength()));
							} catch (BadLocationException e) {
								logger.error(e, e);
							}
						} else if (model instanceof JToggleButton.ToggleButtonModel) {
							final boolean isSelected = ((JToggleButton.ToggleButtonModel)model).isSelected();
							//TODO: constant for type = radio?
							if (type.equals("radio") && isSelected) {
								createElement(writer, name, value);
								//logger.info(type + "=" + value);
							} else if (type.equals("checkbox")) {
								createElement(writer, name, String.valueOf(isSelected));
								//logger.info(name + ": '" + isSelected + "'");
							}
						} else if (model instanceof DefaultComboBoxModel) {
							//logger.info(name + ": '" + ((DefaultComboBoxModel)model).getSelectedItem() + "'");
							createElement(writer, name, ((DefaultComboBoxModel)model).getSelectedItem().toString());
						}
					}
				}
				createElement(writer, "button", pressedButton);
				
				return writer.getDocumentNode().getUnderlyingValue();
				
			} catch (SaxonApiException | XMLStreamException e) {
				logger.error(e, e);
				throw new XPathException("Failed to create return value: " + e.getMessage());
			}
		} 
		
		private void createElement(BuildingStreamWriterImpl writer, String name, String value) throws XPathException, XMLStreamException {
			writer.writeStartElement(name);
			writer.writeCharacters(value);
			writer.writeEndElement();
		}
		
          
		private void saveState(String properties, Dimension size) {
			try {
				//logger.info("saveState");
				final WSOptionsStorage 	optionsStorage 	= PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		    	final String			sizeString 		= String.valueOf(size.width) + "," + String.valueOf(size.height);
		    	optionsStorage.setOption(properties, sizeString);
			} catch (Exception e) {
				logger.error(e, e);
			}
		}
		
		private Dimension loadState(String properties, Dimension defaultSize) {
			//logger.info("loadState");
			final WSOptionsStorage 	optionsStorage 	= PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
			final String 			sizeProperties 	= optionsStorage.getOption(properties, "");
			//logger.info("sizeProperties: " + sizeProperties);
			if (sizeProperties != null) {
				final String[] size = sizeProperties.split(",");
				if (size.length == 2) {
					return new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
				}
			}
			return defaultSize;
		}
	}
}
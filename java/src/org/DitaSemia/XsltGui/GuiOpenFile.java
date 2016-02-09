package org.DitaSemia.XsltGui;

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.style.Compilation;
import net.sf.saxon.style.ComponentDeclaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;

import org.apache.log4j.Logger;

import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.Workspace;

public class GuiOpenFile extends ExtensionInstruction {

//	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiOpenFile.class.getName());
	
	Expression url;
	Expression view;
	
	@Override
	protected void prepareAttributes() throws XPathException {
      logger.info("prepareAttributes");
      //required url attribute
      String urlAtt = getAttributeValue("", "url");
      if (urlAtt != null) {
      	url = makeAttributeValueTemplate(urlAtt);
      } else {
      	reportAbsence(urlAtt);
      }
      logger.info("URL: " + url);
      
      //optional view attribute
      String viewAtt = getAttributeValue("", "view");
      if (viewAtt != null) {
      	view = makeAttributeValueTemplate(viewAtt);
      } else {
      	view = makeAttributeValueTemplate("");
      }
      logger.info("view: " + view);
	}

	@Override
    public void validate(ComponentDeclaration decl) throws XPathException {
        super.validate(decl);
//        url 			= typeCheck("url", 	url);
        view 			= typeCheck("view", view);
    }

	@Override
    public Expression compile(Compilation exec, ComponentDeclaration decl) throws XPathException {
        return new OpenFileInstruction(url, view);
    }
	
	private static class OpenFileInstruction extends SimpleExpression {

		public static final int URL		= 0;
        public static final int VIEW	= 1;
        
        Workspace workspace = PluginWorkspaceProvider.getPluginWorkspace();
        
		public OpenFileInstruction(Expression url, Expression view) {
			logger.info("new OpenFileDialogInstruction: url: " + url + ", view: " + view);
			Expression[] subs = {url, view};
        	setArguments(subs);
		}
		
		public int computeCardinality() {
            return StaticProperty.EXACTLY_ONE;
        }

        public String getExpressionType() {
            return "gui:open-file";
        }
		
		public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
			logger.info("call");
			try{
				final URL url = getURL(context, arguments[URL].head().getStringValue());
				logger.info("url: " + url.toExternalForm());
	            final String viewString = arguments[VIEW].head().getStringValue();
	            logger.info("view: " + viewString);
	            if (viewString.equals("author")) {
	        		workspace.open(url, EditorPageConstants.PAGE_AUTHOR);
	        	} else if (viewString.equals("text")) {
	        		workspace.open(url, EditorPageConstants.PAGE_TEXT);
	        	} else {
	        		throw new XPathException("The given view ('" + viewString + "') is invalid. It must be either 'author' or 'text'.");
        	}
			} catch (Exception e) {
				logger.error(e, e);
			}
			
            return EmptySequence.getInstance();
		}
		
		private URL getURL(XPathContext context, String filename) throws MalformedURLException {
			URL base = null;
			final Item item = context.getContextItem();
			if (item instanceof NodeInfo) {
				base = new URL(((NodeInfo) item).getBaseURI());
			}
			URL url = new URL(base, filename);
			return url;
		}
	}
}

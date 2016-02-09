# XSLT-GUI
Provide a graphical userinterface from within your xslt script

When writing implementing xsl transformations scenarios in oXygen in occurs that you need to get some feedback from the user or need to enure that the notices some kind of output where The standard xsl:message instruction might no sufficient. This repository contains saxon extensions instructions that allow you to create message- and option-dialogs inside your xsl script when they are executed from within oXygen.


## Samples
The samples are located in a dedicated samples folder. There is an oXygen project as well with a suitable transformation scenario configured for each sample. Just open the xml file in the folder you want to check out and start the configured transformation.


## Installation
These stapes are required to use xslt-gui in your own transformation scenarios:

1. Make sure that saxon finds the DitaSemiaXsltGui.jar file. This can be done either by placing it in the oxygen-lib folder (and restart oxygen afterwards) or by adding the file explicitly as extension to your transformation scenario.

2. You need to link the extension factory org.DitaSemia.XsltGui.SaxonExtension to a namespace in the saxon configuration. oXygen currently (v17.0) does not support this through the dialog so you have to either create your own configuration file or use the one from this project: config/saxon-xsltgui-config.xml.

3. Within your xsl script you need to bind this namespace to a prefix and add this prefix to the extension-element-prefixes attribut. See samples/message-dialog/message-dialog.xsl for a very simple sample.

Now you can use the new instructions.


## Syntax

XSLT-GUI provides the following commands:

### message-dialog
It opens a simple message box with an OK button.

Attributes:
- title (mandatory): The title of the dialog
- icon (option): The icon displayed in the dialog. Valid values are: plain, error, info, warning, question.
- text (option): The text to be displayed. (Use &#x0A; for newlines.)

As an alternative to using the text attribute you can add sequence constructors within the instruction. You need to specify ecaclty one of both: either the text attribute or child nodes.


### option-dialog
It opens an option dialog box with two or three buttons.

Attributes:
- title (mandatory): The title of the dialog
- options (mandatory): A sequence containing the text for the buttons. The sequence needs to contain exactly two or three items.
- icon (option): The icon displayed in the dialog. Valid values are: plain, error, info, warning, question.
- text (option): The text to be displayed. (Use &#x0A; for newlines.)
- default (option): The index (starting with 1) of the option to be selected pressing the enter button.

As an alternative to using the text attribute you can add sequence constructors within the instruction. You need to specify ecaclty one of both: either the text attribute or child nodes.

## Compiling
To compile it with eclipse you have to set the variable OXYGENXML locating the oXyen installation folder (e.g. "C:/Program Files/Oxygen XML Editor"). This needs to be done twice:
- Eclipse Path variable: Window -> Preferences -> Java -> Build Path -> Classpath Variables
- For the Ant Builder: Window -> Preferences -> Ant -> Runtime -> Properties
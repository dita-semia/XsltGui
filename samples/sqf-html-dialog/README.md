# Sample SQF HTML Dialog

This is a simple demonstration how to create an HTML dialog from within a Schematron Quick Fix.
The advantage over the <sqf:user-entry> is that you can create more complex dialogs depending on the actual data.

Currently it's not possible to add external libraries to an oXygen validation scenario (as you can do for transformation scenarios). 
Thus, you have to copy the DitaSemiaXsltGui.jar in the oxygen lib folder so that Saxon will find it.
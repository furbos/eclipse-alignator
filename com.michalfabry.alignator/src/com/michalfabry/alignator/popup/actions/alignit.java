package com.michalfabry.alignator.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;


public class alignit implements IObjectActionDelegate {

	/**
	 * Constructor for Action1.
	 */
	public alignit() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) { }

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
			// Get editor
			IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(editorPart instanceof ITextEditor) {
				final ITextEditor editor = (ITextEditor) editorPart;
				IDocumentProvider documentProvider = editor.getDocumentProvider();
				IDocument document = documentProvider.getDocument(editor.getEditorInput());
				ISelection selection = editor.getSelectionProvider().getSelection();

				// Get current selection
				if(selection instanceof TextSelection) {
					final TextSelection textSelection = (TextSelection) selection;
					int length = textSelection.getLength();
					
					if(length > 0) {
						int startLine = textSelection.getStartLine();
						int endLine = textSelection.getEndLine();

						// Multiple lines must be selected
						if(endLine > startLine) {
							int startLineOffset = document.getLineOffset(startLine);
							int endLineOffset   = document.getLineOffset(endLine);
							int endLineLength   = document.getLineLength(endLine);
							
							// First we go throught all lines to find the longest one
							int longestLineLength = 0;
							for(int i = startLine; i <= endLine; i++) {
								String line = document.get(document.getLineOffset(i), document.getLineLength(i));
								
								if(line.matches(".*=.*\n?")) {
									int l = line.split("\\s*=")[0].length();
									if(l > longestLineLength) {
										longestLineLength = l;
									}
								}
							}
							
							// Now we can pad lines and align them
							String alignedLines = "";
							for(int i = startLine; i <= endLine; i++) {
								String line = document.get(document.getLineOffset(i), document.getLineLength(i));
								String delimiter = "="; 
								
								if(line.matches(".*=.*\n?")) {
									if(line.matches(".*=>.*\n?")) {
										delimiter = "=>";
									}
									
									int index = line.indexOf(delimiter, 0); 

									alignedLines += rightPad(line.substring(0, index).replaceAll("\\s+$", ""), longestLineLength + 1, " ") + delimiter + " " + line.substring(index + delimiter.length(), line.length()).replaceAll("^\\s+", ""); 
								} else {
									alignedLines += line;
								}
							}
							
							document.replace(startLineOffset, (endLineOffset + endLineLength) - startLineOffset, alignedLines);
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String rightPad(String str, int size, String pad) {
		int strSize = str.length();
		int pads = size - strSize;
		
		if(pads <= 0) {
			return str;
		}
		
		char[] padding = new char[pads];
		char[] padChars = pad.toCharArray();
		for(int i = 0; i < pads; i++) {
			padding[i] = padChars[0];
		}
		return str.concat(new String(padding));
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) { }

}

/*
 Kick Assembler plugin - An Eclipse plugin for convenient Kick Assembling
 Copyright (c) 2012 - P-a Backstrom <pa.backstrom@gmail.com>
 
 Based on ASMPlugin - http://sourceforge.net/projects/asmplugin/
 Copyright (c) 2006 - Andy Reek, D. Mitte
 
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/ 
package org.lyllo.kickassplugin.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.lyllo.kickassplugin.Constants;


/**
 * The editor class.
 * 
 * @author Andy Reek
 * @since 06.10.2005
 */
public class ASMEditor extends TextEditor {

  private ASMContentOutlinePage contentOutlinePage;

  /**
   * The ContextTypeRegistry of this Editor.
   */
  private static ContextTypeRegistry contextTypeRegistry;

  private ProjectionAnnotationModel annotationModel;

  /**
   * Returns the ContextTypeRegistry of this Editor.
   * 
   * @return The ContextTypeRegistry.
   */
  public static ContextTypeRegistry getContextTypeRegistry() {
    if (contextTypeRegistry == null) {
      contextTypeRegistry = new ContextTypeRegistry();
      contextTypeRegistry.addContextType(new TemplateContextType(Constants.ASM_EDITOR_CONTEXT));
    }

    return contextTypeRegistry;
  }

  /**
   * {@inheritDoc}
   */
  protected void initializeEditor() {
    super.initializeEditor();
    setSourceViewerConfiguration(new ASMSourceViewerConfiguration(this));
  }

  /**
   * {@inheritDoc}
   */
  protected void initializeKeyBindingScopes() {
    setKeyBindingScopes(new String[] { "org.lyllo.kickassplugin.editorScope" });
  }

  /**
   * {@inheritDoc}
   */
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    super.init(site, input);

    IDocument document = getDocumentProvider().getDocument(getEditorInput());
    FastPartitioner partitioner = new FastPartitioner(new ASMPartitionScanner(),
                                                      new String[] { Constants.PARTITION_STRING,
                                                                    Constants.PARTITION_COMMENT_SINGLE,
                                                                    Constants.PARTITION_COMMENT_MULTI });
    partitioner.connect(document);
    document.setDocumentPartitioner(partitioner);
  }

  /**
   * {@inheritDoc}
   */
  protected void createActions() {
    super.createActions();

    ResourceBundle bundle = ResourceBundle.getBundle("org.lyllo.kickassplugin.messages");

    IAction action = new TextOperationAction(bundle, "ContentAssistProposal.", this,
                                             ISourceViewer.CONTENTASSIST_PROPOSALS);
    action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
    setAction("ContentAssistProposal", action);
    markAsStateDependentAction("ContentAssistProposal", true);

    if (getSourceViewer() instanceof SourceViewer) {
      action = new ToggleCommentAction((SourceViewer) getSourceViewer());
      action.setActionDefinitionId("org.lyllo.kickassplugin.toggle.comment");
      setAction("toggle.comment", action);
      markAsStateDependentAction("toggle.comment", true);
    }
  }

  /**
   * Refreshs the editor.
   */
  public void refreshSourceViewer() {
    ISourceViewer isv = getSourceViewer();
    if (isv instanceof SourceViewer) {
      ((SourceViewer) getSourceViewer()).refresh();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void dispose() {
    super.dispose();

    SourceViewerConfiguration svc = getSourceViewerConfiguration();
    if (svc instanceof ASMSourceViewerConfiguration) {
      ((ASMSourceViewerConfiguration) svc).dispose();
    }
  }

  /**
   * {@inheritDoc}
   */
  public Object getAdapter(Class adapter) {
    if (adapter != null && IContentOutlinePage.class.equals(adapter)) {
      if (contentOutlinePage == null) {
        contentOutlinePage = new ASMContentOutlinePage(this);
        IEditorInput input = getEditorInput();

        if (input != null) {
          contentOutlinePage.setInput(input);
        }
      }

      return contentOutlinePage;
    }
    return super.getAdapter(adapter);
  }

  /**
   * Updates the view of the outlinepage.
   */
  public void updateContentOutlinePage() {
    if (contentOutlinePage != null) {
      IEditorInput input = getEditorInput();

      if (input != null) {
        contentOutlinePage.setInput(input);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();

    ProjectionSupport projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
    projectionSupport.install();

    // turn projection mode on
    viewer.doOperation(ProjectionViewer.TOGGLE);

    annotationModel = viewer.getProjectionAnnotationModel();
  }

  /**
   * {@inheritDoc}
   */
  protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);

    // ensure decoration support has been created and configured.
    getSourceViewerDecorationSupport(viewer);

    viewer.setDefaultPrefixes(new String[] { "//" }, IDocument.DEFAULT_CONTENT_TYPE);
    viewer.setDefaultPrefixes(new String[] { "//" }, Constants.PARTITION_COMMENT_SINGLE);
    return viewer;
  }

  /**
   * Update the folding structure with a given list of Positions.
   * 
   * @param positions The positions
   */
  public void updateFoldingStructure(ArrayList<Position> positions) {
    ArrayList<ProjectionAnnotation> deletions = new ArrayList<ProjectionAnnotation>();
    Object annotationObject = null;
    ProjectionAnnotation annotation = null;
    Position position = null;

    for (Iterator<?> iter = annotationModel.getAnnotationIterator(); iter.hasNext();) {
      annotationObject = iter.next();

      if (annotationObject instanceof ProjectionAnnotation) {
        annotation = (ProjectionAnnotation) annotationObject;

        position = annotationModel.getPosition(annotation);

        if (positions.contains(position)) {
          positions.remove(position);
        } else {
          deletions.add(annotation);
        }
      }

    }

    Annotation[] removeAnnotations = deletions.toArray(new Annotation[deletions.size()]);

    // this will hold the new annotations along
    // with their corresponding positions
    HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();

    for (int i = 0; i < positions.size(); i++) {
      annotation = new ProjectionAnnotation();

      newAnnotations.put(annotation, positions.get(i));
    }

    annotationModel.modifyAnnotations(removeAnnotations, newAnnotations, new Annotation[] {});
  }

public ASMContentOutlinePage getOutline() {
	return this.contentOutlinePage;
}
}

/*
 * Copyright (c) 2010 Eclipse Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ch.netcetera.eclipse.workspaceconfig.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Dialog to display the preference recorder results.
 * 
 * @author Michael Pellaton
 */
public class WorkspacePrefercensRecorderResultDialog extends TitleAreaDialog {

  private final String result;
  private final ITextAccessor textAccessor;

  
  /**
   * Constructor.
   * 
   * @param parentShell the parent shell
   * @param textAccessor the text accessor
   * @param result the result to display
   */
  public WorkspacePrefercensRecorderResultDialog(Shell parentShell, ITextAccessor textAccessor, String result) {
    super(parentShell);
    this.textAccessor = textAccessor;
    this.result = result;
    setHelpAvailable(false);
  }

  
  /** {@inheritDoc} */
  @Override
  protected boolean isResizable() {
    return true;
  }
  
  /** {@inheritDoc} */
  @Override
  public void create() {
    super.create();
    setTitle(this.textAccessor.getText("recorder.dialog.titlearea.title"));
    setMessage(this.textAccessor.getText("recorder.dialog.titlearea.description"));
  }

  /** {@inheritDoc} */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }
  
  /** {@inheritDoc} */
  @Override
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(this.textAccessor.getText("recorder.dialog.title"));
  }
  
  /** {@inheritDoc} */
  @Override
  protected Point getInitialSize() {
    return new Point(600, 500);
  }

  /** {@inheritDoc} */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 10;
    layout.marginWidth = 15;
    layout.numColumns = 2;
    
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    composite.setFont(parent.getFont());
    
    Text resultText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
    
    GridData nameTextGridData = new GridData(GridData.FILL_BOTH);
    nameTextGridData.minimumWidth = 300;
    nameTextGridData.minimumHeight = 200;
    nameTextGridData.heightHint = 300;
    resultText.setLayoutData(nameTextGridData);
    resultText.setText(result);
    resultText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));

    resultText.setText(this.result);

    return composite;
  }
}
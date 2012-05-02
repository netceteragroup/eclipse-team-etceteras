/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETE Project     - copy out of jdt-internal api into own plugin
 *                     - reformatted (checkstyle compliant), adapted to own needs
 *******************************************************************************/
package ch.netcetera.eclipse.common.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog offering a selection from a combo box.
 */
public class ComboSelectionDialog extends Dialog {

  private String fSelection = null;
  private final String fShellTitle;
  private final String fLabelText;
  private final List<String> fAllowedStrings;
  private final int fInitialSelectionIndex;

  /**
   * Constructor.
   *
   * @param parentShell the parent shell
   * @param shellTitle the title
   * @param labelText the label text
   * @param comboStrings the strings to be displayed in the combo box
   * @param initialSelectionIndex the index of the initially selected item
   */
  public ComboSelectionDialog(Shell parentShell, String shellTitle, String labelText, List<String> comboStrings,
      int initialSelectionIndex) {
    super(parentShell);
    this.fShellTitle = shellTitle;
    this.fLabelText = labelText;
    this.fAllowedStrings = comboStrings;
    this.fInitialSelectionIndex = initialSelectionIndex;
  }

  /**
   * Gets the selected item.
   *
   * @return the selected item
   */
  public String getSelectedString() {
    return this.fSelection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    getShell().setText(this.fShellTitle);

    Composite composite = (Composite) super.createDialogArea(parent);
    Composite innerComposite = new Composite(composite, SWT.NONE);
    innerComposite.setLayoutData(new GridData());
    GridLayout gl = new GridLayout();
    gl.numColumns = 1;
    innerComposite.setLayout(gl);

    Label label = new Label(innerComposite, SWT.NONE);
    label.setText(this.fLabelText);
    label.setLayoutData(new GridData());

    final Combo combo = new Combo(innerComposite, SWT.READ_ONLY);
    for (String item : this.fAllowedStrings) {
      combo.add(item);
    }
    combo.select(this.fInitialSelectionIndex);

    this.fSelection = combo.getItem(combo.getSelectionIndex());
    GridData gd = new GridData();
    int widthHint = convertWidthInCharsToPixels(getMaxStringLength()) + 21;
    gd.widthHint = (widthHint <= getParentShell().getSize().x ? widthHint : getParentShell().getSize().x - 42);
    combo.setLayoutData(gd);
    combo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        ComboSelectionDialog.this.fSelection = combo.getItem(combo.getSelectionIndex());
      }
    });
    applyDialogFont(composite);
    return composite;
  }

  private int getMaxStringLength() {
    int max = 0;
    for (String item : this.fAllowedStrings) {
      max = Math.max(max, item.length());
    }
    return max;
  }
}

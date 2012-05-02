/*
 * Copyright (c) 2009 Netcetera AG and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Netcetera AG: initial implementation
 */
package ch.netcetera.eclipse.projectconfig.ui.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

import ch.netcetera.eclipse.common.fieldeditor.AbstractEditableStringListFieldEditor;
import ch.netcetera.eclipse.common.squasher.PreferenceListSquasher;
import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.common.validator.UriInputValidator;

/**
 * A string list field editor that allows to edit the items of the list.
 */
public class EditableStringListFieldEditor extends AbstractEditableStringListFieldEditor {

  /**
   * Constructor.
   *
   * @param name the name of the preference this field editor edits
   * @param labelText the label text
   * @param parent the parent {@link Composite}
   * @param textAccessor the text accessor
   */
  protected EditableStringListFieldEditor(String name, String labelText, Composite parent, ITextAccessor textAccessor) {
    super(name, labelText, parent, textAccessor);
  }

  /** {@inheritDoc} */
  @Override
  public String getNewListItem() {
    InputDialog dialog = new InputDialog(getAddButtonShell(),
       getTextAccessor().getText("preference.dialog.title"), getTextAccessor().getText("preference.dialog.text"),
        null, new UriInputValidator(getListItems(), null, getTextAccessor()));
    if (dialog.open() == Window.OK) {
      return dialog.getValue().trim();
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public  String editListItem(String item) {
    InputDialog dialog = new InputDialog(getEditButtonShell(),
        getTextAccessor().getText("preference.dialog.title"), getTextAccessor().getText("preference.dialog.text"),
        item, new UriInputValidator(getListItems(), item, getTextAccessor()));
    if (dialog.open() == Window.OK) {
      return dialog.getValue().trim();
    } else {
      return item;
    }
  }
  
  /** {@inheritDoc} */
  @Override
  protected List<String> parsePreferenceString(String preferenceString) {
    return PreferenceListSquasher.splitListItemsToStringArray(preferenceString);
  }

  /** {@inheritDoc} */
  @Override
  protected String marshallPreferenceString(String[] items) {
    return PreferenceListSquasher.combineListItemsToPreferenceString(items);
  }
}

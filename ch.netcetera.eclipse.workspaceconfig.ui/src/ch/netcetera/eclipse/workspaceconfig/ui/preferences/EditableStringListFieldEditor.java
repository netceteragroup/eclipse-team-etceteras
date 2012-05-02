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
package ch.netcetera.eclipse.workspaceconfig.ui.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

import ch.netcetera.eclipse.common.fieldeditor.AbstractEditableStringListFieldEditor;
import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.common.validator.StringListInputValidator;
import ch.netcetera.eclipse.workspaceconfig.ui.WorkspaceConfigurationUIPlugin;

/**
 * A string list field editor that allows to edit the items of the list.
 */
public class EditableStringListFieldEditor extends AbstractEditableStringListFieldEditor {

  private ConfigurationXMLMarshaler marshaler = new ConfigurationXMLMarshaler();
  
  /**
   * Constructor.
   *
   * @param name the name of the preference this field editor edits
   * @param labelText the label text
   * @param parent the parent {@link Composite}
   * @param textAccessor the text accessor
   */
  protected EditableStringListFieldEditor(String name, String labelText, Composite parent,
      ITextAccessor textAccessor) {
    super(name, labelText, parent, textAccessor);
  }

  /** {@inheritDoc} */
  @Override
  public String getNewListItem() {
    InputDialog dialog = new InputDialog(getAddButtonShell(),
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.dialog.title"),
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.dialog.text"), null,
        new StringListInputValidator(getListItems(), null, getTextAccessor()));
    if (dialog.open() == Window.OK) {
      return dialog.getValue();
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public  String editListItem(String item) {
    InputDialog dialog = new InputDialog(getEditButtonShell(),
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.dialog.title"),
        WorkspaceConfigurationUIPlugin.getDefault().getText("preference.dialog.text"), item,
        new StringListInputValidator(getListItems(), item, getTextAccessor()));
    if (dialog.open() == Window.OK) {
      return dialog.getValue();
    } else {
      return item;
    }
  }

  /** {@inheritDoc} */
  @Override
  protected List<String> parsePreferenceString(String preferenceString) {
    return ConfigurationUtil.getEnvReplacements();
  }

  /** {@inheritDoc} */
  @Override
  protected String marshallPreferenceString(String[] items) {
    List<String> envVarList = new ArrayList<String>(items.length);
    for (String string : items) {
      envVarList.add(string);
    }
    try {
      return marshaler.marshalEnvVarList(envVarList);
    } catch (IOException e) {
      return "";
    }
  }
}

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
import ch.netcetera.eclipse.common.validator.UriInputValidator;

/**
 * A string list field editor that allows to edit the items of the list.
 */
public class EditableUrlStringListFieldEditor extends AbstractEditableStringListFieldEditor {

  private ConfigurationXMLMarshaler marshaler = new ConfigurationXMLMarshaler();

  /**
   * Constructor.
   *
   * @param name the name of the preference this field editor edits
   * @param labelText the label text
   * @param parent the parent {@link Composite}
   * @param textAccessor the text accessor
   */
  protected EditableUrlStringListFieldEditor(String name, String labelText, Composite parent,
      ITextAccessor textAccessor) {
    this(name, labelText, parent, textAccessor, false);
  }
  /**
   * Constructor.
   *
   * @param name the name of the preference this field editor edits
   * @param labelText the label text
   * @param parent the parent {@link Composite}
   * @param textAccessor the text accessor
   * @param isReorderable {@code true} if a reorderable list shall be created
   */
  protected EditableUrlStringListFieldEditor(String name, String labelText, Composite parent,
      ITextAccessor textAccessor, boolean isReorderable) {
    super(name, labelText, parent, textAccessor, isReorderable);
  }

  /** {@inheritDoc} */
  @Override
  public String getNewListItem() {
    InputDialog dialog = new InputDialog(getAddButtonShell(),
       getTextAccessor().getText("preference.dialog.url.title"),
       getTextAccessor().getText("preference.dialog.url.text"),
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
        getTextAccessor().getText("preference.dialog.url.title"),
        getTextAccessor().getText("preference.dialog.url.text"),
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
    return ConfigurationUtil.getEpfUrls();
  }

  /** {@inheritDoc} */
  @Override
  protected String marshallPreferenceString(String[] items) {
    List<String> epfUrlList = new ArrayList<>(items.length);
    for (String string : items) {
      epfUrlList.add(string);
    }
    try {
      return marshaler.marshalEpfUrlList(epfUrlList);
    } catch (IOException e) {
      return "";
    }
  }
}

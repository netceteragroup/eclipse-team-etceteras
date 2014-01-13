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
package ch.netcetera.eclipse.common.validator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Validator for string list entries.
 */
public final class StringListInputValidator implements IInputValidator {

  private final List<String> itemList;
  private final ITextAccessor textAccessor;

  /**
   * Constructor.
   *
   * @param variableList the list of existing variables
   * @param itemToEdit the item that gets edited (pass NULL in case of new item)
   * @param textAccessor the text accessor
   */
  public StringListInputValidator(List<String> variableList, String itemToEdit, ITextAccessor textAccessor) {
    this.textAccessor = textAccessor;
    this.itemList = new ArrayList<String>(variableList);

    // remove the original item if an item is edited
    if (itemToEdit != null) {
      this.itemList.remove(itemToEdit);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String isValid(String text) {

    // null and empty texts are not validated
    if (text == null || text.trim().isEmpty()) {
      return null;
    }

    // make sure the separator is not present
    String trimmedText = text.trim();
    if (trimmedText.contains("#")) {
      return this.textAccessor.getText("var.validation.error.invalidchar");
    }

    // make sure the new variable is unique
    if (this.itemList.contains(trimmedText)) {
      return this.textAccessor.getText("var.validation.error.notunique");
    }
    return null;
  }
}

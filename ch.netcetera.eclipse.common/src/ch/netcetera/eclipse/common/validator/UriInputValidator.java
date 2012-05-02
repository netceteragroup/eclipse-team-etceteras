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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Validator for URIs supported by the plug-in.
 */
public final class UriInputValidator implements IInputValidator {

  private final List<String> itemList;
  private final ITextAccessor textAccessor;

  /**
   * Constructor.
   *
   * @param uris the list of existing uris
   * @param itemToEdit the item that gets edited (pass NULL in case of new item)
   * @param textAccessor the text accessor
   */
  public UriInputValidator(List<String> uris, String itemToEdit, ITextAccessor textAccessor) {
    this.textAccessor = textAccessor;
    this.itemList = new ArrayList<String>(uris.size());
    this.itemList.addAll(uris);

    // remove the original item if an item is edited
    if (itemToEdit != null) {
      this.itemList.remove(itemToEdit);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String isValid(String text) {

    // null and empty texts are not validated
    if (text == null || text.trim().isEmpty()) {
      return null;
    }

    // make sure the protocol is supported
    String trimmedText = text.trim();
    String trimmedTextLower = trimmedText.toLowerCase();
    if (!(trimmedText.toLowerCase().startsWith("http:/")
        || trimmedTextLower.startsWith("https:/")
        || trimmedText .toLowerCase().startsWith("file:/"))) {
      return this.textAccessor.getText("url.validation.error.unsupported");
    }

    // make sure the separator is not present
    if (trimmedText.contains("#")) {
      return this.textAccessor.getText("url.validation.error.invalidchar");
    }

    // make sure the new URL is unique
    if (this.itemList.contains(trimmedText)) {
      return this.textAccessor.getText("url.validation.error.notunique");
    }

    // try to create an URI object
    try {
      URI uri = new URI(trimmedText);
      if (trimmedTextLower.startsWith("http") || uri.getAuthority() == null) {
        return null;
      } else {
        return this.textAccessor.getText("url.validation.error.invalid");
      }
    } catch (URISyntaxException e) {
      return this.textAccessor.getText("url.validation.error.invalid");
    }
  }
}

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

import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.jface.dialogs.IInputValidator;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Abstract superclass of tests testing {@link IInputValidator}.
 */
public abstract class AbstractInputValidatorTest {

  /**
   * Helper method doing the boring work of calling isValid().
   *
   * @param url the url to test
   * @param existingItems the existing items
   * @param textLookupKey the text lookup key
   * @param textLookupValue the text lookup value
   * @param itemToEdit the item to edit
   * @return the validation result
   */
  String runIsValid(String url, List<String> existingItems, String textLookupKey, String textLookupValue, 
      String itemToEdit) {
    ITextAccessor textAccessor = EasyMock.createMock(ITextAccessor.class);
    if (textLookupKey.length() > 0) {
      EasyMock.expect(textAccessor.getText(textLookupKey)).andReturn(textLookupValue);
    }
    EasyMock.replay(textAccessor);
    IInputValidator validator = getInputValidatorInstance(existingItems, itemToEdit, textAccessor);
    String result = validator.isValid(url);
    EasyMock.verify(textAccessor);
    return result;
  }

  /**
   * Gets the {@link IInputValidator} instance under test.
   *
   * @param existingItems the existing items
   * @param itemToEdit the item to edit
   * @param textAccessor the {@link ITextAccessor}
   * @return the instance
   */
  abstract IInputValidator getInputValidatorInstance(List<String> existingItems, String itemToEdit, 
      ITextAccessor textAccessor);
}

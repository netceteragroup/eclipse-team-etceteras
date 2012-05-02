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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.netcetera.eclipse.common.text.ITextAccessor;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link StringListInputValidator}.
 */
@RunWith(Parameterized.class)
public class JreSystemPropertyInputValidatorTest extends AbstractInputValidatorTest {

  private final String variable;
  private final List<String> existingVariables;
  private final String textKey;
  private final String textValue;
  private final String item;
  private final String expected;

  /**
   * Constructor.
   *
   * @param expected the expected result
   * @param variable the variable
   * @param existingVariables the existing variables
   * @param textKey the text lookup key
   * @param textValue the text value
   * @param item the item to edit (only in case of edit)
   */
  public JreSystemPropertyInputValidatorTest(String expected, String variable, List<String> existingVariables,
      String textKey, String textValue, String item) {
    this.variable = variable;
    this.existingVariables = existingVariables;
    this.textKey = textKey;
    this.textValue = textValue;
    this.item = item;
    this.expected = expected;
  }

  /**
   * Gets the test data.
   *
   * @return the test data
   */
  @Parameters
  public static List<Object[]> data() {
    return Arrays.asList(
      new Object[]{null, null, Collections.emptyList(), "", "", null},
      new Object[]{null, "", Collections.emptyList(), "", "", null},
      new Object[]{null, "  \t", Collections.emptyList(), "", "", null},
      new Object[]{"invalidchar", "user.name#", Collections.emptyList(), "var.validation.error.invalidchar", 
          "invalidchar", null},
      new Object[]{"notunique", "user.name", Arrays.asList("user.name"), "var.validation.error.notunique", 
          "notunique", null},
      new Object[]{null, "user.name", Arrays.asList("user.name"), "", "", "user.name"},
      new Object[]{null, "user.name", Collections.emptyList(), "", "", null});
  }

  /**
   * Tests {@link UriInputValidator#isValid(String)}.
   */
  @Test
  public void testIsValid() {
      assertEquals(this.expected, runIsValid(this.variable, this.existingVariables, this.textKey, this.textValue, 
          this.item));
  }

  /** 
   * {@inheritDoc} 
   */
  @Override
  IInputValidator getInputValidatorInstance(List<String> existingItems, String itemToEdit, ITextAccessor textAccessor) {
    return new StringListInputValidator(existingItems, itemToEdit, textAccessor);
  }
}

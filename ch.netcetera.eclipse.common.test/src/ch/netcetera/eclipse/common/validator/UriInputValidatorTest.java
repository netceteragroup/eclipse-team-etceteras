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
 * Tests for {@link UriInputValidator}.
 */
@RunWith(Parameterized.class)
public class UriInputValidatorTest extends AbstractInputValidatorTest {

  private final String url;
  private final List<String> existingUrls;
  private final String textKey;
  private final String textValue;
  private final String item;
  private final String expected;

  /**
   * Constructor.
   *
   * @param expected the expected result
   * @param url the URL
   * @param existingUrls the existing URLs
   * @param textKey the text lookup key
   * @param textValue the text value
   * @param item the item to edit (only in case of edit)
   */
  public UriInputValidatorTest(String expected, String url, List<String> existingUrls, String textKey,
      String textValue, String item) {
    this.url = url;
    this.existingUrls = existingUrls;
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
        new Object[]{"unsupported", "ftp://bla.com", Collections.emptyList(), "url.validation.error.unsupported",
            "unsupported", null},
        new Object[]{"invalidchar", "http://bla.com#", Collections.emptyList(), "url.validation.error.invalidchar",
            "invalidchar", null},
        new Object[]{"notunique", "http://foo.bar", Arrays.asList("http://foo.bar"), "url.validation.error.notunique",
            "notunique", null},
        new Object[]{null, "http://foo.bar", Arrays.asList("http://foo.bar"), "", "", "http://foo.bar"},
        new Object[]{"invalid", "file://[]bla.com", Collections.emptyList(), "url.validation.error.invalid", "invalid",
            null},
        new Object[]{null, "file:/home/bla", Collections.emptyList(), "", "", null},
        new Object[]{null, "file:/C:/foo/bla", Collections.emptyList(), "", "", null},
        new Object[]{"invalid", "file://C:/foo/bla", Collections.emptyList(), "url.validation.error.invalid", "invalid",
            null},
        new Object[]{"invalid", "file://foo/bla", Collections.emptyList(), "url.validation.error.invalid", "invalid",
            null},
        new Object[]{null, "http://foo.com", Collections.emptyList(), "", "", null});
  }

  /**
   * Tests {@link UriInputValidator#isValid(String)}.
   */
  @Test
  public void testIsValid() {
    assertEquals(this.expected, runIsValid(this.url, this.existingUrls, this.textKey, this.textValue, this.item));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  IInputValidator getInputValidatorInstance(List<String> existingItems, String itemToEdit, ITextAccessor textAccessor) {
    return new UriInputValidator(existingItems, itemToEdit, textAccessor);
  }
}

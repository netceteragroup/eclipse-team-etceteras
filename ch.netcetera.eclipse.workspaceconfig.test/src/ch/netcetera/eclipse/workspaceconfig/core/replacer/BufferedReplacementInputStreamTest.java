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
package ch.netcetera.eclipse.workspaceconfig.core.replacer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.netcetera.eclipse.workspaceconfig.core.internal.BufferedReplacementInputStream;
import ch.netcetera.eclipse.workspaceconfig.core.internal.IReplacer;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link BufferedReplacementInputStream}.
 */
@RunWith(Parameterized.class)
public class BufferedReplacementInputStreamTest {

  private static final String ENCODING = "UTF-8";
  private static final int BUFFER_SIZE = 1000;
  private final String expected;
  private final IReplacer replacer;
  private final String input;

  /**
   * Constructor.
   *
   * @param expected the expected result
   * @param replacer the replacer to use
   * @param input the input as string (restriction in this test: length {@code BUFFER_SIZE}  characters!)
   */
  public BufferedReplacementInputStreamTest(String expected, IReplacer replacer, String input) {
    if (input.length() > BUFFER_SIZE) {
      throw new IllegalArgumentException("The max length for 'input' is " + BUFFER_SIZE
          + ", but was " + input.length());
    }
    this.expected = expected;
    this.replacer = replacer;
    this.input = input;
  }

  /**
   * Initializes the test data.
   *
   * @return the test data
   */
  @Parameters
  public static List<Object[]> data() {
    return Arrays.asList(
        new Object[]{"", null, ""},
        new Object[]{"aa\n", null, "aa"},
        new Object[]{"bba\nCCB\n", new TestStringReplacer("cc", "CC"), "bba\nccB"},
        new Object[]{"data\n", new TestNullReplacer(), "data"}
        );
  }

  /**
   * Tests {@link BufferedReplacementInputStream#BufferedReplacementInputStream(IReplacer, java.io.InputStream)}
   * with null arguments.
   *
   * @throws IOException on error
   */
  @Test
  public void testConstructorNullArguments() throws IOException {
    try (BufferedReplacementInputStream bris = new BufferedReplacementInputStream(replacer,
        new ByteArrayInputStream(new StringBuffer(input).toString().getBytes(ENCODING)));) {
      byte[] result = new byte[BUFFER_SIZE];
      int read = bris.read(result);
      String stringResult = new String(result, ENCODING).substring(0, read == -1 ? 0 : read);
      assertEquals(expected, stringResult);
    }
  }
}

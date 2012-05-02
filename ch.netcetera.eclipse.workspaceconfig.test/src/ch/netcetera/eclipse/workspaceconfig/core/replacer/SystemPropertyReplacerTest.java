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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.netcetera.eclipse.workspaceconfig.core.internal.IReplacer;
import ch.netcetera.eclipse.workspaceconfig.core.internal.SystemPropertyReplacer;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SystemPropertyReplacerTest}.
 */
@RunWith(Parameterized.class)
public class SystemPropertyReplacerTest {

  private final String expected;
  private final Map<String, String> replacementMap;
  private final String input;
  private IReplacer replacer;
  
  /**
   * Constructor.
   * 
   * @param expected the expected result
   * @param replacementMap the replacements to be made
   * @param input the input
   */
  public SystemPropertyReplacerTest(String expected, Map<String, String> replacementMap, String input) {
    this.expected = expected;
    this.replacementMap = replacementMap;
    this.input = input;
  }

  /**
   * Gets the test data.
   * 
   * @return the test data
   */
  @Parameters
  public static List<Object[]> data() {
    return Arrays.asList(
        new Object[]{"v1", new HashMap<String, String>() { { put("junit1", "v1"); } }, "$junit1"}, //NOPMD
        new Object[]{"v1 v2",
            new HashMap<String, String>() { { put("junit1", "v1"); put("junit2", "v2"); } }, // NOPMD
            "$junit1 $junit2"},
        new Object[]{"$foo", new HashMap<String, String>() { { put("junit2", "v1"); } }, "$foo"}); // NOPMD
  }
  
  /**
   * Initializes the test data and sets up the system properties used for the test.
   */
  @Before
  public void setUp() {
    replacer = new SystemPropertyReplacer(new ArrayList<String>(replacementMap.keySet()));

    for (String sysPropName : replacementMap.keySet()) {

      // only set system properties that do not exist yet
      if (System.getProperty(sysPropName) != null) {
        throw new IllegalArgumentException(
            "Tests shall only be conducted with non existin system properties (property '"
                + sysPropName + "' exists)");
      }
      System.setProperty(sysPropName, replacementMap.get(sysPropName));
    }
  }

  /**
   * Removes system properties set for the test.
   */
  @After
  public void cleanUp() {
    for (String sysPropName : replacementMap.keySet()) {
      System.clearProperty(sysPropName);
    }
  }
  
  /**
   * Tests {@link SystemPropertyReplacer#replace(String)}.
   */
  @Test
  public void testReplace() {
    assertEquals(expected, replacer.replace(input));
  }
}

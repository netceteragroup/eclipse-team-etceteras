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
package ch.netcetera.eclipse.common.squasher;


import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link PreferenceListSquasher}.
 */
@RunWith(Parameterized.class)
public class PreferenceListSquasherTestCombine {

  private final String expected;
  private final String[] listItems;

  /**
   * Constructor.
   *
   * @param expected the expected result
   * @param listItems the list items to combine
   */
  public PreferenceListSquasherTestCombine(String expected, String[] listItems) {
    this.expected = expected;
    this.listItems = listItems;
  }

  /**
   * Gets the test data.
   *
   * @return the test data
   */
  @Parameters
  public static List<Object[]> data() {
    return Arrays.asList(
        new Object[]{"", null},
        new Object[]{"", new String[]{}},
        new Object[]{"FOO#BAR#", new String[]{"FOO", "BAR"}});
  }

  /**
   * Tests {@link PreferenceListSquasher#combineListItemsToPreferenceString(String[])}
   * with a null input array.
   */
  @Test
  public void testCombineListItemsToPreferenceString() {
    assertEquals(this.expected, PreferenceListSquasher.combineListItemsToPreferenceString(this.listItems));
  }
}

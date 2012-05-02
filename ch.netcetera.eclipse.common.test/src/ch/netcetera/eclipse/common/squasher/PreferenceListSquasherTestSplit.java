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
public class PreferenceListSquasherTestSplit {

  private final String preferenceString;
  private final Integer expectedCount;

  /**
   * Constructor.
   *
   * @param expectedCount the expected number of items after the split
   * @param preferenceString the preference string to split
   */
  public PreferenceListSquasherTestSplit(Integer expectedCount, String preferenceString) {
    this.expectedCount = expectedCount;
    this.preferenceString = preferenceString;
  }

  /**
   * Gets the test data.
   *
   * @return the test data
   */
  @Parameters
  public static List<Object[]> data() {
    return Arrays.asList(
        new Object[]{0, null},
        new Object[]{0, ""},
        new Object[]{2, "FOO#BAR#"},
        new Object[]{2, "#FOO#BAR##"});
  }

  /**
   * Tests {@link PreferenceListSquasher#splitListItemsToStringArray(String)}.
   */
  @Test
  public void testSplitListItemsToStringArray() {
    assertEquals(this.expectedCount.intValue(), 
        PreferenceListSquasher.splitListItemsToStringArray(this.preferenceString).size());
  }
}

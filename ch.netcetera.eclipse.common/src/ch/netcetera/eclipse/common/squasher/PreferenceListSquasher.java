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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that offers splitting and assembly functionality for the
 * lists of strings stored as a single string in the preference store.
 */
public final class PreferenceListSquasher {

  private static final String SEPARATOR = "#";

  /**
   * Private constructor to avoid instantiation.
   */
  private PreferenceListSquasher() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Combines the list of items into a single string that is used as preference
   * value.
   *
   * @param listItems the list's items
   * @return the preference string
   */
  public static String combineListItemsToPreferenceString(String[] listItems) {
    StringBuffer buffer = new StringBuffer();
    if (listItems != null) {
      for (int i = 0; i < listItems.length; i++) {
        buffer.append(listItems[i]);
        if (i < listItems.length) {
          buffer.append(SEPARATOR);
        }
      }
    }
    return buffer.toString();
  }

  /**
   * Splits the preference string passed into different list items. Null length
   * entries are omitted.
   *
   * @param preferenceString the preference string to split
   * @return the list items
   */
  public static List<String> splitListItemsToStringArray(String preferenceString) {
    List<String> resultList = new ArrayList<>();
    if (preferenceString != null) {
      String[] splitArray = preferenceString.split(SEPARATOR);
      for (String item : splitArray) {
        if (item.length() > 0) {
          resultList.add(item);
        }
      }
    }
    return resultList;
  }
}

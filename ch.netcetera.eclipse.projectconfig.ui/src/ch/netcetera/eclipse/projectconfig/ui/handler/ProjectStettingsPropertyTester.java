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
package ch.netcetera.eclipse.projectconfig.ui.handler;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.netcetera.eclipse.projectconfig.ui.ProjectConfigurationUIPlugin;

/**
 * Property tester for workspace settings.
 */
public final class ProjectStettingsPropertyTester extends PropertyTester {

  /**
   * The name of the property whether the settings URL is configured.
   */
  public static final String URL_CONFIGURED = "urlConfigured";

  /**
   * {@inheritDoc}
   */
  public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
    if (URL_CONFIGURED.equals(property)) {
      IPreferenceStore preferences = ProjectConfigurationUIPlugin.getDefault().getPreferenceStore();
      String url = preferences.getString(ProjectConfigurationUIPlugin.CONFIG_CMDFILE_URL);
      return url.length() > 0 == toBoolean(expectedValue);
    }
    return false;
  }

  /**
   * Converts the given expected value to a boolean.
   *
   * @param expectedValue the expected value (may be <code>null</code>).
   * @return <code>false</code> if the expected value equals Boolean.FALSE,
   * <code>true</code> otherwise
   */
  private static boolean toBoolean(Object expectedValue) {
    if (expectedValue instanceof Boolean) {
      return ((Boolean) expectedValue).booleanValue();
    }
    return true;
  }
}

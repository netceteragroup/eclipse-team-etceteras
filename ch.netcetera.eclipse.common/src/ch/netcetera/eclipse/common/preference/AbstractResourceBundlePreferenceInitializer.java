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
package ch.netcetera.eclipse.common.preference;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * Abstract preference initializer that reads the preferences from a {@link ResourceBundle}.
 */
public abstract class AbstractResourceBundlePreferenceInitializer extends
    AbstractPreferenceInitializer {

  private ResourceBundle resourceBundle;

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract void initializeDefaultPreferences();

  /**
   * Gets a string for the given key from the resource bundle. If there is no
   * value associated with the key passed, the default value is returned.
   *
   * @param key the key of the value used for the lookup
   * @param defaultValue the default value
   * @return the value belonging to the key or the default value in case there
   * is no entry for key
   */
  protected String getConfigValue(String key, String defaultValue) {
    String value = defaultValue;
    if (this.resourceBundle != null && this.resourceBundle.containsKey(key)) {
      value = this.resourceBundle.getString(key);
      if (value == null) {
        value = defaultValue;
      }
    }
    return value;
  }

  /**
   * Sets the resource bundle.
   *
   * @param resourceBundle the resource bundle to set
   */
  public void setResourceBundle(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  /**
   * Gets the resource bundle.
   *
   * @return the resource bundle
   */
  public ResourceBundle getResourceBundle() {
    return this.resourceBundle;
  }
}

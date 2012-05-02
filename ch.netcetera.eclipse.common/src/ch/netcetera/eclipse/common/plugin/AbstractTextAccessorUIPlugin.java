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
package ch.netcetera.eclipse.common.plugin;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * {@link AbstractUIPlugin} that additionally implements the {@link ITextAccessor} interface.
 */
public abstract class AbstractTextAccessorUIPlugin extends AbstractUIPlugin implements ITextAccessor {

  /**
   * Gets the resource bundle of the actual plugin.
   *
   * @return the resource bundle
   */
  public abstract ResourceBundle getResourceBundle();

  /** 
   * {@inheritDoc} 
   */
  public String getText(String key) {
    return getText(key, null);
  }

  /** 
   * {@inheritDoc} 
   */
  public String getText(String key, Object[] arguments) {
    ResourceBundle resourceBundle = getResourceBundle();
    if (resourceBundle != null) {
      try {
        String text = resourceBundle.getString(key);
        if (arguments != null && arguments.length > 0) {
          text = MessageFormat.format(text, arguments);
        } else {
          text = MessageFormat.format(text, new Object[]{});
        }
        return text;
      } catch (MissingResourceException mre) {
        return "missing resource=" + key;
      }
    } else {
      return "missing resource bundle";
    }
  }
}

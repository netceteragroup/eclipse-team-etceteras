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

import ch.netcetera.eclipse.workspaceconfig.core.internal.IReplacer;

/**
 * {@link IReplacer} implementation based on
 * {@link String#replaceAll(String, String)} used for unit tests.
 */
public class TestStringReplacer implements IReplacer {

  private final String regex;
  private final String replacement;
  
  /**
   * Constructor.
   * 
   * @param regex the regular expression used to replace
   * @param replacement the replacement
   */
  public TestStringReplacer(String regex, String replacement) {
    this.regex = regex;
    this.replacement = replacement;
  }

  /** {@inheritDoc} */
  @Override
  public String replace(String line) {
    return line.replaceAll(regex, replacement);
  }
}

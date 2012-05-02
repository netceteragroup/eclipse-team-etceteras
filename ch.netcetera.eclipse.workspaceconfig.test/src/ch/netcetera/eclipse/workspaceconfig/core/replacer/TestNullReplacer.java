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
 * Null {@link IReplacer} implementation used in unit tests. 
 */
public class TestNullReplacer implements IReplacer {

  /** {@inheritDoc} */
  public String replace(String line) {
    return line;
  }
}

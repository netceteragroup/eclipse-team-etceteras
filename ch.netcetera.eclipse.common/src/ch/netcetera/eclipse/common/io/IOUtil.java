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
package ch.netcetera.eclipse.common.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Utility class to handle some common IO tasks.
 */
public final class IOUtil {

  /**
   * Private default constructor.
   */
  private IOUtil() {
    throw new AssertionError("not instantiable.");
  }
  
  
  /**
   * Closes a {@link Closeable} silently, which means that all errors are caught.
   *
   * @param closable the closeable to close
   */
  public static void closeSilently(Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException e) {
        // don't do anything -> close SILENTLY
      }
    }
  }
}

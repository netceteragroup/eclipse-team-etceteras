/*
 * Copyright (c) 2009 the Eclipsed Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Eclipse Team Etceteras - http://ete.kenai.com
 */
package ch.netcetera.eclipse.common.security;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Helper class that handles all operations involving the Equinox Secure
 * Preferences service.
 */
public final class SecurePreferencesUtil {

  /**
   * Private default constructor to avoid instantiation.
   */
  private SecurePreferencesUtil() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Stores a the value using a key in the secure preference node having the
   * node path name passed.
   *
   * @param nodePathName the node path name
   * @param key the key
   * @param value the value to store
   * @throws StorageException if an exception occurred during encryption
   */
  public static void store(String nodePathName, String key, String value) throws StorageException {
    getNode(nodePathName).put(key, value, true);
  }

  /**
   * Stores a the value using a key in the secure preference node having the
   * node path name passed. This operation is done silently which means that
   * the caller is not informed about any problem that happened during the
   * storage of the value.
   *
   * @param nodePathName the node path name
   * @param key the key
   * @param value the value to store
   */
  public static void storeSilently(String nodePathName, String key, String value) {
    try {
      store(nodePathName, key, value);
    } catch (StorageException e) {
      // do nothing
    }
  }

  /**
   * Gets the value associated with the key from the secure preference node
   * having the node path name passed.
   *
   * @param nodePathName the node path name
   * @param key the key
   * @return the value
   * @throws StorageException if an exception occurred during decryption
   */
  public static String get(String nodePathName, String key) throws StorageException {
    return getNode(nodePathName).get(key, "");
  }

  /**
   * Gets the value associated with the key from the secure preference node
   * having the node path name passed or if that does not exist or there is a
   * problem during decryption the default value.
   *
   * @param nodePathName the node path name
   * @param key the key
   * @param defaultValue the default value
   * @return the value
   */
  public static String getSilentlyWithDefault(String nodePathName, String key, String defaultValue) {
    try {
      return getNode(nodePathName).get(key, defaultValue);
    } catch (StorageException e) {
      return defaultValue;
    }
  }

  /**
   * Removes the value associated with the key from the secure preference node
   * having the node path name passed. This operation is done silently which
   * means that the caller is not informed about any problem that happened
   * during the removal of the value.
   *
   * @param nodePathName the node path name
   * @param key the key
   */
  public static void removeSilently(String nodePathName, String key) {
    try {
      getNode(nodePathName).remove(key);
    } catch (IllegalStateException e) {
      // do nothing, node was removed before and the goal of the call is already met
    }
  }

  /**
   * Gets the node with the passed path name from the secure preferences factory.
   *
   * @param nodePathName the node path name
   * @return the node
   */
  private static ISecurePreferences getNode(String nodePathName) {
    return SecurePreferencesFactory.getDefault().node(nodePathName);
  }
}

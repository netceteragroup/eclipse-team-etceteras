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

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the text resource handling of the {@link AbstractTextAccessorUIPlugin}.
 */
public class AbstractTextAccessorUIPluginTest {

  /**
   * Test implementation that allows to inject the resource bundle
   */
  private static class TextAccessorUIPlugin extends AbstractTextAccessorUIPlugin {

    private final ResourceBundle resourceBundle;

    TextAccessorUIPlugin(ResourceBundle resourceBundle) {
      this.resourceBundle = resourceBundle;
    }

    /** {@inheritDoc} */
    @Override
    public ResourceBundle getResourceBundle() {
      return this.resourceBundle;
    }
  }

  private static final String RESOURCE_BUNDLE_NAME = "ch.netcetera.eclipse.common.plugin.messages";

  private TextAccessorUIPlugin plugin;

  /**
   * Set up the test fixture.
   */
  @Before
  public void setUpPlugin() {
    ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, Locale.ENGLISH);
    this.plugin = new TextAccessorUIPlugin(resourceBundle);
  }

  /**
   * Tests {@link AbstractTextAccessorUIPlugin#getText(String)} and
   * {@link AbstractTextAccessorUIPlugin#getText(String, Object[])} with a
   * missing resource bundle.
   */
  @Test
  public void testGetTextNullResourceBundle() {
    TextAccessorUIPlugin plugin = new TextAccessorUIPlugin(null);
    assertEquals("missing resource bundle", plugin.getText("foo"));
    assertEquals("missing resource bundle", plugin.getText("foo", new String[]{"bar"}));
  }

  /**
   * Tests {@link AbstractTextAccessorUIPlugin#getText(String)}.
   */
  @Test
  public void testGetText() {
    assertEquals("bar", this.plugin.getText("foo"));
  }

  /**
   * Tests {@link AbstractTextAccessorUIPlugin#getText(String)}.
   */
  @Test
  public void testGetTextMissing() {
    assertEquals("missing resource=missing", this.plugin.getText("missing"));
  }

  /**
   * Tests {@link AbstractTextAccessorUIPlugin#getText(String, Object[])}.
   */
  @Test
  public void testGetTextMsgFormat() {
    assertEquals("values: one two", this.plugin.getText("msgformat", new String[]{"one", "two"}));
  }
}

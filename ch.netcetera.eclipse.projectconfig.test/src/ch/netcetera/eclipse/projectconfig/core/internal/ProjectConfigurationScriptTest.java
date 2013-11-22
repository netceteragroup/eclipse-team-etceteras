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
package ch.netcetera.eclipse.projectconfig.core.internal;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;

import ch.netcetera.eclipse.projectconfig.core.ProjectConfigurationScript;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.CommentProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.IProjectConfigurationCommand;

import static ch.netcetera.eclipse.projectconfig.core.configurationcommands.PluginIdMatcher.hasPluginId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ProjectConfigurationScript}.
 */
public class ProjectConfigurationScriptTest {

  private static final String URL = "url";
  private ProjectConfigurationScript script;

  /**
   * Prepare the test data.
   */
  @Before
  public void initData() {
    this.script = new ProjectConfigurationScript(URL);
  }

  /**
   * Tests {@link ProjectConfigurationScript#ProjectConfigurationScript(String)}.
   */
  @Test
  public void testConstructor() {
    assertEquals(URL, this.script.getUrl());
  }

  /**
   * Tests {@link ProjectConfigurationScript#setUrl(String)} and
   * {@link ProjectConfigurationScript#getUrl()}.
   */
  @Test
  public void testSetGetUrl() {
    final String url = "lru";
    this.script.setUrl(url);
    assertEquals(url, this.script.getUrl());
  }

  /**
   * Tests {@link ProjectConfigurationScript#setCommands(List)}
   * and {@link ProjectConfigurationScript#getCommandList()}.
   */
  @Test
  public void testSetGetCommands() {
    final List<IProjectConfigurationCommand> commands = Arrays.asList(
        (IProjectConfigurationCommand) new CommentProjectConfigurationCommand(null, null, "plugin1", null),
        (IProjectConfigurationCommand) new CommentProjectConfigurationCommand(null, null, "plugin2", null));
    this.script.setCommands(commands);
    assertThat(this.script.getCommandList().size(), Is.is(2));
    assertThat(this.script.getCommandList(), IsCollectionContaining.hasItem(hasPluginId("plugin1")));
  }
}

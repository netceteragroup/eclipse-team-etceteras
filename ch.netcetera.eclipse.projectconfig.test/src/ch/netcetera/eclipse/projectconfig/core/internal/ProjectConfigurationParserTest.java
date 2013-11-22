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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.projectconfig.core.ProjectConfigurationScript;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.IProjectConfigurationCommand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for {@link ProjectConfigurationParser}.
 */
public class ProjectConfigurationParserTest {

  /**
   * Tests {@link ProjectConfigurationParser#splitLine(String)}.
   */
  @Test
  public void testSplitLine() {
    final String line = " a  b c   d    ";
    List<String> elements = ProjectConfigurationParser.splitLine(line);
    assertEquals(4, elements.size());
    assertThat(elements, IsCollectionContaining.hasItem("a"));
    assertThat(elements, IsNot.not(IsCollectionContaining.hasItem(" ")));
    assertThat(elements,
        IsNot.not(IsCollectionContaining.hasItem(StringContains.containsString(" "))));
  }

  /**
   * Tests
   * {@link ProjectConfigurationParser# parse(ProjectConfigurationScript, InputStream, ITextAccessor, String, ILog)}
   * .
   *
   * @throws IOException on error
   */
  @Test
  public void testParse() throws IOException {
    final String scriptUrl = "scriptUrl";
    final ProjectConfigurationScript script = new ProjectConfigurationScript(scriptUrl);
    final ITextAccessor textAccessor = null;
    final String pluginId = null;
    final ILog log = null;

    try (InputStream epcsInputStream = getClass().getResourceAsStream("test.epcs");) {
      if (epcsInputStream != null) {

        ProjectConfigurationParser.parse(script, epcsInputStream, textAccessor, pluginId, log);

        // six commands (download is duplicated)
        List<IProjectConfigurationCommand> commands = script.getCommandList();
        assertEquals(6, commands.size());
      } else {
        fail("Could not open test epcs file.");
      }
    }
  }
}

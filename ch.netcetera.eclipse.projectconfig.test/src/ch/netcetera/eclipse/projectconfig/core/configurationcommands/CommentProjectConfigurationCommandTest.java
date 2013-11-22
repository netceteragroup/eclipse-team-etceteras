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
package ch.netcetera.eclipse.projectconfig.core.configurationcommands;

import java.util.Collections;

import org.easymock.EasyMock;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.junit.Before;
import org.junit.Test;

import ch.netcetera.eclipse.common.text.ITextAccessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for {@link CommentProjectConfigurationCommand}.
 */
public class CommentProjectConfigurationCommandTest {


  private CommentProjectConfigurationCommand command;

  /**
   * Initializes the test data.
   */
  @Before
  public void initData() {
    final String textKey = "";
    final String textValue = "value";
    final ITextAccessor textAccessor = EasyMock.createMock(ITextAccessor.class);
    EasyMock.expect(textAccessor.getText(textKey)).andReturn(textValue);
    EasyMock.replay(textAccessor);
    this.command = new CommentProjectConfigurationCommand(Collections.<String>emptyList(), textAccessor, "plugin",
        null);
  }

  /**
   * Tests {@link CommentProjectConfigurationCommand#execute(java.util.List)}.
   */
  @Test
  public void testExecute() {
    assertEquals(IStatus.OK, this.command.execute(Collections.<IProject>emptyList()).getSeverity());
  }

  /**
   * Tests {@link CommentProjectConfigurationCommand#execute(java.util.List)}.
   */
  @Test
  public void testExecuteOnProject() {
    assertEquals(IStatus.OK, this.command.executeOnProject(null).getSeverity());
  }

  /**
   * Tests {@link CommentProjectConfigurationCommand#execute(java.util.List)}.
   */
  @Test
  public void testIsEnabled() {
    this.command = new CommentProjectConfigurationCommand(Collections.<String>emptyList(), null, "plugin", null);
    assertFalse(this.command.isEnabled());
  }

}

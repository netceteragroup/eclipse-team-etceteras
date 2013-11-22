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

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Test;
import org.osgi.framework.Bundle;

import ch.netcetera.eclipse.common.text.ITextAccessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link AbstractProjectConfigurationCommand}.
 */
public class AbstractProjectConfigurationCommandTest {

  private static class TestProjectConfigurationCommand extends AbstractProjectConfigurationCommand {

    private final IStatus status;

    TestProjectConfigurationCommand(List<String> arguments, ITextAccessor textAccessor, String pluginId,
        ILog log, IStatus status) {
      super(arguments, textAccessor, pluginId, log);
      this.status = status;
    }

    @Override
    boolean isEnabled() {
      return true;
    }

    @Override
    IStatus executeOnProject(IProject project) {
      return this.status;
    }
  }

  /**
   * Tests the setters and getters.
   */
  @Test
  public void testSetterGetter() {
    final List<String> arguments = Arrays.asList("a", "b");
    final ITextAccessor textAccessor = EasyMock.createMock(ITextAccessor.class);
    EasyMock.replay(textAccessor);
    final ILog log = EasyMock.createMock(ILog.class);
    EasyMock.replay(log);
    final String pluginId = "plugin";

    TestProjectConfigurationCommand command = new TestProjectConfigurationCommand(arguments,
        textAccessor, pluginId, log, Status.OK_STATUS);
    assertEquals(pluginId, command.getPluginId());
    assertEquals(2, command.getArgumentList().size());
    assertNotNull(command.getTextAccessor());
    assertNotNull(command.getLog());
  }

  /**
   * Tests the createStatus methods.
   */
  @Test
  public void testCreateStatus() {
    final List<String> arguments = Arrays.asList("a", "b");
    final String messageKey = "message-key";
    final String messageValue = "message-value";
    final ITextAccessor textAccessor = EasyMock.createMock(ITextAccessor.class);
    EasyMock.expect(textAccessor.getText(messageKey)).andReturn(messageValue);
    EasyMock.replay(textAccessor);
    final ILog log = EasyMock.createMock(ILog.class);
    EasyMock.replay(log);
    final String pluginId = "plugin";

    TestProjectConfigurationCommand command = new TestProjectConfigurationCommand(arguments,
        textAccessor, pluginId, log, Status.OK_STATUS);

    IStatus status = command.createStatus(IStatus.ERROR, messageKey);
    assertNotNull(status);
    assertEquals(pluginId, status.getPlugin());
    assertEquals(IStatus.ERROR, status.getSeverity());
    assertEquals(messageValue, status.getMessage());
  }

  /**
   * Tests the execute method.
   */
  @Test
  public void testExecuteOk() {
    final List<String> arguments = Arrays.asList("a", "b");
    final ITextAccessor textAccessor = EasyMock.createMock(ITextAccessor.class);
    EasyMock.replay(textAccessor);
    final ILog log = EasyMock.createMock(ILog.class);
    EasyMock.replay(log);
    final String pluginId = "plugin";

    TestProjectConfigurationCommand command = new TestProjectConfigurationCommand(arguments,
        textAccessor, pluginId, log, Status.OK_STATUS);
    final IProject project = EasyMock.createMock(IProject.class);
    EasyMock.replay(project);
    List<IProject> projects = Arrays.asList(project);

    IStatus status = command.execute(projects);
    assertNotNull(status);
    assertEquals(IStatus.OK, status.getSeverity());
  }

  /**
   * Tests the execute method.
   */
  @Test
  public void testExecuteError() {
    final List<String> arguments = Arrays.asList("a", "b");
    final String messageKey = "error.config";
    final String messageValue = "message-value";
    final IStatus status = new Status(IStatus.ERROR, "a", messageKey);
    final ITextAccessor textAccessor = EasyMock.createMock(ITextAccessor.class);
    EasyMock.expect(textAccessor.getText(messageKey)).andReturn(messageValue);
    EasyMock.replay(textAccessor);
    final ILog log = new ILog() {

      @Override
      public void removeLogListener(ILogListener listener) {
        // nop
      }

      @Override
      public void log(IStatus astatus) {
        // nop
      }

      @Override
      public Bundle getBundle() {
        return null;
      }

      @Override
      public void addLogListener(ILogListener listener) {
        // nop
      }
    };

    final String pluginId = "plugin";

    TestProjectConfigurationCommand command = new TestProjectConfigurationCommand(arguments,
        textAccessor, pluginId, log, status);
    final IProject project = EasyMock.createMock(IProject.class);
    EasyMock.replay(project);
    List<IProject> projects = Arrays.asList(project);

    IStatus result = command.execute(projects);
    assertNotNull(result);
    assertEquals(IStatus.WARNING, result.getSeverity());
  }
}

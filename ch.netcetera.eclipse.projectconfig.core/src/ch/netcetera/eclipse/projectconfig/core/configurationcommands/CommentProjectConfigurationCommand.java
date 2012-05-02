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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * Project configuration command to handle comments.
 */
public class CommentProjectConfigurationCommand extends AbstractProjectConfigurationCommand {

  /** Command name that specifies the command handled by this class. */
  public static final String COMMAND_NAME = "#";

  /**
   * Constructor.
   *
   * @param argumentList the arguments
   * @param textAccessor the text accessor to retrieve text resources
   * @param pluginId the plugin id used for logging
   * @param log the log
   */
  public CommentProjectConfigurationCommand(List<String> argumentList, ITextAccessor textAccessor, String pluginId, 
      ILog log) {
    super(argumentList, textAccessor, pluginId, log);
  }

  /** 
   * {@inheritDoc} 
   */
  // override because comments don't need to be iterated
  @Override
  public IStatus execute(List<IProject> projectList) {
    // nothing to do for a comment
    return Status.OK_STATUS;
  }

  /**
   * {@inheritDoc} 
   */
  @Override
  IStatus executeOnProject(IProject project) {
    // nothing to do
    return Status.OK_STATUS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  boolean isEnabled() {
    // comments are always ignored: disabling to not being called
    return false;
  }
}

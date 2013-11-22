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
package ch.netcetera.eclipse.projectconfig.core;

import java.util.ArrayList;
import java.util.List;

import ch.netcetera.eclipse.projectconfig.core.configurationcommands.IProjectConfigurationCommand;

/**
 * The project configuration script represents one EPCS file containing a metadata
 * object and a list of commands to execute.
 */
public class ProjectConfigurationScript {

  /** Key of the script version metadata tag. */
  public static final String SCRIPT_METADATA_KEY_VERSION = "version";

  private List<IProjectConfigurationCommand> commandList = new ArrayList<>();
  private String url = "";

  /**
   * Constructor.
   *
   * @param url the URL of the command file
   */
  public ProjectConfigurationScript(String url) {
    this.url = url;
  }

  /**
   * Sets the commands.
   *
   * @param commandList the commands to set
   */
  public void setCommands(List<IProjectConfigurationCommand> commandList) {
    this.commandList = commandList;
  }

  /**
   * Gets the commands.
   *
   * @return the commands
   */
  public List<IProjectConfigurationCommand> getCommandList() {
    return this.commandList;
  }

  /**
   * Sets the URL.
   *
   * @param url the URL to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets the URL.
   *
   * @return the URL
   */
  public String getUrl() {
    return this.url;
  }
}

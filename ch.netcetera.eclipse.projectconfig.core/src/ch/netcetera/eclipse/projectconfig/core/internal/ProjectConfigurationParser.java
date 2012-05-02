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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ILog;

import ch.netcetera.eclipse.common.io.IOUtil;
import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.projectconfig.core.ProjectConfigurationScript;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.AddNatureProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.CommentProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.DownloadProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.IProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.MkdirProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.RemoveNatureProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.RmProjectConfigurationCommand;
import ch.netcetera.eclipse.projectconfig.core.configurationcommands.RmdirProjectConfigurationCommand;

/**
 * Parser for an Eclipse project configuration script (EPCS).
 */
final class ProjectConfigurationParser {

  /**
   * Private default constructor to avoid instantiation.
   */
  private ProjectConfigurationParser() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Reads the configuration script from the passed input stream and fills the commands and metadata
   * into the script object passed.
   *
   * @param script the script to add the commands
   * @param inputStream the ionput stream to read from
   * @param textAccessor the text accessor
   * @param pluginId the plugin id
   * @param log the log
   * @throws IOException on IO errors
   */
  static void parse(ProjectConfigurationScript script, InputStream inputStream,
      ITextAccessor textAccessor, String pluginId, ILog log) throws IOException {
    List<IProjectConfigurationCommand> commandList = new ArrayList<IProjectConfigurationCommand>();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    try {
      String line;
      while ((line = bufferedReader.readLine()) != null) { //NOPMD pellaton 2010-11-20 ok
        List<String> split = splitLine(line);
        if (!split.isEmpty()) {
          String command = split.get(0);
          if (command.equals(CommentProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new CommentProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else if (command.equals(DownloadProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new DownloadProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else if (command.equals(MkdirProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new MkdirProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else if (command.equals(RmdirProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new RmdirProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else if (command.equals(RmProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new RmProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else if (command.equals(AddNatureProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new AddNatureProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else if (command.equals(RemoveNatureProjectConfigurationCommand.COMMAND_NAME)) {
            commandList.add(new RemoveNatureProjectConfigurationCommand(split, textAccessor, pluginId, log));
          } else {
            // unknown command: ignore
          }
        }
      }
    } finally {
      IOUtil.closeSilently(bufferedReader);
    }
    script.setCommands(commandList);
  }

  /**
   * Splits the line into an array not containing any empty strings.
   *
   * @param line the line to split
   * @return the array
   */
  static List<String> splitLine(String line) {
    String[] split = line.trim().split(" ");
    List<String> list = new ArrayList<String>(split.length);
    for (int i = 0; i < split.length; i++) {
      String element = split[i].trim();
      if (element.length() > 0) {
        list.add(element);
      }
    }
    return list;
  }
}

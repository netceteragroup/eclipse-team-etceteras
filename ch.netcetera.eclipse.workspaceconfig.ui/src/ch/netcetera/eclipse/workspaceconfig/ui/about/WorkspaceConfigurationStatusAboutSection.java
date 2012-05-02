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
package ch.netcetera.eclipse.workspaceconfig.ui.about;

import java.io.PrintWriter;

import org.eclipse.ui.about.ISystemSummarySection;

import ch.netcetera.eclipse.workspaceconfig.ui.WorkspaceConfigurationUIPlugin;
import ch.netcetera.eclipse.workspaceconfig.ui.util.WorkspaceConfigurationStatusUtil;


/**
 * The about section to display the date of the workspace configuration import.
 */
public class WorkspaceConfigurationStatusAboutSection implements ISystemSummarySection {

  /** {@inheritDoc} */
  public void write(PrintWriter writer) {
    String status = "";
    if (WorkspaceConfigurationStatusUtil.isNewWorkspace()) {
      status = WorkspaceConfigurationUIPlugin.getDefault().getText("about.section.status.new.unconfigured");
    } else if (WorkspaceConfigurationStatusUtil.isUnconfiguredWorkspace()) {
      status = WorkspaceConfigurationUIPlugin.getDefault().getText("about.section.status.unconfigured.user");
    } else if (WorkspaceConfigurationStatusUtil.isConfiguredWorkspace()) {
      status = WorkspaceConfigurationUIPlugin.getDefault().getText("about.section.status.configured") + " "
          + WorkspaceConfigurationStatusUtil.getConfigDate();
    } else if (WorkspaceConfigurationStatusUtil.isErrorDuringConfiguration()) {
      status = WorkspaceConfigurationUIPlugin.getDefault().getText("about.section.status.error") + " "
          + WorkspaceConfigurationStatusUtil.getErrorDate();
    } else {
      status = WorkspaceConfigurationUIPlugin.getDefault().getText("about.section.status.unknown");
    }

    writer.write(WorkspaceConfigurationUIPlugin.getDefault().getText("about.section.status") + " " + status);
    writer.println();
  }
}
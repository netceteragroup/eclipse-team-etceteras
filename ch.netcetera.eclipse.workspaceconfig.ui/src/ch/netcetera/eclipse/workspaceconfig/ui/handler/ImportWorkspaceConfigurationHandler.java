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
package ch.netcetera.eclipse.workspaceconfig.ui.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.FrameworkUtil;

import ch.netcetera.eclipse.workspaceconfig.core.IPreferencesImportService;
import ch.netcetera.eclipse.workspaceconfig.ui.WorkspaceConfigurationUIPlugin;
import ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationUtil;

/**
 * Imports the remote workspace configuration.
 */
public class ImportWorkspaceConfigurationHandler extends AbstractHandler {

  /** The id of the command. */
  static final String COMMAND_ID = "ch.netcetera.eclipse.workspaceconfig.ui.applySettings";
  

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    this.applySettings();
    return null;
  }

  /**
   * Fetches and imports the workspace settings.
   */
  private void applySettings() {
    String bundleId = FrameworkUtil.getBundle(getClass()).getSymbolicName();

    IPreferencesImportService service = WorkspaceConfigurationUIPlugin.getDefault().getPreferencesImportService();
    IStatus status = Status.OK_STATUS;
  
    if (service != null) {
      List<String> urlList = ConfigurationUtil.getEpfUrls();
      List<String> replacementList = ConfigurationUtil.getEnvReplacements();
      for (String url : urlList) {
        status = service.importConfigFile(url, replacementList);
        if (!status.isOK()) {
          logStatus(status);
          status = Status.OK_STATUS;
        }
      }
    } else {
      logStatus(new Status(IStatus.ERROR, bundleId, "could not obtain service reference of IPreferencesImportService"));
    }
  }

  private void logStatus(IStatus status) {
    WorkspaceConfigurationUIPlugin.getDefault().getLog().log(status);
  }
}

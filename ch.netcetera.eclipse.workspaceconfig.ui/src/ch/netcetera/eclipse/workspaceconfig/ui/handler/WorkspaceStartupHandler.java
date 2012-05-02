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

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.framework.FrameworkUtil;

import ch.netcetera.eclipse.workspaceconfig.ui.PluginImages;
import ch.netcetera.eclipse.workspaceconfig.ui.WorkspaceConfigurationUIPlugin;
import ch.netcetera.eclipse.workspaceconfig.ui.preferences.ConfigurationUtil;
import ch.netcetera.eclipse.workspaceconfig.ui.preferences.WorkspaceConfigurationConstants;
import ch.netcetera.eclipse.workspaceconfig.ui.util.WorkspaceConfigurationStatusUtil;


/**
 * Class that is called by the environment on startup. It then checks whether a
 * new workspace is started and offers to import a remote configuration.
 */
public class WorkspaceStartupHandler implements IStartup {

  /** Dialog result to enable the configuration. */
  protected static final int IMPORT_CONFIG = 0;

  /** Dialog result to skip the configuration. */
  protected static final int DO_NOT_IMPORT_CONFIG = 1;

  /** {@inheritDoc} */
  @Override
  public void earlyStartup() {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final String bundleId = FrameworkUtil.getBundle(getClass()).getSymbolicName();
    boolean isNewWorkspace = WorkspaceConfigurationStatusUtil.isNewWorkspace();
    boolean checkEnabled = Platform.getPreferencesService().getBoolean(bundleId,
        WorkspaceConfigurationConstants.CONFIG_STARTUP_CHECK, true, null);
    boolean urlConfigured = ConfigurationUtil.isEpfUrlConfigured();

    if (checkEnabled && isNewWorkspace && urlConfigured) {

      workbench.getDisplay().asyncExec(new Runnable() {

        @Override
        public void run() {
          IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
          if (window != null) {
            String[] buttonLabels = new String[] {
                WorkspaceConfigurationUIPlugin.getDefault().getText("startup.handler.dialog.button.yes"),
                WorkspaceConfigurationUIPlugin.getDefault().getText("startup.handler.dialog.button.no"),
                WorkspaceConfigurationUIPlugin.getDefault().getText("startup.handler.dialog.button.ask.again")};
            String message = WorkspaceConfigurationUIPlugin.getDefault().getText("startup.handler.dialog.text");
            Image titleImage = WorkspaceConfigurationUIPlugin.getImageDescriptor(
                PluginImages.IMG_DIALOG_TITLE).createImage();
            String title = WorkspaceConfigurationUIPlugin.getDefault().getText("startup.handler.dialog.title");
            MessageDialog dialog = new MessageDialog(window.getShell(), title, titleImage, message,
                MessageDialog.QUESTION, buttonLabels, 0);
            int result = dialog.open();

            if (result == IMPORT_CONFIG) {
              applySettings();
              WorkspaceConfigurationStatusUtil.writeConfiguredFlag();
            } else if (result == DO_NOT_IMPORT_CONFIG) {
              WorkspaceConfigurationStatusUtil.writeNoConfigFlag();
            }
            // else: do nothing
          }
        }

        private void applySettings() {
          IHandlerService service = (IHandlerService) workbench.getService(IHandlerService.class);
          try {
            service.executeCommand(ImportWorkspaceConfigurationHandler.COMMAND_ID, null);
          } catch (CommandException e) {
            ILog log = WorkspaceConfigurationUIPlugin.getDefault().getLog();
            log.log(new Status(IStatus.ERROR, bundleId, "applying settings failed", e));
            displayErrorDialog();
          }
        }
      });
    }
  }
  
  /**
   * Displays an error dialog that the import failed.
   */
  private void displayErrorDialog() {
    WorkspaceConfigurationStatusUtil.writeErrorFlag();
    MessageBox errorDialog = new MessageBox(
        WorkspaceConfigurationUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
        SWT.ERROR);
    errorDialog.setText(WorkspaceConfigurationUIPlugin.getDefault().getText(
        "startup.handler.resultdialog.title.error"));
    errorDialog.setMessage(WorkspaceConfigurationUIPlugin.getDefault().getText(
        "startup.handler.resultdialog.text.error"));
    errorDialog.open();
  }
}
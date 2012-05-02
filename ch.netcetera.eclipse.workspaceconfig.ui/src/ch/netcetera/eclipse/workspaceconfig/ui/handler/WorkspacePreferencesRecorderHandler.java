/*
 * Copyright (c) 2010 Eclipse Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ch.netcetera.eclipse.workspaceconfig.ui.handler;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

import ch.netcetera.eclipse.common.text.ITextAccessor;
import ch.netcetera.eclipse.workspaceconfig.ui.WorkspaceConfigurationUIPlugin;
import ch.netcetera.eclipse.workspaceconfig.ui.dialog.WorkspacePrefercensRecorderResultDialog;

/**
 * Handler start and stop the preference recorder.
 * 
 * @author Michael Pellaton
 */
public class WorkspacePreferencesRecorderHandler extends AbstractHandler {

  private static final String ISO_8859_1 = "ISO-8859-1";
  private static final String EQUAL = "=";
  private static final String EOL = "\n";
  private static final String HASH = "#";
  private static final String AT = "@";
  
  private final ITextAccessor textAccessor = WorkspaceConfigurationUIPlugin.getDefault();
  private final ILog log = WorkspaceConfigurationUIPlugin.getDefault().getLog();
  private final String bundleId = WorkspaceConfigurationUIPlugin.getDefault().getBundle().getSymbolicName();
  
  private boolean isRecording = false;
  private Map<String, String> beforeMap = new HashMap<String, String>();
  

  /** {@inheritDoc} */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      Command command = event.getCommand();
      if (isRecording) {
        this.endRecording();
        command.define(this.textAccessor.getText("recorder.handler.start"),
            this.textAccessor.getText("recorder.handler.start.description"), command.getCategory());
      } else {
        this.startRecording();
        command.define(this.textAccessor.getText("recorder.handler.stop"),
            this.textAccessor.getText("recorder.handler.stop.description"), command.getCategory());
      }
    } catch (NotDefinedException e) {
      logError(e);
    }
    return null;
  }


  private void startRecording() {
    this.isRecording = true;
    this.beforeMap.clear();
    this.beforeMap = getPreferencesMap();
  }

  private void endRecording() {
    this.isRecording = false;
    Map<String, String> afterMap = getPreferencesMap();
    StringBuilder preferencesDelta = new StringBuilder();
    
    // detect removed preferences
    preferencesDelta.append(this.textAccessor.getText("recorder.result.removed")).append(EOL);
    Set<String> removedKeySet = new HashSet<String>(this.beforeMap.keySet());
    removedKeySet.removeAll(afterMap.keySet());
    for (String string : removedKeySet) {
      preferencesDelta.append(string).append(EQUAL).append(beforeMap.get(string)).append(EOL);
    }
    
    // detect added preferences
    preferencesDelta.append(EOL).append(this.textAccessor.getText("recorder.result.added")).append(EOL);
    Set<String> addedKeySet = new HashSet<String>(afterMap.keySet());
    addedKeySet.removeAll(this.beforeMap.keySet());
    for (String string : addedKeySet) {
      preferencesDelta.append(string).append(EQUAL).append(afterMap.get(string)).append(EOL);
    }
    
    // detect changed preferences
    preferencesDelta.append(EOL).append(this.textAccessor.getText("recorder.result.changed")).append(EOL);
    Set<String> possiblyChangedKeySet = new HashSet<String>(afterMap.keySet());
    possiblyChangedKeySet.retainAll(this.beforeMap.keySet());
    for (String string : possiblyChangedKeySet) {
      String newValue = afterMap.get(string);
      if (!this.beforeMap.get(string).equals(newValue)) {
        preferencesDelta.append(string).append(EQUAL).append(newValue).append(EOL);
      }
    }
    
    new WorkspacePrefercensRecorderResultDialog(null, this.textAccessor, preferencesDelta.toString()).open();
  }

  
  private Map<String, String> getPreferencesMap() {
    String[] split = getPreferencesArray();
    Map<String, String> prefMap = new HashMap<String, String>(split.length);
    for (String string : split) {
      if (!string.startsWith(AT) && !string.startsWith(HASH)) {
        String[] keyvalue = string.split(EQUAL, 2);
        prefMap.put(keyvalue[0], keyvalue[1]);
      }
    }
    return prefMap;
  }
  
  
  private String[] getPreferencesArray() {
    IPreferencesService service = Platform.getPreferencesService();
    IEclipsePreferences node = (IEclipsePreferences) service.getRootNode().node(InstanceScope.SCOPE);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      service.exportPreferences(node, outputStream , null);
      return new String(outputStream.toByteArray(), ISO_8859_1).split(EOL);
    } catch (CoreException e) {
      logError(e);
    } catch (UnsupportedEncodingException e) {
      logError(e);
    }
    return new String[0];
  }
  
  private void logError(Throwable e) {
    this.log.log(new Status(IStatus.ERROR, this.bundleId, this.textAccessor.getText("recorder.error.message"), e));
  }
}

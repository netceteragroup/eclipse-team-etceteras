/*
 * Copyright (c) 2009 the Eclipsed Team Etceteras Project and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Eclipse Team Etceteras - http://ete.kenai.com
 */
package ch.netcetera.eclipse.common.preference;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ch.netcetera.eclipse.common.security.SecurePreferencesUtil;

/**
 * Field editor that stores it's value in the cryptographically secured Equinox
 * secure preferences. Besides that, the text field displays the character '*'
 * instead of the Instead of the character actually typed.
 */
public class SecurePreferenceStringFieldEditor extends StringFieldEditor {

  private static final String DEFAULT_VALUE = "";

  private final Text textControl;
  private final String nodePathName;

  /**
   * Constructor.
   *
   * @param name the name of the field editor
   * @param labelText the label text
   * @param parent the parent control
   * @param echoCharacter the echo character to be used (password fields) or <code>'\0'</code> for none
   * @param nodePathName the secure preferences node path name
   */
  public SecurePreferenceStringFieldEditor(String name, String labelText, Composite parent, char echoCharacter,
      String nodePathName) {
    this.nodePathName = nodePathName;
    init(name, labelText);
    createControl(parent);
    this.textControl = getTextControl();
    this.textControl.setEchoChar(echoCharacter);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doLoad() {
    if (this.textControl != null && isEnabled()) {
      String value = "";
      try {
        value = SecurePreferencesUtil.get(this.nodePathName, getPreferenceName());
      } catch (StorageException e) {
        // no useful action to take
      }
      this.textControl.setText(value);
      this.oldValue = value;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doLoadDefault() {
    if (this.textControl != null) {
      this.textControl.setText(DEFAULT_VALUE);
    }
    valueChanged();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doStore() {
    if (isEnabled()) {
      try {
        SecurePreferencesUtil.store(this.nodePathName, getPreferenceName(), this.textControl.getText().trim());
      } catch (StorageException e) {
        // no useful action to take
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEnabled(boolean enabled, Composite parent) {
    super.setEnabled(enabled, parent);

    // clear the contents and the value stored in the secure preferences when disabled
    if (!enabled) {
      this.setStringValue(DEFAULT_VALUE);
      SecurePreferencesUtil.removeSilently(this.nodePathName, getPreferenceName());
    }
  }

  private boolean isEnabled() {
    return getTextControl().isEnabled();
  }
}

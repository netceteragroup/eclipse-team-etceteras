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
package ch.netcetera.eclipse.common.fieldeditor;

import java.util.Arrays;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import ch.netcetera.eclipse.common.text.ITextAccessor;

/**
 * A string list field editor that allows to edit the items of the list.
 */
public abstract class AbstractEditableStringListFieldEditor extends FieldEditor {

  private List list;
  private SelectionAdapter listSelectionListener;

  private Composite buttonPanel;
  private Button addButton;
  private SelectionListener addButtonSelectionListener;
  private Button removeButton;
  private SelectionListener removeButtonSelectionListener;
  private Button editButton;
  private SelectionListener editButtonSelectionListener;
  private Button upButton;
  private SelectionListener upButtonSelectionListener;
  private Button downButton;
  private SelectionListener downButtonSelectionListener;

  private final ITextAccessor textAccessor;
  private final boolean isReorderable;

  /**
   * Constructor.
   *
   * @param name the name of the preference this field editor edits
   * @param labelText the label text
   * @param parent the parent {@link Composite}
   * @param textAccessor the text accessor
   */
  public AbstractEditableStringListFieldEditor(String name, String labelText, Composite parent,
      ITextAccessor textAccessor) {
    this(name, labelText, parent, textAccessor, false);
  }
  
  /**
   * Constructor.
   *
   * @param name the name of the preference this field editor edits
   * @param labelText the label text
   * @param parent the parent {@link Composite}
   * @param textAccessor the text accessor
   * @param isReorderable {@code true} if a reorderable list shall be created
   */
  public AbstractEditableStringListFieldEditor(String name, String labelText, Composite parent,
      ITextAccessor textAccessor, boolean isReorderable) {
    this.textAccessor = textAccessor;
    this.isReorderable = isReorderable;
    init(name, labelText);
    createSelectionListeners(); // NOPMD by michael on 11/20/10 2:21 PM
    createControl(parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doFillIntoGrid(Composite parent, int numColumns) {
    Control control = getLabelControl(parent);
    GridData gridData = new GridData();
    gridData.horizontalSpan = numColumns;
    control.setLayoutData(gridData);

    this.list = createListControl(parent);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalSpan = numColumns - 1;
    gridData.grabExcessHorizontalSpace = true;
    this.list.setLayoutData(gridData);

    this.buttonPanel = createButtonBoxControl(parent);
    gridData = new GridData();
    gridData.verticalAlignment = GridData.BEGINNING;
    this.buttonPanel.setLayoutData(gridData);
  }

  /**
   * Creates the list control.
   *
   * @param parent the parent control
   * @return the list control
   */
  private List createListControl(Composite parent) {
    if (this.list == null) {
      this.list = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
      this.list.setFont(parent.getFont());
      this.list.addSelectionListener(this.listSelectionListener);
      this.list.addDisposeListener(new DisposeListener() {

        /** {@inheritDoc} */
        @Override
        public void widgetDisposed(DisposeEvent event) {
          AbstractEditableStringListFieldEditor.this.list = null;
        }
      });
    } else {
      checkParent(this.list, parent);
    }
    return this.list;
  }

  /**
   * Creates this field editor's button {@link Composite}.
   *
   * @param parent the parent control
   * @return the button {@link Composite}
   */
  public Composite createButtonBoxControl(Composite parent) {
    if (this.buttonPanel == null) {
      this.buttonPanel = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      this.buttonPanel.setLayout(layout);

      this.addButton = createPushButton(this.buttonPanel, "preference.page.button.add",
          this.addButtonSelectionListener);
      this.editButton = createPushButton(this.buttonPanel, "preference.page.button.edit",
          this.editButtonSelectionListener);
      this.removeButton = createPushButton(this.buttonPanel, "preference.page.button.remove",
          this.removeButtonSelectionListener);

      if (this.isReorderable) {
        this.upButton = createPushButton(this.buttonPanel, "preference.page.button.up",
            this.upButtonSelectionListener);
        this.downButton = createPushButton(this.buttonPanel, "preference.page.button.down",
            this.downButtonSelectionListener);
      }
      
      this.buttonPanel.addDisposeListener(new DisposeListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetDisposed(DisposeEvent event) {
          AbstractEditableStringListFieldEditor.this.addButton = null;
          AbstractEditableStringListFieldEditor.this.removeButton = null;
          AbstractEditableStringListFieldEditor.this.editButton = null;
          AbstractEditableStringListFieldEditor.this.upButton = null;
          AbstractEditableStringListFieldEditor.this.downButton = null;
          AbstractEditableStringListFieldEditor.this.buttonPanel = null;
        }
      });

    } else {
      checkParent(this.buttonPanel, parent);
    }

    selectionChanged();
    return this.buttonPanel;
  }

  /**
   * Gets the add button's shell.
   *
   * @return the shell
   */
  protected Shell getAddButtonShell() {
    return this.addButton == null ? null : this.addButton.getShell();
  }

  /**
   * Gets the edit button's shell.
   *
   * @return the shell
   */
  protected Shell getEditButtonShell() {
    return this.editButton == null ? null : this.editButton.getShell();
  }

  /**
   * Creates the button selection listener.
   */
  private void createSelectionListeners() {
    this.addButtonSelectionListener = new SelectionAdapter() {
      
      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPresentsDefaultValue(false);
        String newItem = getNewListItem();

        if (newItem != null) {
          int index = AbstractEditableStringListFieldEditor.this.list.getSelectionIndex();
          if (index >= 0) {
            AbstractEditableStringListFieldEditor.this.list.add(newItem, index + 1);
          } else {
            AbstractEditableStringListFieldEditor.this.list.add(newItem);
          }
          selectionChanged();
        }
      }
    };

    this.removeButtonSelectionListener = new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPresentsDefaultValue(false);
        int index = AbstractEditableStringListFieldEditor.this.list.getSelectionIndex();
        if (index >= 0) {
          AbstractEditableStringListFieldEditor.this.list.remove(index);
          selectionChanged();
        }
      }
    };

    this.editButtonSelectionListener = new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPresentsDefaultValue(false);

        int index = AbstractEditableStringListFieldEditor.this.list.getSelectionIndex();
        if (index >= 0) {
          String item = editListItem(AbstractEditableStringListFieldEditor.this.list.getItem(index));

          // remove the item from the list if the edited item is null or empty
          if (item != null && item.length() > 0) {
            AbstractEditableStringListFieldEditor.this.list.setItem(index, item);
          } else {
            AbstractEditableStringListFieldEditor.this.list.remove(index);
          }
          selectionChanged();
        }
      }
    };

    this.upButtonSelectionListener = new SelectionAdapter() {
      
      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPresentsDefaultValue(false);
        
        List itemList = AbstractEditableStringListFieldEditor.this.list;
        int index = itemList.getSelectionIndex();
        if (index >= 1) {
          String item = itemList.getItem(index);
          itemList.remove(index);
          itemList.add(item, index - 1);
          itemList.setSelection(index - 1);
          selectionChanged();
        }
      }
    };
    
    this.downButtonSelectionListener = new SelectionAdapter() {
      
      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPresentsDefaultValue(false);
        
        List itemList = AbstractEditableStringListFieldEditor.this.list;
        int index = itemList.getSelectionIndex();
        if (index >= 0 && index < (itemList.getItemCount() - 1)) {
          String item = itemList.getItem(index);
          itemList.remove(index);
          itemList.add(item, index + 1);
          itemList.setSelection(index + 1);
          selectionChanged();
        }
      }
    };

    this.listSelectionListener = new SelectionAdapter() {

      /** {@inheritDoc} */
      @Override
      public void widgetSelected(SelectionEvent e) {
       selectionChanged();
      }
    };
  }

  /**
   * Helper method to create a push button.
   *
   * @param parent the parent control
   * @param key the key used to look-up the button text
   * @param selectionListener the selection listener to attach to the new button
   * @return the new button
   */
  private Button createPushButton(Composite parent, String key, SelectionListener selectionListener) {
    Button button = new Button(parent, SWT.PUSH);
    button.setText(this.textAccessor.getText(key));
    button.setFont(parent.getFont());
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
    data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
    button.setLayoutData(data);
    button.addSelectionListener(selectionListener);
    return button;
  }

  /**
   * Called when the selection changed and the UI might need to adapt to the new
   * selection.
   */
  protected void selectionChanged() {
    int index = this.list.getSelectionIndex();
    this.removeButton.setEnabled(index >= 0);
    this.editButton.setEnabled(index >= 0);
    if (this.isReorderable) {
      this.upButton.setEnabled(index >= 1);
      this.downButton.setEnabled(index >= 0 && index < (this.list.getItemCount() - 1));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEnabled(boolean enabled, Composite parent) {
    super.setEnabled(enabled, parent);
    this.list.setEnabled(enabled);
    this.addButton.setEnabled(enabled);
    this.removeButton.setEnabled(enabled);
    this.editButton.setEnabled(enabled);
    if (this.isReorderable) {
      this.upButton.setEnabled(enabled);
      this.downButton.setEnabled(enabled);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    if (this.list != null) {
      this.list.setFocus();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumberOfControls() {
    return 2;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void adjustForNumColumns(int numColumns) {
    Control control = getLabelControl();
    ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
    ((GridData) this.list.getLayoutData()).horizontalSpan = numColumns - 1;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  protected void doLoad() {
    if (this.list != null) {
      String preferenceString = getPreferenceStore().getString(getPreferenceName());
      java.util.List<String> valueList = parsePreferenceString(preferenceString);
      for (String item : valueList) {
        this.list.add(item);
      }
    }
  }
  
  /**
   * Parses a preference string into the items.
   * 
   * @param preferenceString the preference string to parse
   * @return the items
   */
  protected abstract java.util.List<String> parsePreferenceString(String preferenceString);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doLoadDefault() {
    if (this.list != null) {
      this.list.removeAll();
      String preferenceString = getPreferenceStore().getDefaultString(getPreferenceName());
      java.util.List<String> valueList = parsePreferenceString(preferenceString);
      for (String item : valueList) {
        this.list.add(item);
      }
    }
  }
  
  /** {@inheritDoc} */
  @Override
  protected void doStore() {
    String s = marshallPreferenceString(this.list.getItems());
    if (s != null) {
      getPreferenceStore().setValue(getPreferenceName(), s);
    }
  }
  
  /**
   * Marshalls the items into a preference string.
   * 
   * @param items the items to marshall
   * @return the marshalled string
   */
  protected abstract String marshallPreferenceString(String[] items);
  
  /**
   * Gets the text accessor.
   *
   * @return the text accessor
   */
  public ITextAccessor getTextAccessor() {
    return this.textAccessor;
  }

  /**
   * Gets the list items.
   *
   * @return the list items
   */
  public java.util.List<String> getListItems() {
    return Arrays.asList(this.list.getItems());
  }
  /**
   * Gets a new item for the list.
   *
   * @return the new list item
   */
  public abstract String getNewListItem();

  /**
   * Edit a list item.
   *
   * @param item the list item to edit
   * @return the new version of the list item
   */
  public abstract String editListItem(String item);
}

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
package ch.netcetera.eclipse.workspaceconfig.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.FrameworkUtil;

/**
 * The {@link ImageRegistry} of the plug-in.
 */
public final class PluginImages extends ImageRegistry {

  /** Launch dialog title image. */
  public static final String IMG_DIALOG_TITLE = "rsrc/dialog.gif";
  /** Import wizard banner. */
  public static final String IMG_IMPORT_WIZBAN = "rsrc/import_wizban.png";

  // list of all plug-in images
  private static final String[] IMAGE_IDS = {
    IMG_DIALOG_TITLE,
    IMG_IMPORT_WIZBAN
  };

  /**
   * Private default constructor to prohibit instantiation.
   */
  private PluginImages() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Initialize the image register.
   *
   * @param registry the {@link ImageRegistry} to initialize
   */
  public static void initializeImageRegistry(ImageRegistry registry) {
    String imagePath = null;
    ImageDescriptor descriptor = null;

    //  load images to image register
    for (int i = 0; i < IMAGE_IDS.length; i++) {
      imagePath = IMAGE_IDS[i];
      descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
          FrameworkUtil.getBundle(PluginImages.class).getSymbolicName(), imagePath);
      registry.put(IMAGE_IDS[i], descriptor);
    }
  }
}
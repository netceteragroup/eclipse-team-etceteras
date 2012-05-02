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

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

/**
 * A matcher for the plugin id of an {@link AbstractProjectConfigurationCommand}
 * .
 */
public final class PluginIdMatcher extends TypeSafeMatcher<IProjectConfigurationCommand> {

  private final Matcher<? super String> instance;

  /**
   * Constructor.
   * 
   * @param instance the matcher for the plugin id
   */
  private PluginIdMatcher(Matcher<? super String> pluginIdMatcher) {
    this.instance = pluginIdMatcher;
  }

  /**
   * Factory method that returns a new {@link TypeSafeMatcher} of
   * {@link AbstractProjectConfigurationCommand} for the given plugin id .
   * 
   * @param pluginId the plugin id
   * @return a new matcher.
   */
  @Factory
  public static TypeSafeMatcher<IProjectConfigurationCommand> hasPluginId(String pluginId) {
    return new PluginIdMatcher(equalTo(pluginId));
  }

  /**
   * Factory method that returns a new {@link TypeSafeMatcher} of
   * {@link AbstractProjectConfigurationCommand} for the given plugin id .
   * 
   * @param pluginIdMatcher the pluginIdMatcher instance
   * @return a new matcher
   */
  @Factory
  public static TypeSafeMatcher<IProjectConfigurationCommand> hasPluginId(Matcher<? super String> pluginIdMatcher) {
    return new PluginIdMatcher(pluginIdMatcher);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matchesSafely(IProjectConfigurationCommand item) {
    if (!(item instanceof AbstractProjectConfigurationCommand)) {
      return false;
    }
    AbstractProjectConfigurationCommand command = (AbstractProjectConfigurationCommand) item;
    return this.instance.matches(command.getPluginId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void describeTo(Description description) {
    description.appendText("has plugin id ");
    this.instance.describeTo(description);
  }

}

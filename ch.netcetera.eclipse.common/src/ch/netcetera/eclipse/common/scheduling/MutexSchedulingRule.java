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
package ch.netcetera.eclipse.common.scheduling;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Simple mutex scheduling rule that makes sure no two jobs that have it
 * assigned are executed at the same time.
 */
public class MutexSchedulingRule implements ISchedulingRule {

  /** The one and only rule to use. */
  public static final MutexSchedulingRule RULE = new MutexSchedulingRule();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean contains(ISchedulingRule rule) {
    return this == rule;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isConflicting(ISchedulingRule rule) {
    return this == rule;
  }
}

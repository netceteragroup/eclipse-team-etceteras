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
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link MutexSchedulingRule}.
 */
public class MutexSchedulingRuleTest {

  private final MutexSchedulingRule schedulingRule = MutexSchedulingRule.RULE;

  /**
   * Tests {@link MutexSchedulingRule#contains(ISchedulingRule)}.
   */
  @Test
  public void contains() {
    assertTrue(this.schedulingRule.contains(this.schedulingRule));
    assertFalse(this.schedulingRule.contains(null));
    assertFalse(this.schedulingRule.contains(new ISchedulingRule() {
      @Override
      public boolean isConflicting(ISchedulingRule rule) {
        return false;
      }

      @Override
      public boolean contains(ISchedulingRule rule) {
        return false;
      }
    }));
  }

  /**
   * Tests {@link MutexSchedulingRule#isConflicting(ISchedulingRule)}.
   */
  @Test
  public void isConflicting() {
    assertTrue(this.schedulingRule.isConflicting(this.schedulingRule));
    assertFalse(this.schedulingRule.isConflicting(null));
    assertFalse(this.schedulingRule.isConflicting(new ISchedulingRule() {
      @Override
      public boolean isConflicting(ISchedulingRule rule) {
        return false;
      }

      @Override
      public boolean contains(ISchedulingRule rule) {
        return false;
      }
    }));
  }
}

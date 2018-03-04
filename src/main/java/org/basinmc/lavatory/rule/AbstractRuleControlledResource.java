/*
 * Copyright 2018 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.basinmc.lavatory.rule;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.basinmc.lavatory.ResolverContext;
import org.basinmc.lavatory.rule.Rule.Action;

/**
 * Provides an abstract base to resources which are included based on whether their rules (if any)
 * match.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractRuleControlledResource implements RuleControlledResource {

  private final Set<Rule> rules;

  public AbstractRuleControlledResource(@Nullable Set<Rule> rules) {
    this.rules = rules == null ? Collections.emptySet() : new HashSet<>(rules);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Set<Rule> getRules() {
    return Collections.unmodifiableSet(this.rules);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean evaluate(@NonNull ResolverContext ctx) {
    return this.rules.stream().allMatch((r) -> r.evaluate(ctx) == Action.ALLOW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractRuleControlledResource)) {
      return false;
    }
    AbstractRuleControlledResource that = (AbstractRuleControlledResource) o;
    return Objects.equals(this.rules, that.rules);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.rules);
  }
}

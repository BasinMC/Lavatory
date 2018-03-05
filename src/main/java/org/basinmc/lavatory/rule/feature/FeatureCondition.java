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
package org.basinmc.lavatory.rule.feature;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.basinmc.lavatory.ResolverContext;
import org.basinmc.lavatory.rule.RuleCondition;

/**
 * Represents a condition which evaluates whether or not a particular feature is currently enabled
 * within the context.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class FeatureCondition implements RuleCondition {

  private final Map<Feature, Boolean> featureMap;

  @JsonCreator
  public FeatureCondition(@NonNull Map<String, Boolean> values) {
    this.featureMap = values.entrySet().stream()
        .collect(Collectors.toMap(
            (e) -> Feature.byKey(e.getKey())
                .orElseThrow(
                    () -> new IllegalArgumentException("Unknown feature \"" + e.getKey() + "\"")),
            Entry::getValue
        ));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean evaluate(@NonNull ResolverContext ctx) {
    return this.featureMap.entrySet().stream()
        .allMatch((e) -> ctx.isFeatureEnabled(e.getKey()) == e.getValue());
  }

  @NonNull
  public Map<Feature, Boolean> getFeatureMap() {
    return Collections.unmodifiableMap(this.featureMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    FeatureCondition that = (FeatureCondition) o;
    return Objects.equals(this.featureMap, that.featureMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.featureMap);
  }
}

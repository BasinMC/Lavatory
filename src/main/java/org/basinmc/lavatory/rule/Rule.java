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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.basinmc.lavatory.ResolverContext;
import org.basinmc.lavatory.rule.Rule.Deserializer;
import org.basinmc.lavatory.rule.feature.FeatureCondition;
import org.basinmc.lavatory.rule.system.OperatingSystemCondition;

/**
 * Represents a rule which evaluates whether or not to include a certain value within the final
 * result based on a set of special conditions.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@JsonDeserialize(using = Deserializer.class)
public class Rule {

  private final Action action;
  private final Set<RuleCondition> conditions;

  public Rule(@NonNull Action action, @NonNull Set<RuleCondition> conditions) {
    this.action = action;
    this.conditions = new HashSet<>(conditions);
  }

  /**
   * Evaluates the result of this rule and thus whether or not the respective value it is attached
   * to is to be included.
   *
   * @param ctx a context.
   * @return {@link Action#ALLOW} when inclusion is expected, {@link Action#DENY} otherwise.
   */
  public Action evaluate(@NonNull ResolverContext ctx) {
    if (this.conditions.stream().allMatch((c) -> c.evaluate(ctx))) {
      return this.action;
    }

    return Action.negate(this.action);
  }

  @NonNull
  public Action getAction() {
    return this.action;
  }

  @NonNull
  public Set<RuleCondition> getConditions() {
    return Collections.unmodifiableSet(this.conditions);
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
    Rule rule = (Rule) o;
    return this.action == rule.action &&
        Objects.equals(this.conditions, rule.conditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.action, this.conditions);
  }

  /**
   * Provides a list of valid rule return values.
   */
  public enum Action {
    /**
     * <p><strong>Permit inclusion</strong></p>
     *
     * <p>Indicates that the value to which this rule is attached may be included in the final
     * result.</p>
     */
    ALLOW,

    /**
     * <p><strong>Deny inclusion</strong></p>
     *
     * <p>Indicates that the value to which this rule is attached may <strong>NOT</strong> be
     * included in the final result.</p>
     */
    DENY;

    /**
     * Retrieves the negated value of the supplied action.
     *
     * @param action an action.
     * @return a negated action.
     */
    @NonNull
    public static Action negate(@NonNull Action action) {
      if (action == ALLOW) {
        return DENY;
      }

      return ALLOW;
    }
  }

  /**
   * Provides a custom de-serialization implementation for rules which permits the dynamic
   * application of various types of conditions within the object body.
   */
  public static class Deserializer extends JsonDeserializer<Rule> {

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Rule deserialize(@NonNull JsonParser p, @NonNull DeserializationContext ctxt)
        throws IOException {
      ObjectMapper mapper = (ObjectMapper) p.getCodec();
      ObjectNode node = p.readValueAsTree();

      Action action = null;
      if (!node.has("action")) {
        ctxt.reportInputMismatch(Rule.class, "Illegal rule definition: Missing action");
      }
      try {
        action = Action.valueOf(node.get("action").asText().toUpperCase());
      } catch (IllegalArgumentException ex) {
        ctxt.reportInputMismatch(Action.class, "Illegal action definition");
      }

      Set<RuleCondition> conditions = new HashSet<>();
      if (node.has("features")) {
        conditions.add(mapper.treeToValue(node.get("features"), FeatureCondition.class));
      }
      if (node.has("os")) {
        conditions.add(mapper.treeToValue(node.get("os"), OperatingSystemCondition.class));
      }

      return new Rule(action, conditions);
    }
  }
}

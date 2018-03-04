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
package org.basinmc.lavatory.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.basinmc.lavatory.rule.AbstractRuleControlledResource;
import org.basinmc.lavatory.rule.Rule;

/**
 * Represents a command line argument which may be passed to the game based on a special set of
 * conditions.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProgramArgument extends AbstractRuleControlledResource {

  private final List<String> value;

  @JsonCreator
  private ProgramArgument(
      @NonNull @JsonProperty(value = "value", required = true) List<String> value,
      @NonNull @JsonProperty(value = "rules", required = true) Set<Rule> rules) {
    super(rules);
    this.value = value;
  }

  @JsonCreator
  public ProgramArgument(@NonNull String value) {
    this(Collections.singletonList(value), Collections.emptySet());
  }

  /**
   * <p>Retrieves a list of arguments which are included within this argument object.</p>
   *
   * <p>As this object acts a logical grouping of command line arguments which may be enabled
   * conditionally based on a set of rules, it may contain more than one element in some cases (for
   * instance when specifying a launch resolution).</p>
   *
   * @return a list of argument strings.
   */
  @NonNull
  public List<String> getValue() {
    return Collections.unmodifiableList(this.value);
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
    if (!super.equals(o)) {
      return false;
    }
    ProgramArgument that = (ProgramArgument) o;
    return Objects.equals(this.value, that.value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.value);
  }
}

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
package org.basinmc.lavatory.rule.system;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.basinmc.lavatory.ResolverContext;
import org.basinmc.lavatory.rule.RuleCondition;

/**
 * Provides a condition which evaluates whether an operating system and its version match a
 * predefined value.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class OperatingSystemCondition implements RuleCondition {

  private final OperatingSystem system;
  private final Pattern version;
  // TODO: Arch is probably a valid property as well?

  @JsonCreator
  public OperatingSystemCondition(
      @Nullable @JsonProperty("name") String system,
      @Nullable @JsonProperty("version") Pattern version) {
    this.system = system == null ? null : OperatingSystem.byKey(system)
        .orElseThrow(
            () -> new IllegalArgumentException("Illegal operating system: \"" + system + "\""));
    this.version = version;

    if (system == null && version == null) {
      throw new IllegalArgumentException(
          "Illegal operating system condition: Must specify either system or version");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean evaluate(@NonNull ResolverContext ctx) {
    if (this.system != null && this.system != ctx.getOperatingSystem()) {
      return false;
    }

    if (this.version != null && !this.version.matcher(ctx.getOperatingSystemVersion()).matches()) {
      return false;
    }

    return true;
  }

  @NonNull
  public Optional<OperatingSystem> getSystem() {
    return Optional.ofNullable(this.system);
  }

  @NonNull
  public Optional<Pattern> getVersion() {
    return Optional.ofNullable(this.version);
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
    OperatingSystemCondition that = (OperatingSystemCondition) o;
    return this.system == that.system &&
        Objects.equals(this.version, that.version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.system, this.version);
  }
}

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
package org.basinmc.lavatory;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.basinmc.lavatory.rule.feature.Feature;
import org.basinmc.lavatory.rule.system.OperatingSystem;

/**
 * Provides contextual information to rules in order to en- or disable certain values or otherwise
 * customize values.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface ResolverContext {

  /**
   * Creates a new empty resolver context factory.
   *
   * @return a factory.
   */
  @NonNull
  static Builder builder() {
    return new Builder();
  }

  /**
   * Retrieves the operating system for which the launcher values are to be generated.
   *
   * @return an operating system.
   */
  @NonNull
  OperatingSystem getOperatingSystem();

  /**
   * Retrieves the operating system version number for which the launcher values are to be
   * generated.
   *
   * @return a version number.
   */
  @NonNull
  String getOperatingSystemVersion();

  /**
   * Evaluates whether the specified feature is currently enabled within the context.
   *
   * @param feature a feature.
   * @return true if enabled, false otherwise.
   */
  boolean isFeatureEnabled(@NonNull Feature feature);

  /**
   * <p>Provides a builder for arbitrary resolver context instances.</p>
   *
   * <p>By default this builder will assume the current system's values for every property (e.g. it
   * will choose the current operating system, version, etc).</p>
   */
  final class Builder {

    private OperatingSystem operatingSystem = OperatingSystem.current();
    private String operatingSystemVersion = System.getProperty("os.version", "0.0");
    private final Set<Feature> features = EnumSet.noneOf(Feature.class);

    private Builder() {
    }

    /**
     * Constructs a new resolver context using the configuration within this builder.
     *
     * @return a context.
     */
    @NonNull
    public ResolverContext build() {
      return new ResolverContextImpl(
          this.operatingSystem,
          this.operatingSystemVersion,
          this.features
      );
    }

    /**
     * Enables the specified feature(s).
     *
     * @param features an array of features.
     * @return a reference to this builder.
     */
    @NonNull
    public Builder enableFeature(@NonNull Feature... features) {
      this.features.addAll(Arrays.asList(features));
      return this;
    }

    /**
     * Disables the specified feature(s).
     *
     * @param features an array of features.
     * @return a reference to this builder.
     */
    @NonNull
    public Builder disableFeature(@NonNull Feature... features) {
      this.features.removeAll(Arrays.asList(features));
      return this;
    }

    /**
     * Selects the detected operating system.
     *
     * @return a reference to this builder.
     */
    @NonNull
    public Builder withDetectedOperatingSystem() {
      return this.withOperatingSystem(OperatingSystem.current());
    }

    /**
     * Selects the detected operating system version.
     *
     * @return a reference to this builder.
     */
    @NonNull
    public Builder withDetectedOperatingSystemVersion() {
      return this.withOperatingSystemVersion(System.getProperty("os.version", "0.0"));
    }

    /**
     * Selects the specified operating system.
     *
     * @param operatingSystem an operating system.
     * @return a reference to this builder.
     */
    @NonNull
    public Builder withOperatingSystem(@NonNull OperatingSystem operatingSystem) {
      this.operatingSystem = operatingSystem;
      return this;
    }

    /**
     * Selects the specified operating system version.
     *
     * @param version an operating system version.
     * @return a reference to this builder.
     */
    @NonNull
    public Builder withOperatingSystemVersion(@NonNull String version) {
      this.operatingSystemVersion = version;
      return this;
    }
  }
}

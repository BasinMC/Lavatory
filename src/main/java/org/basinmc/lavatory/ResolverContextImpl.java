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
import java.util.EnumSet;
import java.util.Set;
import org.basinmc.lavatory.rule.feature.Feature;
import org.basinmc.lavatory.rule.system.OperatingSystem;

/**
 * Provides a configurable implementation of the resolver context specification.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class ResolverContextImpl implements ResolverContext {

  private final OperatingSystem operatingSystem;
  private final String operatingSystemVersion;
  private final Set<Feature> features;

  ResolverContextImpl(
      @NonNull OperatingSystem operatingSystem,
      @NonNull String operatingSystemVersion,
      @NonNull Set<Feature> features) {
    this.operatingSystem = operatingSystem;
    this.operatingSystemVersion = operatingSystemVersion;
    this.features = EnumSet.copyOf(features);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public OperatingSystem getOperatingSystem() {
    return this.operatingSystem;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getOperatingSystemVersion() {
    return this.operatingSystemVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFeatureEnabled(@NonNull Feature feature) {
    return this.features.contains(feature);
  }
}

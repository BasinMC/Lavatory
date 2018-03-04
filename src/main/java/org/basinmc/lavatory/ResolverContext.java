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
}

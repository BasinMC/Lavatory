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

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Provides a list of features which enable special elements within the game.</p>
 *
 * <p>These values effectively provide an implementation agnostic layer between the game and
 * launcher implementations which permits the conditional passing of arbitrary arguments or
 * retrieval of libraries.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public enum Feature {
  CUSTOM_RESOLUTION("has_custom_resolution"),
  DEMO_USER("is_demo_user");

  private static final Map<String, Feature> keyMap = new HashMap<>();
  private final String key;

  static {
    for (Feature feature : values()) {
      keyMap.put(feature.key, feature);
    }
  }

  Feature(@NonNull String key) {
    this.key = key;
  }

  @NonNull
  public static Optional<Feature> byKey(@NonNull String key) {
    return Optional.ofNullable(keyMap.get(key));
  }

  @NonNull
  public String getKey() {
    return this.key;
  }
}

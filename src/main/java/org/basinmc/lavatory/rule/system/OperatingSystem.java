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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides a list of operating systems which are officially supported and recognized (as well as a
 * fallback value which is used when the operating system is unknown).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public enum OperatingSystem {
  UNKNOWN,
  LINUX("linux"),
  MAC_OS("osx"),
  WINDOWS("windows");

  private static final Map<String, OperatingSystem> keyMap = new HashMap<>();
  private final String key;

  static {
    for (OperatingSystem os : values()) {
      if (os.key == null) {
        continue;
      }

      keyMap.put(os.key, os);
    }
  }

  OperatingSystem() {
    this(null);
  }

  OperatingSystem(@Nullable String key) {
    this.key = key;
  }

  /**
   * Retrieves the operating system on which the application is currently being executed (or {@link
   * #UNKNOWN} if the operating system is not detected correctly).
   *
   * @return an operating system.
   */
  @NonNull
  public static OperatingSystem current() {
    String name = System.getProperty("os.name", "unknown");

    if (name.contains("linux")) {
      return LINUX;
    } else if (name.contains("osx")) {
      return MAC_OS;
    } else if (name.contains("win")) {
      return WINDOWS;
    }

    return UNKNOWN;
  }

  /**
   * Retrieves an operating system based on its identification key.
   *
   * @param key a key.
   * @return an operating system or, if none matches, an empty optional.
   */
  @NonNull
  public static Optional<OperatingSystem> byKey(@NonNull String key) {
    return Optional.ofNullable(keyMap.get(key));
  }

  @Nullable
  public String getKey() {
    return this.key;
  }
}

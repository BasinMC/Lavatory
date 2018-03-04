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

/**
 * Provides a list of valid release types which identify the stability and age of the release.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public enum VersionType {

  /**
   * <p><strong>Alpha</strong></p>
   *
   * <p>Indicates that a version has been released during the game's alpha, infdev indev or
   * pre-classic phases (ranging from the initial game version up to 1.0.0).</p>
   *
   * <p>Note that this differs from the typical classification of version numbers and refers mostly
   * to how the game is to be launched rather than identifying its development phase (thus including
   * the full range up until the end of alpha).</p>
   */
  OLD_ALPHA(false, true),

  /**
   * <p><strong>Beta</strong></p>
   *
   * <p>Indicates that a version has been released during the game's beta phase (ranging from beta
   * 1.0 up to 1.9-pre6).</p>
   */
  OLD_BETA(false, true),

  /**
   * <p><strong>Release</strong></p>
   *
   * <p>Indicates that a version has been released after the full release of the game (starting with
   * version 1.0) and is considered stable.</p>
   */
  RELEASE(true, false),

  /**
   * <p><strong>Snapshot</strong></p>
   *
   * <p>Indicates that a version is a preview release which does not represent the state of the full
   * release (e.g. it is considered unstable and may change significantly before being
   * released).</p>
   */
  SNAPSHOT(false, false);

  private final boolean stable;
  private final boolean old;

  VersionType(boolean stable, boolean old) {
    this.stable = stable;
    this.old = old;
  }

  /**
   * <p>Evaluates whether this particular release type is considered stable (e.g. is relatively
   * unlikely to crash and will not change by much with the exception of bugfixes).</p>
   *
   * <p>Note that this information should be taken with a grain of salt as history shows that bug
   * fix releases often contain new features in addition to their respective bug fixes and may even
   * be incredibly buggy at times.</p>
   */
  public boolean isStable() {
    return this.stable;
  }

  /**
   * <p>Evaluates whether this particular release type is considered old.</p>
   *
   * <p>Note that this value does not actually indicate a certain release age but instead refer to
   * release types which are no longer used (e.g. alpha and beta releases). Typically these releases
   * also go hand in hand with special requirements when attempting to launch them.</p>
   */
  public boolean isOld() {
    return this.old;
  }
}

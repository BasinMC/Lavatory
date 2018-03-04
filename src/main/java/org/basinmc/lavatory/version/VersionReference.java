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
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Provides an excerpt of version metadata for a specific game release.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class VersionReference {

  private final String id;
  private final VersionType type;
  private final OffsetDateTime releaseTime;
  private final OffsetDateTime modificationTime;
  private final URL url;

  @JsonCreator
  private VersionReference(
      @NonNull @JsonProperty(value = "id", required = true) String id,
      @NonNull @JsonProperty(value = "type", required = true) String type,
      @NonNull @JsonProperty(value = "releaseTime", required = true) String releaseTime,
      @NonNull @JsonProperty(value = "time", required = true) String modificationTime,
      @NonNull @JsonProperty(value = "url", required = true) URL url) {
    this.id = id;
    this.url = url;

    try {
      this.type = VersionType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Illegal type: " + type, ex);
    }

    try {
      this.releaseTime = OffsetDateTime.parse(releaseTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Illegal releaseTime: " + releaseTime, ex);
    }

    try {
      this.modificationTime = OffsetDateTime
          .parse(modificationTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Illegal modificationTime: " + modificationTime, ex);
    }
  }

  @NonNull
  public String getId() {
    return this.id;
  }

  @NonNull
  public VersionType getType() {
    return this.type;
  }

  @NonNull
  public OffsetDateTime getReleaseTime() {
    return this.releaseTime;
  }

  @NonNull
  public OffsetDateTime getModificationTime() {
    return this.modificationTime;
  }

  @NonNull
  public URL getUrl() {
    return this.url;
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
    VersionReference that = (VersionReference) o;
    return Objects.equals(this.id, that.id) &&
        this.type == that.type &&
        Objects.equals(this.releaseTime, that.releaseTime) &&
        Objects.equals(this.modificationTime, that.modificationTime) &&
        Objects.equals(this.url, that.url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.type, this.releaseTime, this.modificationTime, this.url);
  }
}

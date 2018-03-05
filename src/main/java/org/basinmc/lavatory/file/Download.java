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
package org.basinmc.lavatory.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.net.URL;
import java.util.Objects;

/**
 * Represents a downloadable artifact which identifies its location, size and file checksum.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Download {

  private final String sha1;
  private final long size;
  private final URL url;

  @JsonCreator
  public Download(
      @NonNull @JsonProperty(value = "sha1", required = true) String sha1,
      @JsonProperty(value = "size", required = true) long size,
      @NonNull @JsonProperty(value = "url", required = true) URL url) {
    this.sha1 = sha1;
    this.size = size;
    this.url = url;
  }

  /**
   * Retrieves a sha1 checksum for the file in its valid state.
   *
   * @return a checksum.
   */
  @NonNull
  public String getSha1() {
    return this.sha1;
  }

  /**
   * Retrieves the total file size for this download (in bytes).
   *
   * @return a file size.
   */
  public long getSize() {
    return this.size;
  }

  /**
   * Retrieves the absolute URL from which this file may be retrieved.
   *
   * @return a url.
   */
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
    if (!(o instanceof Download)) {
      return false;
    }
    Download download = (Download) o;
    return this.size == download.size &&
        Objects.equals(this.sha1, download.sha1) &&
        Objects.equals(this.url, download.url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.sha1, this.size, this.url);
  }
}
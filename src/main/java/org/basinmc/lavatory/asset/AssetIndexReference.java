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
package org.basinmc.lavatory.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import org.basinmc.lavatory.file.Download;

/**
 * Represents a reference to an asset index which effectively identifies where to retrieve game
 * assets from.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AssetIndexReference extends Download {

  private final String id;
  private final long totalSize;

  @JsonCreator
  public AssetIndexReference(
      @NonNull @JsonProperty(value = "id", required = true) String id,
      @NonNull @JsonProperty(value = "sha1", required = true) String sha1,
      @JsonProperty(value = "size", required = true) long size,
      @NonNull @JsonProperty(value = "url", required = true) URL url,
      @JsonProperty(value = "totalSize", required = true) long totalSize) {
    super(sha1, size, url);
    this.id = id;
    this.totalSize = totalSize;
  }

  /**
   * Fetches the full asset index from the server and parses its contents.
   *
   * @return a parsed asset index.
   * @throws IOException when the server is unreachable, responds with an error code or when the
   * data is malformed.
   */
  @NonNull
  public AssetIndex fetch() throws IOException {
    try (InputStream inputStream = this.getUrl().openStream()) {
      return AssetIndex.read(inputStream);
    }
  }

  /**
   * Retrieves a globally unique identifier for this asset index.
   *
   * @return an index.
   */
  @NonNull
  public String getId() {
    return this.id;
  }

  /**
   * Retrieves the combined size for all files within the asset index (in bytes).
   *
   * @return the total size.
   */
  public long getTotalSize() {
    return this.totalSize;
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
    AssetIndexReference that = (AssetIndexReference) o;
    return this.totalSize == that.totalSize;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.totalSize);
  }
}

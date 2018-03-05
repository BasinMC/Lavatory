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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Represents a single asset object of an arbitrary file type within an index.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AssetObject {

  /**
   * <p>Defines the asset object URL format which indicates where a specific asset can be
   * downloaded.</p>
   *
   * <p>This format accepts two parameters (supplied via {@link String#format(String,
   * Object...)}):</p>
   *
   * <ol>
   *
   * <li>The first two characters of the file hash</li>
   *
   * <li>The complete file hash</li>
   *
   * </ol>
   */
  public static final String ASSET_URL_FORMAT = "https://resources.download.minecraft.net/%s/%s";

  private final String hash;
  private final long size;
  private final URL url;

  public AssetObject(String hash, long size, URL url) {
    this.hash = hash;
    this.size = size;
    this.url = url;
  }

  @JsonCreator
  protected AssetObject(
      @JsonProperty(value = "hash", required = true) String hash,
      @JsonProperty(value = "size", required = true) long size) throws MalformedURLException {
    this.hash = hash;
    this.size = size;
    this.url = new URL(String.format(ASSET_URL_FORMAT, hash.substring(0, 2), hash));
  }

  @NonNull
  public String getHash() {
    return this.hash;
  }

  public long getSize() {
    return this.size;
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
    AssetObject that = (AssetObject) o;
    return this.size == that.size &&
        Objects.equals(this.hash, that.hash) &&
        Objects.equals(this.url, that.url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.hash, this.size, this.url);
  }
}

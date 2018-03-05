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
import java.net.MalformedURLException;
import java.net.URL;
import org.basinmc.lavatory.file.Download;

/**
 * Represents a single asset object of an arbitrary file type within an index.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AssetObject extends Download {

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

  public AssetObject(String hash, long size, URL url) {
    super(hash, size, url);
  }

  @JsonCreator
  protected AssetObject(
      @JsonProperty(value = "hash", required = true) String hash,
      @JsonProperty(value = "size", required = true) long size) throws MalformedURLException {
    this(hash, size, new URL(String.format(ASSET_URL_FORMAT, hash.substring(0, 2), hash)));
  }
}

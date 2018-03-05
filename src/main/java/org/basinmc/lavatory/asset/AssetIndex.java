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
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an index of resource files.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AssetIndex {

  private final Map<String, AssetObject> objects;

  @JsonCreator
  public AssetIndex(
      @NonNull @JsonProperty(value = "objects", required = true) Map<String, AssetObject> objects) {
    this.objects = new HashMap<>(objects);
  }

  /**
   * Retrieves a specific object from the index.
   *
   * @param path a relative path.
   * @return an asset object or, if no such object exists, an empty optional.
   */
  @NonNull
  public Optional<AssetObject> getObject(@NonNull String path) {
    return Optional.ofNullable(this.objects.get(path));
  }

  @NonNull
  public Map<String, AssetObject> getObjects() {
    return Collections.unmodifiableMap(this.objects);
  }

  /**
   * Decodes an asset index from the specified input stream.
   *
   * @param inputStream an input stream.
   * @return an asset index.
   * @throws IOException when reading from the input stream fails or the data is malformed.
   */
  @NonNull
  public static AssetIndex read(@NonNull InputStream inputStream) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(inputStream, AssetIndex.class);
  }

  /**
   * Decodes an asset index from the specified file.
   *
   * @param path a file path.
   * @return an asset index.
   * @throws IOException when reading from the file fails or the data is malformed.
   */
  @NonNull
  public static AssetIndex read(@NonNull Path path) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      return read(inputStream);
    }
  }

  /**
   * Decodes an asset index from the specified reader.
   *
   * @param reader a reader.
   * @return an asset index.
   * @throws IOException when reading from the file fails or the data is malformed.
   */
  @NonNull
  public static AssetIndex read(@NonNull Reader reader) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(reader, AssetIndex.class);
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
    AssetIndex that = (AssetIndex) o;
    return Objects.equals(this.objects, that.objects);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.objects);
  }
}

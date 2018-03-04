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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.basinmc.lavatory.version.VersionReference;

/**
 * Represents a loaded version manifest which lists every available game version, their release
 * date, modification time, type and manifest url as well as the latest available version for the
 * two currently available release types (release and snapshot).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class Manifest {

  /**
   * Defines the default location from which this manifest can typically be retrieved.
   */
  public static final String MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

  private final VersionReference latestStable;
  private final VersionReference latestSnapshot;
  private final Map<String, VersionReference> versionMap;

  public Manifest(
      @NonNull VersionReference latestStable,
      @NonNull VersionReference latestSnapshot,
      @NonNull Map<String, VersionReference> versionMap) {
    this.latestStable = latestStable;
    this.latestSnapshot = latestSnapshot;
    this.versionMap = new HashMap<>(versionMap);
  }

  @JsonCreator
  protected Manifest(
      @NonNull @JsonProperty(value = "latest", required = true) Map<String, String> latestVersions,
      @NonNull @JsonProperty(value = "versions", required = true) Set<VersionReference> references) {
    this.versionMap = references.stream()
        .collect(Collectors.toMap(VersionReference::getId, (r) -> r));

    String latestVersion = latestVersions.get("release");
    if (latestVersion == null) {
      throw new IllegalArgumentException(
          "Illegal version table: No latest stable release version given");
    }
    this.latestStable = this.versionMap.get(latestVersion);
    if (this.latestStable == null) {
      throw new IllegalArgumentException(
          "Illegal version table: Invalid latest stable release version given (\"" + latestVersion
              + "\")");
    }

    latestVersion = latestVersions.get("snapshot");
    if (latestVersion == null) {
      throw new IllegalArgumentException(
          "Illegal version table: No latest snapshot release version given");
    }
    this.latestSnapshot = this.versionMap.get(latestVersion);
    if (this.latestSnapshot == null) {
      throw new IllegalArgumentException(
          "Illegal version table: Invalid latest snapshot release version given (\"" + latestVersion
              + "\")");
    }
  }

  /**
   * Fetches the current version manifest from the servers.
   *
   * @return a manifest.
   * @throws IOException when the server response is invalid (e.g. when any type of error code is
   * sent), no connection can be established or the data is malformed.
   */
  @NonNull
  public static Manifest fetch() throws IOException {
    try (InputStream inputStream = new URL(MANIFEST_URL).openStream()) {
      return read(inputStream);
    }
  }

  /**
   * Retrieves an excerpt of the latest stable version.
   *
   * @return a version excerpt.
   */
  @NonNull
  public VersionReference getLatestStable() {
    return this.latestStable;
  }

  /**
   * Retrieves an excerpt of the latest snapshot version.
   *
   * @return a version excerpt.
   */
  @NonNull
  public VersionReference getLatestSnapshot() {
    return this.latestSnapshot;
  }

  /**
   * Retrieves an excerpt of a specific version (if known).
   *
   * @param versionId a version identifier.
   * @return a version excerpt or an empty optional if no such version is available.
   */
  @NonNull
  public Optional<VersionReference> getVersion(@NonNull String versionId) {
    return Optional.ofNullable(this.versionMap.get(versionId));
  }

  /**
   * Retrieves a set of available versions (and their respective excerpts).
   *
   * @return a set of version excerpts.
   */
  @NonNull
  public Set<VersionReference> getVersions() {
    return Collections.unmodifiableSet(new HashSet<>(this.versionMap.values()));
  }

  /**
   * Decodes a launcher version manifest from the specified input stream.
   *
   * @param inputStream an input stream.
   * @return a version manifest.
   * @throws IOException when reading from the input stream fails or the data is malformed.
   */
  @NonNull
  public static Manifest read(@NonNull InputStream inputStream) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(inputStream, Manifest.class);
  }

  /**
   * Decodes a launcher version manifest from the specified file.
   *
   * @param path a file path.
   * @return a version manifest.
   * @throws IOException when reading from the file fails or the data is malformed.
   */
  @NonNull
  public static Manifest read(@NonNull Path path) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      return read(inputStream);
    }
  }

  /**
   * Decodes a launcher version manifest from the specified reader.
   *
   * @param reader a reader.
   * @return a version manifest.
   * @throws IOException when reading from the file fails or the data is malformed.
   */
  @NonNull
  public static Manifest read(@NonNull Reader reader) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(reader, Manifest.class);
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
    Manifest that = (Manifest) o;
    return Objects.equals(this.latestStable, that.latestStable) &&
        Objects.equals(this.latestSnapshot, that.latestSnapshot) &&
        Objects.equals(this.versionMap, that.versionMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.latestStable, this.latestSnapshot, this.versionMap);
  }
}

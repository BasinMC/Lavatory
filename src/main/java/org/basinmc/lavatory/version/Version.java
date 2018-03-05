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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.basinmc.lavatory.ResolverContext;
import org.basinmc.lavatory.asset.AssetIndexReference;
import org.basinmc.lavatory.file.Download;
import org.basinmc.lavatory.file.LibraryReference;
import org.basinmc.lavatory.file.LoggerConfiguration;
import org.basinmc.lavatory.rule.RuleControlledResourceContainer;

/**
 * Represents a full set of version details which identifies all the necessary values to download
 * and run a particular game version along with all of its dependencies and assets.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Version implements RuleControlledResourceContainer {

  private final String id;
  private final int minimumLauncherVersion;
  private final VersionType type;
  private final OffsetDateTime releaseTime;
  private final OffsetDateTime modificationTime;

  private final String mainClass;
  private final List<ProgramArgument> gameArguments;
  private final List<ProgramArgument> jvmArguments;

  private final Download clientDownload;
  private final Download serverDownload;
  private final Download windowsServerDownload;
  private final String assets;
  private final AssetIndexReference assetIndex;
  private final Map<String, LibraryReference> libraries;

  private final LoggerConfiguration clientLoggerConfiguration;

  public Version(
      @NonNull String id,
      int minimumLauncherVersion,
      @NonNull VersionType type,
      @NonNull OffsetDateTime releaseTime,
      @NonNull OffsetDateTime modificationTime,
      @NonNull String mainClass,
      @NonNull List<ProgramArgument> gameArguments,
      @NonNull List<ProgramArgument> jvmArguments,
      @NonNull Download clientDownload,
      @Nullable Download serverDownload,
      @Nullable Download windowsServerDownload, String assets,
      @NonNull AssetIndexReference assetIndex,
      @NonNull Set<LibraryReference> libraries,
      @Nullable LoggerConfiguration clientLoggerConfiguration) {
    this.id = id;
    this.minimumLauncherVersion = minimumLauncherVersion;
    this.type = type;
    this.releaseTime = releaseTime;
    this.modificationTime = modificationTime;
    this.mainClass = mainClass;
    this.gameArguments = new ArrayList<>(gameArguments);
    this.jvmArguments = new ArrayList<>(jvmArguments);
    this.clientDownload = clientDownload;
    this.serverDownload = serverDownload;
    this.windowsServerDownload = windowsServerDownload;
    this.assets = assets;
    this.assetIndex = assetIndex;
    this.libraries = libraries.stream()
        .collect(Collectors.toMap(
            LibraryReference::getName,
            (l) -> l,
            (a, b) -> a
        ));
    this.clientLoggerConfiguration = clientLoggerConfiguration;
  }

  @JsonCreator
  protected Version(
      @NonNull @JsonProperty(value = "id", required = true) String id,
      @JsonProperty(value = "minimumLauncherVersion", required = true) int minimumLauncherVersion,
      @NonNull @JsonProperty(value = "type", required = true) String type,
      @NonNull @JsonProperty(value = "releaseTime", required = true) OffsetDateTime releaseTime,
      @NonNull @JsonProperty(value = "time", required = true) OffsetDateTime modificationTime,
      @NonNull @JsonProperty(value = "mainClass", required = true) String mainClass,
      @Nullable @JsonProperty("arguments") Map<String, List<ProgramArgument>> arguments,
      @NonNull @JsonProperty(value = "downloads", required = true) Map<String, Download> downloads,
      @NonNull @JsonProperty(value = "assets", required = true) String assets,
      @NonNull @JsonProperty(value = "assetIndex", required = true) AssetIndexReference assetIndex,
      @NonNull @JsonProperty(value = "libraries", required = true) Set<LibraryReference> libraries,
      @Nullable @JsonProperty("logging") Map<String, LoggerConfiguration> loggerConfigurations,
      @Nullable @JsonProperty("minecraftArguments") String legacyArguments) {
    this.id = id;
    this.minimumLauncherVersion = minimumLauncherVersion;
    this.type = VersionType.valueOf(type.toUpperCase());
    this.releaseTime = releaseTime;
    this.modificationTime = modificationTime;
    this.mainClass = mainClass;
    this.assets = assets;
    this.assetIndex = assetIndex;
    this.libraries = libraries.stream()
        .collect(Collectors.toMap(
            LibraryReference::getName,
            (l) -> l,
            (a, b) -> a
        ));
    this.clientLoggerConfiguration =
        loggerConfigurations == null ? null : loggerConfigurations.get("client");

    // legacy versions make use of a slightly less complex argument structure and thus we'll need to
    // convert them to the new format to bridge the gap for our callers
    if (legacyArguments != null) {
      if (arguments == null) {
        arguments = new HashMap<>();
      }

      List<ProgramArgument> args = arguments.computeIfAbsent("game", (k) -> new ArrayList<>());

      Stream.of(legacyArguments.split(" "))
          .map(ProgramArgument::new)
          .forEach(args::add);
    }

    this.gameArguments = Optional.ofNullable(arguments)
        .flatMap((m) -> Optional.ofNullable(m.get("game")))
        .orElseGet(Collections::emptyList);
    this.jvmArguments = Optional.ofNullable(arguments)
        .flatMap((m) -> Optional.ofNullable(m.get("jvm")))
        .orElseGet(Collections::emptyList);

    this.clientDownload = downloads.get("client");
    this.serverDownload = downloads.get("server");
    this.windowsServerDownload = downloads.get("windows_server");
  }

  /**
   * Retrieves a globally unique identifier for this version.
   *
   * @return an identifier.
   */
  @NonNull
  public String getId() {
    return this.id;
  }

  /**
   * Retrieves the minimum required launcher specification which is required to be supported in
   * order to download and run this version.
   *
   * @return a specification revision.
   */
  public int getMinimumLauncherVersion() {
    return this.minimumLauncherVersion;
  }

  /**
   * Retrieves the type and stability of this particular version.
   *
   * @return a version type.
   */
  @NonNull
  public VersionType getType() {
    return this.type;
  }

  /**
   * Retrieves the time at which this version has originally been released.
   *
   * @return a time and date.
   */
  @NonNull
  public OffsetDateTime getReleaseTime() {
    return this.releaseTime;
  }

  /**
   * Retrieves the time at which this manifest was last modified.
   *
   * @return a time and date.
   */
  @NonNull
  public OffsetDateTime getModificationTime() {
    return this.modificationTime;
  }

  /**
   * Retrieves the class which contains the client's main method.
   *
   * @return a class name.
   */
  @NonNull
  public String getMainClass() {
    return this.mainClass;
  }

  /**
   * <p>Retrieves a list of potential game arguments which are to be passed to the client executable
   * when launching the game.</p>
   *
   * <p>Note that these parameters may be tied to rules which evaluate whether and under which
   * circumstances they are to be passed to the client.</p>
   *
   * @return a list of parsed arguments.
   */
  @NonNull
  public List<ProgramArgument> getGameArguments() {
    return Collections.unmodifiableList(this.gameArguments);
  }

  /**
   * <p>Retrieves a list of potential JVM arguments which are to be passed to the Java executable
   * when launching the game (only applies to the client).</p>
   *
   * <p>Note that these parameters may be tied to rules which evaluate whether and under which
   * circumstances they are to be passed to the client.</p>
   *
   * @return a list of parsed arguments.
   */
  @NonNull
  public List<ProgramArgument> getJvmArguments() {
    return Collections.unmodifiableList(this.jvmArguments);
  }

  /**
   * Retrieves the location, size and checksum for the client archive for this version.
   *
   * @return a download reference.
   */
  @NonNull
  public Download getClientDownload() {
    return this.clientDownload;
  }

  /**
   * Retrieves the location, size and checksum for the server archive for this version.
   *
   * @return a download reference if a dedicated server implementation exists for this version.
   */
  @NonNull
  public Optional<Download> getServerDownload() {
    return Optional.ofNullable(this.serverDownload);
  }

  /**
   * <p>Retrieves the location, size and checksum for the Windows server executable for this
   * version.</p>
   *
   * <p>Typically this will be a wrapper executable which contains an attached version of the
   * standard dedicated server jar and may thus be used interchangeably with the standard jar on
   * Windows machines.</p>
   *
   * @return a download reference if a windows executable for the dedicated server implementation
   * exists for this version.
   */
  @NonNull
  public Optional<Download> getWindowsServerDownload() {
    return Optional.ofNullable(this.windowsServerDownload);
  }

  /**
   * Retrieves the asset revision on which the client of this game version relies upon.
   *
   * @return an asset version.
   */
  @NonNull
  public String getAssets() {
    return this.assets;
  }

  /**
   * Retrieves the location, size, checksum and total asset size for the asset index used by this
   * game version.
   *
   * @return an asset version.
   */
  @NonNull
  public AssetIndexReference getAssetIndex() {
    return this.assetIndex;
  }

  /**
   * Retrieves a specific library based on its name from within this version's dependencies.
   *
   * @param name a library name (including its version).
   * @return a library reference or, if none matches, an empty optional.
   */
  @NonNull
  public Optional<LibraryReference> getLibrary(@NonNull String name) {
    return Optional.ofNullable(this.libraries.get(name));
  }

  /**
   * <p>Retrieves a set of libraries on which this game version relies.</p>
   *
   * <p>Note that these libraries typically only apply to the server archive as the required
   * libraries are part of the server archive.</p>
   *
   * @return a set of libraries.
   */
  @NonNull
  public Set<LibraryReference> getLibraries() {
    return Collections.unmodifiableSet(new HashSet<>(this.libraries.values()));
  }

  /**
   * Retrieves the logger configuration for this client version.
   *
   * @return a logger configuration if an external configuration is used.
   */
  @NonNull
  public Optional<LoggerConfiguration> getClientLoggerConfiguration() {
    return Optional.ofNullable(this.clientLoggerConfiguration);
  }

  /**
   * Decodes a version manifest from the specified input stream.
   *
   * @param inputStream an input stream.
   * @return a version manifest.
   * @throws IOException when reading from the input stream fails or the data is malformed.
   */
  @NonNull
  public static Version read(@NonNull InputStream inputStream) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    mapper.registerModule(new JavaTimeModule());
    return mapper.readValue(inputStream, Version.class);
  }

  /**
   * Decodes a version manifest from the specified file.
   *
   * @param path a file path.
   * @return a version manifest.
   * @throws IOException when reading from the file fails or the data is malformed.
   */
  @NonNull
  public static Version read(@NonNull Path path) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      return read(inputStream);
    }
  }

  /**
   * Decodes a version manifest from the specified reader.
   *
   * @param reader a reader.
   * @return a version manifest.
   * @throws IOException when reading from the file fails or the data is malformed.
   */
  @NonNull
  public static Version read(@NonNull Reader reader) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    mapper.registerModule(new JavaTimeModule());
    return mapper.readValue(reader, Version.class);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Version reduce(@NonNull ResolverContext ctx) {
    return new Version(
        this.id,
        this.minimumLauncherVersion,
        this.type,
        this.releaseTime,
        this.modificationTime,
        this.mainClass,
        this.gameArguments.stream()
            .filter((a) -> a.evaluate(ctx))
            .collect(Collectors.toList()),
        this.jvmArguments.stream()
            .filter((a) -> a.evaluate(ctx))
            .collect(Collectors.toList()),
        this.clientDownload,
        this.serverDownload,
        this.windowsServerDownload,
        this.assets,
        this.assetIndex,
        this.libraries.values().stream()
            .filter((l) -> l.evaluate(ctx))
            .collect(Collectors.toSet()),
        this.clientLoggerConfiguration
    );
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
    Version version = (Version) o;
    return this.minimumLauncherVersion == version.minimumLauncherVersion &&
        Objects.equals(this.id, version.id) &&
        this.type == version.type &&
        Objects.equals(this.releaseTime, version.releaseTime) &&
        Objects.equals(this.modificationTime, version.modificationTime) &&
        Objects.equals(this.mainClass, version.mainClass) &&
        Objects.equals(this.gameArguments, version.gameArguments) &&
        Objects.equals(this.jvmArguments, version.jvmArguments) &&
        Objects.equals(this.clientDownload, version.clientDownload) &&
        Objects.equals(this.serverDownload, version.serverDownload) &&
        Objects.equals(this.windowsServerDownload, version.windowsServerDownload) &&
        Objects.equals(this.assets, version.assets) &&
        Objects.equals(this.assetIndex, version.assetIndex) &&
        Objects.equals(this.libraries, version.libraries);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.minimumLauncherVersion, this.type, this.releaseTime,
        this.modificationTime,
        this.mainClass,
        this.gameArguments, this.jvmArguments, this.clientDownload, this.serverDownload,
        this.windowsServerDownload,
        this.assets,
        this.assetIndex, this.libraries);
  }
}

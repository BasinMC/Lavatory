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
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.basinmc.lavatory.rule.AbstractRuleControlledResource;
import org.basinmc.lavatory.rule.Rule;

/**
 * Represents a reference to a library on which a game version depends as well as all of its
 * respective classifiers.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LibraryReference extends AbstractRuleControlledResource {

  private final String name;

  private final LibraryArtifact linuxNativesArtifact;
  private final LibraryArtifact macNativesArtifact;
  private final LibraryArtifact windowsNativesArtifact;

  private final DownloadMap downloads;
  private final ExtractionConfiguration extractionConfiguration;

  public LibraryReference(
      @NonNull String name,
      @Nullable LibraryArtifact linuxNativesArtifact,
      @Nullable LibraryArtifact macNativesArtifact,
      @Nullable LibraryArtifact windowsNativesArtifact,
      @NonNull DownloadMap downloads,
      @Nullable ExtractionConfiguration extractionConfiguration,
      @Nullable Set<Rule> rules) {
    super(rules);
    this.name = name;
    this.linuxNativesArtifact = linuxNativesArtifact;
    this.macNativesArtifact = macNativesArtifact;
    this.windowsNativesArtifact = windowsNativesArtifact;
    this.downloads = downloads;
    this.extractionConfiguration = extractionConfiguration;
  }

  @JsonCreator
  protected LibraryReference(
      @NonNull @JsonProperty(value = "name", required = true) String name,
      @NonNull @JsonProperty(value = "downloads", required = true) DownloadMap downloads,
      @Nullable @JsonProperty("natives") Map<String, String> classifierMap,
      @Nullable @JsonProperty("extract") ExtractionConfiguration extractionConfiguration,
      @Nullable @JsonProperty("rules") Set<Rule> rules) {
    super(rules);
    this.name = name;
    this.downloads = downloads;
    this.extractionConfiguration = extractionConfiguration;

    if (classifierMap == null) {
      classifierMap = Collections.emptyMap();
    }

    String classifier = classifierMap.get("linux");
    this.linuxNativesArtifact =
        classifier != null ? Optional.ofNullable(this.downloads.classifiers.get(classifier))
            .orElseThrow(() -> new IllegalArgumentException(
                "Illegal native artifact for linux: Artifact is missing")) : null;

    classifier = classifierMap.get("osx");
    this.macNativesArtifact =
        classifier != null ? Optional.ofNullable(this.downloads.classifiers.get(classifier))
            .orElseThrow(() -> new IllegalArgumentException(
                "Illegal native artifact for osx: Artifact is missing")) : null;

    classifier = classifierMap.get("windows");
    this.windowsNativesArtifact =
        classifier != null ? Optional.ofNullable(this.downloads.classifiers.get(classifier))
            .orElseThrow(() -> new IllegalArgumentException(
                "Illegal native artifact for windows: Artifact is missing")) : null;
  }

  /**
   * Retrieves the identifier for this library reference (typically similarly formatted to a maven
   * coordinate).
   *
   * @return an identifier.
   */
  @NonNull
  public String getName() {
    return this.name;
  }

  /**
   * Retrieves the artifact which contains the Linux natives for this particular library.
   *
   * @return an artifact or, if none is specified, an empty optional.
   */
  @NonNull
  public Optional<LibraryArtifact> getLinuxNativesArtifact() {
    return Optional.ofNullable(this.linuxNativesArtifact);
  }

  /**
   * Retrieves the artifact which contains the Mac OS natives for this particular library.
   *
   * @return an artifact or, if none is specified, an empty optional.
   */
  @NonNull
  public Optional<LibraryArtifact> getMacNativesArtifact() {
    return Optional.ofNullable(this.macNativesArtifact);
  }

  /**
   * Retrieves the artifact which contains the Windows natives for this particular library.
   *
   * @return an artifact or, if none is specified, an empty optional.
   */
  @NonNull
  public Optional<LibraryArtifact> getWindowsNativesArtifact() {
    return Optional.ofNullable(this.windowsNativesArtifact);
  }

  /**
   * Retrieves a map of downloadable artifacts.
   *
   * @return a download map.
   */
  @NonNull
  public DownloadMap getDownloads() {
    return this.downloads;
  }

  /**
   * Retrieves an extraction config which identifies whether and how the artifact is to be
   * extracted.
   *
   * @return an extraction config or, if no extraction is desired, an empty optional.
   */
  @NonNull
  public Optional<ExtractionConfiguration> getExtractionConfiguration() {
    return Optional.ofNullable(this.extractionConfiguration);
  }

  /**
   * Provides a map of downloadable artifacts along with their classifiers.
   */
  public static class DownloadMap {

    private final LibraryArtifact artifact;
    private final Map<String, LibraryArtifact> classifiers;

    @JsonCreator
    public DownloadMap(
        @Nullable @JsonProperty("artifact") LibraryArtifact artifact,
        @Nullable @JsonProperty("classifiers") Map<String, LibraryArtifact> classifiers) {
      this.artifact = artifact;
      this.classifiers = classifiers == null ? Collections.emptyMap() : classifiers;
    }

    /**
     * Retrieves the main artifact for the parent library.
     *
     * @return an artifact or, when no main artifact exists, an empty optional.
     */
    @NonNull
    public Optional<LibraryArtifact> getArtifact() {
      return Optional.ofNullable(this.artifact);
    }

    /**
     * Retrieves the full classifier map which maps specific library classifiers to their respective
     * libraries.
     *
     * @return a classifier map.
     */
    @NonNull
    public Map<String, LibraryArtifact> getClassifiers() {
      return Collections.unmodifiableMap(this.classifiers);
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
      DownloadMap that = (DownloadMap) o;
      return Objects.equals(this.artifact, that.artifact) &&
          Objects.equals(this.classifiers, that.classifiers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return Objects.hash(this.artifact, this.classifiers);
    }
  }

  /**
   * Represents an extraction configuration which identifies whether and how a library is to be
   * extracted.
   */
  public static class ExtractionConfiguration {

    private final Set<String> excludes;
    private final Set<String> includes;

    @JsonCreator
    public ExtractionConfiguration(
        @Nullable @JsonProperty("exclude") Set<String> excludes,
        @Nullable @JsonProperty("include") Set<String> includes) {
      this.excludes = excludes == null ? Collections.emptySet() : excludes;
      this.includes = includes == null ? Collections.emptySet() : includes;
    }

    /**
     * Retrieves a set of paths which are to be excluded in the extracted version of the archive.
     *
     * @return a set of excluded paths.
     */
    @NonNull
    public Set<String> getExcludes() {
      return Collections.unmodifiableSet(this.excludes);
    }

    /**
     * Retrieves a set of paths which are to be explicitly included in the extracted version of the
     * archive.
     *
     * @return a set of included paths.
     */
    @NonNull
    public Set<String> getIncludes() {
      return Collections.unmodifiableSet(this.includes);
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
      ExtractionConfiguration that = (ExtractionConfiguration) o;
      return Objects.equals(this.excludes, that.excludes) &&
          Objects.equals(this.includes, that.includes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return Objects.hash(this.excludes, this.includes);
    }
  }
}

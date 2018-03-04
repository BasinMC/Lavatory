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
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a single artifact for a specific library as well as its expected storage location.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LibraryArtifact extends Download {

  private final Path path;

  @JsonCreator
  public LibraryArtifact(
      @NonNull @JsonProperty(value = "sha1", required = true) String sha1,
      @JsonProperty(value = "size", required = true) long size,
      @NonNull @JsonProperty(value = "url", required = true) URL url,
      @NonNull @JsonProperty(value = "path", required = true) Path path) {
    super(sha1, size, url);
    this.path = path;
  }

  /**
   * Retrieves a relative path to the expected library location within the launcher directory.
   *
   * @return a relative path.
   */
  @NonNull
  public Path getPath() {
    return this.path;
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
    LibraryArtifact library = (LibraryArtifact) o;
    return Objects.equals(this.path, library.path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.path);
  }
}

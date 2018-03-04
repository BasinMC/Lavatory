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
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a special logger configuration which is passed to the program in order to customize
 * its log output.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LoggerConfiguration {

  private final LoggerConfigurationFile file;
  private final String argument;
  private final String type;

  @JsonCreator
  public LoggerConfiguration(
      @NonNull @JsonProperty(value = "file", required = true) LoggerConfigurationFile file,
      @Nullable @JsonProperty("argument") String argument,
      @NonNull @JsonProperty(value = "type", required = true) String type) {
    this.file = file;
    this.argument = argument;
    this.type = type;
  }

  /**
   * Retrieves the logger configuration file which is to be passed.
   *
   * @return a configuration file.
   */
  @NonNull
  public LoggerConfigurationFile getFile() {
    return this.file;
  }

  /**
   * <p>Retrieves the argument with which this configuration is introduced to the application.</p>
   *
   * <p>Typically this value, if present, will also include the {@code ${path}} variable which is
   * expected to be replaced with a reference to the log configuration file.</p>
   *
   * @return an argument which introduces the log file to the application or, if none is necessary,
   * an empty optional.
   */
  @NonNull
  public Optional<String> getArgument() {
    return Optional.ofNullable(this.argument);
  }

  /**
   * <p>Retrieves the type of log configuration.</p>
   *
   * <p>TODO: Replace with an enum?</p>
   *
   * @return a configuration type.
   */
  @NonNull
  public String getType() {
    return this.type;
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
    LoggerConfiguration that = (LoggerConfiguration) o;
    return Objects.equals(this.file, that.file) &&
        Objects.equals(this.argument, that.argument) &&
        Objects.equals(this.type, that.type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.file, this.argument, this.type);
  }
}

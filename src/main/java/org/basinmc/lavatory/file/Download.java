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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a downloadable artifact which identifies its location, size and file checksum.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Download {

  /**
   * Defines the message digest algorithm name used to generate the checksum.
   */
  public static final String CHECKSUM_ALGORITHM = "SHA-1";

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
   * Downloads the file from the server and writes it to the specified target file.
   *
   * @param target a target file.
   * @throws IOException when the server is unreachable, responds with an error code, the connection
   * is interrupted or when writing to the file fails.
   */
  public void fetch(@NonNull Path target) throws IOException {
    try (FileChannel channel = FileChannel
        .open(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING)) {
      try (InputStream inputStream = this.url.openStream()) {
        try (ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
          channel.transferFrom(inputChannel, 0, Long.MAX_VALUE);
        }
      }
    }
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
   * Retrieves the actual decoded checksum bytes.
   *
   * @return a checksum.
   * @throws IllegalStateException when the hash is of an illegal length.
   */
  public byte[] getSha1Bytes() {
    if (this.sha1.length() % 2 != 0) {
      throw new IllegalStateException("Illegal hash: Odd number of characters");
    }

    byte[] bytes = new byte[this.sha1.length() / 2];

    for (int i = 0; i < bytes.length; ++i) {
      String element = this.sha1.substring(i * 2, i * 2 + 2);
      bytes[i] = (byte) (Short.parseShort(element, 16) & 0xFF);
    }

    return bytes;
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
   * Evaluates whether a specified file matches the checksum and size of this downloadable
   * artifact.
   *
   * @param path a file path.
   * @return true if the files match, false otherwise.
   * @throws IOException when accessing the target file fails.
   * @throws UnsupportedOperationException when the JVM does not support the checksum algorithm.
   */
  public boolean verify(@NonNull Path path) throws IOException {
    // check whether the file size matches first as this is an extremely cheap operation and avoids
    // potential (insanely rare) hash collision issues
    if (Files.size(path) != this.size) {
      return false;
    }

    // if above's check succeeded, we'll actually generate a hash for the file itself and compare it
    // to the expected checksum to make sure the file contents are equal
    MessageDigest digest;

    try {
      digest = MessageDigest.getInstance(CHECKSUM_ALGORITHM);
    } catch (NoSuchAlgorithmException ex) {
      throw new UnsupportedOperationException(
          "JVM does not support " + CHECKSUM_ALGORITHM + " digest algorithm");
    }

    try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
      ByteBuffer heap = ByteBuffer.allocate(512);

      while (channel.read(heap) > 0) {
        heap.flip();
        digest.update(heap);
        heap.clear();
      }

      byte[] checksum = digest.digest();
      return Arrays.equals(this.getSha1Bytes(), checksum);
    }
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

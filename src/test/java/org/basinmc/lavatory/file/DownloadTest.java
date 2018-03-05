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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.Assert;
import org.junit.Test;

/**
 * Evaluates whether the download implementation operates as expected.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DownloadTest {

  /**
   * Evaluates whether hashes (literal notations of all bytes within the resulting checksum) are
   * decoded correctly.
   */
  @Test
  public void testDecodeHash() throws MalformedURLException {
    Download download = new Download(
        "000102030405060708090A0B0C0D0E0F102030405060708090A0B0C0D0E0F0FF", 0,
        new URL("https://example.org"));

    Assert.assertArrayEquals(
        new byte[]{
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
            0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70,
            (byte) 0x80, (byte) 0x90, (byte) 0xA0, (byte) 0xB0,
            (byte) 0xC0, (byte) 0xD0, (byte) 0xE0, (byte) 0xF0, (byte) 0xFF
        },
        download.getSha1Bytes()
    );
  }

  /**
   * Evaluates whether file checksums are verified correctly.
   */
  @Test
  public void testVerifyChecksum() throws IOException {
    Path target = Files.createTempFile("lavatory_test_", ".tmp");

    try {
      try (FileChannel channel = FileChannel
          .open(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
              StandardOpenOption.TRUNCATE_EXISTING);
          InputStream inputStream = this.getClass().getResourceAsStream("/asset_index.json");
          ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
        channel.transferFrom(inputChannel, 0, Long.MAX_VALUE);
      }

      Download downloadA = new Download("be699a4139b08ca97b32cfab621270815037fdbd", 657,
          new URL("https://example.org"));
      Download downloadB = new Download("be699a4139b08ca97b32cfab621270815037fdbd", 655,
          new URL("https://example.org"));
      Download downloadC = new Download("be699a4139b08ca97b32cfab621270815037fdbe", 657,
          new URL("https://example.org"));

      Assert.assertTrue(downloadA.verify(target));
      Assert.assertFalse(downloadB.verify(target));
      Assert.assertFalse(downloadC.verify(target));
    } finally {
      Files.deleteIfExists(target);
    }
  }
}

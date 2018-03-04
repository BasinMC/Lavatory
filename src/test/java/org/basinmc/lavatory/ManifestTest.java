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

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.basinmc.lavatory.version.VersionReference;
import org.basinmc.lavatory.version.VersionType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Evaluates whether {@link Manifest} operates within its expected boundaries.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ManifestTest {

  /**
   * Evaluates whether the manifest is correctly parsed using the data of an arbitrary json
   * document.
   */
  @Test
  public void testParse() throws IOException {
    try (InputStream inputStream = ManifestTest.class
        .getResourceAsStream("/version_manifest.valid.json")) {
      Manifest manifest = Manifest.read(inputStream);

      Assert.assertNotNull(manifest.getLatestSnapshot());

      VersionReference reference = manifest.getLatestStable();
      Assert.assertNotNull(reference);
      Assert.assertEquals("1.12.2", reference.getId());
      Assert.assertEquals(VersionType.RELEASE, reference.getType());
      Assert.assertEquals(
          OffsetDateTime.ofInstant(Instant.ofEpochSecond(1505723986), ZoneId.of("GMT")),
          reference.getReleaseTime()
      );
      Assert.assertEquals(
          OffsetDateTime.ofInstant(Instant.ofEpochSecond(1518712005), ZoneId.of("GMT")),
          reference.getModificationTime()
      );
      Assert.assertEquals(
          "https://launchermeta.mojang.com/mc/game/cf72a57ff499d6d9ade870b2143ee54958bd33ef/1.12.2.json",
          reference.getUrl().toExternalForm()
      );

      reference = manifest.getLatestSnapshot();
      Assert.assertNotNull(reference);
      Assert.assertEquals("18w09a", reference.getId());
      Assert.assertEquals(VersionType.SNAPSHOT, reference.getType());
      Assert.assertEquals(
          OffsetDateTime.ofInstant(Instant.ofEpochSecond(1519913710), ZoneId.of("GMT")),
          reference.getReleaseTime()
      );
      Assert.assertEquals(
          OffsetDateTime.ofInstant(Instant.ofEpochSecond(1519913801), ZoneId.of("GMT")),
          reference.getModificationTime()
      );
      Assert.assertEquals(
          "https://launchermeta.mojang.com/mc/game/4b95a1f10798a6b05f65765bc52cc74862e9289d/18w09a.json",
          reference.getUrl().toExternalForm()
      );
    }
  }

  /**
   * Evaluates whether the parser correctly fails when a release is referenced but not defined.
   */
  @Test(expected = InvalidDefinitionException.class)
  public void testMissingRelease() throws IOException {
    try (InputStream inputStream = ManifestTest.class
        .getResourceAsStream("/version_manifest.missingRelease.json")) {
      Manifest.read(inputStream);
    }
  }

  /**
   * Evaluates whether the parser correctly fails when a snapshot is referenced but not defined.
   */
  @Test(expected = InvalidDefinitionException.class)
  public void testMissingSnapshot() throws IOException {
    try (InputStream inputStream = ManifestTest.class
        .getResourceAsStream("/version_manifest.missingSnapshot.json")) {
      Manifest.read(inputStream);
    }
  }
}

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

import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * Evaluates whether the asset index behaves as expected.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AssetIndexTest {

  /**
   * Evaluates whether the asset index is correctly parsed.
   */
  @Test
  public void testParse() throws IOException {
    try (InputStream inputStream = this.getClass().getResourceAsStream("/asset_index.json")) {
      AssetIndex index = AssetIndex.read(inputStream);

      Assert.assertEquals(5, index.getObjects().size());
      Assert.assertTrue(index.getObject("minecraft/sounds/mob/stray/death2.ogg").isPresent());
      Assert.assertTrue(index.getObject("minecraft/lang/es_mx.json").isPresent());
      Assert.assertTrue(index.getObject("minecraft/sounds/mob/husk/step4.ogg").isPresent());
      Assert.assertTrue(index.getObject("realms/lang/uk_ua.json").isPresent());
      Assert.assertTrue(index.getObject("minecraft/sounds/entity/rabbit/attack2.ogg").isPresent());

      AssetObject object = index.getObject("minecraft/sounds/mob/stray/death2.ogg").get();
      Assert.assertEquals("d48940aeab2d4068bd157e6810406c882503a813", object.getHash());
      Assert.assertEquals(18817, object.getSize());
      Assert.assertEquals(
          "https://resources.download.minecraft.net/d4/d48940aeab2d4068bd157e6810406c882503a813",
          object.getUrl().toExternalForm());
    }
  }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.basinmc.lavatory.ResolverContext;
import org.basinmc.lavatory.asset.AssetIndexReference;
import org.basinmc.lavatory.file.Download;
import org.basinmc.lavatory.file.LibraryReference;
import org.basinmc.lavatory.file.LoggerConfiguration;
import org.basinmc.lavatory.rule.Rule;
import org.basinmc.lavatory.rule.Rule.Action;
import org.basinmc.lavatory.rule.RuleCondition;
import org.basinmc.lavatory.rule.system.OperatingSystem;
import org.basinmc.lavatory.rule.system.OperatingSystemCondition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Provides test cases which evaluate whether versions and all of their children are parsed
 * correctly and operate as expected.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class VersionTest {

  /**
   * Evaluates whether modern snapshot documents are parsed correctly.
   */
  @Test
  public void testModernSnapshot() throws IOException {
    try (InputStream inputStream = this.getClass()
        .getResourceAsStream("/version_modern_snapshot.json")) {
      Version version = Version.read(inputStream);

      Assert.assertEquals("18w09a", version.getId());
      Assert.assertEquals("net.minecraft.client.main.Main", version.getMainClass());
      Assert.assertEquals(21, version.getMinimumLauncherVersion());
      Assert.assertEquals(VersionType.SNAPSHOT, version.getType());
      // TODO: Check dates

      Set<String> arguments = version.getGameArguments().stream()
          .flatMap((a) -> a.getValue().stream())
          .collect(Collectors.toSet());
      Assert.assertEquals(23, arguments.size());
      Assert.assertTrue(arguments.contains("--username"));
      Assert.assertTrue(arguments.contains("${auth_player_name}"));
      Assert.assertTrue(arguments.contains("--version"));
      Assert.assertTrue(arguments.contains("${version_name}"));
      Assert.assertTrue(arguments.contains("--gameDir"));
      Assert.assertTrue(arguments.contains("${game_directory}"));
      Assert.assertTrue(arguments.contains("--assetsDir"));
      Assert.assertTrue(arguments.contains("${assets_root}"));
      Assert.assertTrue(arguments.contains("--assetIndex"));
      Assert.assertTrue(arguments.contains("${assets_index_name}"));
      Assert.assertTrue(arguments.contains("--uuid"));
      Assert.assertTrue(arguments.contains("${auth_uuid}"));
      Assert.assertTrue(arguments.contains("--userType"));
      Assert.assertTrue(arguments.contains("${user_type}"));
      Assert.assertTrue(arguments.contains("--versionType"));
      Assert.assertTrue(arguments.contains("${version_type}"));
      Assert.assertTrue(arguments.contains("--demo"));
      Assert.assertTrue(arguments.contains("--width"));
      Assert.assertTrue(arguments.contains("${resolution_width}"));
      Assert.assertTrue(arguments.contains("--height"));
      Assert.assertTrue(arguments.contains("${resolution_height}"));

      arguments = version.getJvmArguments().stream()
          .flatMap((a) -> a.getValue().stream())
          .collect(Collectors.toSet());
      Assert.assertEquals(9, arguments.size());
      Assert.assertTrue(arguments.contains("-XstartOnFirstThread"));
      Assert.assertTrue(arguments.contains(
          "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump"));
      Assert.assertTrue(arguments.contains("-Dos.name=Windows 10"));
      Assert.assertTrue(arguments.contains("-Dos.version=10.0"));
      Assert.assertTrue(arguments.contains("-Djava.library.path=${natives_directory}"));
      Assert.assertTrue(arguments.contains("-Dminecraft.launcher.brand=${launcher_name}"));
      Assert.assertTrue(arguments.contains("-Dminecraft.launcher.version=${launcher_version}"));
      Assert.assertTrue(arguments.contains("-cp"));
      Assert.assertTrue(arguments.contains("${classpath}"));

      Assert.assertEquals("1.13", version.getAssets());
      Assert.assertEquals("1.13", version.getAssetIndex().getId());
      Assert.assertEquals("9576de2997192ad3af0e40d5e863b04eee5e2c27",
          version.getAssetIndex().getSha1());
      Assert.assertEquals(188852, version.getAssetIndex().getSize());
      Assert.assertEquals(
          "https://launchermeta.mojang.com/mc/assets/1.13/9576de2997192ad3af0e40d5e863b04eee5e2c27/1.13.json",
          version.getAssetIndex().getUrl().toExternalForm());
      Assert.assertEquals(138739294, version.getAssetIndex().getTotalSize());

      Assert.assertEquals(35, version.getLibraries().size());
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.mojang:patchy:1.1".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "oshi-project:oshi-core:1.1".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "net.java.dev.jna:jna:4.4.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "net.java.dev.jna:platform:3.4.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.ibm.icu:icu4j-core-mojang:51.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "net.sf.jopt-simple:jopt-simple:5.0.3".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.paulscode:codecjorbis:20101023".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.paulscode:codecwav:20101023".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.paulscode:libraryjavasound:20101123".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.paulscode:soundsystem:20120107".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "io.netty:netty-all:4.1.9.Final".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.google.guava:guava:21.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.apache.commons:commons-lang3:3.5".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "commons-io:commons-io:2.5".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "commons-codec:commons-codec:1.10".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "net.java.jinput:jinput:2.0.5".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "net.java.jutils:jutils:1.0.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.mojang:brigadier:0.1.24".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.google.code.gson:gson:2.8.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.mojang:authlib:1.5.25".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.apache.commons:commons-compress:1.8.1".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.apache.httpcomponents:httpclient:4.3.3".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "commons-logging:commons-logging:1.1.3".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.apache.httpcomponents:httpcore:4.3.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "it.unimi.dsi:fastutil:7.1.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.apache.logging.log4j:log4j-api:2.8.1".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.apache.logging.log4j:log4j-core:2.8.1".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-jemalloc:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-openal:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-opengl:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-glfw:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-stb:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-jemalloc:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-openal:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-opengl:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-glfw:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "org.lwjgl:lwjgl-stb:3.1.2".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "com.mojang:text2speech:1.10.3".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "ca.weblite:java-objc-bridge:1.0.0".equals(l.getName())));
      Assert.assertTrue(version.getLibraries().stream()
          .anyMatch((l) -> "ca.weblite:java-objc-bridge:1.0.0".equals(l.getName())));

      LibraryReference reference = version.getLibrary("org.lwjgl:lwjgl:3.1.2")
          .orElseThrow(
              () -> new AssertionError("Failed to find library \"org.lwjgl:lwjgl:3.1.2\""));
      Assert.assertEquals(3, reference.getDownloads().getClassifiers().size());
      Assert.assertTrue(reference.getDownloads().getClassifiers().containsKey("natives-linux"));
      Assert.assertTrue(reference.getDownloads().getClassifiers().containsKey("natives-macos"));
      Assert.assertTrue(reference.getDownloads().getClassifiers().containsKey("natives-windows"));
      Assert.assertTrue(reference.getLinuxNativesArtifact().isPresent());
      Assert.assertTrue(reference.getMacNativesArtifact().isPresent());
      Assert.assertTrue(reference.getWindowsNativesArtifact().isPresent());

      reference = version.getLibrary("ca.weblite:java-objc-bridge:1.0.0")
          .orElseThrow(() -> new AssertionError(
              "Failed to find library \"ca.weblite:java-objc-bridge:1.0.0\""));
      Assert.assertEquals(1, reference.getDownloads().getClassifiers().size());
      Assert.assertEquals(1, reference.getRules().size());
      Rule rule = reference.getRules().iterator().next();
      Assert.assertNotNull(rule);
      Assert.assertEquals(Action.ALLOW, rule.getAction());
      Assert.assertEquals(1, rule.getConditions().size());
      RuleCondition condition = rule.getConditions().iterator().next();
      Assert.assertNotNull(condition);
      Assert.assertTrue(condition instanceof OperatingSystemCondition);
      OperatingSystemCondition osCondition = (OperatingSystemCondition) condition;
      Assert.assertTrue(osCondition.getSystem().isPresent());
      Assert.assertEquals(OperatingSystem.MAC_OS, osCondition.getSystem().get());
      Assert.assertFalse(osCondition.getVersion().isPresent());

      LoggerConfiguration loggerConfiguration = version.getClientLoggerConfiguration()
          .orElseThrow(() -> new AssertionError("Expected client logger configuration"));
      Assert.assertEquals("client-1.12.xml", loggerConfiguration.getFile().getId());
      Assert.assertEquals("ef4f57b922df243d0cef096efe808c72db042149",
          loggerConfiguration.getFile().getSha1());
      Assert.assertEquals(877, loggerConfiguration.getFile().getSize());
      Assert.assertEquals(
          "https://launchermeta.mojang.com/mc/log_configs/client-1.12.xml/ef4f57b922df243d0cef096efe808c72db042149/client-1.12.xml",
          loggerConfiguration.getFile().getUrl().toExternalForm());
      Assert.assertEquals("-Dlog4j.configurationFile=${path}",
          loggerConfiguration.getArgument().orElse(null));
      Assert.assertEquals("log4j2-xml", loggerConfiguration.getType());
    }
  }

  /**
   * Evaluates whether modern release documents are parsed correctly.
   */
  @Test
  public void testModernRelease() throws IOException {
    try (InputStream inputStream = this.getClass()
        .getResourceAsStream("/version_modern_release.json")) {
      Version version = Version.read(inputStream);

      Assert.assertEquals("1.12.2", version.getId());
      Assert.assertEquals("1.12", version.getAssets());
      Assert.assertEquals(VersionType.RELEASE, version.getType());

      AssetIndexReference assetIndex = version.getAssetIndex();
      Assert.assertEquals("1.12", assetIndex.getId());
      Assert.assertEquals("98c430c16a705f18a58a281b27caabf3ef09d40d", assetIndex.getSha1());
      Assert.assertEquals(169253, assetIndex.getSize());
      Assert.assertEquals(
          "https://launchermeta.mojang.com/mc/assets/1.12/98c430c16a705f18a58a281b27caabf3ef09d40d/1.12.json",
          assetIndex.getUrl().toExternalForm());
      Assert.assertEquals(127453671, assetIndex.getTotalSize());

      Download download = version.getClientDownload();
      Assert.assertEquals("0f275bc1547d01fa5f56ba34bdc87d981ee12daf", download.getSha1());
      Assert.assertEquals(10180113, download.getSize());
      Assert.assertEquals(
          "https://launcher.mojang.com/mc/game/1.12.2/client/0f275bc1547d01fa5f56ba34bdc87d981ee12daf/client.jar",
          download.getUrl().toExternalForm());

      download = version.getServerDownload()
          .orElseThrow(() -> new AssertionError("Expected a server download"));
      Assert.assertEquals("886945bfb2b978778c3a0288fd7fab09d315b25f", download.getSha1());
      Assert.assertEquals(30222121, download.getSize());
      Assert.assertEquals(
          "https://launcher.mojang.com/mc/game/1.12.2/server/886945bfb2b978778c3a0288fd7fab09d315b25f/server.jar",
          download.getUrl().toExternalForm());

      Assert.assertEquals(37, version.getLibraries().size());
      LibraryReference reference = version
          .getLibrary("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209")
          .orElseThrow(() -> new AssertionError(
              "Cannot find library \"org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209\""));
      Assert.assertEquals("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209", reference.getName());
      Assert.assertEquals(2, reference.getRules().size());

      Assert.assertTrue(version.getJvmArguments().isEmpty());
      Assert.assertEquals(18, version.getGameArguments().size());
    }
  }

  /**
   * Evaluates whether old alpha release documents are parsed correctly.
   */
  @Test
  public void testOldAlpha() throws IOException {
    try (InputStream inputStream = this.getClass().getResourceAsStream("/version_old_alpha.json")) {
      Version version = Version.read(inputStream);

      Assert.assertEquals("rd-20090515", version.getId());
      Assert.assertEquals("legacy", version.getAssets());
      Assert.assertEquals(VersionType.OLD_ALPHA, version.getType());

      AssetIndexReference assetIndex = version.getAssetIndex();
      Assert.assertEquals("legacy", assetIndex.getId());
      Assert.assertEquals("c0fd82e8ce9fbc93119e40d96d5a4e62cfa3f729", assetIndex.getSha1());
      Assert.assertEquals(134284, assetIndex.getSize());
      Assert.assertEquals(
          "https://launchermeta.mojang.com/mc/assets/legacy/c0fd82e8ce9fbc93119e40d96d5a4e62cfa3f729/legacy.json",
          assetIndex.getUrl().toExternalForm());
      Assert.assertEquals(111220701, assetIndex.getTotalSize());

      Assert.assertNotNull(version.getClientDownload());
      Assert.assertFalse(version.getServerDownload().isPresent());
      Assert.assertFalse(version.getWindowsServerDownload().isPresent());

      Assert.assertEquals(10, version.getLibraries().size());
      LibraryReference library = version
          .getLibrary("org.lwjgl.lwjgl:lwjgl_util:2.9.1-nightly-20130708-debug3")
          .orElseThrow(() -> new AssertionError(
              "Cannot find library \"org.lwjgl.lwjgl:lwjgl_util:2.9.1-nightly-20130708-debug3\""));
      Assert.assertEquals("org.lwjgl.lwjgl:lwjgl_util:2.9.1-nightly-20130708-debug3",
          library.getName());
      Assert.assertEquals(1, library.getRules().size());
      Rule rule = library.getRules().iterator().next();
      Assert.assertEquals(Action.ALLOW, rule.getAction());
      Assert.assertEquals(1, rule.getConditions().size());
      RuleCondition condition = rule.getConditions().iterator().next();
      Assert.assertTrue(condition instanceof OperatingSystemCondition);
      OperatingSystemCondition osCondition = (OperatingSystemCondition) condition;
      Assert.assertTrue(osCondition.getSystem().isPresent());
      Assert.assertEquals(OperatingSystem.MAC_OS, osCondition.getSystem().get());
      Assert.assertTrue(osCondition.getVersion().isPresent());
      Assert.assertEquals("^10\\.5\\.\\d$", osCondition.getVersion().get().pattern());

      Assert.assertEquals(2, version.getGameArguments().size());
      Assert.assertTrue(version.getJvmArguments().isEmpty());
    }
  }

  /**
   * Evaluates whether elements which do not match the resolver context are correctly removed.
   */
  @Test
  public void testReduce() throws IOException {
    try (InputStream inputStream = this.getClass()
        .getResourceAsStream("/version_modern_snapshot.json")) {
      ResolverContext ctx = ResolverContext.builder()
          .withOperatingSystem(OperatingSystem.WINDOWS)
          .withOperatingSystemVersion("10.0")
          .build();

      Version version = Version.read(inputStream)
          .reduce(ctx);

      List<String> arguments = version.getGameArguments().stream()
          .flatMap((a) -> a.getValue().stream())
          .collect(Collectors.toList());
      Assert.assertEquals(18, arguments.size());
      Assert.assertFalse(arguments.contains("--demo"));
      Assert.assertFalse(arguments.contains("--width"));

      Assert.assertEquals(34, version.getLibraries().size());
      version.getLibrary("ca.weblite:java-objc-bridge:1.0.0")
          .ifPresent((l) -> {
            throw new AssertionError(
                "Did not expect \"ca.weblite:java-objc-bridge:1.0.0\" on Windows");
          });
    }
  }
}

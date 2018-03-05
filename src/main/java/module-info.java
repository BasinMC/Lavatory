/**
 * Provides a client for the launcher metadata system.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
module org.basinmc.lavatory {
  exports org.basinmc.lavatory;
  exports org.basinmc.lavatory.asset;
  exports org.basinmc.lavatory.file;
  exports org.basinmc.lavatory.rule;
  exports org.basinmc.lavatory.rule.feature;
  exports org.basinmc.lavatory.rule.system;
  exports org.basinmc.lavatory.version;

  requires static com.github.spotbugs.annotations;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
}

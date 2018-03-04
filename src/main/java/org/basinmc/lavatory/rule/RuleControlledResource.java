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
package org.basinmc.lavatory.rule;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Set;
import org.basinmc.lavatory.ResolverContext;

/**
 * Provides a base to resources which may be added/removed based on whether their internal set of
 * rules is matched or not.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface RuleControlledResource {

  /**
   * Evaluates whether the resource is to be included in the final result.
   *
   * @param ctx a context.
   * @return true if inclusion is permitted, false otherwise.
   */
  boolean evaluate(@NonNull ResolverContext ctx);

  /**
   * Retrieves the internal set of rules which apply to this particular resource.
   *
   * @return a set of rules.
   */
  @NonNull
  Set<Rule> getRules();
}

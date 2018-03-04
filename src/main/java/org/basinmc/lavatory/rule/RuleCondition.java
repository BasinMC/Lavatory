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
import org.basinmc.lavatory.ResolverContext;

/**
 * Represents a condition which is placed on a value and decides whether or not to include the value
 * within the actual manifest within a certain execution context.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@FunctionalInterface
public interface RuleCondition {

  /**
   * Evaluates whether a particular rule condition is met within the specified context.
   *
   * @param ctx a context.
   * @return true if met, false otherwise.
   */
  boolean evaluate(@NonNull ResolverContext ctx);
}

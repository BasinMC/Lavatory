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
 * Provides a base to containers which wrap multiple rule controlled resources.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@FunctionalInterface
public interface RuleControlledResourceContainer {

  /**
   * <p>Reduces the values within this container to the set of elements which match the specified
   * context.</p>
   *
   * <p>This method returns a completely new instance of this container type with any elements which
   * do not match the specified context removed.</p>
   *
   * @param ctx a resolver context.
   * @return a new container instance which contains only matching elements.
   */
  @NonNull
  RuleControlledResourceContainer reduce(@NonNull ResolverContext ctx);
}

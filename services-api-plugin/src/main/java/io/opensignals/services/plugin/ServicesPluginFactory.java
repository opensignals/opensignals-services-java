/*
 * Copyright © 2020 OpenSignals Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package io.opensignals.services.plugin;

import io.opensignals.services.Services.Environment;

import java.util.Optional;

/**
 * The plugin provider factory interface for the services runtime.
 *
 * @author wlouth
 * @since 1.0
 */

public interface ServicesPluginFactory {

  /**
   * A method called once per plugin factory configuration.
   *
   * @param environment the configuration used in creating the plugin
   * @return A {@link ServicesPlugin} that will be called for each context creation, or an empty Optional if the environment is not properly configured
   */

  Optional< ServicesPlugin > create ( Environment environment );

}

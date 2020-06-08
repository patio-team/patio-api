/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.infrastructure.vfs;

import com.bertramlabs.plugins.karman.StorageProvider;
import com.bertramlabs.plugins.karman.local.LocalStorageProvider;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;


/** Handle the virtual file system resolution */
@Singleton
public class GeneralProvider {

  private final transient List<Provider> providers;
  private final String defaultProvider;

  public enum ProviderTypes {
    aws, local
  }

  public StorageProvider byType(ProviderTypes providerType) {
    var provider = this.providers.stream().filter( p ->
                                           p.getName().equals(providerType.name())).findFirst().get();
    // TODO: End this
//    if (provider.getName().equals(ProviderTypes.local.name())) {
      return new LocalStorageProvider(Map.of("basepath", provider.getBase()));
//    }
  }

  public StorageProvider byDefault() {
    var provider = this.providers.stream().filter( p ->
                                                     p.getName().equals(this.defaultProvider)).findFirst().get();
    return new LocalStorageProvider(Map.of("basepath", provider.getBase()));
  }

  public GeneralProvider(
    List<Provider> providers,
    @Value("${storage.default}") String defaultProvider) {
    this.providers = providers;
    this.defaultProvider = defaultProvider;
  }
}

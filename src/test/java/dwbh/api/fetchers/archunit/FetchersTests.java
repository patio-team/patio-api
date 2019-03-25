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
package dwbh.api.fetchers.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

/**
 * Checks fetcher constraints
 *
 * @since 0.1.0
 */
@AnalyzeClasses(packages = "dwbh.api.fetchers")
public class FetchersTests {

  private static final String NAME_FETCHER_SUFFIX_ = "Fetcher";
  private static final String NAME_FETCHER_UTIL_SUFFIX = "FetcherUtils";

  @ArchTest
  void checkClassNamingConvention(JavaClasses classes) {
    classes()
        .should()
        .haveSimpleNameEndingWith(NAME_FETCHER_SUFFIX_)
        .orShould()
        .haveSimpleNameEndingWith(NAME_FETCHER_UTIL_SUFFIX)
        .check(classes);
  }

  @ArchTest
  void checkRelationshipBetweenFetchersAndUtils() {
    classes()
        .that()
        .haveSimpleNameEndingWith(NAME_FETCHER_UTIL_SUFFIX)
        .should()
        .onlyBeAccessed()
        .byClassesThat()
        .haveSimpleNameEndingWith(NAME_FETCHER_SUFFIX_);
  }
}

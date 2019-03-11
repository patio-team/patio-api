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
package dwbh.api.fixtures;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes data fixtures
 *
 * @since 0.1.0
 */
public class Fixtures {

  private static final Logger LOG = LoggerFactory.getLogger(Fixtures.class);
  private final DSLContext context;

  /**
   * Initializes the fixtures
   *
   * @param context Jooq context to execute database queries
   * @since 0.1.0
   * @see DSLContext
   */
  public Fixtures(DSLContext context) {
    this.context = context;
  }

  /**
   * Returns the current {@link DSLContext} instance
   *
   * @return an instance of {@link DSLContext}
   */
  public DSLContext getContext() {
    return this.context;
  }

  /**
   * Executes loading data queries
   *
   * @param locator base class to find the sql files
   * @param sqlFilename name of the fixtures file
   * @since 0.1.0
   */
  public void load(Class locator, String sqlFilename) {
    try {
      var sqlArray = getStatements(locator, sqlFilename);

      context.batch(sqlArray).execute();
    } catch (URISyntaxException | IOException exception) {
      LOG.error(exception.getMessage(), exception);
    }
  }

  private String[] getStatements(Class base, String filename)
      throws URISyntaxException, IOException {
    URL fileURL = base.getResource(filename);
    URI fileURI = fileURL.toURI();
    Path filePath = Paths.get(fileURI);

    return Files.lines(filePath).toArray(String[]::new);
  }
}

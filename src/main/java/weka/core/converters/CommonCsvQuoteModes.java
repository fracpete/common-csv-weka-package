/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * CommonCsvQuoteModes.java
 * Copyright (C) 2020 FracPete
 */

package weka.core.converters;

import org.apache.commons.csv.QuoteMode;
import weka.core.Tag;

/**
 * The available quote modes.
 *
 * @author FracPete (fracpete at gmail dot com)
 */
public class CommonCsvQuoteModes {
  public static final int ALL = 0;
  public static final int ALL_NON_NULL = 1;
  public static final int MINIMAL = 2;
  public static final int NON_NUMERIC = 3;
  public static final int NONE = 4;

  public static final Tag[] TAGS_QUOTEMODES = {
    new Tag(ALL, "ALL", "All"),
    new Tag(ALL_NON_NULL, "ALL_NON_NULL", "All non-null"),
    new Tag(MINIMAL, "MINIMAL", "Minimal"),
    new Tag(NON_NUMERIC, "NON_NUMERIC", "Non-numeric"),
    new Tag(NONE, "NONE", "None")
  };

  /**
   * Returns the corresponding QuoteMode for the given tag ID.
   *
   * @param id		the ID to get the mode for
   * @return		the mode
   */
  public static QuoteMode getQuoteMode(int id) {
    switch (id) {
      case ALL:
        return QuoteMode.ALL;
      case ALL_NON_NULL:
        return QuoteMode.ALL_NON_NULL;
      case MINIMAL:
        return QuoteMode.MINIMAL;
      case NON_NUMERIC:
        return QuoteMode.NON_NUMERIC;
      case NONE:
        return QuoteMode.NONE;
      default:
        throw new IllegalStateException("Unhandled quote mode: " + id);
    }
  }
}

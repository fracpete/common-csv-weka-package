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
 * CommonCsvFormats.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.core.converters;

import org.apache.commons.csv.CSVFormat;
import weka.core.Tag;

/**
 * The available formats.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CommonCsvFormats {
  
  //public static final int CUSTOM = 0;
  public static final int DEFAULT = 1;
  public static final int EXCEL = 2;
  public static final int INFORMIX_UNLOAD = 3;
  public static final int INFORMIX_UNLOAD_CSV = 4;
  public static final int MYSQL = 5;
  public static final int ORACLE = 6;
  public static final int POSTGRESSQL_CSV = 7;
  public static final int POSTGRESSQL_TEXT = 8;
  public static final int RFC_4180 = 9;
  public static final int TDF = 10;

  public static final Tag[] TAGS_FORMATS = {
    //new Tag(CUSTOM, "CUSTOM", "Custom"),
    new Tag(DEFAULT, "DEFAULT", "Default"),
    new Tag(EXCEL, "EXCEL", "Excel"),
    new Tag(INFORMIX_UNLOAD, "INFORMIX", "Informix (unload)"),
    new Tag(INFORMIX_UNLOAD_CSV, "INFORMIXCSV", "Informix (unload CSV)"),
    new Tag(MYSQL, "MYSQL", "MySQL"),
    new Tag(ORACLE, "ORACLE", "Oracle"),
    new Tag(POSTGRESSQL_CSV, "POSTGRESQLCSV", "PostgreSQL (CSV)"),
    new Tag(POSTGRESSQL_TEXT, "POSTGRESQLTXT", "PostgreSQL (Text)"),
    new Tag(RFC_4180, "RFC4180", "RFC-4180"),
    new Tag(TDF, "TDF", "TDF (tab-delimited)"),
  };

  /**
   * Returns the corresponding CSVFormat instance for the given tag ID.
   *
   * @param id		the ID to get the format for
   * @return		the format
   */
  public static CSVFormat getFormat(int id) {
    switch (id) {
      case DEFAULT:
        return CSVFormat.DEFAULT;
      case EXCEL:
        return CSVFormat.EXCEL;
      case INFORMIX_UNLOAD:
        return CSVFormat.INFORMIX_UNLOAD;
      case INFORMIX_UNLOAD_CSV:
        return CSVFormat.INFORMIX_UNLOAD_CSV;
      case MYSQL:
        return CSVFormat.MYSQL;
      case ORACLE:
        return CSVFormat.ORACLE;
      case POSTGRESSQL_CSV:
        return CSVFormat.POSTGRESQL_CSV;
      case POSTGRESSQL_TEXT:
        return CSVFormat.POSTGRESQL_TEXT;
      case RFC_4180:
        return CSVFormat.RFC4180;
      case TDF:
        return CSVFormat.TDF;
      default:
        throw new IllegalStateException("Unhandled format: " + id);
    }
  }
}

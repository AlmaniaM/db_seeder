package ch.konnexions.db_seeder.generated;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Test Data Generator for a Database - Abstract Generated Seeder.
 * <br>
 * @author  GenerateSchema.class
 * @version 2.0.0
 * @since   2020-07-27
 */
abstract class AbstractGenSeeder extends AbstractGenSchema {

  private static final Logger logger = Logger.getLogger(AbstractGenSeeder.class);

  /**
   * Initialises a new abstract generated seeder object.
   *
   * @param dbmsTickerSymbol DBMS ticker symbol 
   */
  public AbstractGenSeeder(String dbmsTickerSymbol) {
    super(dbmsTickerSymbol);

    if (isDebug) {
      logger.debug("Start Constructor - dbmsTickerSymbol=" + dbmsTickerSymbol);
    }

    if (isDebug) {
      logger.debug("End   Constructor");
    }
  }

  /**
   * Initialises a new abstract generated seeder object.
   *
   * @param dbmsTickerSymbol DBMS ticker symbol 
   * @param isClient client database version
   */
  public AbstractGenSeeder(String dbmsTickerSymbol, boolean isClient) {
    super(dbmsTickerSymbol, isClient);

    if (isDebug) {
      logger.debug("Start Constructor - dbmsTickerSymbol=" + dbmsTickerSymbol + " - isClient=" + isClient);
    }

    if (isDebug) {
      logger.debug("End   Constructor");
    }
  }

  protected final void insertTable(PreparedStatement preparedStatement, final String tableName, final int rowNo, final ArrayList<Object> pkList) {
    if (isDebug) {
      logger.debug("Start");
    }

    switch (tableName) {
    case TABLE_NAME_CITY -> prepDmlStmntInsertCity(preparedStatement,
                                                   rowNo);
    case TABLE_NAME_COMPANY -> prepDmlStmntInsertCompany(preparedStatement,
                                                         rowNo);
    case TABLE_NAME_COUNTRY -> prepDmlStmntInsertCountry(preparedStatement,
                                                         rowNo);
    case TABLE_NAME_COUNTRY_STATE -> prepDmlStmntInsertCountryState(preparedStatement,
                                                                    rowNo);
    case TABLE_NAME_TIMEZONE -> prepDmlStmntInsertTimezone(preparedStatement,
                                                           rowNo);
    default -> throw new RuntimeException("Not yet implemented - database table : " + String.format(FORMAT_TABLE_NAME,
                                                                                                    tableName));
    }

    if (isDebug) {
      logger.debug("End");
    }
  }

  private void prepDmlStmntInsertCity(PreparedStatement preparedStatement, int rowNo) {
    int i = 0;

    prepStmntColBigint(preparedStatement,
                       "CITY",
                       "PK_CITY_ID",
                       ++i,
                       rowNo);
    prepStmntColFkOpt(preparedStatement,
                      "CITY",
                      "FK_COUNTRY_STATE_ID",
                      ++i,
                      rowNo,
                      pkLists.get(TABLE_NAME_COUNTRY_STATE));
    prepStmntColBlobOpt(preparedStatement,
                        "CITY",
                        "CITY_MAP",
                        ++i,
                        rowNo);
    prepStmntColTimestamp(preparedStatement,
                          "CITY",
                          "CREATED",
                          ++i,
                          rowNo);
    prepStmntColTimestampOpt(preparedStatement,
                             "CITY",
                             "MODIFIED",
                             ++i,
                             rowNo);
    prepStmntColVarchar(preparedStatement,
                        "CITY",
                        "NAME",
                        ++i,
                        rowNo,
                        100,
                        null,
                        null,
                        null);
  }

  private void prepDmlStmntInsertCompany(PreparedStatement preparedStatement, int rowNo) {
    int i = 0;

    prepStmntColBigint(preparedStatement,
                       "COMPANY",
                       "PK_COMPANY_ID",
                       ++i,
                       rowNo);
    prepStmntColFk(preparedStatement,
                   "COMPANY",
                   "FK_CITY_ID",
                   ++i,
                   rowNo,
                   pkLists.get(TABLE_NAME_CITY));
    prepStmntColVarchar(preparedStatement,
                        "COMPANY",
                        "ACTIVE",
                        ++i,
                        rowNo,
                        1,
                        null,
                        null,
                        Arrays.asList("N",
                                      "Y"));
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "ADDRESS1",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "ADDRESS2",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "ADDRESS3",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColTimestamp(preparedStatement,
                          "COMPANY",
                          "CREATED",
                          ++i,
                          rowNo);
    prepStmntColClobOpt(preparedStatement,
                        "COMPANY",
                        "DIRECTIONS",
                        ++i,
                        rowNo);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "EMAIL",
                           ++i,
                           rowNo,
                           100,
                           null,
                           null,
                           null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "FAX",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColTimestampOpt(preparedStatement,
                             "COMPANY",
                             "MODIFIED",
                             ++i,
                             rowNo);
    prepStmntColVarchar(preparedStatement,
                        "COMPANY",
                        "NAME",
                        ++i,
                        rowNo,
                        100,
                        null,
                        null,
                        null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "PHONE",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "POSTAL_CODE",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "URL",
                           ++i,
                           rowNo,
                           250,
                           null,
                           null,
                           null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COMPANY",
                           "VAT_ID_NUMBER",
                           ++i,
                           rowNo,
                           100,
                           null,
                           null,
                           null);
  }

  private void prepDmlStmntInsertCountry(PreparedStatement preparedStatement, int rowNo) {
    int i = 0;

    prepStmntColBigint(preparedStatement,
                       "COUNTRY",
                       "PK_COUNTRY_ID",
                       ++i,
                       rowNo);
    prepStmntColBlobOpt(preparedStatement,
                        "COUNTRY",
                        "COUNTRY_MAP",
                        ++i,
                        rowNo);
    prepStmntColTimestamp(preparedStatement,
                          "COUNTRY",
                          "CREATED",
                          ++i,
                          rowNo);
    prepStmntColVarcharOpt(preparedStatement,
                           "COUNTRY",
                           "ISO3166",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
    prepStmntColTimestampOpt(preparedStatement,
                             "COUNTRY",
                             "MODIFIED",
                             ++i,
                             rowNo);
    prepStmntColVarchar(preparedStatement,
                        "COUNTRY",
                        "NAME",
                        ++i,
                        rowNo,
                        100,
                        null,
                        null,
                        null);
  }

  private void prepDmlStmntInsertCountryState(PreparedStatement preparedStatement, int rowNo) {
    int i = 0;

    prepStmntColBigint(preparedStatement,
                       "COUNTRY_STATE",
                       "PK_COUNTRY_STATE_ID",
                       ++i,
                       rowNo);
    prepStmntColFk(preparedStatement,
                   "COUNTRY_STATE",
                   "FK_COUNTRY_ID",
                   ++i,
                   rowNo,
                   pkLists.get(TABLE_NAME_COUNTRY));
    prepStmntColFk(preparedStatement,
                   "COUNTRY_STATE",
                   "FK_TIMEZONE_ID",
                   ++i,
                   rowNo,
                   pkLists.get(TABLE_NAME_TIMEZONE));
    prepStmntColBlobOpt(preparedStatement,
                        "COUNTRY_STATE",
                        "COUNTRY_STATE_MAP",
                        ++i,
                        rowNo);
    prepStmntColTimestamp(preparedStatement,
                          "COUNTRY_STATE",
                          "CREATED",
                          ++i,
                          rowNo);
    prepStmntColTimestampOpt(preparedStatement,
                             "COUNTRY_STATE",
                             "MODIFIED",
                             ++i,
                             rowNo);
    prepStmntColVarchar(preparedStatement,
                        "COUNTRY_STATE",
                        "NAME",
                        ++i,
                        rowNo,
                        100,
                        null,
                        null,
                        null);
    prepStmntColVarcharOpt(preparedStatement,
                           "COUNTRY_STATE",
                           "SYMBOL",
                           ++i,
                           rowNo,
                           50,
                           null,
                           null,
                           null);
  }

  private void prepDmlStmntInsertTimezone(PreparedStatement preparedStatement, int rowNo) {
    int i = 0;

    prepStmntColBigint(preparedStatement,
                       "TIMEZONE",
                       "PK_TIMEZONE_ID",
                       ++i,
                       rowNo);
    prepStmntColVarchar(preparedStatement,
                        "TIMEZONE",
                        "ABBREVIATION",
                        ++i,
                        rowNo,
                        50,
                        null,
                        null,
                        null);
    prepStmntColTimestamp(preparedStatement,
                          "TIMEZONE",
                          "CREATED",
                          ++i,
                          rowNo);
    prepStmntColTimestampOpt(preparedStatement,
                             "TIMEZONE",
                             "MODIFIED",
                             ++i,
                             rowNo);
    prepStmntColVarchar(preparedStatement,
                        "TIMEZONE",
                        "NAME",
                        ++i,
                        rowNo,
                        100,
                        null,
                        null,
                        null);
    prepStmntColVarcharOpt(preparedStatement,
                           "TIMEZONE",
                           "V_TIME_ZONE",
                           ++i,
                           rowNo,
                           4000,
                           null,
                           null,
                           null);
  }

  /**
   * Sets the designated column to a BIGINT value.
   *
   * @param preparedStatement the prepared statement
   * @param tableName         the table name
   * @param columnName        the column name
   * @param columnPos         the column position
   * @param rowNo             the current row number
   */
  @Override
  protected void prepStmntColBigint(PreparedStatement preparedStatement, String tableName, String columnName, int columnPos, int rowNo) {
    super.prepStmntColBigint(preparedStatement,
                             tableName,
                             columnName,
                             columnPos,
                             rowNo);
  }

  /**
   * Sets the designated column to a BLOB value.
   *
   * @param preparedStatement the prepared statement
   * @param tableName         the table name
   * @param columnName        the column name
   * @param columnPos         the column position
   * @param rowNo             the current row number
   */
  @Override
  protected void prepStmntColBlob(PreparedStatement preparedStatement, String tableName, String columnName, int columnPos, int rowNo) {
    super.prepStmntColBlob(preparedStatement,
                           tableName,
                           columnName,
                           columnPos,
                           rowNo);
  }

  /**
   * Sets the designated column to a CLOB value.
   *
   * @param preparedStatement the prepared statement
   * @param tableName         the table name
   * @param columnName        the column name
   * @param columnPos         the column position
   * @param rowNo             the current row number
   */
  @Override
  protected void prepStmntColClob(PreparedStatement preparedStatement, String tableName, String columnName, int columnPos, int rowNo) {
    super.prepStmntColClob(preparedStatement,
                           tableName,
                           columnName,
                           columnPos,
                           rowNo);
  }

  /**
   * Sets the designated column to an existing foreign key value.
   *
   * @param preparedStatement the prepared statement
   * @param tableName         the table name
   * @param columnName        the column name
   * @param columnPos         the column position
   * @param rowNo             the current row number
   * @param fkList            the existing foreign keys
   */
  @Override
  protected void prepStmntColFk(PreparedStatement preparedStatement, String tableName, String columnName, int columnPos, int rowNo, ArrayList<Object> fkList) {
    super.prepStmntColFk(preparedStatement,
                         tableName,
                         columnName,
                         columnPos,
                         rowNo,
                         fkList);
  }

  /**
   * Sets the designated column to a TIMESTAMP value.
   *
   * @param preparedStatement the prepared statement
   * @param tableName         the table name
   * @param columnName        the column name
   * @param columnPos         the column position
   * @param rowNo             the current row number
   */
  @Override
  protected void prepStmntColTimestamp(PreparedStatement preparedStatement, String tableName, String columnName, int columnPos, int rowNo) {
    super.prepStmntColTimestamp(preparedStatement,
                                tableName,
                                columnName,
                                columnPos,
                                rowNo);
  }

  /**
   * Sets the designated column to a VARCHAR value.
   *
   * @param preparedStatement the prepared statement
   * @param tableName         the table name
   * @param columnName        the column name
   * @param columnPos         the column position
   * @param rowNo             the current row number
   * @param size              the column size
   * @param lowerRange        the lower range
   * @param upperRange        the upper range
   * @param validValues       the valid values
   */
  @Override
  protected void prepStmntColVarchar(PreparedStatement preparedStatement,
                                     String tableName,
                                     String columnName,
                                     int columnPos,
                                     int rowNo,
                                     int size,
                                     String lowerRange,
                                     String upperRange,
                                     List<String> validValues) {
    super.prepStmntColVarchar(preparedStatement,
                              tableName,
                              columnName,
                              columnPos,
                              rowNo,
                              size,
                              lowerRange,
                              upperRange,
                              validValues);
  }
}

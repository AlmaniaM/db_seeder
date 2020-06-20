/**
 *
 */
package ch.konnexions.db_seeder.jdbc.cratedb;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.DatabaseSeeder;
import ch.konnexions.db_seeder.jdbc.AbstractJdbcSeeder;

/**
 * <h1> Test Data Generator for a CrateDB DBMS. </h1>
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public class CratedbSeeder extends AbstractJdbcSeeder {

  private static Logger logger = Logger.getLogger(CratedbSeeder.class);

  /**
   *
   */
  public CratedbSeeder() {
    super();

    dbms           = Dbms.CRATEDB;

    urlBase        = config.getCratedbConnectionPrefix() + config.getJdbcConnectionHost() + ":" + config.getCratedbConnectionPort() + "/?strict=true";

    url            = urlBase + "&user=" + config.getCratedbUser() + "&password=" + config.getCratedbPassword();
    urlSetup       = urlBase + "&user=crate";

    dropTableStmnt = "SELECT UPPER(table_name), 'DROP TABLE \"' || LOWER(table_name) || '\"' FROM information_schema.tables WHERE table_name = LOWER(?) AND table_schema = 'doc'";
  }

  @SuppressWarnings("preview")
  @Override
  protected final String createDdlStmnt(final String tableName) {
    switch (tableName) {
    case TABLE_NAME_CITY:
      return """
             CREATE TABLE CITY (
                 PK_CITY_ID          BIGINT    NOT NULL PRIMARY KEY,
                 FK_COUNTRY_STATE_ID BIGINT,
                 CITY_MAP            OBJECT,
                 CREATED             TIMESTAMP NOT NULL,
                 MODIFIED            TIMESTAMP,
                 NAME                TEXT      NOT NULL
              )""";
    case TABLE_NAME_COMPANY:
      return """
             CREATE TABLE COMPANY (
                 PK_COMPANY_ID BIGINT    NOT NULL PRIMARY KEY,
                 FK_CITY_ID    BIGINT    NOT NULL,
                 ACTIVE        TEXT      NOT NULL,
                 ADDRESS1      TEXT,
                 ADDRESS2      TEXT,
                 ADDRESS3      TEXT,
                 CREATED       TIMESTAMP NOT NULL,
                 DIRECTIONS    TEXT,
                 EMAIL         TEXT,
                 FAX           TEXT,
                 MODIFIED      TIMESTAMP,
                 NAME          TEXT      NOT NULL,
                 PHONE         TEXT,
                 POSTAL_CODE   TEXT,
                 URL           TEXT,
                 VAT_ID_NUMBER TEXT
             )""";
    case TABLE_NAME_COUNTRY:
      return """
             CREATE TABLE COUNTRY (
                PK_COUNTRY_ID BIGINT    NOT NULL PRIMARY KEY,
                COUNTRY_MAP   OBJECT,
                CREATED       TIMESTAMP NOT NULL,
                ISO3166       TEXT,
                MODIFIED      TIMESTAMP,
                NAME          TEXT      NOT NULL
             )""";
    case TABLE_NAME_COUNTRY_STATE:
      return """
             CREATE TABLE COUNTRY_STATE (
                PK_COUNTRY_STATE_ID BIGINT    NOT NULL PRIMARY KEY,
                FK_COUNTRY_ID       BIGINT    NOT NULL,
                FK_TIMEZONE_ID      BIGINT    NOT NULL,
                COUNTRY_STATE_MAP   OBJECT,
                CREATED             TIMESTAMP NOT NULL,
                MODIFIED            TIMESTAMP,
                NAME                TEXT      NOT NULL,
                SYMBOL              TEXT
             )""";
    case TABLE_NAME_TIMEZONE:
      return """
             CREATE TABLE TIMEZONE (
                PK_TIMEZONE_ID BIGINT     NOT NULL PRIMARY KEY,
                ABBREVIATION   TEXT       NOT NULL,
                CREATED        TIMESTAMP  NOT NULL,
                MODIFIED       TIMESTAMP,
                NAME           TEXT       NOT NULL,
                V_TIME_ZONE    TEXT
             )""";
    default:
      throw new RuntimeException("Not yet implemented - database table : " + String.format(DatabaseSeeder.FORMAT_TABLE_NAME, tableName));
    }
  }

  @Override
  protected void resetAndCreateDatabase() {
    String methodName = null;

    methodName = new Object() {
    }.getClass().getEnclosingMethod().getName();
    logger.debug(String.format(DatabaseSeeder.FORMAT_METHOD_NAME, methodName)
        + " - Start - database table \" + String.format(DatabaseSeeder.FORMAT_TABLE_NAME, tableName) + \" - \"\n"
        + "        + String.format(DatabaseSeeder.FORMAT_ROW_NO, rowCount) + \" rows to be created");

    // -----------------------------------------------------------------------
    // Connect.
    // -----------------------------------------------------------------------

    connection = connect(urlSetup);

    // -----------------------------------------------------------------------
    // Drop the database user and tables if already existing.
    // -----------------------------------------------------------------------
    // java.sql.ParameterMetaData as returned by e.g. java.sql.PreparedStatement
    // DataSource is not implemented
    // -----------------------------------------------------------------------

    String cratedbUser = config.getCratedbUser();

    try {
      statement = connection.createStatement();
      statement.execute("DROP USER IF EXISTS " + cratedbUser);

      for (String tableName : TABLE_NAMES) {
        statement.execute("DROP TABLE IF EXISTS " + tableName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Create the database user and grant the necessary rights.
    // -----------------------------------------------------------------------

    try {
      statement.execute("CREATE USER " + cratedbUser + " WITH (PASSWORD = '" + config.getCratedbPassword() + "')");

      statement.execute("GRANT ALL PRIVILEGES TO " + cratedbUser);

      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Disconnect and reconnect.
    // -----------------------------------------------------------------------

    disconnect(connection);

    connection = connect(url);

    logger.debug(String.format(DatabaseSeeder.FORMAT_METHOD_NAME, methodName) + " - End");
  }
}

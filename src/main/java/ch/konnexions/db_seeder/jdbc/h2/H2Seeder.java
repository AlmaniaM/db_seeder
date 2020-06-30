/**
 *
 */
package ch.konnexions.db_seeder.jdbc.h2;

import java.sql.SQLException;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.jdbc.AbstractJdbcSeeder;

/**
 * Test Data Generator for a H2 Database Engine DBMS.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public class H2Seeder extends AbstractJdbcSeeder {

  private static Logger logger = Logger.getLogger(AbstractJdbcSeeder.class);

  /**
   * Instantiates a new H2 Database Engine seeder.
   */
  public H2Seeder() {
    super();

    String methodName = new Object() {
    }.getClass().getName();

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- Start Constructor");

    init();

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- End   Constructor");
  }

  /**
   * Instantiates a new H2 Database Engine seeder.
   *
   * @param isClient client database version
   */
  public H2Seeder(boolean isClient) {
    super(isClient);

    String methodName = new Object() {
    }.getClass().getName();

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- Start Constructor");

    init();

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- End   Constructor");
  }

  @SuppressWarnings("preview")
  @Override
  protected final String createDdlStmnt(final String tableName) {
    switch (tableName) {
    case TABLE_NAME_CITY:
      return """
             CREATE TABLE "CITY" (
                 PK_CITY_ID          BIGINT         NOT NULL PRIMARY KEY,
                 FK_COUNTRY_STATE_ID BIGINT,
                 CITY_MAP            BLOB,
                 CREATED             TIMESTAMP      NOT NULL,
                 MODIFIED            TIMESTAMP,
                 NAME                VARCHAR(100)   NOT NULL,
                 CONSTRAINT FK_CITY_COUNTRY_STATE FOREIGN KEY (FK_COUNTRY_STATE_ID) REFERENCES "COUNTRY_STATE" (PK_COUNTRY_STATE_ID)
              )""";
    case TABLE_NAME_COMPANY:
      return """
             CREATE TABLE "COMPANY" (
                 PK_COMPANY_ID BIGINT       NOT NULL PRIMARY KEY,
                 FK_CITY_ID    BIGINT       NOT NULL,
                 ACTIVE        VARCHAR(1)   NOT NULL,
                 ADDRESS1      VARCHAR(50),
                 ADDRESS2      VARCHAR(50),
                 ADDRESS3      VARCHAR(50),
                 CREATED       TIMESTAMP    NOT NULL,
                 DIRECTIONS    CLOB,
                 EMAIL         VARCHAR(100),
                 FAX           VARCHAR(50),
                 MODIFIED      TIMESTAMP,
                 NAME          VARCHAR(250) NOT NULL UNIQUE,
                 PHONE         VARCHAR(50),
                 POSTAL_CODE   VARCHAR(50),
                 URL           VARCHAR(250),
                 VAT_ID_NUMBER VARCHAR(100),
                 CONSTRAINT FK_COMPANY_CITY FOREIGN KEY (FK_CITY_ID) REFERENCES "CITY" (PK_CITY_ID)
             )""";
    case TABLE_NAME_COUNTRY:
      return """
             CREATE TABLE "COUNTRY" (
                PK_COUNTRY_ID BIGINT         NOT NULL PRIMARY KEY,
                COUNTRY_MAP   BLOB,
                CREATED       TIMESTAMP      NOT NULL,
                ISO3166       VARCHAR(50),
                MODIFIED      TIMESTAMP,
                NAME          VARCHAR(100)   NOT NULL UNIQUE
             )""";
    case TABLE_NAME_COUNTRY_STATE:
      return """
             CREATE TABLE "COUNTRY_STATE" (
                PK_COUNTRY_STATE_ID BIGINT         NOT NULL PRIMARY KEY,
                FK_COUNTRY_ID       BIGINT         NOT NULL,
                FK_TIMEZONE_ID      BIGINT         NOT NULL,
                COUNTRY_STATE_MAP   BLOB,
                CREATED             TIMESTAMP      NOT NULL,
                MODIFIED            TIMESTAMP,
                NAME                VARCHAR(100)   NOT NULL,
                SYMBOL              VARCHAR(50),
                CONSTRAINT FK_COUNTRY_STATE_COUNTRY  FOREIGN KEY (FK_COUNTRY_ID)  REFERENCES "COUNTRY"  (PK_COUNTRY_ID),
                CONSTRAINT FK_COUNTRY_STATE_TIMEZONE FOREIGN KEY (FK_TIMEZONE_ID) REFERENCES "TIMEZONE" (PK_TIMEZONE_ID),
                CONSTRAINT UQ_COUNTRY_STATE          UNIQUE      (FK_COUNTRY_ID,NAME)
             )""";
    case TABLE_NAME_TIMEZONE:
      return """
             CREATE TABLE "TIMEZONE" (
                PK_TIMEZONE_ID BIGINT        NOT NULL PRIMARY KEY,
                ABBREVIATION   VARCHAR(50)   NOT NULL,
                CREATED        TIMESTAMP     NOT NULL,
                MODIFIED       TIMESTAMP,
                NAME           VARCHAR(100)  NOT NULL UNIQUE,
                V_TIME_ZONE    VARCHAR(4000)
             )""";
    default:
      throw new RuntimeException("Not yet implemented - database table : " + String.format(FORMAT_TABLE_NAME, tableName));
    }
  }

  private final void dropAllTables() {
    try {
      statement = connection.createStatement();

      for (String tableName : TABLE_NAMES_DROP) {
        statement.execute("DROP TABLE IF EXISTS \"" + tableName + "\"");
      }

      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private final void init() {
    String methodName = new Object() {
    }.getClass().getEnclosingMethod().getName();

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- Start");

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- client  =" + isClient);
    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- embedded=" + isEmbedded);

    dbms   = Dbms.H2;

    driver = "org.h2.Driver";

    if (isClient) {
      url = config.getH2ConnectionPrefix() + "tcp://" + config.getJdbcConnectionHost() + (SystemUtils.IS_OS_WINDOWS ? "" : ":" + config.getH2ConnectionPort())
          + "/" + config.getH2Database();
    } else {
      url = config.getH2ConnectionPrefix() + "file:" + config.getH2Database();
    }

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- End");
  }

  @Override
  protected final void setupDatabase() {
    String methodName = new Object() {
    }.getClass().getEnclosingMethod().getName();

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- Start");

    // -----------------------------------------------------------------------
    // Connect.
    // -----------------------------------------------------------------------

    connection = connect(url, driver, "sa", "");

    try {
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    String h2Password = config.getH2Password();
    String h2User     = config.getH2User();

    // -----------------------------------------------------------------------
    // Drop the schema and the database tables if already existing
    // -----------------------------------------------------------------------

    try {
      dropAllTables();

      statement = connection.createStatement();

      statement.execute("DROP USER IF EXISTS " + h2User);
    } catch (SQLException e) {
      e.printStackTrace();
      connection = connect(url, driver, "sa", "");
    }

    // -----------------------------------------------------------------------
    // Create the schema
    // -----------------------------------------------------------------------

    try {
      statement.execute("CREATE USER " + h2User + " PASSWORD '" + h2Password + "' ADMIN");

      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Disconnect and reconnect.
    // -----------------------------------------------------------------------

    disconnect(connection);

    connection = connect(url, null, h2User, h2Password);

    logger.debug(String.format(FORMAT_METHOD_NAME, methodName) + "- End");
  }
}

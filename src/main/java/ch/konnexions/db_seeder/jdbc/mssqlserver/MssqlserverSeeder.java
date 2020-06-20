/**
 *
 */
package ch.konnexions.db_seeder.jdbc.mssqlserver;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.DatabaseSeeder;
import ch.konnexions.db_seeder.jdbc.AbstractJdbcSeeder;

/**
 * <h1> Test Data Generator for a Microsoft SQL Server DBMS. </h1>
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public class MssqlserverSeeder extends AbstractJdbcSeeder {

  private static Logger logger = Logger.getLogger(MssqlserverSeeder.class);

  /**
   * 
   */
  public MssqlserverSeeder() {
    super();

    dbms     = Dbms.MSSQLSERVER;

    urlBase  = config.getMssqlserverConnectionPrefix() + config.getJdbcConnectionHost() + ":" + config.getMssqlserverConnectionPort() + ";databaseName=";

    url      = urlBase + config.getMssqlserverDatabase() + ";user=" + config.getMssqlserverUser() + ";password=" + config.getMssqlserverPassword();
    urlSetup = urlBase + "master;user=sa;password=" + config.getMssqlserverPasswordSys();
  }

  @SuppressWarnings("preview")
  @Override
  protected final String createDdlStmnt(final String tableName) {
    switch (tableName) {
    case TABLE_NAME_CITY:
      return """
             CREATE TABLE CITY (
                 PK_CITY_ID          BIGINT         NOT NULL PRIMARY KEY IDENTITY (1,1),
                 FK_COUNTRY_STATE_ID BIGINT,
                 CITY_MAP            VARBINARY(MAX),
                 CREATED             DATETIME2      NOT NULL,
                 MODIFIED            DATETIME2,
                 NAME                VARCHAR(100)   NOT NULL,
                 CONSTRAINT FK_CITY_COUNTRY_STATE FOREIGN KEY (FK_COUNTRY_STATE_ID) REFERENCES COUNTRY_STATE (PK_COUNTRY_STATE_ID)
              )""";
    case TABLE_NAME_COMPANY:
      return """
             CREATE TABLE COMPANY (
                 PK_COMPANY_ID BIGINT       NOT NULL PRIMARY KEY IDENTITY (1,1),
                 FK_CITY_ID    BIGINT       NOT NULL,
                 ACTIVE        VARCHAR(1)   NOT NULL,
                 ADDRESS1      VARCHAR(50),
                 ADDRESS2      VARCHAR(50),
                 ADDRESS3      VARCHAR(50),
                 CREATED       DATETIME2    NOT NULL,
                 DIRECTIONS    VARCHAR(MAX),
                 EMAIL         VARCHAR(100),
                 FAX           VARCHAR(20),
                 MODIFIED      DATETIME2,
                 NAME          VARCHAR(250) NOT NULL UNIQUE,
                 PHONE         VARCHAR(50),
                 POSTAL_CODE   VARCHAR(20),
                 URL           VARCHAR(250),
                 VAT_ID_NUMBER VARCHAR(50),
                 CONSTRAINT FK_COMPANY_CITY FOREIGN KEY (FK_CITY_ID) REFERENCES CITY (PK_CITY_ID)
             )""";
    case TABLE_NAME_COUNTRY:
      return """
             CREATE TABLE COUNTRY (
                PK_COUNTRY_ID BIGINT         NOT NULL PRIMARY KEY IDENTITY (1,1),
                COUNTRY_MAP   VARBINARY(MAX),
                CREATED       DATETIME2      NOT NULL,
                ISO3166       VARCHAR(2),
                MODIFIED      DATETIME2,
                NAME          VARCHAR(100)   NOT NULL UNIQUE
             )""";
    case TABLE_NAME_COUNTRY_STATE:
      return """
             CREATE TABLE COUNTRY_STATE (
                PK_COUNTRY_STATE_ID BIGINT         NOT NULL PRIMARY KEY IDENTITY (1,1),
                FK_COUNTRY_ID       BIGINT         NOT NULL,
                FK_TIMEZONE_ID      BIGINT         NOT NULL,
                COUNTRY_STATE_MAP   VARBINARY(MAX),
                CREATED             DATETIME2      NOT NULL,
                MODIFIED            DATETIME2,
                NAME                VARCHAR(100)   NOT NULL,
                SYMBOL              VARCHAR(10),
                CONSTRAINT FK_COUNTRY_STATE_COUNTRY  FOREIGN KEY (FK_COUNTRY_ID)  REFERENCES COUNTRY  (PK_COUNTRY_ID),
                CONSTRAINT FK_COUNTRY_STATE_TIMEZONE FOREIGN KEY (FK_TIMEZONE_ID) REFERENCES TIMEZONE (PK_TIMEZONE_ID),
                CONSTRAINT UQ_COUNTRY_STATE          UNIQUE      (FK_COUNTRY_ID,NAME)
             )""";
    case TABLE_NAME_TIMEZONE:
      return """
             CREATE TABLE TIMEZONE (
                PK_TIMEZONE_ID BIGINT        NOT NULL PRIMARY KEY IDENTITY (1,1),
                ABBREVIATION   VARCHAR(20)   NOT NULL,
                CREATED        DATETIME2     NOT NULL,
                MODIFIED       DATETIME2,
                NAME           VARCHAR(100)  NOT NULL UNIQUE,
                V_TIME_ZONE    VARCHAR(4000)
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

    try {
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Drop the database if already existing.
    // -----------------------------------------------------------------------

    final String mssqlserverDatabase = config.getMssqlserverDatabase();
    final String mssqlserverSchema   = config.getMssqlserverSchema();
    final String mssqlserverUser     = config.getMssqlserverUser();

    try {
      statement = connection.createStatement();

      statement.execute("DROP DATABASE IF EXISTS " + mssqlserverDatabase);
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Create the database, schema and database user.
    // -----------------------------------------------------------------------

    try {
      statement.execute("sp_configure 'contained database authentication', 1");

      statement.execute("RECONFIGURE");

      statement.execute("USE master");

      statement.execute("CREATE DATABASE " + mssqlserverDatabase);

      statement.execute("USE master");

      statement.execute("ALTER DATABASE " + mssqlserverDatabase + " SET CONTAINMENT = PARTIAL");

      statement.execute("USE " + mssqlserverDatabase);

      statement.execute("CREATE SCHEMA " + mssqlserverSchema);

      statement.execute("CREATE USER " + mssqlserverUser + " WITH PASSWORD = '" + config.getMssqlserverPassword() + "', DEFAULT_SCHEMA=" + mssqlserverSchema);

      statement.execute("sp_addrolemember 'db_owner', '" + mssqlserverUser + "'");

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

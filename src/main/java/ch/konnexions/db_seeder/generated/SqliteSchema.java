/**
 * 
 */
package ch.konnexions.db_seeder.generated;

import java.util.HashMap;

/**
 * CREATE TABLE statements for a SQLite DBMS.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public final class SqliteSchema implements JdbcSchema {

  public final static HashMap<String, String> createTableStmnts = createTableStmnts();

  /**
   * Creates the CREATE TABLE statements.
   */
  @SuppressWarnings("preview")
  private final static HashMap<String, String> createTableStmnts() {
    HashMap<String, String> statements = new HashMap<String, String>();

    statements.put(TABLE_NAME_CITY,
                   """
                   CREATE TABLE CITY (
                       PK_CITY_ID          INTEGER        NOT NULL PRIMARY KEY,
                       FK_COUNTRY_STATE_ID BIGINT,
                       CITY_MAP            BLOB,
                       CREATED             DATETIME       NOT NULL,
                       MODIFIED            DATETIME,
                       NAME                VARCHAR2 (100) NOT NULL,
                       FOREIGN KEY (FK_COUNTRY_STATE_ID) REFERENCES COUNTRY_STATE (PK_COUNTRY_STATE_ID)
                   )
                   """);

    statements.put(TABLE_NAME_COMPANY,
                   """
                   CREATE TABLE COMPANY (
                       PK_COMPANY_ID       INTEGER        NOT NULL PRIMARY KEY,
                       FK_CITY_ID          BIGINT         NOT NULL,
                       ACTIVE              VARCHAR2 (1)   NOT NULL,
                       ADDRESS1            VARCHAR2 (50),
                       ADDRESS2            VARCHAR2 (50),
                       ADDRESS3            VARCHAR2 (50),
                       CREATED             DATETIME       NOT NULL,
                       DIRECTIONS          CLOB,
                       EMAIL               VARCHAR2 (100),
                       FAX                 VARCHAR2 (20),
                       MODIFIED            DATETIME,
                       NAME                VARCHAR2 (250) NOT NULL UNIQUE,
                       PHONE               VARCHAR2 (50),
                       POSTAL_CODE         VARCHAR2 (20),
                       URL                 VARCHAR2 (250),
                       VAT_ID_NUMBER       VARCHAR2 (50),
                       FOREIGN KEY (FK_CITY_ID)          REFERENCES CITY (PK_CITY_ID)
                   )
                   """);

    statements.put(TABLE_NAME_COUNTRY,
                   """
                   CREATE TABLE COUNTRY (
                       PK_COUNTRY_ID INTEGER        NOT NULL PRIMARY KEY,
                       COUNTRY_MAP   BLOB,
                       CREATED       DATETIME       NOT NULL,
                       ISO3166       VARCHAR2 (2),
                       MODIFIED      DATETIME,
                       NAME          VARCHAR2 (100) NOT NULL UNIQUE
                   )
                   """);

    statements.put(TABLE_NAME_COUNTRY_STATE,
                   """
                   CREATE TABLE COUNTRY_STATE (
                       PK_COUNTRY_STATE_ID INTEGER        NOT NULL PRIMARY KEY,
                       FK_COUNTRY_ID       BIGINT         NOT NULL,
                       FK_TIMEZONE_ID      BIGINT         NOT NULL,
                       COUNTRY_STATE_MAP   BLOB,
                       CREATED             DATETIME       NOT NULL,
                       MODIFIED            DATETIME,
                       NAME                VARCHAR2 (100) NOT NULL,
                       SYMBOL              VARCHAR2 (10),
                       FOREIGN KEY (FK_COUNTRY_ID)  REFERENCES COUNTRY  (PK_COUNTRY_ID),
                       FOREIGN KEY (FK_TIMEZONE_ID) REFERENCES TIMEZONE (PK_TIMEZONE_ID),
                       UNIQUE (FK_COUNTRY_ID, NAME)
                   )
                   """);

    statements.put(TABLE_NAME_TIMEZONE,
                   """
                   CREATE TABLE TIMEZONE (
                       PK_TIMEZONE_ID INTEGER         NOT NULL PRIMARY KEY,
                       ABBREVIATION   VARCHAR2 (20)   NOT NULL,
                       CREATED        DATETIME        NOT NULL,
                       MODIFIED       DATETIME,
                       NAME           VARCHAR2 (100)  NOT NULL UNIQUE,
                       V_TIME_ZONE    VARCHAR2 (4000)
                   )
                   """);

    return statements;
  }

}
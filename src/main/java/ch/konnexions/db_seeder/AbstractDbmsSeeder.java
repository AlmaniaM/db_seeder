package ch.konnexions.db_seeder;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.utils.Config;

/**
 * Test Data Generator for a Database - Abstract DBMS Seeder.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public abstract class AbstractDbmsSeeder {

  public enum DbmsEnum {

    CRATEDB(
        "cratedb"
    ),
    CUBRID(
        "cubrid"
    ),
    DERBY(
        "derby"
    ),
    FIREBIRD(
        "firebird"
    ),
    H2(
        "h2"
    ),
    HSQLDB(
        "hsqldb"
    ),
    IBMDB2(
        "ibmdb2"
    ),
    INFORMIX(
        "informix"
    ),
    MARIADB(
        "mariadb"
    ),
    MIMER(
        "mimer"
    ),
    MSSQLSERVER(
        "mssqlserver"
    ),
    MYSQL(
        "mysql"
    ),
    ORACLE(
        "oracle"
    ),
    POSTGRESQL(
        "postgresql"
    ),
    SQLITE(
        "sqlite"
    );

    private final String tickerSymbol;

    DbmsEnum(String tickerSymbol) {
      this.tickerSymbol = tickerSymbol;
    }

    public String getTickerSymbol() {
      return tickerSymbol;
    }
  }

  public static final Map<String, String[]> dbmsDetails                       = initDbmsDetails();

  //  public static final int                   DBMS_DETAILS_NAME_CHOICE          = 1;
  //  public static final int                   DBMS_DETAILS_CLIENT_EMBEDDED      = 2;
  private static final int                  DBMS_DETAILS_NAME                 = 3;
  private static final int                  DBMS_DETAILS_IDENTIFIER_DELIMITER = 4;
  //  public static final int                   DBMS_DETAILS_TICKER_SYMBOL        = 0;

  public static final String                FORMAT_IDENTIFIER                 = "%-10d";
  // protected static final String   FORMAT_IDENTIFIER_RIGHT  = "%010d";
  protected static final String             FORMAT_ROW_NO                     = "%1$10d";
  protected static final String             FORMAT_TABLE_NAME                 = "%-17s";

  private static final Logger               logger                            = Logger.getLogger(AbstractDbmsSeeder.class);

  /**
   * Initialises the DBMS details.
   * 
   * Attributes: 0 - DBMS name long
   *             1 - client / embedded
   *             2 - DBMS name short
   *             3 - identifier delimiter
   *
   * @return the map containing the DBMS details
   */
  private static Map<String, String[]> initDbmsDetails() {
    Map<String, String[]> dbmsDetails = new HashMap<>();

    dbmsDetails.put("cratedb",
                    new String[] {
                        "cratedb",
                        "CrateDB",
                        "client",
                        "CrateDB",
                        "" });
    dbmsDetails.put("cubrid",
                    new String[] {
                        "cubrid",
                        "CUBRID",
                        "client",
                        "CUBRID",
                        "\"" });
    dbmsDetails.put("derby",
                    new String[] {
                        "derby",
                        "Apache Derby [client]",
                        "client",
                        "Apache Derby",
                        "" });
    dbmsDetails.put("derby_emb",
                    new String[] {
                        "derby",
                        "Apache Derby [embedded]",
                        "embedded",
                        "Apache Derby",
                        "" });
    dbmsDetails.put("firebird",
                    new String[] {
                        "firebird",
                        "Firebird",
                        "client",
                        "Firebird",
                        "" });
    dbmsDetails.put("h2",
                    new String[] {
                        "h2",
                        "H2 Database Engine [client]",
                        "client",
                        "H2",
                        "" });
    dbmsDetails.put("h2_emb",
                    new String[] {
                        "h2",
                        "H2 Database Engine [embedded]",
                        "embedded",
                        "H2",
                        "" });
    dbmsDetails.put("hsqldb",
                    new String[] {
                        "hsqldb",
                        "HyperSQL Database [client]",
                        "client",
                        "HyperSQL",
                        "" });
    dbmsDetails.put("hsqldb_emb",
                    new String[] {
                        "hsqldb",
                        "HyperSQL Database [embedded]",
                        "embedded",
                        "HyperSQL",
                        "" });
    dbmsDetails.put("ibmdb2",
                    new String[] {
                        "ibmdb2",
                        "IBM Db2 Database",
                        "client",
                        "IBM Db2",
                        "" });
    dbmsDetails.put("informix",
                    new String[] {
                        "informix",
                        "IBM Informix",
                        "client",
                        "IBM Informix",
                        "" });
    dbmsDetails.put("mariadb",
                    new String[] {
                        "mariadb",
                        "MariaDB Server",
                        "client",
                        "MariaDB",
                        "`" });
    dbmsDetails.put("mimer",
                    new String[] {
                        "mimer",
                        "Mimer SQL",
                        "client",
                        "Mimer",
                        "" });
    dbmsDetails.put("mssqlserver",
                    new String[] {
                        "mssqlserver",
                        "MS SQL Server",
                        "client",
                        "MS SQL Server",
                        "" });
    dbmsDetails.put("mysql",
                    new String[] {
                        "mysql",
                        "MySQL Database",
                        "client",
                        "MySQL",
                        "`" });
    dbmsDetails.put("oracle",
                    new String[] {
                        "oracle",
                        "Oracle Database",
                        "client",
                        "Oracle",
                        "" });
    dbmsDetails.put("postgresql",
                    new String[] {
                        "postgresql",
                        "PostgreSQL Database",
                        "client",
                        "PostgreSQL",
                        "" });
    dbmsDetails.put("sqlite",
                    new String[] {
                        "sqlite",
                        "SQLite",
                        "embedded",
                        "SQLite",
                        "" });

    return dbmsDetails;
  }

  protected Config        config;
  protected DbmsEnum      dbmsEnum;

  protected String        dbmsTickerSymbol;

  protected String        identifierDelimiter;
  protected final boolean isDebug = logger.isDebugEnabled();

  /**
   * Initialises a new abstract DBMS seeder object.
   */
  public AbstractDbmsSeeder() {
    super();
  }

  /**
   * Initialises a new abstract DBMS seeder object.
   *
   * @param dbmsTickerSymbol DBMS ticker symbol 
   */
  public AbstractDbmsSeeder(String dbmsTickerSymbol) {
    super();

    if (isDebug) {
      logger.debug("Start Constructor - dbmsTickerSymbol=" + dbmsTickerSymbol);
    }

    identifierDelimiter = dbmsDetails.get(dbmsTickerSymbol)[DBMS_DETAILS_IDENTIFIER_DELIMITER];

    if (isDebug) {
      logger.debug("End   Constructor");
    }
  }

  /**
   * Initialises a new abstract DBMS seeder object.
   *
   * @param dbmsTickerSymbol DBMS ticker symbol 
   * @param isClient client database version
   */
  public AbstractDbmsSeeder(String dbmsTickerSymbol, boolean isClient) {
    super();

    if (isDebug) {
      logger.debug("Start Constructor - dbmsTickerSymbol=" + dbmsTickerSymbol + " - isClient=" + isClient);
    }

    identifierDelimiter = dbmsDetails.get(dbmsTickerSymbol)[DBMS_DETAILS_IDENTIFIER_DELIMITER];

    if (isDebug) {
      logger.debug("End   Constructor");
    }
  }

  /**
   * Gets the DBMS name.
   *
   * @param tickerSymbolLower the lower case ticker symbol
   *
   * @return the DBMS name
   */
  public final String getDbmsName(String tickerSymbolLower) {
    return dbmsDetails.get(tickerSymbolLower)[DBMS_DETAILS_NAME];
  }

  /**
   * Gets the identifier delimiter.
   *
   * @param tickerSymbolLower the lower case ticker symbol
   *
   * @return the identifier delimiter
   */
  public final String getIdentifierDelimiter(String tickerSymbolLower) {
    return dbmsDetails.get(tickerSymbolLower)[DBMS_DETAILS_IDENTIFIER_DELIMITER];
  }
}
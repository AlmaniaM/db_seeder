/**
 *
 */
package ch.konnexions.db_seeder.jdbc.apache.derby;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.jdbc.AbstractJdbcSeeder;

/**
 * Test Data Generator for a Apache Derby DBMS.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public class DerbySeeder extends AbstractJdbcSeeder {

  private static Logger logger = Logger.getLogger(AbstractJdbcSeeder.class);

  /**
   * Instantiates a new Apache Derby seeder.
   * 
   * @param dbmsTickerSymbol 
   */
  public DerbySeeder(String dbmsTickerSymbol) {
    super();

    String methodName = null;

    if (isDebug) {
      methodName = new Object() {
      }.getClass().getName();

      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- Start Constructor");
    }

    this.dbmsTickerSymbol = dbmsTickerSymbol;

    init();

    if (isDebug) {
      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- End   Constructor");
    }
  }

  /**
   * Instantiates a new Apache Derby seeder.
   * 
   * @param dbmsTickerSymbol 
   * @param isClient client database version
   */
  public DerbySeeder(String dbmsTickerSymbol, boolean isClient) {
    super(isClient);

    String methodName = null;

    if (isDebug) {
      methodName = new Object() {
      }.getClass().getName();

      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- Start Constructor");
    }

    this.dbmsTickerSymbol = dbmsTickerSymbol;

    init();

    if (isDebug) {
      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- End   Constructor");
    }
  }

  /**
   * Create the DDL statement: CREATE TABLE.
   *
   * @param tableName the database table name
   *
   * @return the 'CREATE TABLE' statement
   */
  @Override
  protected final String createDdlStmnt(final String tableName) {
    return DerbySchema.createTableStmnts.get(tableName);
  }

  /**
   * The common intialisation part.
   */
  private final void init() {
    String methodName = null;

    if (isDebug) {
      methodName = new Object() {
      }.getClass().getEnclosingMethod().getName();

      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- Start");

      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- client  =" + isClient);
      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- embedded=" + isEmbedded);
    }

    dbms = Dbms.DERBY;

    if (isClient) {
      driver  = "org.apache.derby.jdbc.ClientDriver";
      urlBase = config.getConnectionPrefix() + "//" + config.getConnectionHost() + ":" + config.getConnectionPort() + "/" + config.getDatabase() + ";create=";
    } else {
      driver  = "org.apache.derby.jdbc.EmbeddedDriver";
      urlBase = config.getConnectionPrefix() + ";databaseName=" + config.getDatabase() + ";create=";
    }

    tableNameDelimiter = "";

    url                = urlBase + "false";
    urlSetup           = urlBase + "true";

    dropTableStmnt     = "SELECT 'DROP TABLE \"' || T.TABLENAME || '\"' FROM SYS.SYSTABLES T INNER JOIN SYS.SYSSCHEMAS S ON T.SCHEMAID = S.SCHEMAID WHERE T.TABLENAME = '?' AND S.SCHEMANAME = 'APP'";

    if (isDebug) {
      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- End");
    }
  }

  /**
   * Delete any existing relevant database schema objects (database, user, 
   * schema or tables)and initialise the database for a new run.
   */
  @Override
  protected final void setupDatabase() {
    String methodName = null;

    if (isDebug) {
      methodName = new Object() {
      }.getClass().getEnclosingMethod().getName();

      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- Start");
    }

    // -----------------------------------------------------------------------
    // Connect.
    // -----------------------------------------------------------------------

    connection = connect(urlSetup,
                         driver,
                         true);

    // -----------------------------------------------------------------------
    // Tear down an existing schema.
    // -----------------------------------------------------------------------

    try {
      statement = connection.createStatement();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    dropAllTables(dropTableStmnt);

    try {
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

    if (isDebug) {
      logger.debug(String.format(FORMAT_METHOD_NAME,
                                 methodName) + "- End");
    }
  }
}

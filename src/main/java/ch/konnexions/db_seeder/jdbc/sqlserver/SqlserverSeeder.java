package ch.konnexions.db_seeder.jdbc.sqlserver;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ch.konnexions.db_seeder.generated.AbstractGenSqlserverSchema;
import ch.konnexions.db_seeder.jdbc.AbstractJdbcSeeder;

/**
 * Test Data Generator for a Microsoft SQL Server DBMS.
 * <br>
 * @author  walter@konnexions.ch
 * @since   2020-05-01
 */
public final class SqlserverSeeder extends AbstractGenSqlserverSchema {

  private static final Logger logger = Logger.getLogger(SqlserverSeeder.class);

  /**
   * Gets the connection URL for Presto (used by PrestoEnvironment).
   *
   * @param connectionHost the connection host name
   * @param connectionPort the connection port number
   * @param connectionPrefix the connection prefix
   * @param database the database with non-privileged access
   * @param user the user with non-privileged access
   * @param password the password with non-privileged access
   *
   * @return the connection URL for non-privileged access
   */
  public final static String getUrlPresto(String connectionHost, int connectionPort, String connectionPrefix, String database, String user, String password) {
    return getUrlUser(connectionHost,
                      connectionPort,
                      connectionPrefix,
                      database,
                      user,
                      password);
  }

  /**
   * Gets the connection URL for privileged access.
   *
   * @param connectionHost the connection host name
   * @param connectionPort the connection port number
   * @param connectionPrefix the connection prefix
   * @param databaseSys the database with privileged access
   * @param userSys the user with privileged access
   * @param passwordSys the password with privileged access
   *
   * @return the connection URL for privileged access
   */
  private final static String getUrlSys(String connectionHost,
                                        int connectionPort,
                                        String connectionPrefix,
                                        String databaseSys,
                                        String userSys,
                                        String passwordSys) {
    return connectionPrefix + connectionHost + ":" + connectionPort + ";databaseName=" + databaseSys + ";user=" + userSys + ";password=" + passwordSys;
  }

  /**
   * Gets the connection URL for non-privileged access.
   *
   * @param connectionHost the connection host name
   * @param connectionPort the connection port number
   * @param connectionPrefix the connection prefix
   * @param database the database with non-privileged access
   * @param user the user with non-privileged access
   * @param password the password with non-privileged access
   *
   * @return the connection URL for non-privileged access
   */
  private final static String getUrlUser(String connectionHost, int connectionPort, String connectionPrefix, String database, String user, String password) {
    return connectionPrefix + connectionHost + ":" + connectionPort + ";databaseName=" + database + ";user=" + user + ";password=" + password;
  }

  private final boolean isDebug = logger.isDebugEnabled();

  /**
   * Instantiates a new Microsoft SQL Server seeder object.
   * 
   * @param tickerSymbolExtern the external DBMS ticker symbol 
   */
  public SqlserverSeeder(String tickerSymbolExtern) {
    this(tickerSymbolExtern, "client");
  }

  /**
   * Instantiates a new Microsoft SQL Server seeder object.
   * 
   * @param tickerSymbolExtern the external DBMS ticker symbol 
   * @param dbmsOption client, embedded or presto
   */
  public SqlserverSeeder(String tickerSymbolExtern, String dbmsOption) {
    super(tickerSymbolExtern, dbmsOption);

    if (isDebug) {
      logger.debug("Start Constructor - tickerSymbolExtern=" + tickerSymbolExtern + " - dbmsOption=" + dbmsOption);
    }

    dbmsEnum = DbmsEnum.SQLSERVER;

    if (isPresto) {
      urlPresto = AbstractJdbcSeeder.getUrlPresto(tickerSymbolLower,
                                                  config.getConnectionHostPresto(),
                                                  config.getConnectionPortPresto(),
                                                  config.getDatabase());
    }

    urlSys  = getUrlSys(config.getConnectionHost(),
                        config.getConnectionPort(),
                        config.getConnectionPrefix(),
                        config.getDatabaseSys(),
                        config.getUserSys(),
                        config.getPasswordSys());

    urlUser = getUrlUser(config.getConnectionHost(),
                         config.getConnectionPort(),
                         config.getConnectionPrefix(),
                         config.getDatabase(),
                         config.getUser(),
                         config.getPassword());

    if (isDebug) {
      logger.debug("End   Constructor");
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
  protected final String createDdlStmnt(String tableName) {
    return AbstractGenSqlserverSchema.createTableStmnts.get(tableName);
  }

  /**
   * Delete any existing relevant database schema objects (database, user, 
   * schema or valTableNames)and initialise the database for a new run.
   */
  @Override
  protected final void setupDatabase() {
    if (isDebug) {
      logger.debug("Start");
    }

    // -----------------------------------------------------------------------
    // Connect.
    // -----------------------------------------------------------------------

    connection = connect(urlSys,
                         true);

    final String databaseNmame = config.getDatabase();
    final String schemaName    = config.getSchema();
    final String userName      = config.getUser();

    // -----------------------------------------------------------------------
    // Tear down an existing schema.
    // -----------------------------------------------------------------------

    try {
      statement = connection.createStatement();

      executeDdlStmnts("DROP DATABASE IF EXISTS " + databaseNmame);
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Setup the database.
    // -----------------------------------------------------------------------

    try {
      executeDdlStmnts("sp_configure 'contained database authentication', 1",
                       "RECONFIGURE",
                       "USE master",
                       "CREATE DATABASE " + databaseNmame,
                       "USE master",
                       "ALTER DATABASE " + databaseNmame + " SET CONTAINMENT = PARTIAL",
                       "USE " + databaseNmame,
                       "CREATE SCHEMA " + schemaName,
                       "CREATE USER " + userName + " WITH PASSWORD = '" + config.getPassword() + "', DEFAULT_SCHEMA=" + schemaName,
                       "sp_addrolemember 'db_owner', '" + userName + "'");

      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Create database schema.
    // -----------------------------------------------------------------------

    disconnect(connection);

    connection = connect(urlUser);

    try {
      statement = connection.createStatement();

      createSchema();

      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // -----------------------------------------------------------------------
    // Disconnect and reconnect - Presto.
    // -----------------------------------------------------------------------

    if (isPresto) {
      disconnect(connection);

      connection = connect(urlPresto,
                           driver_presto,
                           true);
    }

    if (isDebug) {
      logger.debug("End");
    }
  }
}
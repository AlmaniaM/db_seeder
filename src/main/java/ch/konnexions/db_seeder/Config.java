/*
 *
 */

package ch.konnexions.db_seeder;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * The configuration parameters for the supported database management systems. 
 * 
 * The configuration parameters are made available to the configuration object
 * in a text file.
 * 
 * The parameter name and parameter value must be separated by an equal sign (=).
 */
public class Config {

  @SuppressWarnings("unused")
  private static Logger                                                logger     = Logger.getLogger(Config.class);

  private int                                                          cratedbConnectionPort;
  private String                                                       cratedbConnectionPrefix;
  private String                                                       cratedbPassword;
  private String                                                       cratedbUser;

  private int                                                          cubridConnectionPort;
  private String                                                       cubridConnectionPrefix;
  private String                                                       cubridDatabase;
  private String                                                       cubridPassword;
  private String                                                       cubridUser;

  private int                                                          derbyConnectionPort;
  private String                                                       derbyConnectionPrefix;
  private String                                                       derbyDatabase;

  private boolean                                                      encodingIso_8859_1;
  private boolean                                                      encodingUtf_8;

  private final FileBasedConfigurationBuilder<PropertiesConfiguration> fileBasedConfigurationBuilder;
  private String                                                       fileConfigurationName;

  private int                                                          firebirdConnectionPort;
  private String                                                       firebirdConnectionPrefix;
  private String                                                       firebirdDatabase;
  private String                                                       firebirdPassword;
  private String                                                       firebirdPasswordSys;
  private String                                                       firebirdUser;

  @SuppressWarnings("unused")
  private final DateTimeFormatter                                      formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn");

  private int                                                          h2ConnectionPort;
  private String                                                       h2ConnectionPrefix;
  private String                                                       h2Database;
  private String                                                       h2Password;
  private String                                                       h2Schema;
  private String                                                       h2User;

  private int                                                          hsqldbConnectionPort;
  private String                                                       hsqldbConnectionPrefix;
  private String                                                       hsqldbDatabase;
  private String                                                       hsqldbPassword;
  private String                                                       hsqldbSchema;
  private String                                                       hsqldbUser;

  private int                                                          ibmdb2ConnectionPort;
  private String                                                       ibmdb2ConnectionPrefix;
  private String                                                       ibmdb2Database;
  private String                                                       ibmdb2Password;
  private String                                                       ibmdb2Schema;

  private String                                                       jdbcConnectionHost;

  private ArrayList<String>                                            keysSorted = new ArrayList<>();

  private int                                                          mariadbConnectionPort;
  private String                                                       mariadbConnectionPrefix;
  private String                                                       mariadbDatabase;
  private String                                                       mariadbPassword;
  private String                                                       mariadbPasswordSys;
  private String                                                       mariadbUser;

  private int                                                          maxRowCity;
  private int                                                          maxRowCompany;
  private int                                                          maxRowCountry;
  private int                                                          maxRowCountryState;
  private int                                                          maxRowTimezone;

  private int                                                          mssqlserverConnectionPort;
  private String                                                       mssqlserverConnectionPrefix;
  private String                                                       mssqlserverDatabase;
  private String                                                       mssqlserverPassword;
  private String                                                       mssqlserverPasswordSys;
  private String                                                       mssqlserverSchema;
  private String                                                       mssqlserverUser;

  private int                                                          mysqlConnectionPort;
  private String                                                       mysqlConnectionPrefix;
  private String                                                       mysqlConnectionSuffix;
  private String                                                       mysqlDatabase;
  private String                                                       mysqlPassword;
  private String                                                       mysqlPasswordSys;
  private String                                                       mysqlUser;

  private int                                                          oracleConnectionPort;
  private String                                                       oracleConnectionPrefix;
  private String                                                       oracleConnectionService;
  private String                                                       oraclePassword;
  private String                                                       oraclePasswordSys;
  private String                                                       oracleUser;

  private int                                                          postgresqlConnectionPort;
  private String                                                       postgresqlConnectionPrefix;
  private String                                                       postgresqlDatabase;
  private String                                                       postgresqlPassword;
  private String                                                       postgresqlPasswordSys;
  private String                                                       postgresqlUser;

  private PropertiesConfiguration                                      propertiesConfiguration;

  private String                                                       sqliteConnectionPrefix;
  private String                                                       sqliteDatabase;

  /**
   * Initialises a new configuration object.
   */
  public Config() {
    super();

    String methodName = new Object() {
    }.getClass().getName();

    logger.debug(String.format(AbstractDatabaseSeeder.FORMAT_METHOD_NAME, methodName) + "- Start Constructor");

    fileBasedConfigurationBuilder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);

    File configFile = new File(System.getenv("DB_SEEDER_FILE_CONFIGURATION_NAME"));
    fileBasedConfigurationBuilder.configure(new Parameters().properties().setFile(configFile));

    try {
      propertiesConfiguration = fileBasedConfigurationBuilder.getConfiguration();
      updatePropertiesFromOs();
      keysSorted = getKeysSorted();
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }

    storeConfiguration();
    validateProperties();

    logger.debug(String.format(AbstractDatabaseSeeder.FORMAT_METHOD_NAME, methodName) + "- End   Constructor");
  }

  // Apache Derby ------------------------------------------------------------

  /**
   * @return the Apache Derby port number where the database server is listening for requests
   */
  public final int getApachederbyConnectionPort() {
    return derbyConnectionPort;
  }

  /**
   * @return the prefix of the Apache Derby connection string
   */
  public final String getApachederbyConnectionPrefix() {
    return derbyConnectionPrefix;
  }

  /**
   * @return the IBM Db2 Database name
   */
  public final String getApachederbyDatabase() {
    return derbyDatabase;
  }

  @SuppressWarnings("unused")
  private final List<String> getBooleanProperties() {

    List<String> list = new ArrayList<>();

    list.add("db_seeder.encoding.iso_8859_1");
    list.add("db_seeder.encoding.utf_8");

    return list;
  }

  // CrateDB ----------------------------------------------------------

  /**
   * @return the CrateDB port number where the database server is listening for requests
   */
  public final int getCratedbConnectionPort() {
    return cratedbConnectionPort;
  }

  /**
   * @return the prefix of the CrateDB connection string
   */
  public final String getCratedbConnectionPrefix() {
    return cratedbConnectionPrefix;
  }

  /**
   * @return the CrateDB password to connect as normal user to the database
   */
  public final String getCratedbPassword() {
    return cratedbPassword;
  }

  /**
   * @return the CrateDB user name to connect as normal user to the database
   */
  public final String getCratedbUser() {
    return cratedbUser;
  }

  // CUBRID ------------------------------------------------------------------

  /**
   * @return the CUBRID port number where the database server is listening for requests
   */
  public final int getCubridConnectionPort() {
    return cubridConnectionPort;
  }

  /**
   * @return the prefix of the CUBRID connection string
   */
  public final String getCubridConnectionPrefix() {
    return cubridConnectionPrefix;
  }

  /**
   * @return the CUBRID name
   */
  public final String getCubridDatabase() {
    return cubridDatabase;
  }

  /**
   * @return the CUBRID password to connect to the database
   */
  public final String getCubridPassword() {
    return cubridPassword;
  }

  /**
   * @return the CUBRID schema name
   */
  public final String getCubridUser() {
    return cubridUser.toUpperCase();
  }

  // Encoding ----------------------------------------------------------------

  /**
   * @return the encoding option for ISO-8859-1
   */
  public final boolean getEncodingIso_8859_1() {
    return encodingIso_8859_1;
  }

  /**
   * @return the encoding option for UTF-8
   */
  public final boolean getEncodingUtf_8() {
    return encodingUtf_8;
  }

  // Firebird ----------------------------------------------------------------

  /**
   * @return the Firebird port number where the database server is listening for requests
   */
  public final int getFirebirdConnectionPort() {
    return firebirdConnectionPort;
  }

  /**
   * @return the prefix of the Firebird connection string
   */
  public final String getFirebirdConnectionPrefix() {
    return firebirdConnectionPrefix;
  }

  /**
   * @return the Firebird database name
   */
  public final String getFirebirdDatabase() {
    return firebirdDatabase;
  }

  /**
   * @return the Firebird password to connect as normal user to the database
   */
  public final String getFirebirdPassword() {
    return firebirdPassword;
  }

  /**
   * @return the Firebird password to connect as privileged user to the database
   */
  public final String getFirebirdPasswordSys() {
    return firebirdPasswordSys;
  }

  /**
   * @return the MariaDB user name to connect as normal user to the database
   */
  public final String getFirebirdUser() {
    return firebirdUser.toUpperCase();
  }

  // H2 Database Engine-------------------------------------------------------

  /**
   * @return the H2 Database Engine port number where the database server is listening for requests
   */
  public final int getH2ConnectionPort() {
    return h2ConnectionPort;
  }

  /**
   * @return the prefix of the H2 Database Engine connection string
   */
  public final String getH2ConnectionPrefix() {
    return h2ConnectionPrefix;
  }

  /**
   * @return the H2 Database Engine database name
   */
  public final String getH2Database() {
    return h2Database;
  }

  /**
   * @return the H2 Database Engine password to connect as normal user to the database
   */
  public final String getH2Password() {
    return h2Password;
  }

  /**
   * @return the H2 Database Engine schema
   */
  public final String getH2Schema() {
    return h2Schema.toUpperCase();
  }

  /**
   * @return the H2 Database Engine user name
   */
  public final String getH2User() {
    return h2User.toUpperCase();
  }

  // HyperSQL Database--------------------------------------------------------

  /**
   * @return the HyperSQL Database port number where the database server is listening for requests
   */
  public final int getHSQLDBConnectionPort() {
    return hsqldbConnectionPort;
  }

  /**
   * @return the prefix of the HyperSQL Database connection string
   */
  public final String getHSQLDBConnectionPrefix() {
    return hsqldbConnectionPrefix;
  }

  /**
   * @return the HyperSQL Database database name
   */
  public final String getHSQLDBDatabase() {
    return hsqldbDatabase;
  }

  /**
   * @return the HyperSQL Database password to connect as normal user to the database
   */
  public final String getHSQLDBPassword() {
    return hsqldbPassword;
  }

  /**
   * @return the HyperSQL Database schema
   */
  public final String getHSQLDBSchema() {
    return hsqldbSchema.toUpperCase();
  }

  /**
   * @return the HyperSQL Database user name
   */
  public final String getHSQLDBUser() {
    return hsqldbUser.toUpperCase();
  }

  // IBM Db2 Database --------------------------------------------------------

  /**
   * @return the IBM Db2 port number where the database server is listening for requests
   */
  public final int getIbmdb2ConnectionPort() {
    return ibmdb2ConnectionPort;
  }

  /**
   * @return the prefix of the IBM Db2 connection string
   */
  public final String getIbmdb2ConnectionPrefix() {
    return ibmdb2ConnectionPrefix;
  }

  /**
   * @return the IBM Db2 Database name
   */
  public final String getIbmdb2Database() {
    return ibmdb2Database;
  }

  /**
   * @return the IBM Db2 password to connect to the database
   */
  public final String getIbmdb2Password() {
    return ibmdb2Password;
  }

  /**
   * @return the IBM Db2 schema name
   */
  public final String getIbmdb2Schema() {
    return ibmdb2Schema.toUpperCase();
  }

  // JDBC Connection ---------------------------------------------------------

  /**
   * @return the host name or the IP address of the database
   */
  public final String getJdbcConnectionHost() {
    return jdbcConnectionHost;
  }

  private final ArrayList<String> getKeysSorted() {

    for (final Iterator<String> iterator = propertiesConfiguration.getKeys(); iterator.hasNext();) {
      keysSorted.add(iterator.next());
    }

    Collections.sort(keysSorted);

    return keysSorted;
  }

  // MariaDB Server ----------------------------------------------------------

  /**
   * @return the MariaDB port number where the database server is listening for requests
   */
  public final int getMariadbConnectionPort() {
    return mariadbConnectionPort;
  }

  /**
   * @return the prefix of the MariaDB connection string
   */
  public final String getMariadbConnectionPrefix() {
    return mariadbConnectionPrefix;
  }

  /**
   * @return the MariaDB database name
   */
  public final String getMariadbDatabase() {
    return mariadbDatabase;
  }

  /**
   * @return the MariaDB password to connect as normal user to the database
   */
  public final String getMariadbPassword() {
    return mariadbPassword;
  }

  /**
   * @return the MariaDB password to connect as privileged user to the database
   */
  public final String getMariadbPasswordSys() {
    return mariadbPasswordSys;
  }

  /**
   * @return the MariaDB user name to connect as normal user to the database
   */
  public final String getMariadbUser() {
    return mariadbUser;
  }

  // MAX (rows) --------------------------------------------------------------

  /**
   * @return the maximum number of rows to be generated for database table CITY
   */
  public final int getMaxRowCity() {
    return maxRowCity;
  }

  /**
   * @return the maximum number of rows to be generated for database table COMPANY
   */
  public final int getMaxRowCompany() {
    return maxRowCompany;
  }

  /**
   * @return the maximum number of rows to be generated for database table COUNTRY
   */
  public final int getMaxRowCountry() {
    return maxRowCountry;
  }

  /**
   * @return the maximum number of rows to be generated for database table COUNTRY_STATE
   */
  public final int getMaxRowCountryState() {
    return maxRowCountryState;
  }

  /**
   * @return the maximum number of rows to be generated for database table COUNTRY
   */
  public final int getMaxRowTimezone() {
    return maxRowTimezone;
  }

  // Microsoft SQL Server ----------------------------------------------------

  /**
   * @return the Microsoft SQL Server port number where the database server is listening for requests
   */
  public final int getMssqlserverConnectionPort() {
    return mssqlserverConnectionPort;
  }

  /**
   * @return the prefix of the Microsoft SQL Server connection string
   */
  public final String getMssqlserverConnectionPrefix() {
    return mssqlserverConnectionPrefix;
  }

  /**
   * @return the Microsoft SQL Server database name
   */
  public final String getMssqlserverDatabase() {
    return mssqlserverDatabase;
  }

  /**
   * @return the Microsoft SQL Server password to connect as normal user to the database
   */
  public final String getMssqlserverPassword() {
    return mssqlserverPassword;
  }

  /**
   * @return the Microsoft SQL Server password to connect as privileged user to the database
   */
  public final String getMssqlserverPasswordSys() {
    return mssqlserverPasswordSys;
  }

  /**
   * @return the Microsoft SQL Server schema name
   */
  public final String getMssqlserverSchema() {
    return mssqlserverSchema;
  }

  /**
   * @return the Microsoft SQL Server user name to connect as normal user to the database
   */
  public final String getMssqlserverUser() {
    return mssqlserverUser;
  }

  // MySQL Database ----------------------------------------------------------

  /**
   * @return the MySQL port number where the database server is listening for requests
   */
  public final int getMysqlConnectionPort() {
    return mysqlConnectionPort;
  }

  /**
   * @return the prefix of the MySQL connection string
   */
  public final String getMysqlConnectionPrefix() {
    return mysqlConnectionPrefix;
  }

  /**
   * @return the suffix of the MySQL connection string
   */
  public final String getMysqlConnectionSuffix() {
    return mysqlConnectionSuffix;
  }

  /**
   * @return the MySQL database name
   */
  public final String getMysqlDatabase() {
    return mysqlDatabase;
  }

  /**
   * @return the MySQL password to connect as normal user to the database
   */
  public final String getMysqlPassword() {
    return mysqlPassword;
  }

  /**
   * @return the MySQL password to connect as privileged user to the database
   */
  public final String getMysqlPasswordSys() {
    return mysqlPasswordSys;
  }

  /**
   * @return the MySQL user name to connect as normal user to the database
   */
  public final String getMysqlUser() {
    return mysqlUser;
  }

  @SuppressWarnings("unused")
  private final List<String> getNumericProperties() {

    List<String> list = new ArrayList<>();

    list.add("db_seeder.derby.connection.port");
    list.add("db_seeder.cratedb.connection.port");
    list.add("db_seeder.cubrid.connection.port");
    list.add("db_seeder.firebird.connection.port");
    list.add("db_seeder.h2.connection.port");
    list.add("db_seeder.hsqldb.connection.port");
    list.add("db_seeder.ibmdb2.connection.port");
    list.add("db_seeder.mariadb.connection.port");
    list.add("db_seeder.max.row.city");
    list.add("db_seeder.max.row.company");
    list.add("db_seeder.max.row.country");
    list.add("db_seeder.max.row.country_state");
    list.add("db_seeder.max.row.timezone");
    list.add("db_seeder.mssqlserver.connection.port");
    list.add("db_seeder.mysql.connection.port");
    list.add("db_seeder.oracle.connection.port");
    list.add("db_seeder.postgresql.connection.port");

    return list;
  }

  // Oracle Database ---------------------------------------------------------

  /**
   * @return the Oracle port number where the database server is listening for requests
   */
  public final int getOracleConnectionPort() {
    return oracleConnectionPort;
  }

  /**
   * @return the prefix of the Oracle connection string
   */
  public final String getOracleConnectionPrefix() {
    return oracleConnectionPrefix;
  }

  /**
   * @return the Oracle service name to connect to the database
   */
  public final String getOracleConnectionService() {
    return oracleConnectionService;
  }

  /**
   * @return the Oracle password to connect as normal user to the database
   */
  public final String getOraclePassword() {
    return oraclePassword;
  }

  /**
   * @return the Oracle password to connect as privileged user to the database
   */
  public final String getOraclePasswordSys() {
    return oraclePasswordSys;
  }

  /**
   * @return the Oracle user name to connect as normal user to the database
   */
  public final String getOracleUser() {
    return oracleUser.toUpperCase();
  }

  // PostgreSQL Database -----------------------------------------------------

  /**
   * @return the PostgreSQL Database port number where the database server is listening for requests
   */
  public final int getPostgresqlConnectionPort() {
    return postgresqlConnectionPort;
  }

  /**
   * @return the prefix of the PostgreSQL Database connection string
   */
  public final String getPostgresqlConnectionPrefix() {
    return postgresqlConnectionPrefix;
  }

  /**
   * @return the PostgreSQL Database database name
   */
  public final String getPostgresqlDatabase() {
    return postgresqlDatabase;
  }

  /**
   * @return the PostgreSQL Database password to connect as normal user to the database
   */
  public final String getPostgresqlPassword() {
    return postgresqlPassword;
  }

  /**
   * @return the PostgreSQL Database password to connect as privileged user to the database
   */
  public final String getPostgresqlPasswordSys() {
    return postgresqlPasswordSys;
  }

  /**
   * @return the PostgreSQL Database user name to connect as normal user to the database
   */
  public final String getPostgresqlUser() {
    return postgresqlUser;
  }

  // SQLite ---------------------------------------------------------

  /**
   * @return the prefix of the SQLite connection string
   */
  public final String getSQLiteConnectionPrefix() {
    return sqliteConnectionPrefix;
  }

  /**
   * @return the SQLite database name
   */
  public final String getSQLiteDatabase() {
    return sqliteDatabase;
  }

  private final void storeConfiguration() {

    propertiesConfiguration.setThrowExceptionOnMissing(true);

    derbyConnectionPort         = propertiesConfiguration.getInt("db_seeder.derby.connection.port");
    derbyConnectionPrefix       = propertiesConfiguration.getString("db_seeder.derby.connection.prefix");
    derbyDatabase               = propertiesConfiguration.getString("db_seeder.derby.database");

    cratedbConnectionPort       = propertiesConfiguration.getInt("db_seeder.cratedb.connection.port");
    cratedbConnectionPrefix     = propertiesConfiguration.getString("db_seeder.cratedb.connection.prefix");
    cratedbPassword             = propertiesConfiguration.getString("db_seeder.cratedb.password");
    cratedbUser                 = propertiesConfiguration.getString("db_seeder.cratedb.user");

    cubridConnectionPort        = propertiesConfiguration.getInt("db_seeder.cubrid.connection.port");
    cubridConnectionPrefix      = propertiesConfiguration.getString("db_seeder.cubrid.connection.prefix");
    cubridDatabase              = propertiesConfiguration.getString("db_seeder.cubrid.database");
    cubridPassword              = propertiesConfiguration.getString("db_seeder.cubrid.password");
    cubridUser                  = propertiesConfiguration.getString("db_seeder.cubrid.user");

    encodingIso_8859_1          = propertiesConfiguration.getBoolean("db_seeder.encoding.iso_8859_1");
    encodingUtf_8               = propertiesConfiguration.getBoolean("db_seeder.encoding.utf_8");

    fileConfigurationName       = propertiesConfiguration.getString("db_seeder.file.configuration.name");

    firebirdConnectionPort      = propertiesConfiguration.getInt("db_seeder.firebird.connection.port");
    firebirdConnectionPrefix    = propertiesConfiguration.getString("db_seeder.firebird.connection.prefix");
    firebirdDatabase            = propertiesConfiguration.getString("db_seeder.firebird.database");
    firebirdPassword            = propertiesConfiguration.getString("db_seeder.firebird.password");
    firebirdPasswordSys         = propertiesConfiguration.getString("db_seeder.firebird.password.sys");
    firebirdUser                = propertiesConfiguration.getString("db_seeder.firebird.user");

    h2ConnectionPort            = propertiesConfiguration.getInt("db_seeder.h2.connection.port");
    h2ConnectionPrefix          = propertiesConfiguration.getString("db_seeder.h2.connection.prefix");
    h2Database                  = propertiesConfiguration.getString("db_seeder.h2.database");
    h2Password                  = propertiesConfiguration.getString("db_seeder.h2.password");
    h2Schema                    = propertiesConfiguration.getString("db_seeder.h2.schema");
    h2User                      = propertiesConfiguration.getString("db_seeder.h2.user");

    hsqldbConnectionPort        = propertiesConfiguration.getInt("db_seeder.hsqldb.connection.port");
    hsqldbConnectionPrefix      = propertiesConfiguration.getString("db_seeder.hsqldb.connection.prefix");
    hsqldbDatabase              = propertiesConfiguration.getString("db_seeder.hsqldb.database");
    hsqldbPassword              = propertiesConfiguration.getString("db_seeder.hsqldb.password");
    hsqldbSchema                = propertiesConfiguration.getString("db_seeder.hsqldb.schema");
    hsqldbUser                  = propertiesConfiguration.getString("db_seeder.hsqldb.user");

    ibmdb2ConnectionPort        = propertiesConfiguration.getInt("db_seeder.ibmdb2.connection.port");
    ibmdb2ConnectionPrefix      = propertiesConfiguration.getString("db_seeder.ibmdb2.connection.prefix");
    ibmdb2Database              = propertiesConfiguration.getString("db_seeder.ibmdb2.database");
    ibmdb2Password              = propertiesConfiguration.getString("db_seeder.ibmdb2.password");
    ibmdb2Schema                = propertiesConfiguration.getString("db_seeder.ibmdb2.schema");

    jdbcConnectionHost          = propertiesConfiguration.getString("db_seeder.jdbc.connection.host");

    mariadbConnectionPort       = propertiesConfiguration.getInt("db_seeder.mariadb.connection.port");
    mariadbConnectionPrefix     = propertiesConfiguration.getString("db_seeder.mariadb.connection.prefix");
    mariadbDatabase             = propertiesConfiguration.getString("db_seeder.mariadb.database");
    mariadbPassword             = propertiesConfiguration.getString("db_seeder.mariadb.password");
    mariadbPasswordSys          = propertiesConfiguration.getString("db_seeder.mariadb.password.sys");
    mariadbUser                 = propertiesConfiguration.getString("db_seeder.mariadb.user");

    maxRowCity                  = propertiesConfiguration.getInt("db_seeder.max.row.city");
    maxRowCompany               = propertiesConfiguration.getInt("db_seeder.max.row.company");
    maxRowCountry               = propertiesConfiguration.getInt("db_seeder.max.row.country");
    maxRowCountryState          = propertiesConfiguration.getInt("db_seeder.max.row.country_state");
    maxRowTimezone              = propertiesConfiguration.getInt("db_seeder.max.row.timezone");

    mssqlserverConnectionPort   = propertiesConfiguration.getInt("db_seeder.mssqlserver.connection.port");
    mssqlserverConnectionPrefix = propertiesConfiguration.getString("db_seeder.mssqlserver.connection.prefix");
    mssqlserverDatabase         = propertiesConfiguration.getString("db_seeder.mssqlserver.database");
    mssqlserverPassword         = propertiesConfiguration.getString("db_seeder.mssqlserver.password");
    mssqlserverPasswordSys      = propertiesConfiguration.getString("db_seeder.mssqlserver.password.sys");
    mssqlserverSchema           = propertiesConfiguration.getString("db_seeder.mssqlserver.schema");
    mssqlserverUser             = propertiesConfiguration.getString("db_seeder.mssqlserver.user");

    mysqlConnectionPort         = propertiesConfiguration.getInt("db_seeder.mysql.connection.port");
    mysqlConnectionPrefix       = propertiesConfiguration.getString("db_seeder.mysql.connection.prefix");
    mysqlConnectionSuffix       = propertiesConfiguration.getString("db_seeder.mysql.connection.suffix");
    mysqlDatabase               = propertiesConfiguration.getString("db_seeder.mysql.database");
    mysqlPassword               = propertiesConfiguration.getString("db_seeder.mysql.password");
    mysqlPasswordSys            = propertiesConfiguration.getString("db_seeder.mysql.password.sys");
    mysqlUser                   = propertiesConfiguration.getString("db_seeder.mysql.user");

    oracleConnectionPort        = propertiesConfiguration.getInt("db_seeder.oracle.connection.port");
    oracleConnectionPrefix      = propertiesConfiguration.getString("db_seeder.oracle.connection.prefix");
    oracleConnectionService     = propertiesConfiguration.getString("db_seeder.oracle.connection.service");
    oraclePassword              = propertiesConfiguration.getString("db_seeder.oracle.password");
    oraclePasswordSys           = propertiesConfiguration.getString("db_seeder.oracle.password.sys");
    oracleUser                  = propertiesConfiguration.getString("db_seeder.oracle.user");

    postgresqlConnectionPort    = propertiesConfiguration.getInt("db_seeder.postgresql.connection.port");
    postgresqlConnectionPrefix  = propertiesConfiguration.getString("db_seeder.postgresql.connection.prefix");
    postgresqlDatabase          = propertiesConfiguration.getString("db_seeder.postgresql.database");
    postgresqlPassword          = propertiesConfiguration.getString("db_seeder.postgresql.password");
    postgresqlPasswordSys       = propertiesConfiguration.getString("db_seeder.postgresql.password.sys");
    postgresqlUser              = propertiesConfiguration.getString("db_seeder.postgresql.user");

    sqliteConnectionPrefix      = propertiesConfiguration.getString("db_seeder.sqlite.connection.prefix");
    sqliteDatabase              = propertiesConfiguration.getString("db_seeder.sqlite.database");
  }

  private final void updatePropertiesFromOs() {

    Map<String, String> environmentVariables = System.getenv();

    // Apache Derby ------------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_DERBY_CONNECTION_PORT")) {
      derbyConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_DERBY_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", derbyConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_DERBY_CONNECTION_PREFIX")) {
      derbyConnectionPrefix = environmentVariables.get("DB_SEEDER_DERBY_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", derbyConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_DERBY_DATABASE")) {
      derbyDatabase = environmentVariables.get("DB_SEEDER_DERBY_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.derby.database", derbyDatabase);
    }

    // CrateDB ----------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_CRATEDB_CONNECTION_PORT")) {
      cratedbConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_CRATEDB_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", cratedbConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CRATEDB_CONNECTION_PREFIX")) {
      cratedbConnectionPrefix = environmentVariables.get("DB_SEEDER_CRATEDB_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", cratedbConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CRATEDB_PASSWORD")) {
      cratedbPassword = environmentVariables.get("DB_SEEDER_CRATEDB_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.cratedb.password", cratedbPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CRATEDB_USER")) {
      cratedbUser = environmentVariables.get("DB_SEEDER_CRATEDB_USER");
      propertiesConfiguration.setProperty("db_seeder.cratedb.user", cratedbUser);
    }

    if (environmentVariables.containsKey("DB_SEEDER_FILE_CONFIGURATION_NAME")) {
      fileConfigurationName = environmentVariables.get("DB_SEEDER_FILE_CONFIGURATION_NAME");
      propertiesConfiguration.setProperty("db_seeder.file.configuration.name", fileConfigurationName);
    }

    // CUBRID ------------------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_CUBRID_CONNECTION_PORT")) {
      cubridConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_CUBRID_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", cubridConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CUBRID_CONNECTION_PREFIX")) {
      cubridConnectionPrefix = environmentVariables.get("DB_SEEDER_CUBRID_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", cubridConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CUBRID_DATABASE")) {
      cubridDatabase = environmentVariables.get("DB_SEEDER_CUBRID_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.cubrid.database", cubridDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CUBRID_PASSWORD")) {
      cubridPassword = environmentVariables.get("DB_SEEDER_CUBRID_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.cubrid.password", cubridPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_CUBRID_USER")) {
      cubridUser = environmentVariables.get("DB_SEEDER_CUBRID_USER");
      propertiesConfiguration.setProperty("db_seeder.cubrid.user", cubridUser);
    }

    // Encoding ----------------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_ENCODING_ISO_8859_1")) {
      String encodingIso_8859_1Helper = environmentVariables.get("DB_SEEDER_ENCODING_ISO_8859_1");
      propertiesConfiguration.setProperty("db_seeder.encoding.is_8859_1", "true".equals(encodingIso_8859_1Helper.toLowerCase()) ? true : false);
    }

    if (environmentVariables.containsKey("DB_SEEDER_ENCODING_UTF_8")) {
      String encodingUtf_8Helper = environmentVariables.get("DB_SEEDER_ENCODING_UTF_8");
      propertiesConfiguration.setProperty("db_seeder.encoding.utf_8", "true".equals(encodingUtf_8Helper.toLowerCase()) ? true : false);
    }

    // Firebird ------------------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_FIREBIRD_CONNECTION_PORT")) {
      firebirdConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_FIREBIRD_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", firebirdConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_FIREBIRD_CONNECTION_PREFIX")) {
      firebirdConnectionPrefix = environmentVariables.get("DB_SEEDER_FIREBIRD_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", firebirdConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_FIREBIRD_DATABASE")) {
      firebirdDatabase = environmentVariables.get("DB_SEEDER_FIREBIRD_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.firebird.database", firebirdDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_FIREBIRD_PASSWORD")) {
      firebirdPassword = environmentVariables.get("DB_SEEDER_FIREBIRD_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.firebird.password", firebirdPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_FIREBIRD_PASSWORD_SYS")) {
      firebirdPasswordSys = environmentVariables.get("DB_SEEDER_FIREBIRD_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.firebird.password.sys", firebirdPasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_FIREBIRD_USER")) {
      firebirdUser = environmentVariables.get("DB_SEEDER_FIREBIRD_USER");
      propertiesConfiguration.setProperty("db_seeder.firebird.user", firebirdUser);
    }

    // H2 database Engine --------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_H2_CONNECTION_PORT")) {
      h2ConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_H2_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", h2ConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_H2_CONNECTION_PREFIX")) {
      h2ConnectionPrefix = environmentVariables.get("DB_SEEDER_H2_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", h2ConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_H2_DATABASE")) {
      h2Database = environmentVariables.get("DB_SEEDER_H2_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.h2.database", h2Database);
    }

    if (environmentVariables.containsKey("DB_SEEDER_H2_PASSWORD")) {
      h2Password = environmentVariables.get("DB_SEEDER_H2_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.h2.password", h2Password);
    }

    if (environmentVariables.containsKey("DB_SEEDER_H2_SCHEMA")) {
      h2Schema = environmentVariables.get("DB_SEEDER_H2_SCHEMA");
      propertiesConfiguration.setProperty("db_seeder.h2.password.sys", h2Schema);
    }

    if (environmentVariables.containsKey("DB_SEEDER_H2_USER")) {
      h2User = environmentVariables.get("DB_SEEDER_H2_USER");
      propertiesConfiguration.setProperty("db_seeder.h2.user", h2User);
    }

    // HyperSQL Database --------------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_HSQLDB_CONNECTION_PORT")) {
      hsqldbConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_HSQLDB_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", hsqldbConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_HSQLDB_CONNECTION_PREFIX")) {
      hsqldbConnectionPrefix = environmentVariables.get("DB_SEEDER_HSQLDB_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", hsqldbConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_HSQLDB_DATABASE")) {
      hsqldbDatabase = environmentVariables.get("DB_SEEDER_HSQLDB_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.hsqldb.database", hsqldbDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_HSQLDB_PASSWORD")) {
      hsqldbPassword = environmentVariables.get("DB_SEEDER_HSQLDB_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.hsqldb.password", hsqldbPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_HSQLDB_SCHEMA")) {
      hsqldbSchema = environmentVariables.get("DB_SEEDER_HSQLDB_SCHEMA");
      propertiesConfiguration.setProperty("db_seeder.hsqldb.password.sys", hsqldbSchema);
    }

    if (environmentVariables.containsKey("DB_SEEDER_HSQLDB_USER")) {
      hsqldbUser = environmentVariables.get("DB_SEEDER_HSQLDB_USER");
      propertiesConfiguration.setProperty("db_seeder.hsqldb.user", hsqldbUser);
    }

    // IBM Db2 Database ----------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_CONNECTION_PORT")) {
      ibmdb2ConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_IBMDB2_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", ibmdb2ConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_CONNECTION_PREFIX")) {
      ibmdb2ConnectionPrefix = environmentVariables.get("DB_SEEDER_IBMDB2_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", ibmdb2ConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_DATABASE")) {
      ibmdb2Database = environmentVariables.get("DB_SEEDER_IBMDB2_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.ibmdb2.database", ibmdb2Database);
    }

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_PASSWORD")) {
      ibmdb2Password = environmentVariables.get("DB_SEEDER_IBMDB2_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.ibmdb2.password", ibmdb2Password);
    }

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_SCHEMA")) {
      ibmdb2Schema = environmentVariables.get("DB_SEEDER_IBMDB2_SCHEMA");
      propertiesConfiguration.setProperty("db_seeder.ibmdb2.schema", ibmdb2Schema);
    }

    // JDBC Connection ---------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_JDBC_CONNECTION_HOST")) {
      jdbcConnectionHost = environmentVariables.get("DB_SEEDER_JDBC_CONNECTION_HOST");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.host", jdbcConnectionHost);
    }

    // MariaDB Server ----------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_MARIADB_CONNECTION_PORT")) {
      mariadbConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_MARIADB_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", mariadbConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MARIADB_CONNECTION_PREFIX")) {
      mariadbConnectionPrefix = environmentVariables.get("DB_SEEDER_MARIADB_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", mariadbConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MARIADB_DATABASE")) {
      mariadbDatabase = environmentVariables.get("DB_SEEDER_MARIADB_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.mariadb.database", mariadbDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MARIADB_PASSWORD")) {
      mariadbPassword = environmentVariables.get("DB_SEEDER_MARIADB_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.mariadb.password", mariadbPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MARIADB_PASSWORD_SYS")) {
      mariadbPasswordSys = environmentVariables.get("DB_SEEDER_MARIADB_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.mariadb.password.sys", mariadbPasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MARIADB_USER")) {
      mariadbUser = environmentVariables.get("DB_SEEDER_MARIADB_USER");
      propertiesConfiguration.setProperty("db_seeder.mariadb.user", mariadbUser);
    }

    // MAX (rows) --------------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_MAX_ROW_CITY")) {
      maxRowCity = Integer.parseInt(environmentVariables.get("DB_SEEDER_MAX_ROW_CITY"));
      propertiesConfiguration.setProperty("db_seeder.max.row.city", maxRowCity);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MAX_ROW_COMPANY")) {
      maxRowCompany = Integer.parseInt(environmentVariables.get("DB_SEEDER_MAX_ROW_COMPANY"));
      propertiesConfiguration.setProperty("db_seeder.max.row.company", maxRowCompany);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MAX_ROW_COUNTRY")) {
      maxRowCountry = Integer.parseInt(environmentVariables.get("DB_SEEDER_MAX_ROW_COUNTRY"));
      propertiesConfiguration.setProperty("db_seeder.max.row.country", maxRowCountry);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MAX_ROW_COUNTRY_STATE")) {
      maxRowCountryState = Integer.parseInt(environmentVariables.get("DB_SEEDER_MAX_ROW_COUNTRY_STATE"));
      propertiesConfiguration.setProperty("db_seeder.max.row.country_state", maxRowCountryState);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MAX_ROW_TIMEZONE")) {
      maxRowTimezone = Integer.parseInt(environmentVariables.get("DB_SEEDER_MAX_ROW_TIMEZONE"));
      propertiesConfiguration.setProperty("db_seeder.max.row.timezone", maxRowTimezone);
    }

    // Microsoft SQL Server ----------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_CONNECTION_PORT")) {
      mssqlserverConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_MSSQLSERVER_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", mssqlserverConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_CONNECTION_PREFIX")) {
      mssqlserverConnectionPrefix = environmentVariables.get("DB_SEEDER_MSSQLSERVER_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", mssqlserverConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_DATABASE")) {
      mssqlserverDatabase = environmentVariables.get("DB_SEEDER_MSSQLSERVER_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.mssqlserver.database", mssqlserverDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_PASSWORD")) {
      mssqlserverPassword = environmentVariables.get("DB_SEEDER_MSSQLSERVER_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.mssqlserver.password", mssqlserverPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_PASSWORD_SYS")) {
      mssqlserverPasswordSys = environmentVariables.get("DB_SEEDER_MSSQLSERVER_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.mssqlserver.password.sys", mssqlserverPasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_SCHEMA")) {
      mssqlserverSchema = environmentVariables.get("DB_SEEDER_MSSQLSERVER_SCHEMA");
      propertiesConfiguration.setProperty("db_seeder.mssqlserver.schema", mssqlserverSchema);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MSSQLSERVER_USER")) {
      mssqlserverUser = environmentVariables.get("DB_SEEDER_MSSQLSERVER_USER");
      propertiesConfiguration.setProperty("db_seeder.mssqlserver.user", mssqlserverUser);
    }

    // MySQL Database ----------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_CONNECTION_PORT")) {
      mysqlConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_MYSQL_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", mysqlConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_CONNECTION_PREFIX")) {
      mysqlConnectionPrefix = environmentVariables.get("DB_SEEDER_MYSQL_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", mysqlConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_CONNECTION_SUFFIX")) {
      mysqlConnectionSuffix = environmentVariables.get("DB_SEEDER_MYSQL_CONNECTION_SUFFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.suffix", mysqlConnectionSuffix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_DATABASE")) {
      mysqlDatabase = environmentVariables.get("DB_SEEDER_MYSQL_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.mysql.database", mysqlDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_PASSWORD")) {
      mysqlPassword = environmentVariables.get("DB_SEEDER_MYSQL_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.mysql.password", mysqlPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_PASSWORD_SYS")) {
      mysqlPasswordSys = environmentVariables.get("DB_SEEDER_MYSQL_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.mysql.password.sys", mysqlPasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_MYSQL_USER")) {
      mysqlUser = environmentVariables.get("DB_SEEDER_MYSQL_USER");
      propertiesConfiguration.setProperty("db_seeder.mysql.user", mysqlUser);
    }

    // Oracle Database ---------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_ORACLE_CONNECTION_PORT")) {
      oracleConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_ORACLE_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", oracleConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_ORACLE_CONNECTION_PREFIX")) {
      oracleConnectionPrefix = environmentVariables.get("DB_SEEDER_ORACLE_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", oracleConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_ORACLE_CONNECTION_SERVICE")) {
      oracleConnectionService = environmentVariables.get("DB_SEEDER_ORACLE_CONNECTION_SERVICE");
      propertiesConfiguration.setProperty("db_seeder.oracle.connection.service", oracleConnectionService);
    }

    if (environmentVariables.containsKey("DB_SEEDER_ORACLE_PASSWORD")) {
      oraclePassword = environmentVariables.get("DB_SEEDER_ORACLE_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.oracle.password", oraclePassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_ORACLE_PASSWORD_SYS")) {
      oraclePasswordSys = environmentVariables.get("DB_SEEDER_ORACLE_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.oracle.password.sys", oraclePasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_ORACLE_USER")) {
      oracleUser = environmentVariables.get("DB_SEEDER_ORACLE_USER");
      propertiesConfiguration.setProperty("db_seeder.oracle.user", oracleUser);
    }

    // PostgreSQL Database -----------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_POSTGRESQL_CONNECTION_PORT")) {
      postgresqlConnectionPort = Integer.parseInt(environmentVariables.get("DB_SEEDER_POSTGRESQL_CONNECTION_PORT"));
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.port", postgresqlConnectionPort);
    }

    if (environmentVariables.containsKey("DB_SEEDER_POSTGRESQL_CONNECTION_PREFIX")) {
      postgresqlConnectionPrefix = environmentVariables.get("DB_SEEDER_POSTGRESQL_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", postgresqlConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_POSTGRESQL_DATABASE")) {
      postgresqlDatabase = environmentVariables.get("DB_SEEDER_POSTGRESQL_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.postgresql.database", postgresqlDatabase);
    }

    if (environmentVariables.containsKey("DB_SEEDER_POSTGRESQL_PASSWORD")) {
      postgresqlPassword = environmentVariables.get("DB_SEEDER_POSTGRESQL_PASSWORD");
      propertiesConfiguration.setProperty("db_seeder.postgresql.password", postgresqlPassword);
    }

    if (environmentVariables.containsKey("DB_SEEDER_POSTGRESQL_PASSWORD_SYS")) {
      postgresqlPasswordSys = environmentVariables.get("DB_SEEDER_POSTGRESQL_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.postgresql.password.sys", postgresqlPasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_POSTGRESQL_USER")) {
      postgresqlUser = environmentVariables.get("DB_SEEDER_POSTGRESQL_USER");
      propertiesConfiguration.setProperty("db_seeder.postgresql.user", postgresqlUser);
    }

    // SQLite ---------------------------------------------------------

    if (environmentVariables.containsKey("DB_SEEDER_SQLITE_CONNECTION_PREFIX")) {
      sqliteConnectionPrefix = environmentVariables.get("DB_SEEDER_SQLITE_CONNECTION_PREFIX");
      propertiesConfiguration.setProperty("db_seeder.jdbc.connection.prefix", sqliteConnectionPrefix);
    }

    if (environmentVariables.containsKey("DB_SEEDER_SQLITE_DATABASE")) {
      sqliteDatabase = environmentVariables.get("DB_SEEDER_SQLITE_DATABASE");
      propertiesConfiguration.setProperty("db_seeder.sqlite.database", sqliteDatabase);
    }
  }

  private final void validateProperties() {

    String methodName = new Object() {
    }.getClass().getEnclosingMethod().getName();

    logger.debug(String.format(AbstractDatabaseSeeder.FORMAT_METHOD_NAME, methodName) + "- Start");

    boolean isChanged = false;

    //    if (benchmarkBatchSize < 0) {
    //      logger.error(String.format(FORMAT_METHOD_NAME, methodName) 
    //          + "Attention: The value of the configuration parameter 'benchmark.batch.size' ["
    //          + benchmarkBatchSize + "] must not be less than 0, the specified value is replaced by 0.");
    //      benchmarkBatchSize = 0;
    //      propertiesConfiguration.setProperty("benchmark.batch.size", benchmarkBatchSize);
    //      isChanged = true;
    //    }

    if (isChanged) {
      try {
        fileBasedConfigurationBuilder.save();
        propertiesConfiguration = fileBasedConfigurationBuilder.getConfiguration();
      } catch (ConfigurationException e) {
        e.printStackTrace();
      }
      logger.debug(String.format(AbstractDatabaseSeeder.FORMAT_METHOD_NAME, methodName) + "- End");
    }
  }
}

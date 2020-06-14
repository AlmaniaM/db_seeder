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
 * The configuration parameters for the supported database brands. The 
 * configuration parameters are made available to the configuration object
 * in a text file.
 * 
 * The parameter name and parameter value must be separated by an equal sign
 * (=).
 */
public class Config {

  @SuppressWarnings("unused")
  private static Logger                                                logger     = Logger.getLogger(Config.class);
  private final FileBasedConfigurationBuilder<PropertiesConfiguration> fileBasedConfigurationBuilder;
  private String                                                       fileConfigurationName;

  @SuppressWarnings("unused")
  private final DateTimeFormatter                                      formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn");

  private int                                                          ibmdb2ConnectionPort;
  private String                                                       ibmdb2ConnectionPrefix;
  private String                                                       ibmdb2Database;
  private String                                                       ibmdb2Password;
  private String                                                       ibmdb2PasswordSys;
  private String                                                       ibmdb2Schema;
  private String                                                       ibmdb2User;

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

  /**
   * Constructs a Configuration object.
   */
  public Config() {
    super();

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
  }

  // IBM DB2 Database --------------------------------------------------------

  /**
   * @return the IBM DB2 port number where the database server is listening for requests
   */
  public final int getIbmdb2ConnectionPort() {
    return ibmdb2ConnectionPort;
  }

  /**
   * @return the prefix of the IBM DB2 connection string
   */
  public final String getIbmdb2ConnectionPrefix() {
    return ibmdb2ConnectionPrefix;
  }

  /**
   * @return the IBM DB2 database name
   */
  public final String getIbmdb2Database() {
    return ibmdb2Database;
  }

  /**
   * @return the IBM DB2 password to connect as normal user to the database
   */
  public final String getIbmdb2Password() {
    return ibmdb2Password;
  }

  /**
   * @return the IBM DB2 password to connect as privileged user to the database
   */
  public final String getIbmdb2PasswordSys() {
    return ibmdb2PasswordSys;
  }

  /**
   * @return the IBM DB2 schema name
   */
  public final String getIbmdb2Schema() {
    return ibmdb2Schema;
  }

  /**
   * @return the IBM DB2 user name to connect as normal user to the database
   */
  public final String getIbmdb2User() {
    return ibmdb2User;
  }

  /**
   * @return the host name or the IP address of the database
   */
  public final String getJdbcConnectionHost() {
    return jdbcConnectionHost;
  }

  private ArrayList<String> getKeysSorted() {

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
  private List<String> getNumericProperties() {

    List<String> list = new ArrayList<>();

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

  private void storeConfiguration() {

    propertiesConfiguration.setThrowExceptionOnMissing(true);

    fileConfigurationName       = propertiesConfiguration.getString("db_seeder.file.configuration.name");

    ibmdb2ConnectionPort        = propertiesConfiguration.getInt("db_seeder.ibmdb2.connection.port");
    ibmdb2ConnectionPrefix      = propertiesConfiguration.getString("db_seeder.ibmdb2.connection.prefix");
    ibmdb2Database              = propertiesConfiguration.getString("db_seeder.ibmdb2.database");
    ibmdb2Password              = propertiesConfiguration.getString("db_seeder.ibmdb2.password");
    ibmdb2PasswordSys           = propertiesConfiguration.getString("db_seeder.ibmdb2.password.sys");
    ibmdb2Schema                = propertiesConfiguration.getString("db_seeder.ibmdb2.schema");
    ibmdb2User                  = propertiesConfiguration.getString("db_seeder.ibmdb2.user");

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
  }

  private void updatePropertiesFromOs() {

    Map<String, String> environmentVariables = System.getenv();

    if (environmentVariables.containsKey("DB_SEEDER_FILE_CONFIGURATION_NAME")) {
      fileConfigurationName = environmentVariables.get("DB_SEEDER_FILE_CONFIGURATION_NAME");
      propertiesConfiguration.setProperty("db_seeder.file.configuration.name", fileConfigurationName);
    }

    // IBM DB2 Database ----------------------------------------------------------

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

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_PASSWORD_SYS")) {
      ibmdb2PasswordSys = environmentVariables.get("DB_SEEDER_IBMDB2_PASSWORD_SYS");
      propertiesConfiguration.setProperty("db_seeder.ibmdb2.password.sys", ibmdb2PasswordSys);
    }

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_SCHEMA")) {
      ibmdb2Schema = environmentVariables.get("DB_SEEDER_IBMDB2_SCHEMA");
      propertiesConfiguration.setProperty("db_seeder.ibmdb2.database", ibmdb2Schema);
    }

    if (environmentVariables.containsKey("DB_SEEDER_IBMDB2_USER")) {
      ibmdb2User = environmentVariables.get("DB_SEEDER_IBMDB2_USER");
      propertiesConfiguration.setProperty("db_seeder.ibmdb2.user", ibmdb2User);
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
  }

  private void validateProperties() {

    boolean isChanged = false;

    //    if (benchmarkBatchSize < 0) {
    //      logger.error("Attention: The value of the configuration parameter 'benchmark.batch.size' ["
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

    }
  }
}

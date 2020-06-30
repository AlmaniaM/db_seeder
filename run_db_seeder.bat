@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder.bat: Creation of dummy data in an empty database schema / user.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

set DB_SEEDER_DBMS_DEFAULT=sqlite
set DB_SEEDER_DBMS_EMBEDDED=no

if ["%1"] EQU [""] (
    echo ===========================================
    echo derby       - Apache Derby [client]
    echo derby_emb   - Apache Derby [embedded]
    echo cratedb     - CrateDB
    echo cubrid      - CUBRID
    echo firebird    - Firebird
    echo h2          - H2 Database Engine [client]
    echo h2_emb      - H2 Database Engine [embedded]
    echo ibmdb2      - IBM Db2 Database
    echo mariadb     - MariaDB Server
    echo mssqlserver - Microsoft SQL Server
    echo mysql       - MySQL
    echo oracle      - Oracle Database
    echo postgresql  - PostgreSQL Database
    echo sqlite      - SQLite [embedded]
    echo -------------------------------------------
    set /P DB_SEEDER_DBMS="Enter the desired database management system [default: %DB_SEEDER_DBMS_DEFAULT%] "

    if ["!DB_SEEDER_DBMS!"] EQU [""] (
        set DB_SEEDER_DBMS=%DB_SEEDER_DBMS_DEFAULT%
    )
) else (
    set DB_SEEDER_DBMS=%1
)

set DB_SEEDER_ENCODING_ISO_8859_1=
set DB_SEEDER_ENCODING_UTF_8=

set DB_SEEDER_FILE_CONFIGURATION_NAME=src\main\resources\db_seeder.properties

set DB_SEEDER_JAVA_CLASSPATH=%CLASSPATH%;lib/*

set DB_SEEDER_JDBC_CONNECTION_HOST=

set DB_SEEDER_MAX_ROW_CITY=
set DB_SEEDER_MAX_ROW_COMPANY=
set DB_SEEDER_MAX_ROW_COUNTRY=
set DB_SEEDER_MAX_ROW_COUNTRY_STATE=
set DB_SEEDER_MAX_ROW_TIMEZONE=

if ["%DB_SEEDER_DBMS%"] EQU ["cratedb"] (
    set DB_SEEDER_CRATEDB_CONNECTION_PORT=
    set DB_SEEDER_CRATEDB_CONNECTION_PREFIX=
    set DB_SEEDER_CRATEDB_PASSWORD=
    set DB_SEEDER_CRATEDB_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["cubrid"] (
    set DB_SEEDER_ENCODING_UTF_8=false
    set DB_SEEDER_CUBRID_CONNECTION_PORT=
    set DB_SEEDER_CUBRID_CONNECTION_PREFIX=
    set DB_SEEDER_CUBRID_DATABASE=
    set DB_SEEDER_CUBRID_PASSWORD=
    set DB_SEEDER_CUBRID_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["derby"] (
    set DB_SEEDER_DERBY_CONNECTION_PORT=
    set DB_SEEDER_DERBY_CONNECTION_PREFIX=
    set DB_SEEDER_DERBY_DATABASE=
)
if ["%DB_SEEDER_DBMS%"] EQU ["derby_emb"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
    set DB_SEEDER_DERBY_CONNECTION_PREFIX=
    set DB_SEEDER_DERBY_DATABASE=
)
if ["%DB_SEEDER_DBMS%"] EQU ["firebird"] (
    set DB_SEEDER_FIREBIRD_CONNECTION_PORT=
    set DB_SEEDER_FIREBIRD_CONNECTION_PREFIX=
    set DB_SEEDER_FIREBIRD_DATABASE=
    set DB_SEEDER_FIREBIRD_PASSWORD=
    set DB_SEEDER_FIREBIRD_PASSWORD_SYS=
    set DB_SEEDER_FIREBIRD_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["h2"] (
    set DB_SEEDER_H2_CONNECTION_PORT=
    set DB_SEEDER_H2_CONNECTION_PREFIX=
    set DB_SEEDER_H2_DATABASE=
    set DB_SEEDER_H2_PASSWORD=
    set DB_SEEDER_H2_SCHEMA=
    set DB_SEEDER_H2_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["h2_emb"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
    set DB_SEEDER_H2_CONNECTION_PREFIX=
    set DB_SEEDER_H2_DATABASE=
    set DB_SEEDER_H2_PASSWORD=
    set DB_SEEDER_H2_SCHEMA=
    set DB_SEEDER_H2_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["ibmdb2"] (
    set DB_SEEDER_IBMDB2_CONNECTION_PORT=
    set DB_SEEDER_IBMDB2_CONNECTION_PREFIX=
    set DB_SEEDER_IBMDB2_DATABASE=
    set DB_SEEDER_IBMDB2_PASSWORD=
    set DB_SEEDER_IBMDB2_SCHEMA=
)
if ["%DB_SEEDER_DBMS%"] EQU ["mariadb"] (
    set DB_SEEDER_MARIADB_CONNECTION_PORT=
    set DB_SEEDER_MARIADB_CONNECTION_PREFIX=
    set DB_SEEDER_MARIADB_DATABASE=
    set DB_SEEDER_MARIADB_PASSWORD=
    set DB_SEEDER_MARIADB_PASSWORD_SYS=
    set DB_SEEDER_MARIADB_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["mssqlserver"] (
    set DB_SEEDER_ENCODING_UTF_8=false
    set DB_SEEDER_MSSQLSERVER_CONNECTION_PORT=
    set DB_SEEDER_MSSQLSERVER_CONNECTION_PREFIX=
    set DB_SEEDER_MSSQLSERVER_DATABASE=
    set DB_SEEDER_MSSQLSERVER_PASSWORD=
    set DB_SEEDER_MSSQLSERVER_PASSWORD_SYS=
    set DB_SEEDER_MSSQLSERVER_SCHEMA=
    set DB_SEEDER_MSSQLSERVER_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["mysql"] (
    set DB_SEEDER_MYSQL_CONNECTION_PORT=
    set DB_SEEDER_MYSQL_CONNECTION_PREFIX=
    set DB_SEEDER_MYSQL_CONNECTION_SUFFIX=
    set DB_SEEDER_MYSQL_DATABASE=
    set DB_SEEDER_MYSQL_PASSWORD=
    set DB_SEEDER_MYSQL_PASSWORD_SYS=
    set DB_SEEDER_MYSQL_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["oracle"] (
    set DB_SEEDER_ORACLE_CONNECTION_PORT=
    set DB_SEEDER_ORACLE_CONNECTION_PREFIX=
    set DB_SEEDER_ORACLE_CONNECTION_SERVICE=
    set DB_SEEDER_ORACLE_PASSWORD=
    set DB_SEEDER_ORACLE_PASSWORD_SYS=
    set DB_SEEDER_ORACLE_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["postgresql"] (
    set DB_SEEDER_POSTGRESQL_CONNECTION_PORT=
    set DB_SEEDER_POSTGRESQL_CONNECTION_PREFIX=
    set DB_SEEDER_POSTGRESQL_DATABASE=
    set DB_SEEDER_POSTGRESQL_PASSWORD=
    set DB_SEEDER_POSTGRESQL_PASSWORD_SYS=
    set DB_SEEDER_POSTGRESQL_USER=
)
if ["%DB_SEEDER_DBMS%"] EQU ["sqlite"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
    set DB_SEEDER_SQLITE_CONNECTION_PREFIX=
    set DB_SEEDER_SQLITE_DATABASE=
)

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - Creation of dummy data in an empty database schema / user.
echo --------------------------------------------------------------------------------
echo DBMS                            : %DB_SEEDER_DBMS%
echo DBMS_EMBEDDED                   : %DB_SEEDER_DBMS_EMBEDDED%
echo --------------------------------------------------------------------------------
echo FILE_CONFIGURATION_NAME         : %DB_SEEDER_FILE_CONFIGURATION_NAME%
echo JAVA_CLASSPATH                  : %DB_SEEDER_JAVA_CLASSPATH%
echo --------------------------------------------------------------------------------
echo CONNECTION_HOST                 : %DB_SEEDER_JDBC_CONNECTION_HOST%
echo --------------------------------------------------------------------------------
echo MAX_ROW_CITY                    : %DB_SEEDER_MAX_ROW_CITY%
echo MAX_ROW_COMPANY                 : %DB_SEEDER_MAX_ROW_COMPANY%
echo MAX_ROW_COUNTRY                 : %DB_SEEDER_MAX_ROW_COUNTRY%
echo MAX_ROW_COUNTRY_STATE           : %DB_SEEDER_MAX_ROW_COUNTRY_STATE%
echo MAX_ROW_TIMEZONE                : %DB_SEEDER_MAX_ROW_TIMEZONE%
echo --------------------------------------------------------------------------------
if ["%DB_SEEDER_DBMS%"] EQU ["cratedb"] (
    echo CRATEDB_CONNECTION_PORT         : %DB_SEEDER_CRATEDB_CONNECTION_PORT%
    echo CRATEDB_CONNECTION_PREFIX       : %DB_SEEDER_CRATEDB_CONNECTION_PREFIX%
    echo CRATEDB_PASSWORD                : %DB_SEEDER_CRATEDB_PASSWORD%
    echo CRATEDB_USER                    : %DB_SEEDER_CRATEDB_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["cubrid"] (
    echo CUBRID_CONNECTION_PORT          : %DB_SEEDER_CUBRID_CONNECTION_PORT%
    echo CUBRID_CONNECTION_PREFIX        : %DB_SEEDER_CUBRID_CONNECTION_PREFIX%
    echo CUBRID_DATABASE                 : %DB_SEEDER_CUBRID_DATABASE%
    echo CUBRID_PASSWORD                 : %DB_SEEDER_CUBRID_PASSWORD%
    echo CUBRID_USER                     : %DB_SEEDER_CUBRID_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["derby"] (
    echo DERBY_CONNECTION_PORT           : %DB_SEEDER_DERBY_CONNECTION_PORT%
    echo DERBY_CONNECTION_PREFIX         : %DB_SEEDER_DERBY_CONNECTION_PREFIX%
    echo DERBY_DATABASE                  : %DB_SEEDER_DERBY_DATABASE%
)
if ["%DB_SEEDER_DBMS%"] EQU ["derby_emb"] (
    echo DERBY_CONNECTION_PREFIX         : %DB_SEEDER_DERBY_CONNECTION_PREFIX%
    echo DERBY_DATABASE                  : %DB_SEEDER_DERBY_DATABASE%
)
if ["%DB_SEEDER_DBMS%"] EQU ["firebird"] (
    echo FIREBIRD_CONNECTION_PORT        : %DB_SEEDER_FIREBIRD_CONNECTION_PORT%
    echo FIREBIRD_CONNECTION_PREFIX      : %DB_SEEDER_FIREBIRD_CONNECTION_PREFIX%
    echo FIREBIRD_DATABASE               : %DB_SEEDER_FIREBIRD_DATABASE%
    echo FIREBIRD_PASSWORD               : %DB_SEEDER_FIREBIRD_PASSWORD%
    echo FIREBIRD_PASSWORD_SYS           : %DB_SEEDER_FIREBIRD_PASSWORD_SYS%
    echo FIREBIRD_USER                   : %DB_SEEDER_FIREBIRD_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["h2"] (
    echo H2_CONNECTION_PORT              : %DB_SEEDER_H2_CONNECTION_PORT%
    echo H2_CONNECTION_PREFIX            : %DB_SEEDER_H2_CONNECTION_PREFIX%
    echo H2_DATABASE                     : %DB_SEEDER_H2_DATABASE%
    echo H2_PASSWORD                     : %DB_SEEDER_H2_PASSWORD%
    echo H2_SCHEMA                       : %DB_SEEDER_H2_SCHEMA%
    echo H2_USER                         : %DB_SEEDER_H2_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["h2_emb"] (
    echo H2_CONNECTION_PREFIX            : %DB_SEEDER_H2_CONNECTION_PREFIX%
    echo H2_DATABASE                     : %DB_SEEDER_H2_DATABASE%
    echo H2_PASSWORD                     : %DB_SEEDER_H2_PASSWORD%
    echo H2_SCHEMA                       : %DB_SEEDER_H2_SCHEMA%
    echo H2_USER                         : %DB_SEEDER_H2_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["ibmdb2"] (
    echo IBMDB2_CONNECTION_PORT          : %DB_SEEDER_IBMDB2_CONNECTION_PORT%
    echo IBMDB2_CONNECTION_PREFIX        : %DB_SEEDER_IBMDB2_CONNECTION_PREFIX%
    echo IBMDB2_DATABASE                 : %DB_SEEDER_IBMDB2_DATABASE%
    echo IBMDB2_PASSWORD                 : %DB_SEEDER_IBMDB2_PASSWORD%
    echo IBMDB2_SCHEMA                   : %DB_SEEDER_IBMDB2_SCHEMA%
)
if ["%DB_SEEDER_DBMS%"] EQU ["mariadb"] (
    echo MARIADB_CONNECTION_PORT         : %DB_SEEDER_MARIADB_CONNECTION_PORT%
    echo MARIADB_CONNECTION_PREFIX       : %DB_SEEDER_MARIADB_CONNECTION_PREFIX%
    echo MARIADB_DATABASE                : %DB_SEEDER_MARIADB_DATABASE%
    echo MARIADB_PASSWORD                : %DB_SEEDER_MARIADB_PASSWORD%
    echo MARIADB_PASSWORD_SYS            : %DB_SEEDER_MARIADB_PASSWORD_SYS%
    echo MARIADB_USER                    : %DB_SEEDER_MARIADB_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["mssqlserver"] (
    echo MSSQLSERVER_CONNECTION_PORT     : %DB_SEEDER_MSSQLSERVER_CONNECTION_PORT%
    echo MSSQLSERVER_CONNECTION_PREFIX   : %DB_SEEDER_MSSQLSERVER_CONNECTION_PREFIX%
    echo MSSQLSERVER_DATABASE            : %DB_SEEDER_MSSQLSERVER_DATABASE%
    echo MSSQLSERVER_PASSWORD            : %DB_SEEDER_MSSQLSERVER_PASSWORD%
    echo MSSQLSERVER_PASSWORD_SYS        : %DB_SEEDER_MSSQLSERVER_PASSWORD_SYS%
    echo MSSQLSERVER_SCHEMA              : %DB_SEEDER_MSSQLSERVER_SCHEMA%
    echo MSSQLSERVER_USER                : %DB_SEEDER_MSSQLSERVER_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["mysql"] (
    echo MYSQL_CONNECTION_PORT           : %DB_SEEDER_MYSQL_CONNECTION_PORT%
    echo MYSQL_CONNECTION_PREFIX         : %DB_SEEDER_MYSQL_CONNECTION_PREFIX%
    echo MYSQL_CONNECTION_SUFFIX         : %DB_SEEDER_MYSQL_CONNECTION_SUFFIX%
    echo MYSQL_DATABASE                  : %DB_SEEDER_MYSQL_DATABASE%
    echo MYSQL_PASSWORD                  : %DB_SEEDER_MYSQL_PASSWORD%
    echo MYSQL_PASSWORD_SYS              : %DB_SEEDER_MYSQL_PASSWORD_SYS%
    echo MYSQL_USER                      : %DB_SEEDER_MYSQL_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["oracle"] (
    echo ORACLE_CONNECTION_PORT          : %DB_SEEDER_ORACLE_CONNECTION_PORT%
    echo ORACLE_CONNECTION_PREFIX        : %DB_SEEDER_ORACLE_CONNECTION_PREFIX%
    echo ORACLE_CONNECTION_SERVICE       : %DB_SEEDER_ORACLE_CONNECTION_SERVICE%
    echo ORACLE_PASSWORD                 : %DB_SEEDER_ORACLE_PASSWORD%
    echo ORACLE_PASSWORD_SYS             : %DB_SEEDER_ORACLE_PASSWORD_SYS%
    echo ORACLE_USER                     : %DB_SEEDER_ORACLE_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["postgresql"] (
    echo POSTGRESQL_CONNECTION_PORT      : %DB_SEEDER_POSTGRESQL_CONNECTION_PORT%
    echo POSTGRESQL_CONNECTION_PREFIX    : %DB_SEEDER_POSTGRESQL_CONNECTION_PREFIX%
    echo POSTGRESQL_DATABASE             : %DB_SEEDER_POSTGRESQL_DATABASE%
    echo POSTGRESQL_PASSWORD             : %DB_SEEDER_POSTGRESQL_PASSWORD%
    echo POSTGRESQL_PASSWORD_SYS         : %DB_SEEDER_POSTGRESQL_PASSWORD_SYS%
    echo POSTGRESQL_USER                 : %DB_SEEDER_POSTGRESQL_USER%
)
if ["%DB_SEEDER_DBMS%"] EQU ["sqlite"] (
    echo SQLITE_CONNECTION_PREFIX        : %DB_SEEDER_SQLITE_CONNECTION_PREFIX%
    echo SQLITE_DATABASE                 : %DB_SEEDER_SQLITE_DATABASE%
)

echo --------------------------------------------------------------------------------
echo:| TIME
echo ================================================================================

if exist db_seeder.log del /f /q db_seeder.log

java --enable-preview -cp %DB_SEEDER_JAVA_CLASSPATH% ch.konnexions.db_seeder.DatabaseSeeder %DB_SEEDER_DBMS%

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_presto_environment.bat: Creating a Presto environment.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

set DB_SEEDER_JAVA_CLASSPATH=%CLASSPATH%;lib/*

set DB_SEEDER_DIRECTORY_CATALOG_PROPERTY=docker/presto/catalog
set DB_SEEDER_GLOBAL_CONNECTION_HOST=192.168.1.109
set DB_SEEDER_RELEASE=2.1.0
set DB_SEEDER_VERSION_PRESTO=340

set DB_SEEDER_MSSQLSERVER_CONNECTION_HOST=%DB_SEEDER_GLOBAL_CONNECTION_HOST%
set DB_SEEDER_MSSQLSERVER_CONNECTION_PORT=1433
set DB_SEEDER_MSSQLSERVER_CONNECTION_PREFIX="jdbc:sqlserver://"
set DB_SEEDER_MSSQLSERVER_PASSWORD=mssqlserver_2019
set DB_SEEDER_MSSQLSERVER_USER=kxn_user

set DB_SEEDER_MYSQL_CONNECTION_HOST=%DB_SEEDER_GLOBAL_CONNECTION_HOST%
set DB_SEEDER_MYSQL_CONNECTION_PORT=3306
set DB_SEEDER_MYSQL_CONNECTION_PREFIX="jdbc:mysql://"
set DB_SEEDER_MYSQL_CONNECTION_SUFFIX="?serverTimezone=UTC&failOverReadOnly=false"
set DB_SEEDER_MYSQL_PASSWORD=mysql
set DB_SEEDER_MYSQL_USER=kxn_user

set DB_SEEDER_ORACLE_CONNECTION_HOST=%DB_SEEDER_GLOBAL_CONNECTION_HOST%
set DB_SEEDER_ORACLE_CONNECTION_PORT=1521
set DB_SEEDER_ORACLE_CONNECTION_PREFIX="jdbc:oracle:thin:@//"
set DB_SEEDER_ORACLE_CONNECTION_SERVICE=orclpdb1
set DB_SEEDER_ORACLE_PASSWORD=oracle
set DB_SEEDER_ORACLE_USER=kxn_user

set DB_SEEDER_POSTGRESQL_CONNECTION_HOST=%DB_SEEDER_GLOBAL_CONNECTION_HOST%
set DB_SEEDER_POSTGRESQL_CONNECTION_PORT=5432
set DB_SEEDER_POSTGRESQL_CONNECTION_PREFIX="jdbc:postgresql://"
set DB_SEEDER_POSTGRESQL_DATABASE=kxn_db
set DB_SEEDER_POSTGRESQL_PASSWORD=postgresql
set DB_SEEDER_POSTGRESQL_USER=kxn_user

set LOG_FILE=run_db_seeder_presto_environment.log

echo.
echo Script %0 is now running
echo.
echo You can find the run log in the file %LOG_FILE%
echo.
echo Please wait ...
echo.

> %LOG_FILE% 2>&1 (

    echo ================================================================================
    echo Start %0
    echo --------------------------------------------------------------------------------
    echo DB Seeder - Creating a Presto environment.
    echo --------------------------------------------------------------------------------
    echo DIRECTORY_CATALOG_PROPERTY    : %DB_SEEDER_DIRECTORY_CATALOG_PROPERTY%
    echo RELEASE                       : %DB_SEEDER_RELEASE%
    echo VERSION_PRESTO                : %DB_SEEDER_VERSION_PRESTO%
    echo --------------------------------------------------------------------------------
    echo CONNECTION_HOST_PRESTO        : %DB_SEEDER_CONNECTION_HOST_PRESTO%
    echo CONNECTION_PORT_PRESTO        : %DB_SEEDER_CONNECTION_PORT_PRESTO%
    echo --------------------------------------------------------------------------------
    echo MSSQLSERVER_CONNECTION_HOST   : %DB_SEEDER_MSSQLSERVER_CONNECTION_HOST%
    echo MSSQLSERVER_CONNECTION_PORT   : %DB_SEEDER_MSSQLSERVER_CONNECTION_PORT%
    echo MSSQLSERVER_CONNECTION_PREFIX : %DB_SEEDER_MSSQLSERVER_CONNECTION_PREFIX%
    echo MSSQLSERVER_PASSWORD          : %DB_SEEDER_MSSQLSERVER_PASSWORD%
    echo MSSQLSERVER_USER              : %DB_SEEDER_MSSQLSERVER_USER%
    echo --------------------------------------------------------------------------------
    echo MYSQL_CONNECTION_HOST         : %DB_SEEDER_MYSQL_CONNECTION_HOST%
    echo MYSQL_CONNECTION_PORT         : %DB_SEEDER_MYSQL_CONNECTION_PORT%
    echo MYSQL_CONNECTION_PREFIX       : %DB_SEEDER_MYSQL_CONNECTION_PREFIX%
    echo MYSQL_CONNECTION_SUFFIX       : %DB_SEEDER_MYSQL_CONNECTION_SUFFIX%
    echo MYSQL_PASSWORD                : %DB_SEEDER_MYSQL_PASSWORD%
    echo MYSQL_USER                    : %DB_SEEDER_MYSQL_USER%
    echo --------------------------------------------------------------------------------
    echo ORACLE_CONNECTION_HOST        : %DB_SEEDER_ORACLE_CONNECTION_HOST%
    echo ORACLE_CONNECTION_PORT        : %DB_SEEDER_ORACLE_CONNECTION_PORT%
    echo ORACLE_CONNECTION_PREFIX      : %DB_SEEDER_ORACLE_CONNECTION_PREFIX%
    echo ORACLE_CONNECTION_SERVICE     : %DB_SEEDER_ORACLE_CONNECTION_SERVICE%
    echo ORACLE_PASSWORD               : %DB_SEEDER_ORACLE_PASSWORD%
    echo ORACLE_USER                   : %DB_SEEDER_ORACLE_USER%
    echo --------------------------------------------------------------------------------
    echo POSTGRESQL_CONNECTION_HOST    : %DB_SEEDER_POSTGRESQL_CONNECTION_HOST%
    echo POSTGRESQL_CONNECTION_PORT    : %DB_SEEDER_POSTGRESQL_CONNECTION_PORT%
    echo POSTGRESQL_CONNECTION_PREFIX  : %DB_SEEDER_POSTGRESQL_CONNECTION_PREFIX%
    echo POSTGRESQL_DATABASE           : %DB_SEEDER_POSTGRESQL_DATABASE%
    echo POSTGRESQL_PASSWORD           : %DB_SEEDER_POSTGRESQL_PASSWORD%
    echo POSTGRESQL_USER               : %DB_SEEDER_POSTGRESQL_USER%
    echo --------------------------------------------------------------------------------
    echo JAVA_CLASSPATH                : %DB_SEEDER_JAVA_CLASSPATH%
    echo --------------------------------------------------------------------------------
    echo:| TIME
    echo ================================================================================
    echo Compile and generate catalog property files.
    echo --------------------------------------------------------------------------------

    java --enable-preview -cp %DB_SEEDER_JAVA_CLASSPATH% ch.konnexions.db_seeder.PrestoEnvironment mssqlserver mysql oracle postgresql
    if %ERRORLEVEL% NEQ 0 (
        exit %ERRORLEVEL%
    )

    echo --------------------------------------------------------------------------------
    echo Create Docker image.
    echo --------------------------------------------------------------------------------

    if exist tmp rmdir /Q/S tmp
    mkdir tmp

    xcopy /E /I docker tmp
    rename tmp\dockerfile_presto dockerfile

    docker build -t konnexionsgmbh/db_seeder_presto tmp
    
    docker tag konnexionsgmbh/db_seeder_presto konnexionsgmbh/db_seeder_presto:%DB_SEEDER_RELEASE%

    docker images -q -f "dangling=true" -f "label=autodelete=true"

    for /F %%I in ('docker images -q -f "dangling=true" -f "label=autodelete=true"') do (docker rmi -f %%I)

    echo --------------------------------------------------------------------------------
    echo:| TIME
    echo --------------------------------------------------------------------------------
    echo End   %0
    echo ================================================================================
)

@echo off

rem ------------------------------------------------------------------------------
rem
rem run_test_presto_postgresql.bat: Demonstration Issues PostgreSQL Connector.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - Demonstration Issues PostgreSQL Connector.
echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------

java --enable-preview -cp %CLASSPATH%;lib/* ch.konnexions.db_seeder.test.TestPrestoPostgresql
if %ERRORLEVEL% NEQ 0 (
    echo Processing of the script was aborted, error code=%ERRORLEVEL%
    exit %ERRORLEVEL%
)

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_complete_presto.bat: Run all Presto DBMS variations.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

set ERRORLEVEL=

set DB_SEEDER_COMPLETE_RUN=yes
set DB_SEEDER_NO_CREATE_RUNS_DEFAULT=2

if ["%1"] EQU [""] (
    set /P DB_SEEDER_NO_CREATE_RUNS="Number of data creation runs (0-2) [default: %DB_SEEDER_NO_CREATE_RUNS_DEFAULT%] "

    if ["!DB_SEEDER_NO_CREATE_RUNS!"] EQU [""] (
        set DB_SEEDER_NO_CREATE_RUNS=%DB_SEEDER_NO_CREATE_RUNS_DEFAULT%
    )
) else (
    set DB_SEEDER_NO_CREATE_RUNS=%1
)

set DB_SEEDER_DBMS_MYSQL_PRESTO=yes
set DB_SEEDER_DBMS_ORACLE_PRESTO=yes
set DB_SEEDER_DBMS_POSTGRESQL_PRESTO=yes
set DB_SEEDER_DBMS_SQLSERVER_PRESTO=yes

echo.
echo Script %0 is now running
echo.
echo You can find the run log in the file run_db_seeder_complete_presto.log
echo.
echo Please wait ...
echo.

> run_db_seeder_complete_presto.log 2>&1 (

    if ["%DB_SEEDER_FILE_STATISTICS_NAME%"] EQU [""] (
        set DB_SEEDER_FILE_STATISTICS_NAME=resources\statistics\db_seeder_cmd_presto_unknown_%DB_SEEDER_RELEASE%.tsv
    )

    if exist %DB_SEEDER_FILE_STATISTICS_NAME% del /f /q %DB_SEEDER_FILE_STATISTICS_NAME%
    
    echo ================================================================================
    echo Start %0
    echo --------------------------------------------------------------------------------
    echo DB Seeder - Run all DBMS variations.
    echo --------------------------------------------------------------------------------
    echo COMPLETE_RUN                    : %DB_SEEDER_COMPLETE_RUN%
    echo FILE_STATISTICS_NAME            : %DB_SEEDER_FILE_STATISTICS_NAME%
    echo NO_CREATE_RUNS                  : %DB_SEEDER_NO_CREATE_RUNS%
    echo --------------------------------------------------------------------------------
    echo DBMS_MYSQL_PRESTO               : %DB_SEEDER_DBMS_MYSQL_PRESTO%
    echo DBMS_ORACLE_PRESTO              : %DB_SEEDER_DBMS_ORACLE_PRESTO%
    echo DBMS_POSTGRESQL                 : %DB_SEEDER_DBMS_POSTGRESQL_PRESTO%
    echo DBMS_SQLSERVER                  : %DB_SEEDER_DBMS_SQLSERVER_PRESTO%
    echo --------------------------------------------------------------------------------
    echo:| TIME
    echo ================================================================================
    
    call scripts\run_db_seeder_generate_schema.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Processing of the script was aborted, error code=%ERRORLEVEL%
        exit %ERRORLEVEL%
    )
    
    call scripts\run_db_seeder_presto_environment.bat complete
    if %ERRORLEVEL% NEQ 0 (
        echo Processing of the script was aborted, error code=%ERRORLEVEL%
        exit %ERRORLEVEL%
    )

    call scripts\run_db_seeder_setup_presto.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Processing of the script was aborted, error code=%ERRORLEVEL%
        exit %ERRORLEVEL%
    )

    rem ------------------------------------------------------------------------------
    rem MySQL Database - via Presto.
    rem ------------------------------------------------------------------------------
    
    if ["%DB_SEEDER_DBMS_MYSQL_PRESTO%"] EQU ["yes"] (
        call run_db_seeder.bat mysql_presto yes %DB_SEEDER_NO_CREATE_RUNS%
        if %ERRORLEVEL% NEQ 0 (
            echo Processing of the script was aborted, error code=%ERRORLEVEL%
            exit %ERRORLEVEL%
        )
    )
    
    rem ------------------------------------------------------------------------------
    rem Oracle Database - via Presto.
    rem ------------------------------------------------------------------------------
    
    if ["%DB_SEEDER_DBMS_ORACLE_PRESTO%"] EQU ["yes"] (
        call run_db_seeder.bat oracle_presto yes %DB_SEEDER_NO_CREATE_RUNS%
        if %ERRORLEVEL% NEQ 0 (
            echo Processing of the script was aborted, error code=%ERRORLEVEL%
            exit %ERRORLEVEL%
        )
    )
    
    rem ------------------------------------------------------------------------------
    rem PostgreSQL Database.
    rem ------------------------------------------------------------------------------
    
    if ["%DB_SEEDER_DBMS_POSTGRESQL_PRESTO%"] EQU ["yes"] (
        call run_db_seeder.bat postgresql yes %DB_SEEDER_NO_CREATE_RUNS%
        if %ERRORLEVEL% NEQ 0 (
            echo Processing of the script was aborted, error code=%ERRORLEVEL%
            exit %ERRORLEVEL%
        )
    )
    
    rem ------------------------------------------------------------------------------
    rem Microsoft SQL Server.
    rem ------------------------------------------------------------------------------
    
    if ["%DB_SEEDER_DBMS_SQLSERVER_PRESTO%"] EQU ["yes"] (
        call run_db_seeder.bat sqlserver yes %DB_SEEDER_NO_CREATE_RUNS%
        if %ERRORLEVEL% NEQ 0 (
            echo Processing of the script was aborted, error code=%ERRORLEVEL%
            exit %ERRORLEVEL%
        )
    )
    
    echo --------------------------------------------------------------------------------
    echo:| TIME
    echo --------------------------------------------------------------------------------
    echo End   %0
    echo ================================================================================
)

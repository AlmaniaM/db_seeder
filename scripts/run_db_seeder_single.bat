@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_single.bat: Run a single DBMS variation.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

set DB_SEEDER_DBMS_EMBEDDED=no

if ["%DB_SEEDER_DBMS%"] EQU ["derby_emb"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
)

if ["%DB_SEEDER_DBMS%"] EQU ["h2_emb"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
)

if ["%DB_SEEDER_DBMS%"] EQU ["hsqldb_emb"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
)

if ["%DB_SEEDER_DBMS%"] EQU ["sqlite_emb"] (
    set DB_SEEDER_DBMS_EMBEDDED=yes
)

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - Run a single DBMS variation.
echo --------------------------------------------------------------------------------
echo DBMS                            : %DB_SEEDER_DBMS%
echo DBMS_EMBEDDED                   : %DB_SEEDER_DBMS_EMBEDDED%
echo NO_CREATE_RUNS                  : %DB_SEEDER_NO_CREATE_RUNS%
echo SETUP_DBMS                      : %DB_SEEDER_SETUP_DBMS%
echo --------------------------------------------------------------------------------
echo:| TIME
echo ================================================================================
    
if ["%DB_SEEDER_SETUP_DBMS%"] EQU ["yes"] (
    call scripts\run_db_seeder_setup_dbms.bat %DB_SEEDER_DBMS%
)
    
if ["%DB_SEEDER_NO_CREATE_RUNS%"] EQU ["1"] (
    call scripts\run_db_seeder_create_data.bat %DB_SEEDER_DBMS%
)
    
if ["%DB_SEEDER_NO_CREATE_RUNS%"] EQU ["2"] (
    call scripts\run_db_seeder_create_data.bat %DB_SEEDER_DBMS%
    call scripts\run_db_seeder_create_data.bat %DB_SEEDER_DBMS%
)

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

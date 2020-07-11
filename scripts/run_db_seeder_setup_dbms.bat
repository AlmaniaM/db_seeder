@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_setup_dbms.bat: Setup a database Docker container.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion


if ["%DB_SEEDER_DBMS_EMBEDDED%"] == ["no"] (
    echo Docker stop/rm db_seeder_db ................................ before:
    docker ps -a
    docker ps -qa --filter "name=db_seeder_db" | grep -q . && docker stop db_seeder_db && docker rm -fv db_seeder_db
    echo ............................................................. after:
    docker ps -a
)

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - setup a database Docker container.
echo --------------------------------------------------------------------------------
echo DBMS                      : %DB_SEEDER_DBMS%
echo DBMS_EMBEDDED             : %DB_SEEDER_DBMS_EMBEDDED%
   
call scripts\run_db_seeder_setup_files.bat %DB_SEEDER_DBMS%

if ["%DB_SEEDER_DBMS_EMBEDDED%"] EQU ["no"] (
    lib\Gammadyne\timer.exe /reset
    lib\Gammadyne\timer.exe /q
    
    call scripts\run_db_seeder_setup_%DB_SEEDER_DBMS%.bat
    
    docker ps
)

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

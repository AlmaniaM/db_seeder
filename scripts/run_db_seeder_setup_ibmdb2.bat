@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_setup_ibmdb2.bat: Setup a IBM Db2 Database Docker container.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

if ["%DB_SEEDER_CONNECTION_PORT%"] EQU [""] (
    set DB_SEEDER_CONNECTION_PORT=50000
)

if ["%DB_SEEDER_CONTAINER_PORT%"] EQU [""] (
    set DB_SEEDER_CONTAINER_PORT=50000
)

if ["%DB_SEEDER_VERSION%"] EQU [""] (
    docker ps    | grep "db_seeder_db" && docker stop db_seeder_db
    docker ps -a | grep "db_seeder_db" && docker rm db_seeder_db
    set DB_SEEDER_VERSION=latest
)

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - setup a IBM Db2 Database Docker container.
echo --------------------------------------------------------------------------------
echo DBMS_PRESTO               : %DB_SEEDER_DBMS_PRESTO%
echo DB_SEEDER_CONNECTION_PORT : %DB_SEEDER_CONNECTION_PORT%
echo DB_SEEDER_CONTAINER_PORT  : %DB_SEEDER_CONTAINER_PORT%
echo VERSION                   : %DB_SEEDER_VERSION%
echo --------------------------------------------------------------------------------
echo:| TIME
echo ================================================================================

rem ------------------------------------------------------------------------------
rem IBM Db2 Database                               https://hub.docker.com/_/ibmdb2
rem ------------------------------------------------------------------------------

echo IBM Db2 Database
echo --------------------------------------------------------------------------------
lib\Gammadyne\timer.exe
echo Docker create db_seeder_db (IBM Db2 Database %DB_SEEDER_VERSION%)
docker create --name db_seeder_db -e DBNAME=%DB_SEEDER_DATABASE% -e DB2INST1_PASSWORD=ibmdb2 -e LICENSE=accept -p %DB_SEEDER_CONNECTION_PORT%:%DB_SEEDER_CONTAINER_PORT% --privileged=true ibmcom/db2:%DB_SEEDER_VERSION%

echo Docker start db_seeder_db (IBM Db2 Database %DB_SEEDER_VERSION%)
docker start db_seeder_db
 
ping -n 300 127.0.0.1>nul

for /f "delims=" %%A in ('lib\Gammadyne\timer.exe /s') do set "CONSUMED=%%A"
echo DOCKER IBM Db2 Database was ready in %CONSUMED%

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

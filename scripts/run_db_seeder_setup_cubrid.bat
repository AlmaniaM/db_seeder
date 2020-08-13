@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_setup_cubrid.bat: Setup a CUBRID Docker container.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

if ["%DB_SEEDER_VERSION%"] EQU [""] (
    docker ps    | grep -r "db_seeder_db" && docker stop db_seeder_db
    docker ps -a | grep -r "db_seeder_presto" && docker rm db_seeder_db
    set DB_SEEDER_VERSION=latest
)

if ["%DB_SEEDER_CONNECTION_PORT%"] EQU [""] (
    set DB_SEEDER_CONNECTION_PORT=33000
)

if ["%DB_SEEDER_CONTAINER_PORT%"] EQU [""] (
    set DB_SEEDER_CONTAINER_PORT=33000
)

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - setup a CUBRID Docker container.
echo --------------------------------------------------------------------------------
echo DB_SEEDER_CONNECTION_PORT : %DB_SEEDER_CONNECTION_PORT%
echo DB_SEEDER_CONTAINER_PORT  : %DB_SEEDER_CONTAINER_PORT%
echo VERSION                   : %DB_SEEDER_VERSION%
echo --------------------------------------------------------------------------------
echo:| TIME
echo ================================================================================

rem ------------------------------------------------------------------------------
rem CUBRID                                  https://hub.docker.com/r/cubrid/cubrid
rem ------------------------------------------------------------------------------

echo CUBRID
echo --------------------------------------------------------------------------------
lib\Gammadyne\timer.exe
echo Docker create db_seeder_db (CUBRID %DB_SEEDER_VERSION%)
docker create --name db_seeder_db -e CUBRID_DB=%DB_SEEDER_DATABASE% -p %DB_SEEDER_CONNECTION_PORT%:%DB_SEEDER_CONTAINER_PORT%/tcp cubrid/cubrid:%DB_SEEDER_VERSION%

echo Docker start db_seeder_db (CUBRID %DB_SEEDER_VERSION%) ...
docker start db_seeder_db

ping -n 30 127.0.0.1>nul

for /f "delims=" %%A in ('lib\Gammadyne\timer.exe /s') do set "CONSUMED=%%A"
echo DOCKER CUBRID was ready in %CONSUMED%

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

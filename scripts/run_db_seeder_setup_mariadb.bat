@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_setup_mariadb.bat: Setup a MariaDB Server Docker container.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - setup a MariaDB Server Docker container.
echo --------------------------------------------------------------------------------
echo VERSION                   : %DB_SEEDER_VERSION%
echo --------------------------------------------------------------------------------
echo:| TIME
echo ================================================================================

rem ------------------------------------------------------------------------------
rem MariaDB Server                                https://hub.docker.com/_/mariadb
rem ------------------------------------------------------------------------------

echo MariaDB Server
echo --------------------------------------------------------------------------------
lib\Gammadyne\timer.exe
echo Docker create db_seeder_db (MariaDB Server %DB_SEEDER_VERSION%)
docker create --name db_seeder_db -e MYSQL_ROOT_PASSWORD=mariadb -p %DB_SEEDER_CONNECTION_PORT%:%DB_SEEDER_CONTAINER_PORT%/tcp mariadb:%DB_SEEDER_VERSION% --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

echo Docker start db_seeder_db (MariaDB Server %DB_SEEDER_VERSION%) ...
docker start db_seeder_db

ping -n 30 127.0.0.1>nul

for /f "delims=" %%A in ('lib\Gammadyne\timer.exe /s') do set "CONSUMED=%%A"
echo DOCKER MariaDB Server was ready in %CONSUMED%

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

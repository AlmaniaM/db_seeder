@echo off

rem ------------------------------------------------------------------------------
rem
rem run_db_seeder_setup_informix.bat: Setup a IBM Informix Docker container.
rem
rem ------------------------------------------------------------------------------

setlocal EnableDelayedExpansion

echo ================================================================================
echo Start %0
echo --------------------------------------------------------------------------------
echo DB Seeder - setup a IBM Informix Docker container.
echo --------------------------------------------------------------------------------
echo VERSION_INFORMIX          : %DB_SEEDER_VERSION_INFORMIX%
echo --------------------------------------------------------------------------------
echo:| TIME
echo ================================================================================

rem ------------------------------------------------------------------------------
rem IBM Informix       https://hub.docker.com/r/ibmcom/informix-developer-database
rem ------------------------------------------------------------------------------

echo IBM Informix
echo --------------------------------------------------------------------------------
echo Docker create db_seeder_db (IBM Informix %DB_SEEDER_VERSION_INFORMIX%)
docker run -itd --name db_seeder_db --restart unless-stopped -e LICENSE=accept -e DB_INIT=1 -p 9088:9088 --privileged ibmcom/informix-developer-database:%DB_SEEDER_VERSION_INFORMIX%

for /f "delims=" %%A in ('lib\Gammadyne\timer.exe /s') do set "CONSUMED=%%A"
echo DOCKER IBM Informix was ready in %CONSUMED%

:check_health_status:
mkdir tmp >nul 2>&1
docker inspect -f {{.State.Health.Status}} db_seeder_db > tmp\docker_health_status.txt
set /P DOCKER_HEALTH_STATUS=<tmp\docker_health_status.txt
if NOT ["%DOCKER_HEALTH_STATUS%"] == ["healthy"] (
    docker ps --filter "name=db_seeder_db"
    ping -n 10 127.0.0.1 >nul
    goto :check_health_status
)

docker exec -i db_seeder_db bash < scripts\run_db_seeder_setup_informix.input

echo --------------------------------------------------------------------------------
echo:| TIME
echo --------------------------------------------------------------------------------
echo End   %0
echo ================================================================================

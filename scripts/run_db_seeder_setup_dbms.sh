#!/bin/bash

set -e

# ------------------------------------------------------------------------------
#
# run_db_seeder_setup_dbms.sh: Setup database files or a Docker container.
#
# ------------------------------------------------------------------------------

echo "================================================================================"
echo "Start $0"
echo "--------------------------------------------------------------------------------"
echo "DB Seeder - setting up the DBMS."
echo "--------------------------------------------------------------------------------"
echo "DBMS_DB                   : ${DB_SEEDER_DBMS_DB}"
echo "DBMS_EMBEDDED             : ${DB_SEEDER_DBMS_EMBEDDED}"
echo "DBMS_PRESTO               : ${DB_SEEDER_DBMS_PRESTO}"
echo "VERSION                   : ${DB_SEEDER_VERSION}"
echo --------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_EMBEDDED}" = "no" ]; then
    echo "Docker stop/rm db_seeder_db ................................ before:"
    docker ps    | grep "db_seeder_db" && docker stop db_seeder_db
    docker ps -a | grep "db_seeder_db" && docker rm db_seeder_db
    docker network prune --force
    echo "............................................................. after:"
    docker ps -a
fi

if [ "${DB_SEEDER_DBMS_EMBEDDED}" = "yes" ] ||
   [ "${DB_SEEDER_DBMS_DB}" = "derby" ] ||
   [ "${DB_SEEDER_DBMS_DB}" = "h2" ] ||
   [ "${DB_SEEDER_DBMS_DB}" = "ibmdb2" ] ; then
    ( ./scripts/run_db_seeder_setup_files.sh "${DB_SEEDER_DBMS_DB}" )
fi

# ------------------------------------------------------------------------------
# AgensGraph                         https://hub.docker.com/r/bitnine/agensgraph
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "agens" ]; then
    start=$(date +%s)
    echo "AgensGraph."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (AgensGraph ${DB_SEEDER_VERSION})"

    docker create --name db_seeder_db \
                  -p     ${DB_SEEDER_CONNECTION_PORT}:${DB_SEEDER_CONTAINER_PORT} \
                  -t \
                  bitnine/agensgraph:${DB_SEEDER_VERSION} agens

    echo "Docker start db_seeder_db (AgensGraph ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    end=$(date +%s)
    echo "DOCKER AgensGraph was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# CrateDB                                         https://hub.docker.com/_/crate
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "cratedb" ]; then
    start=$(date +%s)
    echo "CrateDB."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (CrateDB ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db --env CRATE_HEAP_SIZE=2g -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp crate:"${DB_SEEDER_VERSION}" crate -Cnetwork.host=_site_ -Cdiscovery.type=single-node

    echo "Docker start db_seeder_db (CrateDB ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    end=$(date +%s)
    echo "DOCKER CrateDB was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# CUBRID                                  https://hub.docker.com/r/cubrid/cubrid
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "cubrid" ]; then
    start=$(date +%s)
    echo "CUBRID."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (CUBRID ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -e CUBRID_DB="${DB_SEEDER_DATABASE}" -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp cubrid/cubrid:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (CUBRID ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    end=$(date +%s)
    echo "DOCKER CUBRID was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# Apache Derby
#           https://hub.docker.com/repository/docker/konnexionsgmbh/apache_derby
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "derby" ]; then
    echo "Apache Derby"
    echo "--------------------------------------------------------------------------------"
    start=$(date +%s)
    echo "Docker create db_seeder_db (Apache Derby ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp konnexionsgmbh/apache_derby:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (Apache Derby ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    while [ "`docker inspect -f {{.State.Health.Status}} db_seeder_db`" != "healthy" ]; do docker ps --filter "name=db_seeder_db"; sleep 30; done
    if [ $? -ne 0 ]; then
        exit 255
    fi

    end=$(date +%s)
    echo "DOCKER Apache Derby was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# Exasol                               https://hub.docker.com/r/exasol/docker-db
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "exasol" ]; then
    start=$(date +%s)
    echo "Exasol."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (Exasol ${DB_SEEDER_VERSION})"
    docker run --detach \
               --name         db_seeder_db \
               -p             127.0.0.1:${DB_SEEDER_CONNECTION_PORT}:${DB_SEEDER_CONTAINER_PORT}/tcp \
               --privileged \
               exasol/docker-db:${DB_SEEDER_VERSION}

    echo "Docker start db_seeder_db (Exasol ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 60

    end=$(date +%s)
    echo "DOCKER Exasol was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# Firebird                        https://hub.docker.com/r/jacobalberty/firebird
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "firebird" ]; then
    start=$(date +%s)
    echo "Firebird."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (Firebird ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -e FIREBIRD_DATABASE="${DB_SEEDER_DATABASE}" -e ISC_PASSWORD=firebird -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp jacobalberty/firebird:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (Firebird ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    while [ "`docker inspect -f {{.State.Health.Status}} db_seeder_db`" != "healthy" ]; do docker ps --filter "name=db_seeder_db"; sleep 30; done
    if [ $? -ne 0 ]; then
        exit 255
    fi

    end=$(date +%s)
    echo "DOCKER Firebird was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# H2 Database Engine
#     https://hub.docker.com/repository/docker/konnexionsgmbh/h2_database_engine
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "h2" ]; then
    echo "H2 Database Engine"
    echo "--------------------------------------------------------------------------------"
    start=$(date +%s)
    echo "Docker create db_seeder_db (H2 Database Engine ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp konnexionsgmbh/h2_database_engine:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (H2 Database Engine ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    while [ "`docker inspect -f {{.State.Health.Status}} db_seeder_db`" != "healthy" ]; do docker ps --filter "name=db_seeder_db"; sleep 30; done
    if [ $? -ne 0 ]; then
        exit 255
    fi

    end=$(date +%s)
    echo "DOCKER H2 Database Engine was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# HyperSQL Database
#      https://hub.docker.com/repository/docker/konnexionsgmbh/hypersql_database
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "hsqldb" ]; then
    echo "HyperSQL Database"
    echo "--------------------------------------------------------------------------------"
    start=$(date +%s)
    echo "Docker create db_seeder_db (HyperSQL Database ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp konnexionsgmbh/hypersql_database:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (HyperSQL Database ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    while [ "`docker inspect -f {{.State.Health.Status}} db_seeder_db`" != "healthy" ]; do docker ps --filter "name=db_seeder_db"; sleep 30; done
    if [ $? -ne 0 ]; then
        exit 255
    fi

    end=$(date +%s)
    echo "DOCKER HyperSQL Database was ready in $((end - start)) seconds"
fi

## -----------------------------------------------------------------------------
# IBM Db2 Database                           https://hub.docker.com/r/ibmcom/db2
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "ibmdb2" ]; then
    start=$(date +%s)
    echo "IBM Db2 Database."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (IBM Db2 ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -e DBNAME="${DB_SEEDER_DATABASE}" -e DB2INST1_PASSWORD=ibmdb2 -e LICENSE=accept -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}" --privileged=true ibmcom/db2:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (IBM Db2 ${DB_SEEDER_VERSION})"
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 300

    end=$(date +%s)
    echo "DOCKER IBM Db2 Database was ready in $((end - start)) seconds"
fi

## -----------------------------------------------------------------------------
# IBM Informix       https://hub.docker.com/r/ibmcom/informix-developer-database
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "informix" ]; then
    start=$(date +%s)
    echo "IBM Informix."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (IBM Informix ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -e LICENSE=accept -e DB_INIT=1 -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}" --privileged ibmcom/informix-developer-database:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (IBM Informix ${DB_SEEDER_VERSION})"
    if ! docker start db_seeder_db; then
        exit 255
    fi

    while [ "`docker inspect -f {{.State.Health.Status}} db_seeder_db`" != "healthy" ]; do docker ps --filter "name=db_seeder_db"; sleep 30; done
    if [ $? -ne 0 ]; then
        exit 255
    fi

    docker exec -i db_seeder_db bash < scripts/run_db_seeder_setup_informix.input

    end=$(date +%s)
    echo "DOCKER IBM Informix was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# MariaDB Server                                https://hub.docker.com/_/mariadb
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "mariadb" ]; then
    start=$(date +%s)
    echo "MariaDB Server."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (MariaDB Server ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -e MYSQL_ROOT_PASSWORD=mariadb -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp mariadb:"${DB_SEEDER_VERSION}" --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

    echo "Docker start db_seeder_db (MariaDB Server ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    end=$(date +%s)
    echo "DOCKER MariaDB Server was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# Mimer SQL                     https://hub.docker.com/r/mimersql/mimersql_v11.0
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "mimer" ]; then
    start=$(date +%s)
    echo "Mimer SQL."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (Mimer SQL ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -e MIMER_SYSADM_PASSWORD=mimer -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp mimersql/mimersql_v11.0:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (Mimer SQL ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    docker exec -i db_seeder_db bash < scripts/run_db_seeder_setup_mimer.input

    end=$(date +%s)
    echo "DOCKER Mimer SQL was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# MonetDB                               https://hub.docker.com/r/monetdb/monetdb
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "monetdb" ]; then
    start=$(date +%s)
    echo "MonetDB."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (MonetDB ${DB_SEEDER_VERSION})"
    docker create --name db_seeder_db -p "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp monetdb/monetdb:"${DB_SEEDER_VERSION}"

    echo "Docker start db_seeder_db (MonetDB ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    end=$(date +%s)
    echo "DOCKER MonetDB was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# MySQL Database                                  https://hub.docker.com/_/mysql
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "mysql" ]; then
    start=$(date +%s)
    echo "MySQL Database."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (MySQL ${DB_SEEDER_VERSION})"

    if [ "${DB_SEEDER_DBMS_PRESTO}" = "yes" ]; then
        docker create --name    db_seeder_db \
                      -e        MYSQL_ROOT_PASSWORD=mysql \
                      --network db_seeder_net \
                      -p        "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp \
                      mysql:"${DB_SEEDER_VERSION}"
    else
        docker create --name    db_seeder_db \
                      -e        MYSQL_ROOT_PASSWORD=mysql \
                      -p        "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp \
                      mysql:"${DB_SEEDER_VERSION}"
    fi  

    echo "Docker start db_seeder_db (MySQL ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 60

    end=$(date +%s)
fi

# ------------------------------------------------------------------------------
# Oracle Database.
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "oracle" ]; then
    echo "Oracle Database"
    echo "--------------------------------------------------------------------------------"
    start=$(date +%s)
    echo "Docker create db_seeder_db (Oracle ${DB_SEEDER_VERSION})"

    if [ "${DB_SEEDER_DBMS_PRESTO}" = "yes" ]; then
        docker create --name     db_seeder_db \
                      -e         ORACLE_PWD=oracle \
                      --network   db_seeder_net \
                      -p         "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp \
                      --shm-size 1G \
                      konnexionsgmbh/${DB_SEEDER_VERSION}
    else
        docker create --name     db_seeder_db \
                      -e         ORACLE_PWD=oracle \
                      -p         "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}"/tcp \
                      --shm-size 1G \
                      konnexionsgmbh/${DB_SEEDER_VERSION}
    fi  

    echo "Docker start db_seeder_db (Oracle ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    while [ "`docker inspect -f {{.State.Health.Status}} db_seeder_db`" != "healthy" ]; do docker ps --filter "name=db_seeder_db"; sleep 60; done
    if [ $? -ne 0 ]; then
        exit 255
    fi

    end=$(date +%s)
    echo "DOCKER Oracle Database was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# Percona Server                         https://hub.docker.com/_/percona-server
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "percona" ]; then
    start=$(date +%s)
    echo "Percona Server."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (Percona Server ${DB_SEEDER_VERSION})"

    docker create --name db_seeder_db \
                  -e     MYSQL_ROOT_PASSWORD=percona \
                  -p     $DB_SEEDER_CONNECTION_PORT:$DB_SEEDER_CONTAINER_PORT/tcp \
                  store/percona/percona-server:$DB_SEEDER_VERSION

    echo "Docker start db_seeder_db (Percona Server ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 60

    end=$(date +%s)
fi

# ------------------------------------------------------------------------------
# PostgreSQL Database                          https://hub.docker.com/_/postgres
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "postgresql" ]; then
    start=$(date +%s)
    echo "PostgreSQL Database."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (PostgreSQL ${DB_SEEDER_VERSION})"

    if [ "${DB_SEEDER_DBMS_PRESTO}" = "yes" ]; then
        docker create --name    db_seeder_db \
                      -e        POSTGRES_DB=kxn_db_sys \
                      -e        POSTGRES_PASSWORD=postgresql \
                      -e        POSTGRES_USER=kxn_user_sys \
                      --network db_seeder_net \
                      -p        "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}" \
                      postgres:"${DB_SEEDER_VERSION}"
    else
        docker create --name db_seeder_db \
                      -e     POSTGRES_DB=kxn_db_sys \
                      -e     POSTGRES_PASSWORD=postgresql \
                      -e     POSTGRES_USER=kxn_user_sys \
                      -p     "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}" \
                      postgres:"${DB_SEEDER_VERSION}"
    fi  

    echo "Docker start db_seeder_db (PostgreSQL ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    end=$(date +%s)
    echo "DOCKER PostgreSQL Database was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# Microsoft SQL Server Database  https://hub.docker.com/_/microsoft-mssql-server
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "sqlserver" ]; then
    start=$(date +%s)
    echo "Microsoft SQL Server."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (Microsoft SQL Server ${DB_SEEDER_VERSION})"

    if [ "${DB_SEEDER_DBMS_PRESTO}" = "yes" ]; then
        docker create --name    db_seeder_db \
                      -e        "ACCEPT_EULA=Y" \
                      -e        "SA_PASSWORD=sqlserver_2019" \
                      --network db_seeder_net \
                      -p        "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}" \
                      mcr.microsoft.com/mssql/server:"${DB_SEEDER_VERSION}"
    else
        docker create --name db_seeder_db \
                      -e     "ACCEPT_EULA=Y" \
                      -e     "SA_PASSWORD=sqlserver_2019" \
                      -p     "${DB_SEEDER_CONNECTION_PORT}":"${DB_SEEDER_CONTAINER_PORT}" \
                      mcr.microsoft.com/mssql/server:"${DB_SEEDER_VERSION}"
    fi  

    echo "Docker start db_seeder_db (Microsoft SQL Server ${DB_SEEDER_VERSION}) ..."
    if ! docker start db_seeder_db; then
        exit 255
    fi

    sleep 30

    end=$(date +%s)
    echo "DOCKER Microsoft SQL Server was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# VoltDB                        https://hub.docker.com/r/voltdb/voltdb-community
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "voltdb" ]; then
    start=$(date +%s)
    echo "VoltDB."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (VoltDB ${DB_SEEDER_VERSION})"

docker run -d \
           -e        HOST_COUNT=1 \
           --name    db_seeder_db \
           -p        21212:21212 \
           -v        $PWD/resources/voltdb/deployment.xml:/tmp/deployment.xml \
           voltdb/voltdb-community:${DB_SEEDER_VERSION}

    echo "Docker start db_seeder_db (VoltDB ${DB_SEEDER_VERSION}) ..."

    sleep 20

    end=$(date +%s)
    echo "DOCKER VoltDB was ready in $((end - start)) seconds"
fi

# ------------------------------------------------------------------------------
# YugabyteDB                       https://hub.docker.com/r/yugabytedb/yugabyte/
# ------------------------------------------------------------------------------

if [ "${DB_SEEDER_DBMS_DB}" = "yugabyte" ]; then
    start=$(date +%s)
    echo "YugabyteDB."
    echo "--------------------------------------------------------------------------------"
    echo "Docker create db_seeder_db (YugabyteDB ${DB_SEEDER_VERSION})"

    rm -rf $PWD/tmp/yb_data
    mkdir -p $PWD/tmp
    mkdir $PWD/tmp/yb_data

    docker run -d \
               --name db_seeder_db \
               -p     5433:5433 \
               -p     7000:7000 \
               -p     9000:9000 \
               -p     9042:9042 \
               -v     $PWD/tmp/yb_data:/home/yugabyte/var \
               yugabytedb/yugabyte:$DB_SEEDER_VERSION bin/yugabyted start --daemon=false

    echo "Docker start db_seeder_db (YugabyteDB ${DB_SEEDER_VERSION}) ..."

    sleep 60

    end=$(date +%s)
    echo "DOCKER YugabyteDB was ready in $((end - start)) seconds"
fi

if [ "${DB_SEEDER_DBMS_EMBEDDED}" == "no" ]; then
    docker ps
fi    

echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "--------------------------------------------------------------------------------"
echo "End   $0"
echo "================================================================================"

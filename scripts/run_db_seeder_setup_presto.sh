#!/bin/bash

set -e

# ------------------------------------------------------------------------------
#
# run_db_seeder_setup_presto.sh: Setup a Presto Distributed Query Engine Docker container.
#
# ------------------------------------------------------------------------------

export DB_SEEDER_RELEASE=2.1.0

echo "================================================================================"
echo "Start $0"
echo "--------------------------------------------------------------------------------"
echo "DB Seeder - setup a Presto Distributed Query Engine Docker container."
echo "--------------------------------------------------------------------------------"
echo "DBMS_PRESTO                       : ${DB_SEEDER_DBMS_PRESTO}"
echo "PRESTO_INSTALLATION_DIRECTORY     : ${DB_SEEDER_PRESTO_INSTALLATION_DIRECTORY}"
echo "PRESTO_INSTALLATION_TYPE          : ${DB_SEEDER_PRESTO_INSTALLATION_TYPE}"
echo "VERSION_PRESTO                    : ${DB_SEEDER_VERSION_PRESTO}"
echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "================================================================================"

# ------------------------------------------------------------------------------
# Presto Distributed Query Engine      https://hub.docker.com/r/prestosql/presto
# ------------------------------------------------------------------------------

if ! docker ps | grep db_seeder_presto; then
    if ! docker ps -a | grep db_seeder_presto; then
        start=$(date +%s)
        echo "--------------------------------------------------------------------------------"
        echo "Start Presto Distributed Query Engine - creating and starting the container"
        echo "--------------------------------------------------------------------------------"
        echo "Docker create presto (Presto Distributed Query Engine)"
        docker create --name db_seeder_presto -p 8080:8080/tcp konnexionsgmbh/db_seeder_presto
        echo "Docker start presto (Presto Distributed Query Engine) ..."
        docker start db_seeder_presto
        
        sleep 30
        
        end=$(date +%s)
        echo "Docker Presto Distributed Query Engine was ready in in $((end - start)) seconds"
        
        docker ps
    else
        echo "--------------------------------------------------------------------------------"
        echo "Start Presto Distributed Query Engine - starting the container"
        echo "--------------------------------------------------------------------------------"
        docker start db_seeder_presto
        docker ps
    fi 
else    
    echo "--------------------------------------------------------------------------------"
    echo "Start Presto Distributed Query Engine - the container is already running"
    echo "--------------------------------------------------------------------------------"
    docker ps
fi     

echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "--------------------------------------------------------------------------------"
echo "End   $0"
echo "================================================================================"
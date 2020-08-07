#!/bin/bash

export -e

# ------------------------------------------------------------------------------
#
# run_db_seeder_presto_environment.sh: Creating a Presto environment.
#
# ------------------------------------------------------------------------------

export DB_SEEDER_JAVA_CLASSPATH=%CLASSPATH%;lib/*

export DB_SEEDER_DIRECTORY_CATALOG_PROPERTY=docker/presto/catalog
export DB_SEEDER_RELEASE=2.1.0
export DB_SEEDER_VERSION_PRESTO=339

export DB_SEEDER_MYSQL_CONNECTION_HOST=localhost
export DB_SEEDER_MYSQL_CONNECTION_HOST=192.168.1.109
export DB_SEEDER_MYSQL_CONNECTION_PORT=3306
export DB_SEEDER_MYSQL_CONNECTION_PREFIX=jdbc:mysql://
export DB_SEEDER_MYSQL_CONNECTION_SUFFIX=?serverTimezone=UTC&failOverReadOnly=false
export DB_SEEDER_MYSQL_PASSWORD=mysql
export DB_SEEDER_MYSQL_USER=kxn_user

export LOG_FILE=run_db_seeder_presto_environment.log

echo "================================================================================"
echo "Start $0"
echo "--------------------------------------------------------------------------------"
echo "DB Seeder - Creating a Presto environment."
echo "--------------------------------------------------------------------------------"
echo "DIRECTORY_CATALOG_PROPERTY : %DB_SEEDER_DIRECTORY_CATALOG_PROPERTY"
echo "RELEASE                    : %DB_SEEDER_RELEASE"
echo "VERSION_PRESTO             : %DB_SEEDER_VERSION_PRESTO"
echo "--------------------------------------------------------------------------------"
echo "CONNECTION_HOST_PRESTO      : %DB_SEEDER_CONNECTION_HOST_PRESTO"
echo "CONNECTION_PORT_PRESTO      : %DB_SEEDER_CONNECTION_PORT_PRESTO"
echo "--------------------------------------------------------------------------------"
echo "MYSQL_CONNECTION_HOST      : %DB_SEEDER_MYSQL_CONNECTION_HOST"
echo "MYSQL_CONNECTION_PORT      : %DB_SEEDER_MYSQL_CONNECTION_PORT"
echo "MYSQL_CONNECTION_PREFIX    : %DB_SEEDER_MYSQL_CONNECTION_PREFIX"
echo "MYSQL_CONNECTION_SUFFIX    : %DB_SEEDER_MYSQL_CONNECTION_SUFFIX"
echo "MYSQL_PASSWORD             : %DB_SEEDER_MYSQL_PASSWORD"
echo "MYSQL_USER                 : %DB_SEEDER_MYSQL_USER"
echo "--------------------------------------------------------------------------------"
echo "JAVA_CLASSPATH             : %DB_SEEDER_JAVA_CLASSPATH"
echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "================================================================================"
echo "Compile and generate catalog property files."
echo "--------------------------------------------------------------------------------"

if ! (java --enable-preview -cp "{$DB_SEEDER_JAVA_CLASSPATH}" ch.konnexions.db_seeder.PrestoEnvironment mysql; then
    exit 255
fi    

echo "--------------------------------------------------------------------------------"
echo "Create Docker image."
echo "--------------------------------------------------------------------------------"

if [ -d "tmp" ]; then 
    rm -Rf tmp
fi

mkdir tmp

cp -a docker tmp
mv tmp/dockerfile_presto tmp/dockerfile

docker build -t konnexionsgmbh/db_seeder_presto tmp

docker tag konnexionsgmbh/db_seeder_presto konnexionsgmbh/db_seeder_presto:%DB_SEEDER_RELEASE

docker push konnexionsgmbh/db_seeder_presto:%DB_SEEDER_RELEASE

docker images -q -f "dangling=true" -f "label=autodelete=true"

for IMAGE in $(docker images -q -f "dangling=true" -f "label=autodelete=true")
do
    docker rmi -f $IMAGE
done

echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "--------------------------------------------------------------------------------"
echo "End   $0"
echo "================================================================================"

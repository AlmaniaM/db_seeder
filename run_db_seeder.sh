#!/bin/bash

set -e

# ------------------------------------------------------------------------------
#
# run_db_seeder.sh: Creation of dummy data in an empty databases, 
#                   database users or schemas.
#
# ------------------------------------------------------------------------------

export DB_SEEDER_CONNECTION_PORT_DEFAULT=4711
export DB_SEEDER_DBMS_DEFAULT=sqlite
export DB_SEEDER_NO_CREATE_RUNS_DEFAULT=2
export DB_SEEDER_RELEASE=2.6.1
export DB_SEEDER_SETUP_DBMS_DEFAULT=yes
export DB_SEEDER_VERSION_PRESTO=347

if [ -z "$1" ]; then
    echo "========================================================="
    echo "complete           - All implemented DBMSs"
    echo "complete_client    - All implemented client DBMSs"
    echo "complete_emb       - All implemented embedded DBMSs"
    echo "complete_presto    - All implemented Presto enabled DBMSs"
    echo "---------------------------------------------------------"
    echo "agens              - AgensGraph [client]"
    echo "derby              - Apache Derby [client]"
    echo "derby_emb          - Apache Derby [embedded]"
    echo "cratedb            - CrateDB"
    echo "cubrid             - CUBRID"
    echo "exasol             - Exasol"
    echo "firebird           - Firebird"
    echo "h2                 - H2 Database Engine [client]"
    echo "h2_emb             - H2 Database Engine [embedded]"
    echo "hsqldb             - HyperSQL Database [client]"
    echo "hsqldb_emb         - HyperSQL Database [embedded]"
    echo "ibmdb2             - IBM Db2 Database"
    echo "informix           - IBM Informix"
    echo "mariadb            - MariaDB Server"
    echo "mimer              - Mimer SQL"
    echo "monetdb            - MonetDB"
    echo "sqlserver          - Microsoft SQL Server"
    echo "sqlserver_presto   - Microsoft SQL Server via Presto"
    echo "mysql              - MySQL Database"
    echo "mysql_presto       - MySQL Database via Presto"
    echo "oracle             - Oracle Database"
    echo "oracle_presto      - Oracle Database via Presto"
    echo "percona            - Percona Server for MySQL"
    echo "postgresql         - PostgreSQL Database"
    echo "postgresql_presto  - PostgreSQL Database via Presto"
    echo "sqlite             - SQLite [embedded]"
    echo "voltdb             - VoltDB"
    echo "yugabyte           - YugabyteDB"
    echo "---------------------------------------------------------"
    read -p "Enter the desired database management system [default: ${DB_SEEDER_DBMS_DEFAULT}] " DB_SEEDER_DBMS
    export DB_SEEDER_DBMS=${DB_SEEDER_DBMS}

    if [ -z "${DB_SEEDER_DBMS}" ]; then
        export DB_SEEDER_DBMS=${DB_SEEDER_DBMS_DEFAULT}
    fi
else
    export DB_SEEDER_DBMS=$1
fi

if [ -z "$2" ]; then
    read -p "Setup the DBMS (yes/no) [default: $DB_SEEDER_SETUP_DBMS_DEFAULT] " DB_SEEDER_SETUP_DBMS
    export DB_SEEDER_SETUP_DBMS=${DB_SEEDER_SETUP_DBMS}

    if [ -z "${DB_SEEDER_SETUP_DBMS}" ]; then
        export DB_SEEDER_SETUP_DBMS=$DB_SEEDER_SETUP_DBMS_DEFAULT
    fi
else
    export DB_SEEDER_SETUP_DBMS=$2
fi

if [ -z "$3" ]; then
    read -p "Number of data creation runs (0-2) [default: $DB_SEEDER_NO_CREATE_RUNS_DEFAULT] " DB_SEEDER_NO_CREATE_RUNS
    export DB_SEEDER_NO_CREATE_RUNS=${DB_SEEDER_NO_CREATE_RUNS}

    if [ -z "${DB_SEEDER_NO_CREATE_RUNS}" ]; then
        export DB_SEEDER_NO_CREATE_RUNS=$DB_SEEDER_NO_CREATE_RUNS_DEFAULT
    fi
else
    export DB_SEEDER_NO_CREATE_RUNS=$3
fi

if [ -z "${DOCKER_USERNAME}" ]; then
    read -p "Enter the docker username " DOCKER_USERNAME
    export DOCKER_USERNAME=${DOCKER_USERNAME}
fi

if [ -z "${DOCKER_PASSWORD}" ]; then
    read -p "Enter the docker password " DOCKER_PASSWORD
    export DOCKER_PASSWORD=${$DOCKER_PASSWORD}
fi

export DB_SEEDER_JAVA_CLASSPATH=".:lib/*:JAVA_HOME/lib"

# ------------------------------------------------------------------------------
# Start Properties.
# ------------------------------------------------------------------------------

if [ -z "${DB_SEEDER_COMPLETE_RUN}" ]; then
    export DB_SEEDER_COMPLETE_RUN=no
fi 

export DB_SEEDER_DBMS_EMBEDDED=no
export DB_SEEDER_DBMS_PRESTO=no

export DB_SEEDER_FILE_CONFIGURATION_NAME=src/main/resources/db_seeder.properties

if [ "${DB_SEEDER_DBMS}" = "agens" ]; then
    export DB_SEEDER_CONNECTION_PORT=5432
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:agensgraph://
    export DB_SEEDER_CONTAINER_PORT=5432
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=agens
    export DB_SEEDER_PASSWORD=agens
    export DB_SEEDER_PASSWORD_SYS=agens
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=agens
    export DB_SEEDER_VERSION=v2.1.1
fi

if [ "${DB_SEEDER_DBMS}" = "cratedb" ]; then
    export DB_SEEDER_CONNECTION_PORT=5432
    export DB_SEEDER_CONNECTION_PREFIX=crate://
    export DB_SEEDER_CONTAINER_PORT=5432
    export DB_SEEDER_PASSWORD=cratedb
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=crate
    export DB_SEEDER_VERSION=4.1.6
    export DB_SEEDER_VERSION=4.1.8
    export DB_SEEDER_VERSION=4.2.2
    export DB_SEEDER_VERSION=4.2.3
    export DB_SEEDER_VERSION=4.2.4
    export DB_SEEDER_VERSION=4.2.6
    export DB_SEEDER_VERSION=4.2.7
    export DB_SEEDER_VERSION=4.3.0
    export DB_SEEDER_VERSION=4.3.1
fi

if [ "${DB_SEEDER_DBMS}" = "cubrid" ]; then
    export DB_SEEDER_CONNECTION_PORT=33000
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:cubrid:"
    export DB_SEEDER_CONTAINER_PORT=33000
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_PASSWORD=cubrid
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=DBA
    export DB_SEEDER_VERSION=10.2
fi

if [ "${DB_SEEDER_DBMS}" = "derby" ]; then
    export DB_SEEDER_CONNECTION_PORT=$DB_SEEDER_CONNECTION_PORT_DEFAULT
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:derby:
    export DB_SEEDER_CONTAINER_PORT=1527
    export DB_SEEDER_DATABASE=./tmp/derby_kxn_db
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_VERSION=10.15.2.0
fi

if [ "${DB_SEEDER_DBMS}" = "derby_emb" ]; then
    # TODO Bug in Apache Derby
    if [ "${DB_SEEDER_NO_CREATE_RUNS}" = "2" ]; then
        export DB_SEEDER_NO_CREATE_RUNS=1
    fi
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:derby:
    export DB_SEEDER_DATABASE=./tmp/derby_kxn_db
    export DB_SEEDER_DBMS_EMBEDDED=yes
    export DB_SEEDER_SCHEMA=kxn_schema
fi

if [ "${DB_SEEDER_DBMS}" = "exasol" ]; then
    export DB_SEEDER_CONNECTION_PORT=8563
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:exa:
    export DB_SEEDER_CONTAINER_PORT=8563
    export DB_SEEDER_PASSWORD=exasol
    export DB_SEEDER_PASSWORD_SYS=exasol
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=sys
    export DB_SEEDER_VERSION=6.2.8-d1
    export DB_SEEDER_VERSION=7.0.2
    export DB_SEEDER_VERSION=7.0.3
fi

if [ "${DB_SEEDER_DBMS}" = "firebird" ]; then
    export DB_SEEDER_CONNECTION_PORT=3050
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:firebirdsql://"
    export DB_SEEDER_CONNECTION_SUFFIX="?encoding=UTF8&useFirebirdAutocommit=true&useStreamBlobs=true"
    export DB_SEEDER_CONTAINER_PORT=3050
    export DB_SEEDER_DATABASE=firebird_kxn_db
    export DB_SEEDER_PASSWORD=firebird
    export DB_SEEDER_PASSWORD_SYS=firebird
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=SYSDBA
    export DB_SEEDER_VERSION=3.0.5
    export DB_SEEDER_VERSION=3.0.7
fi

if [ "${DB_SEEDER_DBMS}" = "h2" ]; then
    export DB_SEEDER_CONNECTION_PORT=9092
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:h2:
    export DB_SEEDER_CONTAINER_PORT=9092
    export DB_SEEDER_DATABASE=./tmp/h2_kxn_db
    export DB_SEEDER_PASSWORD=h2
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_VERSION=1.4.200
fi

if [ "${DB_SEEDER_DBMS}" = "h2_emb" ]; then
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:h2:
    export DB_SEEDER_DATABASE=./tmp/h2_kxn_db
    export DB_SEEDER_DBMS_EMBEDDED=yes
    export DB_SEEDER_PASSWORD=h2
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
fi

if [ "${DB_SEEDER_DBMS}" = "hsqldb" ]; then
    export DB_SEEDER_CONNECTION_PORT=9001
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:hsqldb:"
    export DB_SEEDER_CONNECTION_SUFFIX=";ifexists=false;shutdown=true"
    export DB_SEEDER_CONTAINER_PORT=9001
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_PASSWORD=hsqldb
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=SA
    export DB_SEEDER_VERSION=2.5.1
fi

if [ "${DB_SEEDER_DBMS}" = "hsqldb_emb" ]; then
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:hsqldb:"
    export DB_SEEDER_CONNECTION_SUFFIX=";ifexists=false;shutdown=true"
    export DB_SEEDER_DATABASE=./tmp/hsqldb_kxn_db
    export DB_SEEDER_DBMS_EMBEDDED=yes
    export DB_SEEDER_PASSWORD=hsqldb
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=SA
fi

if [ "${DB_SEEDER_DBMS}" = "ibmdb2" ]; then
    export DB_SEEDER_CONNECTION_PORT=50000
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:db2://
    export DB_SEEDER_CONTAINER_PORT=50000
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_PASSWORD_SYS=ibmdb2
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER_SYS=db2inst1
    export DB_SEEDER_VERSION=11.5.0.0a
    export DB_SEEDER_VERSION=11.5.4.0
fi

if [ "${DB_SEEDER_DBMS}" = "informix" ]; then
    export DB_SEEDER_CONNECTION_PORT=9088
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:informix-sqli://"
    export DB_SEEDER_CONTAINER_PORT=9088
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=sysmaster
    export DB_SEEDER_PASSWORD_SYS=in4mix
    export DB_SEEDER_USER_SYS=informix
    export DB_SEEDER_VERSION=14.10.FC3DE
    export DB_SEEDER_VERSION=14.10.FC4DE
fi

if [ "${DB_SEEDER_DBMS}" = "mariadb" ]; then
    export DB_SEEDER_CONNECTION_PORT=3306
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:mariadb://
    export DB_SEEDER_CONTAINER_PORT=3306
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=mysql
    export DB_SEEDER_PASSWORD=mariadb
    export DB_SEEDER_PASSWORD_SYS=mariadb
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=root
    export DB_SEEDER_VERSION=10.4.13
    export DB_SEEDER_VERSION=10.5.3
    export DB_SEEDER_VERSION=10.5.4
    export DB_SEEDER_VERSION=10.5.5
    export DB_SEEDER_VERSION=10.5.6
    export DB_SEEDER_VERSION=10.5.8
fi

if [ "${DB_SEEDER_DBMS}" = "mimer" ]; then
    export DB_SEEDER_CONNECTION_PORT=11360
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:mimer://
    export DB_SEEDER_CONTAINER_PORT=1360
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=mimerdb
    export DB_SEEDER_PASSWORD=mimer
    export DB_SEEDER_PASSWORD_SYS=mimer
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=SYSADM
    export DB_SEEDER_VERSION=v11.0.3c
    export DB_SEEDER_VERSION=v11.0.4b
fi

if [ "${DB_SEEDER_DBMS}" = "monetdb" ]; then
    export DB_SEEDER_CONNECTION_PORT=50000
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:monetdb://
    export DB_SEEDER_CONTAINER_PORT=50000
    export DB_SEEDER_DATABASE_SYS=demo
    export DB_SEEDER_PASSWORD=monetdb
    export DB_SEEDER_PASSWORD_SYS=monetdb
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=monetdb
    export DB_SEEDER_VERSION=Jun2020-SP1
fi

if [ "${DB_SEEDER_DBMS}" = "mysql" ]; then
    export DB_SEEDER_CONNECTION_PORT=3306
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:mysql://"
    export DB_SEEDER_CONNECTION_SUFFIX="&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true"
    export DB_SEEDER_CONTAINER_PORT=3306
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=sys
    export DB_SEEDER_PASSWORD=mysql
    export DB_SEEDER_PASSWORD_SYS=mysql
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=root
    export DB_SEEDER_VERSION=8.0.20
    export DB_SEEDER_VERSION=8.0.21
    export DB_SEEDER_VERSION=8.0.22
fi

if [ "${DB_SEEDER_DBMS}" = "mysql_presto" ]; then
    export DB_SEEDER_CONNECTION_PORT=3306
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:mysql://"
    export DB_SEEDER_CONNECTION_SUFFIX="&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true"
    export DB_SEEDER_CONTAINER_PORT=3306
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=sys
    export DB_SEEDER_DBMS_PRESTO=yes
    export DB_SEEDER_PASSWORD=mysql
    export DB_SEEDER_PASSWORD_SYS=mysql
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=root
    export DB_SEEDER_VERSION=8.0.20
    export DB_SEEDER_VERSION=8.0.21
    export DB_SEEDER_VERSION=8.0.22
fi

if [ "${DB_SEEDER_DBMS}" = "oracle" ]; then
    export DB_SEEDER_CONNECTION_PORT=1521
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:oracle:thin:@//
    export DB_SEEDER_CONNECTION_SERVICE=orclpdb1
    export DB_SEEDER_CONTAINER_PORT=1521
    export DB_SEEDER_PASSWORD=oracle
    export DB_SEEDER_PASSWORD_SYS=oracle
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS="SYS AS SYSDBA"
    export DB_SEEDER_VERSION=db_12_2_ee
    export DB_SEEDER_VERSION=db_18_3_ee
    export DB_SEEDER_VERSION=db_19_3_ee
    export DB_SEEDER_VERSION=db_18_3_ee
fi

if [ "${DB_SEEDER_DBMS}" = "oracle_presto" ]; then
    export DB_SEEDER_CONNECTION_PORT=1521
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:oracle:thin:@//
    export DB_SEEDER_CONNECTION_SERVICE=orclpdb1
    export DB_SEEDER_CONTAINER_PORT=1521
    export DB_SEEDER_DBMS_PRESTO=yes
    export DB_SEEDER_PASSWORD=oracle
    export DB_SEEDER_PASSWORD_SYS=oracle
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS="SYS AS SYSDBA"
    export DB_SEEDER_VERSION=db_12_2_ee
    export DB_SEEDER_VERSION=db_18_3_ee
    export DB_SEEDER_VERSION=db_19_3_ee
    export DB_SEEDER_VERSION=db_18_3_ee
fi

if [ "${DB_SEEDER_DBMS}" = "percona" ]; then
    export DB_SEEDER_CONNECTION_PORT=3306
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:mysql://"
    export DB_SEEDER_CONNECTION_SUFFIX="&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false&rewriteBatchedStatements=true"
    export DB_SEEDER_CONTAINER_PORT=3306
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=sys
    export DB_SEEDER_PASSWORD=percona
    export DB_SEEDER_PASSWORD_SYS=percona
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=root
    export DB_SEEDER_VERSION=5.7.14
fi

if [ "${DB_SEEDER_DBMS}" = "postgresql" ]; then
    export DB_SEEDER_CONNECTION_PORT=5432
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:postgresql://
    export DB_SEEDER_CONTAINER_PORT=5432
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=kxn_db_sys
    export DB_SEEDER_PASSWORD=postgresql
    export DB_SEEDER_PASSWORD_SYS=postgresql
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=kxn_user_sys
    export DB_SEEDER_VERSION=12.4-alpine
    export DB_SEEDER_VERSION=13-alpine
    export DB_SEEDER_VERSION=13.1-alpine
fi

if [ "${DB_SEEDER_DBMS}" = "postgresql_presto" ]; then
    export DB_SEEDER_CONNECTION_PORT=5432
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:postgresql://
    export DB_SEEDER_CONTAINER_PORT=5432
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=kxn_db_sys
    export DB_SEEDER_DBMS_PRESTO=yes
    export DB_SEEDER_PASSWORD=postgresql
    export DB_SEEDER_PASSWORD_SYS=postgresql
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=kxn_user_sys
    export DB_SEEDER_VERSION=12.4-alpine
    export DB_SEEDER_VERSION=13-alpine
    export DB_SEEDER_VERSION=13.1-alpine
fi

if [ "${DB_SEEDER_DBMS}" = "sqlite" ]; then
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:sqlite:
    export DB_SEEDER_DATABASE=./tmp/sqlite_kxn_db
    export DB_SEEDER_DBMS_EMBEDDED=yes
fi

if [ "${DB_SEEDER_DBMS}" = "sqlserver" ]; then
    export DB_SEEDER_CONNECTION_PORT=1433
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:sqlserver://
    export DB_SEEDER_CONTAINER_PORT=1433
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=master
    export DB_SEEDER_PASSWORD=sqlserver_2019
    export DB_SEEDER_PASSWORD_SYS=sqlserver_2019
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=sa
    export DB_SEEDER_VERSION=2019-latest
fi

if [ "${DB_SEEDER_DBMS}" = "sqlserver_presto" ]; then
    export DB_SEEDER_CONNECTION_PORT=1433
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:sqlserver://
    export DB_SEEDER_CONTAINER_PORT=1433
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=master
    export DB_SEEDER_DBMS_PRESTO=yes
    export DB_SEEDER_PASSWORD=sqlserver_2019
    export DB_SEEDER_PASSWORD_SYS=sqlserver_2019
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=sa
    export DB_SEEDER_VERSION=2019-latest
fi

if [ "${DB_SEEDER_DBMS}" = "voltdb" ]; then
    export DB_SEEDER_CONNECTION_PORT=21212
    export DB_SEEDER_CONNECTION_PREFIX="jdbc:voltdb://"
    export DB_SEEDER_CONNECTION_SUFFIX="?autoreconnect=true"
    export DB_SEEDER_CONTAINER_PORT=21212
    export DB_SEEDER_PASSWORD=voltdb
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_VERSION=9.2.1
fi

if [ "${DB_SEEDER_DBMS}" = "yugabyte" ]; then
    export DB_SEEDER_CONNECTION_PORT=5433
    export DB_SEEDER_CONNECTION_PREFIX=jdbc:postgresql://
    export DB_SEEDER_CONTAINER_PORT=5433
    export DB_SEEDER_DATABASE=kxn_db
    export DB_SEEDER_DATABASE_SYS=yugabyte
    export DB_SEEDER_PASSWORD=yugabyte
    export DB_SEEDER_SCHEMA=kxn_schema
    export DB_SEEDER_USER=kxn_user
    export DB_SEEDER_USER_SYS=yugabyte
    export DB_SEEDER_VERSION=2.2.2.0-b15
    export DB_SEEDER_VERSION=2.3.1.0-b15
    export DB_SEEDER_VERSION=2.3.2.0-b37
    export DB_SEEDER_VERSION=2.3.3.0-b106
    export DB_SEEDER_VERSION=2.5.0.0-b2
fi

if [ -z "${DB_SEEDER_CONNECTION_HOST}" ]; then
    export DB_SEEDER_CONNECTION_HOST=localhost
fi

if [ "${DB_SEEDER_DBMS_PRESTO}" = "yes" ]; then
    export DB_SEEDER_CONNECTION_HOST_PRESTO=${DB_SEEDER_CONNECTION_HOST}
    export DB_SEEDER_CONNECTION_PORT_PRESTO=8080
fi

# ------------------------------------------------------------------------------
# End   Properties.
# ------------------------------------------------------------------------------

echo "================================================================================"
echo "Start $0"
echo "--------------------------------------------------------------------------------"
echo "DB Seeder - Creation of dummy data in an empty database schema / user."
echo "--------------------------------------------------------------------------------"
echo "COMPLETE_RUN                      : ${DB_SEEDER_COMPLETE_RUN}"
echo "DBMS                              : ${DB_SEEDER_DBMS}"
echo "DBMS_DB                           : ${DB_SEEDER_DBMS_DB}"
echo "DBMS_DEFAULT                      : ${DB_SEEDER_DBMS_DEFAULT}"
echo "DBMS_EMBEDDED                     : ${DB_SEEDER_DBMS_EMBEDDED}"
echo "DBMS_PRESTO                       : ${DB_SEEDER_DBMS_PRESTO}"
echo "DIRECTORY_CATALOG_PROPERTY        : ${DB_SEEDER_DIRECTORY_CATALOG_PROPERTY}"
echo "DOCKER_USERNAME                   : ${DOCKER_USERNAME}"
echo "FILE_CONFIGURATION_NAME           : ${DB_SEEDER_FILE_CONFIGURATION_NAME}"
echo "FILE_JSON_NAME                    : ${DB_SEEDER_FILE_JSON_NAME}"
echo "FILE_STATISTICS_DELIMITER         : ${DB_SEEDER_FILE_STATISTICS_DELIMITER}"
echo "FILE_STATISTICS_HEADER            : ${DB_SEEDER_FILE_STATISTICS_HEADER}"
echo "FILE_STATISTICS_NAME              : ${DB_SEEDER_FILE_STATISTICS_NAME}"
echo "JAVA_CLASSPATH                    : ${DB_SEEDER_JAVA_CLASSPATH}"
echo "NO_CREATE_RUNS                    : ${DB_SEEDER_NO_CREATE_RUNS}"
echo "RELEASE                           : ${DB_SEEDER_RELEASE}"
echo "SETUP_DBMS                        : ${DB_SEEDER_SETUP_DBMS}"
echo "VERSION_PRESTO                    : ${DB_SEEDER_VERSION_PRESTO}"
echo --------------------------------------------------------------------------------
echo "CONNECTION_HOST_PRESTO            : ${DB_SEEDER_CONNECTION_HOST_PRESTO}"
echo "CONNECTION_PORT_PRESTO            : ${DB_SEEDER_CONNECTION_PORT_PRESTO}"
echo "--------------------------------------------------------------------------------"
echo "CATALOG                           : ${DB_SEEDER_CATALOG}"
echo "CATALOG_SYS                       : ${DB_SEEDER_CATALOG_SYS}"
echo "CONNECTION_HOST                   : ${DB_SEEDER_CONNECTION_HOST}"
echo "CONNECTION_PORT                   : ${DB_SEEDER_CONNECTION_PORT}"
echo "CONNECTION_PREFIX                 : ${DB_SEEDER_CONNECTION_PREFIX}"
echo "CONNECTION_SERVICE                : ${DB_SEEDER_CONNECTION_SERVICE}"
echo "CONNECTION_SUFFIX                 : ${DB_SEEDER_CONNECTION_SUFFIX}"
echo "CONTAINER_PORT                    : ${DB_SEEDER_CONTAINER_PORT}"
echo "DATABASE                          : ${DB_SEEDER_DATABASE}"
echo "DATABASE_SYS                      : ${DB_SEEDER_DATABASE_SYS}"
echo "PASSWORD                          : ${DB_SEEDER_PASSWORD}"
echo "PASSWORD_SYS                      : ${DB_SEEDER_PASSWORD_SYS}"
echo "SCHEMA                            : ${DB_SEEDER_SCHEMA}"
echo "USER                              : ${DB_SEEDER_USER}"
echo "USER_SYS                          : ${DB_SEEDER_USER_SYS}"
echo "VERSION                           : ${DB_SEEDER_VERSION}"
echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "================================================================================"

docker login -u "${DOCKER_USERNAME}" -p "$DOCKER_PASSWORD"

if [ "${DB_SEEDER_DBMS}" = "complete" ]; then
    if ! ( ./scripts/run_db_seeder_complete.sh ${DB_SEEDER_NO_CREATE_RUNS}); then
        exit 255
    fi
elif [ "${DB_SEEDER_DBMS}" = "complete_client" ]; then
    if ! ( ./scripts/run_db_seeder_complete_client.sh ${DB_SEEDER_NO_CREATE_RUNS}); then
        exit 255
    fi    
elif [ "${DB_SEEDER_DBMS}" = "complete_client_1" ]; then
    if ! ( ./scripts/run_db_seeder_complete_client_1.sh ${DB_SEEDER_NO_CREATE_RUNS}); then
        exit 255
    fi    
elif [ "${DB_SEEDER_DBMS}" = "complete_client_2" ]; then
    if ! ( ./scripts/run_db_seeder_complete_client_2.sh ${DB_SEEDER_NO_CREATE_RUNS}); then
        exit 255
    fi    
elif [ "${DB_SEEDER_DBMS}" = "complete_emb" ]; then
    if ! ( ./scripts/run_db_seeder_complete_emb.sh ${DB_SEEDER_NO_CREATE_RUNS}); then
        exit 255
    fi    
elif [ "${DB_SEEDER_DBMS}" = "complete_presto" ]; then
    if ! ( ./scripts/run_db_seeder_complete_presto.sh ${DB_SEEDER_NO_CREATE_RUNS}); then
        exit 255
    fi    
else
    if [ "${DB_SEEDER_DBMS_PRESTO}" = "yes" ]; then
        if [ "${DB_SEEDER_SETUP_DBMS}" = "yes" ]; then
            if ! ( ./scripts/run_db_seeder_presto_environment.sh complete ); then
                exit 255
            fi
            if ! ( ./scripts/run_db_seeder_setup_presto.sh ); then
                exit 255
            fi
        fi        
    fi        
    if ! ( ./scripts/run_db_seeder_single.sh "${DB_SEEDER_DBMS}" ); then
        exit 255
    fi    
fi  

echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "--------------------------------------------------------------------------------"
echo "End   $0"
echo "================================================================================"

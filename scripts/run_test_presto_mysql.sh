#!/bin/bash

set -e

# ------------------------------------------------------------------------------
#
# run_test_presto_mysql.sh: Demonstration Issues MySQL Connector.
#
# ------------------------------------------------------------------------------

echo "================================================================================"
echo "Start $0"
echo "--------------------------------------------------------------------------------"
echo "DB Seeder - Demonstration Issues MySQL Connector."
echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "--------------------------------------------------------------------------------"

if ! (java --enable-preview -cp "${CLASSPATH}:lib/*" ch.konnexions.db_seeder.test.TestPrestoMysql); then
    exit 255
fi    

echo "--------------------------------------------------------------------------------"
date +"DATE TIME : %d.%m.%Y %H:%M:%S"
echo "--------------------------------------------------------------------------------"
echo "End   $0"
echo "================================================================================"

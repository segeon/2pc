#!/usr/bin/env bash

JAR_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && cd ../jar && pwd)
PROJECT_HOME=$(cd "$(dirname "${BASH_SOURCE[0]}")" && cd ../.. && pwd)

ensure_mvn() {
    mvn -version 2>&1 1>/dev/null
    if [ $? -ne 0 ]; then
        echo "please make sure make is properly installed and configured!"
        exit 1
    fi
}

ensure_java8() {
    java -version 2>&1 |grep 1.8 > /dev/null
    if [ $? -ne 0 ]; then
        echo "requires jre 1.8!"
        exit 1
    fi
}

ensure_mvn
ensure_java8

cd $PROJECT_HOME
mvn -Dmaven.test.skip=true package
rm -rf $JAR_DIR/*
if [ $? -eq 0 ]; then
    cp boc-server/target/boc-server-1.0-SNAPSHOT.jar $JAR_DIR/
    cp cbrc-server/target/cbrc-server-1.0-SNAPSHOT.jar $JAR_DIR/
    cp ccb-server/target/ccb-server-1.0-SNAPSHOT.jar $JAR_DIR/
    cp transaction-manager/target/transaction-manager-1.0-SNAPSHOT.jar $JAR_DIR
    cp customer-client/target/customer-client-1.0-SNAPSHOT.jar $JAR_DIR
    echo "build succeeded!"
else
    echo "build failed!"
fi
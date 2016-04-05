#!/bin/sh

script_name=$0
command=$1

JAR_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && cd ../jar && pwd)
LOG_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && cd .. && pwd)/log

BOC_JAR=$JAR_DIR/boc-server-1.0-SNAPSHOT.jar
CBRC_JAR=$JAR_DIR/cbrc-server-1.0-SNAPSHOT.jar
CCB_JAR=$JAR_DIR/ccb-server-1.0-SNAPSHOT.jar
TM_JAR=$JAR_DIR/transaction-manager-1.0-SNAPSHOT.jar
CLIENT_JAR=$JAR_DIR/customer-client-1.0-SNAPSHOT.jar


JAVA_OPTS="-Xms256M -Xmx256m"

usage() {
    echo "$script_name [servers|boc|ccb|cbrc|tm|client]"
}

ensure_log_dir() {
    if [ ! -d $LOG_DIR ]; then
        mkdir -p $LOG_DIR
    fi
}

ensure_java8() {
    java -version 2>&1 |grep 1.8 > /dev/null
    if [ $? -ne 0 ]; then
        echo "requires jre 1.8!"
        exit 1
    fi
}

start() {
    name=$1
    jar_path=$2
    echo "starting $name ..."
    nohup java $JAVA_OPTS -jar $jar_path 2>&1 1>$LOG_DIR/$name.log &
}

start_client() {
    java $JAVA_OPTS -jar $CLIENT_JAR $@
}

if [ $# -eq 0 ]; then
    usage
    exit 1
fi

ensure_java8
ensure_log_dir

case $command in
    servers)
        start boc-server $BOC_JAR
        start ccb-server $CCB_JAR
        start cbrc-server $CBRC_JAR
        start transaction-manager-server $TM_JAR
        ;;
    boc)
        start boc-server $BOC_JAR
        ;;
    ccb)
        start ccb-server $CCB_JAR
        ;;
    cbrc)
        start cbrc-server $CBRC_JAR
        ;;
    tm)
        start transaction-manager-server $TM_JAR
        ;;
    client)
        shift 1
        start_client $@
        ;;
    *)
        usage
        ;;
esac
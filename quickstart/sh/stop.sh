#!/usr/bin/env bash

script_name=$0
command=$1

usage() {
    echo "$script_name [servers|boc|ccb|cbrc|tm|client]"
}


stop() {
    name=$1
    echo "stopping $name ..."
    pid=`ps -ef|grep -w $name|grep -v grep|awk '{print $2;}'`
    if [ "x$pid" = "x" ]; then
        echo "$name stopped!"
        return 0
    fi
    kill $pid 2>&1 1>/dev/null
    sleep 2
    ps -ef|grep -w $name |grep -v grep 2>&1 1>/dev/null
    if [ $? -ne 0 ]; then
        echo "$name stopped!"
    else
        echo "failed to stop $name!"
    fi
}

if [ $# -eq 0 ]; then
    usage
    exit 1
fi

case $command in
    servers)
        stop boc-server
        stop ccb-server
        stop cbrc-server
        stop transaction-manager
        ;;
    boc)
        stop boc-server
        ;;
    ccb)
        stop ccb-server
        ;;
    cbrc)
        stop cbrc-server
        ;;
    tm)
        stop transaction-manager
        ;;
    client)
        stop client
        ;;
    *)
        usage
        ;;
esac
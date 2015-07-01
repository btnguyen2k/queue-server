#!/bin/bash

# -------------------------------- #
# Start/Stop script on *NIX        #
# -------------------------------- #
# Command-line arguments:          #
# -p <http_port>                   #
# -t <thrift_port>                 #
# -m <mem in mb>                   #
# -c <config_file.conf>            #
# -s <spring_config_file.conf>     #
# -j "extra-jvm-options"           #
# -------------------------------- #

# from http://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
pushd $(dirname "${0}") > /dev/null
_basedir=$(pwd -L)
popd > /dev/null

APP_HOME=$_basedir/..
APP_NAME=queue-server
APP_PID=$APP_HOME/$APP_NAME.pid

DEFAULT_APP_PORT=8080
DEFAULT_THRIFT_PORT=9090
DEFAULT_APP_MEM=64
DEFAULT_APP_CONF=application.conf
DEFAULT_APP_SPRING_CONF=spring/beans.xml

APP_PORT=$DEFAULT_APP_PORT
THRIFT_PORT=$DEFAULT_THRIFT_PORT
APP_MEM=$DEFAULT_APP_MEM
APP_CONF=$DEFAULT_APP_CONF
APP_SPRING_CONF=$DEFAULT_APP_SPRING_CONF

JVM_EXTRA_OPS=

isRunning() {
    local PID=$(cat "$1" 2>/dev/null) || return 1
    kill -0 "$PID" 2>/dev/null
}

doStop() {
    echo -n "Stopping $APP_NAME: "

    if isRunning $APP_PID; then
        local PID=$(cat "$APP_PID" 2>/dev/null)
        kill "$PID" 2>/dev/null
        
        TIMEOUT=30
        while isRunning $APP_PID; do
            if (( TIMEOUT-- == 0 )); then
                kill -KILL "$PID" 2>/dev/null
            fi
            sleep 1
        done
        
        rm -f "$APP_PID"
    fi
    
    echo OK
}

doStart() {
    echo -n "Starting $APP_NAME: "
    
    if [ -f "$APP_PID" ]; then
        if isRunning $APP_PID; then
            echo "Already running!"
            exit 1
        else
            # dead pid file - remove
            rm -f "$APP_PID"
        fi
    fi
    
    _startsWithSlash_='^\/.*$'

    if [ "$APP_CONF" == "" ]; then
        echo "Empty App config file"
        exit 1
    else
        if [[ $APP_CONF =~ $_startsWithSlash_ ]]; then
            FINAL_APP_CONF=$APP_CONF
        else
            FINAL_APP_CONF=$APP_HOME/conf/$APP_CONF
        fi

        if [ ! -f "$FINAL_APP_CONF" ]; then
            echo "App config file not found: $FINAL_APP_CONF"
            exit 1
        fi
    fi
    
    if [ "$APP_SPRING_CONF" == "" ]; then
        echo "Empty Spring config file"
        exit 1
    else
        if [[ $APP_SPRING_CONF =~ $_startsWithSlash_ ]]; then
            FINAL_APP_SPRING_CONF=$APP_SPRING_CONF
        else
            FINAL_APP_SPRING_CONF=$APP_HOME/conf/$APP_SPRING_CONF
        fi

        if [ ! -f "$FINAL_APP_SPRING_CONF" ]; then
            echo "Spring config file not found: $FINAL_APP_SPRING_CONF"
            exit 1
        fi
    fi
    
    RUN_CMD=($APP_HOME/bin/$APP_NAME -Dapp.home=$APP_HOME -Dthrift.port=$THRIFT_PORT -Dhttp.port=$APP_PORT -Dhttp.address=0.0.0.0)
    RUN_CMD+=(-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -J-server -mem $APP_MEM)
    RUN_CMD+=(-Dspring.profiles.active=development)
    RUN_CMD+=(-Dconfig.file=$FINAL_APP_CONF -Dspring.config.file=$FINAL_APP_SPRING_CONF)
    RUN_CMD+=($JVM_EXTRA_OPS)

    "${RUN_CMD[@]}" &
    disown $!
    echo $! > "$APP_PID"
    
    echo "STARTED $APP_NAME `date`"
    
    echo "APP_MEM         : $APP_MEM"
    echo "APP_PORT        : $APP_PORT"
    echo "THRIFT_PORT     : $THRIFT_PORT"
    echo "APP_CONF        : $FINAL_APP_CONF"
    echo "APP_SPRING_CONF : $FINAL_APP_SPRING_CONF"
    echo "APP_PID         : $APP_PID"
    echo "JVM_EXTRA_OPS   : $JVM_EXTRA_OPS"
}

usageAndExit() {
    echo "Usage: ${0##*/} <{start|stop}> [-m <JVM memory limit in mb>] [-p <http port>] [-t <thrift port>] [-s <custom spring config file>] [-c <custom app config file>] [-j "<extra jvm options>"]"
    echo "    stop : stop the server"
    echo "    start: start the server"
    echo "       -m : JVM memory limit in mb (default $DEFAULT_APP_MEM)"
    echo "       -p : Http port for REST APIs (default $DEFAULT_APP_PORT)"
    echo "       -t : Thrift port for Thrift APIs (default $DEFAULT_THRIFT_PORT)"
    echo "       -c : Custom app config file, relative file is prefixed with ./conf (default $DEFAULT_APP_CONF)"
    echo "       -s : Custom spring config file, relative file is prefixed with ./conf (default $DEFAULT_APP_SPRING_CONF)"
    echo "       -j : Extra JVM options (example: -Djava.rmi.server.hostname=localhost)"
    echo
    echo "Example: start server 64mb memory limit, with custom configuration file"
    echo "    ${0##*/} start -m 64 -c abc.conf"
    echo
    exit 1
}

ACTION=$1
shift

# parse parameters: see https://gist.github.com/jehiah/855086
_number_='^[0-9]+$'
while [ "$1" != "" ]; do
    PARAM=$1
    shift
    VALUE=$1
    shift

    case $PARAM in
        -h|--help)
            usageAndExit
            ;;

        -m)
            APP_MEM=$VALUE
            if ! [[ $APP_MEM =~ $_number_ ]]; then
                echo "ERROR: invalid memory value \"$APP_MEM\""
                usageAndExit
            fi
            ;;

        -p)
            APP_PORT=$VALUE
            if ! [[ $APP_PORT =~ $_number_ ]]; then
                echo "ERROR: invalid port number \"$APP_PORT\""
                usageAndExit
            fi
            ;;

        -t)
            THRIFT_PORT=$VALUE
            if ! [[ $THRIFT_PORT =~ $_number_ ]]; then
                echo "ERROR: invalid port number \"$THRIFT_PORT\""
                usageAndExit
            fi
            ;;

        -c)
            APP_CONF=$VALUE
            ;;

        -s)
            APP_SPRING_CONF=$VALUE
            ;;

        -j)
            JVM_EXTRA_OPS=$VALUE
            ;;

        *)
            echo "ERROR: unknown parameter \"$PARAM\""
            usageAndExit
            ;;
    esac
done

case "$ACTION" in
    stop)
        doStop
        ;;

    start)
        doStart
        ;;

    *)
        usageAndExit
        ;;
esac

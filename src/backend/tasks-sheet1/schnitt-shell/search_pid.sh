#!/bin/sh

while getopts ":h:u:p:o:" opt; do
    case $opt in
        h)
            hostfile=$OPTARG
            ;;
        u)
            user=$OPTARG
            ;;
        p)
            process=$OPTARG
            ;;
        o)
            output=$OPTARG
            ;;
        \?)
            echo "Invalid option: -$OPTARG" >&2
            exit 1
            ;;
        :)
            echo "Option -$OPTARG requires an argument." >&2
            exit 1
            ;;
    esac
done

DIR=`dirname $0`
IS_RUNNING_RELATIVE="$DIR/isrunning.sh"
IS_RUNNING="$(cd "$(dirname "$IS_RUNNING_RELATIVE")"; pwd)/$(basename "$IS_RUNNING_RELATIVE")"

for host in `cat $hostfile`; do
    SSH_HOST=$USER@$host.cip.ifi.lmu.de
    echo "Checking $SSH_HOST"

    if eval "ssh $SSH_HOST 'eval \"pgrep -u $user $process > /dev/null\" && return 0 || return 1'"; then
        echo "Running"
    else
        echo "Not running"
    fi
done
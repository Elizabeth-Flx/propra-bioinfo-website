#!/bin/sh

PROCESSNAME=$1
USERNAME=$2

eval "pgrep -u $USERNAME $PROCESSNAME > /dev/null" && return 1 || return 0

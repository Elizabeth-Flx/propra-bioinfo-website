#!/bin/sh

HOST=$1

ssh $HOST "cat /proc/loadavg | awk '{print \$2}'"
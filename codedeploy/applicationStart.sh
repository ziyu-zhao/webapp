#!/bin/bash

#java -jar /home/ubuntu/web/web.jar >/dev/null 2>&1 &
#java -jar /usr/local/web/web.jar > /usr/local/web/log

source /etc/profile
#/usr/local/web/start.sh

java -jar /usr/local/web/web.jar > /usr/local/web/log 2>/dev/null &
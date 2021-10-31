#!/bin/bash

java -jar /usr/local/web.jar > /tmp/weblog &
ps -A | grep java
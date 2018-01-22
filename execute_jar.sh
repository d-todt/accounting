#!/bin/bash

java -cp "./dist/data/lang:./dist/accounting.jar:${CLASSPATH}:/home/se110512/lib/java/java-getopt/java-getopt-1.0.14.jar" \
    application.accounting.Accounting "$@"

#!/bin/bash

java -cp "./dist/data/lang:./dist/accounting.jar:${CLASSPATH}" \
    application.accounting.Accounting "$@"

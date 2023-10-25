#!/bin/bash

set -ox pipefail

if [[ $1 == mysql || -z $1 ]]; then
  docker stop books-mysql > /dev/null 2>&1
  docker run --name books-mysql --network=host --rm -di -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=books -e MYSQL_ROOT_HOST=% mysql/mysql-server:latest
fi

if [[ $1 == app || -z $1 ]]; then
  docker stop books-app > /dev/null 2>&1
  docker rmi books-app > /dev/null 2>&1
  docker build -t books-app .
  docker run --name books-app --network=host --rm -di books-app
fi

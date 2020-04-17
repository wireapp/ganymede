#!/bin/bash

config_file=$(pwd)/.env
store_name=aves.jks

run() {
  clean
  create_env
  append_store
  start_jar
}

clean()
{
  rm "$config_file" || true
}

create_env() {
  cp .env.template "$config_file"
}

append_store() {
  cat >>"$config_file" <<-_EOF_
STORE_PATH=$(pwd)/$store_name
STORE_PASS=$store_pass
STORE_TYPE=JKS
_EOF_
}

start_jar() {
  (
    export PROPS_PATH="$config_file"
    java -jar app.jar
  )
}

usage() {
  echo "usage: ./run.sh -p key_store_password"
}


store_pass=

while [ "$1" != "" ]; do
  case $1 in
  -p | --password)
    shift
    store_pass=$1
    ;;
  -h | --help)
    usage
    exit
    ;;
  *)
    usage
    exit 1
    ;;
  esac
  shift
done

if [ -z "$store_pass" ]; then
  usage
else
  run
fi

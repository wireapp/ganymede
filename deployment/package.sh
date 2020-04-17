#!/bin/bash

output_dir=package
certificate=$output_dir/swisscom.jks

run() {
  create_certificate
  create_build
  create_tar
}

create_certificate() {
  echo "$certificate_base" | base64 -d -o "$certificate"
}

create_build() {
  (
    cd ../
    ./gradlew fatJar
    cp build/libs/app.jar deployment/$output_dir/app.jar
  )
}

create_tar() {
  tar -zcvf release.tar.gz "$output_dir"
}

usage() {
  echo "usage: ./package -c <base 64 of certificate>"
}

certificate_base=

while [ "$1" != "" ]; do
  case $1 in
  -c | --certificate)
    shift
    certificate_base=$1
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

if [ -z "$certificate_base" ]; then
  usage
else
  run
fi

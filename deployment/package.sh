#!/bin/bash

output_dir=ganymede
version_file=$output_dir/version.txt
certificate=$output_dir/swisscom.jks
app=$output_dir/app.jar

output_file=ganymede.tar.gz

run() {
  clean
  create_version_file
  create_certificate
  create_build
  create_tar
}

clean() {
  rm "$version_file" || true
  rm "$app" || true
  rm "$certificate" || true
  rm "$output_file" || true
}

create_version_file() {
  echo "$version" >"$version_file"
}

create_certificate() {
  echo "$certificate_base" | base64 -d -o "$certificate"
}

create_build() {
  (
    cd ../
    ./gradlew fatJar
    cp build/libs/app.jar deployment/$app
  )
}

create_tar() {
  tar -zcvf "$output_file" "$output_dir"
}

usage() {
  echo "usage: ./package.sh -c <base 64 of certificate> -v <version deployed>"
}

certificate_base=
version=

while [ "$1" != "" ]; do
  case $1 in
  -c | --certificate)
    shift
    certificate_base=$1
    ;;
  -v | --version)
    shift
    version=$1
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

if [ -z "$certificate_base" ] || [ -z "$version" ]; then
  usage
else
  run
fi

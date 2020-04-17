#!/bin/bash

echo "$1" | base64 -d -o package/swisscom.jks

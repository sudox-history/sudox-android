#!/bin/bash

rm -R tmp
wget https://github.com/google/libphonenumber/archive/master.zip -P tmp

unzip tmp/master.zip 'libphonenumber-master/java/libphonenumber/src/com/google/i18n/phonenumbers/data/*' -d tmp/unpacked
mkdir -p src/main/assets/com/google/i18n/phonenumbers/data
mv tmp/unpacked/libphonenumber-master/java/libphonenumber/src/com/google/i18n/phonenumbers/data/* src/main/assets/com/google/i18n/phonenumbers/data

rm -R tmp

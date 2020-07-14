#!/bin/bash

rm -R tmp
mkdir -p src/main/assets/flags
mkdir tmp
cd tmp

wget https://github.com/twitter/twemoji/archive/master.zip
unzip master.zip
cd twemoji-master/assets/72x72

START=0x1F1E6
END=0x1F1FF

for ((i=$START;i<=$END;i++))
do
STARTS_WITH="$(bc <<< "obase=16;$i")-"
STARTS_WITH="${STARTS_WITH,,}"
cp $STARTS_WITH* ../../../../src/main/assets/flags

done

unset STARTS_WITH
unset START
unset END

cd ../../../..
rm -R tmp

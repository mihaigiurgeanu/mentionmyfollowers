#!/bin/sh
ver=0.1.0-SNAPSHOT
rm dist/*.jar
lein ring uberjar
cp target/uberjar/mentionmyfollowers-${ver}-standalone.jar dist/
rm mentionmyfollowers-${ver}.zip
zip -r mentionmyfollowers-${ver}.zip dist/


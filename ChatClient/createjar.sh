#!/bin/bash

cp src/*.class build
cd build
jar cmf ../res/MANIFEST.MF ChatClient.jar *.class
chmod +x ChatClient.jar
rm *.class

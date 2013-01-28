#!/bin/bash

cp src/*.class build
cd build
jar cmf ../res/MANIFEST.MF ChatServer.jar *.class
chmod +x ChatServer.jar
rm *.class

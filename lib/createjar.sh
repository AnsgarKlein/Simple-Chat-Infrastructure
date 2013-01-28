#!/bin/bash

cp ChatProtocol/*.class build
cd build
jar cf ChatProtocol.jar *.class
chmod +x ChatProtocol.jar
rm *.class

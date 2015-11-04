#!/usr/bin/env bash

usage="./start.sh path/to/image/file"

javaLibraryPath=/usr/local/share/OpenCV/java
javaClasspath=.:/usr/local/share/OpenCV/java/opencv-300.jar
appName=OCRApplication

if [ $# -eq 1 ]
then
    echo $javaClasspath
    echo $javaLibraryPath
    java -Djava.library.path=$javaLibraryPath -cp $javaClasspath $appName $1
else
    echo $usage
fi
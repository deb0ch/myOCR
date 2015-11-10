#!/usr/bin/env bash

usage="path/to/image/file"

javaLibraryPath=/usr/local/share/OpenCV/java
javaClasspath=.:/usr/local/share/OpenCV/java/opencv-300.jar
appName=OCRApplication

if [ $# -eq 1 ]
then
    java -Djava.library.path=$javaLibraryPath -cp $javaClasspath $appName $1
else
    echo $0 $usage
fi

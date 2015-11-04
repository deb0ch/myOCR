#!/usr/bin/env bash

javaLibraryPath=/usr/local/share/OpenCV/java
javaClasspath=.:/usr/local/share/OpenCV/java/opencv-300.jar

if [ $# -eq 1 ]
then
    java -Djava.library.path=$javaLibraryPath -cp
fi
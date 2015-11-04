#!/usr/bin/env bash



if [ $# eq 1 ]
then
    java -Djava.library.path=/usr/local/share/OpenCV/java -cp .:/usr/local/share/OpenCV/java/opencv-300.jar
fi
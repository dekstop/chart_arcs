#!/bin/sh

function doit() {
    java -classpath /Users/mongo/Documents/code/_visualizations/chart_arcs/bin/:/Users/mongo/src/processing/build/macosx/work/lib/core.jar fm.last.visualizations.arc.ChartArcs "lastfm_team/${1}.xml" -batch
    convert images/${1}.tga html/images/${1}.png
    convert -resize 150x150 -unsharp 1.5x1.2+0.8+0.10 images/${1}.tga html/images/${1}_sm.png
}

doit RNR
doit RJ
doit mainstream
doit crshamburg
doit spencerhyman
doit mischa
doit Russ
doit muesli
doit flaneur
doit skr
doit mokele
doit InvisibleA
doit pete_bug
doit sharevari
doit hannahdonovan
doit nova77LF
doit vincro
doit sideb0ard
doit mustaqila
doit MissHimi
doit joanofarctan
doit fionapinkstars
doit julians
doit lumberjack
doit Jonty
doit martind
doit hoff

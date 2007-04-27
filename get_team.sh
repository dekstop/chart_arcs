#!/bin/sh

function doit() {
    php -f artistcharts_for_user.php "${1}" | tee "${1}.xml"
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

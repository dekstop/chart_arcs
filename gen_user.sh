#!/bin/sh
ssh martind@gimli.last.fm "php -f chart_arcs/artistcharts_for_user.php ${1}" | tee "data/$1.xml"
java -classpath /Users/mongo/Documents/code/_visualizations/chart_arcs/bin/:/Users/mongo/src/processing/build/macosx/work/lib/core.jar fm.last.visualizations.arc.ChartArcs "data/$1.xml"
convert images/${1}.tga images/${1}.png
echo "-> images/${1}.png" 
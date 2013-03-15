#!/bin/sh
APP_HOME=`dirname "$0"`
java -Xmx1500m -classpath ${APP_HOME}/bin/:${APP_HOME}/lib/* fm.last.visualizations.arc.ChartArcs $@

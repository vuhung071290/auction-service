#!/bin/sh

JVM_OPTS="\
        -Dfile.encoding=UTF-8 \
        -XX:SurvivorRatio=8 \
        -XX:TargetSurvivorRatio=90 \
        -XX:+AggressiveOpts \
        -XX:+UseParNewGC \
        -XX:+UseConcMarkSweepGC \
        -XX:+CMSParallelRemarkEnabled \
        -XX:MaxTenuringThreshold=2 \
        -Xmx4G \
        -Xms512M"

java $CONF_JVM $JVM_OPTS -jar *.jar

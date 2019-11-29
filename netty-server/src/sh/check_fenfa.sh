#!/bin/bash
source /etc/profile
jps -l | grep netty-server-0.0.1-jar-with-dependencies.jar &>/dev/null
#echo $fenfa
if [ $? -eq 0 ];then
 #  exit
 echo data_fenfa OK `date +%c`>>/opt/data_fen.txt
    exit
else
 echo data_fenfa Fail `date +%c` >>/opt/data_fen.txt
 cd /root/zhanglu/
 nohup java -jar netty-server-0.0.1-jar-with-dependencies.jar 1>server.out 2>&1 &
fi

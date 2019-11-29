#! /bin/bash
source /etc/profile
cd /root/zhanglu
nohup java -jar netty-server-0.0.1-jar-with-dependencies.jar 1>server.out 2>&1 & 

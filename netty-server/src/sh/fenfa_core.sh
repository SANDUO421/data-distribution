#! /bin/bash
cd /root/zhanglu
nohup java -jar data-receive-core-server-0.0.1.jar 1>data.out 2>&1 &

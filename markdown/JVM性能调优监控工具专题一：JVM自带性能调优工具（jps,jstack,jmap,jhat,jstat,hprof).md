jps(Java Virtual Machine Process Status Tool)      ： 基础工具

       实际中这是最常用的命令，下面要介绍的小工具更多的都是先要使用jps查看出当前有哪些Java进程，获取该Java进程的id后再对该进程进行处理。

 

    jps主要用来输出JVM中运行的进程状态信息。语法格式如下：

 

Java代码  收藏代码
jps [options] [hostid]  
 如果不指定hostid就默认为当前主机或服务器。

 

  命令行参数选项说明如下：

 

Java代码  收藏代码
-q 不输出类名、Jar名和传入main方法的参数  
-m 输出传入main方法的参数  
-l 输出main类或Jar的全限名  
-v 输出传入JVM的参数  
 

  

比如

1、我现在有一个WordCountTopo的Strom程序正在本机运行。

2、使用java -jar deadlock.jar & 启动一个线程死锁的程序 

 

 

Java代码  收藏代码
wangsheng@WANGSHENG-PC /E  
$ jps -ml  
14200 deadlock.jar  
13952 com.wsheng.storm.topology.WordCountTopo D://input/ 3  
13248 sun.tools.jps.Jps -ml  
9728  
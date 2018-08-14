### 分布式文件系统：HDFS
* 参考地址:  http://www.360doc.com/content/18/0729/18/58224932_774231051.shtml
* 名词介绍：
	* Datanode： 实际存储的地方
	* Namenode： 元数据信息： 哪个数据块，在哪个datanode 哪个目录下；存在内存中？？->导致不能有太多文件的数量
	* Client： 客户端
	* Block： 数据文件拆成数据块去存储
	* Replication：副本	

* 写流程
	* 客户端发起远程请求，给namenode
	* namenode检测文件是否存在，权限，如果操作成功，则返回正常操作码
	* 客户端开始写数据，将要写的内容切分多个数据包packets， 并在内部以data queue维护； 并向namenode 申请block;
	* 以pipeline（管道）的形式将packet写入所有的replication中 ；当最后一个datanode存储成功后，给客户端返回一个ack标志；
	* 如果其中一个datanode失败，则会namenode会重新分配一个新的DataNode，保持replication设定的数量。

* 读流程
	* 使用HDFS提供的客户端，向远程的Namenode发起请求；
	* NameNode会视情况返回文件的部分或者全部block列表，对于每个block，NameNode都会返回有该block拷贝的Datanode地址
	* 客户端会选取离客户端最接近的Datanode来读取block；
	* 读取完当前block的数据后，关闭与当前的Datanode连接，并为读取下一个block寻找最佳的Datanode； 

* 问题：
	* 1、多个客户端向同一个文件写数据，那么我们该怎么处理？
	* 2、Namenode如果挂掉 怎么保证系统正常工作？

* A-0: 为了保证数据的可靠性，HDFS采取了副本的策略
* C: 简单的一致性模型，为一次写入，多次读取一个文件的访问模式，支持追加(append)操作，但无法更改已写入数据.一个文件同一时间内只允许一个客户端操作，且当写入的副本数=dfs.namenode.replication.min时，则认为文件数据写入成功；但此时文件被标记为unreplication，会继续向其他datanode写入数据；因此当dfs.namenode.replication.min=dfs.replication时，数据一致性是在写阶段确定的；
* A-1:可用性
	* 一、 NFS: Network File System 共享存储的方案，
	* Namenode的高可用性是通过为其设置一个Standby Namenode来实现的，要是目前的Namenode挂掉了，就启用备用的Namenode。而两个Namenode之间通过共享的存储来同步信息，以下是一些要点
	* 利用共享存储来在两个NameNode间同步edits信息
	* DataNode同时向两个NameNode汇报块信息
	* 用于监视和控制NameNode进程的FailoverController进程
	* 隔离(Fencing)：就是保证在任何时候只有一个主NameNode，包括三个方面
		* 共享存储fencing，确保只有一个NameNode可以写入edits
		* 客户端fencing，确保只有一个NameNode可以响应客户端的请求
		* DataNode fencing，确保只有一个NameNode可以向DataNode下发命令，譬如删除块，复制块  
	
	
	
	* 二、QJM : 让Standby Node与Active Node保持同步
		* 就是为了让Standby Node与Active Node保持同步，这两个Node都与一组称为JNS(Journal Nodes)的互相独立的进程保持通信。它的基本原理就是用2N 1台JournalNode存储edits文件，每次写数据操作有大多数（大于等于N 1）返回成功时即认为该次写成功 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
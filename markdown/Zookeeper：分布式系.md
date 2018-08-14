### Zookeeper：分布式系统
* agenda：
	*  什么是一致性：a:cap base模型  b:一致性模型
	*  强一致性算法： paxos、raft、zab
	*  项目实战：zk、etcd

* 1、cap base模型:
	* C	: (强)一致性是指数据在多个副本之间能否保持一致的特性
	* A	: 指一个系统提供的服务必须一直处于可用的状态，对于每一个客户端提交的操作，总是能在有限的时间内提供返回结果
		*  强调的点：1、有限时间，任何系统服务设计之初都会设计好返回结果的超时时间，如果超过这个时间就认为是不可用的；2、返回结果可用性也是一个指标，每个请求必须返回一个正常的结果，不能返回一个客户端不能处理的结果
	* P	: 分区容错，由于网络等原因导致网络断开，也可能是机器宕机、网络延时导致数据交换没办法在规定的时间内完成
	* 任何的数据模型都应该抽象为：Query=Function(all data)——hadoop hdfs mapreduce	

	
* 2、一致性模型：弱一致性，最终一致性：dns,gossip(cassandra通信协议)。 举例：域名 test.lhp.com ->1.1.1.1 -> 最终一致性
* 3、明切问题：1、数据不能存在单个节点上 2、分布式系统对于fault tolorence 一般采用的方案是 state mathine replication。状态机可以理解为一个函数，有一个初始值，经过input和一系列变化，变成一个最终值。而分布式系统一般采取的策略是把一个个的变化采用记录log的方式复制到各个节点的状态机上。因此这节讨论的问题应该叫做state mathine replication的consensus共识算法 和 合理的client行为。
* 强一致性算法：	
	* 1、主从同步：1、master负责写请求、2、master复制数据到slave上 3、master等待，直到所有从节点返回 。 问题：master阻塞，节点失败会导致集群不可用
	* 2、多数派：写入大于n/2个节点成功，读大于n/2个节点中读。 问题： 并发情况下，无法保证对于一个值修改的请求的顺序性。	
	* 3、pasos协议：basic 、multi、fast 三个版本。城邦法律。
		* 角色：
			* 1、client:请求发起者 民众 
			* 2、proposer接收client请求，向集群提出提议propose；当
			* 3、aceptor:提议投票和接收者 类似国会（多数派投票）
			* 4、learner:提议接收者
		* 阶段：
			* 1.1、prepare: proposer提出一个编号为N的提案，此编号大于之前提出的提案编号
			* 1.2、promise：N必须大于之前接收到的提案编号，否则拒绝，重新提交一个编号大于N的提案重新提交；！！
			* 2.1、accept:如果到达了多数派，proposer会发出accpt请求，此次请求包括请求提案编号和对应的提案内容
			* 2.2、如果acceptor在此期间，没收到编号大于n的题按，则接受此提案内容，否则拒绝
			* Message flow: Basic Paxos
			first round is successful)
				Client   Proposer    Acceptor     Learner
			   |         |          |  |  |       |  |
			   X-------->|          |  |  |       |  |  Request
			   |         X--------->|->|->|       |  |  Prepare(1)
			   |         |<---------X--X--X       |  |  Promise(1,{Va,Vb,Vc})
			   |         X--------->|->|->|       |  |  Accept!(1,Vn)
			   |         |<---------X--X--X------>|->|  Accepted(1,Vn)
			   |<---------------------------------X--X  Response
			   |         |          |  |  |       |  |
		  
		   * basic paxos问题：难实现，效率低，活锁
		   * multi paxos:leader作为唯一一个 Proposer，所有请求都经过leader
		
		* 3、raft算法：
			* 划分三个子问题：leader 选举、log replication、safety、
			* 角色：leader、follower、candicate
			* 动画理解：	http://thesecretlivesofdata.com/raft/
			* 1、Leader Election：If followers don't hear from a leader then they can become a candidate.The candidate becomes the leader if it gets votes from a majority of nodes.All changes to the system now go through the leader.
			* 2、Log Replication
			* 3、客户端超时，有可能会写入成功，也有可能会被数据覆盖，最终交给客户端去处理这个事情。但是集群的一致性是可以保证的

			
		* 4、zab算法：基本和raft相同	
			
			
			
			
			
			
###分布式系统消息中间件——MMB
* 简介：可以针对分布式系统进行接入，针对的分布式系统通用架构是多个主节点（NodeManager），和多个任务处理节点（TaskNode）,该种架构的系统比较普遍，比较典型的是kunkka和mml都是多个主节点和多个执行节点的架构设计，为了解决期间的高可用架构需求，因此设计第三方中间件产品

* 分布式系统通信的痛点
	* 备注：下面主节点统称为NodeManager， 检测NM; 从节点统称为TaskNode，检测TN
	* 1、多个NodeManager点情况下，选举一个主节点的问题
	* 2、单个NodeManager情况下，系统宕机期间，消息丢失问题
	* 3、TaskNode宕机期间消息丢失问题
	* 4、NodeManager和TaskNode 任何节点宕机告警检测问题
	* 5、某个NodeManager针对其他NodeManager广播问题
	* 6、NodeManager针对所有NodeManager广播问题
	* 7、TaskNode部署期间通信问题（消息不丢失，消息顺序问题）
	* 8、批量TaskNode部署问题
	* 9、主节点NodeManager部署期间，消息可靠传输问题


kafka:

* 什么是消费者组:
	* consumer group下可以有一个或多个consumer instance，consumer instance可以是一个进程，也可以是一个线程
	* group.id是一个字符串，唯一标识一个consumer group
	* consumer group下订阅的topic下的每个分区只能分配给某个group下的一个consumer(当然该分区还可以被分配给其他group)
*  消费者位置(consumer position)
	*	消费者在消费的过程中需要记录自己消费了多少数据，即消费位置信息。在Kafka中这个位置信息有个专门的术语：位移(offset)。很多消息引擎都把这部分信息保存在服务器端(broker端)。这样做的好处当然是实现简单，但会有三个主要的问题：1. broker从此变成有状态的，会影响伸缩性；2. 需要引入应答机制(acknowledgement)来确认消费成功。3. 由于要保存很多consumer的offset信息，必然引入复杂的数据结构，造成资源浪费。而Kafka选择了不同的方式：每个consumer group保存自己的位移信息，那么只需要简单的一个整数表示位置就够了；同时可以引入checkpoint机制定期持久化，简化了应答机制的实现。

* Rebalance：rebalance本质上是一种协议，规定了一个consumer group下的所有consumer如何达成一致来分配订阅topic的每个分区
	* 触发条件：
		* 组成员发生变更(新consumer加入组、已有consumer主动离开组或已有consumer崩溃了——这两者的区别后面会谈到)
		* 订阅主题数发生变更——这当然是可能的，如果你使用了正则表达式的方式进行订阅，那么新建匹配正则表达式的topic就会触发rebalance
		* 订阅主题的分区数发生变更 
	
	
	
* 由于partition存在副本的概念，Kafka的生产者Producer将消息直接发送给Partition分区的Leader节点，其他节点follower只是单调的和leader 跟进,同步消息即可。有点像redis的master和slaven机制 主节点进行写操作，slave节点进行同步读取操作。
* 生产者高可用：1、同步|异步、批量 2、发送数据元语：ack 3、retry


* produce: push模式； consumer：pull模式

* producer如何知道有多少个broker，应该把消息发送到哪个broker？ consumer如何知道有多少个broker，应该从哪个broker取数据？
* 1、producer属于sdk形式存在，初始化时知道broker地址列表，只要有一个连接成功，就能从broker获取到对应的所有broker的元信息保存到producer的内存里，然poducer会定期刷新元信息内存，当消息发送失败时也会触发刷新元信息内存；
* 2、consumer通过连接zookeeper来获取broker的元信息
* 至于producer应该把消息发送到哪个broker， consumer应该从哪个broker获取数据 ，这就涉及到topic和partition的概念设计
* topic 逻辑的概念: 消息只能属于一个topic, 有点类似于table的概念  ； 不管是消费还是生产，都必须指定特定的topic; partition有点类似hbase里面的region
* partition:物理的概念，对应一个文件夹，里面包括多个segment。清除过期日志，直接请求segment即可;
* producer 根据hashpartiotion策略 来决定数据应该存到那个partition里， robin是轮训的策略
* sync producer : 低延迟 低吞吐 无数据丢失
* aync producer: 高延迟 高吞吐 可能数据丢失


* 数据复制与failover
* cap： 理论
* 解决一致性的方案：
	* 1、maser slave：读写分离; 同步复制（保持数据强一致性, 但是slave多 会影响可用性） vs 异步复制（最终一致性，如果slave挂了，数据会不一致， 提高可用性）
	* 2、WRN:去中心的p2p分布式数据库中 ; N表示副本数，W表示每次写操作至少成功的副本数， R表示每次读操作 至少读取的副本数
	* w+r>n 每次读取的数据至少有一个副本有最新的更新
	* 3、paxos：zab chhubby raft 
 
* replica:partition的副本  默认均匀分布在各个broker上
* 1、如何propagate消息  ：
	* 写数据：针对每一个topic的每个partition都有一个主节点 叫做leader, 负责接收写请求，然后从对应的topic的partition节点follower定期去leader 拉取pull数据； 
	* 读数据 也只能通过leader？？？

* 2、何时commit : commit就是leader告诉数据写成功了 就表示commit了；ISR机制: in sync replica; leader维护一个replica列表，写数据，foloower会发送ack信息给leader，当所有follower都发送ack后，则认为成功commit；同步数据落后（时间或者是条数）太多，则会把对应的followe剔除出去； kafka相当于在 一致性和高可用性之间做了一个动态策略的调整；   备注 consumer只能读取到commit的数据

* 如何处理replica恢复  ： 如果leader节点的数据在没有被commit的情况下挂机了，leader再次启动后，是会丢失的，因此producer要不停的retry操作，于是就会造成数据顺序错乱； 如果全部宕机:1、等待isr中任意一个replica恢复 提高数据一致性， 2、等待第一个恢复的replica 提高可用性 作为leader  


* zookeeper: 一个leader 多个follower ;leader负责写操作，所有写操作顺序，leader、follower负责读操作。
* zab协议：
	* 1、广播模式：Leader将所有更新（称为proposal），顺序发送给Follower；当Leader收到半数以上的Follower对此proposal的ACK时，即向所有Follower发送commit消息，并在本地commit该消息
	* 2、恢复模式：进入恢复模式-> 当Leader宕机或者丢失大多数Follower后，即进入恢复模式 ;结束恢复模式 新领导被选举出来，且大多数Follower完成了与Leader的状态同步后，恢复模式即结束，从而进入广播模式 ;   恢复阶段的保证:1、若一条消息在一台机器上被deliver，那么该消息必须将在每台机器上deliver，即使那台机器故障了 2、一个被skip的消息，必须仍然需要被skip 
* Zookeeper一致性保证
	* 1、顺序一致性 从一个客户端发出的更新操作会按发送顺序被顺序执
	* 2、原子性 更新操作要么成功要么失败，无中间状态
	* 3、单一系统镜像 一个客户端只会看到同一个view，无论它连到哪台服务器
	* 4、实时性 保证客户端可在一定时间（通常是几十秒）内看到最新的视图无论它连到哪台服务器
	* 5、可靠性
		Ø 一旦一个更新被应用，该更新将被持久化，直到有客户端更新该结果
		Ø 如果一个客户端得到更新成功的状态码，则该更新一定已经生效
		Ø 任何一个被客户端通过读或者更新“看到”的结果，将不会被回滚，即使是从失败中恢复 
* Zookeeper使用注意事项
	* 	只保证同一客户端的单一系统镜像，并不保证多个不同客户端在同一时刻一定看到同一系统镜像，如果要实现这种
效果，需要在读取数据之前调用sync操作
	* Ø Zookeeper读性能好于写性能，因为任何Server均可提供读服务，而只有Leader可提供写服务
	* Ø 为了保证Zookeeper本身的Leader Election顺利进行，通常将Server设置为奇数
	* Ø 若需容忍f个Server的失败，必须保证有2f+1个以上的Server
			
	
* kafka: zookeeper: 配置管理、Leader Election、服务发现
	
	
	
* Kafka High Availability	
	* 1、topic partition
	* 2、partition leader Election & failover
	* 3、broker controller leader Election & failover
	
	
	一、partition
	1、什么是topic partition
	partiton为了减少在生产数据对topic造成的压力，因此对一个业务数据的管道topic需要拆分成多个分区进行存储；hbase hregion, rdbms partition;parition是物理上的概念，每个topic包含一个或多个partition，创建topic时可指定parition数量。每个partition对应于一个文件夹，该文件夹下存储该partition的数据和索引文件。
	
	
 	2、为何需要Replication: 防止数据所在的服务器故障，导致数据丢失；在Kafka在0.8以前的版本中，是没有Replication的，一旦某一个Broker宕机，则其上所有的Partition数据都不可被消费，这与Kafka数据持久性及Delivery Guarantee的设计目标相悖。同时Producer都不能再将数据存于这些Partition中
	3、什么是leader partition 
 　　 引入Replication之后，同一个Partition可能会有多个Replica，而这时需要在这些Replica中选出一个Leader，Producer和Consumer只与这个Leader交互，其它Replica作为Follower从Leader中复制数据。　
	4、为何需要leader partition 
	　因为需要保证同一个Partition的多个Replica之间的数据一致性。（其中一个宕机后其它Replica必须要能继续服务并且即不能造成数据重复也不能造成数据丢失）。如果没有一个Leader，所有Replica都可同时读/写数据，那就需要保证多个Replica之间互相（N×N条通路）同步数据，数据的一致性和有序性非常难保证，大大增加了Replication实现的复杂性，同时也增加了出现异常的几率。而引入Leader后，只有Leader负责数据读写，Follower只向Leader顺序Fetch数据（N条通路），系统更加简单且高效。
		5、既然知道了partition的目的,那么就要保证partion均匀分配到整个集群上，那么如何去做这个事情呢？
	算法如下：
			1、将所有Broker（假设共n个Broker）和待分配的Partition排序
			2、将第i个Partition分配到第（i mod n）个Broker上
			3、将第i个Partition的第j个Replica分配到第（(i + j) mod n）个Broker上
		举例：假如已经部署了3（序号 0、1、2）个broker,一个topic设了6个partition,每个partition设置的replica=3，则如果第4个partition存在第（4%3）=1个broker,那么该partition的第二个Replica存在（4+2）%6 = 0个broker，;
		6、既然已经设计了parition的方式，那么对于一条数据是如何发生，以及如何同步到partition的replica呢？
		Producer在发布消息到某个Partition时，先通过Zookeeper找到该Partition的Leader，然后无论该Topic的Replication Factor为多少（也即该Partition有多少个Replica），Producer只将该消息发送到该Partition的Leader。Leader会将该消息写入其本地Log。每个Follower都从Leader pull数据。这种方式上，Follower存储的数据顺序与Leader保持一致。Follower在收到该消息并写入其Log后，向Leader发送ACK。一旦Leader收到了ISR中的所有Replica的ACK，该消息就被认为已经commit了，Leader将增加HW并且向Producer发送ACK。
		Kafka的复制机制既不是完全的同步复制，也不是单纯的异步复制。事实上，同步复制要求所有能工作的Follower都复制完，这条消息才会被认为commit，这种复制方式极大的影响了吞吐率（高吞吐率是Kafka非常重要的一个特性）。而异步复制方式下，Follower异步的从Leader复制数据，数据只要被Leader写入log就被认为已经commit，这种情况下如果Follower都复制完都落后于Leader，而如果Leader突然宕机，则会丢失数据。而Kafka的这种使用ISR的方式则很好的均衡了确保数据不丢失以及吞吐率。Follower可以批量的从Leader复制数据，这样极大的提高复制性能（批量写磁盘），极大减少了Follower与Leader的差距。
		　需要说明的是，Kafka只解决fail/recover，不处理“Byzantine”（“拜占庭”）问题。一条消息只有被ISR里的所有Follower都从Leader复制过去才会被认为已提交。这样就避免了部分数据被写进了Leader，还没来得及被任何Follower复制就宕机了，而造成数据丢失（Consumer无法消费这些数据）。而对于Producer而言，它可以选择是否等待消息commit，这可以通过request.required.acks来设置。这种机制确保了只要ISR有一个或以上的Follower，一条被commit的消息就不会丢失
		7、Leader Election算法
		如果Leader不在了，新的Leader必须拥有原来的Leader commit过的所有消息；这就需要作一个折衷，如果Leader在标明一条消息被commit前等待更多的Follower确认，那在它宕机之后就有更多的Follower可以作为新的Leader，但这也会造成吞吐率的下降。　
	  一种非常常用的Leader Election的方式是“Majority Vote”（“少数服从多数”），但Kafka并未采用这种方式。这种模式下，如果我们有2f+1个Replica（包含Leader和Follower），那在commit之前必须保证有f+1个Replica复制完消息，为了保证正确选出新的Leader，fail的Replica不能超过f个。因为在剩下的任意f+1个Replica里，至少有一个Replica包含有最新的所有消息。这种方式有个很大的优势，系统的latency只取决于最快的几个Broker，而非最慢那个。Majority Vote也有一些劣势，为了保证Leader Election的正常进行，它所能容忍的fail的follower个数比较少。如果要容忍1个follower挂掉，必须要有3个以上的Replica，如果要容忍2个Follower挂掉，必须要有5个以上的Replica。也就是说，在生产环境下为了保证较高的容错程度，必须要有大量的Replica，而大量的Replica又会在大数据量下导致性能的急剧下降。这就是这种算法更多用在Zookeeper这种共享集群配置的系统中而很少在需要存储大量数据的系统中使用的原因。例如HDFS的HA Feature是基于majority-vote-based journal，但是它的数据存储并没有使用这种方式。
	  8、如何处理所有Replica都不工作
		上文提到，在ISR中至少有一个follower时，Kafka可以确保已经commit的数据不丢失，但如果某个Partition的所有Replica都宕机了，就无法保证数据不丢失了。这种情况下有两种可行的方案：
		1、等待ISR中的任一个Replica“活”过来，并且选它作为Leader
		2、选择第一个“活”过来的Replica（不一定是ISR中的）作为Leader
　　这就需要在可用性和一致性当中作出一个简单的折衷。如果一定要等待ISR中的Replica“活”过来，那不可用的时间就可能会相对较长。而且如果ISR中的所有Replica都无法“活”过来了，或者数据都丢失了，这个Partition将永远不可用。选择第一个“活”过来的Replica作为Leader，而这个Replica不是ISR中的Replica，那即使它并不保证已经包含了所有已commit的消息，它也会成为Leader而作为consumer的数据源（前文有说明，所有读写都由Leader完成）。Kafka0.8.*使用了第二种方式。根据Kafka的文档，在以后的版本中，Kafka支持用户通过配置选择这两种方式中的一种，从而根据不同的使用场景选择高可用性还是强一致性。 　　
	9、如何选举Leader
	动删除，此时所有Follower都尝试创建该节点，而创建成功者（Zookeeper保证只有一个能创建成功）即是新的Leader，其它Replica即为Follower。
　　但是该方法会有3个问题： 　　

split-brain 这是由Zookeeper的特性引起的，虽然Zookeeper能保证所有Watch按顺序触发，但并不能保证同一时刻所有Replica“看”到的状态是一样的，这就可能造成不同Replica的响应不一致
herd effect 如果宕机的那个Broker上的Partition比较多，会造成多个Watch被触发，造成集群内大量的调整
Zookeeper负载过重 每个Replica都要为此在Zookeeper上注册一个Watch，当集群规模增加到几千个Partition时Zookeeper负载会过重。
　　Kafka 0.8.*的Leader Election方案解决了上述问题，它在所有broker中选出一个controller，所有Partition的Leader选举都由controller决定。controller会将Leader的改变直接通过RPC的方式（比Zookeeper Queue的方式更高效）通知需为此作出响应的Broker。同时controller也负责增删Topic以及Replica的重新分配
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

		
		

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
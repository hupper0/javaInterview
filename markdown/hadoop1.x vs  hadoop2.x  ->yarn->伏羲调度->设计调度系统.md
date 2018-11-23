#### 前言：
* 在hadoop 1.x中和hadoop 2.x中，mapreduce的执行流程也不一样（完全不一样），在hadoop1.x中 mapreduce的 资源管理与分配和任务监控都是集中在 jobtracker 上，这样会造成jobtracker的负担非常大，而且在hadoop 1.x中没有jobtracker的HA机制，从而会让集群的健壮性很差


#### 1、hadoop1.x mapreduce执行过程

###### 简述：client端提交job给jobtracker，jobtracker会给这个job分配资源，在tasktracker上启动task任务，而且还要监控task任务的状况，如果task挂了，jobtracker还得重新分配新的资源给挂了的task任务，当task执行完成后，jobtracker会为reduce任务分配资源，然后监控reduce的执行流程，最后执行完成输出

* 1、客户端（Client）：编写mapreduce程序，配置作业，提交作业，启动Jobclient进程。
* 2、Jobclient向JobTracker请求一个Job ID， 也就是作业ID。
* 3、Jobclient拷贝计算需要的代码等文件
	* Jobclient将运行作业所需要的资源文件复制到HDFS上，包括MapReduce程序打包的JAR文件、配置文件和客户端计算所得的输入划分信息。这些文件都存放在JobTracker专门为该作业创建的文件夹中。文件夹名为该作业的Job ID。JAR文件默认会有10个副本.
   * mapred.submit.replication属性控制；输入划分信息告诉了JobTracker应该为这个作业启动多少个map任务等信息
* 4、JobTracker接收到作业后，放入调度队列，等待调度
	* JobTracker接收到Jobclient提交的作业后，将其放在一个作业队列里，等待作业调度器对其进行调度
* 5、在map task开始执行时
	* 它的输入数据来源于HDFS的block，当然在MapReduce概念中，map task只读取split。Split与block的对应关系可能是多对一，默认是一对一。
	* 读取输入文件内容，解析成key、value对。对输入文件的每一行，解析成key、value对。每一个键值对调用一次map函数，转换成新的key、value输出。
	* map任务执行过程中溢写执行过程：当map task的输出结果很多时，就可能会撑爆内存，所以需要在一定条件下将缓冲区中的数据临时写入磁盘，然后重新利用这块缓冲区。这个从内存往磁盘写数据的过程被称为Spill，中文可译为溢写，字面意思很直观。这个溢写是由单独线程来完成，不影响往缓冲区写map结果的线程。溢写线程启动时不应该阻止map的结果输出，所以整个缓冲区有个溢写的比例spill.percent。这个比例默认是0.8，也就是当缓冲区的数据已经达到阈值（buffer size * spill percent = 100MB * 0.8 = 80MB），溢写线程启动，锁定这80MB的内存，执行溢写过程。Map task的输出结果还可以往剩下的20MB内存中写，互不影响。 当溢写线程启动后，需要对这80MB空间内的key做排序(Sort)。排序是MapReduce模型默认的行为，这里的排序也是对序列化的字节做的排序。此处参考另外一篇文章《MapReduce之mapOutputBuffer解析》
	* 当map task真正完成时，内存缓冲区中的数据也全部溢写到磁盘中形成一个溢写文件。
		* 最终磁盘中会至少有一个这样的溢写文件存在(如果map的输出结果很少，当map执行完成时，只会产生一个溢写文件)，因为最终的文件只有一个，所以需要将这些溢写文件归并到一起，这个过程就叫做Merge。Merge是怎样的？如前面的例子，“aaa”从某个map task读取过来时值是5，从另外一个map 读取时值是8，因为它们有相同的key，所以得merge成group。什么是group。对于“aaa”就是像这样的：{“aaa”, [5, 8, 2, …]}，数组中的值就是从不同溢写文件中读取出来的，然后再把这些值加起来。请注意，因为merge是将多个溢写文件合并到一个文件，所以可能也有相同的key存在，在这个过程中如果client设置过Combiner，也会使用Combiner来合并相同的key
	* 至此，map端的所有工作都已结束：tasktarcker通知JobTracker，map任务执行完毕，并告知数据输出路径，JobTracker启动reduce任务
		* 最终生成的这个文件也存放在TaskTracker够得着的某个本地目录内。每个reduce task不断地通过RPC从JobTracker那里获取map task是否完成的信息，如果reduce task得到通知，获知某台TaskTracker上的map task执行完成，Shuffle的后半段过程开始启动。 简单地说，reduce task在执行之前的工作就是不断地拉取当前job里每个map task的最终结果，然后对从不同地方拉取过来的数据不断地做merge，也最终形成一个文件作为reduce task的输入文件


* 5、Partitioner： 该主要在Shuffle过程中按照Key值将中间结果分成R份，其中每份都有一个Reduce去负责，可以通过job.setPartitionerClass()方法进行设置，默认的使用hashPartitioner类。实现getPartition函数

* 6、在reduce task开始执行
	*  COPY过程，简单地拉取数据
		*  Reduce进程启动一些数据copy线程(Fetcher)，通过HTTP方式请求map task所在的TaskTracker获取map task的输出文件。因为map task早已结束，这些文件就归TaskTracker管理在本地磁盘中。
	* Merge阶段:这里的merge如map端的merge动作，只是数组中存放的是不同map端copy来的数值.
		* Copy过来的数据会先放入内存缓冲区中，这里的缓冲区大小要比map端的更为灵活，它基于JVM的heap size设置，因为Shuffle阶段Reducer不运行，所以应该把绝大部分的内存都给Shuffle用.
		* merge三种形式 :
			* 1)内存到内存: 默认情况关闭)
			* 2)内存到磁盘: 当内存中的数据量到达一定阈值
			* 3)磁盘到磁
		* 与map 端类似，这也是溢写的过程，这个过程中如果你设置有Combiner，也是会启用的
		* 第二种merge方式一直在运行，直到没有map端的数据时才结束，然后启动第三种磁盘到磁盘的merge方式生成最终的那个文件
	* Reducer的输入文件：不断地merge后，最后会生成一个“最终文件”
		* 为什么加引号？因为这个文件可能存在于磁盘上，也可能存在于内存中。对我们来说，当然希望它存放于内存中，直接作为Reducer的输入，但默认情况下，这个文件是存放于磁盘中的。当Reducer的输入文件已定，整个Shuffle才最终结束。然后就是Reducer执行，把结果放到HDFS上。


	![avatar](http://dl.iteye.com/upload/attachment/0066/0128/8aab5880-d171-30f7-91d6-aaacba2d03ce.jpg)

####2、Yarn简介
* Yarn采用Master/Slave结构，总体上采用了双层调度架构。
	* 在第一层的调度是ResourceManager和NodeManager。ResourceManager是Master节点，相当于JobTracker，包含Scheduler和App Manager两个组件。这两个组件分管资源调度和应用管理；NodeManager是Slave节点，可以部署在独立的机器上，用于管理机器上的资源。NodeManager会向ResourceManager报告它的资源数量、使用情况并接受ResourceManager的资源调度。
	* 第二层的调度指的是NodeManager和Container。NodeManager会将Cpu&内存等资源抽象成一个个的Container，并管理这些Container的生命周期。

* yarn架构组件分析
	* ResourceManager（master服务）：负责资源管理的主服务，整个系统只有一个，它包含两个组件Scheduler和ApplicationManager
	  * 1。Scheduler用于调度集群中的各种队列，应用。在Hadoop的MapReduce框架中主要有Capacity Scheduler和Fair Scheduler
	  * 2. ApplicationManager主要负责接收Job的提交请求，为应用分配第一个Container来运行ApplicationMaster，同时监控ApplicationMaster，遇到失败时重启ApplicationMaster
	
	* NodeManager（Slave服务）：每个节点上的进程，管理这个节点上的资源分配和监控节点的健康状态
	  1. 将cpu和内存资源抽象成一个个的Container，并管理它们的生命周期
	  2. 启动时向RM注册并告知本身有多少资源可用，运行期间上报／接收查询资源的请求
	  3. 分配Container给应用的某个任务，不知道运行在它上面的应用的信息

* 双层资源调度的意思是Master调度Slave（NM），NM调度Container（cpu&内存）

* 一个作业的执行过程：
	* 1、客户端向ResourceManager的App Manager提交应用并请求一个App Master实例
	* 2、ResourceManager找到可以运行一个Container的NodeManager，并在这个Container中启动App Master实例
	* 3、App Master向ResourceManager注册，注册之后，客户端就可以查询ResourceManager获得自己App Master的详情以及和App Master直接交互了
	* 4、接着App Master向Resource Manager请求资源，即Container
	* 5、获得Container后，App Master启动Container，并执行Task
	* 6、Container执行过程中，会把运行进度和状态等信息发送给App Master
	* 7、客户端主动和App Master交流应用的运行状态、进度更新等信息
	* 8、所有工作完成，App Master向RM取消注册然后关闭，同时所有的Container也归还给系统
	* 通过这个Job的处理过程，我们可以看到，App Master的角色是作为Job的驱动，驱动了Job任务的调度执行。在这个运作流程中，App Manager只需要管理App Master的生命周期以及保存它的内部状态。
而App Master这个角色的抽象，使得每种类型的应用，都可以定制自己的App Master。这样其他的计算模型就可以相对容易地运行在Yarn集群上。


![avatar](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/yarn_architecture.gif)


* Yarn的三种资源分配方式。Yarn是通过将资源分配给queue来进行资源分配的。每个queue可以设置它的资源分配方式
	*  FIFO Scheduler
		*  如果没有配置策略的话，所有的任务都提交到一个default队列。根据它们的提交顺序执行。如果有富裕资源，就执行任务；如果资源不富裕，就等待前面的任务执行完毕后释放资源。在这个时间点Job1提交，它占用了所有的资源；在它之后不久，Job2提交了，但是此时系统中已经没有资源可以分配给它了。加入Job1是一个大任务，那么Job2就只能等待一段很长的时间才能获得执行的资源。
		* 所以这个先入先出的分配方式存在一个问题就是大任务会占用很多资源，造成后面的小任务等待时间太长而饿死。因此一般不使用这个默认配置。

		![avatar](https://img-blog.csdn.net/20160722232503547)
	*  Capacity Scheduler
		*  	Capacity Scheduler是一种多租户，弹性的分配方式。每个租户一个队列，每个队列可以配置能使用的资源上限与下限（譬如50%，达到这个上限后即使其他的资源空置着，也不可使用），通过配置可以令队列至少有资源下限配置的资源可使用。
		![avatar](https://img-blog.csdn.net/20160722232717013)


		* 图中的队列A和队列B分配了相互独立的资源。Job1提交给队列A执行，它只能使用队列A的资源。不久后，Job2提交给了队列B，队列B，此时Job2就不必等待Job1释放资源了。这样就可以将大任务和小任务分配在两个队列中，这两个队列的资源相互独立，就不会造成小任务饿死的情况了
	* Fair Scheduler
		* 是一种公平的分配方式。所谓的公平就是队列间会平均地分配资源。它是抢占式的一种分配方式。
图中的Job1提交给队列A，它占用了集群的所有资源。接着Job2提交给了队列B。这时Job1就需要释放它的一半的资源给队列A中的Job2使用。接着，Job3也提交给了队列B。这个时候Job2如果还未执行完毕的话，也必须释放一半的资源给Job3.这就是公平的分配方式，在队列范围内，所有任务享用到的资源都是均分的 
	
			![avatar](https://img-blog.csdn.net/20160722232958768)
	* 参考地址：https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/CapacityScheduler.html#Overview

#### 3、简述mapreduce在yarn中调度
* 1、client端会调用resourcemanager，申请执行一个job
* 2、resourcemanager会给客户端返回一个hdfs的目录以及一个application_id号。
* 3、client端会将切片信息、job的配置信息以及jar包上传到上一步收到的hdfs目录下（三个文件分别是：job.split、job.xml、jar包）
* 4、client请求resourcemanager启动mrappmaster
* 5、resourcemanager将client请求初始化成一个task任务，放到执行队列里面（默认FIFO），当执行到这个task的时候会给该job分配资源。
* 6、resourcemanager会找空闲的nodemanager创建一个container容器，并启动mrappmaster
* 7、当mrappmaster启动之后会先将client提交hdfs的资源（job.split、job.xml、jar包）下载到本地
* 8、mrappmaster根据资源信息的情况请求resourcemanager启动maptask
* 9、resourcemanager会为上面的请求找空闲的nodemanager并创建maptask的container
* 10、mrappmaster将资源发送给各个nodemanager并且启动上面相应的maptask程序，监控maptask的运行情况（如果maptask挂掉之后，由mrappmaster去处理）。
* 11、当maptask执行完成后，mrappmaster又会向resourcemanager申请reducetask的资源
* 12、resourcemanager又会为上面的请求找空闲的nodemanager并创建reducetask的container
* 13、mrappmaster然后又启动reducetask任务，并且监控reducetask任务的执行状况。
* 14、直到mapreduce的程序执行完成
* 当mrappmaster挂掉之后，resourcemanager会重新找其他的nodemanager并重新启动一个新的mrappmaster，所以mrappmaster不存在点单故障问题


####4、伏羲调度系统
* 分布式调度系统需要解决两个问题：
	* 任务调度：如何将海量数据分片，并在几千上万台机器上并行处理，最终汇聚成用户需要的结果？当并行任务中个别失败了如何处理？不同任务之间的数据如何传递？
	* 资源调度：分布式计算天生就是面向多用户、多任务的，如何让多个用户能够共享集群资源？如何在多个任务之间调配资源以使得每个任务公平的得到资源？
* 伏羲调度架构分析：
	*  整个集群包括一台Fuxi Master以及多台Tubo。其中Fuxi Master是集群的中控角色，它负责资源的管理和调度；Tubo是每台机器上都有的一个Agent，它负责管理本台机器上的用户进程；同时集群中还有一个叫Package Manager的角色，因为用户的可执行程序以及一些配置需要事先打成一个压缩包并上传到Package Manager上，Package Manager专门负责集群中包的分发。
* 任务执行流程分析
	* 1、客户端提交任务请求，给Fuxi Master
	* 2、Fuxi Master在一个空闲的节点，启动一个App Master
	* 3、App Master启动之后，会发起资源请求，给FuxiMaster, 资源请求协议丰富，避免交互较长
	* 4、FuxiMaster把资源调度情况结果返回给App Master
	* 5、APP Master就知道，在哪些节点启动 App Worker ，于是App Master通知 Tobo进程， 拉起相应机器的App Worker进程
	* 6、App Worker启动成功后，回到App Master进程注册，告诉App Worker 它已经Ready了。
	* 7、App Master于是下发任务给对应的App Worker, 包括App Worker处理的数据分片、存储位置、以及处理结果存放的地方，这个过程称之为Instance下发。
		![avatar](https://yqfile.alicdn.com/3897139a6d0f2bf211143a6ecafbf47c.jpeg)

* 任务调度的技术要点
	* 1、数据本地性
     * Instance从本地读取数据，考虑资源是否均衡
	* 2、数据的Shuffle 传递
    * 1、1：1 一对一模式
    * 2、1对N 模式 每个Map 发送给所有个Reduce
    * 3、M：N模式 ,Patition 模式
	* 3、Instance 重试和容错性 Backup Instance
     * 1、Instance 由于机器挂架导致失败，可把这个Instance 放到其他机器进行执行
     * 2、由于机器没挂，但是由于机器硬件老化，导致运行很慢，这种情况叫做“长尾”。 
     * 3、解决策略： 为每个App Worker的Instance启动一个 BackUp Instace, 当App Master发现Insatance出现长尾情况后， 让BackUp Instance运行， 让俩个Instance同时运行， 谁先运行完 就算OK。
	* 4、触发机制： 处理时间远远超过了其他Instance平均运行时间, 已经完成Instance比例

* 资源调度：目标：最大化集群资源利用率，最小化每个任务等待时间，支持资源的配额，支持任务抢占
* 	1、优先级
    * 每个作业都有优先级标签，priority。
    * 优先级越高越先调度，相同优先级按提交时间排序调度
    * 优先分配搞优先级的JOB, 剩余分配次优先级job
* 	2、正在运行的系统，插入高优先级任务，如何进行抢占？
     * 优先抢占优先级低的任务，去暂停优先级低的任务，回收资源，分配给紧急任务；如果还满足不了，那就抢占优先级倒数第二的任务的资源，值到高优先级任务呗满足执行。是一个递归过程
* 	3、优先级分组
     * 同一个优先级组内平均分配，按提交时间 先到先得。

* 	4、策略配额 Quota
    * 多个任务组成Group, 按业务区分。每个Group的Job所分配的资源“付费”。
    * 资源共享和资源配额限制。实现动态调节Quota配额。
    * 某个Group没用为，按比例分给其他Group.
* 容错技术:故障恢复要考虑下面四个方面
	* 1、正在运行任务 不中断
	* 2、对用户透明
	* 3、自动恢复故障
	* 4、系统恢复时保持可用性
	* 5、安全与性能隔离

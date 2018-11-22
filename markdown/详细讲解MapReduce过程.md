####详细讲解MapReduce过程
##### 节点工作内容剖析
* 客户端（Client）：编写mapreduce程序，配置作业，提交作业，这就是程序员完成的工作；
* JobTracker：JobTracker是一个后台服务进程，启动之后，会一直监听并接收来自各个TaskTracker发送的心跳信息，包括资源使用情况和任务运行情况等信息。 
	* 作业控制：在hadoop中每个应用程序被表示成一个作业，每个作业又被分成多个任务，JobTracker的作业控制模块则负责作业的分解和状态监控。
	* 状态监控：主要包括TaskTracker状态监控、作业状态监控和任务状态监控。主要作用：容错和为任务调度提供决策依据。
	* JobTracker只有一个，他负责了任务的信息采集整理，你就把它当做包工头把，这个和采用Master/Slave结构中的Master保持一致
	* JobTracker 对应于 NameNode
	* 一般情况应该把JobTracker部署在单独的机器上
* TaskTracker：TaskTracker是JobTracker和Task之间的桥梁。TaskTracker与JobTracker和Task之间采用了RPC协议进行通信。 
	* 从JobTracker接收并执行各种命令：运行任务、提交任务、杀死任务等
	* 将本地节点上各个任务的状态通过心跳周期性汇报给JobTracker，节点健康情况、资源使用情况，任务执行进度、任务运行状态等，比如说map task我做完啦，你什么时候让reduce task过来拉数据啊
	* TaskTracker是运行在多个节点上的slaver服务。TaskTracker主动与JobTracker通信，接收作业，并负责直接执行每一个任务。
	* TaskTracker都需要运行在HDFS的DataNode上
* HDFS：保存作业的数据、配置信息等等，最后的结果也是保存在hdfs上面 
* NameNode： 管理文件目录结构，接受用户的操作请求,管理数据节点(DataNode)
* DataNode：是HDFS中真正存储数据的
* Block：是hdfs读写数据的基本单位，默认64MB大小，就是说如果你有130MB数据，那就要分成三个block，两个存放64MB，最后一个存放2MB数据，虽然最后一个block块是64MB，但实际上占用空间为2MB
* Sencondary NameNode：它的目的是帮助 NameNode 合并编辑日志，减少 NameNode 启动时间，在文件系统中设置一个检查点来帮助NameNode更好的工作。它不是要取代掉NameNode也不是NameNode的备份。可参考Secondary NameNode:它究竟有什么作用？

	![avatar](http://dl.iteye.com/upload/attachment/0066/0128/8aab5880-d171-30f7-91d6-aaacba2d03ce.jpg)
	
	
####流程分析：
* 1.在客户端启动一个作业。
* 2.向JobTracker请求一个Job ID。
* 3.将运行作业所需要的资源文件复制到HDFS上，包括MapReduce程序打包的JAR文件、配置文件和客户端计算所得的输入划分信息。这些文件都存放在JobTracker专门为该作业创建的文件夹中。文件夹名为该作业的Job ID。JAR文件默认会有10个副本（mapred.submit.replication属性控制）；输入划分信息告诉了JobTracker应该为这个作业启动多少个map任务等信息。
* 4.JobTracker接收到作业后，将其放在一个作业队列里，等待作业调度器对其进行调度（这里是不是很像微机中的进程调度呢，呵呵），当作业调度器根据自己的调度算法调度到该作业时，会根据输入划分信息为每个划分创建一个map任务，并将map任务分配给TaskTracker执行。对于map和reduce任务，TaskTracker根据主机核的数量和内存的大小有固定数量的map槽和reduce槽。这里需要强调的是：map任务不是随随便便地分配给某个TaskTracker的，这里有个概念叫：数据本地化（Data-Local）。意思是：将map任务分配给含有该map处理的数据块的TaskTracker上，同时将程序JAR包复制到该TaskTracker上来运行，这叫“运算移动，数据不移动”。而分配reduce任务时并不考虑数据本地化。
* 5.TaskTracker每隔一段时间会给JobTracker发送一个心跳，告诉JobTracker它依然在运行，同时心跳中还携带着很多的信息，比如当前map任务完成的进度等信息。当JobTracker收到作业的最后一个任务完成信息时，便把该作业设置成“成功”。当JobClient查询状态时，它将得知任务已完成，便显示一条消息给用户。
* 以上是在客户端、JobTracker、TaskTracker的层次来分析MapReduce的工作原理的，下面我们再细致一点，从map任务和reduce任务的层次来分析分析吧
	
	
	
	
	
####细节说明：
##### map阶段
* 1、 在map task执行时，它的输入数据来源于HDFS的block，当然在MapReduce概念中，map task只读取split。Split与block的对应关系可能是多对一，默认是一对一。
* 2、当map task的输出结果很多时，就可能会撑爆内存，所以需要在一定条件下将缓冲区中的数据临时写入磁盘，然后重新利用这块缓冲区。这个从内存往磁盘写数据的过程被称为Spill，中文可译为溢写，字面意思很直观。这个溢写是由单独线程来完成，不影响往缓冲区写map结果的线程。溢写线程启动时不应该阻止map的结果输出，所以整个缓冲区有个溢写的比例spill.percent。这个比例默认是0.8，也就是当缓冲区的数据已经达到阈值（buffer size * spill percent = 100MB * 0.8 = 80MB），溢写线程启动，锁定这80MB的内存，执行溢写过程。Map task的输出结果还可以往剩下的20MB内存中写，互不影响。 当溢写线程启动后，需要对这80MB空间内的key做排序(Sort)。排序是MapReduce模型默认的行为，这里的排序也是对序列化的字节做的排序
* 3、当map task真正完成时，内存缓冲区中的数据也全部溢写到磁盘中形成一个溢写文件。最终磁盘中会至少有一个这样的溢写文件存在(如果map的输出结果很少，当map执行完成时，只会产生一个溢写文件)，因为最终的文件只有一个，所以需要将这些溢写文件归并到一起，这个过程就叫做Merge。Merge是怎样的？如前面的例子，“aaa”从某个map task读取过来时值是5，从另外一个map 读取时值是8，因为它们有相同的key，所以得merge成group。什么是group。对于“aaa”就是像这样的：{“aaa”, [5, 8, 2, …]}，数组中的值就是从不同溢写文件中读取出来的，然后再把这些值加起来。请注意，因为merge是将多个溢写文件合并到一个文件，所以可能也有相同的key存在，在这个过程中如果client设置过Combiner，也会使用Combiner来合并相同的key
* 4、至此，map端的所有工作都已结束，最终生成的这个文件也存放在TaskTracker够得着的某个本地目录内。每个reduce task不断地通过RPC从JobTracker那里获取map task是否完成的信息，如果reduce task得到通知，获知某台TaskTracker上的map task执行完成，Shuffle的后半段过程开始启动。 简单地说，reduce task在执行之前的工作就是不断地拉取当前job里每个map task的最终结果，然后对从不同地方拉取过来的数据不断地做merge，也最终形成一个文件作为reduce task的输入文件


##### reduce阶段
* 1.  Copy过程，简单地拉取数据。Reduce进程启动一些数据copy线程(Fetcher)，通过HTTP方式请求map task所在的TaskTracker获取map task的输出文件。因为map task早已结束，这些文件就归TaskTracker管理在本地磁盘中。 
* 2. Merge阶段。这里的merge如map端的merge动作，只是数组中存放的是不同map端copy来的数值。Copy过来的数据会先放入内存缓冲区中，这里的缓冲区大小要比map端的更为灵活，它基于JVM的heap size设置，因为Shuffle阶段Reducer不运行，所以应该把绝大部分的内存都给Shuffle用。这里需要强调的是，merge有三种形式：1)内存到内存  2)内存到磁盘  3)磁盘到磁盘。默认情况下第一种形式不启用，让人比较困惑，是吧。当内存中的数据量到达一定阈值，就启动内存到磁盘的merge。与map 端类似，这也是溢写的过程，这个过程中如果你设置有Combiner，也是会启用的，然后在磁盘中生成了众多的溢写文件。第二种merge方式一直在运行，直到没有map端的数据时才结束，然后启动第三种磁盘到磁盘的merge方式生成最终的那个文件。 
* 3. Reducer的输入文件。不断地merge后，最后会生成一个“最终文件”。为什么加引号？因为这个文件可能存在于磁盘上，也可能存在于内存中。对我们来说，当然希望它存放于内存中，直接作为Reducer的输入，但默认情况下，这个文件是存放于磁盘中的。当Reducer的输入文件已定，整个Shuffle才最终结束。然后就是Reducer执行，把结果放到HDFS上。 


	
	
	
	
	
	
	
	
	
	
	
	
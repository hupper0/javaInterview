
###1、什么是CMS？
Concurrent Mark Sweep。并发标记清除，针对老年代回收
###2、CMS有什么用？
CMS以获取最小停顿时间为目的。在一些对响应时间有很高要求的应用或网站中，用户程序不能有长时间的停顿，CMS 可以用于此场景。
###3、CMS步骤
* 3.1 初始标记(STW) *inital mark*
* 3.2 并发标记 *concurrent mark* 
* 3.3 并发预清理 *concurrent pre clean*
* 3.4 重标记(STW) *remark*
* 3.5 并发清理 *concurrent clean*
* 3.6 重置 *concurrent reset*
下面一个个介绍：
####3.1 初始标记inital mark STW。
该阶段进行可达性分析，标记GC ROOT能直接关联到的对象。注意是直接关联间接关联的对象在下一阶段标记
GC ROOT 包括:


* 虚拟机栈中的引用对象
* 方法区中类静态属性引用的对象
* 方法区中常量引用对象
* 本地方法栈中JNI引用对象


####3.2 并发标记阶段, 是和用户线程并发执行的过程。
该阶段进行GC ROOT TRACING，在第一个阶段被暂停的线程重新开始运行由前阶段标记过的对象出发，所有可到达的对象都在本阶段中标记。
<br/>
注意：此阶段采用的是<font color="red">三色标记法</font>。
因为CMS是并发标记，因此需要执行三次标记，来保证原子性，因此其完整的因此GC执行时间会比并行GC长。
####3.3 并发预处理
注意此阶段主要工作还是标记。
此阶段标记从新生代晋升的对象、新分配到老年代的对象以及在并发阶段被修改了的对象。

<b>这个阶段毕竟复杂</br>
请思考下面俩个情况：
  1、在标记阶段，如果老年代的对象被新生代中的对象引用怎么办呢。 remember set
  2、回收新生代是，Minor gc时 如果老年代引用了新生代中的对象怎么办。 card table
下面一个一个解答：
1、答案是必须扫面新生代来解决，这也就是为什么cms gc也要扫描老年代的原因

[GC[YG occupancy: 820 K (6528 K)]</br>
[Rescan (parallel) , 0.0024157 secs]]</br>
[weak refs processing, 0.0000143 secs]</br>
[scrub string table, 0.0000258 secs]</br>
[1 CMS-remark: 479379K(515960K)] 480200K(522488K), 0.0025249 secs]</br>
[Times: user=0.01 sys=0.00, real=0.00 secs]</br>
Parallel Rescan阶段(remark阶段的一个子阶段)会扫描新生代和老年代中的对象。
由于扫描是很慢的，针对这个阶段进行优化，下面针对新生代和老年的不同，选择的策略也不同
* 新生代
* 策略： 在扫面前进行一次Minor gc。
* 实现：
* 1、CMSScheduleRemarkEdenSizeThreshold 默认值：2M
* 2、CMSScheduleRemarkEdenPenetration 默认值：50%
* 3、CMSMaxAbortablePrecleanTime 5s
* 4、CMSScavengeBeforeRemark

前俩个参数组合起来的意思就是：pre clean 预清理后，eden空间使用超过2M时启动可中断的并发预清理,进入remark阶段。
我们的目标是在可中止的预清理阶段发生一次Minor GC。<br />
第三个参数：只要到了5S，不管发没发生Minor GC，有没有到CMSScheduleRemardEdenPenetration都会中止此阶段，进入remark。<br />
如果以上参数设置后都没执行Minor gc, 可以使用第四个参数，remark前强制进行一次Minor GC。这样做利弊都有。好的一面是减少了remark阶段的停顿时间;坏的一面是Minor GC后紧跟着一个remark pause。如此一来，停顿时间也比较久。
7688.150: [CMS-concurrent-preclean-start]
7688.186: [CMS-concurrent-preclean: 0.034/0.035 secs]
7688.186: [CMS-concurrent-abortable-preclean-start]
7688.465: [GC 7688.465: [ParNew: 1040940K->1464K(1044544K), 0.0165840 secs] 1343593K->304365K(2093120K), 
0.0167509 secs]7690.093: [CMS-concurrent-abortable-preclean: 1.012/1.907 secs] 7690.095: [GC[YG occupancy: 522484 K (1044544 K)]
7690.095: [Rescan (parallel) , 0.3665541 secs]7690.462: [weak refs processing, 0.0003850 secs] [1 CMS-remark: 302901K(1048576K)] 825385K(2093120K), 0.3670690 secs]

说明：7688.186启动了可终止的预清理，在随后的三秒内启动了Minor GC，然后进入了Remark阶段。实际上是为了减少remark阶段的stw时间，remark的rescan是多线程的，为了便于多线程扫描新生代，预清理阶段会将新生代分块，每个块中存放着多个对象，这样remark阶段就不需要从头开始识别每个对象的起始位置.多个线程的职责就很明确了，把分块分配给多个线程，很快就扫描完。遗憾的是，这种办法仍然是建立在发生了Minor GC的条件下。如果没有发生Minor GC，top（下一个可以分配的地址空间）以下的所有空间被认为是一个块(这个块包含了新生代大部分内容)。这种块对于remark阶段并不会起到多少作用，因此并行效率也会降低

* 老年代

老年代的机制与一个叫CARD TABLE（其实就是个数组,数组中每个位置存的是一个byte）的东西密不可分。
CMS将老年代的空间分成大小为512bytes的块，card table中的每个元素对应着一个块。
并发标记时，如果某个对象的引用发生了变化，就标记该对象所在的块为 dirty card。
并发预清理阶段就会重新扫描该块，将该对象引用的对象标识为可达。<br/>
CARD TABLE另外一个作用，该老年代是否有新生代的引用。<br/>
这里点一下，hotspot 虚拟机使用字节码解释器、JIT编译器、 write barrier维护 card table。
当字节码解释器或者JIT编译器更新了引用，就会触发write barrier操作card table.
再点一下，由于card table的存在，当老年代空间很大时会发生什么？（这里大家可以自由发挥想象）
至此，预清理阶段的工作讲完。

####3.4 重标记(STW) remark
暂停所有用户线程，重新扫描堆中的对象，进行可达性分析,标记活着的对象。
有了前面的基础，这个阶段的工作量被大大减轻，停顿时间因此也会减少。
注意这个阶段是多线程的。

####3.5 并发清理 
用户线程被重新激活，同时清理那些无效的对象


####3.6 重置
CMS清除内部状态，为下次回收做准备





### 详细介绍
* 1，首先jvm根据-XX:CMSInitiatingOccupancyFraction，-XX:+UseCMSInitiatingOccupancyOnly来决定什么时间开始垃圾收集；
* 2，如果设置了-XX:+UseCMSInitiatingOccupancyOnly，那么只有当old代占用确实达到了-XX:CMSInitiatingOccupancyFraction参数所设定的比例时才会触发cms gc；
* 3，如果没有设置-XX:+UseCMSInitiatingOccupancyOnly，那么系统会根据统计数据自行决定什么时候触发cms gc；因此有时会遇到设置了80%比例才cms gc，但是50%时就已经触发了，就是因为这个参数没有设置的原因；
* 4，当cms gc开始时，首先的阶段是CMS-initial-mark，此阶段是初始标记阶段，是stop the world阶段，因此此阶段标记的对象只是从root集最直接可达的对象；
     CMS-initial-mark：961330K（1572864K），指标记时，old代的已用空间和总空间
* 5，下一个阶段是CMS-concurrent-mark，此阶段是和应用线程并发执行的，所谓并发收集器指的就是这个，主要作用是标记可达的对象
       此阶段会打印2条日志：CMS-concurrent-mark-start，CMS-concurrent-mark
* 6，下一个阶段是CMS-concurrent-preclean，此阶段主要是进行一些预清理，因为标记和应用线程是并发执行的，因此会有些对象的状态在标记后会改变，此阶段正是解决这个问题因为之后的Rescan阶段也会stop the world，为了使暂停的时间尽可能的小，也需要preclean阶段先做一部分工作以节省时间
     此阶段会打印2条日志：CMS-concurrent-preclean-start，CMS-concurrent-preclean
* 7，下一阶段是CMS-concurrent-abortable-preclean阶段，加入此阶段的目的是使cms gc更加可控一些，作用也是执行一些预清理，以减少Rescan阶段造成应用暂停的时间
     此阶段涉及几个参数：
     -XX:CMSMaxAbortablePrecleanTime：当abortable-preclean阶段执行达到这个时间时才会结束
     -XX:CMSScheduleRemarkEdenSizeThreshold（默认2m）：控制abortable-preclean阶段什么时候开始执行，
      即当eden使用达到此值时，才会开始abortable-preclean阶段
     -XX:CMSScheduleRemarkEdenPenetratio（默认50%）：控制abortable-preclean阶段什么时候结束执行
      此阶段会打印一些日志如下：
     CMS-concurrent-abortable-preclean-start，CMS-concurrent-abortable-preclean，
      CMS：abort preclean due to time XXX
* 8，再下一个阶段是第二个stop the world阶段了，即Rescan阶段，此阶段暂停应用线程，对对象进行重新扫描并标记；
       YG occupancy：964861K（2403008K），指执行时young代的情况
       CMS remark：961330K（1572864K），指执行时old代的情况
      此外，还打印出了弱引用处理、类卸载等过程的耗时
* 9，再下一个阶段是CMS-concurrent-sweep，进行并发的垃圾清理
* 10，最后是CMS-concurrent-reset，为下一次cms gc重置相关数据结构
* 11，full gc：
有2种情况会触发full gc，在full gc时，整个应用会暂停
       A，concurrent-mode-failure：当cms gc正进行时，此时有新的对象要进行old代，但是old代空间不足造成的
       B，promotion-failed：当进行young gc时，有部分young代对象仍然可用，但是S1或S2放不下，因此需要放到old代，但此时old代空间无法容纳此。

影响cms gc时长及触发的参数是以下2个：
        -XX:CMSMaxAbortablePrecleanTime=5000
        -XX:CMSInitiatingOccupancyFraction=80
解决也是针对这两个参数来的，根本的原因是每次请求消耗的内存量过大
解决方式：
      A，针对cms gc的触发阶段，调整-XX:CMSInitiatingOccupancyFraction=50，提早触发cms gc，就可以缓解当old代达到80%，cms gc处理不完，从而造成concurrent mode failure引发full gc
     B，修改-XX:CMSMaxAbortablePrecleanTime=500，缩小CMS-concurrent-abortable-preclean阶段的时间
     C，考虑到cms gc时不会进行compact，因此加入-XX:+UseCMSCompactAtFullCollection
       （cms gc后会进行内存的compact）和-XX:CMSFullGCsBeforeCompaction=4（在full gc4次后会进行compact）参数




####netty介绍
* netty是什么：
	* 异步事件驱动框架，用于快手开发高性能服务器服务端客户端
	* 封装了jdk底层的bio, nio，提供高可用api 
	* 自带编解码器，解决拆包粘包问题，开发者只关心自己的逻辑
	* 精心设计的reactor线程模型。支持高并发海量连接
	* 自带各种协议栈	

	
* nioeventloop: 监听客户端链接，处理每个链接的读写数据


* 对应关系
* nioeventloop-->thread
* channel--->socket
* bytebuffer--->IO Byte
* pipeline-->logic chain
* channel handler->logic



* netty 服务端初始化
	* 流程：
		* 1、创建服务端channel:调用jdk底层的api创建channel, netty将他包装成自己的channel，同时创建一些基本组件.
		* bind()->initAndRegister()->newChannel()
		* 反射创建服务端 NioServerSocketChannel: 
			*  newSocket: 创建底层的jdk channel
			*  NioServerSocketChannelConfig() TCP参数
			*  AbstractNioMessageChannel
				* configureBlocking(false)
				* id,unsafe, pipeline
		* 2、初始化 服务端channel: 添加基本属性和逻辑处理器
			* init初始化
			* set channelOptions, channelAttrs
			* set ChildOptions,ChildAttrs
			* config handler 配置pipeline
			* add serverbootstrapAcceptor: 添加连接器 
		* 3、注册selector, netty将jdk底层channel注册到事件轮训处理器selector上面，并把netty channel作为一个attachement，绑定在jdk底层channel上面。
			*  
		* 4、端口绑定
* NioEventLoop创建
	* 流程
		*  new NioEventLoopGroup(线程组 默认 2*cpu)
		 * new ThreadPerTaskExcetor线程创建器
		 * for(){new child}{构造NioEventLoop}
		 * chooserFactory.newChooser()[线程选择器]

		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		   



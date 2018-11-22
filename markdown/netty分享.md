####netty 简介
* jobss、java.nio、reactor
* 案例：hadoop、dubbo、rocketmq、storm....

	* 异步事件驱动框架，用于快手开发高性能服务器服务端客户端
	* 封装了jdk底层的bio, nio，提供高可用api 
	* 自带编解码器，解决拆包粘包问题，开发者只关心自己的逻辑
	* 精心设计的reactor线程模型。支持高并发海量连接
	* 自带各种协议栈	


####unix io模型

* io执行过程
	* 等待数据准备，转移到内核区 
	* 将数据从内核拷贝到进程中 
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170102/1483366231306044257.png)
	
* 1、阻塞式I/O：blocking IO
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170102/1483366318807076211.png)
	* IO执行的两个阶段都被block了
* 2、非阻塞式I/O： nonblocking IO
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170102/1483366389700024047.png)
	* 一个阶段不是阻塞的,需要不断的主动询问kernel数据好了没有；第二个阶段依然阻塞
* 3、I/O复用（select，poll，epoll...）：IO multiplexing
	* 利用了新的select系统调用，由内核来负责本来是请求进程该做的轮询操作
	* 支持多路IO
	* 就是select /epoll这个function会不断的轮询所负责的所有socket，当某个socket有数据到达了，就通知用户进程。它的流程如图：
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170102/1483366448898081321.png)
	* 当用户进程调用了select，那么整个进程会被block，而同时，kernel会“监视”所有select负责的socket，当任何一个 socket中的数据准备好了，select就会返回。这个时候用户进程再调用read操作，将数据从kernel拷贝到用户进程
	* vs ~~ poll ,epoll
	
* 4、信号驱动式I/O（SIGIO）：signal driven IO
	* 这类函数的工作机制是告知内核启动某个操作，并让内核在整个操作（包括将数据从内核拷贝到用户空间）完成后通知我们。如图：
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170102/1483366526919011627.png)
	* 用户进程发起read操作之后，立刻就可以开始去做其它的事。而另一方面，从kernel的角度，当它受到一个asynchronous read之后，首先它会立刻返回，所以不会对用户进程产生任何block。然后，kernel会等待数据准备完成，然后将数据拷贝到用户内存，当这一切都 完成之后，kernel会给用户进程发送一个signal，告诉它read操作完成了。 在这整个过程中，进程完全没有被block
* java bio->nio->aio 演进过程
 

	
#### BIO编程与其局限性	
* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170102/1483366681774007008.png)
* 案例

```
 public static void main(String[] args) throws Exception {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        //创建socket服务,监听10101端口
        ServerSocket server=new ServerSocket(10101);
        System.out.println("服务器启动！");
        while(true){
            //获取一个套接字（阻塞）
            final Socket socket = server.accept();
            System.out.println("来个一个新客户端！");
            newCachedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    //业务处理
                    handler(socket);
                }
            });

        }
    }
    /**
     * 读取数据
     * @param socket
     * @throws Exception
     */
    public static void handler(Socket socket){
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            while(true){
                //读取数据（阻塞）
                int read = inputStream.read(bytes);
                if(read != -1){
                    System.out.println(new String(bytes, 0, read));
                }else{
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                System.out.println("socket关闭");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```

### NIO 
* channel
	* 简介：通道(Channel)可以理解为数据传输的管道。通道与流不同的是，流只是在一个方向上移动(一个流必须是inputStream或者outputStream的子类)，而通道可以用于读、写或者同时用于读写 
	*  socket:
		* SocketChannel、 ServerSocketChannel 、 DatagramChannel   
	* File I/O ： FileChannel
* buffer: 缓冲区是包在一个对象内的基本数据元素数组
	*  capacity：缓冲区能够容纳的数据元素的最大数量，可以理解为数组的长度。 这一容量在缓冲区创建时被设定，并且永远不能被改变。
	*  limit: 缓冲区的第一个不能被读或写的元素。或者说，缓冲区中现存元素的计数。
	*  position:下一个要被读或写的元素的索引。Buffer类提供了get( )和 put( )函数 来读取或存入数据，position位置会自动进行相应的更新。
	*  ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170103/1483455604027086782.png)

* selector
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170104/1483540103154037014.png)
	* 案例
* DirectByteBuffer
	* 直接内存的申请
	* 直接内存的释放
	* sun.misc.Cleaner 回调释放 
	
### netty
* 组件
	* nioeventloop-->thread
	* channel--->socket
	* bytebuffer--->IO Byte
	* pipeline-->logic chain
	* channel handler->logic

* 案例

```
public class TimeServer {
    private int port=8080;
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });
           
            ChannelFuture f = b.bind(port).sync(); // (5)
            System.out.println("TimeServer Started on 8080...");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        new TimeServer().run();
    }
}
```	

* 解析	
	* 1、首先我们创建了两个EventLoopGroup实例：bossGroup和workerGroup，目前可以将bossGroup和workerGroup理解为两个线程池。其中bossGroup用于接受客户端连接，bossGroup在接受到客户端连接之后，将连接交给workerGroup来进行处理。
	* 2、接着，我们创建了一个ServerBootstrap实例，从名字上就可以看出来这是一个服务端启动类，我们需要给设置一些参数，包括第1步创建的bossGroup和workerGroup。
	* 3、我们通过channel方法指定了NioServerSocketChannel，这是netty中表示服务端的类，用于接受客户端连接，对应于java.nio包中的ServerSocketChannel。
	* 4、我们通过childHandler方法，设置了一个匿名内部类ChannelInitializer实例，用于初始化客户端连接SocketChannel实例。在第3步中，我们提到NioServerSocketChannel是用于接受客户端连接，在接收到客户端连接之后，netty会回调ChannelInitializer的initChannel方法需要对这个连接进行一些初始化工作，主要是告诉netty之后如何处理和响应这个客户端的请求。在这里，主要是添加了3个ChannelHandler实例：LineBasedFrameDecoder、StringDecoder、TimeServerHandler。其中LineBasedFrameDecoder、StringDecoder是netty本身提供的，用于解决TCP粘包、解包的工具类。
		* LineBasedFrameDecoder在解析客户端请求时，遇到字符”\n”或”\r\n”时则认为是一个完整的请求报文，然后将这个请求报文的二进制字节流交给StringDecoder处理。
		* StringDecoder将字节流转换成一个字符串，交给TimeServerHandler来进行处理。
		* TimeServerHandler是我们自己要编写的类，在这个类中，我们要根据用户请求返回当前时间。
	* 5、在所有的配置都设置好之后，我们调用了ServerBootstrap的bind(port)方法，开启真正的监听在8080端口，接受客户端请求。 
	
	
* Reactor线程模型
	* 单线程reactor线程模型
	* 多线程reactor线程模型
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170203/1486130401975018511.png)
	* 混合型reactor线程模型
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170203/1486130497191094359.png)
*  Netty中的Reactor
	* ![avatar](http://static.tianshouzhi.com/ueditor/upload/image/20170203/1486130747296085576.png)
	* 解析
		* 1、设置服务端ServerBootStrap启动参数
		* 2、通过ServerBootStrap的bind方法启动服务端，bind方法会在parentGroup中注册NioServerScoketChannel，监听客户端的连接请求
		* 3、Client发起连接CONNECT请求，parentGroup中的NioEventLoop不断轮循是否有新的客户端请求，如果有，ACCEPT事件触发
		* 4、ACCEPT事件触发后，parentGroup中NioEventLoop会通过NioServerSocketChannel获取到对应的代表客户端的NioSocketChannel，并将其注册到childGroup中
		* 5、childGroup中的NioEventLoop不断检测自己管理的NioSocketChannel是否有读写事件准备好，如果有的话，调用对应的ChannelHandler进行处理

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

* netty线程模拟实现
	* https://github.com/hupper0/nioNetty 
	
	
	
	
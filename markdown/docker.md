docker 
使用命令查看一下docker都有那些命令：

docker -h
你将得到如下结果：

复制代码
A self-sufficient runtime for linux containers.

Options:

  --api-cors-header=                   Set CORS headers in the remote API
  -b, --bridge=                        Attach containers to a network bridge
  --bip=                               Specify network bridge IP
  -D, --debug=false                    Enable debug mode
  -d, --daemon=false                   Enable daemon mode
  --default-gateway=                   Container default gateway IPv4 address
  --default-gateway-v6=                Container default gateway IPv6 address
  --default-ulimit=[]                  Set default ulimits for containers
  --dns=[]                             DNS server to use
  --dns-search=[]                      DNS search domains to use
  -e, --exec-driver=native             Exec driver to use
  --exec-opt=[]                        Set exec driver options
  --exec-root=/var/run/docker          Root of the Docker execdriver
  --fixed-cidr=                        IPv4 subnet for fixed IPs
  --fixed-cidr-v6=                     IPv6 subnet for fixed IPs
  -G, --group=docker                   Group for the unix socket
  -g, --graph=/var/lib/docker          Root of the Docker runtime
  -H, --host=[]                        Daemon socket(s) to connect to
  -h, --help=false                     Print usage
  --icc=true                           Enable inter-container communication
  --insecure-registry=[]               Enable insecure registry communication
  --ip=0.0.0.0                         Default IP when binding container ports
  --ip-forward=true                    Enable net.ipv4.ip_forward
  --ip-masq=true                       Enable IP masquerading
  --iptables=true                      Enable addition of iptables rules
  --ipv6=false                         Enable IPv6 networking
  -l, --log-level=info                 Set the logging level
  --label=[]                           Set key=value labels to the daemon
  --log-driver=json-file               Default driver for container logs
  --log-opt=map[]                      Set log driver options
  --mtu=0                              Set the containers network MTU
  -p, --pidfile=/var/run/docker.pid    Path to use for daemon PID file
  --registry-mirror=[]                 Preferred Docker registry mirror
  -s, --storage-driver=                Storage driver to use
  --selinux-enabled=false              Enable selinux support
  --storage-opt=[]                     Set storage driver options
  --tls=false                          Use TLS; implied by --tlsverify
  --tlscacert=~/.docker/ca.pem         Trust certs signed only by this CA
  --tlscert=~/.docker/cert.pem         Path to TLS certificate file
  --tlskey=~/.docker/key.pem           Path to TLS key file
  --tlsverify=false                    Use TLS and verify the remote
  --userland-proxy=true                Use userland proxy for loopback traffic
  -v, --version=false                  Print version information and quit

Commands:
    attach    Attach to a running container
    build     Build an image from a Dockerfile
    commit    Create a new image from a container's changes
    cp        Copy files/folders from a container's filesystem to the host path
    create    Create a new container
    diff      Inspect changes on a container's filesystem
    events    Get real time events from the server
    exec      Run a command in a running container
    export    Stream the contents of a container as a tar archive
    history   Show the history of an image
    images    List images
    import    Create a new filesystem image from the contents of a tarball
    info      Display system-wide information
    inspect   Return low-level information on a container or image
    kill      Kill a running container
    load      Load an image from a tar archive
    login     Register or log in to a Docker registry server
    logout    Log out from a Docker registry server
    logs      Fetch the logs of a container
    pause     Pause all processes within a container
    port      Lookup the public-facing port that is NAT-ed to PRIVATE_PORT
    ps        List containers
    pull      Pull an image or a repository from a Docker registry server
    push      Push an image or a repository to a Docker registry server
    rename    Rename an existing container
    restart   Restart a running container
    rm        Remove one or more containers
    rmi       Remove one or more images
    run       Run a command in a new container
    save      Save an image to a tar archive
    search    Search for an image on the Docker Hub
    start     Start a stopped container
    stats     Display a stream of a containers' resource usage statistics
    stop      Stop a running container
    tag       Tag an image into a repository
    top       Lookup the running processes of a container
    unpause   Unpause a paused container
    version   Show the Docker version information
    wait      Block until a container stops, then print its exit code

Run 'docker COMMAND --help' for more information on a command.
复制代码
下面来详细说明一些命令的使用，在此之前，说一下如果不理解理解 Docker 和 Docker Hub 及两者关系，可以类比 Git 和 GitHub 理解。

1. docker version

显示 Docker 版本信息。

2. docker info

显示 Docker 系统信息，包括镜像和容器数。

3. docker search

从 Docker Hub 中搜索符合条件的镜像。

docker search -s 3 --automated --no-trunc django
上面命令的意思是搜索处收藏数不小于 3 ，并且能够自动化构建的 django 镜像，并且完整显示镜像描述。

参数：

  --automated=false    Only show automated builds 只列出 automated build类型的镜像--no-trunc=false     Don't truncate output 显示完整的镜像描述
  -s, --stars=0        Only displays with at least x stars 只列出不低于x个收藏的镜像
4. docker pull

从 Docker Hub 中拉取或者更新指定镜像。

docker pull ubuntu:latest
上面命令的意思是拉取ubuntu最新的镜像。

参数：

-a, --all-tags=false    Download all tagged images in the repository 拉取所有 tagged 镜像
5. docker login

按步骤输入在 Docker Hub 注册的用户名、密码和邮箱即可完成登录。

6. docker logout

运行后从指定服务器登出，默认为官方服务器。

7. docker images
列出本地所有镜像。对镜像名称进行关键词查询。

docker images ubuntu
上面命令的意思是列出本地镜像名为 ubuntu 的所有镜像。不加 ubuntu，就列出所有本地镜像。

参数：

  -a, --all=false      Show all images (default hides intermediate images) 列出所有镜像（含中间映像层，默认情况下，过滤掉中间映像层）
  --digests=false      Show digests 展示镜像的摘要
  -f, --filter=[]      Filter output based on conditions provided 过滤镜像，如： -f ['dangling=true'] 只列出满足dangling=true 条件的镜像
  --no-trunc=false     Don't truncate output 显示完整的镜像ID
  -q, --quiet=false    Only show numeric IDs 仅列出镜像ID
8. docker ps

列出所有运行中容器。

参数：

复制代码
  -a, --all=false       Show all containers (default shows just running) 列出所有容器（含沉睡镜像）
  --before=             Show only container created before Id or Name 列出在某一容器之前创建的容器，接受容器名称和ID作为参数
  -f, --filter=[]       Filter output based on conditions provided -f [exited=<int>] 列出满足exited=<int> 条件的容器-l, --latest=false    Show the latest created container, include non-running 仅列出最新创建的一个容器
  -n=-1                 Show n last created containers, include non-running 列出最近创建的n个容器
  --no-trunc=false      Don't truncate output 显示完整的容器ID
  -q, --quiet=false     Only display numeric IDs 仅列出容器ID
  -s, --size=false      Display total file sizes 显示容器大小
  --since=              Show created since Id or Name, include non-running 列出在某一容器之后创建的容器，接受容器名称和ID作为参数
复制代码
9. docker rmi
从本地移除一个或多个指定的镜像。

docker rmi nginx:latest ubuntu:14.04
上面命令的意思是移除 nginx 最新版本的镜像和ubuntu 14.04 版本的镜像。

参数：

  -f, --force=false    Force removal of the image 强行移除该镜像，即使其正被使用
  --no-prune=false     Do not delete untagged parents 不移除该镜像的过程镜像，默认移除
10. docker rm

从本地移除一个或多个指定的容器。

docker rm harrysun/lnmp
docker rm -l webapp/redis
上面命令的意思分别是移除 harrysun/lnmp 的本地容器和移除 webapp/redis 容器的网络连接。

参数：

  -f, --force=false      Force the removal of a running container (uses SIGKILL) 强行移除该容器，即使其正在运行-l, --link=false       Remove the specified link 移除容器间的网络连接，而非容器本身
  -v, --volumes=false    Remove the volumes associated with the container 移除与容器关联的空间
11. docker history

查看指定镜像的创建历史。

docker history -H harrysun/lnmp:0.1
上面命令的意思是查看 harrysun/lnmp:0.1 镜像的历史。

  -H, --human=true     Print sizes and dates in human readable format 以可读的格式打印镜像大小和日期
  --no-trunc=false     Don't truncate output 显示完整的提交记录
  -q, --quiet=false    Only show numeric IDs 仅列出提交记录ID
12. docker start|stop|restart

启动、停止和重启一个或多个指定容器。

docker start -i b5e08e1435b3
上面命令的意思是启动一个 ID 为 b5e08e1435b3 的容器，并进入交互模式。

参数：

  -a, --attach=false         Attach STDOUT/STDERR and forward signals 启动一个容器并打印输出结果和错误
  -i, --interactive=false    Attach container's STDIN 启动一个容器并进入交互模式
  -t, --time=10      Seconds to wait for stop before killing the container 停止或者重启容器的超时时间（秒），超时后系统将杀死进程。
13. docker kill

杀死一个或多个指定容器进程。

docker kill -s KILL 94c6b3c3f04a
上面命令的意思是杀死一个 ID 为 94c6b3c3f04a 的容器，并向容器发送 KILL 信号。

参数：

  -s, --signal=KILL    Signal to send to the container 自定义发送至容器的信号
14. docker events

从服务器拉取个人动态，可选择时间区间。

docker events --since="20150720" --until="20150808"
上面命令的意思是拉取个人从 2015/07/20 到 2015/08/08 的个人动态。

参数：

  -f, --filter=[]    Filter output based on conditions provided--since=           Show all events created since timestamp 开始时间
  --until=           Stream events until this timestamp 结束时间
15. docker save

将指定镜像保存成 tar 归档文件， docker load 的逆操作。保存后再加载（saved-loaded）的镜像不会丢失提交历史和层，可以回滚。

docker save -o ubuntu14.04.tar ubuntu:14.04
上面命令的意思是将镜像 ubuntu:14.04 保存为 ubuntu14.04.tar 文件。

参数：

  -o, --output=      Write to an file, instead of STDOUT 输出到的文件
16. docker load

从 tar 镜像归档中载入镜像， docker save 的逆操作。保存后再加载（saved-loaded）的镜像不会丢失提交历史和层，可以回滚。

docker load -i ubuntu14.04.tar
上面命令的意思是将 ubuntu14.04.tar 文件载入镜像中。

参数：

  -i, --input=       Read from a tar archive file, instead of STDIN 加载的tar文件
17. docker export

将指定的容器保存成 tar 归档文件， docker import 的逆操作。导出后导入（exported-imported)）的容器会丢失所有的提交历史，无法回滚。

docker export -o ubuntu14.04.tar 94c6b3c3f04a
上面命令的意思是将 ID 为 94c6b3c3f04a 容器保存为 ubuntu14.04.tar 文件。

参数：

  -o, --output=      Write to a file, instead of STDOUT
18. docker import

从归档文件（支持远程文件，.tar, .tar.gz, .tgz, .bzip, .tar.xz, .txz）创建一个镜像， export 的逆操作，可为导入镜像打上标签。导出后导入（exported-imported)）的容器会丢失所有的提交历史，无法回滚。

cat ./ubuntu14.04.tar | sudo docker import - ubuntu:14.04
上面命令的意思是使用 ./ubuntu14.04.tar 文件创建 ubuntu:14.04 的镜像，默认会从远端拉取文件。

19. docker top

查看一个正在运行容器进程，支持 ps 命令参数。

20. docker inspect

检查镜像或者容器的参数，默认返回 JSON 格式。 （Template）

docker inspect --format '{{.DockerVersion}}' ubuntu:14.04
上面命令的意思是返回 ubuntu:14.04  镜像的 docker 版本

参数：

  -f, --format=      Format the output using the given go template 指定返回值的模板文件
21. docker pause

暂停某一容器的所有进程。

22. docker unpause

恢复某一容器的所有进程。

23. docker tag

标记本地镜像，将其归入某一仓库。

sudo docker tag 5db5f8471261 harrysun/lnmp:0.2
上面命令的意思是将 ID 为 5db5f8471261 的容器标记为 harrysun/lnmp:0.2 镜像。

参数：

  -f, --force=false    Force 会覆盖已有标记
24. docker push

将镜像推送至远程仓库，默认为 Docker Hub 。

docker push harrysun/lnmp:0.2
上面命令的意思是将 harrysun/lnmp:0.2 镜像推送到远端。

25. docker logs

获取容器运行时的输出日志。

docker logs -f --tail 10 94c6b3c3f04a
上面命令的意思是将追踪 ID 为 94c6b3c3f04a 的容器最新的10条日志。

参数：

  -f, --follow=false        Follow log output 跟踪容器日志的最近更新
  --since=                  Show logs since timestamp 开始时间
  -t, --timestamps=false    Show timestamps 显示容器日志的时间戳
  --tail=all                Number of lines to show from the end of the logs 仅列出最新n条容器日志
26. docker run

启动一个容器，在其中运行指定命令。

docker run -i -t ubuntu:14.04 /bin/bash
上面命令的意思是以 ubuntu:14.04 镜像启动一个容器，以交互模式运行，并为容器重新分配一个伪输入终端。

参数：（这个命令的参数有点多，只说其中一部分） 

复制代码
  -a, --attach=[]             Attach to STDIN, STDOUT or STDERR 指定标准输入输出内容类型，可选 STDIN/STDOUT/STDERR 三项
  --add-host=[]               Add a custom host-to-IP mapping (host:ip)
  --blkio-weight=0            Block IO (relative weight), between 10 and 1000
  -c, --cpu-shares=0          CPU shares (relative weight)
  --cap-add=[]                Add Linux capabilities
  --cap-drop=[]               Drop Linux capabilities
  --cgroup-parent=            Optional parent cgroup for the container
  --cidfile=                  Write the container ID to the file
  --cpu-period=0              Limit CPU CFS (Completely Fair Scheduler) period
  --cpu-quota=0               Limit the CPU CFS quota
  --cpuset-cpus=              CPUs in which to allow execution (0-3, 0,1) 绑定容器到指定CPU运行
  --cpuset-mems=              MEMs in which to allow execution (0-3, 0,1) 绑定容器到指定MEM运行
  -d, --detach=false          Run container in background and print container ID 后台运行容器，并返回容器ID
  --device=[]                 Add a host device to the container
  --dns=[]                    Set custom DNS servers 指定容器使用的DNS服务器，默认和宿主一致
  --dns-search=[]             Set custom DNS search domains 指定容器DNS搜索域名，默认和宿主一致
  -e, --env=[]                Set environment variables 设置环境变量
  --entrypoint=               Overwrite the default ENTRYPOINT of the image
  --env-file=[]               Read in a file of environment variables 从指定文件读入环境变量
  --expose=[]                 Expose a port or a range of ports
  -h, --hostname=             Container host name 指定容器的hostname
  --help=false                Print usage
  -i, --interactive=false     Keep STDIN open even if not attached 以交互模式运行容器，通常与 -t 同时使用
  --ipc=                      IPC namespace to use
  -l, --label=[]              Set meta data on a container
  --label-file=[]             Read in a line delimited file of labels
  --link=[]                   Add link to another container
  --log-driver=               Logging driver for container
  --log-opt=[]                Log driver options
  --lxc-conf=[]               Add custom lxc options
  -m, --memory=               Memory limit
  --mac-address=              Container MAC address (e.g. 92:d0:c6:0a:29:33)
  --memory-swap=              Total memory (memory + swap), '-1' to disable swap
  --name=                     Assign a name to the container 为容器指定一个名称
  --net=bridge                Set the Network mode for the container  指定容器的网络连接类型，支持 bridge/host/none/container:<name|id> 四种类型
  --oom-kill-disable=false    Disable OOM Killer
  -P, --publish-all=false     Publish all exposed ports to random ports
  -p, --publish=[]            Publish a container's port(s) to the host
  --pid=                      PID namespace to use
  --privileged=false          Give extended privileges to this container
  --read-only=false           Mount the container's root filesystem as read only
  --restart=no                Restart policy to apply when a container exits
  --rm=false                  Automatically remove the container when it exits
  --security-opt=[]           Security Options
  --sig-proxy=true            Proxy received signals to the process
  -t, --tty=false             Allocate a pseudo-TTY 为容器重新分配一个伪输入终端，通常与 -i 同时使用
  -u, --user=                 Username or UID (format: <name|uid>[:<group|gid>])
  --ulimit=[]                 Ulimit options
  --uts=                      UTS namespace to use
  -v, --volume=[]             Bind mount a volume
  --volumes-from=[]           Mount volumes from the specified container(s)
  -w, --workdir=              Working directory inside the container
复制代码
 
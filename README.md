# THEMIS_Rebuild
I STILL LOVE BGP, AND ... JAVA ?

## 环境要求
- Redis 5.0或更高
- JDK 15或更高
- Maven

## 检测流程

![THEMIS](.\docs\img\THEMIS.jpg)

## 配置准备

### Redis配置

在`RedisPool`中进行本地Redis数据库配置，并且按照如下信息修改`redis.conf`，启动Redis数据库

```
protected-mode no
daemonize yes
```

### 消息队列插入

在`redis-cli`或其它Redis客户端中执行以下命令，将模拟待测报文插入`db0`中的`bgp_message` Stream消息队列

```
xadd bgp_message * msg "{\"type\":\"A\",\"timestamp\":1635594484.4040916,\"peer_asn\":100,\"host\":\"\",\"path\":[100,100,200,300,400],\"communities\":[],\"prefix\":[\"10.0.0.0/8\",\"20.0.0.0/16\"]}"
```
***报文路径为左邻又源**

### Stream配置消费者组
```
xgroup create bgp_message jedis $
```

### 运行主进程，进行测试

## Team Message

sdn 504-2




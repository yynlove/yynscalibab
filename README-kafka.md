[TOC]

# RocketMQ

## 基本概念

### 消息（Message）

消息系统所传输信息的物理载体，生产和消费数据的最小单位，每条消息必须属于一个主题。

### 主题（Topic）

 一个生产者可以同时发送多种Topic的消息；而一个消费者只对某种特定的Topic感兴趣，即只可以订阅 和消费一种Topic的消息。 producer:topic 1:n consumer:topic 1:1

### 标签 (tag)

消息设置的标签，用于同一主题下区分不同类型的消息。

### 队列

存储消息的物理实体。一个Topic中可以包含多个Queue，每个Queue中存放的就是该Topic的消息。一  个Topic的Queue也被称为一个Topic中消息的分区（Partition）。  一个Topic的Queue中的消息只能被一个消费者组中的一个消费者消费。一个Queue中的消息不允许同 一个消费者组中的多个消费者同时消费。

### 分片（Sharding）

分片不同于分区。在RocketMQ  中，分片指的是存放相应Topic的Broker。每个分片中会创建出相应数量的分区，即Queue，每个Queue的大小都是相同的。

### 消息标识（MessageId/Key）

RocketMQ中每个消息拥有唯一的MessageId，且可以携带具有业务标识的Key，以方便对消息的查询。  
不过需要注意的是，MessageId有两个：在生产者send()消息时会自动生成一个MessageId（msgId)，  
当消息到达Broker后，Broker也会自动生成一个MessageId(offsetMsgId)。msgId、offsetMsgId与key都  
称为消息标识。

- msgId：由producer端生成，其生成规则为：  
  producerIp + 进程pid + MessageClientIDSetter类的ClassLoader的hashCode +  
  当前时间 + AutomicInteger自增计数器

- offsetMsgId：由broker端生成，其生成规则为： brokerIp + 物理分区的offset（Queue中的 
  偏移量）

- key：由用户指定的业务相关的唯一标识

## RocketMQ系统架构

### 工作流程

1. 启动NameServer，NameServer启动后开始监听端口，等待Broker、Producer、Consumer连接。

2. 启动Broker时，Broker会与所有的NameServer建立并保持长连接，然后每30秒向NameServer定时发送心跳包。

3. 发送消息前，可以先创建Topic，创建Topic时需要指定该Topic要存储在哪些Broker上，当然，在创建Topic时也会将Topic与Broker的关系写入到NameServer中。不过，这步是可选的，也可以在发送消息时自动创建Topic。 

4. Producer发送消息，启动时先跟NameServer集群中的其中一台建立长连接，并从NameServer中获 取路由信息，即当前发送的Topic消息的Queue与Broker的地址（IP+Port）的映射关系。然后根据算法策略从队选择一个Queue，与队列所在的Broker建立长连接从而向Broker发消息。当然，在获取到路由信息后，Producer会首先将路由信息缓存到本地，再每30秒从NameServer更新一次路由信息。 

5. Consumer跟Producer类似，跟其中一台NameServer建立长连接，获取其所订阅Topic的路由信息， 然后根据算法策略从路由信息中获取到其所要消费的Queue，然后直接跟Broker建立长连接，开始消费 其中的消息。Consumer在获取到路由信息后，同样也会每30秒从NameServer更新一次路由信息。不过不同于Producer的是，Consumer还会向Broker发送心跳，以确保Broker的存活状态。

### 读/写队列

    从物理上来讲，读/写队列是同一个队列。所以，不存在读/写队列数据同步问题。读/写队列是逻辑上进行区分的概念。一般情况下，读/写队列数量是相同的。
    
    例如，创建Topic时设置的写队列数量为8，读队列数量为4，此时系统会创建8个Queue，分别是0 1 2 3  4 5 6 7。Producer会将消息写入到这8个队列，但Consumer只会消费0 1 2 3这4个队列中的消息，4 5 6  7中的消息是不会被消费到的。
    
    再如，创建Topic时设置的写队列数量为4，读队列数量为8，此时系统会创建8个Queue，分别是0 1 2 3  4 5 6 7。Producer会将消息写入到0 1 2 3 这4个队列，但Consumer只会消费0 1 2 3 4 5 6 7这8个队列中的消息，但是4 5 6 7中是没有消息的。此时假设Consumer Group中包含两个Consuer，Consumer1消费01 2 3，而Consumer2消费4 5 6 7。但实际情况是，Consumer2是没有消息可消费的。也就是说，当读/写队列数量设置不同时，总是有问题的。那么，为什么要这样设计呢？  
    其这样设计的目的是为了，方便Topic的Queue的缩容。
    
    例如，原来创建的Topic中包含16个Queue，如何能够使其Queue缩容为8个，还不会丢失消息？可以动态修改写队列数量为8，读队列数量不变。此时新的消息只能写入到前8个队列，而消费都消费的却是 16个队列中的数据。当发现后8个Queue中的消息消费完毕后，就可以再将读队列数量动态设置为8。整 个缩容过程，没有丢失任何消息。
    
    perm用于设置对当前创建Topic的操作权限：2表示只写，4表示只读，6表示读写。

### 注意点

1. 消费者组中Consumer的数量应该小于等于订阅Topic的Queue数量。如果超出Queue数量，则多出的Consumer将不能消费消息。

## 消息存储

### commitlog目录

1. commitlog目录中存放者mappedFile文件，第一个文件名一定是20位0构成的。因为第一个文件的第一条消息的偏移量commitlog offset为0，当第一个文件放满时，则会自动生成第二个文件继续存放消息。一个Broker中所有mappedFile文件的commitlog offset是连续的

2. 无论当前Broker中存放着多少Topic的消息，这些消息都是被顺序写入到了mappedFile文件中的。也就是说，这些消息在Broker中存放时并没有被按照Topic进行分类存放。

3. mappedFile文件内容由一个个的消息单元构成。每个消息单元中包含消息总长度MsgLen、消息的物理 位置physicalOffset、消息体内容Body、消息体长度BodyLength、消息主题Topic、Topic长度 TopicLength、消息生产者BornHost、消息发送时间戳BornTimestamp、消息所在的队列QueueId、消息在Queue中存储的偏移量QueueOffset等近20余项消息相关属性。
   
   1. 一个mappedFile文件中第m+1个消息单元的commitlog offset偏移量  
      L(m+1) = L(m) + MsgLen(m) (m >= 0)

### consumequeue目录

1. 为了提高效率，会为每个Topic在~/store/consumequeue中创建一个目录，目录名为Topic名称。在该Topic目录下，会再为每个该Topic的Queue建立一个目录，目录名为queueId。每个目录中存放着若干consumequeue文件，consumequeue文件是commitlog的索引文件，可以根据consumequeue定位到具体的消息。

2. **onsumequeue文件名也由20位数字构成**，表示当前文件的第一个索引条目的起始位移偏移量。与mappedFile文件名不同的是，其后续文件名是固定的。因为**consumequeue文件大小是固定不变的。**

3. 每个consumequeue文件可以包含30w个索引条目，每个索引条目包含了三个消息重要属性：消息在mappedFile文件中的**偏移量CommitLog Offset、消息长度、消息Tag的hashcode值**。这三个属性占20个字节，所以每个文件的大小是固定的30w * 20字节。

4. 一个consumequeue文件中所有消息的Topic一定是相同的。但每条消息的Tag可能是不同的。

### 文件读写

#### 消息写入

一条消息进入到Broker后经历了以下几个过程才最终被持久化：

1. Broker根据queueId，获取到该消息对应索引条目要在consumequeue目录中的写入偏移量，即 QueueOffset

2. 将queueId、queueOffset等数据，与消息一起封装为消息单元

3. 将消息单元写入到commitlog

4. 同时，形成消息索引条目

5. 将消息索引条目分发到相应的consumequeue

#### 消息读取

当Consumer来拉取消息时会经历以下几个步骤：

1. Consumer获取到其要消费消息所在Queue的消费偏移量offset ，计算出其要消费消息的  
   消息offset
   
   1. 消费offset即消费进度，consumer对某个Queue的消费offset，即消费到了该Queue的第几 条消息  
   
   2. 消息offset = 消费offset + 1

2. Consumer向Broker发送拉取请求，其中会包含其要拉取消息的Queue、消息offset及消息Tag。

3. Broker计算在该consumequeue中的queueOffset。
   
   1. queueOffset = 消息offset * 20字节

4. 从该queueOffset处开始向后查找第一个指定Tag的索引条目。

5. 解析该索引条目的前8个字节，即可定位到该消息在commitlog中的commitlog offset

6. 从对应commitlog offset中读取消息单元，并发送给Consumer

### key查询

store目录中的index子目录中的indexFile进行索引实现的快速查询。当然，这个indexFile中的索引数据是在包含了key的消息被发送到Broker时写入的。如果消息中没有包含key，则不会写入;

## 消息消费

### 消费类型

1. 获取消费类型
   
   1. pull(拉取式)：Consumer 控制
   
   2. push(推送式)：brocker主动推送

2. 消费模式
   
   1. 集群消费：消费进度（offset）保存在Brocker端
   
   2. 广播消费：消费进度保存在Consumer者端，各自消费者保存各自的进度

### Rebalance机制

Rebalance即再均衡，指的是，将⼀个Topic下的多个Queue在同⼀个Consumer Group中的多个Consumer间进行重新分配的过程。

#### 消费危害

1. 消费暂停

2. 消费重复

3. 消费突刺

### 消费成功

RocketMQ有一个原则：每条消息必须要被成功消费一次。

那么什么是成功消费呢？Consumer在消费完消息后会向其消费进度记录器提交其消费消息的offset，offset被成功记录到记录器中，那么这条消费就被成功消费了。

### 订阅关系的一致型

订阅关系的一致性指的是，同一个消费者组（Group ID相同）下所有Consumer实例所订阅的Topic与Tag及对消息的处理逻辑必须完全一致。否则，消息消费的逻辑就会混乱，甚至导致消息丢失。

### Consumer的消费进度offset

#### offset 本地管理模式

当消费模式为广播消费时，offset使用本地模式存储。因为每条消息会被所有的消费者消费，每个消费者管理自己的消费进度，各个消费者之间不存在消费进度的交集。

#### offset 远程管理模式

当消费模式为集群消费时，offset使用远程模式管理。因为所有Cosnumer实例对消息采用的是均衡消费，所有Consumer共享Queue的消费进度。

Consumer在集群消费模式下offset相关数据以json的形式持久化到Broker磁盘文件中，文件路径为当前用户主目录下的store/config/consumerOffset.json 。

Broker启动时会加载这个文件，并写入到一个双层Map（ConsumerOffsetManager）。外层map的key为topic@group，value为内层map。内层map的key为queueId，value为offset。当发生Rebalance时， 新的Consumer会从该Map中获取到相应的数据来继续消费。

集群模式下offset采用远程管理模式，主要是为了保证Rebalance机制。

当消费完一批消息后，Consumer会提交其消费进度offset给Broker，Broker在收到消费进度后会将其更新到那个双层Map（ConsumerOffsetManager）及consumerOffset.json文件中，然后向该Consumer进行ACK，而ACK内容中包含三项数据：当前消费队列的最小offset（minOffset）、最大offset（maxOffset）、及下次消费的起始offset（nextBeginOffset）。

当rocketMQ对消息的消费出现异常时，会将发生异常的消息的offset提交到Broker中的重试队列。系统在发生消息消费异常时会为当前的topic@group创建一个重试队列，该队列以%RETRY%开头，到达重试时间后进行消费重试。

#### offset的同步提交与异步提交

集群消费模式下，Consumer消费完消息后会向Broker提交消费进度offset，其提交方式分为两种：

- 同步提交： 消费者在消费完一批消息后会向broker提交这些消息的offset，然后等待broker的成功响应。若在等待超时之前收到了成功响应，则继续读取下一批消息进行消费（从ACK中获取nextBeginOffset）。若没有收到响应，则会重新提交，直到获取到响应。而在这个等待过程中，消费者是阻塞的。其严重影响了消费者的吞吐量。

- 异步提交： 消费者在消费完一批消息后向broker提交offset，但无需等待Broker的成功响应，可以继续读取并消费下一批消息。这种方式增加了消费者的吞吐量。但需要注意，broker在收到提交的offset后，还是会向消费者进行响应的。可能还没有收到ACK，此时Consumer会从Broker中直接获取nextBeginOffset。

## spring-cloud配置

### 普通消息

```yaml
      bindings:
        one-to-one-input:
          destination: one-to-one  # 主题
          contentType: application/json
          group: one-to-one-cus-group # 消费者分组
          consumer:
            max-attempts: 1   # 最大重试次数

      rocketmq:
        bindings:
          one-to-one-input:
            consumer:
              enabled: true # 是否开启消费，默认为 true
          one-to-one-output:
            producer:
              group: test # 生产者分组
              sync: true # 是否同步发送消息，默认为 false 异步。              
              delay-level-when-next-consume: -1 # 异步消费消息模式下消费失败重试策略，默认为 brocker 0  1-consumer控制 -1 直接进入死信
```

### 消息广播

```yaml
      rocketmq:
        bindings:
          one-to-many-input:
            consumer:
              enabled: true # 是否开启消费，默认为 true
              messageModel: BROADCASTING  # 将消费模式改为广播模式
```

### 顺序消息消费

```yaml
    stream:
      bindings:
        one-to-one-order-output:
          destination: one-to-one-order  # 顺序消费
          contentType: application/json
          producer:
            partition-key-expression: headers['orderId'] # 分区 key 表达式。该表达式基于 Spring EL，从消息中获得分区 key。

    rocketmq:
      binder:
        one-to-one-order-input:
          consumer:
            enabled: true # 是否开启消费，默认为 true
        one-to-one-order-output:
          producer:
            group: test-order # 生产者分组
            sync: false # 是否同步发送消息，默认为 false 异步。
            orderly: true # 启用顺序消息
```

### 消息过滤



1. 基于header过滤
```
//生产者
setHeader("key", "tag");
//消费者
@StreamListener(value = YynChannelBinder.ONE_TO_ONE_ORDER_INPUT,condition = "headers['key'] == 'tag'");
```

2. 基于tag | sql 过滤
```
setHeader(MessageConst.PROPERTY_TAGS, orderItem.getMessage())
```
```yaml
  one-to-one-order-input:
    consumer:
      enabled: true # 是否开启消费，默认为 true
      subscription:  yuan || a   # 基于 tag 标签
      # sql: # 基于 SQL 订阅，默认为空
```

### 事务消息

#### 原理

[事务消息文档](https://cloud.tencent.com/document/product/1493/61585)

1. 本地事务处理主要在步骤3；
2. 保证本地事务和消息发送 最终一致性

#### 生产者配置

1. yml配置
```yaml
      rocketmq:
        bindings:
          one-to-one-trans-output:
            producer:
              group: one-to-one-trans-group # 生产者分组
              producerType: Trans   # 事务消息
              sync: true # 是否同步发送消息，默认为 false 异步。
              transactionListener: oneToOneTransListener     # 事务监听器配置  
      stream:
        bindings:
          one-to-one-trans-output:
            destination: one-to-one-trans    
            contentType: application/json     
```
2. java代码

```java
@Slf4j
@Service
public class RocketmqDemoService {
    public void sendOTOTrans(String message) {
        //不做业务处理
        Table1 table1 = new Table1();
        table1.setTest1(message + "开始发消息");
        String transactionId = UUID.randomUUID().toString();
        table1.setTest4(transactionId);

        String jsonString = JSON.toJSONString(table1);
        Message<Table1> springMessage = MessageBuilder.withPayload(table1)
                .setHeader("TRANSACTION_ID", table1.getTest4())
                .setHeader("args", jsonString) // 
                .build();
        //发送消息
        log.info("sendOTOTrans {}", table1.getTest4());
        yynChannelBinder.sendOneToOneTransChannel().send(springMessage);
    }
}

//监听器
@Slf4j
@Component
public class OneToOneTransListener implements TransactionListener {

    @Resource
    private Table1Service table1Service;
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        Map<String, String> headers = message.getProperties();
        String transactionId = headers.get("TRANSACTION_ID");
        String s = headers.get("args");
        log.info("executeLocalTransaction transactionId {} args {}",transactionId,s);
        try {
            //处理业务 模拟事务提交或回滚
            table1Service.transSave(s);
            return LocalTransactionState.COMMIT_MESSAGE;
        }catch (Exception e){
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        log.info("LocalTransactionState {}",JSON.toJSON(messageExt));
        Map<String, String> headers = messageExt.getProperties();
        String transactionId = headers.get("TRANSACTION_ID");
        //查询处理结果 决定回滚 还是发送消息
        LambdaQueryWrapper<Table1> eq = new QueryWrapper<Table1>().lambda().eq(Table1::getTest4, transactionId);
        int count = this.table1Service.count(eq);
        if(count>0){
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        return LocalTransactionState.ROLLBACK_MESSAGE;
    }
}

```

3. 消费者

```yaml
    stream:
      bindings:
        one-to-one-trans-input:
          destination: one-to-one-trans
          contentType: application/json
          group: one-to-one-trans-cus-group # 消费者分组
```

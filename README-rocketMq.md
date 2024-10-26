# RocketMQ




## 普通消息
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
## 消息广播
```yaml
      rocketmq:
        bindings:
          one-to-many-input:
            consumer:
              enabled: true # 是否开启消费，默认为 true
              messageModel: BROADCASTING  # 将消费模式改为广播模式
```

## 顺序消息消费
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


## 消息过滤

### 基于 Tag 过滤
```java
//setHeader(MessageConst.PROPERTY_TAGS, tag); 不生效
//生产者
setHeader("rocketmq_TAGS", "tag");
//消费者
@StreamListener(value = YynChannelBinder.ONE_TO_ONE_ORDER_INPUT,condition = "headers['rocketmq_TAGS'] == 'tag'");

```
```yaml
```

## 事务消息

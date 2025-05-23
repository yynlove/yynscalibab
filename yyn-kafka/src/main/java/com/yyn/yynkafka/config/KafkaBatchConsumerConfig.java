package com.yyn.yynkafka.config;



import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@SpringBootConfiguration
public class KafkaBatchConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${spring.kafka.properties.session.timeout.ms}")
    private String sessionTimeout;
    @Value("${spring.kafka.properties.max.poll.interval.ms}")
    private String maxPollIntervalTime;
    @Value("${spring.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${spring.kafka.listener.concurrency}")
    private Integer concurrency;
    @Value("${spring.kafka.listener.missing-topics-fatal}")
    private boolean missingTopicsFatal;
    @Value("${spring.kafka.listener.poll-timeout}")
    private long pollTimeout;

    @Value("${test_batch_topic.group}")
    private String groupId;

    @Bean(name = "batchConsumerConfigs")
    public Map<String, Object> batchConsumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>(16);
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //是否自动提交偏移量，默认值是true，为了避免出现重复数据和数据丢失，可以把它设置为false，然后手动提交偏移量
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        //自动提交的时间间隔，自动提交开启时生效
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "2000");
        //该属性指定了消费者在读取一个没有偏移量的分区或者偏移量无效的情况下该作何处理：
        //earliest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费分区的记录
        //latest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据（在消费者启动之后生成的记录）
        //none：当各分区都存在已提交的offset时，从提交的offset开始消费；只要有一个分区不存在已提交的offset，则抛出异常
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        //两次poll之间的最大间隔，默认值为5分钟。如果超过这个间隔会触发reBalance
        propsMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalTime);
        //这个参数定义了poll方法最多可以拉取多少条消息，默认值为500。如果在拉取消息的时候新消息不足500条，那有多少返回多少；如果超过500条，每次只返回500。
        //这个默认值在有些场景下太大，有些场景很难保证能够在5min内处理完500条消息，
        //如果消费者无法在5分钟内处理完500条消息的话就会触发reBalance,
        //然后这批消息会被分配到另一个消费者中，还是会处理不完，这样这批消息就永远也处理不完。
        //要避免出现上述问题，提前评估好处理一条消息最长需要多少时间，然后覆盖默认的max.poll.records参数
        //注：需要开启BatchListener批量监听才会生效，如果不开启BatchListener则不会出现reBalance情况
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        //当broker多久没有收到consumer的心跳请求后就触发reBalance，默认值是10s
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        //这两个参数也会影响到批次消费

        // 调大fetch的相关参数, 以便于提升吞吐量, 但会增大延时
        // 一次fetch操作最小的字节数, 如果低于这个字节数, 就会等待, 直到超时后才返回给消费者. 这里给100kB, 缺省是1B
        propsMap.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 100); // fetch.min.bytes
        // 一次fetch操作的最大等待时间，“最大等待时间”与“最小字节”任何一个先满足了就立即返回给消费者
        // 需要注意：“最大等待时间”不能超过 session.timeout.ms 和 request.timeout.ms
        propsMap.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 10000); // fetch.max.wait.ms, 缺省是500

        //序列化（建议使用Json，这种序列化方式可以无需额外配置传输实体类）
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return propsMap;
    }

    public ConsumerFactory<Object, Object> batchConsumerFactory() {
        //配置消费者的 Json 反序列化的可信赖包，反序列化实体类需要
        try(JsonDeserializer<Object> deserializer = new JsonDeserializer<>()) {
            deserializer.trustedPackages("*");
            return new DefaultKafkaConsumerFactory<>(batchConsumerConfigs(), new JsonDeserializer<>(), deserializer);
        }
    }

    @Bean(name = "batchKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(batchConsumerFactory());
        //在侦听器容器中运行的线程数，一般设置为 机器数*分区数
        factory.setConcurrency(concurrency);
        //消费监听接口监听的主题不存在时，默认会报错，所以设置为false忽略错误
        factory.setMissingTopicsFatal(missingTopicsFatal);
        //自动提交关闭，需要设置手动消息确认
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        //设置为批量监听，需要用List接收
        factory.setBatchListener(true);
        return factory;
    }



}

# 1、启动nocas
    startup.cmd -m standalone
## 1-1、nocas config
- 使用 nocas config 需要加入 spring-cloud-starter-bootstrap 依赖
- spring.profiles.active 改为 spring.config.activate.on-profile
# 2、sentinel 客户端
    java -Dserver.port=8090 -Dcsp.sentinel.dashboard.server=localhost:8090 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard.jar




# 其他文档链接
-  [详解RocketMq](./README-rocketMq.md)
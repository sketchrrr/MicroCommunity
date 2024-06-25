# 使用官方的OpenJDK镜像作为基础镜像
FROM openjdk:8

# 添加 hosts 条目
RUN echo "127.0.0.1 dev.db.java110.com" >> /etc/hosts && \
    echo "127.0.0.1 dev.zk.java110.com" >> /etc/hosts && \
    echo "127.0.0.1 dev.kafka.java110.com" >> /etc/hosts && \
    echo "127.0.0.1 dev.redis.java110.com" >> /etc/hosts && \
    echo "127.0.0.1 api.java110.com" >> /etc/hosts && \
    echo "127.0.0.1 dev.java110.com" >> /etc/hosts

# 设置工作目录
WORKDIR /smart_community

# 将项目的JAR文件复制到容器中
COPY smart_community.jar /smart_community/release.jar

# 暴露应用的端口
EXPOSE 8080

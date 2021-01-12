# NJU-Itxia 聊天机器人


## 项目概述

项目当前还处于萌芽期，欢迎大家参与贡献.

### 项目结构

```
main
├── kotlin
│   └── cn
│       └── itxia
│           └── chatbot
│               ├── controller       # controller层，接受HTTP请求
│               ├── dto
│               ├── enum
│               ├── filter
│               ├── message
│               │   ├── incoming     # 接收消息的类型
│               │   └── response     # 返回消息的类型
│               ├── service
│               │   ├── message      # 消息处理service的抽象类
│               │   └── messageImpl  # 消息处理service的实现
│               ├── util             # 工具类
│               └── vo
└── resources
    └── application.properties       # 配置文件

```

### 语言、框架

项目在SpringBoot框架下，使用kotlin语言编写.

可使用Java，但仍然建议用kotlin(你可把它当成是Java的语法糖，上手十分简单).

QQ机器人库暂选用[mirai](https://github.com/mamoe/mirai).


## How to contribute
请参阅[CONTRIBUTING](CONTRIBUTING.md)文档.

## 需求
欢迎~~画大饼~~提需求.

功能需求:
- [x] 语雀文档查询
- [x] 常用网址服务
- [x] help命令
- [ ] 活跃QQ群气氛
- [x] 有新预约时发送提醒 (未启用)
- [x] 简单的关键词学习功能
- [ ] 推机bot
- [ ] anything interesting...

技术工作:
- [x] QQ机器人
- [x] 提供web接口(现在还很简陋)
- [ ] web使用WebSocket连接
- [ ] web端UI界面

## 使用说明

### 构建和运行

#### 修改配置
先修改application.properties.

如果启用QQ机器人，需要填写账号密码.(建议拿小号测试)

若不用QQ机器人也可通过HTTP调用.

#### 运行/调试

打开IDE(A)，调试运行.

或运行命令构建:
```shell
#run directly
./gradlew bootRun

#build and run
./gradlew build
cd build/libs/
java -jar chatbot-0.0.1-SNAPSHOT.jar

#some common options:
#specify another port than 8080
java -jar chatbot-0.0.1-SNAPSHOT.jar --server.port=${yourPort}
#active your config file
java -jar chatbot-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profileName}
#run with external config file
java -jar chatbot-0.0.1-SNAPSHOT.jar --spring.config.location=${filePath}
```

### 使用QQ机器人

啊...好像没什么说的，直接用就好.

mirai运行过程中会不断产生log，对找问题有很大帮助.

### 使用HTTP调用机器人

调用示例：
```http request
POST http://localhost:8080/chat
{
    "content": "hello"
}
```

# NJU-Itxia 聊天机器人


## 项目概述

项目当前还处于萌芽期，欢迎大家参与贡献.

### 需求

初步的需hua求bing大致是：
- 群聊活跃气氛，~~卖萌~~
- 查询语雀文档(详见语雀搜索API)
- 和预约系统联动，提供预约单查询等等功能
- 按预算、需求推荐电脑(不急)
- 提供多端使用，万一QQ机器人被封杀还有其他端可用
- anything interesting...

### 项目结构

```
.
├── main
│   ├── kotlin
│   │   └── cn
│   │       └── itxia
│   │           └── chatbot
│   │               ├── controller  # controller层，接受HTTP请求
│   │               ├── dto         # dto，前端传来的数据
│   │               ├── enum        # enum
│   │               ├── service     # service层，执行业务逻辑
│   │               ├── util        # 公共类
│   │               └── vo          # vo，返回给前端的数据
│   └── resources
│       └── application.properties  # 配置文件
```

### 语言、框架

项目在SpringBoot框架下，使用kotlin语言编写.

可使用Java，但仍然建议用kotlin(你可把它当成是Java的语法糖，上手十分简单).

QQ机器人库暂选用[mirai](https://github.com/mamoe/mirai).


## 使用说明

### 构建和运行

#### 修改配置
先修改application.properties.
如果启用QQ机器人，需要填写账号密码.(建议拿小号测试)

若不用QQ机器人也可通过HTTP调用.

#### 运行/调试

打开IDE(A)，调试运行。

或运行命令构建:
```shell
./gradlew build
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

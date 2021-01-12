# How to contribute

## 为机器人添加新功能

依赖已经抽象好的框架，你仅需添加短短几行代码就能接入机器人.

这里我们以说废话的小编体为例，设计一个这样的指令:

```
//使用指令"废话"，但没有参数
群消息: bot 废话
机器人: 说废话是怎么回事呢？下面就让小编带大家一起了解吧！
       说废话，其实就是说废话了，那么为什么说废话呢，相信大家都很好奇。
       ...(此处省略)

//指令后跟有参数
群消息: @机器人 废话 食堂涨价
机器人: 食堂涨价是怎么回事呢？下面就让小编带大家一起了解吧！
       食堂涨价，其实就是食堂涨价了，那么为什么食堂涨价呢，相信大家都很好奇。
       ...(此处省略)

//一个corner case，群消息偶然含有"废话"，但机器人不应该调用废话指令
群消息: 废话 不装驱动当然没法用
(机器人不应该生成废话，而是忽略不理)
```

请先抛开机器人，想一下如何完成这个简单的功能.

之后我们要做的就仅仅是把机器人连接上.

### 扩展处理消息的抽象类

首先，你需要在service.messageImpl包下面新建一个类，建议以XxxService命名.

之后，你需要扩展(extends)AbstractMessageProcessService，或者它的子类.

这边我们已经抽象好了一个很方便的子类AbstractCommandProcessService，让我们来extends它.
(在kotlin里，extends和implements都用冒号表示)

大部分的工作其实IDE都帮你做好了，之后你应该得到这么一个类.

```kotlin
package cn.itxia.chatbot.service.messageImpl

import cn.itxia.chatbot.message.Command
import cn.itxia.chatbot.message.ProcessResult
import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.service.message.AbstractCommandProcessService

class TrashMessageService : AbstractCommandProcessService() {

}
```

此时你的IDE应该会提示错误，这是因为CommandProcessService是个抽象类，所以你得实现它.

把鼠标移到class那行，IDE会提示你implements members.

之后，再在类最前面加上一个@Service. 这会告诉框架，启动的时候把它也加载起来.

```kotlin
@Service
class TrashMessageService : AbstractCommandProcessService() {

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        TODO("Not yet implemented")
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        TODO("Not yet implemented")
    }

}
```

好了，现在我们已经建好我们的服务类. 虽然好像什么都没干，但它已经成为机器人的一部分了!

接下来我们来实现废话功能 😊

### 判断指令

我们希望机器人收到"废话"命令的消息时才生成废话，所以要在shouldExecute函数里判断.

```kotlin
@Service
class TrashMessageService : AbstractCommandProcessService() {

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && command.commandName == "废话"
    }

    //...
}

```

上面的isExplicitCall指的是*是否是在(明确地)呼叫机器人*，群消息里仅当消息用bot开头、或@机器人时才为true，这样就避免了上面的corner case.

### 执行指令

接下来的executeCommand函数才是真正执行命令的地方.

command如其名，就是收到的指令.

还记得上面的栗子吗？没有参数时默认用"说废话"代替，有参数就用参数.

command中已经给你解析出参数了，只需要直接用就好.

```kotlin
@Service
class TrashMessageService : AbstractCommandProcessService() {

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && command.commandName == "废话"
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        val template = """
            0是怎么回事呢？下面就让小编带大家一起了解吧。
            0，其实就是0了，那么为什么0，相信大家都很好奇是怎么回事。
            大家可能会感到很惊讶，0究竟是为什么呢？但事实就是这样，小编也感到非常惊讶。
            那么这就是关于0的事情了，大家有没有觉得很神奇呢？看了今天的内容，大家有什么想法呢？欢迎回复一起讨论噢~
        """.trimIndent()

        val actualArgument = if (command.isArgumentEmpty) {
            "说废话"
        } else {
            command.argument
        }

        val trash = template.replace("0", actualArgument)
    }
}

```

### 返回消息

最后，怎么让机器人回复消息呢？

我们的函数要返回ProcessResult类的实例，但是它已经被抽象成不能new的了.

~~(都说程序员可以自己new一个女朋友，实际上并不是这样)~~

而是要调用封装好的ProcessResult.xxx()来返回.

请看栗子:

```kotlin
@Service
class TrashMessageService : AbstractCommandProcessService() {

    override fun shouldExecute(command: Command, message: IncomingMessage): Boolean {
        return message.isExplicitCall && command.commandName == "废话"
    }

    override fun executeCommand(command: Command, message: IncomingMessage): ProcessResult {
        val template = """
            0是怎么回事呢？下面就让小编带大家一起了解吧。
            0，其实就是0了，那么为什么0，相信大家都很好奇是怎么回事。
            大家可能会感到很惊讶，0究竟是为什么呢？但事实就是这样，小编也感到非常惊讶。
            那么这就是关于0的事情了，大家有没有觉得很神奇呢？看了今天的内容，大家有什么想法呢？欢迎回复一起讨论噢~
        """.trimIndent()

        val actualArgument = if (command.isArgumentEmpty) {
            "说废话"
        } else {
            command.argument
        }

        val trash = template.replace("0", actualArgument)

        return ProcessResult.reply(trash)
    }
}

```

至此，说废话的功能就实现好了🎉🎉🎉

### 总结

实际上，接入机器人并不需要做太多额外的工作.

上面的代码中，我们只是额外做了:

1. 扩展AbstractCommandProcessService类
2. 添加@Service注解
3. 在shouldExecute函数判断是否执行
4. 在executeCommand函数执行，并调用ProcessResult.xxx()回复消息

剩下的其实是说废话本身就要做的工作，接入机器人的工作本身是非常简单的.
(甚至不需要看一眼别处的代码，无需清楚它如何工作)

## 示例

如果你还是困惑如何开发，不妨参考同一个包下的其它Service:

- **WebsiteLinkService**: 常用网址, 非常简单
- **RepeaterService**: 复读机, 演示了AbstractCommandProcessService抽象类的功能
- **YuqueDocumentSearchService**: 搜索语雀文档, 演示了如何调用第三方API服务
- **KeywordTrainService**: 关键词学习, 较为复杂, 演示了图片消息收发、序列化存储、优先级等等

另外，抽象类的接口也写有不少注释.

Happy coding.

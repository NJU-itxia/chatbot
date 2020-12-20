# How to contribute

## 为机器人添加新功能

依赖已经抽象好的框架，你仅需添加短短几行代码就能接入机器人.

这里我们以抛骰子为例，设计一个这样的指令:

```
//只抛一次
群消息: bot dice
机器人: 唔...这次抛了2点.

//抛多次，注意后面多了个次数
群消息: bot dice 3
机器人: 让我来抛3次.
机器人: 3,6,5. 一共14点.
```

请先抛开机器人，想一下如何完成这个简单的功能.

之后我们要做的就仅仅是把机器人连接上.

(为方便没学过Kotlin的同学，这里以**Java**语言为例. 后面会给出Kotlin的等价代码)

### 继承处理消息的接口

首先，你需要在service.process包下面新建一个类，建议以XxxService命名.

之后，你需要扩展(extends)MessageProcessService类，或者它的子类.

这边我们已经抽象好了一个很方便的子类CommandProcessService，让我们来extends它.

(为什么说方便？写完之后你可以尝试换成extends MessageProcessService类，看看要额外做哪些工作)

```java

public class DiceService extends CommandProcessService {

}
```

此时你的IDE应该会提示错误，这是因为CommandProcessService是个抽象类，所以你得实现它.

把鼠标移到public class那行，IDE会提示你implements methods.

之后，再在类最前面加上一个@Service. 这会告诉框架，启动的时候把它也加载起来.

```java
package cn.itxia.chatbot.service.process;

import cn.itxia.chatbot.message.incoming.IncomingMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class DiceService extends CommandProcessService {
    @Override
    public boolean shouldExecute(@NotNull String commandName, boolean isExplicitCall, boolean isArgumentEmpty) {
        return false;
    }

    @NotNull
    @Override
    public ProcessResult executeCommand(@NotNull String argument, boolean isExplicitCall, @NotNull IncomingMessage message) {
        return null;
    }
}
```

好了，现在我们已经建好我们的服务类. 虽然好像什么都没干，但它已经成为机器人的一部分了!

接下来我们来实现抛骰子的功能 😊

### 实现功能

我们希望机器人收到"dice"命令的消息时才抛骰子，所以要在shouldExecute函数里判断.

```java
public class DiceService extends CommandProcessService {
    @Override
    public boolean shouldExecute(@NotNull String commandName, boolean isExplicitCall, boolean isArgumentEmpty) {
        //注意Java里不能用==判断String内容相同, 得用equals.
        return commandName.equals("dice");
    }
    //...
}
```

接下来的executeCommand函数才是真正执行命令的地方.

argument如其名，就是命令的参数. 还记得上面的栗子吗？参数为空时只抛一次，有参数时就抛多次.

这里已经给你解析出参数了，只需要直接用就好.

```java
public class DiceService extends CommandProcessService {
    //...
    @NotNull
    @Override
    public ProcessResult executeCommand(@NotNull String argument, boolean isExplicitCall, @NotNull IncomingMessage message) {
        //TODO write your dice code
        return null;
    }
}
```

### 返回消息

最后，怎么让机器人回复消息呢？

我们的函数要返回ProcessResult类的实例，但是它已经被抽象成不能new的了.

而是要调用封装好的ProcessResult.xxx()来返回.
(在Java里是ProcessResult.Companion.xxx)

请看栗子:

```java
public class DiceService extends CommandProcessService {
    //...
    private int onceDice() {
        return (int) Math.floor(Math.random() * 5 + 1);
    }

    @NotNull
    @Override
    public ProcessResult executeCommand(@NotNull String argument, boolean isExplicitCall, @NotNull IncomingMessage message) {
        //抛一次
        int result = onceDice();
        String content;
        if (result < 5) {
            content = "唔...这次抛了" + result + "点";
        } else {
            content = "手气不错," + result + "点";
        }
        //返回消息
        return ProcessResult.Companion.reply(content, true, false);
    }
}
```

完成抛多次的代码后，抛骰子的功能就实现好了🎉🎉🎉

### 总结

实际上，接入机器人并不需要做太多额外的工作.

上面的代码中，我们只是额外做了:

1. 扩展CommandProcessService类
2. 添加@Service注解
3. 在shouldExecute函数判断是否执行
4. 在executeCommand函数执行，并调用ProcessResult.xxx()回复消息

剩下的其实是抛骰子本身就要做的工作，接入机器人的工作本身是非常简单的.
(甚至不需要看一眼别处的代码，无需清楚它如何工作)

### Kotlin

下面是Kotlin的等效代码，相比Java来说有种简洁的美，却更有表达力.

```kotlin
package cn.itxia.chatbot.service.process

import cn.itxia.chatbot.message.incoming.IncomingMessage
import cn.itxia.chatbot.message.response.TextResponseMessage
import org.springframework.stereotype.Service
import kotlin.math.floor

@Service
class DiceService : CommandProcessService() {
    override fun shouldExecute(commandName: String, isExplicitCall: Boolean, isArgumentEmpty: Boolean): Boolean {
        //终于可以用==判断String内容相同了
        return commandName == "dice"    //语句结束 再 也 不 用 写 分 号;
    }

    private fun onceDice(): Int {
        return floor(Math.random() * 5 + 1).toInt()
    }

    override fun executeCommand(argument: String, isExplicitCall: Boolean, message: IncomingMessage): ProcessResult {
        //抛一次
        //Kotlin自动类型推导，result自动获得了Int类型
        val result = onceDice()
        
        val content = if (result < 5) {
            "唔...这次抛了" + result + "点"
        } else {
            "手气不错," + result + "点"
        }
        
        return ProcessResult.reply(content)
    }
}
```

Kotlin其实就是基于JVM的语言，你完全可以把Kotlin当成Java的语法糖来使用，并且它更优雅也更强大.

```kotlin
//优雅的switch语句😊
//注意我们实际上是在定义字符串变量content
val content = when (argument) {
    "on" -> {
        isRepeaterEnable = true
        "我只是个没有感情的复读机"
    }
    "off" -> {
        isRepeaterEnable = false
        "差不多得了😅"
    }
    else -> "命令无效，请输入on/off."
}
```

### 示例

如果你还是困惑如何开发，不妨参考同一个包下的其它Service:

- WebsiteLinkService: 常用网址, 非常简单
- RepeaterService: 复读机, 演示了CommandProcessService抽象类的所有功能
- YuqueDocumentSearchService: 搜索语雀文档, 演示了如何调用第三方API服务

另外，抽象类的接口也写有不少注释.

Happy coding.

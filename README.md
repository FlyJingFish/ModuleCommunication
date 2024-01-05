# ModuleCommunication

[![Maven central](https://img.shields.io/maven-central/v/io.github.FlyJingFish.ModuleCommunication/module-communication-annotation)](https://central.sonatype.com/search?q=io.github.FlyJingFish.ModuleCommunication)
[![GitHub stars](https://img.shields.io/github/stars/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/network/members)
[![GitHub issues](https://img.shields.io/github/issues/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/issues)
[![GitHub license](https://img.shields.io/github/license/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/blob/master/LICENSE)


### ModuleCommunication 旨在解决模块间的通信需求，可以让模块间的代码在依旧存在于其自己的模块的前提下，实现能够互相访问而不需要下沉到公共模块。以此来解决公共模块因为各个模块下沉代码而导致的不断膨胀的问题

[灵感来源-微信Android模块化架构重构实践](https://mp.weixin.qq.com/s/6Q818XA5FaHd7jJMFBG60w)

## 使用步骤

**在开始之前可以给项目一个Star吗？非常感谢，你的支持是我唯一的动力。欢迎Star和Issues!**

#### 一、引入插件

1、在 **项目根目录** 的 ```build.gradle``` 里依赖插件

```gradle
buildscript {
    dependencies {
        //必须项 👇
        classpath 'io.github.FlyJingFish.ModuleCommunication:module-communication-plugin:1.0.2'
    }
}
```

2、在 **项目根目录** 的 ```build.gradle``` 里依赖插件

```gradle
plugins {
    //必须项 👇下边版本号根据你项目的 Kotlin 版本决定👇
    id 'com.google.devtools.ksp' version '1.8.10-1.0.9' apply false
}
```

[Kotlin 和 KSP Github 的匹配版本号列表](https://github.com/google/ksp/releases)

#### 二、新增负责通信的 module

- 1、例如新建一个名为 ```communication``` 的module(下文将以 ```communication``` 为例介绍)

- 2、在 ```communication``` 的 ```build.gradle``` 添加

```gradle
//必须项 👇
plugins {
    ...
    id 'communication.module'
}
```

- 3、在项目根目录的 ```gradle.properties``` 新增如下配置

```
CommunicationModuleName = communication
```

#### 三、开始使用

以下面代码结构为例介绍下

<img src="/screenshot/demo.png" alt="show" />

下边的暴露代码在本项目的 ```lib-user``` 模块中

- 1、在需要暴露代码的模块 ```lib-user``` 的 ```build.gradle``` 添加

```gradle
//必须项 👇
plugins {
    ...
    id 'communication.export'
}
```

- 2、在需要暴露给其他module使用的逻辑代码接口上使用 ```@ExposeInterface```

```kotlin
@ExposeInterface
interface UserHelper {
    fun getUser():User
}
```

- 3、把```@ExposeInterface```注解的接口类涉及的数据类上使用 ```@ExposeBean```

```kotlin
@ExposeBean
data class User (val id:String)
```

- 4、在```@ExposeInterface```注解的接口类的实现类上使用 ```@ImplementClass(UserHelper::class)```，**实现类必须只有一个**

```kotlin
@ImplementClass(UserHelper::class)
class UserHelperImpl :UserHelper {
    override fun getUser():User {
        Log.e("UserHelperImpl","getUser")
        return User("1111")
    }
}
```
- 5、调用 gradle 命令，生成共享代码

communication -> generateCommunication

<img src="/screenshot/gradle.png" alt="show" />

调用这个命令，将会生成共享代码。不调用直接运行代码可能会报错，一般报错最多次数为项目的 module 个数，即可生成完所有共享代码

- 6、在需要使用 ```lib-login``` 模块 上引入通信模块 ```communication``` 

  a、```lib-login``` 引入通信模块
```gradle
compileOnly(project(":communication"))
```

**注意引入方式必须是 compileOnly ，否则会导致打包失败** 

  b、如果 ```lib-login``` 也已经引入过 ```communication.export``` 插件，就无需配置这一步（不报错找不到类就无需引入）

```gradle
dependencies {
    //必须项 👇（可以直接放在公共 module）
    implementation 'io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:1.0.2'
}
```

- 7、在 ```lib-login``` 模块使用 ```lib-user``` 暴露出来的的代码

```kotlin
class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getSingleInstance 是获取单例  getNewInstance 是获取新的对象
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class.java)
        val user = userHelper.getUser()
        Log.e("user",""+user)
    }
}
```

#### 四、番外（非必须项）

如果你想定义更多的通信模块，而不是使用同一个，可以在使用 ```'communication.export'``` module 加入以下配置项

```gradle
plugins {
    id("communication.export")
}
communicationConfig{
    exportModuleName = "communication2"
}
```

这样共享代码会转移到 ```communication2``` 这个 module 中

#### 混淆规则

下边是涉及到本库的一些必须混淆规则

```
# ModuleCommunication必备混淆规则 -----start-----

-keepnames @com.flyjingfish.module_communication_annotation.ExposeInterface class * {*;}
-keepnames class * implements com.flyjingfish.module_communication_annotation.BindClass
-keep class * implements com.flyjingfish.module_communication_annotation.BindClass{
    public <init>();
}

# ModuleCommunication必备混淆规则 -----end-----
```



### 最后推荐我写的另外一些库

- [OpenImage 轻松实现在应用内点击小图查看大图的动画放大效果](https://github.com/FlyJingFish/OpenImage)

- [ShapeImageView 支持显示任意图形，只有你想不到没有它做不到](https://github.com/FlyJingFish/ShapeImageView)

- [FormatTextViewLib 支持部分文本设置加粗、斜体、大小、下划线、删除线，下划线支持自定义距离、颜色、线的宽度；支持添加网络或本地图片](https://github.com/FlyJingFish/FormatTextViewLib)

- [主页查看更多开源库](https://github.com/FlyJingFish)


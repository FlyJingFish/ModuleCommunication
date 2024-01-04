# ModuleCommunication

[![Maven central](https://img.shields.io/maven-central/v/io.github.FlyJingFish.ModuleCommunication/module-communication-annotation)](https://central.sonatype.com/search?q=io.github.FlyJingFish.ModuleCommunication)
[![GitHub stars](https://img.shields.io/github/stars/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/network/members)
[![GitHub issues](https://img.shields.io/github/issues/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/issues)
[![GitHub license](https://img.shields.io/github/license/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/blob/master/LICENSE)


### ModuleCommunication 旨在解决模块间通信，可以让模块间的代码在互相访问，并且依旧保持各个模块的代码依旧存在于其自己的模块而不需要下沉到公共模块


## 使用步骤

**在开始之前可以给项目一个Star吗？非常感谢，你的支持是我唯一的动力。欢迎Star和Issues!**

#### 一、引入插件

1、在 **项目根目录** 的 ```build.gradle``` 里依赖插件

```gradle
buildscript {
    dependencies {
        //必须项 👇
        classpath 'io.github.FlyJingFish.ModuleCommunication:module-communication-plugin:1.0.0'
    }
}
```

2、在 **项目根目录** 的 ```build.gradle``` 里依赖插件

```gradle
plugins {
    //非必须项 👇，如果需要自定义切面，并且使用 android-aop-ksp 这个库的话需要配置 ，下边版本号根据你项目的 Kotlin 版本决定
    id 'com.google.devtools.ksp' version '1.8.0-1.0.9' apply false
}
```

[Kotlin 和 KSP Github 的匹配版本号列表](https://github.com/google/ksp/releases)

#### 二、配置负责通信的 module

- 1、例如新建一个名为 ```communication``` 的module(下文将以 ```communication``` 为例介绍)

- 2、在项目根目录的 ```gradle.properties``` 新增如下配置

```
CommunicationModuleName = communication
```

#### 三、配置通信 module

- 1、在 **负责通信模块(```communication```)** 的 ```build.gradle``` 添加

老版本

```gradle
//必须项 👇
apply plugin: 'communication.module'
```

或者新版本

```gradle
//必须项 👇
plugins {
    ...
    id 'communication.module'
}
```

- 2、在需要 暴露代码的模块 的 ```build.gradle``` 添加


老版本

```gradle
//必须项 👇
apply plugin: 'communication.export'
```

或者新版本

```gradle
//必须项 👇
plugins {
    ...
    id 'communication.export'
}
```

#### 四、开始使用

- 1、在需要暴露给其他module使用的逻辑代码接口上使用 ```@ExposeInterface```

```kotlin
@ExposeInterface
interface UserHelper {
    fun getUser():User
}
```

- 2、把```@ExposeInterface```注解的接口类涉及的数据类上使用 ```@ExposeBean```

```kotlin
@ExposeBean
data class User (val id:String)
```

- 3、在```@ExposeInterface```注解的接口类的实现类上使用 ```@ImplementClass(UserHelper::class)```

```kotlin
@ImplementClass(UserHelper::class)
class UserHelperImpl :UserHelper {
    override fun getUser():User {
        Log.e("UserHelperImpl","getUser")
        return User("1111")
    }
}
```

- 4、在需要使用 负责通信模块(```communication```) 的 module 上引入 ```communication``` 

```gradle
compileOnly(project(":communication"))
```

**注意引入方式必须是 compileOnly ，否则会导致打包失败** 

- 5、调用 gradle 命令

<img src="/screenshot/gradle.png" alt="show" />

- 6、调用共享的代码

```kotlin
class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class.java)
        val user = userHelper.getUser()
        Log.e("user",""+user)
    }
}
```

#### 五、引入依赖库

```gradle
dependencies {
    //必须项 👇（可以直接放在公共 module）
    implementation 'io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:1.0.0'
}
```

这个配置项理论上来说不需要加，因为在配置上边步骤三中引入插件时，已经默认引入了

#### 混淆规则

下边是涉及到本库的一些必须混淆规则

```
# ModuleCommunication必备混淆规则 -----start-----

-keepnames @com.flyjingfish.module_communication_annotation.ExposeInterface class * {*;}
-keepnames @com.flyjingfish.module_communication_annotation.KeepClass class * {*;}
-keep @com.flyjingfish.module_communication_annotation.KeepClass class * {
    public <init>();
}

# ModuleCommunication必备混淆规则 -----end-----
```



### 最后推荐我写的另外一些库

- [OpenImage 轻松实现在应用内点击小图查看大图的动画放大效果](https://github.com/FlyJingFish/OpenImage)

- [ShapeImageView 支持显示任意图形，只有你想不到没有它做不到](https://github.com/FlyJingFish/ShapeImageView)

- [FormatTextViewLib 支持部分文本设置加粗、斜体、大小、下划线、删除线，下划线支持自定义距离、颜色、线的宽度；支持添加网络或本地图片](https://github.com/FlyJingFish/FormatTextViewLib)

- [主页查看更多开源库](https://github.com/FlyJingFish)


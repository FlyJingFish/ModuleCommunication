# ModuleCommunication

[![Maven central](https://img.shields.io/maven-central/v/io.github.FlyJingFish.ModuleCommunication/module-communication-annotation)](https://central.sonatype.com/search?q=io.github.FlyJingFish.ModuleCommunication)
[![GitHub stars](https://img.shields.io/github/stars/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/network/members)
[![GitHub issues](https://img.shields.io/github/issues/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/issues)
[![GitHub license](https://img.shields.io/github/license/FlyJingFish/ModuleCommunication.svg)](https://github.com/FlyJingFish/ModuleCommunication/blob/master/LICENSE)


### ModuleCommunication 旨在解决模块间的通信需求，可以让模块间的代码在依旧存在于其自己的模块的前提下，实现能够互相访问而不需要下沉到公共模块。以此来解决公共模块因为各个模块下沉代码而导致的不断膨胀的问题

### 每个模块使用此库生成属于自己模块的跳转 Activity 的帮助类，也是可以做到市面上那些 router 的功能，框架代码极少，风险可控！（不过仅限您一个小项目可以这么做，如果没有插件化完全够用）

## 特色功能

1、支持模块间共享 Java 或 Kotlin 代码

2、支持模块间共享 res 文件夹下的资源

3、支持模块间共享 assets 资源

[灵感来源-微信Android模块化架构重构实践](https://mp.weixin.qq.com/s/6Q818XA5FaHd7jJMFBG60w)

## 使用步骤

**在开始之前可以给项目一个Star吗？非常感谢，你的支持是我唯一的动力。欢迎Star和Issues!**

### 一、引入插件

1、在 **项目根目录** 的 ```build.gradle``` 里依赖插件

```gradle
buildscript {
    dependencies {
        //必须项 👇
        classpath 'io.github.FlyJingFish.ModuleCommunication:module-communication-plugin:1.0.9'
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

### 二、新增负责通信的 module

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

### 三、开始使用

#### 1⃣️ 共享 Kotlin 或 Java 代码

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

<img src="/screenshot/copy_code.png" alt="show" />

调用这个命令，将会生成共享代码。不调用直接运行代码可能会报错，一般报错最多次数为项目的 module 个数，即可生成完所有共享代码，如下图示
暴露的代码出现在了 ```communication``` 模块下

<img src="/screenshot/code.png" alt="show" />

- 6、在需要使用 ```lib-login``` 模块 上引入通信模块 ```communication```

a、```lib-login``` 引入通信模块

```gradle
compileOnly(project(":communication"))
```

**注意引入方式必须是 compileOnly ，否则会导致打包失败。并且在哪个 module 中使用就在哪引入**

b、如果 ```lib-login``` 也已经引入过 ```communication.export``` 插件，就无需配置这一步（不报错找不到类就无需引入）

```gradle
dependencies {
    //必须项 👇（可以直接放在公共 module）
    implementation 'io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:1.0.9'
}
```

- 7、在 ```lib-login``` 模块使用 ```lib-user``` 暴露出来的的代码

```kotlin
class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getSingleInstance 是获取单例  getNewInstance 是获取新的对象
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
        val user = userHelper.getUser()
        Log.e("user",""+user)
    }
}
```

#### 2⃣️ 共享 res 或 assets 文件夹下的资源

共享资源目前支持的类型包括 drawable、mipmap、string、array、layout、style、color、dimen、anim、animator、styleable、raw、menu、xml、navigation

以下面代码结构为例介绍下

<img src="/screenshot/res_demo.png" alt="show" />

- 1、 ```lib-login``` 需要暴露 res 或 assets 代码，可在 ```build.gradle``` 设置如下代码：

```gradle
communicationConfig{
     exposeResIds.addAll(arrayOf(
        "R.drawable.login_logo",
        "R.mipmap.login_logo2",
        "R.string.login_text",
        "R.array.weekname",
        "R.style.LoginAppTheme",
        "R.color.color_theme",
        "R.color.color_white_both",
        "R.layout.activity_login",
        "R.anim.toast_out",
        "R.animator.animator_incoming",
        "R.styleable.ColorTextView",
        "R.dimen.dp20",
        "R.raw.call_video_play",
        "R.raw.connecting",
        "R.menu.main_menu",
        "R.xml.dialog_match_success_scene",
        "R.navigation.nav_main",
        "R.color.textcolor_btn_tiger_bottom"
    ))
    //直接可以输入 assets 下的文件夹或者文件路径即可
    exposeAssets.addAll(arrayOf(
        "matching",
        "swipe_like"
    ))
}
```
直接调用下边命令即可

<img src="/screenshot/copy_assets_res.png" alt="show" />

共享之后在使用共享资源之前需要把下边这项设置关掉

<img src="/screenshot/gradle_set.png" alt="show" />

根目录下的 ```gradle.properties``` 的 ```android.nonTransitiveRClass``` 设置为 ```false```（否则 R 文件的包名只能用通信module的，打包后会出现异常）

- 2、在 ```lib-user``` 中使用资源即可

```kotlin
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        findViewById<ImageView>(R.id.iv_image).setImageResource(R.drawable.login_logo)
        val text = R.string.login_text
        val theme = R.style.LoginAppTheme
    }
}
```


#### 四、番外（非必须项）

- 1、如果你想定义更多的通信模块，而不是使用同一个，可以在使用 ```'communication.export'``` module 加入以下配置项

```gradle
plugins {
    id("communication.export")
}
communicationConfig{
    exportModuleName = "communication2"
}
```

这样共享代码会转移到 ```communication2``` 这个 module 中

- 2、调用以下命令可一键暴露所有代码

<img src="/screenshot/copy_all.png" alt="show" />



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

#### 常见问题

1、删除某一个类或资源后，通信 module 中依然存在删除的代码

- 这种情况下建议直接 clean 项目，再次重新调用 generateCommunication 命令。

### 最后推荐我写的另外一些库

- [OpenImage 轻松实现在应用内点击小图查看大图的动画放大效果](https://github.com/FlyJingFish/OpenImage)

- [AndroidAOP 是专属于 Android 端 Aop 框架，只需一个注解就可以请求权限、切换线程、禁止多点、监测生命周期等等，没有使用 AspectJ，也可以定制出属于你的 Aop 代码](https://github.com/FlyJingFish/AndroidAOP)

- [ShapeImageView 支持显示任意图形，只有你想不到没有它做不到](https://github.com/FlyJingFish/ShapeImageView)

- [FormatTextViewLib 支持部分文本设置加粗、斜体、大小、下划线、删除线，下划线支持自定义距离、颜色、线的宽度；支持添加网络或本地图片](https://github.com/FlyJingFish/FormatTextViewLib)

- [主页查看更多开源库](https://github.com/FlyJingFish)


## LogTools
---

正常的Log驱动都会有自己的打印控制，Android系统也不例外，不过不是很好，需要代码手动判断，可以看package/app下面打印log时都是这么使用的。因此我们封装一个Log库即可。

``` java
if (Log.isLoggable(tag, Log.DEBUG)) {
    Log.d(tag, msg);
}
```

目前功能为：
- 打印等级控制(静态编译[不建议] + 系统动态)
- 默认获取当前类名作为默认TAG（使用动态控制TAG时，建议使用全局TAG便于控制）
- 打印时，打印当前类名，线程及行号等

-------
## import/引入

module's build.gradle (模块的build.gradle)

``` gradle
	dependencies {
	         compile 'com.jerey.logtools:logtools:0.4'
	}
```

## 用法

#### 1.直接使用
直接使用，默认无边框，默认所有都打印，默认使用类名为TAG
``` java
  //未做初始化，默认使用
  LogUtils.i("xiamin");
  LogUtils.w("xiamin");
  LogUtils.e("xiamin");
  LogUtils.json(text_json);
```

![](http://upload-images.jianshu.io/upload_images/2305881-92e0fd4307ee2df4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


使用自定义TAG

``` java
  LogUtils.e("iii","xiamin");
```

![](http://upload-images.jianshu.io/upload_images/2305881-e2f5f99470c5788c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 2.进行打印等级配置

设置取消边框，设置打印等级为WARN，那么打印等级低于WARN的都不会被打印 <br>
(打印等级为：Log.VERBOSE < Log.DEBUG < Log.INFO < Log.WARN < Log.ERROR < Log.ASSERT)

``` java
在Application类中进行初始化全局TAG。
LogUtils.getSettings()
        .setGlobalLogTag(LogApp.class.getSimpleName()) //设置全局TAG
        .setLogLevel(Log.VERBOSE)   // 设置内部打印等级，可不设置
        .setBorderEnable(true)   // 设置边框开启，默认关闭
        .setInfoEnable(true)   // 设置线程信息打印
        .setLogEnable(true);   //Log总开关，默认开启。可不设置

LogUtils.v("hello1");
LogUtils.i("hello2");
LogUtils.w("hello3");
LogUtils.e("hello4");
```
使用时：不重要的Log可以使用LogUtils.v(Obj) 直接输出，LogUtils会自动识别类名和行号打印的。
此时效果：

![](http://upload-images.jianshu.io/upload_images/2305881-41367949ee48cbdd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到没有“hello1”的打印

开启log的命令：adb shell setprop log.tag.@params{全局TAG} {level}
敲了：adb shell setprop log.tag.LogApp VERBOSE 之后


![](http://upload-images.jianshu.io/upload_images/2305881-9af7ed38e451777e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到hello1出来了

注1：adb shell setprop log.tag.@params{全局TAG} {level}  动态设置log打印等级重启后不生效，每次开机都得设置。
注2：Terminal中 adb logcat输入对于换行符的情况会自动再次打印TAG，即明明一行Log输出的确是前面两个,在使用grep命令的时候可能会存在问题
![](http://upload-images.jianshu.io/upload_images/2305881-fc418c218d662363.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](http://upload-images.jianshu.io/upload_images/2305881-747f68ddb9553f31.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


-----------------------

## Why this library ?

To help developer control log easily. We don't want write `private static final String TAG = "xxx"`

-------
##License

```
Copyright 2017 Jerey-Jobs.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

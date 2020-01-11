```
目录

1. 前言
2. 尝试的方法
    2.1. Smart Tomcat插件
3. 可行方法
    3.1. 说明
    3.2. 依赖环境
    3.3. IDEA中执行Gradle脚本
        3.3.1. 在Terminal中执行
        3.3.2. 在Run/Debug Configurations中执行
            3.3.2.1. 解决在Run/Debug Configurations中执行Gradle脚本中文乱码问题
    3.4. 在IDEA中进行远程调试
        3.4.1. 在IDEA创建远程调试配置并获取调试参数
        3.4.2. 增加调试参数后启动被调试Java进程
        3.4.3. 在IDEA启动调试
    3.5. runTomcat.gradle脚本使用方法
        3.5.1. 任务及参数说明
            3.5.1.1. 环境变量
            3.5.1.2. JVM参数
        3.5.2. 环境配置
        3.5.3. 使用场景
            3.5.3.1. 正常启动Tomcat进程
            3.5.3.2. 使用Tomcat实例启动脚本启动Tomcat进程
            3.5.3.3. 停止Tomcat进程
                3.5.3.3.1. 直接关闭Tomcat窗口（应用实例无法接收到Web容器销毁通知）
                3.5.3.3.2. 使用Tomcat实例停止脚本停止Tomcat进程（应用实例可以接收到Web容器销毁通知）
            3.5.3.4. 调试Web应用
                3.5.3.4.1. 进程启动后调试
                3.5.3.4.2. 从进程启动开始调试（操作两次）
                3.5.3.4.3. 从进程启动开始调试（一键完成）
                3.5.3.4.4. 调试Tomcat的类
    3.6. 其他说明
    3.7. 原理说明
        3.7.1. 生成Web应用所需文件
        3.7.2. 生成Tomcat实例
        3.7.3. 处理Tomcat上下文描述符文件
        3.7.4. 生成Tomcat实例启动/停止脚本
        3.7.5. 启动Tomcat
        3.7.6. 调试Web应用
```

# 1. 前言

IntelliJ IDEA Community Edition（社区版）不支持Tomcat，不想花钱购买Ultimate版本，也不想使用Eclipse，尝试通过其他方式使IDEA社区版支持Tomcat。

# 2. 尝试的方法

## 2.1. Smart Tomcat插件

在IDEA社区版（2019.2.4）中安装了Smart Tomcat插件，并使用其启动Tomcat应用，遇到了以下问题：

- Web应用的class文件未被自动拷贝到对应的Web应用根目录中，导致Web应用的代码未被加载，需要手工处理，使用不方便；
- Tomcat日志只生成了localhost_access_log.txt日志文件，没有生成catalina.log、localhost.log等日志文件，排查问题不方便。

使用Smart Tomcat插件遇到问题之后，放弃了使用该插件，没有再去分析是否因为使用方法不当。

# 3. 可行方法

## 3.1. 说明

之后通过Gradle脚本，使IDEA社区版支持Tomcat（也支持IDEA Ultimate版）。

完成的Gradle脚本及示例Web工程代码可以从 https://github.com/Adrninistrator/IDEA-IC-Tomcat/ 、 https://gitee.com/adrninistrator/IDEA-IC-Tomcat/  下载，脚本内容很短，有效行数不超过200行，处理也很简单。

通过上述Gradle脚本，结合IDEA的功能，在完成配置后，可以实现以下功能，能够达到与Eclipse或IDEA Ultimate版本对Tomcat支持的功能接近的效果。

- 一键启动Tomcat并加载Web应用
- 一键停止Tomcat（应用实例可以接收到Web容器销毁通知）
- 一键启动可调试的Tomcat（Web应用）
- 一键从Tomcat（Web应用）启动时开始调试

## 3.2. 依赖环境

- IDEA

使用IntelliJ IDEA Community Edition 2019.2.4版本。

- Tomcat

支持Tomcat 7、8、9版本（测试过Tomcat 7.0.55、7.0.79、8.5.20、9.0.30版本），理论上也支持Tomcat 5、6版本（未测试）。

- Gradle

支持Gradle 4、5、6版本（测试过Gradle 4.1、4.7、5.6.4、6.0.1版本）。

- JDK

使用JDK 1.8.0_144版本。

- 操作系统

使用Windows 7 x64 SP1版本。

## 3.3. IDEA中执行Gradle脚本

假设存在以下Gradle任务：

```gradle
task testTask {
    doFirst {
        println "测试-" + System.getProperty("arg")
    }
}
```

### 3.3.1. 在Terminal中执行

在IDEA的Terminal中执行以上Gradle任务时，可以通过gradle或gradlew命令，以命令行的方式执行，并可以通过“-D”前缀指定传递给Gradle脚本的JVM参数，与执行Java程序时类似。

执行上述任务的Gradle命令示例如下所示：

```cmd
gradle testTask -Darg=abc
gradlew testTask -Darg=abc
```

执行的结果如下所示：

```
> Task :testTask
测试-abc
```

### 3.3.2. 在Run/Debug Configurations中执行

打开IDEA的“Run/Debug Configurations”窗口，点击加号后，从弹出菜单中选择“Gradle”，可以新增一个配置，用于执行对应的Gradle任务。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a01.jpg)

打开“Configuration”标签页，对参数进行修改。

- 点击“Gradle project”右侧的图标，选择当前项目；

- 在“Tasks”右侧填入需要执行的Gradle任务名称，如“testTask”；

- 在“VM options”右侧填入需要传递给Gradle脚本的JVM参数，如“-Darg=test_arg”，点击箭头图标可以展开编辑框。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a02.jpg)

在Run/Debug Configurations中完成配置后，可以选中对应的配置，点击执行按钮开始执行。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a03.jpg)

执行的结果在“Run”窗口中显示，如下所示：

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a04.jpg)

当需要修改Run/Debug Configurations使用的Gradle时，可以打开IDEA的“File | Settings | Build, Execution, Deployment | Build Tools | Gradle”菜单，修改“Use Gradle from”选项。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a05.jpg)


#### 3.3.2.1. 解决在Run/Debug Configurations中执行Gradle脚本中文乱码问题

在Run/Debug Configurations中执行Gradle脚本或编译过程时，输出的中文可能乱码。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a06.jpg)

或如下图所示：

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a07.jpg)

进行以下设置，可以解决上述中文乱码问题。

- 打开IDEA的“Help”“Edit Custom VM Options...”菜单；

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a08.jpg)

- 在打开的文件最后增加“-Dfile.encoding=UTF-8”；

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a09.jpg)

- 重启已打开的IDEA后生效。

## 3.4. 在IDEA中进行远程调试

### 3.4.1. 在IDEA创建远程调试配置并获取调试参数

打开IDEA的“Run/Debug Configurations”窗口，点击加号后，从弹出菜单中选择“Remote”，可以新增一个配置，用于进行远程调试。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a10.jpg)

打开“Configuration”标签页，对参数进行修改。

- “Debugger mode”选项保持“Attach to remote JVM”
- “Transport”选项保持“Socket”
- “Host”参数保持“localhost”
- “Port”参数指定被调试的Java进程监听的调试端口
- “Use module classpath”选择被调试的Java进程对应的源代码模块

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a11.jpg)

“Command line arguments for remote JVM”展示的调试参数不能编辑，会跟随上方的参数变化。

- “Transport”选项“Socket”对应调试参数“transport=dt_socket”
- “Port”参数对应调试参数“address=”

### 3.4.2. 增加调试参数后启动被调试Java进程

复制“Command line arguments for remote JVM”对应的调试参数，将其添加到被调试Java进程的JVM参数中，启动Java进程。

**需要注意，IDEA调试配置中的Port参数，与被调试Java进程使用的调试参数中的address参数值需要相同，即调试器连接的端口需要与被调试Java进程监听的端口一致。**

### 3.4.3. 在IDEA启动调试

选中对应的远程调试配置，点击调试按钮开始调试，与使用IDEA启动Java进程并调试类似。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a12.jpg)

调试启动成功后，在“Debug”“Console”窗口提示“Connected to the target VM”，如下所示。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a13.jpg)

点击停止按钮可以停止调试，IDEA的“Debug”窗口会出现类似“Disconnected from the target VM, address: 'localhost:5555', transport: 'socket'”的提示。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a14.jpg)

停止被调试Java进程时，IDEA启动的调试会自动结束。

停止调试后，不会使被调试的Java进程退出。

当对Tomcat进行调试时，停止调试后在Tomcat窗口会出现类似“Listening for transport dt_socket at address: 5555”提示。

以上调试方法也支持非Web应用，以及远程的Java进程。

## 3.5. runTomcat.gradle脚本使用方法

将runTomcat.gradle脚本拷贝至Java Web应用工程中，在build.gradle脚本中添加“apply from: 'runTomcat.gradle'”。

### 3.5.1. 任务及参数说明

runTomcat.gradle脚本中提供了名称为“startTomcat”的任务，用于启动Tomcat并加载Web应用。

在脚本中使用了以下参数。

#### 3.5.1.1. 环境变量

- TOMCAT_HOME_4IDEA

|环境变量名称|TOMCAT_HOME_4IDEA|
| ------------ | ------------ |
|作用|指定需要使用的Tomcat的安装目录|
|示例|C:\program\apache-tomcat-7.0.79|
|必须设置|是|
|说明|使用Tomcat解压后的目录|

- TOMCAT_INSTANCE_4IDEA
  
|环境变量名称|TOMCAT_INSTANCE_4IDEA|
| ------------ | ------------ |
|作用|指定保存各应用对应的Tomcat实例的目录|
|示例|D:\tomcat-test|
|必须设置|否|
|说明|默认使用当前用户目录的“.tomcat_idea”目录。例如为“C:\Users\user\.tomcat_idea”目录，在该目录中保存了各应用对应的Tomcat实例，各实例分别对应其中的一个目录|

#### 3.5.1.2. JVM参数

- appName
  
|JVM参数名称|appName|
| ------------ | ------------ |
|作用|指定当前应用对应的Tomcat实例的名称|
|示例|app_test|
|必须设置|是|

- noBuild
  
|JVM参数名称|noBuild|
| ------------ | ------------ |
|作用|指定跳过Web应用的编译过程|
|示例|1|
|必须设置|否|
|说明|当为非空时，会跳过；不指定或为空时，不会跳过|

- contextPath
  
|JVM参数名称|contextPath|
| ------------ | ------------ |
|作用|指定当前Web应用的上下文路径|
|示例|context_test|
|必须设置|否|
|说明|默认使用appName参数值|

- arg4Tomcat
  
|JVM参数名称|arg4Tomcat|
| ------------ | ------------ |
|作用|启动Tomcat时使用的JVM参数|
|示例|"-DtestValue=aaabbbccc -Dlog.home=E:\desktop\log-test -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555"|
|必须设置|否|
|说明|***需要使用半角双引号包含全部的参数，否则会被空格截断***。可以指定jdwp调试参数|

### 3.5.2. 环境配置

在使用提供的Gradle脚本runTomcat.gradle时，首先需要完成环境配置，“TOMCAT_HOME_4IDEA”环境变量配置需要增加，“TOMCAT_INSTANCE_4IDEA”环境变量的配置可选。

在完成环境变量配置后，需要重启已打开的IDEA后生效。

为了验证环境变量配置是否已生效，可在IDEA的Terminal中执行“echo %TOMCAT_HOME_4IDEA%”，当配置完成时会输出对应的环境变量值，未配置或未生效时会输出“%TOMCAT_HOME_4IDEA%”。

### 3.5.3. 使用场景

#### 3.5.3.1. 正常启动Tomcat进程

正常启动Tomcat进程，加载示例工程中的Web应用的Gradle命令如下所示：

```gradle
gradlew startTomcat -DappName=test-tomcat -Darg4Tomcat="-DtestValue=aaabbbccc -Dlog.home=E:\desktop\log-test"
```

在以上示例中，指定当前应用对应的Tomcat实例的名称，以及Web应用的上下文路径，均为“test-tomcat”，指定启动Tomcat时使用的JVM参数为“"-DtestValue=aaabbbccc -Dlog.home=E:\desktop\log-test"”。

第一次执行上述Gradle命令时（或删除了当前应用对应的Tomcat实例的目录后），Gradle脚本输出的结果如下所示：

```gradle
> Configure project :
noBuild参数值: null

> Task :clean
> Task :compileJava
> Task :processResources
> Task :classes

> Task :startTomcat
appName参数值: test-tomcat
contextPath参数值: test-tomcat
arg4Tomcat参数值: -DtestValue=aaabbbccc -Dlog.home=E:\desktop\log-test
tomcatDir参数值: C:\program\apache-tomcat-7.0.55
instanceDir参数值: C:\Users\user\.tomcat_idea
当前路径: E:\IDEA-IC-Tomcat
当前应用使用的Tomcat实例目录: C:\Users\user\.tomcat_idea\test-tomcat
生成Web应用所需文件
生成Web应用所需文件-完成
判断是否需要创建Tomcat实例
创建Tomcat实例
文件不存在: C:\Users\user\.tomcat_idea\test-tomcat\conf\Catalina\localhost\test-tomcat.xml
写入文件: C:\Users\user\.tomcat_idea\test-tomcat\conf\Catalina\localhost\test-tomcat.xml
生成bat停止脚本文件: C:\Users\user\.tomcat_idea\test-tomcat\test-tomcat-stop.bat
文件不存在: C:\Users\user\.tomcat_idea\test-tomcat\test-tomcat-stop.bat
写入文件: C:\Users\user\.tomcat_idea\test-tomcat\test-tomcat-stop.bat
生成bat启动脚本文件: C:\Users\user\.tomcat_idea\test-tomcat\test-tomcat-start.bat
文件不存在: C:\Users\user\.tomcat_idea\test-tomcat\test-tomcat-start.bat
写入文件: C:\Users\user\.tomcat_idea\test-tomcat\test-tomcat-start.bat
```

当以上Gradle脚本执行成功后，会启动Tomcat，Tomcat进程会产生单独的命令行窗口。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a15.jpg)

使用浏览器访问示例工程的Controller，URL为“ http://localhost:8080/test-tomcat/testrest/get ”，输出结果为当前时间戳及“testValue”对应的JVM参数值，访问结果如下所示：

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a16.jpg)

#### 3.5.3.2. 使用Tomcat实例启动脚本启动Tomcat进程

runTomcat.gradle脚本的“startTomcat”任务执行时，会在当前Web应用对应的Tomcat实例目录生成启动脚本，如前文输出的示例“C:\Users\user\\.tomcat_idea\test-tomcat\test-tomcat-start.bat”。

当不需要对Web应用重新编译时，可以直接执行上述启动脚本，启动Tomcat进程，加载Web应用。

#### 3.5.3.3. 停止Tomcat进程

在示例工程中，TestPostConstructLazyFalse.preDestroy()方法使用了@PreDestroy注解，该方法会在应用停止阶段执行，会在当前目录生成名称为“preDestroy-”及当前时间戳的目录。

##### 3.5.3.3.1. 直接关闭Tomcat窗口（应用实例无法接收到Web容器销毁通知）

将Tomcat窗口关闭，可以停止Tomcat进程。

通过该方法停止Tomcat进程，会使Tomcat进程直接结束，应用实例无法接收到Web容器销毁通知，示例工程的TestPostConstructLazyFalse.preDestroy()方法不会执行，当前目录不会生成目录。

##### 3.5.3.3.2. 使用Tomcat实例停止脚本停止Tomcat进程（应用实例可以接收到Web容器销毁通知）

runTomcat.gradle脚本的“startTomcat”任务执行时，会在当前Web应用对应的Tomcat实例目录生成停止脚本，如前文输出的示例“C:\Users\user\\.tomcat_idea\test-tomcat\test-tomcat-stop.bat”。

执行上述停止脚本，会执行Tomcat提供的stop命令，可以停止Tomcat进程，应用实例可以接收到Web容器销毁通知，示例工程的TestPostConstructLazyFalse.preDestroy()方法会执行，当前目录会生成目录，如下所示。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a17.jpg)

#### 3.5.3.4. 调试Web应用

以下在IDEA创建远程调试配置并获取调试参数的过程，可以参考前文对应内容。

##### 3.5.3.4.1. 进程启动后调试

- 启动Tomcat进程

获取到调试参数如下所示：

```gradle
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555
```

在执行Gradle “startTomcat”任务时，添加以上参数至arg4Tomcat参数中（可添加到IDEA的“Run/Debug Configurations”的Gradle配置中），如下所示：

```gradle
gradlew -DappName=test-tomcat
-Darg4Tomcat="-DtestValue=aaabbbccc -Dlog.home=E:\desktop\log-test -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555"
```

执行以上Gradle命令后，启动Tomcat进程。

- 启动IDEA调试

之后可以在IDEA中启动调试。

对URI“/testrest/get”对应的TestRestController.get()方法设置断点，通过浏览器访问后，IDEA调试器进入断点，可在“Debug”“Debugger”窗口查看。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a18.jpg)

##### 3.5.3.4.2. 从进程启动开始调试（操作两次）

以上使用的调试参数中的suspend参数值为“n”，被调试的进程在启动时不会暂停线程，会正常启动。只支持先启动被调试进程，再进行调试。

当需要从进程启动开始调试时，需要将调试参数中的suspend参数值设为“y”，被调试的进程在启动时会暂停线程，等待调试器连接address指定的端口后，才会继续启动。

- 启动Tomcat进程

当需要从进程启动开始调试时，调试参数示例如下。

```gradle
-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5555
```

将Gradle任务“startTomcat”的“arg4Tomcat”参数中配置的“suspend”参数设置为“y”，再通过该命令启动Tomcat，Tomcat窗口只显示“Listening for transport dt_socket at address: 5555”，未显示其他内容，即Tomcat进程此时在等待调试器连接address参数对应的端口，未完成启动。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a19.jpg)

- 启动IDEA调试

在Web应用初始化阶段会执行的代码设置断点，例如在示例工程的带有@PostConstruct注解的TestPostConstructLazyFalse.postConstruct()方法设置断点。

在IDEA启动调试，查看Tomcat窗口日志已更新，说明Tomcat进程已启动。

查看IDEA调试窗口，已进入以上设置的断点，证明可以从Web应用启动开始调试。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a20.jpg)

##### 3.5.3.4.3. 从进程启动开始调试（一键完成）

以上从进程启动开始调试的操作需要先启动Tomcat进程，再启动IDEA调试，可以优化为一键完成。

打开IDEA的“Run/Debug Configurations”窗口，选择“Remote”配置，点击“Before launch: Activate tool window”下方的加号按钮，选择“Run Gradle task”。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a21.jpg)

弹出“Select Gradle Task”窗口，“Gradle project”“Tasks”“VM options”参数配置，可参考在IDEA添加Gradle配置，通过“startTomcat”任务启动Tomcat进程的步骤，需要确保“VM options”参数填写的“arg4Tomcat”参数中的调试参数“suspend”为“y”。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a22.jpg)

“Before launch: Activate tool window”下方的列表会出现配置的Gradle任务。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a23.jpg)

完成以上配置后，在启动IDEA调试之前，会执行指定的Gradle任务“startTomcat”，以“suspend=y”的调试参数启动Tomcat进程。可以实现一键从进程启动开始调试，与IDEA Ultimate版或Eclipse对Web应用从启动开始调试的效果类似。

##### 3.5.3.4.4. 调试Tomcat的类

当需要对Tomcat的类进行调试时，需要将Tomcat的lib目录添加至IDEA的Web应用工程的依赖中，否则调试时无法查看Tomcat的类。

打开IDEA的“Project Structure”窗口，选择“Project Settings”“Modules”标签页，在打开的窗口中选择Web项目主模块，选择“Dependencies”标签页，点击加号按钮，选择“JARs or directories...”菜单。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a24.jpg)

在弹出的窗口，选择当前使用的Tomcat的安装目录的lib目录。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a25.jpg)

完成添加后，Tomcat的lib目录会出现在“Dependencies”标签页的最下方。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a26.jpg)

当刷新Gradle后，项目配置会重置，以上添加的依赖会被清理，需要重新添加。

完成以上配置后，在Tomcat的org.apache.catalina.startup.HostConfig$DeployDescriptor类run方法设置断点，从进程启动开始调试，可以在IDEA的Debug窗口看到已进入断点。该方法是Tomcat启动时执行的第一个Tomcat的类的方法。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a27.jpg)

## 3.6. 其他说明

- 生成Web应用所需文件调整

runTomcat.gradle脚本中buildFiles4WebApp方法用于生成Web应用所需文件，拷贝的目录与文件可以根据实际情况调整。

- 调试端口需要确保未被监听

被调试Java进程的调试参数中指定的adderss参数对应的调试端口，需要确保未被监听，否则被调试Java进程会启动失败，窗口会自动消失，Tomcat的提示如下。

![pic](https://github.com/Adrninistrator/IDEA-IC-Tomcat/blob/master/pic/a28.jpg)

- 重新创建Tomcat实例

当因为需要修改使用的Tomcat版本，或其他原因，导致需要重新创建Web应用使用的Tomcat实例时，需要将对应的Tomcat实例目录删除，如“C:\Users\user\.tomcat_idea\test-tomcat”，之后再执行Gradle的startTomcat任务。

- Tomcat实例目录配置修改与日志查看

当前Web应用对应的Tomcat实例目录，在执行Gradle “startTomcat”任务时会输出，如“C:\Users\user\.tomcat_idea\test-tomcat”。

当需要修改当前Web应用对应的Tomcat使用的HTTP服务端口、SSL配置、线程池数量等参数时，可以修改Tomcat实例目录的“conf\server.xml”文件，说明略。

当需要同时启动多个Tomcat进程分别加载不同的Web应用时，需要先修改对应Tomcat实例的“conf\server.xml”文件中的监听端口，避免不同的Tomcat实例使用同一个端口导致不可用。

Tomcat实例目录的“logs”目录保存了Tomcat日志文件，使用默认配置时，包括“catalina.log”“localhost.log”“localhost_access_log.txt”“host-manager.log”“manager.log”等。

## 3.7. 原理说明

通过runTomcat.gradle脚本启动Tomcat进程并加载Web应用，与Eclipse或IDEA Ultimate（2018.3及之前版本）的原理类似，如下所示。

### 3.7.1. 生成Web应用所需文件

当noBuild参数未指定或为空时，会先执行Gradle的classes任务完成编译，再执行buildFiles4WebApp方法，完成以下操作：

- 将编译生成的class文件拷贝至“build/tomcat/WEB-INF/classes”目录中
- 将“src/main/resources/”目录（配置文件）拷贝至“build/tomcat/WEB-INF/classes”目录中
- 将“src/main/webapp/”目录（静态资源与WEB-INF/web.xml文件）拷贝至“build/tomcat”目录中
- 将依赖的jar包拷贝至“build/tomcat/WEB-INF/lib”目录中

### 3.7.2. 生成Tomcat实例

判断当前应用使用的Tomcat实例目录是否已存在，若已存在时则不再处理。

当前应用使用的Tomcat实例目录不存在时，进行以下操作生成Tomcat实例：

- 在“TOMCAT_INSTANCE_4IDEA”环境变量参数值对应的目录，或当前用户目录的“.tomcat_idea”目录中，创建当前Web应用使用的Tomcat实例目录，使用Gradle “startTomcat”任务执行时的“appName”参数值作为目录名称；
- 将“TOMCAT_HOME_4IDEA”环境变量参数值指定的，需要使用的Tomcat安装目录的bin、conf目录拷贝至当前Web应用使用的Tomcat实例目录中；
- 在当前Web应用使用的Tomcat实例目录创建logs、temp、work目录。

### 3.7.3. 处理Tomcat上下文描述符文件

上下文描述符是一个XML文件，其中包含与Tomcat相关的上下文配置，例如命名资源或会话管理器配置等。当Tomcat启动时，上下文描述符会被首先部署。可参考 https://tomcat.apache.org/tomcat-7.0-doc/deployer-howto.html 。

上下文描述符需要保存在当前Web应用使用的Tomcat实例目录的“conf\Catalina\localhost”目录中，当前Web应用的上下文路径与上下文描述符文件名相同（不含.xml后缀），上下文路径的大小写与文件名的大小写一致。

runTomcat.gradle脚本会检查当前Web应用对应的Tomcat实例的上下文描述符，若文件已存在且内容不需要修改，则不执行写入操作；若文件不存在或文件内容需要修改，则执行文件写入操作。

### 3.7.4. 生成Tomcat实例启动/停止脚本

Tomcat实例启动/停止脚本会保存在当前Web应用对应的Tomcat实例目录中。

runTomcat.gradle脚本会检查对应的脚本文件，在需要写入时进行写入操作。

在启动脚本中会调用Tomcat实例目录的“bin\startup.bat”脚本；在停止脚本中会调用Tomcat实例目录的“bin\shutdown.bat”脚本。

### 3.7.5. 启动Tomcat

runTomcat.gradle脚本会执行生成的Tomcat实例启动脚本，以启动Tomcat。

### 3.7.6. 调试Web应用

以上的远程调试使用了JDWP（Java Debug Wire Protocol），可参考 https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/jdwp-spec.html ，JDWP是用于调试器与其调试的Java虚拟机之间的通信协议。

JPDA（The Java Platform Debugger Architecture）包含三个接口，供调试器在桌面系统的开发环境中使用。JDWP属于其中一个，JPDA的说明可参考 https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/ 。

在JPDA连接和调用详细信息（https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/conninv.html ）中，说明当suspend参数为“y”时，VMStartEvent会使用SUSPEND_ALL作为暂停策略；当suspend参数为“n”时，VMStartEvent会使用SUSPEND_NONE作为暂停策略。

参考 https://docs.oracle.com/javase/8/docs/jdk/api/jpda/jdi/com/sun/jdi/event/VMStartEvent.html ，说明VMStartEvent是目标VM的初始化通知，在启动主线程之前和执行任何应用程序代码之前，会收到此事件。

参考 https://docs.oracle.com/javase/8/docs/jdk/api/jpda/jdi/com/sun/jdi/request/EventRequest.html ，说明SUSPEND_ALL在事件发生时会暂停全部线程；SUSPEND_NONE在事件发生时不会暂停线程。

根据以上说明可知，当Java进程的调试参数中的suspend参数为“y”时，在启动时会暂时全部线程；suspend参数为“n”时，在启动时不会暂时线程。

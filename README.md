通用逻辑的工具库，此分支为Android Support依赖版本

Tools库是一个工具库，里面包含文件操作、日期读取、媒体操作等众多工具函数，目前最新代码已将文件存储做了沙箱存储的适配，如果项目需要适配Android10以上版本，建议使用最新的Tools库代码。

工具函数不一一列举了，麻烦自行查看，一般常用的有FileTool和LogTool,LogTool是用来打印日志的，做了大日志打印和存储的逻辑，打印大日志，会缓冲打印，不会因超过4k而截断。

Tools库使用时需要先初始化一下，代码如下：

```java
第一个参数是Application对象，第二个参数是是否为debug，此参数会影响Log打印，默认release下不打印日志
ToolInit.get().init(application,true);
```
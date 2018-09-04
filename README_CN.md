更多请移步至：[我的CSDN博客](http://blog.csdn.net/liaoinstan/article/details/51023907)  　　　　apk演示：[点击下载](https://github.com/liaoinstan/SpringView/blob/master/apk/demo-release.apk?raw=true)  　　　　English document：[English document](https://github.com/liaoinstan/SpringView/blob/master/README.md)

SpringView
=====
**SpringView** 是一个提供了上下拖拽的功能组件，能够进行高度自定义，实现各种下拉\上拉动画效果，demo里实现了：仿阿里旅行、仿美团，仿QQ下拉刷红包，仿acfun等，完全兼容源生控件如ListView、RecyclerView、ScrollView、WebView等，使用简单，轻易定制自己风格的拖拽页面
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/springview.png)

**SpringView** 单独将头部/尾部独立出来，几乎可以实现任何你想要的效果，只需要继承BaseHeader(或Footer)实现接口

**SpringView** 能在运行时动态地替换头部/尾部，只需要设置不同的头尾即可：springView.setHeader(MyHeader());

**SpringView** 支持多点触控，可以两只手连续拖拽，你可以定制一些非常有趣的效果（例如demo5）

**SpringView** 提供了2种拖拽方式（重叠和跟随），可以动态地切换

**SpringView** 为不想去自定义头/尾的懒人提供了7种默认的实现（模仿了阿里，腾讯，美团等多种风格）如下，还会继续增加 

**SpringView** 支持和 AppBarLayout 联动
　

![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212323072.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212372609.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212462800.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212485237.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212517801.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212658972.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212769245.gif)

**📌新增：** 

![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/weixin_header.gif) 

**如何使用 SpringView**
--------

在布局文件中添加SpringView，注意SpringView和ScrollView有同样的限制：只能有一个子元素：

```
	<com.liaoinstan.springview.widget.SpringView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:header="@layout/myheader"
            app:footer="@layout/myfooter">

            <ListView RecyclerView ScrollView or others
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

	</com.liaoinstan.springview.widget.SpringView>
```
当然，你也可以不在布局中设置header\footer，使用代码动态添加：

```
	springView.setHeader(new DefaultHeader(this));
	springView.setFooter(new DefaultFooter(this));
```

**添加监听**
--------
如果需要处理的话，只需在代码中添加监听：

```
springView.setListener(new SpringView.OnFreshListener() {
      @Override
      public void onRefresh() {
      }
      @Override
      public void onLoadmore() {
      }
});
```

**获取SpringView**
--------
使用 Gradle:
```
dependencies {

   //SpringView核心库 (只包含DefaultHeader/Footer)
   implementation 'com.liaoinstan.springview:library:1.5.1'

   //以下是各个风格的Header/Footer，选择自己喜欢的引入
   implementation 'com.liaoinstan.springview:AcfunHeader:1.5.1'         //AcFun风格 （header and footer）
   implementation 'com.liaoinstan.springview:AliHeader:1.5.1'           //阿里旅行风格 （header and footer）
   implementation 'com.liaoinstan.springview:MeituanHeader:1.5.1'       //美团风格 （header and footer）
   implementation 'com.liaoinstan.springview:RotationHeader:1.5.1'      //齿轮机械风格 （header and footer）
   implementation 'com.liaoinstan.springview:WeixinHeader:1.5.1'        //微信小程序header（只有header）
}
```
或者 Maven：
```
<dependency>
  <groupId>com.liaoinstan.springview</groupId>
  <artifactId>library</artifactId>
  <version>1.5.1</version>
  <type>pom</type>
</dependency>
```


**更新日志**
--------
####**v1.5.1**
 - 修复在SpringView内部嵌套布局无法滚动的bug，修复给SpringView设置Give.NONE熟悉时调用callFresh()无法正确回弹的问题。

####**v1.5.0**
 - 把所有的header和footer单独从SpringView库中分离出来了（除DefaultHeader/Footer），现在可以只选择单独导入自己想要的headers/footers，彻底精简后的SpringView library只有**26KB**，后期新增的header/footer都将以独立库的形式发布，不再冗余到SpringView核心库里，库链接参见上述[获取SpringView]内容

####**v1.4.0**
 - 新增setMovePara()方法用于设置拖拽时的阻尼系数，新增了一个WeixinHeader仿微信小程序header，新增新的模式：DRAG（拖拽模式），可以实现类似于SwipeRefreshLayout的交互效果，重新设计了overLap模式的实现方式，更加稳定，弃用了support过时api，优化拖拽流畅性和交互效果细节

####**v1.3.3**
 - 优化几个体验性问题，新增onFinishFreshAndLoadDelay(int delay)，callFreshDelay(int delay)方法，优化和BottomSheetBehavior联用的表现，优化在BottomSheetDialog中使用的表现

####**v1.3.2**
 - 修复空数据情况下和AppBarLayout联动阻尼的bug，新增setEnableFooter(boolean)，setEnableHeader(boolean)禁用或启用header/footer
 
####**v1.3.0**
 - 支持和 AppBarLayout 联动，修复滚动冲突的问题

####**v1.2.7**
 - 修复一些bug

####**v1.2.6**
 - 修复了内边距的失效的问题

####**v1.2.5**
 - 新增 **setEnable(boolean)** 方法，用于禁用/启用SpringView

####**v1.2.4**

 - 新增 **callFresh()** 方法, 用于手动调用刷新
 - 修复了内置水平滑动控件的事件冲突

####**v1.2.2**
 - 修复了会多次回调刷新的问题

####**v1.2.1**
 - 修复点击事件偶尔失效的问题


**反馈**
--------
如果遇到问题或者好的建议，请反馈到我的邮箱：liaoinstan@outlook.com
或者在我的博客留言

如果觉得对你有用的话，点一下右上的星星赞一下吧

**更多**
--------
更多请移步至：[我的CSDN博客](http://blog.csdn.net/liaoinstan) 

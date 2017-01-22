More, please visit：[My CSDN](http://blog.csdn.net/liaoinstan/article/details/51023907)  　　　　apk demo：[download](https://github.com/liaoinstan/SpringView/blob/master/apk/demo-debug.apk?raw=true)  　　　　中文文档：[中文文档](https://github.com/liaoinstan/SpringView/blob/master/README_CN.md)

SpringView
=====
**SpringView**  is one provides the function of the upper and lower drag and drop components, can be highly customized, implement all kinds of drop-down \ pull painting effect, implementation in the demo: imitation ali travel, imitation Meituan, imitation QQ drop-down brush red envelopes, imitation acfun etc., is fully compatible with the source control such as ListView, RecyclerView, ScrollView, WebView, etc., using a simple, easily customize their style of drag and drop page
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/springview.png)
**SpringView** Individual independence will head/foot,  almost achieve the effect of whatever you want, just need to inherit BaseHeader (or Footer) to implement the interface

**SpringView** Can dynamically replace the head/tail, you just need to set up different Footer：springView.setHeader(MyHeader());

**SpringView** Multi-touch support, can two hands drag and drop in a row, you can customize some interesting results

**SpringView** Provides two methods for drag and drop (overlap and follow), can be dynamically switching

**SpringView** For lazy people don't want to go to the custom head/tail provides 7 kinds of the default implementation (mimics the ali, tencent, Meituan etc. Various style) as follows, will continue to increase
　
　

![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212323072_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212372609_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212462800_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212485237_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212517801_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212658972_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212769245_s.gif)

**How to use SpringView**
--------

Add Spring View in the layout file, note that Spring View and ScrollView have the same limitation: only one child element：

```
	<com.liaoinstan.springview.widget.SpringView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:header="@layout/myheader"
            app:footer="@layout/myfooter">

            <listView
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

     </com.liaoinstan.springview.widget.SpringView>
```
Of course, you also can not add in the layout, use the code dynamically added：

```
	springView.setHeader(new DefaultHeader(this));
	springView.setFooter(new DefaultFooter(this));
```

**Add Listener**
--------
If you need to deal with the case, just add a listener in code：

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

**Get SpringView**
--------
use Gradle:
```
dependencies {
   compile 'com.liaoinstan.springview:library:1.2.6'
}
```
or Maven：
```
<dependency>
  <groupId>com.liaoinstan.springview</groupId>
  <artifactId>library</artifactId>
  <version>1.2.6</version>
  <type>pom</type>
</dependency>
```


**Update log**
--------
####**v1.2.6**
 - padding bug fix

####**v1.2.5**
 - add **setEnable(boolean)** function

####**v1.2.4**

 - add **callFresh()** method, used to call the refresh operation manual
 - fixes the lateral sliding conflict

####**v1.2.2**
 - repair the callback refresh many times

####**v1.2.1**
 - repair the click event occasional failure problem


**Feedback**
--------
If there are any questions or Suggestions, please feedback to my email: liaoinstan@outlook.com
Or in my blog

If it works to you, please give me a star for my hardwork ,thank you

**More**
--------
More, please visit：[my CSDN blog](http://blog.csdn.net/liaoinstan)

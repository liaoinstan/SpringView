更多请异步至：[我的CSDN博客](http://blog.csdn.net/liaoinstan)  　　　　apk演示：[点击下载](https://github.com/liaoinstan/SpringView/blob/master/apk/demo-debug.apk?raw=true)

SpringView
=====
**SpringView** 是一个提供了上下拖拽的功能组件，能够进行高度自定义，实现各种下拉\上拉动画效果，demo里实现了：仿阿里旅行、仿美团，仿QQ下拉刷红包，仿acfun等，完全兼容源生控件如ListView、RecyclerView、ScrollView、WebView等，使用简单，轻易定制自己风格的拖拽页面

**SpringView** 单独将头部/尾部独立出来，几乎可是实现任何你想要的效果，只需要继承BaseHeader/BaseFooter实现对应接口就可以。

**SpringView** 能动态地替换头部/尾部，只需要设置不同的头尾即可：springView.setHeader(MyHeader());

**SpringView** 使用简单，对于简单的需求甚至不用写任何代码，只需要在布局中为SpringView设置header="@layout/..."属性即可。

**SpringView** 支持多点触控，可以两只手连续拖拽，你可以定制一些有趣味的效果

**SpringView** 提供了2种拖拽方式（重叠和跟随），可以动态地切换

**SpringView** 为不想去自定义头/尾的懒人提供了7种默认的实现（模仿了阿里，腾讯，美团等多种风格）如下，还会继续增加 

![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212323072_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212372609_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212462800_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212485237_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212517801_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212658972_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212769245_s.gif)

如何使用 SpringView
--------

在布局文件中添加SpringView，并把你想要拖拽的控件放在SpringView中，给SpringView添加app:header="@layout/..."属性，设置一个自己编写的头部的布局即可（footer同理）：

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
当然，你也可以不再布局中设置，使用代码动态添加：

```
	//DefaultHeader/Footer是SpringView已经实现的默认头/尾之一
	//更多还有MeituanHeader、AliHeader、RotationHeader等如上图7种
	springView.setHeader(new DefaultHeader(this));
    springView.setFooter(new DefaultFooter(this));
```

添加监听
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

####**获取SpringView**
添加Gradle依赖:
```
dependencies {
   compile 'com.liaoinstan.springview:library:1.1.0'
}
```

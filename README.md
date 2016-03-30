更多请异步至：[我的CSDN博客](http://blog.csdn.net/liaoinstan)  　　　　apk演示：[点击下载](https://github.com/liaoinstan/SpringView/blob/master/apk/demo-debug.apk?raw=true)

> 关于下拉刷新/上拉加载更多的解决方案网上已经有很多了，浏览了目前主流的下拉控件比如PullToRefresh库等，**第一：大多数实现库都难以进行动画和样式的自定义。第二：不能很好的兼容多种滚动控件**，它们都对listView、RecyclerView等进行了不同程度的重新实现，你在项目中就得使用库提供的PullToRefreshListView、PullToRefreshRecyclerView等来代替源生的listView、RecyclerView等，这样的方式其实并不好，随着android版本的不断升级源生的listView、RecyclerView也不断加入新特性和对之前bug的修改，这使得三方实现的控件很难跟上google的节奏，当源生listView已经更新多个版本之后，三方的ListView可能还在使用低版本的实现方式。

##**SpringView有哪些优点？**

 1. 能对header/footer（头部/尾部）的样式和动画进行高度自定义，单独将header/footer独立出来，几乎可是实现任何你想要的效果，只需要继承BaseHeader/BaseFooter实现对应接口就可以。

 2. 能动态地替换header/footer，只需要设置不同的头尾即可：springView.setHeader(MyHeader());

 3. 在不重写源生控件的情况下，完美支持系统源生的listView、RecyclerView、ScrollView、WebView等，你依然使用google提供的官方控件，SpringView完全不做干涩。

 4. 使用简单，对于简单的需求甚至不用写任何代码，只需要在布局中为SpringView设置header="@layout/..."属性即可。

 4. SpringView是非常轻量级的实现，控件本身只有一个类

 6. SpringView支持多点触控，可以两只手连续拖拽，你可以定制一些趣味的动画（例如demo5的仿acfun效果）

 6. SpringView提供了2种拖拽方式（重叠和跟随），可以动态地切换

 5. SpringView为不想去自定义头/尾的懒人提供了7种默认的实现（模仿了阿里，腾讯，美团等多种风格）如下，还会继续增加 

![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212323072_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212372609_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212462800_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212485237_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212517801_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212658972_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/gif/1459212769245_s.gif)

###**如何使用 SpringView？**

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

####**刷新和加载更多的事件处理**
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

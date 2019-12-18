More, please visit：[My CSDN](http://blog.csdn.net/liaoinstan/article/details/51023907)  　　　　apk demo：[download](https://github.com/liaoinstan/SpringView/blob/master/apk/DemoSpring-1.7.0-release.apk?raw=true)  　　　　中文文档：[中文文档](https://github.com/liaoinstan/SpringView/blob/master/README_CN.md)

SpringView
=====
**SpringView**  is one provides the function of the upper and lower drag and drop components, can be highly customized, implement all kinds of drop-down \ pull painting effect, implementation in the demo: imitation ali travel, imitation Meituan, imitation QQ drop-down brush red envelopes, imitation acfun etc., is fully compatible with the source control such as ListView, RecyclerView, ScrollView, WebView, etc., using a simple, easily customize their style of drag and drop page
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/springview.png)

**SpringView** Individual independence will head/foot,  almost achieve the effect of whatever you want, just need to inherit BaseHeader (or Footer) to implement the interface

**SpringView** Can dynamically replace the head/tail, you just need to set up different Footer：springView.setHeader(MyHeader());

**SpringView** Multi-touch support, can two hands drag and drop in a row, you can customize some interesting results

**SpringView** Provides two methods for drag and drop (overlap and follow), can be dynamically switching

**SpringView** For lazy people don't want to go to the custom head/tail provides 7 kinds of the default implementation (mimics the ali, tencent, Meituan etc. Various style) as follows, will continue to increase

**SpringView** support AppBarLayout
 
 
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212323072_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212372609_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212462800_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212485237_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212517801_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212658972_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/1459212769245_s.gif) 

**📌new：** 

![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/weixin_header_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/weixin_header_v2_s.gif)
![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/auto_footer_s.gif) ![这里写图片描述](https://github.com/liaoinstan/SpringView/blob/master/screenshot/du_header_s.gif)

**How to use SpringView**
--------

Add Spring View in the layout file, note that Spring View and ScrollView have the same limitation: only one child element：

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
Of course, you can also add the header/footer in java code：

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

   //SpringView core library (only include DefaultHeader/Footer)
   implementation 'com.liaoinstan.springview:library:1.7.0'

   //other Headers/Footers, choose one or more that you like
   implementation 'com.liaoinstan.springview:AcfunHeader:1.7.0'         //AcFun style （header and footer）
   implementation 'com.liaoinstan.springview:AliHeader:1.7.0'           //Alitrip style （header and footer）
   implementation 'com.liaoinstan.springview:MeituanHeader:1.7.0'       //Meituan style （header and footer）
   implementation 'com.liaoinstan.springview:RotationHeader:1.7.0'      //Mechanical gear style （header and footer）
   implementation 'com.liaoinstan.springview:WeixinHeader:1.7.0'        //WeChat Mini Program header（only header）
   implementation 'com.liaoinstan.springview:DuHeader:1.7.0'		//Du app header（only header）
}
```
or Maven：
```
<dependency>
  <groupId>com.liaoinstan.springview</groupId>
  <artifactId>library</artifactId>
  <version>1.7.0</version>
  <type>pom</type>
</dependency>
```


**Update log**
--------
### **v1.7.0**
- Add new a header (DuHeader), and a auto-scrolling Footer (AutoFooter).
- Add a new type SCROLL. Based on this mode, effects such as scrolling to the bottom and automatic loading can be achieved.
- Header / Footer now has setType () method, you can set different Type for Header and Footer respectively.
- Fix issus.
 
### **v1.6.0**
- Add a new header (WeixinHeaderV2) for new version of WeChat (WeChat 7).
- The movement parameter of SpringView (MovePara) no longer affects both Header and Footer. BaseHeader/Footer now has a new getMovePara() interface that allows you to set different movement parameters for Header and Footer to match different drag-and-drop feel.(if you don't implement the interface, SpringView is still compatible with the previous rules).
- Update to Android X.
- Fixed several drag and drop stickiness issues, and callFresh method callback issues.
 
### **v1.5.1**
 - Fix bug when nested layout, fix bug callFresh() can't spring back when set SpringView Give.NONE.

### **v1.5.0**
 - headers /footers split off from SpringView library(except DefaultHeader/Footer)，simplified core library，now core library's size only **26KB**,You can import headers/footers separately

### **v1.4.0**
 - New function setMovePara(),a new header for weixin:WeixinHeader,a new type DRAG like SwipeRefreshLayout:spring.setType(Type.DRAG)

### **v1.3.3**
 - Optimize several experiential issues,Added onFinishFreshAndLoadDelay(int delay), callFreshDelay(int delay) method,Optimizing performance with BottomSheetBehavior,Optimize the performance used in BottomSheetDialog

### **v1.3.2**
 - Fix the bug of the linkage damping of AppBarLayout in the case of null data,
new setEnableFooter(Boolean), setEnableHeader(Boolean) disable or enable header/footer.
 
### **v1.3.0**
 - Support AppBarLayout,fix sliding conflict

### **v1.2.7**
 - Bug fix

### **v1.2.6**
 - Padding bug fix

### **v1.2.5**
 - Add **setEnable(boolean)** function

### **v1.2.4**

 - Add **callFresh()** method, used to call the refresh operation manual
 - Fixes the lateral sliding conflict

### **v1.2.2**
 - Repair the callback refresh many times

### **v1.2.1**
 - Repair the click event occasional failure problem


**Feedback**
--------
If there are any questions or Suggestions, please feedback to my email: liaoinstan@outlook.com;
Or in my blog

If it works to you, please give me a star for my hardwork ,thank you

**More**
--------
More, please visit：[my CSDN blog](http://blog.csdn.net/liaoinstan)

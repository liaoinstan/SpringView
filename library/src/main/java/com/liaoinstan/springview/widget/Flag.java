package com.liaoinstan.springview.widget;

class Flag {
    //Flag:调用header/footer回调: startAnim finishAnim resetAnim
    int callHeaderAnimFlag = 0; //startAnim:0->1 finishAnim: 1->2  resetAnim: 2->0
    int callFooterAnimFlag = 0; //startAnim:0->1 finishAnim: 1->2  resetAnim: 2->0

    //Flag:全部回弹，刷新动画回弹，收场动画回弹 调用控制
    boolean hasCallFull = false;
    boolean hasCallRefresh = false;
    boolean hasCallEnding = false;

    //Flag:拖拽回调，用于在动画结束时判断动画类别以调用不同回调接口
    //0：没有拖拽
    //1：拖拽超过header阈值header.onResetAnim
    //2：拖拽超过footer阈值footer.onResetAnim
    //3：拖拽header但未超过阈值header.onFinishDrag
    //4：拖拽footer但未超过阈值header.onFinishDrag
    int callFreshOrLoad = 0;
    //Flag:记录回弹动画类别，用于在动画结束时判断动画类别以调用不同回调接口
    //回弹动画类别 0:结束动画1:回弹动画2:退场动画
    int scrollAnimType;

    //Flag:控制preDrag每个手势链只调用一次
    boolean hasCallPreDrag = false;
}

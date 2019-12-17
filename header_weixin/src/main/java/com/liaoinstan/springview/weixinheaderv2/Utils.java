package com.liaoinstan.springview.weixinheaderv2;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import androidx.core.content.PermissionChecker;

import com.liaoinstan.springview.weixinheader.Program;

import java.util.List;

class Utils {
    static int getMoreItemIndex(List<Program> programList) {
        for (int i = 0; i < programList.size(); i++) {
            Program program = programList.get(i);
            if (program.getType() == RecycleAdapterWeixinHeaderV2.TYPE_MORE) {
                return i;
            }
        }
        return -1;
    }

    //判断当前位置是否是"最近使用"小程序
    static boolean isRecentProgram(List<Program> programList, int position) {
        int indexMore = getMoreItemIndex(programList);
        return position < indexMore;
    }

    //判断是否正要拖拽到'最近使用'类别中
    static boolean isDragToRecentProgram(List<Program> programList, int position) {
        int indexMore = getMoreItemIndex(programList);
        return position <= indexMore;
    }

    static boolean isMineProgram(List<Program> programList, int position) {
        int indexMore = getMoreItemIndex(programList);
        return position > indexMore;
    }

    static boolean isLastItem(List<Program> programList, int position) {
        return position == programList.size() - 1;
    }

    static boolean hasMineProgram(List<Program> programList) {
        return programList.get(programList.size() - 1).getType() != RecycleAdapterWeixinHeaderV2.TYPE_HEADER;
    }

    static int getMineTitlePosition(List<Program> programList) {
        for (int i = programList.size() - 1; i >= 0; i--) {
            Program program = programList.get(i);
            if (program.getType() == RecycleAdapterWeixinHeaderV2.TYPE_HEADER) {
                return i;
            }
        }
        return -1;
    }

    static int getRecentProgramCount(List<Program> programList) {
        int indexMore = getMoreItemIndex(programList);
        return indexMore - 1;
    }

    static int getMineProgramCount(List<Program> programList) {
        int indexMore = getMoreItemIndex(programList);
        return programList.size() - indexMore - 2;
    }

    /**
     * 检查如果已经申请了震动权限，就震动一下，震动时长：time
     */
    static void vibrate(Context context, int time) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.VIBRATE) == PermissionChecker.PERMISSION_GRANTED) {
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            if (vib != null) vib.vibrate(time);
        }
    }
}

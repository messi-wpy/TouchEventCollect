package com.example.messi_lp.touchdemo;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.concurrent.RecursiveTask;

import io.reactivex.subjects.PublishSubject;

/**
 * adb shell  getevent -l:
 * /dev/input/event4: EV_ABS       ABS_MT_TRACKING_ID   00000011
 * /dev/input/event4: EV_KEY       BTN_TOUCH            DOWN
 * /dev/input/event4: EV_KEY       BTN_TOOL_FINGER      DOWN
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    0000026e
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004d5
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MAJOR   00000004
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000003
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000002
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    00000269
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004d6
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000001
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    00000262
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004d8
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    00000258
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004dc
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MAJOR   00000005
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000002
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    00000248
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004e2
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MAJOR   00000003
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000003
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    00000234
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004e9
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MAJOR   00000004
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    0000021e
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004f0
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MAJOR   00000005
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000005
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    00000205
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    000004f8
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000004
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    000001e9
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    00000501
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MAJOR   00000004
 * /dev/input/event4: EV_SYN       SYN_REPORT           00000000
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_X    000001cc
 * /dev/input/event4: EV_ABS       ABS_MT_POSITION_Y    0000050a
 * /dev/input/event4: EV_ABS       ABS_MT_TOUCH_MINOR   00000002
 * /dev/input/event4: EV_SYN       SYN_REPORT           00
 *
 */
public class CommonUtils {

    public static final int ACTION_DOWN_NUM=-9999;
    public static final int ACTION_UP_NUM=9999;
    public static final String ACTION_DOWN="action_down";
    public static final String ACTION_UP="action_up";
    /**
     *
     * @param str:一行命令结果
     * @return -1--->不是
     *          <0是y坐标
     *          >0是x坐标
     * /dev/input/event4: EV_ABS       ABS_MT_TRACKING_ID   00000011
     */
    public static int convertToSting(String str){
        boolean xFlag=false;
        boolean yFlag= false;
        String []strings=str.split("\\s+");
        for (String s:strings) {
            if (s.equals("ABS_MT_POSITION_X")) {
                xFlag = true;
                break;
            }
            if (s.equals("ABS_MT_POSITION_Y")) {
                yFlag = true;
                break;
            }
            if (s.equals("DOWN")){
                return ACTION_DOWN_NUM;
            }
            if (s.equals("UP"))
                return ACTION_UP_NUM;
        }
        if (xFlag){
            int x16=Integer.parseInt(strings[strings.length-1],16);
            return x16;
        }else if(yFlag){
            int x16=Integer.parseInt(strings[strings.length-1],16);
            return -x16;

        } else
            return 0;

    }

    //返回距离的平方
    public static int distance2(int x1,int y1,int x2,int y2){
        return (int )(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));

    }

    public static String timeName(){
        Date now=new Date();
        return String.format("%ty%tm%td%tH%tM",now,now,now,now,now)+"position";


    }
}

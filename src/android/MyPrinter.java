//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mrboss.jsposprinter;

import android.content.Context;
import com.apos.aposio.aposio;
import com.apos.aposprinter.AposException;
import com.apos.aposprinter.Builder;

public class MyPrint {
    public static final int DEVTYPE_RS232 = 0;
    public static final int DEVTYPE_USB = 1;
    public static final int DEVTYPE_TCP = 2;
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int PARAM_UNSPECIFIED = -1;
    public static final int PARAM_DEFAULT = -2;
    public static final int ST_NO_RESPONSE = 1;
    public static final int ST_PRINT_SUCCESS = 2;
    public static final int ST_DRAWER_KICK = 4;
    public static final int ST_BATTERY_OFFLINE = 4;
    public static final int ST_OFF_LINE = 8;
    public static final int ST_COVER_OPEN = 32;
    public static final int ST_PAPER_FEED = 64;
    public static final int ST_WAIT_ON_LINE = 256;
    public static final int ST_PANEL_SWITCH = 512;
    public static final int ST_MECHANICAL_ERR = 1024;
    public static final int ST_AUTOCUTTER_ERR = 2048;
    public static final int ST_UNRECOVER_ERR = 8192;
    public static final int ST_AUTORECOVER_ERR = 16384;
    public static final int ST_RECEIPT_NEAR_END = 131072;
    public static final int ST_RECEIPT_END = 524288;
    public static final int ST_BUZZER = 16777216;
    private static aposio w = new aposio();
    private Context x = null;
    private boolean y = false;

    public MyPrint(Context var1) {
        this.x = var1;
    }

    public MyPrint() {
    }

    public void openPrinter(int var1, String var2, int var3, int var4) throws AposException {
        if((var1 != 1 || var2 == "RTPSO") && (var1 != 0 || Integer.parseInt(var2) == 1 || Integer.parseInt(var2) == 2 || Integer.parseInt(var2) == 4 || Integer.parseInt(var2) == 5 || var4 == 115200 || var4 == '阀' || var4 == 19200 || var4 == 9600 || var4 == 4800) && (var1 != 2 || var2.length() <= 15 && var2.length() >= 7)) {
            if(!w.Initaposio(var1, this.x, var2, var3, var4)) {
                this.y = false;
                throw new AposException(2);
            } else {
                this.y = true;
            }
        } else {
            throw new AposException(1);
        }
    }

    public void closePrinter() throws AposException {
        if(this.y) {
            w.Closeio();
        } else {
            throw new AposException(7);
        }
    }

    public void sendData(Builder var1, int var2, int[] var3) throws AposException {
        if(this.y) {
            byte[] var4 = new byte[Builder.sbuffer.length()];
            Builder.sbuffer.getBytes(0, Builder.sbuffer.length(), var4, 0);
            if(w.Writeio(var4, this.x)) {
                var3[0] = 2;
            } else {
                var3[0] = 1;
                throw new AposException(255);
            }
        } else {
            throw new AposException(7);
        }
    }

    public void sendData(LitterBuilder var1, int var2, int[] var3) throws AposException {
        if(this.y) {
            byte[] var4 = new byte[LitterBuilder.sbuffer.length()];
            LitterBuilder.sbuffer.getBytes(0, LitterBuilder.sbuffer.length(), var4, 0);
            if(w.Writeio(var4, this.x)) {
                var3[0] = 2;
            } else {
                var3[0] = 1;
                throw new AposException(255);
            }
        } else {
            throw new AposException(7);
        }
    }

    public void getData(Builder var1, byte[] var2, int[] var3) throws AposException {
        if(this.y) {
            if(w.Readio(var2, this.x)) {
                var3[0] = 2;
            } else {
                var3[0] = 1;
                throw new AposException(255);
            }
        } else {
            throw new AposException(7);
        }
    }
}

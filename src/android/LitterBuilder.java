//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mrboss.jsposprinter;

import com.apos.aposprinter.AposException;

public class LitterBuilder {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static String sbuffer = "";
    public static final int PARAM_UNSPECIFIED = -1;
    public static final int PARAM_DEFAULT = -2;
    public static final int MODEL_ANK = 0;
    public static final int MODEL_JAPANESE = 1;
    public static final int MODEL_CHINESE = 2;
    public static final int MODEL_TAIWAN = 3;
    public static final int MODEL_KOREAN = 4;
    public static final int MODEL_THAI = 5;
    public static final int MODEL_SOUTHASIA = 6;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int LANG_EN = 0;
    public static final int LANG_JA = 1;
    public static final int LANG_ZH_CN = 2;
    public static final int LANG_ZH_TW = 3;
    public static final int LANG_KO = 4;
    public static final int LANG_TH = 5;
    public static final int LANG_VI = 6;
    public static final int FONT_A = 0;
    public static final int FONT_B = 1;
    public static final int COLOR_NONE = 0;
    public static final int COLOR_1 = 1;
    public static final int COLOR_2 = 2;
    public static final int COLOR_3 = 3;
    public static final int COLOR_4 = 4;
    public static final int MODE_MONO = 0;
    public static final int MODE_GRAY16 = 1;
    public static final int HALFTONE_DITHER = 0;
    public static final int HALFTONE_ERROR_DIFFUSION = 1;
    public static final int HALFTONE_THRESHOLD = 2;
    public static final int BARCODE_UPC_A = 0;
    public static final int BARCODE_UPC_E = 1;
    public static final int BARCODE_EAN13 = 2;
    public static final int BARCODE_JAN13 = 3;
    public static final int BARCODE_EAN8 = 4;
    public static final int BARCODE_JAN8 = 5;
    public static final int BARCODE_CODE39 = 6;
    public static final int BARCODE_ITF = 7;
    public static final int BARCODE_CODABAR = 8;
    public static final int BARCODE_CODE93 = 9;
    public static final int BARCODE_CODE128 = 10;
    public static final int HRI_NONE = 0;
    public static final int HRI_ABOVE = 1;
    public static final int HRI_BELOW = 2;
    public static final int HRI_BOTH = 3;
    public static final int SYMBOL_QRCODE_MODEL_2 = 3;
    public static final int LEVEL_L = 9;
    public static final int LEVEL_M = 10;
    public static final int LEVEL_Q = 11;
    public static final int LEVEL_H = 12;
    public static final int LEVEL_DEFAULT = 13;
    public static final int LINE_THIN = 0;
    public static final int LINE_MEDIUM = 1;
    public static final int LINE_THICK = 2;
    public static final int LINE_THIN_DOUBLE = 3;
    public static final int LINE_MEDIUM_DOUBLE = 4;
    public static final int LINE_THICK_DOUBLE = 5;
    public static final int DIRECTION_LEFT_TO_RIGHT = 0;
    public static final int DIRECTION_BOTTOM_TO_TOP = 1;
    public static final int DIRECTION_RIGHT_TO_LEFT = 2;
    public static final int DIRECTION_TOP_TO_BOTTOM = 3;
    public static final int CUT_NO_FEED = 0;
    public static final int CUT_FEED = 1;
    public static final int DRAWER_1 = 0;
    public static final int DRAWER_2 = 1;
    public static final int PULSE_100 = 0;
    public static final int PULSE_200 = 1;
    public static final int PULSE_300 = 2;
    public static final int PULSE_400 = 3;
    public static final int PULSE_500 = 4;
    public static final int PATTERN_A = 1;
    public static final int PATTERN_B = 2;
    public static final int PATTERN_C = 3;
    public static final int PATTERN_D = 4;
    public static final int PATTERN_E = 5;
    public static final int PATTERN_ERROR = 6;
    public static final int PATTERN_PAPER_END = 7;
    public static final int PATTERN_1 = 8;
    public static final int PATTERN_2 = 9;
    public static final int PATTERN_3 = 10;
    public static final int PATTERN_4 = 11;
    public static final int PATTERN_5 = 12;
    public static final int PATTERN_6 = 13;
    public static final int PATTERN_7 = 14;
    public static final int PATTERN_8 = 15;
    public static final int PATTERN_9 = 16;
    public static final int PATTERN_10 = 17;
    public static final int FEED_PEELING = 0;
    public static final int FEED_CUTTING = 1;
    public static final int FEED_CURRENT_TOF = 2;
    public static final int FEED_NEXT_TOF = 3;
    public static final int LAYOUT_RECEIPT = 0;
    public static final int LAYOUT_LABEL = 1;
    public static final int LAYOUT_LABEL_BM = 2;
    public static final int LAYOUT_RECEIPT_BM = 3;
    private boolean v = false;

    public LitterBuilder(String var1, int var2) throws AposException {
        if(var1 == "RTPSO") {
            this.v = true;
        } else {
            this.v = false;
            throw new AposException(1);
        }
    }

    public void clearCommandBuffer() throws AposException {
        if(this.v) {
            if(sbuffer.length() != 0) {
                sbuffer = "";
            }
        } else {
            throw new AposException(7);
        }
    }

    public void addText(String var1) throws AposException {
        if(this.v) {
            char[] var2 = new char[]{'\n'};
            char[] var3 = new char[]{'\t'};
            char[] var4 = new char[]{'\r'};
            var1.replace("\\n", var2.toString());
            var1.replace("\\t", var3.toString());
            var1.replace("\\\\", var4.toString());
            sbuffer = sbuffer + var1;
        } else {
            throw new AposException(7);
        }
    }

    public static byte[] toByteArray(int var0, int var1) {
        byte[] var2 = new byte[var1];

        for(int var3 = 0; var3 < 4 && var3 < var1; ++var3) {
            var2[var3] = (byte)(var0 >> var3 * 8);
        }

        return var2;
    }

    public void addCommand(byte[] var1) throws AposException {
        if(!this.v) {
            throw new AposException(7);
        } else {
            char[] var2 = new char[var1.length];

            int var3;
            for(var3 = 0; var3 < var1.length; ++var3) {
                var2[var3] = (char)var1[var3];
            }

            for(var3 = 0; var3 < var2.length; ++var3) {
                sbuffer = sbuffer + var2[var3];
            }

        }
    }
}

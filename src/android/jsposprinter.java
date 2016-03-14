/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package com.mrboss.jsposprinter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.*;
import org.apache.cordova.engine.*;

import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.app.AlertDialog;

import com.apos.aposprinter.*;

import java.io.*;

public class jsposprinter extends CordovaPlugin {

    private static final String LOG_TAG = "jsposprinterPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("UsbPrint".equals(action)) {
                String printtext = args.getString(0);
                UsbPrint(printtext);
                callbackContext.success(200);
                return true;
            } else if ("TcpPrint".equals(action)) {
                String printtext = args.getString(0);
                String ip = args.getString(1);
                int port = args.getInt(2);
                TcpPrint(printtext, ip, port, callbackContext);
                return true;
            } else if ("TestUsbPrint".equals(action)) {
                UsbPrint("clear::::addText;;;;AppPrintPOS\n::::");
                callbackContext.success(200);
                return true;
            } else if ("TestTcpPrint".equals(action)) {
                String ip = args.getString(0);
                int port = args.getInt(1);
                TcpPrint("PrintText", ip, port, callbackContext);
                return true;
            }
        } catch (AposException e) {
            // TODO Auto-generated catch block
            int errstatus = e.getErrorStatus();
            callbackContext.error(errstatus);
            return false;
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.error("No This Method");
        return false;
    }

    public void UsbPrint(String printtext) throws AposException {
        Print printer = new Print(cordova.getActivity());
        printer.openPrinter(Print.DEVTYPE_USB, "RTPSO", 0, 0);
        LitterBuilder build = new LitterBuilder("RTPSO", LitterBuilder.MODEL_CHINESE);
        int []status = {1};
        String[] printArr = printtext.split("::::");

        for (int i = 0; i < printArr.length; i++) {
            String[] oneprint = printArr[i].split(";;;;");
            if (oneprint.length > 0) {
                ExplainComment(build, oneprint);
            }
        }
        printer.sendData(build, 1000, status);
        build.clearCommandBuffer();
    }

    private void ExplainComment(LitterBuilder build, String[] oneprint) throws AposException {
        String comment = oneprint[0];
        if (comment.equals("addText")) {
            build.addText(oneprint[1]);
        } else if (comment.equals("addCommand")) {
            try {
                byte[] GB_bytes = oneprint[2].getBytes(oneprint[1]);
                build.addCommand(GB_bytes);
            } catch (UnsupportedEncodingException e) {
                build.addText("UnsupportedEncodingException:" + oneprint[1]);
            }
        } else if (comment.equals("clearCommandBuffer")) {
            build.clearCommandBuffer();
        } else if (comment.equals("clear")) {
            byte clear[] = {0x1b, 0x40};
            build.addCommand(clear);
        } else {
            // Alert("NoThisComment:" + comment);
        }
    }

    private void Alert(String msg) {
        Dialog alertDialog = new AlertDialog.Builder(this.cordova.getActivity()).
        setTitle("对话框的标题").
        setMessage(msg).
        setCancelable(false).
        setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).
        create();
        alertDialog.show();
    }

    public void TcpPrint(String printstr, String ip, int port, CallbackContext callbackContext) {
        TcpPrinterTask tpt = new TcpPrinterTask();
        tpt.ip = ip;
        tpt.port = port;
        tpt.printstr = printstr;
        tpt.callbackContext = callbackContext;
        tpt.execute();
    }

    private class TcpPrinterTask extends AsyncTask {
        public CallbackContext callbackContext;
        public String encode = "GBK";
        public String printstr = "";
        public String ip = "";
        public int port;
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected Object doInBackground(Object[] params) {
            String result = "OK";

            try {
                LitterBuilder build = new LitterBuilder("RTPSO", LitterBuilder.MODEL_CHINESE);
                String[] printArr = printstr.split("::::");
                for (int i = 0; i < printArr.length; i++) {
                    String[] oneprint = printArr[i].split(";;;;");
                    if (oneprint.length > 0) {
                        ExplainComment(build, oneprint);
                    }
                }
                byte[] t_printstr = build.sbuffer.getBytes("UTF-8");
                Socket socket = new Socket();
                SocketAddress socAddress = new InetSocketAddress(ip, port);
                socket.connect(socAddress, 3000);
                OutputStream ostream = socket.getOutputStream();
                ostream.write(t_printstr);
                socket.shutdownOutput();
                socket.close();
            }
            // catch (IOException e) {
            //     result = e.getMessage();
            // } 
            catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            // Alert(o.toString() + "");
            if (o.toString() == "OK") {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, o.toString()));
                callbackContext.success();
            } else {
                callbackContext.error(o.toString());
            }
        }
    }
}
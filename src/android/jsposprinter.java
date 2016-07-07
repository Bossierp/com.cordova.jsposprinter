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
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.app.AlertDialog;

import com.apos.aposprinter.*;

import java.io.*;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;

import java.util.Set;

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
                String encode = args.getString(3);
                int timeout = args.getInt(4);
                TcpPrint(printtext, ip, port, timeout, encode, callbackContext);
                return true;
            } else if ("BlueToothPrint".equals(action)) {
                String printtext = args.getString(0);
                String startname = args.getString(1);
                String encode = args.getString(2);
                BlueToothPrint(printtext, startname, encode, callbackContext);
                return true;
            } else if ("TestUsbPrint".equals(action)) {
                UsbPrint("clear::::addText;;;;AppPrintPOS\n::::");
                callbackContext.success(200);
                return true;
            } else if ("TestTcpPrint".equals(action)) {
                String ip = args.getString(0);
                int port = args.getInt(1);
                TcpPrint("PrintText", ip, port, 3000, "GBK", callbackContext);
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
        MyPrint printer = new MyPrint(cordova.getActivity());
        printer.openPrinter(MyPrint.DEVTYPE_USB, "RTPSO", 0, 0);
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

    private void ExplainComment(Builder build, String[] oneprint) throws AposException {
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
        } else if (comment.equals("addTextAlign")) {
            build.addTextAlign(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("clearCommandBuffer")) {
            build.clearCommandBuffer();
        } else if (comment.equals("addCut")) {
            if (oneprint.length == 1) {
                build.addCut(Builder.CUT_FEED);
            } else {
                build.addCut(Integer.parseInt(oneprint[1]));
            }
        } else if (comment.equals("addTextLineSpace")) {
            build.addTextLineSpace(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addTextRotate")) {
            build.addTextRotate(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addTextFont")) {
            build.addTextFont(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addTextSmooth")) {
            build.addTextSmooth(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addTextDouble")) {
            if (oneprint.length == 1) {
                build.addTextDouble(Builder.FALSE, Builder.FALSE);
            } else {
                build.addTextDouble(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]));
            }
        } else if (comment.equals("addTextSize")) {
            build.addTextSize(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]));
        } else if (comment.equals("addTextStyle")) {
            build.addTextStyle(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]), Integer.parseInt(oneprint[3]), Integer.parseInt(oneprint[4]));
        } else if (comment.equals("addTextPosition")) {
            build.addTextPosition(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addFeedUnit")) {
            build.addFeedUnit(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addFeedLine")) {
            build.addFeedLine(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addBarcode")) {
            if (oneprint.length == 2) {
                build.addBarcode(oneprint[1], Builder.BARCODE_EAN13,
                                 Builder.HRI_BELOW, Builder.PARAM_UNSPECIFIED,
                                 2, 60);
            } else {
                build.addBarcode(oneprint[1], Integer.parseInt(oneprint[2]), Integer.parseInt(oneprint[3]),
                                 Integer.parseInt(oneprint[4]), Integer.parseInt(oneprint[5]), Integer.parseInt(oneprint[6]));
            }
        } else if (comment.equals("addSymbol")) {
            build.addSymbol(oneprint[1], Builder.SYMBOL_QRCODE_MODEL_2, Builder.LEVEL_L, 120, 120, 0);
        } else if (comment.equals("addPageBegin")) {
            build.addPageBegin();
        } else if (comment.equals("addPageEnd")) {
            build.addPageEnd();
        } else if (comment.equals("addPageArea")) {
            build.addPageArea(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]), Integer.parseInt(oneprint[3]), Integer.parseInt(oneprint[4]));
        } else if (comment.equals("addPageDirection")) {
            build.addPageDirection(Integer.parseInt(oneprint[1]));
        } else if (comment.equals("addPulse")) {
            build.addPulse(Integer.parseInt(oneprint[1]), Integer.parseInt(oneprint[2]));
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

    public void TcpPrint(String printstr, String ip, int port, int timeout, String encode, CallbackContext callbackContext) {
        TcpPrinterTask tpt = new TcpPrinterTask();
        tpt.ip = ip;
        tpt.port = port;
        tpt.printstr = printstr;
        tpt.timeout = timeout;
        tpt.encode = encode;
        tpt.callbackContext = callbackContext;
        tpt.execute();
    }

    private class TcpPrinterTask extends AsyncTask {
        public CallbackContext callbackContext;
        public String encode = "GBK";
        public String printstr = "";
        public String ip = "";
        public int timeout;
        public int port;
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected Object doInBackground(Object[] params) {
            String result = "OK";

            try {
                // LitterBuilder build = new LitterBuilder("RTPSO", LitterBuilder.MODEL_CHINESE);
                // String[] printArr = printstr.split("::::");
                // for (int i = 0; i < printArr.length; i++) {
                //     String[] oneprint = printArr[i].split(";;;;");
                //     if (oneprint.length > 0) {
                //         ExplainComment(build, oneprint);
                //     }
                // }
                // byte[] t_printstr = build.sbuffer.getBytes(encode);
                byte[] t_printstr = printstr.getBytes(encode);
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

    public void BlueToothPrint(String printstr, String startname, String encode, CallbackContext callbackContext) {
        BlueToothPrinterTask btpt = new BlueToothPrinterTask();
        btpt.startname = startname;
        btpt.printstr = printstr;
        btpt.encode = encode;
        btpt.callbackContext = callbackContext;
        btpt.mainactive = this.cordova.getActivity();
        btpt.execute();
    }

    private class BlueToothPrinterTask extends AsyncTask {
        private GpService mGpService = null;
        public Activity mainactive = null;
        public CallbackContext callbackContext;
        public String encode = "GBK";
        public String printstr = "";
        public String startname = "";
        public int printerId = 0;
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected Object doInBackground(Object[] params) {
            String result = "OK";
            try {
                connection();
                // byte[] t_printstr = printstr.getBytes(encode);
                // Socket socket = new Socket();
                // SocketAddress socAddress = new InetSocketAddress(ip, port);
                // socket.connect(socAddress, 3000);
                // OutputStream ostream = socket.getOutputStream();
                // ostream.write(t_printstr);
                // socket.shutdownOutput();
                // socket.close();
                mGpService.openPort(printerId, PortParameters.BLUETOOTH, getBLUETOOTHDeviceAddress(startname), 0);
                int rel = mGpService.sendEscCommand(printerId, printstr);
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                mGpService.closePort(printerId);
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    result = GpCom.getErrorText(r);
                }
                else{
                    result = "OK";
                }
            } 
            catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                result = e1.getMessage();
            }
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

        protected String getBLUETOOTHDeviceAddress(String startname){
            // Get the local Bluetooth adapter
            BluetoothAdapter tempmBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // Get a set of currently paired devices
            Set<BluetoothDevice> pairedDevices = tempmBluetoothAdapter.getBondedDevices();
            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if(device.getName().startsWith(startname)){
                        return device.getAddress();
                    }
                }
            }
            return "";
        }

        private void connection() {
            PrinterServiceConnection conn = new PrinterServiceConnection();
            Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
            intent.setPackage(mainactive.getPackageName());
            mainactive.bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
        }

        class PrinterServiceConnection implements ServiceConnection {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mGpService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mGpService = GpService.Stub.asInterface(service);
            }
        };
    }
}
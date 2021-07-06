package com.rz.crashreport;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import static com.rz.crashreport.DevicePropertiesKt.serialNo;

/**
 * @author : create by yanggaosheng$
 * @time : 2020/7/9$
 * @des :  $当出现崩溃异常时，默认的处理（包括日志的存储，上传等操作）
 **/
class DefaultICrashCallbackImpl implements ICrashCallback {
    private final String TAG = "DefaultICrashImpl";
    private Context context;

    public DefaultICrashCallbackImpl(Context context) {
        this.context = context;
        // Send all pending crash log files.
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (File file : TombstoneManager.getAllTombstones()) {
                    sendThenDeleteCrashLog(file.getAbsolutePath(), null);
                }
            }
        }).start();
    }

    @Override
    public void onCrash(String logPath, String emergency) throws Exception {
        Log.d(TAG, "log path: " + (logPath != null ? logPath : "(null)") + ", emergency: " + (emergency != null ? emergency : "(null)"));

        debug(logPath, emergency);
        sendThenDeleteCrashLog(logPath, emergency);
    }

    private void sendThenDeleteCrashLog(String logPath, String emergency) {
        Map<String, String> map = null;
        try {
            //添加扩展字段（eg:业务相关的一些数据），todo 后面提供对应的接口
            TombstoneManager.appendSection(logPath, "devices_serialNo", serialNo());
//            TombstoneManager.appendSection(logPath, "login_param", serialNo());

            //添加业务接口
            if (XCrash.getExtraInfoMap().size()>0) {
                for (Map.Entry<String, String> item : XCrash.getExtraInfoMap().entrySet()) {
                    TombstoneManager.appendSection(logPath, item.getKey(), item.getValue());
                }
            }

            map = TombstoneParser.parse(logPath, emergency);
            if (!TombstoneParser.shouldPass(map)) {
                Log.e(TAG, "rzCrash_sendThenDeleteCrashLog: no dby crash did`nt send ");
                TombstoneManager.deleteTombstone(logPath);
                return;
            }
            JSONObject rootJsonObj = new JSONObject();
            rootJsonObj.put("type", "custom");
            rootJsonObj.put("info", new JSONObject(map).put("crash", true));
            rootJsonObj.put("info", new JSONObject(map).put("crash", true));

            boolean b = OkhttpUtils.getInstance().postSynJson(OkhttpUtils.UPLOAD_PATH, rootJsonObj.toString());
            if (b) {
                Log.e(TAG, "sendThenDeleteCrashLog: success");
                TombstoneManager.deleteTombstone(logPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 主要目的，方便在开发阶段导出日志；查看崩溃信息
     *
     * @param logPath
     * @param emergency
     */
    private void debug(String logPath, String emergency) {

        FileWriter writer = null;
        try {
            File debug = new File(XCrash.getLogDir() + "/debug.json");
            debug.createNewFile();
            writer = new FileWriter(debug, false);
            writer.write(new JSONObject(TombstoneParser.parse(logPath, emergency)).toString());


        } catch (Exception e) {
            Log.d(TAG, "debug failed" + e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {
                }
            }
        }
    }


}

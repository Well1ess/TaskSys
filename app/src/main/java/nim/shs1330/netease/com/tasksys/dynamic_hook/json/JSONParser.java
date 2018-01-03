package nim.shs1330.netease.com.tasksys.dynamic_hook.json;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nim.shs1330.netease.com.tasksys.helper.Client;
import nim.shs1330.netease.com.tasksys.helper.FileHelper;

/**
 * Created by shs1330 on 2017/10/30.
 * {
 * apkName:
 * packageName:
 * apkMainType:{Fragment,Activity}
 * apkMain:
 * version:1.0
 * }
 * PluginSetting.json
 */
public class JSONParser {
    private final static String PluginSetting = "PluginSetting.json";
    public final static String ApkName = "apkName";
    public final static String PackName = "packageName";
    public final static String ApkMainType = "apkMainType";
    public final static String ApkMain = "apkMain";
    public final static String Version = "version";

    private static final String TAG = "JSONParser";

    public static void parser() throws IOException {
        FileHelper.extractAssets(PluginSetting);
        String jsonContent = "";
        File file = Client.getContext().getFileStreamPath(PluginSetting);
        InputStream inputStream = new FileInputStream(file);
        byte[] buf = new byte[1024];
        while (inputStream.read(buf) > 0) {
            jsonContent += new String(buf, "utf-8");
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            Log.d(TAG, "parser: " + jsonObject.get("apkMainType"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static class PluginInfo{
        public String apkName;
        public String packName;
        public String apkMainType;
        public String apkMain;
        public String version;

        public static PluginInfo CreatePluginInfo(JSONObject jsonObject){
            try {
                PluginInfo pluginInfo = new PluginInfo(jsonObject.getString(ApkName),
                        jsonObject.getString(PackName),
                        jsonObject.getString(ApkMainType),
                        jsonObject.getString(ApkMain),
                        jsonObject.getString(Version));
                return pluginInfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public PluginInfo() {
        }

        public PluginInfo(String apkName, String packName, String apkMainType, String apkMain, String version) {
            this.apkName = apkName;
            this.packName = packName;
            this.apkMainType = apkMainType;
            this.apkMain = apkMain;
            this.version = version;
        }

        @Override
        public String toString() {
            return "PluginInfo{" +
                    "apkName='" + apkName + '\'' +
                    ", packName='" + packName + '\'' +
                    ", apkMainType='" + apkMainType + '\'' +
                    ", apkMain='" + apkMain + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }
}

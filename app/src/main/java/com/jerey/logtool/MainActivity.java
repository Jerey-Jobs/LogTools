package com.jerey.logtool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jerey.loglib.LogTools;

public class MainActivity extends AppCompatActivity {


    private static final String test_xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
            "<android.support.constraint.ConstraintLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"+
            "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n"+
            "    xmlns:tools=\"http://schemas.android.com/tools\"\n"+
            "    android:layout_width=\"match_parent\"\n"+
            "    android:layout_height=\"match_parent\"\n"+
            "    tools:context=\"com.jerey.logtool.MainActivity\">\n"+
            "\n"+
            "    <TextView\n"+
            "        android:layout_width=\"wrap_content\"\n"+
            "        android:layout_height=\"wrap_content\"\n"+
            "        android:text=\"Hello World!\"\n"+
            "        app:layout_constraintBottom_toBottomOf=\"parent\"\n"+
            "        app:layout_constraintLeft_toLeftOf=\"parent\"\n"+
            "        app:layout_constraintRight_toRightOf=\"parent\"\n"+
            "        app:layout_constraintTop_toTopOf=\"parent\" />\n"+
            "\n"+
            "</android.support.constraint.ConstraintLayout>\n";

    private static final String text_json = "{\n" +
            "      \"_id\": \"58d4e454421aa93abd1fd15a\", \n" +
            "      \"createdAt\": \"2017-03-24T17:18:12.745Z\", \n" +
            "      \"desc\": \"RecyclerView\\u4fa7\\u6ed1\\u83dc\\u5355\", \n" +
            "      \"images\": [\n" +
            "        \"http://img.gank.io/99a9d510-195d-4d50-a310-13b098c0c776\"\n" +
            "      ], \n" +
            "      \"publishedAt\": \"2017-03-29T11:48:49.343Z\", \n" +
            "      \"source\": \"web\", \n" +
            "      \"type\": \"Android\", \n" +
            "      \"url\": \"http://www.jianshu.com/p/af9f940d8d1c\", \n" +
            "      \"used\": true, \n" +
            "      \"who\": \"pss\"\n" +
            "    }";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogTools.i("xiamin");
        LogTools.w("xiamin");
        LogTools.e("xiamin");
        LogTools.json(text_json);

        LogTools.e("iii","xiamin");

        LogTools.getSettings()
                .setLogLevel(Log.WARN)
                .setBorderEnable(false)
                .setLogEnable(true);

        LogTools.i("xiamin");
        LogTools.w("xiamin");
        LogTools.e("xiamin");
        LogTools.json(text_json);

        LogTools.getSettings()
                .setLogLevel(Log.WARN)
                .setBorderEnable(false)
                .setInfoEnable(false)
                .setLogEnable(true);

        LogTools.i("xiamin");
        LogTools.w("xiamin");
        LogTools.e("xiamin");
        LogTools.json(text_json);
//        LogUtils.e("xiamin");
//        LogUtils.e(test_xml);

    }
}

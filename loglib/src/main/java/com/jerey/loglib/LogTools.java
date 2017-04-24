package com.jerey.loglib;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Formatter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * ┌───┐   ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
 * │Esc│   │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│  ┌┐    ┌┐    ┌┐
 * └───┘   └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘  └┘    └┘    └┘
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐ ┌───┬───┬───┐ ┌───┬───┬───┬───┐
 * │~ `│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp │ │Ins│Hom│PUp│ │N L│ / │ * │ - │
 * ├───┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤ ├───┼───┼───┤ ├───┼───┼───┼───┤
 * │ Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ │ │Del│End│PDn│ │ 7 │ 8 │ 9 │   │
 * ├─────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤ └───┴───┴───┘ ├───┼───┼───┤ + │
 * │ Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │               │ 4 │ 5 │ 6 │   │
 * ├──────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤     ┌───┐     ├───┼───┼───┼───┤
 * │ Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │     │ ↑ │     │ 1 │ 2 │ 3 │   │
 * ├─────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤ ┌───┼───┼───┐ ├───┴───┼───┤ E││
 * │ Ctrl│    │Alt │         Space         │ Alt│    │    │Ctrl│ │ ← │ ↓ │ → │ │   0   │ . │←─┘│
 * └─────┴────┴────┴───────────────────────┴────┴────┴────┴────┘ └───┴───┴───┘ └───────┴───┴───┘
 * Created by xiamin on 3/29/17.
 */

/**
 * <pre>
 *     author: xiamin
 *     blog  : http://jerey.cn
 *     desc  : 一个可控制边框美化,可控制打印等级,可控制Log info, 自动识别类名为Tag, 同时支持自定义Tag的日志工具库
 * </pre>
 */
public final class LogTools {

    private static final int JSON = -1;
    private static final int XML = -2;
    private static final int MAX_LEN = 4000;

    private static final String TOP_BORDER = "╔═════════════════════════════════════════════════════════════════════════════════════";
    private static final String LEFT_BORDER = "║ ";
    private static final String BOTTOM_BORDER = "╚═════════════════════════════════════════════════════════════════════════════════════";
    //解决windows和linux换行不一致的问题 功能和"\n"是一致的,但是此种写法屏蔽了 Windows和Linux的区别 更保险.
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static String mLogDir;  // log存储目录
    private static boolean mLogEnable = true; // log总开关
    private static String mGlobalLogTag = ""; // log标签
    private static boolean mTagIsSpace = true; // log标签是否为空白
    private static boolean mLog2FileEnable = false;// log是否写入文件
    private static boolean mLogBorderEnable = true; // log边框
    private static boolean mLogInfoEnable = true;   // log详情开关
    private static int mLogFilter = Log.VERBOSE;    // log过滤器


    private static final String NULL_TIPS = "Log with a null object;";
    private static final String NULL = "null";
    private static final String ARGS = "args";

    private LogTools() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void i(Object contents) {
        log(Log.INFO, mGlobalLogTag, contents);
    }

    public static void i(String tag, Object... contents) {
        log(Log.INFO, tag, contents);
    }

    public static void w(Object contents) {
        log(Log.WARN, mGlobalLogTag, contents);
    }

    public static void w(String tag, Object... contents) {
        log(Log.WARN, tag, contents);
    }

    public static void e(Object contents) {
        log(Log.ERROR, mGlobalLogTag, contents);
    }

    public static void e(String tag, Object... contents) {
        log(Log.ERROR, tag, contents);
    }

    public static void a(Object... contents) {
        log(Log.ASSERT, mGlobalLogTag, contents);
    }

    public static void a(String tag, Object... contents) {
        log(Log.ASSERT, tag, contents);
    }

    public static void d(Object contents) {
        log(Log.DEBUG, mGlobalLogTag, contents);
    }

    public static void d(String tag, Object... contents) {
        log(Log.DEBUG, tag, contents);
    }

    public static void json(String contents) {
        log(JSON, mGlobalLogTag, contents);
    }

    public static void json(String tag, Object... contents) {
        log(JSON, tag, contents);
    }

    public static void xml(Object contents) {
        log(XML, mGlobalLogTag, contents);
    }

    public static void xml(String tag, Object... contents) {
        log(XML, tag, contents);
    }

    /**
     * @param type
     * @param tag
     * @param objects
     */
    private static void log(int type, String tag, Object... objects) {
        //全局未开,直接返回
        if (!mLogEnable) {
            return;
        }
        final String[] processContents = processObj(type, tag, objects);
        tag = processContents[0];
        String msg = processContents[1];
        switch (type) {
            case Log.INFO:
            case Log.ASSERT:
            case Log.DEBUG:
            case Log.ERROR:
            case Log.WARN:
                if (mLogFilter <= type) {
                    logOutout(type, tag, msg);
                }
                break;
            case JSON:
                logOutout(Log.DEBUG, tag, msg);
                break;
            case XML:
                logOutout(Log.DEBUG, tag, msg);
                break;
        }
    }

    private static String[] processObj(int type, String tag, Object... contents) {
        StackTraceElement targetElement = Thread.currentThread().getStackTrace()[5];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");

        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        if (className.contains("$")) {
            className = className.split("\\$")[0];
        }
        if (!mTagIsSpace) {// 如果全局tag不为空，那就用全局tag
            tag = mGlobalLogTag;
        } else {// 全局tag为空时，如果传入的tag为空那就显示类名，否则显示tag
            tag = isSpace(tag) ? className : tag;
        }
        String head = new Formatter()
                .format("Thread: %s,  Method: %s  (%s.java  Line:%d)" + LINE_SEPARATOR,
                        Thread.currentThread().getName(),
                        targetElement.getMethodName(),
                        className,
                        targetElement.getLineNumber())
                .toString();
        String msg = NULL_TIPS;
        if (contents != null) {
            //正常使用情况下都是只有一个参数
            if (contents.length == 1) {
                Object object = contents[0];
                msg = object == null ? NULL : object.toString();
                if (type == JSON) {
                    msg = formatJson(msg);
                } else if (type == XML) {
                    msg = formatXml(msg);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS)
                            .append("[")
                            .append(i)
                            .append("]")
                            .append(" = ")
                            .append(content == null ? NULL : content.toString())
                            .append(LINE_SEPARATOR);
                }
                msg = sb.toString();
            }
        }
        if (mLogBorderEnable) {
            StringBuilder sb = new StringBuilder();
            String[] lines = msg.split(LINE_SEPARATOR);
            sb.append(TOP_BORDER).append(LINE_SEPARATOR);
            sb.append(LEFT_BORDER).append(head);
            for (String line : lines) {
                sb.append(LEFT_BORDER).append(line).append(LINE_SEPARATOR);
            }
            sb.append(BOTTOM_BORDER).append(LINE_SEPARATOR);
            msg = sb.toString();
            return new String[]{tag, msg};
        }

        if (mLogInfoEnable) {
            StringBuilder sb = new StringBuilder();
            String[] lines = msg.split(LINE_SEPARATOR);
            for (String line : lines) {
                sb.append(line).append(LINE_SEPARATOR);
            }
            msg = sb.toString();
            return new String[]{tag, head + msg};
        }

        return new String[]{tag, msg};
    }

    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEPARATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * 输出log
     *
     * @param type
     * @param tag
     * @param msg
     */
    private static void logOutout(int type, String tag, String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            String sub;
            for (int i = 0; i < countOfSub; i++) {
                sub = msg.substring(index, index + MAX_LEN);
                printSubLog(type, tag, sub);
                index += MAX_LEN;
            }
            printSubLog(type, tag, msg.substring(index, len));
        } else {
            printSubLog(type, tag, msg);
        }
    }

    private static void printSubLog(final int type, final String tag, String msg) {
        switch (type) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.ASSERT:
                Log.wtf(tag, msg);
                break;
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * LogTools设置类
     */
    public static class Settings {
        /**
         * 设置Log是否开启
         *
         * @param enable
         * @return
         */
        public Settings setLogEnable(Boolean enable) {
            LogTools.mLogEnable = enable;
            return this;
        }

        /**
         * 设置打印等级,只有高于该打印等级的log会被打印<br>
         * 打印等级从低到高分别为: Log.VERBOSE < Log.DEBUG < Log.INFO < Log.WARN < Log.ERROR < Log.ASSERT
         *
         * @param logLevel
         */
        public Settings setLogLevel(int logLevel) {
            LogTools.mLogFilter = logLevel;
            return this;
        }

        /**
         * 设置边框是否开启
         *
         * @param enable
         * @return
         */
        public Settings setBorderEnable(boolean enable) {
            LogTools.mLogBorderEnable = enable;
            return this;
        }

        /**
         * 设置Log 行号,方法,class详情信息是否打印的开关
         * @param enable
         * @return
         */
        public Settings setInfoEnable(boolean enable){
            LogTools.mLogInfoEnable = false;
            return this;
        }

        /**
         * 获取打印等级
         *
         * @return
         */
        public int getLogLevel() {
            return LogTools.mLogFilter;
        }

    }

    /**
     * 设置入口
     * <code>
     * LogTools.getSettings()
     * .setLogLevel(Log.WARN)
     * .setBorderEnable(true)
     * .setLogEnable(true);
     * </code>
     *
     * @return
     */
    public static Settings getSettings() {
        return new Settings();
    }
}

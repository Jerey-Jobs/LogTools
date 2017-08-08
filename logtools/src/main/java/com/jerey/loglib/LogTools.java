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
 * <pre>
 *     Created by xiamin on 4/24/17.
 *     desc  : A log lib, one can control the border beautify,
 *             can control the print level,
 *             can control the Log info,
 *             automatically identify the class as Tag,
 *             while supporting the custom Tag
 *     use: LogTools.i(Object obj)...
 *     setting:
 *           LogTools.getSettings()
 *                   .setGlobalLogTag(TAG)
 *                   .setLogLevel(Log.WARN)
 *                   .setBorderEnable(true)
 *                   .setLogEnable(true);
 * </pre>
 * <p>
 * use <code>adb shell setprop log.tag.@params{tag} {level}</code> to control your logLevel
 * <p>
 * </P>
 */
public final class LogTools {

    private static final int JSON = -1;
    private static final int XML = -2;
    private static final int MAX_LEN = 4000;
    private static final int BUILD_DEBUG = -111;

    private static final String TOP_BORDER =
            "╔═════════════════════════════════════════════════════════════════════════════════════";
    private static final String LEFT_BORDER = "║ ";
    private static final String BOTTOM_BORDER =
            "╚═════════════════════════════════════════════════════════════════════════════════════";
    // To solve the problem of inconsistent windows and linux problems and "\n"
    // is the same, but this way to shield the difference between Windows and
    // Linux more insurance.
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static String mLogDir; // log save directory
    private static boolean mLogEnable = true; // log master switch
    private static String mGlobalLogTag = ""; // log tag
    private static boolean mTagIsSpace = true; // Whether the tag is blank
    // Whether the log will be written to file
    private static boolean mLog2FileEnable = false;
    private static boolean mLogBorderEnable = false; // log border
    private static boolean mLogInfoEnable = true; // log info switch
    private static int mLogFilter = Log.VERBOSE; // log filter

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
        log(Log.VERBOSE, tag, contents);
    }

    public static void v(Object contents) {
        log(Log.VERBOSE, mGlobalLogTag, contents);
    }

    public static void v(String tag, Object... contents) {
        log(Log.VERBOSE, tag, contents);
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


    public static void debug_build(Object contents) {
        log(BUILD_DEBUG, mGlobalLogTag, contents);
    }

    /**
     * @param type
     * @param tag
     * @param objects
     */
    private static void log(int type, String tag, Object... objects) {
        // The global is not open, direct return
        if (!mLogEnable) {
            return;
        }
        final String[] processContents = processObj(type, tag, objects);
        tag = processContents[0];
        String msg = processContents[1];
        switch (type) {
            case Log.VERBOSE:
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
            case BUILD_DEBUG:
                logOutout(BUILD_DEBUG, tag, msg);
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
        // If the global tag is not empty, use the global tag
        if (mTagIsSpace) {
            tag = isSpace(tag) ? mGlobalLogTag : tag;
        } else {
            // When the global tag is empty, the class name is displayed if the
            // incoming tag is empty, otherwise the tag is displayed
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
            // normal use are only one parameter
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
            return new String[] {
                    tag, msg
            };
        }

        if (mLogInfoEnable) {
            StringBuilder sb = new StringBuilder();
            String[] lines = msg.split(LINE_SEPARATOR);
            for (String line : lines) {
                sb.append(line).append(LINE_SEPARATOR);
            }
            msg = sb.toString();
            return new String[] {
                    tag, head + msg
            };
        }

        return new String[] {
                tag, msg
        };
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
     * Output log
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
                if (Log.isLoggable(tag, Log.VERBOSE)) {
                    Log.v(tag, msg);
                }
                break;
            case Log.DEBUG:
                if (Log.isLoggable(tag, Log.DEBUG)) {
                    Log.d(tag, msg);
                }
                break;
            case Log.INFO:
                if (Log.isLoggable(tag, Log.INFO)) {
                    Log.i(tag, msg);
                }
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
            case BUILD_DEBUG:
                if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.INFO)) {
                    Log.i(tag, msg);
                }
        }
    }

    private static boolean isSpace(String s) {
        if (s == null)
            return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * LogTools's settings helper
     */
    public static class Settings {
        /**
         * Set Log enable
         * @param enable
         * @return
         */
        public Settings setLogEnable(Boolean enable) {
            LogTools.mLogEnable = enable;
            return this;
        }

        /**
         * Set the print level, only the log above the print level will be
         * printed<br>
         * Print levels are low to high: Log.VERBOSE < Log.DEBUG < Log.INFO <
         * Log.WARN < Log.ERROR < Log.ASSERT
         * @param logLevel
         */
        public Settings setLogLevel(int logLevel) {
            LogTools.mLogFilter = logLevel;
            return this;
        }

        /**
         * Set whether the border is open
         * @param enable
         * @return
         */
        public Settings setBorderEnable(boolean enable) {
            LogTools.mLogBorderEnable = enable;
            return this;
        }

        /**
         * Set the log line number, method, class details information whether to
         * print
         * @param enable
         * @return
         */
        public Settings setInfoEnable(boolean enable) {
            LogTools.mLogInfoEnable = enable;
            return this;
        }

        /**
         * suggest use the globalTAG because in this way, TestEngineer can use global tag
         * control log level
         * @param globalLogTag
         * @return
         */
        public Settings setGlobalLogTag(String globalLogTag) {
            LogTools.mGlobalLogTag = globalLogTag;
            LogTools.mTagIsSpace = false;
            return this;
        }

        /**
         * get current log level
         * @return
         */
        public int getLogLevel() {
            return LogTools.mLogFilter;
        }

    }

    /**
     * the setting enter <br>
     * <pre>
     *     LogTools.getSettings()
     *         .setGlobalLogTag(TAG)
     *         .setBorderEnable(false)
     *         .setInfoEnable(true);
     * </pre>
     * @return
     */
    public static Settings getSettings() {
        return new Settings();
    }
}

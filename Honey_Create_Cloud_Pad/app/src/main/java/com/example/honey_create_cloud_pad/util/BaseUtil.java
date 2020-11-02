package com.example.honey_create_cloud_pad.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
* author : zhoujr
* date : 2020/10/29 10:21
* desc : 常用公共类
*/
public class BaseUtil {

    /**
     * 汉字转拼音
     * @param chars
     * @return
     */
    public static String getPinYinHeadChar(char[] chars) {
        StringBuffer sb = new StringBuffer();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 转base64
     * @param url
     * @return
     */
    public static String tobase64(String url) {
        try {
            File file = new File(url);
            // 下载网络文件
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = new FileInputStream(file);
            int size = inStream.available();
            byte[] buffer = new byte[size];
            while ((byteread = inStream.read(buffer)) != -1) {
                inStream.read(buffer);
                inStream.close();
                byte[] bytes = Base64.encodeBase64(buffer);
//                byte[] bytes = new byte[]{};
                String str = new String(bytes);
                if (str != null) {
                    str = str.replaceAll(System.getProperty("line.separator"), "");
                    str = str.replaceAll("=", "");
                    str = str.replaceAll(" ", "");
                }
                return str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

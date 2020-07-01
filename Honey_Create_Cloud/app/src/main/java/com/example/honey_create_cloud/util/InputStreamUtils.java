package com.example.honey_create_cloud.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangpan on 2020/6/28
 */
public class InputStreamUtils {

    final static int BUFFER_SIZE=4096;

    /**
     * 将InputStream转换成String
     *
     * @paraminInputStream
     * @returnString
     * @throwsException
     */
    public static String InputStreamTOString(InputStream in)throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "ISO-8859-1");
    }

    /**
     * 将InputStream转换成某种字符编码的String
     *
     * @return
     * @paramin
     * @paramencoding
     * @throwsException
     */
    public static String InputStreamTOString(InputStream in, String encoding)throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "ISO-8859-1");
    }

    /**
     * 将String转换成InputStream
     *
     * @return
     * @paramin
     * @throwsException
     */
    public static InputStream StringTOInputStream(String in)throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @paraminInputStream
     * @returnbyte[]
     * @throwsIOException
     */
    public static byte[] InputStreamTOByte(InputStream in)throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将byte数组转换成InputStream
     *
     * @return
     * @paramin
     * @throwsException
     */
    public static InputStream byteTOInputStream(byte[] in)throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

    /**
     * 将byte数组转换成String
     *
     * @return
     * @paramin
     * @throwsException
     */
    public static String byteTOString(byte[] in)throws Exception {

        InputStream is = byteTOInputStream(in);
        return InputStreamTOString(is);
    }
}

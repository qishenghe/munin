package com.qishenghe.munin.banner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Banner printer
 *
 * @author qishenghe
 * @date 2020/12/29 19:09
 * @change 2020/12/29 19:09 by qishenghe@bonc.com.cn for init
 */
public class MuninBannerPrinter {

    /**
     * banner count
     */
    private static int bannerCount = 0;

    /**
     * Banner printer
     */
    public static void printBanner () {

        if (bannerCount == 0) {
            String bannerPath = System.getProperty("user.dir") + "\\src\\main\\resources\\" + "banner.txt";
            List<String> bannerList = readFileList(bannerPath);
            for (String single : bannerList) {
                System.out.println(single);
            }

            bannerCount ++;
        }
    }

    /**
     * 读文件
     * @param path 文件路径
     * @return 文件内容
     */
    private static List<String> readFileList(String path) {
        List<String> lineList = new ArrayList<>();
        try (InputStreamReader isReader = new InputStreamReader(new FileInputStream(path), Charset.forName("UTF-8")); BufferedReader br = new BufferedReader(isReader)) {
            String tempString;
            while ((tempString = br.readLine()) != null) {
                lineList.add(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineList;
    }

}

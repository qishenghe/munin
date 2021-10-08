package com.qishenghe.munin.banner;

import com.qishenghe.munin.version.MuninVersion;

/**
 * Banner printer
 *
 * @author qishenghe
 * @date 2020/12/29 19:09
 * @change 2020/12/29 19:09 by qishenghe@bonc.com.cn for init
 */
public class MuninBannerPrinter {

    /**
     * BANNER_HEAD
     */
    private static final String BANNER_HEAD = "   __  ___          _\n" +
            "  /  |/  /_ _____  (_)__\n" +
            " / /|_/ / // / _ \\/ / _ \\\n" +
            "/_/  /_/\\_,_/_//_/_/_//_/";

    /**
     * BANNER_NAME
     */
    private static final String BANNER_NAME = ":: Munin ::";

    /**
     * Banner printer
     */
    public static void printBanner () {

        String version = MuninVersion.getVersion();

        System.out.println(BANNER_HEAD);

        System.out.print("\u001B[36m" + BANNER_NAME + "\u001B[0;39m");
        System.out.print("\t\t");
        System.out.println("\u001B[33m" + "(v" + version + ")" + "\u001B[0;39m");

    }
}

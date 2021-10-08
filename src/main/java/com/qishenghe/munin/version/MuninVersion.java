package com.qishenghe.munin.version;

/**
 * Munin Version
 *
 * @author qishenghe
 * @date 2020/12/29 19:09
 * @change 2020/12/29 19:09 by qishenghe@bonc.com.cn for init
 */
public class MuninVersion {

    /**
     * Constructor
     */
    private MuninVersion() {
    }

    /**
     * Get Version
     * @return version number
     */
    public static String getVersion () {
        Package pkg = MuninVersion.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : null;
    }


}

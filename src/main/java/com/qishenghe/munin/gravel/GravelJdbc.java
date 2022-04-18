package com.qishenghe.munin.gravel;

import com.alibaba.druid.util.StringUtils;
import com.qishenghe.munin.gravel.job.GravelJdbcInitJob;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * jdbc gravel
 *
 * @author qishenghe
 * @date 4/18/22 3:29 PM
 * @change 4/18/22 3:29 PM by shenghe.qi@relxtech.com for init
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GravelJdbc extends Gravel {

    /**
     * [base] url
     */
    private String url;

    /**
     * [base] username
     */
    private String username;

    /**
     * [base] password
     */
    private String password;

    /**
     * [base null maybe] driver
     */
    private String driver;

    /**
     * [source] table name
     */
    private String sourceTableName;

    /**
     * [source] sql
     */
    private String sourceSql;

    /**
     * [column name] dictCode [non]
     */
    private String colNameDictCode;

    /**
     * [column name] dictName
     */
    private String colNameDictName;

    /**
     * [column name] code [non]
     */
    private String colNameCode;

    /**
     * [column name] meaning [non]
     */
    private String colNameMeaning;

    /**
     * [column name] sortNum
     */
    private String colNameSortNum;

    /**
     * [column name] expand
     */
    private String colNameExpand;

    /**
     * create init job
     * 
     * @return init job
     * @since 1.0.0
     * @author qishenghe
     * @date 4/18/22 5:36 PM
     * @change 4/18/22 5:36 PM by shenghe.qi@relxtech.com for init
     */
    public GravelJdbcInitJob createInitJob () {

        GravelJdbcInitJob initJob = new GravelJdbcInitJob();
        initJob.setGravel(this);

        return initJob;
    }

}

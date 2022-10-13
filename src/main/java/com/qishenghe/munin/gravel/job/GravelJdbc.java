package com.qishenghe.munin.gravel.job;

import com.alibaba.druid.DbType;
import com.qishenghe.munin.cache.job.DictPackInitJob;
import com.qishenghe.munin.gravel.Gravel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

/**
 * @author shenghe.qi
 * @date 10/13/22 2:15 PM
 * @change 10/13/22 2:15 PM by shenghe.qi for init
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GravelJdbc extends Gravel {

    /**
     * 数据源
     */
    private DataSource dataSource;

    /**
     * 资源表名
     */
    private String sourceTable;

    /**
     * 资源sql
     */
    private String sourceSql;

    /**
     * 数据库类型
     */
    private DbType sourceDbType;

    /**
     * 【列名】字典编码【not null】
     */
    private String colDictCode;

    /**
     * 【列名】字典名称
     */
    private String colDictName;

    /**
     * 【列名】编码【not null】
     */
    private String colCode;

    /**
     * 【列名】值【not null】
     */
    private String colMeaning;

    /**
     * 【列名】序号
     */
    private String colSortNum;

    /**
     * 【列名】扩展信息
     */
    private String colExpand;

    /**
     * Builder
     */
    public static class Builder {

        /**
         * 数据源
         */
        private DataSource dataSource;

        /**
         * 资源表名
         */
        private String sourceTable;

        /**
         * 资源sql
         */
        private String sourceSql;

        /**
         * 数据库类型
         */
        private DbType sourceDbType;

        /**
         * 【列名】字典编码【not null】
         */
        private String colDictCode;

        /**
         * 【列名】字典名称
         */
        private String colDictName;

        /**
         * 【列名】编码【not null】
         */
        private String colCode;

        /**
         * 【列名】值【not null】
         */
        private String colMeaning;

        /**
         * 【列名】序号
         */
        private String colSortNum;

        /**
         * 【列名】扩展信息
         */
        private String colExpand;

        /**
         * set datasource
         * @param dataSource datasource
         * @return builder
         */
        public synchronized Builder setDataSource (DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        /**
         * set sourceTable
         * @param sourceTable sourceTable
         * @return builder
         */
        public synchronized Builder setSourceTable (String sourceTable) {
            this.sourceTable = sourceTable;
            return this;
        }

        /**
         * set sourceSql
         * @param sourceSql sourceSql
         * @return builder
         */
        public synchronized Builder setSourceSql (String sourceSql) {
            this.sourceSql = sourceSql;
            return this;
        }

        /**
         * set sourceDbType
         * @param sourceDbType sourceDbType
         * @return builder
         */
        public synchronized Builder setSourceDbType (DbType sourceDbType) {
            this.sourceDbType = sourceDbType;
            return this;
        }

        /**
         * set colDictCode
         * @param colDictCode colDictCode
         * @return builder
         */
        public synchronized Builder setColDictCode (String colDictCode) {
            this.colDictCode = colDictCode;
            return this;
        }

        /**
         * set colDictName
         * @param colDictName colDictName
         * @return builder
         */
        public synchronized Builder setColDictName (String colDictName) {
            this.colDictName = colDictName;
            return this;
        }

        /**
         * set colCode
         * @param colCode colCode
         * @return builder
         */
        public synchronized Builder setColCode (String colCode) {
            this.colCode = colCode;
            return this;
        }

        /**
         * set colMeaning
         * @param colMeaning colMeaning
         * @return builder
         */
        public synchronized Builder setColMeaning (String colMeaning) {
            this.colMeaning = colMeaning;
            return this;
        }

        /**
         * set colSortNum
         * @param colSortNum colSortNum
         * @return builder
         */
        public synchronized Builder setColSortNum (String colSortNum) {
            this.colSortNum = colSortNum;
            return this;
        }

        /**
         * set colExpand
         * @param colExpand colExpand
         * @return builder
         */
        public synchronized Builder setColExpand (String colExpand) {
            this.colExpand = colExpand;
            return this;
        }

        /**
         * build
         * @return shortcut
         */
        public GravelJdbc build () {
            // 检查数据源
            if (dataSource == null) {
                throw new RuntimeException("数据源不可用");
            }
            // 检查资源表
            if (StringUtils.isEmpty(sourceTable) && StringUtils.isEmpty(sourceSql)) {
                throw new RuntimeException("资源表未知");
            }
            // 检查必要字段映射
            if (StringUtils.isEmpty(colDictCode) || StringUtils.isEmpty(colCode) || StringUtils.isEmpty(colMeaning)) {
                throw new RuntimeException("必要字段映射为空，必要映射 : [colDictCode][colCode][colMeaning]");
            }

            GravelJdbc gravelJdbc = new GravelJdbc();
            gravelJdbc.setDataSource(dataSource);
            gravelJdbc.setSourceTable(sourceTable);
            gravelJdbc.setSourceSql(sourceSql);
            gravelJdbc.setSourceDbType(sourceDbType);
            gravelJdbc.setColDictCode(colDictCode);
            gravelJdbc.setColDictName(colDictName);
            gravelJdbc.setColCode(colCode);
            gravelJdbc.setColMeaning(colMeaning);
            gravelJdbc.setColSortNum(colSortNum);
            gravelJdbc.setColExpand(colExpand);

            return gravelJdbc;
        }

    }

    /**
     * create init job
     *
     * @return dict pack init job
     * @author shenghe.qi
     * @date 10/9/22 11:46 AM
     * @change 10/9/22 11:46 AM by shenghe.qi for init
     */
    @Override
    public DictPackInitJob createInitJob() {

        GravelJdbcInitJob initJob = new GravelJdbcInitJob();
        initJob.setGravelJdbc(this);

        return initJob;
    }
}

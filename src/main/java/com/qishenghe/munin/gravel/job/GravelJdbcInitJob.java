package com.qishenghe.munin.gravel.job;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.qishenghe.munin.cache.job.DictPackInitJob;
import com.qishenghe.munin.cache.pack.DictEntity;
import com.qishenghe.munin.gravel.GravelJdbc;
import lombok.Data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * gravel
 *
 * @author qishenghe
 * @date 4/18/22 4:37 PM
 * @change 4/18/22 4:37 PM by shenghe.qi@relxtech.com for init
 */
@Data
public class GravelJdbcInitJob implements DictPackInitJob {

    /**
     * [jdbc] gravel
     */
    private GravelJdbc gravel;

    /**
     * init
     * 
     * @return 标准输出
     * @since 1.0.0
     * @author qishenghe
     * @date 4/18/22 5:38 PM
     * @change 4/18/22 5:38 PM by shenghe.qi@relxtech.com for init
     */
    @Override
    public List<DictEntity> init() {

        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setUrl(gravel.getUrl());
        druidDataSource.setUsername(gravel.getUsername());
        druidDataSource.setPassword(gravel.getPassword());
        if (!StringUtils.isEmpty(gravel.getDriver())) {
            druidDataSource.setDriverClassName(gravel.getDriver());
        }

        druidDataSource.setInitialSize(1);
        druidDataSource.setAsyncInit(true);

        // 初始化
        try {
            druidDataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("【gravel】数据库链接池初始化异常");
        }

        // 创建查询sql
        String sql;
        if (!StringUtils.isEmpty(gravel.getSourceSql())) {
            sql = gravel.getSourceSql();
        } else {
            sql = createSql();
        }
        // 执行sql
        List<Map<String, Object>> mapList;
        try {
            mapList = JdbcUtils.executeQuery(druidDataSource, sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("【gravel】数据库查询失败");
        }

        // 将查询结果处理成标准输出
        List<DictEntity> resultList = processQueryResult(mapList);

        druidDataSource.close();

        return resultList;
    }

    /**
     * 创建sql
     * 
     * @return sql
     * @since 1.0.0
     * @author qishenghe
     * @date 4/18/22 6:05 PM
     * @change 4/18/22 6:05 PM by shenghe.qi@relxtech.com for init
     */
    private String createSql () {

        DbType dbType = JdbcUtils.getDbTypeRaw(gravel.getUrl(), gravel.getDriver());

        SQLSelectBuilderImpl builder = new SQLSelectBuilderImpl(dbType);

        builder.from(gravel.getSourceTableName());

        List<String> columnList = new ArrayList<>();

        if (!StringUtils.isEmpty(gravel.getColNameDictCode())) {
            columnList.add(gravel.getColNameDictCode());
        }

        if (!StringUtils.isEmpty(gravel.getColNameDictName())) {
            columnList.add(gravel.getColNameDictName());
        }

        if (!StringUtils.isEmpty(gravel.getColNameCode())) {
            columnList.add(gravel.getColNameCode());
        }

        if (!StringUtils.isEmpty(gravel.getColNameMeaning())) {
            columnList.add(gravel.getColNameMeaning());
        }

        if (!StringUtils.isEmpty(gravel.getColNameSortNum())) {
            columnList.add(gravel.getColNameSortNum());
        }

        if (!StringUtils.isEmpty(gravel.getColNameExpand())) {
            columnList.add(gravel.getColNameExpand());
        }

        builder.select(columnList.toArray(new String[0]));

        return builder.toString();
    }

    /**
     * 处理查询结果
     * 
     * @param mapList 查询结果
     * @return 处理结果
     * @since 1.0.0
     * @author qishenghe
     * @date 4/18/22 6:09 PM
     * @change 4/18/22 6:09 PM by shenghe.qi@relxtech.com for init
     */
    private List<DictEntity> processQueryResult (List<Map<String, Object>> mapList) {

        List<DictEntity> resultList = new ArrayList<>();

        if (mapList != null && mapList.size() > 0) {

            for (Map<String, Object> singleMap : mapList) {

                DictEntity singleEntity = new DictEntity();

                if (!StringUtils.isEmpty(gravel.getColNameDictCode())) {
                    singleEntity.setDictCode(singleMap.get(gravel.getColNameDictCode()) == null ? null : singleMap.get(gravel.getColNameDictCode()).toString());
                }

                if (!StringUtils.isEmpty(gravel.getColNameDictName())) {
                    singleEntity.setDictName(singleMap.get(gravel.getColNameDictName()) == null ? null : singleMap.get(gravel.getColNameDictName()).toString());
                }

                if (!StringUtils.isEmpty(gravel.getColNameCode())) {
                    singleEntity.setCode(singleMap.get(gravel.getColNameCode()) == null ? null : singleMap.get(gravel.getColNameCode()).toString());
                }

                if (!StringUtils.isEmpty(gravel.getColNameMeaning())) {
                    singleEntity.setMeaning(singleMap.get(gravel.getColNameMeaning()) == null ? null : singleMap.get(gravel.getColNameMeaning()).toString());
                }

                if (!StringUtils.isEmpty(gravel.getColNameSortNum())) {
                    singleEntity.setSortNum(singleMap.get(gravel.getColNameSortNum()) == null ? null : Integer.parseInt(singleMap.get(gravel.getColNameSortNum()).toString()));
                }

                if (!StringUtils.isEmpty(gravel.getColNameExpand())) {
                    singleEntity.setExpand(singleMap.get(gravel.getColNameExpand()) == null ? null : singleMap.get(gravel.getColNameExpand()).toString());
                }

                resultList.add(singleEntity);
            }

        }

        return resultList;
    }


}

package com.qishenghe.munin.gravel.job;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.qishenghe.munin.cache.job.DictPackInitJob;
import com.qishenghe.munin.cache.pack.DictEntity;
import lombok.Data;

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
     * shortcut jdbc
     */
    private GravelJdbc gravelJdbc;

    /**
     * init
     *
     * @return 标准输出
     * @author shenghe.qi
     * @date 10/9/22 10:34 AM
     * @change 10/9/22 10:34 AM by shenghe.qi for init
     */
    @Override
    public List<DictEntity> init() {

        // 创建查询sql
        String querySql = createQuerySql();
        // 执行sql
        List<Map<String, Object>> mapList;
        try {
            mapList = JdbcUtils.executeQuery(gravelJdbc.getDataSource(), querySql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[gravel][jdbc]数据库查询失败，原因 : " + e.getMessage());
        }
        // 将查询结果处理为标准输出
        return processQueryResult(mapList);
    }

    /**
     * 创建查询sql
     *
     * @return query sql
     * @author shenghe.qi
     * @date 10/9/22 11:28 AM
     * @change 10/9/22 11:28 AM by shenghe.qi for init
     */
    private String createQuerySql () {

        if (gravelJdbc.getSourceDbType() == null) {
            gravelJdbc.setSourceDbType(DbType.mysql);
        }

        // 临时资源表别名
        String alias = "tmp";

        SQLSelectBuilderImpl builder = new SQLSelectBuilderImpl(gravelJdbc.getSourceDbType());

        if (StringUtils.isEmpty(gravelJdbc.getSourceSql())) {
            // 资源sql为空，以资源表为查询依据
            builder.from("(select * from " + gravelJdbc.getSourceTable() + ")", alias);
        } else {
            // 以资源sql为基础查询依据
            builder.from("(" + gravelJdbc.getSourceSql() + ")", alias);
        }

        if (!StringUtils.isEmpty(gravelJdbc.getColDictCode())) {
            builder.selectWithAlias(alias + "." + gravelJdbc.getColDictCode(), gravelJdbc.getColDictCode());
        }

        if (!StringUtils.isEmpty(gravelJdbc.getColDictName())) {
            builder.selectWithAlias(alias + "." + gravelJdbc.getColDictName(), gravelJdbc.getColDictName());
        }

        if (!StringUtils.isEmpty(gravelJdbc.getColCode())) {
            builder.selectWithAlias(alias + "." + gravelJdbc.getColCode(), gravelJdbc.getColCode());
        }

        if (!StringUtils.isEmpty(gravelJdbc.getColMeaning())) {
            builder.selectWithAlias(alias + "." + gravelJdbc.getColMeaning(), gravelJdbc.getColMeaning());
        }

        if (!StringUtils.isEmpty(gravelJdbc.getColSortNum())) {
            builder.selectWithAlias(alias + "." + gravelJdbc.getColSortNum(), gravelJdbc.getColSortNum());
        }

        if (!StringUtils.isEmpty(gravelJdbc.getColExpand())) {
            builder.selectWithAlias(alias + "." + gravelJdbc.getColExpand(), gravelJdbc.getColExpand());
        }

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

                if (!StringUtils.isEmpty(gravelJdbc.getColDictCode())) {
                    singleEntity.setDictCode(singleMap.get(gravelJdbc.getColDictCode()) == null ? null : singleMap.get(gravelJdbc.getColDictCode()).toString());
                }

                if (!StringUtils.isEmpty(gravelJdbc.getColDictName())) {
                    singleEntity.setDictName(singleMap.get(gravelJdbc.getColDictName()) == null ? null : singleMap.get(gravelJdbc.getColDictName()).toString());
                }

                if (!StringUtils.isEmpty(gravelJdbc.getColCode())) {
                    singleEntity.setCode(singleMap.get(gravelJdbc.getColCode()) == null ? null : singleMap.get(gravelJdbc.getColCode()).toString());
                }

                if (!StringUtils.isEmpty(gravelJdbc.getColMeaning())) {
                    singleEntity.setMeaning(singleMap.get(gravelJdbc.getColMeaning()) == null ? null : singleMap.get(gravelJdbc.getColMeaning()).toString());
                }

                if (!StringUtils.isEmpty(gravelJdbc.getColSortNum())) {
                    singleEntity.setSortNum(singleMap.get(gravelJdbc.getColSortNum()) == null ? null : Integer.parseInt(singleMap.get(gravelJdbc.getColSortNum()).toString()));
                }

                if (!StringUtils.isEmpty(gravelJdbc.getColExpand())) {
                    singleEntity.setExpand(singleMap.get(gravelJdbc.getColExpand()) == null ? null : singleMap.get(gravelJdbc.getColExpand()).toString());
                }

                resultList.add(singleEntity);
            }

        }

        return resultList;
    }

}

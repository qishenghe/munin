package com.qishenghe.munin;

import com.qishenghe.munin.cache.job.DictPackInitJob;
import com.qishenghe.munin.cache.pack.DictEntity;
import com.qishenghe.munin.session.MuninSession;
import com.qishenghe.munin.util.DictCtrlUtil;
import com.qishenghe.munin.util.DictTransUtil;
import com.qishenghe.munin.util.MuninPoint;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 示例
 *
 * @author qishenghe
 * @date 2020/12/29 19:09
 * @change 2020/12/29 19:09 by qishenghe for init
 */
@Data
public class Demo {

    /**
     * 【demo】性别
     */
    private String sexName;

    /**
     * 【demo】性别编码
     */
    @MuninPoint(dictCode = "SEX", overTransCopyTo = "sexName")
    private String sexCode;

    /**
     * 【示例】Session
     *
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/9/22 19:12
     * @change 2021/9/22 19:12 by qishenghe for init
     */
    private MuninSession demoSession() {

        // 【demo】Session声明
        MuninSession muninSession = MuninSession.builder()
                .setDictPackInitJob((DictPackInitJob) () -> {
                    List<DictEntity> resultList = new ArrayList<>();

                    DictEntity entity1 = new DictEntity();
                    entity1.setDictCode("SEX");
                    entity1.setCode("01");
                    entity1.setMeaning("男");
                    entity1.setExpand("man");

                    DictEntity entity2 = new DictEntity();
                    entity2.setDictCode("SEX");
                    entity2.setCode("02");
                    entity2.setMeaning("女");
                    entity2.setExpand("woman");

                    resultList.add(entity1);
                    resultList.add(entity2);

                    return resultList;
                })
                .setAutoRefreshCron("0 0 0/10 * * ?")
                .setDictCtrlUtilConfig("readOnly", Boolean.TRUE.toString())
                .getOrCreate();

        System.out.println(muninSession.toString());

        return muninSession;
    }

    /**
     * 【示例】字典控制工具
     *
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/9/22 19:14
     * @change 2021/9/22 19:14 by qishenghe for init
     */
    private void demoDictCtrlUtil () {
        MuninSession muninSession = demoSession();

        // 【demo】字典控制工具
        DictCtrlUtil dictCtrlUtil = muninSession.getDictCtrlUtil();

        // 根据字典与编码获取字典实体
        dictCtrlUtil.getDictInfoByCode("SEX", "01");
        // 获取字典内容
        dictCtrlUtil.getPairsMapByDictCode("SEX");
    }

    /**
     * 【示例】字典转换工具
     *
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/9/22 19:15
     * @change 2021/9/22 19:15 by qishenghe for init
     */
    private void demoDictTransUtil () {
        MuninSession muninSession = demoSession();

        // 【demo】字典转换工具
        DictTransUtil dictTransUtil = muninSession.getDictTransUtil();

        // 示例数据集
        List<Demo> dataList = new ArrayList<>();
        Demo demo = new Demo();
        demo.setSexCode("01");
        dataList.add(demo);

        // 执行转换
        dictTransUtil.transResultCodeToMeaning(dataList);
        // 执行转换【并行处理】
        dictTransUtil.transResultCodeToMeaningMultiThread(dataList);
    }

}

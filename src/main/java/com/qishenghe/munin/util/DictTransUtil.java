package com.qishenghe.munin.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.qishenghe.munin.cache.pack.DictEntity;
import com.qishenghe.munin.session.MuninSession;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 字典数据转换工具
 *
 * @author qishenghe
 * @date 2021/6/5 18:15
 * @change 2021/6/5 18:15 by qishenghe for init
 */
@Data
public class DictTransUtil {

    /**
     * MuninSession
     */
    private transient MuninSession muninSession;

    /**
     * 线程工厂（显式定义名称）
     */
    private static ThreadFactory nameFactory =
                    new ThreadFactoryBuilder().setNameFormat("DictTransUtil-ThreadPool-%d").build();

    /**
     * 转换工具独立线程池（FixedThreadPool，poolSize：50）（阻塞队列MaxSize：2000）（抛弃策略：CallerRunsPolicy）
     */
    private static ExecutorService transUtilThreadPool = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(2000), nameFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 构造
     *
     * @param muninSession muninSession
     */
    public DictTransUtil(MuninSession muninSession) {
        this.muninSession = muninSession;
    }

    /**
     * 编码根据字典向原值转换
     *
     * @param result 结果
     * @param dictPoint 字典指向（字典指向优先级大于属性注解）
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> void transResultCodeToMeaning(T result, Map<String, String> dictPoint) {
        // 判空
        if (result == null) {
            return;
        }
        if (dictPoint == null) {
            dictPoint = new HashMap<>(0);
        }

        // 获取类属性，转Map（key：属性名，value：属性对象）
        Map<String, Field> fieldMap = getObjectFieldMap(result);

        for (String fieldName : fieldMap.keySet()) {
            // 当前属性
            Field field = fieldMap.get(fieldName);
            // 字典编码
            String dictCode = null;
            if (dictPoint.containsKey(fieldName)) {
                // 指向map中存在该属性
                dictCode = dictPoint.get(fieldName);
            } else {
                // 指向map中不存在
                if (field.isAnnotationPresent(MuninPoint.class)) {
                    // 被字典指向注解所修饰
                    if (!StringUtils.isEmpty(field.getAnnotation(MuninPoint.class).dictCode())) {
                        dictCode = field.getAnnotation(MuninPoint.class).dictCode();
                    }
                }
            }

            try {
                if (dictCode != null) {
                    // 需要进行转换
                    field.setAccessible(true);
                    String code = field.get(result) == null ? null : field.get(result).toString();
                    if (code != null) {
                        // 获取meaning
                        String meaning = getMeaningByCode(dictCode, code);
                        // 执行转换
                        if (field.isAnnotationPresent(MuninPoint.class)) {
                            // 存在指向注解，根据注解中的指示进行转换覆盖

                            // 转换前code覆盖指向，转换后meaning覆盖指向
                            String beforeTransCopyTo = field.getAnnotation(MuninPoint.class).beforeTransCopyTo();
                            String overTransCopyTo = field.getAnnotation(MuninPoint.class).overTransCopyTo();

                            if (StringUtils.isEmpty(beforeTransCopyTo) && StringUtils.isEmpty(overTransCopyTo)) {
                                // 转换前指向与转换后指向字段均为空，则直接覆盖原值
                                field.set(result, meaning);
                            } else {
                                // 转换前覆盖指向空值修正（修改覆盖指向为当前字段）
                                if (StringUtils.isEmpty(beforeTransCopyTo)) {
                                    beforeTransCopyTo = fieldName;
                                }
                                // 转换后覆盖指向空值修正（修改覆盖指向为当前字段）
                                if (StringUtils.isEmpty(overTransCopyTo)) {
                                    overTransCopyTo = fieldName;
                                }
                                // 转换后Meaning保留优先级高于原值，所以先赋值转换前Code，后赋值转换后Meaning，防止转换后结果被Code覆盖
                                Field beforeTransCopyToField = fieldMap.get(beforeTransCopyTo);
                                Field overTransCopyToField = fieldMap.get(overTransCopyTo);
                                // 设为可修改
                                beforeTransCopyToField.setAccessible(true);
                                overTransCopyToField.setAccessible(true);
                                // 顺序修改
                                beforeTransCopyToField.set(result, code);
                                overTransCopyToField.set(result, meaning);
                            }
                        } else {
                            // 未找到指向注解，直接覆盖原值
                            field.set(result, meaning);
                        }
                    }
                }
            } catch (Exception ignored) {
                // 转换时异常，冷处理
            }
        }
    }

    /**
     * 【重载】编码根据字典向原值转换（无字典指向map，依靠注解转换）
     *
     * @param result 结果
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> void transResultCodeToMeaning(T result) {
        transResultCodeToMeaning(result, new HashMap<>(0));
    }

    /**
     * 编码根据字典向原值转换（List）
     *
     * @param resultList 结果
     * @param dictPoint 字典指向（字典指向优先级大于属性注解）
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> void transResultCodeToMeaning(List<T> resultList, Map<String, String> dictPoint) {
        for (T singleResult : resultList) {
            transResultCodeToMeaning(singleResult, dictPoint);
        }
    }

    /**
     * 【重载】编码根据字典向原值转换（List）（无字典指向map，依靠注解转换）
     *
     * @param resultList 结果
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> void transResultCodeToMeaning(List<T> resultList) {
        transResultCodeToMeaning(resultList, new HashMap<>(0));
    }

    /**
     * 编码根据字典向原值转换（List）（多线程处理）
     *
     * @param resultList 结果
     * @param dictPoint 字典指向
     * @param block 阻塞（true：阻塞，false：非阻塞）
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> List<Future> transResultCodeToMeaningMultiThread(List<T> resultList, Map<String, String> dictPoint,
                    boolean block) {
        List<Future> futureList = new LinkedList<>();
        for (T single : resultList) {
            Future<Boolean> singleFuture =
                            transUtilThreadPool.submit(() -> transResultCodeToMeaning(single, dictPoint), true);
            futureList.add(singleFuture);
        }

        if (block) {
            // 在方法体内阻塞执行
            for (Future single : futureList) {
                try {
                    single.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        return futureList;
    }

    /**
     * 【重载】编码根据字典向原值转换（List）（多线程处理）
     *
     * @param resultList 结果
     * @param block 阻塞（true：阻塞，false：非阻塞）
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> List<Future> transResultCodeToMeaningMultiThread(List<T> resultList, boolean block) {
        return transResultCodeToMeaningMultiThread(resultList, new HashMap<>(0), block);
    }

    /**
     * 【多线程阻塞处理】编码根据字典向原值转换（List）（多线程处理）
     *
     * @param resultList 结果
     * @param dictPoint 字典指向
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> void transResultCodeToMeaningMultiThread(List<T> resultList, Map<String, String> dictPoint) {
        transResultCodeToMeaningMultiThread(resultList, dictPoint, true);
    }

    /**
     * 【多线程阻塞处理】编码根据字典向原值转换（List）（多线程处理）
     *
     * @param resultList 结果
     * @author qishenghe
     * @date 2021/6/8 10:44
     * @change 2021/6/8 10:44 by qishenghe for init
     * @since 1.0.0
     */
    public <T> void transResultCodeToMeaningMultiThread(List<T> resultList) {
        transResultCodeToMeaningMultiThread(resultList, new HashMap<>(0), true);
    }

    /**
     * 【封装】根据字典编码和编码（键）获取含义（值）
     *
     * @param dictCode 字典编码
     * @param code 编码（键）
     * @return 含义（值）
     * @author qishenghe
     * @date 2021/6/8 10:40
     * @change 2021/6/8 10:40 by qishenghe for init
     * @since 1.0.0
     */
    private String getMeaningByCode(String dictCode, String code) {
        DictEntity dictEntity = muninSession.getDictCtrlUtil().getDictInfoByCode(dictCode, code);
        return dictEntity.getMeaning();
    }

    /**
     * 【封装】将对象的属性置入Map中，避免遍历
     *
     * @param object obj
     * @return Map(Obj Name - Obj)
     */
    private static Map<String, Field> getObjectFieldMap(Object object) {
        // 获取所有属性
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields != null && fields.length != 0) {
            return Arrays.stream(fields).collect(Collectors.toMap(Field::getName, t -> t));
        } else {
            return new HashMap<>(0);
        }
    }

}

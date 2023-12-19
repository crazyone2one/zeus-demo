package cn.master.zeus.common.annotation;

import cn.master.zeus.common.constants.OperateLogConstants;

import java.lang.annotation.*;

/**
 * @author Created by 11's papa on 12/19/2023
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    /**
     * 功能模块
     *
     * @return
     */
    String module();

    /**
     * 项目
     *
     * @return
     */
    String project() default "";

    /**
     * 操作类型
     *
     * @return
     */
    OperateLogConstants type() default OperateLogConstants.OTHER;

    /**
     * 标题
     */
    String title() default "";

    /**
     * 资源ID
     */
    String sourceId() default "";


    /**
     * 操作内容
     *
     * @return
     */
    String content() default "";

    /**
     * 操作前触发内容
     *
     * @return
     */
    String beforeEvent() default "";

    /**
     * 传入执行类
     *
     * @return
     */
    Class[] msClass() default {};
}

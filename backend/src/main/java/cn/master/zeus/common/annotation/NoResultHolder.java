package cn.master.zeus.common.annotation;

import java.lang.annotation.*;

/**
 * @author Created by 11's papa on 12/27/2023
 **/
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResultHolder {
}

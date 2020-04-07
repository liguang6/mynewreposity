package com.byd.zzjmes.common.annotation;

import java.lang.annotation.*;

/**
 * 数据过滤
 *
 * @author Mark 
 * @since 3.0.0 2017-09-17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {
    /**  表的别名 */
    String tableAlias() default "";

    /**  true：没有本部门数据权限，也能查询本人数据 */
    boolean user() default true;

    /**  true：拥有子部门数据权限 */
    boolean subDept() default false;
    
    /** true: 包含部没有分配部门的数据  | ADDED: [YL]某些功能要查出没有设置部门的数据 **/
    boolean deptNull() default false;

    /**  部门ID */
    String deptId() default "dept_id";

    /**  用户ID */
    String userId() default "user_id";
}


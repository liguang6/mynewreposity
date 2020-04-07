/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.qms.common.annotation;

import java.lang.annotation.*;

/**
 * 权限数据过滤
 *
 * @author qiu.jiaming1
 * @since 3.0.0 2019-03-06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionDataFilter {
    /**  菜单key*/
    String menuKey() default "";

    /**  菜单类型: 1：菜单   2：按钮*/
    String menuType() default "1";
}


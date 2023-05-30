/**
 * Licensed to the Apache Software Foundation （ASF） under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * （the "License"）； you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * https://www.q3z3.com
 * QQ : 939313737
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.platform.modules.sms.service;

import cn.hutool.core.lang.Dict;
import com.platform.modules.sms.enums.SmsTypeEnum;
import com.platform.modules.sms.vo.SmsVo;

/**
 * <p>
 * 短信 服务层
 * </p>
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @return
     */
    Dict sendSms(SmsVo smsVo);

    /**
     * 验证短信
     *
     * @return
     */
    void verifySms(String phone, String code, SmsTypeEnum type);

}

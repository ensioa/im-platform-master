package com.platform.modules.common.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.AppConstants;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.utils.redis.RedisUtils;
import com.platform.modules.common.config.TrtcConfig;
import com.platform.modules.common.service.TrtcService;
import com.platform.modules.common.vo.TrtcVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

@Service("trtcService")
public class TrtcServiceImpl implements TrtcService {

    @Autowired
    private TrtcConfig trtcConfig;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public TrtcVo getSign() {
        String key = AppConstants.REDIS_TRTC_SIGN + ShiroUtils.getUserId();
        if (redisUtils.hasKey(key)) {
            return JSONUtil.toBean(redisUtils.get(key), TrtcVo.class);
        }
        String userId = AppConstants.REDIS_TRTC_USER + ShiroUtils.getUserId();
        long currTime = DateUtil.currentSeconds();
        Dict doc = Dict.create()
                .set("TLS.ver", "2.0")
                .set("TLS.identifier", userId)
                .set("TLS.sdkappid", trtcConfig.getAppId())
                .set("TLS.expire", trtcConfig.getExpire())
                .set("TLS.time", currTime)
                .set("TLS.sig", hmacsha256(userId, currTime));
        Deflater compressor = new Deflater();
        compressor.setInput(JSONUtil.toJsonStr(doc).getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] bytes = new byte[2048];
        int length = compressor.deflate(bytes);
        compressor.end();
        TrtcVo trtcVo = new TrtcVo().setUserId(userId)
                .setAppId(trtcConfig.getAppId())
                .setExpire(trtcConfig.getExpire())
                .setSign(base64EncodeUrl(ArrayUtil.resize(bytes, length)));
        redisUtils.set(key, JSONUtil.toJsonStr(trtcVo), 5, TimeUnit.DAYS);
        return trtcVo;
    }

    private String hmacsha256(String userId, long currTime) {
        String contentToBeSigned = "TLS.identifier:" + userId + "\n"
                + "TLS.sdkappid:" + trtcConfig.getAppId() + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + trtcConfig.getExpire() + "\n";
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, StrUtil.bytes(trtcConfig.getSecret(), StandardCharsets.UTF_8));
        byte[] signed = mac.digest(contentToBeSigned);
        return cn.hutool.core.codec.Base64.encode(signed);
    }

    private String base64EncodeUrl(byte[] input) {
        byte[] base64 = Base64.encode(input).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        return new String(base64);
    }
}

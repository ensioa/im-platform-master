package com.platform.modules.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.platform.common.constant.AppConstants;
import com.platform.common.exception.BaseException;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.utils.redis.RedisUtils;
import com.platform.common.web.service.impl.BaseServiceImpl;
import com.platform.modules.chat.dao.ChatApplyDao;
import com.platform.modules.chat.domain.ChatApply;
import com.platform.modules.chat.domain.ChatUser;
import com.platform.modules.chat.enums.ApplySourceEnum;
import com.platform.modules.chat.enums.ApplyStatusEnum;
import com.platform.modules.chat.enums.ApplyTypeEnum;
import com.platform.modules.chat.service.ChatApplyService;
import com.platform.modules.chat.service.ChatUserService;
import com.platform.modules.chat.vo.ApplyVo02;
import com.platform.modules.chat.vo.ApplyVo03;
import com.platform.modules.push.enums.PushNoticeTypeEnum;
import com.platform.modules.push.service.ChatPushService;
import com.platform.modules.push.vo.PushParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 好友申请表 服务层实现
 * q3z3
 * </p>
 */
@Service("chatApplyService")
public class ChatApplyServiceImpl extends BaseServiceImpl<ChatApply> implements ChatApplyService {

    @Resource
    private ChatApplyDao chatApplyDao;

    @Lazy
    @Resource
    private ChatUserService chatUserService;

    @Resource
    private ChatPushService chatPushService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatApplyDao);
    }

    @Override
    public List<ChatApply> queryList(ChatApply t) {
        List<ChatApply> dataList = chatApplyDao.queryList(t);
        return dataList;
    }

    @Override
    public void applyFriend(Long acceptId, ApplySourceEnum source, String reason) {
        Date now = DateUtil.date();
        Long fromId = ShiroUtils.getUserId();
        // 查询
        ChatApply query = new ChatApply()
                .setFromId(fromId)
                .setToId(acceptId)
                .setTargetId(acceptId)
                .setApplyType(ApplyTypeEnum.FRIEND)
                .setApplyStatus(ApplyStatusEnum.NONE);
        ChatApply apply = this.queryOne(query);
        query.setApplySource(source).setReason(reason).setCreateTime(now);
        if (apply == null) {
            this.add(query);
        } else {
            this.updateById(query.setId(apply.getId()));
        }
        // 给好友发送通知
        PushParamVo paramVo = new PushParamVo().setToId(acceptId);
        chatPushService.pushNotice(paramVo, PushNoticeTypeEnum.FRIEND_APPLY);
    }

    @Override
    public void applyGroup(Long acceptId) {
        Date now = DateUtil.date();
        Long fromId = ShiroUtils.getUserId();
        // 查询
        ChatApply query = new ChatApply()
                .setFromId(fromId)
                .setToId(acceptId)
                .setApplyType(ApplyTypeEnum.GROUP)
                .setApplyStatus(ApplyStatusEnum.NONE);
        ChatApply apply = this.queryOne(query);
        query.setReason("申请加入群聊")
                .setApplySource(ApplySourceEnum.SCAN)
                .setCreateTime(now);
        if (apply == null) {
            this.add(query);
        } else {
            this.updateById(query.setId(apply.getId()));
        }
        // 给群主发送通知
        PushParamVo paramVo = new PushParamVo().setToId(acceptId);
        chatPushService.pushNotice(paramVo, PushNoticeTypeEnum.FRIEND_APPLY);
    }

    @Override
    public PageInfo list() {
        Long userId = ShiroUtils.getUserId();
        // 清空角标
        redisUtils.delete(AppConstants.REDIS_FRIEND_NOTICE + userId);
        // 查询
        List<ChatApply> dataList = queryList(new ChatApply().setToId(userId));
        // 获取申请人
        List<Long> fromList = dataList.stream().map(ChatApply::getFromId).collect(Collectors.toList());
        // 集合判空
        if (CollectionUtils.isEmpty(fromList)) {
            return new PageInfo();
        }
        // 查询申请人
        HashMap<Long, ChatUser> dataMap = chatUserService.getByIds(fromList).stream().collect(HashMap::new, (x, y) -> {
            x.put(y.getUserId(), y);
        }, HashMap::putAll);
        // 转换
        List<ApplyVo02> dictList = new ArrayList<>();
        for (ChatApply apply : dataList) {
            ChatUser chatUser = ChatUser.initUser(dataMap.get(apply.getFromId()));
            ApplyVo02 applyVo = BeanUtil.toBean(apply, ApplyVo02.class)
                    .setApplyId(apply.getId())
                    .setUserId(apply.getFromId())
                    .setPortrait(chatUser.getPortrait())
                    .setNickName(chatUser.getNickName());
            dictList.add(applyVo);
        }
        return getPageInfo(dictList, dataList);
    }

    @Override
    public ApplyVo03 getInfo(Long applyId) {
        ChatApply apply = getById(applyId);
        if (apply == null) {
            throw new BaseException("申请已过期，请刷新后重试");
        }
        ChatUser chatUser = ChatUser.initUser(chatUserService.getById(apply.getFromId()));
        return BeanUtil.toBean(chatUser, ApplyVo03.class)
                .setApplyId(applyId)
                .setApplySource(apply.getApplySource())
                .setApplyStatus(apply.getApplyStatus())
                .setReason(apply.getReason());
    }

}

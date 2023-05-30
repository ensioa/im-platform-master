package com.platform.modules.collect.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.platform.common.exception.BaseException;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.web.service.impl.BaseServiceImpl;
import com.platform.modules.collect.dao.ChatCollectDao;
import com.platform.modules.collect.domain.ChatCollect;
import com.platform.modules.collect.service.ChatCollectService;
import com.platform.modules.collect.vo.CollectVo01;
import com.platform.modules.collect.vo.CollectVo02;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 收藏表 服务层实现
 * q3z3
 * </p>
 */
@Service("chatCollectService")
public class ChatCollectServiceImpl extends BaseServiceImpl<ChatCollect> implements ChatCollectService {

    @Resource
    private ChatCollectDao chatCollectDao;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatCollectDao);
    }

    @Override
    public List<ChatCollect> queryList(ChatCollect t) {
        List<ChatCollect> dataList = chatCollectDao.queryList(t);
        return dataList;
    }

    @Override
    public void addCollect(CollectVo01 collectVo) {
        ChatCollect collect = new ChatCollect()
                .setUserId(ShiroUtils.getUserId())
                .setCollectType(collectVo.getCollectType())
                .setContent(collectVo.getContent())
                .setCreateTime(DateUtil.date());
        this.add(collect);
    }

    @Override
    public void deleteCollect(Long collectId) {
        ChatCollect collect = this.getById(collectId);
        if (collect == null) {
            return;
        }
        if (!ShiroUtils.getUserId().equals(collect.getUserId())) {
            throw new BaseException("删除失败，不能删除别人的收藏");
        }
        this.deleteById(collectId);
    }

    @Override
    public PageInfo collectList(ChatCollect collect) {
        collect.setUserId(ShiroUtils.getUserId());
        List<ChatCollect> collectList = queryList(collect);
        List<CollectVo02> dataList = new ArrayList<>();
        collectList.forEach(e -> {
            dataList.add(BeanUtil.toBean(e, CollectVo02.class).setCollectId(e.getId()));
        });
        return getPageInfo(dataList, collectList);
    }

}

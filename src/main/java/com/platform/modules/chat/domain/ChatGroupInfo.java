package com.platform.modules.chat.domain;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 实体类
 * q3z3
 * </p>
 */
@Data
@TableName("chat_group_info")
@Accessors(chain = true) // 链式调用
@NoArgsConstructor
public class ChatGroupInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long infoId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 群组id
     */
    private Long groupId;
    /**
     * 是否置顶
     */
    private YesOrNoEnum top;
    /**
     * 是否免打扰
     */
    private YesOrNoEnum disturb;
    /**
     * 是否保存群组
     */
    private YesOrNoEnum keepGroup;
    /**
     * 是否被踢
     */
    private YesOrNoEnum kicked;
    /**
     * 加入时间
     */
    private Date createTime;

    public ChatGroupInfo(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
        this.createTime = DateUtil.date();
        this.top = YesOrNoEnum.NO;
        this.disturb = YesOrNoEnum.NO;
        this.keepGroup = YesOrNoEnum.NO;
        this.kicked = YesOrNoEnum.NO;
    }
}

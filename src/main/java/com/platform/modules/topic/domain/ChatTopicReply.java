package com.platform.modules.topic.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.web.domain.BaseEntity;
import com.platform.modules.topic.enums.TopicReplyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 帖子回复表实体类
 * q3z3
 * </p>
 */
@Data
@TableName("chat_topic_reply")
@Accessors(chain = true) // 链式调用
public class ChatTopicReply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long replyId;
    /**
     * 回复类型1帖子2用户
     */
    private TopicReplyTypeEnum replyType;
    /**
     * 回复状态
     */
    private YesOrNoEnum replyStatus;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 帖子id
     */
    private Long topicId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 目标id
     */
    private Long targetId;
    /**
     * 回复时间
     */
    private Date createTime;

}

package com.tgf.exhibition.http;

/**
 * Created by jeff on 2016/5/19.
 */
public class RequestParamKeys {
    public static final String PARAM_KEY_START_TIME = "start_time";
    public static final String PARAM_KEY_END_TIME = "end_time";

    public static final String PARAM_KEY_REQ_FROM = "request_from";
    public static final String PARAM_KEY_TEL = "tel";
    public static final String PARAM_KEY_VERIFY_CODE = "code";

    public static final String PARAM_KEY_FILE = "file";
    public static final String PARAM_KEY_USER_TOKEN = "user_token";
    public static final String PARAM_KEY_USER_NICK_NAME = "nickname";
    public static final String PARAM_KEY_USER_SEX = "sex";
    public static final String PARAM_KEY_USER_AVATAR = "avatar";

    public static final String PARAM_KEY_SCENE_ID = "scene_id";

    public static final String PARAM_KEY_ACTION_NAME = "type";
    /**
     * 申报类型ID
     */
    public static final String PARAM_KEY_DECLARATION_TYPE_ID = "type_id";
    /**
     * 作业对象ID: (区域 + 作业对象类型 = 具体的作业对象)
     */
    public static final String PARAM_KEY_WOBJ_ID = "object_id";

    /**
     * 检查项ID: (不同的 作业对象类型 对应不同的 检查项列表)
     */
    public static final String PARAM_KEY_CHECK_ITEM_ID = "item_id";

    /**
     * 订单中购买的数量
     */
    public static final String PARAM_KEY_ORDER_PAY_FOR_NUM = "num";

    /**
     * 订单编号
     */
    public static final String PARAM_KEY_ORDER_NUM = "order_num";

    /**
     * 订单状态:
     * pending 为 tab - 待付款  文字为“待付款”
     * starCount   为 tab - 待服务  文字为 付款成功
     * finish  为 tab - 待确认  文字为 等待确认
     * signin  为 tab - 已完成  文字为 交易成功
     */
    public static final String PARAM_KEY_ORDER_STATUS = "task_status";

    /**
     * 服务星级
     */
    public static final String PARAM_KEY_SERVICE_STAR_LEVEL = "star";

    /**
     * 服务评论
     */
    public static final String PARAM_KEY_SERVICE_COMMENTS = "comment";

    /**
     * 服务完成凭证留言
     */
    public static final String PARAM_KEY_SERVICE_MESSAGE = "service_message";

    /**
     * 服务现场信息(服务人员提交)
     */
    public static final String PARAM_KEY_SERVICE_INFO = "service_message";

    /**
     * 服务现场图片(服务人员提交)
     */
    public static final String PARAM_KEY_SERVICE_FILE = "service_file";

    /**
     * 支付方式：
     * prepay 预付款相关;
     * wechat  微信付款
     */
    public static final String PARAM_KEY_ORDER_PAY_FOR_TYPE = "type";
}

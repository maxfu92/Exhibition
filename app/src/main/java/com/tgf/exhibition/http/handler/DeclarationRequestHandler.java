package com.tgf.exhibition.http.handler;

import android.app.Activity;

import com.tgf.exhibition.http.IRequestUrl;

/**
 * Created by jeff on 2016/5/19.
 */
public class DeclarationRequestHandler extends UserCenterRequestHandler {
    public enum DeclarationURL implements IRequestUrl {
        /** 申报-类型列表(参与人) */
        APPLY_TYPE("/applyType"),
        /** 申报-新消息(参与人) */
        APPLY_NEW_ORDER_MSG("/newMessage"),
        APPLY_CLEAR_MAN_PAGE_MSG("/clearMessage"),
        /** 申报-获得作业对象 */
        APPLY_WORK_OBJ("/getObject"),
        /** 申报-获得申报科目 */
        APPLY_ITEM("/getItem"),
        /** 申报-创建订单 */
        APPLY_CREATE_ORDER("/createOrder"),
        /** 申报-获得余额 */
        APPLY_REMAIND_MONEY("/getObjectMoney"),
        /** 申报-预存款支付 */
        APPLY_PAY_PY_PREPAID("/usePrepayMoney"),

        /** 申报-订单列表(参与人) */
        ORDER_LIST("/orderList"),
        /** 申报-确认收货(参与人) */
        ORDER_CONFIRM_SERVICE("/orderSign"),

        /** 申报-订单列表(服务执行人) */
        SERVICE_ORDER_LIST("/staffOrderList"),
        /** 申报-确认服务完成(服务执行人)  */
        SERVICE_ORDER_FINISHED("/orderFinish"),

        /** 申报-取消订单 */
        ORDER_CANCEL("/orderCancel"),
        /** 申报-取消订单 */
        ORDER_DELETE("/orderDelete"),
        /** 申报-评论接口 */
        ORDER_PUSH_COMMENT("/comment"),
        /** 申报详情 */
        ORDER_DETAIL("/orderDetail"),

        /** 申报-预付款记录 */
        ORDER_PREPAY_LOG("/payLog"),;

        private final String PATH;
        DeclarationURL(String path) {
            PATH = XCAPPLY_URL_PATH + path;
        }

        @Override
        public String getStringUrl() {
            return PATH;
        }
    }

    public DeclarationRequestHandler(Activity activity) {
        super(activity);
    }

    public DeclarationRequestHandler setActionName(String actionName) {
        return putParam(PARAM_KEY_ACTION_NAME, actionName);
    }

    public DeclarationRequestHandler setDeclarationTypeId(String declarationTypeId) {
        return putParam(PARAM_KEY_DECLARATION_TYPE_ID, declarationTypeId);
    }

    public DeclarationRequestHandler setWorkObjectId(String workObjectId) {
        return putParam(PARAM_KEY_WOBJ_ID, workObjectId);
    }

    public DeclarationRequestHandler setCheckItemId(String checkItemId) {
        return putParam(PARAM_KEY_CHECK_ITEM_ID, checkItemId);
    }

    public DeclarationRequestHandler setPayForNumber(String payForNumber) {
        return putParam(PARAM_KEY_ORDER_PAY_FOR_NUM, payForNumber);
    }

    public DeclarationRequestHandler setStartTime(String startTime) {
        return putParam(PARAM_KEY_START_TIME, startTime);
    }
    public DeclarationRequestHandler setEndTime(String endTime) {
        return putParam(PARAM_KEY_END_TIME, endTime);
    }

    public DeclarationRequestHandler setOrderNumber(String orderNumber) {
        return putParam(PARAM_KEY_ORDER_NUM, orderNumber);
    }

    public DeclarationRequestHandler setOrderPayForType(String orderPayForType) {
        return putParam(PARAM_KEY_ORDER_PAY_FOR_TYPE, orderPayForType);
    }

    public DeclarationRequestHandler setOrderStatus(String orderStatus) {
        return putParam(PARAM_KEY_ORDER_STATUS, orderStatus);
    }

    public DeclarationRequestHandler setServiceStarLevel(String serviceStarLevel) {
        return putParam(PARAM_KEY_SERVICE_STAR_LEVEL, serviceStarLevel);
    }

    public DeclarationRequestHandler setServiceComments(String serviceComments) {
        return putParam(PARAM_KEY_SERVICE_COMMENTS, serviceComments);
    }

    public DeclarationRequestHandler setServiceMessage(String serviceMessage) {
        return putParam(PARAM_KEY_SERVICE_MESSAGE, serviceMessage);
    }

    public DeclarationRequestHandler setServiceInfo(String serviceInfo) {
        return putParam(PARAM_KEY_SERVICE_INFO, serviceInfo);
    }

    public DeclarationRequestHandler setServicePictureUrl(String servicePictureUrl) {
        return putParam(PARAM_KEY_SERVICE_FILE, servicePictureUrl);
    }
}

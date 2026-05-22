package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.SystemMessage;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.so.user.QueryMessageAdminSO;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.MessageService;
import com.example.core.mainbody.so.user.QueryMessageSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统消息Controller
 *
 */
@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    /** 查询未读消息数 */
    @GetMapping(value = "/unread/count", produces = {"application/json"})
    public ResultMessage queryUnreadCount() {
        UserInfo userInfo = this.getLoginUser();
        return messageService.queryUnreadCount(userInfo.getUserId());
    }

    /** 查询消息列表（分页） */
    @PostMapping(value = "/list", produces = {"application/json"})
    public ResultMessage queryMessageList(@RequestBody QueryMessageSO so) {
        UserInfo userInfo = this.getLoginUser();
        return messageService.queryMessageList(userInfo.getUserId(), so);
    }

    /** 标记消息已读 */
    @PostMapping(value = "/read", produces = {"application/json"})
    public ResultMessage markAsRead(@RequestParam Integer messageId) {
        UserInfo userInfo = this.getLoginUser();
        return messageService.markAsRead(userInfo.getUserId(), messageId);
    }

    /** 查询消息列表 */
    @PostMapping(value = "/admin/list", produces = {"application/json"})
    public ResultMessage queryMessageListForAdmin(@RequestBody QueryMessageAdminSO so) {
        return messageService.queryMessageListForAdmin(so);
    }

    /** 新增单条消息 */
    @PostMapping(value = "/admin/add", produces = {"application/json"})
    public ResultMessage addMessage(@RequestBody SystemMessage message) {
        return messageService.addMessage(message);
    }

    /** 删除消息 */
    @PostMapping(value = "/admin/delete", produces = {"application/json"})
    public ResultMessage deleteMessage(@RequestParam Integer id) {
        return messageService.deleteMessage(id);
    }

    /** 全员推送公告 */
    @PostMapping(value = "/admin/broadcast", produces = {"application/json"})
    public ResultMessage broadcastMessage(@RequestBody SystemMessage message) {
        return messageService.broadcastMessage(message);
    }
}

package com.chat.pk.DTO;

import com.chat.pk.Message;

import java.util.List;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class ChatDTO {
    String message="",success="",msg="";
    List<Message> chatHistory=null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Message> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<Message> chatHistory) {
        this.chatHistory = chatHistory;
    }
}

package com.chat.pk.DTO;

import java.util.Date;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class MatchesDTO {
	
	///For Simple Chat
	
	String success, msg, UserId, UserName, UserGender, UserDob, UserImageName,
			UserLoginStatus, UserProfileThumbnail, UserLastMessage = "",
			time = "",UserLastMessageType="",selectStatus="0",userType="";
	Date date = null;
	int Count = 0;

	//For Group Chat
	String groupId = "", groupAdminName = "", groupName = "",
			groupAdminId = "";
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSelectStatus() {
		return selectStatus;
	}

	public void setSelectStatus(String selectStatus) {
		this.selectStatus = selectStatus;
	}

	public String getUserLastMessageType() {
		return UserLastMessageType;
	}

	public void setUserLastMessageType(String userLastMessageType) {
		UserLastMessageType = userLastMessageType;
	}

	

	public String getUserLastMessage() {
		return UserLastMessage;
	}

	public void setUserLastMessage(String userLastMessage) {
		UserLastMessage = userLastMessage;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getCount() {
		return Count;
	}

	public void setCount(int count) {
		Count = count;
	}

	public String getUserProfileThumbnail() {
		return UserProfileThumbnail;
	}

	public void setUserProfileThumbnail(String userProfileThumbnail) {
		UserProfileThumbnail = userProfileThumbnail;
	}

	public String getUserLoginStatus() {
		return UserLoginStatus;
	}

	public void setUserLoginStatus(String userLoginStatus) {
		UserLoginStatus = userLoginStatus;
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

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserGender() {
		return UserGender;
	}

	public void setUserGender(String userGender) {
		UserGender = userGender;
	}

	public String getUserDob() {
		return UserDob;
	}

	public void setUserDob(String userDob) {
		UserDob = userDob;
	}

	public String getUserImageName() {
		return UserImageName;
	}

	public void setUserImageName(String userImageName) {
		UserImageName = userImageName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupAdminName() {
		return groupAdminName;
	}

	public void setGroupAdminName(String groupAdminName) {
		this.groupAdminName = groupAdminName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupAdminId() {
		return groupAdminId;
	}

	public void setGroupAdminId(String groupAdminId) {
		this.groupAdminId = groupAdminId;
	}


}

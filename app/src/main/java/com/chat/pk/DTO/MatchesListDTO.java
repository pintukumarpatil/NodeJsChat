package com.chat.pk.DTO;

import java.util.List;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class MatchesListDTO {
	String UserName = "", UserId = "", msg = "", success = "";
	List<MatchesDTO> matchesDTOs = null;

	
	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public List<MatchesDTO> getMatchesDTOs() {
		return matchesDTOs;
	}

	public void setMatchesDTOs(List<MatchesDTO> matchesDTOs) {
		this.matchesDTOs = matchesDTOs;
	}

}

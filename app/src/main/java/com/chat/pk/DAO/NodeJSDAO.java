package com.chat.pk.DAO;

import android.content.Context;

import com.chat.pk.AppSession;
import com.chat.pk.DTO.ChatDTO;
import com.chat.pk.DTO.GroupDTO;
import com.chat.pk.Message;
import com.chat.pk.Util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class NodeJSDAO {
	Context mContext;
	AppSession appSession = null;

	public NodeJSDAO(Context mContext) {
		this.mContext = mContext;
		appSession = new AppSession(mContext);
	}
	public GroupDTO parseGroupList(String json) {

		GroupDTO listDTO=new GroupDTO();
		ArrayList<GroupDTO> matchesDTOs=new ArrayList<GroupDTO>();
		try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has("success"))
					listDTO.setSuccess(jsonObject.getString("success"));
				if (jsonObject.has("msg"))
					listDTO.setMessage(jsonObject.getString("msg"));

				if (jsonObject.has("result")) {
					JSONArray jsonArray=jsonObject.getJSONArray("result");
					for (int i = 0; i < jsonArray.length(); i++) {
						GroupDTO dto=new GroupDTO();
						JSONObject jsonObject2=jsonArray.getJSONObject(i);
						if (jsonObject2.has("group_id")) {
							dto.setGroupId(jsonObject2.getString("group_id"));
						}if (jsonObject2.has("group_name")) {
							dto.setGroupName(jsonObject2.getString("group_name"));
						}if (jsonObject2.has("owner_id")) {
							dto.setOwnerId(jsonObject2.getString("owner_id"));
						}if (jsonObject2.has("user_id")) {
							dto.setUserId(jsonObject2.getString("user_id"));
						}if (jsonObject2.has("image")) {
							dto.setImage(jsonObject2.getString("image"));
						}
						matchesDTOs.add(dto);
					}
					listDTO.setDtos(matchesDTOs);
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return listDTO;
	}
	public GroupDTO parseFriendList(String json) {

		GroupDTO listDTO=new GroupDTO();
		ArrayList<GroupDTO> matchesDTOs=new ArrayList<GroupDTO>();
		try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has("success"))
					listDTO.setSuccess(jsonObject.getString("success"));
				if (jsonObject.has("msg"))
					listDTO.setMessage(jsonObject.getString("msg"));

				if (jsonObject.has("result")) {
					JSONArray jsonArray=jsonObject.getJSONArray("result");
					for (int i = 0; i < jsonArray.length(); i++) {
						GroupDTO dto=new GroupDTO();
						JSONObject jsonObject2=jsonArray.getJSONObject(i);
						if (jsonObject2.has("user_name")) {
							dto.setUserName(jsonObject2.getString("user_name"));
						}if (jsonObject2.has("user_id")) {
							dto.setUserId(jsonObject2.getString("user_id"));
						}if (jsonObject2.has("profile_image")) {
							dto.setImage(jsonObject2.getString("profile_image"));
						}
						matchesDTOs.add(dto);
					}
					listDTO.setDtos(matchesDTOs);
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return listDTO;
	}
	public Message parseGroupMessages(String json) {
		//toId,toName,fromId,fromName,message,messageType,formate
		Message message=new Message();
		try {
			try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has(Constants.PN_TO_ID))
					message.setToId(jsonObject.getString(Constants.PN_TO_ID));
				if (jsonObject.has(Constants.PN_TO_NAME))
					message.setToName(jsonObject.getString(Constants.PN_TO_NAME));
				if (jsonObject.has(Constants.PN_FROM_ID))
					message.setFromId(jsonObject.getString(Constants.PN_FROM_ID));
				if (jsonObject.has(Constants.PN_FROM_NAME))
					message.setFromName(jsonObject.getString(Constants.PN_FROM_NAME));
				if (jsonObject.has(Constants.PN_MESSAGE))
					message.setMessage(jsonObject.getString(Constants.PN_MESSAGE));
				if (jsonObject.has(Constants.PN_USER_ID))
					message.setUserId(jsonObject.getString(Constants.PN_USER_ID));
				if (jsonObject.has(Constants.PN_USER_NAME))
					message.setUserName(jsonObject.getString(Constants.PN_USER_NAME));
				if (jsonObject.has(Constants.PN_GROUP_ID))
					message.setGroupId(jsonObject.getString(Constants.PN_GROUP_ID));
				if (jsonObject.has(Constants.PN_GROUP_NAME))
					message.setGroupName(jsonObject.getString(Constants.PN_GROUP_NAME));
				if (jsonObject.has(Constants.PN_CURRENT_TIME))
					message.setTime(jsonObject.getString(Constants.PN_CURRENT_TIME));
				if (jsonObject.has(Constants.PN_MESSAGE_TYPE))
					message.setMessageType(jsonObject.getString(Constants.PN_MESSAGE_TYPE));
				if (jsonObject.has(Constants.PN_FILE_NAME))
					message.setFileName(jsonObject.getString(Constants.PN_FILE_NAME));
			} else {
				return null;
			}
			}
			catch (OutOfMemoryError e) {
				// TODO: handle exception
				e.printStackTrace();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	public Message parsePrivateMessages(String json) {
		//toId,toName,fromId,fromName,message,messageType,formate
		Message message=new Message();
		try {
			try {
				Object object = new JSONTokener(json).nextValue();
				if (object instanceof JSONObject) {
					JSONObject jsonObject = new JSONObject(json);
					if (jsonObject.has(Constants.PN_TO_ID))
						message.setUserId(jsonObject.getString(Constants.PN_TO_ID));
					if (jsonObject.has(Constants.PN_TO_NAME))
						message.setUserName(jsonObject.getString(Constants.PN_TO_NAME));

					if (jsonObject.has(Constants.PN_FROM_ID))
						message.setFriendId(jsonObject.getString(Constants.PN_FROM_ID));
					if (jsonObject.has(Constants.PN_FROM_NAME))
						message.setFriendName(jsonObject.getString(Constants.PN_FROM_NAME));

					if (jsonObject.has(Constants.PN_MESSAGE))
						message.setMessage(jsonObject.getString(Constants.PN_MESSAGE));
					if (jsonObject.has(Constants.PN_CURRENT_TIME))
						message.setTime(jsonObject.getString(Constants.PN_CURRENT_TIME));
					if (jsonObject.has(Constants.PN_MESSAGE_TYPE))
						message.setMessageType(jsonObject.getString(Constants.PN_MESSAGE_TYPE));
					if (jsonObject.has(Constants.PN_FILE_NAME))
						message.setFileName(jsonObject.getString(Constants.PN_FILE_NAME));
				} else {
					return null;
				}
			}
			catch (OutOfMemoryError e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	public String parseSuccess(String json) {

		String response = null;
		try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has("success"))
					response = jsonObject.getString("success");
				return response;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	public ChatDTO parseGroupInfo(String json) {

		ChatDTO  chatDTO=new ChatDTO();
		try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has("success"))
					chatDTO.setSuccess(jsonObject.getString("success"));
				if (jsonObject.has("msg"))
					chatDTO.setMsg(jsonObject.getString("msg"));
				if (jsonObject.has("message"))
					chatDTO.setMessage(jsonObject.getString("message"));
				if (jsonObject.has("chatHistory"))
				{
					JSONArray jsonArray=jsonObject.getJSONArray("chatHistory");
					List<Message> chatHistory=new ArrayList<>();
					for (int i=0;i<jsonArray.length();i++){
						Message message=new Message();
						JSONObject jsonObject1=jsonArray.getJSONObject(i);
						if (jsonObject1.has("message"))
							message.setMessage(jsonObject1.getString("message"));
						if (jsonObject1.has("date_time"))
							message.setTime(jsonObject1.getString("date_time"));
						if (jsonObject1.has("msg_type"))
							message.setMessageType(jsonObject1.getString("msg_type"));
						if (jsonObject1.has("sender_id")){
							if (jsonObject1.getString("sender_id").equals(appSession.getUserId())){
								message.setUserType(Constants.USER_TYPE_ME);
								if (jsonObject1.has("sender_name"))
									message.setUserName(jsonObject1.getString("sender_name"));
							}else {
								message.setUserType(Constants.USER_TYPE_OTHER);
								if (jsonObject1.has("sender_name"))
									message.setFromName(jsonObject1.getString("sender_name"));
							}
						}
						chatHistory.add(message);
					}
					chatDTO.setChatHistory(chatHistory);
				}
				return chatDTO;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return chatDTO;
	}
	public ChatDTO parseUserInfo(String json) {

		ChatDTO  chatDTO=new ChatDTO();
		try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has("success"))
					chatDTO.setSuccess(jsonObject.getString("success"));
				if (jsonObject.has("msg"))
					chatDTO.setMsg(jsonObject.getString("msg"));
				if (jsonObject.has("online_status"))
					chatDTO.setMessage(jsonObject.getString("online_status"));
				if (jsonObject.has("chatHistory"))
				{
					JSONArray jsonArray=jsonObject.getJSONArray("chatHistory");
					List<Message> chatHistory=new ArrayList<>();
					for (int i=0;i<jsonArray.length();i++){
						Message message=new Message();
						JSONObject jsonObject1=jsonArray.getJSONObject(i);
						if (jsonObject1.has("message"))
							message.setMessage(jsonObject1.getString("message"));
						if (jsonObject1.has("date_time"))
							message.setTime(jsonObject1.getString("date_time"));
						if (jsonObject1.has("msg_type"))
							message.setMessageType(jsonObject1.getString("msg_type"));
						if (jsonObject1.has("sender_id")){
							if (jsonObject1.getString("sender_id").equals(appSession.getUserId())){
								message.setUserType(Constants.USER_TYPE_ME);
								if (jsonObject1.has("sender_name"))
									message.setUserName(jsonObject1.getString("sender_name"));
							}else {
								message.setUserType(Constants.USER_TYPE_OTHER);
								if (jsonObject1.has("sender_name"))
									message.setFromName(jsonObject1.getString("sender_name"));
							}
						}
						chatHistory.add(message);
					}
					chatDTO.setChatHistory(chatHistory);
				}
				return chatDTO;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return chatDTO;
	}
	public String[] parseUploadResponse(String json) {

		String[] response = new String[3];
		try {
			Object object = new JSONTokener(json).nextValue();
			if (object instanceof JSONObject) {
				JSONObject jsonObject = new JSONObject(json);
				if (jsonObject.has("success"))
					response[0] = jsonObject.getString("success");
				if (jsonObject.has("file_name"))
					response[1] = jsonObject.getString("file_name");
				if (jsonObject.has("file_size"))
					response[2] = jsonObject.getString("file_size");
				return response;
			} else {

				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}

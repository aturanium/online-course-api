package com.onlinecourse.service;

import com.onlinecourse.dto.response.ChatRoomResponse;
import com.onlinecourse.dto.response.UserSearchResponse;
import java.util.List;

public interface ChatService {

    public List<UserSearchResponse> searchUsersByEmail(String emailQuery);

    public ChatRoomResponse createOrGetRoom(String currentUserEmail, Integer targetUserId);

    public List<ChatRoomResponse> getMyRooms(String currentUserEmail);

    public void sendMessage(String currentUserEmail, Integer roomId, String content);

    public void deleteRoom(String currentUserEmail, Integer roomId);
}

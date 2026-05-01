package com.onlinecourse.repository;

import com.onlinecourse.pojo.ChatRoom;
import java.util.List;

public interface ChatRoomRepository {

    void save(ChatRoom chatRoom);

    void delete(ChatRoom chatRoom);

    ChatRoom findById(Integer id);

    ChatRoom findByUsers(Integer userId1, Integer userId2);

    List<ChatRoom> findByUserId(Integer userId);
}

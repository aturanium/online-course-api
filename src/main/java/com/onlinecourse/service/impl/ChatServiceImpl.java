package com.onlinecourse.service.impl;

import com.onlinecourse.dto.response.*;
import com.onlinecourse.pojo.*;
import com.onlinecourse.repository.*;
import com.onlinecourse.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FirebaseService firebaseService;

    @Override
    public List<UserSearchResponse> searchUsersByEmail(String emailQuery) {
        return userRepository.searchByEmailContaining(emailQuery).stream().map(u -> {
            UserSearchResponse res = new UserSearchResponse();
            res.setId(u.getId());
            res.setEmail(u.getEmail());
            res.setFullName(u.getFirstName() + " " + u.getLastName());
            res.setAvatar(u.getAvatar());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public ChatRoomResponse createOrGetRoom(String currentUserEmail, Integer targetUserId) {
        User currentUser = userRepository.findByEmail(currentUserEmail);
        User targetUser = userRepository.findById(targetUserId);
        if (targetUser == null) {
            throw new RuntimeException("Người dùng không tồn tại!");
        }

        ChatRoom room = chatRoomRepository.findByUsers(currentUser.getId(), targetUserId);

        if (room == null) {
            room = new ChatRoom();
            room.setUser1(currentUser);
            room.setUser2(targetUser);
            room.setLastMessage("");
            chatRoomRepository.save(room);
        }

        return mapToResponse(room, currentUser.getId());
    }

    @Override
    public List<ChatRoomResponse> getMyRooms(String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail);
        return chatRoomRepository.findByUserId(currentUser.getId())
                .stream().map(r -> mapToResponse(r, currentUser.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void sendMessage(String currentUserEmail, Integer roomId, String content) {
        User sender = userRepository.findByEmail(currentUserEmail);
        ChatRoom room = chatRoomRepository.findById(roomId);

        if (room == null) {
            throw new RuntimeException("Phòng chat không tồn tại!");
        }
        if (!room.getUser1().getId().equals(sender.getId()) && !room.getUser2().getId().equals(sender.getId())) {
            throw new RuntimeException("Bạn không thuộc phòng chat này!");
        }

        room.setLastMessage(content);
        room.setDeletedByUser1(false);
        room.setDeletedByUser2(false);
        chatRoomRepository.save(room);

        String fullName = sender.getFirstName() + " " + sender.getLastName();
        firebaseService.pushMessageToFirebase(roomId, sender.getId(), fullName, sender.getAvatar(), content);
    }

    @Override
    public void deleteRoom(String currentUserEmail, Integer roomId) {
        User user = userRepository.findByEmail(currentUserEmail);
        ChatRoom room = chatRoomRepository.findById(roomId);

        if (room != null) {

            if (room.getUser1().getId().equals(user.getId())) {
                room.setDeletedByUser1(true);
            } else if (room.getUser2().getId().equals(user.getId())) {
                room.setDeletedByUser2(true);
            }

            if (room.getDeletedByUser1() && room.getDeletedByUser2()) {
                chatRoomRepository.delete(room);
            } else {
                chatRoomRepository.save(room);
            }
        }
    }

    private ChatRoomResponse mapToResponse(ChatRoom room, Integer currentUserId) {
        User targetUser = room.getUser1().getId().equals(currentUserId) ? room.getUser2() : room.getUser1();

        ChatRoomResponse res = new ChatRoomResponse();
        res.setRoomId(room.getId());
        res.setTargetUserEmail(targetUser.getEmail());
        res.setTargetUserFullName(targetUser.getFirstName() + " " + targetUser.getLastName());
        res.setTargetUserAvatar(targetUser.getAvatar());
        res.setLastMessage(room.getLastMessage());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (room.getUpdatedAt() != null) {
            res.setUpdatedAt(room.getUpdatedAt().format(formatter));
        }

        return res;
    }
}

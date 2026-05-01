package com.onlinecourse.controller;

import com.onlinecourse.dto.request.MessageRequest;
import com.onlinecourse.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatService chatService;

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam("email") String email) {
        return ResponseEntity.ok(chatService.searchUsersByEmail(email));
    }

    @GetMapping("/chat-room")
    public ResponseEntity<?> getMyRooms() {
        return ResponseEntity.ok(chatService.getMyRooms(getCurrentEmail()));
    }

    @PostMapping("/chat-room/{targetUserId}")
    public ResponseEntity<?> createOrGetRoom(@PathVariable("targetUserId") Integer targetUserId) {
        return ResponseEntity.ok(chatService.createOrGetRoom(getCurrentEmail(), targetUserId));
    }

    @PostMapping("/chat-room/{roomId}/message")
    public ResponseEntity<?> sendMessage(@PathVariable("roomId") Integer roomId, @RequestBody MessageRequest request) {
        chatService.sendMessage(getCurrentEmail(), roomId, request.getContent());
        return ResponseEntity.ok("Đã gửi tin nhắn!");
    }

    @DeleteMapping("/chat-room/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable("roomId") Integer roomId) {
        chatService.deleteRoom(getCurrentEmail(), roomId);
        return ResponseEntity.ok("Đã xóa phòng chat!");
    }
}

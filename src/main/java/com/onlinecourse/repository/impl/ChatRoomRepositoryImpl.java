package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.ChatRoom;
import com.onlinecourse.repository.ChatRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(ChatRoom chatRoom) {
        if (chatRoom.getId() == null) {
            entityManager.persist(chatRoom);
        } else {
            entityManager.merge(chatRoom);
        }
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        entityManager.remove(entityManager.contains(chatRoom) ? chatRoom : entityManager.merge(chatRoom));
    }

    @Override
    public ChatRoom findById(Integer id) {
        return entityManager.find(ChatRoom.class, id);
    }

    @Override
    public ChatRoom findByUsers(Integer u1, Integer u2) {
        try {
            return entityManager.createQuery(
                    "SELECT c FROM ChatRoom c WHERE (c.user1.id = :u1 AND c.user2.id = :u2) OR (c.user1.id = :u2 AND c.user2.id = :u1)", ChatRoom.class)
                    .setParameter("u1", u1)
                    .setParameter("u2", u2)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ChatRoom> findByUserId(Integer userId) {
        return entityManager.createQuery(
                "SELECT c FROM ChatRoom c WHERE "
                + "(c.user1.id = :userId AND c.deletedByUser1 = false) OR "
                + "(c.user2.id = :userId AND c.deletedByUser2 = false) "
                + "ORDER BY c.updatedAt DESC", ChatRoom.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}

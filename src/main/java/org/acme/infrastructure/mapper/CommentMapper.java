package org.acme.infrastructure.mapper;

import org.acme.domain.models.Comment;
import org.acme.infrastructure.entities.CommentEntity;

public class CommentMapper {

    public static Comment toDomain(CommentEntity entity) {
        if (entity == null) return null;
        return new Comment(
                entity.getId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getAuthorEmail()
        );
    }
}

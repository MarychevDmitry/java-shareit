package ru.practicum.shareit.comment.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment fromComment(CommentDto commentDto, Item item, User user, LocalDateTime dateTime) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(dateTime)
                .item(item)
                .author(user)
                .build();
    }

    public static List<CommentDto> toCommentDtoList(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(toCommentDto(comment));
        }
        return result;
    }
}

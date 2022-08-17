package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.entity.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }
}

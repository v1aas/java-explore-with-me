package ru.practicum.comment.mapper;

import ru.practicum.comment.model.Commentary;
import ru.practicum.comment.model.CommentaryDto;

import java.util.ArrayList;
import java.util.List;

public class CommentaryMapper {

    public static CommentaryDto toCommentaryDto(Commentary comment) {
        return new CommentaryDto(comment.getId(), comment.getAuthor(), comment.getTitle(), comment.getText(),
                comment.getCreated());
    }

    public static Commentary toCommentary(CommentaryDto comment) {
        return new Commentary(comment.getId(), comment.getAuthor(), comment.getTitle(), comment.getText(),
                comment.getCreated());
    }

    public static List<CommentaryDto> toCommentaryDtoList(List<Commentary> comments) {
        List<CommentaryDto> commentaryDtoList = new ArrayList<>();
        for (Commentary comment : comments) {
            commentaryDtoList.add(toCommentaryDto(comment));
        }
        return commentaryDtoList;
    }
}
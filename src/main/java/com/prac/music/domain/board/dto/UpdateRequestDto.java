package com.prac.music.domain.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {
    private String title;

    private String contents;

    @Builder
    public UpdateRequestDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}

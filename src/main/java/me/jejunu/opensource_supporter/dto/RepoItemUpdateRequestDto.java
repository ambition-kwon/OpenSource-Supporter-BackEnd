package me.jejunu.opensource_supporter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepoItemUpdateRequestDto {
    private Long repoId;
    private String description;
    private List<String> tags;
}

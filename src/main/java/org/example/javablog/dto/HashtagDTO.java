package org.example.javablog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashtagDTO {
    private Long id;
    private String name;
    private Long postCount;
    public HashtagDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

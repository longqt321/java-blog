package org.example.javablog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRelationshipDTO {
    private boolean liked;
    private boolean saved;
    private boolean hidden;
    private boolean reported;

}

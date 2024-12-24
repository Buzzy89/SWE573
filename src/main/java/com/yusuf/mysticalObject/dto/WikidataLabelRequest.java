package com.yusuf.mysticalObject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikidataLabelRequest {
    private String qid;
    private String title;
    private String description;
}
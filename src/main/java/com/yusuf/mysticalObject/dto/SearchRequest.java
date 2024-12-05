package com.yusuf.mysticalObject.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchRequest {
    private List<String> tags;
}

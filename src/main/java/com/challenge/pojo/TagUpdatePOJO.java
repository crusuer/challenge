package com.challenge.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagUpdatePOJO {
    private String oldTag;
    private String newTag;
}

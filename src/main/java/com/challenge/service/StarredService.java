package com.challenge.service;

import com.challenge.entity.Starred;
import com.challenge.pojo.TagUpdatePOJO;
import com.challenge.pojo.TagsPOJO;

import java.util.List;

public interface StarredService {
    List<Starred> getStarred(String username);

    Starred addTag(Long id, TagsPOJO tagsPOJO);

    Starred updateTag(Long id, TagUpdatePOJO tagUpdatePOJO);

    Starred deleteTag(Long id, TagsPOJO tagsPOJO);

    List<Starred> searchTag(String tag);

    List<String> recommendTag(Long id);
}

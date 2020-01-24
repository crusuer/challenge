package com.challenge.pojo;

import com.challenge.entity.Starred;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoriesPOJO {
    @NonNull
    private List<Starred> starredRepositories;

    public RepositoriesPOJO(Starred starred) {
        starredRepositories = new ArrayList<>(Arrays.asList(starred));
    }
}

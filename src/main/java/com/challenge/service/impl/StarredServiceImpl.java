package com.challenge.service.impl;

import com.challenge.entity.Starred;
import com.challenge.exception.StarredNotFoundException;
import com.challenge.exception.UserNotFoundException;
import com.challenge.pojo.TagUpdatePOJO;
import com.challenge.pojo.TagsPOJO;
import com.challenge.repository.StarredRepository;
import com.challenge.service.StarredService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StarredServiceImpl implements StarredService {
    private static final Logger LOGGER = LogManager.getLogger(StarredServiceImpl.class);

    @Autowired
    private StarredRepository starredRepository;

    @Override
    public List<Starred> getStarred(String username) {
        saveRetrievedRepositories(username);
        return starredRepository.findAll();
    }

    @Override
    public Starred addTag(Long id, TagsPOJO tagsPOJO) {

        Starred starred = starredRepository.findById(id).orElseThrow(() -> new StarredNotFoundException(id));

        Set<String> existentTags = starred.getTags();
        tagsPOJO.getTags().forEach(tag -> existentTags.add(tag.toLowerCase().trim()));

        return starredRepository.save(starred);
    }

    @Override
    public Starred updateTag(Long id, TagUpdatePOJO tagUpdatePOJO) {

        Starred starred = starredRepository.findById(id).orElseThrow(() -> new StarredNotFoundException(id));

        Set<String> existentTags = starred.getTags();
        existentTags.remove(tagUpdatePOJO.getOldTag());
        existentTags.add(tagUpdatePOJO.getNewTag());

        return starredRepository.save(starred);
    }

    @Override
    public Starred deleteTag(Long id, TagsPOJO tagsPOJO) {

        Starred starred = starredRepository.findById(id).orElseThrow(() -> new StarredNotFoundException(id));

        Set<String> existentTags = starred.getTags();
        tagsPOJO.getTags().forEach(existentTags::remove);

        return starredRepository.save(starred);

    }

    @Override
    public List<Starred> searchTag(String tag) {
        List<Starred> starredRepos = starredRepository.findAll();
        for (Starred starred : starredRepos) {
            if (searchTagsByTag(starred.getTags(), tag)) {
                continue;
            }
            starredRepos.remove(starred);
        }
        return starredRepos;
    }

    @Override
    public List<String> recommendTag(Long id) {
        Starred starred = starredRepository.findById(id).orElseThrow(() -> new StarredNotFoundException(id));
        //read list of most popular programming languages
        List<String> recommendations = new ArrayList<>();
        try {
            recommendations = FileUtils.readLines(new File("src/main/resources/popular_languages.txt"), "utf-8");
        } catch (IOException e) {
            LOGGER.error(e);
        }
        //remove from the list the tags already added
        recommendations.removeAll(starred.getTags());
        return recommendations;
    }

    private boolean searchTagsByTag(Set<String> tags, String tag) {
        return tags.stream().anyMatch(s -> s.startsWith(tag));
    }

    private void saveRetrievedRepositories(String username) {

        try {
            //retrieve starred repos of a User by Github API
            GitHub github = GitHubBuilder.fromEnvironment().build();
            GHUser user = github.getUser(username);
            PagedIterable<GHRepository> starredRepositories = user.listStarredRepositories();

            //save retrieved repositories in a local database
            for (GHRepository repository : starredRepositories) {

                if (!starredRepository.findById(repository.getId()).isPresent()) {
                    Starred starred = new Starred();
                    starred.setId(repository.getId());
                    starred.setName(repository.getName());
                    starred.setHtmlUrl(repository.getHtmlUrl().toString());
                    starred.setDescription(repository.getDescription());
                    starred.setLanguage(repository.getLanguage());
                    starred.setTags(new HashSet<>());

                    starredRepository.save(starred);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
            throw new UserNotFoundException(username);
        }
    }
}

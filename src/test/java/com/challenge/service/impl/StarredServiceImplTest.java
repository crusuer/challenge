package com.challenge.service.impl;


import com.challenge.Application;
import com.challenge.entity.Starred;
import com.challenge.exception.StarredNotFoundException;
import com.challenge.exception.UserNotFoundException;
import com.challenge.pojo.TagUpdatePOJO;
import com.challenge.pojo.TagsPOJO;
import com.challenge.repository.StarredRepository;
import com.challenge.service.StarredService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
class StarredServiceImplTest {

    @Autowired
    private StarredRepository starredRepository;
    @Autowired
    private StarredService starredService;

    @BeforeEach
    public void setUp() {
        Starred starred = new Starred();
        starred.setId(123L);
        starred.setName("Testing");
        starred.setDescription("Demo repository");
        starred.setLanguage("JavaScript");
        starred.setHtmlUrl("https://github.com/xpto/demo");
        starred.setUsername("xpto");
        starred.setTags(new HashSet<>(Arrays.asList("python", "css")));

        starredRepository.save(starred);
    }

    @AfterEach
    void tearDown() {
        starredRepository.deleteById(123L);
    }

    @Test
    void getStarredFromExistentUser() {
        Exception exception = assertThrows(UserNotFoundException.class, () -> starredService.getStarred("xptoxpto"));
        String expectedMessage = "User xptoxpto not found on Github API";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getStarredFromInexistentUser() {

        List<Starred> result = starredService.getStarred("crusuer");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(repo -> repo.getName().equals("ProductManagement")));
    }

    @Test
    void addTagToExistentRepo() {
        List<String> tags = Arrays.asList("ruby", "scala");
        TagsPOJO tagsPOJO = new TagsPOJO();
        tagsPOJO.setTags(tags);
        Starred result = starredService.addTag(123L, tagsPOJO);

        assertEquals(4, result.getTags().size());
        assertTrue(result.getTags().contains("ruby"));
        assertTrue(result.getTags().contains("scala"));
    }

    @Test
    void addRepetedTagToExistentRepo() {
        List<String> tags = Arrays.asList("ruby", "scala", "scala");
        TagsPOJO tagsPOJO = new TagsPOJO();
        tagsPOJO.setTags(tags);
        Starred result = starredService.addTag(123L, tagsPOJO);

        assertEquals(4, result.getTags().size());
        assertTrue(result.getTags().contains("ruby"));
        assertTrue(result.getTags().contains("scala"));
    }

    @Test
    void addTagToInexistentRepo() {
        Exception exception = assertThrows(StarredNotFoundException.class, () -> starredService.addTag(0L, new TagsPOJO()));

        String expectedMessage = "Starred repo id 0 not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateTag() {
        TagUpdatePOJO tagsPOJO = new TagUpdatePOJO();
        tagsPOJO.setOldTag("python");
        tagsPOJO.setNewTag("java");
        Starred result = starredService.updateTag(123L, tagsPOJO);

        assertEquals(2, result.getTags().size());
        assertTrue(result.getTags().contains("java"));
        assertFalse(result.getTags().contains("python"));
    }

    @Test
    void deleteTags() {
        List<String> tags = Arrays.asList("css", "python");
        TagsPOJO tagsPOJO = new TagsPOJO();

        tagsPOJO.setTags(tags);
        Starred result = starredService.deleteTag(123L, tagsPOJO);

        assertEquals(0, result.getTags().size());
        assertFalse(result.getTags().contains("python"));
        assertFalse(result.getTags().contains("css"));
    }

    @Test
    void searchExistentTag() {
        List<Starred> starredRepos = starredService.searchTag("xpto", "pyth");
        assertEquals(1, starredRepos.size());
        assertEquals(123L, starredRepos.get(0).getId());
        assertTrue(starredRepos.get(0).getTags().contains("python"));
    }

    @Test
    void searchInexistentTag() {
        List<Starred> starredRepos = starredService.searchTag("xpto", "pythonip");
        assertEquals(0, starredRepos.size());
    }

    @Test
    void recommendTag() {
        List<String> recommendations = starredService.recommendTag(123L);

        assertTrue(recommendations.size() > 0);
        assertTrue(recommendations.contains("kotlin"));
        assertTrue(recommendations.contains("php"));
        assertFalse(recommendations.contains("css"));
        assertFalse(recommendations.contains("python"));
    }
}
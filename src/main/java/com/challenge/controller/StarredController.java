package com.challenge.controller;

import com.challenge.exception.StarredNotFoundException;
import com.challenge.exception.UserNotFoundException;
import com.challenge.pojo.RepositoriesPOJO;
import com.challenge.pojo.TagUpdatePOJO;
import com.challenge.pojo.TagsPOJO;
import com.challenge.service.StarredService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1")
@Api("Operations to get starred repositories and manage their tags, version V1")
public class StarredController {
    @Autowired
    private StarredService starredService;

    @ApiOperation(value = "Retrieving starred repositories of a user")
    @GetMapping(value = "/starred/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoriesPOJO> starredRepositories(@PathVariable String username) {
        RepositoriesPOJO repositoriesPOJO = new RepositoriesPOJO(starredService.getStarred(username));
        return ResponseEntity.ok(repositoriesPOJO);
    }

    @ApiOperation(value = "Add tags to starred repository by id")
    @PostMapping(value = "/starred/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoriesPOJO> addTags(@PathVariable Long id, @Valid @RequestBody TagsPOJO tagsPOJO) {
        RepositoriesPOJO repositoriesPOJO = new RepositoriesPOJO(starredService.addTag(id, tagsPOJO));
        return ResponseEntity.status(HttpStatus.CREATED).body(repositoriesPOJO);
    }

    @ApiOperation(value = "Update tags of starred repository by id")
    @PatchMapping(value = "/starred/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoriesPOJO> updateTag(@PathVariable Long id, @Valid @RequestBody TagUpdatePOJO tagUpdatePOJO) {
        RepositoriesPOJO repositoriesPOJO = new RepositoriesPOJO(starredService.updateTag(id, tagUpdatePOJO));
        return ResponseEntity.status(HttpStatus.OK).body(repositoriesPOJO);
    }

    @ApiOperation(value = "Delete tags of starred repository by id")
    @DeleteMapping(value = "/starred/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTags(@PathVariable Long id, @Valid @RequestBody TagsPOJO tagsPOJO) {
        starredService.deleteTag(id, tagsPOJO);
    }

    @ApiOperation(value = "Search starred repositories by tag")
    @GetMapping(value = "/search/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoriesPOJO> searchStarred(@PathVariable String username, @RequestParam String tag) {
        RepositoriesPOJO repositoriesPOJO = new RepositoriesPOJO(starredService.searchTag(username, tag));
        return ResponseEntity.status(HttpStatus.OK).body(repositoriesPOJO);
    }

    @ApiOperation(value = "Get a list of recommended tags")
    @GetMapping(value = "/recommendation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagsPOJO> recommendTag(@PathVariable Long id) {
        TagsPOJO tagsPOJO = new TagsPOJO();
        tagsPOJO.setTags(starredService.recommendTag(id));
        return ResponseEntity.status(HttpStatus.OK).body(tagsPOJO);
    }

    @ExceptionHandler(StarredNotFoundException.class)
    public void springHandleStarredNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public void springHandleUserNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}

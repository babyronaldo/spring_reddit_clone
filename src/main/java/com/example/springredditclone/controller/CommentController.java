package com.example.springredditclone.controller;

import com.example.springredditclone.dto.CommentDto;
import com.example.springredditclone.model.User;
import com.example.springredditclone.service.AuthService;
import com.example.springredditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final AuthService    authService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {

        return status(HttpStatus.CREATED).body(commentService.createComment(commentDto));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllForPost(@PathVariable("postId") Long postId) {

        return status(HttpStatus.OK).body(commentService.getByPost(postId));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<CommentDto>> getAllByUser(@PathVariable("userName") String userName) {

        return status(HttpStatus.OK).body(commentService.getByUser(userName));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllByCurrentUser() {
        User user = authService.getCurrentUser();

        return status(HttpStatus.OK).body(commentService.getByUser(user.getUsername()));
    }
}

package com.example.springredditclone.service;

import com.example.springredditclone.dto.CommentDto;
import com.example.springredditclone.exception.EntityNotFoundException;
import com.example.springredditclone.mapper.CommentMapper;
import com.example.springredditclone.model.Comment;
import com.example.springredditclone.model.NotificationEmail;
import com.example.springredditclone.model.Post;
import com.example.springredditclone.model.User;
import com.example.springredditclone.repository.CommentRepository;
import com.example.springredditclone.repository.PostRepository;
import com.example.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {
    //TODO: Construct POST URL
    private static final String POST_URL = "";

    private final CommentMapper      commentMapper;
    private final PostRepository     postRepository;
    private final CommentRepository  commentRepository;
    private final UserRepository     userRepository;
    private final AuthService        authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService        mailService;

    public CommentDto createComment(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
             .orElseThrow(() -> new EntityNotFoundException("Could not find Post with id: " + commentDto.getPostId()));

        Comment comment = commentMapper.map(commentDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String msg = mailContentBuilder.build(authService.getCurrentUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(msg, post.getUser());

        return commentMapper.mapToDto(comment);
    }

    public List<CommentDto> getByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Post with id: " + postId));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getByUser(String userName) {
        User user = userRepository.findByUsername(userName)
             .orElseThrow(() -> new EntityNotFoundException("Could not find the User with username: " + userName));

        return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(Collectors.toList());
    }

    private void sendCommentNotification(String msg, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " commented on your post.", user.getEmail(), msg));
    }
}

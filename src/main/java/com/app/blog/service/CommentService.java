package com.app.blog.service;

import com.app.blog.model.Comment;
import com.app.blog.model.Post;
import com.app.blog.repository.CommentRepository;
import com.app.blog.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public Comment addCommentToPost(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono posta z id: " + postId));
        comment.setPost(post);
        return commentRepository.save(comment);
    }


    public List<Comment> getCommentsByPostIdSorted(Long postId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return commentRepository.findAllByPostId(postId, sort);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteCommentById(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Optional<Comment> updateComment(Long id, Comment commentDetails) {
        return commentRepository.findById(id).map(existingComment -> {
            existingComment.setContent(commentDetails.getContent());
            return commentRepository.save(existingComment);
        });
    }
    public Page<Comment> getPaginatedCommentsByPostId(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return commentRepository.findByPostId(postId, pageable);
    }
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

}
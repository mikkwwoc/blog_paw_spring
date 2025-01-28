package com.app.blog.repository;

import com.app.blog.model.Post;
import com.app.blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByOrderByCreatedAtAsc();

    @Query("SELECT p FROM Post p LEFT JOIN PostLike pl ON p.id = pl.post.id GROUP BY p.id ORDER BY COUNT(pl.id) DESC")
    List<Post> findAllByOrderByLikesDesc();

    @Query("SELECT p FROM Post p LEFT JOIN PostDislike pd ON p.id = pd.post.id GROUP BY p.id ORDER BY COUNT(pd.id) DESC")
    List<Post> findAllByOrderByDislikesDesc();

    List<Post> findTop3ByUserOrderByCreatedAtDesc(User user);


    @Query("SELECT p FROM Post p LEFT JOIN PostLike pl ON pl.post.id = p.id " +
            "GROUP BY p ORDER BY COUNT(pl.id) DESC")
    Page<Post> findTop3ByOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN PostDislike pd ON pd.post.id = p.id " +
            "GROUP BY p ORDER BY COUNT(pd.id) DESC")
    Page<Post> findTop3ByOrderByDislikeCountDesc(Pageable pageable);

    List<Post> findAllByUserId(Long userId);

}

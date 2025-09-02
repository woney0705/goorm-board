package io.goorm.board.post.controller;


import io.goorm.board.post.entity.Post;
import io.goorm.board.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html을 찾아서 렌더링
    }

    @GetMapping("/posts")
    public String list(Model model) {
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "post/list";
    }

    @GetMapping("/posts/{seq}")
    public String show(@PathVariable Long seq, Model model) {
        Post post = postService.findBySeq(seq);
        model.addAttribute("post", post);
        return "post/show"; // templates/post/show.html
    }

    @GetMapping("/posts/new")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/form"; // templates/post/form.html
    }

    @GetMapping("/posts/{seq}/edit")
    public String editForm(@PathVariable Long seq, Model model) {
        Post post = postService.findBySeq(seq);
        model.addAttribute("post", post);
        return "post/form"; // templates/post/form.html
    }

    @PostMapping("/posts")
    public String create(Post post, RedirectAttributes redirectAttributes) {
        postService.save(post);
        redirectAttributes.addAttribute("message", "✅ 게시글이 등록되었습니다.");
        return "redirect:/posts/" + post.getSeq();
    }

    @PostMapping("/posts/{seq}")
    public String update(@PathVariable Long seq, Post post, RedirectAttributes redirectAttributes) {
        postService.update(seq, post);
        redirectAttributes.addAttribute("message", "✅ 게시글이 등록되었습니다.");
        return "redirect:/posts/" + seq;
    }

    @PostMapping("/posts/{seq}/delete")
    public String delete(@PathVariable Long seq, RedirectAttributes redirectAttributes) {
        postService.delete(seq);
        redirectAttributes.addAttribute("message", "✅ 게시글이 삭제되었습니다.");
        return "redirect:/posts";
    }

}

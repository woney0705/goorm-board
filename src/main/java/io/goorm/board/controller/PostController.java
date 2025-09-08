package io.goorm.board.controller;


import io.goorm.board.entity.Post;
import io.goorm.board.entity.User;
import io.goorm.board.exception.AccessDeniedException;
import io.goorm.board.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String createForm(HttpSession session, Model model) {
        model.addAttribute("post", new Post());
        return "post/form"; // templates/post/form.html
    }

    @GetMapping("/posts/{seq}/edit")
    public String editForm(@PathVariable Long seq, HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");

        Post post = postService.findBySeq(seq);

        // 본인 글이 아니면 접근 거부
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        model.addAttribute("post", post);
        return "post/form"; // templates/post/form.html
    }

    @PostMapping("/posts")
    public String create(@Valid @ModelAttribute Post post, BindingResult bindingResult,
                         HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        if(bindingResult.hasErrors()) {
            return "post/form";
        }

        post.setAuthor(user);

        postService.save(post);
        redirectAttributes.addFlashAttribute("flashMessage", "flash.post.created");

        return "redirect:/posts/" + post.getSeq();
    }

    @PostMapping("/posts/{seq}")
    public String update(@PathVariable Long seq,
                         @Valid @ModelAttribute  Post post,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        // 기존 게시글 조회
        Post existingPost = postService.findBySeq(seq);

        // 본인 글이 아니면 접근 거부
        if (!existingPost.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인이 작성한 글만 수정할 수 있습니다.");
        }


        if(bindingResult.hasErrors()) {
            post.setSeq(seq);
            return "post/form";
        }

        postService.update(seq, post);
        redirectAttributes.addFlashAttribute("flashMessage", "flash.post.updated");

        return "redirect:/posts/" + seq;
    }

    @PostMapping("/posts/{seq}/delete")
    public String delete(@PathVariable Long seq, HttpSession session,RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        // 기존 게시글 조회
        Post existingPost = postService.findBySeq(seq);

        // 본인 글이 아니면 접근 거부
        if (!existingPost.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        postService.delete(seq);
        redirectAttributes.addFlashAttribute("flashMessage", "flash.post.deleted");
        return "redirect:/posts";
    }

}

package com.github.liamryan.rundle.controllers;

import com.github.liamryan.rundle.models.Permissions;
import com.github.liamryan.rundle.models.Post;
import com.github.liamryan.rundle.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PostsControllerTests {
	private PostRepository postRepository;
	private PostsController postsController;

	private Collection<GrantedAuthority> viewHiddenAuthorities =
		AuthorityUtils.createAuthorityList(Permissions.Post.VIEWHIDDEN.getText());

	private Post hiddenPost;
	private Post post;
	private List<Post> posts;

	@BeforeEach
	void setup() {
		setupMockRepository();
		setupMockSecurityContextHolder();
		resetPostsController();
		refreshPostHelperObjects();
	}

	@Test
	void listRoleCanViewHiddenPosts() {
		when(postRepository.findAll()).thenReturn(posts);
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn((Collection) this.viewHiddenAuthorities);

		assertEquals(postsController.list().size(), 2);
	}

	@Test
	void listNoRoleHidesHiddenPosts() {
		when(postRepository.findAll()).thenReturn(posts);
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn(null);
		List<Post> posts = postsController.list();

		assertEquals(posts.size(), 1);
		assertFalse(posts.get(0).isHidden());
	}

	@Test
	void getRoleCanViewHiddenPosts() {
		when(postRepository.getOne(1L)).thenReturn(hiddenPost);
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn((Collection) this.viewHiddenAuthorities);

		assertNotNull(postsController.get(1));
	}

	@Test
	void getRoleCanViewNormalPosts() {
		when(postRepository.getOne(1L)).thenReturn(post);
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn((Collection) this.viewHiddenAuthorities);

		assertNotNull(postsController.get(1));
	}

	@Test
	void getNoRoleHidesHiddenPosts() {
		when(postRepository.getOne(1L)).thenReturn(hiddenPost);
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn(null);

		assertNull(postsController.get(1));
	}

	@Test
	void getNoRoleCanViewNormalPosts() {
		when(postRepository.getOne(1L)).thenReturn(post);
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn(null);

		assertNotNull(postsController.get(1));
	}

	@Test
	void createSetsLastEditedDate() {
		when(postRepository.save(this.post)).thenReturn(this.post);

		assertNotNull(postsController.create(post).getLastEditedDate());
	}

	private void resetPostsController() {
		postsController = new PostsController(postRepository);
	}

	private void setupMockSecurityContextHolder() {
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	private void setupMockRepository() {
		postRepository = Mockito.mock(PostRepository.class);
	}

	private void refreshPostHelperObjects() {
		hiddenPost = new Post("dummy", "dummy", null, null, "hidden", "", true);
		post = new Post("dummy", "dummy", null, null, "visible", "", false);
		posts = Arrays.asList(hiddenPost, post);
	}
}

package com.github.liamryan.rundle.controllers;

import com.github.liamryan.rundle.models.Permissions;
import com.github.liamryan.rundle.models.Post;
import com.github.liamryan.rundle.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;


class PostsControllerTests {
	private PostRepository postRepository;
	private PostsController postsController;

	PostsControllerTests() {
		setupMockRepository();
	}

	@BeforeEach
	void setup() {
		resetPostsController();
		setupMockSecurityContextHolder();
	}

	@Test
	void roleCanViewHiddenPosts() {
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn((Collection) AuthorityUtils.createAuthorityList(Permissions.Post.VIEWHIDDEN.getText()));

		assertEquals(postsController.list().size(), 2);
	}

	@Test
	void noRoleHidesHiddenPosts() {
		when(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.thenReturn(null);
		List<Post> posts = postsController.list();

		assertEquals(posts.size(), 1);
		assertFalse(posts.get(0).isHidden());
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
		List<Post> posts = Arrays.asList(
			new Post("dummy", "dummy", null, null, "hidden", "", true),
			new Post("dummy", "dummy", null, null, "visible", "", false)
		);
		when(postRepository.findAll()).thenReturn(posts);
	}
}

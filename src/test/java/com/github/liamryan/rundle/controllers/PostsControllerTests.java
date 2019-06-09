package com.github.liamryan.rundle.controllers;

import com.github.liamryan.rundle.models.Permissions;
import com.github.liamryan.rundle.models.Post;
import com.github.liamryan.rundle.repositories.PostRepository;
import com.github.liamryan.rundle.services.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import static org.mockito.ArgumentMatchers.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PostsControllerTests {
	private PostRepository postRepository;
	private PostsController postsController;
	private Post hiddenPost;
	private Post post;
	private List<Post> posts;

	private SecurityService securityServiceMock = Mockito.mock(SecurityService.class);
	private Authentication authenticationMock = Mockito.mock(Authentication.class);

	private Collection<GrantedAuthority> viewHiddenAuthorities =
		AuthorityUtils.createAuthorityList(Permissions.Post.VIEWHIDDEN.getText());

	@BeforeEach
	void setup() {
		setupMockRepository();
		resetPostsController();
		refreshPostHelperObjects();
	}

	private void setupMockRepository() {
		postRepository = Mockito.mock(PostRepository.class);
	}

	private void resetPostsController() {
		postsController = new PostsController(postRepository, securityServiceMock);
	}

	private void refreshPostHelperObjects() {
		hiddenPost = new Post("dummy", "dummy", null, null, "hidden", null, true);
		post = new Post("dummy", "dummy", null, null, "visible", null, false);
		posts = Arrays.asList(hiddenPost, post);
	}

	@Test
	void listRoleCanViewHiddenPosts() {
		when(postRepository.findAll()).thenReturn(posts);
		when(securityServiceMock.hasAuthority(any(), any())).thenReturn(true);
		when(authenticationMock.getAuthorities()).thenReturn((Collection) viewHiddenAuthorities);

		assertEquals(postsController.list(authenticationMock).size(), 2);
	}

	@Test
	void listNoRoleHidesHiddenPosts() {
		when(postRepository.findAll()).thenReturn(posts);
		when(authenticationMock.getAuthorities()).thenReturn(null);

		List<Post> posts = postsController.list(authenticationMock);

		assertEquals(posts.size(), 1);
		assertFalse(posts.get(0).isHidden());
	}

	@Test
	void getRoleCanViewHiddenPosts() {
		when(postRepository.getOne(1L)).thenReturn(hiddenPost);
		when(securityServiceMock.hasAuthority(any(), any())).thenReturn(true);
		when(authenticationMock.getAuthorities()).thenReturn((Collection) viewHiddenAuthorities);

		assertNotNull(postsController.get(1, authenticationMock));
	}

	@Test
	void getRoleCanViewNormalPosts() {
		when(postRepository.getOne(1L)).thenReturn(post);
		when(authenticationMock.getAuthorities()).thenReturn((Collection) viewHiddenAuthorities);

		assertNotNull(postsController.get(1, authenticationMock));
	}

	@Test
	void getNoRoleHidesHiddenPosts() {
		when(postRepository.getOne(1L)).thenReturn(hiddenPost);
		when(authenticationMock.getAuthorities()).thenReturn(null);

		assertNull(postsController.get(1, authenticationMock));
	}

	@Test
	void getNoRoleCanViewNormalPosts() {
		when(postRepository.getOne(1L)).thenReturn(post);
		when(authenticationMock.getAuthorities()).thenReturn(null);

		assertNotNull(postsController.get(1, authenticationMock));
	}

	@Test
	void createSetsLastEditedDate() {
		when(postRepository.save(this.post)).thenReturn(this.post);

		assertNotNull(postsController.create(post).getLastEditedDate());
	}
}

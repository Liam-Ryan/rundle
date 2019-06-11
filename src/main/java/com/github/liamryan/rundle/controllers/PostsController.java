/*
 * Copyright (c) 2018 Liam Ryan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.github.liamryan.rundle.controllers;

import com.github.liamryan.rundle.models.Permissions;
import com.github.liamryan.rundle.models.Post;
import com.github.liamryan.rundle.repositories.PostRepository;
import com.github.liamryan.rundle.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@CrossOrigin(origins = {"${settings.allowed-origins}"})
public class PostsController {
	private PostRepository postRepository;
	private SecurityService securityService;
	private Logger log = LoggerFactory.getLogger(PostsController.class);


	@Autowired
	public PostsController(PostRepository postRepository, SecurityService securityService) {
		this.postRepository = postRepository;
		this.securityService = securityService;
	}

	@GetMapping
	public List<Post> list(Authentication authentication) {
		return filterPosts(canViewHidden(authentication), postRepository.findAll());
	}

	List<Post> filterPosts(boolean canViewHidden, List<Post> posts) {
		return posts.stream()
			.filter(post -> canViewHidden || !post.isHidden())
			.collect(Collectors.toList());
	}

	private boolean canViewHidden(Authentication authentication) {
		boolean canViewHidden = false;
		if (authentication != null) {
			canViewHidden = securityService.hasAuthority(Permissions.Post.VIEWHIDDEN, authentication.getAuthorities());
		}

		return canViewHidden;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Post create(@RequestBody Post post) {
		post.setLastEditedDate(new Date());
		postRepository.save(post);
		return post;
	}

	@GetMapping("/{id}")
	public Post get(@PathVariable("id") long id, Authentication authentication) {
		try {
			Post post = postRepository.getOne(id);
			if (!post.isHidden() || canViewHidden(authentication)) {
				return post;
			}
		} catch (EntityNotFoundException e) {
			log.info(String.format("Record not found for post id %d", id));
		}
		return null;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") long id) {
		postRepository.deleteById(id);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Post update(@PathVariable("id") long id, @RequestBody Post post) {
		post.setPostId(id);
		post.setLastEditedDate(new Date());
		postRepository.save(post);
		return post;
	}

	@RequestMapping(method = RequestMethod.OPTIONS)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void options() {
	}
}

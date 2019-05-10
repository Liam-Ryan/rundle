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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private Logger log = LoggerFactory.getLogger(PostsController.class);

	private boolean hasAuthority(Permissions.Post permission) {
		try {
			return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals(permission.getText()));
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	@Autowired
	public PostsController(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@GetMapping
	public List<Post> list() {
		boolean canViewHidden = hasAuthority(Permissions.Post.VIEWHIDDEN);
		return postRepository.findAll().stream()
			.filter(post -> canViewHidden || !post.isHidden())
			.collect(Collectors.toList());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Post create(@RequestBody Post post) {
		post.setLastEditedDate(new Date());
		postRepository.save(post);
		return post;
	}

	@GetMapping("/{id}")
	public Post get(@PathVariable("id") long id) {
		try {
			Post p = postRepository.getOne(id);
			if (!p.isHidden() || hasAuthority(Permissions.Post.VIEWHIDDEN)) {
				return p;
			}
		} catch(EntityNotFoundException e) {
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
		post.setId(id);
		post.setLastEditedDate(new Date());
		postRepository.save(post);
		return post;
	}

	@RequestMapping(method = RequestMethod.OPTIONS)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void options() {
	}
}

/*
 * Copyright (c) 2018 Liam Ryan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.github.liamryan.rundle.controllers;

import com.github.liamryan.rundle.models.Post;
import com.github.liamryan.rundle.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostsController {
	private PostRepository postRepository;

	@Autowired
	public PostsController(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@GetMapping
	public List<Post> list() {
		return postRepository.findAll();
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
		return postRepository.getOne(id);
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
}

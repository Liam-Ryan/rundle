package com.github.liamryan.rundle.controllers;

import com.github.liamryan.rundle.models.Category;
import com.github.liamryan.rundle.models.Permissions;
import com.github.liamryan.rundle.models.Post;
import com.github.liamryan.rundle.repositories.CategoryRepository;
import com.github.liamryan.rundle.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@CrossOrigin(origins = {"${settings.allowed-origins}"})
public class CategoryController {

	private CategoryRepository categoryRepository;
	private SecurityService securityService;
	private PostsController postsController;

	private Logger log = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	public CategoryController(CategoryRepository categoryRepository, SecurityService securityService, PostsController postsController) {
		this.categoryRepository = categoryRepository;
		this.securityService = securityService;
		this.postsController = postsController;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Category create(@RequestBody Category category) {
		try {
			categoryRepository.save(category);
		} catch (org.hibernate.exception.ConstraintViolationException ignore) {

		}
		return category;
	}

	@GetMapping
	public List<Category> list(Authentication authentication) {
		boolean canViewHidden;

		if(authentication != null) {
			canViewHidden = securityService.hasAuthority(Permissions.Post.VIEWHIDDEN, authentication.getAuthorities());
		} else {
			//required for lambda ( must be effectively final )
			canViewHidden = false;
		}

		List<Category> categories = categoryRepository.findAll();
		categories.forEach(category -> filterCategoryPosts(canViewHidden, category));
		return categories;
	}

	@GetMapping("/{id}")
	public Category get(@PathVariable("id") long id, Authentication authentication) {
		try {
			boolean canViewHidden = securityService.hasAuthority(Permissions.Post.VIEWHIDDEN, authentication.getAuthorities());
			return filterCategoryPosts(canViewHidden, categoryRepository.getOne(id));

		} catch (EntityNotFoundException e) {
			log.info(String.format("Record not found for post id %d", id));
		}
		return null;
	}

	private Category filterCategoryPosts(boolean canViewHidden, Category category) {
		if (category == null)
			return null;
		List<Post> filteredPosts = postsController.filterPosts(canViewHidden, category.getPosts());
		category.setPosts(filteredPosts);
		return category;
	}
}

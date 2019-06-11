package com.github.liamryan.rundle.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

	private Category() {
	}

	public Category(String name, Post... posts) {
		this.name = name;
		this.posts = Stream.of(posts)
			.collect(Collectors.toList());
		this.posts.forEach(post -> post.setCategory(this));
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long categoryId;

	private String name;

	@OneToMany(mappedBy = "category")
	// prevent json infinite recursion, allowSetters is required to be true to access the json annotations on posts for deserializing
	@JsonIgnoreProperties(value = "category", allowSetters = true)
	private List<Post> posts;

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Category category = (Category) o;
		return categoryId == category.categoryId &&
			Objects.equals(name, category.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoryId, name);
	}
}

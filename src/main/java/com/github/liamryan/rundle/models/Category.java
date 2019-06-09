package com.github.liamryan.rundle.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Category {

	private Category(){}

	public Category(String name, Post... posts) {
		this.name = name;
		this.posts = Stream.of(posts)
			.collect(Collectors.toList());
		this.posts.forEach(post -> post.setCategory(this));
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String name;

	@OneToMany(mappedBy = "category")
	@JsonManagedReference  //required by jackson, there's a corresponding annotation in Post.java
	private List<Post> posts;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
		return id == category.id &&
			Objects.equals(name, category.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}

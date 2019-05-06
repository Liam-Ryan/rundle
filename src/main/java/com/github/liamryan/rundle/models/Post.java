/*
 * Copyright (c) 2018 Liam Ryan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.github.liamryan.rundle.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
//ignore hibernate methods added to object automatically
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	@Column(length = 100000)
	private String description;

	@Lob
	@Column(length = 100000)
	private String content;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
	private Date lastEditedDate;

	@ElementCollection
	private List<String> tags;

	private String title;
	private String category;
	private boolean isHidden;

	public Long getId () {
		return id;
	}

	//auto-generated but jackson needs the method for marshalling
	public void setId (Long id) {
		this.id = id;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public List<String> getTags () {
		return tags;
	}

	public void setTags (List<String> tags) {
		this.tags = tags;
	}

	public String getCategory () {
		return category;
	}

	public void setCategory (String category) {
		this.category = category;
	}

	public String getContent () {
		return content;
	}

	public void setContent (String content) {
		this.content = content;
	}

	public boolean isHidden () {
		return isHidden;
	}

	public void setHidden (boolean hidden) {
		isHidden = hidden;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public Date getLastEditedDate () {
		return lastEditedDate;
	}

	public void setLastEditedDate (Date lastEditedDate) {
		this.lastEditedDate = lastEditedDate;
	}
}

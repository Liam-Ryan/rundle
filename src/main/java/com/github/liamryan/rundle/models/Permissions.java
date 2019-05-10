package com.github.liamryan.rundle.models;

public class Permissions {
	public enum Post {
		VIEWHIDDEN("view:hidden-post"),
		CREATE("create:post"),
		DELETE("delete:post");

		private String permission;

		Post(String permission) {
			this.permission = permission;
		}

		public String getText() {
			return this.permission;
		}
	}
}

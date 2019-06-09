package com.github.liamryan.rundle.services;

import com.github.liamryan.rundle.controllers.PostsController;
import com.github.liamryan.rundle.models.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SecurityService {

	private Logger log = LoggerFactory.getLogger(PostsController.class);

	public boolean hasAuthority(Permissions.Post permission, Collection<? extends GrantedAuthority> authorities) {
		try {
			return authorities.stream()
				.anyMatch(role -> role.getAuthority().equals(permission.getText()));
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
}

/*
 * Copyright (c) 2018 Liam Ryan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.github.liamryan.rundle.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import com.github.liamryan.rundle.models.Permissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value(value = "${auth0.apiAudience}")
	private String apiAudience;

	@Value(value = "${auth0.issuer}")
	private String issuer;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		JwtWebSecurityConfigurer
			.forRS256(apiAudience, issuer)
			.configure(http)
			.authorizeRequests()
			.antMatchers(HttpMethod.POST, "/api/v1/posts").hasAuthority(Permissions.Post.CREATE.getText())
			.antMatchers(HttpMethod.GET, "/api/v1/posts").permitAll()
			.antMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
			.antMatchers(HttpMethod.DELETE, "/api/v1/posts/**").hasAuthority(Permissions.Post.DELETE.getText())
			.antMatchers(HttpMethod.PUT, "/api/v1/posts/**").hasAuthority(Permissions.Post.CREATE.getText())
			.anyRequest()
			.authenticated();
		http.cors();
	}
}

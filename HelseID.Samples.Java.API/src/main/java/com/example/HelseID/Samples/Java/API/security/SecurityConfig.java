package com.example.HelseID.Samples.Java.API.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${helseid.audience}")
	private String audience;

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuer;

	@Bean
	JwtDecoder jwtDecoder() {
		var jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);
		
		// check issuer
		var issuerValidator = new JwtIssuerValidator(issuer);
		// check notBefore and expiration
		var timestampValidator = new JwtTimestampValidator();
		// check audience (requires that audience is the only audience)
		var audienceValidator = new JwtClaimValidator<List<String>>("aud", aud -> aud.get(0).equals(audience) && aud.size() == 1);

		// check user claims
		// var assuranceLevelValidator = new JwtClaimValidator<String>("helseid://claims/identity/assurance_level", assuranceLevel -> assuranceLevel.equals("high"));
		// var securityLevelValidator = new JwtClaimValidator<String>("helseid://claims/identity/security_level", securityLevel -> securityLevel.equals("4"));
		// var pidValidator = new JwtClaimValidator<String>("helseid://claims/identity/security_level", pid -> pid.equals("13065906141"));
		// var hprValidator = new JwtClaimValidator<String>("helseid://claims/identity/security_level", hpr -> hpr.equals("6081940"));

		// check client claims
		// var orgNrParentValidator = new JwtClaimValidator<String>("helseid://claims/client/claims/orgnr_parent", orgNr -> orgNr.equals(""));
		// var orgNrChildValidator = new JwtClaimValidator<String>("helseid://claims/client/claims/orgnr_child", orgNr -> orgNr.equals(""));

		// wrap all validators in one validator
		var validator = new DelegatingOAuth2TokenValidator<Jwt>(issuerValidator, timestampValidator, audienceValidator);

		jwtDecoder.setJwtValidator(validator);

		return jwtDecoder;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.mvcMatchers("/api/public").permitAll()
				.mvcMatchers("/api/private").authenticated()
				.mvcMatchers("/api/private-scoped").hasAuthority("SCOPE_norsk-helsenett:java-sample-api/read")
				.and().cors()
				.and().oauth2ResourceServer().jwt();
	}
}
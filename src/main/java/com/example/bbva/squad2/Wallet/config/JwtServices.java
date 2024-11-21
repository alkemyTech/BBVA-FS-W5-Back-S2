package com.example.bbva.squad2.Wallet.config;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServices {
	 
	@Value("${application.security.jwt.login-key}")
    private String loginKey;
	
	@Value("${application.security.jwt.login-key-expiration}")
	 private String loginTokenExpiration;

	public String generateToken(
			final Long usuarioId,
			final String username,
			final String roles
	) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("roles", roles);
		final LocalDateTime dueDateToken = LocalDateTime
				.now()
				.plusMinutes(Long.parseLong(loginTokenExpiration))
				.atZone(ZoneId.of("UTC"))
				.toLocalDateTime();
		final Date expirationDate = Date.from(dueDateToken.atZone(ZoneId.systemDefault()).toInstant());
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setId(usuarioId.toString())
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(expirationDate)
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(loginKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(final String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenValid(final String token, UserDetails userDetails) {
		final String userEmail = extractUsername(token);
		return (userEmail.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	public <T> T extractClaim(final String token, Function<Claims, T> claimsResolver) {
		final Claims claims = Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claimsResolver.apply(claims);
	}

	private boolean isTokenExpired(final String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(final String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public Claims extractAllClaims(
			final String token
	) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public UsuarioSeguridad validateAndGetSecurity(
			final String token
	) {
		Claims claims = extractAllClaims(token);
		UsuarioSeguridad security = new UsuarioSeguridad(claims);
		return security;
	}
}




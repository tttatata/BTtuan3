package vn.btthtuan4pttk.demo.jwt.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.btthtuan4pttk.demo.jwt.common.ERole;
import vn.btthtuan4pttk.demo.jwt.common.JwtUtils;
import vn.btthtuan4pttk.demo.jwt.dto.JwtResponse;
import vn.btthtuan4pttk.demo.jwt.dto.LoginRequest;
import vn.btthtuan4pttk.demo.jwt.dto.MessageResponse;
import vn.btthtuan4pttk.demo.jwt.dto.SignupRequest;
import vn.btthtuan4pttk.demo.jwt.entities.Role;
import vn.btthtuan4pttk.demo.jwt.entities.User;
import vn.btthtuan4pttk.demo.jwt.repositories.RoleRepository;
import vn.btthtuan4pttk.demo.jwt.repositories.UserRepository;
import vn.btthtuan4pttk.demo.jwt.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired

	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;

@PostMapping("/signin")
public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
Authentication authentication = authenticationManager.authenticate(
		new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
,loginRequest.getPassword()));
SecurityContextHolder.getContext().setAuthentication (authentication);
String jwt = jwtUtils.generateJwtToken(authentication);
UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
List<String> roles = userDetails.getAuthorities().stream()
.map (item -> item.getAuthority()) 
.collect(Collectors.toList());
return ResponseEntity.ok(new JwtResponse (jwt,
userDetails.getId(),
userDetails.getUsername(), 
userDetails.getEmail(), 
roles));}

@PostMapping("/signup") 
public ResponseEntity<?> registerUser (@Validated @RequestBody SignupRequest signUpRequest) { 
	if (userRepository.existByUsername(signUpRequest.getUsername())){ 
		return ResponseEntity
.badRequest() 
.body(new MessageResponse("Error: Username is already taken!"));}
	
if(userRepository.existByEmail(signUpRequest.getEmail())) { 
	return ResponseEntity
.badRequest() 
.body(new MessageResponse("Error: Email is already in use!"));
	}

	 User user = new User(signUpRequest.getUsername(),
			 signUpRequest.getEmail(),
			 encoder.encode(signUpRequest.getPassword()));
 Set<String> strRoles = signUpRequest.getRole();
 Set<Role> roles = new HashSet<>();
 if(strRoles == null) {
	 Role userRole = roleRepository.findByName(ERole.ROLE_USER)
			 .orElseThrow(()->new RuntimeException("Error: Role is not found:"));
			 roles.add(userRole);
 }else {
	 strRoles.forEach(role -> {
		 switch (role) {
		 case"admin":
			 Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
			 .orElseThrow(()->new RuntimeException("Error: Role is not found:"));
			 roles.add(adminRole);
			 break;
		 case"mod":
			 Role mod = roleRepository.findByName(ERole.ROLE_MODERATOR)
			 .orElseThrow(()->new RuntimeException("Error: Role is not found:"));
			 roles.add(mod);
			 break;
			 default:
				 Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				 .orElseThrow(()->new RuntimeException("Error: Role is not found:"));
				 roles.add(userRole);
		 }
	 });
 }
 user.setRoles(roles);
 userRepository.save(user);
 return ResponseEntity.ok(new MessageResponse("User registered successfully"));
			  }}

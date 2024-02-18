package com.example.education.controller;

import com.example.education.data.request.LoginForm;
import com.example.education.data.request.SignupRequest;
import com.example.education.data.response.JwtResponse;
import com.example.education.data.response.ResponseMessage;
import com.example.education.model.oauth.ERole;
import com.example.education.model.oauth.Role;
import com.example.education.model.oauth.User;
import com.example.education.repository.RoleRepository;
import com.example.education.repository.UserRepository;
import com.example.education.security.jwt.JwtProvider;
import com.example.education.service.UserService;
import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    JwtProvider jwtProvider;
    @Autowired
    UserService userService;

    @PostMapping("/signin")
    @CrossOrigin
//	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
    public ResponseEntity<?> authenticateUser(@RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String result = "error";
        if (!jwt.isEmpty()) {
            result = "success";
        }

        Long id = null;
        // TODO: 9/8/23 remove and read  UserDetailsServiceImpl.loadUserByUsername
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            id = user.get().getId();
            if (user.get().getActive() == 2) {
                result = "notActive";
            }

            PersianDate currentDate = PersianDateTime.now().toLocalDate();
            PersianDate startTime = PersianDate.parse(user.get().getStartTime());
            PersianDate endTime = PersianDate.parse(user.get().getEndTime());
//            if ((currentDate.getYear() - startTime.getYear() >= 0) &&
//                    (currentDate.getMonth().getValue() - startTime.getMonth().getValue() >= 0) &&
//                    (currentDate.getDayOfMonth() - startTime.getDayOfMonth() >= 0)) {
//                result = "success";
//            } else {
//                result = "notActive";
//            }


            if (currentDate.getYear() - startTime.getYear() > 0) {
                result = "success";
            } else if (currentDate.getYear() - startTime.getYear() >= 0) {
                if (currentDate.getMonth().getValue() - startTime.getMonth().getValue() > 0) {
                    result = "success";
                } else if (currentDate.getMonth().getValue() - startTime.getMonth().getValue() == 0) {
                    if (currentDate.getDayOfMonth() - startTime.getDayOfMonth() >= 0) {
                        result = "success";
                    } else {
                        result = "notActive";
                    }
                } else {
                    result = "notActive";
                }
            } else {
                result = "notActive";
            }

//            if (!result.equals("notActive")) {
//                if ((endTime.getYear() - currentDate.getYear() >= 0) &&
//                        (endTime.getMonth().getValue() - currentDate.getMonth().getValue() >= 0) &&
//                        (endTime.getDayOfMonth() - currentDate.getDayOfMonth() >= 0)) {
//                    result = "success";
//                } else {
//                    result = "notActive";
//                }
//            }

            if (!result.equals("notActive")) {
                if (endTime.getYear() - currentDate.getYear() > 0){
                    result = "success";

                } else if (endTime.getYear() - currentDate.getYear() == 0) {
                    if(endTime.getMonth().getValue() - currentDate.getMonth().getValue() > 0){
                        result = "success";
                    } else if (endTime.getMonth().getValue() - currentDate.getMonth().getValue() == 0) {
                        if(endTime.getDayOfMonth() - currentDate.getDayOfMonth() >= 0){
                            result = "success";
                        } else {
                            result = "notActive";
                        }
                    } else {
                        result = "notActive";
                    }
                } else {
                    result = "notActive";
                }
            }
        }

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities(), result, id));
    }

    @PostMapping("/signup")
    @CrossOrigin
//	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (signUpRequest.getUsername().isEmpty() || signUpRequest.getUsername() == null) {
            Optional<User> user = userRepository.findByUsername(signUpRequest.getUsername());
            user.ifPresent(value -> userService.updateUser(value.getId(), signUpRequest));
        }

        // TODO: 9/21/23
        if (userRepository.existsByUsername(signUpRequest.getUsername()) || userRepository.existsByEmail(signUpRequest.getEmail())) {
            Optional<User> user = userRepository.findByUsername(signUpRequest.getUsername());
            user.ifPresent(value -> userService.updateUser(value.getId(), signUpRequest));
//            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
//                    HttpStatus.BAD_REQUEST);
        }

        // TODO: 9/21/23
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
//                    HttpStatus.BAD_REQUEST);
//        }

//		// Creating user's account
//		User user = new User(signUpRequest.getFirstname(), signUpRequest.getLastname(),
//								signUpRequest.getUsername(), signUpRequest.getEmail(),
//				encoder.encode(signUpRequest.getPassword()));

        // Creating user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getActive(),
                signUpRequest.getStartTime(), signUpRequest.getEndTime());

        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRoles() != null) {
            Set<String> strRoles = signUpRequest.getRoles();

            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        roles.add(adminRole);

                        break;
                    case "pm":
                        Role pmRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        roles.add(pmRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        roles.add(userRole);
                }
            });
        } else {
            // default mode : User register
            Role userRole =
                    roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() ->
                                    new RuntimeException("Fail! -> Cause: User Role not find."));
            roles.add(userRole);
        }

        user.setAuthorities(roles);
        userRepository.save(user);

        return new ResponseEntity<>(
                new ResponseMessage("User "
//                        + signUpRequest.getFirstname()
                        + signUpRequest.getUsername()
                        + " is registered successfully!"),
                HttpStatus.OK);
    }
}

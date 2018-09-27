package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.User;
import kz.stanislav.voting.persistence.model.Role;
import kz.stanislav.voting.persistence.dao.UserRepository;
import kz.stanislav.voting.util.UserUtil;
import kz.stanislav.voting.util.ValidationUtil;
import java.util.Collections;
import java.util.List;
import java.net.URI;
import javax.validation.Valid;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = AdminRestController.REST_URL)
public class AdminRestController {
    private static final Logger logger = LoggerFactory.getLogger(AdminRestController.class);
    static final String REST_URL = "/api/admin/users";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;
    private final UserUtil userUtil;

    @Autowired
    public AdminRestController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                               UserUtil userUtil, ValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userUtil = userUtil;
        this.validationUtil = validationUtil;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        logger.info("get all users");
        return userRepository.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@PathVariable("id") int id) {
        logger.info("get user with id={}", id);
        return userRepository.findById(id).orElseThrow(validationUtil.notFoundWithId("id={}", id));
    }

    @GetMapping(value = "/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getByMail(@RequestParam("email") String email) {
        logger.info("get user by email={}", email);
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(validationUtil.notFoundWithId("e-mail={}", email.trim().toLowerCase()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        logger.info("create {}", user);
        validationUtil.checkNew(user);

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singletonList(Role.ROLE_USER));
        }

        User created = userRepository.save(userUtil.prepareToSave(user, passwordEncoder));

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@PathVariable("id") int id, @Valid @RequestBody User user) {
        logger.info("update {}", user);
        validationUtil.assureIdConsistent(user, id);
        validationUtil.checkNotFoundWithId(userRepository.existsById(id), id);

        User updated = userRepository.save(userUtil.prepareToSave(user, passwordEncoder));

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        logger.info("delete user with id={}", id);
        validationUtil.checkNotFoundWithId(userRepository.existsById(id), id);
        userRepository.deleteById(id);
    }
}
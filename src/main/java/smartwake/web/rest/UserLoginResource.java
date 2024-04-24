package smartwake.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartwake.domain.UserLogin;
import smartwake.repository.UserLoginRepository;
import smartwake.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link smartwake.domain.UserLogin}.
 */
@RestController
@RequestMapping("/api/user-logins")
@Transactional
public class UserLoginResource {

    private final Logger log = LoggerFactory.getLogger(UserLoginResource.class);

    private static final String ENTITY_NAME = "userLogin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserLoginRepository userLoginRepository;

    public UserLoginResource(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    /**
     * {@code POST  /user-logins} : Create a new userLogin.
     *
     * @param userLogin the userLogin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userLogin, or with status {@code 400 (Bad Request)} if the userLogin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserLogin> createUserLogin(@Valid @RequestBody UserLogin userLogin) throws URISyntaxException {
        log.debug("REST request to save UserLogin : {}", userLogin);
        if (userLogin.getId() != null) {
            throw new BadRequestAlertException("A new userLogin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userLogin = userLoginRepository.save(userLogin);
        return ResponseEntity.created(new URI("/api/user-logins/" + userLogin.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userLogin.getId().toString()))
            .body(userLogin);
    }

    /**
     * {@code PUT  /user-logins/:id} : Updates an existing userLogin.
     *
     * @param id the id of the userLogin to save.
     * @param userLogin the userLogin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLogin,
     * or with status {@code 400 (Bad Request)} if the userLogin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userLogin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserLogin> updateUserLogin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserLogin userLogin
    ) throws URISyntaxException {
        log.debug("REST request to update UserLogin : {}, {}", id, userLogin);
        if (userLogin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userLogin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userLoginRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userLogin = userLoginRepository.save(userLogin);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userLogin.getId().toString()))
            .body(userLogin);
    }

    /**
     * {@code PATCH  /user-logins/:id} : Partial updates given fields of an existing userLogin, field will ignore if it is null
     *
     * @param id the id of the userLogin to save.
     * @param userLogin the userLogin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLogin,
     * or with status {@code 400 (Bad Request)} if the userLogin is not valid,
     * or with status {@code 404 (Not Found)} if the userLogin is not found,
     * or with status {@code 500 (Internal Server Error)} if the userLogin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserLogin> partialUpdateUserLogin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserLogin userLogin
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserLogin partially : {}, {}", id, userLogin);
        if (userLogin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userLogin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userLoginRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserLogin> result = userLoginRepository
            .findById(userLogin.getId())
            .map(existingUserLogin -> {
                if (userLogin.getUsername() != null) {
                    existingUserLogin.setUsername(userLogin.getUsername());
                }
                if (userLogin.getPassword() != null) {
                    existingUserLogin.setPassword(userLogin.getPassword());
                }

                return existingUserLogin;
            })
            .map(userLoginRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userLogin.getId().toString())
        );
    }

    /**
     * {@code GET  /user-logins} : get all the userLogins.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userLogins in body.
     */
    @GetMapping("")
    public List<UserLogin> getAllUserLogins() {
        log.debug("REST request to get all UserLogins");
        return userLoginRepository.findAll();
    }

    /**
     * {@code GET  /user-logins/:id} : get the "id" userLogin.
     *
     * @param id the id of the userLogin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userLogin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserLogin> getUserLogin(@PathVariable("id") Long id) {
        log.debug("REST request to get UserLogin : {}", id);
        Optional<UserLogin> userLogin = userLoginRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userLogin);
    }

    /**
     * {@code DELETE  /user-logins/:id} : delete the "id" userLogin.
     *
     * @param id the id of the userLogin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserLogin(@PathVariable("id") Long id) {
        log.debug("REST request to delete UserLogin : {}", id);
        userLoginRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

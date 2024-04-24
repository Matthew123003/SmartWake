package smartwake.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static smartwake.domain.UserLoginAsserts.*;
import static smartwake.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import smartwake.IntegrationTest;
import smartwake.domain.UserLogin;
import smartwake.repository.UserLoginRepository;

/**
 * Integration tests for the {@link UserLoginResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserLoginResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-logins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserLoginMockMvc;

    private UserLogin userLogin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLogin createEntity(EntityManager em) {
        UserLogin userLogin = new UserLogin().username(DEFAULT_USERNAME).password(DEFAULT_PASSWORD);
        return userLogin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLogin createUpdatedEntity(EntityManager em) {
        UserLogin userLogin = new UserLogin().username(UPDATED_USERNAME).password(UPDATED_PASSWORD);
        return userLogin;
    }

    @BeforeEach
    public void initTest() {
        userLogin = createEntity(em);
    }

    @Test
    @Transactional
    void createUserLogin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserLogin
        var returnedUserLogin = om.readValue(
            restUserLoginMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLogin)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserLogin.class
        );

        // Validate the UserLogin in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserLoginUpdatableFieldsEquals(returnedUserLogin, getPersistedUserLogin(returnedUserLogin));
    }

    @Test
    @Transactional
    void createUserLoginWithExistingId() throws Exception {
        // Create the UserLogin with an existing ID
        userLogin.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserLoginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLogin)))
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userLogin.setUsername(null);

        // Create the UserLogin, which fails.

        restUserLoginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLogin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userLogin.setPassword(null);

        // Create the UserLogin, which fails.

        restUserLoginMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLogin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserLogins() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        // Get all the userLoginList
        restUserLoginMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLogin.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }

    @Test
    @Transactional
    void getUserLogin() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        // Get the userLogin
        restUserLoginMockMvc
            .perform(get(ENTITY_API_URL_ID, userLogin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userLogin.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }

    @Test
    @Transactional
    void getNonExistingUserLogin() throws Exception {
        // Get the userLogin
        restUserLoginMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserLogin() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userLogin
        UserLogin updatedUserLogin = userLoginRepository.findById(userLogin.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserLogin are not directly saved in db
        em.detach(updatedUserLogin);
        updatedUserLogin.username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restUserLoginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserLogin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedUserLogin))
            )
            .andExpect(status().isOk());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserLoginToMatchAllProperties(updatedUserLogin);
    }

    @Test
    @Transactional
    void putNonExistingUserLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLogin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLoginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userLogin.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLogin))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLogin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userLogin))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLogin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLogin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserLoginWithPatch() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userLogin using partial update
        UserLogin partialUpdatedUserLogin = new UserLogin();
        partialUpdatedUserLogin.setId(userLogin.getId());

        partialUpdatedUserLogin.username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restUserLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserLogin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserLogin))
            )
            .andExpect(status().isOk());

        // Validate the UserLogin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserLoginUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserLogin, userLogin),
            getPersistedUserLogin(userLogin)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserLoginWithPatch() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userLogin using partial update
        UserLogin partialUpdatedUserLogin = new UserLogin();
        partialUpdatedUserLogin.setId(userLogin.getId());

        partialUpdatedUserLogin.username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restUserLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserLogin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserLogin))
            )
            .andExpect(status().isOk());

        // Validate the UserLogin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserLoginUpdatableFieldsEquals(partialUpdatedUserLogin, getPersistedUserLogin(partialUpdatedUserLogin));
    }

    @Test
    @Transactional
    void patchNonExistingUserLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLogin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userLogin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userLogin))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLogin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userLogin))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserLogin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLogin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userLogin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserLogin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserLogin() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userLogin
        restUserLoginMockMvc
            .perform(delete(ENTITY_API_URL_ID, userLogin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userLoginRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserLogin getPersistedUserLogin(UserLogin userLogin) {
        return userLoginRepository.findById(userLogin.getId()).orElseThrow();
    }

    protected void assertPersistedUserLoginToMatchAllProperties(UserLogin expectedUserLogin) {
        assertUserLoginAllPropertiesEquals(expectedUserLogin, getPersistedUserLogin(expectedUserLogin));
    }

    protected void assertPersistedUserLoginToMatchUpdatableProperties(UserLogin expectedUserLogin) {
        assertUserLoginAllUpdatablePropertiesEquals(expectedUserLogin, getPersistedUserLogin(expectedUserLogin));
    }
}

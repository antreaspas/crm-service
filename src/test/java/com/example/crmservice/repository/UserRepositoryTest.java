package com.example.crmservice.repository;

import com.example.crmservice.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByUsername() {
        assertThat(userRepository.findByUsername("admin")).isEmpty();
        entityManager.persist(User.builder().username("admin").password("pass").admin(true).build());
        assertThat(userRepository.findByUsername("admin"))
                .get().extracting("username", "password", "admin")
                .containsExactly("admin", "pass", true);
    }

    @Test
    public void testCountAdmins() {
        assertThat(userRepository.countByAdminIsTrue()).isEqualTo(0);
        entityManager.persist(User.builder().username("user").password("pass").admin(false).build());
        assertThat(userRepository.countByAdminIsTrue()).isEqualTo(0);
        entityManager.persist(User.builder().username("admin1").password("pass").admin(true).build());
        entityManager.persist(User.builder().username("admin2").password("pass").admin(true).build());
        assertThat(userRepository.countByAdminIsTrue()).isEqualTo(2);
    }

    @Test
    public void testExistsByUsername() {
        entityManager.persist(User.builder().username("admin1").password("pass").admin(true).build());
        assertThat(userRepository.existsByUsername("admin1")).isTrue();
        assertThat(userRepository.existsByUsername("admin2")).isFalse();
    }

}

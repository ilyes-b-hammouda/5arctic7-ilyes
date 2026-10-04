package tn.esprit.backend.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockitoHere are the 3 test suites covering the repository, service, and controller layers, tailored to JUnit 5, Spring Boot 4.x / Spring 6 standards, and Mockito.

---

### Test Suite 1: Data JPA Repository Unit Test
This unit test validates custom queries using `@DataJpaTest` with an in-memory database context.

**File:** `src/test/java/tn/esprit/backend/repository/EquipeRepositoryTest.java`

```java
package tn.esprit.backend.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;

import java.util.List;

import static org.assertsyntax.Assertions.assertThat;

@DataJpaTest
class EquipeRepositoryTest {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Test
    @DisplayName("Should retrieve equipes by assigned entreprise ID")
    void shouldFindEquipesByEntrepriseId() {
        // Given
        Entreprise entreprise = Entreprise.builder()
                .nom("Tech Corp")
                .adresse("Tunis")
                .build();
        Entreprise savedEntreprise = entrepriseRepository.save(entreprise);

        Equipe devEquipe = Equipe.builder()
                .nom("Dev Team")
                .specialite("Java")
                .entreprise(savedEntreprise)
                .build();

        Equipe qaEquipe = Equipe.builder()
                .nom("QA Team")
                .specialite("Testing")
                .entreprise(savedEntreprise)
                .build();

        equipeRepository.save(devEquipe);
        equipeRepository.save(qaEquipe);

        List<Equipe> result = equipeRepository.findByEntrepriseId(savedEntreprise.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Equipe::getNom).containsExactlyInAnyOrder("Dev Team", "QA Team");
    }
}
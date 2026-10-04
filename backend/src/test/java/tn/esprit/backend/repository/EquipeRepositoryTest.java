package tn.esprit.backend.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.esprit.backend.entity.Equipe;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EquipeRepositoryTest {

    @Autowired
    private EquipeRepository equipeRepository;

    @Test
    @DisplayName("Should save and retrieve an Equipe successfully")
    void shouldSaveAndFindEquipeById() {
        Equipe equipe = Equipe.builder()
                .nom("Beta Team")
                .specialite("Backend")
                .build();

        Equipe savedEquipe = equipeRepository.save(equipe);
        Optional<Equipe> foundEquipe = equipeRepository.findById(savedEquipe.getId());

        assertThat(foundEquipe).isPresent();
        assertThat(foundEquipe.get().getNom()).isEqualTo("Beta Team");
    }
}
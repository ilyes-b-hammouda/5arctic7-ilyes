package tn.esprit.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Equipe;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EquipeRepositoryTest {

    @Mock
    private EquipeRepository equipeRepository;

    private Equipe sampleEquipe;

    @BeforeEach
    void setUp() {
        sampleEquipe = Equipe.builder()
                .id(1L)
                .nom("Beta Team")
                .specialite("Backend")
                .build();
    }

    @Test
    @DisplayName("Should save and retrieve an Equipe successfully")
    void shouldSaveAndFindEquipeById() {
        given(equipeRepository.save(any(Equipe.class))).willReturn(sampleEquipe);
        given(equipeRepository.findById(1L)).willReturn(Optional.of(sampleEquipe));

        Equipe savedEquipe = equipeRepository.save(sampleEquipe);
        Optional<Equipe> foundEquipe = equipeRepository.findById(savedEquipe.getId());

        assertThat(foundEquipe).isPresent();
        assertThat(foundEquipe.get().getNom()).isEqualTo("Beta Team");
    }
}
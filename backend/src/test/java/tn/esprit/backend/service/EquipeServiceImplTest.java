package tn.esprit.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.EquipeServiceImpl;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EquipeServiceImplTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe sampleEquipe;
    private Entreprise sampleEntreprise;
    private Projet sampleProjet;

    @BeforeEach
    void setUp() {
        sampleEquipe = Equipe.builder()
                .id(1L)
                .nom("Alpha")
                .specialite("Fullstack")
                .projets(new ArrayList<>())
                .build();

        sampleEntreprise = Entreprise.builder()
                .id(10L)
                .nom("Esprit")
                .adresse("Ariana")
                .build();

        sampleProjet = Projet.builder()
                .id(100L)
                .sujet("DevOps Pipeline")
                .build();
    }

    @Test
    @DisplayName("Should assign an existing entreprise to an existing equipe")
    void shouldAssignEquipeToEntreprise() {
        given(equipeRepository.findById(1L)).willReturn(Optional.of(sampleEquipe));
        given(entrepriseRepository.findById(10L)).willReturn(Optional.of(sampleEntreprise));
        given(equipeRepository.save(any(Equipe.class))).willAnswer(invocation -> invocation.getArgument(0));

        Equipe updatedEquipe = equipeService.assignEquipeToEntreprise(1L, 10L);

        assertThat(updatedEquipe).isNotNull();
        assertThat(updatedEquipe.getEntreprise()).isEqualTo(sampleEntreprise);
        verify(equipeRepository).save(sampleEquipe);
    }

    @Test
    @DisplayName("Should append a project to the equipe project list")
    void shouldAssignEquipeToProjet() {
        given(equipeRepository.findById(1L)).willReturn(Optional.of(sampleEquipe));
        given(projetRepository.findById(100L)).willReturn(Optional.of(sampleProjet));
        given(equipeRepository.save(any(Equipe.class))).willAnswer(invocation -> invocation.getArgument(0));

        Equipe updatedEquipe = equipeService.assignEquipeToProjet(1L, 100L);

        assertThat(updatedEquipe).isNotNull();
        assertThat(updatedEquipe.getProjets()).contains(sampleProjet);
        verify(equipeRepository).save(sampleEquipe);
    }
}
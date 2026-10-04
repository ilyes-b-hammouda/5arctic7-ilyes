package tn.esprit.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.service.IEntrepriseService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EntrepriseControllerTest {

    @Mock
    private IEntrepriseService entrepriseService;

    @InjectMocks
    private EntrepriseController entrepriseController;

    private Entreprise sampleEntreprise;

    @BeforeEach
    void setUp() {
        sampleEntreprise = Entreprise.builder()
                .id(1L)
                .nom("Esprit")
                .adresse("Ariana")
                .build();
    }

    @Test
    @DisplayName("Should return list of entreprises")
    void shouldReturnAllEntreprises() {
        given(entrepriseService.getAllEntreprises()).willReturn(List.of(sampleEntreprise));

        List<Entreprise> result = entrepriseController.getAllEntreprises();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Esprit");
        verify(entrepriseService).getAllEntreprises();
    }
}
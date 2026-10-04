package tn.esprit.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.service.IEntrepriseService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntrepriseController.class)
class EntrepriseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IEntrepriseService entrepriseService;

    @Test
    @DisplayName("POST /entreprise/add should create and return new Entreprise")
    void shouldCreateEntreprise() throws Exception {
        Entreprise inputEntreprise = Entreprise.builder()
                .nom("Innovation Hub")
                .adresse("Ghazela")
                .build();

        Entreprise savedEntreprise = Entreprise.builder()
                .id(1L)
                .nom("Innovation Hub")
                .adresse("Ghazela")
                .build();

        given(entrepriseService.addEntreprise(any(Entreprise.class))).willReturn(savedEntreprise);

        mockMvc.perform(post("/entreprise/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputEntreprise)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Innovation Hub"))
                .andExpect(jsonPath("$.adresse").value("Ghazela"));
    }

    @Test
    @DisplayName("GET /entreprise/all should return list of all entreprises")
    void shouldGetAllEntreprises() throws Exception {
        List<Entreprise> list = List.of(
                Entreprise.builder().id(1L).nom("Company A").adresse("Location A").build(),
                Entreprise.builder().id(2L).nom("Company B").adresse("Location B").build()
        );

        given(entrepriseService.getAllEntreprises()).willReturn(list);

        mockMvc.perform(get("/entreprise/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Company A"))
                .andExpect(jsonPath("$[1].nom").value("Company B"));
    }
}
package com.gatto.sector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.sector.service.UserSelectionService;
import com.gatto.sector.view.UserSelectionView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSelectionController.class)
class UserSelectionViewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserSelectionService userSelectionService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/user-selections -> 200 and returns the saved profile")
    void saveProfile_returnsSavedProfile() throws Exception {
        UserSelectionView requestDto = new UserSelectionView("john", List.of(1L));
        UserSelectionView savedDto   = new UserSelectionView("john", List.of(1L));

        given(userSelectionService.saveSelection(any(UserSelectionView.class)))
        .willReturn(savedDto);

        mockMvc.perform(post("/api/user-selections")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.sectorIds[0]").value(1));
    }

    @Test
    @DisplayName("GET /api/user-selections?username=john -> 200 and returns the profile")
    void getProfile_returnsProfile() throws Exception {
        UserSelectionView profile = new UserSelectionView("john", List.of(1L));

        given(userSelectionService.getSelection("john"))
                .willReturn(profile);

        mockMvc.perform(get("/api/user-selections")
                        .param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.sectorIds[0]").value(1));
    }
}

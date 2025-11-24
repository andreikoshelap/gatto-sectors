package com.gatto.sector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.sector.service.UserProfileService;
import com.gatto.sector.view.UserProfile;
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

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserProfileService userProfileService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/profile -> 200 and returns the saved profile")
    void saveProfile_returnsSavedProfile() throws Exception {
        UserProfile requestDto = new UserProfile("john", List.of(1L));
        UserProfile savedDto   = new UserProfile("john", List.of(1L));

        given(userProfileService.saveProfile(any(UserProfile.class)))
                .willReturn(savedDto);

        mockMvc.perform(post("/api/profile")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("john"))
                .andExpect(jsonPath("$.sectorIds[0]").value(1));
    }

    @Test
    @DisplayName("GET /api/profile?name=john -> 200 and returns the profile")
    void getProfile_returnsProfile() throws Exception {
        UserProfile profile = new UserProfile("john", List.of(1L));

        given(userProfileService.getProfile("john"))
                .willReturn(profile);

        mockMvc.perform(get("/api/profile")
                        .param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("john"))
                .andExpect(jsonPath("$.sectorIds[0]").value(1));
    }
}

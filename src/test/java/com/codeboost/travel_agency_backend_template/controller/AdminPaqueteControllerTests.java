package com.codeboost.travel_agency_backend_template.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AdminPaqueteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void muestraElFormularioParaUnAdministrador() throws Exception {
        mockMvc.perform(get("/dashboard/paquetes/nuevo")
                        .with(user("admin.test@hexatours.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/paquetes/form"))
                .andExpect(model().attributeExists("paquete"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("package-image-preview")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Elegir imagen")));
    }

    @Test
    void conservaElFormularioYExponeErroresPorCampo() throws Exception {
        MockMultipartFile imagenVacia = new MockMultipartFile(
                "imagen", "", "application/octet-stream", new byte[0]);

        mockMvc.perform(multipart("/dashboard/paquetes/nuevo")
                        .file(imagenVacia)
                        .param("nombre", "")
                        .param("descripcion", "corta")
                        .param("destino", "")
                        .param("categoria", "")
                        .param("orden", "0")
                        .with(csrf())
                        .with(user("admin.test@hexatours.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/paquetes/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "paquete", "nombre", "descripcion", "destino",
                        "categoria", "orden", "imagen"));
    }
}

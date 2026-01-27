package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class) // Indica que solo se testea la capa MVC, cargando HomeController
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc inyectado por Spring para simular peticiones HTTP


    /**
     * Comprueba el caso más básico:
     * <ul>
     *   <li>Ruta: {@code GET /}</li>
     *   <li>Respuesta: {@code 200 OK}</li>
     *   <li>Vista: {@code "index"}</li>
     * </ul>
     *
     * @throws Exception si falla la ejecución de MockMvc
     */
    @Test // Marca el método como test de JUnit
    @WithMockUser
    @DisplayName("GET / debe devolver 200 y renderizar la vista index") // Nombre legible en el informe de tests
    void home_shouldReturnIndexView() throws Exception { // Método de test (puede lanzar Exception para simplificar)
        mockMvc // Usamos MockMvc para ejecutar una petición HTTP simulada
                .perform(get("/").with(user("testUser"))) // Ejecuta un GET a la ruta raíz "/"
                .andExpect(status().isOk()) // Espera que el status HTTP sea 200
                .andExpect(view().name("index")); // Espera que el nombre lógico de la vista sea "index"
    }
}

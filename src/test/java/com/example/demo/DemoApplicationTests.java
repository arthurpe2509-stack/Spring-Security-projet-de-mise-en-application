package com.example.demo;

import com.example.demo.controller.AuthController;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(this.authController).isNotNull();
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ROLE_USER"})
    public void canGetBooks() throws Exception {
        this.mvc.perform(get("/api/books")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ROLE_USER"})
    public void cannotPostBooks() throws Exception {
        String bookJson = """
                    {
                        "title": "test",
                        "author": "test author",
                        "category": "test category",
                        "publicationDate": "2024-01-01",
                        "copiesAvailable": 5
                    }
                """;
        this.mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ROLE_USER"})
    public void cannotDeleteBooks() throws Exception {
        String responseJson = this.mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode books = mapper.readTree(responseJson);
        String bookId = books.get(0).get("id").asText();
        this.mvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ROLE_ADMIN"})
    public void addBooks() throws Exception{

        String bookJson = """
                    {
                        "title": "test",
                        "author": "test author",
                        "category": "test category",
                        "publicationDate": "2024-01-01",
                        "copiesAvailable": 5
                    }
                """;
        this.mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ROLE_ADMIN"})
    public void updateBooks() throws Exception{
        String responseJson = this.mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode books = mapper.readTree(responseJson);
        String bookId = books.get(0).get("id").asText();

        String updatedBookJson = """
                    {
                        "title": "updated test",
                        "author": "updated test author",
                        "category": "updated test category",
                        "publicationDate": "2024-01-01",
                        "copiesAvailable": 10
                    }
                """;
        this.mvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ROLE_ADMIN"})
    public void deleteBooks() throws Exception {
        String responseJson = this.mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode books = mapper.readTree(responseJson);
        String bookId = books.get(0).get("id").asText();
        this.mvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void postRegister() throws Exception{
        String userJson = """
                    {
                        "username":"test",
                        "password":"testmotdepasse"
    }
               """;
        this.mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andDo(print()).andExpect(status().isOk());
    }


    @Test
    public void passwordIsHashed() throws Exception {
        String plainPassword = "testmotdepasse";
        String userJson = """
            {
                "username": "test_hash",
                "password": "%s"
            }
            """.formatted(plainPassword);

        this.mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByUsername("test_hash").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo(plainPassword);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertThat(encoder.matches(plainPassword, savedUser.getPassword())).isTrue();
    }

    @Test
    public void shouldLogUser()throws Exception {
        String newUser = """
                    {
                        "username": "bastien@example.com",
                        "password": "tacostacos"
                    }
                """;
        MvcResult result= this.mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString().contains("token")).isTrue();
    }

    @Test
    public void invalidUserLogin()throws Exception {
        String invalidUser = """
                    {
                        "username": "invaliduser",
                        "password": "invalidpassword"
                    }
                """;
        this.mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUser))
                .andDo(print()).andExpect(status().isUnauthorized());
    }
}

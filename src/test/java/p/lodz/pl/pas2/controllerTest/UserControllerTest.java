package p.lodz.pl.pas2.controllerTest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import p.lodz.pl.pas2.controllers.UserController;
import p.lodz.pl.pas2.model.Client;
import p.lodz.pl.pas2.model.Moderator;
import p.lodz.pl.pas2.model.User;
import p.lodz.pl.pas2.services.UserService;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

@MockBean
 private UserService userService;


    @Test
    public void getClients() throws Exception {
        User user = new Moderator("Jaca", true);
        User user2 = new Moderator("Praca", true);
        List<User> users_list = new ArrayList<>();
        users_list.add(user);
        users_list.add(user2);
        Mockito.when(userService.getUsers()).thenReturn(users_list);
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value(user.getUsername()))
                .andExpect(jsonPath("$[0].active").value(user.isActive()))
                .andExpect(jsonPath("$[1].username").value(user2.getUsername()))
                .andExpect(jsonPath("$[1].active").value(user2.isActive()));


    }

    @Test
    public void testGetClientByNickname() throws Exception {

        User user = new Moderator("Jaca", true);
        Mockito.when(userService.getUser(Mockito.anyString())).thenReturn(user);
        mockMvc.perform(get("/api/v1/users/username/{username}", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.active").value(user.isActive()));
    }
    @Test
    public void testGetClientByID() throws Exception {

        User user = new Client("Jaca", true, "Jaca", "Jaca");
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        Mockito.when(userService.getUser(user.getId())).thenReturn(user);
        mockMvc.perform(get("/api/v1/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.active").value(user.isActive()))
                .andExpect(jsonPath("$.id").value(user.getId().toString()));;
    }


}
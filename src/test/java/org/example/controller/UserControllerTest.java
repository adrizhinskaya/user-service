package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.EmailConflictException;
import org.example.exception.EntityNotFoundException;
import org.example.exception.ErrorHandler;
import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;
import org.example.service.UserService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;
    private NewUserDto newUserDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        newUserDto = makeNewUserDto();
        userDto = new EasyRandom().nextObject(UserDto.class);
    }

    @Test
    @DisplayName("Should add a user successfully")
    void TestCreateUserSuccess() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.createdAt", is(userDto.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
    }

    @Test
    @DisplayName("Should handle exception when creating user")
    void testCreateUserWhenServiceThrowsException() throws Exception {
        when(userService.create(any()))
                .thenThrow(EmailConflictException.class);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    @DisplayName("Should get a user by id successfully")
    void testGetUserByIdSuccess() throws Exception {
        when(userService.getById(any()))
                .thenReturn(userDto);

        mvc.perform(get("/user/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.createdAt", is(userDto.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
    }

    @Test
    @DisplayName("Should handle exception when getting user")
    void testGetByIdUserWhenServiceThrowsException() throws Exception {
        when(userService.getById(any()))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/user/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Should update a user successfully")
    void testUpdateUserSuccess() throws Exception {
        when(userService.update(any(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/user/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto.getId()))
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.createdAt", is(userDto.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException when updating user")
    void testUpdateUserWhenServiceThrows404Exception() throws Exception {
        when(userService.update(any(), any()))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(patch("/user/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto.getId()))
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @DisplayName("Should handle EmailConflictException when updating user")
    @Test
    void testUpdateUserWhenServiceThrows409Exception() throws Exception {
        when(userService.update(any(), any()))
                .thenThrow(EmailConflictException.class);

        mvc.perform(patch("/user/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto.getId()))
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    @DisplayName("Should delete a user successfully")
    void testDeleteUserSuccess() throws Exception {
        mvc.perform(delete("/user/1")
                        .content(mapper.writeValueAsString(userDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

    }

    private NewUserDto makeNewUserDto() {
        return NewUserDto.builder()
                .name("John")
                .email("some@email.com")
                .age(22)
                .build();
    }
}
package org.example.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.controller.UserController;
import org.example.exception.ErrorHandler;
import org.example.service.UserService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewUserDtoValidTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        mapper.registerModule(new JavaTimeModule());
        userDto = new EasyRandom().nextObject(UserDto.class);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNames")
    @DisplayName("Should return 400 status for invalid name")
    void testCreateNewUserDtoWithInvalidName(String name) throws Exception {
        NewUserDto newUserDto = makeNewUserDto();
        newUserDto.setName(name);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @MethodSource("provideValidNames")
    @DisplayName("Should return 201 status for valid name")
    void testCreateNewUserDtoWithValidName(String name) throws Exception {
        NewUserDto newUserDto = makeNewUserDto();
        newUserDto.setName(name);

        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEmails")
    @DisplayName("Should return 400 status for invalid email")
    void testCreateNewUserDtoWithInvalidEmail(String email) throws Exception {
        NewUserDto newUserDto = makeNewUserDto();
        newUserDto.setEmail(email);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @MethodSource("provideValidEmails")
    @DisplayName("Should return 201 status for valid email")
    void testCreateNewUserDtoWithValidEmail(String email) throws Exception {
        NewUserDto newUserDto = makeNewUserDto();
        newUserDto.setEmail(email);

        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAges")
    @DisplayName("Should return 400 status for invalid age")
    void testCreateNewUserDtoWithInvalidAge(Integer age) throws Exception {
        NewUserDto newUserDto = makeNewUserDto();
        newUserDto.setAge(age);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @MethodSource("provideValidAges")
    @DisplayName("Should return 201 status for valid age")
    void testCreateNewUserDtoWithValidAge(Integer age) throws Exception {
        NewUserDto newUserDto = makeNewUserDto();
        newUserDto.setAge(age);

        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/user")
                        .content(mapper.writeValueAsString(newUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));
    }

    static Stream<Arguments> provideInvalidNames() {
        String name_1_symbol = "A";
        String name_251_symbol = new StringBuilder().append("a".repeat(251)).toString();

        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of("   ", false),
                Arguments.of(name_1_symbol, false),
                Arguments.of(name_251_symbol, false)
        );
    }

    static Stream<Arguments> provideValidNames() {
        String name_2_symbol = "AA";
        String name_250_symbol = new StringBuilder().append("a".repeat(250)).toString();

        return Stream.of(
                Arguments.of(name_2_symbol, true),
                Arguments.of(name_250_symbol, true)
        );
    }

    static Stream<Arguments> provideInvalidEmails() {
        StringBuilder emailBuilder = new StringBuilder().append("a".repeat(64))
                .append("@")
                .append("a".repeat(63))
                .append(".")
                .append("a".repeat(63))
                .append(".")
                .append("a".repeat(62));

        String email_255_symbols = emailBuilder.toString();
        String email_5_symbols = "a@b.c";

        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of("   ", false),
                Arguments.of("invalid-email", false),
                Arguments.of(email_5_symbols, false),
                Arguments.of(email_255_symbols, false)
        );
    }

    static Stream<Arguments> provideValidEmails() {
        StringBuilder emailBuilder = new StringBuilder().append("a".repeat(64))
                .append("@")
                .append("a".repeat(63))
                .append(".")
                .append("a".repeat(63))
                .append(".")
                .append("a".repeat(61));

        String email_254_symbols = emailBuilder.toString();
        String email_6_symbols = "a@b.cc";

        return Stream.of(
                Arguments.of(email_6_symbols, true),
                Arguments.of(email_254_symbols, true)
        );
    }

    static Stream<Arguments> provideInvalidAges() {
        return Stream.of(
                Arguments.of(-1, false),
                Arguments.of(0, false),
                Arguments.of(null, false)
        );
    }

    static Stream<Arguments> provideValidAges() {
        return Stream.of(
                Arguments.of(1, true)
        );
    }

    private NewUserDto makeNewUserDto() {
        return NewUserDto.builder()
                .name("John")
                .email("some@email.com")
                .age(22)
                .build();
    }
}
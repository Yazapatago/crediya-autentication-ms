package co.com.crediya.api;

import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.ErrorCode;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private static final String URL = "/api/v1/usuarios";
    private static final String BODY_VALIDO = """
            {
              "nombre": "Juan",
              "apellido": "Pérez",
              "fechaNacimiento": "1995-04-20",
              "email": "juan.perez@correo.com",
              "salarioBase": 3500000
            }
            """;

    private RegistrarUsuarioUseCase useCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        useCase = mock(RegistrarUsuarioUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registraUsuarioYRetorna201() throws Exception {
        when(useCase.registrar(any(User.class))).thenReturn(User.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@correo.com")
                .salarioBase(new BigDecimal("3500000"))
                .build());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("juan.perez@correo.com"));
    }

    @Test
    void retorna409SiElEmailYaExiste() throws Exception {
        when(useCase.registrar(any(User.class))).thenThrow(
                new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED, "El correo electrónico ya se encuentra registrado"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AUTH-004"))
                .andExpect(jsonPath("$.path").value(URL));
    }

    @Test
    void retorna400SiFallaUnaValidacion() throws Exception {
        when(useCase.registrar(any(User.class))).thenThrow(
                new BusinessException(ErrorCode.REQUIRED_FIELD, "El campo nombre es obligatorio"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El campo nombre es obligatorio"));
    }

    @Test
    void retorna400SiElCuerpoTieneFormatoInvalido() throws Exception {
        String body = """
                { "nombre": "Juan", "apellido": "Pérez", "email": "a@b.com", "salarioBase": "abc" }
                """;

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AUTH-400"));
    }

    @Test
    void retorna500ControladoAnteErrorInesperado() throws Exception {
        when(useCase.registrar(any(User.class))).thenThrow(new IllegalStateException("boom"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado, intente más tarde"));
    }

    @Test
    void retorna405SiElMetodoNoEstaPermitido() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("AUTH-405"));
    }
}

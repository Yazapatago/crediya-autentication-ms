package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.ErrorCode;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal("15000000");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final String MSG_CAMPO_OBLIGATORIO = "El campo %s es obligatorio";
    private static final String MSG_USUARIO_OBLIGATORIO = "La información del usuario es obligatoria";
    private static final String MSG_EMAIL_INVALIDO = "El campo email no tiene un formato válido";
    private static final String MSG_SALARIO_FUERA_RANGO = "El campo salarioBase debe estar entre 0 y 15000000";
    private static final String MSG_EMAIL_REGISTRADO = "El correo electrónico ya se encuentra registrado";

    private final UserRepository userRepository;

    public User registrar(User user) {
        validarDatos(user);

        String email = user.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED, MSG_EMAIL_REGISTRADO);
        }

        User usuarioNuevo = user.toBuilder()
                .id(null)
                .nombre(user.getNombre().trim())
                .apellido(user.getApellido().trim())
                .email(email)
                .build();

        return userRepository.save(usuarioNuevo);
    }

    private void validarDatos(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, MSG_USUARIO_OBLIGATORIO);
        }
        validarObligatorio(user.getNombre(), "nombre");
        validarObligatorio(user.getApellido(), "apellido");
        validarObligatorio(user.getEmail(), "email");
        validarEmail(user.getEmail());
        validarSalario(user.getSalarioBase());
    }

    private static void validarObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, String.format(MSG_CAMPO_OBLIGATORIO, campo));
        }
    }

    private static void validarEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_FORMAT, MSG_EMAIL_INVALIDO);
        }
    }

    private static void validarSalario(BigDecimal salarioBase) {
        if (salarioBase == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, String.format(MSG_CAMPO_OBLIGATORIO, "salarioBase"));
        }
        if (salarioBase.compareTo(SALARIO_MINIMO) < 0 || salarioBase.compareTo(SALARIO_MAXIMO) > 0) {
            throw new BusinessException(ErrorCode.SALARY_OUT_OF_RANGE, MSG_SALARIO_FUERA_RANGO);
        }
    }
}
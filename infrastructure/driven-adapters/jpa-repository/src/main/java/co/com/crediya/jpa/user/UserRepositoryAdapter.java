package co.com.crediya.jpa.user;

import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.ErrorCode;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private static final String MSG_EMAIL_REGISTRADO = "El correo electrónico ya se encuentra registrado";

    private final UserDataRepository userDataRepository;

    @Override
    @Transactional
    public User save(User user) {
        log.debug("Guardando usuario en base de datos");
        try {
            UserEntity saved = userDataRepository.saveAndFlush(UserEntityMapper.toEntity(user));
            log.info("Usuario guardado con id={}", saved.getId());
            return UserEntityMapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            // Respaldo ante dos registros simultáneos con el mismo email (restricción UNIQUE)
            log.warn("Violación de integridad al guardar usuario: {}", e.getMostSpecificCause().getMessage());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED, MSG_EMAIL_REGISTRADO);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userDataRepository.existsByEmail(email);
    }
}

# TODO — POST /api/usuarios

## Fase 1: Infraestrutura e domínio

- [ ] T1 — `pom.xml`: adicionar `spring-security-crypto`
- [ ] T2 — Criar `Usuario.java` + `EmailJaCadastradoException.java`
- [ ] T3 — Criar `UsuarioCadastroDTO.java` + `UsuarioRespostaDTO.java`
- [ ] T4 — Criar `UsuarioDao.java` + `UsuarioDaoImpl.java`
- [ ] T5 — Criar `UsuarioService.java` + `UsuarioServiceImpl.java`

## CHECKPOINT 1: `mvn compile` sem erros

## Fase 2: API e tratamento de erros

- [ ] T6 — Criar `UsuarioRestController.java` + `GlobalExceptionHandler.java`

## Fase 3: Testes

- [ ] T7 — Criar `UsuarioServiceImplTest.java` (3 casos unitários)
- [ ] T8 — Criar `UsuarioRestControllerTest.java` + `application-test.properties` (4 casos integração)

## CHECKPOINT 2: `mvn test` verde

## Fase 4: Verificação

- [ ] T9 — `mvn clean test` completo + smoke test manual com curl

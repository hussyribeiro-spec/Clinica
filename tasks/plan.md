# Plano de Implementação — POST /api/usuarios

## Grafo de dependências

```
T1 (pom.xml: BCrypt)
  └── T2 (Usuario + EmailJaCadastradoException)
        └── T3 (DTOs: Cadastro + Resposta)
        └── T4 (UsuarioDao + Impl)
              └── T5 (UsuarioService + Impl)  ← usa T3 + T4
                    └── T6 (Controller + ExceptionHandler)
                          └── T7 (Testes unitários — Service)
                          └── T8 (Testes integração — Controller + H2)
                                └── T9 (Verificação final)
```

---

## T1 — Adicionar dependência `spring-security-crypto`

**Arquivo:** `pom.xml`

**O que fazer:** adicionar a dependência abaixo dentro de `<dependencies>`:
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

**Por que só o módulo crypto:** evita que o Spring Security ative filtros automáticos
de autenticação que quebrariam os endpoints MVC existentes (Medico, Paciente, Consulta).

**Verificação:** `mvn dependency:resolve | grep security-crypto` retorna OK.

---

## T2 — Entidade `Usuario` + exceção `EmailJaCadastradoException`

**Arquivos:**
- `src/main/java/.../domain/Usuario.java`
- `src/main/java/.../exception/EmailJaCadastradoException.java`

**Entidade `Usuario`:**
- `@Data @Entity @Table(name = "usuario")`
- `id`: Long, `@GeneratedValue(IDENTITY)`
- `nome`: String, `@NotBlank @Size(min=2, max=120)`, `@Column(nullable=false, length=120)`
- `email`: String, `@NotBlank @Email`, `@Column(nullable=false, unique=true)`
- `senha`: String, `@NotBlank`, `@Column(nullable=false)` (armazena hash)

**`EmailJaCadastradoException`:** extends `RuntimeException`, construtor com mensagem.

**Verificação:** `mvn compile` sem erros.

---

## T3 — DTOs de entrada e saída

**Arquivos:**
- `src/main/java/.../domain/dto/UsuarioCadastroDTO.java`
- `src/main/java/.../domain/dto/UsuarioRespostaDTO.java`

**`UsuarioCadastroDTO`** (entrada — inclui senha):
- `@Data`, campos: `nome` (`@NotBlank @Size(min=2,max=120)`), `email` (`@NotBlank @Email`), `senha` (`@NotBlank @Size(min=6)`)

**`UsuarioRespostaDTO`** (saída — sem senha):
- `@Data`, campos: `id` (Long), `nome` (String), `email` (String)
- Construtor que recebe `Usuario` para facilitar a conversão

**Verificação:** `mvn compile` sem erros.

---

## T4 — `UsuarioDao` + `UsuarioDaoImpl`

**Arquivos:**
- `src/main/java/.../dao/UsuarioDao.java`
- `src/main/java/.../dao/UsuarioDaoImpl.java`

**Interface `UsuarioDao`:**
```java
void salvar(Usuario usuario);
Optional<Usuario> buscarPorEmail(String email);
```

**`UsuarioDaoImpl`** (`@Repository`):
- Injeta `EntityManager` via `@PersistenceContext`
- `salvar`: `em.persist(usuario)`
- `buscarPorEmail`: JPQL `select u from Usuario u where u.email = :email`, retorna `Optional`

**Verificação:** `mvn compile` sem erros.

---

## T5 — `UsuarioService` + `UsuarioServiceImpl`

**Arquivos:**
- `src/main/java/.../service/UsuarioService.java`
- `src/main/java/.../service/UsuarioServiceImpl.java`

**Interface `UsuarioService`:**
```java
Usuario salvar(UsuarioCadastroDTO dto);
```

**`UsuarioServiceImpl`** (`@Service @Transactional`):
1. Verifica se e-mail já existe via `usuarioDao.buscarPorEmail` → lança `EmailJaCadastradoException` se sim
2. Cria `Usuario` a partir do DTO
3. Encripta senha: `new BCryptPasswordEncoder().encode(dto.getSenha())`
4. Persiste via `usuarioDao.salvar`
5. Retorna o `Usuario` persistido

**Verificação:** `mvn compile` sem erros.

---

## CHECKPOINT 1 — Camada de domínio completa

Antes de avançar, verificar:
- [ ] T1–T5 compilam sem erros (`mvn compile`)
- [ ] Estrutura de pacotes está correta
- [ ] Nenhum arquivo MVC existente foi alterado

---

## T6 — `UsuarioRestController` + `GlobalExceptionHandler`

**Arquivos:**
- `src/main/java/.../controller/UsuarioRestController.java`
- `src/main/java/.../exception/GlobalExceptionHandler.java`

**`UsuarioRestController`** (`@RestController @RequestMapping("/api/usuarios")`):
- `POST /` com `@Valid @RequestBody UsuarioCadastroDTO`
- Chama `usuarioService.salvar(dto)`
- Retorna `ResponseEntity.status(201).body(new UsuarioRespostaDTO(usuario))`

**`GlobalExceptionHandler`** (`@RestControllerAdvice`):
- `@ExceptionHandler(EmailJaCadastradoException.class)` → `409` com `{ "erro": mensagem }`
- `@ExceptionHandler(MethodArgumentNotValidException.class)` → `400` com mapa `campo → mensagem`

**Verificação:** `mvn compile` sem erros.

---

## T7 — Testes unitários do Service

**Arquivo:** `src/test/java/.../service/UsuarioServiceImplTest.java`

**Casos:**
1. `salvar_deveEncriptarSenha` — verifica que a senha no objeto persistido é um hash BCrypt (começa com `$2a$`)
2. `salvar_deveLancarExcecaoSeEmailDuplicado` — mock retorna `Optional.of(usuario)`, verifica `assertThrows(EmailJaCadastradoException.class, ...)`
3. `salvar_devePersistirERetornarUsuario` — mock retorna `Optional.empty()`, verifica que `usuarioDao.salvar` foi chamado e retorno tem `id`, `nome`, `email`

**Verificação:** `mvn test -Dtest=UsuarioServiceImplTest` — todos passam.

---

## T8 — Testes de integração do Controller

**Arquivos:**
- `src/test/java/.../controller/UsuarioRestControllerTest.java`
- `src/test/resources/application-test.properties` (H2 em memória)

**`application-test.properties`:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

**Casos (`@SpringBootTest + MockMvc + @ActiveProfiles("test")`):**
1. `cadastrar_comDadosValidos_deveRetornar201` — body válido → 201 + id/nome/email na resposta, sem senha
2. `cadastrar_semNome_deveRetornar400` — nome em branco → 400 com campo `nome`
3. `cadastrar_emailInvalido_deveRetornar400` — e-mail malformado → 400 com campo `email`
4. `cadastrar_emailDuplicado_deveRetornar409` — cadastra o mesmo e-mail duas vezes → 409

**Verificação:** `mvn test -Dtest=UsuarioRestControllerTest` — todos passam.

---

## CHECKPOINT 2 — Testes verdes

- [ ] `mvn test` roda sem falhas
- [ ] Testes MVC existentes (se houver) continuam passando
- [ ] H2 não afeta o banco MariaDB de desenvolvimento

---

## T9 — Verificação final

1. `mvn clean test` — suite completa verde
2. Conferir que `application.properties` principal não foi alterado
3. Conferir que nenhum controller MVC existente foi modificado
4. Opcional: subir a aplicação e testar com `curl`:
   ```bash
   curl -s -X POST http://localhost:8080/api/usuarios \
     -H "Content-Type: application/json" \
     -d '{"nome":"Teste","email":"teste@email.com","senha":"senha123"}' | jq .
   ```

---

## Ordem de execução recomendada

```
T1 → T2 → T3 → T4 → T5 → [CHECKPOINT 1] → T6 → T7 → T8 → [CHECKPOINT 2] → T9
```

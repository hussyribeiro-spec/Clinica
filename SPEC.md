# SPEC — Endpoint REST de Cadastro de Usuário

## 1. Objetivo

Adicionar um endpoint REST `POST /api/usuarios` ao projeto Clínica JM que permita
cadastrar um usuário com nome, e-mail e senha. A senha é armazenada com BCrypt.
O endpoint retorna os dados do usuário criado **sem** a senha.

**Público-alvo:** qualquer cliente HTTP (Postman, front-end futuro, etc.).
**Sem autenticação** por enquanto — endpoint público.

---

## 2. Contrato da API

### Request
```
POST /api/usuarios
Content-Type: application/json

{
  "nome":  "João Silva",
  "email": "joao@email.com",
  "senha": "minhasenha123"
}
```

### Responses

| Status | Quando | Corpo |
|--------|--------|-------|
| `201 Created` | Cadastro OK | `{ "id": 1, "nome": "João Silva", "email": "joao@email.com" }` |
| `400 Bad Request` | Validação falhou | `{ "campo": "mensagem de erro", ... }` |
| `409 Conflict` | E-mail já cadastrado | `{ "erro": "E-mail já cadastrado." }` |

---

## 3. Estrutura de arquivos a criar

```
domain/
  Usuario.java                  ← entidade JPA

domain/dto/
  UsuarioCadastroDTO.java       ← dados de entrada (com senha)
  UsuarioRespostaDTO.java       ← dados de saída (sem senha)

dao/
  UsuarioDao.java               ← interface
  UsuarioDaoImpl.java           ← EntityManager (padrão do projeto)

service/
  UsuarioService.java           ← interface
  UsuarioServiceImpl.java       ← BCrypt + regra de e-mail único

controller/
  UsuarioRestController.java    ← @RestController, POST /api/usuarios

exception/
  EmailJaCadastradoException.java  ← RuntimeException semântica
  GlobalExceptionHandler.java      ← @RestControllerAdvice para 400 e 409
```

---

## 4. Entidade `Usuario`

Campos:
- `id` — Long, gerado automaticamente
- `nome` — String, obrigatório, 2–120 caracteres
- `email` — String, obrigatório, formato válido, único no banco
- `senha` — String, obrigatório (armazena o hash BCrypt)

Coluna `email` tem `unique = true` no banco.

---

## 5. Stack e dependências

| Item | Decisão |
|------|---------|
| Framework | Spring Boot 3.2.5 (já no projeto) |
| Persistência | `EntityManager` direto (padrão do projeto) |
| Validação | Bean Validation (já no projeto) |
| Hash de senha | `spring-security-crypto` (só o módulo de cripto, sem Spring Security completo) |
| Testes | JUnit 5 + Mockito (já no projeto via `spring-boot-starter-test`) |

> **Decisão a confirmar:** foi escolhido `spring-security-crypto` em vez do Spring
> Security completo para não introduzir filtros de segurança que bloqueariam os
> endpoints MVC existentes.

---

## 6. Estilo de código

Seguir o padrão já existente no projeto:
- Lombok `@Data` nas entidades
- Injeção via `@Autowired`
- `@Transactional` no `ServiceImpl`, `@Repository` no `DaoImpl`
- Nomes em português (mesma convenção de `salvar`, `recuperar`, `excluir`)
- Sem comentários óbvios; apenas onde a decisão técnica não é evidente

---

## 7. Estratégia de testes

### Unitário — `UsuarioServiceImplTest`
- `salvar()` → encripta a senha antes de persistir
- `salvar()` → lança `EmailJaCadastradoException` se e-mail duplicado
- `salvar()` → persiste e retorna o usuário

### Integração — `UsuarioRestControllerTest`
- `POST /api/usuarios` com dados válidos → `201`
- `POST /api/usuarios` com campo faltando → `400`
- `POST /api/usuarios` com e-mail duplicado → `409`

> Testes de integração usam `@SpringBootTest` + `MockMvc` com banco em memória H2.

---

## 8. Limites (o que está fora do escopo)

- **Fora:** login / geração de token JWT
- **Fora:** listagem, atualização ou exclusão de usuários
- **Fora:** Spring Security com filtros e regras de acesso
- **Fora:** endpoint MVC (Thymeleaf) para usuários — só REST

---

## 9. Critérios de aceite

- [ ] `POST /api/usuarios` com dados válidos retorna `201` e o usuário sem senha
- [ ] Campo `email` inválido retorna `400` com mensagem descritiva
- [ ] E-mail duplicado retorna `409` com mensagem clara
- [ ] A senha armazenada no banco **nunca** é texto puro (BCrypt verificável)
- [ ] Todos os testes passam (`mvn test`)
- [ ] Nenhum endpoint MVC existente é afetado

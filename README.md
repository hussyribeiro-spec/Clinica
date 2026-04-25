# Clínica JM

Sistema web para gerenciamento de médicos, pacientes e consultas.
Construído com Spring Boot 3.2.5, Spring MVC (Thymeleaf) e JPA/Hibernate.

## Tecnologias

- Java 17
- Spring Boot 3.2.5 (Web, Data JPA, Validation, Thymeleaf)
- MariaDB / MySQL
- Lombok
- Bootstrap 5

## Executar localmente

**Pré-requisitos:** Java 17, Maven 3.6+, MariaDB rodando na porta 3306.

Configure as credenciais do banco em `src/main/resources/application.properties`
(ou via variáveis de ambiente — veja a seção abaixo) e execute:

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Variáveis de ambiente para produção

Em produção, substitua as credenciais hardcoded definindo:

```
SPRING_DATASOURCE_URL=jdbc:mariadb://<host>:3306/clinica
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<senha>
```

Ative o perfil de produção para desligar o log de SQL e usar `ddl-auto=validate`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Executar testes

Os testes usam H2 em memória — não requerem banco externo.

```bash
./mvnw test
```

## API REST

### Cadastro de usuário

```
POST /api/usuarios
Content-Type: application/json

{
  "nome":  "João Silva",
  "email": "joao@email.com",
  "senha": "minhasenha123"
}
```

| Status | Quando |
|--------|--------|
| `201 Created` | Cadastro realizado — retorna `id`, `nome`, `email` (sem senha) |
| `400 Bad Request` | Campo inválido — retorna `{ "campo": "mensagem" }` |
| `409 Conflict` | E-mail já cadastrado — retorna `{ "erro": "E-mail já cadastrado." }` |

## Rotas MVC (interface web)

| Rota | Descrição |
|------|-----------|
| `GET /medicos/listar` | Lista médicos |
| `GET /medicos/cadastro` | Formulário de cadastro de médico |
| `GET /pacientes/listar` | Lista pacientes |
| `GET /consultas/listar` | Lista consultas |

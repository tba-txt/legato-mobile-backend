#  Spring Security API Template

Pequeno template de API REST em **Java 17** que demonstra autenticação e autorização
usando **Spring Boot 3**, **Spring Security** e **JWT**.  

##  Tecnologias

- Java 17
- Spring Boot 3 (SB Starter Web, Security, Data JPA)
- Maven
- Banco de dados à sua escolha (ex.: PostgreSQL, H2 ou SQLite)  
  > Basta ajustar `spring.datasource.*` em `application.properties`.

---

##  Pré‑requisitos

| Ferramenta | Versão mínima |
|------------|:---------------:|
| Java SDK   | 17 |
| Maven      | 3.8 |

---

##  Configuração rápida

1. **Clone o repositório**

   ```bash
   git clone https://github.com/<seu‑usuario>/spring-security-api-template.git
   cd spring-security-api-template

2. **Configure o application.properties**

    ```bash
    # Exemplo com postgresql
    api.security.token.secret=troque-este-segredo
    spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
    spring.datasource.username=usuario
    spring.datasource.password=senha
    spring.jpa.hibernate.ddl-auto=update

3. **Configure o application.properties**

    ```bash
    mvn spring-boot:run



## Endpoints 


| Método |    Caminho     | Acesso      | Descrição                 | 
|--------|:--------------:|-------------|---------------------------|
| POST   | /auth/register | Público     | Cadastro de novo usuário  |
| POST   |  /auth/login   | Público     | Login e obtenção do token | 
| POST   |   /products    | ADMIN only  | Exmplo de rota protegida  |
| GET/   | qualquer outro | Autenticado | Protegido por padão       |








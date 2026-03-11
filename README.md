CSV - Controle Sobre Vendas 

O CSV é uma solução de Ponto de Venda (PDV) de código aberto, projetada para ser robusta, segura e, acima de tudo, acessível. Nossa missão é empoderar pequenos empreendedores com uma ferramenta de gestão profissional sem custos de licenciamento.

🛠 Tecnologias Utilizadas
O projeto utiliza o que há de mais moderno no ecossistema Java para garantir performance e manutenibilidade:

Linguagem: Java 21 (LTS)

Framework: Spring Boot 3.4+

Banco de Dados: PostgreSQL

Migrações: Flyway (Versionamento de banco de dados)

Segurança: Spring Security + JWT (JSON Web Token)

Documentação: Swagger (OpenAPI 3)

Containers: Docker & Docker Compose

Qualidade & Testes:

JUnit 5 & Mockito (Testes Unitários)

Testcontainers (Testes de Integração com banco real em Docker)

JaCoCo (Relatórios de cobertura de código)

🔐 Pilares de Segurança
Segurança não é um opcional no CSV, é a base. Implementamos as seguintes camadas:

Autenticação Stateless: Uso de tokens JWT com expiração curta para garantir que a API seja escalável e segura.

Autorização Granular: Controle de acesso baseado em perfis (ADMIN, VENDEDOR), garantindo que cada usuário acesse apenas o que lhe é permitido.

Proteção de Dados: Criptografia de senhas utilizando o algoritmo BCrypt antes de qualquer persistência no banco.

Caminhos Blindados: Testes de integração automatizados que validam tentativas de invasão ou acesso indevido (403 Forbidden).

🗺 Roadmap (Caminho à Frente)
[x] Estrutura Inicial da API e Segurança.

[x] CRUD de Empresas e Usuários.

[x] Infraestrutura de Testes de Integração com Testcontainers.

[ ] Módulo de Gestão de Produtos e Estoque.

[ ] Registro de Vendas e Emissão de Relatórios.

[ ] Desenvolvimento do Frontend (Angular/React).
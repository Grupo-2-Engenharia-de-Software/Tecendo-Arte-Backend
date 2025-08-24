# Tecendo Arte - Backend

Este é o repositório backend do projeto **Tecendo Arte**, uma plataforma de financiamento coletivo voltada para artistas plásticos e visuais, desenvolvida como parte da disciplina de **Engenharia de Software** (UFCG - 2025.2).

## Descrição

O sistema tem como objetivo conectar artistas locais com apoiadores interessados em financiar obras de arte, promovendo cultura e incentivando a produção artística independente.

Funcionalidades principais:
- Cadastro e autenticação de usuários (artistas e apoiadores);
- Publicação de obras por artistas;
- Doações via Pix e recompensas;
- Feed público de obras disponíveis;
- Comentários e engajamento com artistas;
- Painel administrativo com métricas e gestão.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web / Spring Data JPA
- Banco de dados relacional (PostgreSQL)
- Gradle
- Lombok
- Swagger (em breve)
- Docker

## Estrutura do Projeto

src/
├── main/
│ ├── java/com/crowdfunding/tecendearte/
│ └── resources/
│ ├── static/
│ └── templates/
└── test/
└── java/com/crowdfunding/tecendearte/


## Equipe (Backend)

- Alex
- Arthur
- Gabrielly
- Rafael

## Organização do Trabalho

O gerenciamento das tarefas está sendo feito via **GitHub Projects** com issues organizadas no backlog, priorizadas por rótulos e campos personalizados.

## Como rodar localmente

### Pré-requisitos
- Java 21
- Gradle
- Docker e Docker Compose (para PostgreSQL)

### Executando a aplicação

```bash
# Clone o repositório
git clone https://github.com/SEU_USUARIO/Tecendo-Arte-Backend.git

# Navegue até o diretório
cd Tecendo-Arte-Backend

# Inicia com H2 (para desenvolvimento/testes)
./gradlew bootRun --args='--spring.profiles.active=h2'

# Inicia com PostgreSQL
# Na raiz do projeto, execute:
docker-compose up -d postgres
./gradlew bootRun

# Inicia em produção
./gradlew bootRun --args='--spring.profiles.active=prod'
```

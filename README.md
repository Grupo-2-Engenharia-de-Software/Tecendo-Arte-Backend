# Tecendo-Arte-Backend
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
- Docker (planejado)

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

```bash
# Clone o repositório
git clone https://github.com/SEU_USUARIO/Tecendo-Arte-Backend.git

# Navegue até o diretório
cd Tecendo-Arte-Backend

# Rode a aplicação
./gradlew bootRun
# Configuração PostgreSQL - Tecendo Arte Backend

Este documento explica como configurar e rodar o projeto com PostgreSQL.

## Pré-requisitos

- Docker e Docker Compose instalados
- Java 21
- Gradle

## Configuração do PostgreSQL

### 1. Iniciar o PostgreSQL com Docker

```bash
# Na raiz do projeto, execute:
docker-compose up -d postgres
```

Isso irá:
- Baixar a imagem do PostgreSQL 15
- Criar um container chamado `tecendoarte-postgres`
- Configurar o banco de dados `tecendoarte`
- Criar o usuário `tecendoarte_user` com senha `tecendoarte_password`
- Expor a porta 5432

### 2. Verificar se o PostgreSQL está rodando
Antes de continuar, é crucial verificar se o container do PostgreSQL está no status running.

```bash
# Verificar se o container está rodando
docker ps

# Verificar logs do container
docker logs tecendoarte-postgres
```
O resultado deve mostrar o container tecendoarte-postgres com o status Up (running).

Se ele não estiver running, utilize o comando ```docker logs tecendoarte-postgres``` para verificar o que pode ter ocorrido.

## Comandos úteis

### Gerenciar o container PostgreSQL

```bash
# Parar o PostgreSQL
docker-compose stop postgres

# Iniciar o PostgreSQL
docker-compose start postgres

# Parar e remover o container (dados serão perdidos)
docker-compose down postgres

# Parar e remover o container e volumes (dados serão perdidos)
docker-compose down -v postgres
```

## Troubleshooting

### Erro de conexão
- Verifique se o PostgreSQL está rodando: `docker ps`
- Verifique se a porta 5432 está livre
- Verifique as credenciais no application.properties

## Perfis de Execução

### Perfil Padrão (PostgreSQL)
```bash
# Inicia com PostgreSQL (padrão)
./gradlew bootRun
```

### Perfil H2 (Desenvolvimento/Testes)
```bash
# Inicia com H2 para desenvolvimento
./gradlew bootRun --args='--spring.profiles.active=h2'
```

### Perfil Produção
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

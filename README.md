# RESTful API em Java 

### Esse projeto consiste de uma RESTful API utilizando Spring Boot. Há autorização e autenticação por token JWT, criptografia de senhas e conexão com o banco de dados PostgreSQL.

## Instalação

1. Clone o repositório
```
git clone https://github.com/lucas-bernardino/api-java-spring.git
```
2. Entre na pasta clonada
```
cd api-java-spring
```
3. Verifique as configurações da conexão com o banco de dados
```
# Entre na pasta do arquivo application.properties
cd src/main/resources

# Preencha as varíaveis no arquivo application.properties com as suas credencias. Lembre-se de ter criado um banco de dados.

spring.datasource.url=jdbc:postgresql://localhost:5432/SEU_DATABASE
spring.datasource.username=SEU_USERNAME
spring.datasource.password=SUA_SENHA

```
4. Instale as dependências e inicie a aplicação
```
# Volte para o raiz do projeto
cd ../../..

# Instale as dependências
mvn clean install

# Inicie a aplicação
mvn spring-boot:run
```

## Estrutura do projeto

### As entidades que compõem são:

- Usuários
- Tarefas

Cada usuário pode possuir várias tarefas diferentes, porém uma tarefa pode pertencer a somente um usuário. Existem duas roles no sistema: ADMIN (administrador) e USER (usuário). O usuário pode adicionar, visualizar, deletar e visualizar somente tarefas que estejam associados com o seu ID, mas para isso é preciso primeiro estar cadastrado e logado com um token JWT válido. Cada administrador também possui um ID próprio, porém ele pode adicionar, visualizar, deletar e visualizar qualquer tarefa como também fazer essas operações em qualquer usuário do sistema.




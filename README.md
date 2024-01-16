# RESTful API em Java 

### Esse projeto consiste de uma RESTful API utilizando Spring Boot. Há autorização e autenticação por token JWT, criptografia de senhas e conexão com o banco de dados PostgreSQL.

## Instalação sem docker

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
4. Inicie o projeto inicializando o arquivo *DemoApplication.java*

5. A rota padrão para fazer as requições será http://localhost:8080

## Instalação com docker

1. Clone o repositório
```
git clone https://github.com/lucas-bernardino/api-java-spring.git
```
2. Entre na pasta clonada
```
cd api-java-spring
```
3. Certifique-se de não estar utilizando a porta `5432`
```
# Listar processo rodando na porta 5432
sudo lsof -i :5432

# Mate o processo substituindo <PID> com o seu processo
sudo kill <PID>
```

4. Suba o container
```
docker-compose up
```
5. A rota padrão para fazer as requições será http://localhost:8081


## Estrutura do projeto

### As entidades que compõem são:

- Usuários
- Tarefas

Cada usuário pode possuir várias tarefas diferentes, porém uma tarefa pode pertencer a somente um usuário. Existem duas roles no sistema: ADMIN (administrador) e USER (usuário). O usuário pode adicionar, visualizar, deletar e visualizar somente tarefas que estejam associados com o seu ID, mas para isso é preciso primeiro estar cadastrado e logado com um token JWT válido. Cada administrador também possui um ID próprio, porém ele pode adicionar, visualizar, deletar e visualizar qualquer tarefa como também fazer essas operações em qualquer usuário do sistema.

## Rotas

### Rotas de Autorização - /auth

No início do projeto, um novo usuário deve ser criado para começar a utilizar as demais rotas, visto que, exceto a roda de cadastro e login, todas as rotas só são acessíveis através do acesso do token.

1. Registrar novo usuário

- Endpoint: `/auth/register`
- Método: `post`
- Parâmetros:
```
{
    "username": "NOME_DO_USUARIO",
    "password": "SENHA_DO_USUARIO",
    "role": "ROLE_DO_USUARIO" #ADMIN ou USER
}
```
2. Logar um usuário já cadastrado
- Endpoint: `/auth/login`
- Método: `post`
- Parâmetros:
```
{
    "username": "NOME_DO_USUARIO",
    "password": "SENHA_DO_USUARIO",
}
```
- Retorno: *Um Token JWT codificado com o username do usuário*
### Rotas de Usuarios - /user
Os endpoints pertencentes a */user* permitem operações de cadastro, visualização, atualização e exclusão de usuários. 

Para acessar tais rotas, deve-se mandar um Bearer Token no *header* do método desejado, que foi obtido através do login. Somente entidades ADMIN são permitidos nessa rota.

1. Cadastrar novo usuário

- Endpoint: `/user`
- Método: `post`
- Parâmetros:
```
{
    "username": "NOME_DO_USUARIO",
    "password": "SENHA_DO_USUARIO",
    "role": "ROLE_DO_USUARIO" #ADMIN ou USER
}
```

2. Obter todos os usuários

- Endpoint: `/user`
- Método: `get`
- Retorno: *Lista de usuários cadastrados*

3. Obter usuário por ID

- Endpoint: `/user/{id}`
- Método: `get`
- Retorno: *Usuário correspondente*

4. Editar usuário

- Endpoint: `/user/{id}`
- Método: `put`
- Parâmetros:
```
{
    "password": "NOVA_SENHA_DO_USUARIO",
}
```

5. Deletar usuário

- Endpoint: `/user/{id}`
- Método: `delete`


### Rotas de Tarefas - /task

De maneira semelhante ao endpoint dos usuários, cada requisição deve ser feita através de um Bearer Token válido.

1. Cadastrar nova tarefa (Somente para ADMIN)

- Endpoint: `/task`
- Método: `post`
- Parâmetros:
```
{
    "description": "DESCRIÇÃO_DA_TAREFA",
    "user": {
        "id": "ID_DO_USUARIO_PARA_VINCULAR_A_TASK"
    }
}
```


2. Obter tarefa por ID (Somente para ADMIN)

- Endpoint: `/task/{id}`
- Método: `get`
- Retorno: *Tarefa correspondente*

3. Obter todas as tarefas de um usuário (Somente para ADMIN)

- Endpoint: `/task/user/{id}`
- Método: `get`
- Retorno: *Lista de tarefas do usuário*


4. Editar tarefa (Somente para ADMIN)

- Endpoint: `/task/{id}`
- Método: `put`
- Parâmetros:
```
{
    "description": "DESCRIÇÃO_DA_TAREFA",
}
```

5. Deletar tarefa (Somente para ADMIN)

- Endpoint: `/task/{id}`
- Método: `delete`


Os próximos endpoints correspondem as tarefas relacionadas ao próprio usuário, de modo que cada ação realizada corresponderá somente a quem enviou o token.

6. Cadastrar tarefa

- Endpoint: `/task/usertask/`
- Método: `post`
- Parâmetros:
```
{
    "description": "DESCRIÇÃO_DA_TAREFA",
}
```

7. Obter todas as tarefas do próprio usuário

- Endpoint: `/task/usertask/`
- Método: `get`
- Parâmetros:
- Retorno: *Lista de tarefas atreladas ao usuário portador do token*

8. Editar tarefa

- Endpoint: `/task/usertask/{id}`
- Método: `put`
- Parâmetros:
```
{
    "description": "NOVA_DESCRIÇÃO_DA_TAREFA",
}
```

9. Remover tarefa

- Endpoint: `/task/usertask/{id}`
- Método: `post`

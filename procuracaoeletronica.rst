API de Procuração - Roteiro Técnico
==================================

.. contents::
   :local:
   :depth: 2

Introdução
----------

A **API de Procuração do Acesso gov.br** permite que sistemas clientes integrem seus serviços
com as funcionalidades de procuração digital do governo.  
Com essa API é possível:

- Registrar o histórico de acessos realizados por aplicações que utilizam procurações.
- Consultar procurações já cadastradas, recuperando informações sobre outorgantes
  (quem delega poderes) e outorgados (quem recebe os poderes).

Objetivo
--------

Este documento descreve os serviços existentes na **API de Procuração** do acesso gov.br,
com exemplos de chamadas e explicações detalhadas para facilitar a integração.

Autenticação
------------

O acesso à API exige que o usuário esteja **autenticado via gov.br** e que o sistema
cliente obtenha um **Access Token**.  

Esse *token* deve ser enviado em todas as requisições no cabeçalho
**Authorization**, no formato:

Authorization: Bearer <access_token>


Além disso, cada serviço da API exige um **escopo específico** no token, que valida
se o sistema realmente tem permissão de uso.

Serviços Disponíveis
--------------------

A API expõe **dois serviços principais**:

1. **Histórico de acessos de Sistema Cliente**  
   Permite registrar as ações executadas por um sistema quando utiliza
   determinada procuração.

2. **Recuperação de procurações do cliente**  
   Permite consultar quais procurações estão disponíveis para um usuário
   autenticado como procurador/outorgado.


Histórico de Acessos de Sistema Cliente
---------------------------------------

Este serviço é utilizado para **registrar no sistema** cada vez que uma aplicação
usa uma procuração, garantindo que seja possível saber:

- Qual sistema acessou a procuração.
- Qual serviço foi utilizado.
- Em que momento o evento ocorreu.

**Segurança**

Para usar este serviço, é necessário que o *access token* contenha o escopo:

poav1_retrievePowersOfAttorneyByAgentAccountIdAndGrantorAccountIdAndClientIdAndIsActive_agent


**Requisição**

- **Método**: POST  
- **Endpoint**:
  ``https://api.staging.acesso.gov.br/procuracoes/v1/procuracoes/:powerOfAttorneyId/historico-acessos``

**Parâmetros**

- ``powerOfAttorneyId`` (*Path Param*): identificador da procuração.

**Corpo da Requisição \\ Body (JSON)**

.. code-block:: json

   {
     "poaAccessHistory": {
       "clientId": "exemplo.local.acesso.gov.br",
       "serviceId": 10,
       "instanteEvento": "2023-10-05T10:11:47.000"
     }
   }

Explicação dos campos:

- **clientId** → identificador único do sistema cliente.  
- **serviceId** → código numérico do serviço utilizado.  
- **instanteEvento** → data e hora em que o evento ocorreu.

**Resposta de Sucesso**

- **HTTP 201 Created**

.. code-block:: json

   {
     "id": 12345
   }

Onde ``id`` é o identificador do histórico gerado.

**Exemplos de Erro**

1. **ClientId diferente do token**

.. code-block:: json

   {
     "errors": [
       {
         "status": 403,
         "code": "REQUEST_POAACCESSHISTORY_CLIENTID_MUSTMATCHREQUESTACCESSTOKENAUD",
         "title": "O identificador do sistema deve coincidir com o sistema do access token."
       }
     ]
   }

2. **Serviço não permitido para a procuração**

.. code-block:: json

   {
     "errors": [
       {
         "status": 403,
         "code": "REQUEST_POAACCESSHISTORY_CLIENTIDANDSERVICEID_MUSTMATCHPOASERVICECLIENTIDANDSERVICEID",
         "title": "O sistema e serviço informados não coincidem com os autorizados na procuração."
       }
     ]
   }

Recuperação de Procurações do Cliente
-------------------------------------

Este serviço permite **consultar todas as procurações** nas quais um usuário é
outorgado (procurador).  
Ou seja: retorna a lista de poderes que esse usuário pode exercer em nome de
outra pessoa (outorgante).

**Exemplo de token que habilita este serviço:**

.. code-block:: json

   {
     "aud": "exemplo.staging.acesso.gov.br",
     "sub": "05297085667",
     "agent": true,
     "grantor_account": "99999999999",
     "scope": ["openid", "profile", "email"],
     "iss": "https://sso2.staging.acesso.gov.br/"
   }

Explicação:

- **agent: true** → indica que o usuário está autenticado como procurador.  
- **grantor_account** → CPF do outorgante (quem deu a procuração).  
- **sub** → CPF do usuário autenticado (outorgado).  
- **aud** → clientId da aplicação cliente.  

**Segurança**

Para usar este serviço, o *access token* precisa conter o escopo:

poav1_createPoaAccessHistory_agent


**Requisição**

- **Método**: GET  
- **Endpoint**:
  ``https://api.staging.acesso.gov.br/procuracoes/v1/procuracoes``

**Parâmetros de consulta (query params):**

- ``filtrar-por-outorgante`` → CPF do outorgante.  
- ``filtrar-por-outorgado`` → CPF do procurador/outorgado.  
- ``filtrar-por-clientid`` → clientId da aplicação cliente (deve coincidir com o claim ``aud``).  
- ``filtrar-por-situacao`` → situação da procuração (ex.: ``ativo``).

**Exemplo de requisição**

GET http://localhost:8197/procuracoes/v1/procuracoes?filtrar-por-clientid=exemplo.local.acesso.gov.br&filtrar-por-outorgado=11111111111&filtrar-por-outorgante=99999999999&filtrar-por-situacao=ativo


**Resposta de Sucesso**

.. code-block:: json

   {
     "powerOfAttorneyCollection": [
       {
         "id": 1,
         "grantorAccount": {
           "id": "11111111111",
           "name": "Fulano de Tal"
         },
         "agentAccount": {
           "id": "99999999999",
           "name": "Usuário de Teste"
         },
         "validAfter": "2023-10-02",
         "validBefore": "2024-04-26",
         "grantorAddress": "Rua A, 1403, AP 1, Belém, PA, CEP 66060160",
         "agentAddress": "Rua B, 12, Belém, PA, CEP 66060160",
         "services": [
           {
             "clientId": "exemplo.local.acesso.gov.br",
             "serviceId": 11395,
             "serviceName": "Obter imagens de sensoriamento remoto"
           }
         ],
         "createdAt": "2023-10-02T12:09:51.447",
         "status": "ACTIVE",
         "beforeTerm": false,
         "expired": false,
         "revoked": false
       }
     ]
   }

Explicação dos campos principais:

- **grantorAccount** → dados do outorgante.  
- **agentAccount** → dados do procurador.  
- **validAfter / validBefore** → período de validade da procuração.  
- **services** → lista de serviços que podem ser utilizados com esta procuração.  
- **status** → situação atual (``ACTIVE``, ``EXPIRED``, ``REVOKED``, etc.).

Considerações Finais
--------------------

- Sempre confira se o **access token** contém os **escopos exigidos**.  
- O **clientId** informado nas requisições deve coincidir com o valor presente no token.  
- Em caso de erro, a API retorna mensagens padronizadas no campo ``errors``.  


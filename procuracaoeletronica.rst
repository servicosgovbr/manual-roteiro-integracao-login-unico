API de Procuração - Roteiro Técnico
==================================

.. contents::
   :local:
   :depth: 2

Introdução
----------

A utilização da Procuração Eletrônica gov.br é uma funcionalidade disponibilizada para serviços públicos federais que já estejam integrados ao login gov.br, com o objetivo de permitir que um procurador represente um cidadão especificamente no serviço em que a funcionalidade foi implementada.
Com ela, o procurador pode acessar e realizar serviços em nome do outorgante nos sistemas que aceitam a Procuração eletrônica gov.br.

**Importante:** A Procuração eletrônica vale somente para serviços digitais integrados ao sistema gov.br. Ela não é válida em documento impresso, e não há possibilidade de reutilização ou incorporação de procurações emitidas por outros meios.

A **API de Procuração do Acesso gov.br** permite que sistemas clientes integrem seus serviços
com as funcionalidades de procuração digital do governo.  
Com essa API é possível:

- Registrar o histórico de acessos realizados por aplicações que utilizam procurações.
- Consultar procurações já cadastradas, recuperando informações sobre outorgantes
  (quem delega poderes) e outorgados (quem recebe os poderes).


##################################

Pré requisitos
--------------

- Para utilizar a procuração o sistema **obrigatoriamente deve estar integrado com o Login Único.**

- É necessário também que o serviço esteja cadastrado no Portal gov.br e que no serviço esteja marcado como serviço habilitado para procuração.
 - **Mais instruções sobre Portal de Serviços gov.br:**

  - `Guia de Edição de Serviços do Portal Gov.br`_

  - `Guia de Edição de Serviços no Portal de Serviços ORIENTAÇÕES`_

  - `Solicitar atendimento para edição de serviços do portal GOV.BR`_

.. _`Guia de Edição de Serviços do Portal Gov.br`: https://www.gov.br/pt-br/guia-de-edicao-de-servicos-do-gov.br

.. _`Guia de Edição de Serviços no Portal de Serviços ORIENTAÇÕES`: https://www.gov.br/economia/pt-br/assuntos/planejamento/cidadania-digital/arquivos/guia-de-edicao-de-servicos-no-portal_pcd.pdf

.. _`Solicitar atendimento para edição de serviços do portal GOV.BR`: https://www.gov.br/pt-br/servicos/solicitar-atendimento-para-edicao-de-servicos-do-portal-gov-br


**Atenção:** No momento, a Procuração gov.br somente pode ser utilizada por serviços públicos federais devidamente cadastrados e atualizados no Portal gov.br, com a indicação de que o serviço pode ser realizado por meio da Procuração Eletrônica gov.br.


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

Liberação da Procuração gov.br em ambiente de homologação.
--------------------------------------------

**ETAPA 1:** Solicitação de Habilitação do Escopo (Homologação)

 1. Acesse o `Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`_, e clique em **Iniciar**.
 
 2. Na aba **Dados da Solicitação**, localize o campo Qual é o tipo de solicitação? e selecione a opção **Outras Solicitações**.
 
 3. Na seção **Informações da Solicitação**, localize o campo Descreva sua Solicitação e insira o seguinte texto: **'Solicito a inclusão do escopo [escopo] para habilitação da funcionalidade Procuração Eletrônica gov.br no ambiente de homologação do Login Único para o Client [client id]'**.

 4. Clique em **Enviar Solicitação**.

**ETAPA 2**: Homologação da Funcionalidade

 1. Envie um e-mail com o vídeo demonstrativo para **integracaid@gestao.gov.br**, com cópia para **apoio-sustentacao-id@gestao.gov.br**. **Assunto**: Vídeo de Homologação - Procuração Eletrônica.

 2. Acesse o `Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`_ e clique em **Acompanhamento**.

 3. Na aba **Dados da Solicitação**, selecione **Não** na pergunta A solicitação foi atendida?

 4. No campo **Detalhar o que não foi atendido**, escreva: 'Vídeo de homologação enviado em [data].'

 5. Clique em **Mandar para Análise**.

Liberação da Procuração gov.br em ambiente de produção.
---------------------------------------------

Para a **liberação do uso da Procuração gov.br** o serviço integrado deve solicitar a habilitação pelo `Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`_, no mesmo protocolo aberto para homologação, e no campo **Considerações** informar o seguinte:

'**Solicito habilitação para uso da Procuração gov.br, no [inserir client_id de produção do Login Único gov.br].**'

É necessário também anexar vídeos demonstrando o correto funcionamento da integração no ambiente de homologação, conforme roteiro abaixo:

Vídeo 1: Deve demonstrar a emissão de uma procuração de um CPF para outro, demonstrando a seleção do serviço em questão.
Deve mostrar também a procuração ativa na lista de procurações do procurador.

Vídeo 2: Deve demonstrar o procurador acessando o serviço em questão, selecionando o CPF que irá representar no serviço, para a procuração que foi dada para ele.

Vídeo 3: Deve ser mostrado o CPF que concedeu a procuração ao PROCURADOR, visualizando o histórico de serviços que foram acessados com a procuração em seu nome.




.. _`Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`: https://www.gov.br/governodigital/pt-br/estrategias-e-governanca-digital/transformacao-digital/servico-de-pos-integracao-aos-produtos-do-ecossistema-da-identidade-digital-gov.br




Serviços Disponíveis
--------------------

A API possui **dois serviços principais**:

1. **Histórico de acessos de Sistema Cliente**  
   Permite registrar as ações executadas por um sistema quando utiliza
   determinada procuração.

   **Atenção:** O registro de uso de procuração é obrigatório!

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
- **status** → situação atual ( ``ACTIVE, CANCELED, CANCELED_BY_ACCOUNT_LOCK, CANCELED_BY_ACCOUNT_REMOVAL, CANCELED_BY_ACCOUNT_REREGISTER, EXPIRED,  RENOUNCED, REVOKED``).

Considerações Finais
--------------------

- Sempre confira se o **access token** contém os **escopos exigidos**.  
- O **clientId** informado nas requisições deve coincidir com o valor presente no token.  
- Em caso de erro, a API retorna mensagens padronizadas no campo ``errors``.  


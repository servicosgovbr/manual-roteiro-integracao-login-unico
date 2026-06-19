API de Procuração - Roteiro Técnico
==================================

.. contents::
   :local:
   :depth: 2

Introdução
----------

A **Procuração** Eletrônica é uma funcionalidade disponibilizada para serviços públicos federais integrados ao Login Único gov.br. Seu objetivo é permitir que um procurador represente legalmente um cidadão (outorgante) especificamente no serviço digital em que a funcionalidade foi implementada.

Com essa ferramenta, o procurador pode acessar sistemas e realizar serviços em nome do cidadão de forma 100% digital.

 ⚠️ **Importante**: A Procuração Eletrônica é válida apenas para serviços digitais integrados ao Login Único gov.br. Ela não possui validade em formato impresso e não substitui, nem permite a incorporação de, procurações emitidas por outros meios.

**O que é possível fazer com a API de Procuração?**

A API permite que sistemas clientes integrem seus serviços às funcionalidades de procuração digital do governo, viabilizando:

 - **Consulta de procurações**: Localizar procurações já cadastradas e recuperar dados dos outorgantes (quem delega poderes) e outorgados (quem recebe).

 - **Registro de histórico**: Rastrear e auditar os acessos realizados pelas aplicações que utilizam as procurações.

Pré requisitos e Orientações
----------------------------

Para utilizar a Procuração Eletrônica, o sistema deve obrigatoriamente cumprir os seguintes critérios:

 | 1. Estar integrado ao Login Único gov.br.

 | 2. Estar devidamente cadastrado no Portal de Serviços gov.br, com a opção de atendimento via Procuração Eletrônica ativada.

**Links Úteis e Manuais:**

  - `Guia de Edição de Serviços do Portal Gov.br`_

  - `Guia de Edição de Serviços no Portal de Serviços ORIENTAÇÕES`_

  - `Solicitar atendimento para edição de serviços do portal GOV.BR`_

.. _`Guia de Edição de Serviços do Portal Gov.br`: https://www.gov.br/pt-br/guia-de-edicao-de-servicos-do-gov.br

.. _`Guia de Edição de Serviços no Portal de Serviços ORIENTAÇÕES`: https://www.gov.br/economia/pt-br/assuntos/planejamento/cidadania-digital/arquivos/guia-de-edicao-de-servicos-no-portal_pcd.pdf

.. _`Solicitar atendimento para edição de serviços do portal GOV.BR`: https://www.gov.br/pt-br/servicos/solicitar-atendimento-para-edicao-de-servicos-do-portal-gov-br


🛑 **Atenção:** Atualmente, o recurso está restrito a serviços públicos federais devidamente atualizados no Portal de Serviços.


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

Processo de Liberação (Passo a Passo)
-------------------------------------

A liberação do uso da API ocorre em duas fases: Homologação e Produção.

**ETAPA 1: Solicitação de Habilitação do Escopo**

 | 1. Acesse o Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR e clique em Iniciar. 
 
 | 2. Na aba *Dados da Solicitação*, localize o campo *Qual é o tipo de solicitação?* e selecione **Outras Solicitações**.

 | 3. Na seção *Informações da Solicitação*, vá em *Descreva sua Solicitação* e insira o texto padrão abaixo:

  'Solicito a inclusão do escopo [inserir_escopo] para habilitação da funcionalidade Procuração Eletrônica gov.br no ambiente de homologação do Login Único para o Client Id [inserir_client_id (homologação)].'

 | 4. Clique em **Enviar Solicitação**.

**ETAPA 2: Homologação da Funcionalidade**

 | 1. Envie um e-mail com os vídeos demonstrativos (roteiro abaixo) para apoio-sustentacao-id@gestao.gov.br , com cópia para apoio-integracaid@gestao.gov.br.

  - **Assunto do e-mail**: *Vídeos de Homologação - Procuração Eletrônica*.

 | 2. Retorne ao **Portal do Serviço de Pós-Integração**, clique em **Acompanhamento** e localize o seu chamado.

 | 3. Na aba *Dados da Solicitação*, marque **Não** para a pergunta *'A solicitação foi atendida?'*.

 | 4. No campo *Detalhar o que não foi atendido*, informe o envio dos vídeos:

  'Vídeos de homologação enviados por e-mail em [DD/MM/AAAA]. Client Id Produção: [inserir_client_id (produção)]'.

 | 5. Clique em **Mandar para Análise**.

**Roteiro Obrigatório de Vídeos Demonstrativos**

É obrigatório anexar os vídeos que comprovem o funcionamento da integração no ambiente de homologação. Siga o roteiro:

 - **Vídeo 1: Emissão de Procuração**

  Deve demonstrar o fluxo de emissão de uma procuração entre CPFs, incluindo a seleção do serviço correspondente. Ao final, deve exibir a procuração ativa na lista de procurações do procurador.

 - **Vídeo 2: Acesso do Procurador**

  Deve demonstrar o procurador acessando o serviço, selecionando o CPF do representado e utilizando a procuração concedida.

 - **Vídeo 3: Histórico de Acessos**

  Deve demonstrar o CPF do outorgante (quem concedeu a procuração) visualizando o histórico de serviços que foram acessados em seu nome por meio da procuração.

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


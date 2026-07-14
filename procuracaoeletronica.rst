API de Procuração - Roteiro Técnico
==================================

.. contents::
   :local:
   :depth: 2

Introdução
----------

A **Procuração** Eletrônica é uma funcionalidade disponibilizada para serviços públicos federais integrados ao Login Único gov.br. Seu objetivo é permitir que um procurador represente legalmente um cidadão (outorgante) especificamente no serviço digital em que a funcionalidade foi implementada.

Com essa ferramenta, o procurador pode acessar sistemas e realizar serviços em nome do cidadão de forma 100% digital.

 ⚠️ **Importante**: A Procuração Eletrônica é válida apenas para serviços digitais integrados ao Login Único gov.br. Ela não possui validade em formato impresso e não substitui, nem permite a incorporação de, procurações emitidas por outros meios. Elas devem ser aceitas/utilizadas para os serviços específicos para os quais foram emitidas.

**O que é possível fazer com a API de Procuração?**

A API permite que sistemas clientes integrados ao Login Único gov.br, usem às funcionalidades de procuração eletrônica gov.br, viabilizando:

 - **Consulta de procurações**: Localizar procurações já cadastradas e recuperar dados dos outorgantes (quem delega poderes) e outorgados (quem recebe).

 - **Registro de histórico**: Rastrear e auditar os acessos realizados pelas aplicações que utilizam as procurações.

.. _pre_requisitos_e_orientacoes: 

Pré-requisitos e Orientações
----------------------------

Para utilizar a Procuração Eletrônica gov.br, o sistema deve obrigatoriamente cumprir os seguintes critérios:

 | 1. Estar integrado ao Login Único gov.br.

 | 2. Estar devidamente cadastrado no Portal gov.br, com a opção de atendimento via Procuração Eletrônica gov.br ativada.

**Links Úteis e Manuais:**

  - `Guia de Edição de Serviços do Portal Gov.br`_

  - `Guia de Edição de Serviços no Portal de Serviços ORIENTAÇÕES`_

  - `Solicitar atendimento para edição de serviços do portal GOV.BR`_

.. _`Guia de Edição de Serviços do Portal Gov.br`: https://www.gov.br/pt-br/guia-de-edicao-de-servicos-do-gov.br

.. _`Guia de Edição de Serviços no Portal de Serviços ORIENTAÇÕES`: https://www.gov.br/economia/pt-br/assuntos/planejamento/cidadania-digital/arquivos/guia-de-edicao-de-servicos-no-portal_pcd.pdf

.. _`Solicitar atendimento para edição de serviços do portal GOV.BR`: https://www.gov.br/pt-br/servicos/solicitar-atendimento-para-edicao-de-servicos-do-portal-gov-br


🛑 **Atenção:** Atualmente, o recurso está restrito a serviços públicos federais devidamente atualizados no Portal gov.br.


Objetivo
--------

Este documento descreve os serviços existentes na **API de Procuração** do gov.br, com exemplos de chamadas e explicações detalhadas para facilitar a integração.

Autenticação
------------

O acesso à API exige que o usuário esteja **autenticado via gov.br** e que o sistema
cliente obtenha um **Access Token**.  

Esse *token* deve ser enviado em todas as requisições no cabeçalho
**Authorization**, no formato:

Authorization: Bearer <access_token>


Além disso, cada serviço da API exige um **escopo específico** no token, que valida
se o sistema realmente possui permissão de uso.

Processo de Liberação (Passo a Passo)
-------------------------------------

A liberação do uso da API ocorre em duas fases: Homologação e Produção.

**ETAPA 1: Solicitação de Habilitação do Escopo**

 | 1. Acesse o `Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`_ e clique em **Iniciar**. 
 
 | 2. Na aba *Dados da Solicitação*, localize o campo *Qual é o tipo de solicitação?* e selecione **Outras Solicitações**.

 | 3. Na seção *Informações da Solicitação*, vá em *Descreva sua Solicitação* e insira o texto padrão abaixo:

  'Solicito a habilitação da funcionalidade Procuração Eletrônica gov.br no ambiente de homologação do Login Único gov.br para o Client Id [inserir_client_id (homologação)].'

 | 4. Clique em **Enviar Solicitação**.

**ETAPA 2: Homologação da Funcionalidade**

 | 1. Envie um e-mail com os vídeos demonstrativos (roteiro abaixo) para apoio-sustentacao-id@gestao.gov.br, com cópia para apoio-integracaid@gestao.gov.br.

  - **Assunto do e-mail**: *[NÚMERO DO PROTOCOLO DE PÓS INTEGRAÇÃO] - PÓS INTEGRAÇÃO - Vídeos de Homologação - Procuração Eletrônica*.

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

  Deve demonstrar o CPF do outorgante (quem concedeu a procuração) visualizando o histórico dos serviços acessados em seu nome por meio da procuração.

.. _`Portal do Serviço de Pós-Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`: https://www.gov.br/governodigital/pt-br/estrategias-e-governanca-digital/transformacao-digital/servico-de-pos-integracao-aos-produtos-do-ecossistema-da-identidade-digital-gov.br

**ETAPA 3: Habilitação da Funcionalidade em Produção**

  Após a homologação com sucesso, será habilitada a funcionalidade da Procuração Eletrônica gov.br no ambiente de produção para o client_id informado.

  **Atenção!** Para o correto funcionamento da procuração, o serviço deve atender aos `Pré Requisitos`_.  

  .. _`Pré Requisitos`: #pre-requisitos-e-orientacoes


Serviços Disponíveis
--------------------

A API possui **dois serviços principais**:

 1. **Histórico de acessos de Sistema Cliente**  
   Permite registrar as ações executadas por um sistema quando utiliza
   determinada procuração.

   **Atenção:** O registro de uso de procuração é obrigatório!

 2. **Recuperação de procurações do cliente**  
   Permite consultar quais procurações estão disponíveis para um usuário autenticado como procurador (outorgado).

   O **Swagger** com os detalhes das APIs dos serviços da Procuração Eletrônica gov.br estão nos seguintes endereços:

   **Homologação:**
   https://api.staging.acesso.gov.br/procuracoes/v2/docs/index.html

   **Produção:**
   https://api.acesso.gov.br/procuracoes/v2/docs/index.html

   **Atenção!** Utilize sempre a última versão das APIs disponível.

Histórico de Acessos de Sistema Cliente
---------------------------------------

Este serviço é utilizado para **registrar no sistema** cada vez que uma aplicação utiliza uma procuração, garantindo que seja possível saber:

- Qual sistema acessou a procuração.
- Qual serviço foi utilizado.
- Em que momento o evento ocorreu.

**Segurança**

Para usar este serviço, é necessário que o *access token* contenha o escopo:

``poav2_createPoaAccessHistory_agent``


**Requisição**

- **Método**: POST  
- **Endpoint**:
  ``https://api.staging.acesso.gov.br/procuracoes/v2/procuracoes/:poaId/historico-acessos``

**Parâmetros**

- ``poaId`` (*Path Param*): identificador da procuração.

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
- **serviceEventCreatedAtUtc** → data e hora em que o evento ocorreu em UTC.

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

Este serviço permite **consultar todas as procurações** nas quais um usuário é outorgado (procurador).  
Ou seja, retorna a lista de poderes que esse usuário pode exercer em nome de outra pessoa (outorgante).

**IMPORTANTE:** As procurações são emitidas para serviços específicos, verifique se nas procurações consta uma para o serviço específico que está sendo solicitado!!!!

**Exemplo de token que habilita este serviço:**

.. code-block:: json

   {
     "aud": "exemplo.staging.acesso.gov.br",
     "sub": "88888888888",
     "agent": true,
     "grantor_account": "99999999999",
     "scope": ["openid", "profile", "email"],
     "iss": "https://sso.staging.acesso.gov.br/"
   }

Explicação:

- **agent: true** → indica que o usuário está autenticado como procurador.  
- **grantor_account** → CPF do outorgante (quem deu a procuração).  
- **sub** → CPF do usuário autenticado (procurador).  
- **aud** → clientId da aplicação cliente.  

**Segurança**

Para usar este serviço, o *access token* precisa conter o escopo:

``poav2_retrievePoasByAgentAccountIdAndGrantorAccountIdAndClientIdAndIsActive_agent``


**Requisição**

- **Método**: GET  
- **Endpoint**:
  ``https://api.staging.acesso.gov.br/procuracoes/v2/procuracoes``

**Parâmetros de consulta (query params):**

- ``filtrar-por-outorgante`` → CPF do outorgante.  
- ``filtrar-por-procurador`` → CPF do procurador.  
- ``filtrar-por-clientid`` → clientId da aplicação cliente (deve coincidir com o claim ``aud``).  
- ``filtrar-por-situacao`` → situação da procuração (ex.: ``ativo``).

**Exemplo de requisição**

GET http://api.staging.acesso.gov.br/procuracoes/v2/procuracoes?filtrar-por-clientid=exemplo.staging.acesso.gov.br&filtrar-por-procurador=11111111111&filtrar-por-outorgante=99999999999&filtrar-por-situacao=ativo


**Resposta de Sucesso**

.. code-block:: json

  {
    "poas": [
      {
        "id": 8743,
        "grantorAccount": {
          "id": "c1b2d3e4-5f6a-7b8c-9d0e-1f2a3b4c5d6e",
          "name": "Ana Luiza Pereira",
          "address": "Rua das Flores, 245, São Paulo - SP, 01023-040, Brasil"
        },
        "agentAccount": {
          "id": "f7e8d9c0-1b2a-3c4d-5e6f-7a8b9c0d1e2f",
          "name": "Carlos Eduardo Silva",
          "address": "Avenida das Américas, 1089, Rio de Janeiro - RJ, 20031-170, Brasil"
        },
        "createdAtUtc": "2024-05-02T13:27:45.000",
        "expiresAtUtc": "2028-05-02T13:27:45.000",
        "revokedAtUtc": null,
        "renouncedAtUtc": null,
        "canceledAtUtc": null,
        "statusDetails": {},
        "status": "ACTIVE",
        "statusAtUtc": "2025-02-10T09:15:30.000",
        "statusAt": "2025-02-10T06:15:30.000",
        "createdAt": "2025-02-10T06:15:30.000",
        "expiresAt": "2030-02-10T06:15:30.000",
        "services": [
          {
            "clientId": "9a8b7c6d-5e4f-3a2b-1c0d-9e8f7a6b5c4d",
            "serviceId": 3124,
            "serviceName": "Digital Signature Service"
          },
          {
            "clientId": "3d2c1b0a-9e8f-7a6b-5c4d-3e2f1a0b9c8d",
            "serviceId": 5879,
            "serviceName": "Document Storage Service"
          }
        ]
      }
    ]
  }

Explicação dos campos principais:

- **grantorAccount** → dados do outorgante.  
- **agentAccount** → dados do procurador.  
- **createdAtUtc / expiresAtUtc** → período de validade da procuração, uma vez que os outros campos **revokedAtUtc**, **renouncedAtUtc** e **canceledAtUtc** estão nulos.  
- **services** → lista de serviços que podem ser utilizados com esta procuração.  
- **status** → situação atual ( ``ACTIVE, CANCELED, CANCELED_BY_ACCOUNT_LOCK, CANCELED_BY_ACCOUNT_REMOVAL, CANCELED_BY_ACCOUNT_REREGISTER, EXPIRED, RENOUNCED, REVOKED``).


Considerações Finais
--------------------

- Sempre confira se o **access token** contém os **escopos exigidos**.  
- O **clientId** informado nas requisições deve coincidir com o valor presente no token.  
- Em caso de erro, a API retorna mensagens padronizadas no campo ``errors``.  


Criar Chaves Teste e Produçao Login
===================================

Criar Chaves Teste e Enviar pela Ferramenta LECOM
++++++++++++++++++

Passo 1
-------
Entrar na ferramenta de LECOM: https://mgi.servicos.gov.br/

Passo 2
-------
Clicar na demanda com status Verificar Documento Assinado

Passo 3
--------
Selecionar opção Decisão: Aprovar 

Passo 4
--------
Clicar no Botão Aprovar

Passo 5
--------
Clicar no Botão Voltar

Passo 6
--------
Clicar na demanda com status Analisar Itens Homologação

Passo 7
--------
Entrar no link https://sso.staging.acesso.gov.br/manage

Passo 8
-------
Clicar no botão Novo Cliente

Passo 9
-------
Preencher o conteúdo da crdencial segundo a referência da LECOM

- Aba MAIN -> Nome do Cliente: Nome do Sistema (LECOM)
- Aba MAIN -> Client_id: Será o domínio do endereço presente na URL de retorno de Produção. Caso não possua, colocar o nome do sistema em minusculo. (LECOM)
- Aba MAIN -> Redirecionar URI(s): URL do Retorno PRODUÇãO (LECOM)
- Aba MAIN -> Home Page: URL única para página inicial do sistema (LECOM)
- Aba Outros -> Redirecionamento Pos-logout: URL de Logout HOMOLOGAÇÃO (LECOM)    

Clicar no botão Salvar

Passo 10
-------
Substitu na parte (Adicionar o client_id e secret) com o client_id e secret gerado no passo anterior e adicionar o texto ao campo Observações da Ferramenta Lecom com contéudo abaixo:

.. code-block:: text

	Prezado,

	Segue a credencial cadastrada para o ambiente de INTEGRAÇÃO/TESTES do serviço:

	(Adicionar o client_id e secret) 

	Para implementação da integração, seguir os itens do Roteiro de Integração presente no link http://manual-roteiro-integracao-login-unico.servicos.gov.br/:
	-"Iniciando a Integração"
	-"Exemplo de implementação"

	Para realização dos testes, favor seguir os passos presentes na FAQ para criar usuários: http://faq-login-unico.servicos.gov.br/en/latest/_perguntasdafaq/contaacesso.html#cadastro-com-as-informacoes-basicas-do-cidadao

	O padrão para responder as perguntas:
	-Nome da mãe: MAMAE
	-Data de Nascimento : 01/01/1980

Passo 11
--------
Selecionar Decisão HOMOLOGACÃO: Aprovar

Passo 12
--------
Clicar no Botão Aprovar


Criar Chaves Produção e Enviar pela Ferramenta LECOM
++++++++++++++++++

Passo 1
-------
Entrar na ferramenta de LECOM: https://mgi.servicos.gov.br/

Passo 2
-------
Clicar na demanda com status Analisar Itens Produção

Passo 3
-------
Entrar no link https://sso.acesso.gov.br/manage

Passo 4
-------
Clicar no botão Novo Cliente

Passo 5
-------
Preencher o conteúdo da credencial segundo a referência da LECOM

- Aba MAIN -> Nome do Cliente: Nome do Sistema (LECOM)
- Aba MAIN -> Client_id: Será o domínio do endereço presente na URL de retorno de PRODUÇÃO. Caso não possua, colocar o nome do sistema em minusculo. (LECOM)
- Aba MAIN -> Redirecionar URI(s): URL do Retorno PRODUÇÃO (LECOM)
- Aba MAIN -> Home Page: URL única para página inicial do sistema PRODUÇÃO (LECOM)
- Aba Outros -> Redirecionamento Pos-logout: URL de Logout de PRODUÇÃO (LECOM)    

Clicar no botão Salvar

Passo 6
-------
Substitu na parte (Adicionar o client_id e secret) com o client_id e secret gerado no passo anterior e adicionar o texto ao campo Observações da Ferramenta Lecom com contéudo abaixo:

.. code-block:: text

	Prezados,

	Segue credencial cadastrada para o ambiente de produção 

	(Adicionar client_id e secret)

	Realizar a troca dos seguintes domínios para acesso:

	"https://sso.staging.acesso.gov.br" por "https://sso.acesso.gov.br";
	"https://api.staging.acesso.gov.br" por "https://api.acesso.gov.br"

Passo 7
--------
Selecionar Decisão Produção: Aprovar

Passo 8
--------
Clicar no Botão Aprovar


Criar Chaves Prodção para Sistema ESUS pela Ferramenta LECOM
++++++++++++++++++

Passo 1
-------
Entrar na ferramenta de LECOM: https://mgi.servicos.gov.br/

Passo 2
-------
Clicar na demanda com status Verificar Documento Assinado

Passo 3
--------
Selecionar opção Decisão: Aprovar 

Passo 4
--------
Clicar no Botão Aprovar

Passo 5
--------
Clicar no Botão Voltar

Passo 6
--------
Clicar na demanda com status Analisar Itens Homologação

Passo 7
-------
Entrar no link https://sso.acesso.gov.br/manage

Passo 8
-------
Clicar no botão Novo Cliente

Passo 9
-------
Preencher o conteúdo da crdencial segundo a referência da LECOM

- Aba MAIN -> Nome do Cliente: Nome do Sistema (LECOM)
- Aba MAIN -> Client_id: Será o domínio do endereço presente na URL de retorno de HOMOLOGACÃO. Caso não possua, colocar o nome do sistema em minusculo. (LECOM)
- Aba MAIN -> Redirecionar URI(s): URL do Retorno HOMOLOGAÇÃO (LECOM)
- Aba MAIN -> Home Page: URL única para página inicial do sistema HOMOLOGAÇÃO(LECOM)
- Aba Outros -> Redirecionamento Pos-logout: URL de Logout HOMOLOGAÇÃO (LECOM)    

Clicar no botão Salvar

Passo 10
-------
Substitu na parte (Adicionar o client_id e secret) com o client_id e secret gerado no passo anterior e adicionar o texto ao campo Observações da Ferramenta Lecom com contéudo abaixo:

.. code-block:: text

	Prezados,

	Segue a credencial de acesso para o Login Único no eSUS APS dentro do arquivo application.properties:

	(Adicionar o client_id e secret)

Passo 11
--------
Selecionar Decisão HOMOLOGACÃO: Aprovar

Passo 12
--------
Clicar no Botão Aprovar


Homologar integração Login para permitir chave produção
++++++++++++++++++

Passo 1
-------
Entrar na ferramenta de LECOM: https://mgi.servicos.gov.br/

Passo 2
-------
Clicar na demanda com status Homologar Login

Passo 3
-------
Fazer o Download do vídeo em Favor adicionar vídeo que demonstre a integração ao Login gov.br

Passo 4
-------
Verificar se no video possui a entrada do serviço com botão entrar com gov.br. Seguir exemplo do link: https://acesso.gov.br/roteiro-tecnico/arquivos/exemplo_comprovacao_integracao.mp4

Passo 5 (Caso o video esteja correto)
--------
Selecionar Decisão Produção: Aprovar 

Passo 6
--------
Clicar no Botão Aprovar

Passo 7 (Caso o video esteja errado)
--------
Selecionar Decisão Produção: Solicitar Ajuste 

Passo 8
-------
Escrever o motivo no campo Observações

Passo 9
-------
Clicar no Botão Solicitar Ajuste


.. |site externo| image:: _images/site-ext.gif
.. _`codificador para Base64`: https://www.base64decode.org/
.. _`Plano de Integração`: arquivos/Modelo_PlanodeIntegracao_LOGINUNICO_Versao-4.doc
.. _`OpenID Connect`: https://openid.net/specs/openid-connect-core-1_0.html#TokenResponse
.. _`auth 2.0 Redirection Endpoint`: https://tools.ietf.org/html/rfc6749#section-3.1.2
.. _`Exemplos de Integração`: exemplointegracao.html
.. _`Design System de Governo`: https://webcomponent-ds.estaleiro.serpro.gov.br/?path=/story/componentes-signin--tipo-externo-com-texto
.. _`Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)`: iniciarintegracao.html#resultado-esperado-do-acesso-ao-servico-de-confiabilidade-cadastral-selos
.. _`Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Categorias)` : iniciarintegracao.html#resultado-esperado-do-acesso-ao-servico-de-confiabilidade-cadastral-categorias
.. _`Documento verificar Código de Compensação dos Bancos` : arquivos/TabelaBacen.pdf
.. _`administrar as chaves PGP para credenciais do Login Único`: chavepgp.html
.. _`RFC PKCE`: https://datatracker.ietf.org/doc/html/rfc7636
.. _`Passo 3`: iniciarintegracao.html#passo-3
.. _`Ajuda para geração do code_challenge`: https://tonyxu-io.github.io/pkce-generator/
.. _`Credencial de Teste para Login Único`: solicitacaocredencial.html#credencial-de-teste-para-login-unico
.. _`Credencial de Produção para Login Único`: solicitacaocredencial.html#credencial-de-producao-para-login-unico
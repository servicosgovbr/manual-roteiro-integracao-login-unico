Iniciando a Integração
=====================

Solicitação de Configuração
+++++++++++++++++++++++++++

Para utilização do sistema Brasil Cidadão, há necessidade de liberar os ambientes para aplicação cliente possa utilizar. Essa liberação ocorre por meio do preenchimento do `Plano de Configuração`_.

O formulário deverá ser encaminhado para os integrantes da Secretaria de Tecnologia da Informação e Comunicação (SETIC) do Ministério do Planejamento para realizar configuração da utilização do Brasil Cidadão.

Métodos e interfaces de integração (Passo-a-Passo para Integrar)
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Autenticação
------------

Para que a autenticação aconteça, todo o canal de comunicação deve ser realizado com o protocolo HTTPS.

1. Ao requisitar autenticação via Provedor, o mesmo verifica se o usuário está logado. Caso o usuário não esteja logado o provedor redireciona para a página de login.

2. A requisição é feita através de um GET para o endereço https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/authorize passando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**response_type**  Especifica para o provedor o tipo de autorização. Neste caso será **code**
**client_id**      Chave de acesso, que identifica o serviço consumidor fornecido pelo Brasil Cidadão para a aplicação cadastrada
**scope**          Especifica os recursos que o serviço consumidor quer obter. Um ou mais escopos inseridos para a aplicação cadastrada. Se for mais de um, esta informação deve vir separada pelo caractere “+”.
**redirect_uri**   URI de retorno cadastrada para a aplicação cliente no formato *URL Encode*
**nonce**          Sequência de caracteres usado para associar uma sessão do serviço consumidor a um *Token* de ID e para atenuar os ataques de repetição. Pode ser um valor aleatório, mas que não seja de fácil dedução. Item obrigatório.
**state**          Valor usado para manter o estado entre a solicitação e o retorno de chamada. Item não obrigatório. 
=================  ======================================================================

Exemplo de requisição:

.. code-block:: console

	https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/authorize?response_type=code&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544&scope=openid+brasil_cidadao&redirect_uri=http://appcliente.com.br/phpcliente/loginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

3. Após autenticado, o provedor redireciona para a página de autorização. O usuário habilitará o consumidor no sistema para os escopos solicitados. Caso o usuário da solicitação autorize o acesso ao recurso protegido, é gerado um “ticket de acesso” intitulado *access_token* (vide especificação *OAUTH 2.0*);

4. Após a autorização, a requisição é retornada para a URL especificada no passo 1, enviando os seguintes parâmetros: code=Z85qv1e state=358578ce6728b. Lembrando que para essa requisição o *code* têm um tempo de expiração e só pode ser utilizado uma única vez;

5. Para obter o *token* e o *access_token*, o consumidor deve fazer uma requisição POST para o endereço https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token passando as seguintes informações:

Parâmetros do Header para requisição Post https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Content-Type**   Tipo do conteúdo da requisição que está sendo enviada. Nesse caso estamos enviando como um formulário
**Authorization**  Informação codificada em *Base64*, no seguinte formato: CLIENT_ID:CLIENT_SECRET (senha de acesso do serviço consumidor)(utilizar `codificador para Base64`_ |site externo|  para gerar codificação). A palavra Basic deve está antes da informação. 
=================  ======================================================================
	
Exemplo de *header*:

.. code-block:: console

	Content-Type:application/x-www-form-urlencoded
	Authorization: Basic											
	ZWM0MzE4ZDYtZjc5Ny00ZDY1LWI0ZjctMzlhMzNiZjRkNTQ0OkFJSDRoaXBfTUJYcVJkWEVQSVJkWkdBX2dRdjdWRWZqYlRFT2NWMHlFQll4aE1iYUJzS0xwSzRzdUVkSU5FcS1kNzlyYWpaZ3I0SGJu VUM2WlRXV1lJOA==

Parâmetros da Query para requisição Post https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**grant_type**     Especifica para o provedor o tipo de autorização. Neste caso será **authorization_code**
**code**           Código retornado pela requisição anterior (exemplo: Z85qv1)
**redirect_uri**   URI de retorno cadastrada no Brasil Cidadão no formato *URL Encode*
=================  ======================================================================

Exemplo de requisição

.. code-block:: console

	https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token?grant_type=authorization_code&code=Z85qv1&redirect_uri=http://appcliente.com.br/phpcliente/loginecidadao.Php	

O serviço retornará, em caso de sucesso, a informação, no formato JSON, conforme exemplo:

.. code-block:: JSON

	{ 
		"access_token": "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI2ODI1NjQwNzA0MiIsImF6cCI6IjQ1ZGYzZWJjLTkwZjItNDMwMy1iMmQyLWUwY2ZiZjhkOWEwZCIsInNjb3BlIjpbXSwibmFtZSI6InRlc3RlIGVtcHJlc2EgaW5tZXRybyIsImlzcyI6Imh0dHBzOlwvXC90ZXN0ZXNjcC1lY2lkYWRhby5lc3RhbGVpcm8uc2VycHJvLmdvdi5iclwvc2NwXC8iLCJleHAiOjE1NTA2MTQ0NDIsImlhdCI6MTU1MDYxMDg0MiwiYXV0aF9mYWN0b3IiOiJDUEZfU0VOSEEiLCJqdGkiOiJhMGJlYmM1Mi1hYWQ5LTRlNzktYWEzNC03YTUzMWU0ZmE4ZDUifQ.dM-lUCSUU2vvWJruR9pMuUTf3_0qMo2JQFCccthn0dfc6cyUG-e_Vdl7t1j4bxrXk2IKx_8oEMk9c9csDzLxVx7HIy3mKp9pA2VmRGGU5FD3pUrAqkOgwGns0s9P0eCCIQKd_ylyUisPJwRroow7g72ldrCxm8BJneG4MX5soWHiiMfnu0IWSBiKQuQJ7fRfkJJC6Cxveq4AtZJ4mID3tPK496rFMFsY1RytsI-ed_Q_dGj6XxiEQpAlHiLCgxynrhIVMOyjU20h8FOWGWxE3rtr14Dl1fl6rvXp8wl5BJGurinj2kZjfe_HI1TJR0ykR84YibMM34DqJ93hseJLNw", 
		"token_type": "Bearer", 
		"expires_in": 3599 
	} 

Ou , no caso de falha, a informação, conforme exemplo abaixo:

.. code-block:: JSON

	{
		"error":"invalid_request"
	}

6. De posse das informações de *token* e *access token*, a aplicação consumidora já está habilitada para consultar dados de recursos protegidos, que são os escopos de informações. Deve fazer uma requisição GET para o endereço https://testeservicos-ecidadao.estaleiro.serpro.gov.br/servicos-ecidadao/ecidadao/usuario/getUserInfo/brasil_cidadao passando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token
=================  ======================================================================

Exemplo de retorno do barramento de serviços no formato JSON:

.. code-block:: JSON

	{
		"cpf": "88918894588",
		"nome": "HENRIQUE PRETORIUM ",
		"email": "henrique.pretorium@enterprisex.gov.br",
		"telefone": "00000000",
		"foto":"informacao da foto em formato base 64 com tamanho até 4 MB"
	}

Resultados Esperados do Acesso ao Serviços de Autenticação	
----------------------------------------------------------

Os acessos aos serviços do Brasil Cidadão ocorrem por meio de chamadas de URLs e a resposta são códigos presentes conforme padrão do protocolo http. Estes códigos são:

- **Código 200**: Dados acessados e retornados em formato JSON ao usuário, de acordo com o JSON de cada escopo;
- **Código 400**: Token recebido por mais de um método;
- **Código 401**: Token não encontrado ou inválido , CPF inválido, usuário não existente no sistema, access token inválido;
- **Código 403**: Escopo solicitado não autorizado pelo usuário;
- **Código 404**: Escopo obrigatório.

Acesso ao Serviço de Cadastro de Pessoas Jurídicas
--------------------------------------------------

O Brasil Cidadão disponibiliza dois serviços para acesso a informações de Pessoa Jurídica. O primeiro apresenta todos os CNPJs cadastrados para um determinado usuário. O segundo, utiliza desse CNPJ para extrair informações cadastradas no Brasil Cidadão para aquela pessoa e empresa.

Para acessar o serviço que disponibiliza os CNPJs vinculados a um determinado usuário, é necessário o seguinte:

1. Na requisição de autenticação, adicionar o escopo “brasil_cidadao_empresa“, conforme exemplo:

Exemplo de requisição

.. code-block:: console

	https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/authorize?response_type=code&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544&scope=openid+brasil_cidadao+brasil_cidadao_empresa&redirect_uri=http://appcliente.com.br/phpcliente/loginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

2. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://testeservicos-ecidadao.estaleiro.serpro.gov.br/servicos-ecidadao/ecidadao/servicos-ecidadao/ecidadao/usuario/getConfiabilidade enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token
=================  ======================================================================

3. O resultado em formato JSON são selos de confiabilidade da autenticação. O delo a ser verificado será o “Representante Legal do CNPJ”, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON
	
	{
		"id": 0,
		"nivel": 11,
		"descricao": "REPRESENTANTE E-CNPJ"
	}

4. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://testeservicos-ecidadao.estaleiro.serpro.gov.br/servicos-ecidadao/ecidadao/empresa/escopo/brasil_cidadao_empresa enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token
=================  ======================================================================

5. O resultado em formato JSON é a lista de CNPJs do CPF autenticado, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	{
		"cnpjs":
		[
			{
			 "cnpj": "CNPJ da empresa",
			 "nome": "NOME FANTASIA DA EMPRESA"
			},
			
		],
		"cpf": "CPF do representante da empresa"
	}

6. Com o usuário autenticado, a aplicação cliente deverá acessar, por meio do método GET, a URL: https://testeservicos-ecidadao.estaleiro.serpro.gov.br/servicos-ecidadao/ecidadao/empresa/"cnpj"/escopo/brasil_cidadao_empresa enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token
**cnpj**           CNPJ da empresa formatado (sem ponto, barra etc).
=================  ======================================================================

7. O resultado em formato JSON é o detalhamento do CNPJ do CPF autenticado, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	{
		"cnpj": "CNPJ", 
		"nomeFantasia": "NOME FANTASIA",
		"atuacao": "ATUACÃO tendo o valor SOCIO, CONTADOR e REPRESENTANTE_LEGAL",
		"cpfResponsavel": "CPF DO RESPONSÁVEL",
		"nomeResponsavel": "NOME DO RESPONSÁVEL"
	}

Resultados Esperados do Acesso ao Serviço de Cadastro de Pessoas Jurídicas
--------------------------------------------------------------------------

Os acessos aos serviços do Brasil Cidadão ocorrem por meio de chamadas de URLs e a resposta são códigos presentes conforme padrão do protocolo http. Estes códigos são:

- **Código 200**: Dados acessados e retornados em formato JSON ao usuário, de acordo com o JSON de cada escopo;
- **Código 400**: Token recebido por mais de um método;
- **Código 401**: Token não encontrado ou inválido , CNPJ inválido, usuário não existente no sistema, access token inválido;
- **Código 403**: Escopo solicitado não autorizado pelo usuário;
- **Código 404**: Escopo obrigatório.

Acesso ao Serviço de Confiabilidade Cadastral (Selos)
-----------------------------------------------------

Para acessar o serviço de consulta de empresas é necessário:

1. Com usuário autenticado, deverá acessar, por meio do método GET, a URL: https://testeservicos-ecidadao.estaleiro.serpro.gov.br/servicos-ecidadao/ecidadao/usuario/getConfiabilidade;

Parâmetros do Header para requisição GET "https://testeservicos-ecidadao.estaleiro.serpro.gov.br/servicos-ecidadao/ecidadao/usuario/getConfiabilidade"

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://testescp-ecidadao.estaleiro.serpro.gov.br/scp/token
=================  ======================================================================

2. A resposta em caso de sucesso retorna sempre um *array* de objetos JSON no seguinte formato:

.. code-block:: JSON

	{
		"id" : "Número do selo cadastrado no Login Único",
		"nivel" : "Escala do nível presente no conceito do Login Único",
		"descricao" : "nome do selo cadastrado no Login Único"
	}
	
	
Resultados Esperados do Acesso ao Serviço de Confiabilidade Cadastral (Selos)
-----------------------------------------------------------------------------

Os selos existentes no Brasil Cidadão são:

.. code-block:: JSON

	[
		{
			"id": 0,
			"nivel": 2,
			"descricao": "Institucional (Servidor Público)" 
		},
		{
			"id": 0,
			"nivel": 1,
			"descricao": "Conformidade"
		},
		{
			"id": 0,
			"nivel": 4,
			"descricao": "Biometria"
		},
		{
			"id": 0,
			"nivel": 5,
			"descricao": "Certificado Digital" 
		},	
		{	
			"id": 0,
			"nivel": 3,
			"descricao": "Convalidação (Módulo Balcão)" 
		},
		{
			"id": 0,
			"nivel": 10,
			"descricao": "DNI"
		},
		{
			"id": 0,
			"nivel": 11,
			"descricao": "REPRESENTANTE E-CNPJ"
		}
	]

	


.. |site externo| image:: _images/site-ext.gif
.. _`codificador para Base64`: https://www.base64decode.org/
.. _`Plano de Configuração`: arquivos/plano-configuracao-brasil-cidadao-v9.doc

Iniciando a Integração
=====================

Solicitação de Configuração
+++++++++++++++++++++++++++

Para utilização do sistema Login Único, há necessidade de liberar os ambientes para aplicação cliente possa utilizar. Essa liberação ocorre por meio do preenchimento do `Plano de Configuração`_.

O formulário deverá ser encaminhado para os integrantes da Secretaria de Governança Digital (SGD) do Ministério da Economia (ME) para realizar configuração da utilização do Login Único.

Métodos e interfaces de integração (Passo-a-Passo para Integrar)
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Autenticação
------------

Para que a autenticação aconteça, todo o canal de comunicação deve ser realizado com o protocolo HTTPS. Será feito um redirecionamento para uma URL de autorização do Login Único e, após a autenticação ser concluída, retornará um código de autenticação para a aplicação cliente com intuito de adquirir um ticket de acesso para os serviços protegidos.

A utilização da autenticação do Login Único depende dos seguintes passos:

1. A chamada para autenticação deverá ocorrer pelo botão com o conteúdo **Entrar com GOV.BR**.

2. Ao requisitar autenticação via Provedor, o mesmo verifica se o usuário está logado. Caso o usuário não esteja logado o provedor redireciona para a página de login. 

3. A requisição é feita através de um GET para o endereço https://sso.staging.acesso.gov.br/authorize passando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**response_type**  Especifica para o provedor o tipo de autorização. Neste caso será **code**
**client_id**      Chave de acesso, que identifica o serviço consumidor fornecido pelo Login Único para a aplicação cadastrada
**scope**          Especifica os recursos que o serviço consumidor quer obter. Um ou mais escopos inseridos para a aplicação cadastrada. Informação a ser preenchida por padrão: **openid+email+phone+profile**. 
**redirect_uri**   URI de retorno cadastrada para a aplicação cliente no formato *URL Encode*. Este parâmetro não pode conter caracteres especiais conforme consta na especificação `auth 2.0 Redirection Endpoint`_
**nonce**          Sequência de caracteres usado para associar uma sessão do serviço consumidor a um *Token* de ID e para atenuar os ataques de repetição. Pode ser um valor aleatório, mas que não seja de fácil dedução. Item obrigatório.
**state**          Valor usado para manter o estado entre a solicitação e o retorno de chamada. Item não obrigatório. 
=================  ======================================================================

Exemplo de requisição:

.. code-block:: console

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544&scope=openid+email+phone+profile&redirect_uri=http://appcliente.com.br/phpcliente/loginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

4. Após a autorização, a requisição é retornada para a URL especificada no redirect_uri do serviço https://sso.staging.acesso.gov.br/authorize, enviando os parâmetros:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**code**           Código de autenticação gerado pelo provedor. Será utilizado para obtenção do Token de Resposta. Possui tempo de expiração e só pode ser utilizado uma única vez. 
**state**          *State* passado anteriormente do https://sso.staging.acesso.gov.br/authorize que pode ser utilizado para controle da aplicação cliente. Pode correlacionar com o *code* gerado.  
=================  ======================================================================

4. Após autenticado, o provedor redireciona para a página de autorização. O usuário habilitará o consumidor no sistema para os escopos solicitados. Caso o usuário da solicitação autorize o acesso, é gerado um “ticket de acesso”, conforme demonstra na especificação `OpenID Connect`_ ;

5. Para obter o *ticket de acesso*, o consumidor deve fazer uma requisição POST para o endereço https://staging.acesso.gov.br/token passando as seguintes informações:

Parâmetros do Header para requisição Post https://sso.staging.acesso.gov.br/token

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
	ZWM0MzE4ZDYtZjc5Ny00ZDY1LWI0ZjctMzlhMzNiZjRkNTQ0OkFJSDRoaXBfTUJYcVJkWEVQSVJkWkdBX2dRdjdWRWZqYlRFT2NWMHlFQll4aE1iYUJzS0xwSzRzdUVkSU5FcS1kNzlyYWpaZ3I0SGJuVUM2WlRXV1lJOA==

Parâmetros da Query para requisição Post https://sso.staging.acesso.gov.br/token
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**grant_type**     Especifica para o provedor o tipo de autorização. Neste caso será **authorization_code**
**code**           Código retornado pela requisição anterior (exemplo: Z85qv1)
**redirect_uri**   URI de retorno cadastrada para a aplicação cliente no formato *URL Encode*. Este parâmetro não pode conter caracteres especiais conforme consta na especificação `auth 2.0 Redirection Endpoint`_
=================  ======================================================================

Exemplo de *query*

.. code-block:: console

	https://sso.staging.acesso.gov.br/token?grant_type=authorization_code&code=Z85qv1&redirect_uri=http://appcliente.com.br/phpcliente/loginecidadao.Php	

O serviço retornará, em caso de sucesso, no formato JSON, as informações conforme exemplo:

.. code-block:: JSON

	{ 
		"access_token": "(Token de acesso a recursos protegidos do autenticador, bem como serviços do Login Único.)", 
		"id_token": "(Token de autenticação com informações básicas do usuário.)", 
		"token_type": "(O tipo do token gerado. Padrão: Bearer)", 
		"expires_in": "(Tempo de vida do token em segundos.)" 
	} 

6. De posse das informações do json anterior, a aplicação consumidora está habilitada para consultar dados de recursos protegidos, que são as informações e método de acesso do usuário ou serviços externos do Login Único. 

7. Antes de utilizar as informações do JSON anterior, de forma especifica os **ACCESS_TOKEN** e **ID_TOKEN**, para buscar informações referente ao método de acesso e cadastro básico do usuário, há necessidade da aplicação consumidora validar se as informações foram geradas pelos serviços do Login Único. Esta validação ocorrerá por meio da consulta da chave pública disponível no serviço https://sso.staging.acesso.gov.br/jwk. Para isso, verificar o método **processToClaims** dos `Exemplos de Integração`_.    

8. A utilização das informações do **ACCESS_TOKEN** e **ID_TOKEN** ocorrerá ao extrair do JSON codificado os seguintes parâmetros: 

**JSON do ACCESS_TOKEN**

.. code-block:: JSON

	{
		"sub": "(CPF do usuário autenticado)",
		"aud": "Client ID da aplicação onde o usuário se autenticou",
		"scope": ["(Escopos autorizados pelo provedor de autenticação.)"],
		"amr": "(Fator de autenticação do usuário. Pode ser “passwd” se o mesmo logou fornecendo a senha, ou “x509” se o mesmo utilizou certificado digital ou certificado em nuvem.)",
		"iss": "(URL do provedor de autenticação que emitiu o token.)",
		"exp": "(Data/hora de expiração do token)",
		"iat": "(Data/hora em que o token foi emitido.)",
		"jti": "(Identificador único do token, reconhecido internamente pelo provedor de autenticação.)",
		"cnpj": "CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica."
	}

**JSON do ID_TOKEN**

.. code-block:: JSON

	{
		"sub": "(CPF do usuário autenticado.)",
		"amr": "(Fator de autenticação do usuário. Pode ser “passwd” se o mesmo logou fornecendo a senha, ou “x509” se o mesmo utilizou certificado digital ou certificado em nuvem.)",
		"picture": "(URL de acesso à foto do usuário cadastrada no Gov.br. A mesma é protegida e pode ser acessada passando o access token recebido.)",
		"name": "(Nome cadastrado no Gov.br do usuário autenticado.)",
		"phone_number": "(Número de telefone cadastrado no Gov.br do usuário autenticado.)",
		"email": "(Endereço de e-mail cadastrado no Gov.br do usuário autenticado.)",
		"cnpj": "(CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica.)"
	}

9. Para solicitação do conteúdo da foto salva no cadastro do cidadão, deverá acessar, pelo método GET, o serviço https://sso.staging.acesso.gov.br/userinfo/picture e acrescentar o atributo Authorization ao header do HTTP da requisição:
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

O serviço retornará, em caso de sucesso a informação em formato Base64

Acesso ao Serviço de Confiabilidade Cadastral (Selos)
-----------------------------------------------------

1. Com usuário autenticado, deverá acessar, por meio do método GET, a URL: https://api.staging.acesso.gov.br/api/info/usuario/selo;

Parâmetros do Header para requisição GET https://api.staging.acesso.gov.br/api/info/usuario/selo

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

2. A resposta em caso de sucesso retorna sempre um *array* de objetos JSON no seguinte formato:

.. code-block:: JSON

	{
		"id" : "(Número do selo cadastrado no Login Único)",
		"nivel" : "(Escala do nível presente no conceito do Login Único)",
		"descricao" : "(nome do selo cadastrado no Login Único)"
	}
	
	
Resultados Esperados do Acesso ao Serviço de Confiabilidade Cadastral (Selos)
-----------------------------------------------------------------------------

Os selos existentes no Login Único são:

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


Acesso ao Serviço de Cadastro de Pessoas Jurídicas
--------------------------------------------------

O Login Único disponibiliza dois serviços para acesso a informações de Pessoa Jurídica. O primeiro apresenta todos os CNPJs cadastrados para um determinado usuário. O segundo, utiliza desse CNPJ para extrair informações cadastradas no Login Único para aquela pessoa e empresa.

Para acessar o serviço que disponibiliza os CNPJs vinculados a um determinado usuário, é necessário o seguinte:

1. Na requisição de autenticação, adicionar o escopo “govbr_empresa“, conforme exemplo:

Exemplo de requisição

.. code-block:: console

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=minha-aplicacao&scope=openid+profile+email+phone+govbr_empresa&redirect_uri=http://appcliente.com.br/phpcliente/loginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b 

2. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://api.staging.acesso.gov.br/api/info/usuario/selo enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

3. O resultado em formato JSON são selos de confiabilidade da autenticação. O selos a serem verificados serão o “Representante Legal do CNPJ”, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	[	
		{
			"id": 0,
			"nivel": 11,
			"descricao": "REPRESENTANTE E-CNPJ"
		}
	]
	
4. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://api.staging.acesso.gov.br/api/empresas/escopo/govbr_empresa enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

5. O resultado em formato JSON é a lista de CNPJs do CPF autenticado, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	{
		"cnpjs":
		[
			{
			 "cnpj": "(CNPJ da empresa)",
			 "nome": "(NOME FANTASIA DA EMPRESA)"
			},
			
		],
		"cpf": "(CPF do representante da empresa)"
	}

6. Com o usuário autenticado, a aplicação cliente deverá acessar, por meio do método GET, a URL https://api.staging.acesso.gov.br/api/empresa/**cnpj**/escopo/govbr_empresa enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cnpj**           CNPJ da empresa formatado (sem ponto, barra etc).
=================  ======================================================================

7. O resultado em formato JSON é o detalhamento do CNPJ do CPF autenticado, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	{
		"cnpj": "(Número do CNPJ)", 
		"nomeFantasia": "(NOME FANTASIA)",
		"atuacao": "(ATUACÃO tendo o valor SOCIO, CONTADOR e REPRESENTANTE_LEGAL)",
		"cpfResponsavel": "(CPF DO RESPONSÁVEL)",
		"nomeResponsavel": "(NOME DO RESPONSÁVEL)"
	}

Acesso ao Serviço de Informações cadastradas pelo Balcão (Selo Nível 3)
----------------------------------------------------------------------

O Login Único disponibiliza o serviço para recuperar as informações apresentadas no balcão pelo cidadão.

Para acessar o serviço é necessário:

1. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://api.staging.acesso.gov.br/api/info/usuario/selo enviando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

2. O resultado em formato JSON são selos de confiabilidade da autenticação. O selo a ser verificado será o "Convalidação" (selo que representa o balcão), conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON
	
	{
		"id": 0,
		"nivel": 3,
		"descricao": "Convalidação"
	}

3. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://api.staging.acesso.gov.br/info/documentos/orgao/sigla-do-orgao-do-balcao enviando as seguintes informações:

============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**Authorization**             palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**sigla-do-orgao-do-balcao**  sigla do órgão do balcão que recolheu os documentos
============================  ======================================================================

4. O resultado em formato JSON é a detalhamento das informações cadastradas pelo atendente do balcão, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	[
		{
		"id": "Número do Documento (Formulário Cadastro pelo Ministério da Economia)",
		"nome": "Nome do Documento (Formulário Cadastro pelo Ministério da Economia)",
		"docAssinado": "Identifica se documento utilizou assinatura digital. Possui valor true e false" ,
		"campos":
			[
				{
				"id": "(Identificador do Campo cadastrado no documento)",
				"nome": "(Nome do Campo cadastrado no documento)",
				"tipo": "(Tipo do Campo cadastrado no documento. Possui os valores Arquivo (Upload de documentos); Campo Textual (letras e números); Data ; Hora; Número; Enumeração (Conteúdo com lista de valores); Assinatura (Informa que area deverá ter assinatura por certificado digital))",
				"enumeracao": "(Caso o tipo do campo seja Enumeração, mostrará quais conteúdos pertencem a lista.",
				"ordem": "(Número que aparece o campo no documento)",
				"formato": "(Formatação da Mascará cadastrada para campo.)",
				"obrigatorio": "(Indica se o preenchimento do campo é obrigatório ou não. Possui valor true ou false)",
				"valor": "(Valor do campo escolhido para preenchimento)"
				}
			]	
		}
   	]

Resultados Esperados ou Erros do Acesso ao Serviços do Login Único	
------------------------------------------------------------------

Os acessos aos serviços do Login Único ocorrem por meio de chamadas de URLs e as respostas são códigos presentes conforme padrão do protocolo http por meio do retorno JSON, conforme exemplo:

.. code-block:: JSON

  {
	"codigo": "(Código HTTP do erro)",
	"descricao": "(Descrição detalhada do erro ocorrido. )"
  }

.. |site externo| image:: _images/site-ext.gif
.. _`codificador para Base64`: https://www.base64decode.org/
.. _`Plano de Configuração`: arquivos/plano-configuracao-brasil-cidadao-v9.doc
.. _`OpenID Connect`: https://openid.net/specs/openid-connect-core-1_0.html#TokenResponse
.. _`auth 2.0 Redirection Endpoint`: https://tools.ietf.org/html/rfc6749#section-3.1.2
.. _`Exemplos de Integração`: exemplointegracao.html
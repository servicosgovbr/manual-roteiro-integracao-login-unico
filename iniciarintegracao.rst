Iniciando a Integração
=====================

Solicitação de Configuração
+++++++++++++++++++++++++++

Para utilização do sistema Login Único, há necessidade de liberar os ambientes para aplicação cliente possa utilizar. Essa liberação ocorre por meio do preenchimento do `Plano de Integração`_.

O formulário deverá ser encaminhado para os integrantes da Secretaria de Governança Digital (SGD) do Ministério da Economia (ME) para realizar configuração da utilização do Login Único.

Métodos e interfaces de integração (Passo-a-Passo para Integrar)
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Autenticação
------------

Para que a autenticação aconteça, todo o canal de comunicação deve ser realizado com o protocolo HTTPS. Será feito um redirecionamento para uma URL de autorização do Login Único e, após a autenticação ser concluída, retornará um código de autenticação para a aplicação cliente com intuito de adquirir um ticket de acesso para os serviços protegidos.

A utilização da autenticação do Login Único depende dos seguintes passos:

1. A chamada para autenticação deverá ocorrer pelo botão com o conteúdo **Entrar com GOV.BR**. Para o formato do botão, seguir as orientações do `Design System do Governo Federal`_ |site externo|. 

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

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544&scope=openid+email+phone+profile&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

4. Após a autorização, a requisição é retornada para a URL especificada no redirect_uri do serviço https://sso.staging.acesso.gov.br/authorize, enviando os parâmetros:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**code**           Código de autenticação gerado pelo provedor. Será utilizado para obtenção do Token de Resposta. Possui tempo de expiração e só pode ser utilizado uma única vez. 
**state**          *State* passado anteriormente do https://sso.staging.acesso.gov.br/authorize que pode ser utilizado para controle da aplicação cliente. Pode correlacionar com o *code* gerado.  
=================  ======================================================================

5. Após autenticado, o provedor redireciona para a página de autorização. O usuário habilitará o consumidor no sistema para os escopos solicitados. Caso o usuário da solicitação autorize o acesso, é gerado um “ticket de acesso”, conforme demonstra na especificação `OpenID Connect`_ ;

6. Para obter o *ticket de acesso*, o consumidor deve fazer uma requisição POST para o endereço https://sso.staging.acesso.gov.br/token passando as seguintes informações:

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

	https://sso.staging.acesso.gov.br/token?grant_type=authorization_code&code=Z85qv1&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php	

O serviço retornará, em caso de sucesso, no formato JSON, as informações conforme exemplo:

.. code-block:: JSON

	{ 
		"access_token": "(Token de acesso a recursos protegidos do autenticador, bem como serviços do Login Único.)", 
		"id_token": "(Token de autenticação com informações básicas do usuário.)", 
		"token_type": "(O tipo do token gerado. Padrão: Bearer)", 
		"expires_in": "(Tempo de vida do token em segundos.)" 
	} 

7. De posse das informações do json anterior, a aplicação consumidora está habilitada para consultar dados de recursos protegidos, que são as informações e método de acesso do usuário ou serviços externos do Login Único. 

8. Antes de utilizar as informações do JSON anterior, de forma especifica os **ACCESS_TOKEN** e **ID_TOKEN**, para buscar informações referente ao método de acesso e cadastro básico do usuário, há necessidade da aplicação consumidora validar se as informações foram geradas pelos serviços do Login Único. Esta validação ocorrerá por meio da consulta da chave pública disponível no serviço https://sso.staging.acesso.gov.br/jwk. Para isso, verificar o método **processToClaims** dos `Exemplos de Integração`_.    

9. A utilização das informações do **ACCESS_TOKEN** e **ID_TOKEN** ocorrerá ao extrair do JSON codificado os seguintes parâmetros: 

**JSON do ACCESS_TOKEN**

.. code-block:: JSON

	{
		"sub": "(CPF do usuário autenticado)",
		"aud": "Client ID da aplicação onde o usuário se autenticou",
		"scope": ["(Escopos autorizados pelo provedor de autenticação.)"],
		"amr": ["(Listagem dos fatores de autenticação do usuário. Pode ser “passwd” se o mesmo logou fornecendo a senha, ou “x509” se o mesmo utilizou certificado digital ou certificado em nuvem.)"],
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
		"amr": ["(Listagem dos fatores de autenticação do usuário. Pode ser “passwd” se o mesmo logou fornecendo a senha, ou “x509” se o mesmo utilizou certificado digital ou certificado em nuvem.)"],
		"picture": "(URL de acesso à foto do usuário cadastrada no Gov.br. A mesma é protegida e pode ser acessada passando o access token recebido.)",
		"name": "(Nome cadastrado no Gov.br do usuário autenticado.)",
		"phone_number_verified": "(Confirma se o telefone foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")",
		"phone_number": "(Número de telefone cadastrado no Gov.br do usuário autenticado. Caso o atributo phone_number_verified do ID_TOKEN tiver o valor false, o atributo phone_number não virá no ID_TOKEN)",
		"email_verified": "(Confirma se o email foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")",
		"email": "(Endereço de e-mail cadastrado no Gov.br do usuário autenticado. Caso o atributo email_verified do ID_TOKEN tiver o valor false, o atributo email não virá no ID_TOKEN)",
		"cnpj": "(CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica.)"
	}

**Os paramêtros email,phone_number,picture não são obrigatórios. Ambos podem estar preenchidos ou não.**	
	
	
10. Para solicitação do conteúdo da foto salva no cadastro do cidadão, deverá acessar, pelo método GET, o serviço https://sso.staging.acesso.gov.br/userinfo/picture e acrescentar o atributo Authorization ao header do HTTP da requisição:
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

O serviço retornará, em caso de sucesso a informação em formato Base64

Acesso ao Serviço de Log Out
----------------------------

1. Com usuário autenticado, deverá acessar, por meio do método GET ou POST, a URL: https://sso.staging.acesso.gov.br/logout. O acesso ao Log Out deverá ser pelo **Front End** da aplicação a ser integrada com Login Único.

.. Parâmetros do Header para requisição GET https://sso.staging.acesso.gov.br/logout

.. =================  ======================================================================
.. **Variavél**  	   **Descrição**
.. -----------------  ----------------------------------------------------------------------
.. **Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
.. =================  ======================================================================

Parâmetros da Query para requisição GET https://sso.staging.acesso.gov.br/logout
	
============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**post_logout_redirect_uri**  URL que direciona ao Login Único qual página deverá ser aberta quando o token for inválidado. A URL deverá ser previamente liberada por meio do preenchimento do campo **URL de Log Out** presente no `Plano de Integração`_.  
============================  ======================================================================

Exemplo 1 de *execução* no front end em javascript

.. code-block:: javascript

	var form = document.createElement("form");      
	form.setAttribute("method", "post");
    form.setAttribute("action", "https://sso.staging.acesso.gov.br/logout?post_logout_redirect_uri=https://www.minha-aplicacao.gov.br/retorno.html");
    document.body.appendChild(form);  
	form.submit();

Exemplo 2 de *execução* no front end em javascript

.. code-block:: javascript

	window.location.href='https://sso.staging.acesso.gov.br/logout?post_logout_redirect_uri=https://www.minha-aplicacao.gov.br/retorno.html';	
	

Acesso ao Serviço de Confiabilidade Cadastral (Selos)
-----------------------------------------------------

Para acessar o serviço que disponibiliza as confiablidades cadastrais, é necessário o seguinte:

1. Na requisição de autenticação, adicionar o escopo “govbr_confiabilidades“, conforme exemplo:

Exemplo de requisição

.. code-block:: console

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=minha-aplicacao&scope=openid+email+phone+profile+govbr_confiabilidades&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

2. Com usuário autenticado, deverá acessar, por meio do método GET, a URL: https://api.staging.acesso.gov.br/confiabilidades/v1/usuarios/**cpf**/confiabilidades
;

Parâmetros para requisição GET https://api.staging.acesso.gov.br/confiabilidades/v1/usuarios/**cpf**/confiabilidades 

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cpf**            CPF do cidadão (sem ponto, barra etc).
=================  ======================================================================

3. A resposta em caso de sucesso retorna sempre um *array* de objetos JSON no seguinte formato:

.. code-block:: JSON

	[
	  {
		"confiabilidade": {
		  "id": "(Identificação para reconhecer o selo)",
		  "categoria": "(Identifica qual nível pertence o selo adquirido pelo cidadão)", 
		  "titulo": "(Identificação do selo em tela para o cidadão)",
		  "descricao": "(Descrição padrão do significado do selo)"
		},
		"dataCriacao": "(Mostra a data e hora da criação do selo na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
	  }
	]

Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)
---------------------------------------------------------------------------

Os selos existentes no Login Único são:

.. code-block:: JSON

	[
		{
		"confiabilidade": {
		"id": "cadastro_basico",
		"categoria": "basica",
		"titulo": "Cadastro com validação de dados na Receita Federal",
		"descricao": "Validação do cadastro via base de Cadastro de Pessoas Físicas."
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},
		
		{
		"confiabilidade": {
		"id": "kba_previdencia",
		"categoria": "basica_previdencia",
		"titulo": "Cadastro com validação de dados no INSS",
		"descricao": "Validação através de formulário On-Line da Previdência"
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},
		
		{
		"confiabilidade": {
		"id": "balcao_correios",
		"categoria": "verificada",
		"titulo": "Cadastro via Balcão dos Correios",
		"descricao": "Validação por meio da apresentação de documento de identificação em Agências dos Correios"
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},

		{
		"confiabilidade": {
		"id": "balcao_nai_previdencia",
		"categoria": "verificada",
		"titulo": "Cadastro via Internet Banking",
		"descricao": "Validação através de Internet Banking ou Caixa eletrônico"
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},
		
		{
		"confiabilidade": {
		"id": "balcao_sat_previdencia",
		"categoria": "verificada",
		"titulo": "Cadastro via Balcão do INSS",
		"descricao": "Validação por meio da apresentação de documento de identificação em Agências do INSS"
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},

		{
		"confiabilidade": {
		"id": "bb_internet_banking",
		"categoria": "verificada",
		"titulo": "Cadastro via Internet Banking do Banco do Brasil",
		"descricao": "Validação através Autenticação no Internet Banking do Banco do Brasil"
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},

		{
		"confiabilidade": {
		"id": "biovalid_facial",
		"categoria": "verificada",
		"titulo": "Cadastro validado por Biometria Facial",
		"descricao": "Validação através da Biometria Facial usando o Biovalid"
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},
		
		{
		"confiabilidade": {
		"id": "servidor_publico",
		"categoria": "verificada",
		"titulo": "Cadastro validado em base de dados de servidores públicos da União",
		"descricao": "Validação e autenticação do cadastro via base de dados de Servidores Públicos da União."
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		},
		
		{
		"confiabilidade": {
		"id": "certificado_digital",
		"categoria": "comprovada",
		"titulo": "Cadastro validado por certificado digital",
		"descricao": "Validação e autenticação do cadastro via Certificado Digital compatível com as especificações da Infraestrutura de Chaves Públicas Brasileira (ICP-BRASIL)."
		},
		"dataCriacao": "2020-04-13T14:28:40.936-0300"
		}
				
	]	

Acesso ao Serviço de Catálogo de Confiabilidade Cadastral (Selos)
-----------------------------------------------------------------

O catálogo de confiabilidade cadastral faz parte da area de privacidade da gestão de conta do gov.br. Esse está disponível para ser chamado pelos sistemas integrados para permitir ao usuário adquirir determinada determinada confiabilidade e continuar acesso ao sistema integrado.	

Para acesso ao catálogo, basta seguir os passos:

1. Com usuário autenticado, deverá acessar, por meio do método GET, a URL: https://catalogo.staging.acesso.gov.br/#/login passando seguintes informações:

Parâmetros para requisição GET https://catalogo.staging.acesso.gov.br/#/login

============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**Authorization**             palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**client_id**                 Chave de acesso, que identifica o serviço consumidor fornecido pelo Login Único para a aplicação cadastrada
**confiabilidades**           Os selos que aplicação integrada deseja para acessar serviço podendo ser 1 ou vários separado por vírgula. Os selos permitidos estão presentes no atributo **id** do retorno do serviço `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)`_. 
============================  ======================================================================

2. A resposta em caso de sucesso permite o cidadão adquirir os selos de confiabilidade e retornar ao serviço que solicitou o catálogo. O retorno do Login Único ao serviço utilizará a URL de Página inicial de serviço cadastrada no client_id.   	
	
Acesso ao Serviço de Cadastro de Pessoas Jurídicas
--------------------------------------------------

O Login Único disponibiliza dois serviços para acesso a informações de Pessoa Jurídica. O primeiro apresenta todos os CNPJs cadastrados para um determinado usuário. O segundo, utiliza desse CNPJ para extrair informações cadastradas no Login Único para aquela pessoa e empresa.

Para acessar o serviço que disponibiliza os CNPJs vinculados a um determinado usuário, é necessário o seguinte:

1. Na requisição de autenticação, adicionar o escopo “govbr_empresa“, conforme exemplo:

Exemplo de requisição

.. code-block:: console

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=minha-aplicacao&scope=openid+email+phone+profile+govbr_empresa&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

2. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://api.staging.acesso.gov.br/empresas/v2/empresas?ﬁltrar-por-participante=**cpf** enviando as seguintes informações:

Parâmetros para requisição GET https://api.staging.acesso.gov.br/empresas/v2/empresas?ﬁltrar-por-participante=cpf

============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**Authorization**             palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cpf**                       CPF do cidadão (sem ponto, barra etc).
============================  ======================================================================

3. O resultado em formato JSON é a lista de CNPJs do CPF autenticado, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	[
		{
		"cnpj": "(Número de CNPJ da empresa vinculada)",
		"razaoSocial": "(Razão Social (Nome da empresa) cadastrada na Receita Federal)",
		"dataCriacao": "(Mostra a data e hora da vinculação do CNPJ a conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}
	]

4. Com o usuário autenticado, a aplicação cliente deverá acessar, por meio do método GET, a URL https://api.staging.acesso.gov.br/empresas/v2/empresas/**cnpj**/participantes/**cpf** enviando as seguintes informações:

Parâmetros para requisição GET https://api.staging.acesso.gov.br/empresas/v2/empresas/**cnpj**/participantes/**cpf**

============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**Authorization**             palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cpf**   					  CPF do cidadão (sem ponto, barra etc).
**cnpj**					  CNPJ da empresa (sem ponto, barra etc).
============================  ======================================================================

5. O resultado em formato JSON é o detalhamento do CNPJ do CPF autenticado, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	{
	"cpf": "(Número do CPF que pode atuar com empresa)",
	"atuacao": "(Papel do CPF na empresa na Receita Federal. O conteúdo será SOCIO, CONTADOR, REPRESENTANTE_LEGAL ou NAO_ATUANTE. O NAO_ATUANTE representa CPF possui certificado digital de pessoa jurídica, porém não possui um papel na empresa na base da Receita Federal. Se CPF for colaborador, atributo atuacao não aparecerá)",
	"cadastrador": "(Identifica se o CPF pode realizar cadastro de colaboradores para CNPJ. O conteúdo false determinar que o CPF é um colaborador da empresa. O conteúdo true determina CPF é representante da empresa com certificado digital de pessoal jurídica)",
	"cpfCadastrador": "(CPF responsável por realizar cadastro do Colaborador. Se CPF apresentar atributo cadastrador com conteúdo true, o atributo cpfCadastrador não aparecerá)",
	"dataCriacao": "(Mostra a data e hora da vinculação do CPF ao CNPJ. A mascará será YYYY-MM-DD HH:MM:SS)",
	"dataExpiracao": "(Mostra a data e hora que o CPF poderá atuar com CNPJ. A mascará será YYYY-MM-DD HH:MM:SS)"
	}

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
.. _`Plano de Integração`: arquivos/Modelo_PlanodeIntegracao_LOGINUNICO_final.doc
.. _`OpenID Connect`: https://openid.net/specs/openid-connect-core-1_0.html#TokenResponse
.. _`auth 2.0 Redirection Endpoint`: https://tools.ietf.org/html/rfc6749#section-3.1.2
.. _`Exemplos de Integração`: exemplointegracao.html
.. _`Design System do Governo Federal`: http://dsgov.estaleiro.serpro.gov.br/ds/componentes/button
.. _`Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)`: iniciarintegracao.html#resultado-esperado-do-acesso-ao-servico-de-confiabilidade-cadastral-selos
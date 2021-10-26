Passo-a-Passo para Integrar
===========================

Autenticação
++++++++++++

Para que a autenticação aconteça, todo o canal de comunicação deve ser realizado com o protocolo HTTPS e, não utilizar a tecnologia *WebView* no ambiente mobile, mas navegador nativo. Será feito um redirecionamento para uma URL de autorização do Login Único e, após a autenticação ser concluída, retornará um código de autenticação para a aplicação cliente com intuito de adquirir um ticket de acesso para os serviços protegidos.

A utilização da autenticação do Login Único depende dos seguintes passos:

Passo 1
-------
A chamada para autenticação deverá ocorrer pelo botão com o conteúdo **Entrar com GOV.BR**. Para o formato do botão, seguir as orientações do `Design System do Governo Federal`_ |site externo|. 

Passo 2
-------
Ao requisitar autenticação via Provedor, o mesmo verifica se o usuário está logado. Caso o usuário não esteja logado o provedor redireciona para a página de login. 

Passo 3
-------
A requisição é feita através de um GET para o endereço https://sso.staging.acesso.gov.br/authorize passando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**response_type**  Especifica para o provedor o tipo de autorização. Neste caso será **code**
**client_id**      Chave de acesso, que identifica o serviço consumidor fornecido pelo Login Único para a aplicação cadastrada
**scope**          Especifica os recursos que o serviço consumidor quer obter. Um ou mais escopos inseridos para a aplicação cadastrada. Informação a ser preenchida por padrão: **openid+email+phone+profile+govbr_confiabilidades**. 
**redirect_uri**   URI de retorno cadastrada para a aplicação cliente no formato *URL Encode*. Este parâmetro não pode conter caracteres especiais conforme consta na especificação `auth 2.0 Redirection Endpoint`_
**nonce**          Sequência de caracteres usado para associar uma sessão do serviço consumidor a um *Token* de ID e para atenuar os ataques de repetição. Pode ser um valor aleatório, mas que não seja de fácil dedução. Item obrigatório.
**state**          Valor usado para manter o estado entre a solicitação e o retorno de chamada. Item obrigatório. 
=================  ======================================================================

Exemplo de requisição:

.. code-block:: console

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544&scope=openid+email+phone+profile&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

Passo 4
-------	
Após a autorização, a requisição é retornada para a URL especificada no redirect_uri do serviço https://sso.staging.acesso.gov.br/authorize, enviando os parâmetros:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**code**           Código de autenticação gerado pelo provedor. Será utilizado para obtenção do Token de Resposta. Possui tempo de expiração e só pode ser utilizado uma única vez. 
**state**          *State* passado anteriormente do https://sso.staging.acesso.gov.br/authorize que pode ser utilizado para controle da aplicação cliente. Pode correlacionar com o *code* gerado. O cliente consegue saber se o CODE veio de um state gerado por ele.  
=================  ======================================================================

Passo 5
-------
Após autenticado, o provedor redireciona para a página de autorização. O usuário habilitará o consumidor no sistema para os escopos solicitados. Caso o usuário da solicitação autorize o acesso, é gerado um “ticket de acesso”, conforme demonstra na especificação `OpenID Connect`_ ;

Passo 6
-------
Para obter o *ticket de acesso*, o consumidor deve fazer uma requisição POST para o endereço https://sso.staging.acesso.gov.br/token passando as seguintes informações:

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

Parâmetros do Body para requisição Post https://sso.staging.acesso.gov.br/token
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**grant_type**     Especifica para o provedor o tipo de autorização. Neste caso será **authorization_code**
**code**           Código retornado pela requisição anterior (exemplo: Z85qv1)
**redirect_uri**   URI de retorno cadastrada para a aplicação cliente no formato *URL Encode*. Este parâmetro não pode conter caracteres especiais conforme consta na especificação `auth 2.0 Redirection Endpoint`_
=================  ======================================================================

Exemplo de *query*

.. code-block:: console

	curl -X POST -d 'grant_type=authorization_code&code=Z85qv1&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php' https://sso.staging.acesso.gov.br/token	

O serviço retornará, em caso de sucesso, no formato JSON, as informações conforme exemplo:

.. code-block:: JSON

	{ 
		"access_token": "(Token de acesso a recursos protegidos do autenticador, bem como serviços do Login Único.)", 
		"id_token": "(Token de autenticação com informações básicas do usuário.)", 
		"token_type": "(O tipo do token gerado. Padrão: Bearer)", 
		"expires_in": "(Tempo de vida do token em segundos.)" 
	} 

Passo 7
-------
De posse das informações do json anterior, a aplicação consumidora está habilitada para consultar dados de recursos protegidos, que são as informações e método de acesso do usuário ou serviços externos do Login Único. 

Passo 8
-------
Antes de utilizar as informações do JSON anterior, de forma especifica os **ACCESS_TOKEN** e **ID_TOKEN**, para buscar informações referente ao método de acesso e cadastro básico do usuário, há necessidade da aplicação consumidora validar se as informações foram geradas pelos serviços do Login Único. Esta validação ocorrerá por meio da consulta da chave pública disponível no serviço https://sso.staging.acesso.gov.br/jwk. Para isso, verificar o método **processToClaims** dos `Exemplos de Integração`_.    

Passo 9
-------
A utilização das informações do **ACCESS_TOKEN** e **ID_TOKEN** ocorrerá ao extrair do JSON codificado os seguintes parâmetros: 

**JSON do ACCESS_TOKEN**

.. code-block:: JSON

	{
		"sub": "(CPF do usuário autenticado)",
		"aud": "Client ID da aplicação onde o usuário se autenticou",
		"scope": ["(Escopos autorizados pelo provedor de autenticação.)"],
		"amr": ["(Listagem dos fatores de autenticação do usuário. Pode ser “app” se logou por QR-CODE do aplicativo gov.br, “passwd” se o mesmo logou fornecendo a senha, “x509” se o mesmo utilizou certificado digital ou certificado em nuvem, ou “bank” para indicar utilização de conta bancária para autenticar. Esse último seguirá com número de identificação do banco, conforme código de compensação do Bacen presente ao final da explicação.)"],
		"iss": "(URL do provedor de autenticação que emitiu o token.)",
		"exp": "(Data/hora de expiração do token)",
		"iat": "(Data/hora em que o token foi emitido.)",
		"jti": "(Identificador único do token, reconhecido internamente pelo provedor de autenticação.)",
		"cnpj": "CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica."
	}

**Observações para ACCESS_TOKEN:**

- Caso um novo método de autenticação seja adicionado, será listado no atributo *AMR*. As integrações devem contemplar futuras adições.
- Caso conta do cidadão esteja com segundo fator de autenticação ativado, quando o atributo *AMR* vier com *passwd*, aparecerão os conteúdos *mfa* (indica presença de segundo fator) e *otp* (forma de segundo fator com código encaminhado pelo aplicativo gov.br).
- Documento para verificação do Código de Compensação dos possíveis bancos integrados ao Login Único:`Documento verificar Código de Compensação dos Bancos`_.
	
**JSON do ID_TOKEN**

.. code-block:: JSON

	{
		"sub": "(CPF do usuário autenticado.)",
		"amr": ["(Listagem dos fatores de autenticação do usuário. Pode ser “app” se logou por QR-CODE do aplicativo gov.br, “passwd” se o mesmo logou fornecendo a senha, “x509” se o mesmo utilizou certificado digital ou certificado em nuvem, ou “bank” para indicar utilização de conta bancária para autenticar. Esse último seguirá com número de identificação do banco, conforme código de compensação do Bacen presente ao final da explicação.)"],
		"picture": "(URL de acesso à foto do usuário cadastrada no Gov.br. A mesma é protegida e pode ser acessada passando o access token recebido.)",
		"name": "(Nome cadastrado no Gov.br do usuário autenticado.)",
		"phone_number_verified": "(Confirma se o telefone foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")",
		"phone_number": "(Número de telefone cadastrado no Gov.br do usuário autenticado. Caso o atributo phone_number_verified do ID_TOKEN tiver o valor false, o atributo phone_number não virá no ID_TOKEN)",
		"email_verified": "(Confirma se o email foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")",
		"email": "(Endereço de e-mail cadastrado no Gov.br do usuário autenticado. Caso o atributo email_verified do ID_TOKEN tiver o valor false, o atributo email não virá no ID_TOKEN)",
		"cnpj": "(CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica.)"
	}

**Observações para ID_TOKEN:**

- Os paramêtros email,phone_number,picture não são obrigatórios. Ambos podem estar preenchidos ou não.
- Caso um novo método de autenticação seja adicionado, será listado no atributo *AMR*. As integrações devem contemplar futuras adições.
- Caso conta do cidadão esteja com segundo fator de autenticação ativado, quando o atributo *AMR* vier com *passwd*, aparecerão os conteúdos *mfa* (indica presença de segundo fator) e *otp* (forma de segundo fator com código encaminhado pelo aplicativo gov.br).
- Documento para verificação do Código de Compensação dos possíveis bancos integrados ao Login Único:`Documento verificar Código de Compensação dos Bancos`_.
	
Passo 10
--------
Para solicitação do conteúdo da foto salva no cadastro do cidadão, deverá acessar, pelo método GET, o serviço https://sso.staging.acesso.gov.br/userinfo/picture e acrescentar o atributo Authorization ao header do HTTP da requisição:
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
=================  ======================================================================

O serviço retornará, em caso de sucesso a informação em formato Base64

Passo 11
--------
Para verificar quais níveis da conta do cidadão está localizada, deverá acessar, pelo método GET, o serviço https://api.staging.acesso.gov.br/confiabilidades/v3/contas/**cpf**/niveis?response-type=ids

Parâmetros para requisição GET https://api.staging.acesso.gov.br/confiabilidades/v3/contas/**cpf**/niveis?response-type=ids 

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cpf**            CPF do cidadão (sem ponto, barra etc).
=================  ======================================================================

A resposta em caso de sucesso retorna sempre um **array** de objetos JSON no seguinte formato:

.. code-block:: JSON

	[
		{
		"id": "(Identificação para reconhecer o nível)",
		"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização do nível na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}
	]

Verificar quais níveis estão disponíveis, acesse `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Níveis)`_

Passo 12
--------
Para verificar quais catagorias da conta do cidadão está localizado, deverá acessar, pelo método GET, o serviço https://api.staging.acesso.gov.br/confiabilidades/v3/contas/**cpf**/categorias?response-type=ids

Parâmetros para requisição GET https://api.staging.acesso.gov.br/confiabilidades/v3/contas/**cpf**/categorias?response-type=ids 

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cpf**            CPF do cidadão (sem ponto, barra etc).
=================  ======================================================================

A resposta em caso de sucesso retorna sempre um **array** de objetos JSON no seguinte formato:

.. code-block:: JSON

	[
		{
		"id": "(Identificação para reconhecer a categoria)",
		"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}
	]
	
Verificar quais categorias estão disponíveis, acesse `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Categorias)`_

Passo 13
--------
Para verificar quais selos de confiabilidade a conta do cidadão possui, deverá acessar, pelo método GET, o serviço https://api.staging.acesso.gov.br/confiabilidades/v3/contas/**cpf**/confiabilidades?response-type=ids 

Parâmetros para requisição GET https://api.staging.acesso.gov.br/confiabilidades/v3/contas/**cpf**/confiabilidades?response-type=ids 

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Authorization**  palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
**cpf**            CPF do cidadão (sem ponto, barra etc).
=================  ======================================================================

A resposta em caso de sucesso retorna sempre um **array** de objetos JSON no seguinte formato:

.. code-block:: JSON

	[
		{
		"id": "(Identificação para reconhecer a confiabilidade)",
		"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}
	]

Verificar quais selos de confiabilidade estão disponíveis, acesse `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)`_  	

Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Níveis)
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

As categorias existentes no Login Único são:

.. code-block:: JSON

	[
		{
			"id": "1 (conta_basica)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "2 (verificada)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "3 (comprovada)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}
	]


Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Categorias)
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

As categorias existentes no Login Único são:

.. code-block:: JSON

	[
		{
			"id": "101 (carrosel_perguntas_previdencia)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "102 (carrosel_perguntas)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "201 (servidor_publico)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		{
			"id": "202 (biometria_facial)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "203 (balcao_presencial)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "204 (internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		{
			"id": "301 (biometria_individualizada)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": " 302 (certificado_digital)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da categoria na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}
				
	]


Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Os selos existentes no Login Único são:

.. code-block:: JSON

	[
		{
			"id": "101 (kba_previdencia)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "201 (cadastro_basico)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "301 (servidor_publico)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		{
			"id": "401 (biovalid_facial)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "501 (balcao_sat_previdencia)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "502 (balcao_denatran)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "503 (balcao_correios)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "504 (balcao_cadastro_presencial_govbr)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "601 (balcao_nai_previdencia)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "602 (bb_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "603 (banrisul_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "604 (bradesco_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "605 (caixa_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "606 (brb_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "607 (sicoob_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "608 (santander_internet_banking)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "701 (tse_facial)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		},
		
		{
			"id": "801 (certificado_digital)",
			"dataAtualizacao": "(Mostra a data e hora que ocorreu atualização da confiabilidade na conta do usuário. A mascará será YYYY-MM-DD HH:MM:SS)"
		}		
	]


Acesso ao serviço de Catálogo de Confiabilidades (Selos)
++++++++++++++++++++++++++++++++++++++++++++++++++++++++

1. Com usuário autenticado, deverá acessar, por meio do método GET ou POST, a URL https://confiabilidades.staging.acesso.gov.br/

Parâmetros da Query para requisição GET https://confiabilidades.staging.acesso.gov.br/

============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**client_id**                 Chave de acesso, que identifica o serviço consumidor fornecido pelo Login Único para a aplicação cadastrada
**niveis**					  Recurso de segurança da informação da identidade, que permitem flexibilidade para realização do acesso. **Atributo opcional**
**categorias**				  Permitem manutenção mais facilitada da utilização dos níveis e confiabilidades (selos) do Login Único. **Atributo obrigatório**
**confiabilidades**			  Consistem em orientar para qualificação das contas com a obtenção dos atributos autoritativos do cidadão a partir das bases oficias de governo, por meio das quais permitirão a utilização da credencial de acesso em sistemas internos dos clientes e serviços providos diretamente ao cidadão. **Atributo obrigatório**
============================  ======================================================================

2. O resultado será o Catálogo apresentado com as configurações solicitadas. Após atendido as configurações, o Login Único devolverá o fluxo para aplicação por meio da URL de Lançador de Serviços, conforme `Plano de Integração`_. 

**Observações sobre as variáveis do serviço de catálogo**

1. Conteúdo para variável *niveis* : Será a informação do atributo id presente em cada nível no `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Níveis)`_
2. Conteúdo para variável *categorias* : Será a informação do atributo id presente em cada categoria no `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Categorias)`_
3. Contéudo para variável *confiabilidades*: Será a informação do atributo id presentes em cada confiabilidade no `Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)`_
4. Tratamento do conteúdo para cada variável:

- Todos são obrigatórios, deve-se separá-los por vírgula. **Exemplo (categorias=102,101)**
- Apenas um é obrigatório, deve-se separar por barra invertida. **Exemplo (confiabilidades=(301/801)** 	
	
Acesso ao Serviço de Log Out
++++++++++++++++++++++++++++

1. **Implementação obrigatória** a fim de encerrar a sessão do usuário com o Login Único.

2. Com usuário autenticado, deverá acessar, por meio do método GET ou POST, a URL: https://sso.staging.acesso.gov.br/logout. O acesso ao Log Out deverá ser pelo **Front End** da aplicação a ser integrada com Login Único.

Parâmetros da Query para requisição GET https://sso.staging.acesso.gov.br/logout
	
============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**post_logout_redirect_uri**  URL que direciona ao Login Único qual página deverá ser aberta quando o token for invalidado. A URL deverá ser previamente liberada por meio do preenchimento do campo **URL de Log Out** presente no `Plano de Integração`_.  
============================  ======================================================================

Exemplo 1 de **execução** no front end em javascript

.. code-block:: javascript

	var form = document.createElement("form");      
	form.setAttribute("method", "post");
    form.setAttribute("action", "https://sso.staging.acesso.gov.br/logout?post_logout_redirect_uri=https://www.minha-aplicacao.gov.br/retorno.html");
    document.body.appendChild(form);  
	form.submit();

Exemplo 2 de **execução** no front end em javascript

.. code-block:: javascript

	window.location.href='https://sso.staging.acesso.gov.br/logout?post_logout_redirect_uri=https://www.minha-aplicacao.gov.br/retorno.html';	
	
Acesso ao Serviço de Cadastro de Pessoas Jurídicas
++++++++++++++++++++++++++++++++++++++++++++++++++

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

Acesso ao Serviço de Revalidação de Senha
+++++++++++++++++++++++++++++++++++++++++

Para que a revalidação aconteça, todo o canal de comunicação deve ser realizado com o protocolo HTTPS. Será feito um redirecionamento para URL de autorização especifica do Login Único e, após a revalidação ser concluída, retornará um código de autenticação para a aplicação cliente com intuito de adquirir um ticket de acesso para revalidação da senha e continuação do fluxo do sistema.

Esta estrutura precisará autorização específica a ser solicitada para Ministério da Economia. 

Passos para processo:

1. Requisição GET para  endereço https://oauth.staging.acesso.gov.br/v1/authorize passando as seguintes informações:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**response_type**  Especifica para o provedor o tipo de autorização. Neste caso será **code**
**client_id**      Chave de acesso, que identifica o serviço consumidor fornecido pelo Login Único para a aplicação cadastrada
**scope**          Informação ser preenchida: **password-validation**. 
**redirect_uri**   URL de retorno cadastrada para a aplicação cliente continuar o processo após a revalidação da senha no formato *URL Encode*. Este parâmetro não pode conter caracteres especiais conforme consta na especificação `auth 2.0 Redirection Endpoint`_
**state**          Valor usado para manter o estado entre a solicitação e o retorno de chamada. Item não obrigatório.
=================  ======================================================================

Exemplo de requisição:

.. code-block:: console

	https://oauth.staging.acesso.gov.br/v1/authorize?response_type=code&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&scope=password-validationstate=exASJANS12sa
	
2. Após a autorização, a requisição é retornada para a URL especificada no redirect_uri do serviço https://oauth.staging.acesso.gov.br/v1/authorize, enviando os parâmetros:

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**code**           Código de autenticação gerado pelo provedor. Será utilizado para obtenção do Token de Resposta. Possui tempo de expiração de 2 minutos e só pode ser utilizado uma única vez. 
**state**          *State* passado anteriormente do https://oauth.staging.acesso.gov.br/v1/authorize que pode ser utilizado para controle da aplicação cliente. Pode correlacionar com o *code* gerado.  
=================  ======================================================================

3. Para obter o novo *ticket de acesso*, o consumidor deve fazer uma requisição POST para o endereço https://oauth.staging.acesso.gov.br/v1/token passando as seguintes informações:

Parâmetros do Header para requisição Post https://oauth.staging.acesso.gov.br/v1/token

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Content-Type**   Tipo do conteúdo da requisição que está sendo enviada. Nesse caso estamos enviando como um formulário (**application/x-www-form-urlencoded**)
**Authorization**  Informação codificada em *Base64*, no seguinte formato: CLIENT_ID:CLIENT_SECRET (senha de acesso do serviço consumidor)(utilizar `codificador para Base64`_ |site externo|  para gerar codificação). A palavra Basic deve está antes da informação. 
=================  ======================================================================
	
Exemplo de *header*:

.. code-block:: console

	Content-Type:application/x-www-form-urlencoded
	Authorization: Basic											
	ZWM0MzE4ZDYtZjc5Ny00ZDY1LWI0ZjctMzlhMzNiZjRkNTQ0OkFJSDRoaXBfTUJYcVJkWEVQSVJkWkdBX2dRdjdWRWZqYlRFT2NWMHlFQll4aE1iYUJzS0xwSzRzdUVkSU5FcS1kNzlyYWpaZ3I0SGJuVUM2WlRXV1lJOA==

Parâmetros da Query para requisição Post https://oauth.staging.acesso.gov.br/v1/token
	
=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**grant_type**     Especifica para o provedor o tipo de autorização. Neste caso será **authorization_code**
**code**           Código retornado pela requisição anterior (exemplo: 8bfac143-c4ca-daf-8ea8-517f0d93db7a)
**redirect_uri**   URI de retorno cadastrada para a aplicação cliente no formato *URL Encode*. Este parâmetro não pode conter caracteres especiais conforme consta na especificação `auth 2.0 Redirection Endpoint`_
**state**          Valor usado para manter o estado entre a solicitação e o retorno de chamada. Item não obrigatório.
=================  ======================================================================

Exemplo de *query*

.. code-block:: console

	https://oauth.staging.acesso.gov.br/v1/token?grant_type=authorization_code&code=8bfac143-c4ca-daf-8ea8-517f0d93db7a&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&client_id=ec4318d6-f797-4d65-b4f7-39a33bf4d544

O serviço retornará, em caso de sucesso, no formato JSON, as informações conforme exemplo:

.. code-block:: JSON

	{ 
		"access_token": "(Token de acesso a recursos protegidos do autenticador, bem como serviços do Login Único.)", 
		"token_type": "(O tipo do token gerado. Padrão: Bearer)", 
		"expires_in": "(Tempo de vida do token em segundos.)" 
	} 

4. De posse das informações do json anterior, a aplicação consumidora está habilitada para concluir revalidação da senha. 

5. Antes de utilizar as informações do JSON anterior, de forma especifica o **ACCESS_TOKEN**, para revalidação da senha, há necessidade da aplicação consumidora validar se as informações foram geradas pelos serviços do Login Único. Esta validação ocorrerá por meio da consulta da chave pública disponível no serviço https://oauth.staging.acesso.gov.br/v1/jwks. Para isso, verificar o método **processToClaims** dos `Exemplos de Integração`_.

6. Verificado o access token, a aplicação cliente consegue, através do atributo **sub**, saber qual usuário confirmou sua identidade gov.br com sucesso e continuar o processo no sistema integrado.

Acesso ao Serviço de Recuperação do Tipo de Certificado
+++++++++++++++++++++++++++++++++++++++++++++++++++++++

1. Na requisição de autenticação, adicionar o escopo “govbr_recupera_certificadox509“, conforme exemplo:

Exemplo de requisição

.. code-block:: console

	https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=minha-aplicacao&scope=openid+email+phone+profile+govbr_recupera_certificadox509&redirect_uri=http%3A%2F%2Fappcliente.com.br%2Fphpcliente%2Floginecidadao.Php&nonce=3ed8657fd74c&state=358578ce6728b

2. Com o usuário autenticado, a aplicação deverá realizar uma requisição por meio do método GET a URL https://sso.staging.acesso.gov.br/api/x509/info enviando as seguintes informações:

Parâmetros para requisição GET https://sso.staging.acesso.gov.br/api/x509/info

============================  ======================================================================
**Variavél**  	              **Descrição**
----------------------------  ----------------------------------------------------------------------
**Authorization**             palavra **Bearer** e o *ACCESS_TOKEN* da requisição POST do https://sso.staging.acesso.gov.br/token
============================  ======================================================================

3. O resultado em formato JSON é tipo de certificado da autenticação, conforme o exemplo abaixo:

Exemplo de requisição

.. code-block:: JSON

	[
		{
		  "provider":"(Indicará qual o provedor disponibilizará o certificado. Aparecerá para certificado em nuvem)",
		  "amr":["(Lista de forma de certificados autenticados. Padrão é x509)"],
		  "certificate":"(Demonstra o nome do cerfificado da autenticação)",
		  "type":"(Informa qual tipo de certificado utilizado para autenticação. O contéudo será <device> para certificados A1 e A3 e <cloud> para indicar certificado em núvem)"
		}
	]
	
Resultados Esperados ou Erros do Acesso ao Serviços do Login Único	
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Os acessos aos serviços do Login Único ocorrem por meio de chamadas de URLs e as respostas são códigos presentes conforme padrão do protocolo http por meio do retorno JSON, conforme exemplo:

.. code-block:: JSON

  {
	"error": "(Código HTTP do erro)",
	"erro_description": "(Descrição detalhada do erro ocorrido. )"
  }

.. |site externo| image:: _images/site-ext.gif
.. _`codificador para Base64`: https://www.base64decode.org/
.. _`Plano de Integração`: arquivos/Modelo_PlanodeIntegracao_LOGINUNICO_Versao-4.doc
.. _`OpenID Connect`: https://openid.net/specs/openid-connect-core-1_0.html#TokenResponse
.. _`auth 2.0 Redirection Endpoint`: https://tools.ietf.org/html/rfc6749#section-3.1.2
.. _`Exemplos de Integração`: exemplointegracao.html
.. _`Design System do Governo Federal`: https://dsgov.estaleiro.serpro.gov.br/components/signin?tab=designer
.. _`Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Selos)`: iniciarintegracao.html#resultado-esperado-do-acesso-ao-servico-de-confiabilidade-cadastral-selos
.. _`Resultado Esperado do Acesso ao Serviço de Confiabilidade Cadastral (Categorias)` : iniciarintegracao.html#resultado-esperado-do-acesso-ao-servico-de-confiabilidade-cadastral-categorias
.. _`Documento verificar Código de Compensação dos Bancos` : arquivos/TabelaBacen.pdf
.. _`administrar as chaves PGP para credenciais do Login Único`: chavepgp.html
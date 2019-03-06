Arquitetura de Serviço e Protocolos
===================================

*Openid Connect (OIDC)*
++++++++++++++++++++++

O *Openid Connect* é um protocolo baseado no *OAuth 2.0* que especifica autorização e autenticação. Define como implementar o gerenciamento de autorizações de acesso, gerenciamento de sessão, fornecimento de informações sobre usuário logado. O OIDC permite executar o *logon* único dos usuários e apresenta o conceito de um *id token*: um *token* de segurança que permite verificar a identidade do usuário e obter informações básicas sobre o usuário. Tem característica de ser interoperável, porque segue o protocolo *RestFull* e usa o formato de saída de dados: JSON (*JavaScript Object Notation*).

Além disso, o OIDC suporta vários tipos de clientes, como aplicações que utilizam o *browser*, clientes *javascript*, aplicações *mobile* e outros. A Figura 1 ilustra as requisições da autenticação entre cliente e servidor.

.. figure:: _images/figura-1-descricao-fluxo-oidc.jpg
   :align: center
   :alt: representação do comportamento do Openid Connect: Usuário navegação na aplicação web; Aplicação web direciona o usuário para realização do login, passando o client_id; Usuário insere as credenciais; Usuário consede as permissões; Aplicação retornar o token de autorização; Aplicação, por meio do método Post HTML, envia o token para URL de redirecionamento; Valida o token e seta na sessão do browser; Retorna página segura para usuário.

*OAUTH2*   
+++++++

*OAuth2* é um protocolo aberto para autorização que permite aos clientes obterem acesso a recursos protegidos do servidor em nome do proprietário do recurso. O proprietário pode ser um cliente ou usuário final. Também especifica como um usuário final pode autorizar o acesso de terceiros aos seus recursos do servidor sem precisar compartilhar suas credenciais. Atualmente ele está sendo usado por grandes empresas como *Google*, *Facebook*, *Microsoft*, *Twitter*, e outros.

O protocolo fornece 4 estratégias para concessão de autorização: código de autorização, implícita, credenciais de senha do proprietário do recurso e credenciais do cliente. A estratégia usada no Brasil Cidadão é o código de autorização, que utiliza um *token*.

*Json Web Token - JWT*
+++++++++++++++++++++
   
O JWT define como transmitir objetos JSON de forma segura entre aplicações. Tem a característica de ser um padrão aberto.

A manipulação do padrão possui uma assinatura a ser realizada com uma palavra secreta ou uma chave publica/privada.

O JWT é composto por 3 elementos: *Headers*, *Payload* e *Signature*, explicados a seguir.

*Headers*   
--------

São objetos JSON definidos por 2 atributos: tipo do *token(typ)* e o algorítimo (alg) de encriptação , como SHA256 ou RSA. Exemplo:

.. code-block:: JSON

	{	
		"alg": "HS256",
		"typ": "JWT"
	}
   
*Payload*   
--------

São os atributos de uma entidade representada por objetos JSON. Exemplo:

.. code-block:: JSON

   {
		"sub": "1234567890",
		"name": "John Doe",
		"admin": true
   }

*Signature*
----------

Para criar a assinatura, há necessidade de assinar o *header* codificado, o *payload* codificado e informar o *secret*, palavra secreta definida na aplicação. A assinatura é criada para verificar se quem enviou a requisição é quem realmente diz ser.

.. code-block:: console

	eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9(header).eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9(payload).TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ(signature)

Para acessar recursos protegidos, o cliente deve enviar o *token* gerado através do atributo *Authorization* do *header* da requisição, com a *flag Bearer*, como abaixo:

.. code-block:: console

	Authorization: Bearer											
	eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
   

Código Autorizador
++++++++++++++++++

A estratégia é autorizar clientes a acessarem informações dos usuários proprietários através de um código identificador (Cliente ID). O cliente deve ser cadastrado no Portal de Gestão do Brasil Cidadão para obter um Cliente ID. Após , o proprietário da informação, ao ser requisitado, deve habilitar esse cliente para ter acesso às suas informações. O cliente está habilitado para obter do servidor os recursos necessários.

Essa estratégia é muito utilizada no mercado pois é otimizada para as aplicações *server-side*, o qual o código fonte não é exposto e a confidencialidade do Cliente ID é mantida.

A Figura 2 explica a obtenção de recursos do servidor para um cliente cadastrado.

.. figure:: _images/figura-2-obter-recurso-cliente-cadastrado.jpg
   :align: center
   :alt: Tem se o fluxo para código autorizador na visão cliente (Requisição da autorização, Requisição do Access Token e ID Token, Acesso ao recurso protegido) e na visão do provedor da identificação (Solicitação da identificação do usuário, Obter autorização, Direcionar a solicitação da identifiação do usuário, validar o cliente, validar código de autorização, Passar as permissões para o Access Token)

No passo A, a aplicação cliente solicita autorização. O usuário realiza a autenticação no Brasil Cidadão, obtêm a autorização e é redirecionado para o cliente, conforme o passo B. Já no passo C, o cliente solicita o *Access Token* e o *ID Token*, que são as credenciais para permitir as consultas de recursos por um determinado tempo. As credenciais são geradas no servidor e não podem navegar pelo cliente, para manter a confidencialidade. Após o cliente ser validado e receber o *ID Token* e *Access Token* no passo D, ele pode solicitar ao Brasil Cidadão os recursos necessários. A Figura 3 mostra os parâmetros necessários para as requisições da Figura 2.  

.. figure:: _images/figura-3-explicacao-parametros-necessarios.jpg
   :align: center
   :alt: Mostra as variáveis em cada passo citado anterior: Passo A (response_type com conteúdo code, client_id, redirect_uri, scope, state), Passo B (code, state), Passo C (grant_type com conteúdo authorization_code, code, redirect_uri), Passo D (access_token, token_type, expires_in, refresh_token, scope).

Autorização para Recursos protegidos
++++++++++++++++++++++++++++++++++++

Na plataforma de autenticação, os serviços utilizam informações pessoais relacionadas aos cidadãos, logo, existe a necessidade de vincular recurso informacional ao serviço no processo de habilitação. Quando o cidadão se autentica e acessa algum recurso pela primeira vez, uma solicitação de autorização de uso de dados pessoais é feita. A autorização do uso de dados pessoais permite o funcionamento correto. A figura 4 apresenta a tela em que o cidadão autoriza o uso de recurso protegido (dados pessoais):

.. figure:: _images/figura-4-autorizacao-uso-dados-pessoais.jpg
   :align: center
   :alt: Demonstra a tela que aparece ao cidadão para fazer primeira autorização do serviço. Apresenta o texto sobre restrições do uso do Login Único e os botões para autorizar ou não o acesso ao serviço.


.. |site externo| image:: _images/site-ext.gif
            
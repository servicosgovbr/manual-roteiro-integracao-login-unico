Iniciando a Integração
=====================

Solicitação de Configuração
+++++++++++++++++++++++++++

Para utilização do sistema Brasil Cidadão, há necessidade de liberar os ambientes para aplicação cliente possa utilizar. Essa liberação ocorre por meio do preenchimento do plano de configuração encaminhado junto com este documento (**plano-configuracao-brasil-cidadao-vX.doc**).

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

=================  ======================================================================
**Variavél**  	   **Descrição**
-----------------  ----------------------------------------------------------------------
**Content-Type**   Tipo do conteúdo da requisição que está sendo enviada. Nesse caso estamos enviando como um formulário
**Authorization**  Informação codificada em *Base64*, no seguinte formato: CLIENT_ID:CLIENT_SECRET (senha de acesso do serviço consumidor)(utilizar `codificador para Base64`_ |site externo|  para gerar codificação). A palavra Basic deve está antes da informação. 
=================  ======================================================================
	
	
.. |site externo| image:: _images/site-ext.gif
.. _`codificador para Base64`: https://www.base64decode.org/

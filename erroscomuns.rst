Erros Comuns para Implementação do Login Único
==============================================

==========================  ======================================================================
**Erro**                    invalid_grant
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando a URL utilizada na chamada `authorize`_ não está cadastrada no Login Único.
--------------------------  ----------------------------------------------------------------------
**Solução**                 Corrigir a URL na chamada `authorize`_ ou solicitar a inclusão da URL no Login Único.
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    invalid_client
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando o client_id utilizado na chamada `authorize`_ está incorreto.
--------------------------  ----------------------------------------------------------------------
**Solução**                 Verificar e corrigir o client_id utilizado chamada `authorize`_.
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    [invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: 401 Unauthorized: [no body]
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando o `client_secret`_ está incorreto.
--------------------------  ----------------------------------------------------------------------
**Solução**                 Verificar e corrigir o `client_secret`_.
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    invalid_client no eSUS, porém o client_id está correto.
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando o client_id utilizado no arquivo application.properties está incorreto.
--------------------------  ----------------------------------------------------------------------
**Solução**                 Verificar se ficaram espaços em branco, ou quebra de linha após o client_id no arquivo application.properties, pode ser verificado se na URL de erro consta "%20" após o client_id.
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    [invalid_id_token] An error occurred while attempting to decode the Jwt: The ID Token contains invalid claims: {iat=2024-08-29T12:10:08Z}“
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando a data e hora do servidor, onde a aplicação está instalada, estão incorretas, onde apresenta a data do servidor no último parâmetro do erro {iat=2024-08-29T12:10:08Z}
--------------------------  ----------------------------------------------------------------------
**Solução**                 Verificar e corrigir a data e hora do servidor.
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    [invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: I/O error on POST request for “https://sso.acesso.gov.br/token”: Connection reset by peer; nested exception is java.net.SocketException: Connection reset by peer
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando o IP do servidor, onde a aplicação está instalada, está bloqueado no firewall do gov.br.
--------------------------  ----------------------------------------------------------------------
**Solução**                 É necessário enviar o client_id e IP da aplicação para que seja verificado o bloqueio e liberado o firewall.
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    "ACCESSTOKEN_SCOPE_MUSTCONTAINSEXPECTEDSCOPE" "Escopo requerido não encontrado. Valor esperado: '{0}', valor recebido: '{1}'."
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando se tenta utilizar a API sem o escopo necessário
--------------------------  ----------------------------------------------------------------------
**Solução**                 Adicionar na chamada authorize o escopo correspondente ao serviço que se deseja utilizar
==========================  ======================================================================

==========================  ======================================================================
**Erro**                    "unauthorized", "error_description":"Bad credentials"
--------------------------  ----------------------------------------------------------------------
**Descrição**               Este erro ocorre quando a credencial do client_id está incorreta na chamada token
--------------------------  ----------------------------------------------------------------------
**Solução**                 Adicionar no parâmetro Authorization, da chamada Token, o valor CLIENT_ID:CLIENT_SECRET, codificado em Base64, instruções no passo 6, das instruções de autenticação
==========================  ======================================================================




.. **Troubleshoot:**

.. - Retorno **401**: ACCESSTOKEN_SCOPE_MUSTCONTAINSEXPECTEDSCOPE

.. Sugestão: verifique se está preenchendo os parâmetros corretamente, principalmente o parâmetro **scope**



.. _`authorize`: iniciarintegracao.html#passo-3
.. _`client_secret`: iniciarintegracao.html#passo-6

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
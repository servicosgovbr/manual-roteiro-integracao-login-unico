Exemplo de implementação
========================

Os exemplos são básicos da forma de realizar as requisições para Login Único. Cabe ao desenvolvedor realizar a organização e aplicação da segurança necessária na aplicação consumidora.

JAVA
++++

`Exemplo para download em JAVA`_

**Observações para executar o exemplo JAVA**
--------------------------------------------

Link para biblioteca `jose4j`_ |site externo|.

.. ANDROID (MOBILE)
.. ++++++++++++++++

.. `Exemplo para download para ANDROID`_

.. **Observações para executar o exemplo ANDROID**
.. -----------------------------------------------

.. 1. Alterar configurações da tag "data" do AndroidManifest.xml da pasta /OauthMobileApp:

.. - android:scheme="coloque-aqui-o-esquema-da-sua-uri-de-retorno"
.. - android:host="coloque-aqui-o-host-da-sua-uri-de-retorno"
.. - android:path="coloque-aqui-o-path-da-sua-uri-de-retorno"

.. **Exemplo:** URL de Retorno ("local://exemplo.com/callback"), o scheme sera ("local"), o host será ("exemplo.com") e o path será (/callback)

.. 2. Instalar as bibliotecas:

.. - `RETROFIT`_ |site externo|
.. - `OKHTTP`_ |site externo|
.. - `jose4j`_ |site externo|

.. 3. Alterar as configurações no arquivo Config.java da pasta /OauthMobileApp:

.. - CLIENT_ID("coloque-aqui-o-client-id-da-sua-aplicação")
.. - REDIRECT_URI("coloque-aqui-a-url-retorno-liberada")
.. - AUTHORIZATION_SCOPE("openid profile phone email govbr_empresa")
.. - AUTHORIZATION_ENDPOINT_URI("https://sso.staging.acesso.gov.br/authorize")
.. - LOGOUT_ENDPOINT_URI("https://sso.staging.acesso.gov.br/logout")
.. - TOKEN_ENDPOINT_URI("endereco-backend-para-acessar-conteudo-da-pasta-OauthMobileBackend")

.. 5. Cadastrar variáveis de ambiente do serviço backend da pasta /OauthMobileBackend

.. - CLIENT_ID="coloque-aqui-o-client-id-da-sua-aplicação"
.. - TOKEN_SERVICE_URL="https://sso.staging.acesso.gov.br/token"
.. - REDIRECT_URI="coloque-aqui-o-redirect-uri-identico-ao-informado-nomanifest-do-app-android"
.. - ISSUER="https://seu-domínio"
.. - CREDENTIALS="coloque-aqui-as-credenciais-co"
.. - JWK_RSA_WEB_KEY="coloque-aqui-o-par-de-chaves-que-assinarão-o-token-desessao-gerado-por-algoritmo-RSA-tendo-padrao-jwk"
.. - JWK_SERVICE_URL="https://sso.staging.acesso.gov.br/jwk"
.. - SERVICE_URL="ttps://api.staging.acesso.gov.br"

.. |site externo| image:: _images/site-ext.gif
.. _`jose4j` : https://javalibs.com/artifact/org.bitbucket.b_c/jose4j
.. _`firebase/php-jwt`: https://github.com/firebase/php-jwt
.. _`RETROFIT`: https://square.github.io/retrofit/
.. _`OKHTTP`: https://square.github.io/okhttp/
.. _`Exemplo para download para ANDROID`: arquivos/android-oauth-sdk-master.zip
.. _`Exemplo para download em JAVA` : arquivos/ExemploIntegracaoGovBr.java

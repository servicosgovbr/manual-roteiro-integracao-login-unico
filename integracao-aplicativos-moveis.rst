Integração do Login gov.br com Aplicativos Móveis (Android e iOS)
===============================================================

 ``ATENÇÃO``  **Roteido em desenvolvimento, não é válido como documentação para integração**

Esta seção descreve as boas práticas, requisitos técnicos e o fluxo recomendado
para a integração do **Login gov.br**, baseado em **OpenID Connect (OIDC)**,
com **aplicativos móveis Android e iOS**.

O objetivo é garantir segurança, interoperabilidade e conformidade com
o modelo de autenticação adotado pelo gov.br.

----

Princípios gerais para aplicativos móveis
-----------------------------------------

A integração do Login gov.br com aplicativos móveis segue os mesmos padrões
utilizados em aplicações web, com adaptações específicas para o ambiente mobile.

Requisitos obrigatórios
~~~~~~~~~~~~~~~~~~~~~~~

* O fluxo de autenticação **DEVE** ser realizado em navegador externo ao aplicativo.
* **NÃO DEVE** ser utilizado WebView embutido no aplicativo.
* O redirecionamento de retorno **DEVE** utilizar URLs HTTPS válidas.
* O aplicativo **DEVE** receber o retorno por meio de mecanismos nativos
  do sistema operacional.

Esses requisitos são necessários para:

* Garantir a segurança do usuário.
* Evitar captura indevida de credenciais.
* Assegurar conformidade com o padrão OpenID Connect.

----

Modelo de redirecionamento em aplicativos móveis
------------------------------------------------

Aplicativos móveis não recebem redirecionamentos HTTP diretamente,
como ocorre em aplicações web.

Por esse motivo, o retorno do Login gov.br ocorre em duas etapas distintas:

1. Redirecionamento HTTPS realizado pelo Login gov.br.
2. Interceptação desse endereço pelo sistema operacional (Android ou iOS),
   que direciona o usuário de volta ao aplicativo.

Fluxo resumido
~~~~~~~~~~~~~~

::

   Aplicativo
      ↓
   Navegador do sistema
      ↓
   Login gov.br
      ↓
   Redirect URI (HTTPS)
      ↓
   Sistema operacional
      ↓
   Aplicativo

----

Cadastro da aplicação no Login gov.br
------------------------------------

O cadastro da aplicação móvel no Login gov.br segue os mesmos campos básicos
de uma aplicação web, com observações específicas para o contexto mobile.

Identificação do aplicativo
~~~~~~~~~~~~~~~~~~~~~~~~~~~

No cadastro da aplicação deve ser informado um identificador único,
correspondente ao identificador oficial do aplicativo:

* **Android**: ``applicationId`` (exemplo: ``br.gov.meuapp``)
* **iOS**: ``bundleIdentifier`` (exemplo: ``br.gov.meuapp``)

Este identificador:

* **NÃO é um endereço de redirecionamento**
* É utilizado apenas para identificar o aplicativo no ecossistema mobile
* Pode variar entre ambientes (homologação e produção)

Exemplo:

* Homologação: ``br.gov.meuapp.hml``
* Produção: ``br.gov.meuapp``

----

Home Page
~~~~~~~~~

Deve ser informado um endereço HTTPS válido, preferencialmente associado
à aplicação ou ao órgão responsável.

Exemplo:

::

   https://meuapp.meudominio.gov.br

----

Redirect URI
~~~~~~~~~~~~

O *Redirect URI* define o endereço para o qual o Login gov.br retornará
o usuário após a autenticação.

Para aplicativos móveis:

* **DEVE** ser uma URL HTTPS.
* **NÃO DEVE** utilizar esquemas customizados (por exemplo: ``meuapp://``).
* **DEVE** corresponder exatamente ao endereço configurado no aplicativo.

Exemplo:

::

   https://meuapp.meudominio.gov.br/callback

É recomendada a separação de ambientes:

* Homologação:
  ::

     https://meuapp-hml.meudominio.gov.br/callback

* Produção:
  ::

     https://meuapp.meudominio.gov.br/callback

----

Redirect URI de Logout
~~~~~~~~~~~~~~~~~~~~~

Caso a aplicação implemente logout integrado, deve ser informado
um endereço HTTPS adicional.

Exemplo:

::

   https://meuapp.meudominio.gov.br/logout

----

Configuração do aplicativo Android
----------------------------------

Abertura do fluxo de autenticação
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

O aplicativo Android **DEVE** abrir o fluxo de autenticação utilizando
o navegador do sistema, preferencialmente por meio de **Chrome Custom Tabs**.

O uso de WebView embutido é **VEDADO**.

----

Recebimento do retorno (App Links)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Para que o Android direcione automaticamente o retorno do Login gov.br
ao aplicativo, deve ser configurado um **App Link**.

O App Link associa explicitamente um domínio HTTPS a um aplicativo Android,
por meio de uma validação feita pelo próprio sistema operacional.

Configuração no aplicativo
^^^^^^^^^^^^^^^^^^^^^^^^^^

O aplicativo deve declarar que aceita a URL cadastrada como *Redirect URI*,
considerando:

* Scheme: ``https``
* Host: domínio cadastrado
* Path: caminho definido no redirect

----

Configuração no servidor (Android)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

O domínio utilizado no *Redirect URI* **DEVE** disponibilizar o arquivo:

::

   https://SEU_DOMINIO/.well-known/assetlinks.json

Exemplo:

::

   https://meuapp.meudominio.gov.br/.well-known/assetlinks.json

Esse arquivo é uma declaração pública que associa o domínio ao aplicativo
Android autorizado a abrir seus links.

Exemplo de conteúdo:

::

   [
     {
       "relation": ["delegate_permission/common.handle_all_urls"],
       "target": {
         "namespace": "android_app",
         "package_name": "br.gov.meuapp",
         "sha256_cert_fingerprints": [
           "AA:BB:CC:DD:EE:FF:11:22:..."
         ]
       }
     }
   ]

Observações importantes:

* O ``package_name`` **DEVE** corresponder ao ``applicationId`` do aplicativo.
* O ``sha256_cert_fingerprints`` **DEVE** corresponder ao certificado usado
  para assinar o aplicativo.
* Certificados de homologação e produção **possuem fingerprints diferentes**.

Cada ambiente (homologação e produção) deve possuir seu próprio
``assetlinks.json``, apontando para o aplicativo e certificado corretos.

----

Configuração do aplicativo iOS
------------------------------

Abertura do fluxo de autenticação
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

O aplicativo iOS **DEVE** iniciar o fluxo de autenticação utilizando
mecanismos nativos do sistema, como ``ASWebAuthenticationSession``.

O uso de WebView embutido é **VEDADO**.

----

Recebimento do retorno (Universal Links)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

No iOS, o retorno do login ocorre por meio de **Universal Links**,
que funcionam de forma equivalente aos App Links do Android.

Configuração no aplicativo
^^^^^^^^^^^^^^^^^^^^^^^^^^

O aplicativo deve declarar o domínio como associado ao seu
``bundleIdentifier``.

----

Configuração no servidor (iOS)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

O domínio utilizado no *Redirect URI* **DEVE** disponibilizar o arquivo:

::

   https://SEU_DOMINIO/.well-known/apple-app-site-association

Exemplo:

::

   https://meuapp.meudominio.gov.br/.well-known/apple-app-site-association

Esse arquivo permite que o iOS valide que o domínio pertence ao aplicativo.

Exemplo de conteúdo:

::

   {
     "applinks": {
       "apps": [],
       "details": [
         {
           "appID": "ABCDE12345.br.gov.meuapp",
           "paths": ["/callback"]
         }
       ]
     }
   }

Onde:

* ``ABCDE12345`` corresponde ao *Team ID* da Apple.
* ``br.gov.meuapp`` corresponde ao *Bundle Identifier*.
* O ``appID`` pode variar entre homologação e produção.

----

Uso de esquemas customizados
----------------------------

Esquemas customizados, como:

::

   meuapp://callback
   br.gov.meuapp://callback

**NÃO DEVEM** ser utilizados como *Redirect URI* no Login gov.br.

Esses esquemas:

* Não são verificáveis por DNS.
* Não utilizam TLS.
* Não permitem validação de propriedade do destino.

Podem existir apenas para uso interno no aplicativo,
não fazendo parte do fluxo oficial de redirecionamento.

----

Considerações de segurança
--------------------------

* O Login gov.br não delega a segurança do redirecionamento
  ao sistema operacional do usuário.
* O uso de HTTPS garante:

  * Identificação do domínio.
  * Validação por certificado digital.
  * Rastreabilidade e auditoria.

A interceptação do link pelo aplicativo ocorre somente
após a validação HTTPS realizada pelo sistema operacional.

----

Boas práticas recomendadas
--------------------------

* Utilizar domínios distintos para homologação e produção.
* Manter identificadores de aplicativo distintos por ambiente.
* Utilizar o mesmo padrão de *Redirect URI* em Android e iOS.
* Garantir que os arquivos de associação estejam acessíveis publicamente.
* Validar rigorosamente a correspondência entre cadastro, servidor e aplicativo.

----

Resumo técnico
--------------

* Aplicativos móveis utilizam HTTPS como ponto de retorno.
* Android utiliza **App Links** com ``assetlinks.json``.
* iOS utiliza **Universal Links** com ``apple-app-site-association``.
* Esquemas customizados não são aceitos como *Redirect URI*.
* WebView embutido não deve ser utilizado.

----

Conclusão
---------

A integração do Login gov.br com aplicativos móveis segue um modelo
padronizado, seguro e interoperável, alinhado às boas práticas
internacionais de OpenID Connect.

A correta configuração do cadastro da aplicação, do aplicativo
e dos arquivos de associação no servidor é essencial para garantir
o funcionamento do fluxo de autenticação e a segurança do cidadão.

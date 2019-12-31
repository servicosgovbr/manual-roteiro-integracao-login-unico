Execução dos Exemplos de Integração
===================================

PHP
+++

Caso você queira ver o exemplo PHP funcionando execute com o Docker instalado:

.. code-block:: console

    $ docker run -d -p 80:80 --env CLIENT_ID='MEU-CLIENT-ID' --env SECRET='MEU-SECRET' \
      --env REDIRECT_URI='http://MINHA-URL' \
      --name meu-login-teste govbr/login-php-exemplo

.. warning::
    Altere as variáveis antes de executar o comando. Caso a redirect_uri não seja para localhost coloque no seu /etc/hosts um redirect para o localhost.

Depois acesso o http://localhost ou o redirect_uri alterado no hosts.

Gif mostrando como executa o exemplo:

.. figure:: _static/exemplo-docker.*
   :alt: Exemplo com o comando docker em PHP.

   Exemplo com o comando docker em PHP.

.. |site externo| image:: _images/site-ext.gif

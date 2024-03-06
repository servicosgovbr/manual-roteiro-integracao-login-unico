Presença do Segundo Fator de Autenticação (2FA) no Login Único 
==============================

Para que o sistema integrado ao Login Único possa identificar a entrada pelo Segundo Fator de Autenticação, basta analisar o atributo **AMR** do ACCESS_TOKEN conforme o exemplo:

.. code-block:: JSON

	{
		"sub": "(CPF do usuário autenticado)",
		"aud": "Client ID da aplicação onde o usuário se autenticou",
		"scope": ["(Escopos autorizados pelo provedor de autenticação.)"],
		"amr": ["mfa","otp"],
		"iss": "(URL do provedor de autenticação que emitiu o token.)",
		"exp": "(Data/hora de expiração do token)",
		"iat": "(Data/hora em que o token foi emitido.)",
		"jti": "(Identificador único do token, reconhecido internamente pelo provedor de autenticação.)",

	}

Orientamos a seguir os `passos para realizar a integração`_ e ter acesso ao conteúdo do ACCESS_TOKEN no momento da autenticação



.. |site externo| image:: _images/site-ext.gif
.. _`passos para realizar a integração`: iniciarintegracao.html

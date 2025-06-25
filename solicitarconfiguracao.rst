Alteração de Credencial do Login Único
=======================================

.. Solicitação de Configuração
.. +++++++++++++++++++++++++++

.. Para utilização do sistema Login Único, há necessidade de liberar os ambientes para aplicação cliente possa utilizar. Essa liberação ocorre pelos passos:

.. 1. Preenchimento do `Plano de Integração`_. Leia atentamente as instruções de preenchimento que constam no próprio documento/
.. 2. Geração da Chave PGP - A chave PGP é solicitada para transmissão das credenciais de forma segura. Informações sobre como `administrar as chaves PGP para credenciais do Login Único`_.

.. Para encaminhamento das informações aos integrantes da Secretaria de Governança Digital (SGD) do Ministério da Economia (ME), deverá seguir as orientações:

.. 1. A assinatura digital do documento deverá ser pelo Representante Legal do órgão ou entidade dona do serviço a ser integrado, e Representante Técnico. Ambos devem constar na tabela do item 3. O documento deve ter o formato .doc, .pdf ou .odt. Não serão aceitos documentos escaneados.;
.. 2. A chave púbica PGP deverá ser gerada pelo Representante Legal do órgão ou entidade dona do serviço a ser integrado, e Representante Técnico. Ambos devem constar na tabela do item 3;
.. 3. Com recebimento do documento e da chave pública PGP, todos com correta completude das informações, a credencial de teste ou produção será gerada e encaminhada aos e-mails dos representantes descritos na tabela do item 3 deste documento;
.. 4. O Assunto do e-mail de liberação de chaves terá o padrão: **CHAVE DO AMBIENTE [nome do ambiente] – [Nome do Órgão/Entidade] – UF**;
.. 5. A chave de produção somente será emitida após comprovação da integração com sucesso ao ambiente de TESTE. Para fins de comprovação, deve ser encaminhado para o e-mail com vídeo da integração em funcionamento, junto com o Plano de Integração preenchido com as URLs do ambiente de produção do órgão/entidade e chave pública PGP do Órgão/Entidade. **ATENÇÃO: SÃO PERMITIDAS APENAS URLS com HTTPS NO AMBIENTE DE PRODUÇÃO**.
.. 6. O Órgão/Entidade **DEVE** avisar, por meio de email, que a integração está disponível para sociedade;

.. **O endereço de envio encontra-se no** `Plano de Integração`_.

Alteração de Configuração
+++++++++++++++++++++++++

Para alterar as configurações da credencial, deverá seguir as orientações:

1. Enviar e-mail informando o CLIENT_ID no qual as alterações deverão ser aplicadas;
2. O Assunto do e-mail de alteração de chaves deve seguir o padrão: **ALTERAÇÃO EM CHAVE DO AMBIENTE [nome do ambiente] – [client_id]**;
3. No corpo do e-mail de alteração, o responsável deve informar

=============================  ======================================================================
**Client_id**                  **Identificação do Client_id que deseja alterar**
-----------------------------  ----------------------------------------------------------------------
**URL de Retorno**             URL Retorno deseja alterar
**URL de Página Inicial**      URL da Página Inicial (Home Page), deseja alterar
**URL de Logout**              URL de Logout deseja alterar
=============================  ======================================================================

**O endereço de envio encontra-se no** `Plano de Integração`_.

.. _`Plano de Integração`: arquivos/Modelo_PlanodeIntegracao_LOGINUNICO_Versao-4.2.doc
.. _`administrar as chaves PGP para credenciais do Login Único`: chavepgp.html

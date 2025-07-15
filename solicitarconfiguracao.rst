﻿Alteração de Credencial do Login Único
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

Para modificar as configurações das credenciais, siga as orientações abaixo: 

**Integrações em andamento**

1. Acesse o `Portal do Serviço de Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`_ e clique no botão **"Acompanhamento"**.

2. Na aba "Enviar dados/dúvidas" do seu protocolo de solicitação, forneça as seguintes informações:

=============================  ======================================================================
**Client_id / Ambiente**       client_id que deseja alterar / Ambiente (produção ou homologação)
-----------------------------  ----------------------------------------------------------------------
**URL de Retorno**             URL Retorno deseja alterar
**URL de Página Inicial**      URL da Página Inicial (Home Page), deseja alterar
**URL de Logout**              URL de Logout deseja alterar
=============================  ======================================================================

**Integrações concluídas**

1. Envie um e-mail para integracao-acesso-govbr@economia.gov.br com as informações solicitadas a seguir. 

2. No campo "Assunto" do e-mail, siga este padrão: **ALTERAÇÃO DE CREDENCIAL DO LOGIN ÚNICO**.

3. No corpo do e-mail, forneça as seguintes informações:

=============================  ======================================================================
**Client_id / Ambiente**       client_id que deseja alterar / Ambiente (produção ou homologação)
-----------------------------  ----------------------------------------------------------------------
**URL de Retorno**             URL Retorno deseja alterar
**URL de Página Inicial**      URL da Página Inicial (Home Page), deseja alterar
**URL de Logout**              URL de Logout deseja alterar
=============================  ======================================================================

.. **O endereço de envio encontra-se no** `Plano de Integração`_.

.. _`Portal do Serviço de Integração aos Produtos do Ecossistema da Identidade Digital GOV.BR`: https://www.gov.br/governodigital/pt-br/estrategias-e-governanca-digital/transformacao-digital/servico-de-integracao-aos-produtos-de-identidade-digital-gov.br
.. _`Plano de Integração`: arquivos/Modelo_PlanodeIntegracao_LOGINUNICO_Versao-4.2.docx
.. _`administrar as chaves PGP para credenciais do Login Único`: chavepgp.html

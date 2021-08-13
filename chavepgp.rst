Administração das chaves PGP para credenciais do Login Único
============================================================


Como criar um par de chaves PGP
+++++++++++++++++++++++++++++++

O PGP (abreviação de Pretty Good Privacy, ou Muito Boa Privacidade) é um programa de criptografia de chave pública e privada altamente seguro e utilizado para proteção de conteúdo enviado por correio eletrônico.
 
Orientaremos como criar par de chaves PGP por meio do programa Gpg4win (versão windows), porém existem outras aplicativos permitem geração.

Sigam os passos para criação:

1. Realize download do aplicativo `Gpg4win`_ ;
2. Siga os passos até finalizar instalação;
3. Execute o programa Kleopatra.

.. figure:: _images/tela_inicial_kleopatra_pgp.jpg
   :align: center
   :alt:
 
4. Selecione item do Menu **Arquivo / Novo Par de chaves**;

.. figure:: _images/tela_kleopatra_menu_novo_par_chaves_pgp.jpg
   :align: center
   :alt:

5. Selecione opção **Criar um par de chaves OpenPGP pessoal**;

.. figure:: _images/tela_confirmacao_tipopar_chaves_pgp.jpg
   :align: center
   :alt:

6. Informe o **Nome** e o **Email**, marque a opção **Proteger a chave com senha** e clique no botão **Criar**. O nome e email deverão ser algum dos presentes no Item **Responsáveis pela Integração** no Plano de Integração 

.. figure:: _images/tela_informacoes_nome_email_chave_pgp.jpg
   :align: center
   :alt: 

7. Concluída a criação clique no botão **Terminar**;

.. figure:: _images/tela_finalizacao_criacao_chave_PGP.jpg
   :align: center
   :alt:
   
8. Selecione a chave criada, clique item do Menu **Arquivo / Exportar** para salvar chave pública;

.. figure:: _images/tela_exporta_certificado_chave_publica_PGP.jpg
   :align: center
   :alt:

9. Enviar chave pública junto com Plano de Integração.

Como ler arquivo da credencial com chave PGP
++++++++++++++++++++++++++++++++++++++++++++

Orientaremos como ler arquivo por meio do programa Gpg4win (versão windows), porém existem outras aplicativos permitem geração.

Sigam os passos para criação:

1. Realize download do aplicativo `Gpg4win`_ ;
2. Siga os passos até finalizar instalação;
3. Execute o programa Kleopatra.

.. figure:: _images/tela_inicial_kleopatra_pgp.jpg
   :align: center
   :alt:

4. Selecione item do Menu **Arquivo / Descriptografar/Verificar**;    

.. figure:: _images/tela_inicial_descriptografar_arquivo_PGP.jpg
   :align: center
   :alt:

5. Selecione o arquivo com a credencial criptografada, digite a senha da geração do par de chaves, clique no botão **OK**;

.. figure:: _images/tela_digitar_senha_descriptografar_PGP.jpg
   :align: center
   :alt:
   
6. Selecione a pasta deseja salvar o aquivo descriptografado e clique no botão **Save all**;

.. figure:: _images/tela_salva_arquivo_descriptografia_PGP.jpg
   :align: center
   :alt:
   
.. _`Gpg4win`: https://files.gpg4win.org/gpg4win-3.1.16.exe   
   
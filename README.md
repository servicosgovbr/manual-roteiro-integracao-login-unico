Roteiro de Integração do Login Único para os órgãos 
===================================================


Sobre
-----

Documentação para auxiliar órgãos interessados em integrar com o Login Único GOV.BR 
[![Status da Documentação](https://readthedocs.org/projects/roteiro-de-integracao-do-login-unico/badge/?version=stable)](https://manual-roteiro-integracao-login-unico.servicos.gov.br/pt/stable/?badge=stable)


Tabela de conteúdos
-------------------

   * [Sobre](#sobre)
   * [Tabela de conteúdos](tabela-de-conteudos)
   * [Instalação](#instalação)
      * [Rodando localmente](#rodando-localmente) 
   * [Como contribuir](#como-contribuir)
   * [Tecnologias](#tecnologias)

Instalação 
----------

Antes de começar, você vai precisar ter instalado em sua máquina as seguintes ferramentas:
[Git](https://git-scm.com), [Python](https://python.org/). 
Além disto é bom ter um editor para trabalhar com o código como [VSCode](https://code.visualstudio.com/)

### Rodando localmente

```bash
# Clone este repositório
$ git clone https://github.com/servicosgovbr/manual-roteiro-integracao-login-unico

# Acesse a pasta do projeto no terminal/cmd
$ cd manual-roteiro-integracao-login-unico

# Crie um virtualenv
$ python3 -m venv p3

# Faça o source da sua nova virtualenv
$ source p3/bin/activate 

# Instale as dependências
$ pip install -r requirements.txt

# Execute a aplicação em modo de desenvolvimento
$ sphinx-autobuild . _build

# O servidor inciará na porta:8000 - acesse <http://localhost:8000>
$ xdg-open http://localhost:8000
```

Como contribuir
---------------

[Instale e execute](#instalação) e altere ou adicione páginas na pasta. Depois envie seu Pull Request.
Você pode abrir issues também.


Tecnologias
-----------

- [reStructuredText](https://docutils.sourceforge.io/rst.html)
- [Read the docs](https://readthedocs.org)

Níveis de Autenticação e Selos de Confiabilidade 
================================================

Os Níveis de Autenticação tem como principal característica ser um recurso de segurança da informação das identidades, que permitem flexibilidade para realização do acesso e atribuição dos selos de conformidade de acordo com as normas de segurança. Possume a divisão:

- **Nível Simples**
- **Nível Avançado**
- **Nível Qualificado**

Os Selos de Confiabilidade estão presentes em cada nível de autenticação e consistem em orientar para qualificação das contas com a obtenção dos atributos autoritativos do cidadão a partir das bases oficias de governo, por meio das quais permitirão a utilização da credencial de acesso em sistemas internos dos clientes e serviços providos diretamente ao cidadão.

Uso possível para os níveis e os selos é o utilização do nível de confiança cadastral pelos serviços para aplicar controle de acesso às funcionalidades mais críticas.

O mapeamento entre nível e selo presente no Login Único será apresentado para determinar como sistema integrado deverá receber a conta do cidadão.

1 - **Nível Simples**

- **Selo Cadastro Básico com Validação de Dados Pessoais**: Validação do cadastro do cidadão por meio da base de Cadastro de Pessoas Físicas (Ministério da Economia / Receita Federal). Esse é adquirido com a resposta de algumas perguntas do cadastro do cidadão em dois momentos (cadastramento da conta no Login Único ou na area principal do Login Único);
- **Selo Cadastro Básico com Validação de Dados Previdênciarios**: Validação do cadastro do cidadão por meio da base de Casdatro Nacional de Informações Sociais (CNIS / INSS). Esse é adquirido com a resposta de algumas perguntas sobre base previdência (CNIS) em dois momentos (cadastramento da conta no Login Único tendo como ponto de partida o acesso ao sistema `Meu INSS`_ ou na area principal do Login Único);

2 - **Nível Avançado**

- **Selo Balcão Presencial (INSS)**: Validação do cadastro do cidadão por meio do Balcão presencial localizado nas agências do INSS. Esses geram uma senha temporária para cidadão se autenticar na plataforma do Login Único. 
- **Selo Internet Banking**: Validação do cadastro do cidadão por meio da plataforma de Internet Banking dos bancos conveniados. Esses geram uma senha temporária para o cidadão se autenticar. As orientações estão presentes em cada banco conveniado; 
- **Selo Validação Facial**: Validação do cadastro do cidadão por meio de biometria facial. A base utilizada para comparação é a da Carteira Nacional de Habilitação (Ministério da Infraestrutura / Denatran). Está em construção em breve será disponibilizado aos Órgãos e cidadãos.
- **Selo Cadastro Básico com Validação em Base de Dados de Servidores Públicos da União**: Validação do cadastro por meio de base de dados de Servidores Públicos da União. A base de dados pertence ao Ministério da Economia e necessita de senha no Sistema de Gestão de Acesso `SIGAC/SIGEPE`_.


3 - **Nível Qualificado**

- **Selo de Validação de Biometria da Digital**: Validação do cadastro do cidadão por meio de chegagem da biometria da digital. A base utilizada para comparação é a eleitoral (Tribunal Superior Eleitoral -TSE). Está em construção para forma de liberação para os Órgãos e cidadãos;
- **Selo de Certificado Digital de Pessoa Física**: Validação do cadastro do cidadão por meio da utilização de certificado digital de pessoal física. Esse é adquirido por meio dos certificados digitais do tipo máquina (A1) ou Token (A3) e em dois momentos (cadastramento da conta no Login Único ou na area principal do Login Único);  
- **Selo de Certificado Digital de Pessoa Jurídica**: Atribuição de um representante de empresa por meio da utilização de certificado digital de pessoa jurídica. Esse é adquirido por meio dos certificados digitais do tipo máquina (A1) ou Token (A3) e acessando area principal do Login Único;

.. |site externo| image:: _images/site-ext.gif
.. _`Meu INSS` : https://meu.inss.gov.br/
.. _`SIGAC/SIGEPE` : https://sso.gestaodeacesso.planejamento.gov.br/cassso/login              
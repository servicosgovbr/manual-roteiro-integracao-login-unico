Retornos Padrão de APIs REST
============================

Esta tabela descreve os principais códigos de retorno HTTP utilizados em APIs RESTful,
agrupados por categoria (1xx, 2xx, 3xx, 4xx, 5xx).  
Esses códigos indicam o resultado de uma requisição e ajudam o cliente a entender
se a operação foi bem-sucedida, redirecionada, inválida ou se houve erro no servidor.

.. list-table::
   :header-rows: 1
   :widths: 10 20 70

   * - **Código**
     - **Descrição**
     - **Explicação**
   * - **1xx – Informativo**
     - A requisição foi recebida e o processo continua.
     - Usado raramente em APIs REST. Indica que o servidor recebeu a requisição, mas ainda está processando.
   * - 100 Continue
     - Continuação
     - O cliente deve continuar com a requisição (geralmente após enviar cabeçalhos iniciais).
   * - 101 Switching Protocols
     - Mudança de protocolo
     - O servidor concorda em mudar o protocolo conforme solicitado no cabeçalho `Upgrade`.
   * - 102 Processing
     - Processando
     - Indica que o servidor recebeu a requisição e ainda está processando (usado em WebDAV).

   * - **2xx – Sucesso**
     - A requisição foi recebida, entendida e aceita com sucesso.
     - Esses códigos indicam que o servidor executou corretamente a operação solicitada.
   * - 200 OK
     - Sucesso
     - A requisição foi bem-sucedida e o servidor retornou o resultado esperado (GET, PUT, PATCH).
   * - 201 Created
     - Criado
     - Recurso criado com sucesso (comumente em requisições POST). O cabeçalho `Location` pode indicar a nova URL.
   * - 202 Accepted
     - Aceito
     - A requisição foi aceita para processamento, mas ainda não concluída.
   * - 204 No Content
     - Sem conteúdo
     - A requisição foi bem-sucedida, mas não há corpo de resposta (usado em DELETE, PUT).
   * - 206 Partial Content
     - Conteúdo parcial
     - O servidor retornou parte dos dados solicitados (usado com cabeçalhos `Range`).

   * - **3xx – Redirecionamento**
     - Indica que é necessário realizar uma ação adicional para completar a requisição.
     - Em APIs REST, redirecionamentos são menos comuns, mas podem ocorrer.
   * - 301 Moved Permanently
     - Movido permanentemente
     - O recurso foi movido para outra URL; o cliente deve atualizar seus links.
   * - 302 Found
     - Encontrado
     - O recurso foi temporariamente movido; o cliente deve usar a nova URL apenas nesta requisição.
   * - 303 See Other
     - Veja outro
     - O cliente deve buscar o recurso em outra URI (usado após POST).
   * - 304 Not Modified
     - Não modificado
     - Indica que o recurso não mudou desde a última requisição (cache válido).
   * - 307 Temporary Redirect
     - Redirecionamento temporário
     - Similar a 302, mas o método HTTP deve permanecer o mesmo.
   * - 308 Permanent Redirect
     - Redirecionamento permanente
     - Similar a 301, mas preserva o método original da requisição.

   * - **4xx – Erro do cliente**
     - Indica que a requisição contém erro ou está incorreta.
     - O cliente deve corrigir a requisição antes de tentar novamente.
   * - 400 Bad Request
     - Requisição inválida
     - O servidor não pôde processar devido a erro de sintaxe ou parâmetros incorretos.
   * - 401 Unauthorized
     - Não autorizado
     - Falha na autenticação. Requer credenciais válidas (ex: token JWT expirado).
   * - 403 Forbidden
     - Proibido
     - O servidor entendeu a requisição, mas o cliente não tem permissão.
   * - 404 Not Found
     - Não encontrado
     - O recurso solicitado não existe.
   * - 405 Method Not Allowed
     - Método não permitido
     - O método HTTP usado não é suportado pelo recurso (ex: usar DELETE em endpoint apenas GET).
   * - 409 Conflict
     - Conflito
     - Ocorreu conflito com o estado atual do recurso (ex: duplicidade de registro).
   * - 410 Gone
     - Recurso removido
     - O recurso existia, mas foi permanentemente removido.
   * - 422 Unprocessable Entity
     - Entidade não processável
     - A requisição é bem formada, mas contém erros de validação.
   * - 429 Too Many Requests
     - Muitas requisições
     - O cliente excedeu o limite de requisições permitido (rate limit).

   * - **5xx – Erro do servidor**
     - Ocorreu um erro interno ao processar a requisição.
     - Esses códigos indicam falhas do lado do servidor, e não do cliente.
   * - 500 Internal Server Error
     - Erro interno
     - Erro genérico quando algo inesperado ocorre no servidor.
   * - 501 Not Implemented
     - Não implementado
     - O servidor não suporta o método solicitado.
   * - 502 Bad Gateway
     - Gateway inválido
     - O servidor intermediário (proxy/gateway) recebeu resposta inválida de outro servidor.
   * - 503 Service Unavailable
     - Serviço indisponível
     - O servidor está temporariamente fora do ar (sobrecarga ou manutenção).
   * - 504 Gateway Timeout
     - Tempo limite do gateway
     - O servidor intermediário não recebeu resposta a tempo do servidor de origem.
   * - 505 HTTP Version Not Supported
     - Versão HTTP não suportada
     - O servidor não suporta a versão do protocolo HTTP usada na requisição.

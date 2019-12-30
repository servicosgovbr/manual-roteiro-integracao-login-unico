Exemplo de implementação
========================


Os exemplos são básicos da forma de realizar as requisições para Login Único. Cabe ao desenvolvedor realizar a organização e aplicação da segurança necessária na aplicação consumidora.

DOCKER
++++++

Caso você só queira ver o exemplo funcionando execute com o Docker instalado:

$ docker run -d -p 80:80 --env CLIENT_ID='MEU-CLIENT-ID' --env SECRET='MEU-SECRET' --env REDIRECT_URI='http://MINHA-URL' --name meu-login-teste govbr/login-php-exemplo

.. warning::
    Altere as variáveis antes de executar o comando. Caso a redirect_uri não seja para localhost coloque no seu /etc/hosts um redirect para o localhost.

Depois acesso o http://localhost ou o redirect_uri alterado no hosts.

JAVA
++++

Link para biblioteca `jose4j`_ |site externo|.

.. code-block:: java

		package teste;

		import java.io.BufferedReader;
		import java.io.ByteArrayOutputStream;
		import java.io.InputStream;
		import java.io.InputStreamReader;
		import java.math.BigInteger;
		import java.net.URL;
		import java.net.URLEncoder;
		import java.security.SecureRandom;
		import java.util.ArrayList;
		import java.util.Base64;
		import java.util.List;

		import javax.net.ssl.HttpsURLConnection;

		import org.apache.commons.io.IOUtils;
		import org.jose4j.json.internal.json_simple.JSONArray;
		import org.jose4j.json.internal.json_simple.JSONObject;
		import org.jose4j.json.internal.json_simple.parser.JSONParser;
		import org.jose4j.jwk.PublicJsonWebKey;
		import org.jose4j.jwt.JwtClaims;
		import org.jose4j.jwt.consumer.JwtConsumer;
		import org.jose4j.jwt.consumer.JwtConsumerBuilder;

		/**
		 *
		 * O presente código tem por objetivo exemplificar de forma minimalista o
		 * consumo dos serviços utilizados pelo Gov.br.
		 *
		 */

		public class ExemploIntegracaoGovBr {

			/**
			 * O processo de autenticação e autorização de recursos ocorre essencialmente em
			 * três etapas: Etapa 1: Chamada do serviço de autorização do Gov.br; Etapa 2:
			 * Recuperação do Access Token e Etapa 3: Validação do Access Token por meio da
			 * verificação de sua assinatura. Após concluída essas três etapas, a aplicação
			 * cliente terá as informações básicas para conceder acesso de acordo com suas
			 * próprias políticas de autorização. Caso a aplicação cliente necessite de
			 * informações adicionais, fica habilitado o acesso à todos os serviços
			 * (presentes e futuros) fornecidos pelo Gov.br por meio do access token. O
			 * presente código exemplifica a chamada aos seguintes serviços: getUserInfo -
			 * Extração das informações básicas de usuário através do ID Token; Serviço 1:
			 * getFoto - Serviço que recupera a foto do usuário; Serviço 2:
			 * getConfiabilidade - Serviço que recupera os selos de confiabilidade
			 * atribuidos ao usuário; Serviço 3: getEmpresasVinculadas - Serviço que
			 * recupera a lista de empresas vinculadas ao usuário; Serviço 4:
			 * getDadosEmpresa - Serviço que detalha a empresa e o papel do usuário nesta
			 * empresa.
			 *
			 *
			 * *************************************************************************************************
			 *
			 * Informações de uso ------------------ Atribua às variáveis abaixo os valores
			 * de acordo com o seu sistema.
			 *
			 */

			private static final String URL_PROVIDER = "https://sso.staging.acesso.gov.br";
			private static final String URL_SERVICOS = "https://api.staging.acesso.gov.br";
			private static final String REDIRECT_URI = "<coloque-aqui-a-uri>";                                                      //redirectURI informada na chamada do serviço do authorize.
			private static final String SCOPES = "openid+email+phone+profile+govbr_empresa"; 	// Escopos pedidos para a aplicação.
			private static final String CLIENT_ID = "<coloque-aqui-o-clientid-cadastrado-para-o-seu-sistema>";                      //clientId informado na chamada do serviço do authorize.
			private static final String SECRET = "<coloque-aqui-o-secret-cadastrado-para-o-seu-sistema>";                           //secret de conhecimento apenas do backend da aplicação.

			public static void main(String[] args) throws Exception {

				/**
				 * Etapa 1: No Browser, chamar a URL do Authorize para recuperar o code e o
				 * state (opcional) conforme o exemplo abaixo:
				 * https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=<coloque-aqui-o-client-id>&scope=openid+profile+phone+email+govbr_empresa&redirect_uri=<coloque-aqui-a-uri-de-redirecionamento>&nonce=<coloque-aqui-um-numero-aleatorio>&state=<coloque-aqui-um-numero-aleatorio>
				 * Descrição dos parametros: response_type: Sempre "code"; client_id:
				 * Identificador do sistema que usa o Gov.br. Este identificador é único para
				 * cada sistema; scope: Lista de escopos requisitados pelo sistema. Escopos são
				 * agrupamentos de informações cujo acesso deverá ser autorizado pelo cidadão
				 * que acessa o sistema. Cada sistema deverá informar que conjunto de
				 * informações (escopos) deseja; redirect_uri: Uri para qual será feito o
				 * redirect após o login do cidadão (usuário). Para Celulares, usamos uma pseudo
				 * URI; nonce: número aleatório; state: número aleatório (opcional)
				 *
				 * Observação: Sem o escopo "govbr_empresa", não será possível utilizar
				 * o serviço de recuperação de informações de empresas.
				 */

				System.out.println("--------------------Etapa 1 - URL do Serviço Authorize------------------");
				System.out.println("Abra um Browser (Chrome ou Firefox), aperte F12. Clique na aba 'Network'.");
				System.out.println(
						"Cole a URL abaixo no Browser (Chrome ou Firefox) e entre com um usuário cadastrado no Gov.br");
				System.out.println(URL_PROVIDER + "/authorize?response_type=code&client_id=" + CLIENT_ID + "&scope="
						+ SCOPES + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") + "&nonce="
						+ createRandomNumber() + "&state=" + createRandomNumber());

				/**
				 * Etapa 2: De posse do code retornado pelo passo 1, chame o serviço para
				 * recuperar os tokens disponíveis para sua aplicação (Access Token, Id Token) conforme o exemplo abaixo.
				 */

				System.out.println("\n--------------------Etapa 2 - Recuperação dos Tokens de Acesso------------------");
				System.out.println("Digite abaixo o parâmetro 'code' retornado pelo redirect da etapa 1");
				System.out.print("Digite o valor do parâmetro code retornado:");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String code = br.readLine();

				String tokens = extractToken(code);
				System.out.println("JSON retornado:");
				System.out.println(tokens);

				JSONParser parser = new JSONParser();
				JSONObject tokensJson = (JSONObject) parser.parse(tokens);

				String accessToken = (String) tokensJson.get("access_token");
				String idToken = (String) tokensJson.get("id_token");

				/**
				 * Etapa 3: De posse do access token, podemos extrair algumas informações acerca
				 * do usuário. Aproveitamos também para checar a assinatura e tempo de expiração
				 * do token. Para isso, este exemplo usa a biblioteca Open Source chamada
				 * "jose4j" mas qualquer outra biblioteca que implemente a especificação pode
				 * ser usada.
				 *
				 * O Access Token fornece as seguintes informações acerca do usuário: 1- id
				 * client da aplicação à qual o usuário se autenticou; 2- Escopos requeridos
				 * pela aplicação autorizados pelo usuário; 3- CPF do usuário autenticado 4-
				 * Nome completo do usuário cadastrado no Gov.br. Atenção, este é o nome que foi
				 * fornecido pelo usuário no momento do seu cadastro
				 *
				 */

				JwtClaims accessTokenJwtClaims;
				JwtClaims idTokenJwtClaims;
				try {
					accessTokenJwtClaims = processToClaims(accessToken);
					idTokenJwtClaims = processToClaims(idToken);
				} catch (Exception e) {
					System.out.println("Access Token inválido!");
					throw new Exception(e);
				}

				String idClient = accessTokenJwtClaims.getAudience().get(0); // Client Id
				List<String> scopes = accessTokenJwtClaims.getStringListClaimValue("scope"); // Escopos autorizados pelo usuário
				String nomeCompleto = idTokenJwtClaims.getStringClaimValue("name"); // Nome Completo do cadastro feito pelo usuário no Gov.br.
				String fotoUrl = idTokenJwtClaims.getStringClaimValue("picture"); //

				System.out.println("\n--------------------Etapa 3 - Informações obtidas do Access Token------------------");
				System.out.printf("O usuário " + nomeCompleto + " foi autenticado pelo Gov.br para usar o sistema " + idClient
						+ ". Este usuário também autorizou este mesmo sistema à utilizar as informações representadas pelos escopos "
						+ String.join(",", scopes) + ". \n");

				/**
				 * De posse do token de resposta, a aplicação pode usar o id token para extrair
				 * as informações do usuário.
				 *
				 */

				System.out
						.println("\n--------------------Informações do usuário obtidas através do ID Token------------------");
				System.out.println("JSON retornado (base 64):");
				System.out.println(idToken);
				System.out.println("\n\nDados do usuário:");
				System.out.println("CPF: " + idTokenJwtClaims.getSubject()); //CPF do usuário autenticado.
				System.out.println("Nome: " + nomeCompleto); // Nome Completo do cadastro feito pelo usuário no Gov.br.
				System.out.println("Email Validado: " + idTokenJwtClaims.getClaimValue("email_verified")); //(Confirma se o email foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")
				System.out.println("E-Mail: " + idTokenJwtClaims.getClaimValue("email")); //(Endereço de e-mail cadastrado no Gov.br do usuário autenticado. Caso o atributo email_verified do ID_TOKEN tiver o valor false, o atributo email não virá no ID_TOKEN)
				System.out.println("Telefone Validado: " + idTokenJwtClaims.getClaimValue("phone_number_verified")); //(Confirma se o telefone foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")
				System.out.println("Telefone: " + idTokenJwtClaims.getClaimValue("phone_number")); //(Número de telefone cadastrado no Gov.br do usuário autenticado. Caso o atributo phone_number_verified do ID_TOKEN tiver o valor false, o atributo phone_number não virá no ID_TOKEN)
				System.out.println("Link para a foto: " + fotoUrl); //URL de acesso à foto do usuário cadastrada no Gov.br. A mesma é protegida e pode ser acessada passando o access token recebido.
				System.out.println("\n\nDados da Autenticação:");
				System.out.println("Amr: " + idTokenJwtClaims.getClaimValue("amr")); // Fator de autenticação do usuário. Pode ser “passwd” se o mesmo logou fornecendo a senha, ou “x509” se o mesmo utilizou certificado digital ou certificado em nuvem.
				System.out.println("CNPJ: " + idTokenJwtClaims.getClaimValue("cnpj")); // CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica.
				/**
				 * Serviço 1: De posse do access token, a aplicação pode chamar o serviço para receber a foto do usuário.
				 */

				String resultadoFoto = getFoto(fotoUrl, accessToken);

				System.out.println(
						"\n--------------------Serviço 1 - Foto do usuário------------------");
				System.out.println("Foto retornada:");
				System.out.println(resultadoFoto);

				/**
				 * Serviço 2: De posse do access token, a aplicação pode chamar o serviço para
				 * saber quais selos o usuário logado possui.
				 */

				String confiabilidadeJson = getConfiabilidade(accessToken);

				System.out.println(
						"\n--------------------Serviço 2 - Informações acerca da confiabilidade do usuário------------------");
				System.out.println("JSON retornado:");
				System.out.println(confiabilidadeJson);

				List<Long> seloNivels = new ArrayList<Long>();
				for (Object o : (JSONArray) parser.parse(confiabilidadeJson)) {
					if (o instanceof JSONObject) {
						seloNivels.add((Long) ((JSONObject) o).get("nivel"));
					}
				}

				if (seloNivels.contains(new Long(11))) { // Selo de REPRESENTANTE E-CNPJ

					/**
					 * Serviço 3: De posse do access token, a aplicação pode chamar o serviço para
					 * saber quais empresas se encontram vinculadas ao usuário logado.
					 *
					 */

					String empresasJson = getEmpresasVinculadas(accessToken,idTokenJwtClaims.getSubject());

					System.out.println("\n--------------------Serviço 3 - Empresas vinculadas ao usuário------------------");
					System.out.println("JSON retornado:");
					System.out.println(empresasJson);

					/**
					 * Serviço 4: De posse do access token, a aplicação pode chamar o serviço para
					 * obter dados de uma empresa específica e o papel do usuário logado nesta
					 * empresa.
					 */

					JSONObject empresasVinculadasJson = (JSONObject) parser.parse(empresasJson);
					JSONArray cnpjs = (JSONArray) empresasVinculadasJson.get("cnpjs");
					JSONObject cnpj = (JSONObject)cnpjs.get(0);


					if (!cnpjs.isEmpty()) {

						String dadosEmpresaJson = getDadosEmpresa(accessToken, cnpj.get("cnpj").toString() ,idTokenJwtClaims.getSubject());

						System.out.printf(
								"\n--------------------Serviço 4 - Informações acerca da empresa %s------------------",
								cnpjs.get(0));
						System.out.println("JSON retornado:");
						System.out.println(dadosEmpresaJson);

					}
				}

			}

			private static String extractToken(String code) throws Exception {
				String retorno = "";

				String redirectURIEncodedURL = URLEncoder.encode(REDIRECT_URI, "UTF-8");

				URL url = new URL(URL_PROVIDER + "/token?grant_type=authorization_code&code=" + code + "&redirect_uri="
						+ redirectURIEncodedURL);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("authorization", String.format("Basic %s",
						Base64.getEncoder().encodeToString(String.format("%s:%s", CLIENT_ID, SECRET).getBytes())));

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Falhou : HTTP error code : " + conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String tokens = null;
				while ((tokens = br.readLine()) != null) {
					retorno += tokens;
				}

				conn.disconnect();

				return retorno;
			}

			private static JwtClaims processToClaims(String token) throws Exception {
				URL url = new URL(URL_PROVIDER + "/jwk");
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Falhou : HTTP error code : " + conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String ln = null, jwk = "";
				while ((ln = br.readLine()) != null) {
					jwk += ln;
				}

				conn.disconnect();

				JSONParser parser = new JSONParser();
				JSONObject tokensJson = (JSONObject) parser.parse(jwk);

				JSONArray keys = (JSONArray) tokensJson.get("keys");

				JSONObject keyJSONObject = (JSONObject) keys.get(0);

				String key = keyJSONObject.toJSONString();

				PublicJsonWebKey pjwk = PublicJsonWebKey.Factory.newPublicJwk(key);

				JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime() // Exige que o token tenha um
																								// tempo de validade
						.setExpectedAudience(CLIENT_ID).setMaxFutureValidityInMinutes(60) // Testa se o tempo de validade do
																							// access token é inferior ou igual
																							// ao tempo máximo estipulado (Tempo
																							// padrão de 60 minutos)
						.setAllowedClockSkewInSeconds(30) // Esta é uma boa prática.
						.setRequireSubject() // Exige que o token tenha um Subject.
						.setExpectedIssuer(URL_PROVIDER + "/") // Verifica a procedência do token.
						.setVerificationKey(pjwk.getPublicKey()) // Verifica a assinatura com a public key fornecida.
						.build(); // Cria a instância JwtConsumer.

				return jwtConsumer.processToClaims(token);
			}

			private static String getEmpresasVinculadas(String accessToken, String cpf) throws Exception {
				String retorno = "";

				URL url = new URL(URL_SERVICOS + "/empresas/v1/representantes/"+cpf+"/empresas?visao=simples");
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("authorization", "Bearer "+accessToken);

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Falhou : HTTP error code : " + conn.getResponseCode());
				}

				String output;
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				while ((output = br.readLine()) != null) {
					retorno += output;
				}

				conn.disconnect();

				return retorno;
			}

			private static String getDadosEmpresa(String accessToken, String cnpj, String cpf) throws Exception {
				String retorno = "";

				URL url = new URL(URL_SERVICOS + "/empresas/v1/representantes/" + cpf + "/empresas/" + cnpj);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("authorization", "Bearer "+accessToken);

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Falhou : HTTP error code : " + conn.getResponseCode());
				}

				String output;
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				while ((output = br.readLine()) != null) {
					retorno += output;
				}

				conn.disconnect();

				return retorno;
			}

			private static String getFoto(String fotoUrl, String accessToken) throws Exception {
				URL url = new URL(fotoUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Authorization", "Bearer "+accessToken);

				if (conn.getResponseCode() != 200) {
					return "Foto nao encontrada: " + conn.getResponseCode();
				}

				String foto = null;
				try (InputStream inputStream = conn.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
					IOUtils.copy(inputStream, baos);
					String mimeType = conn.getHeaderField("Content-Type");
					foto = new String("data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(baos.toByteArray()));
				}

				conn.disconnect();

				return foto;
			}

			private static String getConfiabilidade(String accessToken) throws Exception {
				String retorno = "";

				URL url = new URL(URL_SERVICOS + "/api/info/usuario/selo");
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Authorization", "Bearer "+accessToken);

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Falhou : HTTP error code : " + conn.getResponseCode());
				}

				String output;
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				while ((output = br.readLine()) != null) {
					retorno += output;
				}

				conn.disconnect();

				return retorno;
			}

			private static String createRandomNumber() {
				return new BigInteger(50, new SecureRandom()).toString(16);

			}

		}

PHP
+++

Link para biblioteca `firebase/php-jwt`_ |site externo|.

Arquivo CSS
-----------

.. code-block:: CSS

		* {
			box-sizing: border-box;
		}

		body {
				font-family: Arial, Helvetica, sans-serif;
				margin: 0;
		}

		.header {
				padding: 20px;
				text-align: center;
				background: rgb(240, 242, 241);
				color: rgb(51, 51, 51);
		}

		.header h1 {
				font-size: 40px;
		}

		.navbar {
				overflow: hidden;
				background-color: #333;
				position: sticky;
				position: -webkit-sticky;
				top: 0;
		}

		.navbar a {
				float: left;
				display: block;
				color: white;
				text-align: center;
				padding: 14px 20px;
				text-decoration: none;
		}

		.navbar a.right {
				float: right;
		}

		.navbar a:hover {
				background-color: #ddd;
				color: black;
		}

		.navbar a.active {
				background-color: #666;
				color: white;
		}

		.row {
			display: -ms-flexbox; /* IE10 */
			display: -webkit-box;                  /* chrome */
			-webkit-justify-content: space-around; /* chrome */
			-webkit-flex-flow: row wrap;           /* chrome */
			-webkit-align-items: stretch;          /* chrome */
			display: flex;
			-ms-flex-wrap: wrap; /* IE10 */
			flex-wrap: wrap;
		}

		.left_side {
			-ms-flex: 30%; /* IE10 */
			flex: 30%;
			width: 30%; /* chrome */
			background-color: #f1f1f1;
			padding: 20px;
		}

		.right_side {
			-ms-flex: 70%; /* IE10 */
			flex: 70%;
			width: 70%; /* chrome */
			background-color: white;
			padding: 20px;
		}

		.result {
			background-color: #aaa;
			width: 100%;
			padding: 20px;
		}

		.resultValido {
			background-color: green;
			width: 100%;
			padding: 20px;
		}

		.resultInvalido {
			background-color: red;
			width: 100%;
			padding: 20px;
		}

		/* Footer */
		.footer {
			padding: 20px;
			text-align: center;
			background: #ddd;
		}

		/* Responsive layout - when the screen is less than 700px wide, make the two columns stack on top of each other instead of next to each other */
		@media screen and (max-width: 700px) {
			.row {
				flex-direction: column;
			}
		}

		/* Responsive layout - when the screen is less than 400px wide, make the navigation links stack on top of each other instead of next to each other */
		@media screen and (max-width: 400px) {
			.navbar a {
				float: none;
				width: 100%;
			}
		}

		pre {
			white-space: pre-wrap;       /* css-3 */
			white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */
			white-space: -pre-wrap;      /* Opera 4-6 */
			white-space: -o-pre-wrap;    /* Opera 7 */
			word-wrap: break-word;       /* Internet Explorer 5.5+ */
		   }

		/* Center the loader */
		#loader {
		  position: absolute;
		  left: 50%;
		  top: 50%;
		  z-index: 1;
		  width: 150px;
		  height: 150px;
		  margin: -75px 0 0 -75px;
		  border: 16px solid #f3f3f3;
		  border-radius: 50%;
		  border-top: 16px solid #3498db;
		  width: 120px;
		  height: 120px;
		  -webkit-animation: spin 2s linear infinite;
		  animation: spin 2s linear infinite;
		}

		@-webkit-keyframes spin {
		  0% { -webkit-transform: rotate(0deg); }
		  100% { -webkit-transform: rotate(360deg); }
		}

		@keyframes spin {
		  0% { transform: rotate(0deg); }
		  100% { transform: rotate(360deg); }
		}

Arquivo PHP
-----------

.. code-block:: PHP

		<?php
        /**
         *
         * O presente código tem por objetivo exemplificar de forma minimalista o consumo dos serviços utilizados pelo Gov.br.
         *
         */

        use \Firebase\JWT\JWT;

        $URL_PROVIDER="https://sso.staging.acesso.gov.br";
        $CLIENT_ID = "<coloque-aqui-o-clientid-cadastrado-para-o-seu-sistema>";
        $SECRET = "<coloque-aqui-o-secret-cadastrado-para-o-seu-sistema>";
        $REDIRECT_URI = "<coloque-aqui-a-uri>";
        $SCOPE = "openid+email+phone+profile+govbr_empresa";
        $URL_SERVICOS="https://api.staging.acesso.gov.br";

        /*
         *  Etapa 1: No Browser, chamar a URL do Authorize para recuperar o code e o state (opcional) conforme o exemplo abaixo:
         *              https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=<coloque-aqui-o-client-id>&scope=openid+profile+phone+email+govbr_empresa&redirect_uri=<coloque-aqui-a-uri-de-redirecionamento>&nonce=<coloque-aqui-um-numero-aleatorio>&state=<coloque-aqui-um-numero-aleatorio>
         *              Descrição dos parametros:
         *                      response_type: Sempre "code";
         *                      client_id:     Identificador do sistema que usa o Gov.br. Este identificador é único para cada sistema;
         *                      scope:         Lista de escopos requisitados pelo sistema. Escopos são agrupamentos de informações cujo acesso deverá
         *                                         ser autorizado pelo cidadão que acessa o sistema. Cada sistema deverá informar que conjunto de informações (escopos) deseja;
         *          redirect_uri:  Uri para qual será feito o redirect após o login do cidadão (usuário). Para Celulares, usamos uma pseudo URI;
         *          nonce: número aleatório;
         *          state: número aleatório (opcional)
         *
         *              Observação: Sem o escopo "govbr_empresa", não será possível utilizar o serviço de recuperação de informações de empresas.
         */

        $uri = $URL_PROVIDER . "/authorize?response_type=code"
        . "&client_id=". $CLIENT_ID
        . "&scope=" . $SCOPE
        . "&redirect_uri=" . urlencode($REDIRECT_URI)
        . "&nonce=" . getRandomHex()
        . "&state=" . getRandomHex();

        function getRandomHex($num_bytes=4) {
                return bin2hex(openssl_random_pseudo_bytes($num_bytes));
        }

        /*
                Etapa 2: De posse do code retornado pelo passo 1, chame o serviço para recuperar os tokens disponíveis para sua aplicação
                                 (Access Token, Id Token) conforme o exemplo abaixo.
        */
        $CODE = $_REQUEST["code"];
        $STATE = $_REQUEST["state"];

        if (isset($CODE)) {

                $campos = array(
                                'grant_type' => urlencode('authorization_code'),
                                'code' => urlencode($CODE),
                                'redirect_uri' => urlencode($REDIRECT_URI)
                                );
                foreach($campos as $key=>$value) {
                                $fields_string .= $key.'='.$value.'&';
                }
                rtrim($fields_string, '&');
                $ch_token = curl_init();
                curl_setopt($ch_token, CURLOPT_URL, $URL_PROVIDER . "/token" );
                curl_setopt($ch_token, CURLOPT_POST, count($fields));
                curl_setopt($ch_token, CURLOPT_POSTFIELDS, $fields_string);
                curl_setopt($ch_token, CURLOPT_RETURNTRANSFER, TRUE);
                curl_setopt($ch_token, CURLOPT_SSL_VERIFYPEER, true);
                $headers = array(
                                'Content-Type:application/x-www-form-urlencoded',
                                'Authorization: Basic '. base64_encode($CLIENT_ID.":".$SECRET)
                );
                curl_setopt($ch_token, CURLOPT_HTTPHEADER, $headers);
                $json_output_tokens = json_decode(curl_exec($ch_token), true);
                curl_close($ch_token);

                /**
                 * Etapa 3: De posse do access token, podemos extrair algumas informações acerca do usuário. Aproveitamos também para checar a assinatura e tempo de expiração do token.
                 *          Para isso, este exemplo usa a biblioteca chamada "firebase/php-jwt" mas qualquer outra biblioteca que implemente a especificação pode ser usada.
                 *
                 *          O Access Token fornece as seguintes informações acerca do usuário:
                 *                              1- id client da aplicação à qual o usuário se autenticou;
                 *                              2- Escopos requeridos pela aplicação autorizados pelo usuário;
                 *                              3- CPF do usuário autenticado
                 *                              4- Nome completo do usuário cadastrado no Gov.br. Atenção, este é o nome que foi fornecido pelo usuário no momento do seu cadastro
                 *                 (ou obtido do Certificado Digital e-CPF caso o cadastro tenha sido feito por este meio).
                 */
                $url = $URL_PROVIDER . "/jwk" ;
                $ch_jwk = curl_init();
                curl_setopt($ch_jwk,CURLOPT_SSL_VERIFYPEER, true);
                curl_setopt($ch_jwk,CURLOPT_URL, $url);
                curl_setopt($ch_jwk, CURLOPT_RETURNTRANSFER, TRUE);
                $json_output_jwk = json_decode(curl_exec($ch_jwk), true);
                curl_close($ch_jwk);

                $access_token = $json_output_tokens['access_token'];

                try{
                        $json_output_payload_access_token = processToClaims($access_token, $json_output_jwk);
                } catch (Exception $e) {
                        $detalhamentoErro = $e;
                }


                /**
                 * Etapa 4: De posse do id token, podemos extrair algumas informações acerca do usuário. Aproveitamos também para checar a assinatura e tempo de expiração do token.
                 *          Para isso, este exemplo usa a biblioteca chamada "firebase/php-jwt" mas qualquer outra biblioteca que implemente a especificação pode ser usada.
                 *
                 *          O Id Token fornece as seguintes informações acerca do usuário:
                 *              1- id client da aplicação à qual o usuário se autenticou;
                 *              2- Escopos requeridos pela aplicação autorizados pelo usuário;
                 *              3- CPF do usuário autenticado
                 *              4- Nome completo do usuário cadastrado no Gov.br. Atenção, este é o nome que foi fornecido pelo usuário no momento do seu cadastro ou obtido do Certificado Digital e-CPF caso o cadastro tenha sido feito por este meio
				 *				5- Número do telefone está valido ou não no cadastro.
				 *				6- Número do telefone.
				 *				7- Endereço de email está valido ou não no cadastro.
				 *				8- Endereço de email.
                 *              9- Método de autenticação (CPF e Senha ou Certificado Digital)
                 *				10- CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica.
                 */
                $id_token = $json_output_tokens['id_token'];

                try{
                    $json_output_payload_id_token = processToClaims($id_token, $json_output_jwk);
                } catch (Exception $e) {
                    $detalhamentoErro = $e;
                }



                /*
                        Serviço de obtenção da foto do usuário: De posse do id token e access token, a aplicação pode chamar o serviço para obter a foto do perfil através da url informada no parâmetro picture no id token
                */
                $url = $json_output_payload_id_token['picture'];
                $ch_user_picture = curl_init();
                curl_setopt($ch_user_picture, CURLOPT_SSL_VERIFYPEER, true);
                curl_setopt($ch_user_picture, CURLOPT_URL, $url);
                curl_setopt($ch_user_picture, CURLOPT_RETURNTRANSFER, TRUE);
                $headers = array(
                        'Authorization: Bearer '. $access_token
                );
                curl_setopt($ch_user_picture, CURLOPT_HTTPHEADER, $headers);
                curl_setopt($ch_user_picture, CURLOPT_VERBOSE, true);
                curl_setopt($ch_user_picture, CURLOPT_FAILONERROR, true);
                $json_output_user_picture = curl_exec($ch_user_picture);
                if (curl_error($ch_user_picture)) {
                        $msg_error = curl_error($ch_user_picture);
                }
                curl_close($ch_user_picture);

                /*
                        Serviço de obtenção de selos de Confiabilidade: De posse do access token, a aplicação pode chamar o serviço para saber quais selos o usuário logado possui.
                */
                $url = $URL_SERVICOS . "/api/info/usuario/selo";
                $ch_confiabilidade = curl_init();
                curl_setopt($ch_confiabilidade, CURLOPT_SSL_VERIFYPEER, true);
                curl_setopt($ch_confiabilidade, CURLOPT_URL, $url);
                curl_setopt($ch_confiabilidade, CURLOPT_RETURNTRANSFER, TRUE);
                $headers = array(
                                'Accept: application/json',
                                'Authorization: Bearer '. $access_token
                );
                curl_setopt($ch_confiabilidade, CURLOPT_HTTPHEADER, $headers);
                $json_output_confiabilidade = json_decode(curl_exec($ch_confiabilidade), true);
                curl_close($ch_confiabilidade);

                /*
                        Verificar se CPF autenticado possui selo de Confiabildidade e-CNPJ.
                */
                if ($json_output_confiabilidade['nivel'] == '11') {
                        /*
                                Serviço de recuperação de empresas vinculadas: De posse do access token, a aplicação pode chamar o serviço para saber quais empresas se encontram vinculadas ao usuário logado.
                        */
                        $ch_empresas_vinculadas = curl_init();
						$cpf = $json_output_payload_id_token['sub'];
                        curl_setopt($ch_empresas_vinculadas, CURLOPT_SSL_VERIFYPEER, true);
						curl_setopt($ch_empresas_vinculadas, CURLOPT_URL, $URL_SERVICOS . "/empresas/v1/representantes/" . $cpf . "/empresas?visao=simples");
                        curl_setopt($ch_empresas_vinculadas, CURLOPT_RETURNTRANSFER, TRUE);
                        $headers = array(
                                        'Accept: application/json',
                                        'Authorization: Bearer '. $json_output_tokens['access_token']
                        );
                        curl_setopt($ch_empresas_vinculadas, CURLOPT_HTTPHEADER, $headers);
                        $json_output_empresas_vinculadas = json_decode(curl_exec($ch_empresas_vinculadas), true);
                        curl_close($ch_empresas_vinculadas);

                        /*
                                Serviço de detalhamento da empresa vinculada: De posse do access token, a aplicação pode chamar o serviço para obter dados de uma empresa específica e o papel do usuário logado nesta empresa.
                        */
                        $cnpj = $json_output_empresas_vinculadas[0];
                        $ch_papel_empresa = curl_init();
                        curl_setopt($ch_papel_empresa,CURLOPT_SSL_VERIFYPEER, true);
						curl_setopt($ch_papel_empresa,CURLOPT_URL, $URL_SERVICOS . "/empresas/v1/representantes/" . $cpf . "/empresas/" . $cnpj);
                        curl_setopt($ch_papel_empresa, CURLOPT_RETURNTRANSFER, TRUE);
                        $headers = array(
                                        'Accept: application/json',
                                        'Authorization: '. $json_output_tokens['access_token']
                        );
                        curl_setopt($ch_papel_empresa, CURLOPT_HTTPHEADER, $headers);
                        $json_output_papel_empresa = json_decode(curl_exec($ch_papel_empresa), true);
                        curl_close($ch_papel_empresa);
                }
        }
        /**
         * Função que valida o token (access_token ou id_token) (Valida o tempo de expiração e a assinatura)
         *
         */
        function processToClaims($token, $jwk)
        {
                $modulus = JWT::urlsafeB64Decode($jwk['keys'][0]['n']);
                $publicExponent = JWT::urlsafeB64Decode($jwk['keys'][0]['e']);
                $components = array(
                        'modulus' => pack('Ca*a*', 2, encodeLength(strlen($modulus)), $modulus),
                        'publicExponent' => pack('Ca*a*', 2, encodeLength(strlen($publicExponent)), $publicExponent)
                );
                $RSAPublicKey = pack(
                        'Ca*a*a*',
                        48,
                        encodeLength(strlen($components['modulus']) + strlen($components['publicExponent'])),
                        $components['modulus'],
                        $components['publicExponent']
                );
                $rsaOID = pack('H*', '300d06092a864886f70d0101010500'); // hex version of MA0GCSqGSIb3DQEBAQUA
                $RSAPublicKey = chr(0) . $RSAPublicKey;
                $RSAPublicKey = chr(3) . encodeLength(strlen($RSAPublicKey)) . $RSAPublicKey;
                $RSAPublicKey = pack(
                        'Ca*a*',
                        48,
                        encodeLength(strlen($rsaOID . $RSAPublicKey)),
                        $rsaOID . $RSAPublicKey
                );
                $RSAPublicKey = "-----BEGIN PUBLIC KEY-----\r\n" . chunk_split(base64_encode($RSAPublicKey), 64) . '-----END PUBLIC KEY-----';

                JWT::$leeway = 3 * 60; //em segundos

                $decoded = JWT::decode($token, $RSAPublicKey, array('RS256'));

                return (array) $decoded;
        }

        function encodeLength($length)
        {
                if ($length <= 0x7F) {
                        return chr($length);
                }
                $temp = ltrim(pack('N', $length), chr(0));
                return pack('Ca*', 0x80 | strlen($temp), $temp);
        }

	?>

	<!DOCTYPE html>
	<html lang="en">
	<head>
			<meta charset="UTF-8">
			<meta name="viewport" content="width=device-width, initial-scale=1.0">
			<meta http-equiv="X-UA-Compatible" content="ie=edge">
			<title>STI Gov.br</title>
			<link rel="stylesheet" type="text/css" href="css/sti.css">
			<script>
					function waiting() {
							document.getElementById("loader").style.display = "block";
					}
			</script>
	</head>
	<body>
			<div class="header">
					<h1>STI Gov.br</h1>
					<p><b>S</b>ite de <b>T</b>este <b>I</b>ntegrado ao Gov.br</p>
			</div>

			<div class="navbar">
					<?php
							if (isset($json_output_payload_access_token)) {
									echo '<a href="#" class="right">Logout</a>';
							} else {
									echo '<a href="' . $uri .'" onClick="waiting();" class="right">Logar com o Gov.br</a>';
							}
					?>
			</div>

			<div id="loader" style="display:none"></div>

			<div class="row">
					<div class="left_side">
							<div>
									<h3>Etapa 1 (obrigatório): Autenticação</h3>
									<p>Ao clicar no botão "Logar com o Gov.br" a seguinte URL será chamada:</p>
							</div>
					</div>
					<div class="right_side">
							<h3>URL do Serviço Authorize:</h3>
							<div class="result" style="height:200px;">
									<pre><?php echo $uri ?></pre>
							</div>
					</div>
			</div>

			<?php
					if (isset($json_output_tokens)) {
			?>
					<div class="row">
							<div class="left_side">
									<div>
											<h3>Etapa 2 (obrigatório): Recuperar os Tokens</h3>
											<p>De posse do code retornado pelo passo 1, chame o serviço para recuperar os tokens disponívels para sua aplicação
											   (Access Token, Id Token):</p>
									</div>
							</div>
							<div class="right_side">
									<h3>Json:</h3>
									<div class="result" style="width:900px;">
											<pre><?php echo json_encode($json_output_tokens, JSON_PRETTY_PRINT); ?></pre>
									</div>
							</div>
					</div>

					<div class="row">
							<div class="left_side">
									<div>
											<h3>Etapa 3 (desejável): Validação do Access Token</h3>
											<p>De posse do access token, podemos extrair algumas informações acerca do usuário. Aproveitamos também para checar a assinatura e tempo de expiração do token:</p>
									</div>
							</div>
							<div class="right_side">
									<?php
											if (isset($json_output_payload_access_token)) {
									?>
											<h3>Json:</h3>
											<div class="result" style="width:900px;">
													<pre><?php echo json_encode($json_output_payload_access_token, JSON_PRETTY_PRINT); ?></pre>
											</div>
											<div id="result-access_token" class="resultValido" style="width:900px;">
													<pre><b>Access Token VALIDO</b></pre>
											</div>
									<?php
											} else {
									?>
											<h3>Access Token:</h3>
											<div class="result" style="width:900px;">
													<pre><?php echo $access_token; ?></pre>
											</div>
											<div id="result-access_token" class="resultInvalido" style="width:900px;">
													<pre><b>Access Token INVÁLIDO</b></pre>
											</div>
											<div class="result" style="width:900px;">
													<pre>Detalhamento: <?php echo $detalhamentoErro; ?></pre>
											</div>

									<?php
											}
									?>
							</div>
					</div>
					<div class="row">
							<div class="left_side">
									<div>
											<h3>Etapa 3 (desejável): Validação do Id Token</h3>
											<p>De posse do id token, podemos extrair algumas informações acerca do usuário. Aproveitamos também para checar a assinatura e tempo de expiração do token:</p>
									</div>
							</div>
							<div class="right_side">
									<?php
											if (isset($json_output_payload_id_token)) {
									?>
											<h3>Json:</h3>
											<div class="result" style="width:900px;">
												<pre><?php echo json_encode($json_output_payload_id_token, JSON_PRETTY_PRINT); ?></pre>
											</div>
											<div id="result-access_token" class="resultValido" style="width:900px;">
												<pre><b>Id Token VALIDO</b></pre>
											</div>
									<?php
											} else {
									?>
											<h3>Id Token:</h3>
											<div class="result" style="width:900px;">
													<pre><?php echo $id_token; ?></pre>
											</div>
											<div id="result-id_token" class="resultInvalido" style="width:900px;">
													<pre><b>Id Token INVÁLIDO</b></pre>
											</div>
											<div class="result" style="width:900px;">
													<pre>Detalhamento: <?php echo $detalhamentoErro; ?></pre>
											</div>

									<?php
											}
									?>
							</div>
					</div>
			<?php
					}
					if (isset($json_output_payload_access_token) or isset($json_output_payload_id_token)) {
			?>
					<div class="row">
							<div class="left_side">
									<div>
											<h3>Serviço: Recuperar Informações do Usuário</h3>
											<p>De posse do id token, a aplicação pode obter as informações do usuário a partir das informações do próprio id token:</p>
									</div>
							</div>
							<div class="right_side">
									<h3>Json:</h3>
									<div class="result" style="width:900px;">
											<pre>CPF: <?php echo $json_output_payload_id_token['sub']; ?></pre> <!-- CPF do usuário autenticado. -->
											<pre>Nome: <?php echo $json_output_payload_id_token['name']; ?></pre> <!-- Nome Completo do cadastro feito pelo usuário no Gov.br. -->
											<pre>Telefone Validado: <?php echo $json_output_payload_id_token['phone_number_verified']; ?></pre> <!-- (Confirma se o telefone foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")-->
											<pre>Telefone: <?php echo $json_output_payload_id_token['phone_number']; ?></pre> <!-- (Número de telefone cadastrado no Gov.br do usuário autenticado. Caso o atributo phone_number_verified do ID_TOKEN tiver o valor false, o atributo phone_number não virá no ID_TOKEN)-->
											<pre>Email Validado: <?php echo $json_output_payload_id_token['email_verified']; ?></pre> <!-- (Confirma se o email foi validado no cadastro do Gov.br. Poderá ter o valor "true" ou "false")-->
											<pre>Email: <?php echo $json_output_payload_id_token['email']; ?></pre> <!-- (Endereço de e-mail cadastrado no Gov.br do usuário autenticado. Caso o atributo email_verified do ID_TOKEN tiver o valor false, o atributo email não virá no ID_TOKEN)-->
											<pre>AMR: <?php echo $json_output_payload_id_token['amr']; ?></pre> <!--  Fator de autenticação do usuário. Pode ser “passwd” se o mesmo logou fornecendo a senha, ou “x509” se o mesmo utilizou certificado digital ou certificado em nuvem. -->
											<pre>CNPJ: <?php echo $json_output_payload_id_token['cnpj']; ?></pre> <!-- CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica. -->
									</div>
							</div>
					</div>

					<div class="row">
							<div class="left_side">
									<div>
											<h3>Serviço: Recuperar Foto do Usuário</h3>
											<p>De posse do access token, a aplicação pode chamar o serviço de recuperação da foto do usuário:</p>
									</div>
							</div>
							<div class="right_side">
									<h3>Json:</h3>
									<div class="result" style="width:900px;">
											<img src="data:image/png;base64, <?php echo base64_encode($json_output_user_picture); ?>" alt="">
									</div>
							</div>
					</div>

					<div class="row">
							<div class="left_side">
									<div>
											<h3>Serviço: Recuperar Selos do Usuário</h3>
											<p>De posse do access token, a aplicação pode chamar o serviço para saber quais selos o usuário logado possui:</p>
									</div>
							</div>
							<div class="right_side">
									<h3>Json:</h3>
									<div class="result" style="width:900px;">
											<pre><?php echo json_encode($json_output_confiabilidade, JSON_PRETTY_PRINT); ?></pre>
									</div>
							</div>
					</div>

					<?php
							if ($json_output_confiabilidade['nivel'] == '11') {
					?>
							<div class="row">
									<div class="left_side">
											<div>
													<h3>Serviço: Recuperar Vinculos com empresas</h3>
													<p>De posse do access token, a aplicação pode chamar o serviço para saber quais empresas se encontram vinculadas ao usuário logado:</p>
											</div>
									</div>
									<div class="right_side">
											<h3>Json:</h3>
											<div class="result" style="width:900px;">
													<pre><?php echo json_encode($json_output_empresas_vinculadas, JSON_PRETTY_PRINT); ?></pre>
											</div>
									</div>
							</div>

							<div class="row">
									<div class="left_side">
											<div>
													<h3>Serviço: Recuperar Dados de Empresa</h3>
													<p>De posse do access token, a aplicação pode chamar o serviço para obter dados de uma empresa específica e o papel do usuário logado nesta empresa:</p>
											</div>
									</div>
									<div class="right_side">
											<h3>Json:</h3>
											<?php
													if (empty($json_output_empresas_vinculadas['cnpjs'])) {
															echo '<div class="result" style="width:900px;"><pre>Não há empresas a detalhar.</pre></div>';
													}
													foreach ($json_output_empresas_vinculadas['cnpjs'] as $empresa) {
															echo '<div class="result" style="width:900px;"><pre>' . json_encode($json_output_papel_empresa, JSON_PRETTY_PRINT) . '</pre></div>';
													}
											?>
									</div>
							</div>
					<?php
							}
					?>


			<?php
					}
			?>
	</body>
	</html>

ANDROID (MOBILE)
++++++++++++++++

`Exemplo para download`_

**Observações para executar o exemplo**
------------------------------------------

1. Não poderá utilizar meio **WEBVIEW**, presente de forma nativa no desenvolvimento **mobile**.Utilizar o componente de chamada para browser do celular.

2. Alterar configurações da tag "data" do AndroidManifest.xml da pasta /OauthMobileApp:

- android:scheme="coloque-aqui-o-esquema-da-sua-uri-de-retorno"
- android:host="coloque-aqui-o-host-da-sua-uri-de-retorno"
- android:path="coloque-aqui-o-path-da-sua-uri-de-retorno"

**Exemplo:** URL de Retorno ("local://exemplo.com/callback"), o scheme sera ("local"), o host será ("exemplo.com") e o path será (/callback)

3. Instalar as bibliotecas:

- `RETROFIT`_ |site externo|
- `OKHTTP`_ |site externo|
- `jose4j`_ |site externo|

4. Alterar as configurações no arquivo Config.java da pasta /OauthMobileApp:

- CLIENT_ID("coloque-aqui-o-client-id-da-sua-aplicação")
- REDIRECT_URI("coloque-aqui-a-url-retorno-liberada")
- AUTHORIZATION_SCOPE("openid profile phone email govbr_empresa")
- AUTHORIZATION_ENDPOINT_URI("https://sso.staging.acesso.gov.br/authorize")
- LOGOUT_ENDPOINT_URI("https://sso.staging.acesso.gov.br/logout")
- TOKEN_ENDPOINT_URI("endereco-backend-para-acessar-conteudo-da-pasta-OauthMobileBackend")

5. Cadastrar variáveis de ambiente do serviço backend da pasta /OauthMobileBackend

- CLIENT_ID="coloque-aqui-o-client-id-da-sua-aplicação"
- TOKEN_SERVICE_URL="https://sso.staging.acesso.gov.br/token"
- REDIRECT_URI="coloque-aqui-o-redirect-uri-identico-ao-informado-nomanifest-do-app-android"
- ISSUER="https://seu-domínio"
- CREDENTIALS="coloque-aqui-as-credenciais-co"
- JWK_RSA_WEB_KEY="coloque-aqui-o-par-de-chaves-que-assinarão-o-token-desessao-gerado-por-algoritmo-RSA-tendo-padrao-jwk"
- JWK_SERVICE_URL="https://sso.staging.acesso.gov.br/jwk"
- SERVICE_URL="ttps://api.staging.acesso.gov.br"

.. |site externo| image:: _images/site-ext.gif
.. _`jose4j` : https://javalibs.com/artifact/org.bitbucket.b_c/jose4j
.. _`firebase/php-jwt`: https://github.com/firebase/php-jwt
.. _`RETROFIT`: https://square.github.io/retrofit/
.. _`OKHTTP`: https://square.github.io/okhttp/
.. _`Exemplo para download`: arquivos/android-oauth-sdk-master.zip

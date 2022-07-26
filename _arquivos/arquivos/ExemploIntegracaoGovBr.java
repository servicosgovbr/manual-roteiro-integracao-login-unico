import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
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
	 * getFoto - Serviço que recupera a foto do usuário; Serviço 2: getNiveis -
	 * Serviço que recupera os niveis do cidadão Serviço 3: getCategorias - Serviço
	 * que recupera as categorias do cidadão Serviço 4: getConfiabilidade - Serviço
	 * que recupera os selos de confiabilidade atribuidos ao usuário; Serviço 5:
	 * getEmpresasVinculadas - Serviço que recupera a lista de empresas vinculadas
	 * ao usuário; Serviço 5: getDadosEmpresa - Serviço que detalha a empresa e o
	 * papel do usuário nesta empresa. Código termina com chamada do Catálogo de
	 * Confiabildides.
	 *
	 * *************************************************************************************************
	 *
	 * Informações de uso ------------------ Atribua às variáveis abaixo os valores
	 * de acordo com o seu sistema.
	 *
	 */

	private static final String URL_PROVIDER = "https://sso.staging.acesso.gov.br";
	private static final String URL_SERVICOS = "https://api.staging.acesso.gov.br";
	private static final String URL_CATALOGO_SELOS = "https://confiabilidades.staging.acesso.gov.br";
	private static final String REDIRECT_URI = "<coloque-aqui-url-de-retorno>"; // redirectURI informada na chamada do serviço do
																		// authorize.
	private static final String SCOPES = "openid+email+profile+govbr_empresa+govbr_confiabilidades"; // Escopos
																												// pedidos
																												// para
																												// a
																												// aplicação.
	private static final String CLIENT_ID = "<coloque-aqui-o-client-id>"; // clientId
																										// informado na
																										// chamada do
																										// serviço do
																										// authorize.
	private static final String SECRET = "<coloque-aqui-o-secret>"; // secret de
																									// conhecimento
																									// apenas do backend
																									// da aplicação.
	
	private static final String CODE_CHALLENGE = "<coloque-aqui-secredo-ser-gerado-pelo-cliente-respeitando-o-passo-3-roteiro-integracao-variavel-code-challenge>";
 	private static final String CODE_CHALLENGE_METHOD = "S256";
	private static final String CODE_VERIFIER = "<coloque-aqui-secredo-ser-gerado-pelo-cliente-respeitando-o-passo-6-roteiro-integracao-variavel-code-verifier>";
	private static final String NIVEIS = "<coloque-aqui-os-niveis-repeitando-sintaxe-virgula-barra-parenteses-segundo-roteiro>";
	private static final String CONFIABILIDADES = "<coloque-aqui-as-confiabilidades-repeitando-sintaxe-virgula-barra-parenteses-segundo-roteiro>";

	public static void main(String[] args) throws Exception {

		/**
		 * Etapa 1: No Browser, chamar a URL do Authorize para recuperar o code e o
		 * state (opcional) conforme o exemplo abaixo:
		 * https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=<coloque-aqui-o-client-id>&scope=openid+profile+(phone/email)+govbr_empresa&redirect_uri=<coloque-aqui-a-uri-de-redirecionamento>&nonce=<coloque-aqui-um-numero-aleatorio>&state=<coloque-aqui-um-numero-aleatorio>
		 * Descrição dos parametros: response_type: Sempre "code"; client_id:
		 * Identificador do sistema que usa o Gov.br. Este identificador é único para
		 * cada sistema; scope: Lista de escopos requisitados pelo sistema. Escopos são
		 * agrupamentos de informações cujo acesso deverá ser autorizado pelo cidadão
		 * que acessa o sistema. Cada sistema deverá informar que conjunto de
		 * informações (escopos) deseja; redirect_uri: Uri para qual será feito o
		 * redirect após o login do cidadão (usuário). Para Celulares, usamos uma pseudo
		 * URI; nonce: número aleatório; state: número aleatório (opcional)
		 *
		 * Observação: Sem o escopo "govbr_empresa", não será possível utilizar o
		 * serviço de recuperação de informações de empresas.
		 */

		System.out.println("--------------------Etapa 1 - URL do Serviço Authorize------------------");
		System.out.println("Abra um Browser (Chrome ou Firefox), aperte F12. Clique na aba 'Network'.");
		System.out.println(
				"Cole a URL abaixo no Browser (Chrome ou Firefox) e entre com um usuário cadastrado no Gov.br");
		System.out.println(URL_PROVIDER + "/authorize?response_type=code&client_id=" + CLIENT_ID + "&scope=" + SCOPES
				+ "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") + "&nonce=" + createRandomNumber()
				+ "&state=" + createRandomNumber() + "&code_challenge=" + CODE_CHALLENGE
				+ "&code_challenge_method="+CODE_CHALLENGE_METHOD);

		/**
		 * Etapa 2: De posse do code retornado pelo passo 1, chame o serviço para
		 * recuperar os tokens disponíveis para sua aplicação (Access Token, Id Token)
		 * conforme o exemplo abaixo.
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
		String nomeCompleto = idTokenJwtClaims.getStringClaimValue("name"); // Nome Completo do cadastro feito pelo
																			// usuário no Gov.br.
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
		System.out.println("CPF: " + idTokenJwtClaims.getSubject()); // CPF do usuário autenticado.
		System.out.println("Nome: " + nomeCompleto); // Nome Completo do cadastro feito pelo usuário no Gov.br.
		System.out.println("Email Validado: " + idTokenJwtClaims.getClaimValue("email_verified")); // (Confirma se o
																									// email foi
																									// validado no
																									// cadastro do
																									// Gov.br. Poderá
																									// ter o valor
																									// "true" ou
																									// "false")
		System.out.println("E-Mail: " + idTokenJwtClaims.getClaimValue("email")); // (Endereço de e-mail cadastrado no
																					// Gov.br do usuário autenticado.
																					// Caso o atributo email_verified do
																					// ID_TOKEN tiver o valor false, o
																					// atributo email não virá no
																					// ID_TOKEN)
		System.out.println("Telefone Validado: " + idTokenJwtClaims.getClaimValue("phone_number_verified")); // (Confirma
																												// se o
																												// telefone
																												// foi
																												// validado
																												// no
																												// cadastro
																												// do
																												// Gov.br.
																												// Poderá
																												// ter o
																												// valor
																												// "true"
																												// ou
																												// "false")
		System.out.println("Telefone: " + idTokenJwtClaims.getClaimValue("phone_number")); // (Número de telefone
																							// cadastrado no Gov.br do
																							// usuário autenticado. Caso
																							// o atributo
																							// phone_number_verified do
																							// ID_TOKEN tiver o valor
																							// false, o atributo
																							// phone_number não virá no
																							// ID_TOKEN)
		System.out.println("Link para a foto: " + fotoUrl); // URL de acesso à foto do usuário cadastrada no Gov.br. A
															// mesma é protegida e pode ser acessada passando o access
															// token recebido.
		System.out.println("CNPJ: " + idTokenJwtClaims.getClaimValue("cnpj")); // CNPJ vinculado ao usuário autenticado.
																				// Atributo será preenchido quando
																				// autenticação ocorrer por certificado
																				// digital de pessoal jurídica.
		System.out.println("Nome Empresa CNPJ " + idTokenJwtClaims.getClaimValue("cnpj_certificate_name")); // Nome da
																											// empresa
																											// vinculada
																											// ao
																											// usuário
																											// autenticado.
																											// Atributo
																											// será
																											// preenchido
																											// quando
																											// autenticação
																											// ocorrer
																											// por
																											// certificado
																											// digital
																											// de
																											// pessoal
																											// jurídica.

		List<String> listaAMR = accessTokenJwtClaims.getStringListClaimValue("amr");

		System.out.println("\n\nDados da Autenticação:");
		System.out.println("Amr: " + String.join(",", listaAMR)); // Lista com os fatores de autenticação do usuário.
																	// Pode ser “passwd” se o mesmo logou fornecendo a
																	// senha, ou “x509” se o mesmo utilizou certificado
																	// digital ou certificado em nuvem.

		/**
		 * Serviço 1: De posse do access token, a aplicação pode chamar o serviço para
		 * receber a foto do usuário.
		 */

		String resultadoFoto = getFoto(fotoUrl, accessToken);

		System.out.println("\n--------------------Serviço 1 - Foto do usuário------------------");
		System.out.println("Foto retornada:");
		System.out.println(resultadoFoto);

		/**
		 * Serviço 2: De posse do access token, a aplicação pode chamar o serviço para
		 * saber quais níveis o usuário logado possui.
		 */

		String niveisJson = getNiveis(accessToken, idTokenJwtClaims.getSubject());

		System.out.println(
				"\n--------------------Serviço 2 - Informações acerca dos níveis do usuário------------------");
		System.out.println("JSON retornado:");
		System.out.println(niveisJson);

		/**
		 * Serviço 3: De posse do access token, a aplicação pode chamar o serviço para
		 * saber quais categorias o usuário logado possui.
		 */

		String categoriasJson = getCategorias(accessToken, idTokenJwtClaims.getSubject());

		System.out.println(
				"\n--------------------Serviço 2 - Informações acerca das categorias do usuário------------------");
		System.out.println("JSON retornado:");
		System.out.println(categoriasJson);

		/**
		 * Serviço 4: De posse do access token, a aplicação pode chamar o serviço para
		 * saber quais selos o usuário logado possui.
		 */

		String confiabilidadeJson = getConfiabilidade(accessToken, idTokenJwtClaims.getSubject());

		System.out.println(
				"\n--------------------Serviço 2 - Informações acerca da confiabilidade do usuário------------------");
		System.out.println("JSON retornado:");
		System.out.println(confiabilidadeJson);

		/**
		 * Serviço 5: De posse do access token, a aplicação pode chamar o serviço para
		 * saber quais empresas se encontram vinculadas ao usuário logado.
		 *
		 */

		String empresasJson = getEmpresasVinculadas(accessToken, idTokenJwtClaims.getSubject());

		System.out.println("\n--------------------Serviço 3 - Empresas vinculadas ao usuário------------------");
		System.out.println("JSON retornado:");
		System.out.println(empresasJson);

		/**
		 * Serviço 6: De posse do access token, a aplicação pode chamar o serviço para
		 * obter dados de uma empresa específica e o papel do usuário logado nesta
		 * empresa.
		 */


		JSONArray empresasVinculadasJson = (JSONArray) parser.parse(empresasJson);
			
		if (!empresasVinculadasJson.isEmpty()) {

			for (Object cnpjSeparado : empresasVinculadasJson) {
				
				JSONObject cnpj = (JSONObject) cnpjSeparado;
				
				String dadosEmpresaJson = getDadosEmpresa(accessToken, cnpj.get("cnpj").toString(),
						idTokenJwtClaims.getSubject());

				System.out.printf("\n--------------------Serviço 4 - Informações acerca da empresa %s------------------",
						cnpj.get("cnpj").toString());
				System.out.println("JSON retornado:");
				System.out.println(dadosEmpresaJson);
				
			}
		}
		
		System.out.println("--------------------Catalogo de Confiabildiades (Selos)------------------");
		System.out.println("Abra um Browser (Chrome ou Firefox), aperte F12. Clique na aba 'Network'.");
		System.out.println(
				"Cole a URL abaixo no Browser (Chrome ou Firefox) para verificar apresentação do catálogo de confiabilidades (selos).");
		System.out.println(URL_CATALOGO_SELOS + "/?client_id=" + CLIENT_ID + "&niveis=" + NIVEIS + "&confiabilidades=" + CONFIABILIDADES);

	}

	private static String extractToken(String code) throws Exception {
		String retorno = "";

		String redirectURIEncodedURL = URLEncoder.encode(REDIRECT_URI, "UTF-8");

		URL url = new URL(URL_PROVIDER + "/token?grant_type=authorization_code&code=" + code + "&redirect_uri="
				+ redirectURIEncodedURL + "&code_verifier="+CODE_VERIFIER);
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

		URL url = new URL(URL_SERVICOS + "/empresas/v2/empresas?filtrar-por-participante=" + cpf);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("authorization", "Bearer " + accessToken);

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

		URL url = new URL(URL_SERVICOS + "/empresas/v2/empresas/" + cnpj + "/participantes/" + cpf);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("authorization", "Bearer " + accessToken);

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
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

		if (conn.getResponseCode() != 200) {
			return "Foto nao encontrada: " + conn.getResponseCode();
		}

		String foto = null;
		try (InputStream inputStream = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			IOUtils.copy(inputStream, baos);
			String mimeType = conn.getHeaderField("Content-Type");
			foto = new String("data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(baos.toByteArray()));
		}

		conn.disconnect();

		return foto;
	}

	private static String getNiveis(String accessToken, String cpf) throws Exception {
		String retorno = "";

		URL url = new URL(URL_SERVICOS + "/confiabilidades/v3/contas/" + cpf + "/niveis?response-type=ids");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

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

	private static String getCategorias(String accessToken, String cpf) throws Exception {
		String retorno = "";

		URL url = new URL(URL_SERVICOS + "/confiabilidades/v3/contas/" + cpf + "/categorias?response-type=ids");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

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

	private static String getConfiabilidade(String accessToken, String cpf) throws Exception {
		String retorno = "";

		URL url = new URL(URL_SERVICOS + "/confiabilidades/v3/contas/" + cpf + "/confiabilidades?response-type=ids");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

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

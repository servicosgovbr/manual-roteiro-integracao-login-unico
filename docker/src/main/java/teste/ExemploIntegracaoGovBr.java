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
 * O presente codigo tem por objetivo exemplificar de forma minimalista o
 * consumo dos servicos utilizados pelo Gov.br.
 *
 */

public class ExemploIntegracaoGovBr {

        /**
         * O processo de autenticacao e autorizacao de recursos ocorre essencialmente em
         * tres etapas: Etapa 1: Chamada do servico de autorizacao do Gov.br; Etapa 2:
         * Recuperacao do Access Token e Etapa 3: Validacao do Access Token por meio da
         * verificacao de sua assinatura. Apos concluida essas tres etapas, a aplicacao
         * cliente tera as informacoes basicas para conceder acesso de acordo com suas
         * proprias politicas de autorizacao. Caso a aplicacao cliente necessite de
         * informacoes adicionais, fica habilitado o acesso a todos os servicos
         * (presentes e futuros) fornecidos pelo Gov.br por meio do access token. O
         * presente codigo exemplifica a chamada aos seguintes servicos: getUserInfo -
         * Extracao das informacoes basicas de usuario atraves do ID Token; Servico 1:
         * getFoto - Servico que recupera a foto do usuario; Servico 2:
         * getConfiabilidade - Servico que recupera os selos de confiabilidade
         * atribuidos ao usuario; Servico 3: getEmpresasVinculadas - Servico que recupera a lista de empresas vinculadas ao usuario;
         * Servico 4: getDadosEmpresa - Servico que detalha a empresa e o papel do usuario nesta
         * empresa.
         *
         *
         * *************************************************************************************************
         *
         * Informacoes de uso ------------------ Atribua as variaveis abaixo os valores
         * de acordo com o seu sistema.
         *
         */

        private static final String URL_PROVIDER = "https://sso.staging.acesso.gov.br";
        private static final String URL_SERVICOS = "https://api.staging.acesso.gov.br";
        private static final String REDIRECT_URI = "<coloque-aqui-a-uri>";                                                      //redirectURI informada na chamada do servico do authorize.
        private static final String SCOPES = "openid+email+phone+profile+govbr_empresa+govbr_confiabilidades";  // Escopos pedidos para a aplicacao.
        private static final String CLIENT_ID = "<coloque-aqui-o-clientid-cadastrado-para-o-seu-sistema>";                      //clientId informado na chamada do servico do authorize.
        private static final String SECRET = "<coloque-aqui-o-secret-cadastrado-para-o-seu-sistema>";                           //secret de conhecimento apenas do backend da aplicacao.

        public static void main(String[] args) throws Exception {

                /**
                 * Etapa 1: No Browser, chamar a URL do Authorize para recuperar o code e o
                 * state (opcional) conforme o exemplo abaixo:
                 * https://sso.staging.acesso.gov.br/authorize?response_type=code&client_id=<coloque-aqui-o-client-id>&scope=openid+profile+phone+email+govbr_empresa&redirect_uri=<coloque-aqui-a-uri-de-redirecionamento>&nonce=<coloque-aqui-um-numero-aleatorio>&state=<coloque-aqui-um-numero-aleatorio>
                 * Descricao dos parametros: response_type: Sempre "code"; client_id:
                 * Identificador do sistema que usa o Gov.br. Este identificador e unico para
                 * cada sistema; scope: Lista de escopos requisitados pelo sistema. Escopos sao
                 * agrupamentos de informacoes cujo acesso devera ser autorizado pelo cidadao
                 * que acessa o sistema. Cada sistema devera informar que conjunto de
                 * informacoes (escopos) deseja; redirect_uri: Uri para qual sera feito o
                 * redirect apos o login do cidadao (usuario). Para Celulares, usamos uma pseudo
                 * URI; nonce: numero aleatorio; state: numero aleatorio (opcional)
                 *
                 * Observacao: Sem o escopo "govbr_empresa", nao sera possivel utilizar
                 * o servico de recuperacao de informacoes de empresas.
                 */

                System.out.println("--------------------Etapa 1 - URL do Servico Authorize------------------");
                System.out.println("Abra um Browser (Chrome ou Firefox), aperte F12. Clique na aba 'Network'.");
                System.out.println(
                                "Cole a URL abaixo no Browser (Chrome ou Firefox) e entre com um usuario cadastrado no Gov.br");
                System.out.println(URL_PROVIDER + "/authorize?response_type=code&client_id=" + CLIENT_ID + "&scope="
                                + SCOPES + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") + "&nonce="
                                + createRandomNumber() + "&state=" + createRandomNumber());

                /**
                 * Etapa 2: De posse do code retornado pelo passo 1, chame o servico para
                 * recuperar os tokens disponiveis para sua aplicacao (Access Token, Id Token) conforme o exemplo abaixo.
                 */

                System.out.println("\n--------------------Etapa 2 - Recuperacao dos Tokens de Acesso------------------");
                System.out.println("Digite abaixo o parametro 'code' retornado pelo redirect da etapa 1");
                System.out.print("Digite o valor do parametro code retornado:");
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
                 * Etapa 3: De posse do access token, podemos extrair algumas informacoes acerca
                 * do usuario. Aproveitamos tambem para checar a assinatura e tempo de expiracao
                 * do token. Para isso, este exemplo usa a biblioteca Open Source chamada
                 * "jose4j" mas qualquer outra biblioteca que implemente a especificacao pode
                 * ser usada.
                 *
                 * O Access Token fornece as seguintes informacoes acerca do usuario: 1- id
                 * client da aplicacao a qual o usuario se autenticou; 2- Escopos requeridos
                 * pela aplicacao autorizados pelo usuario; 3- CPF do usuario autenticado 4-
                 * Nome completo do usuario cadastrado no Gov.br. Atencao, este e o nome que foi
                 * fornecido pelo usuario no momento do seu cadastro
                 *
                 */

                JwtClaims accessTokenJwtClaims;
                JwtClaims idTokenJwtClaims;
                try {
                        accessTokenJwtClaims = processToClaims(accessToken);
                        idTokenJwtClaims = processToClaims(idToken);
                } catch (Exception e) {
                        System.out.println("Access Token invalido!");
                        throw new Exception(e);
                }

                String idClient = accessTokenJwtClaims.getAudience().get(0); // Client Id
                List<String> scopes = accessTokenJwtClaims.getStringListClaimValue("scope"); // Escopos autorizados pelo usuario
                String nomeCompleto = idTokenJwtClaims.getStringClaimValue("name"); // Nome Completo do cadastro feito pelo usuario no Gov.br.
                String fotoUrl = idTokenJwtClaims.getStringClaimValue("picture"); //

                System.out.println("\n--------------------Etapa 3 - Informacoes obtidas do Access Token------------------");
                System.out.printf("O usuario " + nomeCompleto + " foi autenticado pelo Gov.br para usar o sistema " + idClient
                                + ". Este usuario tambem autorizou este mesmo sistema a utilizar as informacoes representadas pelos escopos "
                                + String.join(",", scopes) + ". \n");

                /**
                 * De posse do token de resposta, a aplicacao pode usar o id token para extrair
                 * as informacoes do usuario.
                 *
                 */

                System.out
                                .println("\n--------------------Informacoes do usuario obtidas atraves do ID Token------------------");
                System.out.println("JSON retornado (base 64):");
                System.out.println(idToken);
                System.out.println("\n\nDados do usuario:");
                System.out.println("CPF: " + idTokenJwtClaims.getSubject()); //CPF do usuario autenticado.
                System.out.println("Nome: " + nomeCompleto); // Nome Completo do cadastro feito pelo usuario no Gov.br.
                System.out.println("Email Validado: " + idTokenJwtClaims.getClaimValue("email_verified")); //(Confirma se o email foi validado no cadastro do Gov.br. Podera ter o valor "true" ou "false")
                System.out.println("E-Mail: " + idTokenJwtClaims.getClaimValue("email")); //(Endereco de e-mail cadastrado no Gov.br do usuario autenticado. Caso o atributo email_verified do ID_TOKEN tiver o valor false, o atributo email nao vira no ID_TOKEN)
                System.out.println("Telefone Validado: " + idTokenJwtClaims.getClaimValue("phone_number_verified")); //(Confirma se o telefone foi validado no cadastro do Gov.br. Podera ter o valor "true" ou "false")
                System.out.println("Telefone: " + idTokenJwtClaims.getClaimValue("phone_number")); //(Numero de telefone cadastrado no Gov.br do usuario autenticado. Caso o atributo phone_number_verified do ID_TOKEN tiver o valor false, o atributo phone_number nao vira no ID_TOKEN)
                System.out.println("Link para a foto: " + fotoUrl); //URL de acesso a foto do usuario cadastrada no Gov.br. A mesma e protegida e pode ser acessada passando o access token recebido.
                System.out.println("CNPJ: " + idTokenJwtClaims.getClaimValue("cnpj")); // CNPJ vinculado ao usuario autenticado. Atributo sera preenchido quando autenticacao ocorrer por certificado digital de pessoal juridica.
                System.out.println("Nome Empresa CNPJ " + idTokenJwtClaims.getClaimValue("cnpj_certificate_name")); //Nome da empresa vinculada ao usuario autenticado. Atributo sera preenchido quando autenticacao ocorrer por certificado digital de pessoal juridica.

                List<String> listaAMR = accessTokenJwtClaims.getStringListClaimValue("amr");

                System.out.println("\n\nDados da Autenticacao:");
                System.out.println("Amr: " + String.join(",", listaAMR)); // Lista com os fatores de autenticacao do usuario. Pode ser passwd se o mesmo logou fornecendo a senha, ou x509 se o mesmo utilizou certificado digital ou certificado em nuvem.

                /**
                 * Servico 1: De posse do access token, a aplicacao pode chamar o servico para receber a foto do usuario.
                 */

                String resultadoFoto = getFoto(fotoUrl, accessToken);

                System.out.println(
                                "\n--------------------Servico 1 - Foto do usuario------------------");
                System.out.println("Foto retornada:");
                System.out.println(resultadoFoto);

                /**
                 * Servico 2: De posse do access token, a aplicacao pode chamar o servico para
                 * saber quais selos o usuario logado possui.
                 */

                String confiabilidadeJson = getConfiabilidade(accessToken, idTokenJwtClaims.getSubject());

                System.out.println(
                                "\n--------------------Servico 2 - Informacoes acerca da confiabilidade do usuario------------------");
                System.out.println("JSON retornado:");
                System.out.println(confiabilidadeJson);
                
                /**
                 * Servico 3: De posse do access token, a aplicacao pode chamar o servico para
                 * saber quais empresas se encontram vinculadas ao usuario logado.
                 *
                 */

                String empresasJson = getEmpresasVinculadas(accessToken,idTokenJwtClaims.getSubject());

                System.out.println("\n--------------------Servico 3 - Empresas vinculadas ao usuario------------------");
                System.out.println("JSON retornado:");
                System.out.println(empresasJson);

                /**
                 * Servico 4: De posse do access token, a aplicacao pode chamar o servico para
                 * obter dados de uma empresa especifica e o papel do usuario logado nesta
                 * empresa.
                 */

                JSONObject empresasVinculadasJson = (JSONObject) parser.parse(empresasJson);
                JSONArray cnpjs = (JSONArray) empresasVinculadasJson.get("cnpjs");
                JSONObject cnpj = (JSONObject)cnpjs.get(0);


                if (!cnpjs.isEmpty()) {

                        String dadosEmpresaJson = getDadosEmpresa(accessToken, cnpj.get("cnpj").toString() ,idTokenJwtClaims.getSubject());

                        System.out.printf(
                                        "\n--------------------Servico 4 - Informacoes acerca da empresa %s------------------",
                                        cnpjs.get(0));
                        System.out.println("JSON retornado:");
                        System.out.println(dadosEmpresaJson);

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
                                                                                                                                                                        // access token e inferior ou igual
                                                                                                                                                                        // ao tempo maximo estipulado (Tempo
                                                                                                                                                                        // padrao de 60 minutos)
                                .setAllowedClockSkewInSeconds(30) // Esta e uma boa pratica.
                                .setRequireSubject() // Exige que o token tenha um Subject.
                                .setExpectedIssuer(URL_PROVIDER + "/") // Verifica a procedencia do token.
                                .setVerificationKey(pjwk.getPublicKey()) // Verifica a assinatura com a public key fornecida.
                                .build(); // Cria a instancia JwtConsumer.

                return jwtConsumer.processToClaims(token);
        }

        private static String getEmpresasVinculadas(String accessToken, String cpf) throws Exception {
                String retorno = "";

				URL url = new URL(URL_SERVICOS + "https://api.staging.acesso.gov.br/empresas/v2/empresas?filtrar-por-participante="+cpf);
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

                URL url = new URL(URL_SERVICOS + "/empresas/v2/empresas/" + cnpj + "/participantes/"+ cpf);
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

        private static String getConfiabilidade(String accessToken, String cpf) throws Exception {
                String retorno = "";

				URL url = new URL(URL_SERVICOS + "/confiabilidades/v1/usuarios/" + cpf + "/confiabilidades");
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

<?php
/**
 *
 * O presente código tem por objetivo exemplificar de forma minimalista o consumo dos serviços utilizados pelo Gov.br.
 *
 */

require __DIR__ . '/vendor/autoload.php';

use \Firebase\JWT\JWT;

$URL_PROVIDER="https://sso.staging.acesso.gov.br";
$CLIENT_ID = getenv('CLIENT_ID');
$SECRET = getenv('SECRET');
$REDIRECT_URI = getenv('REDIRECT_URI');
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
                         *                              5- Número do telefone está valido ou não no cadastro.
                         *                              6- Número do telefone.
                         *                              7- Endereço de email está valido ou não no cadastro.
                         *                              8- Endereço de email.
         *              9- Método de autenticação (CPF e Senha ou Certificado Digital)
         *                              10- CNPJ vinculado ao usuário autenticado. Atributo será preenchido quando autenticação ocorrer por certificado digital de pessoal jurídica.
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

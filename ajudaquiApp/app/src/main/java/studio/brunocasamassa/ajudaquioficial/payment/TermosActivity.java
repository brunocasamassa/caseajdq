package studio.brunocasamassa.ajudaquioficial.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.User;

import static studio.brunocasamassa.ajudaquioficial.payment.PaymentActivity3.PAYPAL_REQUEST_CODE;

/**
 * Created by bruno on 12/07/2017.
 */



public class TermosActivity extends AppCompatActivity {
    private String paymentAmount = "9.99";
    private int cameFrom = 0;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private TextView naif;
    private TextView text;

    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos);

        Bundle extras = getIntent().getExtras();
        cameFrom = extras.getInt("cameFrom");

        text = (TextView) findViewById(R.id.texto_termos);
        naif = (TextView) findViewById(R.id.titulo_termos);

        //naif.setText("Termos de Uso");

        text.setText("\n\nO AJUDAQUI estabelece nestes Termos De Uso e Política de Privacidade as\n" +
                "condições para utilização da plataforma/site/blog e fanpage denominado\n" +
                "AJUDAQUI, por meio dos quais o Usuário poderá fazer uso desta ferramenta\n" +
                "que disponibiliza atividades que envolvam empréstimos, trocas, doações de\n" +
                "bens/itens/objetos/coisas/alimentos e serviços, ou seja, aquela “ajudinha”\n" +
                "que pode fazer uma diferença positiva na vida das pessoas que utilizam a\n" +
                "rede AJUDAQUI, ou seja, o Usuário.\n" +
                "O AJUDAQUI promove uma plataforma para os usuários compartilharem\n" +
                "serviços em geral, empréstimos, trocas, doações que envolvam\n" +
                "bens/itens/objetos/coisas/alimentos e serviços uns com os outros. O\n" +
                "AJUDAQUI apenas facilita a conexão entre usuários e não tem nenhuma\n" +
                "responsabilidade com relação aos itens envolvidos na transação entre eles ou\n" +
                "qualquer outro acontecimento que decorra de contatos e/ou relações on-line\n" +
                "e/ou off-line, entre usuários que tenham se conectado por meio do aplicativo\n" +
                "ou se encontrem pessoalmente.\n" +
                "O AJUDAQUI poderá estabelecer Termos de Uso e Políticas de Privacidade\n" +
                "específicas e aplicáveis a determinadas plataformas/sites/blogs e fanpages,\n" +
                "que complementarão e/ou prevalecerão sobre estes Termos de Uso e Política\n" +
                "de Privacidade.\n" +
                "Qualquer usuário, para que possa utilizar os serviços do AJUDAQUI deverá\n" +
                "aceitar integralmente os Termos de Uso e Política de Privacidade.\n" +
                "1. DEFINIÇÕES:\n" +
                "Para os fins deste Termos de Uso e Política de Privacidade, consideram-se:\n" +
                "1.1. Internet: o sistema constituído do conjunto de protocolos lógicos,\n" +
                "estruturado em escala mundial para uso público e irrestrito, com a\n" +
                "finalidade de possibilitar a comunicação de dados entre terminais por\n" +
                "meio de diferentes redes;\n" +
                "1.2. Senha: conjunto de caracteres que podem ser constituídos por letras\n" +
                "e/ou números, com a finalidade de verificar a identidade do Usuário\n" +
                "para acesso a plataforma/site/blog e fanpage;\n" +
                "1.3. Plataforma/Sistema/site/blog e fanpage: sites e aplicativos do\n" +
                "AJUDAQUI por meio dos quais o Usuário acessa os serviços e conteúdos\n" +
                "disponibilizados pelo AJUDAQUI;\n" +
                "1.4. URL: endereço virtual de um recurso disponível na internet ou intranet\n" +
                "com um caminho que indica onde está o que o usuário procura;\n" +
                "1.5. Terminais (ou “Terminal”, quando individualmente considerado):\n" +
                "computadores, notebooks, netbooks, smartphones, tablets, palm tops\n" +
                "e quaisquer outros dispositivos por meio dos quais se conecta a\n" +
                "Internet;\n" +
                "1.6. Usuários (ou “Usuário”, quando individualmente considerado): todas\n" +
                "as pessoas físicas que utilizarão a plataforma/site/blog e fanpage, \n" +
                "maiores de 18 (dezoito) anos ou emancipadas e totalmente capazes de\n" +
                "praticar os atos da vida civil ou os absolutamente ou relativamente\n" +
                "incapazes, devidamente representados ou assistidos por seus pais,\n" +
                "tutores, curadores, os quais aceitaram os Termos de Uso e Política de\n" +
                "Privacidade em nome daqueles, bem como pessoas jurídicas\n" +
                "devidamente registradas perante órgãos competentes;\n" +
                "1.7. Servidor de arquivo: é um computador conectado a uma rede que tem\n" +
                "o objetivo principal a de proporcionar um local para o armazenamento\n" +
                "de arquivos de computadores.\n" +
                "2. ACEITE DOS TERMOS DE USO E POLÍTICA DE PRIVACIDADE\n" +
                "2.1. Ao acessar a plataforma/site/blog e fanpage denominado\n" +
                "AJUDAQUI, o Usuário concorda integralmente e aceita as\n" +
                "disposições destes Termos de Uso e Política de Privacidade.\n" +
                "3. OBJETO\n" +
                "3.1. O serviço objeto deste Termo de Uso e Política de Privacidade consiste\n" +
                "em oferecer uma plataforma que possibilite e facilite aos usuários se\n" +
                "conectarem para interagir e compartilhar\n" +
                "bens/itens/objetos/coisas/alimentos e serviços entre si, sem que\n" +
                "envolva qualquer tipo de comercialização/transação financeira para\n" +
                "isso, com o objetivo de promover a conscientização, humanização e\n" +
                "socialização de pessoas e entre pessoas, além de economizar dinheiro\n" +
                "e poupar o planeta terra.\n" +
                "4. CADASTRO E UTILIZAÇÃO DO AJUDAQUI\n" +
                "4.1. O cadastro e a utilização do AJUDAQUI são gratuitos para os usuários\n" +
                "na versão para uso apenas por geolocalização. Para a utilização da\n" +
                "“Versão Premium”, a partir da inserção e criação de grupos e acesso à\n" +
                "Cabine da Fartura, os usuários deverão contribuir mensalmente ou\n" +
                "anualmente com o valor estipulado no ato da adesão a esse formato e\n" +
                "vigente no momento da renovação mensal/anual. Isso também dará o\n" +
                "direito aos usuários de continuar a utilizar, se assim desejarem, o\n" +
                "formato geolocalização, sendo o uso de ambas versões ilimitado.\n" +
                "4.2. Os serviços do AJUDAQUI estão disponíveis para as pessoas físicas\n" +
                "maiores de 18 (dezoito) anos ou emancipadas e totalmente capazes de\n" +
                "praticar os atos da vida civil ou os absolutamente ou relativamente\n" +
                "incapazes, devidamente representados ou assistidos por seus pais,\n" +
                "tutores, curadores que aceitarem os Termos de Uso e Política de\n" +
                "Privacidade em nome daqueles, bem como pessoas jurídicas\n" +
                "devidamente registradas perante órgãos competentes. O uso da\n" +
                "plataforma em outros países além do Brasil poderá estar sujeito a\n" +
                "legislação específica quanto à capacidade legal para uso, o qual deverá \n" +
                "prevalecer sobre estes Termos de Uso e Política de Privacidade. Não\n" +
                "podem utilizá-los, assim, pessoas que não gozem dessa capacidade,\n" +
                "ou pessoas que tenham sido inabilitadas do sistema do AJUDAQUI,\n" +
                "temporária ou definitivamente.\n" +
                "4.3. No caso que envolva a utilização do aplicativo por menores de dezoito\n" +
                "anos, a responsabilidade total por todos os atos e suas consequências\n" +
                "on-line e off-line, fica por conta de seus genitores e/ou responsável\n" +
                "legal, nos termos do artigo 932 do Código Civil Brasileiro, ou legislação\n" +
                "vigente no país que estiver fazendo uso da plataforma, isentando a\n" +
                "plataforma de qualquer atribuição ou correlação entre os fatos e\n" +
                "consequências.\n" +
                "4.4. Serão admitidas, também na qualidade de usuários, pessoas jurídicas.\n" +
                "Tais usuários necessitarão realizar o cadastro de sua empresa e\n" +
                "deverão pagar pelas taxas vigentes no momento da inserção à\n" +
                "Plataforma, como também na renovação mensal/anual. Tal\n" +
                "participação seguirá os mesmos critérios das pessoas físicas,\n" +
                "salientando que é proibida a comercialização/venda/compra, qualquer\n" +
                "tipo de negociação que envolva dinheiro ou qualquer outra negociação\n" +
                "monetária.\n" +
                "4.5. A realização do cadastro tanto de pessoa física como jurídica dará o\n" +
                "direito ao Ajudaqui ao uso de todos os elementos que compõem o\n" +
                "cadastro, inclusive imagens anexadas e logotipos, para o uso em\n" +
                "publicidade/propaganda/marketing em todos os veículos/mídias e\n" +
                "canais existentes a nível nacional e internacional, por tempo\n" +
                "indeterminado, sem que haja qualquer aviso prévio, restrição ou\n" +
                "cobrança por parte dos usuários.\n" +
                "4.6. Não é permitido que uma mesma pessoa tenha mais de um cadastro.\n" +
                "Se o AJUDAQUI detectar, através do sistema de verificação de dados,\n" +
                "cadastros duplicados e/ou falsos (fakes), irá inabilitar definitivamente\n" +
                "todos os cadastros daqueles usuários.\n" +
                "4.7. As pessoas jurídicas terão a oportunidade de promoverem seus\n" +
                "negócios a partir do fornecimento SEM COBRANÇA de seus produtos e\n" +
                "serviços a título de doação/troca/empréstimo/ajuda para os demais\n" +
                "usuários.\n" +
                "4.8. A plataforma AJUDAQUI não se responsabiliza por\n" +
                "produtos/itens/objetos/coisas/alimentos e serviços oferecidos pelas\n" +
                "pessoas físicas ou jurídicas, como também data de validade, origem,\n" +
                "procedência, condições de uso, e suas consequências para quem os\n" +
                "recebeu ou fez uso. Tal responsabilidade é irrestrita das empresas que\n" +
                "publicaram ou anunciaram assegurando todo o tramite e\n" +
                "consequências do que estão fornecendo em termos de segurança,\n" +
                "durabilidade, confiabilidade, prazos do produto ou serviço oferecido e\n" +
                "tudo o mais que o cerca.\n" +
                "4.9. A eventual participação a outro título será oportunamente avaliada e\n" +
                "regulamentada pelo AJUDAQUI. O preenchimento de todos os campos\n" +
                "obrigatórios do cadastro é condição indispensável para poder usufruir\n" +
                "dos serviços do AJUDAQUI.\n" +
                "4.10. No ato do cadastro, o futuro usuário deverá completar todos os campos\n" +
                "de preenchimento com informações exatas, precisas e verdadeiras, e\n" +
                "assume o compromisso de atualizar os dados pessoais ou dados de sua\n" +
                "empresa sempre que neles ocorrer alguma alteração. O AJUDAQUI se\n" +
                "reserva ao direito de utilizar todos os meios válidos, lícitos e possíveis \n" +
                "para identificar possíveis fraudes e uso de dados falsos manipulados\n" +
                "pelos usuários.\n" +
                "4.11. O AJUDAQUI não se responsabiliza pela correção dos dados inseridos\n" +
                "pelas pessoas físicas ou jurídicas. Os usuários garantem e respondem,\n" +
                "em qualquer caso, pela veracidade, exatidão e autenticidade dos dados\n" +
                "cadastrados. O AJUDAQUI se reserva ao direito de solicitar dados\n" +
                "adicionais e documentos que estime serem idôneos a conferir os dados\n" +
                "pessoais e de empresas informados, assim como de inabilitar,\n" +
                "temporária ou definitivamente, o usuário que apresentar qualquer\n" +
                "informação inverídica ou que a plataforma AJUDAQUI não conseguir\n" +
                "contatar para a verificação dos dados.\n" +
                "4.12. Ao cancelar o cadastro do usuário, automaticamente serão cancelados\n" +
                "os pedidos e buscas por bens/itens/objetos/coisas/alimentos e serviços\n" +
                "por ele veiculados ou respostas de\n" +
                "empréstimos/serviços/doações/trocas de\n" +
                "bens/itens/objetos/coisas/alimentos por ele solicitado a outros\n" +
                "usuários, não assistindo ao usuário, por essa razão, qualquer sorte de\n" +
                "indenização ou ressarcimento de valor de qualquer natureza.\n" +
                "4.13. O usuário acessará sua conta através de um nome de usuário (login)\n" +
                "e senha e compromete-se a não informar a terceiros esses dados,\n" +
                "responsabilizando-se integralmente pelo uso que deles seja feito. O\n" +
                "usuário compromete-se a notificar o AJUDAQUI imediatamente,\n" +
                "através de meio seguro, a respeito de qualquer uso ou acesso não\n" +
                "autorizado de sua conta, por terceiros. O usuário será o único\n" +
                "responsável pelas operações efetuadas em sua conta, uma vez que o\n" +
                "acesso a ela só será possível mediante o fornecimento da senha, cuja\n" +
                "responsabilidade é exclusiva do usuário. Em nenhuma hipótese será\n" +
                "permitida a cessão, venda, aluguel ou outra forma de transferência da\n" +
                "conta, incluindo-se qualificações e reputação.\n" +
                "4.14. O nome de usuário (login) utilizado no AJUDAQUI não poderá guardar\n" +
                "semelhança com o nome AJUDAQUI. Tampouco poderá ser utilizado\n" +
                "qualquer nome que insinue ou sugira que os serviços ou\n" +
                "bens/itens/objetos/coisas/alimentos anunciados sejam pertencentes\n" +
                "ao AJUDAQUI ou que fazem parte de suas publicações ou promoções.\n" +
                "4.15. Também serão eliminados nomes considerados ofensivos, bem como\n" +
                "os que contenham dados pessoais do usuário ou alguma URL ou\n" +
                "endereço eletrônico. O AJUDAQUI se reserva ao direito de recusar\n" +
                "qualquer solicitação de cadastro e de cancelar um cadastro\n" +
                "previamente aceito, a seu exclusivo critério.\n" +
                "4.16. Será possível e permitido, nas condições estipuladas nesse\n" +
                "instrumento, o direito de uso dessa ferramenta dentro de instituições\n" +
                "de ensino público e privado, assim como, no meio corporativo dentro\n" +
                "das empresas/organizações.\n" +
                "5. PRIVACIDADE DAS INFORMAÇÕES\n" +
                "5.1. Os dados pessoais prestados pelos usuários do AJUDAQUI, seus\n" +
                "registros de conexão e de acesso a aplicações serão armazenados em\n" +
                "servidores de alta segurança.\n" +
                "5.2. O AJUDAQUI tomará todas as medidas possíveis para manter a\n" +
                "confidencialidade e a segurança descrita nesta clausula, porém não\n" +
                "responderá por qualquer prejuízo que possa ser derivado da violação\n" +
                "dessas medidas por parte de terceiros que utilizem as redes públicas\n" +
                "ou a internet, subvertendo os sistemas de segurança para acessar as\n" +
                "informações de usuários.\n" +
                "5.3. Os registros armazenados nos servidores poderão ser solicitados\n" +
                "mediante ordem judicial, nos termos da Lei nº 12.965 de 23 de abril\n" +
                "de 2014.\n" +
                "5.4. A plataforma AJUDAQUI tão somente permite a interação e conexão\n" +
                "entre os usuários e interessados, nada mais do que isso, assim, a\n" +
                "plataforma não armazena ou possui acesso, visualização, bem como\n" +
                "qualquer outro tipo de interferência no teor da comunicação\n" +
                "estabelecida entre os usuários, seja dentro de um “chat”, “grupos” ou\n" +
                "outro canal que permita a comunicação entre os usuários, ficando sob\n" +
                "inteira responsabilidade destes as consequências cíveis e criminais cujo\n" +
                "teor das referidas comunicações possam vir a ocasionar. Com isso,\n" +
                "cabe exclusivamente ao usuário manter, ou não, armazenado o\n" +
                "conteúdo da comunicação estabelecida na plataforma com outros\n" +
                "usuários.\n" +
                "6. MODIFICAÇÃO DOS TERMOS E CONDIÇÕES GERAIS\n" +
                "6.1. O AJUDAQUI poderá alterar, a qualquer tempo, estes Termos e\n" +
                "Condições Gerais, visando seu aprimoramento e melhoria dos serviços\n" +
                "prestados. Os novos Termos e Condições entrarão em vigor\n" +
                "imediatamente depois de publicados no site.\n" +
                "6.2. No prazo de 05 (cinco) dias contados a partir da publicação das\n" +
                "modificações, o usuário deverá comunicar-se com o AJUDAQUI pelas\n" +
                "vias disponíveis caso não concorde com as alterações. Neste caso, o\n" +
                "vínculo contratual deixará de existir, ficando cancelado o cadastro do\n" +
                "usuário. Não havendo manifestação no prazo estipulado, entender-se-\n" +
                "á que o usuário aceitou os novos Termos e Condições Gerais.\n" +
                "7. ANÚNCIO E BUSCA\n" +
                "7.1. O usuário poderá pedir ajuda que envolva serviços, trocas, doações,\n" +
                "empréstimos de bens/itens/objetos/coisas/alimentos e ao concordar\n" +
                "em ajudar: doar/trocar/emprestar alguns\n" +
                "objetos/bens/coisas/itens/alimentos ou mesmo prestar algum serviço,\n" +
                "presumir-se-á que o usuário manifesta a intenção e declara possuir o\n" +
                "direito de emprestar/trocar/doar tais\n" +
                "objetos/coisas/bens/itens/alimentos ou está disponível a prestar tal\n" +
                "serviço, ou que está facultado para tal por seu titular.\n" +
                "7.2. O AJUDAQUI poderá remover, a seu exclusivo critério, os anúncios cuja\n" +
                "especificação não esteja suficientemente clara, ou que permitam\n" +
                "algum tipo de variação, dupla interpretação ou violação.\n" +
                "7.3. O usuário poderá indicar no sistema AJUDAQUI o\n" +
                "bens/itens/objetos/coisas/alimentos que lhe interessa para\n" +
                "empréstimo/troca/doação ou a necessidade de algum serviço. O\n" +
                "sistema apenas facilitará o contato entre os usuários que respondam a\n" +
                "tal solicitação. Os usuários poderão entrar em contato direto, por\n" +
                "qualquer meio, para efetuar o combinado, não podendo o AJUDAQUI\n" +
                "ser responsabilizado neste caso.\n" +
                "7.4. Ao efetuar o empréstimo/troca/doação ou serviço, caso os mesmos se\n" +
                "efetivem corretamente, os usuários envolvidos deverão informar tal\n" +
                "fato no sistema através de VALIDAÇÃO (confirmação de que ocorreu).\n" +
                "As condições do empréstimo/troca/doação ou serviço quanto a forma,\n" +
                "duração, local e todas as demais questões envolvidas serão\n" +
                "estabelecidas de comum acordo entre os usuários e sob sua exclusiva\n" +
                "responsabilidade.\n" +
                "7.5. O AJUDAQUI se isenta de toda e qualquer forma de transação realizada\n" +
                "pelos usuários e tudo que a envolve, desde as condições dos objetos,\n" +
                "serviços, data de validade dos alimentos, prazos, existência dos\n" +
                "objetos, entregas, ocorrências, arrependimentos, etc. São de completa\n" +
                "e única responsabilidade dos usuários tudo que envolve tais\n" +
                "negociações entre si, ficando claro que a plataforma AJUDAQUI tão\n" +
                "somente oferece e permite a interação e conexão entre os usuários e\n" +
                "interessados, nada mais do que isso.\n" +
                "7.6. Poderão ser anunciados bens/itens/objetos/coisas/alimentos e\n" +
                "serviços cujo empréstimo/troca/doação e atendimento não estejam\n" +
                "expressamente proibidos pelos Termos de Uso e Política de Privacidade\n" +
                "do AJUDAQUI, ou pela legislação vigente no Brasil, ou no país em que\n" +
                "esteja sendo utilizada a plataforma.\n" +
                "7.7. Fica expressamente proibido o empréstimo/troca/doação/serviços de\n" +
                "tempo, pessoas ou bens/itens/objetos/coisas/alimentos para qualquer\n" +
                "atividade ilícita ou imoral, como também serviços e/ou produtos\n" +
                "relacionados a prostituição ou similares, material pornográfico,\n" +
                "obsceno ou contrário a moral e os bons costumes, quaisquer produtos\n" +
                "cuja a venda é expressamente proibida pelas leis vigentes no Brasil ou\n" +
                "no país em que esteja sendo utilizada a plataforma, de atividades ou\n" +
                "bens/itens/objetos/coisas/alimentos que promovam a violência e/ou a\n" +
                "discriminação baseada em questões de raça, sexo, religião, cor,\n" +
                "nacionalidade, orientação sexual ou de qualquer outro tipo de\n" +
                "discriminação.\n" +
                "7.8. Também ficam proibidos os serviços/atividades ou\n" +
                "bens/itens/coisas/objetos/alimentos que violem a propriedade\n" +
                "intelectual, como direitos autorais, marcas, patentes, modelos,\n" +
                "desenhos industriais, autoria de softwares, direitos de imagem, voz e\n" +
                "quaisquer outros protegidos pelas leis brasileiras ou do país em que\n" +
                "esteja sendo utilizada a plataforma.\n" +
                "7.9. É de responsabilidade exclusiva do usuário velar pela legalidade dos\n" +
                "seus empréstimos/trocas/doações e serviços.\n" +
                "8. PRÁTICAS VEDADAS\n" +
                "8.1. Os usuários não podem:\n" +
                "a) Manipular as características dos\n" +
                "produtos/bens/itens/objetos/coisas/alimentos e serviços oferecidos;\n" +
                "b) Comercializar ou realizar transações financeiras de quaisquer\n" +
                "produtos/bens/itens/objetos/coisas/alimentos;\n" +
                "c) Interferir nas transações entre outros usuários;\n" +
                "d) Prestar informações falsas;\n" +
                "e) Anunciar atividades ou produtos/bens/itens/objetos/coisas/alimentos\n" +
                "e serviços proibidos pelas políticas do AJUDAQUI e pelas leis em vigor\n" +
                "no país;\n" +
                "f) Agredir, caluniar, injuriar ou difamar outros usuários.\n" +
                "8.2. Estes comportamentos poderão ser sancionados com a suspensão ou\n" +
                "cancelamento do pedido, ou com a suspensão ou cancelamento do\n" +
                "cadastro do usuário do AJUDAQUI, sem prejuízo das ações legais que\n" +
                "possam ocorrer pela configuração de infrações penais ou os prejuízos\n" +
                "civis que possam causar aos demais e sem prévio aviso por parte da\n" +
                "plataforma AJUDAQUI.\n" +
                "9. SOBRE A “CABINE DA FARTURA”\n" +
                "9.1. O AJUDAQUI disponibiliza em sua plataforma um espaço destinado\n" +
                "àqueles usuários que possuem especial preocupação com o desperdício\n" +
                "de alimentos, para tanto, há na ferramenta “Cabine da Fartura” um\n" +
                "espaço reservado para aquele usuário que quer dar uma “ajudinha”\n" +
                "para que este alimento chegue à mesa de quem precisa.\n" +
                "9.2. Podem fazer uso da “Cabine da Fartura” apenas os usuários pessoas\n" +
                "físicas e jurídicas que estejam cadastrados na plataforma AJUDAQUI\n" +
                "na “Versão Premium”.\n" +
                "9.3. Nesta ferramenta somente será possível a DOAÇÃO de\n" +
                "alimentos/itens/objetos/bens/coisas e serviços em quantidade mínima\n" +
                "de 10 (dez) itens/unidades/serviços para no mínimo 10 (dez) usuários\n" +
                "(considera-se que 1 (um) usuário somente possa adquirir/receber 1\n" +
                "(uma) unidade/1 (um) item/1 (um) serviço do que esta sendo ofertado\n" +
                "em cada publicação).\n" +
                "9.4. Ao anunciar a doação, o usuário deverá informar as condições do\n" +
                "alimento, tais como:\n" +
                "a) Descrição do(s) alimento(s) e seu fabricante;\n" +
                "b) Data de validade;\n" +
                "c) Se foi devidamente acondicionado em local indicado pelo fabricante.\n" +
                "9.5. O AJUDAQUI apenas permite a conexão entre usuários interessados\n" +
                "que querem doar e outros que querem receber os alimentos, não\n" +
                "possuindo acesso ou mesmo interferindo, moderando ou validando\n" +
                "qualquer comunicação entre eles. Assim, o AJUDAQUI se isenta\n" +
                "integralmente de qualquer responsabilidade decorrente da doação de\n" +
                "alimentos ofertada pelo usuário pessoa física ou jurídica, como também\n" +
                "pela data de validade, origem, procedência, condições de uso e\n" +
                "armazenamento anunciadas e praticadas, bem como suas\n" +
                "consequências para quem os recebeu ou fez uso. Tal responsabilidade\n" +
                "é irrestrita dos usuários que publicaram, anunciaram ou doaram e,\n" +
                "também daqueles que produziram o alimento, sendo estes\n" +
                "responsáveis por todo trâmite e consequências do que estão \n" +
                "fornecendo em termos de segurança, durabilidade, confiabilidade,\n" +
                "prazos de validade do alimento oferecido e tudo o mais que o cerca.\n" +
                "10. OBRIGAÇÕES DOS USUÁRIOS\n" +
                "10.1. Os usuários interessados em realizar uma transação de\n" +
                "empréstimo/troca/doação de bens/itens/coisas/objetos/alimentos e\n" +
                "serviços anunciados por um outro usuário no AJUDAQUI devem fazer\n" +
                "contato dentro do sistema AJUDAQUI, estabelecendo as condições da\n" +
                "transação de empréstimo/troca/doação ou serviço, destacando que o\n" +
                "produto ou serviço em questão não pode ser proibido por lei ou por\n" +
                "estes Termos e Condições Gerais. Ao manifestar o interesse em algum\n" +
                "bens/itens/objetos/coisas/alimentos ou serviço, o usuário obriga-se a\n" +
                "atender às condições de negociação descritas no anúncio.\n" +
                "10.2. Os usuários comprometem-se a prestar uns aos outros apenas\n" +
                "informações verdadeiras, tanto sobre si mesmos quanto sobre o\n" +
                "bens/itens/objetos/coisas/alimentos e serviço em questão e as\n" +
                "condições da transação.\n" +
                "10.3. Após a realização da transação de troca, empréstimo, doação e serviço,\n" +
                "os usuários poderão realizar uma avaliação do outro usuário, que de\n" +
                "maneira global afetará o seu perfil, segundo os requisitos estabelecidos\n" +
                "na plataforma, tais como confiabilidade, satisfação, pontualidade, etc.\n" +
                "A avaliação é opcional, mas condicionará a possibilidade de realização\n" +
                "de novas transações/negociações.\n" +
                "10.4. O usuário deverá ter capacidade legal para efetuar a transação de\n" +
                "doação/troca/empréstimo ou serviço a que se propôs. No caso de\n" +
                "menores de 18 (dezoito) anos, relativamente ou absolutamente\n" +
                "incapazes, os pais ou responsáveis legais deverão manter constante\n" +
                "acompanhamento e controle além de nortearem as transações que\n" +
                "serão realizadas, assumindo total e irrestrita responsabilidade sobre as\n" +
                "ações/atitude de seus filhos e posterior consequências isentando\n" +
                "totalmente a plataforma AJUDAQUI.\n" +
                "10.5. Em virtude do AJUDAQUI possibilitar o encontro entre usuários, e por\n" +
                "não participar das transações que se realizam entre eles, a\n" +
                "responsabilidade por todas as obrigações, sejam elas fiscais, jurídicas,\n" +
                "trabalhistas, consumeristas, criminais ou de qualquer outra natureza,\n" +
                "decorrentes das transações originadas no espaço virtual do aplicativo\n" +
                "serão exclusivamente dos usuários. Em caso de interpelação judicial\n" +
                "que tenha como Réu o AJUDAQUI, cujos fatos fundem-se em ações do\n" +
                "usuário, este será chamado ao processo devendo arcar com todos os\n" +
                "ônus que daí decorram, nos termos do artigo 125, II do Código de\n" +
                "Processo Civil.\n" +
                "10.6. Em virtude da característica do aplicativo, também não pode obrigar o\n" +
                "usuário a honrar sua obrigação ou completar a negociação, fazer\n" +
                "devoluções, entre outros.\n" +
                "10.7. O AJUDAQUI não se responsabiliza pelas obrigações de natureza\n" +
                "tributária que incidam sobre os negócios realizados entre usuários.\n" +
                "Assim, o usuário que praticar ato que gere hipótese de incidência\n" +
                "tributária de qualquer natureza, nos termos da lei em vigor,\n" +
                "responsabilizar-se-á pela integralidade das obrigações oriundas de\n" +
                "suas atividades, notadamente pelos tributos envolvidos.\n" +
                "11. AVALIAÇÕES E QUALIFICAÇÕES\n" +
                "11.1. A plataforma AJUDAQUI não tem condições de realizar a averiguação\n" +
                "da identidade dos internautas usuários do aplicativo e por essa razão\n" +
                "o sistema de avaliação e qualificação mútua, realizada através dos\n" +
                "comentários após as transações/negociações finalizadas, é de extrema\n" +
                "valia.\n" +
                "11.2. Todos os usuários da plataforma devem informar sobre a finalização\n" +
                "da negociação seja por troca, doação, empréstimo ou serviço, incluindo\n" +
                "comentários de total e irrestrita responsabilidade sobre o que\n" +
                "disserem. O AJUDAQUI não tem obrigação e nem responsabilidade de\n" +
                "verificar a veracidade das informações lançadas pelo usuário a fim de\n" +
                "avaliar e qualificar o outro usuário na plataforma, como também no\n" +
                "site, blog, fanpage, e-mail ou em qualquer espaço na internet e fora\n" +
                "dela.\n" +
                "11.3. A Plataforma se reserva ao direito de excluir as avaliações/comentários\n" +
                "que julgar inadequado, impróprios, impertinentes e ofensivos, ainda, o\n" +
                "AJUDAQUI se reserva ao direito de aplicar as sanções necessárias e\n" +
                "previstas nesse instrumento aos usuários que sejam reiteradamente\n" +
                "mal avaliados pelos demais usuários ou se realizarem atividades\n" +
                "vedadas.\n" +
                "11.4. A avaliação e qualificação é uma forma de auxiliar a todos a escolher\n" +
                "pessoas confiáveis e bem intencionadas para fazer suas\n" +
                "negociações/interações.\n" +
                "12. RESPONSABILIDADES\n" +
                "12.1. Estes Termos e Condições Gerais não geram nenhum contrato de\n" +
                "sociedade, de mandato, franquia ou relação de trabalho entre o\n" +
                "AJUDAQUI e o usuário. O usuário manifesta ciência de que o\n" +
                "AJUDAQUI não é parte de nenhuma transação, nem possui controle\n" +
                "algum sobre a qualidade, segurança ou legalidade dos\n" +
                "anúncios/pedidos, sobre a sua veracidade ou exatidão, e sobre a\n" +
                "capacidade dos usuários para negociar.\n" +
                "12.2. O AJUDAQUI não interfere de nenhuma forma na negociação ou na\n" +
                "realização dos empréstimos/trocas/doações e serviços entre os\n" +
                "usuários que se iniciam no aplicativo, somente disponibiliza a\n" +
                "plataforma online como meio facilitador, assim sendo, a Plataforma não\n" +
                "se responsabiliza pela existência, quantidade, qualidade, estado,\n" +
                "integridade, validade ou legitimidade dos\n" +
                "produtos/bens/itens/objetos/coisas/alimentos e/ou serviços\n" +
                "oferecidos, adquiridos, alienados, emprestados, doados, trocados\n" +
                "pelos usuários, assim como pela capacidade para contratar/negociar\n" +
                "dos usuários ou pela veracidade das informações por eles prestadas.\n" +
                "12.3. O AJUDAQUI não se responsabiliza pela existência de vícios ocultos ou\n" +
                "aparentes do objeto a ser negociações entre os usuários, cabendo a\n" +
                "estes, exclusivamente, garantir a boa-fé da negociação.\n" +
                "12.4. Cada usuário conhece e aceita ser o único responsável pelos\n" +
                "pedidos que anuncia e atende. O AJUDAQUI não será\n" +
                "responsável pelo efetivo cumprimento das obrigações\n" +
                "assumidas pelos usuários. Os usuários reconhecem e aceitam\n" +
                "que ao realizar negociações com outros usuários ou terceiros\n" +
                "faz por sua conta e risco.\n" +
                "12.5. Em nenhum caso o AJUDAQUI será responsável pelo lucro cessante ou\n" +
                "por qualquer outro dano e/ou prejuízo que o usuário possa sofrer\n" +
                "devido às negociações ou transações realizadas ou não realizadas\n" +
                "através da plataforma AJUDAQUI. A Plataforma não é intermediária das\n" +
                "transações e recomenda que toda transação seja realizada com cautela\n" +
                "e bom senso.\n" +
                "12.6. O AJUDAQUI não será responsável pelas transações entre os usuários,\n" +
                "mesmo as firmadas com base na confiança depositada no sistema ou\n" +
                "nos serviços prestados pelo AJUDAQUI.\n" +
                "12.7. Nos casos em que um ou mais usuários ou algum terceiro inicie\n" +
                "qualquer tipo de reclamação ou ação legal contra outro(s) usuário(s)\n" +
                "envolvido(s) na(s) reclamação(es) ou ação(es), exime(m) de toda e\n" +
                "qualquer responsabilidade o AJUDAQUI e a seus diretores, gerentes,\n" +
                "empregados, agentes, representantes, terceirizados e procuradores.\n" +
                "12.8. O AJUDAQUI resguarda-se de toda e qualquer responsabilidade por\n" +
                "fatos resultantes da interação entre os usuários, dentro ou fora do\n" +
                "mundo virtual, devendo cada usuário zelar e responsabilizar-se por sua\n" +
                "segurança e pela das pessoas que com ele interagem e de seus filhos\n" +
                "menores de idade que fazem uso da plataforma.\n" +
                "12.9. O AJUDAQUI não pode assegurar o êxito de qualquer transação,\n" +
                "tampouco verificar a identidade ou os dados pessoais dos usuários. O\n" +
                "AJUDAQUI não garante a veracidade da publicação de terceiros que\n" +
                "apareça em sua plataforma e não será responsável pela\n" +
                "correspondência ou contratos que o usuário realize com terceiros.\n" +
                "13. INDENIZAÇÃO\n" +
                "13.1. O usuário indenizará o AJUDAQUI, suas filiais, empresas controladas\n" +
                "ou controladoras, diretores, administradores, colaboradores,\n" +
                "representantes e empregados por qualquer demanda promovida por\n" +
                "outros usuários ou terceiros decorrentes de suas atividades no\n" +
                "aplicativo ou por seu descumprimento dos Termos de Uso e Política de\n" +
                "Privacidade da plataforma, ou pela violação de qualquer lei ou direitos\n" +
                "de terceiros, incluindo honorários de advogados.\n" +
                "14. PROPRIEDADE INTELECTUAL E LINKS\n" +
                "14.1. Os conteúdos das telas relativas aos serviços do AJUDAQUI, assim\n" +
                "como os programas, logomarca, bancos de dados, redes, arquivos que\n" +
                "permitem que o usuário acesse e use sua conta são de propriedade do\n" +
                "AJUDAQUI e estão protegidos pelas leis e tratados internacionais de\n" +
                "direito autoral, marcas, patentes, modelos e desenhos industriais.\n" +
                "14.2. O uso indevido e a reprodução total ou parcial dos referidos conteúdos\n" +
                "são proibidos, salvo autorização expressa do AJUDAQUI.\n" +
                "14.3. O aplicativo/site/blog e fanpage podem apresentar conexão (links) com\n" +
                "outros sites/aplicativos/blogs e fanpages da rede, o que não significa\n" +
                "que esses sites/aplicativos/blogs e fanpages sejam de propriedade ou\n" +
                "operados pelo AJUDAQUI. Não possuindo controle sobre esses\n" +
                "sites/aplicativos/blogs e fanpages, o AJUDAQUI não será responsável\n" +
                "pelos conteúdos, práticas, benefícios e serviços ofertados pelos\n" +
                "mesmos. Observe que ao usar serviços de terceiros, os termos e as\n" +
                "políticas de privacidade aplicáveis serão os elaborados por estes.\n" +
                "14.4. A presença de links para outros sites/aplicativos/blogs e fanpages não\n" +
                "implica relação de sociedade, de supervisão, de cumplicidade ou\n" +
                "solidariedade do AJUDAQUI para com esses sites, seus conteúdos,\n" +
                "práticas, propostas, benefícios e produtos.\n" +
                "15. VIOLAÇÃO DO SISTEMA OU BANCO DE DADOS\n" +
                "15.1. Não é permitida a utilização de nenhum dispositivo, software, ou outro\n" +
                "recurso por um terminal utilizado por usuário ou não usuário que venha\n" +
                "a interferir nas atividades e operações do AJUDAQUI, bem como nos\n" +
                "anúncios, descrições, contas ou seus bancos de dados. Qualquer\n" +
                "intromissão, ou tentativa, ou atividade que viole ou contrarie as leis de\n" +
                "direito de propriedade intelectual e/ou as proibições estipuladas nestes\n" +
                "Termos de Uso e Política de Privacidade, tornarão o responsável\n" +
                "passível das ações legais pertinentes, bem como das sanções aqui\n" +
                "previstas, sendo ainda responsável pelas indenizações por eventuais\n" +
                "danos causados.\n" +
                "15.2. Sem prejuízo de outras medidas, o AJUDAQUI poderá advertir\n" +
                "suspender ou cancelar, temporária ou definitivamente, a conta de um\n" +
                "usuário a qualquer tempo, e iniciar as ações legais cabíveis se:\n" +
                "a) O usuário não cumprir, fraudar ou dolosamente incorrer em qualquer\n" +
                "dispositivo destes Termos de Uso e Política de Privacidade do\n" +
                "AJUDAQUI;\n" +
                "b) Se descumprir com seus deveres de usuário;\n" +
                "c) Se não puder ser verificada a identidade do usuário ou qualquer\n" +
                "informação fornecida por ele esteja incorreta;\n" +
                "d) Caso se verifique duplicidade ou falsidade do cadastro;\n" +
                "e) Se o AJUDAQUI entender que os anúncios/pedidos ou qualquer atitude\n" +
                "do usuário tenha causado qualquer dano a terceiros ou ao próprio\n" +
                "AJUDAQUI ou tenha a potencialidade de assim o fazer.\n" +
                "15.3. Nos casos de inabilitação do cadastro do usuário, todos os\n" +
                "produtos/bens/itens/objetos/alimentos e serviços por este solicitado\n" +
                "ou oferecido serão automaticamente cancelados.\n" +
                "16. FALHAS NO SISTEMA\n" +
                "16.1. O AJUDAQUI não se responsabiliza por qualquer dano, prejuízo ou\n" +
                "perda no equipamento do usuário causada por falhas no sistema, no\n" +
                "servidor ou na internet.\n" +
                "16.2. O AJUDAQUI também não será responsável por qualquer vírus que\n" +
                "possa atacar o equipamento do usuário em decorrência do acesso,\n" +
                "utilização ou navegação no aplicativo/site/fanpage ou blog na internet\n" +
                "ou como consequência da transferência de dados, arquivos, imagens,\n" +
                "textos ou áudio contidos no mesmo.\n" +
                "16.3. Os usuários não poderão atribuir ao AJUDAQUI nenhuma\n" +
                "responsabilidade nem exigir o pagamento por lucro cessante em\n" +
                "virtude de prejuízos resultante de dificuldades técnicas ou falhas nos\n" +
                "sistemas ou na internet.\n" +
                "16.4. O AJUDAQUI não garante o acesso, uso contínuo ou sem interrupções\n" +
                "de seu aplicativo. Eventualmente, o sistema poderá não estar\n" +
                "disponível por motivos técnicos ou falhas da internet, ou por qualquer\n" +
                "outra circunstância alheia a Plataforma.\n" +
                "17. TARIFAS\n" +
                "17.1. O cadastro do usuário pessoa física ou jurídica e a utilização do\n" +
                "AJUDAQUI são gratuitos na versão apenas por geolocalização.\n" +
                "17.2. No caso de participar/criar qualquer grupo, como também ter acesso à\n" +
                "“Cabine da Fartura”, o AJUDAQUI cobrará a taxa em vigor no momento\n" +
                "da adesão à “Versão Premium”. Essa taxa poderá ser escolhida pelo\n" +
                "usuário nas modalidades: mensal ou anual, podendo as mesmas serem\n" +
                "alteradas sem qualquer aviso prévio.\n" +
                "17.3. As promoções que darão descontos em qualquer percentual serão\n" +
                "lançadas e retiradas do sistema sem prévio aviso e também não terão\n" +
                "definição de tempo, podendo durar anos, meses, dias ou horas.\n" +
                "18. LEGISLAÇÃO APLICÁVEL E FORO DE ELEIÇÃO\n" +
                "18.1. Todos os itens destes Termos de Uso e Política de Privacidade estão\n" +
                "regidos pelas leis vigentes na República Federativa do Brasil.\n" +
                "18.2. Para todos os assuntos referentes à interpretação e ao cumprimento\n" +
                "deste Termo de Uso e Política de Privacidade, as partes se submetem\n" +
                "ao Foro Central da Comarca de São Paulo – SP.\n" +
                "19. DISPOSIÇÕES GERAIS\n" +
                "19.1. O presente Termo de Uso e Política de Privacidade foi elaborado em\n" +
                "português (BR). As versões traduzidas são fornecidas apenas para sua\n" +
                "conveniência. Caso haja conflitos entre a versão traduzida e a versão\n" +
                "em português dos nossos Termos, a versão em português prevalecerá.");




        Button aceite = (Button) findViewById(R.id.aceite_termos);
        Button recusar = (Button) findViewById(R.id.recuse_termos);


        text.setMovementMethod(new ScrollingMovementMethod());

        if(cameFrom == 0 ){//sobre activity

            aceite.setText("ACEITAR");
            aceite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUser(true);
                    startActivity(new Intent(TermosActivity.this, PedidosActivity.class));

                }
            });

            recusar.setText("RECUSAR");
            recusar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"Você deve aceitar os termos de uso para continuar", Toast.LENGTH_SHORT).show();
                    //updateUser(false);
                }
            });
        }
        else if(cameFrom == 1) { //payment activity
            naif.setText("Termos de Compra");
            aceite.setText("ACEITAR");
            aceite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPayment();
                }
            });
        }



    }

    private void updateUser(final boolean b) {
        DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios");
        dbUser.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setTermosAceitos(true);
                user.save();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPayment() {
        //Getting the amount from editText

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "BRL", "Simplified Coding Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);



        //Creating Paypal Payment activity intent
        Intent intent = new Intent(TermosActivity.this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PaymentActivity.RESULT_OK);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal

        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {

                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {

                    try {

                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

}

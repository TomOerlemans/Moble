package com.example.tom.moble;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView topText;
    TextView bottomText;
    Button previousButton;
    int page = 0;
    int firstLaunch;
    DatabaseHandler db;
    AlarmReceiver alarm;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        firstLaunch = sharedPref.getInt("First Launch", 0);

        if(firstLaunch == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("First Launch", 1);
            editor.commit();
            setContentView(R.layout.startscreen1);
            db = new DatabaseHandler(this);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    populateDataBase();
                }
            });
        }else{
            setContentView(R.layout.menu);
        }

        topText = (TextView) findViewById(R.id.topText);
        bottomText = (TextView) findViewById(R.id.bottomText);
        previousButton = (Button) findViewById(R.id.previousButton);
        alarm = new AlarmReceiver();



        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    public void onDismiss(DialogInterface dialog) {
                        if (android.os.Build.VERSION.SDK_INT >= 23)
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }

        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    public void nextButtonClick(View view) {
        switch(page){
            case 0: setContentView(R.layout.startscreen2); page = 1; break;
            case 1: setContentView(R.layout.startscreen3); page = 2; break;
        }

    }

    public void previousButtonClick(View view) {
        switch(page){
            case 1: setContentView(R.layout.startscreen1); page = 0;break;
            case 2: setContentView(R.layout.startscreen2); page = 1; break;
        }
    }

    public void menuButtonClick(View view){
        setContentView(R.layout.menu);
        page = 0;
    }

    public void infoButtonClick(View view){
        setContentView(R.layout.startscreen1);
    }

    public void settingsButtonClick(View view){
        setContentView(R.layout.settings);
    }

    public void doneButtonClick(View view){
        setContentView(R.layout.menu);
    }

    public void startTimeButtonClick(View view){
        final TextView startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (hourOfDay < 12){
                            startTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute) + " AM");
                        }else{
                            startTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute)+ " PM");
                        }

                    }
                }, 9, 0, false);
        tpd.show();
    }



    public void endTimeButtonClick(View view){
        final TextView endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (hourOfDay < 12){
                            endTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute) + " AM");
                        }else{
                            endTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute)+ " PM");
                        }
                    }
                }, 21, 0, false);
        tpd.show();
    }

    public void entryTestButtonClick(View view){
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }


    public void startNotifications(View view){
        Log.v("mainacitvity", "1");
        alarm.setAlarm(this);
        Toast.makeText(this, "started notifcations",
                Toast.LENGTH_LONG).show();
    }


    public void stopNotifications(View view){
        alarm.cancelAlarm(this);
        Toast.makeText(this, "stopped notifications",
                Toast.LENGTH_LONG).show();
    }

    public void populateDataBase(){
        db.addEntry(new DatabaseEntry(0,"color","black","preto"));
        db.addEntry(new DatabaseEntry(1,"color","blue","azul"));
        db.addEntry(new DatabaseEntry(2,"color","brown","marrom"));
        db.addEntry(new DatabaseEntry(3,"color","gray","cinza"));
        db.addEntry(new DatabaseEntry(4,"color","green","verde"));
        db.addEntry(new DatabaseEntry(5,"color","orange","laranja"));
        db.addEntry(new DatabaseEntry(6,"color","pink","cor-de-rosa"));
        db.addEntry(new DatabaseEntry(7,"color","red","vermelho"));
        db.addEntry(new DatabaseEntry(8,"color","violet","roxo"));
        db.addEntry(new DatabaseEntry(9,"color","white","branco"));
        db.addEntry(new DatabaseEntry(10,"color","yellow","amarelo"));
        db.addEntry(new DatabaseEntry(11,"anatomy","arm","braço"));
        db.addEntry(new DatabaseEntry(12,"anatomy","back","costas"));
        db.addEntry(new DatabaseEntry(13,"anatomy","beak","bico"));
        db.addEntry(new DatabaseEntry(14,"anatomy","blood","sangue"));
        db.addEntry(new DatabaseEntry(15,"anatomy","body","corpo"));
        db.addEntry(new DatabaseEntry(16,"anatomy","bone","osso"));
        db.addEntry(new DatabaseEntry(17,"anatomy","brain","cérebro"));
        db.addEntry(new DatabaseEntry(18,"anatomy","breath","bafo"));
        db.addEntry(new DatabaseEntry(19,"anatomy","cell","cela"));
        db.addEntry(new DatabaseEntry(20,"anatomy","chest","peito"));
        db.addEntry(new DatabaseEntry(21,"anatomy","chest","peito"));
        db.addEntry(new DatabaseEntry(22,"anatomy","chin","queixo"));
        db.addEntry(new DatabaseEntry(23,"anatomy","ear","orelha"));
        db.addEntry(new DatabaseEntry(24,"anatomy","eye","olho"));
        db.addEntry(new DatabaseEntry(25,"anatomy","face","rosto"));
        db.addEntry(new DatabaseEntry(26,"anatomy","finger","dedo"));
        db.addEntry(new DatabaseEntry(27,"anatomy","foot","pé"));
        db.addEntry(new DatabaseEntry(28,"anatomy","forehead","testa"));
        db.addEntry(new DatabaseEntry(29,"anatomy","gene","gene"));
        db.addEntry(new DatabaseEntry(30,"anatomy","hair","cabelo"));
        db.addEntry(new DatabaseEntry(31,"anatomy","hair","cabelo"));
        db.addEntry(new DatabaseEntry(32,"anatomy","hand","mão"));
        db.addEntry(new DatabaseEntry(33,"anatomy","head","cabeça"));
        db.addEntry(new DatabaseEntry(34,"anatomy","heart","coração"));
        db.addEntry(new DatabaseEntry(35,"anatomy","knee","joelho"));
        db.addEntry(new DatabaseEntry(36,"anatomy","leg","perna"));
        db.addEntry(new DatabaseEntry(37,"anatomy","lip","lábio"));
        db.addEntry(new DatabaseEntry(38,"anatomy","mouth","boca"));
        db.addEntry(new DatabaseEntry(39,"anatomy","muscle","músculo"));
        db.addEntry(new DatabaseEntry(40,"anatomy","nail","unha"));
        db.addEntry(new DatabaseEntry(41,"anatomy","neck","pescoço"));
        db.addEntry(new DatabaseEntry(42,"anatomy","nerve","nervo"));
        db.addEntry(new DatabaseEntry(43,"anatomy","nose","nariz"));
        db.addEntry(new DatabaseEntry(44,"anatomy","skin","pele"));
        db.addEntry(new DatabaseEntry(45,"anatomy","stomach","estômago"));
        db.addEntry(new DatabaseEntry(46,"anatomy","tail","cauda"));
        db.addEntry(new DatabaseEntry(47,"anatomy","throat","garganta"));
        db.addEntry(new DatabaseEntry(48,"anatomy","thumb","polegar"));
        db.addEntry(new DatabaseEntry(49,"anatomy","toe","dedão"));
        db.addEntry(new DatabaseEntry(50,"anatomy","tongue","língua"));
        db.addEntry(new DatabaseEntry(51,"anatomy","tooth","dente"));
        db.addEntry(new DatabaseEntry(52,"anatomy","wing","asa"));
        db.addEntry(new DatabaseEntry(53,"animals","ant","formiga"));
        db.addEntry(new DatabaseEntry(54,"animals","bear","urso"));
        db.addEntry(new DatabaseEntry(55,"animals","bee","abelha"));
        db.addEntry(new DatabaseEntry(56,"animals","bird","ave"));
        db.addEntry(new DatabaseEntry(57,"animals","cat","gato"));
        db.addEntry(new DatabaseEntry(58,"animals","cow","vaca"));
        db.addEntry(new DatabaseEntry(59,"animals","dog","cachorro"));
        db.addEntry(new DatabaseEntry(60,"animals","elephant","elefante"));
        db.addEntry(new DatabaseEntry(61,"animals","fish","peixe"));
        db.addEntry(new DatabaseEntry(62,"animals","fly","mosca"));
        db.addEntry(new DatabaseEntry(63,"animals","goat","cabra"));
        db.addEntry(new DatabaseEntry(64,"animals","horse","cavalo"));
        db.addEntry(new DatabaseEntry(65,"animals","insect","inseto"));
        db.addEntry(new DatabaseEntry(66,"animals","lion","leão"));
        db.addEntry(new DatabaseEntry(67,"animals","monkey","macaco"));
        db.addEntry(new DatabaseEntry(68,"animals","pig","porco"));
        db.addEntry(new DatabaseEntry(69,"animals","sheep","ovelha"));
        db.addEntry(new DatabaseEntry(70,"animals","snake","serpente"));
        db.addEntry(new DatabaseEntry(71,"time","afternoon","tarde"));
        db.addEntry(new DatabaseEntry(72,"time","age","idade"));
        db.addEntry(new DatabaseEntry(73,"time","autumn","outono"));
        db.addEntry(new DatabaseEntry(74,"time","beginning","começo"));
        db.addEntry(new DatabaseEntry(75,"time","century","século"));
        db.addEntry(new DatabaseEntry(76,"time","date","data"));
        db.addEntry(new DatabaseEntry(77,"time","day","dia"));
        db.addEntry(new DatabaseEntry(78,"time","evening","noite"));
        db.addEntry(new DatabaseEntry(79,"time","future","futuro"));
        db.addEntry(new DatabaseEntry(80,"time","hour","hora"));
        db.addEntry(new DatabaseEntry(81,"time","midnight","meia-noite"));
        db.addEntry(new DatabaseEntry(82,"time","minute","minuto"));
        db.addEntry(new DatabaseEntry(83,"time","moment","momento"));
        db.addEntry(new DatabaseEntry(84,"time","moment","instante"));
        db.addEntry(new DatabaseEntry(85,"time","month","mês"));
        db.addEntry(new DatabaseEntry(86,"time","morning","manhã"));
        db.addEntry(new DatabaseEntry(87,"time","night","noite"));
        db.addEntry(new DatabaseEntry(88,"time","now","agora"));
        db.addEntry(new DatabaseEntry(89,"time","past","passado"));
        db.addEntry(new DatabaseEntry(90,"time","pause","pausa"));
        db.addEntry(new DatabaseEntry(91,"time","period","período"));
        db.addEntry(new DatabaseEntry(92,"time","present","presente"));
        db.addEntry(new DatabaseEntry(93,"time","season","estação"));
        db.addEntry(new DatabaseEntry(94,"time","second","segundo"));
        db.addEntry(new DatabaseEntry(95,"time","spring","primavera"));
        db.addEntry(new DatabaseEntry(96,"time","summer","verão"));
        db.addEntry(new DatabaseEntry(97,"time","tomorrow","amanhã"));
        db.addEntry(new DatabaseEntry(98,"time","winter","inverno"));
        db.addEntry(new DatabaseEntry(99,"time","year","ano"));
        db.addEntry(new DatabaseEntry(100,"greetings","good morning","bom dia"));
        db.addEntry(new DatabaseEntry(101,"greetings","good afternoon","boa tarde"));
        db.addEntry(new DatabaseEntry(102,"greetings","good evening","boa noite"));
        db.addEntry(new DatabaseEntry(103,"greetings","good night","boa noite"));
        db.addEntry(new DatabaseEntry(104,"greetings","thank you","obrigado"));
        db.addEntry(new DatabaseEntry(105,"greetings","you are welcome","de nada"));
        db.addEntry(new DatabaseEntry(106,"greetings","please","por favor"));
        db.addEntry(new DatabaseEntry(107,"greetings","good bye","adeus"));
        db.addEntry(new DatabaseEntry(108,"greetings","hello","oi"));
        db.addEntry(new DatabaseEntry(109,"food","bread","pão"));
        db.addEntry(new DatabaseEntry(110,"food","butter","manteiga"));
        db.addEntry(new DatabaseEntry(111,"food","cake","bolo"));
        db.addEntry(new DatabaseEntry(112,"food","cheese","queijo"));
        db.addEntry(new DatabaseEntry(113,"food","chocolate","chocolate"));
        db.addEntry(new DatabaseEntry(114,"food","egg","ovo"));
        db.addEntry(new DatabaseEntry(115,"food","food","comida"));
        db.addEntry(new DatabaseEntry(116,"food","fruit","fruto"));
        db.addEntry(new DatabaseEntry(117,"food","lunch","almoço"));
        db.addEntry(new DatabaseEntry(118,"food","meal","refeição"));
        db.addEntry(new DatabaseEntry(119,"food","meat","carne"));
        db.addEntry(new DatabaseEntry(120,"food","oil","óleo"));
        db.addEntry(new DatabaseEntry(121,"food","rice","arroz"));
        db.addEntry(new DatabaseEntry(122,"food","salad","salada"));
        db.addEntry(new DatabaseEntry(123,"food","salt","sal"));
        db.addEntry(new DatabaseEntry(124,"food","sandwich","sanduíche"));
        db.addEntry(new DatabaseEntry(125,"food","sauce","molho"));
        db.addEntry(new DatabaseEntry(126,"food","soup","sopa"));
        db.addEntry(new DatabaseEntry(127,"food","sugar","açúcar"));
        db.addEntry(new DatabaseEntry(128,"food","sweet","doçura"));
        db.addEntry(new DatabaseEntry(129,"drink","beer","cerveja"));
        db.addEntry(new DatabaseEntry(130,"drink","coffee","café"));
        db.addEntry(new DatabaseEntry(131,"drink","drink","bebida"));
        db.addEntry(new DatabaseEntry(132,"drink","milk","leite"));
        db.addEntry(new DatabaseEntry(133,"drink","tea","chá"));
        db.addEntry(new DatabaseEntry(134,"drink","water","água"));
        db.addEntry(new DatabaseEntry(135,"drink","wine","vinho"));
        db.addEntry(new DatabaseEntry(136,"family","baby","bebê"));
        db.addEntry(new DatabaseEntry(137,"family","birth","nascimento"));
        db.addEntry(new DatabaseEntry(138,"family","birthday","aniversário"));
        db.addEntry(new DatabaseEntry(139,"family","boy","menino"));
        db.addEntry(new DatabaseEntry(140,"family","brother","irmão"));
        db.addEntry(new DatabaseEntry(141,"family","child","criança"));
        db.addEntry(new DatabaseEntry(142,"family","daughter","filha"));
        db.addEntry(new DatabaseEntry(143,"family","death","morte"));
        db.addEntry(new DatabaseEntry(144,"family","family","família"));
        db.addEntry(new DatabaseEntry(145,"family","father","pai"));
        db.addEntry(new DatabaseEntry(146,"family","friend","amigo"));
        db.addEntry(new DatabaseEntry(147,"family","girl","menina"));
        db.addEntry(new DatabaseEntry(148,"family","grandfather","vovô"));
        db.addEntry(new DatabaseEntry(149,"family","human","homem"));
        db.addEntry(new DatabaseEntry(150,"family","husband","marido"));
        db.addEntry(new DatabaseEntry(151,"family","life","vida"));
        db.addEntry(new DatabaseEntry(152,"family","man","homem"));
        db.addEntry(new DatabaseEntry(153,"family","marriage","casamento"));
        db.addEntry(new DatabaseEntry(154,"family","mother","mãe"));
        db.addEntry(new DatabaseEntry(155,"family","neighbor","vizinho"));
        db.addEntry(new DatabaseEntry(156,"family","parent","pai"));
        db.addEntry(new DatabaseEntry(157,"family","person","pessoa"));
        db.addEntry(new DatabaseEntry(158,"family","relationship","relação"));
        db.addEntry(new DatabaseEntry(159,"family","sister","irmã"));
        db.addEntry(new DatabaseEntry(160,"family","son","filho"));
        db.addEntry(new DatabaseEntry(161,"family","wedding","casamento"));
        db.addEntry(new DatabaseEntry(162,"family","wife","esposa"));
        db.addEntry(new DatabaseEntry(163,"family","woman","mulher"));
        db.addEntry(new DatabaseEntry(164,"house","address","endereço"));
        db.addEntry(new DatabaseEntry(165,"house","bathroom","banheiro"));
        db.addEntry(new DatabaseEntry(166,"house","bedroom","quarto"));
        db.addEntry(new DatabaseEntry(167,"house","brick","tijolo"));
        db.addEntry(new DatabaseEntry(168,"house","door","porta"));
        db.addEntry(new DatabaseEntry(169,"house","floor","piso"));
        db.addEntry(new DatabaseEntry(170,"house","floor","andar"));
        db.addEntry(new DatabaseEntry(171,"house","garage","garagem"));
        db.addEntry(new DatabaseEntry(172,"house","garden","jardim"));
        db.addEntry(new DatabaseEntry(173,"house","gate","portão"));
        db.addEntry(new DatabaseEntry(174,"house","hall","vestíbulo"));
        db.addEntry(new DatabaseEntry(175,"house","home","lar"));
        db.addEntry(new DatabaseEntry(176,"house","house","casa"));
        db.addEntry(new DatabaseEntry(177,"house","kitchen","cozinha"));
        db.addEntry(new DatabaseEntry(178,"house","roof","teto"));
        db.addEntry(new DatabaseEntry(179,"house","room","quarto"));
        db.addEntry(new DatabaseEntry(180,"house","shower","chuveiro"));
        db.addEntry(new DatabaseEntry(181,"house","step","degrau"));
        db.addEntry(new DatabaseEntry(182,"house","toilet","privada"));
        db.addEntry(new DatabaseEntry(183,"house","wall","parede"));
        db.addEntry(new DatabaseEntry(184,"house","well","poço"));
        db.addEntry(new DatabaseEntry(185,"house","window","janela"));
        db.addEntry(new DatabaseEntry(186,"furniture","bed","cama"));
        db.addEntry(new DatabaseEntry(187,"furniture","carpet","tapete"));
        db.addEntry(new DatabaseEntry(188,"furniture","chair","cadeira"));
        db.addEntry(new DatabaseEntry(189,"furniture","cupboard","armário"));
        db.addEntry(new DatabaseEntry(190,"furniture","curtain","cortina"));
        db.addEntry(new DatabaseEntry(191,"furniture","desk","escrivaninha"));
        db.addEntry(new DatabaseEntry(192,"furniture","furniture","mobília"));
        db.addEntry(new DatabaseEntry(193,"furniture","seat","lugar"));
        db.addEntry(new DatabaseEntry(194,"furniture","shelf","prateleira"));
        db.addEntry(new DatabaseEntry(195,"furniture","sink","pia"));
        db.addEntry(new DatabaseEntry(196,"furniture","table","mesa"));
        db.addEntry(new DatabaseEntry(197,"clothes","bag","bolsa"));
        db.addEntry(new DatabaseEntry(198,"clothes","boot","bota"));
        db.addEntry(new DatabaseEntry(199,"clothes","cloth","pano"));
        db.addEntry(new DatabaseEntry(200,"clothes","clothing","roupa"));
        db.addEntry(new DatabaseEntry(201,"clothes","coat","casaco"));
        db.addEntry(new DatabaseEntry(202,"clothes","collar","colarinho"));
        db.addEntry(new DatabaseEntry(203,"clothes","hat","chapéu"));
        db.addEntry(new DatabaseEntry(204,"clothes","jacket","jaqueta"));
        db.addEntry(new DatabaseEntry(205,"clothes","jewel","jóia"));
        db.addEntry(new DatabaseEntry(206,"clothes","ring","anel"));
        db.addEntry(new DatabaseEntry(207,"clothes","shirt","camisa"));
        db.addEntry(new DatabaseEntry(208,"clothes","shoe","sapato"));
        db.addEntry(new DatabaseEntry(209,"clothes","skirt","saia"));
        db.addEntry(new DatabaseEntry(210,"clothes","suitcase","maleta"));
        db.addEntry(new DatabaseEntry(211,"clothes","tie","gravata"));
        db.addEntry(new DatabaseEntry(212,"transport","boat","barco"));
        db.addEntry(new DatabaseEntry(213,"transport","bus","ônibus"));
        db.addEntry(new DatabaseEntry(214,"transport","car","carro"));
        db.addEntry(new DatabaseEntry(215,"transport","curve","curva"));
        db.addEntry(new DatabaseEntry(216,"transport","flight","vôo"));
        db.addEntry(new DatabaseEntry(217,"transport","journey","viagem"));
        db.addEntry(new DatabaseEntry(218,"transport","passenger","passageiro"));
        db.addEntry(new DatabaseEntry(219,"transport","plane","avião"));
        db.addEntry(new DatabaseEntry(220,"transport","ship","navio"));
        db.addEntry(new DatabaseEntry(221,"transport","track","trilho"));
        db.addEntry(new DatabaseEntry(222,"transport","train","trem"));
        db.addEntry(new DatabaseEntry(223,"transport","transport","transporte"));
        db.addEntry(new DatabaseEntry(224,"profession","actor","ator"));
        db.addEntry(new DatabaseEntry(225,"profession","actress","atriz"));
        db.addEntry(new DatabaseEntry(226,"profession","businessman","comerciante"));
        db.addEntry(new DatabaseEntry(227,"profession","expert","especialista"));
        db.addEntry(new DatabaseEntry(228,"profession","general","general"));
        db.addEntry(new DatabaseEntry(229,"profession","journalist","jornalista"));
        db.addEntry(new DatabaseEntry(230,"profession","judge","juiz"));
        db.addEntry(new DatabaseEntry(231,"profession","leader","líder"));
        db.addEntry(new DatabaseEntry(232,"profession","minister","ministro"));
        db.addEntry(new DatabaseEntry(233,"profession","musician","músico"));
        db.addEntry(new DatabaseEntry(234,"profession","pilot","piloto"));
        db.addEntry(new DatabaseEntry(235,"profession","policeman","policial"));
        db.addEntry(new DatabaseEntry(236,"profession","politician","político"));
        db.addEntry(new DatabaseEntry(237,"profession","president","presidente"));
        db.addEntry(new DatabaseEntry(238,"profession","professional","profissional"));
        db.addEntry(new DatabaseEntry(239,"profession","representative","representante"));
        db.addEntry(new DatabaseEntry(240,"profession","sailor","marinheiro"));
        db.addEntry(new DatabaseEntry(241,"profession","scientist","cientista"));
        db.addEntry(new DatabaseEntry(242,"profession","secretary","secretário"));
        db.addEntry(new DatabaseEntry(243,"profession","servant","empregado"));
        db.addEntry(new DatabaseEntry(244,"profession","soldier","soldado"));
        db.addEntry(new DatabaseEntry(245,"profession","writer","escritor"));
        db.addEntry(new DatabaseEntry(246,"city","airport","aeroporto"));
        db.addEntry(new DatabaseEntry(247,"city","bank","banco"));
        db.addEntry(new DatabaseEntry(248,"city","bridge","ponte"));
        db.addEntry(new DatabaseEntry(249,"city","building","prédio"));
        db.addEntry(new DatabaseEntry(250,"city","capital","capital"));
        db.addEntry(new DatabaseEntry(251,"city","castle","castelo"));
        db.addEntry(new DatabaseEntry(252,"city","church","igreja"));
        db.addEntry(new DatabaseEntry(253,"city","cinema","cinema"));
        db.addEntry(new DatabaseEntry(254,"city","city","cidade"));
        db.addEntry(new DatabaseEntry(255,"city","factory","fábrica"));
        db.addEntry(new DatabaseEntry(256,"city","apartment","apartamento"));
        db.addEntry(new DatabaseEntry(257,"city","hospital","hospital"));
        db.addEntry(new DatabaseEntry(258,"city","hotel","hotel"));
        db.addEntry(new DatabaseEntry(259,"city","library","biblioteca"));
        db.addEntry(new DatabaseEntry(260,"city","park","parque"));
        db.addEntry(new DatabaseEntry(261,"city","prison","prisão"));
        db.addEntry(new DatabaseEntry(262,"city","restaurant","restaurante"));
        db.addEntry(new DatabaseEntry(263,"city","road","estrada"));
        db.addEntry(new DatabaseEntry(264,"city","school","escola"));
        db.addEntry(new DatabaseEntry(265,"city","station","estação"));
        db.addEntry(new DatabaseEntry(266,"city","street","rua"));
        db.addEntry(new DatabaseEntry(267,"city","structure","estrutura"));
        db.addEntry(new DatabaseEntry(268,"city","subway","metrô"));
        db.addEntry(new DatabaseEntry(269,"city","theater","teatro"));
        db.addEntry(new DatabaseEntry(270,"city","town","cidade"));
        db.addEntry(new DatabaseEntry(271,"city","university","universidade"));
        db.addEntry(new DatabaseEntry(272,"city","village","aldeia"));
        db.addEntry(new DatabaseEntry(273,"geography","bank","margem"));
        db.addEntry(new DatabaseEntry(274,"geography","beach","praia"));
        db.addEntry(new DatabaseEntry(275,"geography","current","curso"));
        db.addEntry(new DatabaseEntry(276,"geography","direction","direção"));
        db.addEntry(new DatabaseEntry(277,"geography","east","leste"));
        db.addEntry(new DatabaseEntry(278,"geography","height","altitude"));
        db.addEntry(new DatabaseEntry(279,"geography","island","ilha"));
        db.addEntry(new DatabaseEntry(280,"geography","lake","lago"));
        db.addEntry(new DatabaseEntry(281,"geography","land","terra"));
        db.addEntry(new DatabaseEntry(282,"geography","map","mapa"));
        db.addEntry(new DatabaseEntry(283,"geography","moon","lua"));
        db.addEntry(new DatabaseEntry(284,"geography","north","norte"));
        db.addEntry(new DatabaseEntry(285,"geography","region","região"));
        db.addEntry(new DatabaseEntry(286,"geography","river","rio"));
        db.addEntry(new DatabaseEntry(287,"geography","sea","mar"));
        db.addEntry(new DatabaseEntry(288,"geography","south","sul"));
        db.addEntry(new DatabaseEntry(289,"geography","space","espaço"));
        db.addEntry(new DatabaseEntry(290,"geography","star","estrela"));
        db.addEntry(new DatabaseEntry(291,"geography","west","oeste"));
        db.addEntry(new DatabaseEntry(292,"weather","sun","sol"));
        db.addEntry(new DatabaseEntry(293,"weather","cloud","nuvem"));
        db.addEntry(new DatabaseEntry(294,"weather","dew","orvalho"));
        db.addEntry(new DatabaseEntry(295,"weather","frost","geada"));
        db.addEntry(new DatabaseEntry(296,"weather","rain","chuva"));
        db.addEntry(new DatabaseEntry(297,"weather","rainbow","arco-íris"));
        db.addEntry(new DatabaseEntry(298,"weather","snow","neve"));
        db.addEntry(new DatabaseEntry(299,"weather","storm","tempestade"));
        db.addEntry(new DatabaseEntry(300,"weather","thunder","trovão"));
        db.addEntry(new DatabaseEntry(301,"weather","wind","vento"));
        db.addEntry(new DatabaseEntry(302,"weather","sunny","ensolarado"));

    }



}




package com.example.tom.moble;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MenuActivity extends AppCompatActivity {
    int page = 0;
    int firstLaunch;
    DatabaseHandler db;
    AlarmReceiver alarm;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    Button MenuInfo;
    Button MenuEntryTest;
    Button MenuFinalTest;
    Button MenuSettings;
    SharedPreferences sharedPref;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);
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
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        firstLaunch = sharedPref.getInt("First Launch", 0);
        if(firstLaunch == 0) {
            Random rgen = new Random();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("First Launch", 1);
            editor.putInt("NotificationCounter", 0);
            editor.commit();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    populateDataBase();
                }
            });
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.menu);
            MenuInfo = (Button) findViewById(R.id.MenuInfo);
            MenuEntryTest = (Button) findViewById(R.id.MenuEntryTest);
            MenuFinalTest = (Button) findViewById(R.id.MenuFinalTest);
            MenuSettings = (Button) findViewById(R.id.MenuSettings);
            MenuInfo.setBackgroundColor(Color.parseColor("#6AB344"));
            MenuEntryTest.setBackgroundColor(Color.parseColor("#6AB344"));
            MenuFinalTest.setBackgroundColor(Color.parseColor("#6AB344"));
            MenuSettings.setBackgroundColor(Color.parseColor("#6AB344"));
        }



    }

    public void onResume(){
        super.onResume();

        firstLaunch = sharedPref.getInt("First Launch", 0);
        if(firstLaunch == 1) {
            setContentView(R.layout.menu);
            MenuInfo = (Button) findViewById(R.id.MenuInfo);
            MenuEntryTest = (Button) findViewById(R.id.MenuEntryTest);
            MenuFinalTest = (Button) findViewById(R.id.MenuFinalTest);
            MenuSettings = (Button) findViewById(R.id.MenuSettings);
            MenuInfo.setBackgroundColor(Color.parseColor("#6AB344"));
            MenuEntryTest.setBackgroundColor(Color.parseColor("#6AB344"));
            MenuFinalTest.setBackgroundColor(Color.parseColor("#6AB344"));
            MenuSettings.setBackgroundColor(Color.parseColor("#6AB344"));
            alarm = new AlarmReceiver();
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

    public void menuButtonClick(View view){
        setContentView(R.layout.menu);
        page = 0;
    }

    public Boolean finalTestAvailable() throws ParseException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String finalTestDayString = sharedPreferences.getString("Final Test Date", null);
//        System.out.println("final test available?");
//        System.out.println(finalTestDayString);
        if( finalTestDayString!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
            Date strDate = sdf.parse(finalTestDayString);
//            System.out.println(strDate + "  " +new Date().toString());
            return (new Date().after(strDate));
        }
        else{return false;}
    }
    public void finalTestButtonClick(View view) throws ParseException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean finalTestDone = sharedPreferences.getBoolean("Final Test Done", false);
        if (finalTestAvailable()==true && finalTestDone == false){
            // final test ready
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
        }else if(finalTestAvailable()==true && finalTestDone == true){
            Toast.makeText(this, "Final test already taken",
                    Toast.LENGTH_LONG).show();
        }
        else if (finalTestAvailable() == false) {


            if (sharedPreferences.getString("Final Test Date", null) == null) {
                Toast.makeText(this, "Please finish the entry test first", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(this, "Final test available at " + sharedPreferences.getString("Final Test Date", null), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void infoButtonClick(View view){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }



    public void settingsButtonClick(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void entryTestButtonClick(View view){


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if( sharedPreferences.contains("Final Test Date")){
            Toast.makeText(this, "Already completed entry test",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
        }
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().remove("Final Test Date").commit();
        sharedPreferences.edit().remove("Final Test Done").commit();
    }



    public void deleteHistory(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Final Test Date");
        editor.commit();
    }

    public void populateDataBase(){
        db.addEntry(new DatabaseEntry(0,"house","address","endereço", null, null, null));
        db.addEntry(new DatabaseEntry(1,"house","bathroom","banheiro", null, null, null));
        db.addEntry(new DatabaseEntry(2,"house","bedroom","quarto", null, null, null));
        db.addEntry(new DatabaseEntry(3,"house","brick","tijolo", null, null, null));
        db.addEntry(new DatabaseEntry(4,"house","door","porta", null, null, null));
        db.addEntry(new DatabaseEntry(5,"house","floor","piso", null, null, null));
        db.addEntry(new DatabaseEntry(6,"home","floor","andar", null, null, null));
        db.addEntry(new DatabaseEntry(7,"home","garage","garagem", null, null, null));
        db.addEntry(new DatabaseEntry(8,"home","garden","jardim", null, null, null));
        db.addEntry(new DatabaseEntry(9,"home","gate","portão", null, null, null));
        db.addEntry(new DatabaseEntry(10,"home","hall","vestíbulo", null, null, null));
        db.addEntry(new DatabaseEntry(11,"home","home","lar", null, null, null));
        db.addEntry(new DatabaseEntry(12,"home","house","casa", null, null, null));
        db.addEntry(new DatabaseEntry(13,"home","roof","teto", null, null, null));
        db.addEntry(new DatabaseEntry(14,"home","room","quarto", null, null, null));
        db.addEntry(new DatabaseEntry(15,"home","shower","chuveiro", null, null, null));
        db.addEntry(new DatabaseEntry(16,"home","step","degrau", null, null, null));
        db.addEntry(new DatabaseEntry(17,"home","toilet","privada", null, null, null));
        db.addEntry(new DatabaseEntry(18,"home","wall","parede", null, null, null));
        db.addEntry(new DatabaseEntry(19,"home","well","poço", null, null, null));
        db.addEntry(new DatabaseEntry(20,"home","window","janela", null, null, null));
        db.addEntry(new DatabaseEntry(21,"home","bed","cama", null, null, null));
        db.addEntry(new DatabaseEntry(22,"home","carpet","tapete", null, null, null));
        db.addEntry(new DatabaseEntry(23,"home","chair","cadeira", null, null, null));
        db.addEntry(new DatabaseEntry(24,"home","cupboard","armário", null, null, null));
        db.addEntry(new DatabaseEntry(25,"home","curtain","cortina", null, null, null));
        db.addEntry(new DatabaseEntry(26,"home","desk","escrivaninha", null, null, null));
        db.addEntry(new DatabaseEntry(27,"home","furniture","mobília", null, null, null));
        db.addEntry(new DatabaseEntry(28,"home","seat","lugar", null, null, null));
        db.addEntry(new DatabaseEntry(29,"home","shelf","prateleira", null, null, null));
        db.addEntry(new DatabaseEntry(30,"home","sink","pia", null, null, null));
        db.addEntry(new DatabaseEntry(31,"home","table","mesa", null, null, null));
        db.addEntry(new DatabaseEntry(32,"home","apartment","apartamento", null, null, null));
        db.addEntry(new DatabaseEntry(33,"public transport","airport","aeroporto", null, null, null));
        db.addEntry(new DatabaseEntry(34,"public transport","station","estação", null, null, null));
        db.addEntry(new DatabaseEntry(35,"public transport","street","rua", null, null, null));
        db.addEntry(new DatabaseEntry(36,"public transport","subway","metrô", null, null, null));
        db.addEntry(new DatabaseEntry(37,"public transport","boat","barco", null, null, null));
        db.addEntry(new DatabaseEntry(38,"public transport","bus","ônibus", null, null, null));
        db.addEntry(new DatabaseEntry(39,"public transport","car","carro", null, null, null));
        db.addEntry(new DatabaseEntry(40,"public transport","flight","vôo", null, null, null));
        db.addEntry(new DatabaseEntry(41,"public transport","journey","viagem", null, null, null));
        db.addEntry(new DatabaseEntry(42,"public transport","passenger","passageiro", null, null, null));
        db.addEntry(new DatabaseEntry(43,"public transport","plane","avião", null, null, null));
        db.addEntry(new DatabaseEntry(44,"public transport","ship","navio", null, null, null));
        db.addEntry(new DatabaseEntry(45,"public transport","track","trilho", null, null, null));
        db.addEntry(new DatabaseEntry(46,"public transport","train","trem", null, null, null));
        db.addEntry(new DatabaseEntry(47,"public transport","bicycle","bicicleta", null, null, null));
        db.addEntry(new DatabaseEntry(48,"public transport","motorcycle","motocicleta", null, null, null));
        db.addEntry(new DatabaseEntry(49,"public transport","scooter","lambreta", null, null, null));
        db.addEntry(new DatabaseEntry(50,"public transport","subway","metrô", null, null, null));
        db.addEntry(new DatabaseEntry(51,"public transport","taxi","Táxi", null, null, null));
        db.addEntry(new DatabaseEntry(52,"public transport","truck","caminhão", null, null, null));
        db.addEntry(new DatabaseEntry(53,"public transport","transport","transporte", null, null, null));
        db.addEntry(new DatabaseEntry(54,"public transport","bus driver","motorista de ônibus", null, null, null));
        db.addEntry(new DatabaseEntry(55,"public transport","bus fare","tarifa de onibus", null, null, null));
        db.addEntry(new DatabaseEntry(56,"public transport","bus stop","ponto de ônibus", null, null, null));
        db.addEntry(new DatabaseEntry(57,"public transport","bus lane","faixa de ônibus", null, null, null));
        db.addEntry(new DatabaseEntry(58,"public transport","bus station","estação de ônibus", null, null, null));
        db.addEntry(new DatabaseEntry(59,"public transport","double-decker bus","ônibus de dois andares", null, null, null));
        db.addEntry(new DatabaseEntry(60,"public transport","conductor","condutor", null, null, null));
        db.addEntry(new DatabaseEntry(61,"public transport","luggage hold","porão de bagagens", null, null, null));
        db.addEntry(new DatabaseEntry(62,"public transport","next stop","próxima parada", null, null, null));
        db.addEntry(new DatabaseEntry(63,"public transport","night bus","ônibus noturno", null, null, null));
        db.addEntry(new DatabaseEntry(64,"public transport","request stop","requisitar parada", null, null, null));
        db.addEntry(new DatabaseEntry(65,"public transport","route","rota", null, null, null));
        db.addEntry(new DatabaseEntry(66,"public transport","derailment","descarrilhamento", null, null, null));
        db.addEntry(new DatabaseEntry(67,"public transport","express train","trem expresso", null, null, null));
        db.addEntry(new DatabaseEntry(68,"public transport","guard","guarda", null, null, null));
        db.addEntry(new DatabaseEntry(69,"public transport","railway line","linha ferroviária", null, null, null));
        db.addEntry(new DatabaseEntry(70,"public transport","restaurant car","vagão-restaurante", null, null, null));
        db.addEntry(new DatabaseEntry(71,"public transport","season ticket","bilhete de temporada", null, null, null));
        db.addEntry(new DatabaseEntry(72,"public transport","station","estação", null, null, null));
        db.addEntry(new DatabaseEntry(73,"public transport","stopping service","serviço de parada", null, null, null));
        db.addEntry(new DatabaseEntry(74,"public transport","track","pista", null, null, null));
        db.addEntry(new DatabaseEntry(75,"public transport","train crash","colisão de trem", null, null, null));
        db.addEntry(new DatabaseEntry(76,"public transport","train driver","maquinista", null, null, null));
        db.addEntry(new DatabaseEntry(77,"public transport","train fare","tarifa do trem", null, null, null));
        db.addEntry(new DatabaseEntry(78,"public transport","train journey","viagem de trem", null, null, null));
        db.addEntry(new DatabaseEntry(79,"public transport","travelcard","cartão de viagem", null, null, null));
        db.addEntry(new DatabaseEntry(80,"public transport","underground station","estação subterrânea", null, null, null));
        db.addEntry(new DatabaseEntry(81,"supermarket","bread","pão", null, null, null));
        db.addEntry(new DatabaseEntry(82,"supermarket","butter","manteiga", null, null, null));
        db.addEntry(new DatabaseEntry(83,"supermarket","cake","bolo", null, null, null));
        db.addEntry(new DatabaseEntry(84,"supermarket","cheese","queijo", null, null, null));
        db.addEntry(new DatabaseEntry(85,"supermarket","chocolate","chocolate", null, null, null));
        db.addEntry(new DatabaseEntry(86,"supermarket","egg","ovo", null, null, null));
        db.addEntry(new DatabaseEntry(87,"supermarket","food","comida", null, null, null));
        db.addEntry(new DatabaseEntry(88,"supermarket","fruit","fruto", null, null, null));
        db.addEntry(new DatabaseEntry(89,"supermarket","lunch","almoço", null, null, null));
        db.addEntry(new DatabaseEntry(90,"supermarket","meal","refeição", null, null, null));
        db.addEntry(new DatabaseEntry(91,"supermarket","meat","carne", null, null, null));
        db.addEntry(new DatabaseEntry(92,"supermarket","oil","óleo", null, null, null));
        db.addEntry(new DatabaseEntry(93,"supermarket","rice","arroz", null, null, null));
        db.addEntry(new DatabaseEntry(94,"supermarket","salad","salada", null, null, null));
        db.addEntry(new DatabaseEntry(95,"supermarket","salt","sal", null, null, null));
        db.addEntry(new DatabaseEntry(96,"supermarket","sandwich","sanduíche", null, null, null));
        db.addEntry(new DatabaseEntry(97,"supermarket","sauce","molho", null, null, null));
        db.addEntry(new DatabaseEntry(98,"supermarket","soup","sopa", null, null, null));
        db.addEntry(new DatabaseEntry(99,"supermarket","sugar","açúcar", null, null, null));
        db.addEntry(new DatabaseEntry(100,"supermarket","sweet","doçura", null, null, null));
        db.addEntry(new DatabaseEntry(101,"supermarket","beer","cerveja", null, null, null));
        db.addEntry(new DatabaseEntry(102,"supermarket","coffee","café", null, null, null));
        db.addEntry(new DatabaseEntry(103,"supermarket","drink","bebida", null, null, null));
        db.addEntry(new DatabaseEntry(104,"supermarket","milk","leite", null, null, null));
        db.addEntry(new DatabaseEntry(105,"supermarket","tea","chá", null, null, null));
        db.addEntry(new DatabaseEntry(106,"supermarket","water","água", null, null, null));
        db.addEntry(new DatabaseEntry(107,"supermarket","wine","vinho", null, null, null));
        db.addEntry(new DatabaseEntry(108,"supermarket","electronics","eletrônicos", null, null, null));
        db.addEntry(new DatabaseEntry(109,"supermarket","household goods","artigos domésticos", null, null, null));
        db.addEntry(new DatabaseEntry(110,"supermarket","sporting goods","artigos esportivos", null, null, null));
        db.addEntry(new DatabaseEntry(111,"supermarket","beverages","bebidas", null, null, null));
        db.addEntry(new DatabaseEntry(112,"supermarket","fruit","fruta", null, null, null));
        db.addEntry(new DatabaseEntry(113,"supermarket","vegetables","vegetais", null, null, null));
        db.addEntry(new DatabaseEntry(114,"supermarket","fish","peixe", null, null, null));
        db.addEntry(new DatabaseEntry(115,"supermarket","meat","carne", null, null, null));
        db.addEntry(new DatabaseEntry(116,"supermarket","refundable","reembolsável", null, null, null));
        db.addEntry(new DatabaseEntry(117,"supermarket","return policy","política de devolução", null, null, null));
        db.addEntry(new DatabaseEntry(118,"supermarket","bakery","padaria", null, null, null));
        db.addEntry(new DatabaseEntry(119,"supermarket","frozen food","comida congelada", null, null, null));
        db.addEntry(new DatabaseEntry(120,"supermarket","dairy products","produtos diários", null, null, null));
        db.addEntry(new DatabaseEntry(121,"supermarket","canned goods","enlatados", null, null, null));
        db.addEntry(new DatabaseEntry(122,"supermarket","pet supplies","suprimentos para animais de estimação", null, null, null));
        db.addEntry(new DatabaseEntry(123,"supermarket","cosmetics","cosméticos", null, null, null));
        db.addEntry(new DatabaseEntry(124,"supermarket","guarantee","garantia", null, null, null));
        db.addEntry(new DatabaseEntry(125,"supermarket","credit card","cartão de crédito", null, null, null));
        db.addEntry(new DatabaseEntry(126,"supermarket","debit card","cartão de débito", null, null, null));
        db.addEntry(new DatabaseEntry(127,"supermarket","cash","dinheiro", null, null, null));
        db.addEntry(new DatabaseEntry(128,"supermarket","coin","moeda", null, null, null));
        db.addEntry(new DatabaseEntry(129,"supermarket","discount coupon","cupom de desconto", null, null, null));
        db.addEntry(new DatabaseEntry(130,"supermarket","change","troco", null, null, null));
        db.addEntry(new DatabaseEntry(131,"supermarket","sales tax","imposto sobre vendas", null, null, null));
        db.addEntry(new DatabaseEntry(132,"supermarket","purchase","compra", null, null, null));
        db.addEntry(new DatabaseEntry(133,"supermarket","ticket","bilhete", null, null, null));
        db.addEntry(new DatabaseEntry(134,"supermarket","receipt","recibo", null, null, null));
        db.addEntry(new DatabaseEntry(135,"supermarket","price","preço", null, null, null));
        db.addEntry(new DatabaseEntry(136,"supermarket","half price","metade do preço", null, null, null));
        db.addEntry(new DatabaseEntry(137,"supermarket","escalator","escada rolante", null, null, null));
        db.addEntry(new DatabaseEntry(138,"supermarket","warehouse","armazém", null, null, null));
        db.addEntry(new DatabaseEntry(139,"supermarket","customer","cliente", null, null, null));
        db.addEntry(new DatabaseEntry(140,"supermarket","supervisor","supervisor", null, null, null));
        db.addEntry(new DatabaseEntry(141,"supermarket","manager","gerente", null, null, null));
        db.addEntry(new DatabaseEntry(142,"supermarket","sales assistant","assistente de vendas", null, null, null));
        db.addEntry(new DatabaseEntry(143,"supermarket","barcode reader","leitor de código de barras", null, null, null));
        db.addEntry(new DatabaseEntry(144,"supermarket","market","mercado", null, null, null));
        db.addEntry(new DatabaseEntry(145,"supermarket","supermarket","supermercado", null, null, null));
        db.addEntry(new DatabaseEntry(146,"supermarket","shopping cart","carrinho de compras", null, null, null));
        db.addEntry(new DatabaseEntry(147,"supermarket","basket","cesta", null, null, null));
        db.addEntry(new DatabaseEntry(148,"supermarket","bag","sacola", null, null, null));
        db.addEntry(new DatabaseEntry(149,"supermarket","freezer","congelador", null, null, null));
        db.addEntry(new DatabaseEntry(150,"supermarket","fridge","geladeira", null, null, null));
        db.addEntry(new DatabaseEntry(151,"supermarket","aisle","corredor", null, null, null));
        db.addEntry(new DatabaseEntry(152,"supermarket","shelf","estante", null, null, null));
        db.addEntry(new DatabaseEntry(153,"supermarket","product","produto", null, null, null));
        db.addEntry(new DatabaseEntry(154,"supermarket","packaging","embalagem", null, null, null));
        db.addEntry(new DatabaseEntry(155,"supermarket","barcode","código de barras", null, null, null));
        db.addEntry(new DatabaseEntry(156,"supermarket","nutritional information","informação nutricional", null, null, null));
        db.addEntry(new DatabaseEntry(157,"library","library","biblioteca", null, null, null));
        db.addEntry(new DatabaseEntry(158,"library","school","escola", null, null, null));
        db.addEntry(new DatabaseEntry(159,"library","university","universidade", null, null, null));
        db.addEntry(new DatabaseEntry(160,"library","master's degree","mestrado", null, null, null));
        db.addEntry(new DatabaseEntry(161,"library","bachelor's degree","bacharelado", null, null, null));
        db.addEntry(new DatabaseEntry(162,"library","course","curso", null, null, null));
        db.addEntry(new DatabaseEntry(163,"library","credit","crédito", null, null, null));
        db.addEntry(new DatabaseEntry(164,"library","degree","grau", null, null, null));
        db.addEntry(new DatabaseEntry(165,"library","dorm","dormitório", null, null, null));
        db.addEntry(new DatabaseEntry(166,"library","enroll","inscrever", null, null, null));
        db.addEntry(new DatabaseEntry(167,"library","exam","exame", null, null, null));
        db.addEntry(new DatabaseEntry(168,"library","faculty","faculdade", null, null, null));
        db.addEntry(new DatabaseEntry(169,"library","fail","falhou", null, null, null));
        db.addEntry(new DatabaseEntry(170,"library","financial aid","ajuda financeira", null, null, null));
        db.addEntry(new DatabaseEntry(171,"library","fraternity","fraternidade", null, null, null));
        db.addEntry(new DatabaseEntry(172,"library","graduate","graduado", null, null, null));
        db.addEntry(new DatabaseEntry(173,"library","instructor","instrutor", null, null, null));
        db.addEntry(new DatabaseEntry(174,"library","lecture","aula", null, null, null));
        db.addEntry(new DatabaseEntry(175,"library","master's degree","mestrado", null, null, null));
        db.addEntry(new DatabaseEntry(176,"library","matriculate","matricular", null, null, null));
        db.addEntry(new DatabaseEntry(177,"library","notebook","caderno", null, null, null));
        db.addEntry(new DatabaseEntry(178,"library","notes","notas", null, null, null));
        db.addEntry(new DatabaseEntry(179,"library","pass","passar", null, null, null));
        db.addEntry(new DatabaseEntry(180,"library","PhD","PhD", null, null, null));
        db.addEntry(new DatabaseEntry(181,"library","postgraduate","pós-graduado", null, null, null));
        db.addEntry(new DatabaseEntry(182,"library","prerequisite","pré-requisito", null, null, null));
        db.addEntry(new DatabaseEntry(183,"library","teacher","professor", null, null, null));
        db.addEntry(new DatabaseEntry(184,"library","quiz","questionário", null, null, null));
        db.addEntry(new DatabaseEntry(185,"library","register","registrar", null, null, null));
        db.addEntry(new DatabaseEntry(186,"library","research","pesquisa", null, null, null));
        db.addEntry(new DatabaseEntry(187,"library","roommate","colega de quarto", null, null, null));
        db.addEntry(new DatabaseEntry(188,"library","semester","semestre", null, null, null));
        db.addEntry(new DatabaseEntry(189,"library","spring break","férias de primavera", null, null, null));
        db.addEntry(new DatabaseEntry(190,"library","textbook","livro didático", null, null, null));
        db.addEntry(new DatabaseEntry(191,"library","transcript","histórico escolar", null, null, null));
        db.addEntry(new DatabaseEntry(192,"library","trimester","trimestre", null, null, null));
        db.addEntry(new DatabaseEntry(193,"library","university","universidade", null, null, null));
        db.addEntry(new DatabaseEntry(194,"morning","good morning","bom dia", null, null, null));
        db.addEntry(new DatabaseEntry(195,"morning","morning","manhã", null, null, null));
        db.addEntry(new DatabaseEntry(196,"afternoon","good afternoon","boa tarde", null, null, null));
        db.addEntry(new DatabaseEntry(197,"afternoon","afternoon","tarde", null, null, null));
        db.addEntry(new DatabaseEntry(198,"evening","good evening","boa noite", null, null, null));
        db.addEntry(new DatabaseEntry(199,"evening","good night","boa noite", null, null, null));
        db.addEntry(new DatabaseEntry(200,"evening","evening","noite", null, null, null));
        db.addEntry(new DatabaseEntry(201,"evening","midnight","meia-noite", null, null, null));
        db.addEntry(new DatabaseEntry(202,"evening","night","noite", null, null, null));
        db.addEntry(new DatabaseEntry(203,"evening","bedtime","hora de dormir", null, null, null));
        db.addEntry(new DatabaseEntry(204,"dinnertime","restaurant","restaurante", null, null, null));
        db.addEntry(new DatabaseEntry(205,"dinnertime","kitchen","cozinha", null, null, null));
    }
}




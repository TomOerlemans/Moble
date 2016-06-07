package com.example.tom.moble;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.util.Date;
import java.util.Random;

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
        db = new DatabaseHandler(this);
        if(firstLaunch == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("First Launch", 1);
            editor.commit();
            setContentView(R.layout.startscreen1);
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

       // firebaseStuff();


    }
    public void firebaseStuff(){

        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://moble.firebaseio.com/");
        String id = android.os.Build.SERIAL;
        String categoryCSV = "";
        String englishCSV = "";
        String portugueseCSV = "";
        String entrytestCSV = "";
        String finaltestCSV = "";

        Random rgen = new Random();

        Firebase user = myFirebaseRef.child(Integer.toString(rgen.nextInt(1000)));

        for (int i = 1; i < db.getEntryCount() - 1; i++){
            categoryCSV = db.getEntry(i).getCategory() + " , " + categoryCSV;
            englishCSV = db.getEntry(i).getEnglish() + " , " + englishCSV;
            portugueseCSV = db.getEntry(i).getPortuguese() + " , " + portugueseCSV;
            entrytestCSV = db.getEntry(i).getEntryTest() + " , " + entrytestCSV;
            finaltestCSV = db.getEntry(i).getFinalTest() + " , " + finaltestCSV;
        }

        user.child("Category").setValue(categoryCSV);
        user.child("English").setValue(englishCSV);
        user.child("Portuguese").setValue(portugueseCSV);
        user.child("Entry Test").setValue(entrytestCSV);
        user.child("Final Test").setValue(finaltestCSV);
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
//        System.out.println("final test button onclick");
//        System.out.println(finalTestDone);
//        System.out.println(finalTestAvailable());
//        System.out.println("___________________________________------_____----__");
        if (finalTestAvailable()==true && finalTestDone == false){
            // final test ready
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
        }else if(finalTestAvailable()==true && finalTestDone == true){
            Toast.makeText(this, "final test already taken",
                    Toast.LENGTH_LONG).show();
        }
        else{
            System.out.println("err");
            // if  sharedPreferences.getString("Final Test Done", null) == null: please do entry test first otherwise: "final test avail at:sharedPreferences.getString("Final Test Done", null)"
//            if(sharedPreferences.getString("Final Test Done",null)==null){
//                Toast.makeText(this, "You need to complete the Entry Test first",
//                        Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(this, "final test will be available at " + sharedPreferences.getString("Final Test Done", null),
//                        Toast.LENGTH_LONG).show();
//            }
        }
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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        System.out.println("entry test click");
//        System.out.println(sharedPreferences.toString());
//        System.out.println("===========================-----=-=-=-=-=-=-=-=-=");
//        System.out.println(sharedPreferences.getString("Final Test Date",null));
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
//        DatabaseEntry dbe = new DatabaseEntry("city","station","estação",null,null);
//        dbe.getFinalTest();
//        db.addEntry(dbe);
//        Toast.makeText(this, dbe.getEntryTest() + " |  " +dbe.getFinalTest() + " | " + dbe.getEnglish()+ " || " +String.valueOf(db.getAllEntries().size()),
//                Toast.LENGTH_LONG).show();



    }


    public void stopNotifications(View view){
        alarm.cancelAlarm(this);
        Toast.makeText(this, "stopped notifications",
                Toast.LENGTH_LONG).show();
//        deleteHistory();
//        DatabaseEntry dbe = new DatabaseEntry(265,"city","station","estação", null, null);
//        System.out.println(dbe.getClass().toString());
//        System.out.println("++++++++++++++++++     +++++++++  ++++++++++++  +++++++++++           ++++++++");
//
//        db.addEntry(new DatabaseEntry(0,"color","black","preto", null, null));
    }



    public void deleteHistory(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Final Test Date");
        editor.commit();
    }

    public void populateDataBase(){
        db.addEntry(new DatabaseEntry(0,"color","black","preto", null, null));
        db.addEntry(new DatabaseEntry(5,"color","orange","laranja", null, null));
        db.addEntry(new DatabaseEntry(6,"color","pink","cor-de-rosa", null, null));
        db.addEntry(new DatabaseEntry(7,"color","red","vermelho", null, null));
        db.addEntry(new DatabaseEntry(8,"color","violet","roxo", null, null));
        db.addEntry(new DatabaseEntry(2,"color","brown","marrom", null, null));
        db.addEntry(new DatabaseEntry(1,"color","blue","azul", null, null));
        db.addEntry(new DatabaseEntry(4,"color","green","verde", null, null));
        db.addEntry(new DatabaseEntry(3,"color","gray","cinza", null, null));
        db.addEntry(new DatabaseEntry(9,"color","white","branco", null, null));
        db.addEntry(new DatabaseEntry(10,"color","yellow","amarelo", null, null));
        db.addEntry(new DatabaseEntry(11,"anatomy","arm","braço", null, null));
        db.addEntry(new DatabaseEntry(12,"anatomy","back","costas", null, null));
        db.addEntry(new DatabaseEntry(13,"anatomy","beak","bico", null, null));
        db.addEntry(new DatabaseEntry(14,"anatomy","blood","sangue", null, null));
        db.addEntry(new DatabaseEntry(15,"anatomy","body","corpo", null, null));
        db.addEntry(new DatabaseEntry(16,"anatomy","bone","osso", null, null));
        db.addEntry(new DatabaseEntry(17,"anatomy","brain","cérebro", null, null));
        db.addEntry(new DatabaseEntry(18,"anatomy","breath","bafo", null, null));
        db.addEntry(new DatabaseEntry(19,"anatomy","cell","cela", null, null));
        db.addEntry(new DatabaseEntry(20,"anatomy","chest","peito", null, null));
        db.addEntry(new DatabaseEntry(21,"anatomy","chest","peito", null, null));
        db.addEntry(new DatabaseEntry(22,"anatomy","chin","queixo", null, null));
        db.addEntry(new DatabaseEntry(23,"anatomy","ear","orelha", null, null));
        db.addEntry(new DatabaseEntry(24,"anatomy","eye","olho", null, null));
        db.addEntry(new DatabaseEntry(25,"anatomy","face","rosto", null, null));
        db.addEntry(new DatabaseEntry(26,"anatomy","finger","dedo", null, null));
        db.addEntry(new DatabaseEntry(27,"anatomy","foot","pé", null, null));
        db.addEntry(new DatabaseEntry(28,"anatomy","forehead","testa", null, null));
        db.addEntry(new DatabaseEntry(29,"anatomy","gene","gene", null, null));
        db.addEntry(new DatabaseEntry(30,"anatomy","hair","cabelo", null, null));
        db.addEntry(new DatabaseEntry(31,"anatomy","hair","cabelo", null, null));
        db.addEntry(new DatabaseEntry(32,"anatomy","hand","mão", null, null));
        db.addEntry(new DatabaseEntry(33,"anatomy","head","cabeça", null, null));
        db.addEntry(new DatabaseEntry(34,"anatomy","heart","coração", null, null));
        db.addEntry(new DatabaseEntry(35,"anatomy","knee","joelho", null, null));
        db.addEntry(new DatabaseEntry(36,"anatomy","leg","perna", null, null));
        db.addEntry(new DatabaseEntry(37,"anatomy","lip","lábio", null, null));
        db.addEntry(new DatabaseEntry(38,"anatomy","mouth","boca", null, null));
        db.addEntry(new DatabaseEntry(39,"anatomy","muscle","músculo", null, null));
        db.addEntry(new DatabaseEntry(40,"anatomy","nail","unha", null, null));
        db.addEntry(new DatabaseEntry(41,"anatomy","neck","pescoço", null, null));
        db.addEntry(new DatabaseEntry(42,"anatomy","nerve","nervo", null, null));
        db.addEntry(new DatabaseEntry(43,"anatomy","nose","nariz", null, null));
        db.addEntry(new DatabaseEntry(44,"anatomy","skin","pele", null, null));
        db.addEntry(new DatabaseEntry(45,"anatomy","stomach","estômago", null, null));
        db.addEntry(new DatabaseEntry(46,"anatomy","tail","cauda", null, null));
        db.addEntry(new DatabaseEntry(47,"anatomy","throat","garganta", null, null));
        db.addEntry(new DatabaseEntry(48,"anatomy","thumb","polegar", null, null));
        db.addEntry(new DatabaseEntry(49,"anatomy","toe","dedão", null, null));
        db.addEntry(new DatabaseEntry(50,"anatomy","tongue","língua", null, null));
        db.addEntry(new DatabaseEntry(51,"anatomy","tooth","dente", null, null));
        db.addEntry(new DatabaseEntry(52,"anatomy","wing","asa", null, null));
        db.addEntry(new DatabaseEntry(53,"animals","ant","formiga", null, null));
        db.addEntry(new DatabaseEntry(54,"animals","bear","urso", null, null));
        db.addEntry(new DatabaseEntry(55,"animals","bee","abelha", null, null));
        db.addEntry(new DatabaseEntry(56,"animals","bird","ave", null, null));
        db.addEntry(new DatabaseEntry(57,"animals","cat","gato", null, null));
        db.addEntry(new DatabaseEntry(58,"animals","cow","vaca", null, null));
        db.addEntry(new DatabaseEntry(59,"animals","dog","cachorro", null, null));
        db.addEntry(new DatabaseEntry(60,"animals","elephant","elefante", null, null));
        db.addEntry(new DatabaseEntry(61,"animals","fish","peixe", null, null));
        db.addEntry(new DatabaseEntry(62,"animals","fly","mosca", null, null));
        db.addEntry(new DatabaseEntry(63,"animals","goat","cabra", null, null));
        db.addEntry(new DatabaseEntry(64,"animals","horse","cavalo", null, null));
        db.addEntry(new DatabaseEntry(65,"animals","insect","inseto", null, null));
        db.addEntry(new DatabaseEntry(66,"animals","lion","leão", null, null));
        db.addEntry(new DatabaseEntry(67,"animals","monkey","macaco", null, null));
        db.addEntry(new DatabaseEntry(68,"animals","pig","porco", null, null));
        db.addEntry(new DatabaseEntry(69,"animals","sheep","ovelha", null, null));
        db.addEntry(new DatabaseEntry(70,"animals","snake","serpente", null, null));
        db.addEntry(new DatabaseEntry(71,"time","afternoon","tarde", null, null));
        db.addEntry(new DatabaseEntry(72,"time","age","idade", null, null));
        db.addEntry(new DatabaseEntry(73,"time","autumn","outono", null, null));
        db.addEntry(new DatabaseEntry(74,"time","beginning","começo", null, null));
        db.addEntry(new DatabaseEntry(75,"time","century","século", null, null));
        db.addEntry(new DatabaseEntry(76,"time","date","data", null, null));
        db.addEntry(new DatabaseEntry(77,"time","day","dia", null, null));
        db.addEntry(new DatabaseEntry(78,"time","evening","noite", null, null));
        db.addEntry(new DatabaseEntry(79,"time","future","futuro", null, null));
        db.addEntry(new DatabaseEntry(80,"time","hour","hora", null, null));
        db.addEntry(new DatabaseEntry(81,"time","midnight","meia-noite", null, null));
        db.addEntry(new DatabaseEntry(82,"time","minute","minuto", null, null));
        db.addEntry(new DatabaseEntry(83,"time","moment","momento", null, null));
        db.addEntry(new DatabaseEntry(84,"time","moment","instante", null, null));
        db.addEntry(new DatabaseEntry(85,"time","month","mês", null, null));
        db.addEntry(new DatabaseEntry(86,"time","morning","manhã", null, null));
        db.addEntry(new DatabaseEntry(87,"time","night","noite", null, null));
        db.addEntry(new DatabaseEntry(88,"time","now","agora", null, null));
        db.addEntry(new DatabaseEntry(89,"time","past","passado", null, null));
        db.addEntry(new DatabaseEntry(90,"time","pause","pausa", null, null));
        db.addEntry(new DatabaseEntry(91,"time","period","período", null, null));
        db.addEntry(new DatabaseEntry(92,"time","present","presente", null, null));
        db.addEntry(new DatabaseEntry(93,"time","season","estação", null, null));
        db.addEntry(new DatabaseEntry(94,"time","second","segundo", null, null));
        db.addEntry(new DatabaseEntry(95,"time","spring","primavera", null, null));
        db.addEntry(new DatabaseEntry(96,"time","summer","verão", null, null));
        db.addEntry(new DatabaseEntry(97,"time","tomorrow","amanhã", null, null));
        db.addEntry(new DatabaseEntry(98,"time","winter","inverno", null, null));
        db.addEntry(new DatabaseEntry(99,"time","year","ano", null, null));
        db.addEntry(new DatabaseEntry(100,"greetings","good morning","bom dia", null, null));
        db.addEntry(new DatabaseEntry(101,"greetings","good afternoon","boa tarde", null, null));
        db.addEntry(new DatabaseEntry(102,"greetings","good evening","boa noite", null, null));
        db.addEntry(new DatabaseEntry(103,"greetings","good night","boa noite", null, null));
        db.addEntry(new DatabaseEntry(104,"greetings","thank you","obrigado", null, null));
        db.addEntry(new DatabaseEntry(105,"greetings","you are welcome","de nada", null, null));
        db.addEntry(new DatabaseEntry(106,"greetings","please","por favor", null, null));
        db.addEntry(new DatabaseEntry(107,"greetings","good bye","adeus", null, null));
        db.addEntry(new DatabaseEntry(108,"greetings","hello","oi", null, null));
        db.addEntry(new DatabaseEntry(109,"food","bread","pão", null, null));
        db.addEntry(new DatabaseEntry(110,"food","butter","manteiga", null, null));
        db.addEntry(new DatabaseEntry(111,"food","cake","bolo", null, null));
        db.addEntry(new DatabaseEntry(112,"food","cheese","queijo", null, null));
        db.addEntry(new DatabaseEntry(113,"food","chocolate","chocolate", null, null));
        db.addEntry(new DatabaseEntry(114,"food","egg","ovo", null, null));
        db.addEntry(new DatabaseEntry(115,"food","food","comida", null, null));
        db.addEntry(new DatabaseEntry(116,"food","fruit","fruto", null, null));
        db.addEntry(new DatabaseEntry(117,"food","lunch","almoço", null, null));
        db.addEntry(new DatabaseEntry(118,"food","meal","refeição", null, null));
        db.addEntry(new DatabaseEntry(119,"food","meat","carne", null, null));
        db.addEntry(new DatabaseEntry(120,"food","oil","óleo", null, null));
        db.addEntry(new DatabaseEntry(121,"food","rice","arroz", null, null));
        db.addEntry(new DatabaseEntry(122,"food","salad","salada", null, null));
        db.addEntry(new DatabaseEntry(123,"food","salt","sal", null, null));
        db.addEntry(new DatabaseEntry(124,"food","sandwich","sanduíche", null, null));
        db.addEntry(new DatabaseEntry(125,"food","sauce","molho", null, null));
        db.addEntry(new DatabaseEntry(126,"food","soup","sopa", null, null));
        db.addEntry(new DatabaseEntry(127,"food","sugar","açúcar", null, null));
        db.addEntry(new DatabaseEntry(128,"food","sweet","doçura", null, null));
        db.addEntry(new DatabaseEntry(129,"drink","beer","cerveja", null, null));
        db.addEntry(new DatabaseEntry(130,"drink","coffee","café", null, null));
        db.addEntry(new DatabaseEntry(131,"drink","drink","bebida", null, null));
        db.addEntry(new DatabaseEntry(132,"drink","milk","leite", null, null));
        db.addEntry(new DatabaseEntry(133,"drink","tea","chá", null, null));
        db.addEntry(new DatabaseEntry(134,"drink","water","água", null, null));
        db.addEntry(new DatabaseEntry(135,"drink","wine","vinho", null, null));
        db.addEntry(new DatabaseEntry(136,"family","baby","bebê", null, null));
        db.addEntry(new DatabaseEntry(137,"family","birth","nascimento", null, null));
        db.addEntry(new DatabaseEntry(138,"family","birthday","aniversário", null, null));
        db.addEntry(new DatabaseEntry(139,"family","boy","menino", null, null));
        db.addEntry(new DatabaseEntry(140,"family","brother","irmão", null, null));
        db.addEntry(new DatabaseEntry(141,"family","child","criança", null, null));
        db.addEntry(new DatabaseEntry(142,"family","daughter","filha", null, null));
        db.addEntry(new DatabaseEntry(143,"family","death","morte", null, null));
        db.addEntry(new DatabaseEntry(144,"family","family","família", null, null));
        db.addEntry(new DatabaseEntry(145,"family","father","pai", null, null));
        db.addEntry(new DatabaseEntry(146,"family","friend","amigo", null, null));
        db.addEntry(new DatabaseEntry(147,"family","girl","menina", null, null));
        db.addEntry(new DatabaseEntry(148,"family","grandfather","vovô", null, null));
        db.addEntry(new DatabaseEntry(149,"family","human","homem", null, null));
        db.addEntry(new DatabaseEntry(150,"family","husband","marido", null, null));
        db.addEntry(new DatabaseEntry(151,"family","life","vida", null, null));
        db.addEntry(new DatabaseEntry(152,"family","man","homem", null, null));
        db.addEntry(new DatabaseEntry(153,"family","marriage","casamento", null, null));
        db.addEntry(new DatabaseEntry(154,"family","mother","mãe", null, null));
        db.addEntry(new DatabaseEntry(155,"family","neighbor","vizinho", null, null));
        db.addEntry(new DatabaseEntry(156,"family","parent","pai", null, null));
        db.addEntry(new DatabaseEntry(157,"family","person","pessoa", null, null));
        db.addEntry(new DatabaseEntry(158,"family","relationship","relação", null, null));
        db.addEntry(new DatabaseEntry(159,"family","sister","irmã", null, null));
        db.addEntry(new DatabaseEntry(160,"family","son","filho", null, null));
        db.addEntry(new DatabaseEntry(161,"family","wedding","casamento", null, null));
        db.addEntry(new DatabaseEntry(162,"family","wife","esposa", null, null));
        db.addEntry(new DatabaseEntry(163,"family","woman","mulher", null, null));
        db.addEntry(new DatabaseEntry(164,"house","address","endereço", null, null));
        db.addEntry(new DatabaseEntry(165,"house","bathroom","banheiro", null, null));
        db.addEntry(new DatabaseEntry(166,"house","bedroom","quarto", null, null));
        db.addEntry(new DatabaseEntry(167,"house","brick","tijolo", null, null));
        db.addEntry(new DatabaseEntry(168,"house","door","porta", null, null));
        db.addEntry(new DatabaseEntry(169,"house","floor","piso", null, null));
        db.addEntry(new DatabaseEntry(170,"house","floor","andar", null, null));
        db.addEntry(new DatabaseEntry(171,"house","garage","garagem", null, null));
        db.addEntry(new DatabaseEntry(172,"house","garden","jardim", null, null));
        db.addEntry(new DatabaseEntry(173,"house","gate","portão", null, null));
        db.addEntry(new DatabaseEntry(174,"house","hall","vestíbulo", null, null));
        db.addEntry(new DatabaseEntry(175,"house","home","lar", null, null));
        db.addEntry(new DatabaseEntry(176,"house","house","casa", null, null));
        db.addEntry(new DatabaseEntry(177,"house","kitchen","cozinha", null, null));
        db.addEntry(new DatabaseEntry(178,"house","roof","teto", null, null));
        db.addEntry(new DatabaseEntry(179,"house","room","quarto", null, null));
        db.addEntry(new DatabaseEntry(180,"house","shower","chuveiro", null, null));
        db.addEntry(new DatabaseEntry(181,"house","step","degrau", null, null));
        db.addEntry(new DatabaseEntry(182,"house","toilet","privada", null, null));
        db.addEntry(new DatabaseEntry(183,"house","wall","parede", null, null));
        db.addEntry(new DatabaseEntry(184,"house","well","poço", null, null));
        db.addEntry(new DatabaseEntry(185,"house","window","janela", null, null));
        db.addEntry(new DatabaseEntry(186,"furniture","bed","cama", null, null));
        db.addEntry(new DatabaseEntry(187,"furniture","carpet","tapete", null, null));
        db.addEntry(new DatabaseEntry(188,"furniture","chair","cadeira", null, null));
        db.addEntry(new DatabaseEntry(189,"furniture","cupboard","armário", null, null));
        db.addEntry(new DatabaseEntry(190,"furniture","curtain","cortina", null, null));
        db.addEntry(new DatabaseEntry(191,"furniture","desk","escrivaninha", null, null));
        db.addEntry(new DatabaseEntry(192,"furniture","furniture","mobília", null, null));
        db.addEntry(new DatabaseEntry(193,"furniture","seat","lugar", null, null));
        db.addEntry(new DatabaseEntry(194,"furniture","shelf","prateleira", null, null));
        db.addEntry(new DatabaseEntry(195,"furniture","sink","pia", null, null));
        db.addEntry(new DatabaseEntry(196,"furniture","table","mesa", null, null));
        db.addEntry(new DatabaseEntry(197,"clothes","bag","bolsa", null, null));
        db.addEntry(new DatabaseEntry(198,"clothes","boot","bota", null, null));
        db.addEntry(new DatabaseEntry(199,"clothes","cloth","pano", null, null));
        db.addEntry(new DatabaseEntry(200,"clothes","clothing","roupa", null, null));
        db.addEntry(new DatabaseEntry(201,"clothes","coat","casaco", null, null));
        db.addEntry(new DatabaseEntry(202,"clothes","collar","colarinho", null, null));
        db.addEntry(new DatabaseEntry(203,"clothes","hat","chapéu", null, null));
        db.addEntry(new DatabaseEntry(204,"clothes","jacket","jaqueta", null, null));
        db.addEntry(new DatabaseEntry(205,"clothes","jewel","jóia", null, null));
        db.addEntry(new DatabaseEntry(206,"clothes","ring","anel", null, null));
        db.addEntry(new DatabaseEntry(207,"clothes","shirt","camisa", null, null));
        db.addEntry(new DatabaseEntry(208,"clothes","shoe","sapato", null, null));
        db.addEntry(new DatabaseEntry(209,"clothes","skirt","saia", null, null));
        db.addEntry(new DatabaseEntry(210,"clothes","suitcase","maleta", null, null));
        db.addEntry(new DatabaseEntry(211,"clothes","tie","gravata", null, null));
        db.addEntry(new DatabaseEntry(212,"transport","boat","barco", null, null));
        db.addEntry(new DatabaseEntry(213,"transport","bus","ônibus", null, null));
        db.addEntry(new DatabaseEntry(214,"transport","car","carro", null, null));
        db.addEntry(new DatabaseEntry(215,"transport","curve","curva", null, null));
        db.addEntry(new DatabaseEntry(216,"transport","flight","vôo", null, null));
        db.addEntry(new DatabaseEntry(217,"transport","journey","viagem", null, null));
        db.addEntry(new DatabaseEntry(218,"transport","passenger","passageiro", null, null));
        db.addEntry(new DatabaseEntry(219,"transport","plane","avião", null, null));
        db.addEntry(new DatabaseEntry(220,"transport","ship","navio", null, null));
        db.addEntry(new DatabaseEntry(221,"transport","track","trilho", null, null));
        db.addEntry(new DatabaseEntry(222,"transport","train","trem", null, null));
        db.addEntry(new DatabaseEntry(223,"transport","transport","transporte", null, null));
        db.addEntry(new DatabaseEntry(224,"profession","actor","ator", null, null));
        db.addEntry(new DatabaseEntry(225,"profession","actress","atriz", null, null));
        db.addEntry(new DatabaseEntry(226,"profession","businessman","comerciante", null, null));
        db.addEntry(new DatabaseEntry(227,"profession","expert","especialista", null, null));
        db.addEntry(new DatabaseEntry(228,"profession","general","general", null, null));
        db.addEntry(new DatabaseEntry(229,"profession","journalist","jornalista", null, null));
        db.addEntry(new DatabaseEntry(230,"profession","judge","juiz", null, null));
        db.addEntry(new DatabaseEntry(231,"profession","leader","líder", null, null));
        db.addEntry(new DatabaseEntry(232,"profession","minister","ministro", null, null));
        db.addEntry(new DatabaseEntry(233,"profession","musician","músico", null, null));
        db.addEntry(new DatabaseEntry(234,"profession","pilot","piloto", null, null));
        db.addEntry(new DatabaseEntry(235,"profession","policeman","policial", null, null));
        db.addEntry(new DatabaseEntry(236,"profession","politician","político", null, null));
        db.addEntry(new DatabaseEntry(237,"profession","president","presidente", null, null));
        db.addEntry(new DatabaseEntry(238,"profession","professional","profissional", null, null));
        db.addEntry(new DatabaseEntry(239,"profession","representative","representante", null, null));
        db.addEntry(new DatabaseEntry(240,"profession","sailor","marinheiro", null, null));
        db.addEntry(new DatabaseEntry(241,"profession","scientist","cientista", null, null));
        db.addEntry(new DatabaseEntry(242,"profession","secretary","secretário", null, null));
        db.addEntry(new DatabaseEntry(243,"profession","servant","empregado", null, null));
        db.addEntry(new DatabaseEntry(244,"profession","soldier","soldado", null, null));
        db.addEntry(new DatabaseEntry(245,"profession","writer","escritor", null, null));
        db.addEntry(new DatabaseEntry(246,"city","airport","aeroporto", null, null));
        db.addEntry(new DatabaseEntry(247,"city","bank","banco", null, null));
        db.addEntry(new DatabaseEntry(248,"city","bridge","ponte", null, null));
        db.addEntry(new DatabaseEntry(249,"city","building","prédio", null, null));
        db.addEntry(new DatabaseEntry(250,"city","capital","capital", null, null));
        db.addEntry(new DatabaseEntry(251,"city","castle","castelo", null, null));
        db.addEntry(new DatabaseEntry(252,"city","church","igreja", null, null));
        db.addEntry(new DatabaseEntry(253,"city","cinema","cinema", null, null));
        db.addEntry(new DatabaseEntry(254,"city","city","cidade", null, null));
        db.addEntry(new DatabaseEntry(255,"city","factory","fábrica", null, null));
        db.addEntry(new DatabaseEntry(256,"city","apartment","apartamento", null, null));
        db.addEntry(new DatabaseEntry(257,"city","hospital","hospital", null, null));
        db.addEntry(new DatabaseEntry(258,"city","hotel","hotel", null, null));
        db.addEntry(new DatabaseEntry(259,"city","library","biblioteca", null, null));
        db.addEntry(new DatabaseEntry(260,"city","park","parque", null, null));
        db.addEntry(new DatabaseEntry(261,"city","prison","prisão", null, null));
        db.addEntry(new DatabaseEntry(262,"city","restaurant","restaurante", null, null));
        db.addEntry(new DatabaseEntry(263,"city","road","estrada", null, null));
        db.addEntry(new DatabaseEntry(264,"city","school","escola", null, null));
        db.addEntry(new DatabaseEntry(265,"city","station","estação", null, null));
        db.addEntry(new DatabaseEntry(266,"city","street","rua", null, null));
        db.addEntry(new DatabaseEntry(267,"city","structure","estrutura", null, null));
        db.addEntry(new DatabaseEntry(268,"city","subway","metrô", null, null));
        db.addEntry(new DatabaseEntry(269,"city","theater","teatro", null, null));
        db.addEntry(new DatabaseEntry(270,"city","town","cidade", null, null));
        db.addEntry(new DatabaseEntry(271,"city","university","universidade", null, null));
        db.addEntry(new DatabaseEntry(272,"city","village","aldeia", null, null));
        db.addEntry(new DatabaseEntry(273,"geography","bank","margem", null, null));
        db.addEntry(new DatabaseEntry(274,"geography","beach","praia", null, null));
        db.addEntry(new DatabaseEntry(275,"geography","current","curso", null, null));
        db.addEntry(new DatabaseEntry(276,"geography","direction","direção", null, null));
        db.addEntry(new DatabaseEntry(277,"geography","east","leste", null, null));
        db.addEntry(new DatabaseEntry(278,"geography","height","altitude", null, null));
        db.addEntry(new DatabaseEntry(279,"geography","island","ilha", null, null));
        db.addEntry(new DatabaseEntry(280,"geography","lake","lago", null, null));
        db.addEntry(new DatabaseEntry(281,"geography","land","terra", null, null));
        db.addEntry(new DatabaseEntry(282,"geography","map","mapa", null, null));
        db.addEntry(new DatabaseEntry(283,"geography","moon","lua", null, null));
        db.addEntry(new DatabaseEntry(284,"geography","north","norte", null, null));
        db.addEntry(new DatabaseEntry(285,"geography","region","região", null, null));
        db.addEntry(new DatabaseEntry(286,"geography","river","rio", null, null));
        db.addEntry(new DatabaseEntry(287,"geography","sea","mar", null, null));
        db.addEntry(new DatabaseEntry(288,"geography","south","sul", null, null));
        db.addEntry(new DatabaseEntry(289,"geography","space","espaço", null, null));
        db.addEntry(new DatabaseEntry(290,"geography","star","estrela", null, null));
        db.addEntry(new DatabaseEntry(291,"geography","west","oeste", null, null));
        db.addEntry(new DatabaseEntry(292,"weather","sun","sol", null, null));
        db.addEntry(new DatabaseEntry(293,"weather","cloud","nuvem", null, null));
        db.addEntry(new DatabaseEntry(294,"weather","dew","orvalho", null, null));
        db.addEntry(new DatabaseEntry(295,"weather","frost","geada", null, null));
        db.addEntry(new DatabaseEntry(296,"weather","rain","chuva", null, null));
        db.addEntry(new DatabaseEntry(297,"weather","rainbow","arco-íris", null, null));
        db.addEntry(new DatabaseEntry(298,"weather","snow","neve", null, null));
        db.addEntry(new DatabaseEntry(299,"weather","storm","tempestade", null, null));
        db.addEntry(new DatabaseEntry(300,"weather","thunder","trovão", null, null));
        db.addEntry(new DatabaseEntry(301,"weather","wind","vento", null, null));
        db.addEntry(new DatabaseEntry(302,"weather","sunny","ensolarado", null, null));


    }



}




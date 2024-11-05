package com.toto.travelmantotoproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toto.travelmantotoproject.AutoCompleteAdapter;
import com.toto.travelmantotoproject.model.MetroBusSystem;
import com.toto.travelmantotoproject.model.MetroSystem;
import com.toto.travelmantotoproject.model.Station;
import com.toto.travelmantotoproject.model.TransportingLine;
import com.toto.travelmantotoproject.opengl.render.OpenGLObjectRenderer;
import com.toto.travelmantotoproject.opengl.render.OpenGLRenderer;
import com.toto.travelmantotoproject.R;
import com.toto.travelmantotoproject.utils.Properties;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,GoogleMap.OnMarkerClickListener {

    private static final LatLng ZOCALO_LOCATION = new LatLng(19.432629691186428, -99.13320059814791);
    private static final MetroSystem metroSystem = new MetroSystem();
    private static final MetroBusSystem metrobusSystem = new MetroBusSystem();
    private FrameLayout preview;
    private GoogleMap mMap;
    private MapView mapView;
    private LineMetroView lineMetroView;
    private MetroStationSurfaceView metroStationSurfaceView;
    private LocationManager locationManager;
    private Button metroButton;
    private Button metrobusButton;
    private AutoCompleteTextView autoCompleteTextViewStation;
    private boolean enableMetroLine;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int property = Properties.Companion.getProperty();


        metroButton = findViewById(R.id.metroButton);
        metrobusButton = findViewById(R.id.metrobusButton);
        metrobusButton.setAlpha(0.5f);
        metroSystem.setEnable(true);
        metroButton.setOnClickListener(v -> {
                metroSystem.setEnable(!metroSystem.isEnable());
                metroButton.setAlpha(!metroSystem.isEnable()?0.5f:1.0f);
                if(lineMetroView != null){
                    lineMetroView.invalidate();
                }
            }
        );
        metrobusButton.setOnClickListener(view -> {
                metrobusSystem.setEnable(!metrobusSystem.isEnable());
                metrobusButton.setAlpha(!metrobusSystem.isEnable()?0.5f:1.0f);
                if(lineMetroView != null){
                    lineMetroView.invalidate();
                }

        });

        FloatingActionButton fab = findViewById(R.id.fab);



        metroSystem.getLines().add(new TransportingLine("#f54a91",
                new Station("observatorio", new LatLng(19.398221331566152, -99.20025024991276)),
                new Station("tacubaya", new LatLng(19.40193006555249, -99.18735664362953)),
                new Station("juanacatlan", new LatLng(19.41294672844391, -99.1820013959795)),
                new Station("chapultepec", new LatLng(19.420639826226477, -99.17692333763226)),
                new Station("sevilla", new LatLng(19.42154953760367, -99.17104723266566)),
                new Station("insurgentes", new LatLng(19.42364260530846, -99.16330646783844)),
                new Station("cuauhtemoc", new LatLng(19.425674449467735, -99.15458547645063)),
                new Station("balderas", new LatLng(19.42722328791152, -99.14894518337526)),
                new Station("saltodelagua", new LatLng(19.42719870035575, -99.14207398727936)),
                new Station("isabelacatolica", new LatLng(19.426111647948684, -99.1378725151177)),
                new Station("pinosuarez", new LatLng(19.425930077606946, -99.13305902555115)),
                new Station("merced", new LatLng(19.425577998073305, -99.12522549411044)),
                new Station("candelaria", new LatLng(19.4296467316634, -99.12087900132255)),
                new Station("sanlazaro", new LatLng(19.431388470118325, -99.11419351718634)),
                new Station("moctezuma", new LatLng(19.427362212746285, -99.10957965659415)),
                new Station("balbuena", new LatLng(19.423302897287428, -99.10263688405638)),
                new Station("boulevarptoaereo", new LatLng(19.419518023675977, -99.09638681552559)),
                new Station("gomezfarias", new LatLng(19.41653188267209, -99.09055653900487)),
                new Station("zaragoza", new LatLng(19.411999707191963, -99.08278267289512)),
                new Station("pantitlan", new LatLng(19.41637246004158, -99.07472134639364))));

        metroSystem.getLines().add(new TransportingLine("#095aa6",
                new Station("cuatro_caminos", new LatLng(19.460980299478898, -99.21589297309154)),
                new Station("panteones", new LatLng(19.45867370437011, -99.20307541178512)),
                new Station("tacuba", new LatLng(19.45930098685787, -99.18755355337582)),
                new Station("cuitlahuac", new LatLng(19.457204195883048, -99.18147668565892)),
                new Station("popotla", new LatLng(19.452853842384496, -99.17548337039277)),
                new Station("colegio_militar", new LatLng(19.449188735804157, -99.17186321751521)),
                new Station("normal", new LatLng(19.444682725708994, -99.16734959278867)),
                new Station("san_cosme", new LatLng(19.44191779482246, -99.16070999049029)),
                new Station("revolucion", new LatLng(19.439212878966885, -99.1542317588041)),
                new Station("hidalgo", new LatLng(19.43750714197644, -99.14715562676646)),
                new Station("bellas_artes", new LatLng(19.43590382257731, -99.1418440879039)),
                new Station("allende", new LatLng(19.435543448088715, -99.13689707057583)),
                new Station("zocalo", ZOCALO_LOCATION),
                new Station("pinosuarez", new LatLng(19.425930077606946, -99.13305902555115)),
                new Station("san_antonio_abad", new LatLng(19.41578429159245, -99.13459620109468)),
                new Station("chabacano", new LatLng(19.409139660927043, -99.13561798456348)),
                new Station("viaducto", new LatLng(19.400931092416517, -99.13689395872277)),
                new Station("xola", new LatLng(19.395226149445417, -99.13783172556573)),
                new Station("villa_de_cortes", new LatLng(19.38759568810728, -99.13899842683641)),
                new Station("nativitas", new LatLng(19.37950089087049, -99.14022009657064)),
                new Station("portales", new LatLng(19.3698292282378, -99.14165375739921)),
                new Station("ermita", new LatLng(19.361156677583494, -99.14303105347315)),
                new Station("general_anaya", new LatLng(19.3531461865711, -99.14504657950364)),
                new Station("tasquena", new LatLng(19.3436199316799, -99.14050090249395))));

        metroSystem.getLines().add(new TransportingLine("#b3ad00",
                new Station("indios_verdes", new LatLng(19.495216959367806, -99.1196009185288)),
                new Station("deportivo_18_de_marzo", new LatLng(19.485087862411014, -99.12552187622902)),
                new Station("potrero", new LatLng(19.477233821708207, -99.13198565652971)),
                new Station("la_raza", new LatLng(19.46852983938752, -99.13922894620934)),
                new Station("tlatelolco", new LatLng(19.455061262529277, -99.14318320726471)),
                new Station("guerrero", new LatLng(19.444680932146657, -99.14524197673254)),
                new Station("hidalgo", new LatLng(19.43750714197644, -99.14715562676646)),
                new Station("juarez", new LatLng(19.433471399718727, -99.14766556644845)),
                new Station("balderas", new LatLng(19.42722328791152, -99.14894518337526)),
                new Station("poderjudical", new LatLng(19.41938783471611, -99.15040397567084)),
                new Station("hospital_general", new LatLng(19.413630320623806, -99.15320420184061)),
                new Station("centro_medico", new LatLng(19.406568569751443, -99.15519452222306)),
                new Station("etiopia", new LatLng(19.395759859579417, -99.15615578648702)),
                new Station("eugenia", new LatLng(19.386247623394617, -99.15719135658972)),
                new Station("division_del_norte", new LatLng(19.379070285496983, -99.15938080011412)),
                new Station("zapata", new LatLng(19.370623983875685, -99.16518085069691)),
                new Station("coyoacan", new LatLng(19.36141943185862, -99.17078998730476)),
                new Station("viveros_derechos_humanos", new LatLng(19.353926028407603, -99.17530170244054)),
                new Station("miguel_angel_de_quevedo", new LatLng(19.34590872963737, -99.18069176683623)),
                new Station("copilco", new LatLng(19.33617753414826, -99.17708420676082)),
                new Station("universidad", new LatLng(19.32438164514374, -99.17389029126333))));

        metroSystem.getLines().add(new TransportingLine("#7cc6a2",
                new Station("martin_carrera", new LatLng(19.48331806826899, -99.1063462112432)),
                new Station("talisman", new LatLng(19.474333017228624, -99.10809154829838)),
                new Station("bondojito", new LatLng(19.464878034926954, -99.1117916022826)),
                new Station("consulado", new LatLng(19.458081071003893, -99.11388752433102)),
                new Station("canal_del_norte", new LatLng(19.448913879896764, -99.11590341589664)),
                new Station("morelos", new LatLng(19.439649919055295, -99.11822551895642)),
                new Station("candelaria", new LatLng(19.4296467316634, -99.12087900132255)),
                new Station("fray_servando", new LatLng(19.42155359292069, -99.120522104567)),
                new Station("jamaica", new LatLng(19.410988449559994, -99.12172143252548)),
                new Station("santa_anita", new LatLng(19.404177298389516, -99.12072624558343))));
        metroSystem.getLines().add(new TransportingLine("#ffe600",
                new Station("pantitlan", new LatLng(19.41637246004158, -99.07472134639364)),
                new Station("hangares", new LatLng(19.4251415292229, -99.0882758459505)),
                new Station("terminal_aerea", new LatLng(19.433802405920996, -99.08819001526888)),
                new Station("oceania", new LatLng(19.444607967730196, -99.0866841627275)),
                new Station("aragon", new LatLng(19.451192517858885, -99.0962106420985)),
                new Station("eduardo_molina", new LatLng(19.45134798650124, -99.10534575434218)),
                new Station("consulado", new LatLng(19.458081071003893, -99.11388752433102)),
                new Station("valle_gomez", new LatLng(19.458854243478747, -99.11940052918935)),
                new Station("misterios", new LatLng(19.463042219721615, -99.13032248419687)),
                new Station("la_raza", new LatLng(19.46852983938752, -99.13922894620934)),
                new Station("autobuses_del_norte", new LatLng(19.478842257320537, -99.1406221667848)),
                new Station("instituto_del_petroleo", new LatLng(19.489361274339252, -99.14476349762734)),
                new Station("politecnico", new LatLng(19.500628001628783, -99.14924815091854))));

        metroSystem.getLines().add(new TransportingLine("#fb2500",
                new Station("el_rosario", new LatLng(19.50457680436575, -99.2001386430354)),
                new Station("tezozomoc", new LatLng(19.49489058856408, -99.19622846197024)),
                new Station("azcapotzalco", new LatLng(19.490840206976145, -99.18629387376278)),
                new Station("ferreria", new LatLng(19.49056160553175, -99.17410865783434)),
                new Station("norte_45", new LatLng(19.488782831453648, -99.16317379075967)),
                new Station("vallejo", new LatLng(19.489768660464414, -99.15610363769537)),
                new Station("instituto_del_petroleo", new LatLng(19.48927574670351, -99.14475956534574)),
                new Station("lindavista", new LatLng(19.4879898776305, -99.13471130867583)),
                new Station("deportivo_18_de_marzo", new LatLng(19.48507520329851, -99.12545872865527)),
                new Station("la_villa_basilica", new LatLng(19.481731835814337, -99.11836584166305)),
                new Station("martin_carrera", new LatLng(19.48331806826899, -99.1063462112432))));
        metroSystem.getLines().add(new TransportingLine("#ff6309",
                new Station("el_rosario", new LatLng(19.50457680436575, -99.2001386430354)),
                new Station("aquiles_serdan", new LatLng(19.490649405266286, -99.19533331064204)),
                new Station("camarones", new LatLng(19.479123066291322, -99.1897010332795)),
                new Station("refineria", new LatLng(19.46973630608798, -99.19015164441694)),
                new Station("tacuba", new LatLng(19.45921601489773, -99.18776984279324)),
                new Station("san_joaquin", new LatLng(19.445073240595637, -99.19176096980516)),
                new Station("polanco", new LatLng(19.433600299278705, -99.1908597475794)),
                new Station("auditorio", new LatLng(19.42510130170872, -99.19201846186758)),
                new Station("constituyentes", new LatLng(19.411785311524998, -99.19148202007493)),
                new Station("tacubaya", new LatLng(19.40193006555249, -99.18735664362953)),
                new Station("san_pedro_de_los_pinos", new LatLng(19.391479098783712, -99.18580790255392)),
                new Station("san_antonio", new LatLng(19.385004125394644, -99.18662446627272)),
                new Station("mixcoac", new LatLng(19.376145752201996, -99.18767068854517)),
                new Station("barranca_del_muerto", new LatLng(19.362951975254965, -99.18833320541303))
        ));
        metroSystem.getLines().add(new TransportingLine("#008e4e",
                new Station("garibaldi_lagunilla", new LatLng(19.44384194251216, -99.1386379058323)),
                new Station("bellas_artes", new LatLng(19.4359033499979, -99.14182171585514)),
                new Station("san_juan_de_letran", new LatLng(19.431499431048948, -99.14171672204624)),
                new Station("saltodelagua", new LatLng(19.427168988959636, -99.14195275643682)),
                new Station("doctores", new LatLng(19.421523578172888, -99.1432576287279)),
                new Station("obrera", new LatLng(19.41319990336968, -99.14401420643155)),
                new Station("chabacano", new LatLng(19.409221611026297, -99.13572982188926)),
                new Station("la_viga", new LatLng(19.406273605017894, -99.12655023829969)),
                new Station("santa_anita", new LatLng(19.404177298389516, -99.12072624558343)),
                new Station("coyuya", new LatLng(19.398099722165885, -99.11347640319809)),
                new Station("iztacalco", new LatLng(19.388693147663027, -99.11218343192994)),
                new Station("apatlaco", new LatLng(19.378635167329275, -99.10937648566701)),
                new Station("aculco", new LatLng(19.37312446568486, -99.10775298906523)),
                new Station("escuadron_201", new LatLng(19.364903810519557, -99.10919714293809)),
                new Station("atlalilco", new LatLng(19.35619580572541, -99.1011417679837)),
                new Station("iztapalapa", new LatLng(19.35770555786582, -99.09337013832801)),
                new Station("cerro_de_la_estrella", new LatLng(19.35593509106282, -99.08561135612577)),
                new Station("uam", new LatLng(19.350930013629178, -99.07494754149174)),
                new Station("constitucion_de_1917", new LatLng(19.34597585722435, -99.06401307168672))));
        metroSystem.getLines().add(new TransportingLine("#381101",
                new Station("tacubaya", new LatLng(19.40193006555249, -99.18735664362953)),
                new Station("patriotismo", new LatLng(19.406064920857382, -99.17892189615685)),
                new Station("chilpancingo", new LatLng(19.406117185851354, -99.16900689124863)),
                new Station("centro_medico", new LatLng(19.406643672766368, -99.15534782581028)),
                new Station("lazaro_cardenas", new LatLng(19.40725633806144, -99.14419683173605)),
                new Station("chabacano", new LatLng(19.409221611026297, -99.13572982188926)),
                new Station("jamaica", new LatLng(19.410988449559994, -99.12172143252548)),
                new Station("mixihuca", new LatLng(19.408660989748867, -99.11324721132682)),
                new Station("velodromo", new LatLng(19.408564719691256, -99.10314223542541)),
                new Station("ciudad_deportiva", new LatLng(19.408492368856997, -99.09128201757642)),
                new Station("puebla", new LatLng(19.4072413708679, -99.08252057566682)),
                new Station("pantitlan", new LatLng(19.41637246004158, -99.07472134639364))));

        metroSystem.getLines().add(new TransportingLine("#81017e",
                new Station("pantitlan", new LatLng(19.41637246004158, -99.07472134639364)),
                new Station("agricola_oriental", new LatLng(19.404667350215743, -99.0695987951251)),
                new Station("canal_de_san_juan", new LatLng(19.39880602253489, -99.0596094615524)),
                new Station("tepalcates", new LatLng(19.3913174135021, -99.04634286392617)),
                new Station("guelatao", new LatLng(19.385190161157208, -99.0357168573384)),
                new Station("penon_viejo", new LatLng(19.373292805441398, -99.01715796164866)),
                new Station("acatitla", new LatLng(19.364660813380077, -99.00570714981808)),
                new Station("santa_marta", new LatLng(19.36027237398341, -98.99516694276984)),
                new Station("los_reyes", new LatLng(19.359007207518225, -98.97685671799795)),
                new Station("la_paz", new LatLng(19.350642612598012, -98.96087382051331))));
        metroSystem.getLines().add(new TransportingLine("#a8a8a8",
                new Station("ciudad_azteca", new LatLng(19.53478716743477, -99.02767906676875)),
                new Station("plaza_aragon", new LatLng(19.528356271794664, -99.03012524116194)),
                new Station("olimpica", new LatLng(19.521156600821943, -99.03342972233008)),
                new Station("ecatepec", new LatLng(19.51525101312781, -99.03591881215978)),
                new Station("muzquiz", new LatLng(19.501351334822772, -99.04219360222632)),
                new Station("rio_de_los_remedios", new LatLng(19.491081847711047, -99.04669994324117)),
                new Station("impulsora", new LatLng(19.48563217555597, -99.04903656454117)),
                new Station("nezahualcoyotl", new LatLng(19.472686681116596, -99.05474156191062)),
                new Station("villa_de_aragon", new LatLng(19.46169691407476, -99.0613658709452)),
                new Station("bosque_de_aragon", new LatLng(19.458091773287123, -99.06924058822966)),
                new Station("deportivo_oceania", new LatLng(19.451108926601034, -99.07942146046096)),
                new Station("oceania", new LatLng(19.444607967730196, -99.0866841627275)),
                new Station("romero_rubio", new LatLng(19.440875368396128, -99.09421371675485)),
                new Station("ricardo_flores_magon", new LatLng(19.43662124225767, -99.1037258596629)),
                new Station("sanlazaro", new LatLng(19.431388470118325, -99.11419351718634)),
                new Station("morelos", new LatLng(19.43974536160867, -99.11815695883034)),
                new Station("tepito", new LatLng(19.442782518858092, -99.12413843874356)),
                new Station("lagunilla", new LatLng(19.443352511228603, -99.13179798054148)),
                new Station("garibaldi", new LatLng(19.443811906609266, -99.13867262260062)),
                new Station("guerrero", new LatLng(19.444680932146657, -99.14524197673254)),
                new Station("buenavista", new LatLng(19.44618504521641, -99.1524503171075))));


        metrobusSystem.getLines().add(new TransportingLine("#a0291e",
                new Station("mb_indiosverdes", new LatLng(19.499314808612446, -99.11932580424357)),
                new Station("mb_18marzo", new LatLng(19.486447463043234, -99.12433170100286)),
                new Station("mb_euzkaro", new LatLng(19.4826467035855, -99.1276810504856)),
                new Station("mb_potrero", new LatLng(19.476700241270176, -99.13247365914201)),
                new Station("mb_laraza", new LatLng(19.46887312113965, -99.13880139805129)),
                new Station("mb_circuito", new LatLng(19.462591442589503, -99.14395702205664)),
                new Station("mb_sansimon", new LatLng(19.45962642395025, -99.14644360730978)),
                new Station("mb_manuelgonzalez", new LatLng(19.456723500137038, -99.14939468696099)),
                new Station("mb_buenavista", new LatLng(19.446919269595078, -99.15312373659755)),
                new Station("mb_elchopo", new LatLng(19.44335051223388, -99.15441164140304)),
                new Station("mb_revolucion", new LatLng(19.440284764944447, -99.15545011168162)),
                new Station("mb_plazarepublica", new LatLng(19.436041879271407, -99.15732877923202)),
                new Station("mb_reforma", new LatLng(19.432897694443398, -99.1586992176664)),
                new Station("mb_hamburgo", new LatLng(19.427445413926126, -99.16127590489748)),
                new Station("mb_insurgentes", new LatLng(19.42331612368375, -99.16261595749667)),
                new Station("mb_durango", new LatLng(19.419917116887653, -99.16407591490314)),
                new Station("mb_aobregon", new LatLng(19.416589321359073, -99.16506147355292)),
                new Station("mb_sonora", new LatLng(19.41329204329285, -99.16613968617203)),
                new Station("mb_campeche", new LatLng(19.4096993530393, -99.16730293577983)),
                new Station("mb_chilpancingo", new LatLng(19.406347110855673, -99.16838761614682)),
                new Station("mb_nuevoleon", new LatLng(19.402016301427576, -99.16981974677961)),
                new Station("mb_lapiedad", new LatLng(19.3979590357434, -99.17112847945734)),
                new Station("mb_poliforum", new LatLng(19.393289197149002, -99.17263358292291)),
                new Station("mb_napoles", new LatLng(19.38971109816786, -99.17379568513246)),
                new Station("mb_coloniadelvalle", new LatLng(19.385681736791152, -99.17510427802743)),
                new Station("mb_cddeportes", new LatLng(19.382461536443078, -99.17615990740168)),
                new Station("mb_parquehundido", new LatLng(19.379592536552302, -99.17707627730466)),
                new Station("mb_felixcuevas", new LatLng(19.3742977068391, -99.1788049913051)),
                new Station("mb_riochurubusco", new LatLng(19.3687191167302, -99.18060678513304)),
                new Station("mb_teatroinsurgentes", new LatLng(19.364862887383754, -99.18185648210248)),
                new Station("mb_jmvelasco", new LatLng(19.3616988526425, -99.18288667029263)),
                new Station("mb_francia", new LatLng(19.35842287921499, -99.18394579962879)),
                new Station("mb_olivo", new LatLng(19.354495623345226, -99.18522351598517)),
                new Station("mb_altavista", new LatLng(19.350881461947772, -99.18636653364466)),
                new Station("mb_bombilla", new LatLng(19.346789309419734, -99.18775467342799)),
                new Station("mb_doctorgalvez", new LatLng(19.340816878102128, -99.18999995465397)),
                new Station("mb_cu", new LatLng(19.32298133876803, -99.18850522779591)),
                new Station("mb_ccu", new LatLng(19.315185891980594, -99.18754940243011)),
                new Station("mb_perisur", new LatLng(19.304085323195427, -99.18614373230695)),
                new Station("mb_villaolimpica", new LatLng(19.299097261351037, -99.18557532143517)),
                new Station("mb_corregidora", new LatLng(19.29406585819187, -99.18109860742028)),
                new Station("mb_ayuntamiento", new LatLng(19.29257490861012, -99.1775655349131)),
                new Station("mb_fuentesbrotantes", new LatLng(19.28811411953959, -99.174581447668)),
                new Station("mb_santaursula", new LatLng(19.283319461238865, -99.17535147676175)),
                new Station("mb_lajoya", new LatLng(19.280400776400445, -99.17006014912647)),
                new Station("mb_elcaminero", new LatLng(19.27934891590134, -99.16908533469069))
                ));
        metrobusSystem.getLines().add(new TransportingLine("#7a9a01",
                new Station("mb_tenayuca", new LatLng(19.52898017322238, -99.17017229203307)),
                new Station("mb_sanjosedelaescalera", new LatLng(19.52267218149447, -99.16553152697603)),
                new Station("mb_progresonacional", new LatLng(19.519479732910668, -99.16369324753451)),
                new Station("mb_tresanegas", new LatLng(19.51550361192232, -99.16197488611306)),
                new Station("mb_jupiter", new LatLng(19.508569276812334, -99.15923496881035)),
                new Station("mb_lapatera", new LatLng(19.503657580035572, -99.15727295375629)),
                new Station("mb_poniente146", new LatLng(19.499912576112497, -99.15589183379245)),
                new Station("mb_montevideo", new LatLng(19.496095491974227, -99.1544976739918)),
                new Station("mb_poniente134", new LatLng(19.49266460745537, -99.15318050135016)),
                new Station("mb_poniente128", new LatLng(19.48947600074889, -99.15197529628244)),
                new Station("mb_magdalenadelassalinas", new LatLng(19.483879245964843, -99.1498912836933)),
                new Station("mb_coltongo", new LatLng(19.479765041012854, -99.148301324749)),
                new Station("mb_cuitlahuac", new LatLng(19.474139183294426, -99.14616622959)),
                new Station("mb_heroedenacozari", new LatLng(19.471130568509114, -99.14504867866476)),
                new Station("mb_hospitallaraza", new LatLng(19.46798907670485, -99.14384642673488)),
                new Station("mb_laraza", new LatLng(19.46757725575341, -99.14083272858798)),
                new Station("mb_circuito", new LatLng(19.46332443891792, -99.1441306848326)),
                new Station("mb_tolnahuac", new LatLng(19.4598966834786, -99.14407316166833)),
                new Station("mb_tlatelolco", new LatLng(19.455583460790685, -99.14504632006616)),
                new Station("mb_ricardofloresmagon", new LatLng(19.451718102125206, -99.14594093823925)),
                new Station("mb_guerrero", new LatLng(19.445995095342152, -99.14720427619648)),
                new Station("mb_buenavista", new LatLng(19.445788069321168, -99.15211323174242)),
                new Station("mb_mina", new LatLng(19.440560398414345, -99.14856033279136)),
                new Station("mb_hidalgo", new LatLng(19.43610523629381, -99.1471970994751)),
                new Station("mb_juarez", new LatLng(19.431615400643352, -99.14800653440295)),
                new Station("mb_balderas", new LatLng(19.428079953763124, -99.14874581924401)),
                new Station("mb_cuauhtemoc", new LatLng(19.424922951376903, -99.15371344980177)),
                new Station("mb_jardinpushkin", new LatLng(19.420407884528178, -99.1540770646861)),
                new Station("mb_hospitalgeneral", new LatLng(19.41433141096984, -99.15459800216249)),
                new Station("mb_doctormarquez", new LatLng(19.411160791143647, -99.15484492283166)),
                new Station("mb_centromedico", new LatLng(19.407287884798926, -99.15512959399209)),
                new Station("mb_obreromundial", new LatLng(19.40103062383821, -99.15561934754005)),
                new Station("mb_etiopia", new LatLng(19.397050176369408, -99.155916507429)),
                new Station("mb_luzsavinon", new LatLng(19.39091736834887, -99.15643077941715)),
                new Station("mb_eugenia", new LatLng(19.38592051415991, -99.15723276899129)),
                new Station("mb_division", new LatLng(19.37992970552788, -99.15898255971419)),
                new Station("mb_miguellaurent", new LatLng(19.374346659848676, -99.1603787187606)),
                new Station("mb_pueblostacruz", new LatLng(19.370926752775045, -99.16069939282978))
        ));

        metrobusSystem.getLines().add(new TransportingLine("#893994",
                new Station("mb_tepalcates", new LatLng(19.390740251175163, -99.04749667446292)),
                new Station("mb_nicolasbravo", new LatLng(19.393517480605592, -99.05091574426321)),
                new Station("mb_canalsnjuan", new LatLng(19.396731705151417, -99.05651830337814)),
                new Station("mb_constituciondeapatzingan", new LatLng(19.389472054485257, -99.05978618158242)),
                new Station("mb_general_antonio_de_leon", new LatLng(19.38535576567688, -99.05172259615108)),
                new Station("mb_constituciondeapatzingan", new LatLng(19.389472054485257, -99.05978618158242)),
                new Station("mb_cchoriente", new LatLng(19.38332303313931, -99.06079414621796)),
                new Station("mb_leyesdereforma", new LatLng(19.383454754527754, -99.0658296117261)),
                new Station("mb_delmoral", new LatLng(19.38423698817466, -99.07093568669664)),
                new Station("mb_rojogomez", new LatLng(19.3845245068963, -99.07636455276734)),
                new Station("mb_riofrio", new LatLng(19.387619946544856, -99.0743186352804)),
                new Station("mb_rojogomez", new LatLng(19.3845245068963, -99.07636455276734)),
                new Station("mb_riomayo", new LatLng(19.386965298714564, -99.08002092702318)),
                new Station("mb_riotecolutla", new LatLng(19.389027102210125, -99.083126399568)),
                new Station("mb_rodeo", new LatLng(19.391683067308925, -99.08708740429744)),
                new Station("mb_upiicsa", new LatLng(19.393894251189643, -99.09033342070684)),
                new Station("mb_iztacalco", new LatLng(19.396540478776856, -99.09594987307011)),
                new Station("mb_goma", new LatLng(19.396886387219787, -99.09998094131666)),
                new Station("mb_tlacotal", new LatLng(19.397217067617223, -99.1038823441142)),
                new Station("mb_canela", new LatLng(19.39783998838502, -99.10934854610349)),
                new Station("mb_coyuya", new LatLng(19.398290789026326, -99.11678000637849)),
                new Station("mb_laviga", new LatLng(19.398059209803648, -99.12452919078869)),
                new Station("mb_andresmolina", new LatLng(19.39765333964653, -99.12972445529309)),
                new Station("mb_lasamericas", new LatLng(19.393458147961223, -99.13435498979572)),
                new Station("mb_xola", new LatLng(19.394361648528033, -99.14024329314404)),
                new Station("mb_alamos", new LatLng(19.394739375159606, -99.14293261319277)),
                new Station("mb_centroscop", new LatLng(19.395253351294524, -99.1467364265775)),
                new Station("mb_drvertiz", new LatLng(19.395630675236657, -99.15172131156092)),
                new Station("mb_etiopiaplazadelatransparencia", new LatLng(19.395853827454, -99.15500652284688)),
                new Station("mb_amores", new LatLng(19.396897120751294, -99.16381367645344)),
                new Station("mb_viaducto", new LatLng(19.401329291069114, -99.16808443114589)),
                new Station("mb_nuevoleon", new LatLng(19.403493914100622, -99.17065101359648)),
                new Station("mb_escandon", new LatLng(19.40443134676307, -99.17383232230478)),
                new Station("mb_patriotismo", new LatLng(19.405590511929212, -99.17743357555497)),
                new Station("mb_delasalle", new LatLng(19.407598412482137, -99.1835484694363)),
                new Station("mb_parquelira", new LatLng(19.40776117111719, -99.18911792354928)),
                new Station("mb_tacubaya", new LatLng(19.401938089730795, -99.18702962142494)),
                new Station("mb_antoniomaceo", new LatLng(19.404879106768682, -99.18589821395145)),
                new Station("mb_delasalle", new LatLng(19.407598412482137, -99.1835484694363))));

        metrobusSystem.getLines().add(new TransportingLine("#046a37",
                new Station("mb_indiosverdes", new LatLng(19.499314808612446, -99.11932580424357)),
                new Station("mb_delosmisterios", new LatLng(19.487178278761466, -99.11789944850844)),
                new Station("mb_garrido", new LatLng(19.483512878086852, -99.11926073107172)),
                new Station("mb_avenidatalisman", new LatLng(19.479007728609993, -99.12099017515901)),
                new Station("mb_necaxa", new LatLng(19.47647733351958, -99.12202768673028)),
                new Station("mb_excelsior", new LatLng(19.470916046839488, -99.12414668021486)),
                new Station("mb_roblesdominguez", new LatLng(19.468571088285298, -99.12499825114521)),
                new Station("mb_clave", new LatLng(19.464853839242124, -99.12629557729082)),
                new Station("mb_misterios", new LatLng(19.46252543292387, -99.12716662265409)),
                new Station("mb_mercadobethoveen", new LatLng(19.4576240396195, -99.12906611858804)),
                new Station("mb_peralvillo", new LatLng(19.453749702278827, -99.13064287690521)),
                new Station("mb_tresculturas", new LatLng(19.450935131891477, -99.13278063172139)),
                new Station("mb_glorietacuitlahuac", new LatLng(19.449212412143286, -99.13412849914563)),
                new Station("mb_garibaldi", new LatLng(19.444345916657223, -99.13955733978221)),
                new Station("mb_glorietavioleta", new LatLng(19.441657652763862, -99.14215496479387)),
                new Station("mb_hidalgo", new LatLng(19.4378131963632, -99.14633096827833)),
                new Station("mb_elcaballito", new LatLng(19.43604807638735, -99.14905538751873)),
                new Station("mb_glorietadecolon", new LatLng(19.43357377121387, -99.15420699744466)),
                new Station("mb_paris", new LatLng(19.432658570156168, -99.1559314109675)),
                new Station("mb_reforma", new LatLng(19.431134488910125, -99.15853648537305)),
                new Station("mb_hamburgo", new LatLng(19.430069869237535, -99.16152833599392)),
                new Station("mb_lapalma", new LatLng(19.4284977446816, -99.16476210200409)),
                new Station("mb_elangel", new LatLng(19.426694117434558, -99.16862419145365)),
                new Station("mb_ladiana", new LatLng(19.425602366640096, -99.17096428537353)),
                new Station("mb_chapultepec", new LatLng(19.424008786163423, -99.1744329316524)),
                new Station("mb_gandhi", new LatLng(19.42409100295712, -99.1800244732737)),
                new Station("mb_antropologia", new LatLng(19.424753503092788, -99.1843466469176)),
                new Station("mb_auditorio", new LatLng(19.42618459873251, -99.19374895100157)),
                new Station("mb_campomarte", new LatLng(19.42673563363993, -99.19940788724473)),
                new Station("mb_campomarte", new LatLng(19.426722986118133, -99.19941861608069)),
                new Station("mb_auditorio", new LatLng(19.42588679275644, -99.19373344292178)),
                new Station("mb_antropologia", new LatLng(19.424647631936693, -99.1855544663718)),
                new Station("mb_gandhi", new LatLng(19.423900501486315, -99.1808237382227)),
                new Station("mb_chapultepec", new LatLng(19.42373208136781, -99.17435611583426)),
                new Station("mb_ladiana", new LatLng(19.42457860428395, -99.17250565546465)),
                new Station("mb_elangel", new LatLng(19.42648027286321, -99.16858599578681)),
                new Station("mb_lapalma", new LatLng(19.42808934629859, -99.1650663008001)),
                new Station("mb_hamburgo", new LatLng(19.4291860080615, -99.16278937953517)),
                new Station("mb_reforma", new LatLng(19.431117313189223, -99.15853507193985)),
                new Station("mb_paris", new LatLng(19.43242370319829, -99.15593743591516)),
                new Station("mb_glorietadecolon", new LatLng(19.433420438763065, -99.15384496445644)),
                new Station("mb_elcaballito", new LatLng(19.434937908221045, -99.15034342582112)),
                new Station("mb_hidalgo", new LatLng(19.43669575120335, -99.14753542418293)),
                new Station("mb_glorietavioleta", new LatLng(19.43966472189193, -99.1439111039471)),
                new Station("mb_garibaldi", new LatLng(19.443718983681322, -99.13916985789537)),
                new Station("mb_glorietacuitlahuac", new LatLng(19.447253922315046, -99.13557101591945)),
                new Station("mb_tresculturas", new LatLng(19.450448963516234, -99.13280769118457)),
                new Station("mb_peralvillo", new LatLng(19.452772866036728, -99.13096043393874)),
                new Station("mb_mercadobethoveen", new LatLng(19.4576591292803, -99.12893981283247)),
                new Station("mb_misterios", new LatLng(19.461061828323658, -99.12764366977802)),
                new Station("mb_clave", new LatLng(19.46441424737429, -99.12637615136894)),
                new Station("mb_roblesdominguez", new LatLng(19.467028378380096, -99.12539262657565)),
                new Station("mb_excelsior", new LatLng(19.470883103644038, -99.123931925448)),
                new Station("mb_necaxa", new LatLng(19.47560038175099, -99.12216320982274)),
                new Station("mb_avenidatalisman", new LatLng(19.478479362937016, -99.12105343276001)),
                new Station("mb_garrido", new LatLng(19.483468578820197, -99.11917623603605)),
                new Station("mb_delegaciongustavoamadero", new LatLng(19.483522560607664, -99.11388618414605)),
                new Station("mb_hospitalinfantillavilla", new LatLng(19.48720484429382, -99.11382185829103))));


        metrobusSystem.getLines().add(new TransportingLine("#f59120",
                new Station("mb_buenavista", new LatLng(19.444910308728836, -99.15232113576118)),
                new Station("mb_delcuautemoc", new LatLng(19.44276018267378, -99.15245966149378)),
                new Station("mb_puentedealvarado", new LatLng(19.439140957151757, -99.15327167095548)),
                new Station("mb_plazarepublica", new LatLng(19.43680236345268, -99.15422926754955)),
                new Station("mb_glorietadecolon", new LatLng(19.434042427521767, -99.15350986510593)),
                new Station("mb_exporeforma", new LatLng(19.433113082230538, -99.15060783181686)),
                new Station("mb_voca5", new LatLng(19.431558380362628, -99.1501699578946)),
                new Station("mb_juarez", new LatLng(19.431330105242065, -99.14837894263539)),
                new Station("mb_plazasnjuan", new LatLng(19.43060931675119, -99.14355015907705)),
                new Station("mb_ejecentral", new LatLng(19.430377525916068, -99.14191152133674)),
                new Station("mb_salvador", new LatLng(19.430039908468807, -99.13961229034508)),
                new Station("mb_isabela", new LatLng(19.42971442280223, -99.13722516981686)),
                new Station("mb_museociudad", new LatLng(19.42911687960208, -99.13305715356918)),
                new Station("mb_pinosuarez", new LatLng(19.426401677331466, -99.13235744372234)),
                new Station("mb_lascruces", new LatLng(19.426156624831016, -99.12992580422132)),
                new Station("mb_merced", new LatLng(19.425533495290207, -99.1259462445595)),
                new Station("mb_mercadosonora", new LatLng(19.423258885952112, -99.12407620246105)),
                new Station("mb_ceciliorobelo", new LatLng(19.425374605817353, -99.11979733262731)),
                new Station("mb_ingedmolina", new LatLng(19.42704388901871, -99.11520329211422)),
                new Station("mb_moctezuma", new LatLng(19.42690618162067, -99.11174644993201)),
                new Station("mb_sanlazaro", new LatLng(19.43094360876617, -99.11517502014988)),
                new Station("mb_ingedmolina", new LatLng(19.42704388901871, -99.11520329211422)),
                new Station("mb_hospitalbalbuena", new LatLng(19.424910591490224, -99.11467919069155)),
                new Station("mb_ceciliorobelo", new LatLng(19.425582571206704, -99.11958991074553)),
                new Station("mb_mercadosonora", new LatLng(19.42326971581045, -99.12341419493939)),
                new Station("mb_merced", new LatLng(19.42513299426325, -99.12553915619567)),
                new Station("mb_circunvalacion", new LatLng(19.42840672716141, -99.12584970113551)),
                new Station("mb_lascruces", new LatLng(19.428645991633655, -99.12914583125338)),
                new Station("mb_museociudad", new LatLng(19.42909258561528, -99.1323668285596)),
                new Station("mb_isabela", new LatLng(19.429699904915037, -99.13658409514623)),
                new Station("mb_salvador", new LatLng(19.430199852394644, -99.14015316669855)),
                new Station("mb_ejecentral", new LatLng(19.430450833922084, -99.14191614652495)),
                new Station("mb_plazasnjuan", new LatLng(19.43065835915459, -99.14358050620852)),
                new Station("mb_juarez", new LatLng(19.43123674608587, -99.14758725495354)),
                new Station("mb_voca5", new LatLng(19.431767658833813, -99.1509740739983)),
                new Station("mb_exporeforma", new LatLng(19.433096162597526, -99.15060478663656)),
                new Station("mb_glorietadecolon", new LatLng(19.434557237065178, -99.1534312522773)),
                new Station("mb_plazarepublica", new LatLng(19.437532611427898, -99.15380123341589)),
                new Station("mb_puentedealvarado", new LatLng(19.439525826067076, -99.15304947517005)),
                new Station("mb_delcuautemoc", new LatLng(19.4421555046331, -99.15244401153082)),
                new Station("mb_buenavista", new LatLng(19.444898632371192, -99.15231415112473))));


        metrobusSystem.getLines().add(new TransportingLine("#f59120",
                new Station("mb_buenavista", new LatLng(19.444910308728836, -99.15232113576118)),
                new Station("mb_delcuautemoc", new LatLng(19.44276018267378, -99.15245966149378)),
                new Station("mb_puentedealvarado", new LatLng(19.439140957151757, -99.15327167095548)),
                new Station("mb_sancarlos", new LatLng(19.438054379921635, -99.149935789642)),
                new Station("mb_hidalgo", new LatLng(19.437304432423353, -99.14605865639992)),
                new Station("mb_bellasartes", new LatLng(19.43639133824333, -99.1417939441731)),
                new Station("mb_teatroblanquita", new LatLng(19.438443769740935, -99.13908082088282)),
                new Station("mb_chile", new LatLng(19.43797579105113, -99.13590439195613)),
                new Station("mb_argentina", new LatLng(19.437400739731892, -99.1316730081128)),
                new Station("mb_mercadoabelardo_l_rodriguez", new LatLng(19.436864852942954, -99.12793197426979)),
                new Station("mb_mixcalco", new LatLng(19.43642629709517, -99.12429189306457)),
                new Station("mb_ferrocarrildecintura", new LatLng(19.436120675744505, -99.12150321171741)),
                new Station("mb_morelos", new LatLng(19.435881728534774, -99.11899114299996)),
                new Station("mb_archivogn", new LatLng(19.435427122703683, -99.11552557046132)),
                new Station("mb_sanlazaro", new LatLng(19.430582446508552, -99.11551655154668)),
                new Station("mb_archivogn", new LatLng(19.43511950689561, -99.11462989473317)),
                new Station("mb_morelos", new LatLng(19.43583136759994, -99.11845075641283)),
                new Station("mb_ferrocarrildecintura", new LatLng(19.436125687776588, -99.12090927664957)),
                new Station("mb_mixcalco", new LatLng(19.436412008426966, -99.12363594803136)),
                new Station("mb_mercadoabelardo_l_rodriguez", new LatLng(19.436875280664026, -99.12716167277348)),
                new Station("mb_argentina", new LatLng(19.437414407960148, -99.131333064646)),
                new Station("mb_chile", new LatLng(19.437982904353188, -99.13547894120973)),
                new Station("mb_teatroblanquita", new LatLng(19.438671650254506, -99.14002095703391)),
                new Station("mb_bellasartes", new LatLng(19.43639133824333, -99.1417939441731)),
                new Station("mb_hidalgo", new LatLng(19.437304432423353, -99.14605865639992)),
                new Station("mb_sancarlos", new LatLng(19.43819355987799, -99.1502600295609)),
                new Station("mb_puentedealvarado", new LatLng(19.439525826067076, -99.15304947517005)),
                new Station("mb_delcuautemoc", new LatLng(19.4421555046331, -99.15244401153082)),
                new Station("mb_buenavista", new LatLng(19.444898632371192, -99.15231415112473))));

        metrobusSystem.getLines().add(new TransportingLine("#0d1b5f",
                new Station("mb_riodelosremedios", new LatLng(19.507059186511434, -99.08597083180229)),
                new Station("mb_314memorialnewsdivine", new LatLng(19.50088081663687, -99.0889063985183)),
                new Station("mb_5demayo", new LatLng(19.497164515734106, -99.09043021430304)),
                new Station("mb_vascodequiroga", new LatLng(19.49333774314441, -99.09204027573985)),
                new Station("mb_elcoyol", new LatLng(19.487443650728917, -99.09447016310038)),
                new Station("mb_preparatoria3", new LatLng(19.483587275164425, -99.09617907627181)),
                new Station("mb_sanjuandearagon", new LatLng(19.479952904171125, -99.09773193499534)),
                new Station("mb_rioguadalupe", new LatLng(19.476064040445607, -99.09930323987855)),
                new Station("mb_talisman", new LatLng(19.471070247260712, -99.10134219606464)),
                new Station("mb_victoria", new LatLng(19.467414038999735, -99.10291211432512)),
                new Station("mb_oriente101", new LatLng(19.46097556858533, -99.10564751284977)),
                new Station("mb_riosantacoleta", new LatLng(19.456078167917216, -99.10756707604119)),
                new Station("mb_rioconsulado", new LatLng(19.45378340651417, -99.10854309378837)),
                new Station("mb_canaldelnorte", new LatLng(19.449601707572604, -99.11036892941934)),
                new Station("mb_deportivoeduardomolina", new LatLng(19.44471746133897, -99.11214569724073)),
                new Station("mb_mercadomorelos", new LatLng(19.441274178695114, -99.11343990362012)),
                new Station("mb_archivogn", new LatLng(19.437220349532435, -99.11440180710737)),
                new Station("mb_sanlazaro", new LatLng(19.431196795048123, -99.11522663560962)),
                new Station("mb_moctezuma", new LatLng(19.42558188987155, -99.11150372542355)),
                new Station("mb_venustianocarranza", new LatLng(19.41836852350874, -99.11222239905757)),
                new Station("mb_avenidadeltaller", new LatLng(19.412933829375277, -99.11277592822842)),
                new Station("mb_mixiuhca", new LatLng(19.407894874951452, -99.11333349278141)),
                new Station("mb_hospitalgeneraltroncoso", new LatLng(19.405512305304907, -99.11357266168014)),
                new Station("mb_coyuya", new LatLng(19.399393536078147, -99.11356595975346)),
                new Station("mb_recreo", new LatLng(19.39389366543204, -99.11368230887895)),
                new Station("mb_oriente116", new LatLng(19.390261458985012, -99.11266382836388)),
                new Station("mb_colegiodebachilleres", new LatLng(19.38631337747522, -99.11173941535773)),
                new Station("mb_canalapatlaco", new LatLng(19.38278973096204, -99.11095120240005)),
                new Station("mb_apatlaco", new LatLng(19.378055100697082, -99.10907333211716)),
                new Station("mb_aculco", new LatLng(19.3749014915171, -99.10808475487194)),
                new Station("mb_churubuscooriente", new LatLng(19.3715849267511, -99.10785979007758)),
                new Station("mb_escuadron201", new LatLng(19.366062455063705, -99.10919643888393)),
                new Station("mb_atanasiogsarabia", new LatLng(19.361255531042985, -99.11030306610259)),
                new Station("mb_ermitaiztapalapa", new LatLng(19.35663172815221, -99.11133897595208)),
                new Station("mb_ganaderos", new LatLng(19.35037507301448, -99.11098826117109)),
                new Station("mb_pueblodelosreyes", new LatLng(19.34797686306451, -99.11134309023878)),
                new Station("mb_barriosanantonio", new LatLng(19.343128044566907, -99.11244373916578)),
                new Station("mb_calzadatasquena", new LatLng(19.33875275915406, -99.11300527861107)),
                new Station("mb_cafetales", new LatLng(19.333713492130958, -99.11315474110563)),
                new Station("mb_esimeculhuacan", new LatLng(19.32924252020464, -99.11352708337532)),
                new Station("mb_manuelasaenz", new LatLng(19.324204300313916, -99.11397285812114)),
                new Station("mb_lavirgen", new LatLng(19.320678683851842, -99.113621100777)),
                new Station("mb_tepetlapa", new LatLng(19.316335427024967, -99.1116289829983)),
                new Station("mb_lasbombas", new LatLng(19.31182552810548, -99.11063367810921)),
                new Station("mb_vistahermosa", new LatLng(19.30604018833728, -99.11196194149868)),
                new Station("mb_calzadadelhueso", new LatLng(19.300884710323192, -99.11354073857363)),
                new Station("mb_canaverales", new LatLng(19.29596135615951, -99.11440173129343)),
                new Station("mb_muyuguarda", new LatLng(19.285190720683637, -99.11548617045814)),
                new Station("mb_circuitocuemanco", new LatLng(19.28064878224402, -99.11687482168782)),
                new Station("mb_difxochimilco", new LatLng(19.27747062884611, -99.11808742913978)),
                new Station("mb_preparatoria1", new LatLng(19.273597457503755, -99.12040242123227)),
                new Station("mb_difxochimilco", new LatLng(19.27747062884611, -99.11808742913978)),
                new Station("mb_circuitocuemanco", new LatLng(19.28064878224402, -99.11687482168782)),
                new Station("mb_muyuguarda", new LatLng(19.285190720683637, -99.11548617045814)),
                new Station("mb_canaverales", new LatLng(19.29596135615951, -99.11440173129343)),
                new Station("mb_calzadadelhueso", new LatLng(19.300884710323192, -99.11354073857363)),
                new Station("mb_vistahermosa", new LatLng(19.30604018833728, -99.11196194149868)),
                new Station("mb_lasbombas", new LatLng(19.31182552810548, -99.11063367810921)),
                new Station("mb_tepetlapa", new LatLng(19.316335427024967, -99.1116289829983)),
                new Station("mb_lavirgen", new LatLng(19.320678683851842, -99.113621100777)),
                new Station("mb_manuelasaenz", new LatLng(19.324204300313916, -99.11397285812114)),
                new Station("mb_esimeculhuacan", new LatLng(19.32924252020464, -99.11352708337532)),
                new Station("mb_cafetales", new LatLng(19.333713492130958, -99.11315474110563)),
                new Station("mb_calzadatasquena", new LatLng(19.33875275915406, -99.11300527861107)),
                new Station("mb_barriosanantonio", new LatLng(19.343128044566907, -99.11244373916578)),
                new Station("mb_pueblodelosreyes", new LatLng(19.34797686306451, -99.11134309023878)),
                new Station("mb_ganaderos", new LatLng(19.350324010685227, -99.11084701333685)),
                new Station("mb_ermitaiztapalapa", new LatLng(19.35663172815221, -99.11133897595208)),
                new Station("mb_atanasiogsarabia", new LatLng(19.361255531042985, -99.11030306610259)),
                new Station("mb_escuadron201", new LatLng(19.366062455063705, -99.10919643888393)),
                new Station("mb_churubuscooriente", new LatLng(19.3715849267511, -99.10785979007758)),
                new Station("mb_aculco", new LatLng(19.37492175792812, -99.10793532283165)),
                new Station("mb_apatlaco", new LatLng(19.37809082689387, -99.1090230721555)),
                new Station("mb_canalapatlaco", new LatLng(19.382824616354462, -99.11082912974781)),
                new Station("mb_colegiodebachilleres", new LatLng(19.386318020134688, -99.11162556391332)),
                new Station("mb_oriente116", new LatLng(19.390261458985012, -99.11266382836388)),
                new Station("mb_recreo", new LatLng(19.39379303407574, -99.11356770919275)),
                new Station("mb_coyuya", new LatLng(19.39938330126516, -99.11347605802946)),
                new Station("mb_hospitalgeneraltroncoso", new LatLng(19.405512305304907, -99.11357266168014)),
                new Station("mb_mixiuhca", new LatLng(19.407894874951452, -99.11333349278141)),
                new Station("mb_avenidadeltaller", new LatLng(19.412933829375277, -99.11277592822842)),
                new Station("mb_venustianocarranza", new LatLng(19.41836852350874, -99.11222239905757)),
                new Station("mb_moctezuma", new LatLng(19.42558188987155, -99.11150372542355)),
                new Station("mb_sanlazaro", new LatLng(19.431217227139353, -99.11520580581092)),
                new Station("mb_archivogn", new LatLng(19.437220349532435, -99.11440180710737)),
                new Station("mb_mercadomorelos", new LatLng(19.441274178695114, -99.11343990362012)),
                new Station("mb_deportivoeduardomolina", new LatLng(19.44471746133897, -99.11214569724073)),
                new Station("mb_canaldelnorte", new LatLng(19.449601707572604, -99.11036892941934)),
                new Station("mb_rioconsulado", new LatLng(19.45359792396771, -99.10834332154182)),
                new Station("mb_riosantacoleta", new LatLng(19.455988396676386, -99.10740939226235)),
                new Station("mb_oriente101", new LatLng(19.46082316591124, -99.10541872533165)),
                new Station("mb_victoria", new LatLng(19.46733618098202, -99.10268628757676)),
                new Station("mb_talisman", new LatLng(19.471003264900926, -99.10110114103936)),
                new Station("mb_rioguadalupe", new LatLng(19.476050040662376, -99.09896436314483)),
                new Station("mb_sanjuandearagon", new LatLng(19.479831679522928, -99.09730355354587)),
                new Station("mb_preparatoria3", new LatLng(19.483318464294967, -99.09583505654427)),
                new Station("mb_elcoyol", new LatLng(19.487462675479982, -99.09401221852599)),
                new Station("mb_vascodequiroga", new LatLng(19.493191895025422, -99.09159264676016)),
                new Station("mb_5demayo", new LatLng(19.496987213953513, -99.08996638634214)),
                new Station("mb_314memorialnewsdivine", new LatLng(19.500538006303934, -99.08847753790779)),
                new Station("mb_riodelosremedios", new LatLng(19.507059186511434, -99.08597083180229))));

        metrobusSystem.getLines().add(new TransportingLine("#e10098",
                new Station("mb_elrosario", new LatLng(19.507237439242832, -99.19951831448402)),
                new Station("mb_colegiodebachilleres1", new LatLng(19.511632487092964, -99.19743234825266)),
                new Station("mb_delasculturas", new LatLng(19.51098222536512, -99.19266442983016)),
                new Station("mb_ferrocarrilesnacionales", new LatLng(19.504938845704075, -99.18890204878096)),
                new Station("mb_uamazcapotzalco", new LatLng(19.504010389019303, -99.18392954242631)),
                new Station("mb_tecnoparque", new LatLng(19.502504909725506, -99.17896105074233)),
                new Station("mb_norte59", new LatLng(19.49763574410027, -99.16559091305825)),
                new Station("mb_norte45", new LatLng(19.496351118249073, -99.15977494015543)),
                new Station("mb_montevideo", new LatLng(19.494900646976532, -99.1531303837254)),
                new Station("mb_lindavistavallejo", new LatLng(19.493792065587062, -99.14802478470781)),
                new Station("mb_institutodelpetroleo", new LatLng(19.493161490003395, -99.145143896973)),
                new Station("mb_sanbartolo", new LatLng(19.49199743087135, -99.13907273041663)),
                new Station("mb_institutopolitecniconacional", new LatLng(19.49043108808416, -99.13267107047935)),
                new Station("mb_riobamba", new LatLng(19.488671170742755, -99.12794577492788)),
                new Station("mb_deportivo18demarzo", new LatLng(19.486623320328345, -99.1224482721597)),
                new Station("mb_lavilla", new LatLng(19.485437913429372, -99.1196346452385)),
                new Station("mb_delegaciongustavoamadero", new LatLng(19.4838661141403, -99.11233817453021)),
                new Station("mb_martincarrera", new LatLng(19.482066754656124, -99.10535102357585)),
                new Station("mb_hospitalgenerallavilla", new LatLng(19.480359232220902, -99.10124212357078)),
                new Station("mb_sanjuandearagon", new LatLng(19.478754981086485, -99.09714426544886)),
                new Station("mb_grancanal", new LatLng(19.477024450935836, -99.09283451381454)),
                new Station("mb_casasaleman", new LatLng(19.474295147663366, -99.08833466151296)),
                new Station("mb_pueblosanjuandearagon", new LatLng(19.471128314661186, -99.08641136052856)),
                new Station("mb_loretafabela", new LatLng(19.467379687561564, -99.08086006629428)),
                new Station("mb_482", new LatLng(19.46690395205443, -99.07553011864741)),
                new Station("mb_414", new LatLng(19.468393545658433, -99.07165992395014)),
                new Station("mb_416oriente", new LatLng(19.471140394278034, -99.07096389742682)),
                new Station("mb_lapradera", new LatLng(19.473113545231666, -99.06869639017334)),
                new Station("mb_colegiodebachilleres9", new LatLng(19.46974335783011, -99.06497348447932)),
                new Station("mb_franciscomorazan", new LatLng(19.46713330325543, -99.06195631531482)),
                new Station("mb_villadearagon", new LatLng(19.464581428809996, -99.05904920012222)),
                new Station("mb_franciscomorazan", new LatLng(19.46713330325543, -99.06195631531482)),
                new Station("mb_colegiodebachilleres9", new LatLng(19.46974335783011, -99.06497348447932)),
                new Station("mb_lapradera", new LatLng(19.473113545231666, -99.06869639017334)),
                new Station("mb_volcandefuego", new LatLng(19.47703997466132, -99.07261654679894)),
                new Station("mb_ampliacionprovidencia", new LatLng(19.47985044588391, -99.0753663269675)),
                new Station("mb_deportivolosgaleana", new LatLng(19.478528446005587, -99.0779671520403)),
                new Station("mb_416poniente", new LatLng(19.472563726224514, -99.07933214350915)),
                new Station("mb_loretafabela", new LatLng(19.467379687561564, -99.08086006629428)),
                new Station("mb_pueblosanjuandearagon", new LatLng(19.471128314661186, -99.08641136052856)),
                new Station("mb_casasaleman", new LatLng(19.474295147663366, -99.08833466151296)),
                new Station("mb_grancanal", new LatLng(19.477024450935836, -99.09283451381454)),
                new Station("mb_sanjuandearagon", new LatLng(19.478754981086485, -99.09714426544886)),
                new Station("mb_hospitalgenerallavilla", new LatLng(19.480359232220902, -99.10124212357078)),
                new Station("mb_martincarrera", new LatLng(19.482066754656124, -99.10535102357585)),
                new Station("mb_delegaciongustavoamadero", new LatLng(19.4838661141403, -99.11233817453021)),
                new Station("mb_hospitalinfantillavilla", new LatLng(19.487995051659798, -99.1147333923599)),
                new Station("mb_delosmisterios", new LatLng(19.487178278761466, -99.11789944850844)),
                new Station("mb_lavilla", new LatLng(19.485437913429372, -99.1196346452385))));

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        lineMetroView = new LineMetroView(this);
        metroStationSurfaceView = new MetroStationSurfaceView(this);


        preview = (FrameLayout) findViewById(R.id.map_preview);
        preview.addView(mapView);
        preview.addView(lineMetroView);
        preview.addView(metroStationSurfaceView);
        preview.setZ(0.0f);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        autoCompleteTextViewStation = findViewById(R.id.autoCompleteTextViewStation);
        List<Station> autoCompleteList = new ArrayList<>();
        for (TransportingLine metroLine : metroSystem.getLines()) {
            for (Station station : metroLine.getStations()) {
                autoCompleteList.add(station);
            }
        }

        for (TransportingLine metroLine : metrobusSystem.getLines()) {
            for (Station station : metroLine.getStations()) {
                autoCompleteList.add(station);
            }
        }

        AutoCompleteAdapter adapter = new AutoCompleteAdapter
                (this,R.layout.autocompletetext_row,autoCompleteList);
        autoCompleteTextViewStation.setThreshold(1);
        autoCompleteTextViewStation.setAdapter(adapter);
        autoCompleteTextViewStation.setTextColor(Color.RED);
        autoCompleteTextViewStation.setZ(1.0f);

        autoCompleteTextViewStation.setOnItemClickListener((adapterView, view, position, id) -> {
            Station item = (Station)adapterView.getAdapter().getItem(position);
            Toast.makeText(getApplicationContext(),item.getName(),Toast.LENGTH_LONG).show();

        });
    }

    @Override
    protected void onResume() {
        super.onResume();




        mapView.onResume();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ZOCALO_LOCATION));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f));

        mMap.setOnCameraMoveListener(this);

        Marker markerPerth = mMap.addMarker(new MarkerOptions()
                .position(ZOCALO_LOCATION)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_icon)));

        Marker markerPerth2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.398221331566152, -99.20025024991276))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_icon)));

        markerPerth.setTag(new Station("zocalo", ZOCALO_LOCATION));

        markerPerth2.setTag(new Station("observatorio",
                new LatLng(19.398221331566152, -99.20025024991276)));

        mMap.setOnMarkerClickListener(this);
        ThreadUpdate threadUpdate = new ThreadUpdate();
        threadUpdate.execute();




    }

    @Override
    public void onCameraMove() {

        lineMetroView.setProjection(mMap.getProjection());
        lineMetroView.invalidate();
        metroStationSurfaceView.setProjection(mMap.getProjection());
        metroStationSurfaceView.setZoom(mMap.getCameraPosition().zoom);
        metroStationSurfaceView.invalidate();
        Log.d("DEBUG-->","zoom("+mMap.getCameraPosition().zoom+")");

    }


    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Station stationTag = (Station) marker.getTag();

        Log.d("DEBUG->","tag name"+stationTag.getName());

        return false;
    }

    class LineMetroView extends View {

        private Projection projection;
        private Rect rect;
        private Point point;
        private int width;
        private int height;

        private double currentLatitude;
        private double currentLongitude;
        private double currentCameraAngle;

        public LineMetroView(Context context) {
            super(context);
        }


        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.projection != null) {
                if(metroSystem.isEnable()) {
                    for (TransportingLine metroLine : metroSystem.getLines()) {
                        Paint paintColor = metroLine.getPaintColor();
                        paintColor.setStrokeWidth(20);
                        paintColor.setAlpha(64);
                        for (int index = 0; index < metroLine.getStations().size() - 1; index++) {

                            Point screenPosition = projection.toScreenLocation(metroLine.getStations().get(index).getLatLng());
                            Point screenPosition1 = projection.toScreenLocation(metroLine.getStations().get(index + 1).getLatLng());
                            canvas.drawLine(screenPosition.x, screenPosition.y,
                                    screenPosition1.x, screenPosition1.y, paintColor);

                        }
                    }
                }
                if(metrobusSystem.isEnable()) {
                    for (TransportingLine metroLine : metrobusSystem.getLines()) {
                        Paint paintColor = metroLine.getPaintColor();
                        paintColor.setStrokeWidth(20);
                        paintColor.setAlpha(64);
                        for (int index = 0; index < metroLine.getStations().size() - 1; index++) {

                            Point screenPosition = projection.toScreenLocation(metroLine.getStations().get(index).getLatLng());
                            Point screenPosition1 = projection.toScreenLocation(metroLine.getStations().get(index + 1).getLatLng());
                            canvas.drawLine(screenPosition.x, screenPosition.y,
                                    screenPosition1.x, screenPosition1.y, paintColor);

                        }
                    }
                }
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            width = w;
            height = h;
        }

        public void setProjection(Projection projection) {
            this.projection = projection;

        }
    }

    class MetroStationSurfaceView extends GLSurfaceView {

        private final OpenGLRenderer mRenderer;

        private OpenGLObjectRenderer openGLObjectRenderer;

        public MetroStationSurfaceView(Context context) {
            super(context);

            setEGLContextClientVersion(2);
            setZOrderOnTop(true);

            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            getHolder().setFormat(PixelFormat.RGBA_8888);

            getHolder().setFormat(PixelFormat.TRANSLUCENT);

            mRenderer = new OpenGLRenderer(context, metroSystem, metrobusSystem);
            setRenderer(mRenderer);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        public void setProjection(Projection projection) {
            mRenderer.setProjection(projection);

        }

        public void setZoom(float zoom) {
            mRenderer.setZoom(zoom);
        }


    }

    class ThreadUpdate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            lineMetroView.setProjection(mMap.getProjection());
            lineMetroView.invalidate();


            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);

            }

        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,},
                        100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED
                        && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    closeApplication();
                }
                break;
        }
    }

    private void closeApplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

}
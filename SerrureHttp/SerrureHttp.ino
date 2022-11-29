
/*

Programme pour test fiabilité des serrures DX200I

Description du protocole

*/

const int R1=22;  // Cablâge: Sortie Digital 22 - relais 1
const int R2=23;  // Cablâge: Sortie Digital 23 - relais 2
const int R3=29;  // Cablâge: Sortie Digital 29 - relais 3
const int R4=39;  // Cablâge: Sortie Digital 39
const int R5=49;  // Cablâge: Sortie Digital 49
const int R6=51;  // Cablâge: Sortie Digital 51 - Led temoin marche / arrêt
const int R7=52;  // Cablâge: Sortie Digital 52
const int R8=53;  // Cablâge: Sortie Digital 53

const int I1 = 9; // Entrée sensor 1
const int I2 = 8; // Entrée contact 1
const int I3 = 7; // Entrée sensor 2
const int I4 = 6; // Entrée contact 2
const int I5 = 5; // Entrée sensor 3
const int I6 = 4; // Entrée contact 3
const int I7 = 3;
const int I8 = 2;

int e1;
int e2;
int e3;
int e4;
int e5;
int e6;
int e7;
int e8;

boolean sonorite;         // Flag de validation du buzzer
boolean marche = false;   // Indique si le test est en cours (marche = true) ou s'il est arrêté
boolean pause = false;    // Flag état de pause
boolean manuel = false;      // Mode de marche: automatique ou manuel

// Variables de test

int ECHANTILLONS = 3;      // Nombre d'emplacement d'échantillon sur le banc de test
int TEMPO = 10000;         // Varialble en fonction de la cadence
int CADENCE1 = 1000;
int CADENCE2 = 1500;
int CADENCE3 = 2000;

boolean actifs[3] =  {false, false, false};
boolean erreurs[3] = {false, false, false};
boolean pauses[3] = {false, false, false};
boolean stops[3] = {false, false, false};
long totaux[3] = {0,0,0};        // compteurs de séquences par échantillons


int relais[3] = {R1, R2, R3};
int sensors[3] = {I1, I3, I5};
int contacts[3] = {I2, I4, I6};


// the setup function runs once when you press reset or power the board
void setup()
{

    pinMode(R1, OUTPUT);
    pinMode(R2, OUTPUT);
    pinMode(R3, OUTPUT);
    pinMode(R4, OUTPUT);
    pinMode(R5, OUTPUT);
    pinMode(R6, OUTPUT);
    pinMode(R7, OUTPUT);
    pinMode(R8, OUTPUT);


    pinMode(I1, INPUT);
    pinMode(I2, INPUT);
    pinMode(I3, INPUT);
    pinMode(I4, INPUT);
    pinMode(I5, INPUT);
    pinMode(I6, INPUT);
    pinMode(I7, INPUT);
    pinMode(I8, INPUT);

// Initialisation du port série

    Serial.begin(9600);

}

// the loop function runs over and over again forever


void loop()
{

    lecture();
    simulationCycle();


}// fin loop



// *******************  Déclarations fonctions externes ***********************

void lecture()
{

    String reception;
    reception = Serial.readString();
    reception.trim();

    if (reception == "W:0")    // Demande lancement de test - ordre de démarrage
    {

        if (!pause)
        {

            Serial.print(String("@:LANCEMENT TEST"));
            
        }

        else
        {

            Serial.print(String("@:SORTIE DE PAUSE"));
        }

        marche = true;
        pause = false;
        simulationCycle();

    }


    if (reception == "W:1")    // Ordre de mise à l'arrêt
    {

        Serial.print(String("@ARRET DU TEST"));
        marche = false;
        pause = false;


    }

    if (reception == "W:2")    // Ordre de mise en pause
    {

        Serial.print(String("@:TEST EN PAUSE"));
        marche = true;
        pause = true;
    }

    if (reception.startsWith("W:CONFIG:"))    //  Configuration de test
    {


        Serial.print(String("@:CONFIGURATION OK"));
        Serial.print(String("@ACQ"));
        char actEch1 = reception.charAt(9);
        char actEch2 = reception.charAt(11);
        char actEch3 = reception.charAt(13);
        char cadence = reception.charAt(15);
        char modeMarche = reception.charAt(17);
        if(actEch1 == '0')
        {

            actifs[0] = false;
        }

        if(actEch1 == '1')
        {

            actifs[0] = true;
        }

        if(actEch2 == '0')
        {

            actifs[1] = false;
        }

        if(actEch2 == '1')
        {

            actifs[1] = true;
        }
        if(actEch3 == '0')
        {

            actifs[2] = false;
        }

        if(actEch3 == '1')
        {

            actifs[2] = true;
        }


          if(cadence == '1'){

            TEMPO = CADENCE1;
        }


          if(cadence == '2'){

            TEMPO = CADENCE2;
        }


          if(cadence == '3'){

            TEMPO = CADENCE3;
        }

          if(modeMarche == '0'){

            manuel = true;
        }

         if(modeMarche == '1'){

            manuel = false;
        }

    }

    if (reception == "W:FERMER")          // Ordre de finalisation du test
    {


        Serial.print(String("@FERMETURE"));

    }


    if (reception == "W:RAZ:1")   // Ordre de reset compteur échantillon 1
    {

        Serial.print(String("@:RESET COMPTEUR ECH1"));
        totaux[0] = 0;


    }

    if (reception == "W:RAZ:2")   // Ordre de reset compteur échantillon 2
    {

        Serial.print(String("@:RESET COMPTEUR ECH2"));
        totaux[1] = 0;

    }

    if (reception == "W:RAZ:3")   // Ordre de reset compteur échantillon 3
    {

        Serial.print(String("@:RESET COMPTEUR ECH3"));
        totaux[2] = 0;


    }


    if (reception == "W:STOP:1")   // Ordre d'arrêt échantillon 1
    {

        Serial.print(String("@:ARRET ECH 1"));
        stops[0] = 0;


    }

    if (reception == "W:STOP:2")   // Ordre d'arrêt échantillon 2
    {

        Serial.print(String("@:ARRET ECH 2"));
        stops[1] = 0;

    }

    if (reception == "W:STOP:3")   // Ordre d'arrêt échantillon 3
    {

        Serial.print(String("@:ARRET ECH 3"));
        stops[2] = 0;


    }


    if (reception == "W:PAUSE:1")   // Ordre d'arrêt échantillon 1
    {

        Serial.print(String("@:PAUSE ECH 1"));
        pauses[0] = 0;


    }


    if (reception == "W:PAUSE:2")   // Ordre d'arrêt échantillon 2
    {

        Serial.print(String("@:PAUSE ECH 2"));
        pauses[1] = 0;


    }


    if (reception == "W:PAUSE:3")   // Ordre d'arrêt échantillon 3
    {

        Serial.print(String("@:PAUSE ECH 3"));
        pauses[2] = 0;


    }

    if (reception.startsWith("W:SET:"))    //  Réinitialisation compteur ech
    {
        char num = reception.charAt(6);
        String compteur = reception.substring(8);
        Serial.println(compteur);
        int numEch = (int)(num);

        char arr[compteur.length() + 1];
        strcpy(arr, compteur.c_str());

        char *ptr;
        long ret;

        ret = strtol(arr, &ptr, 10);

        totaux[numEch] = ret;

        Serial.println("Résultat:");
        Serial.print(ret);

    }


    if (reception == ("W:CADENCE:1"))    //  Réception cadence 1
    {

        Serial.print(String("@ACQ"));
        TEMPO = CADENCE1;


    }




    if (reception == ("W:CADENCE:2"))    //  Réception cadence 2
    {

        Serial.print(String("@ACQ"));
        TEMPO = CADENCE2;

    }

    if (reception == ("W:CADENCE:3"))    //  Réception cadence 3
    {

        Serial.print(String("@ACQ"));
        TEMPO = CADENCE3;

    }



} //*** Fin lecteur()



//******************************************************************************************




void cycle()
{

    if (!pause || !marche)
    {

        for(int i=0; i<ECHANTILLONS; i++)
        {

            if (!erreurs[i] && actifs[i])
            {

                digitalWrite(relais[i], HIGH);

                delay(500);
                digitalWrite(relais[i], LOW);
                delay(2000);
                e1 = digitalRead(sensors[i]);    // Lecture entrée I1
                if (e1 != LOW)     // si erreur sur sensor
                {

                    erreurs[i] = true;
                    actifs[i] = false;

                    Serial.print("@:ERREUR:SR:" + String(i+1));

                    sonorite = true;

                }

                e2 = digitalRead(contacts[i]);    // Lecture entrée I2

                if (e2 != LOW)      // si erreur sur contact porte
                {

                    erreurs[i] = true;
                    actifs[i] = false;

                    Serial.print("@:ERREUR:CP:" + String(i+1));
                    //start = false;
                    sonorite = true;

                }
                delay(5000);

                totaux[i]++;
                String info = "@TOTAL ECH #" + String(i+1) + ": "  + String(totaux[i]);
                Serial.print(info);
            }

        }
        delay(3000);
        transfertActifs();
        Serial.print("@SEQ");
        delay(1000);
    }

}

//********************************************************************************

void transfertActifs()
{

    String listeActifs ="@ACTIFS";

    for (int i=0; i<ECHANTILLONS; i++)
    {

        if (actifs[i])
        {

            listeActifs = listeActifs + ":1";

        }
        else
        {

            listeActifs = listeActifs + ":0";

        }

    }

    Serial.print(listeActifs);
    delay(5000);

}


void simulationCycle()
{

    if (!pause || !marche)
    {

        for(int i=0; i<ECHANTILLONS; i++)
        {

            if (!erreurs[i] && actifs[i])
            {
                int r = random(0,20);
                if(r<3 || r>17){
                  
                  erreurs[i] = false;
                  Serial.println("Erreur sur ech: " + String(i));
                  
                  }else{

                    totaux[i]++;
                       Serial.println("Conforme ech: " + String(i));
                    
                    }
            }

        }
        delay(3000);
        transfertActifs();
        Serial.println("Actifs transmis");
        delay(1000);
         String info = "@TOTAL ECH #";
          for(int i=0; i<ECHANTILLONS; i++)
        { info = String(i) + ": "  + String(totaux[i]);}

        Serial.print(info);
        
        Serial.println("Rapport compteurs transmis");
        delay(1000);

         info = "@ERREUR ECH #";;
          for(int i=0; i<ECHANTILLONS; i++)
        { info = String(i) + ": "  + String(erreurs[i]);}
      
      
        Serial.print(info);
        Serial.println("Rapport erreurs transmis");
        delay(1000);
        Serial.print("@SEQ");
        
    }

}

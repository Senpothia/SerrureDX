
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
boolean fin = false;      // indique si tous les échantillons sont en défaut
boolean arretTest = false;

// Variables de test

int ECHANTILLONS = 3;      // Nombre d'emplacement d'échantillon sur le banc de test
int TEMPO = 10000;         // Variable en fonction de la cadence
int CADENCE1 = 2000;
int CADENCE2 = 3000;
int CADENCE3 = 5000;

boolean actifs[3] =  {false, false, false};
boolean erreurs[3] = {false, false, false};
boolean pauses[3] = {false, false, false};
boolean stops[3] = {false, false, false};
long totaux[3] = {0L,0L,0L};        // compteurs de séquences par échantillons


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
    pinMode(LED_BUILTIN, OUTPUT);



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
    randomSeed(analogRead(0));

}

// the loop function runs over and over again forever


void loop()
{
    fin = erreurs[0]||stops[0] && erreurs[1]||stops[1] && erreurs[2]||stops[2];
    if(fin && !arretTest){
      
      delay(2000);
      Serial.print("@FIN");
      arretTest = true;
      
    }
    
    lecture();
      if(marche && !pause  &&!fin)
    {
        simulationCycle();
    }

    


}// fin loop



// *******************  Déclarations fonctions externes ***********************

void lecture()
{

    String reception;
    reception = Serial.readString();
    reception.trim();

     if (reception == "W:NEW"){
      
       for (int i = 0; i < ECHANTILLONS; i++) {

            
            actifs[i] = false;
            erreurs[i] = false;
            totaux[i] = 0;
            marche = false;   // Indique si le test est en cours (marche = true) ou s'il est arrêté
            pause = false;    // Flag état de pause
            manuel = false;      // Mode de marche: automatique ou manuel
            fin = false;      // indique si tous les échantillons sont en défaut
        }

        return;
      }


     

    if (reception == "W:0")    // Demande lancement de test - ordre de démarrage
    {
      /*
        erreurs[0]  = false;
        erreurs[1]  = false;
        erreurs[2]  = false;
        */
        
        delay(1000);
        digitalWrite(LED_BUILTIN, HIGH);
        arretTest = false;
        marche = true;
        pause = false;
        fin = false;
        for(int i=0; i<ECHANTILLONS; i++){

          erreurs[i] = false;
          
          }
        Serial.print(String("@:ACQ"));
      //  Serial.println(String("Démarrage"));
        delay(500);
       // simulationCycle();
        return;

    }


    if (reception == "W:1")    // Ordre de mise à l'arrêt
    {

       // Serial.print(String("@ARRET DU TEST"));
        marche = false;
        pause = false;
        
        return;

    }

    if (reception == "W:2")    // Ordre de mise en pause
    {

        //Serial.print(String("@:TEST EN PAUSE"));
        marche = true;
        !pause;
        return;
    }

    if (reception.startsWith("W:CONFIG:"))    //  Configuration de test
    {
      
       // Ex de trâme: W:CONFIG:1:1:1:1:1
        // Serial.print(String("@:CONFIGURATION OK"));

         for(int i=0; i<ECHANTILLONS; i++){

          erreurs[i] = false;
          
          }
          
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


        if(cadence == '1')
        {

            TEMPO = CADENCE1;
        }


        if(cadence == '2')
        {

            TEMPO = CADENCE2;
        }


        if(cadence == '3')
        {

            TEMPO = CADENCE3;
        }

        if(modeMarche == '0')
        {

            manuel = true;
        }

        if(modeMarche == '1')
        {

            manuel = false;
        }
         return;

    }

    if (reception == "W:FERMER")          // Ordre de finalisation du test
    {


        Serial.print(String("@FERMER"));
        marche = false;
        pause = false;
        digitalWrite(LED_BUILTIN, LOW);
        return;

    }


    if (reception == "W:RAZ:1")   // Ordre de reset compteur échantillon 1
    {

        //Serial.print(String("@:RESET COMPTEUR ECH1"));
        totaux[0] = 0;
        return;


    }

    if (reception == "W:RAZ:2")   // Ordre de reset compteur échantillon 2
    {

        //Serial.print(String("@:RESET COMPTEUR ECH2"));
        totaux[1] = 0;
        return;

    }

    if (reception == "W:RAZ:3")   // Ordre de reset compteur échantillon 3
    {

        //Serial.print(String("@:RESET COMPTEUR ECH3"));
        totaux[2] = 0;
        return;


    }


    if (reception == "W:STOP:1")   // Ordre d'arrêt échantillon 1
    {

        //Serial.print(String("@:ARRET ECH 1"));
        stops[0] = true;
        return;


    }

    if (reception == "W:STOP:2")   // Ordre d'arrêt échantillon 2
    {

       //Serial.print(String("@:ARRET ECH 2"));
        stops[1] = true;
        return;

    }

    if (reception == "W:STOP:3")   // Ordre d'arrêt échantillon 3
    {

        //Serial.print(String("@:ARRET ECH 3"));
        stops[2] = true;
        return;


    }


    if (reception == "W:PAUSE:1")   // Ordre d'arrêt échantillon 1
    {

        //Serial.print(String("@:PAUSE ECH 1"));
        !pauses[0];
        return;


    }


    if (reception == "W:PAUSE:2")   // Ordre d'arrêt échantillon 2
    {

        //Serial.print(String("@:PAUSE ECH 2"));
        !pauses[1];
        return;


    }


    if (reception == "W:PAUSE:3")   // Ordre d'arrêt échantillon 3
    {

        //Serial.print(String("@:PAUSE ECH 3"));
        !pauses[2];
        return;


    }

    if (reception.startsWith("W:SET:"))    //  Réinitialisation compteur ech
    {
        // Exemple de trâme: W:SET:1:1233 fixe la valeur du compteur 1 à 1233
        char num = reception.charAt(6);
        String compteur = reception.substring(8);
       // Serial.print(compteur);
        int numEch = (int)(num);

        char arr[compteur.length() + 1];
        strcpy(arr, compteur.c_str());

        char *ptr;
        long ret;

        ret = strtol(arr, &ptr, 10);

        totaux[numEch] = ret;

       // Serial.print("Résultat:");
       // Serial.print(ret);
        return;

    }


    if (reception == ("W:CADENCE:1"))    //  Réception cadence 1
    {

        //Serial.print(String("@ACQ"));
        TEMPO = CADENCE1;
        return;


    }


    if (reception == ("W:CADENCE:2"))    //  Réception cadence 2
    {

       // Serial.print(String("@ACQ"));
        TEMPO = CADENCE2;
        return;

    }

    if (reception == ("W:CADENCE:3"))    //  Réception cadence 3
    {

       // Serial.print(String("@ACQ"));
        TEMPO = CADENCE3;
        return;

    }

    if (reception.startsWith("W:ACTIFS:"))    //  Réception liste des actifs
    {   
        // Exemple de trâme: W:ACTIFS:1:0:1
        
        char actEch1 = reception.charAt(9);
        char actEch2 = reception.charAt(11);
        char actEch3 = reception.charAt(13);

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


        Serial.print(String("@ACQ"));
        return;
    }

    if (reception.startsWith("W:TOTAL:#0"))    //  Réception des valeurs de compteurs
    {
       // Serial.println("total reçu");
        String valeurs = reception.substring(11);
        int taille = valeurs.length();
        int indices[2] = {0,0};
        String cpt1 = "";
        String cpt2 = "";
        String cpt3 = "";
        int detected = 0;
        int indice = 0;


        while(detected<2)
        {

            char c = valeurs.charAt(indice);
            if(c != ':')
            {

                indice++;

            }
            else
            {

                indices[detected] = indice;
                detected++;
                indice++;

            }
        }
        /*
        Serial.println("indices");
        Serial.println("valeurs: " + valeurs);
        Serial.println(indices[0]);
        Serial.println(indices[1]);
        */
        
        cpt1 = valeurs.substring(0,indices[0]);
        cpt2 = valeurs.substring(indices[0]+1, indices[1]);
        cpt3 = valeurs.substring(indices[1]+1);

        /*
        Serial.println("substrings");
        Serial.println(cpt1);
        Serial.println(cpt2);
        Serial.println(cpt3);
        */
        
        char arr1[cpt1.length()+1];
        strcpy(arr1, cpt1.c_str());
        char *ptr1;
        long ret1;
        totaux[0] = strtol(arr1, &ptr1, 10);

        char arr2[cpt2.length()+1];
        strcpy(arr2, cpt2.c_str());
        char *ptr2;
        long ret2;
        totaux[1] = strtol(arr2, &ptr2, 10);

        char arr3[cpt3.length()+1];
        strcpy(arr3, cpt3.c_str());
        char *ptr3;
        long ret3;
        totaux[2] = strtol(arr3, &ptr3, 10);

        /*
        Serial.println(String("-----"));
        Serial.println(String(totaux[0]));
        Serial.println(String(totaux[1]));
        Serial.println(String(totaux[2]));
        */
        Serial.print(String("@ACQ"));
        return;
        

    }

} //*** Fin lecteur()




//**********************************************************************************************************************


void simulationCycle()
{    
    fin = erreurs[0]||stops[0] && erreurs[1]||stops[1] && erreurs[2]||stops[2];
    if(!fin){
        for(int i=0; i<ECHANTILLONS; i++)
        {

            if (!erreurs[i] && actifs[i] &&!stops[i] && !pauses[i])
            {
                int r = random(0,30);
              
                if(r<3 || r>27)
                {

                      erreurs[i] = true;
                  //  Serial.println("r=" + String(r));
                  //  Serial.println("Erreur sur ech: " + String(i+1));

                }
                else
                {
                     erreurs[i] = false;
                     totaux[i] = totaux[i]+ 1L;
                    /*
                    Serial.println("r= " + String(r));
                    Serial.println("Conforme ech: " + String(i+1));
                    Serial.println("Total ech" + String(i+1) + ":" + String(totaux[i]));
                    */
                }
            }

        }
      
        delay(500);

        String info = "@ERREURS:#0";
        for(int i=0; i<ECHANTILLONS; i++)
        { 
            String statut;
            if(erreurs[i]){

              statut = "1";
              }else{
                
                  statut = "0";
                
                }
            info = info + ":" + statut  ;
        }
        Serial.print(info);
        delay(2000);
             
        info = "@TOTAL:#0";
        for(int i=0; i<ECHANTILLONS; i++)
        {
            info =  info + ":" + String(totaux[i]);
        }

        Serial.print(info);
        delay(2000);
        Serial.print("@SEQ");

          }else{

      marche = false;
      Serial.print("@SEQ");
      delay(TEMPO);
      Serial.print("@FIN");

      }
}

//**********************************************************************************************************************

void cycle()
{    
  //  fin = erreurs[0]||stops[0] && erreurs[1]||stops[1] && erreurs[2]||stops[2];
  //  if(!fin){
        for(int i=0; i<ECHANTILLONS; i++)
        {

            if (!erreurs[i] && actifs[i] &&!stops[i] && !pauses[i])
            {
              
                digitalWrite(relais[i], HIGH);

                delay(500);
                digitalWrite(relais[i], LOW);
                delay(2000);
                e1 = digitalRead(sensors[i]);    // Lecture entrée I1
                if (e1 != LOW)     // si erreur sur sensor
                {

                    erreurs[i] = true;
                  

                }

                e2 = digitalRead(contacts[i]);    // Lecture entrée I2

                if (e2 != LOW)      // si erreur sur contact porte
                {

                    erreurs[i] = true;
                
                }
                delay(2000);

                totaux[i]++;
             
            }

        }
      
        delay(500);

        String info = "@ERREURS:#0";
        for(int i=0; i<ECHANTILLONS; i++)
        { 
            String statut;
            if(erreurs[i]){

              statut = "1";
              }else{
                
                  statut = "0";
                
                }
            info = info + ":" + statut  ;
        }
        Serial.print(info);
        delay(2000);
             
        info = "@TOTAL:#0";
        for(int i=0; i<ECHANTILLONS; i++)
        {
            info =  info + ":" + String(totaux[i]);
        }

        Serial.print(info);
        delay(2000);
        Serial.print("@SEQ");

      /*
          }
          
          else{

      delay(2000);
      marche = false;
      Serial.print("@SEQ");
      delay(2000);
      Serial.print("@FIN");

      }

      */
}

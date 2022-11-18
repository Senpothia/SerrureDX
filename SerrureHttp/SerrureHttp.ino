
/*
  
Programme pour test fiabilité des serrures DX200I

Description du protocole
 
*/

const int R1=22;  // Cablâge: Sortie Digital 22
const int R2=23;  // Cablâge: Sortie Digital 23
const int R3=29;  // Cablâge: Sortie Digital 29
const int R4=39;  // Cablâge: Sortie Digital 39
const int R5=49;  // Cablâge: Sortie Digital 49
const int R6=51;  // Cablâge: Sortie Digital 51
const int R7=52;  // Cablâge: Sortie Digital 52
const int R8=53;  // Cablâge: Sortie Digital 53

const int I1 = 9;
const int I2 = 8;
const int I3 = 7;
const int I4 = 6;
const int I5 = 5;
const int I6 = 4;
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

boolean sonorite;
boolean test = false;
boolean pause = false;

// Variables de test

int ECHANTILLONS = 3;
boolean echantillons[3] = {false, false, false};
boolean erreurs[3] = {false, false, false};
boolean actifs[3] =  {false, false, false};

int relais[3] = {R1, R2, R3};
int sensors[3] = {I1, I3, I5};
int contacts[3] = {I2, I4, I6};
long totaux[3] = {0,0,0};


// the setup function runs once when you press reset or power the board
void setup() {


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


void loop() {

  lecture();
  

}// fin loop



// *******************  Déclarations fonctions externes ***********************

void lecture(){
  
  String reception;
  
 
        reception = Serial.readString();
       // Serial.print(reception);
        reception.trim();
       // Serial.print(reception.substring(1,3));


if (reception == "W:0"){   // Demande lancement de test
            
            //Serial.print("Ordre de mise en marche");

             digitalWrite(R6, HIGH);  
            if (!pause){
              
               Serial.print(String("@:Lancement du test"));
               
              }else {
                
                 Serial.print(String("@:Reprise de test après pause"));
                }
           
            test = true;
            pause = false;
            cycle();
          
 } 


 if (reception == "W:1"){   // Demande de mise à l'arrêt
            
           //Serial.print("Ordre de mise en stop");


           digitalWrite(R6, LOW);  
           Serial.println(String("@ARRET du test"));
           test = false;
           pause = false;
           
           totaux[0] = 0;
           totaux[1] = 0;
           totaux[2] = 0;

           erreurs[0] = false;
           erreurs[1] = false;
           erreurs[2] = false;
           
           actifs[0] = false;
           actifs[1] = false;
           actifs[2] = false;
 } 

 if (reception == "W:2"){   // Demande de mise en pause
            
            // Serial.print("Ordre de mise en pause");

           digitalWrite(R6, HIGH);  
           Serial.println(String("@:Test mis en pause"));
           test = true;
           pause = true;
 } 


if (reception == "W:RAZ1"){  // Demande de mettre en pause le test
        
           Serial.println(String("@:Reset compteur échantillon 1"));
           totaux[0] = 0;
           
                  }   

if (reception == "W:RAZ2"){  // Demande de mettre en pause le test
  
           Serial.println(String("@:Reset compteur échantillon 2"));
           totaux[1] = 0;
           
                  }   

if (reception == "W:RAZ3"){  // Demande de mettre en pause le test
  
           Serial.println(String("@:Reset compteur échantillon 3"));
           totaux[2] = 0;
           
                  }   

if (reception.substring(0,3) == "w:!"){

    Serial.println(String("@:Configuration reçue: ") + String(mot));

    int j=0;
    for (int i=1; i<6; i=i+2){

     
      if (mot[i] == '0'){
        
        echantillons[j] = false;
        actifs[j] = false;
      //Serial.println("ech nbr: "+ String(j)+":" + String(echantillons[j]));
        
        }

      if (mot[i] == '1'){
        
           echantillons[j] = true;
           actifs[j] = true;
      //   Serial.println("ech nbr: "+ String(j)+":" + String(echantillons[j]));
        
        
        }

        j++;
    } 
  }

// Traitement des ajustements de compteurs par la commande SET de l'interface

  if (reception == "W:#"){

      if (mot[1] == '1'){

          String compteur1 = String(mot).substring(3); 
          //Serial.println(compteur1);
          long val = compteur1.toInt();
          //Serial.println(val);
          totaux[0] = val;
           
        }

      if (mot[1] == '2'){
        
          String compteur2 = String(mot).substring(3); 
          //Serial.println(compteur2);
          long val = compteur2.toInt();
          //Serial.println(val);
          totaux[1] = val;
          
        }
      
        if (mot[1] == '3'){
          
          String compteur3 = String(mot).substring(3); 
          //Serial.println(compteur3);
          long val = compteur3.toInt();
          //Serial.println(val);
          totaux[2] = val;
          
          }
  
  }

} //*** Fin lecteur()



//******************************************************************************************




void cycle(){

 //Serial.println("test: " + String(test)+ "; pause: " + String(pause) );

 
     if (!pause){
      
        for(int i=0; i<ECHANTILLONS; i++){
    
             if (!erreurs[i] && actifs[i]){
        
                  digitalWrite(relais[i], HIGH); 
                  
                  delay(500);  
                  digitalWrite(relais[i], LOW);
                  delay(2000);
                  e1 = digitalRead(sensors[i]);    // Lecture entrée I1
                  if (e1 != LOW){    // si erreur sur sensor
                      
                      erreurs[i] = true;
                      actifs[i] = false;
                      //ERREUR0++;
                      //erreurEnCours = true;
                      Serial.println("@:ERREUR:SR:" + String(i+1) + " ");
                      //start = false;
                      sonorite = true;
                      //sortieERREUR();
        
                   }
        
                e2 = digitalRead(contacts[i]);    // Lecture entrée I2
                
                if (e2 != LOW){     // si erreur sur contact porte
                
                      erreurs[i] = true;
                      actifs[i] = false;
                      //if(!erreurEnCours){ERREUR0++;}
                      
                      Serial.println("@:ERREUR:CP:" + String(i+1) + " ");
                      //start = false;
                      sonorite = true;
                      //sortieERREUR();
        
                  }
                delay(5000); 
                
                totaux[i]++;
                String info = "@TOTAL ECH #" + String(i+1) + ": "  + String(totaux[i]) + " ";
                Serial.println(info);
                //Serial.println(TOTAUX[i]);
                //erreurEnCours = false;
        
            }
     
    
            }
            delay(3000);
            transfertActifs();
            Serial.println("@SEQ");
            delay(1000); 
            //transfertActifs();
      
      }

  }

  //********************************************************************************

 void transfertActifs(){

    String listeActifs ="@ACTIFS";
  
    for (int i=0; i<ECHANTILLONS; i++){
      
      if (actifs[i]){
  
            listeActifs = listeActifs + ":1";
        
        }else{
          
           listeActifs = listeActifs + ":0";
            
          }
     
      }
  
    Serial.println(listeActifs);
    delay(5000); 
  
  }

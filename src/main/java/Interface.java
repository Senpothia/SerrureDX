
import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

public class Interface extends javax.swing.JFrame implements Observer {

    private Initializer initializer = new Initializer();  // Charge les propriétés du fichier properties contenant les données liées au cloud(remote)
    public static Initialisation initialisation;          // Centralise les données rapportées par l'initializez
    private boolean buzzer = false;
    private boolean test_off = true;            // le test est arrêté
    private boolean test_on = false;            // le test est en cours
    private boolean test_pause = false;         // le test est en pause
    private boolean arret_valide = false;       // le test est arrêté et la séquence/cycle est terminé
    private boolean auto = true;                // le mode de marche: auto ou manuel
    private boolean modification = false;       // indique si une modification de scéance est en cours. Pilote la fonctionnalité du btn de validation du formulaire
    // S'il n'y a pas modification alors il s'agit d'une ouverture (création de scéance)

    private boolean[] actifs = {false, false, false};
    private boolean[] erreurs = {false, false, false};
    private long[] totaux = {0, 0, 0};

    private int cadence = 1;

    private int baudeRate = 9600;
    private int numDatabits = 8;
    private int parity = 0;
    private int stopBits = 1;
    private int newReadTimeout = 1000;
    private int newWriteTimeout = 0;

    private boolean connexionRS232Active = false;       // état de la connexion RS-232
    private boolean connexionRemoteActive = false;      // Connexion au serveur distant
    private boolean withoutRemote = false;              // Connexion au serveur distant

    Connecteur connecteur = getConnecteur();            // gére la conexion RS232
    Controller controller = new Controller();           // gére le déroulement du test - logique métier

    private String nomDeFichier = null;                 // fichier de sauvegarde locale
    private File repertoire = null;                     // repertoire du ficheir de sauvegarde
    private boolean loadedSceance = false;

    private List<JLabel> compteurs = new ArrayList<>();
    private List<JLabel> statutsEchs = new ArrayList<>();
    private List<JTextField> setCompteurs = new ArrayList<>();
    private List<JRadioButton> echantillonsActifs = new ArrayList<>();
    private List<JButton> btnSets = new ArrayList<>();
    private List<JButton> btnPauses = new ArrayList<>();
    private List<JButton> btnStops = new ArrayList<>();
    private List<JButton> btnResets = new ArrayList<>();
    private List<JMenu> menusRemote = new ArrayList<>();

    private List<String> ordresSETS = new ArrayList<>();
    private List<String> ordresRAZ = new ArrayList<>();
    private List<String> ordresPAUSES = new ArrayList<>();
    private List<String> ordresSTOP = new ArrayList<>();
    private List<String> ordresCadences = new ArrayList<>();

    private FormSeance sceance = new FormSeance();              // contient les éléments de définition / résultats de la scéance en cours à transmettre au cloud
    private Login login = new Login();                          // contient les identifiant de connexion au cloud

    private String newRemoteName = "";
    private String newRemoteAdress = "";

    /*
     * Creates new form Interface
     */
    public Interface() throws FileNotFoundException, IOException {

        initComponents();
        statutRemote.setBackground(Color.red);
        statutRemote.setForeground(Color.red);
        statutRemote.setOpaque(true);

        statutRs232.setBackground(Color.red);
        statutRs232.setForeground(Color.red);
        statutRs232.setOpaque(true);

        statutEch1.setBackground(Color.GRAY);
        statutEch1.setForeground(Color.GRAY);
        statutEch1.setOpaque(true);

        statutEch2.setBackground(Color.GRAY);
        statutEch2.setForeground(Color.GRAY);
        statutEch2.setOpaque(true);

        statutEch3.setBackground(Color.GRAY);
        statutEch3.setForeground(Color.GRAY);
        statutEch3.setOpaque(true);

        voyant.setBackground(Color.RED);
        voyant.setForeground(Color.RED);
        voyant.setOpaque(true);

        startWaiting(true);
        resetStateMachine();

        compteurs.add(compteur1);
        compteurs.add(compteur2);
        compteurs.add(compteur3);

        statutsEchs.add(statutEch1);
        statutsEchs.add(statutEch2);
        statutsEchs.add(statutEch3);

        setCompteurs.add(setCompteur1);
        setCompteurs.add(setCompteur2);
        setCompteurs.add(setCompteur3);

        ordresSETS.add(Constants.SET1);
        ordresSETS.add(Constants.SET2);
        ordresSETS.add(Constants.SET3);

        ordresRAZ.add(Constants.RAZ1);
        ordresRAZ.add(Constants.RAZ2);
        ordresRAZ.add(Constants.RAZ3);

        ordresPAUSES.add(Constants.PAUSE1);
        ordresPAUSES.add(Constants.PAUSE2);
        ordresPAUSES.add(Constants.PAUSE3);

        ordresSTOP.add(Constants.STOP1);
        ordresSTOP.add(Constants.STOP2);
        ordresSTOP.add(Constants.STOP3);

        ordresCadences.add(Constants.CADENCE1);
        ordresCadences.add(Constants.CADENCE2);
        ordresCadences.add(Constants.CADENCE3);

        echantillonsActifs.add(selectEch1);
        echantillonsActifs.add(selectEch2);
        echantillonsActifs.add(selectEch3);

        btnSets.add(set1);
        btnSets.add(set2);
        btnSets.add(set3);

        btnPauses.add(pause1);
        btnPauses.add(pause2);
        btnPauses.add(pause3);

        btnStops.add(arret1);
        btnStops.add(arret2);
        btnStops.add(arret3);

        btnResets.add(reset1);
        btnResets.add(reset2);
        btnResets.add(reset3);

        menusRemote.add(SelectionRemote);
        menusRemote.add(changeRemote);
        menusRemote.add(deleteRemote);

        this.getContentPane().setBackground(new Color(128, 193, 255));

        List<JRadioButtonMenuItem> listePorts = new ArrayList<JRadioButtonMenuItem>();

        List<String> listePortString = connecteur.getListPorts();

        for (String p : listePortString) {

            JRadioButtonMenuItem m = new JRadioButtonMenuItem(p);
            groupPorts.add(m);
            m.addActionListener(new PortSupplier());
            menuPort.add(m);
        }

        initialisation = initializer.getInit();
        if (initialisation.getSceance().equals("na")) {

            menuModifier.setEnabled(false);

        } else {
            menuModifier.setEnabled(true);
        }

        List<String> remotes = initialisation.getRemoteNames();

        for (JMenu mn : menusRemote) {

            for (String r : remotes) {

                JRadioButtonMenuItem m = new JRadioButtonMenuItem(r);
                groupRemotes.add(m);
                m.addActionListener(new RemoteSupplier());
                mn.add(m);
            }

        }
        setEnabledMenusSceance(false);
        setEnabledMenusConfiguration();
        setEnabledSelecteurEchantillons(true);

        //  this.setDefaultCloseOperation(this.closeWindow());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupPorts = new javax.swing.ButtonGroup();
        groupBaud = new javax.swing.ButtonGroup();
        groupBits = new javax.swing.ButtonGroup();
        groupStop = new javax.swing.ButtonGroup();
        groupParity = new javax.swing.ButtonGroup();
        selectionFichier = new javax.swing.JFileChooser();
        groupCadence = new javax.swing.ButtonGroup();
        groupRemotes = new javax.swing.ButtonGroup();
        formulaire = new javax.swing.JFrame();
        descriptionField = new javax.swing.JTextField();
        titre4 = new javax.swing.JLabel();
        titre2 = new javax.swing.JLabel();
        dateField = new javax.swing.JTextField();
        titrePrincipal = new javax.swing.JLabel();
        titre6 = new javax.swing.JLabel();
        counter1 = new javax.swing.JTextField();
        titre7 = new javax.swing.JLabel();
        type1 = new javax.swing.JComboBox<>();
        counter2 = new javax.swing.JTextField();
        titre10 = new javax.swing.JLabel();
        type2 = new javax.swing.JComboBox<>();
        titre8 = new javax.swing.JLabel();
        titre9 = new javax.swing.JLabel();
        titre3 = new javax.swing.JLabel();
        type3 = new javax.swing.JComboBox<>();
        titre11 = new javax.swing.JLabel();
        titre12 = new javax.swing.JLabel();
        counter3 = new javax.swing.JTextField();
        titre13 = new javax.swing.JLabel();
        valideFormulaire = new javax.swing.JButton();
        annulerFormulaire = new javax.swing.JButton();
        actif1 = new javax.swing.JCheckBox();
        actif2 = new javax.swing.JCheckBox();
        actif3 = new javax.swing.JCheckBox();
        loginForm = new javax.swing.JFrame();
        titreLogin = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JTextField();
        annulerLogin = new javax.swing.JButton();
        ValideLogin = new javax.swing.JButton();
        remoteForm = new javax.swing.JFrame();
        nameRemote = new javax.swing.JTextField();
        adresseRemote = new javax.swing.JTextField();
        nomRemoteLabel = new javax.swing.JLabel();
        adresseLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        titre = new javax.swing.JLabel();
        compteur1 = new javax.swing.JLabel();
        selectEch1 = new javax.swing.JRadioButton();
        setCompteur1 = new javax.swing.JTextField();
        set1 = new javax.swing.JButton();
        reset1 = new javax.swing.JButton();
        pause1 = new javax.swing.JButton();
        reset2 = new javax.swing.JButton();
        compteur2 = new javax.swing.JLabel();
        pause2 = new javax.swing.JButton();
        selectEch2 = new javax.swing.JRadioButton();
        setCompteur2 = new javax.swing.JTextField();
        set2 = new javax.swing.JButton();
        reset3 = new javax.swing.JButton();
        compteur3 = new javax.swing.JLabel();
        pause3 = new javax.swing.JButton();
        selectEch3 = new javax.swing.JRadioButton();
        setCompteur3 = new javax.swing.JTextField();
        set3 = new javax.swing.JButton();
        voyant = new javax.swing.JLabel();
        version = new javax.swing.JLabel();
        console = new javax.swing.JTextField();
        statutRs232 = new javax.swing.JLabel();
        statutRemote = new javax.swing.JLabel();
        RS232 = new javax.swing.JLabel();
        Remote = new javax.swing.JLabel();
        arret1 = new javax.swing.JButton();
        arret2 = new javax.swing.JButton();
        arret3 = new javax.swing.JButton();
        start = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        pause = new javax.swing.JButton();
        statutEch1 = new javax.swing.JLabel();
        statutEch2 = new javax.swing.JLabel();
        statutEch3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        MenuFichier = new javax.swing.JMenu();
        menuOuvrir = new javax.swing.JMenuItem();
        menuNouveau = new javax.swing.JMenuItem();
        menuModifier = new javax.swing.JMenuItem();
        menuEffacer = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuSauvegardes = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuQuitter = new javax.swing.JMenuItem();
        menuConnexion = new javax.swing.JMenu();
        menuPort = new javax.swing.JMenu();
        menuBaud = new javax.swing.JMenu();
        baud9600 = new javax.swing.JRadioButtonMenuItem();
        baud19200 = new javax.swing.JRadioButtonMenuItem();
        baud38400 = new javax.swing.JRadioButtonMenuItem();
        baud115200 = new javax.swing.JRadioButtonMenuItem();
        menuBits = new javax.swing.JMenu();
        bits6 = new javax.swing.JRadioButtonMenuItem();
        bits7 = new javax.swing.JRadioButtonMenuItem();
        bits8 = new javax.swing.JRadioButtonMenuItem();
        bits9 = new javax.swing.JRadioButtonMenuItem();
        menuStop = new javax.swing.JMenu();
        stop1 = new javax.swing.JRadioButtonMenuItem();
        stop2 = new javax.swing.JRadioButtonMenuItem();
        menuParity = new javax.swing.JMenu();
        parityNone = new javax.swing.JRadioButtonMenuItem();
        parityOdd = new javax.swing.JRadioButtonMenuItem();
        parityEven = new javax.swing.JRadioButtonMenuItem();
        btnConnexion = new javax.swing.JMenuItem();
        btnDeconnexion = new javax.swing.JMenuItem();
        menuRemote = new javax.swing.JMenu();
        SelectionRemote = new javax.swing.JMenu();
        changeRemote = new javax.swing.JMenu();
        deleteRemote = new javax.swing.JMenu();
        addRemote = new javax.swing.JMenuItem();
        connectRemote = new javax.swing.JMenuItem();
        deconnectRemote = new javax.swing.JMenuItem();
        menuConfig = new javax.swing.JMenu();
        menuCadence = new javax.swing.JMenu();
        cad_2_par_1min = new javax.swing.JRadioButtonMenuItem();
        cad_1_par_2mins = new javax.swing.JRadioButtonMenuItem();
        cad_1_par_5mins = new javax.swing.JRadioButtonMenuItem();
        menuAuto = new javax.swing.JMenuItem();
        menuManuel = new javax.swing.JMenuItem();

        selectionFichier.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        formulaire.setTitle("Test DX200I - Création de scéance");
        formulaire.setMaximizedBounds(new java.awt.Rectangle(0, 0, 300, 200));

        descriptionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionFieldActionPerformed(evt);
            }
        });

        titre4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        titre4.setText("Echantillon 1");

        titre2.setText("Description");

        titrePrincipal.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        titrePrincipal.setText("Scéance");

        titre6.setText("Type");

        titre7.setText("Compteur");

        type1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DX200I", "APX200" }));

        titre10.setText("Compteur");

        type2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DX200I", "APX200" }));
        type2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type2ActionPerformed(evt);
            }
        });

        titre8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        titre8.setText("Echantillon 2");

        titre9.setText("Type");

        titre3.setText("Date");

        type3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DX200I", "APX200" }));

        titre11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        titre11.setText("Echantillon 3");

        titre12.setText("Type");

        titre13.setText("Compteur");

        valideFormulaire.setText("Valider");
        valideFormulaire.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valideFormulaireActionPerformed(evt);
            }
        });

        annulerFormulaire.setText("Annuler");
        annulerFormulaire.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annulerFormulaireActionPerformed(evt);
            }
        });

        actif1.setText("Actif");

        actif2.setText("Actif");

        actif3.setText("Actif");

        javax.swing.GroupLayout formulaireLayout = new javax.swing.GroupLayout(formulaire.getContentPane());
        formulaire.getContentPane().setLayout(formulaireLayout);
        formulaireLayout.setHorizontalGroup(
            formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formulaireLayout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createSequentialGroup()
                        .addComponent(titre8, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actif2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createSequentialGroup()
                        .addComponent(titre4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actif1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createSequentialGroup()
                        .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titre7)
                            .addComponent(titre6))
                        .addGap(18, 18, 18)
                        .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(counter1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                            .addComponent(type1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createSequentialGroup()
                        .addComponent(titre11, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actif3))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(formulaireLayout.createSequentialGroup()
                            .addComponent(annulerFormulaire)
                            .addGap(18, 18, 18)
                            .addComponent(valideFormulaire))
                        .addGroup(formulaireLayout.createSequentialGroup()
                            .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(titre13)
                                .addComponent(titre12))
                            .addGap(18, 18, 18)
                            .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(counter3)
                                .addComponent(type3, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createSequentialGroup()
                        .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titre2)
                            .addGroup(formulaireLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(titre3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(descriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formulaireLayout.createSequentialGroup()
                        .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titre10)
                            .addComponent(titre9))
                        .addGap(18, 18, 18)
                        .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(counter2, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(type2, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formulaireLayout.createSequentialGroup()
                .addContainerGap(360, Short.MAX_VALUE)
                .addComponent(titrePrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(355, 355, 355))
        );
        formulaireLayout.setVerticalGroup(
            formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formulaireLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(titrePrincipal)
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titre2))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titre3))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titre4)
                    .addComponent(actif1))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titre6)
                    .addComponent(type1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(counter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titre7))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titre8)
                    .addComponent(actif2))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titre9)
                    .addComponent(type2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(counter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titre10))
                .addGap(37, 37, 37)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titre11)
                    .addComponent(actif3))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titre12)
                    .addComponent(type3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(counter3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titre13))
                .addGap(18, 18, 18)
                .addGroup(formulaireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valideFormulaire)
                    .addComponent(annulerFormulaire))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        loginForm.setTitle("Test DX200I - Identification");

        titreLogin.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titreLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titreLogin.setText("Entrez vos identifiants");

        jLabel2.setText("Email");

        jLabel3.setText("Password");

        annulerLogin.setText("Annuler");
        annulerLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annulerLoginActionPerformed(evt);
            }
        });

        ValideLogin.setText("Valider");
        ValideLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ValideLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout loginFormLayout = new javax.swing.GroupLayout(loginForm.getContentPane());
        loginForm.getContentPane().setLayout(loginFormLayout);
        loginFormLayout.setHorizontalGroup(
            loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginFormLayout.createSequentialGroup()
                .addGroup(loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(loginFormLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginFormLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(40, 40, 40)))
                        .addGroup(loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordField)
                            .addComponent(usernameField)))
                    .addGroup(loginFormLayout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(annulerLogin)
                        .addGap(18, 18, 18)
                        .addComponent(ValideLogin)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginFormLayout.createSequentialGroup()
                .addGap(0, 109, Short.MAX_VALUE)
                .addComponent(titreLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
        );
        loginFormLayout.setVerticalGroup(
            loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginFormLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(titreLogin)
                .addGap(31, 31, 31)
                .addGroup(loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(loginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(annulerLogin)
                    .addComponent(ValideLogin))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        remoteForm.setTitle("Ajouter remote");

        nomRemoteLabel.setText("Nom");

        adresseLabel.setText("Adressse");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Création d'un remote");

        jButton1.setText("Annuler");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Valider");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout remoteFormLayout = new javax.swing.GroupLayout(remoteForm.getContentPane());
        remoteForm.getContentPane().setLayout(remoteFormLayout);
        remoteFormLayout.setHorizontalGroup(
            remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, remoteFormLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
            .addGroup(remoteFormLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(remoteFormLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(remoteFormLayout.createSequentialGroup()
                        .addGroup(remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nomRemoteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(adresseLabel))
                        .addGap(18, 18, 18)
                        .addGroup(remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameRemote)
                            .addComponent(adresseRemote, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        remoteFormLayout.setVerticalGroup(
            remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(remoteFormLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel5)
                .addGap(46, 46, 46)
                .addGroup(remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameRemote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nomRemoteLabel))
                .addGap(30, 30, 30)
                .addGroup(remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adresseRemote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adresseLabel))
                .addGap(45, 45, 45)
                .addGroup(remoteFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Test d'endurance DX200I");
        setBackground(new java.awt.Color(153, 153, 255));

        titre.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        titre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titre.setText("TEST DX200I");
        titre.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        compteur1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        compteur1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        compteur1.setText("0");

        selectEch1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        selectEch1.setText("Echantillon 1");

        setCompteur1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        set1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        set1.setForeground(new java.awt.Color(255, 51, 0));
        set1.setText("Set");
        set1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                set1ActionPerformed(evt);
            }
        });

        reset1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        reset1.setForeground(new java.awt.Color(255, 51, 0));
        reset1.setText("Reset");
        reset1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset1ActionPerformed(evt);
            }
        });

        pause1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        pause1.setForeground(new java.awt.Color(255, 102, 51));
        pause1.setText("Pause");
        pause1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pause1ActionPerformed(evt);
            }
        });

        reset2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        reset2.setForeground(new java.awt.Color(255, 51, 0));
        reset2.setText("Reset");
        reset2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset2ActionPerformed(evt);
            }
        });

        compteur2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        compteur2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        compteur2.setText("0");

        pause2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        pause2.setForeground(new java.awt.Color(255, 102, 51));
        pause2.setText("Pause");
        pause2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pause2ActionPerformed(evt);
            }
        });

        selectEch2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        selectEch2.setText("Echantillon 2");

        setCompteur2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        set2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        set2.setForeground(new java.awt.Color(255, 51, 0));
        set2.setText("Set");
        set2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                set2ActionPerformed(evt);
            }
        });

        reset3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        reset3.setForeground(new java.awt.Color(255, 51, 0));
        reset3.setText("Reset");
        reset3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset3ActionPerformed(evt);
            }
        });

        compteur3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        compteur3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        compteur3.setText("0");

        pause3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        pause3.setForeground(new java.awt.Color(255, 102, 51));
        pause3.setText("Pause");
        pause3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pause3ActionPerformed(evt);
            }
        });

        selectEch3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        selectEch3.setText("Echantillon 3");

        setCompteur3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        set3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        set3.setForeground(new java.awt.Color(255, 51, 0));
        set3.setText("Set");
        set3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                set3ActionPerformed(evt);
            }
        });

        voyant.setBackground(new java.awt.Color(255, 51, 0));
        voyant.setForeground(new java.awt.Color(255, 0, 0));
        voyant.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        voyant.setText("Voyant");

        version.setText("V1.0");

        console.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        console.setForeground(new java.awt.Color(255, 0, 0));
        console.setText("En attente de connexion!");

        statutRs232.setBackground(new java.awt.Color(0, 153, 0));
        statutRs232.setForeground(new java.awt.Color(0, 153, 51));
        statutRs232.setText("Statut");

        statutRemote.setBackground(new java.awt.Color(255, 51, 0));
        statutRemote.setForeground(new java.awt.Color(255, 51, 0));
        statutRemote.setText("Statut");

        RS232.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        RS232.setText("RS232");

        Remote.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Remote.setText("REMOTE");

        arret1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        arret1.setForeground(new java.awt.Color(255, 0, 0));
        arret1.setText("STOP");
        arret1.setToolTipText("");
        arret1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arret1ActionPerformed(evt);
            }
        });

        arret2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        arret2.setForeground(new java.awt.Color(255, 0, 0));
        arret2.setText("STOP");
        arret2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arret2ActionPerformed(evt);
            }
        });

        arret3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        arret3.setForeground(new java.awt.Color(255, 0, 0));
        arret3.setText("STOP");
        arret3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arret3ActionPerformed(evt);
            }
        });

        start.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        start.setForeground(new java.awt.Color(0, 102, 0));
        start.setText("START");
        start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });

        stop.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        stop.setForeground(new java.awt.Color(255, 51, 0));
        stop.setText("STOP");
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });

        pause.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        pause.setForeground(new java.awt.Color(255, 102, 0));
        pause.setText("PAUSE");
        pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseActionPerformed(evt);
            }
        });

        statutEch1.setBackground(new java.awt.Color(0, 153, 0));
        statutEch1.setForeground(new java.awt.Color(0, 153, 51));
        statutEch1.setText("0000");

        statutEch2.setBackground(new java.awt.Color(0, 153, 0));
        statutEch2.setForeground(new java.awt.Color(0, 153, 51));
        statutEch2.setText("0000");

        statutEch3.setBackground(new java.awt.Color(0, 153, 0));
        statutEch3.setForeground(new java.awt.Color(0, 153, 51));
        statutEch3.setText("0000");

        MenuFichier.setText("Fichier");
        MenuFichier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuFichierActionPerformed(evt);
            }
        });

        menuOuvrir.setText("Ouvrir");
        menuOuvrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOuvrirActionPerformed(evt);
            }
        });
        MenuFichier.add(menuOuvrir);

        menuNouveau.setText("Nouveau");
        menuNouveau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNouveauActionPerformed(evt);
            }
        });
        MenuFichier.add(menuNouveau);

        menuModifier.setLabel("Modifier");
        menuModifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuModifierActionPerformed(evt);
            }
        });
        MenuFichier.add(menuModifier);

        menuEffacer.setText("Effacer");
        menuEffacer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEffacerActionPerformed(evt);
            }
        });
        MenuFichier.add(menuEffacer);
        MenuFichier.add(jSeparator2);

        menuSauvegardes.setLabel("Sauvegardes");
        menuSauvegardes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSauvegardesActionPerformed(evt);
            }
        });
        MenuFichier.add(menuSauvegardes);
        MenuFichier.add(jSeparator1);

        menuQuitter.setText("Quitter");
        menuQuitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuQuitterActionPerformed(evt);
            }
        });
        MenuFichier.add(menuQuitter);

        jMenuBar1.add(MenuFichier);

        menuConnexion.setText("Connexion");
        menuConnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConnexionActionPerformed(evt);
            }
        });

        menuPort.setText("Port");
        menuPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPortActionPerformed(evt);
            }
        });
        menuConnexion.add(menuPort);

        menuBaud.setText("Baud");

        groupBaud.add(baud9600);
        baud9600.setSelected(true);
        baud9600.setText("9600");
        baud9600.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                baud9600StateChanged(evt);
            }
        });
        menuBaud.add(baud9600);

        groupBaud.add(baud19200);
        baud19200.setText("19200");
        baud19200.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                baud19200StateChanged(evt);
            }
        });
        menuBaud.add(baud19200);

        groupBaud.add(baud38400);
        baud38400.setText("38400");
        baud38400.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                baud38400StateChanged(evt);
            }
        });
        menuBaud.add(baud38400);

        groupBaud.add(baud115200);
        baud115200.setText("115200");
        baud115200.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                baud115200StateChanged(evt);
            }
        });
        menuBaud.add(baud115200);

        menuConnexion.add(menuBaud);

        menuBits.setText("Bits");

        groupBits.add(bits6);
        bits6.setText("6");
        bits6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bits6StateChanged(evt);
            }
        });
        menuBits.add(bits6);

        groupBits.add(bits7);
        bits7.setText("7");
        bits7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bits7StateChanged(evt);
            }
        });
        menuBits.add(bits7);

        groupBits.add(bits8);
        bits8.setSelected(true);
        bits8.setText("8");
        bits8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bits8StateChanged(evt);
            }
        });
        menuBits.add(bits8);

        groupBits.add(bits9);
        bits9.setText("9");
        bits9.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bits9StateChanged(evt);
            }
        });
        menuBits.add(bits9);

        menuConnexion.add(menuBits);

        menuStop.setText("Stop");

        groupStop.add(stop1);
        stop1.setSelected(true);
        stop1.setText("1");
        stop1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stop1StateChanged(evt);
            }
        });
        menuStop.add(stop1);

        groupStop.add(stop2);
        stop2.setText("2");
        stop2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stop2StateChanged(evt);
            }
        });
        menuStop.add(stop2);

        menuConnexion.add(menuStop);

        menuParity.setText("Parité");

        groupParity.add(parityNone);
        parityNone.setSelected(true);
        parityNone.setText("None");
        parityNone.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                parityNoneStateChanged(evt);
            }
        });
        menuParity.add(parityNone);

        groupParity.add(parityOdd);
        parityOdd.setText("Paire");
        parityOdd.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                parityOddStateChanged(evt);
            }
        });
        menuParity.add(parityOdd);

        groupParity.add(parityEven);
        parityEven.setText("Impaire");
        parityEven.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                parityEvenStateChanged(evt);
            }
        });
        menuParity.add(parityEven);

        menuConnexion.add(menuParity);

        btnConnexion.setText("Connexion");
        btnConnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnexionActionPerformed(evt);
            }
        });
        menuConnexion.add(btnConnexion);

        btnDeconnexion.setText("Déconnexion");
        btnDeconnexion.setEnabled(false);
        btnDeconnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeconnexionActionPerformed(evt);
            }
        });
        menuConnexion.add(btnDeconnexion);

        jMenuBar1.add(menuConnexion);

        menuRemote.setText("Remote");
        menuRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoteActionPerformed(evt);
            }
        });

        SelectionRemote.setText("Choisir");
        menuRemote.add(SelectionRemote);

        changeRemote.setText("Changer");
        menuRemote.add(changeRemote);

        deleteRemote.setText("Supprimer");
        deleteRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRemoteActionPerformed(evt);
            }
        });
        menuRemote.add(deleteRemote);

        addRemote.setText("Ajouter");
        addRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRemoteActionPerformed(evt);
            }
        });
        menuRemote.add(addRemote);

        connectRemote.setText("Connexion");
        connectRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectRemoteActionPerformed(evt);
            }
        });
        menuRemote.add(connectRemote);

        deconnectRemote.setText("Déconnexion");
        deconnectRemote.setEnabled(false);
        deconnectRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deconnectRemoteActionPerformed(evt);
            }
        });
        menuRemote.add(deconnectRemote);

        jMenuBar1.add(menuRemote);

        menuConfig.setText("Configuration");
        menuConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConfigActionPerformed(evt);
            }
        });

        menuCadence.setText("Cadence");

        groupCadence.add(cad_2_par_1min);
        cad_2_par_1min.setSelected(true);
        cad_2_par_1min.setText("2x1min");
        cad_2_par_1min.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cad_2_par_1minStateChanged(evt);
            }
        });
        cad_2_par_1min.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cad_2_par_1minActionPerformed(evt);
            }
        });
        menuCadence.add(cad_2_par_1min);

        groupCadence.add(cad_1_par_2mins);
        cad_1_par_2mins.setText("1x2mins");
        cad_1_par_2mins.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cad_1_par_2minsStateChanged(evt);
            }
        });
        cad_1_par_2mins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cad_1_par_2minsActionPerformed(evt);
            }
        });
        menuCadence.add(cad_1_par_2mins);

        groupCadence.add(cad_1_par_5mins);
        cad_1_par_5mins.setText("1x5mins");
        cad_1_par_5mins.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cad_1_par_5minsStateChanged(evt);
            }
        });
        cad_1_par_5mins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cad_1_par_5minsActionPerformed(evt);
            }
        });
        menuCadence.add(cad_1_par_5mins);

        menuConfig.add(menuCadence);

        menuAuto.setText("Auto");
        menuAuto.setEnabled(false);
        menuAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAutoActionPerformed(evt);
            }
        });
        menuConfig.add(menuAuto);

        menuManuel.setText("Manuel");
        menuManuel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuManuelActionPerformed(evt);
            }
        });
        menuConfig.add(menuManuel);

        jMenuBar1.add(menuConfig);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(statutRs232, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(RS232))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(statutRemote)
                                .addGap(18, 18, 18)
                                .addComponent(Remote)))
                        .addGap(283, 283, 283)
                        .addComponent(titre, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(594, 594, 594)
                        .addComponent(voyant, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(447, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(stop)
                                .addGap(27, 27, 27)
                                .addComponent(start)
                                .addGap(18, 18, 18)
                                .addComponent(pause)
                                .addGap(188, 188, 188))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(statutEch1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(statutEch2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(statutEch3, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(selectEch1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(compteur1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(50, 50, 50)
                                            .addComponent(setCompteur1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(set1)
                                            .addGap(34, 34, 34)
                                            .addComponent(reset1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(pause1))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(selectEch3)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(compteur3, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(selectEch2)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(compteur2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGap(50, 50, 50)
                                            .addComponent(setCompteur2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(set2)
                                            .addGap(34, 34, 34)
                                            .addComponent(reset2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(pause2)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(setCompteur3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(set3)
                                        .addGap(34, 34, 34)
                                        .addComponent(reset3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(pause3)))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(arret1)
                            .addComponent(arret2)
                            .addComponent(arret3))
                        .addGap(162, 162, 162))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(version)
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(console, javax.swing.GroupLayout.PREFERRED_SIZE, 874, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(205, 205, 205))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statutRs232)
                            .addComponent(RS232))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statutRemote)
                            .addComponent(Remote)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(titre)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectEch1)
                            .addComponent(compteur1)
                            .addComponent(setCompteur1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(set1)
                            .addComponent(reset1)
                            .addComponent(pause1)
                            .addComponent(arret1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statutEch1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(statutEch2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectEch2)
                        .addComponent(compteur2)
                        .addComponent(setCompteur2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(set2)
                        .addComponent(reset2)
                        .addComponent(pause2)
                        .addComponent(arret2)))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(statutEch3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectEch3)
                        .addComponent(compteur3)
                        .addComponent(setCompteur3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(set3)
                        .addComponent(reset3)
                        .addComponent(pause3)
                        .addComponent(arret3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(voyant, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start)
                    .addComponent(stop)
                    .addComponent(pause))
                .addGap(33, 33, 33)
                .addComponent(console, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(version)
                .addGap(16, 16, 16))
        );

        arret1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuNouveauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNouveauActionPerformed
        if (connexionRemoteActive) {

            formulaire.setVisible(true);
            formulaire.setSize(900, 700);
            descriptionField.setText(null);
            dateField.setText(null);
            counter1.setText(null);
            counter2.setText(null);
            counter3.setText(null);
            actif1.setSelected(false);
            actif2.setSelected(false);
            actif3.setSelected(false);
            type1.setSelectedIndex(0);
            type2.setSelectedIndex(0);
            type3.setSelectedIndex(0);

        } else {

            montrerError("Vous devez vous connecter au remote!", "Défaut de connexion");
        }


    }//GEN-LAST:event_menuNouveauActionPerformed

    private void arret1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arret1ActionPerformed

        envoyerOrdreStop(1);
    }//GEN-LAST:event_arret1ActionPerformed

    private void btnConnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnexionActionPerformed

        int i = connecteur.makeConnection(Connecteur.portName, baudeRate, numDatabits, parity, stopBits);
        if (i == 99) {

            console.setForeground(Color.BLUE);
            console.setText("Connexion réussie");
            setStatusRS232(true);
            btnConnexion.setEnabled(false);
            btnDeconnexion.setEnabled(true);
            connexionRS232Active = true;

        } else {

            console.setForeground(Color.red);
            console.setText("Tentative de connexion échouée");
            setStatusRS232(false);

        }

        setEnabledMenusConfiguration();


    }//GEN-LAST:event_btnConnexionActionPerformed

    private void btnDeconnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeconnexionActionPerformed

        int i = connecteur.disconnect();
        if (i == 0) {
            console.setForeground(Color.BLUE);
            console.setText("Déconnexion réussie");
            setStatusRS232(false);
            btnConnexion.setEnabled(true);
            btnDeconnexion.setEnabled(false);
            connexionRS232Active = false;
            setEnabledMenusConfiguration();

        }
    }//GEN-LAST:event_btnDeconnexionActionPerformed

    private void baud9600StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baud9600StateChanged

        if (baud9600.isSelected()) {

            baudeRate = 9600;
        }
    }//GEN-LAST:event_baud9600StateChanged

    private void baud19200StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baud19200StateChanged

        if (baud19200.isSelected()) {

            baudeRate = 19200;
        }
    }//GEN-LAST:event_baud19200StateChanged

    private void baud38400StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baud38400StateChanged

        if (baud38400.isSelected()) {

            baudeRate = 38400;
        }
    }//GEN-LAST:event_baud38400StateChanged

    private void baud115200StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baud115200StateChanged

        if (baud115200.isSelected()) {

            baudeRate = 115200;
        }
    }//GEN-LAST:event_baud115200StateChanged

    private void bits6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bits6StateChanged

        if (bits6.isSelected()) {

            numDatabits = 6;
        }
    }//GEN-LAST:event_bits6StateChanged

    private void bits7StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bits7StateChanged

        if (bits7.isSelected()) {

            numDatabits = 7;
        }
    }//GEN-LAST:event_bits7StateChanged

    private void bits8StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bits8StateChanged

        if (bits8.isSelected()) {

            numDatabits = 8;
        }
    }//GEN-LAST:event_bits8StateChanged

    private void bits9StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bits9StateChanged

        if (bits9.isSelected()) {

            numDatabits = 9;
        }
    }//GEN-LAST:event_bits9StateChanged

    private void stop1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stop1StateChanged

        if (stop1.isSelected()) {

            stopBits = 1;
        }
    }//GEN-LAST:event_stop1StateChanged

    private void stop2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stop2StateChanged

        if (stop2.isSelected()) {

            stopBits = 2;
        }
    }//GEN-LAST:event_stop2StateChanged

    private void parityNoneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_parityNoneStateChanged

        if (parityNone.isSelected()) {

            parity = 0;
        }
    }//GEN-LAST:event_parityNoneStateChanged

    private void parityOddStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_parityOddStateChanged

        if (parityOdd.isSelected()) {

            parity = 1;
        }
    }//GEN-LAST:event_parityOddStateChanged

    private void parityEvenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_parityEvenStateChanged

        if (parityEven.isSelected()) {

            stopBits = 2;
        }
    }//GEN-LAST:event_parityEvenStateChanged

    private void menuConnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConnexionActionPerformed


    }//GEN-LAST:event_menuConnexionActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed

        if (auto) {

            stopRequested();
            connecteur.envoyerData(Constants.ORDRE_ARRET);
            setEnabledSelecteurEchantillons(true);
            try {
                controller.actualiserSceanceRemote(sceance, login);
            } catch (IOException ex) {
                Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
            }
            controller.enregistrerSceanceLocal(sceance);

        } else {
        }


    }//GEN-LAST:event_stopActionPerformed

    private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed

        if (!connexionRS232Active && !withoutRemote) {

            montrerError("Vous devez activer la connexion série!", "Défaut de connexion");
            return;
        }

        if (nomDeFichier == null || repertoire == null) {

            montrerError("Vous devez définir un fichier de sauvegarde!", "Défaut fichier de sauvegarde");
            return;
        }

        if (!connexionRemoteActive && !withoutRemote) {

            int result = JOptionPane.showConfirmDialog(this, "Voulez-vous continuez sans remote?", "Défaut de connexion remote",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {

                withoutRemote = true;
                return;

            } else if (result == JOptionPane.NO_OPTION) {
                return;
            }
        }

        if (!withoutRemote && !loadedSceance) {

            montrerError("Vous devez définir une scéance!", "Scéance indéfinie");
            return;
        }

        if (auto) {

            controller.setFormSceance(sceance);
            int i = envoyerConfiguration();

            if (i == -1) {

                console.setForeground(Color.RED);
                console.setText("Erreur de configuration: sélectionnez les échantillons actifs.");
                setEnabledSelecteurEchantillons(true);
                return;

            } else {

                console.setForeground(Color.RED);
                console.setText("En attente de démarrage!");
                setEnabledSelecteurEchantillons(false);
            }

        } else {

            // TODO mode manuel
        }


    }//GEN-LAST:event_startActionPerformed

    private void pauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseActionPerformed

        if (auto) {
            
            pauseRequested();
            connecteur.envoyerData(Constants.ORDRE_PAUSE);
            controller.enregistrerSceanceLocal(sceance);
            try {
                controller.actualiserSceanceRemote(sceance, login);
            } catch (IOException ex) {
                Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else {

        }

    }//GEN-LAST:event_pauseActionPerformed

    private void menuManuelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuManuelActionPerformed

        auto = false;
        start.setEnabled(false);
        start.setForeground(Color.GRAY);
        stop.setText("Effacer");
        pause.setText("Envoyer");
        pause.setForeground(new Color(0, 102, 0));
        stop.setEnabled(true);
        stop.setForeground(Color.RED);
        pause.setEnabled(true);
        menuAuto.setEnabled(true);
        menuManuel.setEnabled(false);
        for (int i = 0; i < 3; i++) {

            btnStops.get(i).setText("Activer");
        }

        for (int i = 0; i < 3; i++) {

            btnSets.get(i).setEnabled(false);
        }

        for (int i = 0; i < 3; i++) {

            btnPauses.get(i).setEnabled(false);
        }

        for (int i = 0; i < 3; i++) {

            btnResets.get(i).setEnabled(false);
        }

        console.setText("");
    }//GEN-LAST:event_menuManuelActionPerformed

    private void menuConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConfigActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuConfigActionPerformed

    private void menuAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAutoActionPerformed

        auto = true;
        stop.setText("STOP");
        pause.setText("PAUSE");
        menuAuto.setEnabled(false);
        menuManuel.setEnabled(true);
        for (int i = 0; i < 3; i++) {

            btnStops.get(i).setText("STOP");
        }

        for (int i = 0; i < 3; i++) {

            btnSets.get(i).setEnabled(false);
        }

        for (int i = 0; i < 3; i++) {

            btnPauses.get(i).setEnabled(false);
        }

        for (int i = 0; i < 3; i++) {

            btnResets.get(i).setEnabled(false);
        }
        startWaiting(true);

    }//GEN-LAST:event_menuAutoActionPerformed

    private void menuSauvegardesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSauvegardesActionPerformed

        nomDeFichier = JOptionPane.showInputDialog("Entrez un nom pour le fichier de sauvegarde!");
        if (nomDeFichier == null || nomDeFichier.equals("")) {
            montrerError("Vous devez indiquer un nom de fichier valide!", "Nom de fichier incorrect");
            return;
        }
        int showOpenDialog = selectionFichier.showOpenDialog(this);
        repertoire = selectionFichier.getSelectedFile();
        nomDeFichier = repertoire + "\\" + nomDeFichier + ".csv";
        console.setForeground(Color.red);
        console.setText("Les résultats seront sauvegardés à l'emplacement: " + nomDeFichier);
        int i = controller.creationFichier(nomDeFichier);
        if (i == -1) {

            montrerError("Erreur à la création du fichier de sauvegarde!", "Echec création fichier");
        }
    }//GEN-LAST:event_menuSauvegardesActionPerformed

    private void MenuFichierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuFichierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MenuFichierActionPerformed

    private void set1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_set1ActionPerformed

        envoyerInitCompteur(1);

    }//GEN-LAST:event_set1ActionPerformed

    private void set2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_set2ActionPerformed

        envoyerInitCompteur(2);
    }//GEN-LAST:event_set2ActionPerformed

    private void set3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_set3ActionPerformed
        envoyerInitCompteur(3);
    }//GEN-LAST:event_set3ActionPerformed

    private void reset1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset1ActionPerformed

        envoyerResetCompteur(1);
    }//GEN-LAST:event_reset1ActionPerformed

    private void reset2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset2ActionPerformed

        envoyerResetCompteur(2);
    }//GEN-LAST:event_reset2ActionPerformed

    private void reset3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset3ActionPerformed

        envoyerResetCompteur(3);
    }//GEN-LAST:event_reset3ActionPerformed

    private void pause1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pause1ActionPerformed

        envoyerOrdrePause(1);

    }//GEN-LAST:event_pause1ActionPerformed

    private void pause2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pause2ActionPerformed

        envoyerOrdrePause(2);

    }//GEN-LAST:event_pause2ActionPerformed

    private void pause3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pause3ActionPerformed

        envoyerOrdrePause(3);
    }//GEN-LAST:event_pause3ActionPerformed

    private void arret2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arret2ActionPerformed

        envoyerOrdreStop(2);
    }//GEN-LAST:event_arret2ActionPerformed

    private void arret3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arret3ActionPerformed

        envoyerOrdreStop(3);
    }//GEN-LAST:event_arret3ActionPerformed

    private void cad_2_par_1minActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cad_2_par_1minActionPerformed

        envoyerOdreCadence(1);
    }//GEN-LAST:event_cad_2_par_1minActionPerformed

    private void cad_1_par_2minsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cad_1_par_2minsActionPerformed

        envoyerOdreCadence(2);
    }//GEN-LAST:event_cad_1_par_2minsActionPerformed

    private void cad_1_par_5minsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cad_1_par_5minsActionPerformed
        envoyerOdreCadence(3);
    }//GEN-LAST:event_cad_1_par_5minsActionPerformed

    private void menuQuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuQuitterActionPerformed

        int result = JOptionPane.showConfirmDialog(this, "Voulez-vous arrêter le test?", "Demande de fermeture",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {

            System.out.println("Fermeture du programme");
            fermeture();

        } else if (result == JOptionPane.NO_OPTION) {
            return;
        }


    }//GEN-LAST:event_menuQuitterActionPerformed

    private void connectRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectRemoteActionPerformed

        if (initialisation.getRemoteUrl() == null) {
            montrerError("Vous devez choisir un remote!", "Remote inconnu");
            return;
        } else {

            loginForm.setSize(400, 400);
            loginForm.setVisible(true);

        }


    }//GEN-LAST:event_connectRemoteActionPerformed

    private void menuRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoteActionPerformed


    }//GEN-LAST:event_menuRemoteActionPerformed

    private void valideFormulaireActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valideFormulaireActionPerformed

        System.out.println("description: " + descriptionField.getText());

        if (!modification) {  // Création de scéance 

            buildSceance();

            boolean result = controller.enregistrerSceanceRemote(sceance, login);
            if (!result) {

                montrerError("Accès remote refusé!", "Erreur authentification");
            } else {

                console.setText("La séquence a été enregistrée sur le remote");
                loadedSceance = true;

            }
            formulaire.setVisible(false);

        } else {  // Modification d'une scéance existante récupérée sur le cloud

            buildSceance();

            boolean result = controller.modifierSceance(sceance, login);
            if (!result) {

                montrerError("Accès remote refusé!", "Erreur authentification");
            } else {

                console.setText("La séquence a été modifiée sur le remote");
                updateDisplayInterface(0, sceance);
                loadedSceance = true;

            }
            formulaire.setVisible(false);

        }

        modification = false;
    }//GEN-LAST:event_valideFormulaireActionPerformed

    private void annulerFormulaireActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annulerFormulaireActionPerformed

        formulaire.setVisible(false);
    }//GEN-LAST:event_annulerFormulaireActionPerformed

    private void type2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_type2ActionPerformed

    private void annulerLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annulerLoginActionPerformed
        loginForm.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_annulerLoginActionPerformed

    private void ValideLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ValideLoginActionPerformed

        login.setUser(usernameField.getText());
        login.setPassword(passwordField.getText());

        loginForm.setVisible(false);
        try {
            boolean autorisation = controller.connexionRemote(login);
            if (autorisation) {

                connexionRemoteActive = true;
                statutRemote.setBackground(Color.GREEN);
                statutRemote.setForeground(Color.GREEN);
                deconnectRemote.setEnabled(true);
                connectRemote.setEnabled(false);
                setEnabledMenusSceance(true);
                return;

            } else {

                if (withoutRemote) {

                    montrerError("Connexion refusée!", "Erreur connexion remote");
                    return;

                } else {

                    int result = JOptionPane.showConfirmDialog(this, "Voulez-vous activer la connexion au remote?", "Connexion cloud",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {

                        withoutRemote = false;

                    } else if (result == JOptionPane.NO_OPTION) {

                        montrerError("Demande de connexion au remote rejetée", "Connexion cloud");
                        return;
                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);

        }

        boolean autorisation = false;
        try {
            autorisation = controller.connexionRemote(login);
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (autorisation) {

            connexionRemoteActive = true;
            statutRemote.setBackground(Color.GREEN);
            statutRemote.setForeground(Color.GREEN);
            deconnectRemote.setEnabled(true);
            connectRemote.setEnabled(false);
            setEnabledMenusSceance(true);

    }//GEN-LAST:event_ValideLoginActionPerformed
    }
    private void deconnectRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deconnectRemoteActionPerformed

        connexionRemoteActive = false;
        login.setUser(null);
        login.setPassword(null);
        connectRemote.setEnabled(true);
        deconnectRemote.setEnabled(false);
        statutRemote.setForeground(Color.red);
        statutRemote.setBackground(Color.red);
        usernameField.setText("");
        passwordField.setText("");
        setEnabledMenusSceance(false);
        montrerError("Vous êtes déconnecté du remote!", "Déconnexion remote demandée");
    }//GEN-LAST:event_deconnectRemoteActionPerformed

    private void menuModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuModifierActionPerformed

        try {

            modification = true;
            FormSeance f = controller.getSceance(initialisation.getSceance(), login);
            f.formaterDate();
            formulaire.setSize(900, 700);
            formulaire.setVisible(true);
            descriptionField.setText(f.getDescription());
            dateField.setText(f.getDate());
            counter1.setText(String.valueOf(f.getCompteur1()));
            counter2.setText(String.valueOf(f.getCompteur2()));
            counter3.setText(String.valueOf(f.getCompteur3()));
            type1.setSelectedItem(f.getType1());
            type2.setSelectedItem(f.getType2());
            type3.setSelectedItem(f.getType3());
            actif1.setSelected(f.getActif1());
            actif2.setSelected(f.getActif2());
            actif2.setSelected(f.getActif2());

        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
            modification = false;
        }
    }//GEN-LAST:event_menuModifierActionPerformed

    private void descriptionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descriptionFieldActionPerformed

    private void menuOuvrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOuvrirActionPerformed

        try {
            sceance = controller.getSceance(initialisation.getSceance(), login);
            sceance.formaterDate();
            System.out.println("Date scéance au chargement: " + sceance.getDate());
            console.setForeground(Color.red);
            console.setText("La scéance a été initialisée à partir du cloud");
            updateDisplayInterface(0, sceance);
            loadedSceance = true;
            setEnabledMenusSceance(true);
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuOuvrirActionPerformed

    private void menuEffacerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEffacerActionPerformed

        loadedSceance = false;
        sceance = null;
        sceance = new FormSeance();
        updateDisplayInterface(0, sceance);


    }//GEN-LAST:event_menuEffacerActionPerformed

    private void menuPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuPortActionPerformed

    private void cad_2_par_1minStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cad_2_par_1minStateChanged

        if (cad_2_par_1min.isSelected()) {

            cadence = 1;
            buildContext();

        }
    }//GEN-LAST:event_cad_2_par_1minStateChanged

    private void cad_1_par_2minsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cad_1_par_2minsStateChanged

        if (cad_1_par_2mins.isSelected()) {

            cadence = 2;
            buildContext();
        }
    }//GEN-LAST:event_cad_1_par_2minsStateChanged

    private void cad_1_par_5minsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cad_1_par_5minsStateChanged

        if (cad_1_par_5mins.isSelected()) {

            cadence = 3;
            buildContext();
        }
    }//GEN-LAST:event_cad_1_par_5minsStateChanged

    private void addRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRemoteActionPerformed
<<<<<<< HEAD
       
        String nomRemote = JOptionPane.showInputDialog("Entrez d'adresse IP du nouveau remote.", "Ajouter remote");
        try {
            initializer.addRemte(nomRemote);
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }//GEN-LAST:event_addRemoteActionPerformed

=======

        remoteForm.setVisible(true);
        remoteForm.setSize(450, 300);

    }//GEN-LAST:event_addRemoteActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        newRemoteName = nameRemote.getText();
        newRemoteAdress = adresseRemote.getSelectedText();
        try {
            initializer.addRemote(newRemoteName, newRemoteAdress);
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void deleteRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRemoteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteRemoteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        remoteForm.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

>>>>>>> tmp
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Interface().setVisible(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MenuFichier;
    private javax.swing.JLabel RS232;
    private javax.swing.JLabel Remote;
    private javax.swing.JMenu SelectionRemote;
    private javax.swing.JButton ValideLogin;
    private javax.swing.JCheckBox actif1;
    private javax.swing.JCheckBox actif2;
    private javax.swing.JCheckBox actif3;
    private javax.swing.JMenuItem addRemote;
    private javax.swing.JLabel adresseLabel;
    private javax.swing.JTextField adresseRemote;
    private javax.swing.JButton annulerFormulaire;
    private javax.swing.JButton annulerLogin;
    private javax.swing.JButton arret1;
    private javax.swing.JButton arret2;
    private javax.swing.JButton arret3;
    private javax.swing.JRadioButtonMenuItem baud115200;
    private javax.swing.JRadioButtonMenuItem baud19200;
    private javax.swing.JRadioButtonMenuItem baud38400;
    private javax.swing.JRadioButtonMenuItem baud9600;
    private javax.swing.JRadioButtonMenuItem bits6;
    private javax.swing.JRadioButtonMenuItem bits7;
    private javax.swing.JRadioButtonMenuItem bits8;
    private javax.swing.JRadioButtonMenuItem bits9;
    private javax.swing.JMenuItem btnConnexion;
    private javax.swing.JMenuItem btnDeconnexion;
    private javax.swing.JRadioButtonMenuItem cad_1_par_2mins;
    private javax.swing.JRadioButtonMenuItem cad_1_par_5mins;
    private javax.swing.JRadioButtonMenuItem cad_2_par_1min;
    private javax.swing.JMenu changeRemote;
    private javax.swing.JLabel compteur1;
    private javax.swing.JLabel compteur2;
    private javax.swing.JLabel compteur3;
    private javax.swing.JMenuItem connectRemote;
    private javax.swing.JTextField console;
    private javax.swing.JTextField counter1;
    private javax.swing.JTextField counter2;
    private javax.swing.JTextField counter3;
    private javax.swing.JTextField dateField;
    private javax.swing.JMenuItem deconnectRemote;
    private javax.swing.JMenu deleteRemote;
    private javax.swing.JTextField descriptionField;
    private javax.swing.JFrame formulaire;
    private javax.swing.ButtonGroup groupBaud;
    private javax.swing.ButtonGroup groupBits;
    private javax.swing.ButtonGroup groupCadence;
    private javax.swing.ButtonGroup groupParity;
    private javax.swing.ButtonGroup groupPorts;
    private javax.swing.ButtonGroup groupRemotes;
    private javax.swing.ButtonGroup groupStop;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JFrame loginForm;
    private javax.swing.JMenuItem menuAuto;
    private javax.swing.JMenu menuBaud;
    private javax.swing.JMenu menuBits;
    private javax.swing.JMenu menuCadence;
    private javax.swing.JMenu menuConfig;
    private javax.swing.JMenu menuConnexion;
    private javax.swing.JMenuItem menuEffacer;
    private javax.swing.JMenuItem menuManuel;
    private javax.swing.JMenuItem menuModifier;
    private javax.swing.JMenuItem menuNouveau;
    private javax.swing.JMenuItem menuOuvrir;
    private javax.swing.JMenu menuParity;
    private javax.swing.JMenu menuPort;
    private javax.swing.JMenuItem menuQuitter;
    private javax.swing.JMenu menuRemote;
    private javax.swing.JMenuItem menuSauvegardes;
    private javax.swing.JMenu menuStop;
    private javax.swing.JTextField nameRemote;
    private javax.swing.JLabel nomRemoteLabel;
    private javax.swing.JRadioButtonMenuItem parityEven;
    private javax.swing.JRadioButtonMenuItem parityNone;
    private javax.swing.JRadioButtonMenuItem parityOdd;
    private javax.swing.JTextField passwordField;
    private javax.swing.JButton pause;
    private javax.swing.JButton pause1;
    private javax.swing.JButton pause2;
    private javax.swing.JButton pause3;
    private javax.swing.JFrame remoteForm;
    private javax.swing.JButton reset1;
    private javax.swing.JButton reset2;
    private javax.swing.JButton reset3;
    private javax.swing.JRadioButton selectEch1;
    private javax.swing.JRadioButton selectEch2;
    private javax.swing.JRadioButton selectEch3;
    private javax.swing.JFileChooser selectionFichier;
    private javax.swing.JButton set1;
    private javax.swing.JButton set2;
    private javax.swing.JButton set3;
    private javax.swing.JTextField setCompteur1;
    private javax.swing.JTextField setCompteur2;
    private javax.swing.JTextField setCompteur3;
    private javax.swing.JButton start;
    private javax.swing.JLabel statutEch1;
    private javax.swing.JLabel statutEch2;
    private javax.swing.JLabel statutEch3;
    private javax.swing.JLabel statutRemote;
    private javax.swing.JLabel statutRs232;
    private javax.swing.JButton stop;
    private javax.swing.JRadioButtonMenuItem stop1;
    private javax.swing.JRadioButtonMenuItem stop2;
    private javax.swing.JLabel titre;
    private javax.swing.JLabel titre10;
    private javax.swing.JLabel titre11;
    private javax.swing.JLabel titre12;
    private javax.swing.JLabel titre13;
    private javax.swing.JLabel titre2;
    private javax.swing.JLabel titre3;
    private javax.swing.JLabel titre4;
    private javax.swing.JLabel titre6;
    private javax.swing.JLabel titre7;
    private javax.swing.JLabel titre8;
    private javax.swing.JLabel titre9;
    private javax.swing.JLabel titreLogin;
    private javax.swing.JLabel titrePrincipal;
    private javax.swing.JComboBox<String> type1;
    private javax.swing.JComboBox<String> type2;
    private javax.swing.JComboBox<String> type3;
    private javax.swing.JTextField usernameField;
    private javax.swing.JButton valideFormulaire;
    private javax.swing.JLabel version;
    private javax.swing.JLabel voyant;
    // End of variables declaration//GEN-END:variables

    private void setStatusRS232(boolean statut) {

        if (statut) {

            statutRs232.setForeground(Color.GREEN);
            statutRs232.setBackground(Color.GREEN);
        } else {
            statutRs232.setForeground(Color.RED);
            statutRs232.setBackground(Color.RED);
        }

    }

    public void montrerError(String message, String titre) {
        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);
    }

    private Connecteur getConnecteur() {

        if (this.connecteur == null) {
            this.connecteur = new Connecteur();
            this.connecteur.addObserver(this);
        }
        return this.connecteur;

    }

    @Override
    public void update(Observable o, Object arg) {

        String inputLine = (String) arg;
        console.setText(inputLine);
        Context context = buildContext();
        controller.setContext(context);
        Rapport rapport = new Rapport();
        try {
            rapport = controller.parser(inputLine);
            System.out.println("rapport reçu");
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            traiterRapport(rapport);                    // Analyse du rapport pour mise à jour de l'interface
        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        console.setForeground(rapport.getColor());
        console.setText(rapport.getLog());

    }

    private void traiterRapport(Rapport rapport) throws IOException {  // Gestion des affichages en fonction des résultats remontés par Arduino

        System.out.println("Interface.traiterRapport");

        if (rapport.isFermeture()) {

            if (loadedSceance) {

                controller.actualiserSceanceRemote(rapport.getFormSeance(), login);
            }

            System.exit(0);
        }

        if (rapport.isSauvegarde()) {

            controller.actualiserSceanceRemote(rapport.getFormSeance(), login);
            return;
        }
        if (rapport.isAcquittement()) {

            startRequested();
            connecteur.envoyerData(Constants.ORDRE_MARCHE);
            return;
        }

        List<String> totaux = new ArrayList<>();
        totaux.add(Long.toString(rapport.getFormSeance().getCompteur1()));
        // System.out.println("total1 côté parser interface: " + totaux.get(0));
        totaux.add(Long.toString(rapport.getFormSeance().getCompteur2()));
        totaux.add(Long.toString(rapport.getFormSeance().getCompteur3()));

        //  List<Boolean> erreurs = convertArrayToList(rapport.getErreurs());
        List<Boolean> erreurs = new ArrayList<>();

        erreurs.add(rapport.getFormSeance().getErreur1());
        erreurs.add(rapport.getFormSeance().getErreur2());
        erreurs.add(rapport.getFormSeance().getErreur3());

        List<Boolean> actifs = new ArrayList<>();
        actifs.add(rapport.getFormSeance().getActif1());
        actifs.add(rapport.getFormSeance().getActif2());
        actifs.add(rapport.getFormSeance().getActif3());

        List<Boolean> pauses = new ArrayList<>();
        pauses.add(rapport.getFormSeance().getPause1());
        pauses.add(rapport.getFormSeance().getPause2());
        pauses.add(rapport.getFormSeance().getPause3());

        List<Boolean> arrets = new ArrayList<>();
        arrets.add(rapport.getFormSeance().getInterrompu1());
        arrets.add(rapport.getFormSeance().getInterrompu2());
        arrets.add(rapport.getFormSeance().getInterrompu3());
        sceance = rapport.getFormSeance();

        proccessStatusLists(rapport.getMessage(), totaux, erreurs, actifs, pauses, arrets);

    }

    private void startWaiting(boolean activation) {

        if (activation) {

            start.setEnabled(true);
            start.setForeground(new Color(0, 102, 0));
            pause.setEnabled(false);
            pause.setForeground(Color.GRAY);
            stop.setEnabled(false);
            stop.setForeground(Color.GRAY);

        } else {

            start.setEnabled(false);
            start.setForeground(Color.GRAY);
            pause.setEnabled(true);
            pause.setForeground(Color.ORANGE);
            stop.setEnabled(true);
            stop.setForeground(Color.RED);

        }

    }

    private void startRequested() {

        test_off = false;
        test_on = true;
        test_pause = false;
        voyant.setForeground(Color.GREEN);
        voyant.setBackground(Color.GREEN);
        pause.setEnabled(true);
        pause.setForeground(new Color(255, 102, 0));
        start.setEnabled(false);
        start.setForeground(Color.GRAY);
        stop.setEnabled(true);
        stop.setForeground(Color.RED);
    }

    private void pauseRequested() {

        if (auto) {
            test_off = false;
            test_on = true;
            test_pause = true;
            voyant.setForeground(Color.ORANGE);
            voyant.setBackground(Color.ORANGE);
            pause.setEnabled(false);
            pause.setForeground(Color.GRAY);
            start.setEnabled(true);
            start.setForeground(new Color(0, 102, 0));
            stop.setEnabled(true);
            stop.setForeground(Color.RED);

        } else {

        }

    }

    private void stopRequested() {

        if (auto) {

            test_off = true;
            test_on = false;
            test_pause = false;
            voyant.setForeground(Color.RED);
            voyant.setBackground(Color.RED);
            pause.setEnabled(false);
            pause.setForeground(Color.GRAY);
            start.setEnabled(true);
            start.setForeground(new Color(0, 102, 0));
            stop.setEnabled(false);
            stop.setForeground(Color.GRAY);

        } else {

        }

    }

    private void resetStateMachine() {

        test_off = true;
        test_on = false;
        test_pause = false;
        arret_valide = false;

    }

    private void activationVoyant(boolean activation) {

        if (auto) {

            if (activation) {

                voyant.setForeground(Color.GREEN);
                voyant.setBackground(Color.GREEN);

            } else {

                voyant.setForeground(Color.RED);
                voyant.setBackground(Color.RED);
            }

        } else {

        }

    }

    public boolean[] getActifs() {
        return actifs;
    }

    public void setActifs(boolean[] actifs) {
        this.actifs = actifs;
    }

    public long[] getTotaux() {
        return totaux;
    }

    public void setTotaux(long[] totaux) {
        this.totaux = totaux;
    }

    public boolean[] getErreurs() {
        return erreurs;
    }

    public void setErreurs(boolean[] erreurs) {
        this.erreurs = erreurs;
    }

    public void setTotal1(long total1) {
        this.totaux[0] = total1;
    }

    public void setTotal2(long total2) {
        this.totaux[1] = total2;
    }

    public void setTotal3(long total3) {
        this.totaux[2] = total3;
    }

    public long getTotal1() {
        return totaux[0];
    }

    public long getTotal2() {
        return totaux[1];
    }

    public long getTotal3() {
        return totaux[2];
    }

    public boolean getActif1() {
        return actifs[0];
    }

    public boolean getActif2() {
        return actifs[1];
    }

    public boolean getActif3() {
        return actifs[2];
    }

    public void setActif1(boolean actifs1) {
        this.actifs[0] = actifs1;
    }

    public void setActif2(boolean actifs2) {
        this.actifs[1] = actifs2;
    }

    public void setActif3(boolean actifs3) {
        this.actifs[2] = actifs3;
    }

    public boolean getErreur1() {
        return erreurs[0];
    }

    public boolean getErreur2() {
        return erreurs[1];
    }

    public boolean getErreur3() {
        return erreurs[2];
    }

    public void setErreur1(boolean erreur1) {
        this.erreurs[0] = erreur1;
    }

    public void setErreur2(boolean erreur2) {
        this.erreurs[1] = erreur2;
    }

    public void setErreur3(boolean erreur3) {
        this.erreurs[2] = erreur3;
    }

    private void envoyerInitCompteur(int i) {

        try {
            String compteur = setCompteurs.get(i - 1).getText();
            try {
                Long.parseLong(compteur);
                String ordre = ordresSETS.get(i - 1) + ":" + compteur;
                System.out.println("Ordre:" + ordre);
                connecteur.envoyerData(ordre);

            } catch (Exception e) {

                montrerError("Vous devez indiquez une valeur numérique!", "Erreur de format");
            }

        } catch (Exception e) {

            montrerError("Vous devez determiner une valeur", "Défaut de valeur");
        }

    }

    private void envoyerResetCompteur(int i) {

        String ordre = ordresRAZ.get(i - 1);
        System.out.println("Ordre:" + ordre);
        connecteur.envoyerData(ordre);
    }

    private void envoyerOrdrePause(int i) {

        String ordre = ordresPAUSES.get(i - 1);
        System.out.println("Ordre:" + ordre);
        connecteur.envoyerData(ordre);
    }

    private void envoyerOrdreStop(int i) {

        String ordre = ordresSTOP.get(i - 1);
        System.out.println("Ordre:" + ordre);
        connecteur.envoyerData(ordre);
    }

    private void envoyerOdreCadence(int i) {

        String ordre = ordresCadences.get(i - 1);
        System.out.println("Ordre:" + ordre);
        connecteur.envoyerData(ordre);

    }

    private int envoyerConfiguration() {

        String ordre = Constants.CONFIG;
        String s;

        for (int i = 0; i < 3; i++) {

            actifs[i] = echantillonsActifs.get(i).isSelected();

            s = actifs[i] ? ":1" : ":0";
            ordre = ordre + s;

        }
        if (ordre.equals("W:CONFIG:0:0:0")) {

            montrerError("Vous devez sélectionner les échantillons actifs", "Défaut de configuration");
            setEnabledSelecteurEchantillons(true);
            return -1;
        }

        String cadence = null;
        if (cad_1_par_2mins.isSelected()) {
            cadence = ":2";
        }

        if (cad_1_par_5mins.isSelected()) {
            cadence = ":3";
        }

        if (cad_2_par_1min.isSelected()) {
            cadence = ":1";
        }

        ordre = ordre + cadence;

        String mode = null;

        if (menuManuel.isSelected()) {

            mode = ":0";

        } else {
            mode = ":1";
        }

        ordre = ordre + mode;

        System.out.println("Config: " + ordre);
        connecteur.envoyerData(ordre);
        return 0;

    }

    private void fermeture() {

        if (connexionRS232Active) {

            connecteur.envoyerData(Constants.FERMETURE);

        } else {

            System.exit(0);
        }

    }

    private int closeWindow() {

        System.out.println("Fermeture programme");
        // montrerError("Utiliser le menu Fichier pour fermer!", "Fermeture programme");
        return 0;
    }

    private void resetSceance() {

        initializer.update("sceance", "na");
        menuModifier.setEnabled(false);
    }

    void setEnabledMenusSceance(boolean actif) {

        menuNouveau.setEnabled(actif);
        if (initialisation.getSceance().equals("na")) {
            menuModifier.setEnabled(false);
            menuOuvrir.setEnabled(false);
            menuEffacer.setEnabled(false);
        } else {
            menuModifier.setEnabled(actif);
            menuOuvrir.setEnabled(actif);
            menuEffacer.setEnabled(actif);
        }

        if (!loadedSceance) {
            menuModifier.setEnabled(false);
            menuEffacer.setEnabled(false);
        }

    }

    void setEnabledMenusConfiguration() {

        if (connexionRS232Active) {

            cad_1_par_2mins.setEnabled(true);
            cad_1_par_5mins.setEnabled(true);
            cad_2_par_1min.setEnabled(true);

        } else {

            cad_1_par_2mins.setEnabled(false);
            cad_1_par_5mins.setEnabled(false);
            cad_2_par_1min.setEnabled(false);

        }
    }

    void setEnabledSelecteurEchantillons(boolean actifs) {

        selectEch1.setEnabled(actifs);
        selectEch1.setForeground(Color.BLACK);
        selectEch2.setEnabled(actifs);
        selectEch2.setForeground(Color.BLACK);
        selectEch3.setEnabled(actifs);
        selectEch3.setForeground(Color.BLACK);

    }

    private Context buildContext() {

        Context context = new Context();
        context.setConnexionRS232Active(connexionRS232Active);
        context.setConnexionRemoteActive(connexionRemoteActive);
        context.setCadence(cadence);
        context.setLogin(login);
        context.setWithoutRemote(withoutRemote);
        return context;
    }

    private void buildSceance() {

        sceance = new FormSeance();
        sceance.setDescription(descriptionField.getText());
        sceance.setDate(dateField.getText());
        sceance.setActif1(actif1.isSelected());
        sceance.setActif2(actif2.isSelected());
        sceance.setActif3(actif3.isSelected());
        sceance.setCompteur1(Long.parseLong(counter1.getText()));
        sceance.setCompteur2(Long.parseLong(counter2.getText()));
        sceance.setCompteur3(Long.parseLong(counter3.getText()));
        sceance.setType1(type1.getSelectedItem().toString());
        sceance.setType2(type2.getSelectedItem().toString());
        sceance.setType3(type3.getSelectedItem().toString());
        sceance.toString();
    }

    private void updateDisplayInterface(int message, FormSeance sceance) {

        List<String> totaux = new ArrayList<>();
        List<Boolean> erreurs = new ArrayList<>();
        List<Boolean> actifs = new ArrayList<>();
        List<Boolean> pauses = new ArrayList<>();
        List<Boolean> arrets = new ArrayList<>();

        erreurs.add(sceance.getErreur1());
        erreurs.add(sceance.getErreur2());
        erreurs.add(sceance.getErreur3());

        totaux.add(Long.toString(sceance.getCompteur1()));
        totaux.add(Long.toString(sceance.getCompteur2()));
        totaux.add(Long.toString(sceance.getCompteur3()));

        actifs.add(sceance.getActif1());
        actifs.add(sceance.getActif2());
        actifs.add(sceance.getActif3());

        pauses.add(sceance.getPause1());
        pauses.add(sceance.getPause2());
        pauses.add(sceance.getPause3());

        arrets.add(sceance.getInterrompu1());
        arrets.add(sceance.getInterrompu2());
        arrets.add(sceance.getInterrompu3());

        proccessStatusLists(message, totaux, erreurs, actifs, pauses, arrets);

    }

    private List<Boolean> convertArrayToList(boolean[] array) {

        List<Boolean> liste = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {

            liste.add(array[i]);

        }
        return liste;

    }

    private void proccessStatusLists(int message, List<String> totaux, List<Boolean> erreurs, List<Boolean> actifs, List<Boolean> pauses, List<Boolean> arrets) {

        Color color = null;
        for (int i = 0; i < Constants.NBRE_ECHANTILLONS; i++) {

            Boolean erreur = erreurs.get(i);
            Boolean actif = actifs.get(i);
            Boolean pause = pauses.get(i);
            Boolean arret = arrets.get(i);

            String total = totaux.get(i);

            if (!actif) {

                echantillonsActifs.get(i).setSelected(false);
                color = Color.GRAY;

            } else {

                echantillonsActifs.get(i).setSelected(true);
                color = Color.BLUE;
            }

            if (pause && actif) {

                color = Color.MAGENTA;
            }

            if (arret && actif) {

                color = Color.YELLOW;
            }

            if (erreur && actif) {

                color = Color.RED;

            }

            JLabel lab1 = compteurs.get(i);
            lab1.setForeground(color);
            lab1.setText(total);

            JLabel lab2 = statutsEchs.get(i);
            lab2.setForeground(color);
            lab2.setBackground(color);

        }
    }

    private void getCadence() {

        if (cad_2_par_1min.isSelected()) {

            cadence = 1;
        }

        if (cad_1_par_2mins.isSelected()) {

            cadence = 2;
        }

        if (cad_1_par_5mins.isSelected()) {

            cadence = 3;

        }

    }

}

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.lang.Math.*;
import java.awt.event.*;

public class FrameField extends JFrame
{
	// Variabili di gioco
    private int mines = 10;
    private int cols = 10;
    private int rows = 10;


    public Cell userField[][] = new Cell[10][10];
	// Campo contenente tutti i bottoni (Matrice 10*10) visibili dall'utente
	// Inizialmente viene caricato vuoto, e confrontato
	// Ad ogni mossa con quello di gioco


    public Cell gameField[][] = new Cell[10][10];
	// Campo di gioco, nascosto, (Matrice 10*10) contiene tutte le informazioni
	// Di gioco, come le mine, i numeri che contano le mine etc..
	// Utilizzato per confrontare l'azione del giocatore sul vero campo di gioco
    

	// Riferimento a una cella che indicherà quella selezionata in un turno
	public Cell selected;
    
	// Pannelli e widget
    private JPanel flowNord = new JPanel();
    private JPanel gridCenter = new JPanel();
    
    public JButton startBtn = new JButton("Inizia");
    public JButton resetBtn = new JButton("Reset");
    
	public JCheckBox flagChk = new JCheckBox("Posiziona Bandierina");
	
    private ActListener Listener = new ActListener();
    

    public FrameField()
    {
	super("Prato Fiorito");

	Container main = this.getContentPane();

	main.setLayout(new BorderLayout());
	flowNord.setLayout(new FlowLayout());
	gridCenter.setLayout(new GridLayout(10,10));

	flowNord.add(startBtn);
	flowNord.add(resetBtn);
	flowNord.add(flagChk);

	main.add(flowNord,BorderLayout.NORTH);
	main.add(gridCenter,BorderLayout.CENTER);

	resetBtn.addActionListener(Listener);
	startBtn.addActionListener(Listener);

	prepareField();

	this.setSize(430,430);
	this.setVisible(true);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
    }

	// Metodo usato per attivare tutti i bottoni e iniziare la partita
	private void start(){
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				userField[i][j].setEnabled(true);			// Attivo tutti i bottoni
			}
		}
		startBtn.setEnabled(false);
		resetBtn.setEnabled(true);
	}
    
	// Metodo utilizzato per inizializzare i campi di gioco ( Utente e Gioco )
    private void initFields(){
		gridCenter.removeAll();								// Rimuovo tutti gli oggetti nel panel gridCenter(centrale)
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
          		gameField[i][j] = new Cell();				// Alloco una nuova cella per il campo di gioco in posizione i j 
				userField[i][j] = new Cell();				// Alloco una nuova cella per il campo dell'utente in posizione i j
				gridCenter.add(userField[i][j]);			// Aggiungo al panel centrale la cella dell'utente
				userField[i][j].addActionListener(Listener);// Aggiungo l'action listener
				userField[i][j].setEnabled(false);			// Disabilito il bottone
			}
		}
		gridCenter.validate();								// Aggiorno il panel gridCenter cosi si caricano i nuovi bottoni
	}

	// Metodo richiamato quando l'utente perde, praticamente 'copio' il campo di gioco in quello visibile
    private void showFullField(){
		for(int row=0;row<rows;row++){
			for(int col=0;col<cols;col++){
				if(!userField[row][col].isClicked()){							// Se la cella non è già stata rivelata
					userField[row][col].setType(gameField[row][col].getType());	// Imposto il tipo della cella dell'utente a quello del campo vero
					userField[row][col].setValue(gameField[row][col].getValue());// Imposto il valore alla cella dell'utente a quello del campo vero
					userField[row][col].setClicked(true);						// Imposto entrambe le celle come clickate
					gameField[row][col].setClicked(true);	
					if(userField[row][col].getType() == 'm'){					// Se è una mina
						userField[row][col].setText("*");						// Mostro nel bottone un *
					}else if(userField[row][col].getType() == 'e'){				// Se è vuoto
						userField[row][col].setText("");						// Vuoto
					}else if(userField[row][col].getType() == 'f'){				// Se è una bandiera
						userField[row][col].setText("B");
					}else{
						userField[row][col].setText(String.valueOf(gameField[row][col].getValue())); // Altrimenti imposto il numero
					}
					userField[row][col].setEnabled(false);						// Disattivo il bottone
				}
			}
		}
	}
    
    // Metodo che controlla se una cella è una mina
	private int isAMine(int row,int col){
		if(row<rows && col<cols && col>= 0 && row>=0){	// Valido la posizione
			if(gameField[row][col].getType()=='m'){		// Se il tipo è una mina
				return 1;								// Ritorno 1
			}
			return 0;
		}
		return 0;
	}
    
    // Metodo per contare quante mine ci sono vicino a una cella in determinate posizioni
	private int nearMines(int row, int col){
		int tot = 0;
		if(row<rows && col<cols && col>= 0 && row>=0){	// Controllo che la posizione sia valida
			tot += isAMine(row-1,col);					// Controllo la casella a sinistra della richiesta
			tot += isAMine(row-1,col-1);				// Controllo la casella in alto a sinistro dalla richiesta
			tot += isAMine(row+1,col);					// Controllo la casella destra della richiesta
			tot += isAMine(row+1,col-1);				// Controllo la cella a destra in alto della richiesta
			tot += isAMine(row,col-1);					// Controllo la cella sopra a quella richiesta
			tot += isAMine(row,col+1);					// Controllo la cella sotto a quella richiesta
			tot += isAMine(row-1,col+1);				// Controllo la cella in alto a destra della richiesta
			tot += isAMine(row+1,col+1);				// Controllo la cella in basso a destra dalla richiesta
		}
		return tot;
	}
    
    
	// Metodo usato per inizializzare il campo di gioco ( non quello visibile )
	private void prepareField(){
		int row,col;
		initFields();
		for(int i=0;i<mines;i++){							// For che carica tutte le mine nel campo da gioco (nascosto)
			do{
				row = (int)(Math.random() * rows);			// Riga casuale da 0 al massimo delle righe
				col = (int)(Math.random() * cols);			// Colonna casuale dal 0 al massimo delle colonne
				gameField[row][col].setType('m');			// Imposto il tipo a mina
			}while(gameField[row][col].getType() != 'm');	// Finche il tipo in quelle coordinate è diverso da una mina
		}


		for(int i=0;i<rows;i++){							// Calcolo il numero di mine in ogni cella
			for(int j=0;j<cols;j++){
				if(gameField[i][j].getType() != 'm'){		// Se non è una mina
					if(nearMines(i,j) != 0){				// Richiamo il metodo nearMines che conta quante mine ci sono vicino alla cella attuale  e se non è 0
						gameField[i][j].setType('n');		// Ci imposto il tipo 'numero' 
						gameField[i][j].setValue(nearMines(i,j));		// Imposto il valore della cella al numero di mine intorno
					}
				}
			}
		}
		startBtn.setEnabled(true);
		resetBtn.setEnabled(false);
	}

	// Metodo che controlla le celle attorno a se e si chiama ricorsivamente finche non trova un numero
	private void checkEmptyCell(int row,int col){
		if(row<rows && col<cols && col>= 0 && row>=0){
			if(!gameField[row][col].isClicked() && !userField[row][col].isClicked() && gameField[row][col].getType() == 'e'){		// Se la cella in questa posizione non è stata già controllata, ed è vuota ( Non numero, mina, o bandiera)
				userField[row][col].setClicked(true);			// Imposto la cella a controllata, sia per l'utente che per il campo nascosto
				userField[row][col].setType('e');				// Imposto il tipo e 'empty'							
				userField[row][col].setValue(' ');				// Il valore a vuoto
				userField[row][col].setText("");				// Il testo a '' sul campo
				userField[row][col].setEnabled(false);			// Disattivo il bottone
				gameField[row][col].setClicked(true);
				checkEmptyCell(row - 1, col - 1);				// Richiama se stesso controllando ogni cella attorno ( come nearMines )
				checkEmptyCell(row, col - 1);
				checkEmptyCell(row + 1, col - 1);
				checkEmptyCell(row - 1, col);
				checkEmptyCell(row + 1, col);
				checkEmptyCell(row -1, col + 1);
				checkEmptyCell(row, col + 1);
				checkEmptyCell(row + 1, col + 1);

			}else if(gameField[row][col].getType() == 'n' && gameField[row][col].getValue() != 0 && !gameField[row][col].isClicked() && !userField[row][col].isClicked()){	// Altrimenti se la cella è un numero, e non è ancora stata controllata imposta il valore nel campo dell'utente a quello del campo di gioco nascosto che indica il numero di mine vicine
				userField[row][col].setType(gameField[row][col].getType());			// Imposto il tipo della cella dell'utente a quella del campo
				userField[row][col].setValue(gameField[row][col].getValue());		// Imposto il valore della cella dell'utente a quella del campo
				userField[row][col].setClicked(true);								// Imposto come controllata
				gameField[row][col].setClicked(true);	
				userField[row][col].setText(String.valueOf(gameField[row][col].getValue()));	// Come testo del bottone imposto il valore di mine vicino alla cella convertendo in stringa il valore
				userField[row][col].setEnabled(false);								// Disabilito il bottone
			}
		}
	}

	// Metodo per piazzare/rimuovere una bandiera in una posizione
	private void handleFlag(int row,int col){
		if(row<rows && col<cols && col>= 0 && row>=0 ){
			if(userField[row][col].getType() != 'f' && !userField[row][col].isClicked()){	// Se non è già una bandiera è non è ancora stata clickata
				userField[row][col].setPrevtype(userField[row][col].getType());				// Imposto il tipo precedente alla cella a quello attuale ( cosi quando rischiaccio la bandiera torna quello di prima )
				userField[row][col].setType('f');											// Imposto il tipo a flag					
				userField[row][col].setValue('B');											// Imposto il valore a B
				userField[row][col].setText("B");											// Imposto il testo del bottone a B
			}else if(userField[row][col].getType() == 'f'){									// Altrimenti se è gia una bandiera
				userField[row][col].setType(userField[row][col].getPrevtype());				// Imposto al tipo il tipo precendete
				userField[row][col].setText("");											// Metto il testo vuoto
			}
		}
	}

	// Metodo usato per determinare se l'utente ha vinto
	private boolean victory(){
		int checked = 0;							// Celle controllate
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				if(gameField[i][j].isClicked()){	// Se la cella del campo di gioco è controllata in questa posizione
					checked++;						// Aumento il numero
				}
			}
		}
		if(checked+mines==rows*cols){				// Se il prodotto di righe*colonne è uguale alle caselle controllate piu le mine
			return true;							// L'utente ha vinto
		}
		return false;
	}

    public static void main(String[] args){

		try {
			// Metto il tema di windows
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} 	// Se non è supportato printa l'errore
		catch (Exception e) {
			System.out.println(e);
		}
		// Alloco nuovo oggetto di FrameField
		FrameField f = new FrameField();
	}

	public class ActListener implements ActionListener{

		public void actionPerformed(ActionEvent e){
			Object src = (Object) e.getSource();

			if(src == startBtn){		// Se l'evento viene dal bottone start
				start();				// richiamo il metodo start
			}

			if(src == resetBtn){		// Se l'evento viene dal bottone reset
				prepareField();			// ripreparo il campo di gioco
			}

			for(int i=0;i<rows;i++){
				for(int j=0;j<cols;j++){
		      		if(src==userField[i][j] && !userField[i][j].isClicked()){		// Se l'evento viene da una cella che non è ancora stata controllata
						selected = gameField[i][j];									// Salvo in selected la cella corrispondente a quella nel campo di gioco(con le informazioni)
						 if(flagChk.isSelected() || userField[i][j].getType() == 'f'){	// Se ho selezionato la checkbox della bandiera o il tipo della cella è una bandiera
							handleFlag(i,j);										// Richiamo il metodo che gestisce la bandiera
						}
						// Controllo se è una mina
						else if(isAMine(i,j) == 1){
							JOptionPane.showMessageDialog(null,"Hai colpito una mina!");	// Messaggio sconfitta
							showFullField();										// Mostro il campo completo
						}
						// Controllo se è un numero
						else if(selected.getType() == 'n'){
							selected.setClicked(true);								// Imposto a clickato sia il campo utente che gioco
							userField[i][j].setClicked(true);
							userField[i][j].setValue(selected.getValue());			// Imposto il valore della casella a quella del gioco
							userField[i][j].setType('n');							// Imposto il tipo
							userField[i][j].setText(String.valueOf(selected.getValue()));	// Imposto il testo a quello del suo valore
							userField[i][j].setEnabled(false);						// Disabilito il bottone
						}
						else if(selected.getType() == 'e'){							// Se è vuota
							checkEmptyCell(i,j);									// Richiamo la ricorsiva che controlla tutto intorno fino a un numero
						}
						if(victory()){												// Controllo se la partita non è stat vinta
							JOptionPane.showMessageDialog(null,"Complimenti hai vinto!");	// Messaggio vittoria
							showFullField();										// Mostro il campo completo
						}
					}
				}
			}

		}

	}

}

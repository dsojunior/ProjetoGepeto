
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author David pika das Galáxias
 */
public class ITC {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        File INPUT = new File (args[0]);
        File OUTPUT = new File (args[1]);
        Automaton automaton = readAutomaton(INPUT);
        automaton.print();
        
    }
    
    public static Automaton readAutomaton(File input){
        FileReader fr = null;
        Automaton automaton = new Automaton();
        
        try {
            //criando o automato a partir da entrada
            fr = new FileReader(input);
            BufferedReader br = new BufferedReader(fr);
            
            //Lendo a primeira linha do arquivo de entrada
            String[] line1 = br.readLine().split(" ");
            //Verificando o numero de estados presentes no automato
            int n = Integer.parseInt(line1[0]);
            //Criando n estados
            for (int i = 0; i < n; i++) {
                automaton.add(new State(i));
            }
            
            //nao sei pra que é usado ainda .-., agora ja sei
            int s = Integer.parseInt(line1[1]);
            
            //definindo o estado inicial
            int q0 = Integer.parseInt(line1[2]);
            automaton.getStates().get(q0).set0();
            
            automaton.setN(n);
            automaton.setS(s);
            automaton.setQ0(q0);
            
            
            //Lendo a segunda linha do arquivo de entrada
            String[] line2 = br.readLine().split(" ");
            int[] qFinals = new int[line2.length]; 
            //obtendo os estados finais
            for (int i = 0; i < line2.length; i++) {
                qFinals[i] = Integer.parseInt(line2[i]);
            }
            //atualizando os finais no automato
            for (int i = 0; i < qFinals.length; i++) {
                if(qFinals[i] == 1) automaton.getStates().get(i).setFinal();
            }
            
            //Lendo a matriz que define o automato.
            int[][] automatonData = new int[n][s];
            for (int i = 0; i < automatonData.length; i++) {
		String[] linha = br.readLine().trim().split(" ");
		int k = 0;
		for (int l = 0; l < linha.length; l++) {
                    if (!linha[l].equals(" ") && !linha[l].equals("")){
			automatonData[i][k] = Integer.parseInt(linha[l]);
			k++;
                    }
		}		
            }
            
            //Adicionando as transações aos estados
            for (int i = 0; i < automatonData.length; i++) {
                for (int j = 0; j < automatonData[i].length; j++) {
                    //if(automatonData[i][j] != -1){
                        automaton.getStates().get(i).addTransition(j, automatonData[i][j]);
                    //}
                }
            }
            
        } catch (IOException ex) {
            System.out.println("Erro ao abrir arquivo");
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                System.out.println("Erro ao fechar reader");
            }
        }
        return automaton;
    }
}


//classe que define um automato
class Automaton{
    
    private int n;
    private int s;
    private int q0;
    ArrayList<State> states = new ArrayList<>();
    
    public void add(State q){
        states.add(q);
    }    

    public ArrayList<State> getStates() {
        return states;
    }
    
    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getQ0() {
        return q0;
    }

    public void setQ0(int q0) {
        this.q0 = q0;
    }
    
    public void print(){
        System.out.println(n +  " " +  s + " " + q0);
        for (State s : states) {
            if (s.isqFinal()) System.out.print(1 + " ");
            else System.out.print(0 + " ");
        }
        System.out.println();
        for (State s : states) {
            for (Transition t : s.getTransitions()) {
                System.out.print(t.getQ() + " ");
            }
            System.out.println();
        }
    }
}

//classe que define um estado
class State {
    int q;
    boolean q0;
    boolean qFinal;
    ArrayList<Transition> transitions;

    public State(int q) {
        this.q = q;
        this.q0 = false;
        this.qFinal = false;
        transitions = new ArrayList<>();
    }
    
    public void setFinal(){
        this.qFinal = true;
    }

    public boolean isQ0() {
        return q0;
    }

    public boolean isqFinal() {
        return qFinal;
    }
    
    public void set0(){
        this.q0 = true;
    }
    
    public void addTransition(int a, int q){
        Transition t = new Transition(a, q);
        transitions.add(t);
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }
    
} 

//classe que define as transições entre estados
class Transition {
    int a;
    int q;
    
    public Transition(int a, int q){
       this.q = q;
       this.a = a;
    }

    public int getA() {
        return a;
    }

    public int getQ() {
        return q;
    }
}

//pode ser util futuramente
//class Transition2 {
//    int a;
//    State q;
//    
//    public Transition2(int a, State q){
//       this.q = q;
//       this.a = a;
//    }
//}
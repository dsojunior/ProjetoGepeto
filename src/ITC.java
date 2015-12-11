
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
        //File OUTPUT = new File (args[1]);
        Automaton automaton = readAutomaton(INPUT);
        automaton = removerEstadosInacessiveis(automaton);
        //automaton.print();
        automaton = removerEstadosInuteis(automaton);
        //automaton.print();
        automaton = removerEstadosEquivalentes(automaton);
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
            
            //Adicionando a matrizDeTransicao
            automaton.setMatrizDeTransicao();
            
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
    
    public static Automaton removerEstadosInacessiveis(Automaton a){
    	//Automaton a = aut;
    	Grafo g = new Grafo (a);
    	g.buscaProfundidade(g, a.getQ0());
    	
    	for (int i = 0; i < g.verticesVisitados.length; i++) {
			if (g.verticesVisitados[i] == false && a.states.get(i).isQ0() == false){
				for (State s : a.states) {
		            for (Transition t : s.getTransitions()) {
		                if(t.getQ() == i) t.setQ(-1); //Coloca transicao pro "vazio"
		            }
		        }
			}
		}
    	
    	int i = 0;
    	int j = i;
    	while (i<g.verticesVisitados.length){
    		if (g.verticesVisitados[i] == false && a.states.get(j).isQ0() == false){
    			a.states.remove(j);
    			a.setN(a.getN() - 1);
    			if(j<a.getQ0()) a.setQ0(a.getQ0() - 1);
    			for(State s : a.getStates()){
    				if(j<s.getQ()){
    					s.setQ(s.getQ()-1);
    				}
    				for(Transition t : s.getTransitions()){
    					if (j< t.getQ()) t.setQ(t.getQ() -1);
    				}
    			}
    			j--;
    		}
    		j++;
    		i++;
		}
    	a.setMatrizDeTransicao();
    	return a;
    }
    
    public static Automaton removerEstadosInuteis(Automaton a){
    	ArrayList <State> estados = a.getStates();
    	int estadoFinal = -1;
    	//ArrayList <Integer> estadosQueSeraoRemovidos = new ArrayList<Integer>();
    	boolean [] estadosQueSeraoRemovidos = new boolean [a.getN()];
    	
    	for (int j = 0; j < estadosQueSeraoRemovidos.length; j++) {
			estadosQueSeraoRemovidos[j] = false;
		}
    	
    	for (State es : estados){
    		if(es.isqFinal() == true) estadoFinal = es.getQ();
    	}
    	
    	for (State es : estados){
    		if(es.isQ0() == false && es.isqFinal() == false){
    	    	Grafo g = new Grafo (a);
    			g.buscaProfundidade(g, es.getQ());

    			for (int i = 0; i < g.verticesVisitados.length; i++) {
					if(g.verticesVisitados[i] == false && i == estadoFinal) estadosQueSeraoRemovidos[es.getQ()] = true;
				}
    		}
    	}
    	
    	//System.out.println();
    	//a.print();
    	//System.out.println();
    	
    	//remove os estados
    	for (int i = 0; i < estadosQueSeraoRemovidos.length; i++) {
			if (estadosQueSeraoRemovidos[i] == true){
				for (State s : a.states) {
		            for (Transition t : s.getTransitions()) {
		                if(t.getQ() == i)t.setQ(-1); //Coloca transicao pro "vazio"
		            }
		        }
			}
		}
    	
    	
    	
    	int i = 0;
    	int j = i;
    	while (i<estadosQueSeraoRemovidos.length){
    		if (estadosQueSeraoRemovidos[i] == true){
    			a.states.remove(j);
    			a.setN(a.getN() - 1);
    			if(j < a.getQ0()) a.setQ0(a.getQ0() - 1);
    			for(State s : a.getStates()){
    				if(j<s.getQ()){
    					
    					s.setQ(s.getQ()-1);
    				}
    				for(Transition t : s.getTransitions()){
    					if (j< t.getQ()) t.setQ(t.getQ() -1);
    				}
    			}
    			j--;
    		}
    		j++;
    		i++;
		}
    	a.setMatrizDeTransicao();
    	return a;
    }
    
    public static Automaton removerEstadosEquivalentes(Automaton a){
    	int[][] matrizDeSemelhantes = new int [a.getN()][a.getN()];  	
    	ArrayList <State> st = a.getStates();
    	
    	//Preenche matriz inicialmente com zeros, e -1 na diagonal principal
    	for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes[0].length; j++) {
				if(i==j)matrizDeSemelhantes[i][j] = -1;
				else matrizDeSemelhantes[i][j] = 0;
			}
		}
    	
    	
    	/*//1a etapa - Agrupar finais e nao finais    	
    	for (int i = 0; i < st.size(); i++) {
    			int j = i+1;
    			if( j >= st.size()) break;
				if(st.get(i).isqFinal() == true && st.get(j).isqFinal() == true) {
					matrizDeSemelhantes[i][j] = 1;
					matrizDeSemelhantes[j][i] = 1;
				}
				if(st.get(i).isqFinal() == false && st.get(j).isqFinal() == false) {
					matrizDeSemelhantes[i][j] = 1;
					matrizDeSemelhantes[j][i] = 1;
				}
		}*/
    	
    	
    	for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes[0].length; j++) {
				if(i != j){				
					if(st.get(i).isqFinal() == true && st.get(j).isqFinal() == true) {
						matrizDeSemelhantes[i][j] = 1;
						matrizDeSemelhantes[j][i] = 1;
					}
					if(st.get(i).isqFinal() == false && st.get(j).isqFinal() == false) {
						matrizDeSemelhantes[i][j] = 1;
						matrizDeSemelhantes[j][i] = 1;
					}
				}
			}
		}
    	
    	/*for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				System.out.print(matrizDeSemelhantes[i][j] + " ");
			}
			System.out.println();
		}*/
    	
    	
    	//2a etapa - Marcacao dos estados que nao tenham transiçoes sobre os mesmos simbolos
    	for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				if (matrizDeSemelhantes [i][j] == 1){
					ArrayList<Transition> ListaTransicoesEstado1 = st.get(i).transitions;
					ArrayList<Transition> ListaTransicoesEstado2 = st.get(j).transitions;
					ArrayList<Integer> TransicoesEstado1 = new ArrayList<Integer>();
					ArrayList<Integer> TransicoesEstado2 = new ArrayList<Integer>();
					for (int k = 0; k < ListaTransicoesEstado1.size(); k++) {
						int adicionarTransicao = ListaTransicoesEstado1.get(k).getA();
						int proximoDestino = ListaTransicoesEstado1.get(k).getQ();
						if(proximoDestino != -1) TransicoesEstado1.add(adicionarTransicao);
					}
					for (int k = 0; k < ListaTransicoesEstado2.size(); k++) {
						int adicionarTransicao = ListaTransicoesEstado2.get(k).getA();
						int proximoDestino = ListaTransicoesEstado2.get(k).getQ();
						if(proximoDestino != -1) TransicoesEstado2.add(adicionarTransicao);
					}
					
					/*if (i == 0 && j ==1){
						System.out.println("Transicoes Estado 1 " + ListaTransicoesEstado1.size());
						System.out.println("Transicoes Estado 2 " + ListaTransicoesEstado2.size());
						
						for(Integer num : TransicoesEstado1){
							System.out.println("VALOR da Transição: " + num.intValue());
						}
					}*/
					
					//if(!(TransicoesEstado1.containsAll(TransicoesEstado2)) && !(TransicoesEstado2.containsAll(TransicoesEstado1)) && !(TransicoesEstado1.size() == TransicoesEstado2.size())) matrizDeSemelhantes[i][j] = 0;
					if (!(TransicoesEstado1.size() == TransicoesEstado2.size() && (TransicoesEstado1.containsAll(TransicoesEstado2))&&(TransicoesEstado2.containsAll(TransicoesEstado1)))) matrizDeSemelhantes[i][j] = 0;
				}
			}
		}
    	
    	/*for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				System.out.print(matrizDeSemelhantes[i][j] + " ");
			}
			System.out.println();
		}*/
    	
    	    	
    	//3a etapa - Marcação dos estados que possuam transições não equivalentes (não vao para o mesmo estado)
    	for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				if (matrizDeSemelhantes [i][j] == 1){
					ArrayList<Transition> ListaTransicoesEstado1 = st.get(i).transitions;
					ArrayList<Transition> ListaTransicoesEstado2 = st.get(j).transitions;
					ArrayList<Integer> TransicoesEstado1 = new ArrayList<Integer>();
					ArrayList<Integer> TransicoesEstado2 = new ArrayList<Integer>();
					ArrayList<Integer> _TransicoesEstado1 = new ArrayList<Integer>();
					ArrayList<Integer> _TransicoesEstado2 = new ArrayList<Integer>();
					for (int k = 0; k < ListaTransicoesEstado1.size(); k++) {
						int estadoQ = ListaTransicoesEstado1.get(k).getQ();
						if (estadoQ != -1) TransicoesEstado1.add(estadoQ);
					}
					for (int k = 0; k < ListaTransicoesEstado2.size(); k++) {
						int estadoQ = ListaTransicoesEstado2.get(k).getQ();
						if (estadoQ != -1) TransicoesEstado2.add(estadoQ);
					}
					for (int k = 0; k < ListaTransicoesEstado1.size(); k++) {
						int transicao = ListaTransicoesEstado1.get(k).getA();
						int proximoPasso = ListaTransicoesEstado1.get(k).getQ();
						if (proximoPasso != -1) _TransicoesEstado1.add(transicao);
					}
					for (int k = 0; k < ListaTransicoesEstado2.size(); k++) {
						int transicao = ListaTransicoesEstado2.get(k).getA();
						int proximoPasso = ListaTransicoesEstado2.get(k).getQ();
						if (proximoPasso != -1) _TransicoesEstado2.add(transicao);
					}
					if(!(TransicoesEstado1.containsAll(TransicoesEstado2)) && (TransicoesEstado2.containsAll(TransicoesEstado1)) && !(TransicoesEstado1.size() == TransicoesEstado2.size()) && !(_TransicoesEstado1.size() == _TransicoesEstado2.size()) && !(_TransicoesEstado2.containsAll(_TransicoesEstado1)) && !(_TransicoesEstado1.containsAll(_TransicoesEstado2))) matrizDeSemelhantes[i][j] = 0;
					
				}
			}
		}
    	
    	//3a etapa
    	/*for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				if (matrizDeSemelhantes [i][j] == 1){
					ArrayList<Transition> ListaTransicoesEstado1 = st.get(i).transitions;
					ArrayList<Transition> ListaTransicoesEstado2 = st.get(j).transitions;
					
					boolean ehEquivalente = true;
					for (Transition t0 : ListaTransicoesEstado1){
						for (Transition t1 : ListaTransicoesEstado2){
							if(t0.getA() == t1.getA()){
								if(t0.getQ() != t1.getQ()) {
									ehEquivalente = false;
									break;
								}
							}
						}
						if (ehEquivalente == false) break;
					}
					
					
					if(ehEquivalente == false) {
						matrizDeSemelhantes[i][j] = 0;
					}
				}
			}
    	}*/
				
    	
    	
    	
    	
    	
    	
    	/*for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				System.out.print(matrizDeSemelhantes[i][j] + " ");
			}
			System.out.println();
		}*/
    	
    	
    	
    	
    	//4a etapa
    	int [] rep = new int [a.getN()];
    	for (int i = 0; i < rep.length; i++) {
			rep[i] = -1;
		}
    	int cont = 0;
    	
    	for (int i = 0; i < rep.length; i++) {
			if (rep[i] == -1){
				cont++;
				rep [i] = cont-1;
				for (int j = 0; j < matrizDeSemelhantes[i].length; j++) {
					if (matrizDeSemelhantes[i][j] == 1) rep[j] = rep[i];
				}
			}
		}
    	
    	//Finaliza as modificações do Automato (controi o Automato Minimo)
    	Automaton newAutomaton = new Automaton();
    	for (int i = 0; i < cont; i++) {
            newAutomaton.add(new State(i));
        }
    	newAutomaton.getStates().get(rep[a.getQ0()]).set0(); //Estado Inicial
    	
    	for (State s : a.getStates()) {
			if(s.isqFinal()){
				newAutomaton.getStates().get(rep[a.getStates().indexOf(s)]).setFinal();
			}
		}
    	
        newAutomaton.setN(cont);
        newAutomaton.setS(a.getS());
        newAutomaton.setQ0(rep[a.getQ0()]);
        
        int[][] matrizDeTransicaoOriginal = a.getMatrizDeTransicao();
        int[][] novaMatrizDeTransicao = new int [newAutomaton.getN()][newAutomaton.getS()];
    	
        //Popula a nova matriz inicalmente com -1
        for (int i = 0; i < novaMatrizDeTransicao.length; i++) {
			for (int j = 0; j < novaMatrizDeTransicao[0].length; j++) {
				novaMatrizDeTransicao[i][j] = -1;
			}
		}
        
        /*//Popula a nova matriz baseando-se nas equivalencias de estados
        for (int i = 0; i < novaMatrizDeTransicao.length; i++) {
			for (int j = 0; j < novaMatrizDeTransicao[0].length; j++) {
				if(matrizDeTransicaoOriginal[i][j] != -1){
					novaMatrizDeTransicao[ rep[i] ] [j] = rep[matrizDeTransicaoOriginal[i][j]];
				}
			}
		}*/
        
        
        //Popula a nova matriz baseando-se nas equivalencias de estados
        for (int i = 0; i < matrizDeTransicaoOriginal.length; i++) {
        	for (int j = 0; j < matrizDeTransicaoOriginal[0].length; j++) {
        		if (novaMatrizDeTransicao[rep[i]][j] == -1) {
        			novaMatrizDeTransicao[rep[i]][j] = matrizDeTransicaoOriginal[i][j] != -1 ? rep[matrizDeTransicaoOriginal[i][j]] : -1;
        		}
        	}
        }
        
        //Adicionando as transações aos estados do novo automato
        for (int i = 0; i < novaMatrizDeTransicao.length; i++) {
            for (int j = 0; j < novaMatrizDeTransicao[i].length; j++) {
                //if(automatonData[i][j] != -1){
                    newAutomaton.getStates().get(i).addTransition(j, novaMatrizDeTransicao[i][j]);
                //}
            }
        }
    	
    	
    	
    	return newAutomaton;
    }
    
    
}


//classe que define um automato
class Automaton{
    
    private int n;
    private int s;
    private int q0;
    ArrayList<State> states = new ArrayList<>();
    int [][] matrizDeTransicoes;
    
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
    
    public void setMatrizDeTransicao(){
    	matrizDeTransicoes = new int [getN()][getS()];

		int i = 0;
		for (State s : states) {
			int j = 0;
            for (Transition t : s.getTransitions()) {
            	matrizDeTransicoes[i][j] = t.getQ();
            	j++;
            }
            i++;
        }
    }
    
    public int [][] getMatrizDeTransicao(){
    	return matrizDeTransicoes;
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
    
    public int getQ(){
    	return q;
    }
    
    public void setQ(int q){
    	this.q = q;
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
    
    public void setQ(int q){
    	this.q = q;
    }
}

class Grafo{
	int[][] matrizDeAdjacencia;
	int numeroVertices;
	boolean [] verticesVisitados;
	ArrayList <Integer> percurso;
	
	public Grafo(Automaton a){
		percurso = new ArrayList<Integer>();
		
		matrizDeAdjacencia = new int[a.getN()][a.getN()];
		
		//Inicializa todas as posicoes do grafo com -1
		for (int i = 0; i < matrizDeAdjacencia.length; i++) {
			for (int j = 0; j < matrizDeAdjacencia[i].length; j++) {
				matrizDeAdjacencia[i][j] = -1;
			}
		}
		
		for (int i = 0; i < a.matrizDeTransicoes.length; i++) {
			for (int j = 0; j < a.matrizDeTransicoes[0].length; j++) {
				if(a.matrizDeTransicoes[i][j] != -1){
					matrizDeAdjacencia[i][a.matrizDeTransicoes[i][j]] = j;
				}
			}
		}
		
		numeroVertices = a.getN();
		verticesVisitados = new boolean [numeroVertices];
		
		for (int i = 0; i < matrizDeAdjacencia.length; i++) {
			verticesVisitados[i] = false;
		}
	}
	
	
	public void buscaProfundidade(Grafo grafo, int inicio) {    
	    
	    percurso.add(inicio);    
	    
	    verticesVisitados[inicio] = true;
	    for (int i = 0; i < grafo.numeroVertices; i++) {    
	        if (grafo.matrizDeAdjacencia[inicio][i] != -1 && verticesVisitados[i] == false) {    
	            buscaProfundidade(grafo, i);   
	            percurso.add(inicio); //Testar  
	        }    
	    }    
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
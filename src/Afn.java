//EP de ITC - MINIMIZACAO DE AUTOMATOS
//
//Integrantes - Numero USP - Turma
//
//David Henrique Regis Rodrigues - 9004633 - T04
//Decio de Souza Oliveira Junior - 9065701 - T04
//

import java.io.*;
import java.util.*;

//Classe principal do EP, responsavel pela manipulacao e pela modificacoes no automato
public class afn {

	//Metodo Main do EP
	public static void main(String[] args) throws IOException {         
		File INPUT = new File (args[0]); //Armazena o arquivo de entrada em um File
		File OUTPUT = new File (args[1]); //Armazena o arquivo de saida em um File
		Automaton automaton = readAutomaton(INPUT); //Transforma a entrada em txt em um ojeto da classe Automato, para a manipulacao
		automaton = removerEstadosInacessiveis(automaton); //Remove os Estados Inacessiveis, se houver
		automaton = removerEstadosInuteis(automaton); //Remove os Estados Inuteis, se houver
		automaton = removerEstadosEquivalentes(automaton); //Remove / Junta os Estados Equivalentes, se houver
		automaton.writeOutput(OUTPUT); //Escreve o automato final no arquivo de saida e finaliza o programa
	}

	//Metodo que le o txt de entrada e tranforma em Objeto da Classe Automaton para facilitar a manipulacao
	public static Automaton readAutomaton(File input){
		FileReader fr = null; //Inicia um File de Leitura
		Automaton automaton = new Automaton(); //Cria um novo objeto Automaton

		try {
			//Criando o automato a partir da entrada
			fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			String[] line1 = br.readLine().split(" "); //Lendo a primeira linha do arquivo de entrada
			int n = Integer.parseInt(line1[0]); //Verificando o numero de estados presentes no automato
			
			//Criando n estados, ou seja, n objeos da classe State
			for (int i = 0; i < n; i++) {
				automaton.add(new State(i));
			}

			int s = Integer.parseInt(line1[1]); //Lendo o numero de simbolos do alfabeto

			//Definindo o estado inicial
			int q0 = Integer.parseInt(line1[2]);
			automaton.getStates().get(q0).set0();

			//Definindo o Numero de estados, o alfabeto e o estado inicial
			automaton.setN(n);
			automaton.setS(s);
			automaton.setQ0(q0);

			//Lendo a segunda linha do arquivo de entrada
			String[] line2 = br.readLine().split(" ");
			int[] qFinals = new int[line2.length]; 
			
			//Obtendo os estados finais
			for (int i = 0; i < line2.length; i++) {
				qFinals[i] = Integer.parseInt(line2[i]);
			}
			
			//Atualizando os finais no automato
			for (int i = 0; i < qFinals.length; i++) {
				if(qFinals[i] == 1) automaton.getStates().get(i).setFinal();
			}

			//Lendo a matriz de transicoes do automato.
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

			//Adicionando as transações aos respectivos estados
			for (int i = 0; i < automatonData.length; i++) {
				for (int j = 0; j < automatonData[i].length; j++) {
					automaton.getStates().get(i).addTransition(j, automatonData[i][j]);
				}
			}

			automaton.setMatrizDeTransicao(); //Definindo a matriz De Transicao no proprio objeto Automato

		} catch (IOException ex) {
			System.out.println("Erro ao abrir arquivo");
		} finally {
			try {
				fr.close();
			} catch (IOException ex) {
				System.out.println("Erro ao fechar reader");
			}
		}
		return automaton; //Retorna o Automato "montado" a partir do arquivo de entrada
	}

	//Metodo que remove os estados inacessiveis, recebe como parametro e devolve um objeto Automato
	public static Automaton removerEstadosInacessiveis(Automaton a){
		Grafo g = new Grafo (a); //Cria um grafo a partir dos estados e transiçoes do Automato
		g.buscaProfundidade(g, a.getQ0()); //Realiza o algoritmo de busca em profundidade no grafo para identificar quais estados sao inacessiveis a partir do estado inicial

		//Percorre o vetor de estados visitados na busca em profundidade...
		for (int i = 0; i < g.verticesVisitados.length; i++) {
			//... so faz algo se o estado nao foi visitado e nao eh um estado inicial....
			if (g.verticesVisitados[i] == false && a.states.get(i).isQ0() == false){
				for (State s : a.states) {
					for (Transition t : s.getTransitions()) {
						if(t.getQ() == i) t.setQ(-1); //...coloca transicao pro "vazio" para todos as transicoes que tem destino ao estado que sera removido
					}
				}
			}
		}

		//Bloco que realiza a remocao dos estados inacessiveis
		int i = 0;
		int j = i;
		while (i<g.verticesVisitados.length){
			if (g.verticesVisitados[i] == false && a.states.get(j).isQ0() == false){
				a.states.remove(j); //Remove o estado inacessivel
				a.setN(a.getN() - 1); //Atualiza o numero de estados 
				if(j<a.getQ0()) a.setQ0(a.getQ0() - 1); //Atualiza o numero dos estados iniciais, se necessario
				for(State s : a.getStates()){
					if(j<s.getQ()) s.setQ(s.getQ()-1); //Atualiza o "numero" dos estados que sobraram (Exemplo: se o estado removido foi o q2, o q3 agora eh o novo q2)
					for(Transition t : s.getTransitions()){
						if (j< t.getQ()) t.setQ(t.getQ() -1); //Atualiza o "numero" das transicoes dos estados que sobraram (Mesmo exemplo anterior)
					}
				}
				j--;
			}
			j++;
			i++;
		}
		
		a.setMatrizDeTransicao(); //Atualiza a matriz de transicoes
		return a; //devolve o automato com os estados inacessiveis removidos
	}

	//Metodo que remove os estados Inuteis, recebe como parametro e devolve um objeto Automato
	public static Automaton removerEstadosInuteis(Automaton a){
		ArrayList <State> estados = a.getStates(); //Recupera os estados dos automatos
		int estadoFinal = -1; //Cria uma variavel que indica o estado final, inicialmente com um valor invalido, para uso futuro
		boolean [] estadosQueSeraoRemovidos = new boolean [a.getN()]; //Cria um vetor de tamanho igual ao numero de estados, indicando indicando quais serao removidos ou nao

		//Preenche o vetor de estados que serao removidos inicialmente com "false"
		for (int j = 0; j < estadosQueSeraoRemovidos.length; j++) {
			estadosQueSeraoRemovidos[j] = false;
		}

		//Busca e armazena o numero do estado final na variavel  
		for (State es : estados){
			if(es.isqFinal() == true) estadoFinal = es.getQ();
		}

		//Bloco que adiciona os estados inuteis no array de estados que serao removidos 
		for (State es : estados){ //percorre todos os estados...
			if(es.isQ0() == false && es.isqFinal() == false){ //...se o estado não é inicial e nem final, continua....
				Grafo g = new Grafo (a); //...Instacia um novo objeto Grafo a partir do Automato... 
				g.buscaProfundidade(g, es.getQ()); //... e faz uma busca em profundidade a partir de cada estado 
				for (int i = 0; i < g.verticesVisitados.length; i++) { //Percorre o vetor de estados visitados
					if(g.verticesVisitados[i] == false && i == estadoFinal) estadosQueSeraoRemovidos[es.getQ()] = true; //Se o estado final nao foi visitado, indica que estado atual sera removido
				}
			}
		}

		//Bloco que altera as transicoes para os estados inuteis
		for (int i = 0; i < estadosQueSeraoRemovidos.length; i++) { //Percorre o vetor de estados que serao removidos...
			if (estadosQueSeraoRemovidos[i] == true){//...se o estado eh para ser removido... 
				for (State s : a.states) { //...Percorre todos os estados do automato....
					for (Transition t : s.getTransitions()) {//...Recupera as transicoes desse estado..
						if(t.getQ() == i)t.setQ(-1); //Coloca transicao pro "vazio" para todos as transicoes que tenham com destino o estado inutil que sera removido
					}
				}
			}
		}

		//Bloco que realiza a remocao dos estados inuteis
		int i = 0;
		int j = i;
		while (i<estadosQueSeraoRemovidos.length){
			if (estadosQueSeraoRemovidos[i] == true){
				a.states.remove(j); //Remove o estado inutil
				a.setN(a.getN() - 1); //Atualiza o numero de estados
				if(j < a.getQ0()) a.setQ0(a.getQ0() - 1); //Atualiza o numero dos estados iniciais, se necessario
				for(State s : a.getStates()){
					if(j<s.getQ()) s.setQ(s.getQ()-1); //Atualiza o "numero" dos estados que sobraram (Exemplo: se o estado removido foi o q2, o q3 agora eh o novo q2)
					for(Transition t : s.getTransitions()){
						if (j< t.getQ()) t.setQ(t.getQ() -1); //Atualiza o "numero" das transicoes dos estados que sobraram (Mesmo exemplo anterior)
					}
				}
				j--;
			}
			j++;
			i++;
		}
		
		a.setMatrizDeTransicao(); //Atualiza a matriz de Transicoes
		return a; //devolve o automato com os estados inuteis removidos
	}

	//Metodo que remove os estados equivalentes. Recebe como parametro e devolve um objeto Automato
	public static Automaton removerEstadosEquivalentes(Automaton a){
		int[][] matrizDeSemelhantes = new int [a.getN()][a.getN()]; //cria uma matriz que indicam a "semelhanca" dos estados  	
		ArrayList <State> st = a.getStates(); //Recupera os estados do automato

		//Preenche matriz inicialmente com zeros, e -1 na diagonal principal
		for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes[0].length; j++) {
				if(i==j)matrizDeSemelhantes[i][j] = -1;
				else matrizDeSemelhantes[i][j] = 0;
			}
		}

		//1a etapa - Indicar a semelhanca (preenche na matriz o numero 1) de estados finais com finais e nao finais com nao finais  
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

		//2a etapa - Marcacao dos estados que nao tenham transiçoes sobre os mesmos simbolos
		for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				if (matrizDeSemelhantes [i][j] == 1){ //So faz algo quando ja houver uma semelhanca anterior
					ArrayList<Transition> ListaTransicoesEstado1 = st.get(i).transitions; //Recupera todas as transicoes do primeiro estado observado
					ArrayList<Transition> ListaTransicoesEstado2 = st.get(j).transitions; //Recupera todas as transicoes do segundo estado observado
					ArrayList<Integer> TransicoesEstado1 = new ArrayList<Integer>(); //Sera usado para recuperar as transições "individualmente" do primeiro estado observado
					ArrayList<Integer> TransicoesEstado2 = new ArrayList<Integer>(); //Sera usado para recuperar as transições "individualmente" do segundo estado observado
					
					//Bloco que adiciona nos ArrayLists apenas as transiçoes de simbolos válidos (não adiciona as transicoes do tipo -1)
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

					//Se os simbolos de transicoes não forem totalmente iguais, modifica o item da matriz de semelhanca para 0
					if (!(TransicoesEstado1.size() == TransicoesEstado2.size() && (TransicoesEstado1.containsAll(TransicoesEstado2))&&(TransicoesEstado2.containsAll(TransicoesEstado1)))) matrizDeSemelhantes[i][j] = 0;
				}
			}
		}

		//3a Etapa - Marcacao dos estados que não tenham transicoes do mesmo simbolo para os mesmos estados	
		int [][] matrizDeTransicoes = a.getMatrizDeTransicao(); //Recupera a Matriz de Transicoes do automato
		for (int i = 0; i < matrizDeSemelhantes.length; i++) {
			for (int j = 0; j < matrizDeSemelhantes.length; j++) {
				if (matrizDeSemelhantes[i][j] == 1) { //So faz algo quando ja houver uma semelhanca anterior
					for (int k = 0; k < matrizDeTransicoes[0].length; k++) { //Percorre as colunas da matriz de transicao
			
						//Confere se os mesmos simbolos des transicoes levam aos mesmos estados. Caso seja false, modifica o item da matriz de Semelhanca para 0			
						if (!(j != i && (matrizDeTransicoes[i][k] == matrizDeTransicoes[j][k]) || (matrizDeSemelhantes[matrizDeTransicoes[i][k]][matrizDeTransicoes[j][k]] == 1))) {
							matrizDeSemelhantes[i][j] = 0;
							matrizDeSemelhantes[j][i] = 0;
						}
					}
				}
			}
		}

		//4a etapa - Agrupa os estados semelhantes (Usa o algoritmo dado no slide do enunciado)
		//Apos o agrupamento, o numero de estados do novo automato sera o valor da variavel "cont"
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

		//Finaliza as modificações do Automato (constroi o Automato Minimo) / Baseado no slide do enunciado
		Automaton newAutomaton = new Automaton(); //Instancia um novo objeto do tipo Automato
		
		//Adiciona "cont" estados
		for (int i = 0; i < cont; i++) {
			newAutomaton.add(new State(i));
		}
		
		newAutomaton.getStates().get(rep[a.getQ0()]).set0(); //Configura o novo estado inicial, que será o item que está na posicao "Estado inicial anterior" no vetor rep

		//Define os novos estados finais, que são os itens que estão na posicao "Estados finais do automato anterior" do vertor rep
		for (State s : a.getStates()) {
			if(s.isqFinal()){
				newAutomaton.getStates().get(rep[a.getStates().indexOf(s)]).setFinal();
			}
		}

		//Define o numero de estados, o numero do alfabeto (mesmo do automato anterior) e o estado inicial
		newAutomaton.setN(cont);
		newAutomaton.setS(a.getS());
		newAutomaton.setQ0(rep[a.getQ0()]);

		//Bloco que configura a nova matriz de transicao
		int[][] matrizDeTransicaoOriginal = a.getMatrizDeTransicao();
		int[][] novaMatrizDeTransicao = new int [newAutomaton.getN()][newAutomaton.getS()];
		//Popula a nova matriz de transicoes inicialmente com -1
		for (int i = 0; i < novaMatrizDeTransicao.length; i++) {
			for (int j = 0; j < novaMatrizDeTransicao[0].length; j++) {
				novaMatrizDeTransicao[i][j] = -1;
			}
		}
		//Popula a nova matriz de transicoes, baseando-se nas equivalencias de estados ja agrupadas
		for (int i = 0; i < matrizDeTransicaoOriginal.length; i++) {
			for (int j = 0; j < matrizDeTransicaoOriginal[0].length; j++) {
				if (novaMatrizDeTransicao[rep[i]][j] == -1) {
					novaMatrizDeTransicao[rep[i]][j] = matrizDeTransicaoOriginal[i][j] != -1 ? rep[matrizDeTransicaoOriginal[i][j]] : -1;
				}
			}
		}

		//Adicionando as transações aos estados do novo automato, baseando-se na matriz de Transicao
		for (int i = 0; i < novaMatrizDeTransicao.length; i++) {
			for (int j = 0; j < novaMatrizDeTransicao[i].length; j++) {
				newAutomaton.getStates().get(i).addTransition(j, novaMatrizDeTransicao[i][j]);
			}
		}
		
		return newAutomaton; //devolve o novo automato minimizado
	}
}

//Classe que define um Automato
class Automaton{
	private int n; //Numero de estados 
	private int s; //Tamanho do Alfabeto
	private int q0; //Estado inicial
	ArrayList<State> states = new ArrayList<>(); //Estados do automato
	int [][] matrizDeTransicoes; //Matriz de Transicoes

	//Metodos Get e Set
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

	//Metodo que imprime o automato na tela. Util para testes
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

	//Metodo que escreve o automato em um arquivo de saida
	public void writeOutput(File output) throws IOException{
		FileWriter fw = null;
		fw = new FileWriter(output);
		BufferedWriter bw = new BufferedWriter(fw);; 

		try { 
			bw.write(n +  " " +  s + " " + q0 + "\n");
			for (State s : states) {
				if (s.isqFinal()) bw.write(1 + " ");
				else bw.write(0 + " ");
			}
			bw.newLine();
			for (State s : states) {
				for (Transition t : s.getTransitions()) {
					bw.write(t.getQ() + " ");
				}
				bw.newLine();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			bw.close();
		}
	}
}

//Classe que define um Estado
class State {
	int q; //Numero do estado
	boolean q0; //Indica se esse estado eh inicial
	boolean qFinal; //Indica se esse estado eh final
	ArrayList<Transition> transitions; //Todas as transacoes possiveis do estado

	//Construtor
	public State(int q) {
		this.q = q;
		this.q0 = false;
		this.qFinal = false;
		transitions = new ArrayList<>();
	}

	//Metodos Get e Set
	public void setFinal(){
		this.qFinal = true;
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
	
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	//Metodos que devolvem true ou false caso o estado seja inicial ou final
	public boolean isQ0() {
		return q0;
	}

	public boolean isqFinal() {
		return qFinal;
	}
	
	//Metodo que adiciona uma transicao de simbolo "a" para o estado "q" 
	public void addTransition(int a, int q){
		Transition t = new Transition(a, q);
		transitions.add(t);
	}
} 

//Classe que define as transições entre estados
class Transition {
	int a; //Simbolo da transicao
	int q; //Estado "destino" da Transicao

	//Construtor
	public Transition(int a, int q){
		this.q = q;
		this.a = a;
	}

	//Metodos Get e Set
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

//Classe que define um automato como um grafo
class Grafo{
	int[][] matrizDeAdjacencia; //Grafo em forma de Matriz de Adjacencia
	int numeroVertices; //Qtde de vertices do grafo
	boolean [] verticesVisitados; //Vetor de vertices visitados (usado na busca em profundidade)
	ArrayList <Integer> percurso; //Percurso feito quando a busca é realizada

	//Construtor, que recebe um automato e "tranforma" ele em um grafo
	public Grafo(Automaton a){
		percurso = new ArrayList<Integer>();
		matrizDeAdjacencia = new int[a.getN()][a.getN()];

		//Inicializa todas as posicoes do grafo com -1
		for (int i = 0; i < matrizDeAdjacencia.length; i++) {
			for (int j = 0; j < matrizDeAdjacencia[i].length; j++) {
				matrizDeAdjacencia[i][j] = -1;
			}
		}

		//Bloco que define as transicoes de estado no grafo
		for (int i = 0; i < a.matrizDeTransicoes.length; i++) {
			for (int j = 0; j < a.matrizDeTransicoes[0].length; j++) {
				if(a.matrizDeTransicoes[i][j] != -1){
					matrizDeAdjacencia[i][a.matrizDeTransicoes[i][j]] = j;
				}
			}
		}

		//Configura o numero de vertices a partir do Automato recenido no construtor
		numeroVertices = a.getN();
		verticesVisitados = new boolean [numeroVertices];

		//Define inicialmente que todas as posicoes do vetor de vertices visitados como false
		for (int i = 0; i < matrizDeAdjacencia.length; i++) {
			verticesVisitados[i] = false;
		}
	}

	//Realiza a busca em profundidade no grafo (util para remover estados inuteis e inacessiveis)
	public void buscaProfundidade(Grafo grafo, int inicio) {    
		percurso.add(inicio);    
		verticesVisitados[inicio] = true; //Da uma "flag" de que o estado foi visitado
		for (int i = 0; i < grafo.numeroVertices; i++) {    
			if (grafo.matrizDeAdjacencia[inicio][i] != -1 && verticesVisitados[i] == false) {    
				buscaProfundidade(grafo, i); //Se o proximo estado nao foi ainda visitado, va para ele (metodo recursivo) 
				percurso.add(inicio); 
			}    
		}    
	}   
}
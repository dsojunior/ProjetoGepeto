import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class Afn {
	static int nroEstados;
	static int nroSimbolosAlfabeto;
	static int estadoInicial;
	static int [] estadosFinais;
	static int[][] automato;
	static int [][] grafo;
	
	public static void main(String [] args){
		
		File INPUT = new File (args[0]);
		File OUTPUT = new File (args[1]);
		readInput(INPUT);
		converteAutomatoParaGrafo();
		imprimeGrafo();
	}
	
	public static void readInput(File input){
		try{
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			
			
			//Lendo a primeira linha do arquivo de entrada
			String[] cabecalho = br.readLine().split(" ");
			int[] cabecalhoConv = new int [3];
			int j = 0;
			for (int i = 0; i < cabecalho.length; i++) {
				if(!cabecalho[i].equals(" ") && !cabecalho[i].equals("")){
					cabecalhoConv[j] = Integer.parseInt(cabecalho[i]);
					j++;
				}
			}
			nroEstados = cabecalhoConv[0];
			nroSimbolosAlfabeto = cabecalhoConv[1];
			estadoInicial = cabecalhoConv[2];
			
			//Lendo a segunda linha do arquivo de entrada
			String[] estFinais = br.readLine().split(" ");
			estadosFinais = new int [nroEstados];
			int index = 0;
			for (int i = 0; i < estFinais.length; i++) {
				if(!estFinais[i].equals(" ") && !estFinais[i].equals("")){
					estadosFinais[index] = Integer.parseInt(estFinais[i]);
					index++;
				}
			}
			
			
			//Lendo o automato
			automato = new int[nroEstados][nroSimbolosAlfabeto];
			for (int i = 0; i < automato.length; i++) {
				String[] linha = br.readLine().trim().split(" ");
				int k = 0;
				for (int l = 0; l < linha.length; l++) {
					if (!linha[l].equals(" ") && !linha[l].equals("")){
						automato[i][k] = Integer.parseInt(linha[l]);
						k++;
					}
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
		}
		
	}
	
	//Util para remover estados inacessiveis e inuteis
	public static void converteAutomatoParaGrafo(){
		grafo = new int[nroEstados][nroEstados];
		
		//Inicializa todas as posicoes do grafo com -1
		for (int i = 0; i < grafo.length; i++) {
			for (int j = 0; j < grafo[i].length; j++) {
				grafo[i][j] = -1;
			}
		}
		
		for (int i = 0; i < automato.length; i++) {
			for (int j = 0; j < automato[0].length; j++) {
				if(automato[i][j] != -1){
					grafo[i][automato[i][j]] = j;
				}
			}
		}
		
	}
	
	public static void removerEstadosInacessiveis(){
		ArrayList <Integer> inacessiveis = new ArrayList <Integer>();
		
		for (int j = 0; j < grafo[0].length; j++) {
			boolean ehInacessivel = true; 
			for (int i = 0; i < grafo.length; i++) {
				if (grafo[i][j] != -1) ehInacessivel = false; 
			}
			if(ehInacessivel == true) inacessiveis.add(j);
		}
		
		nroEstados = nroEstados - inacessiveis.size();
		
		for (int i = 0; i < inacessiveis.size(); i++) {
			int [] estadosFinaisAtualizado = new int [estadosFinais.length-1];
			int aux = 0;
			for (int j = 0; j < estadosFinais.length; j++) {
				if(j != inacessiveis.get(i)){
					estadosFinaisAtualizado[aux] = estadosFinais[i];
					aux++;
				}
			}
			estadosFinais = estadosFinaisAtualizado;
			
			int [][] automatoAutualizado = new int [automato.length-1][automato[0].length];
			aux=0;
			for (int j = 0; j < automato.length; j++) {
				if (j != inacessiveis.get(i)){
					for (int j2 = 0; j2 < automato[0].length; j2++) {
						automatoAutualizado[aux][j2] = automato[j][j2]; 
					}
					aux++;
				}
			}
			automato = automatoAutualizado;
		}
		//gera um novo grafo
		converteAutomatoParaGrafo();
	}
	
	//Algoritmo de caminho em Grafo. Se há um caminho possivel de A ate B, retorna true, caso contrario, false.
	public static boolean existeCaminho(int a, int b){
		
		//Implementar
		
		
		return false;
	}
	
	public static void imprimeGrafo(){
		for (int i = 0; i < grafo.length; i++) {
			for (int j = 0; j < grafo[i].length; j++) {
				if(j != grafo[i].length-1){
					System.out.print(grafo[i][j] + " ");
				}
				else System.out.println(grafo[i][j]);
			}
		}
	}
	
	public static void imprimeOutput(){
		System.out.println(nroEstados + " " + nroSimbolosAlfabeto + " " + estadoInicial);
		
		for (int i = 0; i < estadosFinais.length; i++) {
			if(i != estadosFinais.length-1){
				System.out.print(estadosFinais[i] + " ");
			}
			else System.out.println(estadosFinais[i]);
		}
		
		for (int i = 0; i < automato.length; i++) {
			for (int j = 0; j < automato[i].length; j++) {
				if(j != automato[i].length-1){
					System.out.print(automato[i][j] + " ");
				}
				else System.out.println(automato[i][j]);
			}
		}
		
	}
	
}

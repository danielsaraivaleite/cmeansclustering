/*
 * AgrupamentoCMeans.java
 *
 * Created on 20 de Outubro de 2007, 23:42
 */

package cmeans;

/**
 * Esta classe implementa o algoritmo C-Means
 * @author Daniel Saraiva Leite, Yin I Hsien
 */
public class AgrupamentoCMeans 
{
    public static final double epsilonPadrao = 0.001; // criterio de parada
    protected BaseDados base;
    protected int nClusters;
    protected double[][] matrizParticao;    // dim. n exemplos x c clusters
    protected double[][] matrizCentroides;  // dim. c clusters x m atributos
    protected double epsilon;    
    public static final double mPadrao = 2;
    protected double m; // influência do grau de pertinência no algoritimo
    
    
    /** Construtor
     * @param base a Base de Dados
     * @param nClusters o numero de clusters
     */
    public AgrupamentoCMeans(BaseDados base, int nClusters) 
    {
        this.base = base;
        this.nClusters = nClusters;
        this.epsilon = epsilonPadrao;
        this.m = mPadrao;
    }
    
    /**
     * Construtor que especifica qual o epsilon e m a serem utilizados
     * @param base a Base de Dados
     * @param nClusters o numero de clusters
     * @param epsilon o criterio de parada (o erro)
     * @param m a influencia do grau de pertinencia. Se a influencia tiver um valor 
     * menor do que 1, o algoritimo assumira valor 2.
     */
    public AgrupamentoCMeans(BaseDados base, int nClusters, double epsilon, double m )
    {
    	this.base = base;
    	this.nClusters = nClusters;
    	this.epsilon = epsilon;
    	if (m < 1)
    	{
    		this.m = mPadrao;
    	}
    	else
    	{
    		this.m = m;
    	}
    }
    
    
    /**
     * Aplica o algoritmo de clustering C-Means
     */
    public void aplicarAlgoritmo() 
    {
        // define a particao inicial aleatoria
        matrizParticao = inicializaMatrizParticoes();
        boolean bConvergiu = false;
        int iIteracao = 0;
        do
        {
            iIteracao++;
            // calcula os novos centroides
            matrizCentroides = calcularCentroides(matrizParticao);
            // calcula a nova matriz de particoes
            double[][] matrizParticoesNova = calculaNovaMatrizParticoes();
            // verifica se convergiu
            bConvergiu = convergiu(matrizParticao, matrizParticoesNova, iIteracao);
            // atualiza matriz de particoes
            matrizParticao = matrizParticoesNova;
        }
        while(!bConvergiu);
    }
    
    
    /**
     * Aplica o algoritmo de clustering C-Means
     * @param epsilon O numero que define critério de parada
     */
    public void aplicarAlgoritmo(double epsilon) 
    {
        this.epsilon = epsilon;
        this.aplicarAlgoritmo();
    }
    
    
    /**
     * Retorna a matriz de centroides resultante
     */
    public double[][] recuperarMatrizCentroides() 
    {
        return matrizCentroides;
    }
    
    
    /**
     * Retorna a matriz de particoes resultante
     */
    public double[][] recuperarMatrizParticoes() 
    {
        return matrizParticao;
    }
    
    
    /**
     * Retorna uma string que mostra os centros de clusters obtidos
     */
    public String recuperarResultado() 
    {
        if(matrizCentroides == null) {
            return "Algoritmo nao aplicado ainda";
        } else {
            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i < matrizCentroides.length; i++) {
                for(int j = 0; j < matrizCentroides[i].length; j++) {
                    buffer.append(matrizCentroides[i][j] + "\t");
                }
                buffer.append("\n");
            }
            return buffer.toString();
        }
    }
    
    
    /**
     * Retorna uma string que mostra o resultado aplicação do algoritmo
     */
    public String toString()
    {
        return recuperarResultado();   
    }
    
    
    /**
     * Calcula a norm quadratrica entre dois vetores 
     * Formula:
     * || vetor1 - vetor2 || ^ 2
     */
    protected double normaQuadratica(double[] vetor1, double[] vetor2) 
    throws ArithmeticException
    {
        if(vetor1.length != vetor2.length)
        {
            throw new ArithmeticException("Dimensoes incompativeis no cálculo "
                    + "da norma quadratica entre dois vetores");
        }
        double dSoma = 0;
        for(int i = 0; i < vetor1.length; i++)
        {
            dSoma += Math.pow(vetor1[i] - vetor2[i], 2);
        }
        return dSoma;
    }
    
    
    /**
     * Calcula o centroide de um cluster denotado por indiceCluster
     * @param matrizParticoes a Matriz de particoes usada na ponderacao
     * @param indiceCluster o indice do cluster, de 0 ... nClusters - 1
     */
    protected double[] centroide(double [][] matrizParticoes, int indiceCluster)
    {
        double dSomaDenominador = 0;
        double[] vetorCentroide = new double[base.recuperarNumeroAtributos()];
        // Inicializa o vetor
        for(int j = 0; j < vetorCentroide.length; j++)
        {
            vetorCentroide[j] = 0;
        }
        // determina o numerador
        for(int i = 0; i < base.recuperarNumeroInstancias(); i++)
        {
            double dPertinencia = Math.pow(matrizParticoes[i][indiceCluster], m);
            dSomaDenominador += dPertinencia;
            for(int j = 0; j < vetorCentroide.length; j++)
            {
                vetorCentroide[j] += base.recuperarInstancia(i)[j] * Math.pow(dPertinencia, m);
            }
        }        
        // divide pelo denominador
        for(int j = 0; j < vetorCentroide.length; j++)
        {
            vetorCentroide[j] /= dSomaDenominador;
        }       
        return vetorCentroide;
    }
    
    
    /**
     * Calcula o centroide de todos os clusters
     * @param matrizParticoes a Matriz de particoes usada na ponderacao
     */
    protected double[][] calcularCentroides(double [][] matrizParticoes)
    {
        double[][] centroides = new double[nClusters][];
        for(int i = 0; i < nClusters; i++)
        {
            centroides[i] = centroide(matrizParticoes, i);
        }
        return centroides;
    }
    
    
    /**
     * Indica se houve convergencia do algoritmo
     * @param matrizParticoesAnterior particoes(t)
     * @param matrizParticoesAtual particoes(t+1)
     * @returns True se convergiu. False caso contrario.
     */
    protected boolean convergiu(double [][] matrizParticoesAnterior, 
            double [][] matrizParticoesAtual, int nIteracao)
    {
        double dMaximaDiferenca = 0;
        // busca a maxima diferenca entre pertinencias nas duas matrizes
        for(int i = 0; i < matrizParticoesAnterior.length; i++)
        {
            for(int j = 0; j < matrizParticoesAnterior[i].length; j++)
            {
                dMaximaDiferenca += Math.abs(matrizParticoesAnterior[i][j] 
                        - matrizParticoesAtual[i][j] );
            }
        }        
        return (dMaximaDiferenca <= epsilon) ? true : false;
    }
    
    
    /**
     * Inicializa aleatoriamente a matriz de particoes, observando
     * as regras de soma linha e soma coluna p. 28 slide
     */
    public double[][] inicializaMatrizParticoes()
    {
    	// Um vetor indicando se a coluna ja foi instanciado ou nao
     	boolean [] bNaoZero = new boolean[nClusters];    	
    	for (int j = 0; j < nClusters; j++)
    	{
    		bNaoZero[j] = false;
    	}
    	
        double dGrauAleatorio = 0;
        double dDiferenca = 0;
        double[][] dMatrizAleatoria = new double [base.numeroInstancias][nClusters];
        double dSomaLinha = 0;
        int dIndiceAleatoria = 0;
        
        for (int k = 0; k < base.numeroInstancias; k++)
        {
        	dSomaLinha = 0;
        	// inicia aleatoriamente os valores para as colunas
        	for (int i = 0; i < nClusters; i++)
        	{
        		// gera um valor aleatorio para a coluna
        		dMatrizAleatoria[k][i] = ((double) ((int) ((Math.random() % 1) * 10000))) / 10000;
        		
        		// se o valor gerado nao eh 0, entao indica que a coluna ja possui
        		// valores maiores do que 0
        		if (dMatrizAleatoria[k][i] != 0)
        		{
        			bNaoZero[i] = true;
        		}
        		
        		// calcula a soma da linha
        		dSomaLinha += dMatrizAleatoria[k][i];
        	}
        	
        	while (dSomaLinha > 1)
        	{
        		// calcula quando falta para que a soma fique zero
        		dDiferenca = dSomaLinha - 1;
        		
        		// escolhe uma coluna qualquer
        		dIndiceAleatoria = (int) ((Math.random() * 10) % nClusters);
        		
        		// retira um valor aleatorio entre 0 e valor que a coluna tem de modo
        		// a nunca zerar a coluna
        		dGrauAleatorio = ((double) ((int) ((Math.random() % 
        				dMatrizAleatoria[k][dIndiceAleatoria]) * 10000))) / 10000;
        		
        		// se o valor a ser retirado eh maior do que a diferenca entre a soma
        		// atual e 1, entao retira a diferenca ao inves do valor aleatorio
        		if (dDiferenca < dGrauAleatorio)
        		{
        			dMatrizAleatoria[k][dIndiceAleatoria] -= dDiferenca;
        			dSomaLinha -= dDiferenca;
        		}
        		else
        		{
        			dMatrizAleatoria[k][dIndiceAleatoria] -= dGrauAleatorio;
        			dSomaLinha -= dGrauAleatorio;
        		}
        	}
        	
        	while (dSomaLinha < 1)
        	{
        		// calcula quando falta para que a soma fique zero
        		dDiferenca = 1 - dSomaLinha ;
        		
        		// escolhe uma coluna qualquer
        		dIndiceAleatoria = (int) (Math.random() % nClusters);
        		
        		dGrauAleatorio =  1 - dMatrizAleatoria[k][dIndiceAleatoria];
        		
        		if (dGrauAleatorio >= dDiferenca) {
        			dMatrizAleatoria[k][dIndiceAleatoria] += dDiferenca;
        			dSomaLinha += dDiferenca;
        		}
        		else        			
        		{
        			dMatrizAleatoria[k][dIndiceAleatoria] += dGrauAleatorio;
        			dSomaLinha += dGrauAleatorio;
        		}
        	}
        }
        
        return dMatrizAleatoria;
    }
    
    
    /**
     * Calcula a nova matriz de particoes
     */
    public double[][] calculaNovaMatrizParticoes()
    {
    	boolean [] I = null; // os dados que se coincidem com alguns dos centroides
    	double[][] dMatrizCalculada = new double[base.numeroInstancias][nClusters];
    	int iQtInstanciasI = 0;
    	double dResultSomatorio = 0;
    	double dNumerador = 0;
    	double dResultParcial = 0;
    	
    	for (int k = 0; k < base.numeroInstancias; k++) {
    		I = null;
    		iQtInstanciasI = 0;
    		for (int i = 0; i < nClusters; i++)
    		{
    			// calcula numerador
    			dNumerador = normaQuadratica(base.recuperarInstancia(k), matrizCentroides[i]);
    			
    			// se o dado sobrepoe com o centroide
    			if (dNumerador == 0)
    			{
    				if (I == null) // primeira vez que ocorre a sobreposicao do dado com algum centroide
    				{
    					I = new boolean[nClusters];
    					
    					for (int h = 0; h < nClusters; h++)
    					{    						
    						I[h] = false;
    					}
    				}
    				
    				// atualiza quais foram os dados que sobreposeram com os centroides
    				I[i] = true;
    				iQtInstanciasI++;
    			}
    			
    			// caso ainda nao tivesse ocorrido nenhuma sobreposicao do dado com centroide
    			if (I == null)
    			{
    				// calcula somatorio
    				for (int j = 0; j < nClusters; j++)
    				{
    					// divide pelo denominador
    					dResultParcial = dNumerador / 
    						normaQuadratica(base.recuperarInstancia(k), matrizCentroides[j]);
    					dResultSomatorio += Math.pow(dResultParcial, (1 / (m - 1))); 
    				}
    			}
    			
    			dMatrizCalculada[k][i] = Math.pow(dResultSomatorio, -1);
    		}
    		
    		// se ocorreu sobreposicao do dado com algum centroide
    		if (I != null)
    		{
    			for (int n = 0; n < nClusters; n++)
    			{
    				if (!I[n])
    				{
    					// zera o grau de pertinencia do dado nos outros clusters
    					dMatrizCalculada[k][n] = 0;
    				}
    				else
    				{
    					// atribui o grau de pertinencia de forma igualitaria entre os
    					// clusteres que o dado sobreposse
    					dMatrizCalculada[k][n] = 1 / iQtInstanciasI;
    				} 
    			}
    		}
        }
    	
    	System.out.println("Gerou uma nova matriz.");
    	
        return dMatrizCalculada;
    }
}

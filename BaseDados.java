/*
 * BaseDados.java
 *
 * Created on 20 de Outubro de 2007, 21:39
 * Sistemas Nebulosos - 2007
 */

package cmeans;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Classe para manipular uma base de dados cujos atributos sao numericos
 * Nao suporta atributos nominais
 * @author Daniel Saraiva Leite
 * @author I Hsien Yin
 */
public class BaseDados 
{
    protected double[][] matDados;
    protected int numeroAtributos;
    protected int numeroInstancias;
    
    /** Construtor 
     *  @param leitorBase um StreamReader já construido com o arquivo texto da base
     */
    public BaseDados(LineNumberReader leitorBase) throws Exception
    {
        // constroi a matriz de dados
        constuirMatrizDados(leitorBase);
        // normaliza os dados
        normalizaBase();
    }
    
    /**
     * Recupera a matriz de dados m x n, com os valores assumidos pelos
     * n atributos para as m instancias
     */
    public double[][] recuperarMatrizDados()
    {
        return matDados;
    }
    
    /**
     * Retorna um vetor que representa uma instancia dessa Base de Dados, com
     * com dimensao de n atributos
     * @param indiceInstancia o indice da instancia na matriz de dados
     */
    public double[] recuperarInstancia(int indiceInstancia)
    {
        return matDados[indiceInstancia];
    }
    
    /**
     * Recupera o numero de instancias desta base
     */
    public int recuperarNumeroInstancias()
    {
        return numeroInstancias;
    }
    
    /**
     * Recupera o numero de atributos desta bases
     */
    public int recuperarNumeroAtributos()
    {
        return numeroAtributos;
    }
    
    /**
     * Normalia a base de dados, divindo o valor assumido em cada
     * atributo pelo max. desse atributo
     */
    protected void normalizaBase()
    {
        for(int j = 0; j < numeroAtributos; j++ )
        {
            double maximo = buscaValorMaximo(j);
            for(int i = 0; i < numeroInstancias; i++)
            {
                matDados[i][j] /= maximo;
            }
        }
    }   
    
    /**
     * Busca o valor máximo assumido por um atributo na base de dados
     * @param indiceAtributo o indice do atributo
     */
    protected double buscaValorMaximo(int indiceAtributo)
    {
        double dValorMaximo = Double.NEGATIVE_INFINITY;
        if(numeroInstancias > 0)
        {
            dValorMaximo = matDados[0][indiceAtributo];
        }
        for(int i = 1; i < numeroInstancias; i++)
        {
            if(matDados[i][indiceAtributo] > dValorMaximo)
            {
                dValorMaximo = matDados[i][indiceAtributo];
            }  
        }
        return dValorMaximo;
    }    
    
    /**
     * Constroi a matriz de dados que contem as instancias e os valores
     * para os atributos, fazendo o devido parsing do arquivo texto da
     * base e indicando eventuais erros
     * @param leitorBase o leitor do stream do arquivo da base
     */
    protected void constuirMatrizDados(LineNumberReader leitorBase) throws Exception
    {
        // Le as linhas do arquivo
        Vector<String> vetorLinhas = new Vector<String>();
        Vector<Integer> vetorNumeroLinhas = new Vector<Integer>();
        String linha;
        while( (linha = leitorBase.readLine()) != null)
        {
            // pula linhas de comentario
            if(!linha.trim().startsWith("#") && !linha.trim().startsWith("//")) 
            {
                vetorLinhas.add(linha);
                vetorNumeroLinhas.add(leitorBase.getLineNumber());
            }
        } 
        // constroi a primeira dimensao da matriz
        matDados = new double[vetorLinhas.size()][];
        numeroInstancias = vetorLinhas.size();
        int iIndiceLinha = 0;
        int iIndiceAtributo = 0;
        try
        {
             Vector<Double> vetorValores = new Vector<Double>();
             // realiza o carregamento da primeira linha
             if(numeroInstancias == 0)
             {
                 throw new Exception("Base de dados nao contem instancias.");
             }
             StringTokenizer tokenizer = new StringTokenizer(vetorLinhas.get(iIndiceLinha));
             while(tokenizer.hasMoreTokens())
             {
                 iIndiceAtributo++;
                 vetorValores.add(Double.parseDouble(tokenizer.nextToken()));
             }
             // agora sabemos o numero de colunas da matriz
             numeroAtributos = vetorValores.size();
             // constroi-se a matriz
             for(int i = 0; i < numeroInstancias; i++)
             {
                 matDados[i] = new double[numeroAtributos];
             }
             // adiciona os valores da primeira linha
             for(int j = 0; j < numeroAtributos; j++)
             {
                 matDados[0][j] = vetorValores.get(j);
             }
             // adiciona os valores das proximas linhas
             for(int i = 1; i < numeroInstancias; i++)
             {
                 iIndiceLinha = i;
                 // tokeniza a linha
                 vetorValores.clear();
                 tokenizer = new StringTokenizer(vetorLinhas.get(iIndiceLinha));
                 iIndiceAtributo = 0;
                 while(tokenizer.hasMoreTokens())
                 {
                     iIndiceAtributo++;
                     vetorValores.add(Double.parseDouble(tokenizer.nextToken()));
                 }
                 if(numeroAtributos != vetorValores.size())
                 {
                     throw new Exception("Numero invalido de atributos na linha " + 
                            vetorNumeroLinhas.get(iIndiceLinha) + ". Deve ser " + numeroAtributos);
                 }
                 // adiciona os valores na matriz
                 for(int j = 0; j < numeroAtributos; j++)
                 {
                     matDados[iIndiceLinha][j] = vetorValores.get(j);
                 }
             }
        }
        catch(NumberFormatException exc)
        {
            throw new Exception("Valor inválido para atributo numerico " +
                    + iIndiceAtributo + " na linha " + 
                    vetorNumeroLinhas.get(iIndiceLinha) + ":\n" + 
                    vetorLinhas.get(iIndiceLinha));
        }          
    }
    
    /**
     * Retorna uma string que representa esta base de dados
     */
    public String toString()
    {
        if(matDados == null)
        {
            return "Base de Dados nula";
        }
        else
        {
            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i < matDados.length; i++)
            {
                for(int j = 0; j < matDados[i].length; j++)
                {
                    buffer.append(matDados[i][j] + "\t");
                }
                buffer.append("\n");
            }
            return buffer.toString();
        }                   
    }
}

package com.avisai.avisai;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import static org.bytedeco.opencv.global.opencv_highgui.cvWaitKey;
import static org.bytedeco.opencv.global.opencv_highgui.imshow;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@SpringBootApplication
public class AvisaiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AvisaiApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo à aplicação Spring Boot!");

        int proximaImagem;
        int classePersonagem;
        String classePersonagemString;
        double red, green, blue;

        // Cabeçalho do arquivo Weka
        StringBuilder exportacao = new StringBuilder();

        exportacao.append("@relation caracteristicas\n\n");
        exportacao.append("@attribute laranja_camisa_bart real\n");
        exportacao.append("@attribute azul_calcao_bart real\n");
        exportacao.append("@attribute azul_sapato_bart real\n");
        exportacao.append("@attribute marrom_boca_homer real\n");
        exportacao.append("@attribute azul_calca_homer real\n");
        exportacao.append("@attribute cinza_sapato_homer real\n");
        exportacao.append("@attribute classe {Bart, Homer}\n\n");
        exportacao.append("@data\n");

        // Diretório onde estão armazenadas as imagens
        File diretorio = new File("src//main//resources//images");
        File[] arquivos = diretorio.listFiles();

        // Características do Homer e Bart
        float laranjaCamisaBart;
        float azulCalcaoBart;
        float azulSapatoBart;
        float azulCalcaHomer;
        float marromBocaHomer;
        float cinzaSapatoHomer;

        // Definição do vetor de características
        float[][] caracteristicas = new float[293][7];

        // Percorre todas as imagens do diretório
        for (int i = 0; i < Objects.requireNonNull(arquivos).length; i++) {
            laranjaCamisaBart = 0;
            azulCalcaoBart = 0;
            azulSapatoBart = 0;
            azulCalcaHomer = 0;
            marromBocaHomer = 0;
            cinzaSapatoHomer = 0;

            // Carrega cada imagem do diretório
            Mat imagemOriginal = imread("src//main//resources//images//" + arquivos[i].getName());

            // Imagem processada - cria uma imagem no formato Mat (recomendado para as versões mais recentes)
            Mat imagemProcessada = new Mat(imagemOriginal.clone());

            // Definição da classe - Homer ou Bart
            // Aprendizagem supervisionada
            if (arquivos[i].getName().charAt(0) == 'b') {
                classePersonagem = 0;
                classePersonagemString = "Bart";
            } else {
                classePersonagem = 1;
                classePersonagemString = "Homer";
            }

            // Cria um indexer para poder acessar os valores dos pixels da imagem de forma mais rápida
            UByteIndexer imgIndexer = imagemOriginal.createIndexer();
            UByteIndexer imgProcessada = imagemProcessada.createIndexer();

            // Varre a imagem pixel a pixel
            for (int altura = 0; altura < imgIndexer.rows(); altura++) {
                for (int largura = 0; largura < imgIndexer.cols(); largura++) {
                    //inicializa uma array para os valores do pixel (3 valores pois são 3 canais de cor: R G B)
                    int[] pixel = new int[3];
                    // Extração do RGB de cada pixel da imagem
                    imgIndexer.get(altura, largura, pixel);
                    blue = pixel[0]; //azul
                    green = pixel[1]; //verde
                    red = pixel[2]; //vermelho

                    // Camisa laranja do Bart
                    if (blue >= 11 && blue <= 22 &&
                            green >= 85 && green <= 105 &&
                            red >= 240 && red <= 255) {
                        // Pinta a imagem processada com o verde limão
                        imgProcessada.put(altura, largura, 0, 255, 128);

                        // Incrementa a quantidade de pixels laranja
                        laranjaCamisaBart++;
                    }

                    // Calção azul do Bart (metade de baixo da imagem)
                    if (altura > (imgIndexer.rows() / 2)) {
                        if (blue >= 125 && blue <= 170 &&
                                green >= 0 && green <= 12 &&
                                red >= 0 && red <= 20) {
                            imgProcessada.put(altura, largura, 0, 255, 128);

                            azulCalcaoBart++;
                        }
                    }

                    // Sapato do Bart (parte inferior da imagem)
                    if (altura > (imgIndexer.rows() / 2) + (imgIndexer.rows() / 3)) {
                        if (blue >= 125 && blue <= 140 &&
                                green >= 3 && green <= 12 &&
                                red >= 0 && red <= 20) {
                            imgProcessada.put(altura, largura, 0, 255, 128);

                            azulSapatoBart++;
                        }
                    }

                    // Calça azul do Homer
                    if (blue >= 150 && blue <= 180 &&
                            green >= 98 && green <= 120 &&
                            red >= 0 && red <= 90) {
                        imgProcessada.put(altura, largura, 0, 255, 255);

                        azulCalcaHomer++;
                    }

                    // Boca do Homer (pouco mais da metade da imagem)
                    if (altura < (imgIndexer.rows() / 2) + (imgIndexer.rows() / 3)) {
                        if (blue >= 95 && blue <= 140 &&
                                green >= 160 && green <= 185 &&
                                red >= 175 && red <= 200) {
                            imgProcessada.put(altura, largura, 0, 255, 255);

                            marromBocaHomer++;
                        }
                    }

                    // Sapato do Homer (parte inferior da imagem)
                    if (altura > (imgIndexer.rows() / 2) + (imgIndexer.rows() / 3)) {
                        if (blue >= 25 && blue <= 45 && green >= 25 &&
                                green <= 45 && red >= 25 && red <= 45) {
                            imgProcessada.put(altura, largura, new int[]{0, 255, 255});

                            cinzaSapatoHomer++;
                        }
                    }

                }
            }

            // Normaliza as características pelo número de pixels totais da imagem
            laranjaCamisaBart = (laranjaCamisaBart / (imagemOriginal.rows() * imagemOriginal.cols())) * 100;
            azulCalcaoBart = (azulCalcaoBart / (imagemOriginal.rows() * imagemOriginal.cols())) * 100;
            azulSapatoBart = (azulSapatoBart / (imagemOriginal.rows() * imagemOriginal.cols())) * 100;
            azulCalcaHomer = (azulCalcaHomer / (imagemOriginal.rows() * imagemOriginal.cols())) * 100;
            marromBocaHomer = (marromBocaHomer / (imagemOriginal.rows() * imagemOriginal.cols())) * 100;
            cinzaSapatoHomer = (cinzaSapatoHomer / (imagemOriginal.rows() * imagemOriginal.cols())) * 100;
            //obs: rows() corresponde ao height() e cols() corresponde ao width()

            // Grava as características no vetor de características
            caracteristicas[i][0] = laranjaCamisaBart;
            caracteristicas[i][1] = azulCalcaoBart;
            caracteristicas[i][2] = azulSapatoBart;
            caracteristicas[i][3] = azulCalcaHomer;
            caracteristicas[i][4] = marromBocaHomer;
            caracteristicas[i][5] = cinzaSapatoHomer;
            caracteristicas[i][6] = classePersonagem;

            System.out.println("Laranja camisa Bart: " + caracteristicas[i][0] + " - Azul calção Bart: " + caracteristicas[i][1] + " - Azul sapato Bart: " + caracteristicas[i][2] + " - Azul calça Homer: " + caracteristicas[i][3] + " - Marrom boca Homer: " + caracteristicas[i][4] + " - Preto sapato Homer: " + caracteristicas[i][5] + " - Classe: " + caracteristicas[i][6]);
            exportacao.append(caracteristicas[i][0]).append(",")
                    .append(caracteristicas[i][1]).append(",")
                    .append(caracteristicas[i][2]).append(",")
                    .append(caracteristicas[i][3]).append(",")
                    .append(caracteristicas[i][4]).append(",")
                    .append(caracteristicas[i][5]).append(",")
                    .append(classePersonagemString)
                    .append("\n");

            imshow("Imagem original", imagemOriginal);
            // Imagem processada de acordo com as características (alteração das cores)
            imshow("Imagem processada", imagemProcessada);
            proximaImagem = cvWaitKey();
        }

        // Grava o arquivo ARFF no disco
        File arquivo = new File("caracteristicas.arff");
        FileOutputStream f = new FileOutputStream(arquivo);
        f.write(exportacao.toString().getBytes());
        f.close();

        scanner.close();
    }

}

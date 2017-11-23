import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import javax.swing.JOptionPane;

/**
 *
 * @author Nilukio
 * 
 * This is my quadtree code to be used specifically
 * with the ImageJ library. I will post soon translated into
 * English. For now, I make available in Portuguese.
 * 
 * 
 */

public class QtreeColorido {
        static double alturaMaxima = Integer.parseInt(JOptionPane.showInputDialog(""
                + "A Altura da Árvore QuaTree se limita ao\n"
                + " tamanho da imagem original. Para uma resolução\n"
                + "padrão de 1024x768, recomenda-se Altura 5.\n\n"
                + "Digite a altura da árvore QuadTree: "));    
        static int rgb[] =  new int [3];
        static final int R = 0, G = 1, B = 2;
        static boolean Heterogenio=false; //
        static boolean Homogenio=false; //
        static double ultimoRamo = Math.pow(4, alturaMaxima);
        static double ultimoDaLinha = Math.pow(4, (alturaMaxima/2));
        static double arvore[][] = new double [3][(int)ultimoRamo+1]; //Cor, Quadrante
        static int ramoAtual=1;
        static int X1;
        static int X2;
        static int Y1;
        static int Y2;
        static int largura;
        static int altura;
        static int red = 0;
        static int green = 0;
        static int blue = 0;
        
    public static void main(String[] args) {
        
        ImagePlus original = ij.IJ.openImage("");
        ImageProcessor ipOriginal = original.getProcessor();

        //Redimensionamento opcional...
        
        if (ipOriginal.getWidth()==ipOriginal.getHeight()){
            IJ.run(original, "Size...", "width=1000 height=1000");
        }
        if (ipOriginal.getWidth()>=ipOriginal.getHeight()){
            IJ.run(original, "Size...", "width=1024 height=768");
        } else {
            IJ.run(original, "Size...", "width=768 height=1024");
        }

        ImagePlus qtree = original.duplicate();
        ImageProcessor ip = qtree.getProcessor();
        ColorProcessor cp = (ColorProcessor) ip;
        
        largura=ip.getWidth();
        altura=ip.getHeight();
        
        for (int i = 1; i <= alturaMaxima; i++) {
            altura=altura/2;
            largura=largura/2;
        }
        
        //Vamos começar...
        X1 = 0;
        X2 = largura;
        Y1 = 0;
        Y2 = altura;
        
        for (ramoAtual = 1; ramoAtual <= ultimoRamo;) { //Leitura do quadrante
            
        for (int i = X1; i <= X2; i++) {
            for (int j = Y1; j <= Y2; j++) {
        
                cp.getPixel(i, j, rgb);
                red=red+rgb[R];
                green=green+rgb[G];
                blue=blue+rgb[B];
                
                if ((cp.getPixel(i, j, rgb))!=(cp.getPixel(i-1, j, rgb))) {
                    Heterogenio=true;
                } else if ((cp.getPixel(i, j, rgb))!=(cp.getPixel(i, j-1, rgb))) {
                    Heterogenio=true;
                } else Homogenio=true;
            }
        }
            
        Atribuir(ramoAtual); // Grava na árvore
        MudaQuad(); // Determina próxima varredura
        ramoAtual++;
        }

        //Nova imagem, Quadtree Colorido
        ImagePlus newImg = NewImage.createImage("Quadtree Colorido", ip.getWidth(), ip.getHeight(), 1, 24, NewImage.FILL_WHITE);
        ImageProcessor ip2 = newImg.getProcessor();
        ColorProcessor cp2 = (ColorProcessor) ip2;
        X1 = 0;
        X2 = largura;
        Y1 = 0;
        Y2 = altura;
        ramoAtual=1;
        for (ramoAtual = 1; ramoAtual <= ultimoRamo;) { //Gravação do quadrante
            
        for (int i = X1; i <= X2; i++) {
            for (int j = Y1; j <= Y2; j++) {
                
                rgb[R]=(int)arvore[R][ramoAtual];
                rgb[G]=(int)arvore[G][ramoAtual];
                rgb[B]=(int)arvore[B][ramoAtual];
                cp2.putPixel(i, j, rgb);
                }
                }
        MudaQuad();
        ramoAtual++;    
        }
ij.IJ.save(original, "imagens/Original_RBG.png");
ij.IJ.save(newImg, "imagens/Quadtree_RGB_altura_"+(int)alturaMaxima+".png");
original.show();
newImg.show();
        }
    
    public static void Atribuir(int ramo){
        if (Homogenio==true){
            arvore[R][ramo]=rgb[R];
            arvore[G][ramo]=rgb[G];
            arvore[B][ramo]=rgb[B];
        }
        if (Heterogenio==true){
            int newRed=red/(largura*altura);
            int newGreen=green/(largura*altura);
            int newBlue=blue/(largura*altura);
            arvore[R][ramo]=newRed;
            arvore[G][ramo]=newGreen;
            arvore[B][ramo]=newBlue;
        }
    Heterogenio=false;
    Homogenio=false;
    red=0;
    green=0;
    blue=0;
    }
    
    public static void MudaQuad(){ //Avalia qual será o próximo quadrante

        if ((ramoAtual>=1)&&(ramoAtual<=ultimoRamo)) {  
            
            if (ramoAtual % ultimoDaLinha != 0){ //muda apenas pra direita
            X1=X2+1; 
            X2=X2+largura;
            
            }    
            if (ramoAtual % ultimoDaLinha == 0){ // muda pra linha debaixo
            X1=0;
            X2=largura-1;
            Y1=Y2;
            Y2=Y2+altura;
            
            }
        }
    }
}

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
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

public class QtreePB {
        static double alturaMaxima = Integer.parseInt(JOptionPane.showInputDialog(""
                + "A Altura da Árvore QuaTree se limita ao\n"
                + "tamanho da imagem original. Para uma resolução\n"
                + "padrão de 1024 x 768, recomenda-se até Altura 5 ou 6.\n\n"
                + "Digite a altura da árvore QuadTree:\n\n"));    
        static boolean BrancoPreto=false; //encontra quadrante misto
        static boolean tudoEstaBranco=false; //encontra tudo branco
        static boolean tudoEstaPreto=false; //encontra tudo preto
        static boolean insereBranco=false; //altera último quadrante de acordo com sua proporção
        static double ultimoRamo = Math.pow(4, alturaMaxima);
        static double ultimoDaLinha = Math.pow(4, (alturaMaxima/2));
        static double arvore[] = new double [(int)ultimoRamo+1]; //todos a ramificação da altura
        static int ramoAtual=1;
        static int X1;
        static int X2;
        static int Y1;
        static int Y2;
        static int largura;
        static int altura;
        
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
        int branco = 0;
        int preto = 0;
        
        for (ramoAtual = 1; ramoAtual <= ultimoRamo;) { //Leitura do quadrante {
        
        //Leitura do quadrante
        for (int i = X1; i <= X2; i++) {
            for (int j = Y1; j <= Y2; j++) {
        
                if (ip.getPixel(i, j)==0) {
                    preto++;} 
                else { branco++; }
                
                }
            }
            if (branco==0) { tudoEstaPreto=true; }
            if (preto==0) { tudoEstaBranco=true; }
            if ((branco!=0)&&(preto!=0)) { BrancoPreto=true; }
            if ((BrancoPreto==true)&&(branco>preto)) { insereBranco=true; }
            
            branco=0;
            preto=0;
        
        Atribuir(ramoAtual); // Grava na árvore
        MudaQuad(); // Determina próxima varredura
        ramoAtual++;
        }

        //Nova imagem, Quadtree Preto e Branco
        ImagePlus newImg = NewImage.createImage("Quadtree", ip.getWidth(), ip.getHeight(), 1, 8, NewImage.FILL_BLACK);
        ImageProcessor ip2 = newImg.getProcessor();
        X1 = 0;
        X2 = largura;
        Y1 = 0;
        Y2 = altura;
        ramoAtual=1;
        for (ramoAtual = 1; ramoAtual <= ultimoRamo;) { //Leitura do quadrante { //Gravação do quadrante
            
        for (int i = X1; i <= X2; i++) {
            for (int j = Y1; j <= Y2; j++) {
                if (arvore[ramoAtual]==0) { // se quadrante já era inteiro preto
                ip2.putPixel(i, j, 0);
                }
                if (arvore[ramoAtual]==1) { // se quadrante já era inteiro branco
                ip2.putPixel(i, j, 255);
                }
                if (arvore[ramoAtual]==3) { // se havia mais preto que branco fica preto
                ip2.putPixel(i, j, 0);
                }
                if (arvore[ramoAtual]==4) { // se havia mais branco que preto fica branco
                ip2.putPixel(i, j, 255);
                }
            }
        }
        MudaQuad();
        ramoAtual++;    
        }
ij.IJ.save(original, "imagens/Original_PB.png");
ij.IJ.save(newImg, "imagens/Quadtree_PB_altura_"+(int)alturaMaxima+".png");
original.show();
newImg.show();
        }
    
    public static void Atribuir(int ramo){
        if (tudoEstaBranco==true){
            arvore[ramo]=1;
        }
        if (tudoEstaPreto==true){
            arvore[ramo]=0;
        }
        if (BrancoPreto==true) { //Se estiver no topo da árvore e quadrante for parcial
            if (insereBranco==true) { arvore[ramo]=4; }
            else { arvore[ramo]=3; }
        }
    BrancoPreto=false;
    tudoEstaBranco=false;
    tudoEstaPreto=false;
    insereBranco=false;
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

package asint;

import alex.UnidadLexica;
import alex.AnalizadorLexicoTiny;
import alex.ClaseLexica;
import errors.GestionErroresTiny;
import java.io.IOException;
import java.io.Reader;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalizadorSintacticoTiny {
   private UnidadLexica anticipo;       // token adelantado
   private AnalizadorLexicoTiny alex;   // analizador léxico 
   private GestionErroresTiny errores;  // gestor de errores
   private Set<ClaseLexica> esperados;  // clases léxicas esperadas
   
   public AnalizadorSintacticoTiny(Reader input) throws IOException {
        // se crea el gestor de errores
      errores = new GestionErroresTiny();
        // se crea el analizador léxico
        alex = new AnalizadorLexicoTiny(input,errores);
        esperados = EnumSet.noneOf(ClaseLexica.class);
        // Se lee el primer token adelantado
      sigToken();                      
   }
   public void analiza() { // Es la unica funcion publica, es lo que llama Main para analizar toda la sintaxis.
      programa();
   }
   private void programa() {
       bloque();
       empareja(ClaseLexica.EOF);
   }
 
   private void bloque() { //Yo creo que esto es asi
    switch(anticipo.clase()) {
        case LLAVEA: 
              empareja(ClaseLexica.LLAVEA);
              declaraciones_opt();
              instrucciones_opt();
              empareja(ClaseLexica.LLAVEC);
              break;
        default:        
             esperados(ClaseLexica.LLAVEA);
             error();
        } 
    }

    private void declaraciones_opt() { //Segun las diapositivas así se hacen las reglas anulables
        switch(anticipo.clase()){
            case INT: case REAL: case BOOL:
                declaraciones();
                empareja(ClaseLexica.AMPERSAND);
                break;
            default:
                esperados(ClaseLexica.INT, ClaseLexica.REAL, ClaseLexica.BOOL);
                break;
        }
    }
    private void instrucciones_opt() {
        switch(anticipo.clase()){
            case ARROBA:
                instrucciones();
                break;
            default:
                esperados(ClaseLexica.ARROBA);
                break;
        }
    }

    private void declaraciones() {
        declaracion_var();
        restodec();
    }
    private void restodec() {
        switch(anticipo.clase()){
            case PUNTOYCOMA:
                empareja(ClaseLexica.PUNTOYCOMA);
                declaraciones();
                break;
            default:
                esperados(ClaseLexica.PUNTOYCOMA);
                break;
        }
    }

    //declaracion_var → tipo identificador
    private void declaracion_var() {
        tipo();
        empareja(ClaseLexica.VAR);
    }


    private void tipo(){ //Hecho en teoria
        switch(anticipo.clase()) {
            case INT: empareja(ClaseLexica.INT); break;
            case REAL: empareja(ClaseLexica.REAL); break;
            case BOOL: empareja(ClaseLexica.BOOL); break;
            default:
                esperados(ClaseLexica.INT,ClaseLexica.REAL,ClaseLexica.BOOL);
                error();
        }
    }

    private void instrucciones() {
        instruccion();
        restoInst();
    }

    private void restoInst() {
        switch(anticipo.clase()){
            case PUNTOYCOMA:
                empareja(ClaseLexica.PUNTOYCOMA);
                instruccion();
                restoInst();
                break;
            default:
                esperados(ClaseLexica.PUNTOYCOMA);
                break;
        }
    }


    private void instruccion() {  //Hechoo
        switch(anticipo.clase()) {
            case ARROBA:
                empareja(ClaseLexica.ARROBA);
                E0();
                break;
            default:
                esperados(ClaseLexica.ARROBA);
                error();
        }
    }

    private void E0() {
        E1();
        RE0();
    }
    private void RE0() {
        switch(anticipo.clase()){
            case ASIG:
                empareja(ClaseLexica.ASIG);
                E0();
                break;
            default:
                esperados(ClaseLexica.ASIG);
                break;
        }
    }
    private void E1() {
        E2();
        RE1();
    }
    private void RE1() {
        switch(anticipo.clase()){
            case MAYOR: case MENOR: case MAYORIG: case MENORIG: case IGUAL: case DESIGUAL:
                OP1();
                E2();
                RE1();
                break;
            default:
                esperados(ClaseLexica.MAYOR,ClaseLexica.MENOR, ClaseLexica.MAYORIG,
                        ClaseLexica.MENORIG, ClaseLexica.IGUAL, ClaseLexica.DESIGUAL);
                break;
        }
    }

    private void E2() {
        E3();
        RE2();
        E2Prima();
    }
    private void RE2() {
        switch(anticipo.clase()){
            case MENOS:
                empareja(ClaseLexica.MENOS);
                E3();
                break;
            default:
                esperados(ClaseLexica.MENOS);
                break;
        }
    }
    private void E2Prima() {
        switch(anticipo.clase()){
            case MAS:
                empareja(ClaseLexica.MAS);
                E3();
                E2Prima();
                break;
            default:
                esperados(ClaseLexica.MAS);
                break;
        }
    }
    private void E3() {
        E4();
        RE3();
    }
    private void RE3() {
       switch(anticipo.clase()){
           case AND: case OR:
               OP3();
               break;
           default:
               esperados(ClaseLexica.AND, ClaseLexica.OR);
               break;
       }
    }
    private void E4() {
        E5();
        RE4();
    }
    private void RE4() {
        switch(anticipo.clase()){
            case POR: case DIV:
                OP4();
                E5();
                RE4();
                break;
            default:
                esperados(ClaseLexica.POR, ClaseLexica.DIV);
                break;
        }
    }
    private void E5() {
        switch (anticipo.clase()) {
            case MENOS: case NOT:
                OP5();
                E5();
                break;
            case LENT:
                empareja(ClaseLexica.LENT);
                break;
            case LREAL:
                empareja(ClaseLexica.LREAL);
                break;
            case TRUE:
                empareja(ClaseLexica.TRUE);
                break;
            case FALSE:
                empareja(ClaseLexica.FALSE);
                break;
            case VAR:
                empareja(ClaseLexica.VAR);
                break;
            case PAP:
                empareja(ClaseLexica.PAP);
                E0();
                empareja(ClaseLexica.PCIERRE);
                break;
            default:
                esperados(ClaseLexica.MENOS, ClaseLexica.NOT, ClaseLexica.LENT,
                        ClaseLexica.LREAL, ClaseLexica.TRUE,ClaseLexica.FALSE, ClaseLexica.VAR, ClaseLexica.PAP);
                error();
        }
    }
    private void OP1() { //Hecho en teoriaa
        switch(anticipo.clase()) {
            case MAYOR: empareja(ClaseLexica.MAYOR); break;
            case MENOR: empareja(ClaseLexica.MENOR); break;
            case MAYORIG: empareja(ClaseLexica.MAYORIG); break;
            case MENORIG: empareja(ClaseLexica.MENORIG); break;
            case IGUAL: empareja(ClaseLexica.IGUAL); break;
            case DESIGUAL: empareja(ClaseLexica.DESIGUAL); break;
            default:
                esperados(ClaseLexica.MAYOR, ClaseLexica.MENOR, ClaseLexica.MAYORIG, ClaseLexica.MENORIG, ClaseLexica.IGUAL, ClaseLexica.DESIGUAL);
                error();
        }
    }

    private void OP3() { //Hecho en teoriaa
        switch(anticipo.clase()) {
            case AND:
                empareja(ClaseLexica.AND);
                E3();
                break;
            case OR:
                empareja(ClaseLexica.OR);
                E4();
                break;
            default:
                esperados(ClaseLexica.AND, ClaseLexica.OR);
                error();
        }
    }
    private void OP4() { //Hecho en teoria
        switch(anticipo.clase()) {
            case POR:
                empareja(ClaseLexica.POR);
                break;
            case DIV:
                empareja(ClaseLexica.DIV);
                break;
            default:
                esperados(ClaseLexica.POR, ClaseLexica.DIV);
                error();
        }
    }
    private void OP5() { //Hecho en teoriaa
        switch(anticipo.clase()) {
            case MENOS:
                empareja(ClaseLexica.MENOS);
                break;
            case NOT:
                empareja(ClaseLexica.NOT);
                break;
            default:
                esperados(ClaseLexica.MENOS, ClaseLexica.NOT);
                error();
        }
    }


    private void esperados(ClaseLexica ... esperadas) {
        for(ClaseLexica c: esperadas) {
            esperados.add(c);
        }
    }


    private void empareja(ClaseLexica claseEsperada) {
        if (anticipo.clase() == claseEsperada) {
            traza_emparejamiento(anticipo);
            sigToken();
        }
        else {
            esperados(claseEsperada);
            error();
        }
    }
    private void sigToken() {
        try {
            anticipo = alex.sigToken();
            esperados.clear();
        }
        catch(IOException e) {
            errores.errorFatal(e);
        }
    }

    private void error() {
        errores.errorSintactico(anticipo.fila(), anticipo.columna(), anticipo.clase(), esperados);
    }

    protected void traza_emparejamiento(UnidadLexica anticipo) {}
}

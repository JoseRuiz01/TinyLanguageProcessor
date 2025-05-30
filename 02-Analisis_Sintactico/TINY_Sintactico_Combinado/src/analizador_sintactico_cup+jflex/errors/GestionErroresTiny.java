package errors;

import alex.UnidadLexica;

public class GestionErroresTiny {
   public class ErrorLexico extends RuntimeException {
       public ErrorLexico(String msg) {
           super(msg);
       }
   } 
   public class ErrorSintactico extends RuntimeException {
       public ErrorSintactico(String msg) {
           super(msg);
       }
   } 
   public void errorLexico(int fila, int columna, String lexema) {
     throw new ErrorLexico("ERROR_LEXICO"); 
   }  
   public void errorSintactico(UnidadLexica unidadLexica) {
     throw new ErrorSintactico("ERROR_SINTACTICO");
   }
}
